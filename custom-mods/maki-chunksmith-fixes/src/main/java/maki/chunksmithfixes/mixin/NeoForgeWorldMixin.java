package maki.chunksmithfixes.mixin;

import maki.chunksmithfixes.NativeChunkResult;
import net.minecraft.server.level.ChunkResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.kishku7.chunksmith.platform.NeoForgeWorld", remap = false)
public abstract class NeoForgeWorldMixin {
    @Inject(method = "lambda$getChunkAtAsync$11", at = @At("HEAD"), require = 1)
    private static void maki$requireFull(ChunkResult<?> result, CallbackInfoReturnable<Void> ci) {
        NativeChunkResult.complete(result);
    }
}
