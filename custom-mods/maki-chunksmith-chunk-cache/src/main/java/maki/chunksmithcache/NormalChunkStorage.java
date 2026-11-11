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
   private static final java.util.concurrent.Semaphore SCANS=new java.util.concurrent.Semaphore(64);
   private static final java.util.concurrent.atomic.AtomicInteger SCAN_IN_FLIGHT=new java.util.concurrent.atomic.AtomicInteger();
   private static final java.util.concurrent.atomic.LongAdder SCAN_REJECTED=new java.util.concurrent.atomic.LongAdder();
   private static final java.util.concurrent.atomic.LongAdder SCAN_COMPLETED=new java.util.concurrent.atomic.LongAdder();
   private static final java.util.concurrent.atomic.LongAdder SCAN_UNKNOWN=new java.util.concurrent.atomic.LongAdder();

   record ScannerStatus(int inFlight,int availablePermits,long rejected,long completed,long unknown) {}
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
      return inspect(level,pos,null);
   }

   static State inspect(ServerLevel level,ChunkPos pos,RegionHeaders headers) {
      State header=headers==null ? headerState(level,pos) : headers.state(level,pos);
      if (header!=State.PROTO) return header;
      try {
         return inspectStatus(level, pos).get(100, java.util.concurrent.TimeUnit.MILLISECONDS);
      } catch (InterruptedException interrupted) {
         Thread.currentThread().interrupt();
         return State.UNKNOWN;
      } catch (Exception unavailable) {
         return State.UNKNOWN;
      }
   }

   /** Call on a storage executor: header reads are blocking; scanner completion never joins. */
   static java.util.concurrent.CompletableFuture<State> inspectAsync(ServerLevel level, ChunkPos pos) {
      // Native scanner observes IOWorker.pendingWrites too. A missing region
      // header alone is not proof of absence while a normal FULL save is pending.
      return inspectStatus(level,pos);
   }

   private static java.util.concurrent.CompletableFuture<State> inspectStatus(ServerLevel level,ChunkPos pos) {
      if (!SCANS.tryAcquire()) {
         SCAN_REJECTED.increment();
         PipelineMetrics.count("normal-scanner-permit-rejected");
         return java.util.concurrent.CompletableFuture.completedFuture(State.UNKNOWN);
      }
      SCAN_IN_FLIGHT.incrementAndGet();
      long started=System.nanoTime();
      try {
         FieldSelector statusSelector = new FieldSelector(StringTag.TYPE, "Status");
         CollectFields collector = new CollectFields(new FieldSelector[]{statusSelector});
         return level.getChunkSource().chunkScanner().scanChunk(pos, collector)
             .handle((ignored, error) -> {
               SCANS.release();
               SCAN_IN_FLIGHT.decrementAndGet();
               SCAN_COMPLETED.increment();
               PipelineMetrics.time("normal-scanner",System.nanoTime()-started);
               State result=error != null ? State.UNKNOWN : collector.getResult() == null ? State.ABSENT
                  : collector.getResult() instanceof CompoundTag tag ? fromTag(tag) : State.UNKNOWN;
               if(result==State.UNKNOWN){
                  SCAN_UNKNOWN.increment();
                  PipelineMetrics.count("normal-scanner-unknown");
               }
               return result;
            });
      } catch (Throwable unavailable) {
         SCANS.release();
         SCAN_IN_FLIGHT.decrementAndGet();
         SCAN_COMPLETED.increment();
         SCAN_UNKNOWN.increment();
         PipelineMetrics.time("normal-scanner",System.nanoTime()-started);
         PipelineMetrics.count("normal-scanner-unknown");
         return java.util.concurrent.CompletableFuture.completedFuture(State.UNKNOWN);
      }
   }

   static ScannerStatus scannerStatus() {
      return new ScannerStatus(SCAN_IN_FLIGHT.get(),SCANS.availablePermits(),
         SCAN_REJECTED.sum(),SCAN_COMPLETED.sum(),SCAN_UNKNOWN.sum());
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
      return snapshotBackfillOwnership(level,center,() -> true);
   }

   static Scope snapshotBackfillOwnership(ServerLevel level, ChunkPos center, java.util.function.BooleanSupplier proceed) {
      int radius = ChunkPyramid.GENERATION_PYRAMID.getStepTo(ChunkStatus.FULL).accumulatedDependencies().getRadius();
      HashSet<Long> suppressed = new HashSet<>();
      int unknown = 0;
      RegionHeaders headers=new RegionHeaders();

      for (int x = center.x - radius; x <= center.x + radius; x++) {
         for (int z = center.z - radius; z <= center.z + radius; z++) {
            if (!proceed.getAsBoolean()) return new Scope(suppressed,unknown+1);
            ChunkPos pos = new ChunkPos(x, z);
            State state = inspect(level, pos,headers);
            if (state == State.ABSENT || state == State.PROTO) {
               suppressed.add(pos.toLong());
            } else if (state == State.UNKNOWN) {
               unknown++;
            }
         }
      }

      return new Scope(suppressed, unknown);
   }

   /** Authority pass: bounded batches include pending native writes, including header misses. */
   static Scope authoritativeScope(ServerLevel level,ChunkPos center,java.util.function.BooleanSupplier proceed) {
      int radius=ChunkPyramid.GENERATION_PYRAMID.getStepTo(ChunkStatus.FULL).accumulatedDependencies().getRadius();
      java.util.ArrayList<ChunkPos> positions=new java.util.ArrayList<>();
      for(int x=center.x-radius;x<=center.x+radius;x++) for(int z=center.z-radius;z<=center.z+radius;z++) positions.add(new ChunkPos(x,z));
      java.util.HashSet<Long> owned=new java.util.HashSet<>(); int unknown=0;
      for(int base=0;base<positions.size();base+=16) {
         if(!proceed.getAsBoolean()) return new Scope(owned,unknown+1);
         int end=Math.min(base+16,positions.size());
         java.util.ArrayList<java.util.concurrent.CompletableFuture<State>> futures=new java.util.ArrayList<>();
         for(int n=base;n<end;n++) futures.add(inspectStatus(level,positions.get(n)));
         try { java.util.concurrent.CompletableFuture.allOf(futures.toArray(java.util.concurrent.CompletableFuture[]::new))
            .get(100,java.util.concurrent.TimeUnit.MILLISECONDS); }
         catch (InterruptedException interrupted) { Thread.currentThread().interrupt(); return new Scope(owned,unknown+1); }
         catch (Exception timeoutOrFailure) { /* incomplete probes remain bounded by SCANS */ }
         for(int n=base;n<end;n++) {
            State state=futures.get(n-base).getNow(State.UNKNOWN);
            if(state==State.ABSENT || state==State.PROTO) owned.add(positions.get(n).toLong());
            else if(state==State.UNKNOWN) unknown++;
         }
         if(unknown!=0) return new Scope(owned,unknown);
      }
      return new Scope(owned,unknown);
   }

   /** One region header read per ownership snapshot, not one file open per dependency. */
   static final class RegionHeaders {
      private final java.util.Map<Long,int[]> headers=new java.util.HashMap<>();
      State state(ServerLevel level,ChunkPos pos) {
         int rx=Math.floorDiv(pos.x,32),rz=Math.floorDiv(pos.z,32);
         long key=((long)rx<<32)^(rz&0xffffffffL);
         int[] header=headers.computeIfAbsent(key,ignored -> {
            Path file=CacheManager.worldRegionRoot(level).resolve("r."+rx+"."+rz+".mca");
            if (Files.notExists(file)) return new int[1024];
            if (!Files.isRegularFile(file)) return new int[0];
            try (RandomAccessFile input=new RandomAccessFile(file.toFile(),"r")) {
               if (input.length()<4096) return new int[0];
               int[] values=new int[1024];
               for (int n=0;n<1024;n++) values[n]=input.readInt();
               return values;
            } catch (IOException failure) { return new int[0]; }
         });
         return header.length==0 ? State.UNKNOWN
            : header[Math.floorMod(pos.x,32)+Math.floorMod(pos.z,32)*32]==0 ? State.ABSENT : State.PROTO;
      }
   }

   private static State headerState(ServerLevel level, ChunkPos pos) {
      Path region = CacheManager.worldRegionRoot(level)
         .resolve("r." + Math.floorDiv(pos.x, 32) + "." + Math.floorDiv(pos.z, 32) + ".mca");
      if (Files.notExists(region)) return State.ABSENT;
      if (!Files.isRegularFile(region)) return State.UNKNOWN;

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
