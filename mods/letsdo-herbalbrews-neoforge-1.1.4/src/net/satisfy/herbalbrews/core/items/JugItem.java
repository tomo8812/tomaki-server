package net.satisfy.herbalbrews.core.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
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
import net.satisfy.herbalbrews.core.util.HerbalBrewsTiers;
import org.jetbrains.annotations.NotNull;

public class JugItem extends BlockItem {
   public static final HerbalBrewsTiers GLASS_TIER = HerbalBrewsTiers.GLASS;

   public JugItem(Block block, Properties properties) {
      super(
         block,
         properties.durability(GLASS_TIER.getUses())
            .attributes(
               ItemAttributeModifiers.builder()
                  .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.0, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                  .add(
                     Attributes.ATTACK_DAMAGE,
                     new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 8.0F + GLASS_TIER.getAttackDamageBonus(), Operation.ADD_VALUE),
                     EquipmentSlotGroup.MAINHAND
                  )
                  .build()
            )
      );
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
      return true;
   }

   public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
      return GLASS_TIER.getRepairIngredient().test(repair) || super.isValidRepairItem(toRepair, repair);
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

   public int getEnchantmentValue() {
      return GLASS_TIER.getEnchantmentValue();
   }
}
