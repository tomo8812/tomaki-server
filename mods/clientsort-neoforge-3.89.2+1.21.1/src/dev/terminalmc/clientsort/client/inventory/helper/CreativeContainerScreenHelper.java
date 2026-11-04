package dev.terminalmc.clientsort.client.inventory.helper;

import dev.terminalmc.clientsort.client.config.Config;
import dev.terminalmc.clientsort.client.inventory.Scope;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class CreativeContainerScreenHelper<T extends CreativeModeInventoryScreen> extends ContainerScreenHelper<T> {
   public CreativeContainerScreenHelper(T screen) {
      super(screen);
   }

   @Override
   public Scope getScope(Slot slot) {
      if (this.screen.isInventoryOpen()) {
         return super.getScope(slot);
      }

      if (slot.container instanceof Inventory && isHotbarSlot(slot)) {
         return switch (Config.options().hotbarScope) {
            case HOTBAR, INVENTORY -> Scope.PLAYER_INV_HOTBAR;
            case NONE -> Scope.INVALID;
         };
      } else {
         return Scope.INVALID;
      }
   }

   @Override
   public void translateSlotIds(int[] slotMapping) {
      if (!this.screen.isInventoryOpen()) {
         for (int i = 0; i < slotMapping.length; i++) {
            slotMapping[i] -= 9;
         }
      }
   }
}
