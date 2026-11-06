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
    private void maki$all(CallbackInfoReturnable<List<Waypoint>> callback) {
        callback.setReturnValue(
            ClientSharing.overlay((WaypointStore) (Object) this, callback.getReturnValue(), null)
        );
    }

    @Inject(
        method = "in(Ljava/lang/String;)Ljava/util/List;",
        at = @At("RETURN"),
        cancellable = true,
        remap = false
    )
    private void maki$in(String dimension, CallbackInfoReturnable<List<Waypoint>> callback) {
        callback.setReturnValue(
            ClientSharing.overlay((WaypointStore) (Object) this, callback.getReturnValue(), dimension)
        );
    }

    @Inject(
        method = "setVisible(Ldev/diorama/waypoint/Waypoint;Z)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void maki$visible(Waypoint waypoint, boolean visible, CallbackInfo callback) {
        if (ClientSharing.shared(waypoint)) {
            ClientSharing.visible(waypoint, visible);
            callback.cancel();
        }
    }

    @Inject(
        method = "remove(Ldev/diorama/waypoint/Waypoint;)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void maki$protect(Waypoint waypoint, CallbackInfo callback) {
        if (ClientSharing.shared(waypoint)) {
            callback.cancel();
        }
    }
}
