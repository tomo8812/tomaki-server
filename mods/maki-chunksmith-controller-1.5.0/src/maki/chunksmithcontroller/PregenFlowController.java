package maki.chunksmithcontroller;

import com.kishku7.chunksmith.GenerationTask;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

final class PregenFlowController {
   private static final Map<PregenFlowController.Key, PregenFlowController.Attempt> ACTIVE = new HashMap<>();
   private static final Map<String, PregenFlowController.Attempt> BY_TOKEN = new HashMap<>();
   private static final long RECEIPT_GRACE_NANOS = 60000000000L;
   private static final EnumMap<PregenFlowController.Stage, PregenFlowController.StageState> STAGES = new EnumMap<>(PregenFlowController.Stage.class);
   private static final AtomicLong SEQUENCE = new AtomicLong();
   private static final String PROCESS = UUID.randomUUID().toString();
   private static final int MIN_SERVICE_SAMPLES = 3;
   private static final long SERVICE_SAMPLE_STALE_NANOS = 30000000000L;
   private static long pairConfirmedRuntime;
   private static long pairNew;
   private static long pairRepair;
   private static long uncertain;

   private PregenFlowController() {
   }

   private static long packed(int x, int z) {
      return (long)x << 32 | z & 4294967295L;
   }

   private static boolean active(GenerationTask task) {
      return task != null && TaskOwnership.isOwned(task) && !task.isStopping() && PlayerCenter.admissionEpoch(task) >= 0L;
   }

   private static PregenFlowController.Attempt attempt(String dim, int x, int z, GenerationTask owner) {
      PregenFlowController.Attempt a = ACTIVE.get(new PregenFlowController.Key(dim, packed(x, z)));
      return a != null && a.owner == owner ? a : null;
   }

   private static void progress(PregenFlowController.Attempt a) {
      a.progressNanos = System.nanoTime();
   }

   static synchronized boolean reserveCandidate(String dim, int x, int z, GenerationTask owner) {
      prune();
      if (active(owner) && PlayerCenter.admissionOpen(owner)) {
         PregenFlowController.Key key = new PregenFlowController.Key(dim, packed(x, z));
         long epoch = PlayerCenter.admissionEpoch(owner);
         PregenFlowController.Attempt prior = ACTIVE.get(key);
         if (prior == null) {
            if (STAGES.get(PregenFlowController.Stage.FULL).outstanding + MakiChunksmithController.activeWork() >= CpuGovernor.cap()) {
               return false;
            }

            EnumSet<PregenFlowController.Stage> needed = EnumSet.allOf(PregenFlowController.Stage.class);
            if (!canReserve(needed)) {
               return false;
            }

            PregenFlowController.Attempt a = new PregenFlowController.Attempt(key, owner, epoch, PROCESS + "-" + SEQUENCE.incrementAndGet());
            reserve(a, needed);
            ACTIVE.put(key, a);
            BY_TOKEN.put(a.token, a);
            return true;
         } else {
            return prior.owner == owner && prior.epoch == epoch;
         }
      } else {
         return false;
      }
   }

   static synchronized void classify(String dim, int x, int z, GenerationTask owner, boolean generated) {
      PregenFlowController.Attempt a = attempt(dim, x, z, owner);
      if (a != null) {
         progress(a);
         a.workClass = generated ? PregenFlowController.WorkClass.REPAIR : PregenFlowController.WorkClass.NEW;
      }
   }

   static synchronized boolean beginClassified(String dim, int x, int z, GenerationTask owner) {
      PregenFlowController.Attempt a = attempt(dim, x, z, owner);
      if (a != null && active(owner) && PlayerCenter.admissionOpen(owner) && a.epoch == PlayerCenter.admissionEpoch(owner)) {
         if (a.workClass == PregenFlowController.WorkClass.UNKNOWN && !owner.getChunky().getConfig().isForceLoadExistingChunks()) {
            a.workClass = PregenFlowController.WorkClass.REPAIR;
         }

         a.actualStarted = true;
         progress(a);
         return true;
      } else {
         return false;
      }
   }

   static synchronized String token(String dim, int x, int z, GenerationTask owner) {
      PregenFlowController.Attempt a = attempt(dim, x, z, owner);
      return a != null && a.actualStarted ? a.token : null;
   }

   static synchronized void nativeCoordinateFinished(String dim, int x, int z, GenerationTask owner) {
      PregenFlowController.Attempt a = attempt(dim, x, z, owner);
      if (a != null && !a.actualStarted) {
         progress(a);
         a.pairResolved = true;
         releaseAllUnstarted(a);
         finishIfDone(a);
      }
   }

