package com.chaosthedude.naturescompass.util;

import com.chaosthedude.naturescompass.NaturesCompass;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemUtils {
   public static ItemStack getHeldNatureCompass(Player player) {
      return getHeldItem(player, NaturesCompass.naturesCompass);
   }

   public static ItemStack getHeldItem(Player player, Item item) {
      if (!player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() == item) {
         return player.getMainHandItem();
      } else {
         return !player.getOffhandItem().isEmpty() && player.getOffhandItem().getItem() == item ? player.getOffhandItem() : ItemStack.EMPTY;
      }
   }
}
