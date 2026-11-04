package dev.terminalmc.clientsort.mixin.client;

import dev.terminalmc.clientsort.client.ClientSort;
import dev.terminalmc.clientsort.client.interaction.InteractionManager;
import dev.terminalmc.clientsort.client.order.CreativeSearchOrder;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
   @Inject(method = "clientSideCloseContainer", at = @At("HEAD"))
   public void beforeContainerClose(CallbackInfo callbackInfo) {
      InteractionManager.clear();
      ClientSort.operatingClient = false;
      ClientSort.clientOpQueue.clear();
   }

   @Inject(method = "setPermissionLevel", at = @At("RETURN"))
   public void afterPermissionLevelChange(int level, CallbackInfo ci) {
      if (!ClientSort.searchOrderUpdated) {
         ClientSort.searchOrderUpdated = true;
         CreativeSearchOrder.tryRefreshStackPositionMap();
      }
   }
}
