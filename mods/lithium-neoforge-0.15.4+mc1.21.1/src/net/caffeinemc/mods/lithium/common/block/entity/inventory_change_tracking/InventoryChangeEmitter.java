package net.caffeinemc.mods.lithium.common.block.entity.inventory_change_tracking;

import net.caffeinemc.mods.lithium.common.hopper.LithiumStackList;

public interface InventoryChangeEmitter {
   void lithium$emitStackListReplaced();

   void lithium$emitRemoved();

   void lithium$emitContentModified();

   void lithium$emitFirstComparatorAdded();

   void lithium$forwardContentChangeOnce(InventoryChangeListener var1, LithiumStackList var2);

   void lithium$forwardMajorInventoryChanges(InventoryChangeListener var1);

   void lithium$stopForwardingMajorInventoryChanges(InventoryChangeListener var1);

   default void emitCallbackReplaced() {
      this.lithium$emitRemoved();
   }
}
