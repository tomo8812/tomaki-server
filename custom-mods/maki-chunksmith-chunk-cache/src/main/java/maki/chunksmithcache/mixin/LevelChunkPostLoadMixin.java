package maki.chunksmithcache.mixin;

import maki.chunksmithcache.PromotionLedger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public abstract class LevelChunkPostLoadMixin {
    @Inject(method = "runPostLoad()V", at = @At("TAIL"), require = 1)
    private void maki$entitiesRestored(CallbackInfo ci) {
        LevelChunk chunk = (LevelChunk)(Object)this;
        if (chunk.getLevel() instanceof ServerLevel level) PromotionLedger.entitiesRestored(level, chunk.getPos());
    }
}
