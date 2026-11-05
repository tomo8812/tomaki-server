package maki.airship.mixin;

import maki.airship.ShipShiftDecision;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

@Mixin(value = LocalPlayer.class, remap = false)
public abstract class ShipShiftMixin {
   @Shadow
   @Final
   public ClientPacketListener connection;
   @Shadow
   private boolean wasShiftKeyDown;
   @Unique
   private boolean maki$wasShipMount;

   @Inject(method = "tick()V", at = @At("TAIL"), remap = false)
   private void maki$syncShipShift(CallbackInfo var1) {
      LocalPlayer var2 = (LocalPlayer)this;
      boolean var3 = var2.getVehicle() instanceof ShipMountingEntity;
      boolean var4 = var2.isShiftKeyDown();
      if (ShipShiftDecision.send(var3, this.maki$wasShipMount, var4, this.wasShiftKeyDown)) {
         this.connection.send(new ServerboundPlayerCommandPacket(var2, var4 ? Action.PRESS_SHIFT_KEY : Action.RELEASE_SHIFT_KEY));
         this.wasShiftKeyDown = var4;
      }

      this.maki$wasShipMount = var3;
   }
}
