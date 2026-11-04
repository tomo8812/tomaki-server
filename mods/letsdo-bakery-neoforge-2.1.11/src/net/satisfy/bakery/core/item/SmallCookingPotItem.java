package net.satisfy.bakery.core.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.bakery.core.block.SmallCookingPotBlock;
import net.satisfy.bakery.core.util.BakeryTiers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmallCookingPotItem extends BlockItem {
   public static final BakeryTiers COOKING_POT_TIER = BakeryTiers.IRON;

   public SmallCookingPotItem(Block block, Properties properties) {
      super(block, properties.durability(COOKING_POT_TIER.getUses()));
   }

   public static ItemAttributeModifiers createAttributes() {
      float attackDamage = 1.2F + COOKING_POT_TIER.getAttackDamageBonus();
      return ItemAttributeModifiers.builder()
         .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.0, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .build();
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
      return true;
   }

   public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
      return COOKING_POT_TIER.getRepairIngredient().test(repair) || super.isValidRepairItem(toRepair, repair);
   }

   public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
      if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
         stack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
      }

      return true;
   }

   @NotNull
   public InteractionResult place(BlockPlaceContext context) {
      Player player = context.getPlayer();
      return player != null && player.isShiftKeyDown() ? super.place(context) : InteractionResult.PASS;
   }

   public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
      super.inventoryTick(itemStack, level, entity, i, bl);
   }

   @Nullable
   protected BlockState getPlacementState(BlockPlaceContext blockPlaceContext) {
      BlockState state = super.getPlacementState(blockPlaceContext);
      if (state != null) {
         state = (BlockState)state.setValue(SmallCookingPotBlock.DAMAGE, blockPlaceContext.getItemInHand().getDamageValue());
      }

      return state;
   }

   public int getEnchantmentValue() {
      return COOKING_POT_TIER.getEnchantmentValue();
   }
}
