package maki.airship.mixin;

import maki.airship.PassengerRecovery;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPacketListener.class, remap = false)
public abstract class ClientPassengerMixin {
   @Inject(
      method = "handleSetEntityPassengersPacket(Lnet/minecraft/network/protocol/game/ClientboundSetPassengersPacket;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V",
         shift = Shift.AFTER
      ),
      cancellable = true,
      remap = false
   )
   private void maki$deferPassengers(ClientboundSetPassengersPacket var1, CallbackInfo var2) {
      if (PassengerRecovery.defer((ClientPacketListener)this, var1)) {
         var2.cancel();
      }
   }
}
