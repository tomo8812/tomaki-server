package maki.millenairelod.mixin;

import com.kishku7.chunksmith.lod.HorizonGuard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.millenaire.world.SiteHunter;
import org.millenaire.world.VillageTerrainMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Millenaire 9.1.0's SiteHunter invokes VillageSpawner.spawnVillage directly,
 * bypassing VillageSpawnQueue.trySpawnVillage and its deferred-candidate guard.
 *
 * Keep the upstream terrain scoring and site selection. Treat a placement area
 * as not yet ready while Chunksmith would discard its selected center chunk.
 * SiteHunter's native waitingForLoad state retains the candidate and retries
 * it when the area is both loaded and no longer disposable.
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
        if (HorizonGuard.drop(level, new ChunkPos(centerX >> 4, centerZ >> 4))) {
            return false;
        }
        return VillageTerrainMap.placementAreaLoaded(level, centerX, centerZ, radius);
    }
}
