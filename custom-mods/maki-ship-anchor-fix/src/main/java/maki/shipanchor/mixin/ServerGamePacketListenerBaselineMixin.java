package maki.shipanchor.mixin;

import maki.shipanchor.ShipAnchorState;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 500, remap = false)
public abstract class ServerGamePacketListenerBaselineMixin {
   @Shadow
   public ServerPlayer player;
   @Shadow
   private double firstGoodX;
   @Shadow
   private double firstGoodY;
   @Shadow
   private double firstGoodZ;
   @Shadow
   private double lastGoodX;
   @Shadow
   private double lastGoodY;
   @Shadow
   private double lastGoodZ;

   @Inject(
      method = "handleMovePlayer(Lnet/minecraft/network/protocol/game/ServerboundMovePlayerPacket;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V",
         shift = Shift.AFTER
      ),
      remap = false
   )
   private void maki$realignAfterShipAnchor(ServerboundMovePlayerPacket var1, CallbackInfo var2) {
      if (this.player != null && !this.player.isPassenger() && !this.player.isSleeping() && ((ShipAnchorState)this.player).maki$consumeShipAnchorHandoff()) {
         this.firstGoodX = this.lastGoodX = this.player.getX();
         this.firstGoodY = this.lastGoodY = this.player.getY();
         this.firstGoodZ = this.lastGoodZ = this.player.getZ();
      }
   }
}