   private static void fail(PregenFlowController.Attempt a) {
      if (!a.pairResolved && a.actualStarted) {
         uncertain++;
      }

      a.pairResolved = true;
      releaseAllUnstarted(a);
      finishIfDone(a);
   }

   static synchronized void candidateFailed(String dim, int x, int z, GenerationTask owner) {
      PregenFlowController.Attempt a = attempt(dim, x, z, owner);
      if (a != null) {
         fail(a);
      }
   }

   static synchronized void logicalCaptureFailed(String token) {
      PregenFlowController.Attempt a = BY_TOKEN.get(token);
      if (a != null) {
         fail(a);
      }
   }

   static synchronized void stageUnavailable(String token, PregenFlowController.Stage stage) {
      PregenFlowController.Attempt a = BY_TOKEN.get(token);
      if (a != null) {
         if (stage == PregenFlowController.Stage.LOD) {
            a.lodSettled = true;
         }

         releaseIfUnstarted(a, stage);
         fail(a);
      }
   }

   static synchronized void physicalStart(String token, PregenFlowController.Stage stage) {
      PregenFlowController.Attempt a = BY_TOKEN.get(token);
      if (a != null
         && (a.held.contains(stage) || stage == PregenFlowController.Stage.LOD && !a.lodSettled && a.started.contains(PregenFlowController.Stage.FULL))
         && a.started.add(stage)) {
         progress(a);
      }
   }

   static synchronized void physicalTerminal(String token, PregenFlowController.Stage stage, long nanos, boolean success) {
      PregenFlowController.Attempt a = BY_TOKEN.get(token);
      if (a != null) {
         boolean held = a.held.remove(stage);
         boolean started = a.started.remove(stage);
         if (held || started) {
            if (held) {
               STAGES.get(stage).outstanding--;
            }

            progress(a);
            if (stage == PregenFlowController.Stage.LOD) {
               a.lodSettled = true;
            }

            STAGES.get(stage).terminal(nanos, success);
            if (!success) {
               fail(a);
            } else {
               if (stage == PregenFlowController.Stage.FULL) {
                  a.fullReady = true;
                  if (!a.lodSettled && !a.started.contains(PregenFlowController.Stage.LOD)) {
                     fail(a);
                  }
               } else {
                  a.lodReady = true;
               }

               tryConfirmRuntimePair(a);
               finishIfDone(a);
            }
         }
      }
   }

   private static void tryConfirmRuntimePair(PregenFlowController.Attempt a) {
      if (!a.pairResolved && a.actualStarted && a.fullReady && a.lodReady) {
         a.pairResolved = true;
         pairConfirmedRuntime++;
         if (a.workClass == PregenFlowController.WorkClass.NEW) {
            pairNew++;
         } else if (a.workClass == PregenFlowController.WorkClass.REPAIR) {
            pairRepair++;
         }
      }
   }

   static synchronized void taskFinished(GenerationTask owner) {
      for (PregenFlowController.Attempt a : new ArrayList<>(ACTIVE.values())) {
         if (a.owner == owner) {
            ACTIVE.remove(a.key, a);
            a.detachedNanos = System.nanoTime();
            if (!a.actualStarted) {
               fail(a);
            } else {
               for (PregenFlowController.Stage stage : PregenFlowController.Stage.values()) {
                  boolean ready = stage == PregenFlowController.Stage.FULL ? a.fullReady : a.lodReady;
                  boolean expectedFromFull = stage == PregenFlowController.Stage.LOD && !a.lodSettled && a.started.contains(PregenFlowController.Stage.FULL);
                  if (!ready && !a.started.contains(stage) && !expectedFromFull) {
                     fail(a);
                  }
               }
            }

            for (PregenFlowController.Stage stage : EnumSet.copyOf(a.held)) {
               a.held.remove(stage);
               STAGES.get(stage).outstanding--;
            }

            finishIfDone(a);
         }
      }
   }

   static synchronized void expireReceipts() {
      long now = System.nanoTime();

      for (PregenFlowController.Attempt a : new ArrayList<>(BY_TOKEN.values())) {
         long last = a.detachedNanos == 0L ? a.progressNanos : Math.max(a.detachedNanos, a.progressNanos);
         if (now - last >= 60000000000L) {
            a.started.clear();
            fail(a);
            finishIfDone(a);
         }
      }
   }

   static synchronized boolean tracked(String token) {
      return BY_TOKEN.containsKey(token);
   }

   static synchronized void superseded(String token, long nanos) {
      PregenFlowController.Attempt a = BY_TOKEN.get(token);
      if (a != null) {
         boolean held = a.held.remove(PregenFlowController.Stage.LOD);
         boolean started = a.started.remove(PregenFlowController.Stage.LOD);
         if (held || started) {
            if (held) {
               STAGES.get(PregenFlowController.Stage.LOD).outstanding--;
            }

            a.lodSettled = true;
            progress(a);
            fail(a);
            finishIfDone(a);
         }
      }
   }

