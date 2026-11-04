package de.maxhenkel.gravestone.corelib.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LockedSlot extends Slot {
   protected boolean inputLocked;
   protected boolean outputLocked;

   public LockedSlot(Container inventoryIn, int index, int xPosition, int yPosition, boolean inputLocked, boolean outputLocked) {
      super(inventoryIn, index, xPosition, yPosition);
      this.inputLocked = inputLocked;
      this.outputLocked = outputLocked;
   }

   public LockedSlot(Container inventoryIn, int index, int xPosition, int yPosition) {
      this(inventoryIn, index, xPosition, yPosition, false, true);
   }

   public boolean mayPickup(Player playerIn) {
      return this.outputLocked ? false : super.mayPickup(playerIn);
   }

   public boolean mayPlace(ItemStack stack) {
      return this.inputLocked ? false : super.mayPlace(stack);
   }
}
