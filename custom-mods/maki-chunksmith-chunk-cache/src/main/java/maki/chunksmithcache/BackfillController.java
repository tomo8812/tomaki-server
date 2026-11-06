package maki.chunksmithcache;
import net.minecraft.world.level.chunk.storage.IOWorker;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ChunkResult;
import java.lang.management.ThreadMXBean;
import java.lang.management.ManagementFactory;
import com.kishku7.chunksmith.mixin.ServerChunkCacheMixin;
import com.kishku7.chunksmith.mixin.IOWorkerAccessor;
import com.kishku7.chunksmith.mixin.ChunkStorageAccessor;
import com.kishku7.chunksmith.ducks.MinecraftServerExtension;
import com.kishku7.chunksmith.PlatformCompat;

import com.kishku7.chunksmith.lod.LodSupport;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Unit;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BackfillController {
   private static final ThreadMXBean THREAD_CPU = ManagementFactory.getThreadMXBean();
   private static final TicketType<Unit> BACKFILL_TICKET = TicketType.create("maki_chunk_cache_backfill", (var0, var1) -> 0);
   private static final Logger LOG = LoggerFactory.getLogger("MakiChunkCache");
   private static final Map<MinecraftServer, BackfillController.State> STATES = new WeakHashMap<>();
   private static volatile List<ServerLevel> LEVELS = List.of();
   private static final long RESCAN_MILLIS = 600000L;
   private static final long FULL_LOAD_WARN_MILLIS = 30000L;
   private static final long FULL_LOAD_TIMEOUT_MILLIS = 300000L;
   private static final long UNLOAD_WARN_MILLIS = 30000L;
   private static final int MAX_RETIRED_GUARDS = 8;
   private static final ExecutorService SCAN = Executors.newSingleThreadExecutor(var0 -> {
      Thread var1 = new Thread(var0, "maki-chunk-cache-scan");
      var1.setDaemon(true);
      return var1;
   });

   private BackfillController() {
   }

   public static List<ServerLevel> levels() {
      return LEVELS;
   }

   public static void tick(MinecraftServer server) {
      Fingerprint.kick(server);
      LEVELS = copyLevels(server);
      BackfillController.State state;
      synchronized (STATES) {
         state = STATES.computeIfAbsent(server, ignored -> new BackfillController.State());
      }

      maintainRetiredGuards(server,state);
      state.ticks++;
      if (state.busy) {
         if (state.activeLevel != null && state.activePos != null && playerNear(state.activeLevel, state.activePos)) {
            handoffToPlayer(state);
         } else if (state.busy) {
            state.playerOwnedDependenciesReleased += BackfillGuard.releasePlayerOwnedDependencies(state.activeLevel, state.activePos);
            if (state.awaitingScopeDrain && BackfillGuard.scopeDrained(state.activeLevel,state.activePos)) {
               BackfillGuard.clear(state.activeLevel,state.activePos);
               clearActive(state);
            }
            if (state.busy && ("waiting-owned-ticket".equals(state.phase) || "waiting-full-chunk".equals(state.phase) || "waiting-chunk-future".equals(state.phase) || "pre-full-timeout-awaiting-future".equals(state.phase))) {
               requestOwnedTicketChunk(state);
            }
            if (state.busy && state.ticketRemovedAt != 0L) {
               observeUnloadWait(server,state);
            }
         }
         report(server,state,false);
         return;
      }
      if (state.scanning) {
         report(server,state,false);
         return;
      }
      if (state.ticks % 20L != 0L) {
         report(server,state,false);
         return;
      }
      if (server.getPlayerCount() != 0) {
         releaseResourceBudget(state,"players-online");
         state.phase="waiting-players";
         report(server,state,false);
         return;
      }
      if (state.retiredGuards.size() >= MAX_RETIRED_GUARDS) {
         releaseResourceBudget(state,"retired-guard-cap");
         enterPhase(state,"waiting-retired-guard-cap");
         state.waitReason="retired-guard-cap";
         report(server,state,false);
         return;
      }
      if (!MillenaireBridge.canBackfillSafely()) {
         releaseResourceBudget(state,"millenaire-safety");
         state.phase="waiting-millenaire-safety";
         report(server,state,false);
         return;
      }
      if (!reserveResourceBudget(state)) {
         report(server,state,false);
         return;
      }

      ArrayList<BackfillController.ScanLevel> levels=new ArrayList<>();
      for (ServerLevel level : LEVELS) {
         String fingerprint=Fingerprint.forLevel(level);
         if (fingerprint != null) levels.add(new BackfillController.ScanLevel(level,fingerprint));
      }
      if (levels.isEmpty()) {
         releaseResourceBudget(state,"no-fingerprint");
         state.phase="waiting-fingerprint";
         report(server,state,false);
         return;
      }

      state.scanning=true;
      state.phase="scanning";
      state.normalStateUnknown=0L;
      state.scansStarted++;
      report(server,state,true);
      CompletableFuture.<BackfillController.Candidate>supplyAsync(() -> findCandidate(levels,state),SCAN)
         .whenComplete((candidate,error) -> server.execute(() -> {
            state.scanning=false;
            if (error != null) {
               releaseResourceBudget(state,"scan-failed");
               state.scanFailures++;
               state.phase="scan-failed";
               LOG.warn("Chunk-cache backfill candidate scan failed",error);
               report(server,state,true);
               return;
            }
            if (candidate == null) {
               releaseResourceBudget(state,"no-candidate");
               long now=System.currentTimeMillis();
               state.phase=state.normalStateUnknown != 0L
                  ? "waiting-normal-state-unknown"
                  : state.candidateDeferredUntil > now ? "waiting-dependency-scope-loaded" : "waiting-no-candidate";
               report(server,state,true);
               return;
            }
            state.candidatesFound++;
            if (state.busy) {
               releaseResourceBudget(state,"became-busy");
               state.phase="busy";
               report(server,state,true);
               return;
            }
            if (server.getPlayerCount() != 0) {
               releaseResourceBudget(state,"players-online");
               state.phase="waiting-players";
               report(server,state,true);
               return;
            }
            if (!MillenaireBridge.canBackfillSafely()) {
               releaseResourceBudget(state,"millenaire-safety");
               state.phase="waiting-millenaire-safety";
               report(server,state,true);
               return;
            }
            if (!reserveResourceBudget(state)) {
               report(server,state,true);
               return;
            }

            ServerLevel level=candidate.level();
            BackfillController.Target target=candidate.target();
            ChunkPos pos=target.pos();
            if (level.getChunkSource().hasChunk(pos.x,pos.z)) {
               releaseResourceBudget(state,"candidate-already-loaded");
               state.phase="candidate-already-loaded";
               state.candidateDeferredUntil=Math.max(state.candidateDeferredUntil,System.currentTimeMillis()+600000L);
               advanceCursor(level,state.cursor.get(CacheManager.dimensionKey(level)));
               report(server,state,true);
            } else if (!BackfillGuard.scopeAvailable(level,candidate.suppressed())) {
               releaseResourceBudget(state,"dependency-scope-loaded");
               state.phase="candidate-dependency-scope-loaded";
               state.candidateDeferredUntil=Math.max(state.candidateDeferredUntil,System.currentTimeMillis()+600000L);
               advanceCursor(level,state.cursor.get(CacheManager.dimensionKey(level)));
               report(server,state,true);
            } else {
               start(level,target,state,candidate.suppressed());
            }
         }));
   }

   private static boolean reserveResourceBudget(BackfillController.State state) {
      String decision=LodResourceBudgetBridge.reserve();
      state.resourceBudgetReserved=LodResourceBudgetBridge.reservationHeld();
      state.budgetReason=decision;
      if("granted".equals(decision)) {
         state.waitReason=null;
         return true;
      }
      state.waitReason="shared-budget:"+decision;
      enterPhase(state,"waiting-shared-budget-"+decision.replaceAll("[^a-z0-9-]","-"));
      return false;
   }

   private static void releaseResourceBudget(BackfillController.State state,String outcome) {
      if(state.resourceBudgetReserved||LodResourceBudgetBridge.reservationHeld()) {
         LodResourceBudgetBridge.release(outcome);
      }
      state.resourceBudgetReserved=false;
      state.budgetReason="released:"+outcome;
   }

   public static void onUnload(ServerLevel level, LevelChunk chunk) {
      synchronized (STATES) {
         State state=STATES.get(level.getServer());
         if(state==null)return;
         if(state.activeLevel==level && state.activePos!=null && state.activePos.equals(chunk.getPos())) {
            state.lastUnloadAt=System.currentTimeMillis();
            // Holder maps can still contain the chunk inside its unload callback.
            // The server tick clears the scope only after every holder has drained.
            if(!state.loadFuturePending && !state.backfillTicketHeld)state.awaitingScopeDrain=true;
            markProgress(state);
         }
      }
   }

   private static void start(ServerLevel var0, BackfillController.Target var1, BackfillController.State var2, Set<Long> suppressed) {
      ChunkPos var3 = var1.pos();
      if (MillenaireBridge.canBackfillSafely()) {
         var2.busy = true;
         var2.activeLevel = var0;
         var2.activePos = var3;
         var2.activeTarget = var1;
         var2.awaitingScopeDrain=false;
         var2.activeSince=System.currentTimeMillis();
         var2.phaseEnteredAt=var2.activeSince;
         var2.lastProgressAt=var2.activeSince;
         var2.ticketAddedAt=0L;
         var2.ticketRemovedAt=0L;
         var2.fullChunkAcquiredAt=0L;
         var2.loadFuturePending=false;
         var2.loadWarningLogged=false;
         var2.fullLoadTimeoutReached=false;
         var2.unloadDelayWarned=false;
         var2.backfillTicketHeld=false;
         var2.completionOutcome=null;
         var2.waitReason=null;
         enterPhase(var2,"loading-chunk");
         var2.backfillsStarted++;
         long var4 = ++var2.token;
         BackfillGuard.arm(var0, var3, suppressed);
         Boolean var6 = MillenaireBridge.suppressNaturalSpawns(true);
         var2.previousSuppression = var6;
         if (MillenaireBridge.isLoaded() && var6 == null) {
            LOG.warn("Millenaire suppression could not be acquired; skipping chunk-cache backfill to preserve natural-generation semantics", new Object[0]);
            BackfillGuard.clear(var0, var3);
            releaseResourceBudget(var2,"millenaire-suppression-failed");
            clearActive(var2);
         } else {
            var2.resourceBudgetReserved=LodResourceBudgetBridge.reservationHeld();
            LodResourceBudgetBridge.started();
            IdlePregenBridge.setBackfillActive(true);
            var2.idlePregenClaimed=IdlePregenBridge.isClaimed();
            LOG.debug("Backfilling cached chunk {} in {}", new Object[]{var3, var0.dimension().location()});
            var2.lastRequestQueuedAt=System.currentTimeMillis();
            var2.waitReason="ticket-safe-point";
            enterPhase(var2,"waiting-owned-ticket");
            report(var0.getServer(),var2,true);
         }
      }
   }

   private static void requestOwnedTicketChunk(BackfillController.State state) {
      ServerLevel level=state.activeLevel;
      BackfillController.Target target=state.activeTarget;
      ChunkPos pos=state.activePos;
      if(level==null||target==null||pos==null)return;

      long now=System.currentTimeMillis();
      long elapsed=now-state.activeSince;
      if(elapsed>=FULL_LOAD_TIMEOUT_MILLIS) {
         if(state.loadFuturePending) {
            if(!state.fullLoadTimeoutReached) {
               state.fullLoadTimeoutReached=true;
               state.waitReason="in-flight-full-generation-timeout";
               enterPhase(state,"pre-full-timeout-awaiting-future");
               releaseIdleClaim(state);
               LOG.warn("Chunk-cache backfill {} in {} has not reached FULL after {} ms; keeping its ticket and persistence guard until the in-flight FULL future settles, while releasing only the idle-work claim",
                  new Object[]{pos,level.dimension().location(),elapsed});
               report(level.getServer(),state,true);
            }
            return;
         }
         LOG.warn("Chunk-cache backfill {} in {} did not reach FULL within {} ms and has no in-flight generation future; aborting before FULL",
            new Object[]{pos,level.dimension().location(),elapsed});
         failBeforeFull(level,pos,state,state.previousSuppression,state.token);
         return;
      }
      if(elapsed>=FULL_LOAD_WARN_MILLIS&&!state.loadWarningLogged) {
         state.loadWarningLogged=true;
         state.waitReason="owned-ticket-full-generation-delayed";
         LOG.warn("Chunk-cache backfill {} in {} is still waiting for its owned ticket to produce a FULL chunk after {} ms",
            new Object[]{pos,level.dimension().location(),elapsed});
         report(level.getServer(),state,true);
      }
      if(state.loadFuturePending)return;

      long token=state.token;
      var future=requestChunkFuture(level,pos,state,token,System.nanoTime());
      state.loadFuturePending=true;
      state.waitReason="owned-ticket-full-generation";
      enterPhase(state,"waiting-full-chunk");
      future.whenComplete((result,error) -> level.getServer().execute(() -> {
         if(!active(state,token,level,pos))return;
         state.loadFuturePending=false;
         state.lastChunkFutureWaitWallMs=state.chunkFutureStartedAtNanos==0L?-1.0:elapsedMillis(state.chunkFutureStartedAtNanos);
         if(error!=null) {
            LOG.warn("Chunk-cache backfill {} failed while waiting for the owned ticket to reach FULL",pos,error);
            failBeforeFull(level,pos,state,state.previousSuppression,token);
            return;
         }
         LevelChunk chunk=levelChunk(result);
         if(chunk==null) {
            if(state.fullLoadTimeoutReached) {
               LOG.warn("Timed-out FULL future for {} in {} settled without a FULL chunk; performing guarded pre-FULL cleanup",
                  new Object[]{pos,level.dimension().location()});
               failBeforeFull(level,pos,state,state.previousSuppression,token);
               return;
            }
            state.waitReason=state.loadWarningLogged?"owned-ticket-full-generation-delayed":"owned-ticket-propagation";
            enterPhase(state,"waiting-owned-ticket");
            return;
         }
         if(state.fullLoadTimeoutReached) {
            state.waitReason="late-full-after-timeout";
            LOG.info("Timed-out FULL future for {} in {} eventually completed; finishing the guarded backfill before releasing its ticket",
               new Object[]{pos,level.dimension().location()});
         }
         handleFullChunk(level,target,state,state.previousSuppression,token,chunk);
      }));
   }

   private static CompletableFuture<ChunkResult<ChunkAccess>> requestChunkFuture(
      ServerLevel level,
      ChunkPos pos,
      BackfillController.State state,
      long token,
      long queuedAt
   ) {
      MinecraftServer server = level.getServer();
      CompletableFuture<ChunkResult<ChunkAccess>> requested = new CompletableFuture<>();

      try {
         ((MinecraftServerExtension)server).chunksmith$atTicketSafePoint(() -> {
            if (!active(state, token, level, pos)) {
               requested.cancel(false);
               return;
            }

            ServerChunkCache chunkSource = level.getChunkSource();
            long setupStartedAt = System.nanoTime();
            long cpuStartedAt = currentThreadCpuNanos();
            state.lastTicketSafePointDelayMs = elapsedMillis(queuedAt);
            state.lastLoadedBeforeRequest = chunkSource.hasChunk(pos.x, pos.z);
            state.lastPendingChunkTasksBefore = chunkSource.getPendingTasksCount();
            state.lastPendingChunkWritesBefore = pendingChunkWrites(level);

            try {
               if (!state.backfillTicketHeld) {
                  chunkSource.addRegionTicket(BACKFILL_TICKET, pos, 0, Unit.INSTANCE);
                  state.backfillTicketHeld = true;
                  state.ticketAddedAt=System.currentTimeMillis();
                  markProgress(state);
               }

               long distanceManagerStartedAt = System.nanoTime();
               if (!PlatformCompat.ENABLE_C2ME_TICKET_COMPAT) {
                  ((ServerChunkCacheMixin)chunkSource).invokeRunDistanceManagerUpdates();
               }
               state.lastDistanceManagerUpdateWallMs = elapsedMillis(distanceManagerStartedAt);

               boolean createMissingChunk = false; // BACKFILL_TICKET alone owns loading; never create UNKNOWN tickets.
               state.lastCreateMissingChunk = createMissingChunk;
               long futureCallStartedAt = System.nanoTime();
               state.chunkFutureStartedAtNanos = futureCallStartedAt;
               CompletableFuture<ChunkResult<ChunkAccess>> nativeFuture = ((ServerChunkCacheMixin)chunkSource)
                  .invokeGetChunkFutureMainThread(pos.x, pos.z, ChunkStatus.FULL, createMissingChunk);
               state.lastGetChunkFutureCallWallMs = elapsedMillis(futureCallStartedAt);
               state.lastRequestSetupWallMs = elapsedMillis(setupStartedAt);
               state.lastRequestSetupCpuMs = elapsedCpuMillis(cpuStartedAt);
               state.lastPendingChunkTasksAfter = chunkSource.getPendingTasksCount();
               state.lastPendingChunkWritesAfter = pendingChunkWrites(level);
               state.phase = "waiting-chunk-future";

               nativeFuture.whenComplete((result, error) -> {
                  ((MinecraftServerExtension)server).chunksmith$markChunkSystemHousekeeping();
                  if (error != null) {
                     requested.completeExceptionally(error);
                  } else {
                     requested.complete(result);
                  }
               });
            } catch (Throwable error) {
               state.lastRequestSetupWallMs = elapsedMillis(setupStartedAt);
               state.lastRequestSetupCpuMs = elapsedCpuMillis(cpuStartedAt);
               state.lastPendingChunkTasksAfter = chunkSource.getPendingTasksCount();
               state.lastPendingChunkWritesAfter = pendingChunkWrites(level);
               requested.completeExceptionally(error);
            }
         });
      } catch (Throwable error) {
         requested.completeExceptionally(error);
      }

      return requested;
   }

   private static long pendingChunkWrites(ServerLevel level) {
      try {
         IOWorker worker = ((ChunkStorageAccessor)level.getChunkSource().chunkMap).chunksmith$getWorker();
         if (worker == null) {
            return -1L;
         }

         Map<?, ?> pending = ((IOWorkerAccessor)worker).chunksmith$getPendingWrites();
         return pending == null ? -1L : pending.size();
      } catch (Throwable ignored) {
         return -1L;
      }
   }

   private static long currentThreadCpuNanos() {
      try {
         return THREAD_CPU.isCurrentThreadCpuTimeSupported() && THREAD_CPU.isThreadCpuTimeEnabled()
            ? THREAD_CPU.getCurrentThreadCpuTime()
            : -1L;
      } catch (UnsupportedOperationException | SecurityException ignored) {
         return -1L;
      }
   }

   private static double elapsedCpuMillis(long startedAt) {
      if (startedAt < 0L) {
         return -1.0;
      }

      long endedAt = currentThreadCpuNanos();
      return endedAt < startedAt ? -1.0 : (endedAt - startedAt) / 1_000_000.0;
   }

   private static double elapsedMillis(long startedAt) {
      return (System.nanoTime() - startedAt) / 1_000_000.0;
   }


   private static LevelChunk levelChunk(net.minecraft.server.level.ChunkResult result) {
      if(result==null)return null;
      LevelChunk[] chunk=new LevelChunk[1];
      ChunkResultBridge.ifSuccess(result,value -> {
         if(value instanceof LevelChunk levelChunk)chunk[0]=levelChunk;
      });
      return chunk[0];
   }

   private static void handleFullChunk(ServerLevel level,BackfillController.Target target,BackfillController.State state,Boolean previousSuppression,long token,LevelChunk chunk) {
      ChunkPos pos=target.pos();
      if(!active(state,token,level,pos))return;
      state.fullChunkAcquiredAt=System.currentTimeMillis();
      state.loadFuturePending=false;
      state.waitReason=null;
      markProgress(state);

      try {
         enterPhase(state,"submitting-lod");
         LodSupport.offer(level,chunk);
         state.lodOffersSubmitted++;
         markProgress(state);
         enterPhase(state,"waiting-lod-durable");
         report(level.getServer(),state,true);
      } catch(Throwable error) {
         LOG.warn("Could not refresh LOD for backfill {}; leaving it pending for retry",new Object[]{pos,error});
         fail(level,pos,state,previousSuppression,token);
         return;
      }

      if(!MillenaireBridge.rememberPending(level,pos)) {
         LOG.warn("Could not preserve Millenaire pending state for backfill {}; leaving it pending for retry",new Object[]{pos});
         fail(level,pos,state,previousSuppression,token);
         return;
      }

      MillenaireBridge.restore(previousSuppression);
      state.previousSuppression=null;
      CompletableFuture.<Boolean>supplyAsync(() -> waitForLodRefresh(target),SCAN)
         .whenComplete((durable,error) -> level.getServer().execute(() -> {
            if(!active(state,token,level,pos))return;
            if(error!=null||!Boolean.TRUE.equals(durable)) {
               LOG.warn("Backfill LOD refresh was not durably observed for {}; leaving it pending for retry",new Object[]{pos,error});
               fail(level,pos,state,previousSuppression,token);
               return;
            }

            state.lodRefreshCompleted++;
            markProgress(state);
            enterPhase(state,"writing-cache");
            report(level.getServer(),state,true);
            CacheManager.capture(level,chunk,true).whenComplete((captured,captureError) -> level.getServer().execute(() -> {
               if(!active(state,token,level,pos))return;
               if(!Boolean.TRUE.equals(captured)) {
                  state.backfillsFailed++;
                  state.completionOutcome="cache-write-failed";
                  LOG.warn("Backfill cache write failed for {}; releasing the owned ticket without treating it as complete",new Object[]{pos,captureError});
               } else {
                  state.backfillsCompleted++;
                  state.completionOutcome="completed";
                  advanceCursor(level,state.cursor.get(CacheManager.dimensionKey(level)));
               }
               markProgress(state);
               enterPhase(state,state.completionOutcome+"-ticket-releasing");
               releaseOwnedTicket(level,pos,state);
               releaseResourceBudget(state,state.completionOutcome);
               if(active(state,token,level,pos)) {
                  if("completed".equals(state.completionOutcome)) {
                     retireSuccessfulGuard(level,pos,state);
                  } else {
                     state.awaitingScopeDrain=true;
                     state.waitReason="natural-unload";
                     enterPhase(state,state.completionOutcome+"-ticket-released-awaiting-unload");
                     report(level.getServer(),state,true);
                  }
               }
            }));
         }));
   }

   private static void retireSuccessfulGuard(ServerLevel level,ChunkPos pos,BackfillController.State state) {
      long now=System.currentTimeMillis();
      BackfillController.RetiredGuard retired=new BackfillController.RetiredGuard(
         level,pos,"completed",now,state.ticketRemovedAt);
      state.retiredGuards.put(retiredKey(level,pos),retired);
      state.retiredGuardsCreated++;
      state.lastOutcome="completed";
      markProgress(state);
      LOG.debug("Detached completed backfill guard {} in {} from the active pipeline; {} retired guard(s) remain",
         new Object[]{pos,level.dimension().location(),state.retiredGuards.size()});
      clearActive(state);
   }

   private static void maintainRetiredGuards(MinecraftServer server,BackfillController.State state) {
      if(state.retiredGuards.isEmpty())return;
      long now=System.currentTimeMillis();
      boolean changed=false;
      for(BackfillController.RetiredGuard retired : new ArrayList<>(state.retiredGuards.values())) {
         if(!BackfillGuard.active(retired.level,retired.pos)) {
            state.retiredGuards.remove(retiredKey(retired.level,retired.pos));
            state.retiredGuardsReleasedExternally++;
            state.lastOutcome=retired.outcome;
            markProgress(state);
            changed=true;
            continue;
         }
         state.playerOwnedDependenciesReleased += BackfillGuard.releasePlayerOwnedDependencies(retired.level,retired.pos);
         if(playerNear(retired.level,retired.pos)) BackfillGuard.releasePlayerTarget(retired.level,retired.pos);
         if(BackfillGuard.scopeDrained(retired.level,retired.pos)) {
            BackfillGuard.clear(retired.level,retired.pos);
            state.retiredGuards.remove(retiredKey(retired.level,retired.pos));
            state.retiredGuardsUnloaded++;
            state.lastOutcome=retired.outcome;
            markProgress(state);
            LOG.debug("Released drained backfill dependency scope {} in {}",
               new Object[]{retired.pos,retired.level.dimension().location()});
            changed=true;
            continue;
         }
         long elapsed=now-retired.ticketRemovedAt;
         if(elapsed>=UNLOAD_WARN_MILLIS&&!retired.warned) {
            retired.warned=true;
            state.retiredGuardWarnings++;
            LOG.warn("Retired backfill guard {} in {} has remained loaded {} ms after its owned ticket and idle claim were released; it no longer blocks later backfills and remains guarded against normal-world persistence",
               new Object[]{retired.pos,retired.level.dimension().location(),elapsed});
            changed=true;
         }
      }
      if(changed)report(server,state,true);
   }

   private static String retiredKey(ServerLevel level,ChunkPos pos) {
      return level.dimension().location().toString()+"@"+pos.toLong();
   }

   private static void releaseOwnedTicket(ServerLevel level,ChunkPos pos,BackfillController.State state) {
      if(state.backfillTicketHeld) {
         state.backfillTicketHeld=false;
         state.ticketRemovedAt=System.currentTimeMillis();
         state.lastTicketRemovedAt=state.ticketRemovedAt;
         markProgress(state);
         level.getChunkSource().removeRegionTicket(BACKFILL_TICKET,pos,0,Unit.INSTANCE);
      }
      releaseIdleClaim(state);
   }

   private static void releaseIdleClaim(BackfillController.State state) {
      IdlePregenBridge.setBackfillActive(false);
      state.idlePregenClaimed=false;
   }

   private static void observeUnloadWait(MinecraftServer server,BackfillController.State state) {
      if(state.ticketRemovedAt==0L||state.activeLevel==null||state.activePos==null)return;
      long elapsed=System.currentTimeMillis()-state.ticketRemovedAt;
      if(elapsed<UNLOAD_WARN_MILLIS||state.unloadDelayWarned)return;

      state.unloadDelayWarned=true;
      boolean frozen=server.tickRateManager().isFrozen();
      state.waitReason=frozen
         ?"owned-ticket-released; server-frozen-by-other-owner-or-admin, or chunk held elsewhere"
         :"owned-ticket-released; chunk held elsewhere or pending natural unload";
      enterPhase(state,(state.completionOutcome==null?"backfill":state.completionOutcome)+"-unload-delayed");
      LOG.warn("Chunk-cache backfill {} in {} has waited {} ms for natural unload after its own ticket and idle claim were released; frozen={}; guard remains active to prevent normal-world persistence",
         new Object[]{state.activePos,state.activeLevel.dimension().location(),elapsed,frozen});
      report(server,state,true);
   }

   private static void enterPhase(BackfillController.State state,String phase) {
      if(!phase.equals(state.phase)) {
         state.phase=phase;
         state.phaseEnteredAt=System.currentTimeMillis();
      }
   }

   private static void markProgress(BackfillController.State state) {
      state.lastProgressAt=System.currentTimeMillis();
   }

   private static void failBeforeFull(ServerLevel var0, ChunkPos var1, BackfillController.State var2, Boolean var3, long var4) {
      if (active(var2, var4, var0, var1)) {
         MillenaireBridge.restore(var3);
         var2.previousSuppression = null;
         var2.awaitingScopeDrain=true;
         var2.backfillsFailed++;
         var2.completionOutcome="failed-before-full";
         releaseOwnedTicket(var0,var1,var2);
         releaseResourceBudget(var2,var2.completionOutcome);
         var2.lastOutcome="failed-before-full";
         markProgress(var2);
         enterPhase(var2,"failed-before-full");
         report(var0.getServer(),var2,true);
         LOG.warn("Chunk-cache backfill for {} failed before reaching FULL; ticket and idle claim are released while the dependency save guard drains",new Object[]{var1});
         if (BackfillGuard.scopeDrained(var0,var1)) {
            BackfillGuard.clear(var0,var1);
            clearActive(var2);
         }
      }
   }

   private static void fail(ServerLevel var0, ChunkPos var1, BackfillController.State var2, Boolean var3, long var4) {
      if (active(var2, var4, var0, var1)) {
         MillenaireBridge.restore(var3);
         var2.previousSuppression = null;
         var2.backfillsFailed++;
         var2.completionOutcome="failed";
         var2.awaitingScopeDrain=true;
         markProgress(var2);
         enterPhase(var2,"failed-ticket-releasing");
         releaseOwnedTicket(var0,var1,var2);
         releaseResourceBudget(var2,var2.completionOutcome);
         if(!var2.busy||var2.activeLevel!=var0||var2.activePos==null||var2.activePos.toLong()!=var1.toLong())return;
         var2.token++;
         var2.waitReason="natural-unload";
         enterPhase(var2,"failed-ticket-released-awaiting-unload");
         report(var0.getServer(),var2,true);
         LOG.warn("Chunk-cache backfill for {} failed after reaching FULL; its ticket and idle claim were released while the guard waits for natural unload or player ownership", new Object[]{var1});
      }
   }

   private static void releaseIdlePregenClaim(BackfillController.State state) {
      if (state.idlePregenClaimed) {
         IdlePregenBridge.setBackfillActive(false);
         state.idlePregenClaimed=false;
      }
   }

   private static boolean active(BackfillController.State var0, long var1, ServerLevel var3, ChunkPos var4) {
      return var0.busy && var0.token == var1 && var0.activeLevel == var3 && var0.activePos != null && var0.activePos.toLong() == var4.toLong();
   }

   private static boolean playerNear(ServerLevel var0, ChunkPos var1) {
      int var2 = var0.getServer().getPlayerList().getViewDistance() + 1;

      for (ServerPlayer var4 : var0.players()) {
         ChunkPos var5 = var4.chunkPosition();
         if (Math.abs(var5.x - var1.x) <= var2 && Math.abs(var5.z - var1.z) <= var2) {
            return true;
         }
      }

      return false;
   }

   private static void handoffToPlayer(BackfillController.State var0) {
      ServerLevel var1 = var0.activeLevel;
      ChunkPos var2 = var0.activePos;
      if (var1 != null && var2 != null) {
         MillenaireBridge.restore(var0.previousSuppression);
         var0.previousSuppression = null;
         if (!MillenaireBridge.rememberPending(var1, var2)) {
            LOG.warn("Could not pre-mark Millenaire pending state while handing backfill {} to a player", new Object[]{var2});
         }

         releaseOwnedTicket(var1,var2,var0);
         releaseResourceBudget(var0,"handed-to-player");
         BackfillGuard.releasePlayerTarget(var1,var2);
         BackfillGuard.releasePlayerOwnedDependencies(var1,var2);
         if(!BackfillGuard.scopeDrained(var1,var2)) {
            var0.retiredGuards.put(retiredKey(var1,var2),new RetiredGuard(var1,var2,"handed-to-player",System.currentTimeMillis(),System.currentTimeMillis()));
         } else BackfillGuard.clear(var1,var2);
         var0.backfillsHandedOff++;
         var0.lastOutcome="handed-to-player";
         enterPhase(var0,"handed-to-player");
         LOG.info("Handed chunk-cache backfill {} to normal player loading", new Object[]{var2});
         clearActive(var0);
      }
   }

   private static void clearActive(BackfillController.State var0) {
      MinecraftServer server=var0.activeLevel==null?null:var0.activeLevel.getServer();
      if(var0.resourceBudgetReserved)releaseResourceBudget(var0,var0.completionOutcome==null?"cleared":var0.completionOutcome);
      IdlePregenBridge.setBackfillActive(false);
      var0.idlePregenClaimed=false;
      var0.busy=false;
      var0.activeLevel=null;
      var0.activePos=null;
      var0.activeTarget=null;
      var0.previousSuppression=null;
      var0.awaitingScopeDrain=false;
      var0.activeSince=0L;
      var0.phaseEnteredAt=System.currentTimeMillis();
      var0.ticketAddedAt=0L;
      var0.ticketRemovedAt=0L;
      var0.fullChunkAcquiredAt=0L;
      var0.loadFuturePending=false;
      var0.backfillTicketHeld=false;
      var0.loadWarningLogged=false;
      var0.fullLoadTimeoutReached=false;
      var0.unloadDelayWarned=false;
      var0.waitReason=null;
      var0.completionOutcome=null;
      var0.phase="idle";
      var0.token++;
      if(server!=null)report(server,var0,true);
   }

   private static void report(MinecraftServer server,BackfillController.State state,boolean force) {
      long now=System.currentTimeMillis();
      if(!force&&now-state.lastReport<30_000L)return;
      state.lastReport=now;
      String dimension=state.activeLevel==null?"none":state.activeLevel.dimension().location().toString();
      String chunk=state.activePos==null?"none":state.activePos.x+","+state.activePos.z;
      LOG.info("Chunk-cache backfill state: phase={}, busy={}, scanning={}, dimension={}, chunk={}, waitReason={}, outcome={}, budgetReason={}, budgetReserved={}, ownTicket={}, futurePending={}, dependencyDrain={}, retiredGuards={}, started={}, completed={}, failed={}, handedOff={}, sharedBudget=[{}]",
         state.phase,state.busy,state.scanning,dimension,chunk,state.waitReason,state.completionOutcome,state.budgetReason,state.resourceBudgetReserved,state.backfillTicketHeld,state.loadFuturePending,state.awaitingScopeDrain,state.retiredGuards.size(),state.backfillsStarted,state.backfillsCompleted,state.backfillsFailed,state.backfillsHandedOff,LodResourceBudgetBridge.status());
   }

   private static Double metric(double value) {
      return value < 0.0 || !Double.isFinite(value) ? null : Math.round(value * 1000.0) / 1000.0;
   }

   private static String instant(long millis) {
      return millis==0L?null:java.time.Instant.ofEpochMilli(millis).toString();
   }

   private static BackfillController.Target nextCandidate(ServerLevel var0, BackfillController.Cursor var1, String var2, BackfillController.State state) {
      Path var3 = CacheManager.lodRoot(var0);
      if (!Files.isDirectory(var3)) {
         return null;
      }

      ArrayList<Path> var4 = new ArrayList<>();

      try (Stream<Path> var5 = Files.list(var3)) {
         var5.filter(var0x -> var0x.getFileName().toString().startsWith("r.") && var0x.getFileName().toString().endsWith(".cslod"))
            .sorted(Comparator.comparing(var0x -> var0x.getFileName().toString()))
            .forEach(var4::add);
      } catch (IOException var18) {
         return null;
      }

      loadCursor(var0, var1, var2);
      if (var1.region >= var4.size()) {
         long var19 = System.currentTimeMillis();
         if (var1.exhaustedAt == 0L) {
            var1.exhaustedAt = var19;
         }

         if (var19 - var1.exhaustedAt < 600000L) {
            return null;
         }

         var1.region = 0;
         var1.slot = 0;
         var1.exhaustedAt = 0L;
         saveCursor(var0, var1, var2);
      }

      for (int var20 = var1.region; var20 < var4.size(); var20++) {
         int var6 = var20 == var1.region ? var1.slot : 0;
         int[] var7 = parseRegion(((Path)var4.get(var20)).getFileName().toString());
         if (var7 != null) {
            byte[] var8 = readHeader((Path)var4.get(var20));
            if (var8 != null) {
               ByteBuffer var9 = ByteBuffer.wrap(var8).order(ByteOrder.BIG_ENDIAN);

               for (int var10 = var6; var10 < 1024; var10++) {
                  int var11 = var9.getInt(var10 * 8);
                  int var12 = var9.getInt(var10 * 8 + 4);
                  var1.region = var20;
                  var1.slot = var10;
                  if (var11 > 0 && var12 > 0) {
                     int var13 = var7[0] * 32 + var10 % 32;
                     int var14 = var7[1] * 32 + var10 / 32;
                     ChunkPos var15 = new ChunkPos(var13, var14);
                     NormalChunkStorage.State normal = NormalChunkStorage.inspect(var0, var15);
                     if (normal != NormalChunkStorage.State.FULL && !CacheManager.cacheExists(var0, var15, var2)) {
                        if (normal == NormalChunkStorage.State.UNKNOWN) {
                           state.normalStateUnknown++;
                        } else {
                           var1.exhaustedAt = 0L;
                           return new BackfillController.Target(var15, (Path)var4.get(var20), var10, var11, var12);
                        }
                     }
                  }
               }

               var1.slot = 0;
               var1.region = var20 + 1;
               saveCursor(var0, var1, var2);
            }
         }
      }

      if (var1.exhaustedAt == 0L) {
         var1.exhaustedAt = System.currentTimeMillis();
      }

      return null;
   }

   private static BackfillController.Candidate findCandidate(List<BackfillController.ScanLevel> var0, BackfillController.State var1) {
      for (BackfillController.ScanLevel var3 : var0) {
         ServerLevel var4 = var3.level();
         BackfillController.Cursor var5;
         synchronized (var1.cursor) {
            var5 = var1.cursor.computeIfAbsent(CacheManager.dimensionKey(var4), var0x -> new BackfillController.Cursor());
         }

         BackfillController.Target var9;
         while ((var9 = nextCandidate(var4, var5, var3.fingerprint(), var1)) != null) {
            if (CacheManager.cacheExists(var4, var9.pos(), var3.fingerprint())) {
               advanceCursor(var4, var5);
               continue;
            }

            NormalChunkStorage.Scope scope = NormalChunkStorage.snapshotBackfillOwnership(var4, var9.pos());
            if (scope.unknown() != 0) {
               var1.normalStateUnknown += scope.unknown();
               advanceCursor(var4, var5);
               continue;
            }
            if (!scope.suppressed().contains(var9.pos().toLong())) {
               advanceCursor(var4, var5);
               continue;
            }

            return new BackfillController.Candidate(var4, var9, scope.suppressed());
         }
      }

      return null;
   }

   private static boolean waitForLodRefresh(BackfillController.Target var0) {
      long var1 = System.nanoTime() + 10000000000L;

      while (System.nanoTime() < var1) {
         int[] var3 = readSlot(var0.region(), var0.slot());
         if (var3 != null && var3[0] > 0 && var3[1] > 0 && (var3[0] != var0.offset() || var3[1] != var0.length())) {
            return true;
         }

         try {
            Thread.sleep(25L);
         } catch (InterruptedException var5) {
            Thread.currentThread().interrupt();
            return false;
         }
      }

      return false;
   }

   private static int[] readSlot(Path var0, int var1) {
      try (RandomAccessFile var2 = new RandomAccessFile(var0.toFile(), "r")) {
         long var3 = var1 * 8L;
         if (var2.length() < var3 + 8L) {
            return null;
         }

         var2.seek(var3);
         return new int[]{var2.readInt(), var2.readInt()};
      } catch (IOException var8) {
         return null;
      }
   }

   private static byte[] readHeader(Path var0) {
      try (RandomAccessFile var1 = new RandomAccessFile(var0.toFile(), "r")) {
         int var2 = (int)Math.min(8192L, var1.length());
         if (var2 < 8) {
            return null;
         }

         byte[] var3 = new byte[8192];
         var1.readFully(var3, 0, var2);
         return var3;
      } catch (IOException var7) {
         return null;
      }
   }

   private static int[] parseRegion(String var0) {
      try {
         String[] var1 = var0.substring(2, var0.length() - 6).split("\\.");
         return var1.length == 2 ? new int[]{Integer.parseInt(var1[0]), Integer.parseInt(var1[1])} : null;
      } catch (RuntimeException var2) {
         return null;
      }
   }

   private static void advanceCursor(ServerLevel var0, BackfillController.Cursor var1) {
      if (var1 != null) {
         var1.slot++;
         if (var1.slot >= 1024) {
            var1.slot = 0;
            var1.region++;
         }

         String var2 = Fingerprint.forLevel(var0);
         if (var2 != null) {
            SCAN.execute(() -> saveCursor(var0, var1, var2));
         }
      }
   }

   private static Path cursorPath(ServerLevel var0) {
      return var0.getServer()
         .getWorldPath(LevelResource.ROOT)
         .resolve("chunksmith_chunk_cache")
         .resolve("backfill-" + CacheManager.dimensionKey(var0) + ".cursor");
   }

   private static void loadCursor(ServerLevel var0, BackfillController.Cursor var1, String var2) {
      if (!var1.loaded) {
         var1.loaded = true;
         Path var3 = cursorPath(var0);

         try {
            List var4 = Files.readAllLines(var3);
            if (var4.size() >= 3 && var2.equals(var4.get(0))) {
               var1.region = Integer.parseInt((String)var4.get(1));
               var1.slot = Integer.parseInt((String)var4.get(2));
            }
         } catch (Exception var5) {
         }
      }
   }

   private static void saveCursor(ServerLevel var0, BackfillController.Cursor var1, String var2) {
      Path var3 = cursorPath(var0);

      try {
         Files.createDirectories(var3.getParent());
         Path var4 = var3.resolveSibling(var3.getFileName().toString() + ".tmp");
         Files.writeString(var4, var2 + "\n" + var1.region + "\n" + var1.slot + "\n", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

         try {
            Files.move(var4, var3, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
         } catch (AtomicMoveNotSupportedException var6) {
            Files.move(var4, var3, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (IOException var7) {
      }
   }

   private static List<ServerLevel> copyLevels(MinecraftServer var0) {
      ArrayList var1 = new ArrayList();

      for (ServerLevel var3 : var0.getAllLevels()) {
         var1.add(var3);
      }

      return List.copyOf(var1);
   }

   private record Candidate(ServerLevel level, BackfillController.Target target, Set<Long> suppressed) {
   }

   private static final class Cursor {
      boolean loaded;
      int region;
      int slot;
      long exhaustedAt;
   }

   private record ScanLevel(ServerLevel level, String fingerprint) {
   }

   private static final class State {
      long ticks;
      boolean busy;
      boolean scanning;
      long token;
      long lastReport;
      long activeSince;
      long phaseEnteredAt;
      long lastProgressAt;
      long ticketAddedAt;
      long ticketRemovedAt;
      long fullChunkAcquiredAt;
      long lastTicketRemovedAt;
      long lastUnloadAt;
      long scansStarted;
      long scanFailures;
      long normalStateUnknown;
      long candidateDeferredUntil;
      long candidatesFound;
      long backfillsStarted;
      long backfillsCompleted;
      long backfillsFailed;
      long backfillsHandedOff;
      long playerOwnedDependenciesReleased;
      long lodOffersSubmitted;
      long lodRefreshCompleted;
      long retiredGuardsCreated;
      long retiredGuardsUnloaded;
      long retiredGuardsHandedOff;
      long retiredGuardsReleasedExternally;
      long retiredGuardWarnings;
      boolean idlePregenClaimed;
      boolean resourceBudgetReserved;
      boolean awaitingScopeDrain;
      long lastRequestQueuedAt;
      double lastTicketSafePointDelayMs = -1.0;
      double lastDistanceManagerUpdateWallMs = -1.0;
      double lastGetChunkFutureCallWallMs = -1.0;
      double lastRequestSetupWallMs = -1.0;
      double lastRequestSetupCpuMs = -1.0;
      double lastChunkFutureWaitWallMs = -1.0;
      long chunkFutureStartedAtNanos;
      int lastPendingChunkTasksBefore = -1;
      int lastPendingChunkTasksAfter = -1;
      long lastPendingChunkWritesBefore = -1L;
      long lastPendingChunkWritesAfter = -1L;
      Boolean lastLoadedBeforeRequest;
      Boolean lastCreateMissingChunk;
      boolean backfillTicketHeld;
      boolean loadFuturePending;
      boolean loadWarningLogged;
      boolean fullLoadTimeoutReached;
      boolean unloadDelayWarned;
      String phase="idle";
      String budgetReason="not-requested";
      String waitReason;
      String completionOutcome;
      String lastOutcome;
      ServerLevel activeLevel;
      ChunkPos activePos;
      BackfillController.Target activeTarget;
      Boolean previousSuppression;
      final Map<String, BackfillController.Cursor> cursor = new HashMap<>();
      final Map<String, BackfillController.RetiredGuard> retiredGuards = new LinkedHashMap<>();
   }

   private static final class RetiredGuard {
      final ServerLevel level;
      final ChunkPos pos;
      final String outcome;
      final long retiredAt;
      final long ticketRemovedAt;
      boolean warned;

      RetiredGuard(ServerLevel level,ChunkPos pos,String outcome,long retiredAt,long ticketRemovedAt) {
         this.level=level;
         this.pos=pos;
         this.outcome=outcome;
         this.retiredAt=retiredAt;
         this.ticketRemovedAt=ticketRemovedAt;
      }
   }

   private record Target(ChunkPos pos, Path region, int slot, int offset, int length) {
   }
}
