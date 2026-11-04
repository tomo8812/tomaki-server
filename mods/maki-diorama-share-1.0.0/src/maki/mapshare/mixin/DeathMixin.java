package maki.mapshare.mixin;

import dev.diorama.waypoint.Waypoints;
import maki.mapshare.ClientSharing;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Waypoints.class, remap = false)
public abstract class DeathMixin {
   @Inject(method = "died(Lnet/minecraft/client/player/LocalPlayer;)V", at = @At("HEAD"), cancellable = true, remap = false)
   private static void maki$serverDeath(LocalPlayer var0, CallbackInfo var1) {
      if (ClientSharing.serverDeaths()) {
         var1.cancel();
      }
   }
}