   static synchronized int fullOutstanding() {
      return STAGES.get(PregenFlowController.Stage.FULL).outstanding;
   }

   static synchronized boolean admissionBlocked() {
      prune();
      return admissionBlockedNoPrune();
   }

   static synchronized PregenFlowController.Snapshot snapshot() {
      prune();
      long oldest = 0L;
      int candidates = 0;
      int actual = 0;
      boolean unknown = false;

      for (PregenFlowController.Attempt a : ACTIVE.values()) {
         long age = Math.max(0L, System.nanoTime() - a.reservedNanos);
         oldest = Math.max(oldest, age);
         if (a.actualStarted) {
            actual++;
         } else {
            candidates++;
         }

         if (System.nanoTime() - a.progressNanos > 60000000000L && !a.held.isEmpty()) {
            unknown = true;
         }
      }

      return new PregenFlowController.Snapshot(
         candidates,
         actual,
         pairConfirmedRuntime,
         pairNew,
         pairRepair,
         uncertain,
         STAGES.get(PregenFlowController.Stage.LOD).outstanding,
         effectiveWindow(PregenFlowController.Stage.LOD),
         STAGES.get(PregenFlowController.Stage.FULL).outstanding,
         effectiveWindow(PregenFlowController.Stage.FULL),
         TimeUnit.NANOSECONDS.toMillis(oldest),
         admissionBlockedNoPrune(),
         unknown,
         fastSignature()
      );
   }

   static synchronized void generationCapChanged(int cap) {
      for (PregenFlowController.StageState s : STAGES.values()) {
         s.serviceEwmaNanos = 0.0;
         s.serviceSamples = 0;
         s.lastServiceSampleNanos = 0L;
         s.window = Math.max(s.safeMin, Math.min(s.window, s.safeMin + 1));
      }
   }

   private static long fastSignature() {
      long result = -3750763034362895579L;

      for (PregenFlowController.Stage s : PregenFlowController.Stage.values()) {
         PregenFlowController.StageState state = STAGES.get(s);
         result = (result ^ s.ordinal()) * 1099511628211L;
         result = (result ^ state.safeMin ^ state.hardMax ^ state.targetWaitNanos) * 1099511628211L;
      }

      return result;
   }

   static synchronized void clear() {
      ACTIVE.clear();
      BY_TOKEN.clear();

      for (PregenFlowController.StageState s : STAGES.values()) {
         s.outstanding = 0;
         s.serviceSamples = 0;
         s.serviceEwmaNanos = 0.0;
         s.lastServiceSampleNanos = 0L;
      }
   }

   private static boolean canReserve(EnumSet<PregenFlowController.Stage> needed) {
      for (PregenFlowController.Stage stage : needed) {
         PregenFlowController.StageState state = STAGES.get(stage);
         int limit = effectiveWindow(stage);
         if (limit <= 0 || state.outstanding + 1 > limit) {
            return false;
         }
      }

      return true;
   }

   private static void reserve(PregenFlowController.Attempt attempt, EnumSet<PregenFlowController.Stage> needed) {
      for (PregenFlowController.Stage stage : needed) {
         attempt.held.add(stage);
         STAGES.get(stage).outstanding++;
      }
   }

   private static void releaseIfUnstarted(PregenFlowController.Attempt attempt, PregenFlowController.Stage stage) {
      if (!attempt.started.contains(stage) && attempt.held.remove(stage)) {
         STAGES.get(stage).outstanding = Math.max(0, STAGES.get(stage).outstanding - 1);
      }
   }

   private static void releaseAllUnstarted(PregenFlowController.Attempt attempt) {
      for (PregenFlowController.Stage stage : PregenFlowController.Stage.values()) {
         releaseIfUnstarted(attempt, stage);
      }
   }

   private static int effectiveWindow(PregenFlowController.Stage stage) {
      PregenFlowController.StageState state = STAGES.get(stage);
      long now = System.nanoTime();
      long oldest = 0L;

      for (PregenFlowController.Attempt attempt : ACTIVE.values()) {
         if (attempt.held.contains(stage)) {
            oldest = Math.max(oldest, Math.max(0L, now - attempt.progressNanos));
         }
      }

      if (oldest >= 25000000000L) {
         return 0;
      } else {
         return oldest >= 15000000000L ? Math.max(state.safeMin, state.window / 2) : state.window;
      }
   }

