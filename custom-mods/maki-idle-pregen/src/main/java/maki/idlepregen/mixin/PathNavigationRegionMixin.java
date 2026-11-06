package maki.idlepregen.mixin;

import maki.idlepregen.MillenaireInitTelemetry;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PathNavigationRegion.class)
public abstract class PathNavigationRegionMixin {
    @Inject(method = "getChunk", at = @At("RETURN"))
    private void maki$countEmptyChunkAccess(
            int chunkX,
            int chunkZ,
            CallbackInfoReturnable<ChunkAccess> cir) {
        if (cir.getReturnValue() instanceof EmptyLevelChunk) {
            MillenaireInitTelemetry.recordEmptyChunkAccess();
        }
    }
}
