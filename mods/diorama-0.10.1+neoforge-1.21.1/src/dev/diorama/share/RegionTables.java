package dev.diorama.share;

import dev.diorama.Diorama;
import dev.diorama.data.ChunkTags;
import dev.diorama.data.Nbt;
import dev.diorama.mixin.ChunkMapInvoker;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelResource;
import org.jspecify.annotations.Nullable;

final class RegionTables {
   private static final Pattern REGION_FILE = Pattern.compile("r\\.(-?\\d{1,8})\\.(-?\\d{1,8})\\.mca");
   private static final long READ_SECONDS = 30L;
   private static final int SCANS_AT_ONCE = 32;
   private final ServerLevel level;
   private final ChunkMap chunkMap;
   private final Path regionFolder;
   private final ChunkTags tags;
   private final Map<Long, RegionTables.Table> tables = new HashMap<>();
   private static final long FRESH_MS = 5000L;

   RegionTables(ServerLevel level) {
      this.level = level;
      this.chunkMap = level.getChunkSource().chunkMap;
      this.regionFolder = DimensionType.getStorageFolder(level.dimension(), level.getServer().getWorldPath(LevelResource.ROOT)).resolve("region");
      this.tags = new ChunkTags(level, false);
   }

   ServerLevel level() {
      return this.level;
   }

   ChunkTags tags() {
      return this.tags;
   }

