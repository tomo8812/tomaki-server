package org.millenaire.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;

public class CalabashItem extends Item {
   public CalabashItem(Properties properties) {
      super(properties);
   }

   public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand usedHand) {
      if (!(target instanceof Cow) && !(target instanceof Ghast)) {
         return InteractionResult.PASS;
      }

      if (!target.isBaby() && !player.getAbilities().instabuild) {
         player.playSound(target instanceof Cow ? SoundEvents.COW_MILK : SoundEvents.GHAST_SCREAM, 1.0F, 1.0F);
         if (!player.level().isClientSide()) {
            ItemStack filled = ItemUtils.createFilledResult(stack, player, new ItemStack((ItemLike)ModItems.MILK_CALABASH.get()), false);
            player.setItemInHand(usedHand, filled);
         }

         return InteractionResult.sidedSuccess(player.level().isClientSide());
      } else {
         return InteractionResult.PASS;
      }
   }
}
