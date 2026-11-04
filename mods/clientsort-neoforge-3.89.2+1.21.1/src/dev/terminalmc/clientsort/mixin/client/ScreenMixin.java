package dev.terminalmc.clientsort.mixin.client;

import dev.terminalmc.clientsort.client.gui.TriggerButtonManager;
import dev.terminalmc.clientsort.mixin.client.accessor.ScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
   @Inject(
      method = "init(Lnet/minecraft/client/Minecraft;II)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;init()V", shift = Shift.AFTER)
   )
   private void afterInit(CallbackInfo ci) {
      if (!(Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen)) {
         this.clientsort$afterInit();
      }
   }

   @Inject(method = "rebuildWidgets", at = @At("RETURN"))
   private void afterRebuildWidgets(CallbackInfo ci) {
      this.clientsort$afterInit();
   }

   @Unique
   private void clientsort$afterInit() {
      TriggerButtonManager.getContainerButtons().forEach(b -> ((ScreenAccessor)this).clientsort$removeWidget(b));
      TriggerButtonManager.getPlayerButtons().forEach(b -> ((ScreenAccessor)this).clientsort$removeWidget(b));
      TriggerButtonManager.afterScreenInit((Screen)this);
   }
}
