package maki.idlepregen.mixin;

import maki.idlepregen.MillenaireInitTelemetry;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EmptyLevelChunk.class)
public abstract class EmptyLevelChunkMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void maki$countEmptyChunkCreation(
            Level level,
            ChunkPos chunkPos,
            Holder<Biome> biome,
            CallbackInfo ci) {
        MillenaireInitTelemetry.recordEmptyChunkCreation();
    }
}
