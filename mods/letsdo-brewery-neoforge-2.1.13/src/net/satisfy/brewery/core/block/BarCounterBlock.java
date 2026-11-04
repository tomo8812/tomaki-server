package net.satisfy.brewery.core.block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.block.LineConnectingBlock;
import net.satisfy.foundation.block.LineConnectingType;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BarCounterBlock extends Block {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final EnumProperty<LineConnectingType> TYPE = LineConnectingBlock.TYPE;
   private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.or(shape, Shapes.box(0.0, 0.0, 0.1875, 1.0, 1.0, 1.0));
      return Shapes.or(shape, Shapes.box(0.0, 0.8125, 0.0, 1.0, 1.0, 0.1875));
   };
   public static final Map<Direction, VoxelShape> SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL.stream().toList()) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
      }
   });

   public BarCounterBlock(Properties settings) {
      super(settings);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, LineConnectingType.NONE)
      );
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE.get(state.getValue(FACING));
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Direction facing = context.getHorizontalDirection().getOpposite();
      BlockState blockState = (BlockState)this.defaultBlockState().setValue(FACING, facing);
      Level world = context.getLevel();
      BlockPos clickedPos = context.getClickedPos();

      return switch (facing) {
         case EAST -> (BlockState)blockState.setValue(
            TYPE, this.getType(blockState, world.getBlockState(clickedPos.south()), world.getBlockState(clickedPos.north()))
         );
         case SOUTH -> (BlockState)blockState.setValue(
            TYPE, this.getType(blockState, world.getBlockState(clickedPos.west()), world.getBlockState(clickedPos.east()))
         );
         case WEST -> (BlockState)blockState.setValue(
            TYPE, this.getType(blockState, world.getBlockState(clickedPos.north()), world.getBlockState(clickedPos.south()))
         );
         default -> (BlockState)blockState.setValue(
            TYPE, this.getType(blockState, world.getBlockState(clickedPos.east()), world.getBlockState(clickedPos.west()))
         );
      };
   }

   public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
      if (!world.isClientSide) {
         Direction facing = (Direction)state.getValue(FACING);

         LineConnectingType type = switch (facing) {
            case EAST -> this.getType(state, world.getBlockState(pos.south()), world.getBlockState(pos.north()));
            case SOUTH -> this.getType(state, world.getBlockState(pos.west()), world.getBlockState(pos.east()));
            case WEST -> this.getType(state, world.getBlockState(pos.north()), world.getBlockState(pos.south()));
            default -> this.getType(state, world.getBlockState(pos.east()), world.getBlockState(pos.west()));
         };
         if (state.getValue(TYPE) != type) {
            state = (BlockState)state.setValue(TYPE, type);
         }

         world.setBlock(pos, state, 3);
      }
   }

   public LineConnectingType getType(BlockState state, BlockState left, BlockState right) {
      boolean shape_left_same = left.getBlock() == state.getBlock() && left.getValue(FACING) == state.getValue(FACING);
      boolean shape_right_same = right.getBlock() == state.getBlock() && right.getValue(FACING) == state.getValue(FACING);
      if (shape_left_same && shape_right_same) {
         return LineConnectingType.MIDDLE;
      } else if (shape_left_same) {
         return LineConnectingType.LEFT;
      } else {
         return shape_right_same ? LineConnectingType.RIGHT : LineConnectingType.NONE;
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, TYPE});
   }

   @NotNull
   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   @NotNull
   public BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }
}
