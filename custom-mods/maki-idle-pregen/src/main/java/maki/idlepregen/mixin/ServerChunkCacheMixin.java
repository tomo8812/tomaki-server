package maki.idlepregen.mixin;

import maki.idlepregen.MillenaireInitTelemetry;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin {
    @Inject(
            method = "getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/ChunkAccess;",
            at = @At("HEAD"))
    private void maki$countSynchronousChunkRequest(
            int chunkX,
            int chunkZ,
            ChunkStatus status,
            boolean load,
            CallbackInfoReturnable<ChunkAccess> cir) {
        ServerChunkCache self = (ServerChunkCache)(Object)this;
        boolean unloadedAtEntry = load && !self.hasChunk(chunkX, chunkZ);
        MillenaireInitTelemetry.recordChunkGet(load, unloadedAtEntry);
    }
}
