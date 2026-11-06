package maki.chunksmithcache;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkPyramid;
import net.minecraft.world.level.chunk.status.ChunkStatus;

final class NormalChunkStorage {
   enum State {
      ABSENT,
      PROTO,
      FULL,
      UNKNOWN
   }

   record Scope(Set<Long> suppressed, int unknown) {
      Scope {
         suppressed = Set.copyOf(suppressed);
      }
   }

   private NormalChunkStorage() {
   }

   static State inspect(ServerLevel level, ChunkPos pos) {
      State header = headerState(level, pos);
      if (header != State.PROTO) {
         return header;
      }

      try {
         FieldSelector statusSelector = new FieldSelector(StringTag.TYPE, "Status");
         CollectFields collector = new CollectFields(new FieldSelector[]{statusSelector});
         level.getChunkSource().chunkScanner().scanChunk(pos, collector).join();
         return collector.getResult() instanceof CompoundTag tag ? fromTag(tag) : State.UNKNOWN;
      } catch (Throwable ignored) {
         return State.UNKNOWN;
      }
   }

   static State fromTag(CompoundTag tag) {
      if (tag == null || !tag.contains("Status")) {
         return State.UNKNOWN;
      }

      String status = tag.getString("Status");
      if ("minecraft:full".equals(status) || "full".equals(status)) {
         return State.FULL;
      }

      return status.isEmpty() ? State.UNKNOWN : State.PROTO;
   }

   static Scope snapshotBackfillOwnership(ServerLevel level, ChunkPos center) {
      int radius = ChunkPyramid.GENERATION_PYRAMID.getStepTo(ChunkStatus.FULL).accumulatedDependencies().getRadius();
      HashSet<Long> suppressed = new HashSet<>();
      int unknown = 0;

      for (int x = center.x - radius; x <= center.x + radius; x++) {
         for (int z = center.z - radius; z <= center.z + radius; z++) {
            ChunkPos pos = new ChunkPos(x, z);
            State state = inspect(level, pos);
            if (state == State.ABSENT || state == State.PROTO) {
               suppressed.add(pos.toLong());
            } else if (state == State.UNKNOWN) {
               unknown++;
            }
         }
      }

      return new Scope(suppressed, unknown);
   }

   private static State headerState(ServerLevel level, ChunkPos pos) {
      Path region = CacheManager.worldRegionRoot(level)
         .resolve("r." + Math.floorDiv(pos.x, 32) + "." + Math.floorDiv(pos.z, 32) + ".mca");
      if (!Files.isRegularFile(region)) {
         return State.ABSENT;
      }

      int slot = Math.floorMod(pos.x, 32) + Math.floorMod(pos.z, 32) * 32;
      try (RandomAccessFile file = new RandomAccessFile(region.toFile(), "r")) {
         long entry = slot * 4L;
         if (file.length() < entry + 4L) {
            return State.UNKNOWN;
         }
         file.seek(entry);
         return file.readInt() == 0 ? State.ABSENT : State.PROTO;
      } catch (IOException ignored) {
         return State.UNKNOWN;
      }
   }
}
