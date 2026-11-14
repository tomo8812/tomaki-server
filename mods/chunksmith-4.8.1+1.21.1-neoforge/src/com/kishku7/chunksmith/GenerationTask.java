package com.kishku7.chunksmith;

import com.kishku7.chunksmith.api.event.task.GenerationCompleteEvent;
import com.kishku7.chunksmith.api.event.task.GenerationProgressEvent;
import com.kishku7.chunksmith.event.task.GenerationTaskFinishEvent;
import com.kishku7.chunksmith.event.task.GenerationTaskUpdateEvent;
import com.kishku7.chunksmith.iterator.ChunkIterator;
import com.kishku7.chunksmith.iterator.ChunkIteratorFactory;
import com.kishku7.chunksmith.lod.CsLodPresenceIndex;
import com.kishku7.chunksmith.lod.LodHorizon;
import com.kishku7.chunksmith.lod.LodPresence;
import com.kishku7.chunksmith.lod.LodSinks;
import com.kishku7.chunksmith.platform.Sender;
import com.kishku7.chunksmith.shape.Shape;
import com.kishku7.chunksmith.shape.ShapeFactory;
import com.kishku7.chunksmith.util.AutoPause;
import com.kishku7.chunksmith.util.ChunkCoordinate;
import com.kishku7.chunksmith.util.ChunkResidency;
import com.kishku7.chunksmith.util.ChunkSettleSupport;
import com.kishku7.chunksmith.util.DispatchControl;
import com.kishku7.chunksmith.util.DispatchStats;
import com.kishku7.chunksmith.util.Formatting;
import com.kishku7.chunksmith.util.GeneratedChunkScan;
import com.kishku7.chunksmith.util.HeapPressure;
import com.kishku7.chunksmith.util.Input;
import com.kishku7.chunksmith.util.Pair;
import com.kishku7.chunksmith.util.RegionCache;
import com.kishku7.chunksmith.util.SettleSweep;
import com.kishku7.chunksmith.util.TickBudget;
import com.kishku7.chunksmith.util.TicketLedger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerationTask implements Runnable {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   private static final int DEFAULT_WORKING_COUNT = Input.tryInteger(System.getProperty("chunksmith.maxWorkingCount")).orElse(50);
   private static final double SAMPLE_INTERVAL = 1000.0 * Math.max(Input.tryInteger(System.getProperty("chunksmith.sampleInterval")).orElse(30), 30);
   private static final double SAMPLE_SUB_INTERVAL = SAMPLE_INTERVAL / 30.0;
   private static final double RATE_MIN_WINDOW_SECONDS = 5.0;
   private static final long NOTICE_INTERVAL_MS = 10000L;
   private static final long DRAIN_TIMEOUT_MS = 60000L;
   private static final int VERIFY_SAMPLE_MAX = 32;
   private static final long VERIFY_TIMEOUT_MS = 15000L;
   private static final long VERIFY_WRITE_DRAIN_MS = 30000L;
   private static final int VERIFY_MIN_MISSING = 4;
   private static final double VERIFY_MISSING_FRACTION = 0.5;
   private static final long RAMP_INTERVAL_MS = 1000L;
   private static final long BACKOFF_INTERVAL_MS = 250L;
   private static final int BACKOFF_STREAK_FOR_HALVING = 4;
   private static final long GOOD_WIDTH_PROMOTE_MS = 10000L;
   private static final long LOD_DRAIN_POLL_MS = 20L;
   private static final long LOD_DRAIN_MAX_MS = 30000L;
   private static final long MSPT_CHECK_INTERVAL_MS = 250L;
   private static final long WRITE_CHECK_INTERVAL_MS = 100L;
   private static final long WRITE_STALL_MILLIS = Math.max(0L, Input.tryInteger(System.getProperty("chunksmith.writeStallMillis")).orElse(2000).intValue());
   private static final long WRITE_STALL_MIN_DEPTH = Math.max(1L, Input.tryInteger(System.getProperty("chunksmith.writeStallMinDepth")).orElse(16).intValue());
   private static final long RESIDENCY_CHECK_INTERVAL_MS = 250L;
   private static final long HEAP_CHECK_INTERVAL_MS = 500L;
   private static final long HEAP_HELD_LOG_INTERVAL_MS = 30000L;
   private static final long RESIDENCY_STALL_MAX_MS = Math.max(
      0L, Input.tryInteger(System.getProperty("chunksmith.residencyStallMaxMillis")).orElse(120000).intValue()
   );
   private final Chunksmith chunky;
   private final Selection selection;
   private final Shape shape;
   private final AtomicLong startTime = new AtomicLong();
   private final AtomicLong updateTime = new AtomicLong();
   private final AtomicLong finishedChunks = new AtomicLong();
   private final AtomicLong generatedChunks = new AtomicLong();
   private final AtomicLong lodOnlyChunks = new AtomicLong();
   private final AtomicLong skippedChunks = new AtomicLong();
   private final AtomicLong horizonChunks = new AtomicLong();
   private final Deque<ChunkCoordinate> verifySamples = new ConcurrentLinkedDeque<>();
   private final AtomicInteger verifySampleCount = new AtomicInteger(0);
   private final AtomicLong generatedSeen = new AtomicLong();
   private final Deque<Pair<Long, AtomicLong>> updateSamples = new ConcurrentLinkedDeque<>();
   private final GenerationTask.Progress progress;
   private final RegionCache.WorldState worldState;
   private final AtomicInteger inFlight = new AtomicInteger(0);
   private final AtomicInteger dispatchLimit = new AtomicInteger(DEFAULT_WORKING_COUNT);
   private final int maxWorkingCount;
   private final AtomicLong lastThrottleNoticeTime = new AtomicLong(0L);
   private final AtomicLong lastRampTime = new AtomicLong(0L);
   private final AtomicLong lastBackoffTime = new AtomicLong(0L);
   private final AtomicInteger goodWidth = new AtomicInteger(Integer.MAX_VALUE);
   private final AtomicInteger overloadStreak = new AtomicInteger(0);
   private static final int NARROW_TICK = 0;
   private static final int NARROW_SLOW_CHUNK = 1;
   private static final int NARROW_LOD_QUEUE = 2;
   private static final long NARROW_REPORT_MS = 300000L;
   private final AtomicLong[] narrowSignals = new AtomicLong[]{new AtomicLong(), new AtomicLong(), new AtomicLong()};
   private volatile long narrowReportAt;
   private final AtomicLong healthySince = new AtomicLong(0L);
   private final AtomicLong lastMsptCheckTime = new AtomicLong(0L);
   private final boolean ioThrottleEnabled;
   private final double targetMspt;
   private final long maxChunkMillis;
   private final long maxQueuedWrites;
   private final long maxLodQueue;
   private final long lodDrainTo;
   private final long resumeQueuedWrites;
   private final AtomicLong lastWriteCheckTime = new AtomicLong(0L);
   private final AtomicLong lastWriteNoticeTime = new AtomicLong(0L);
   private volatile boolean writeQueueStalled;
   private final AtomicLong lastWriteDrainTime = new AtomicLong(0L);
   private long lastQueuedObserved = -1L;
   private final long maxAddedChunks;
   private final long maxHeapPercent;
   private final long tickBudgetMillis;
   private final AtomicLong lastHeapCheckTime = new AtomicLong(0L);
   private final AtomicLong lastHeapNoticeTime = new AtomicLong(0L);
   private final AtomicLong lastHeapHeldLogTime = new AtomicLong(0L);
   private volatile boolean heapStalled;
   private boolean heldNotified;
   private final AtomicLong lastResidencyCheckTime = new AtomicLong(0L);
   private final AtomicLong lastResidencyNoticeTime = new AtomicLong(0L);
   private final AtomicLong residencyStalledSince = new AtomicLong(0L);
   private volatile boolean chunkResidencyStalled;
   private volatile Thread dispatchThread;
   private ChunkIterator chunkIterator;
   private boolean stopped;
   private boolean cancelled;
   private final SettleSweep settleSweep;
   private int[] settleStop;
   private int settleHeldFor;
   private static final int SETTLE_HOLD_COMPLETIONS = 60;
   private long prevTime;
   private static final long SETTLE_TAIL_HOLD_NANOS = 1000000000L;

   public GenerationTask(Chunksmith chunky, Selection selection, long count, long time, boolean cancelled) {
      this(chunky, selection);
      this.chunkIterator = ChunkIteratorFactory.getChunkIterator(selection, count);
      this.finishedChunks.set(count);
      this.cancelled = cancelled;
      this.prevTime = time;
   }

   public GenerationTask(Chunksmith chunky, Selection selection) {
      this.chunky = chunky;
      this.selection = selection;
      this.chunkIterator = ChunkIteratorFactory.getChunkIterator(selection);
      this.shape = ShapeFactory.getShape(selection);
      this.progress = new GenerationTask.Progress(selection.world().getName());
      this.worldState = chunky.getRegionCache().getWorld(selection.world().getName());
      this.ioThrottleEnabled = chunky.getConfig().isIoThrottleEnabled();
      this.targetMspt = chunky.getConfig().getThrottleTargetMspt();
      this.maxChunkMillis = chunky.getConfig().getThrottleMaxChunkMillis();
      this.maxQueuedWrites = chunky.getConfig().getThrottleMaxQueuedWrites();
      this.maxLodQueue = chunky.getConfig().getThrottleMaxLodQueue();
      this.lodDrainTo = chunky.getConfig().getThrottleLodDrainTo();
      this.maxWorkingCount = (int)Math.max(1L, chunky.getConfig().getDispatchMaxConcurrent());
      int startWidth = AutoPause.recommendedStartWidth();
      int opening = startWidth > 0 ? Math.min(this.maxWorkingCount, startWidth) : this.maxWorkingCount;
      this.dispatchLimit.set(opening);
      this.goodWidth.set(DispatchControl.resumeThreshold(this.maxWorkingCount, AutoPause.failedWidth()));
      this.maxAddedChunks = chunky.getConfig().getThrottleMaxAddedChunks();
      this.maxHeapPercent = chunky.getConfig().getThrottleMaxHeapPercent();
      this.tickBudgetMillis = chunky.getConfig().getThrottleTickBudgetMillis();
      TickBudget.configure(this.tickBudgetMillis, chunky.getConfig().getThrottlePlayerReserveMillis(), chunky.getConfig().getThrottleCeilingMillis());
      AutoPause.configure(
         chunky.getConfig().isAutoPauseEnabled(), chunky.getConfig().getAutoPauseGraceSeconds() * 1000L, chunky.getConfig().getThrottleMaxHeapPercent()
      );
      ChunkSettleSupport.configure(
         chunky.getConfig().isPregenSettleEnabled(), chunky.getConfig().getPregenSettleDelayTicks(), chunky.getConfig().getPregenSettleMaxHeld()
      );
      if (chunky.getConfig().isPregenSettleEnabled()) {
         int radius = chunky.getConfig().getPregenSettleRadius();
         this.settleSweep = new SettleSweep(
            selection.centerChunkX() - selection.radiusChunksX(),
            selection.centerChunkZ() - selection.radiusChunksZ(),
            selection.diameterChunksX(),
            selection.diameterChunksZ(),
            radius
         );
      } else {
         this.settleSweep = null;
      }

      this.resumeQueuedWrites = this.maxQueuedWrites > 0L ? Math.max(1L, this.maxQueuedWrites / 2L) : 0L;
   }

   private void preScanExistingChunks(boolean forceLoadExistingChunks) {
      if (!forceLoadExistingChunks) {
         Optional<Path> regionDirectory = this.selection.world().getRegionDirectory();
         if (!regionDirectory.isEmpty()) {
            try {
               int centerChunkX = this.selection.centerChunkX();
               int centerChunkZ = this.selection.centerChunkZ();
               int radiusChunksX = this.selection.radiusChunksX();
               int radiusChunksZ = this.selection.radiusChunksZ();
               long began = System.currentTimeMillis();
               long seeded = GeneratedChunkScan.seed(
                  regionDirectory.get(),
                  this.worldState,
                  centerChunkX - radiusChunksX,
                  centerChunkZ - radiusChunksZ,
                  centerChunkX + radiusChunksX,
                  centerChunkZ + radiusChunksZ
               );
               if (seeded > 0L) {
                  LOGGER.info(
                     "Chunksmith: "
                        + seeded
                        + " chunk(s) in this selection are already generated (found in "
                        + (System.currentTimeMillis() - began)
                        + " ms); they will be skipped without loading."
                  );
               }
            } catch (Throwable t) {
               LOGGER.info("Chunksmith: could not pre-scan the region files (" + t + "); existing chunks will be detected individually as before");
            }
         }
      }
   }

   private void update(int chunkX, int chunkZ, boolean loaded) {
      this.update(chunkX, chunkZ, loaded, loaded);
   }

   private synchronized void update(int chunkX, int chunkZ, boolean loaded, boolean persisted) {
      if (!this.stopped) {
         this.progress.chunkCount = this.finishedChunks.addAndGet(1L);
         this.progress.percentComplete = 100.0F * (float)this.progress.chunkCount / (float)this.chunkIterator.total();
         this.progress.skipped = this.skippedChunks.get();
         this.progress.generated = this.progress.chunkCount - this.progress.skipped;
         long currentTime = System.currentTimeMillis();
         Pair<Long, AtomicLong> bin = this.updateSamples.peekLast();
         if (loaded) {
            if (persisted) {
               this.worldState.setGenerated(chunkX, chunkZ);
            }

            if (bin != null && currentTime - bin.left() < SAMPLE_SUB_INTERVAL) {
               bin.right().addAndGet(1L);
            } else if (this.updateSamples.add(Pair.of(currentTime, new AtomicLong(1L)))) {
               while (!this.updateSamples.isEmpty() && currentTime - this.updateSamples.peek().left() > SAMPLE_INTERVAL) {
                  this.updateSamples.poll();
               }
            }
         }

         Pair<Long, AtomicLong> oldest = this.updateSamples.peek();
         long oldestTime = oldest == null ? currentTime : oldest.left();
         long chunksLeft = this.chunkIterator.total() - this.finishedChunks.get();
         double timeDiff = (currentTime - oldestTime) / 1000.0;
         if (chunksLeft <= 0L || !(timeDiff < 0.1)) {
            long sampleCount = 0L;

            for (Pair<Long, AtomicLong> b : this.updateSamples) {
               sampleCount += b.right().get();
            }

            this.progress.rate = sampleCount / Math.max(timeDiff, 5.0);
            long time;
            if (chunksLeft == 0L) {
               time = (this.prevTime + (currentTime - this.startTime.get())) / 1000L;
               this.progress.complete = true;
            } else {
               time = (long)(chunksLeft / this.progress.rate);
            }

            this.progress.hours = time / 3600L;
            this.progress.minutes = (time - this.progress.hours * 3600L) / 60L;
            this.progress.seconds = time - this.progress.hours * 3600L - this.progress.minutes * 60L;
            this.progress.chunkX = chunkX;
            this.progress.chunkZ = chunkZ;
            this.progress.dispatchCurrent = this.dispatchLimit.get();
            this.progress.dispatchMax = this.maxWorkingCount;
            this.publishDispatchStats();
            this.chunky
               .getEventBus()
               .call(
                  new GenerationProgressEvent(
                     this.progress.world,
                     this.progress.chunkCount,
                     this.progress.complete,
                     this.progress.percentComplete,
                     this.progress.hours,
                     this.progress.minutes,
                     this.progress.seconds,
                     this.progress.rate,
                     this.progress.chunkX,
                     this.progress.chunkZ
                  )
               );
            if (this.progress.complete) {
               this.progress.sendUpdate(this.chunky.getServer().getConsole());
               this.chunky.getEventBus().call(new GenerationTaskUpdateEvent(this));
            } else {
               boolean silentMode = this.chunky.getConfig().isSilent();
               boolean updateIntervalElapsed = (currentTime - this.updateTime.get()) / 1000.0 > this.chunky.getConfig().getUpdateInterval();
               if (updateIntervalElapsed) {
                  if (!silentMode) {
                     this.progress.sendUpdate(this.chunky.getServer().getConsole());
                  }

                  this.chunky.getEventBus().call(new GenerationTaskUpdateEvent(this));
                  this.updateTime.set(currentTime);
               }
            }
         }
      }
   }

   private void adjustFromTickHealth() {
      long now = System.currentTimeMillis();
      long last = this.lastMsptCheckTime.get();
      if (now - last >= 250L && this.lastMsptCheckTime.compareAndSet(last, now)) {
         this.publishDispatchStats();
         double mspt = this.chunky.getServer().getMillisPerTick();
         if (!(mspt < 0.0)) {
            boolean addingLoad = this.inFlight.get() > 0
               && !this.writeQueueStalled
               && !this.chunkResidencyStalled
               && !this.heapStalled
               && !TickBudget.isProbing();
            TickBudget.sample(mspt, addingLoad, this.chunky.getServer().getPlayers().size());
            double target = this.effectiveTargetMspt();
            if (mspt > target + 3.0) {
               this.narrowSignals[0].incrementAndGet();
               this.backoff();
            } else if (mspt < target - 3.0) {
               this.noteHealthy(now);
               double headroom = target - mspt;
               int steps = 1;
               if (headroom > 12.0) {
                  steps = 8;
               } else if (headroom > 6.0) {
                  steps = 3;
               }

               for (int i = 0; i < steps; i++) {
                  this.rampUp(steps > 1);
               }
            }
         }
      }
   }

   private double effectiveTargetMspt() {
      if (this.tickBudgetMillis <= 0L) {
         return this.targetMspt;
      }

      double measured = TickBudget.effectiveTarget();
      return measured < 0.0 ? this.targetMspt : Math.max(this.targetMspt, measured);
   }

   private void adjustFromChunkLatency(long elapsed) {
      if (elapsed > this.maxChunkMillis) {
         this.narrowSignals[1].incrementAndGet();
         this.backoff();
      }
   }

   private void adjustFromLodQueue() {
      if (this.maxLodQueue > 0L) {
         if (LodSinks.get().queueDepth() > this.maxLodQueue) {
            if (this.lodDrainTo <= 0L) {
               this.narrowSignals[2].incrementAndGet();
               this.backoff();
            } else {
               this.drainLodQueue();
            }
         }
      }
   }

   private void drainLodQueue() {
      long started = System.currentTimeMillis();
      long depth = LodSinks.get().queueDepth();
      long startedAt = depth;
      boolean waited = false;

      while (depth > this.lodDrainTo && !this.cancelled) {
         if (System.currentTimeMillis() - started > 30000L) {
            LOGGER.warn(
               String.format(
                  "Chunksmith: the LOD sink did not drain below %d within %ds (still %d); carrying on and letting the governor handle it.",
                  this.lodDrainTo,
                  30L,
                  depth
               )
            );
            return;
         }

         try {
            Thread.sleep(20L);
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
         }

         waited = true;
         depth = LodSinks.get().queueDepth();
      }

      if (waited) {
         LOGGER.debug(
            String.format("Chunksmith: paused generation for %dms while the LOD sink drained %d -> %d", System.currentTimeMillis() - started, startedAt, depth)
         );
      }
   }

   private void publishDispatchStats() {
      DispatchStats.publish(this.inFlight.get(), this.dispatchLimit.get(), this.goodWidth.get(), this.maxWorkingCount, LodSinks.get().queueDepth());
      this.reportNarrowing(System.currentTimeMillis());
   }

   private void reportNarrowing(long now) {
      long due = this.narrowReportAt;
      if (due == 0L) {
         this.narrowReportAt = now + 300000L;
      } else if (now >= due) {
         this.narrowReportAt = now + 300000L;
         long tick = this.narrowSignals[0].getAndSet(0L);
         long slow = this.narrowSignals[1].getAndSet(0L);
         long lod = this.narrowSignals[2].getAndSet(0L);
         int width = this.dispatchLimit.get();
         if (width * 2 <= this.maxWorkingCount) {
            LOGGER.info(
               "Chunksmith: generating {} chunks at a time (ceiling {}). What narrowed it over the last {} min: tick time {}, a slow chunk load {}, the LOD queue {}.",
               new Object[]{width, this.maxWorkingCount, 5L, tick, slow, lod}
            );
         }
      }
   }

   private void backoff() {
      long now = System.currentTimeMillis();
      long last = this.lastBackoffTime.get();
      if (now - last >= 250L && this.lastBackoffTime.compareAndSet(last, now)) {
         this.healthySince.set(0L);
         if (last != 0L && now - last > 750L) {
            this.overloadStreak.set(0);
         }

         boolean sustained = this.overloadStreak.incrementAndGet() >= 4;
         boolean belowFloorEarned = AutoPause.strugglingSeconds(now) * 1000L >= AutoPause.belowFloorGraceMillis();

         int current;
         int reduced;
         do {
            current = this.dispatchLimit.get();
            if (current <= 1) {
               return;
            }

            reduced = DispatchControl.reduce(current, sustained, belowFloorEarned);
         } while (!this.dispatchLimit.compareAndSet(current, reduced));

         if (sustained) {
            int settled = reduced;
            this.goodWidth.updateAndGet(previous -> Math.max(8, Math.min(previous, settled)));
            this.overloadStreak.set(0);
         }

         this.lastRampTime.set(now);
         this.maybeNotify(reduced);
      }
   }

   private void rampUp() {
      this.rampUp(false);
   }

   private void rampUp(boolean burst) {
      long now = System.currentTimeMillis();
      long last = this.lastRampTime.get();
      if (burst || now - last >= 1000L && this.lastRampTime.compareAndSet(last, now)) {
         if (burst) {
            this.lastRampTime.set(now);
         }

         int current;
         do {
            current = this.dispatchLimit.get();
            if (current >= this.maxWorkingCount) {
               return;
            }

            if (burst && !DispatchControl.mayBurst(current, this.goodWidth.get())) {
               return;
            }
         } while (!this.dispatchLimit.compareAndSet(current, current + 1));

         this.maybeNotify(current + 1);
      }
   }

   private void noteHealthy(long now) {
      this.overloadStreak.set(0);
      long since = this.healthySince.get();
      if (since == 0L) {
         this.healthySince.compareAndSet(0L, now);
      } else if (now - since >= 10000L) {
         int current = this.dispatchLimit.get();
         int known = this.goodWidth.get();
         if (current > known) {
            this.goodWidth.set(current);
         }

         this.healthySince.set(now);
         AutoPause.noteSustainedHealth();
      }
   }

   private void maybeNotify(int newLimit) {
      long now = System.currentTimeMillis();
      long last = this.lastThrottleNoticeTime.get();
      if (now - last >= 10000L && this.lastThrottleNoticeTime.compareAndSet(last, now)) {
         this.chunky.getServer().getConsole().sendMessagePrefixed("task_throttle_notice", newLimit, this.maxWorkingCount);
      }
   }

   private void evaluateWriteBackpressure() {
      long now = System.currentTimeMillis();
      long last = this.lastWriteCheckTime.get();
      if (now - last >= 100L && this.lastWriteCheckTime.compareAndSet(last, now)) {
         long queued = this.selection.world().getQueuedChunkWrites();
         if (queued < 0L) {
            this.writeQueueStalled = false;
            this.lastQueuedObserved = -1L;
         } else {
            long prev = this.lastQueuedObserved;
            this.lastQueuedObserved = queued;
            if (queued <= 0L || prev < 0L || queued < prev) {
               this.lastWriteDrainTime.set(now);
            }

            long sinceDrain = now - this.lastWriteDrainTime.get();
            if (this.writeQueueStalled) {
               boolean resume;
               if (WRITE_STALL_MILLIS <= 0L) {
                  resume = queued <= this.resumeQueuedWrites;
               } else {
                  resume = queued <= 0L || queued <= this.resumeQueuedWrites && sinceDrain < WRITE_STALL_MILLIS;
               }

               if (resume) {
                  this.writeQueueStalled = false;
               }
            } else {
               boolean depthExceeded = this.maxQueuedWrites > 0L && queued >= this.maxQueuedWrites;
               boolean drainStalled = WRITE_STALL_MILLIS > 0L && queued >= WRITE_STALL_MIN_DEPTH && sinceDrain >= WRITE_STALL_MILLIS;
               if (depthExceeded || drainStalled) {
                  this.writeQueueStalled = true;
                  this.maybeNotifyWrite(queued);
               }
            }
         }
      }
   }

   private void evaluateChunkResidency() {
      if (this.maxAddedChunks > 0L) {
         long now = System.currentTimeMillis();
         long last = this.lastResidencyCheckTime.get();
         if (now - last >= 250L && this.lastResidencyCheckTime.compareAndSet(last, now)) {
            long added = ChunkResidency.addedChunks();
            if (added >= 0L) {
               if (this.chunkResidencyStalled) {
                  if (added <= Math.max(1L, this.maxAddedChunks / 2L)) {
                     this.chunkResidencyStalled = false;
                     this.residencyStalledSince.set(0L);
                  } else {
                     long since = this.residencyStalledSince.get();
                     if (RESIDENCY_STALL_MAX_MS > 0L && since > 0L && now - since >= RESIDENCY_STALL_MAX_MS) {
                        this.chunkResidencyStalled = false;
                        this.residencyStalledSince.set(0L);
                        this.lastResidencyNoticeTime.set(0L);
                        this.chunky.getServer().getConsole().sendMessagePrefixed("task_residency_stuck_notice", this.maxAddedChunks, (now - since) / 1000L);
                     }
                  }
               } else {
                  if (added >= this.maxAddedChunks) {
                     this.chunkResidencyStalled = true;
                     this.residencyStalledSince.set(now);
                     this.maybeNotifyResidency(added);
                  }
               }
            }
         }
      }
   }

   private void evaluateHeapPressure() {
      if (this.maxHeapPercent > 0L) {
         long now = System.currentTimeMillis();
         long last = this.lastHeapCheckTime.get();
         if (now - last >= 500L && this.lastHeapCheckTime.compareAndSet(last, now)) {
            long heldFor = HeapPressure.heldMillis(now);
            long waitedFor = HeapPressure.waitingMillis(now);
            boolean hold = HeapPressure.shouldHold(this.heapStalled, this.maxHeapPercent);
            if (HeapPressure.consumeReleasedBlind()) {
               LOGGER.warn(
                  "Chunksmith: the heap guard held generation for {}s, and {} MB of what it counts went {}s without a garbage collection, so its reading ({}% raw) cannot tell uncollected garbage from memory in use. Generating again, which lets the collector run; the guard closes again if the memory really is in use.",
                  new Object[]{heldFor / 1000L, HeapPressure.staleMegabytes(), waitedFor / 1000L, String.format("%.0f", HeapPressure.usedPercent())}
               );
            }

            if (hold && !this.heapStalled) {
               this.maybeNotifyHeap();
               this.lastHeapHeldLogTime.set(now);
            } else if (hold) {
               long lastLog = this.lastHeapHeldLogTime.get();
               if (now - lastLog >= 30000L && this.lastHeapHeldLogTime.compareAndSet(lastLog, now)) {
                  double after = HeapPressure.afterCollectionPercent();
                  long stale = HeapPressure.staleMegabytes();
                  LOGGER.info(
                     "Chunksmith: generation still held by the heap guard after {}s -- {}% raw, {} after the last collections, resumes at or under {}%; {}.",
                     new Object[]{
                        heldFor / 1000L,
                        String.format("%.0f", HeapPressure.usedPercent()),
                        after < 0.0 ? "not reported" : String.format("%.0f%%", after),
                        Math.max(50L, this.maxHeapPercent - 15L),
                        stale < 0L
                           ? "no per-pool collection data"
                           : (
                              stale == 0L
                                 ? "every large pool has been collected and it is still full, so this is memory in use"
                                 : stale + " MB has gone " + waitedFor / 1000L + "s without a collection"
                           )
                     }
                  );
               }
            }

            this.heapStalled = hold;
         }
      }
   }

   private void maybeNotifyHeap() {
      long now = System.currentTimeMillis();
      long last = this.lastHeapNoticeTime.get();
      if (now - last >= 10000L && this.lastHeapNoticeTime.compareAndSet(last, now)) {
         this.chunky
            .getServer()
            .getConsole()
            .sendMessagePrefixed(
               "task_heap_backpressure_notice",
               String.format("%.0f", HeapPressure.usedPercent()),
               HeapPressure.usedMegabytes(),
               HeapPressure.maxMegabytes(),
               this.maxHeapPercent
            );
      }
   }

   private void maybeNotifyResidency(long added) {
      long now = System.currentTimeMillis();
      long last = this.lastResidencyNoticeTime.get();
      if (now - last >= 10000L && this.lastResidencyNoticeTime.compareAndSet(last, now)) {
         this.chunky
            .getServer()
            .getConsole()
            .sendMessagePrefixed("task_residency_backpressure_notice", added, this.maxAddedChunks, ChunkResidency.loadedChunks());
      }
   }

   private void maybeNotifyWrite(long queued) {
      long now = System.currentTimeMillis();
      long last = this.lastWriteNoticeTime.get();
      if (now - last >= 10000L && this.lastWriteNoticeTime.compareAndSet(last, now)) {
         this.chunky.getServer().getConsole().sendMessagePrefixed("task_write_backpressure_notice", queued, this.maxQueuedWrites);
      }
   }

   @Override
   public void run() {
      String poolThreadName = Thread.currentThread().getName();
      Thread.currentThread().setName(String.format("Chunksmith-%s Thread", this.selection.world().getName()));
      this.dispatchThread = Thread.currentThread();
      if (!this.chunkIterator.process()) {
         this.stop(true);
      }

      boolean forceLoadExistingChunks = this.chunky.getConfig().isForceLoadExistingChunks();
      this.preScanExistingChunks(forceLoadExistingChunks);
      CsLodPresenceIndex lodIndex = forceLoadExistingChunks
         ? null
         : (
            this.selection.horizon() > 0.0
               ? LodPresence.horizonIndexFor(this.selection.world().getName())
               : LodPresence.indexFor(this.selection.world().getName())
         );
      CsLodPresenceIndex.Cost lodCostBefore = lodIndex == null ? null : lodIndex.cost();
      String worldName = this.selection.world().getName();
      double horizon = this.selection.horizon();
      boolean horizonRound = "circle".equals(this.selection.shape()) || "ellipse".equals(this.selection.shape());
      boolean horizonOn = horizon > 0.0;
      if (horizonOn) {
         String reason = LodHorizon.unavailableReason();
         if (reason == null && lodIndex == null) {
            reason = forceLoadExistingChunks ? "forceLoadExistingChunks is on" : "LOD generation is off";
         }

         if (reason != null) {
            this.chunky.getServer().getConsole().sendMessagePrefixed("task_horizon_unavailable", worldName, reason);
            horizonOn = false;
            this.stop(false);
         } else {
            LodHorizon.arm(worldName, this.selection.centerChunkX(), this.selection.centerChunkZ(), horizon, horizonRound, this.worldState::isGenerated);
            this.chunky
               .getServer()
               .getConsole()
               .sendMessagePrefixed("task_horizon_start", worldName, Formatting.number(horizon), Formatting.number(horizon + 32.0));
         }
      }

      ChunkResidency.noteTaskStart();
      HeapPressure.reset();
      TickBudget.reset();
      TicketLedger.reset();
      this.startTime.set(System.currentTimeMillis());

      while (!this.stopped && this.chunkIterator.hasNext()) {
         ChunkCoordinate chunk = this.chunkIterator.next();
         int chunkCenterX = (chunk.x() << 4) + 8;
         int chunkCenterZ = (chunk.z() << 4) + 8;
         if (!this.shape.isBounding(chunkCenterX, chunkCenterZ)) {
            this.update(chunk.x(), chunk.z(), false);
         } else {
            boolean ring = horizonOn
               && LodHorizon.isBeyond(this.selection.centerChunkX(), this.selection.centerChunkZ(), horizon, horizonRound, chunk.x(), chunk.z());
            if (ring && lodIndex.hasLod(chunk.x(), chunk.z())) {
               this.skippedChunks.incrementAndGet();
               this.update(chunk.x(), chunk.z(), false);
            } else {
               boolean lodBackfill = false;
               if (!forceLoadExistingChunks && this.worldState.isGenerated(chunk.x(), chunk.z())) {
                  if (lodIndex == null || lodIndex.hasLod(chunk.x(), chunk.z())) {
                     this.skippedChunks.incrementAndGet();
                     this.update(chunk.x(), chunk.z(), false);
                     continue;
                  }

                  lodBackfill = true;
               }

               boolean lodOnly = lodBackfill;

               while (!this.stopped) {
                  if (this.ioThrottleEnabled) {
                     this.adjustFromTickHealth();
                     if (this.maxQueuedWrites > 0L) {
                        this.evaluateWriteBackpressure();
                     }
                  }

                  this.adjustFromLodQueue();
                  this.evaluateChunkResidency();
                  this.evaluateHeapPressure();
                  boolean probing = TickBudget.shouldProbe(System.currentTimeMillis());
                  boolean gated = this.writeQueueStalled || this.chunkResidencyStalled || this.heapStalled;
                  long gateNow = System.currentTimeMillis();
                  double mspt = this.chunky.getServer().getMillisPerTick();
                  boolean tickFarBehind = mspt >= 0.0 && TickBudget.atCeiling() && mspt > TickBudget.effectiveTarget() + 3.0;
                  boolean atHardMinimum = this.dispatchLimit.get() <= 1;
                  AutoPause.noteStruggling(gated || tickFarBehind, atHardMinimum, gateNow);
                  if (AutoPause.shouldPause(gateNow, atHardMinimum)) {
                     long pausedTotal = this.chunkIterator.total();
                     long pausedDone = this.finishedChunks.get();
                     LOGGER.warn(
                        "Chunksmith: AUTO-PAUSED pre-gen for {} after {}s unable to sustain it -- trigger: {} (writeQueueStalled={} chunkResidencyStalled={} heapStalled={} tickFarBehind={} mspt={} effectiveTarget={} msptBand={} atCeiling={}); stopped at {}/{} chunks ({}%). It will resume by itself once the server has been healthy for {}s (auto-pause #{} for this run). Set autoPauseOnOverload=false to push on regardless.",
                        new Object[]{
                           this.selection.world().getName(),
                           AutoPause.strugglingSeconds(gateNow),
                           gated ? "a Chunksmith gate held" : "tick running far behind target",
                           this.writeQueueStalled,
                           this.chunkResidencyStalled,
                           this.heapStalled,
                           tickFarBehind,
                           String.format("%.1f", mspt),
                           String.format("%.1f", TickBudget.effectiveTarget()),
                           3.0,
                           TickBudget.atCeiling(),
                           pausedDone,
                           pausedTotal,
                           pausedTotal > 0L ? String.format("%.2f", 100.0 * pausedDone / pausedTotal) : "?",
                           AutoPause.resumeGraceMillis() / 1000L,
                           AutoPause.consecutivePauses() + 1
                        }
                     );
                     this.chunky
                        .getServer()
                        .getConsole()
                        .sendMessagePrefixed("task_auto_paused", this.selection.world().getName(), AutoPause.strugglingSeconds(gateNow));
                     AutoPause.noteWidthFailed(this.dispatchLimit.get());
                     AutoPause.markAutoPaused(this.selection.world().getName());
                     this.stop(false);
                     break;
                  }

                  if (gated != this.heldNotified) {
                     this.heldNotified = gated;
                     ChunkResidency.noteGenerationHeld(gated);
                  }

                  if (!gated && !probing && this.inFlight.get() < Math.max(1, this.dispatchLimit.get())) {
                     break;
                  }

                  LockSupport.parkNanos(1000000L);
               }

               if (this.stopped) {
                  break;
               }

               this.inFlight.incrementAndGet();
               long dispatchTime = System.currentTimeMillis();
               boolean[] lodOnlyRing = new boolean[]{false};
               CompletableFuture<Boolean> isChunkGenerated = !forceLoadExistingChunks && !lodOnly
                  ? this.selection.world().isChunkGenerated(chunk.x(), chunk.z())
                  : CompletableFuture.completedFuture(false);
               isChunkGenerated.<Void>thenCompose(generated -> {
                  if (Boolean.TRUE.equals(generated)) {
                     if (lodIndex != null && !lodIndex.hasLod(chunk.x(), chunk.z())) {
                        lodIndex.markLod(chunk.x(), chunk.z());
                        this.lodOnlyChunks.incrementAndGet();
                        return this.selection.world().getChunkAtAsync(chunk.x(), chunk.z());
                     } else {
                        this.skippedChunks.incrementAndGet();
                        return CompletableFuture.completedFuture(null);
                     }
                  } else {
                     if (lodIndex != null) {
                        lodIndex.markLod(chunk.x(), chunk.z());
                     }

                     if (lodOnly) {
                        this.lodOnlyChunks.incrementAndGet();
                     } else if (ring) {
                        lodOnlyRing[0] = true;
                        this.horizonChunks.incrementAndGet();
                     } else {
                        this.generatedChunks.incrementAndGet();
                        this.noteVerifySample(chunk.x(), chunk.z());
                     }

                     return this.selection.world().getChunkAtAsync(chunk.x(), chunk.z());
                  }
               }).whenComplete((ignored, throwable) -> {
                  long elapsed = System.currentTimeMillis() - dispatchTime;
                  if (this.ioThrottleEnabled) {
                     this.adjustFromChunkLatency(elapsed);
                  }

                  try {
                     this.update(chunk.x(), chunk.z(), true, !lodOnlyRing[0]);
                  } finally {
                     this.inFlight.decrementAndGet();
                     LockSupport.unpark(this.dispatchThread);
                  }

                  this.driveSettleSweep(chunk.x(), chunk.z());
               });
            }
         }
      }

      long drainDeadline = System.currentTimeMillis() + 60000L;

      while (this.inFlight.get() > 0 && System.currentTimeMillis() < drainDeadline) {
         LockSupport.parkNanos(1000000L);
      }

      if (lodIndex != null) {
         this.chunky
            .getServer()
            .getConsole()
            .sendMessagePrefixed(
               "task_lod_summary",
               this.selection.world().getName(),
               this.generatedChunks.get(),
               this.lodOnlyChunks.get(),
               this.skippedChunks.get(),
               lodIndex.describeCostSince(lodCostBefore)
            );
      }

      if (horizonOn) {
         this.chunky.getServer().getConsole().sendMessagePrefixed("task_horizon_summary", worldName, this.horizonChunks.get());
      }

      if (!this.stopped) {
         this.verifyGeneratedSample();
      }

      if (this.stopped) {
         this.chunky.getServer().getConsole().sendMessagePrefixed("task_stopped", this.selection.world().getName());
      } else {
         this.cancelled = true;
      }

      this.chunky.getTaskLoader().saveTask(this);
      this.chunky.getGenerationTasks().remove(this.selection.world().getName());
      Thread.currentThread().setName(poolThreadName);
      ChunkResidency.noteGenerationHeld(false);
      this.finishSettleSweep();
      this.selection.world().settleDrain();
      if (horizonOn) {
         LodHorizon.release(worldName);
      }

      DispatchStats.clear();
      ChunkResidency.noteTaskEnd();
      this.chunky.getEventBus().call(new GenerationTaskFinishEvent(this));
      this.chunky.getEventBus().call(new GenerationCompleteEvent(this.selection.world().getName()));
   }

   private void noteVerifySample(int x, int z) {
      if (this.verifySampleCount.get() < 32) {
         long seen = this.generatedSeen.incrementAndGet();
         long stride = Math.max(1L, this.chunkIterator.total() / 32L);
         if (seen % stride == 0L && this.verifySampleCount.incrementAndGet() <= 32) {
            this.verifySamples.add(new ChunkCoordinate(x, z));
         }
      }
   }

   private void verifyGeneratedSample() {
      if (!this.verifySamples.isEmpty()) {
         long drainUntil = System.currentTimeMillis() + 30000L;

         while (System.currentTimeMillis() < drainUntil) {
            long queued = this.selection.world().getQueuedChunkWrites();
            if (queued <= 0L) {
               break;
            }

            LockSupport.parkNanos(50000000L);
         }

         List<ChunkCoordinate> missing = new ArrayList<>();
         long deadline = System.currentTimeMillis() + 15000L;
         int checked = 0;

         for (ChunkCoordinate coordinate : this.verifySamples) {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0L) {
               break;
            }

            try {
               Boolean present = this.selection.world().isChunkGenerated(coordinate.x(), coordinate.z()).get(remaining, TimeUnit.MILLISECONDS);
               checked++;
               if (!Boolean.TRUE.equals(present)) {
                  missing.add(coordinate);
               }
            } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
               break;
            } catch (Exception e) {
               break;
            }
         }

         if (missing.size() >= 4 && !(missing.size() < checked * 0.5)) {
            StringBuilder examples = new StringBuilder();

            for (int i = 0; i < Math.min(3, missing.size()); i++) {
               if (i > 0) {
                  examples.append("; ");
               }

               examples.append(missing.get(i).x()).append(",").append(missing.get(i).z());
            }

            this.chunky
               .getServer()
               .getConsole()
               .sendMessagePrefixed("task_verify_failed", this.selection.world().getName(), missing.size(), checked, examples.toString());
         }
      }
   }

   private synchronized void driveSettleSweep(int chunkX, int chunkZ) {
      if (this.settleSweep != null && !this.stopped) {
         this.settleSweep.markGenerated(chunkX, chunkZ);
         if (this.settleStop != null) {
            if (++this.settleHeldFor < 60) {
               return;
            }

            this.selection.world().settleRelease(this.settleStop[0], this.settleStop[1], this.settleSweep.radius());
            this.settleStop = null;
         }

         int[] next = this.settleSweep.nextStop();
         if (next != null) {
            this.settleStop = next;
            this.settleHeldFor = 0;
            this.selection.world().settleLoad(next[0], next[1], this.settleSweep.radius());
         }
      }
   }

   private void finishSettleSweep() {
      if (this.settleSweep != null) {
         synchronized (this) {
            if (this.settleStop != null) {
               this.selection.world().settleRelease(this.settleStop[0], this.settleStop[1], this.settleSweep.radius());
               this.settleStop = null;
            }
         }

         int[] stop;
         while (!this.cancelled && (stop = this.settleSweep.nextStop()) != null) {
            this.selection.world().settleLoad(stop[0], stop[1], this.settleSweep.radius());
            LockSupport.parkNanos(1000000000L);
            this.selection.world().settleRelease(stop[0], stop[1], this.settleSweep.radius());
         }
      }
   }

   public void stop(boolean cancelled) {
      this.stopped = true;
      this.cancelled = cancelled;
   }

   public Chunksmith getChunky() {
      return this.chunky;
   }

   public Selection getSelection() {
      return this.selection;
   }

   public long getCount() {
      return this.finishedChunks.get();
   }

   public long getGeneratedChunks() {
      return this.generatedChunks.get();
   }

   public long getLodOnlyChunks() {
      return this.lodOnlyChunks.get();
   }

   public long getSkippedChunks() {
      return this.skippedChunks.get();
   }

   public ChunkIterator getChunkIterator() {
      return this.chunkIterator;
   }

   public Shape getShape() {
      return this.shape;
   }

   public boolean isStopping() {
      return this.stopped;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public long getTotalTime() {
      return this.prevTime + (this.startTime.get() > 0L ? System.currentTimeMillis() - this.startTime.get() : 0L);
   }

   public GenerationTask.Progress getProgress() {
      return this.progress;
   }

   public static final class Progress {
      private static final long THROTTLE_STATUS_INTERVAL_MS = 5000L;
      private final String world;
      private long chunkCount;
      private boolean complete;
      private float percentComplete;
      private long hours;
      private long minutes;
      private long seconds;
      private double rate;
      private long skipped;
      private long generated;
      private int chunkX;
      private int chunkZ;
      private int dispatchCurrent;
      private int dispatchMax;
      private long lastThrottleStatusPrintMs = 0L;

      private Progress(String world) {
         this.world = world;
      }

      public String getWorld() {
         return this.world;
      }

      public long getChunkCount() {
         return this.chunkCount;
      }

      public boolean isComplete() {
         return this.complete;
      }

      public float getPercentComplete() {
         return this.percentComplete;
      }

      public long getHours() {
         return this.hours;
      }

      public long getMinutes() {
         return this.minutes;
      }

      public long getSeconds() {
         return this.seconds;
      }

      public double getRate() {
         return this.rate;
      }

      public long getSkipped() {
         return this.skipped;
      }

      public long getGenerated() {
         return this.generated;
      }

      public int getChunkX() {
         return this.chunkX;
      }

      public int getChunkZ() {
         return this.chunkZ;
      }

      public int getDispatchCurrent() {
         return this.dispatchCurrent;
      }

      public int getDispatchMax() {
         return this.dispatchMax;
      }

      public void sendUpdate(Sender sender) {
         if (this.complete) {
            sender.sendMessagePrefixed(
               "task_done",
               this.world,
               this.chunkCount,
               String.format("%.2f", this.percentComplete),
               String.format("%01d", this.hours),
               String.format("%02d", this.minutes),
               String.format("%02d", this.seconds),
               this.skipped,
               this.generated
            );
         } else {
            sender.sendMessagePrefixed(
               "task_update",
               this.world,
               this.chunkCount,
               String.format("%.2f", this.percentComplete),
               String.format("%01d", this.hours),
               String.format("%02d", this.minutes),
               String.format("%02d", this.seconds),
               String.format("%.1f", this.rate),
               this.chunkX,
               this.chunkZ,
               this.skipped,
               this.generated
            );
            if (this.dispatchCurrent < this.dispatchMax) {
               long now = System.currentTimeMillis();
               if (now - this.lastThrottleStatusPrintMs >= 5000L) {
                  this.lastThrottleStatusPrintMs = now;
                  sender.sendMessagePrefixed("task_throttle_status", this.dispatchCurrent, this.dispatchMax);
               }
            }
         }
      }
   }
}
