package dev.terminalmc.clientsort.mixin.client;

import dev.terminalmc.clientsort.client.gui.TriggerButtonManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.Type;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends Screen {
   protected CreativeModeInventoryScreenMixin(Component title) {
      super(title);
   }

   @Inject(method = "selectTab", at = @At("RETURN"))
   private void afterSelectTab(CreativeModeTab tab, CallbackInfo ci) {
      TriggerButtonManager.getPlayerButtons().forEach(x$0 -> this.removeWidget(x$0));
      if (tab.getType().equals(Type.INVENTORY)) {
         TriggerButtonManager.afterScreenInit(this);
      }
   }
}
