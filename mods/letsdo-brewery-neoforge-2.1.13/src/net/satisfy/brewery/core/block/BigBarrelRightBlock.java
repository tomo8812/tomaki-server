package net.satisfy.brewery.core.block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;

public class BigBarrelRightBlock extends BigBarrelBlock {
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   private static final Supplier<VoxelShape> bottomVoxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.or(shape, Shapes.box(0.125, 0.25, 0.0, 1.0, 1.0, 1.0));
      return Shapes.or(shape, Shapes.box(0.125, 0.0, 0.1875, 1.0, 0.25, 0.4375));
   };
   public static final Map<Direction, VoxelShape> BOTTOM_SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, bottomVoxelShapeSupplier.get()));
      }
   });
   private static final Supplier<VoxelShape> topVoxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      return Shapes.or(shape, Shapes.box(0.125, 0.0, 0.0, 1.0, 1.0, 1.0));
   };
   public static final Map<Direction, VoxelShape> TOP_SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, topVoxelShapeSupplier.get()));
      }
   });

   public BigBarrelRightBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER));
   }

   public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
      if (blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
         level.setBlockAndUpdate(blockPos.above(), (BlockState)blockState.setValue(HALF, DoubleBlockHalf.UPPER));
      }
   }

   @NotNull
   public BlockState updateShape(
      BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2
   ) {
      DoubleBlockHalf doubleBlockHalf = (DoubleBlockHalf)blockState.getValue(HALF);
      if (direction.getAxis() == Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
         return blockState2.is(this) && blockState2.getValue(HALF) != doubleBlockHalf
            ? (BlockState)blockState.setValue(FACING, (Direction)blockState2.getValue(FACING))
            : Blocks.AIR.defaultBlockState();
      } else {
         return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !blockState.canSurvive(levelAccessor, blockPos)
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
      }
   }

   public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
      Direction facing = (Direction)blockState.getValue(FACING);
      BlockPos backPos = blockPos.relative(facing.getOpposite());
      BlockPos sidePos = blockPos.relative(facing.getClockWise());
      BlockPos diagonalPos = sidePos.relative(facing.getOpposite());
      level.removeBlock(backPos, false);
      level.removeBlock(sidePos, false);
      level.removeBlock(diagonalPos, false);
      level.removeBlock(blockPos, false);
      super.onRemove(blockState, level, blockPos, blockState2, bl);
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{HALF});
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      DoubleBlockHalf half = (DoubleBlockHalf)state.getValue(HALF);
      Direction facing = (Direction)state.getValue(FACING);
      return half == DoubleBlockHalf.LOWER ? BOTTOM_SHAPE.get(facing) : TOP_SHAPE.get(facing);
   }
}
