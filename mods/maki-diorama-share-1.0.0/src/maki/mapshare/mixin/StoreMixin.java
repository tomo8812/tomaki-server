package maki.mapshare.mixin;

import dev.diorama.waypoint.Waypoint;
import dev.diorama.waypoint.WaypointStore;
import java.util.List;
import maki.mapshare.ClientSharing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WaypointStore.class, remap = false)
public abstract class StoreMixin {
   @Inject(method = "all()Ljava/util/List;", at = @At("RETURN"), cancellable = true, remap = false)
   private void maki$all(CallbackInfoReturnable<List<Waypoint>> var1) {
      var1.setReturnValue(ClientSharing.overlay((WaypointStore)this, (List<Waypoint>)var1.getReturnValue(), null));
   }

   @Inject(method = "in(Ljava/lang/String;)Ljava/util/List;", at = @At("RETURN"), cancellable = true, remap = false)
   private void maki$in(String var1, CallbackInfoReturnable<List<Waypoint>> var2) {
      var2.setReturnValue(ClientSharing.overlay((WaypointStore)this, (List<Waypoint>)var2.getReturnValue(), var1));
   }

   @Inject(method = "setVisible(Ldev/diorama/waypoint/Waypoint;Z)V", at = @At("HEAD"), cancellable = true, remap = false)
   private void maki$visible(Waypoint var1, boolean var2, CallbackInfo var3) {
      if (ClientSharing.shared(var1)) {
         ClientSharing.visible(var1, var2);
         var3.cancel();
      }
   }

   @Inject(method = "remove(Ldev/diorama/waypoint/Waypoint;)V", at = @At("HEAD"), cancellable = true, remap = false)
   private void maki$protect(Waypoint var1, CallbackInfo var2) {
      if (ClientSharing.shared(var1)) {
         var2.cancel();
      }
   }
}
