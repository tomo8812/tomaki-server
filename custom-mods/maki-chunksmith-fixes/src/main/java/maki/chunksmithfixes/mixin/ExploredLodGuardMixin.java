package maki.chunksmithfixes.mixin;

import com.kishku7.chunksmith.lod.CsLodPresenceIndex;
import com.kishku7.chunksmith.lod.LodSupport;
import maki.chunksmithfixes.LodRefreshPolicy;
import maki.chunksmithfixes.ExploredLodRefresh;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.kishku7.chunksmith.lod.ExploredLodGuard", remap = false)
public abstract class ExploredLodGuardMixin {
    @Inject(method = "onUnload", at = @At("HEAD"), cancellable = true, require = 1)
    private static void maki$skipUnchanged(ServerLevel level, LevelChunk chunk, CallbackInfo ci) {
        if (!chunk.isUnsaved()) {
            var index = LodSupport.presenceIndexFor(level.getServer(), LodSupport.dimensionId(level));
            if (index != null && (ExploredLodRefresh.pending(index.getRoot(), chunk.getPos().x, chunk.getPos().z)
                || !LodRefreshPolicy.shouldTakeOnUnload(false,
                    index.hasLod(chunk.getPos().x, chunk.getPos().z)))) ci.cancel();
        }
    }
    @Redirect(method = "take", at = @At(value = "INVOKE",
        target = "Lcom/kishku7/chunksmith/lod/CsLodPresenceIndex;markLod(II)V"), require = 1)
    private static void maki$deferPresence(CsLodPresenceIndex index, int x, int z) {}
}
