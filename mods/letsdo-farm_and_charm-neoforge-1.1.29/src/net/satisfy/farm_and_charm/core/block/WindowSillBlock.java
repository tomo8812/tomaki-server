package net.satisfy.farm_and_charm.core.block;

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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.StorageTypeRegistry;
import net.satisfy.foundation.storage.StorageBlock;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;

public class WindowSillBlock extends StorageBlock {
   private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.0, 0.0, 0.4375, 1.0, 0.125, 1.0), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.0625, 0.125, 0.5, 0.4375, 0.5, 0.875), BooleanOp.OR);
      return Shapes.joinUnoptimized(shape, Shapes.box(0.53125, 0.125, 0.53125, 0.90625, 0.5, 0.90625), BooleanOp.OR);
   };
   public static final Map<Direction, VoxelShape> SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
      }
   });

   public WindowSillBlock(Properties settings) {
      super(settings);
   }

   public BlockEntityType<?> blockEntityType() {
      return (BlockEntityType<?>)EntityTypeRegistry.STORAGE_ENTITY.get();
   }

   public int size() {
      return 2;
   }

   public boolean canInsertStack(ItemStack stack) {
      return stack.is(ItemTags.SMALL_FLOWERS);
   }

   public ResourceLocation type() {
      return StorageTypeRegistry.WINDOW_SILL;
   }

   public Direction[] unAllowedDirections() {
      return new Direction[]{Direction.DOWN};
   }

   public int getSection(Float x, Float y) {
      return x < 0.5F ? 0 : 1;
   }

   public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
      Direction facing = (Direction)state.getValue(FACING);
      BlockPos neighborPos = facing == Direction.DOWN ? pos.above() : pos.relative(facing.getOpposite());
      BlockState neighborState = world.getBlockState(neighborPos);
      if (neighborState.is(Blocks.GLASS_PANE) || neighborState.getBlock() instanceof IronBarsBlock) {
         return true;
      } else if (facing == Direction.DOWN) {
         return neighborState.isFaceSturdy(world, neighborPos, Direction.DOWN);
      } else {
         return facing == Direction.UP ? false : neighborState.isFaceSturdy(world, neighborPos, facing);
      }
   }

   public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
      if (!state.canSurvive(world, pos)) {
         world.destroyBlock(pos, true);
      }
   }

   @NotNull
   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
      if (!state.canSurvive(world, pos)) {
         world.scheduleTick(pos, this, 1);
      }

      return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE.get(state.getValue(FACING));
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      list.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
   }
}