   private static boolean admissionBlockedNoPrune() {
      for (PregenFlowController.Stage stage : PregenFlowController.Stage.values()) {
         PregenFlowController.StageState state = STAGES.get(stage);
         int limit = effectiveWindow(stage);
         if (limit <= 0 || state.outstanding >= limit) {
            return true;
         }
      }

      return false;
   }

   private static void finishIfDone(PregenFlowController.Attempt a) {
      if (a.held.isEmpty() && a.started.isEmpty() && (!a.actualStarted || a.pairResolved)) {
         ACTIVE.remove(a.key, a);
         BY_TOKEN.remove(a.token, a);
      }
   }

   private static void prune() {
      expireReceipts();

      for (PregenFlowController.Attempt a : new ArrayList<>(ACTIVE.values())) {
         if (!active(a.owner) && !a.actualStarted) {
            a.pairResolved = true;
            releaseAllUnstarted(a);
            finishIfDone(a);
         }
      }
   }

   static {
      STAGES.put(PregenFlowController.Stage.FULL, new PregenFlowController.StageState(3, 1, 16, 8000000000L));
      STAGES.put(PregenFlowController.Stage.LOD, new PregenFlowController.StageState(4, 2, 32, 6000000000L));
   }

   private static final class Attempt {
      final PregenFlowController.Key key;
      final GenerationTask owner;
      final long epoch;
      final String token;
      final long reservedNanos = System.nanoTime();
      long progressNanos = this.reservedNanos;
      long detachedNanos;
      final EnumSet<PregenFlowController.Stage> held = EnumSet.noneOf(PregenFlowController.Stage.class);
      final EnumSet<PregenFlowController.Stage> started = EnumSet.noneOf(PregenFlowController.Stage.class);
      PregenFlowController.WorkClass workClass = PregenFlowController.WorkClass.UNKNOWN;
      boolean actualStarted;
      boolean fullReady;
      boolean lodReady;
      boolean lodSettled;
      boolean pairResolved;

      Attempt(PregenFlowController.Key key, GenerationTask owner, long epoch, String token) {
         this.key = key;
         this.owner = owner;
         this.epoch = epoch;
         this.token = token;
      }
   }

   private record Key(String dimension, long pos) {
   }

   record Snapshot(
      int candidates,
      int actual,
      long pairConfirmedRuntime,
      long pairNew,
      long pairRepair,
      long uncertain,
      int lodOutstanding,
      int lodWindow,
      int fullOutstanding,
      int fullWindow,
      long oldestMillis,
      boolean blocked,
      boolean sensorUnknown,
      long fastSignature
   ) {
   }

   enum Stage {
      FULL,
      LOD;

      // $VF: synthetic method
      private static PregenFlowController.Stage[] $values() {
         return new PregenFlowController.Stage[]{FULL, LOD};
      }
   }

   private static final class StageState {
      int outstanding;
      int window;
      final int safeMin;
      final int hardMax;
      final long targetWaitNanos;
      double serviceEwmaNanos;
      int serviceSamples;
      long lastServiceSampleNanos;
      long terminals;
      long successes;

      StageState(int initial, int safeMin, int hardMax, long targetWaitNanos) {
         this.window = initial;
         this.safeMin = safeMin;
         this.hardMax = hardMax;
         this.targetWaitNanos = targetWaitNanos;
      }

      void terminal(long serviceNanos, boolean success) {
         this.terminals++;
         if (success) {
            this.successes++;
         }

         if (serviceNanos > 0L) {
            long now = System.nanoTime();
            if (this.lastServiceSampleNanos != 0L && now >= this.lastServiceSampleNanos && now - this.lastServiceSampleNanos > 30000000000L) {
               this.serviceEwmaNanos = 0.0;
               this.serviceSamples = 0;
            }

            this.lastServiceSampleNanos = now;
            this.serviceEwmaNanos = this.serviceEwmaNanos == 0.0 ? serviceNanos : this.serviceEwmaNanos * 0.8 + serviceNanos * 0.2;
            if (this.serviceSamples < Integer.MAX_VALUE) {
               this.serviceSamples++;
            }

            if (this.serviceSamples < 3) {
               return;
            }

            long proposed = Math.round(Math.ceil(this.targetWaitNanos / Math.max(1.0, this.serviceEwmaNanos)));
            this.window = (int)Math.max(this.safeMin, Math.min(this.hardMax, proposed));
         }
      }
   }

   enum WorkClass {
      UNKNOWN,
      NEW,
      REPAIR;

      // $VF: synthetic method
      private static PregenFlowController.WorkClass[] $values() {
         return new PregenFlowController.WorkClass[]{UNKNOWN, NEW, REPAIR};
      }
   }
}
