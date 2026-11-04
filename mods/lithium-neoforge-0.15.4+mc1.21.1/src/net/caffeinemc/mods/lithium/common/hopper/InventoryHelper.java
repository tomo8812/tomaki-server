package net.caffeinemc.mods.lithium.common.hopper;

import net.caffeinemc.mods.lithium.api.inventory.LithiumInventory;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class InventoryHelper {
   public static LithiumStackList getLithiumStackList(LithiumInventory inventory) {
      return inventory.getInventoryLithium() instanceof LithiumStackList lithiumStackList ? lithiumStackList : upgradeToLithiumStackList(inventory);
   }

   public static LithiumStackList getLithiumStackListOrNull(LithiumInventory inventory) {
      return inventory.getInventoryLithium() instanceof LithiumStackList lithiumStackList ? lithiumStackList : null;
   }

   private static LithiumStackList upgradeToLithiumStackList(LithiumInventory inventory) {
      inventory.generateLootLithium();
      NonNullList<ItemStack> stackList = inventory.getInventoryLithium();
      LithiumStackList lithiumStackList = new LithiumStackList(stackList, inventory.getMaxStackSize());
      inventory.setInventoryLithium(lithiumStackList);
      return lithiumStackList;
   }
}
