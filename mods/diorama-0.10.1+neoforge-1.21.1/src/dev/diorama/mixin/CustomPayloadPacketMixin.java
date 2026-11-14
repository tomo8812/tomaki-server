package dev.diorama.mixin;

import dev.diorama.XaeroChannels;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundCustomPayloadPacket.class)
public abstract class CustomPayloadPacketMixin {
   @Inject(method = "handle(Lnet/minecraft/network/protocol/common/ClientCommonPacketListener;)V", at = @At("HEAD"))
   private void diorama$queuePeeked(ClientCommonPacketListener listener, CallbackInfo ci) {
      XaeroChannels.queuePeeked(((ClientboundCustomPayloadPacket)this).payload(), listener);
   }
}
