package net.caffeinemc.mods.lithium.mixin.world.chunk_access;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import net.caffeinemc.mods.lithium.common.world.ChunkLoadTricks;
import net.caffeinemc.mods.lithium.common.world.chunk.ChunkHolderExtended;
import net.minecraft.Util;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.ServerChunkCache.MainThreadExecutor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin {
   @Shadow
   @Final
   private MainThreadExecutor mainThreadProcessor;
   @Shadow
   @Final
   private DistanceManager distanceManager;
   @Shadow
   @Final
   public ChunkMap chunkMap;
   @Shadow
   @Final
   Thread mainThread;
   private long time;
   private final long[] cacheKeys = new long[4];
   private final ChunkAccess[] cacheChunks = new ChunkAccess[4];

   @Shadow
   protected abstract ChunkHolder getVisibleChunkIfPresent(long var1);

   @Shadow
   protected abstract boolean chunkAbsent(ChunkHolder var1, int var2);

   @Shadow
   public abstract void tick(BooleanSupplier var1, boolean var2);

   @Shadow
   abstract boolean runDistanceManagerUpdates();

   @Inject(method = "tick", at = @At("HEAD"))
   private void preTick(BooleanSupplier shouldKeepTicking, boolean tickChunks, CallbackInfo ci) {
      this.time++;
   }

   @Overwrite
   public ChunkAccess getChunk(int x, int z, ChunkStatus status, boolean create) {
      if (Thread.currentThread() != this.mainThread) {
         return this.getChunkOffThread(x, z, status, create);
      }

      long[] cacheKeys = this.cacheKeys;
      long key = createCacheKey(x, z, status);

      for (int i = 0; i < 4; i++) {
         if (key == cacheKeys[i]) {
            ChunkAccess chunk = this.cacheChunks[i];
            if (chunk != null || !create) {
               return chunk;
            }
         }
      }

      ChunkAccess chunk = this.getChunkBlocking(x, z, status, create);
      if (chunk != null) {
         this.addToCache(key, chunk);
      } else if (create) {
         throw new IllegalStateException("Chunk not there when requested");
      }

      return chunk;
   }

   @Unique
   private ChunkAccess getChunkOffThread(int x, int z, ChunkStatus status, boolean create) {
      return CompletableFuture.<ChunkAccess>supplyAsync(() -> this.getChunk(x, z, status, create), this.mainThreadProcessor).join();
   }

   @Unique
   private ChunkAccess getChunkBlocking(int x, int z, ChunkStatus leastStatus, boolean create) {
      long key = ChunkPos.asLong(x, z);
      int level = ChunkLevel.byStatus(leastStatus);
      ChunkHolder holder = this.getVisibleChunkIfPresent(key);
      ChunkAccess chunkAccess = ChunkLoadTricks.tryRetrieveCurrentlyLoading(holder);
      if (chunkAccess != null) {
         return chunkAccess;
      }

      if (this.chunkAbsent(holder, level)) {
         if (!create) {
            return null;
         }

         this.createChunkLoadTicket(x, z, level);
         this.runDistanceManagerUpdates();
         holder = this.getVisibleChunkIfPresent(key);
         if (this.chunkAbsent(holder, level)) {
            throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("No chunk holder after ticket has been added"));
         }
      } else if (create && ((ChunkHolderExtended)holder).lithium$updateLastAccessTime(this.time)) {
         this.createChunkLoadTicket(x, z, level);
      }

      if (!((GenerationChunkHolderAccessor)holder).invokeCannotBeLoaded(leastStatus)) {
         CompletableFuture<ChunkResult<ChunkAccess>> directlyAccessedFuture = ((GenerationChunkHolderAccessor)holder)
            .lithium$getChunkFuturesByStatus()
            .get(leastStatus.getIndex());
         if (directlyAccessedFuture != null && directlyAccessedFuture.isDone()) {
            ChunkAccess chunk = (ChunkAccess)directlyAccessedFuture.join().orElse(null);
            if (chunk != null) {
               return chunk;
            }
         }
      }

      CompletableFuture<ChunkResult<ChunkAccess>> loadFuture = holder.scheduleChunkGenerationTask(leastStatus, this.chunkMap);
      if (!loadFuture.isDone()) {
         this.mainThreadProcessor.managedBlock(loadFuture::isDone);
      }

      return (ChunkAccess)loadFuture.join().orElse(null);
   }

   private void createChunkLoadTicket(int x, int z, int level) {
      ChunkPos chunkPos = new ChunkPos(x, z);
      this.distanceManager.addTicket(TicketType.UNKNOWN, chunkPos, level, chunkPos);
   }

   private static long createCacheKey(int chunkX, int chunkZ, ChunkStatus status) {
      return chunkX & 268435455L | (chunkZ & 268435455L) << 28 | (long)status.getIndex() << 56;
   }

   private void addToCache(long key, ChunkAccess chunk) {
      for (int i = 3; i > 0; i--) {
         this.cacheKeys[i] = this.cacheKeys[i - 1];
         this.cacheChunks[i] = this.cacheChunks[i - 1];
      }

      this.cacheKeys[0] = key;
      this.cacheChunks[0] = chunk;
   }

   @Inject(method = "clearCache()V", at = @At("HEAD"))
   private void onCachesCleared(CallbackInfo ci) {
      Arrays.fill(this.cacheKeys, Long.MAX_VALUE);
      Arrays.fill(this.cacheChunks, null);
   }
}
