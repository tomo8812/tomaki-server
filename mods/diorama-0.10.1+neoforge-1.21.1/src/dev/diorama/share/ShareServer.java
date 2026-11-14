package dev.diorama.share;

import dev.diorama.Diorama;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import org.jspecify.annotations.Nullable;

public final class ShareServer {
   private static final int MAX_WAITING = 16384;
   private static final int MAX_TABLES = 16384;
   private static final long READ_SECONDS = 30L;
   private static final int BATCH = 16;
   private static volatile @Nullable ShareServer running;
   private static volatile ShareServer.@Nullable Locked locked;
   private final MinecraftServer server;
   private final ShareSettings settings;
   private final Map<UUID, ShareServer.Session> sessions = new ConcurrentHashMap<>();
   private final Map<ServerLevel, RegionTables> tables = new HashMap<>();
   private final Map<ServerLevel, ChunkPacker> packers = new HashMap<>();
   private Map<Long, CompoundTag> batch = Map.of();
   private final Thread worker;
   private final Object wake = new Object();
   private volatile boolean stopped;

   private ShareServer(MinecraftServer server, ShareSettings settings) {
      this.server = server;
      this.settings = settings;
      this.worker = new Thread(this::work, "diorama-share");
      this.worker.setDaemon(true);
      this.worker.setPriority(4);
   }

   public static void started(MinecraftServer server, Path configFolder) {
      if (!server.isDedicatedServer()) {
         lock(server, ServerLocks.load(configFolder, false));
      } else {
         ShareSettings settings = ShareSettings.load(configFolder);
         lock(server, ServerLocks.load(configFolder, true));
         ShareServer share = new ShareServer(server, settings);
         running = share;
         if (settings.share()) {
            share.worker.start();
         }

         Diorama.LOGGER
            .info(
               "Diorama on the server: {} ({} land, up to {} KB/s a player)",
               new Object[]{
                  settings.share() ? "sharing the map with players who have Diorama" : "not sharing the map",
                  settings.land().key(),
                  settings.kilobytesPerSecond()
               }
            );
      }
   }

   private static void lock(MinecraftServer server, ServerLocks locks) {
      locked = locks.any() ? new ShareServer.Locked(server, locks) : null;
      if (locks.any()) {
         Diorama.LOGGER.info("Diorama on the server: locking map settings for players with Diorama: {}", locks);
      }
   }

   public static boolean mapOff(ServerLevel level) {
      return locks(level.getServer()).mapOffDimensions().contains(level.dimension().location().toString());
   }

   public static ServerLocks locks(MinecraftServer server) {
      ShareServer.Locked locks = locked;
      return locks != null && locks.server == server ? locks.locks : ServerLocks.NONE;
   }

   public static void stopped(MinecraftServer server) {
      ShareServer.Locked locks = locked;
      if (locks != null && locks.server == server) {
         locked = null;
      }

      ShareServer share = running;
      if (share != null && share.server == server) {
         running = null;
         share.stopped = true;
         share.worker.interrupt();
      }
   }

   public static void listening(ServerPlayer player) {
      ShareServer.Locked locks = locked;
      if (locks != null && player.level().getServer() == locks.server) {
         ShareProtocol.send(player, locks.locks.encode());
      }

      ShareServer share = running;
      if (share != null && player.level().getServer() == share.server) {
         ShareProtocol.send(player, share.settings.encode());
      }
   }

