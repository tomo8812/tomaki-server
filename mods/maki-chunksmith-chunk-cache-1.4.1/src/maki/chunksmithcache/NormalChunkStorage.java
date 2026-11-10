package maki.chunksmithcache;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkPyramid;
import net.minecraft.world.level.chunk.status.ChunkStatus;

final class NormalChunkStorage {
   private static final Semaphore SCANS = new Semaphore(64);

   private NormalChunkStorage() {
   }

   static NormalChunkStorage.State inspect(ServerLevel level, ChunkPos pos) {
      return inspect(level, pos, null);
   }

   static NormalChunkStorage.State inspect(ServerLevel level, ChunkPos pos, NormalChunkStorage.RegionHeaders headers) {
      NormalChunkStorage.State header = headers == null ? headerState(level, pos) : headers.state(level, pos);
      if (header != NormalChunkStorage.State.PROTO) {
         return header;
      }

      try {
         return inspectStatus(level, pos).get(100L, TimeUnit.MILLISECONDS);
      } catch (InterruptedException interrupted) {
         Thread.currentThread().interrupt();
         return NormalChunkStorage.State.UNKNOWN;
      } catch (Exception unavailable) {
         return NormalChunkStorage.State.UNKNOWN;
      }
   }

   static CompletableFuture<NormalChunkStorage.State> inspectAsync(ServerLevel level, ChunkPos pos) {
      return inspectStatus(level, pos);
   }

   private static CompletableFuture<NormalChunkStorage.State> inspectStatus(ServerLevel level, ChunkPos pos) {
      if (!SCANS.tryAcquire()) {
         return CompletableFuture.completedFuture(NormalChunkStorage.State.UNKNOWN);
      }

      try {
         FieldSelector statusSelector = new FieldSelector(StringTag.TYPE, "Status");
         CollectFields collector = new CollectFields(new FieldSelector[]{statusSelector});
         return level.getChunkSource()
            .chunkScanner()
            .scanChunk(pos, collector)
            .handle(
               (ignored, error) -> {
                  SCANS.release();
                  return error != null
                     ? NormalChunkStorage.State.UNKNOWN
                     : (
                        collector.getResult() == null
                           ? NormalChunkStorage.State.ABSENT
                           : (collector.getResult() instanceof CompoundTag tag ? fromTag(tag) : NormalChunkStorage.State.UNKNOWN)
                     );
               }
            );
      } catch (Throwable unavailable) {
         SCANS.release();
         return CompletableFuture.completedFuture(NormalChunkStorage.State.UNKNOWN);
      }
   }

   static NormalChunkStorage.State fromTag(CompoundTag tag) {
      if (tag != null && tag.contains("Status")) {
         String status = tag.getString("Status");
         if (!"minecraft:full".equals(status) && !"full".equals(status)) {
            return status.isEmpty() ? NormalChunkStorage.State.UNKNOWN : NormalChunkStorage.State.PROTO;
         } else {
            return NormalChunkStorage.State.FULL;
         }
      } else {
         return NormalChunkStorage.State.UNKNOWN;
      }
   }

   static NormalChunkStorage.Scope snapshotBackfillOwnership(ServerLevel level, ChunkPos center) {
      return snapshotBackfillOwnership(level, center, () -> true);
   }

   static NormalChunkStorage.Scope snapshotBackfillOwnership(ServerLevel level, ChunkPos center, BooleanSupplier proceed) {
      int radius = ChunkPyramid.GENERATION_PYRAMID.getStepTo(ChunkStatus.FULL).accumulatedDependencies().getRadius();
      HashSet<Long> suppressed = new HashSet<>();
      int unknown = 0;
      NormalChunkStorage.RegionHeaders headers = new NormalChunkStorage.RegionHeaders();

      for (int x = center.x - radius; x <= center.x + radius; x++) {
         for (int z = center.z - radius; z <= center.z + radius; z++) {
            if (!proceed.getAsBoolean()) {
               return new NormalChunkStorage.Scope(suppressed, unknown + 1);
            }

            ChunkPos pos = new ChunkPos(x, z);
            NormalChunkStorage.State state = inspect(level, pos, headers);
            if (state == NormalChunkStorage.State.ABSENT || state == NormalChunkStorage.State.PROTO) {
               suppressed.add(pos.toLong());
            } else if (state == NormalChunkStorage.State.UNKNOWN) {
               unknown++;
            }
         }
      }

      return new NormalChunkStorage.Scope(suppressed, unknown);
   }

