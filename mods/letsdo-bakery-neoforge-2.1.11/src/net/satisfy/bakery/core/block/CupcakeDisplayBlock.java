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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.bakery.core.registry.EntityTypeRegistry;
import net.satisfy.bakery.core.registry.StorageTypeRegistry;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;

public class CupcakeDisplayBlock extends StackingStorageBlock {
   private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.or(shape, Shapes.box(0.9375, 0.0, 0.4375, 1.0, 0.875, 0.5625));
      shape = Shapes.or(shape, Shapes.box(0.0, 0.0, 0.4375, 0.0625, 0.875, 0.5625));
      shape = Shapes.or(shape, Shapes.box(0.0625, 0.0625, 0.3125, 0.9375, 0.375, 0.6875));
      shape = Shapes.or(shape, Shapes.box(0.0625, 0.4375, 0.3125, 0.9375, 0.75, 0.6875));
      return Shapes.or(shape, Shapes.box(0.0625, 0.8125, 0.4375, 0.9375, 0.875, 0.5625));
   };
   public static final Map<Direction, VoxelShape> SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL.stream().toList()) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
      }
   });

   public BlockEntityType<?> blockEntityType() {
      return (BlockEntityType<?>)EntityTypeRegistry.STORAGE_ENTITY.get();
   }

   public CupcakeDisplayBlock(Properties settings) {
      super(settings);
   }

   public int size() {
      return 6;
   }

   public ResourceLocation type() {
      return StorageTypeRegistry.CUPCAKE_DISPLAY;
   }

   public boolean canInsertStack(ItemStack stack) {
      return true;
   }

   public Direction[] unAllowedDirections() {
      return new Direction[]{Direction.DOWN, Direction.UP};
   }

   public int getSection(Float x, Float y) {
      int i = y >= 0.5F ? 0 : 1;
      int j = getXSection(x);
      return j + i * 3;
   }

   private static int getXSection(float f) {
      if (f < 0.375F) {
         return 2;
      } else {
         return f < 0.6875F ? 1 : 0;
      }
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE.get(state.getValue(FACING));
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      list.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
   }
}
