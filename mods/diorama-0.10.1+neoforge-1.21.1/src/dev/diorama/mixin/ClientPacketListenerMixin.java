package dev.diorama.mixin;

import dev.diorama.MapSession;
import java.util.BitSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData;
import net.neoforged.neoforge.client.ClientCommandHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
   @Inject(method = "applyLightData", at = @At("TAIL"))
   private void diorama$onLightApplied(int x, int z, ClientboundLightUpdatePacketData lightData, CallbackInfo ci) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level != null) {
         BitSet blockLight = (BitSet)lightData.getBlockYMask().clone();
         blockLight.or(lightData.getEmptyBlockYMask());
         MapSession.lightUpdated(mc.level, x, z, blockLight);
      }
   }

   @Inject(method = "handleBlockEntityData", at = @At("TAIL"))
   private void diorama$onBlockEntityData(ClientboundBlockEntityDataPacket packet, CallbackInfo ci) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level != null) {
         MapSession.blockEntityChanged(mc.level, packet.getPos());
      }
   }

   @Inject(method = "sendUnsignedCommand", at = @At("HEAD"), cancellable = true)
   private void diorama$runClientCommand(String command, CallbackInfoReturnable<Boolean> cir) {
      if (ClientCommandHandler.runCommand(command)) {
         cir.setReturnValue(true);
      }
   }
}
