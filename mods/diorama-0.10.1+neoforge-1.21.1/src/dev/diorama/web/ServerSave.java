package dev.diorama.web;

import dev.diorama.Diorama;
import dev.diorama.data.ChunkSave;
import dev.diorama.data.ChunkSnapshot;
import dev.diorama.data.ChunkTags;
import dev.diorama.data.Nbt;
import dev.diorama.data.SavedChunk;
import dev.diorama.mixin.ChunkMapInvoker;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.RegionFileVersion;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelResource;
import org.jspecify.annotations.Nullable;

final class ServerSave implements ChunkSave {
   private static final Pattern REGION_FILE = Pattern.compile("r\\.(-?\\d{1,8})\\.(-?\\d{1,8})\\.mca");
   private static final int CACHED_CHUNKS = 2048;
   private static final short UNKNOWN = -32768;
   private static final short NONE = -32767;
   private static final long SERVER_BEHIND_NANOS = 40000000L;
   private final ServerLevel level;
   private final Path regionFolder;
   private final ChunkTags tags;
   private final Map<Long, @Nullable SavedChunk> cache = new LinkedHashMap<Long, SavedChunk>(2048, 0.75F, true) {
      @Override
      protected boolean removeEldestEntry(Entry<Long, @Nullable SavedChunk> eldest) {
         return this.size() > 2048;
      }
   };
   private final Map<Long, short[]> tops = new HashMap<>();
   private final AtomicInteger failedReads = new AtomicInteger();
   private final Map<Long, Long> failingSince = new ConcurrentHashMap<>();
   private static final long GIVE_UP_MS = 900000L;
   private volatile boolean closed;

   private ServerSave(ServerLevel level) {
      this.level = level;
      this.regionFolder = DimensionType.getStorageFolder(level.dimension(), level.getServer().getWorldPath(LevelResource.ROOT)).resolve("region");
      this.tags = new ChunkTags(level, false);
   }

   static ServerSave of(ServerLevel level) {
      return new ServerSave(level);
   }

   static long regionKey(int regionX, int regionZ) {
      return (long)regionX << 32 | regionZ & 4294967295L;
   }

   ServerSave.@Nullable Info info(int chunkX, int chunkZ) {
      CompoundTag tag = this.tag(chunkX, chunkZ);
      if (tag == null) {
         this.setTop(chunkX, chunkZ, null);
         return null;
      }

      ChunkTags.Summary summary = ChunkTags.Summary.UNFINISHED;
      if (ChunkTags.finished(tag)) {
         int[] surface = this.tags.surface(tag);
         int[] solid = this.tags.solid(tag);
         if (surface != null && solid != null) {
            summary = ChunkTags.summaryOf(surface, solid);
         }
      }

      this.setTop(chunkX, chunkZ, summary);
      ListTag sections = Nbt.list(tag, "sections");
      int hash = 1;

      for (int s = 0; s < sections.size(); s++) {
         CompoundTag section = Nbt.compound(sections, s);
         hash = hash * 31 + Nbt.sectionY(section);
         hash = hash * 31 + Nbt.compound(section, "block_states").hashCode();
         hash = hash * 31 + Nbt.compound(section, "biomes").hashCode();
      }

      return new ServerSave.Info(summary, Nbt.longValue(tag, "InhabitedTime"), hash);
   }

   int failedReads() {
      return this.failedReads.get();
   }

   private static long chunkKey(int chunkX, int chunkZ) {
      return (long)chunkX << 32 | chunkZ & 4294967295L;
   }

   private @Nullable CompoundTag tag(int chunkX, int chunkZ) {
      CompoundTag tag = this.readFile(chunkX, chunkZ);
      if (tag == null) {
         return null;
      }

      if (NbtUtils.getDataVersion(tag, -1) < ChunkTags.dataVersion()) {
         try {
            tag = ((ChunkMapInvoker)this.level.getChunkSource().chunkMap).diorama$upgrade(tag);
         } catch (RuntimeException e) {
            Diorama.LOGGER.warn("Couldn't upgrade saved chunk {}, {} for the web map", new Object[]{chunkX, chunkZ, e});
            return null;
         }
      }

      return tag;
   }

   private @Nullable CompoundTag readFile(int chunkX, int chunkZ) {
      Path file = this.regionFolder.resolve("r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mca");

      for (int attempt = 0; attempt < 3 && !this.closed; attempt++) {
         if (attempt > 0) {
            try {
               Thread.sleep(20L);
            } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
               return null;
            }
         }

         try {
            CompoundTag tag = this.readFile(file, chunkX, chunkZ);
            this.failingSince.remove(chunkKey(chunkX, chunkZ));
            return tag;
         } catch (IOException | RuntimeException var9) {
         }
      }

      long now = System.currentTimeMillis();
      long since = this.failingSince.computeIfAbsent(chunkKey(chunkX, chunkZ), k -> now);
      if (now - since < 900000L) {
         this.failedReads.incrementAndGet();
      } else if (since > 0L) {
         this.failingSince.put(chunkKey(chunkX, chunkZ), -1L);
         Diorama.LOGGER
            .warn("Saved chunk {}, {} has been unreadable for {} minutes; the web map leaves it out until it can be read", new Object[]{chunkX, chunkZ, 15L});
      }

