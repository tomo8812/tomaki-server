package maki.mapshare.mixin;

import dev.diorama.waypoint.Waypoint;
import maki.mapshare.ClientSharing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Waypoint.class, remap = false)
public abstract class WaypointMixin {
   @Inject(method = "editable()Z", at = @At("HEAD"), cancellable = true, remap = false)
   private void maki$readOnly(CallbackInfoReturnable<Boolean> callback) {
      if (ClientSharing.shared((Waypoint)this)) {
         callback.setReturnValue(false);
      }
   }
}
