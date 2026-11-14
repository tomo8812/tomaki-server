package com.kishku7.chunksmith.mixin;

import java.util.concurrent.CompletableFuture;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerChunkCache.class)
public interface ServerChunkCacheMixin {
   @Invoker("getChunkFutureMainThread")
   CompletableFuture<ChunkResult<ChunkAccess>> invokeGetChunkFutureMainThread(int var1, int var2, ChunkStatus var3, boolean var4);

   @Invoker
   boolean invokeRunDistanceManagerUpdates();
}