   public static void receive(ServerPlayer player, byte[] data) {
      ShareServer share = running;
      if (share != null && share.settings.share() && data.length != 0 && player.level().getServer() == share.server) {
         try {
            label70: {
               try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(data))) {
                  int kind = in.readUnsignedByte();
                  String dimension = in.readUTF();
                  ShareServer.Session session = share.sessions
                     .computeIfAbsent(player.getUUID(), id -> new ShareServer.Session(player, share.settings.kilobytesPerSecond()));
                  session.player = player;
                  switch (kind) {
                     case 1:
                        session.askRegions(dimension);
                        break label70;
                     case 2:
                        int round = in.readInt();
                        boolean last = in.readBoolean();
                        int count = in.readUnsignedShort();
                        Map<Long, Integer> regions = new LinkedHashMap<>();

                        for (int i = 0; i < count; i++) {
                           regions.put(RegionTables.key(in.readInt(), in.readInt()), in.readInt());
                        }

                        session.askTables(dimension, regions, last ? round : 0);
                        break label70;
                     case 3:
                        boolean urgent = in.readBoolean();
                        int count = in.readUnsignedShort();
                        long[] chunks = new long[count];

                        for (int i = 0; i < count; i++) {
                           chunks[i] = ShareProtocol.chunkKey(in.readInt(), in.readInt());
                        }

                        session.chunks(dimension, chunks, urgent);
                        break label70;
                  }
               }

               return;
            }
         } catch (IOException e) {
            Diorama.LOGGER.debug("A malformed share message from {}", player.getName().getString());
            return;
         }

