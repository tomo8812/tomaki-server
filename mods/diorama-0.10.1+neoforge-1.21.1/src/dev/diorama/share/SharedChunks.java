package dev.diorama.share;

import dev.diorama.Diorama;
import dev.diorama.MapSession;
import dev.diorama.data.ChunkSave;
import dev.diorama.data.ChunkSnapshot;
import dev.diorama.data.ChunkTags;
import dev.diorama.data.SavedChunk;
import dev.diorama.data.SavedChunks;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public final class SharedChunks implements ChunkSave {
   private static final long LIST_WAIT_MS = 30000L;
   private static final long FIRST_TABLES_WAIT_MS = 5000L;
   private static final long READ_WAIT_MS = 30000L;
   private static final long ASK_AGAIN_MS = 60000L;
   private static final int TABLES_PER_ASK = 256;
   private static final long REFRESH_MS = 10000L;
   private static final long NBT_QUOTA = 8388608L;
   private static final int KEPT_CHUNKS = 8192;
   private static final int PARSED_CHUNKS = Runtime.getRuntime().maxMemory() > 3221225472L ? 2048 : 768;
   private static volatile @Nullable SharedChunks current;
   private final ClientLevel level;
   private final String dimension;
   private final ChunkTags tags;
   private final Object lock = new Object();
   private volatile boolean closed;
   private volatile @Nullable Runnable changed;
   private volatile long serverClock;
   private long refreshedAt;
   private @Nullable List<Long> regions;
   private boolean regionsAsked;
   private long regionsAskedAt;
   private boolean refreshing;
   private @Nullable Map<Long, SharedChunks.Table> round;
   private int roundId;
   private long roundStartedAt;
   private volatile int tablesAsOf;
   private int roundAsOf;
   private final Map<Long, SharedChunks.Table> tables = new HashMap<>();
   private final Map<Long, Long> tablesAsked = new HashMap<>();
   private final Map<Long, SharedChunks.Asked> chunks = new LinkedHashMap<>(8192, 0.75F, true);
   private final Map<Long, @Nullable SavedChunk> parsed = new LinkedHashMap<Long, SavedChunk>(PARSED_CHUNKS, 0.75F, true) {
      @Override
      protected boolean removeEldestEntry(Entry<Long, @Nullable SavedChunk> eldest) {
         return this.size() > SharedChunks.PARSED_CHUNKS;
      }
   };
   private final Map<Long, Integer> parsedTimes = new HashMap<>();

   private SharedChunks(ClientLevel level) {
      this.level = level;
      this.dimension = level.dimension().location().toString();
      this.tags = new ChunkTags(level, false);
   }

   public static @Nullable SharedChunks open(ClientLevel level) {
      ShareSettings settings = ShareClient.settings();
      if (settings != null && settings.share() && Minecraft.getInstance().getSingleplayerServer() == null) {
         SharedChunks before = current;
         if (before != null) {
            before.close();
         }

         SharedChunks shared = new SharedChunks(level);
         current = shared;
         return shared;
      } else {
         return null;
      }
   }

   public static @Nullable SharedChunks current() {
      return current;
   }

   public void onChange(Runnable changed) {
      this.changed = changed;
   }

   static void closeCurrent() {
      SharedChunks shared = current;
      if (shared != null) {
         shared.close();
      }
   }

   @Override
   public Level level() {
      return this.level;
   }

   @Override
   public boolean isOpen() {
      Minecraft mc = Minecraft.getInstance();
      return !this.closed && current == this && mc.level == this.level && mc.getConnection() != null;
   }

   @Override
   public void close() {
      this.closed = true;
      if (current == this) {
         current = null;
      }

      synchronized (this.lock) {
         for (SharedChunks.Asked asked : this.chunks.values()) {
            asked.answer.completeExceptionally(new ChunkSave.Late("left the server"));
         }

         this.lock.notifyAll();
      }
   }

   @Override
   public Map<Long, int[]> scan() {
      long deadline = System.currentTimeMillis() + 30000L;
      List<Long> toAsk;
      synchronized (this.lock) {
         if (this.regions == null && (!this.regionsAsked || System.currentTimeMillis() - this.regionsAskedAt > 60000L)) {
            this.regionsAsked = true;
            this.regionsAskedAt = System.currentTimeMillis();
            this.send(out -> {
               out.writeByte(1);
               out.writeUTF(this.dimension);
            });
         }

         while (this.regions == null && !this.closed && System.currentTimeMillis() < deadline) {
            this.waitLock(deadline);
         }

         if (this.regions == null) {
            return Map.of();
         }

         toAsk = new ArrayList<>();

         for (long region : this.regions) {
            Long askedAt = this.tablesAsked.get(region);
            if (!this.tables.containsKey(region) && (askedAt == null || System.currentTimeMillis() - askedAt > 60000L)) {
               this.tablesAsked.put(region, System.currentTimeMillis());
               toAsk.add(region);
            }
         }
      }

      if (!toAsk.isEmpty()) {
         this.askTables(toAsk, 0);
         long first = System.currentTimeMillis() + 5000L;
         synchronized (this.lock) {
            while (this.tables.isEmpty() && !this.closed && System.currentTimeMillis() < first) {
               this.waitLock(first);
            }
         }
      }

      Map<Long, int[]> times = new HashMap<>();
      synchronized (this.lock) {
         for (Entry<Long, SharedChunks.Table> table : this.tables.entrySet()) {
            times.put(table.getKey(), (int[])table.getValue().times().clone());
         }

         return times;
      }
   }

   @Override
   public int now() {
      int asOf = this.tablesAsOf;
      return asOf != 0 ? asOf : (int)((System.currentTimeMillis() + this.serverClock) / 1000L);
   }

   @Override
   public void refresh() {
      long now = System.currentTimeMillis();
      synchronized (this.lock) {
         boolean roundOpen = (this.refreshing || this.round != null) && now - this.roundStartedAt < 60000L;
         if (this.regions == null || roundOpen || now - this.refreshedAt < 10000L) {
            return;
         }

         this.refreshedAt = now;
         this.roundStartedAt = now;
         this.refreshing = true;
         this.round = null;
         this.roundId++;
      }

      if (Diorama.DEBUG) {
         Diorama.LOGGER.info("Shared map: asking what changed");
      }

      this.send(out -> {
         out.writeByte(1);
         out.writeUTF(this.dimension);
      });
   }

   private void askTables(List<Long> regions, int round) {
      Entity viewer = MapSession.viewer();
      double x = viewer == null ? 0.0 : viewer.getX();
      double z = viewer == null ? 0.0 : viewer.getZ();
      regions.sort(Comparator.comparingDouble(region -> {
         double dx = (region >> 32) * 512.0 + 256.0 - x;
         double dz = (int)region.longValue() * 512.0 + 256.0 - z;
         return dx * dx + dz * dz;
      }));

      for (int from = 0; from < regions.size() || from == 0 && round != 0; from += 256) {
         List<Long> part = regions.subList(from, Math.min(regions.size(), from + 256));
         boolean last = from + 256 >= regions.size();
         int[] stamps = new int[part.size()];
         synchronized (this.lock) {
            for (int i = 0; i < part.size(); i++) {
               SharedChunks.Table known = this.tables.get(part.get(i));
               stamps[i] = known == null ? 0 : known.stamp();
            }
         }

         this.send(out -> {
            out.writeByte(2);
            out.writeUTF(this.dimension);
            out.writeInt(round);
            out.writeBoolean(last);
            out.writeShort(part.size());

            for (int ix = 0; ix < part.size(); ix++) {
               out.writeInt((int)(part.get(ix) >> 32));
               out.writeInt((int)part.get(ix).longValue());
               out.writeInt(stamps[ix]);
            }
         });
      }
   }

   private void waitLock(long deadline) {
      try {
         this.lock.wait(Math.max(1L, Math.min(100L, deadline - System.currentTimeMillis())));
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         this.closed = true;
      }
   }

   @Override
   public ChunkTags.@Nullable Summary summary(int chunkX, int chunkZ) {
      synchronized (this.lock) {
         SharedChunks.Table table = this.tables.get(SavedChunks.regionKey(chunkX >> 5, chunkZ >> 5));
         int i = (chunkZ & 31) << 5 | chunkX & 31;
         return table != null && table.times()[i] != 0 ? new ChunkTags.Summary(true, table.top()[i], table.floor()[i]) : null;
      }
   }

   @Override
   public int top(int chunkX, int chunkZ) {
      this.knownOrLate(chunkX, chunkZ);
      ChunkTags.Summary summary = this.summary(chunkX, chunkZ);
      return summary == null ? Integer.MIN_VALUE : summary.maxY();
   }

   private void knownOrLate(int chunkX, int chunkZ) {
      long region = SavedChunks.regionKey(chunkX >> 5, chunkZ >> 5);
      synchronized (this.lock) {
         if (this.regions != null && !this.tables.containsKey(region) && this.regions.contains(region)) {
            throw new ChunkSave.Late("region " + (chunkX >> 5) + ", " + (chunkZ >> 5) + " hasn't come yet");
         }
      }
   }

   private int time(int chunkX, int chunkZ) {
      synchronized (this.lock) {
         SharedChunks.Table table = this.tables.get(SavedChunks.regionKey(chunkX >> 5, chunkZ >> 5));
         return table == null ? 0 : table.times()[(chunkZ & 31) << 5 | chunkX & 31];
      }
   }

   @Override
   public boolean prefetches() {
      return true;
   }

   @Override
   public void prefetch(Collection<Long> wanted, boolean urgent) {
      List<Long> ask = new ArrayList<>();
      synchronized (this.lock) {
         long now = System.currentTimeMillis();

         for (long key : wanted) {
            if (this.time((int)(key >> 32), (int)key) != 0) {
               if (this.needsAsking(key, now)) {
                  ask.add(key);
               } else if (urgent) {
                  SharedChunks.Asked asked = this.chunks.get(key);
                  if (asked != null && !asked.answer.isDone()) {
                     ask.add(key);
                  }
               }
            }
         }
      }

      this.askChunks(ask, urgent);
   }

   private boolean needsAsking(long key, long now) {
      SharedChunks.Asked asked = this.chunks.get(key);
      if (asked != null && asked.answer.isDone() && !asked.answer.isCompletedExceptionally()) {
         SharedChunks.Answer answer = asked.answer.getNow(null);
         if (answer != null && answer.time() >= this.time((int)(key >> 32), (int)key)) {
            return false;
         }
      } else if (asked != null && !asked.answer.isDone() && now - asked.askedAt < 60000L) {
         return false;
      }

      SharedChunks.Asked fresh = asked != null && !asked.answer.isDone() ? asked : new SharedChunks.Asked();
      fresh.askedAt = now;
      this.chunks.put(key, fresh);
      return true;
   }

   private void askChunks(List<Long> keys, boolean urgent) {
      for (int from = 0; from < keys.size(); from += 3000) {
         List<Long> part = keys.subList(from, Math.min(keys.size(), from + 3000));
         this.send(out -> {
            out.writeByte(3);
            out.writeUTF(this.dimension);
            out.writeBoolean(urgent);
            out.writeShort(part.size());

            for (long key : part) {
               out.writeInt((int)(key >> 32));
               out.writeInt((int)key);
            }
         });
      }
   }

   @Override
   public @Nullable SavedChunk read(int chunkX, int chunkZ) {
      if (this.closed) {
         throw new ChunkSave.Late("left the server");
      }

      long key = ChunkSnapshot.key(chunkX, chunkZ);
      this.knownOrLate(chunkX, chunkZ);
      int time = this.time(chunkX, chunkZ);
      if (time == 0) {
         return null;
      }

      if (this.parsed.containsKey(key) && this.parsedTimes.getOrDefault(key, 0) >= time) {
         return this.parsed.get(key);
      }

      SharedChunks.Asked asked;
      synchronized (this.lock) {
         if (this.needsAsking(key, System.currentTimeMillis())) {
            this.askChunks(List.of(key), false);
         }

         asked = this.chunks.get(key);
      }

      SharedChunks.Answer answer;
      try {
         answer = asked.answer.get(30000L, TimeUnit.MILLISECONDS);
      } catch (TimeoutException e) {
         throw new ChunkSave.Late("chunk " + chunkX + ", " + chunkZ + " didn't come in time");
      } catch (ExecutionException e) {
         throw new ChunkSave.Late("chunk " + chunkX + ", " + chunkZ + ": " + e.getCause().getMessage());
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         throw new ChunkSave.Late("interrupted");
      }

      SavedChunk chunk = answer.packed() == null ? null : this.unpack(chunkX, chunkZ, answer.packed());
      this.parsed.put(key, chunk);
      this.parsedTimes.put(key, answer.time());
      return chunk;
   }

   private @Nullable SavedChunk unpack(int chunkX, int chunkZ, byte[] packed) {
      try (DataInputStream in = new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(packed)))) {
         CompoundTag tag = NbtIo.read(in, NbtAccounter.create(8388608L));
         return this.tags.read(chunkX, chunkZ, tag);
      } catch (IOException | RuntimeException e) {
         Diorama.LOGGER.warn("Couldn't read shared chunk {}, {}", new Object[]{chunkX, chunkZ, e});
         return null;
      }
   }

   @Override
   public void forgetTops(long region) {
   }

   @Override
   public void clear() {
      this.parsed.clear();
      this.parsedTimes.clear();
   }

   @Override
   public boolean serverBehind() {
      return false;
   }

   static void receive(int kind, DataInputStream in) throws IOException {
      SharedChunks shared = current;
      String dimension = in.readUTF();
      if (shared != null && shared.dimension.equals(dimension)) {
         switch (kind) {
            case 1:
               shared.regions(in);
               break;
            case 2:
               shared.table(in);
               break;
            case 3:
               shared.chunk(in);
               break;
            case 4:
               shared.roundDone(in.readInt());
         }
      }
   }

   private void regions(DataInputStream in) throws IOException {
      long serverTime = in.readLong();
      this.serverClock = serverTime - System.currentTimeMillis();
      int listedAt = (int)(serverTime / 1000L);
      int count = in.readInt();
      if (count >= 0 && count <= in.available() / 8) {
         List<Long> regions = new ArrayList<>(count);

         for (int i = 0; i < count; i++) {
            regions.add(SavedChunks.regionKey(in.readInt(), in.readInt()));
         }

         boolean again;
         synchronized (this.lock) {
            this.regions = regions;
            again = this.refreshing;
            this.refreshing = false;
            if (again) {
               this.roundAsOf = listedAt;
            } else if (this.tablesAsOf == 0) {
               this.tablesAsOf = listedAt;
            }

            if (again) {
               long now = System.currentTimeMillis();

               for (long region : regions) {
                  this.tablesAsked.put(region, now);
               }

               this.round = new HashMap<>();
               this.roundStartedAt = System.currentTimeMillis();
            }

            this.lock.notifyAll();
         }

         if (again) {
            if (Diorama.DEBUG) {
               Diorama.LOGGER.info("Shared map: {} regions listed; asking for their tables", regions.size());
            }

            this.askTables(new ArrayList<>(regions), this.roundId);
         }
      } else {
         throw new IOException("a list of " + count + " regions in " + in.available() + " bytes");
      }
   }

   private void table(DataInputStream in) throws IOException {
      int regionX = in.readInt();
      int regionZ = in.readInt();
      int stamp = in.readInt();
      int[] times = new int[1024];
      short[] top = new short[1024];
      short[] floor = new short[1024];
      Inflater inflater = new Inflater();

      try {
         DataInputStream packed = new DataInputStream(new InflaterInputStream(in, inflater));

         for (int i = 0; i < 1024; i++) {
            times[i] = packed.readInt();
            if (times[i] != 0) {
               top[i] = packed.readShort();
               floor[i] = packed.readShort();
            }
         }
      } finally {
         inflater.end();
      }

      SharedChunks.Table var18 = new SharedChunks.Table(times, top, floor, stamp);
      long var19 = SavedChunks.regionKey(regionX, regionZ);
      synchronized (this.lock) {
         if (this.round != null) {
            this.round.put(var19, var18);
            this.roundStartedAt = System.currentTimeMillis();
            if (Diorama.DEBUG) {
               Diorama.LOGGER.info("Shared map: region {}, {} changed", regionX, regionZ);
            }

            return;
         }

         this.tables.put(var19, var18);
         this.lock.notifyAll();
      }

      this.changed();
   }

   private void roundDone(int id) {
      synchronized (this.lock) {
         if (this.round == null || id != this.roundId) {
            return;
         }

         if (Diorama.DEBUG) {
            Diorama.LOGGER.info("Shared map: {} changed regions taken in", this.round.size());
         }

         for (Entry<Long, SharedChunks.Table> changed : this.round.entrySet()) {
            SharedChunks.Table before = this.tables.get(changed.getKey());
            this.tables.put(changed.getKey(), before == null ? changed.getValue() : changed.getValue().reaching(before));
         }

         this.round = null;
         this.tablesAsOf = this.roundAsOf;
         this.lock.notifyAll();
      }

      this.changed();
   }

   private void chunk(DataInputStream in) throws IOException {
      int chunkX = in.readInt();
      int chunkZ = in.readInt();
      int time = in.readInt();
      int length = in.readInt();
      if (length >= 0 && length <= 524288 && length <= in.available()) {
         byte[] packed = new byte[length];
         in.readFully(packed);
         synchronized (this.lock) {
            long key = ChunkSnapshot.key(chunkX, chunkZ);
            SharedChunks.Asked asked = this.chunks.get(key);
            if (asked == null || asked.answer.isDone()) {
               asked = new SharedChunks.Asked();
               this.chunks.put(key, asked);
            }

            asked.answer.complete(new SharedChunks.Answer(length == 0 ? null : packed, time));
            this.trim();
         }
      } else {
         throw new IOException("a chunk of " + length + " bytes in " + in.available());
      }
   }

   private void trim() {
      if (this.chunks.size() > 8192) {
         Iterator<SharedChunks.Asked> it = this.chunks.values().iterator();

         while (it.hasNext() && this.chunks.size() > 8192) {
            if (it.next().answer.isDone()) {
               it.remove();
            }
         }
      }
   }

   private void changed() {
      Runnable changed = this.changed;
      if (changed != null) {
         changed.run();
      }
   }

   private void send(SharedChunks.Writer writer) {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();

      try (DataOutputStream out = new DataOutputStream(bytes)) {
         writer.write(out);
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }

      byte[] data = bytes.toByteArray();
      Minecraft mc = Minecraft.getInstance();
      mc.execute(() -> {
         if (!this.closed && current == this && mc.getConnection() != null) {
            ShareProtocol.sendToServer(data);
         }
      });
   }

   private record Answer(byte @Nullable [] packed, int time) {
   }

   private static final class Asked {
      final CompletableFuture<SharedChunks.Answer> answer = new CompletableFuture<>();
      long askedAt;
   }

   private record Table(int[] times, short[] top, short[] floor, int stamp) {
      SharedChunks.Table reaching(SharedChunks.Table before) {
         short[] top = (short[])this.top.clone();
         short[] floor = (short[])this.floor.clone();

         for (int i = 0; i < this.times.length; i++) {
            if (this.times[i] != 0 && before.times[i] != 0) {
               top[i] = (short)Math.max(top[i], before.top[i]);
               floor[i] = (short)Math.min(floor[i], before.floor[i]);
            }
         }

         return new SharedChunks.Table(this.times, top, floor, this.stamp);
      }
   }

   private interface Writer {
      void write(DataOutputStream var1) throws IOException;
   }
}
