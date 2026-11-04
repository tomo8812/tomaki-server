package net.satisfy.brewery.core.block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.Plane;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import net.satisfy.brewery.core.block.entity.BigBarrelBlockEntity;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BigBarrelMainBlock extends BigBarrelBlock implements EntityBlock {
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   private static final Supplier<VoxelShape> bottomVoxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.or(shape, Shapes.box(0.0, 0.0, 0.1875, 0.875, 0.25, 0.4375));
      return Shapes.or(shape, Shapes.box(0.0, 0.25, 0.0, 0.875, 1.0, 1.0));
   };
   public static final Map<Direction, VoxelShape> BOTTOM_SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, bottomVoxelShapeSupplier.get()));
      }
   });
   private static final Supplier<VoxelShape> topVoxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      return Shapes.or(shape, Shapes.box(0.0, 0.0, 0.0, 0.875, 1.0, 1.0));
   };
   public static final Map<Direction, VoxelShape> TOP_SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, topVoxelShapeSupplier.get()));
      }
   });

   public BigBarrelMainBlock(Properties properties) {
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

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{HALF});
   }

   public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
      if (!blockState2.is(this) && level.getBlockEntity(blockPos) instanceof BigBarrelBlockEntity barrel) {
         Containers.dropContents(level, blockPos, barrel.takeAll());
      }

      Direction facing = (Direction)blockState.getValue(FACING);
      BlockPos backPos = blockPos.relative(facing.getOpposite());
      BlockPos sidePos = blockPos.relative(facing.getCounterClockWise());
      BlockPos diagonalPos = sidePos.relative(facing.getOpposite());
      level.removeBlock(backPos, false);
      level.removeBlock(sidePos, false);
      level.removeBlock(diagonalPos, false);
      level.removeBlock(blockPos, false);
      super.onRemove(blockState, level, blockPos, blockState2, bl);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      Level level = blockPlaceContext.getLevel();
      BlockPos mainPos = blockPlaceContext.getClickedPos();
      BlockState blockState = super.getStateForPlacement(blockPlaceContext);
      if (blockState == null) {
         return null;
      }

      Direction facing = (Direction)blockState.getValue(FACING);
      BlockPos backPos = mainPos.relative(facing.getOpposite());
      BlockPos sidePos = mainPos.relative(facing.getCounterClockWise());
      BlockPos diagonalPos = sidePos.relative(facing.getOpposite());
      BlockPos topPos = diagonalPos.above();
      boolean placeable = this.canPlace(level, backPos, sidePos, diagonalPos, topPos);
      return placeable ? blockState : null;
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
      super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
      if (!level.isClientSide) {
         Direction facing = (Direction)blockState.getValue(FACING);
         BlockPos backPos = blockPos.relative(facing.getOpposite());
         BlockPos sidePos = blockPos.relative(facing.getCounterClockWise());
         BlockPos diagonalPos = sidePos.relative(facing.getOpposite());
         BlockPos topPos = diagonalPos.above();
         if (this.canPlace(level, backPos, sidePos, diagonalPos, topPos)) {
            level.setBlock(backPos, (BlockState)((Block)ObjectRegistry.BARREL_MAIN_HEAD.get()).defaultBlockState().setValue(FACING, facing), 3);
            level.setBlock(sidePos, (BlockState)((Block)ObjectRegistry.BARREL_RIGHT.get()).defaultBlockState().setValue(FACING, facing), 3);
            level.setBlock(diagonalPos, (BlockState)((Block)ObjectRegistry.BARREL_HEAD_RIGHT.get()).defaultBlockState().setValue(FACING, facing), 3);
         }
      }
   }

   private boolean canPlace(Level level, BlockPos... blockPoses) {
      for (BlockPos blockPos : blockPoses) {
         if (!level.getBlockState(blockPos).isAir()) {
            return false;
         }
      }

      return true;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new BigBarrelBlockEntity(pos, state) : null;
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      DoubleBlockHalf half = (DoubleBlockHalf)state.getValue(HALF);
      Direction facing = (Direction)state.getValue(FACING);
      return half == DoubleBlockHalf.LOWER ? BOTTOM_SHAPE.get(facing) : TOP_SHAPE.get(facing);
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      list.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
   }
}
