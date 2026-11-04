package net.satisfy.bakery.core.block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.bakery.core.registry.EntityTypeRegistry;
import net.satisfy.bakery.core.registry.StorageTypeRegistry;
import net.satisfy.foundation.block.LineConnectingBlock;
import net.satisfy.foundation.block.LineConnectingType;
import net.satisfy.foundation.food.PlaceableEffectFoodItem;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WallDisplayBlock extends StackingStorageBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final EnumProperty<LineConnectingType> TYPE = LineConnectingBlock.TYPE;
   private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      return Shapes.joinUnoptimized(shape, Shapes.box(0.0, 0.0, 0.5, 1.0, 1.0, 1.0), BooleanOp.OR);
   };
   public static final Map<Direction, VoxelShape> SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL.stream().toList()) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
      }
   });

   public BlockEntityType<?> blockEntityType() {
      return (BlockEntityType<?>)EntityTypeRegistry.STORAGE_ENTITY.get();
   }

   public WallDisplayBlock(Properties settings) {
      super(settings);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, LineConnectingType.NONE)
      );
   }

   public int size() {
      return 6;
   }

   public boolean canInsertStack(ItemStack stack) {
      return stack.getItem() instanceof PlaceableEffectFoodItem ? true : !(stack.getItem() instanceof BlockItem) || stack.is(Items.BREAD);
   }

   public ResourceLocation type() {
      return StorageTypeRegistry.WALL_DISPLAY;
   }

   public Direction[] unAllowedDirections() {
      return new Direction[]{Direction.DOWN, Direction.UP};
   }

   public int getSection(Float x, Float y) {
      int row = y > 0.5F ? 1 : 0;
      int column = x < 0.5F ? 1 : 0;
      return row * 2 + column;
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
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{TYPE});
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE.get(state.getValue(FACING));
   }

   @NotNull
   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   @NotNull
   public BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      tooltip.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY}));
   }
}
