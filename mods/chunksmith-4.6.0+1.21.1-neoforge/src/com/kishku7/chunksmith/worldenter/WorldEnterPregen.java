package com.kishku7.chunksmith.worldenter;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.api.ChunksmithAPI;
import com.kishku7.chunksmith.lod.LodHorizon;
import com.kishku7.chunksmith.lod.LodPresence;
import com.kishku7.chunksmith.platform.Config;
import com.kishku7.chunksmith.platform.World;
import com.kishku7.chunksmith.platform.util.Location;
import com.kishku7.chunksmith.util.WorldEnterFreeze;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WorldEnterPregen {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   private static final String PATTERN = "concentric";
   private static final String SHAPE = "circle";
   private static final AtomicBoolean ACTIVE = new AtomicBoolean(false);
   private static final PregenEta ETA = new PregenEta();
   private static volatile MinecraftServer server;
   private static volatile String worldKey;
   private static volatile long chunksTotal;
   private static volatile boolean frozenByUs;
   private static volatile long chunksDone;
   private static volatile float percentComplete;
   private static volatile boolean selectionCovered;
   private static volatile long radiusBlocks;
   private static volatile double centerBlocksX;
   private static volatile double centerBlocksZ;

   private WorldEnterPregen() {
   }

   public static void onServerStarted(MinecraftServer mcServer, boolean integrated) {
      restoreAbandoned(mcServer);
      if (integrated) {
         Config config = ChunksmithProvider.isLoaded() ? ChunksmithProvider.get().getConfig() : null;
         if (config != null && config.isWorldEnterPregenEnabled()) {
            ServerLevel level = mcServer.overworld();
            String dimension = dimensionId(level);
            long radius = config.getWorldEnterPregenRadius();
            double[] center = resolveCenter(mcServer, config, dimension);
            double centerX = center[0];
            double centerZ = center[1];
            Optional<WorldEnterDone> alreadyDone = WorldEnterDone.read(worldDir(mcServer));
            if (alreadyDone.isPresent() && alreadyDone.get().satisfies(dimension, radius, centerX, centerZ)) {
               LOGGER.info(
                  "Chunksmith: this world has already been pre-generated to {} blocks, so the world-enter pregen is being skipped. Raise worldEnterPregenRadius if you want more, or delete {} in the world folder to run it again.",
                  alreadyDone.get().radiusBlocks(),
                  "worldenter-done.json"
               );
            } else if (ACTIVE.compareAndSet(false, true)) {
               server = mcServer;
               worldKey = dimension;
               radiusBlocks = radius;
               centerBlocksX = centerX;
               centerBlocksZ = centerZ;
               chunksTotal = estimateChunks(radius);
               ETA.reset();
               chunksDone = 0L;
               percentComplete = 0.0F;
               selectionCovered = false;
               listenForProgress();
               boolean timeAdvanceWasOn = level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT);
               WorldEnterState borrowed = new WorldEnterState(
                  timeAdvanceWasOn, 0L, config.getThrottleTickBudgetMillis(), config.getThrottlePlayerReserveMillis(), config.getThrottleTargetMspt()
               );
               if (!borrowed.write(worldDir(mcServer))) {
                  LOGGER.warn("Chunksmith: could not record the world-enter state, so the world-enter pregen is being skipped. Nothing has been changed.");
                  ACTIVE.set(false);
               } else {
                  stopTimeAdvancing(mcServer, level);
                  raiseThrottleForAnEmptyWorld(config);
                  freeze(mcServer, true);
                  double horizon = config.getWorldEnterLodHorizon();
                  if (horizon > 0.0) {
                     String reason = LodHorizon.unavailableReason();
                     if (reason == null && LodPresence.indexFor(worldKey) == null) {
                        reason = "LOD is not being built for this world-enter run (worldEnterLod)";
                     }

                     if (reason != null) {
                        LOGGER.info("Chunksmith: world-enter pregen running without its LOD horizon -- {}. Every chunk will be saved.", reason);
                        horizon = 0.0;
                     }
                  }

                  boolean started = ChunksmithProvider.get().getApi().startTask(worldKey, "circle", centerX, centerZ, radius, radius, "concentric", horizon);
                  if (!started) {
                     LOGGER.warn("Chunksmith: the world-enter pregen could not start a task; releasing the world and restoring settings.");
                     release();
                  } else {
                     LOGGER.info(
                        "Chunksmith: world-enter pregen started -- radius {} blocks (~{} chunks) centred on {}, {}. The world is frozen until it finishes or you choose to enter. Freezing does NOT make this faster; it stops the world moving while it runs.",
                        new Object[]{radius, chunksTotal, (long)centerX, (long)centerZ}
                     );
                  }
               }
            }
         }
      }
   }

   public static void release() {
      MinecraftServer mcServer = server;
      if (mcServer != null && ACTIVE.get()) {
         restoreFrom(mcServer, WorldEnterState.read(worldDir(mcServer)).orElse(null));
         WorldEnterState.clear(worldDir(mcServer));
         ACTIVE.set(false);
         LOGGER.info("Chunksmith: world released. Any remaining pre-generation continues in the background and now yields to you.");
      }
   }

   private static void listenForProgress() {
      ChunksmithAPI api = ChunksmithProvider.get().getApi();
      api.onGenerationProgress(event -> {
         if (ACTIVE.get() && event.world().equals(worldKey)) {
            chunksDone = event.chunks();
            percentComplete = event.progress();
            selectionCovered = event.complete();
            ETA.sample(System.currentTimeMillis(), event.chunks());
         }
      });
      api.onGenerationComplete(
         event -> {
            if (ACTIVE.get() && event.world().equals(worldKey)) {
               if (selectionCovered) {
                  recordCompletion();
                  LOGGER.info("Chunksmith: world-enter pregen finished; releasing the world. It will not run again on this world.");
               } else {
                  LOGGER.info(
                     "Chunksmith: the world-enter pregen stopped after {} of ~{} chunks ({}%) without covering the {}-block radius, so it is NOT being recorded as done and will pick up where it left off next time you load this world. Releasing the world now; anything still running continues in the background. A pause under load is the usual reason -- look for an auto-pause line above this one.",
                     new Object[]{chunksDone, chunksTotal, Math.round(percentComplete), radiusBlocks}
                  );
               }

               release();
            }
         }
      );
   }

   private static void recordCompletion() {
      MinecraftServer mcServer = server;
      if (mcServer != null) {
         boolean recorded = new WorldEnterDone(worldKey, radiusBlocks, centerBlocksX, centerBlocksZ, System.currentTimeMillis()).write(worldDir(mcServer));
         if (!recorded) {
            LOGGER.warn(
               "Chunksmith: the world-enter pregen finished but its completion could not be recorded, so it may run again on the next load. Nothing is wrong with the generated chunks."
            );
         }
      }
   }

   private static void restoreAbandoned(MinecraftServer mcServer) {
      Path dir = worldDir(mcServer);
      Optional<WorldEnterState> abandoned = WorldEnterState.read(dir);
      if (!abandoned.isEmpty()) {
         LOGGER.info("Chunksmith: a previous session left this world mid-pregen. Restoring the settings it had borrowed.");
         restoreFrom(mcServer, abandoned.get());
         WorldEnterState.clear(dir);
      }
   }

   private static void restoreFrom(MinecraftServer mcServer, WorldEnterState state) {
      freeze(mcServer, false);
      if (state != null) {
         ServerLevel level = mcServer.overworld();
         ((BooleanValue)level.getGameRules().getRule(GameRules.RULE_DAYLIGHT)).set(state.timeAdvanceWasOn(), mcServer);
         if (ChunksmithProvider.isLoaded()) {
            Config config = ChunksmithProvider.get().getConfig();
            config.setThrottleTickBudgetMillis(state.tickBudgetMillis());
            config.setThrottlePlayerReserveMillis(state.playerReserveMillis());
            config.setThrottleTargetMspt(state.targetMspt());
         }
      }
   }

   private static void freeze(MinecraftServer mcServer, boolean frozen) {
      try {
         mcServer.tickRateManager().setFrozen(frozen);
         frozenByUs = frozen;
         WorldEnterFreeze.set(frozen);
      } catch (RuntimeException e) {
         LOGGER.warn("Chunksmith: could not set the tick freeze ({}); carrying on unfrozen.", e.toString());
         frozenByUs = false;
         WorldEnterFreeze.set(false);
      }
   }

   private static void stopTimeAdvancing(MinecraftServer mcServer, ServerLevel level) {
      ((BooleanValue)level.getGameRules().getRule(GameRules.RULE_DAYLIGHT)).set(false, mcServer);
      level.setDayTime(0L);
   }

   private static void raiseThrottleForAnEmptyWorld(Config config) {
      config.setThrottleTickBudgetMillis(45L);
      config.setThrottlePlayerReserveMillis(0L);
      config.setThrottleTargetMspt(400.0);
   }

   public static boolean isActive() {
      return ACTIVE.get();
   }

   public static boolean isFrozen() {
      return frozenByUs;
   }

   public static long chunksTotal() {
      return chunksTotal;
   }

   public static long chunksDone() {
      return chunksDone;
   }

   public static double fraction() {
      float reported = percentComplete;
      return reported > 0.0F ? Math.max(0.0, Math.min(1.0, reported / 100.0)) : PregenEta.fraction(chunksDone, chunksTotal);
   }

   public static String eta() {
      return ETA.describe(chunksDone, chunksTotal);
   }

   static long estimateChunks(long radiusBlocks) {
      double radiusChunks = radiusBlocks / 16.0;
      return (long)Math.ceil(Math.PI * radiusChunks * radiusChunks);
   }

   private static double[] resolveCenter(MinecraftServer mcServer, Config config, String dimension) {
      WorldEnterCenter wanted = WorldEnterCenter.parse(config.getWorldEnterPregenCenter());
      if (wanted == null) {
         return new double[]{0.0, 0.0};
      }

      if (!wanted.followsSpawn()) {
         return new double[]{wanted.x(), wanted.z()};
      }

      if (wanted.isAuto()) {
         Optional<WorldEnterDone> done = WorldEnterDone.read(worldDir(mcServer));
         if (done.isPresent() && dimension.equals(done.get().dimension())) {
            return new double[]{done.get().centerX(), done.get().centerZ()};
         }
      }

      Optional<World> world = ChunksmithProvider.get().getServer().getWorld(dimension);
      if (world.isEmpty()) {
         LOGGER.warn("Chunksmith: worldEnterPregenCenter is 'spawn' but {} could not be resolved, so the pregen is centred on 0, 0 instead.", dimension);
         return new double[]{0.0, 0.0};
      } else {
         Location spawn = world.get().getSpawn();
         return new double[]{spawn.getX(), spawn.getZ()};
      }
   }

   private static String dimensionId(ServerLevel level) {
      return level.dimension().location().toString();
   }

   private static Path worldDir(MinecraftServer mcServer) {
      return mcServer.getWorldPath(LevelResource.ROOT);
   }
}
