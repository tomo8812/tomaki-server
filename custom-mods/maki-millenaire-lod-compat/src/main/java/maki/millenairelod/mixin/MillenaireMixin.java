package maki.millenairelod.mixin;

import maki.millenairelod.Compat;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.millenaire.Millenaire;
import org.millenaire.world.VillageSpawnQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Millenaire.class, remap = false)
public abstract class MillenaireMixin {
    // Millenaire 9.1.0 performs markResolved, SiteSurvey and SiteHunter
    // bookkeeping in this lambda even for already-existing chunks. Never
    // cancel the method: doing so loses the new site-scoring data.
    @Redirect(
        method = "lambda$new$32",
        at = @At(value = "INVOKE",
            target = "Lorg/millenaire/world/VillageSpawnQueue;enqueue(Lnet/minecraft/core/BlockPos;)V"),
        require = 1
    )
    private static void compat$nativeEnqueue(VillageSpawnQueue queue, BlockPos at) {
        // onChunk at RETURN owns the native-candidate enqueue decision.
        // It also handles prior deferred candidates when isNew is false.
    }

    // 9.1.0's callback is (queue, pos, level, isNew), not the old order.
    @Inject(method = "lambda$new$32", at = @At("RETURN"), require = 1)
    private static void compat$chunk(VillageSpawnQueue queue, ChunkPos pos,
                                     ServerLevel level, boolean isNew, CallbackInfo ci) {
        Compat.onChunk(queue, pos, isNew, level);
    }
}