   static NormalChunkStorage.Scope authoritativeScope(ServerLevel level, ChunkPos center, BooleanSupplier proceed) {
      int radius = ChunkPyramid.GENERATION_PYRAMID.getStepTo(ChunkStatus.FULL).accumulatedDependencies().getRadius();
      ArrayList<ChunkPos> positions = new ArrayList<>();

      for (int x = center.x - radius; x <= center.x + radius; x++) {
         for (int z = center.z - radius; z <= center.z + radius; z++) {
            positions.add(new ChunkPos(x, z));
         }
      }

      HashSet<Long> owned = new HashSet<>();
      int unknown = 0;

      for (int base = 0; base < positions.size(); base += 16) {
         if (!proceed.getAsBoolean()) {
            return new NormalChunkStorage.Scope(owned, unknown + 1);
         }

         int end = Math.min(base + 16, positions.size());
         ArrayList<CompletableFuture<NormalChunkStorage.State>> futures = new ArrayList<>();

         for (int n = base; n < end; n++) {
            futures.add(inspectStatus(level, positions.get(n)));
         }

         try {
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).get(100L, TimeUnit.MILLISECONDS);
         } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            return new NormalChunkStorage.Scope(owned, unknown + 1);
         } catch (Exception var13) {
         }

         for (int n = base; n < end; n++) {
            NormalChunkStorage.State state = futures.get(n - base).getNow(NormalChunkStorage.State.UNKNOWN);
            if (state == NormalChunkStorage.State.ABSENT || state == NormalChunkStorage.State.PROTO) {
               owned.add(positions.get(n).toLong());
            } else if (state == NormalChunkStorage.State.UNKNOWN) {
               unknown++;
            }
         }

         if (unknown != 0) {
            return new NormalChunkStorage.Scope(owned, unknown);
         }
      }

      return new NormalChunkStorage.Scope(owned, unknown);
   }

   private static NormalChunkStorage.State headerState(ServerLevel level, ChunkPos pos) {
      Path region = CacheManager.worldRegionRoot(level).resolve("r." + Math.floorDiv(pos.x, 32) + "." + Math.floorDiv(pos.z, 32) + ".mca");
      if (Files.notExists(region)) {
         return NormalChunkStorage.State.ABSENT;
      }

      if (!Files.isRegularFile(region)) {
         return NormalChunkStorage.State.UNKNOWN;
      }

      int slot = Math.floorMod(pos.x, 32) + Math.floorMod(pos.z, 32) * 32;

      try (RandomAccessFile file = new RandomAccessFile(region.toFile(), "r")) {
         long entry = slot * 4L;
         if (file.length() < entry + 4L) {
            return NormalChunkStorage.State.UNKNOWN;
         }

         file.seek(entry);
         return file.readInt() == 0 ? NormalChunkStorage.State.ABSENT : NormalChunkStorage.State.PROTO;
      } catch (IOException ignored) {
         return NormalChunkStorage.State.UNKNOWN;
      }
   }

   static final class RegionHeaders {
      private final Map<Long, int[]> headers = new HashMap<>();

      NormalChunkStorage.State state(ServerLevel level, ChunkPos pos) {
         int rx = Math.floorDiv(pos.x, 32);
         int rz = Math.floorDiv(pos.z, 32);
         long key = (long)rx << 32 ^ rz & 4294967295L;
         int[] header = this.headers.computeIfAbsent(key, ignored -> {
            Path file = CacheManager.worldRegionRoot(level).resolve("r." + rx + "." + rz + ".mca");
            if (Files.notExists(file)) {
               return new int[1024];
            }

            if (!Files.isRegularFile(file)) {
               return new int[0];
            }

            try (RandomAccessFile input = new RandomAccessFile(file.toFile(), "r")) {
               if (input.length() < 4096L) {
                  return new int[0];
               }

               int[] values = new int[1024];

               for (int n = 0; n < 1024; n++) {
                  values[n] = input.readInt();
               }

               return values;
            } catch (IOException failure) {
               return new int[0];
            }
         });
         return header.length == 0
            ? NormalChunkStorage.State.UNKNOWN
            : (header[Math.floorMod(pos.x, 32) + Math.floorMod(pos.z, 32) * 32] == 0 ? NormalChunkStorage.State.ABSENT : NormalChunkStorage.State.PROTO);
      }
   }

   record Scope(Set<Long> suppressed, int unknown) {
      Scope {
         suppressed = Set.copyOf(suppressed);
      }
   }

   enum State {
      ABSENT,
      PROTO,
      FULL,
      UNKNOWN;

      // $VF: synthetic method
      private static NormalChunkStorage.State[] $values() {
         return new NormalChunkStorage.State[]{ABSENT, PROTO, FULL, UNKNOWN};
      }
   }
}