      return null;
   }

   private @Nullable CompoundTag readFile(Path file, int chunkX, int chunkZ) throws IOException {
      if (!Files.isRegularFile(file)) {
         return null;
      }

      try (FileChannel channel = FileChannel.open(file, StandardOpenOption.READ)) {
         ByteBuffer slot = ByteBuffer.allocate(4);
         readFully(channel, slot, ((chunkZ & 31) << 5 | chunkX & 31) * 4L);
         int location = slot.getInt(0);
         if (location == 0) {
            return null;
         }

         int sectors = location & 0xFF;
         long start = (location >>> 8) * 4096L;
         ByteBuffer head = ByteBuffer.allocate(5);
         readFully(channel, head, start);
         int length = head.getInt(0);
         int version = head.get(4) & 255;
         InputStream raw;
         if ((version & 128) != 0) {
            raw = Files.newInputStream(this.regionFolder.resolve("c." + chunkX + "." + chunkZ + ".mcc"));
            version &= 127;
         } else {
            if (length < 1 || length > sectors * 4096 - 4) {
               throw new IOException("chunk length " + length + " past its sectors");
            }

            ByteBuffer data = ByteBuffer.allocate(length - 1);
            readFully(channel, data, start + 5L);
            raw = new ByteArrayInputStream(data.array());
         }

         try (InputStream source = raw) {
            RegionFileVersion format = RegionFileVersion.fromId(version);
            if (format != null) {
               try (DataInputStream in = new DataInputStream(new BufferedInputStream(format.wrap(source)))) {
                  return NbtIo.read(in);
               }
            } else {
               return null;
            }
         }
      }
   }

   private static void readFully(FileChannel channel, ByteBuffer buffer, long position) throws IOException {
      while (buffer.hasRemaining()) {
         if (channel.read(buffer, position + buffer.position()) < 0) {
            throw new IOException("region file ends early");
         }
      }
   }

   @Override
   public Level level() {
      return this.level;
   }

   @Override
   public boolean isOpen() {
      return !this.closed;
   }

   @Override
   public Map<Long, int[]> scan() {
      Map<Long, int[]> regions = new HashMap<>();
      if (!Files.isDirectory(this.regionFolder)) {
         return regions;
      }

      try (DirectoryStream<Path> files = Files.newDirectoryStream(this.regionFolder, "r.*.mca")) {
         for (Path file : files) {
            Matcher m = REGION_FILE.matcher(file.getFileName().toString());
            if (m.matches()) {
               int[] times = this.readHeader(file);
               if (times != null) {
                  regions.put(regionKey(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))), times);
               }
            }
         }
      } catch (IOException e) {
         Diorama.LOGGER.warn("Couldn't list the saved chunks in {}", this.regionFolder, e);
      }

      return regions;
   }

   private int @Nullable [] readHeader(Path file) {
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
            int time = header.getInt(4096 + i * 4);
            times[i] = location == 0 ? 0 : Math.max(1, time);
         }

         return times;
      } catch (IOException e) {
         this.failedReads.incrementAndGet();
         Diorama.LOGGER.warn("Couldn't read the region file header of {}", file, e);
         return null;
      }
   }

   @Override
   public ChunkTags.@Nullable Summary summary(int chunkX, int chunkZ) {
      ServerSave.Info info = this.info(chunkX, chunkZ);
      return info == null ? null : info.summary();
   }

   private void setTop(int chunkX, int chunkZ, ChunkTags.@Nullable Summary summary) {
      short top = summary != null && summary.finished() && summary.maxY() > this.level.getMinBuildHeight() ? (short)summary.maxY() : -32767;
      this.tops.computeIfAbsent(regionKey(chunkX >> 5, chunkZ >> 5), k -> {
         short[] tops = new short[1024];
         Arrays.fill(tops, (short)-32768);
         return tops;
      })[(chunkZ & 31) << 5 | chunkX & 31] = top;
   }

   @Override
   public int top(int chunkX, int chunkZ) {
      short[] tops = this.tops.get(regionKey(chunkX >> 5, chunkZ >> 5));
      short top = tops == null ? -32768 : tops[(chunkZ & 31) << 5 | chunkX & 31];
      if (top == -32768) {
         this.info(chunkX, chunkZ);
         top = this.tops.get(regionKey(chunkX >> 5, chunkZ >> 5))[(chunkZ & 31) << 5 | chunkX & 31];
      }

      return top == -32767 ? Integer.MIN_VALUE : top;
   }

   @Override
   public @Nullable SavedChunk read(int chunkX, int chunkZ) {
      long key = ChunkSnapshot.key(chunkX, chunkZ);
      if (this.cache.containsKey(key)) {
         return this.cache.get(key);
      }

      CompoundTag tag = this.tag(chunkX, chunkZ);
      SavedChunk chunk = null;
      if (tag != null) {
         try {
            chunk = this.tags.read(chunkX, chunkZ, tag);
         } catch (RuntimeException e) {
            Diorama.LOGGER.warn("Couldn't read saved chunk {}, {} for the web map", new Object[]{chunkX, chunkZ, e});
         }
      }

      if (!this.closed) {
         this.cache.put(key, chunk);
      }

      return chunk;
   }

   @Override
   public void forgetTops(long region) {
      this.tops.remove(region);
   }

   @Override
   public void clear() {
      this.cache.clear();
   }

   @Override
   public void close() {
      this.closed = true;
   }

   @Override
   public boolean serverBehind() {
      return this.level.getServer().getAverageTickTimeNanos() > 40000000L;
   }

   record Info(ChunkTags.Summary summary, long inhabited, int blocks) {
   }
}
