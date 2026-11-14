package dev.diorama.data;

import dev.diorama.Diorama;
import dev.diorama.mixin.ChunkMapInvoker;
import java.io.IOException;
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
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.IOWorker;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelResource;
import org.jspecify.annotations.Nullable;

public final class SavedChunks implements ChunkSave {
   private static final Pattern REGION_FILE = Pattern.compile("r\\.(-?\\d{1,8})\\.(-?\\d{1,8})\\.mca");
   private static final int CACHED_CHUNKS = Runtime.getRuntime().maxMemory() > 3221225472L ? 2048 : 768;
   private static final short UNKNOWN = -32768;
   private static final short NONE = -32767;
   private static final long SERVER_BEHIND_NANOS = 40000000L;
   private static final long WRITES_WAIT_NANOS = 10000000000L;
   private final Level level;
   private final ServerLevel serverLevel;
   private final ChunkMap chunkMap;
   private final Path regionFolder;
   private final ChunkTags tags;
   private final Map<Long, @Nullable SavedChunk> cache = new LinkedHashMap<Long, SavedChunk>(CACHED_CHUNKS, 0.75F, true) {
      @Override
      protected boolean removeEldestEntry(Entry<Long, @Nullable SavedChunk> eldest) {
         return this.size() > SavedChunks.CACHED_CHUNKS;
      }
   };
   private final Map<Long, short[]> tops = new HashMap<>();
   private volatile boolean closed;

   private SavedChunks(Level level, ServerLevel serverLevel, Path regionFolder, boolean withLight) {
      this.level = level;
      this.serverLevel = serverLevel;
      this.chunkMap = serverLevel.getChunkSource().chunkMap;
      this.regionFolder = regionFolder;
      this.tags = new ChunkTags(level, withLight);
   }

   public static @Nullable SavedChunks open(ClientLevel level, boolean withLight) {
      IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
      if (server == null) {
         return null;
      }

      ServerLevel serverLevel = server.getLevel(level.dimension());
      if (serverLevel == null) {
         return null;
      }

      Path folder = DimensionType.getStorageFolder(level.dimension(), server.getWorldPath(LevelResource.ROOT)).resolve("region");
      return new SavedChunks(level, serverLevel, folder, withLight);
   }

   @Override
   public void close() {
      this.closed = true;
   }

   private <T> @Nullable T await(CompletableFuture<T> future) {
      return this.await(future, -1L);
   }

   private <T> @Nullable T await(CompletableFuture<T> future, long nanos) {
      long start = System.nanoTime();

      while (!this.closed && (nanos < 0L || System.nanoTime() - start < nanos)) {
         try {
            return future.get(100L, TimeUnit.MILLISECONDS);
         } catch (TimeoutException var7) {
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
         } catch (ExecutionException e) {
            throw new CompletionException(e.getCause());
         }
      }

      return null;
   }

   @Override
   public Level level() {
      return this.level;
   }

   @Override
   public boolean isOpen() {
      IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
      return server != null && server.isRunning() && server.getLevel(this.level.dimension()) == this.serverLevel;
   }

   public static long regionKey(int regionX, int regionZ) {
      return (long)regionX << 32 | regionZ & 4294967295L;
   }