   List<Long> regions() {
      List<Long> regions = new ArrayList<>();
      if (!Files.isDirectory(this.regionFolder)) {
         return regions;
      }

      try (DirectoryStream<Path> files = Files.newDirectoryStream(this.regionFolder, "r.*.mca")) {
         for (Path file : files) {
            Matcher m = REGION_FILE.matcher(file.getFileName().toString());
            if (m.matches() && Files.size(file) >= 8192L) {
               regions.add(key(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))));
            }
         }
      } catch (IOException e) {
         Diorama.LOGGER.warn("Couldn't list the region files in {}", this.regionFolder, e);
      }

      return regions;
   }

   static long key(int regionX, int regionZ) {
      return (long)regionX << 32 | regionZ & 4294967295L;
   }

   RegionTables.Table table(int regionX, int regionZ, boolean generated) {
      return this.table(regionX, regionZ, generated, false);
   }

   RegionTables.Table table(int regionX, int regionZ, boolean generated, boolean fresh) {
      long key = key(regionX, regionZ);
      if (!Files.isRegularFile(this.regionFolder.resolve("r." + regionX + "." + regionZ + ".mca"))) {
         this.tables.remove(key);
         return new RegionTables.Table();
      }

      RegionTables.Table table = this.tables.computeIfAbsent(key, k -> new RegionTables.Table());
      long now = System.currentTimeMillis();
      if (!fresh && now - table.readAt < 5000L) {
         return table;
      }

      table.readAt = now;
      Path file = this.regionFolder.resolve("r." + regionX + "." + regionZ + ".mca");

      long modified;
      try {
         modified = Files.getLastModifiedTime(file).toMillis();
      } catch (IOException e) {
         modified = -1L;
      }

      if (modified != -1L && modified == table.modifiedAt && !table.failed) {
         return table;
      }

      table.modifiedAt = modified;
      table.failed = false;
      int[] times = readHeader(file);
      table.failed = times == null;
      List<Integer> changed = new ArrayList<>();

      for (int i = 0; i < 1024; i++) {
         int time = times == null ? 0 : times[i];
         if (time != table.saved[i]) {
            table.saved[i] = time;
            table.finished[i] = false;
            if (time != 0) {
               changed.add(i);
            }
         }
      }

      for (int from = 0; from < changed.size(); from += 32) {
         List<Integer> part = changed.subList(from, Math.min(changed.size(), from + 32));
         List<CollectFields> fields = new ArrayList<>(part.size());
         List<CompletableFuture<Void>> scans = new ArrayList<>(part.size());

         for (int i : part) {
            CollectFields wanted = fields();
            fields.add(wanted);
            scans.add(this.chunkMap.chunkScanner().scanChunk(pos(regionX, regionZ, i), wanted));
         }

         for (int n = 0; n < part.size(); n++) {
            this.scanned(table, part.get(n), regionX, regionZ, scans.get(n), fields.get(n));
         }
      }

      for (int i = 0; i < 1024; i++) {
         table.shared[i] = table.saved[i] == 0 || !table.finished[i] || !generated && !table.visited[i] ? 0 : table.saved[i];
      }

      return table;
   }

   private static ChunkPos pos(int regionX, int regionZ, int index) {
      return new ChunkPos(regionX << 5 | index & 31, regionZ << 5 | index >> 5);
   }

   static boolean allowed(CompoundTag tag, boolean generated) {
      return ChunkTags.finished(tag) && (generated || Nbt.longValue(tag, "InhabitedTime") > 0L);
   }

   private static CollectFields fields() {
      return new CollectFields(
         new FieldSelector[]{
            new FieldSelector(IntTag.TYPE, "DataVersion"),
            new FieldSelector(StringTag.TYPE, "Status"),
            new FieldSelector(LongTag.TYPE, "InhabitedTime"),
            new FieldSelector(CompoundTag.TYPE, "Heightmaps"),
            new FieldSelector(CompoundTag.TYPE, "below_zero_retrogen")
         }
      );
   }

   private void scanned(RegionTables.Table table, int index, int regionX, int regionZ, CompletableFuture<Void> scan, CollectFields fields) {
      ChunkPos pos = pos(regionX, regionZ, index);

      CompoundTag tag;
      try {
         scan.get(30L, TimeUnit.SECONDS);
         tag = fields.getResult() instanceof CompoundTag found ? found : null;
         if (tag != null && NbtUtils.getDataVersion(tag, -1) < ChunkTags.dataVersion()) {
            tag = this.read(pos);
         }
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         table.saved[index] = 0;
         table.failed = true;
         return;
      } catch (Exception e) {
         Diorama.LOGGER.warn("Couldn't scan saved chunk {} to share it", pos, e);
         table.saved[index] = 0;
         table.failed = true;
         return;
      }

      if (tag != null && !tag.isEmpty() && ChunkTags.finished(tag)) {
         int[] surface = this.tags.surface(tag);
         int[] solid = this.tags.solid(tag);
         if (surface != null && solid != null) {
            ChunkTags.Summary summary = ChunkTags.summaryOf(surface, solid);
            table.finished[index] = summary.maxY() > this.level.getMinBuildHeight();
            table.top[index] = (short)summary.maxY();
            table.floor[index] = (short)Math.max(-32768, summary.floorY());
            table.visited[index] = Nbt.longValue(tag, "InhabitedTime") > 0L;
         }
      }
   }

   CompletableFuture<Optional<CompoundTag>> readLater(ChunkPos pos) {
      return this.chunkMap.read(pos);
   }

   CompoundTag upgrade(CompoundTag tag) {
      return ((ChunkMapInvoker)this.chunkMap).diorama$upgrade(tag);
   }

   @Nullable CompoundTag read(ChunkPos pos) throws Exception {
      Optional<CompoundTag> read = (Optional<CompoundTag>)this.chunkMap.read(pos).get(30L, TimeUnit.SECONDS);
      return read.isEmpty() ? null : ((ChunkMapInvoker)this.chunkMap).diorama$upgrade(read.get());
   }

   private static int @Nullable [] readHeader(Path file) {
      if (!Files.exists(file)) {
         return null;
      }

      try (FileChannel channel = FileChannel.open(file, StandardOpenOption.READ)) {
         ByteBuffer header = ByteBuffer.allocate(8192);

         while (header.hasRemaining() && channel.read(header) >= 0) {
         }

         if (header.hasRemaining()) {
            return null;
         }

         header.flip();
         int[] times = new int[1024];

         for (int i = 0; i < 1024; i++) {
            int location = header.getInt(i * 4);
            times[i] = location == 0 ? 0 : Math.max(1, header.getInt(4096 + i * 4));
         }

         return times;
      } catch (IOException e) {
         Diorama.LOGGER.warn("Couldn't read the region file header of {}", file, e);
         return null;
      }
   }

   static final class Table {
      final int[] saved = new int[1024];
      final int[] shared = new int[1024];
      final short[] top = new short[1024];
      final short[] floor = new short[1024];
      final boolean[] visited = new boolean[1024];
      final boolean[] finished = new boolean[1024];
      long readAt;
      long modifiedAt = -1L;
      boolean failed;
   }
}
