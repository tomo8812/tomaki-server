package dev.terminalmc.clientsort.mixin.slot;

import dev.terminalmc.clientsort.util.inject.ISlot;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Slot.class)
public abstract class SlotMixin implements ISlot {
   @Shadow
   public int index;

   @Shadow
   public abstract int getContainerSlot();

   @Override
   public int clientsort$getIndexInContainer() {
      return this.getContainerSlot();
   }

   @Override
   public int clientsort$getIndexInMenu() {
      return this.index;
   }
}
