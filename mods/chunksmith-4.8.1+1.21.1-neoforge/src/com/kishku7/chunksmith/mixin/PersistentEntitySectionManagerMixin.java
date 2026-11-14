package com.kishku7.chunksmith.mixin;

import com.kishku7.chunksmith.util.Debug;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.util.thread.StrictQueue.IntRunnable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.chunk.storage.IOWorker;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import net.minecraft.world.level.entity.ChunkEntities;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentEntitySectionManager.class)
public abstract class PersistentEntitySectionManagerMixin {
   @Unique
   private static final Logger CHUNKY$LOG = LogUtils.getLogger();
   @Unique
   private static final long CHUNKY$DIAG_INTERVAL_MS = 5000L;
   @Unique
   private final AtomicLong chunksmith$fastHits = new AtomicLong();
   @Unique
   private final AtomicLong chunksmith$vanillaFalls = new AtomicLong();
   @Unique
   private long chunksmith$lastDiag = 0L;

   @Redirect(
      method = "requestChunkLoad",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/entity/EntityPersistentStorage;loadEntities(Lnet/minecraft/world/level/ChunkPos;)Ljava/util/concurrent/CompletableFuture;"
      )
   )
   private CompletableFuture chunksmith$skipReadForFreshChunks(EntityPersistentStorage permanentStorage, ChunkPos pos) {
      if (!(permanentStorage instanceof EntityStorage)) {
         return permanentStorage.loadEntities(pos);
      }

      ProcessorMailbox<IntRunnable> executor;
      Map<?, ?> pendingWrites;
      Path entityRegionFolder;
      try {
         SimpleRegionStorage simpleRegionStorage = ((EntityStorageAccessor)permanentStorage).chunksmith$getSimpleRegionStorage();
         IOWorker rawWorker = ((SimpleRegionStorageAccessor)simpleRegionStorage).chunksmith$getWorker();
         IOWorkerAccessor worker = (IOWorkerAccessor)rawWorker;
         executor = worker.chunksmith$getMailbox();
         pendingWrites = worker.chunksmith$getPendingWrites();
         RegionFileStorage storage = worker.chunksmith$getStorage();
         entityRegionFolder = ((RegionFileStorageAccessor)storage).chunksmith$getFolder();
      } catch (Throwable introspectionFailed) {
         return permanentStorage.loadEntities(pos);
      }

      CompletableFuture<Boolean> probe = new CompletableFuture<>();
      executor.tell(new IntRunnable(0, () -> {
         boolean present;
         try {
            present = pendingWrites.containsKey(pos) || chunksmith$entitiesOnDisk(entityRegionFolder, pos);
         } catch (Throwable uncertain) {
            present = true;
         }

         if (present) {
            this.chunksmith$vanillaFalls.incrementAndGet();
         } else {
            this.chunksmith$fastHits.incrementAndGet();
         }

         probe.complete(present);
      }));
      return probe.thenCompose(present -> present ? permanentStorage.loadEntities(pos) : CompletableFuture.completedFuture(new ChunkEntities(pos, List.of())));
   }

   @Inject(method = "tick", at = @At("HEAD"), require = 0)
   private void chunksmith$debugTick(CallbackInfo ci) {
      if (Debug.ENABLED) {
         long now = System.currentTimeMillis();
         if (now - this.chunksmith$lastDiag >= 5000L) {
            this.chunksmith$lastDiag = now;

            try {
               String stats = ((PersistentEntitySectionManager)this).gatherStats();
               CHUNKY$LOG.info(
                  "[Chunksmith debug] fastHits={} vanillaFalls={} | known,visible,sections,loadStatuses,visibility,inbox,toUnload={}",
                  new Object[]{this.chunksmith$fastHits.get(), this.chunksmith$vanillaFalls.get(), stats}
               );
            } catch (Throwable var5) {
            }
         }
      }
   }

   @Unique
   private static boolean chunksmith$entitiesOnDisk(Path folder, ChunkPos pos) throws IOException {
      Path mca = folder.resolve("r." + pos.getRegionX() + "." + pos.getRegionZ() + ".mca");
      if (!Files.exists(mca)) {
         return false;
      }

      int slot = pos.getRegionLocalX() + pos.getRegionLocalZ() * 32;

      try (FileChannel channel = FileChannel.open(mca, StandardOpenOption.READ)) {
         ByteBuffer header = ByteBuffer.allocate(4096);

         while (header.hasRemaining() && channel.read(header) >= 0) {
         }

         return header.position() < (slot + 1) * 4 ? false : header.getInt(slot * 4) != 0;
      }
   }
}
