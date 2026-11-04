package net.satisfy.farm_and_charm.core.block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.client.FarmAndCharmClient;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import net.satisfy.foundation.block.FacingBlock;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;

public class PetBowlBlock extends FacingBlock implements EntityBlock {
   public static final EnumProperty<GeneralUtil.FoodType> FOOD_TYPE = EnumProperty.create("food_type", GeneralUtil.FoodType.class);
   public static final BooleanProperty HAS_NAME_TAG = BooleanProperty.create("has_name_tag");
   private static final Supplier<VoxelShape> voxelShapeSupplier = () -> Shapes.box(0.25, 0.0, 0.25, 0.75, 0.25, 0.75);
   public static final Map<Direction, VoxelShape> SHAPE = new HashMap<>();

   public PetBowlBlock(Properties properties) {
      super(properties.mapColor(MapColor.COLOR_BROWN));
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH)).setValue(FOOD_TYPE, GeneralUtil.FoodType.NONE))
            .setValue(HAS_NAME_TAG, false)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{FOOD_TYPE, HAS_NAME_TAG});
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult
   ) {
      if (level.getBlockEntity(pos) instanceof PetBowlBlockEntity entity) {
         if (heldItem.is(Items.SHEARS) && (Boolean)state.getValue(HAS_NAME_TAG)) {
            if (!level.isClientSide) {
               level.setBlock(pos, (BlockState)state.setValue(HAS_NAME_TAG, false), 3);
               popResource(level, pos, new ItemStack(Items.NAME_TAG));
               if (!player.getAbilities().instabuild) {
                  heldItem.hurtAndBreak(1, player, player.getEquipmentSlotForItem(player.getItemInHand(hand)));
               }
            }

            return ItemInteractionResult.SUCCESS;
         } else if (heldItem.is(Items.NAME_TAG) && !(Boolean)state.getValue(HAS_NAME_TAG)) {
            if (!level.isClientSide) {
               level.setBlock(pos, (BlockState)state.setValue(HAS_NAME_TAG, true), 3);
               if (!player.getAbilities().instabuild) {
                  heldItem.shrink(1);
               }
            }

            return ItemInteractionResult.SUCCESS;
         } else if ((Boolean)state.getValue(HAS_NAME_TAG) && player.isShiftKeyDown()) {
            if (level.isClientSide) {
               FarmAndCharmClient.openPetBowlScreen(entity);
            }

            return ItemInteractionResult.SUCCESS;
         } else {
            if (!level.isClientSide && hand == InteractionHand.MAIN_HAND && state.getValue(FOOD_TYPE) == GeneralUtil.FoodType.NONE) {
               GeneralUtil.FoodType type = this.getFoodType(heldItem);
               if (type != GeneralUtil.FoodType.NONE) {
                  level.setBlock(pos, (BlockState)state.setValue(FOOD_TYPE, type), 3);
                  if (!player.getAbilities().instabuild) {
                     heldItem.shrink(1);
                  }

                  entity.setItem(0, new ItemStack(heldItem.getItem()));
                  entity.onFed(heldItem);
                  return ItemInteractionResult.SUCCESS;
               }
            }

            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   private GeneralUtil.FoodType getFoodType(ItemStack stack) {
      if (stack.is((Item)ObjectRegistry.CAT_FOOD.get())) {
         return GeneralUtil.FoodType.CAT;
      } else {
         return stack.is((Item)ObjectRegistry.DOG_FOOD.get()) ? GeneralUtil.FoodType.DOG : GeneralUtil.FoodType.NONE;
      }
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new PetBowlBlockEntity(pos, state);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE.get(state.getValue(FACING));
   }

   @NotNull
   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      int earthy = 16767334;
      int gold = 16766720;
      list.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
      list.add(Component.empty());
      if (Screen.hasShiftDown()) {
         list.add(Component.translatable("tooltip.farm_and_charm.pet_bowl.info_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
         list.add(Component.translatable("tooltip.farm_and_charm.pet_bowl.info_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
      } else {
         list.add(
            Component.translatable(
                  "tooltip.farm_and_charm.tooltip_information.hold",
                  new Object[]{Component.literal("[SHIFT]").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(gold)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy)))
         );
      }
   }

   static {
      for (Direction direction : Plane.HORIZONTAL) {
         SHAPE.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
      }
   }
}
