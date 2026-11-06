package maki.idlepregen.mixin;

import net.minecraft.server.level.ServerLevel;
import org.millenaire.village.Village;
import org.millenaire.village.VillageRehydrator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = VillageRehydrator.class, remap = false)
public abstract class VillageRehydratorMixin {
    @Redirect(
            method = "rehydrateAll",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/millenaire/village/Village;rebuildWaypointGraph(Lnet/minecraft/server/level/ServerLevel;)V"))
    private static void maki$routeStartupGraphThroughGuard(Village village, ServerLevel level) {
        if (!level.getServer().isDedicatedServer()) {
            village.rebuildWaypointGraph(level);
            return;
        }

        // Rehydration calls rebuildWaypointGraph directly, bypassing the normal dirty/throttle path.
        // Keep the graph dirty and route it through the same guarded path so zero-player startup
        // cannot perform an unbudgeted synchronous rebuild.
        village.markWaypointGraphDirty();
        village.rebuildWaypointGraphIfStale(level, 0L);
    }
}
