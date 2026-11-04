package dev.terminalmc.clientsort.mixin.client;

import dev.terminalmc.clientsort.client.ClientSort;
import dev.terminalmc.clientsort.client.config.Config;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
   @Inject(method = "handleLogin", at = @At("HEAD"))
   private void beforeLogin(ClientboundLoginPacket packet, CallbackInfo ci) {
      ClientSort.searchOrderUpdated = false;
      ClientSort.operatingClient = false;
      ClientSort.clientOpQueue.clear();
   }

   @Inject(method = "handleLogin", at = @At("RETURN"))
   private void afterLogin(ClientboundLoginPacket packet, CallbackInfo ci) {
      ClientSort.updateItemTags(Config.options());
      ClientSort.updateItemSets(Config.options());
   }
}
