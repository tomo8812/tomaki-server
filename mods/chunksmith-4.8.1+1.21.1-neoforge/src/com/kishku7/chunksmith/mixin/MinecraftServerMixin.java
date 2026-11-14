package com.kishku7.chunksmith.mixin;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.PlatformCompat;
import com.kishku7.chunksmith.command.CommandArguments;
import com.kishku7.chunksmith.ducks.MinecraftServerExtension;
import com.kishku7.chunksmith.util.AutoPause;
import com.kishku7.chunksmith.util.ChunkResidency;
import com.kishku7.chunksmith.util.ChunkSettleSupport;
import com.kishku7.chunksmith.util.HeapPressure;
import com.kishku7.chunksmith.util.StructureFaultReporter;
import com.kishku7.chunksmith.util.UnloadDiagnostics;
import com.kishku7.chunksmith.util.WorldgenOverreachReporter;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerExtension {
   @Unique
   private final AtomicBoolean chunksmith$needChunkSystemHousekeeping = new AtomicBoolean(false);
   @Unique
   private static final long CHUNKSMITH$MIN_UNLOAD_BUDGET_NANOS = 2000000L;
   @Unique
   private static final long CHUNKSMITH$IDLE_UNLOAD_BUDGET_NANOS = 10000000L;
   @Unique
   private final Queue<Runnable> chunksmith$ticketSafePointQueue = new ConcurrentLinkedQueue<>();
   @Unique
   private volatile Thread chunksmith$ticketSafePointThread;
   @Unique
   private volatile double chunksmith$mspt = 50.0;
   @Unique
   private int chunksmith$lastPlayerCount = -1;
   @Unique
   private long chunksmith$lastTickNanos = 0L;

   @Shadow
   public abstract Iterable<ServerLevel> getAllLevels();

   @Shadow
   public abstract int getPlayerCount();

   @Inject(method = "tickServer", at = @At("HEAD"))
   private void chunksmith$onTickHead(BooleanSupplier booleanSupplier, CallbackInfo ci) {
      this.chunksmith$drainTicketSafePoint();
      this.chunksmith$keepAwakeWhileGenerating();
      boolean wgRunning = ChunksmithProvider.isLoaded() && !ChunksmithProvider.get().getGenerationTasks().isEmpty();
      this.chunksmith$reportChunkResidency();
      int players = this.getPlayerCount();
      ChunkResidency.noteDrainBudget(players == 0);
      if (players == 0 && this.chunksmith$lastPlayerCount > 0) {
         ChunkResidency.reconsiderDrain();
      }

      this.chunksmith$lastPlayerCount = players;
      if (ChunkResidency.isDraining() || ChunkResidency.isGenerationHeld()) {
         this.chunksmith$markChunkSystemHousekeeping();
      }

      ChunkSettleSupport.tick(this.chunksmith$gameTimeForSettle());
      this.chunksmith$tickAutoResume(wgRunning);
      WorldgenOverreachReporter.get().tick(wgRunning);
      StructureFaultReporter.get().tick(wgRunning);
   }

   @Inject(method = "tickServer", at = @At("TAIL"))
   private void chunksmith$onHousekeepingHook(BooleanSupplier booleanSupplier, CallbackInfo ci) {
      this.chunksmith$runChunkSystemHousekeeping(booleanSupplier);
   }

   @Unique
   private void chunksmith$keepAwakeWhileGenerating() {
      if (ChunksmithProvider.isLoaded() && !ChunksmithProvider.get().getGenerationTasks().isEmpty()) {
         this.chunksmith$markChunkSystemHousekeeping();
      }

      long now = System.nanoTime();
      long prev = this.chunksmith$lastTickNanos;
      this.chunksmith$lastTickNanos = now;
      if (prev != 0L) {
         double dtMs = (now - prev) / 1000000.0;
         if (dtMs > 0.0 && dtMs < 10000.0) {
            this.chunksmith$mspt = this.chunksmith$mspt * 0.8 + dtMs * 0.2;
         }
      }
   }

   @Unique
   private long chunksmith$gameTimeForSettle() {
      Iterator var1 = this.getAllLevels().iterator();
      if (var1.hasNext()) {
         ServerLevel level = (ServerLevel)var1.next();
         return level.getGameTime();
      } else {
         return 0L;
      }
   }

   @Unique
   private void chunksmith$reportChunkResidency() {
      long loaded = 0L;
      long toDrop = 0L;
      long unloadQueue = 0L;
      long pendingUnloads = 0L;
      boolean hasTickets = false;

      for (ServerLevel level : this.getAllLevels()) {
         loaded += level.getChunkSource().getLoadedChunksCount();
         ChunkMapMixin chunkMap = (ChunkMapMixin)level.getChunkSource().chunkMap;
         toDrop += chunkMap.getToDrop().size();
         unloadQueue += chunkMap.getUnloadQueue().size();
         pendingUnloads += chunkMap.getPendingUnloads().size();
         hasTickets |= level.getChunkSource().chunkMap.getDistanceManager().hasTickets();
      }

      ChunkResidency.report(loaded);
      UnloadDiagnostics.report(loaded, toDrop, unloadQueue, pendingUnloads, hasTickets);
   }

   @Unique
   private void chunksmith$tickAutoResume(boolean generationRunning) {
      if (AutoPause.isAutoPaused() && !generationRunning) {
         long now = System.currentTimeMillis();
         double heap = HeapPressure.liveEstimatePercent();
         boolean healthy = AutoPause.healthyNow(this.chunksmith$mspt, heap);
         AutoPause.noteHealthy(healthy, now);
         if (AutoPause.shouldResume(now)) {
            String world = AutoPause.pausedWorld();
            AutoPause.logAutoResumed(world, AutoPause.resumeGraceMillis() / 1000L);
            AutoPause.clearAutoPaused();
            if (ChunksmithProvider.isLoaded()) {
               Chunksmith chunky = ChunksmithProvider.get();
               chunky.getServer().getConsole().sendMessagePrefixed("task_auto_resumed", AutoPause.graceMillis() / 1000L, world);
               chunky.getCommands().get("continue").execute(chunky.getServer().getConsole(), CommandArguments.empty());
            }
         }
      }
   }

   @Override
   public double chunksmith$getMillisPerTick() {
      return this.chunksmith$mspt;
   }

   @Override
   public void chunksmith$atTicketSafePoint(Runnable task) {
      this.chunksmith$ticketSafePointQueue.add(task);
      this.chunksmith$markChunkSystemHousekeeping();
   }

   @Override
   public void chunksmith$drainTicketSafePointNow() {
      this.chunksmith$drainTicketSafePoint();
   }

   @Override
   public boolean chunksmith$onTicketSafePoint() {
      return Thread.currentThread() == this.chunksmith$ticketSafePointThread;
   }

   @Override
   public void chunksmith$runChunkSystemHousekeeping(BooleanSupplier haveTime) {
      if (this.chunksmith$needChunkSystemHousekeeping.compareAndSet(true, false)) {
         long floor = this.getPlayerCount() != 0 || !ChunkResidency.isDraining() && !ChunkResidency.isGenerationHeld() ? 2000000L : 10000000L;
         long deadline = System.nanoTime() + floor;
         BooleanSupplier budget = () -> haveTime.getAsBoolean() || System.nanoTime() < deadline;

         for (ServerLevel level : this.getAllLevels()) {
            ((ServerChunkCacheMixin)level.getChunkSource()).invokeRunDistanceManagerUpdates();
            ((ChunkMapMixin)level.getChunkSource().chunkMap).invokeTick(budget);
            if (!PlatformCompat.ENABLE_MOONRISE_WORKAROUNDS) {
               ((ServerLevelMixin)level).getEntityManager().tick();
            }
         }
      }
   }

   @Unique
   private void chunksmith$drainTicketSafePoint() {
      int budget = this.chunksmith$ticketSafePointQueue.size();
      if (budget > 0) {
         this.chunksmith$ticketSafePointThread = Thread.currentThread();

         Runnable task;
         try {
            while (budget-- > 0 && (task = this.chunksmith$ticketSafePointQueue.poll()) != null) {
               task.run();
            }
         } finally {
            this.chunksmith$ticketSafePointThread = null;
         }
      }
   }

   @Override
   public void chunksmith$markChunkSystemHousekeeping() {
      this.chunksmith$needChunkSystemHousekeeping.set(true);
   }
}
