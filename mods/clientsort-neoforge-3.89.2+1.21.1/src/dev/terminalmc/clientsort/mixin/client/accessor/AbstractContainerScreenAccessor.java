package dev.terminalmc.clientsort.mixin.client.accessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
   @Accessor("leftPos")
   int clientsort$getLeftPos();

   @Accessor("topPos")
   int clientsort$getTopPos();

   @Accessor("imageWidth")
   int clientsort$getImageWidth();

   @Accessor("playerInventoryTitle")
   Component clientsort$getPlayerInventoryTitle();

   @Invoker("slotClicked")
   void clientsort$slotClicked(Slot var1, int var2, int var3, ClickType var4);

   @Invoker("isHovering")
   boolean clientsort$isHovering(Slot var1, double var2, double var4);
}
