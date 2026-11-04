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
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.StorageTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import net.satisfy.foundation.storage.StorageBlock;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;

public class ToolRackBlock extends StorageBlock {
   private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      return Shapes.joinUnoptimized(shape, Shapes.box(0.0, 0.8125, 0.875, 1.0, 0.9375, 1.0), BooleanOp.OR);
   };
   private static final Map<Direction, VoxelShape> INTERACTION_SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, Shapes.box(0.0, 0.25, 0.75, 1.0, 1.0, 1.0)));
      }
   });
   public static final Map<Direction, VoxelShape> SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
      }
   });

   public ToolRackBlock(Properties settings) {
      super(settings);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return INTERACTION_SHAPE.get(state.getValue(FACING));
   }

   @NotNull
   public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE.get(state.getValue(FACING));
   }

   public boolean canInsertStack(ItemStack stack) {
      return stack.getItem() instanceof TieredItem || stack.is(TagRegistry.HANGABLE) && !stack.is(TagRegistry.SHIELDS);
   }

   public ResourceLocation type() {
      return StorageTypeRegistry.TOOL_RACK;
   }

   public Direction[] unAllowedDirections() {
      return new Direction[]{Direction.DOWN};
   }

   public int getSection(Float f, Float y) {
      float oneS = 0.33333334F;
      int nSection = (int)(f / oneS);
      return 2 - nSection;
   }

   public BlockEntityType<?> blockEntityType() {
      return (BlockEntityType<?>)EntityTypeRegistry.STORAGE_ENTITY.get();
   }

   public int size() {
      return 3;
   }

   public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
      Direction facing = (Direction)state.getValue(FACING);
      if (facing == Direction.DOWN) {
         BlockPos blockAbove = pos.above();
         return world.getBlockState(blockAbove).isFaceSturdy(world, blockAbove, Direction.DOWN);
      }

      if (facing == Direction.UP) {
         return false;
      }

      BlockPos neighborPos = pos.relative(facing.getOpposite());
      BlockState neighborState = world.getBlockState(neighborPos);
      return neighborState.isFaceSturdy(world, neighborPos, facing);
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

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      list.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
   }
}