   @Override
   public Map<Long, int[]> scan() {
      this.saveHeldChunks();
      Map<Long, int[]> regions = new HashMap<>();
      if (!Files.isDirectory(this.regionFolder)) {
         return regions;
      }

      try (DirectoryStream<Path> files = Files.newDirectoryStream(this.regionFolder, "r.*.mca")) {
         for (Path file : files) {
            Matcher m = REGION_FILE.matcher(file.getFileName().toString());
            if (m.matches()) {
               int[] times = readHeader(file);
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

   private void saveHeldChunks() {
      try {
         long start = System.nanoTime();
         this.await(this.serverLevel.getServer().submit(() -> {
            long saving = System.nanoTime();
            this.serverLevel.getChunkSource().save(false);
            if (Diorama.DEBUG) {
               Diorama.LOGGER.info("Saved chunks: the server saved its loaded chunks in {} ms", (System.nanoTime() - saving) / 100000L / 10.0);
            }
         }));
         if (!this.closed && this.chunkMap.chunkScanner() instanceof IOWorker storage) {
            this.await(storage.synchronize(false), 10000000000L);
         }

         if (Diorama.DEBUG) {
            Diorama.LOGGER.info("Saved chunks: the server's chunks were in its files after {} ms", (System.nanoTime() - start) / 100000L / 10.0);
         }
      } catch (RuntimeException e) {
         Diorama.LOGGER.warn("Couldn't have the server save its loaded chunks", e);
      }
   }

   private static int @Nullable [] readHeader(Path file) {
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
         Diorama.LOGGER.warn("Couldn't read the region file header of {}", file, e);
         return null;
      }
   }

   @Override
   public ChunkTags.@Nullable Summary summary(int chunkX, int chunkZ) {
      ChunkTags.Summary summary = this.summarize(chunkX, chunkZ);
      short top = summary != null && summary.finished() && summary.maxY() > this.level.getMinBuildHeight() ? (short)summary.maxY() : -32767;
      this.tops.computeIfAbsent(regionKey(chunkX >> 5, chunkZ >> 5), k -> {
         short[] tops = new short[1024];
         Arrays.fill(tops, (short)-32768);
         return tops;
      })[(chunkZ & 31) << 5 | chunkX & 31] = top;
      return summary;
   }

   @Override
   public int top(int chunkX, int chunkZ) {
      short[] tops = this.tops.get(regionKey(chunkX >> 5, chunkZ >> 5));
      short top = tops == null ? -32768 : tops[(chunkZ & 31) << 5 | chunkX & 31];
      if (top == -32768) {
         this.summary(chunkX, chunkZ);
         top = this.tops.get(regionKey(chunkX >> 5, chunkZ >> 5))[(chunkZ & 31) << 5 | chunkX & 31];
      }

      return top == -32767 ? Integer.MIN_VALUE : top;
   }

   private ChunkTags.@Nullable Summary summarize(int chunkX, int chunkZ) {
      ChunkPos pos = new ChunkPos(chunkX, chunkZ);
      CollectFields fields = new CollectFields(
         new FieldSelector[]{
            new FieldSelector(IntTag.TYPE, "DataVersion"),
            new FieldSelector(StringTag.TYPE, "Status"),
            new FieldSelector(CompoundTag.TYPE, "Heightmaps"),
            new FieldSelector(CompoundTag.TYPE, "below_zero_retrogen")
         }
      );

      try {
         this.await(this.chunkMap.chunkScanner().scanChunk(pos, fields));
      } catch (RuntimeException e) {
         Diorama.LOGGER.warn("Couldn't scan saved chunk {}", pos, e);
         return null;
      }

      if (!(fields.getResult() instanceof CompoundTag tag && !tag.isEmpty())) {
         return null;
      } else {
         if (NbtUtils.getDataVersion(tag, -1) < ChunkTags.dataVersion()) {
            SavedChunk chunk = this.read(chunkX, chunkZ);
            return chunk == null ? null : (chunk.finished() ? ChunkTags.summaryOf(chunk.surface(), chunk.solid()) : ChunkTags.Summary.UNFINISHED);
         }

         if (!ChunkTags.finished(tag)) {
            return ChunkTags.Summary.UNFINISHED;
         }

         int[] surface = this.tags.surface(tag);
         int[] solid = this.tags.solid(tag);
         return surface != null && solid != null ? ChunkTags.summaryOf(surface, solid) : ChunkTags.Summary.UNFINISHED;
      }
   }

   @Override
   public @Nullable SavedChunk read(int chunkX, int chunkZ) {
      long key = ChunkSnapshot.key(chunkX, chunkZ);
      if (this.cache.containsKey(key)) {
         return this.cache.get(key);
      }

      SavedChunk chunk = this.load(chunkX, chunkZ);
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
   public boolean serverBehind() {
      IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
      return server != null && !Minecraft.getInstance().isPaused() ? server.getAverageTickTimeNanos() > 40000000L : false;
   }

   private @Nullable SavedChunk load(int chunkX, int chunkZ) {
      ChunkPos pos = new ChunkPos(chunkX, chunkZ);

      try {
         Optional<CompoundTag> read = this.await(this.chunkMap.read(pos));
         CompoundTag tag = read == null ? null : read.orElse(null);
         return tag == null ? null : this.tags.read(chunkX, chunkZ, ((ChunkMapInvoker)this.chunkMap).diorama$upgrade(tag));
      } catch (RuntimeException e) {
         Diorama.LOGGER.warn("Couldn't read saved chunk {}", pos, e);
         return null;
      }
   }
}
