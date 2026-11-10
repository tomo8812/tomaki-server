package maki.millenairelod.mixin;

import maki.millenairelod.VillagePlacementReadiness;
import net.minecraft.server.level.ServerLevel;
import org.millenaire.world.SiteHunter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Millenaire 9.1.0's SiteHunter invokes VillageSpawner.spawnVillage directly,
 * bypassing VillageSpawnQueue.trySpawnVillage and its deferred-candidate guard.
 *
 * Keep the upstream terrain scoring and site selection. Treat a placement area
 * as not ready until a player has acquired the FULL area and every chunk
 * is permitted to persist normally, including cache promotion/handoff.
 * SiteHunter's native waitingForLoad retains and retries the candidate.
 */
@Mixin(value = SiteHunter.class, remap = false)
public abstract class SiteHunterMixin {
    @Redirect(
        method = "attempt",
        at = @At(
            value = "INVOKE",
            target = "Lorg/millenaire/world/VillageTerrainMap;placementAreaLoaded(Lnet/minecraft/server/level/ServerLevel;III)Z"
        ),
        require = 1
    )
    private boolean maki$waitForRetainedArea(ServerLevel level, int centerX, int centerZ, int radius) {
        return VillagePlacementReadiness.areaReady(level, centerX, centerZ, radius);
    }
}
