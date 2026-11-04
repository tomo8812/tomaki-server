package dev.terminalmc.clientsort.mixin.client.slot;

import dev.terminalmc.clientsort.util.inject.ISlot;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$SlotWrapper")
public abstract class CreativeSlotMixin implements ISlot {
   @Shadow
   @Final
   Slot target;

   @Override
   public int clientsort$getIndexInContainer() {
      return ((ISlot)this.target).clientsort$getIndexInContainer();
   }

   @Override
   public int clientsort$getIndexInMenu() {
      return ((ISlot)this.target).clientsort$getIndexInMenu();
   }
}
