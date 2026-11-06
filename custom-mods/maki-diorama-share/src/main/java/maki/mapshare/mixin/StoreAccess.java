package maki.mapshare.mixin;

import dev.diorama.waypoint.Waypoint;
import dev.diorama.waypoint.WaypointStore;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = WaypointStore.class, remap = false)
public interface StoreAccess {
    @Accessor("waypoints")
    List<Waypoint> maki$raw();
}
