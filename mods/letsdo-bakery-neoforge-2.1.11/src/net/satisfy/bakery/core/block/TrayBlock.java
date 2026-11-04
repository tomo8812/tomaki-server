package net.satisfy.bakery.core.block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.bakery.core.registry.StorageTypeRegistry;
import net.satisfy.foundation.block.ClothColor;
import net.satisfy.foundation.util.DyeHelper;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;

public class TrayBlock extends CakeStandBlock {
   private static final Map<Direction, VoxelShape> SHAPES = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      Supplier<VoxelShape> voxelShapeSupplier = () -> {
         VoxelShape shape = Shapes.empty();
         return Shapes.join(shape, Shapes.box(0.1875, 0.0, 0.125, 0.8125, 0.3125, 0.875), BooleanOp.OR);
      };

      for (Direction direction : Plane.HORIZONTAL) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
      }
   });
   public static final EnumProperty<ClothColor> COLOR = EnumProperty.create("color", ClothColor.class);

   public TrayBlock(Properties settings) {
      super(settings);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(COLOR, ClothColor.NONE));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{COLOR});
   }

   @NotNull
   @Override
   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      return stack.getItem() instanceof DyeItem dye && ((ClothColor)state.getValue(COLOR)).color() != dye.getDyeColor()
         ? DyeHelper.apply(stack, dye.getDyeColor(), (BlockState)state.setValue(COLOR, ClothColor.of(dye.getDyeColor())), level, pos, player)
         : super.useItemOn(stack, state, level, pos, player, hand, hit);
   }

   @Override
   public int maxItemStack() {
      return 1;
   }

   @Override
   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      ClothColor color = (ClothColor)((BlockItemStateProperties)itemStack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY)).get(COLOR);
      if (color != null && color.color() != null) {
         list.add(Component.translatable("color.minecraft." + color.color().getName()).withStyle(Style.EMPTY.withColor(color.color().getTextColor())));
      }

      super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
   }

   @NotNull
   @Override
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPES.getOrDefault(state.getValue(FACING), Shapes.empty());
   }

   @Override
   public int size() {
      return 5;
   }

   @Override
   public ResourceLocation type() {
      return StorageTypeRegistry.TRAY;
   }

   @Override
   public Direction[] unAllowedDirections() {
      return new Direction[]{Direction.DOWN};
   }

   @Override
   public int getSection(Float f, Float y) {
      float oneS = 0.11111111F;
      int nSection = (int)(f / oneS);
      return 8 - nSection;
   }
}
