package com.archiangel.quadhotbar.compat;

import com.archiangel.quadhotbar.QuadHotbarNetwork;
import com.archiangel.quadhotbar.QuadHotbarPages;
import com.archiangel.quadhotbar.QuadHotbarServerConfig;
import com.mojang.logging.LogUtils;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class DeathPageRecovery {
   private DeathPageRecovery() {
   }

   public static NonNullList<ItemStack> restore(Player player, Object death, boolean main) {
      if (player instanceof ServerPlayer serverPlayer && QuadHotbarServerConfig.deathPageCompatibility() && death instanceof DeathPageAccess access) {
         int active = access.quadhotbar$activePage();
         int[] addresses = access.quadhotbar$pageSlots();
         if (active < 0 && Arrays.stream(addresses).noneMatch(DeathPageRecovery::valid)) {
            return null;
         }

         List<ItemStack> additional;
         List<ItemStack> inventory;
         List<ItemStack> armor;
         List<ItemStack> offhand;
         try {
            additional = items(death, "getAdditionalItems");
            inventory = main ? items(death, "getMainInventory") : List.of();
            armor = main ? items(death, "getArmorInventory") : List.of();
            offhand = main ? items(death, "getOffHandInventory") : List.of();
         } catch (ReflectiveOperationException exception) {
            LogUtils.getLogger().warn("Could not adapt death inventory recovery", exception);
            return null;
         }

         QuadHotbarNetwork.enforceServerLimits(serverPlayer);
         NonNullList<ItemStack> displaced = NonNullList.create();
         if (main) {
            int page = active >= 0 ? active : QuadHotbarPages.state(player).activePage;

            for (int slot = 0; slot < Math.min(inventory.size(), 36); slot++) {
               restoreSlot(player, inventory, slot, page * 36 + slot, displaced);
            }

            restoreEquipment(armor, player.getInventory().armor, displaced);
            restoreEquipment(offhand, player.getInventory().offhand, displaced);
         }

         for (int i = 0; i < additional.size(); i++) {
            int address = i < addresses.length ? addresses[i] : -1;
            if (valid(address)) {
               restoreSlot(player, additional, i, address, displaced);
            }
         }

         NonNullList<ItemStack> remainder = NonNullList.create();

         for (ItemStack stack : displaced) {
            collectRemainder(player, stack, remainder);
         }

         for (ItemStack stack : additional) {
            collectRemainder(player, stack, remainder);
         }

         for (int i = 0; i < additional.size(); i++) {
            additional.set(i, ItemStack.EMPTY);
         }

         for (int i = 0; i < remainder.size(); i++) {
            if (i < additional.size()) {
               additional.set(i, (ItemStack)remainder.get(i));
            } else {
               additional.add((ItemStack)remainder.get(i));
            }
         }

         int[] remainingAddresses = new int[additional.size()];
         Arrays.fill(remainingAddresses, -1);
         access.quadhotbar$setPageSlots(remainingAddresses);
         player.getInventory().setChanged();
         QuadHotbarNetwork.sendPageSync(serverPlayer);
         return remainder;
      } else {
         return null;
      }
   }

   private static List<ItemStack> items(Object death, String method) throws ReflectiveOperationException {
      return (List<ItemStack>)death.getClass().getMethod(method).invoke(death);
   }

   private static boolean valid(int address) {
      return address >= 0 && address < 3600;
   }

   private static void restoreSlot(Player player, List<ItemStack> source, int index, int address, List<ItemStack> displaced) {
      ItemStack stack = source.get(index);
      if (!stack.isEmpty()) {
         int page = address / 36;
         int slot = address % 36;
         ItemStack previous = QuadHotbarPages.getPageItem(player, page, slot);
         source.set(index, ItemStack.EMPTY);
         QuadHotbarPages.setPageItem(player, page, slot, stack);
         if (!previous.isEmpty() && previous != stack) {
            displaced.add(previous);
         }
      }
   }

   private static void restoreEquipment(List<ItemStack> source, List<ItemStack> target, List<ItemStack> displaced) {
      for (int slot = 0; slot < Math.min(source.size(), target.size()); slot++) {
         ItemStack stack = source.get(slot);
         if (!stack.isEmpty()) {
            ItemStack previous = target.get(slot);
            source.set(slot, ItemStack.EMPTY);
            target.set(slot, stack);
            if (!previous.isEmpty() && previous != stack) {
               displaced.add(previous);
            }
         }
      }
   }

   private static void collectRemainder(Player player, ItemStack stack, NonNullList<ItemStack> remainder) {
      if (!stack.isEmpty()) {
         QuadHotbarPages.addPickup(player, stack);
         if (!stack.isEmpty()) {
            remainder.add(stack);
         }
      }
   }
}