         synchronized (share.wake) {
            share.wake.notifyAll();
         }
      }
   }

   private void work() {
      while (!this.stopped) {
         boolean busy = false;

         try {
            for (Entry<UUID, ShareServer.Session> entry : this.sessions.entrySet()) {
               ShareServer.Session session = entry.getValue();
               if (session.player.hasDisconnected()) {
                  this.sessions.remove(entry.getKey());
                  this.logSession(session);
               } else {
                  String regionsOf = session.nextRegionsAsk();
                  if (regionsOf != null) {
                     this.regions(session, regionsOf);
                     busy = true;
                  } else {
                     long[] table = session.nextTable();
                     if (table != null) {
                        String dimension = session.tablesDimension();
                        if (ShareServer.Session.isMark(table[0])) {
                           int round = (int)table[1];
                           this.send(session, out -> {
                              out.writeByte(4);
                              out.writeUTF(dimension);
                              out.writeInt(round);
                           });
                        } else {
                           this.table(session, dimension, table[0], (int)table[1]);
                        }

                        busy = true;
                     } else {
                        List<Long> chunks = session.nextChunks(16);
                        if (!chunks.isEmpty()) {
                           this.serve(session, chunks);
                           busy = true;
                        }
                     }
                  }
               }
            }

            if (!busy) {
               synchronized (this.wake) {
                  this.wake.wait(50L);
               }
            }
         } catch (InterruptedException e) {
            return;
         } catch (RuntimeException e) {
            if (!this.stopped) {
               Diorama.LOGGER.error("Sharing the map failed", e);
            }
         }
      }
   }

   private void logSession(ShareServer.Session session) {
      if (session.sentChunks + session.refused != 0L) {
         double seconds = (System.nanoTime() - session.startedAt) / 1.0E9;
         Diorama.LOGGER
            .info(
               "Shared map with {}: {} chunks, {} KB ({} bytes a chunk) and {} KB of region tables, {} not shared, {} ms of the share thread a chunk, over {} s",
               new Object[]{
                  session.player.getName().getString(),
                  session.sentChunks,
                  session.sentBytes / 1024L,
                  session.sentChunks == 0L ? 0L : session.sentBytes / session.sentChunks,
                  session.tableBytes / 1024L,
                  session.refused,
                  session.sentChunks == 0L ? 0 : String.format(Locale.ROOT, "%.2f", session.workNanos / 1000000.0 / session.sentChunks),
                  Math.round(seconds)
               }
            );
      }
   }

   private @Nullable ServerLevel levelOf(ShareServer.Session session, String dimension) {
      ServerLevel level = (ServerLevel)session.player.level();
      return level.dimension().location().toString().equals(dimension) && !level.dimensionType().hasCeiling() && !mapOff(level) ? level : null;
   }

   private RegionTables tables(ServerLevel level) {
      return this.tables.computeIfAbsent(level, RegionTables::new);
   }

   private void regions(ShareServer.Session session, String dimension) {
      ServerLevel level = this.levelOf(session, dimension);
      if (level != null) {
         List<Long> found = this.tables(level).regions();
         int sent = this.send(session, out -> {
            out.writeByte(1);
            out.writeUTF(dimension);
            out.writeLong(System.currentTimeMillis());
            out.writeInt(found.size());

            for (long region : found) {
               out.writeInt((int)(region >> 32));
               out.writeInt((int)region);
            }
         });
         session.tableBytes += sent;
         session.spent(sent);
      }
   }

   private void table(ShareServer.Session session, String dimension, long region, int known) {
      ServerLevel level = this.levelOf(session, dimension);
      if (level != null) {
         boolean generated = this.settings.land() == ShareSettings.Land.GENERATED;
         int regionX = (int)(region >> 32);
         int regionZ = (int)region;
         RegionTables.Table table = this.tables(level).table(regionX, regionZ, generated, true);
         int stamp = Arrays.hashCode(table.saved);
         stamp = stamp == 0 ? 1 : stamp;
         if (known == 0 || stamp != known) {
            int sentStamp = stamp;
            int sent = this.send(session, out -> {
               out.writeByte(2);
               out.writeUTF(dimension);
               out.writeInt(regionX);
               out.writeInt(regionZ);
               out.writeInt(sentStamp);
               Deflater deflater = new Deflater();

               try {
                  DeflaterOutputStream deflated = new DeflaterOutputStream(out, deflater);
                  DataOutputStream packed = new DataOutputStream(deflated);

                  for (int i = 0; i < 1024; i++) {
                     packed.writeInt(table.shared[i]);
                     if (table.shared[i] != 0) {
                        packed.writeShort(table.top[i]);
                        packed.writeShort(table.floor[i]);
                     }
                  }

                  packed.flush();
                  deflated.finish();
               } finally {
                  deflater.end();
               }
            });
            session.tableBytes += sent;
            session.spent(sent);
         }
      }
   }

   private void serve(ShareServer.Session session, List<Long> chunks) {
      long start = System.nanoTime();
      String dimension = session.dimension();
      ServerLevel level = this.levelOf(session, dimension);
      if (level != null) {
         boolean generated = this.settings.land() == ShareSettings.Land.GENERATED;
         RegionTables tables = this.tables(level);
         Map<Long, Integer> times = new LinkedHashMap<>();

         for (long chunk : chunks) {
            int chunkX = (int)(chunk >> 32);
            int chunkZ = (int)chunk;
            times.put(chunk, tables.table(chunkX >> 5, chunkZ >> 5, generated).shared[(chunkZ & 31) << 5 | chunkX & 31]);
         }

         List<Long> wanted = times.entrySet().stream().filter(e -> e.getValue() != 0).map(Entry::getKey).toList();
         Map<Long, CompoundTag> tags = this.tags(level, tables, wanted);
         this.batch = tags;
         this.packer(level, tables).forgetIfOld();

         try {
            int packed = 0;

            for (long chunk : chunks) {
               int chunkX = (int)(chunk >> 32);
               int chunkZ = (int)chunk;
               CompoundTag tag = tags.get(chunk);
               int time = times.get(chunk);
               if (tag != null || time == 0) {
                  byte[] body = tag != null && RegionTables.allowed(tag, generated) ? this.packer(level, tables).pack(chunkX, chunkZ, tag) : null;
                  if (body != null && body.length > 524288) {
                     body = null;
                  }

                  byte[] sending = body;
                  int sent = this.send(session, out -> {
                     out.writeByte(3);
                     out.writeUTF(dimension);
                     out.writeInt(chunkX);
                     out.writeInt(chunkZ);
                     out.writeInt(time);
                     out.writeInt(sending == null ? 0 : sending.length);
                     if (sending != null) {
                        out.write(sending);
                     }
                  });
                  session.spent(sent);
                  if (body == null) {
                     session.refused++;
                  } else {
                     session.sentChunks++;
                     session.sentBytes += sent;
                     packed++;
                  }
               }
            }

            if (packed > 0) {
               session.workNanos = session.workNanos + (System.nanoTime() - start);
            }
         } finally {
            this.batch = Map.of();
         }
      }
   }

   private Map<Long, CompoundTag> tags(ServerLevel level, RegionTables tables, List<Long> chunks) {
      Map<Long, CompoundTag> tags = new HashMap<>();
      if (chunks.isEmpty()) {
         return tags;
      }

      try {
         Map<Long, Object> copies = (Map<Long, Object>)this.server.submit(() -> {
            Map<Long, Object> loaded = new HashMap<>();

            for (long chunk : chunks) {
               LevelChunk found = level.getChunkSource().getChunkNow((int)(chunk >> 32), (int)chunk);
               if (found != null) {
                  loaded.put(chunk, copy(level, found));
               }
            }

            return loaded;
         }).get(30L, TimeUnit.SECONDS);
         Map<Long, CompletableFuture<Optional<CompoundTag>>> reads = new LinkedHashMap<>();

         for (long chunk : chunks) {
            if (!copies.containsKey(chunk)) {
               reads.put(chunk, tables.readLater(new ChunkPos((int)(chunk >> 32), (int)chunk)));
            }
         }

         for (Entry<Long, Object> copy : copies.entrySet()) {
            try {
               tags.put(copy.getKey(), written(copy.getValue()));
            } catch (RuntimeException e) {
               this.couldNotRead(copy.getKey(), e);
            }
         }

         for (Entry<Long, CompletableFuture<Optional<CompoundTag>>> read : reads.entrySet()) {
            try {
               Optional<CompoundTag> saved = read.getValue().get(30L, TimeUnit.SECONDS);
               if (saved.isPresent()) {
                  tags.put(read.getKey(), tables.upgrade(saved.get()));
               }
            } catch (ExecutionException | TimeoutException | RuntimeException e) {
               this.couldNotRead(read.getKey(), e);
            }
         }
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      } catch (Exception e) {
         if (!this.stopped) {
            Diorama.LOGGER.warn("Couldn't read {} chunks to share them", chunks.size(), e);
         }
      }

      return tags;
   }

   private void couldNotRead(long chunk, Exception e) {
      if (!this.stopped) {
         Diorama.LOGGER.warn("Couldn't read chunk {}, {} to share it", new Object[]{(int)(chunk >> 32), (int)chunk, e});
      }
   }

   private ChunkPacker packer(ServerLevel level, RegionTables tables) {
      return this.packers.computeIfAbsent(level, l -> new ChunkPacker(l, tables.tags(), (x, z) -> {
         CompoundTag known = this.batch.get(ShareProtocol.chunkKey(x, z));
         return known != null ? known : this.tag(l, tables, x, z);
      }));
   }

   private CompoundTag tag(ServerLevel level, RegionTables tables, int chunkX, int chunkZ) {
      try {
         CompletableFuture<Object> loaded = this.server.submit(() -> {
            LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
            return chunk == null ? null : copy(level, chunk);
         });
         Object copy = loaded.get(30L, TimeUnit.SECONDS);
         return copy != null ? written(copy) : tables.read(new ChunkPos(chunkX, chunkZ));
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         return null;
      } catch (Exception e) {
         if (!this.stopped) {
            Diorama.LOGGER.warn("Couldn't read chunk {}, {} to share it", new Object[]{chunkX, chunkZ, e});
         }

         return null;
      }
   }

   private static Object copy(ServerLevel level, LevelChunk chunk) {
      return ChunkSerializer.write(level, chunk);
   }

   private static CompoundTag written(Object copy) {
      return (CompoundTag)copy;
   }

   private int send(ShareServer.Session session, ShareServer.Writer writer) {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();

      try (DataOutputStream out = new DataOutputStream(bytes)) {
         writer.write(out);
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }

      byte[] data = bytes.toByteArray();
      ShareProtocol.send(session.player, data);
      return data.length;
   }

   private record Locked(MinecraftServer server, ServerLocks locks) {
   }

   private static final class Session {
      volatile ServerPlayer player;
      private final Deque<String> regionAsks = new ArrayDeque<>();
      private final LinkedHashMap<Long, Integer> tables = new LinkedHashMap<>();
      private long waitingMark = mark(0);
      private String tablesDimension = "";
      private long listedAt = System.nanoTime() - 10000000000L;
      private final LinkedHashSet<Long> chunks = new LinkedHashSet<>();
      private String dimension = "";
      private final double bytesPerSecond;
      private double allowance;
      private long refilledAt = System.nanoTime();
      long sentChunks;
      long sentBytes;
      long tableBytes;
      long refused;
      long workNanos;
      long startedAt = System.nanoTime();

      Session(ServerPlayer player, int kilobytesPerSecond) {
         this.player = player;
         this.bytesPerSecond = kilobytesPerSecond * 1024.0;
         this.allowance = this.bytesPerSecond;
      }

      synchronized void askRegions(String dimension) {
         if (this.regionAsks.size() < 4) {
            this.regionAsks.add(dimension);
         }
      }

      synchronized @Nullable String nextRegionsAsk() {
         this.refill();
         long now = System.nanoTime();
         if (!this.regionAsks.isEmpty() && !(this.allowance <= 0.0) && now - this.listedAt >= 2000000000L) {
            this.listedAt = now;
            return this.regionAsks.poll();
         } else {
            return null;
         }
      }

      synchronized void askTables(String dimension, Map<Long, Integer> regions, int round) {
         if (!dimension.equals(this.tablesDimension)) {
            this.tables.clear();
            this.tablesDimension = dimension;
         }

         for (Entry<Long, Integer> region : regions.entrySet()) {
            if (this.tables.size() >= 16384 && !this.tables.containsKey(region.getKey())) {
               break;
            }

            this.tables.put(region.getKey(), region.getValue());
         }

         if (round != 0) {
            this.tables.remove(this.waitingMark);
            this.waitingMark = mark(round);
            this.tables.put(this.waitingMark, round);
         }
      }

      synchronized long @Nullable [] nextTable() {
         this.refill();
         if (!(this.allowance <= 0.0) && !this.tables.isEmpty()) {
            Iterator<Entry<Long, Integer>> it = this.tables.entrySet().iterator();
            Entry<Long, Integer> next = it.next();
            it.remove();
            return new long[]{next.getKey(), next.getValue().intValue()};
         } else {
            return null;
         }
      }

      synchronized String tablesDimension() {
         return this.tablesDimension;
      }

      static long mark(int round) {
         return Long.MIN_VALUE | round & 4294967295L;
      }

      static boolean isMark(long key) {
         return (int)(key >> 32) == Integer.MIN_VALUE;
      }

      private void refill() {
         long now = System.nanoTime();
         this.allowance = Math.min(this.bytesPerSecond, this.allowance + (now - this.refilledAt) / 1.0E9 * this.bytesPerSecond);
         this.refilledAt = now;
      }

      synchronized void chunks(String dimension, long[] asked, boolean urgent) {
         if (!dimension.equals(this.dimension)) {
            this.chunks.clear();
            this.dimension = dimension;
         }

         if (urgent) {
            LinkedHashSet<Long> rest = new LinkedHashSet<>(this.chunks);
            this.chunks.clear();

            for (long chunk : asked) {
               if (this.chunks.size() < 16384) {
                  this.chunks.add(chunk);
               }
            }

            for (long chunk : rest) {
               if (this.chunks.size() >= 16384) {
                  break;
               }

               this.chunks.add(chunk);
            }
         } else {
            for (long chunk : asked) {
               if (this.chunks.size() >= 16384) {
                  break;
               }

               this.chunks.add(chunk);
            }
         }
      }

      synchronized List<Long> nextChunks(int most) {
         this.refill();
         List<Long> next = new ArrayList<>(most);
         if (this.allowance <= 0.0) {
            return next;
         }

         Iterator<Long> it = this.chunks.iterator();

         while (it.hasNext() && next.size() < most) {
            next.add(it.next());
            it.remove();
         }

         return next;
      }

      synchronized String dimension() {
         return this.dimension;
      }

      synchronized void spent(int bytes) {
         this.allowance -= bytes;
      }
   }

   private interface Writer {
      void write(DataOutputStream var1) throws IOException;
   }
}
