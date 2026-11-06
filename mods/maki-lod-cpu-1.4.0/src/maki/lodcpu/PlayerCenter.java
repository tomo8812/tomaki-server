package maki.lodcpu;

import com.google.gson.Gson;
import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.lod.LodHorizon;
import com.kishku7.chunksmith.platform.World;
import com.kishku7.chunksmith.util.Parameter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.lang.invoke.MethodHandle;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlayerCenter {
   private static final Logger LOG = LoggerFactory.getLogger("MakiLodFollow");
   private static final Gson JSON = new Gson();
   private static final String WORLD = "minecraft:overworld";
   private static MinecraftServer owner;
   private static PlayerCenter.Saved saved;
   private static Path stateFile;
   private static long boot;
   private static long emptySince;
   private static long lastCheck;
   private static long lastSave;
   private static long lastReport;
   private static long lastCenterChange;
   private static long healthySince;
   private static long lastStateSummary;
   private static String lastLoggedState;
   private static boolean hadPlayers;
   private static boolean manualPause;
   private static boolean loadPause;
   private static boolean requested;
   private static boolean wasEnabled;
   private static String reason = "starting";
   private static MethodHandle toWorld;
   private static MethodHandle isShipyard;
   private static boolean checkedVs;
   private static boolean positionError;
   private static Integer appliedX;
   private static Integer appliedZ;
   private static GenerationTask lastTask;
   private static final ConcurrentLinkedQueue<PlayerCenter.NativeTaskLifecycle> nativeTaskLifecycle = new ConcurrentLinkedQueue<>();
   private static volatile GenerationTask controllerStopTask;

   private static Settings settings() {
      return CpuGovernor.settings();
   }

   private static boolean enabled() {
      return CpuGovernor.healthyConfig() && settings().followEnabled && !saved.disabled;
   }

   private static void initialize(MinecraftServer server, long now) {
      owner = server;
      boot = now;
      lastStateSummary = 0L;
      healthySince = 0L;
      lastCenterChange = 0L;
      lastReport = 0L;
      lastSave = 0L;
      lastCheck = 0L;
      lastLoggedState = null;
      emptySince = server.getPlayerCount() == 0 ? now : 0L;
      hadPlayers = server.getPlayerCount() > 0;
      loadPause = false;
      manualPause = false;
      requested = true;
      positionError = false;
      reason = "startup-wait";
      appliedZ = null;
      appliedX = null;
      lastTask = null;
      nativeTaskLifecycle.clear();
      controllerStopTask = null;
      stateFile = server.getWorldPath(LevelResource.ROOT).resolve("data/maki_lod_follow.json");
      saved = new PlayerCenter.Saved();
      saved.player = settings().followPlayer;
      saved.uuid = settings().followUuid;

      try {
         if (Files.exists(stateFile)) {
            PlayerCenter.Saved old = (PlayerCenter.Saved)JSON.fromJson(Files.readString(stateFile), PlayerCenter.Saved.class);
            if (old != null && settings().followUuid.equals(old.uuid)) {
               saved.disabled = old.disabled;
               if (old.x != null && old.z != null) {
                  FollowPolicy.center(old.x.intValue());
                  FollowPolicy.center(old.z.intValue());
                  saved = old;
               }
            }
         }

         Path player = server.getWorldPath(LevelResource.ROOT).resolve("playerdata").resolve(settings().followUuid + ".dat");
         if (Files.exists(player) && Files.getLastModifiedTime(player).toMillis() >= saved.savedAt) {
            CompoundTag tag = NbtIo.readCompressed(player, NbtAccounter.create(33554432L));
            if (tag.getString("Dimension").equals("minecraft:overworld")) {
               ListTag pos = tag.getList("Pos", 6);
               if (pos.size() == 3) {
                  accept(server.overworld(), new Vec3(pos.getDouble(0), pos.getDouble(1), pos.getDouble(2)));
               }
            }
         }
      } catch (Exception | LinkageError e) {
         LOG.warn("Could not load latest follow position; waiting for the named player when necessary", e);
      }

      wasEnabled = enabled();
      LOG.info(
         "Following {} ({}), online {} slots, radius {}, recenter at most every {}s, idle grace {}s; saved center {},{}",
         new Object[]{
            settings().followPlayer,
            settings().followUuid,
            settings().generateWhileOnline ? settings().onlineMax : 0,
            settings().followRadius,
            settings().recenterSeconds,
            settings().emptyGraceSeconds,
            saved.x,
            saved.z
         }
      );
   }

   private static Vec3 worldPosition(Level level, Vec3 pos) throws ReflectiveOperationException {
      if (!checkedVs) {
         try {
            Class<?> vs = Class.forName("org.valkyrienskies.mod.common.VSGameUtilsKt", false, PlayerCenter.class.getClassLoader());
            MethodHandle transform = ExactStatic.find(vs, "toWorldCoordinates", Vec3.class, Level.class, Vec3.class);
            MethodHandle contains = ExactStatic.find(vs, "isBlockInShipyard", boolean.class, Level.class, Vec3.class);
            toWorld = transform;
            isShipyard = contains;
            checkedVs = true;
         } catch (ClassNotFoundException ignored) {
            checkedVs = true;
         }
      }

      try {
         if (toWorld != null && isShipyard != null && (boolean)isShipyard.invokeExact((Level)level, (Vec3)pos)) {
            pos = (Vec3)toWorld.invokeExact((Level)level, (Vec3)pos);
            if ((boolean)isShipyard.invokeExact((Level)level, (Vec3)pos)) {
               throw new IllegalStateException("Ship world transform is not available yet");
            }
         }

         return pos;
      } catch (Throwable error) {
         if (error instanceof Error fatal) {
            throw fatal;
         } else {
            throw new ReflectiveOperationException("Could not resolve the player's ship-to-world position", error);
         }
      }
   }

   private static void accept(Level level, Vec3 raw) throws ReflectiveOperationException {
      Vec3 pos = worldPosition(level, raw);
      int x = FollowPolicy.center(pos.x);
      int z = FollowPolicy.center(pos.z);
      saved.x = x;
      saved.z = z;
      saved.player = settings().followPlayer;
      saved.uuid = settings().followUuid;
   }

   private static void capture(ServerPlayer player) {
      if (player.getUUID().toString().equals(settings().followUuid) && player.level().dimension() == Level.OVERWORLD) {
         try {
            accept(player.level(), player.position());
            positionError = false;
         } catch (Exception | LinkageError e) {
            if (!positionError) {
               LOG.warn("Keeping the previous LOD center until this player's world position is available", e);
               positionError = true;
            }
         }
      }
   }

   public static void logout(ServerPlayer player) {
      if (owner == player.getServer() && saved != null) {
         capture(player);
         save(System.currentTimeMillis());
      }
   }

   public static void login(ServerPlayer player) {
      if (owner == player.getServer() && saved != null && enabled() && !settings().generateWhileOnline) {
         capture(player);
         boolean followPaused = true;
         if (ChunksmithProvider.isLoaded()) {
            followPaused = pause(ChunksmithProvider.get());
         }

         reason = followPaused ? "players-online-paused" : "external-task-active";
         save(System.currentTimeMillis());
      }
   }

   public static void shutdown(MinecraftServer server) {
      if (owner == server && saved != null) {
         save(System.currentTimeMillis());
      }

      owner = null;
      nativeTaskLifecycle.clear();
      controllerStopTask = null;
   }

   private static void save(long now) {
      if (stateFile != null) {
         try {
            saved.savedAt = now;
            Files.createDirectories(stateFile.getParent());
            Path tmp = stateFile.resolveSibling(stateFile.getFileName() + ".tmp");
            Files.writeString(tmp, JSON.toJson(saved));

            try {
               Files.move(tmp, stateFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
               Files.move(tmp, stateFile, StandardCopyOption.REPLACE_EXISTING);
            }

            lastSave = now;
         } catch (Exception e) {
            LOG.warn("Could not persist LOD follow center", e);
         }
      }
   }

   private static boolean pause(Chunksmith chunky) {
      if (chunky == null) {
         return true;
      }

      GenerationTask current = (GenerationTask)chunky.getGenerationTasks().get("minecraft:overworld");
      if (current != null && !TaskOwnership.isOwned(current)) {
         TaskOwnership.noteExternalActive();
         reason = "external-task-active";
         return false;
      }

      if (current != null && !current.isStopping()) {
         controllerStopTask = current;

         try {
            current.stop(false);
         } finally {
            controllerStopTask = null;
         }
      }

      return true;
   }

   public static void nativeTaskStarted(GenerationTask task) {
      if (task != null && TaskOwnership.isOwned(task)) {
         nativeTaskLifecycle.add(new PlayerCenter.NativeTaskLifecycle(task, true, false));
      }
   }

   public static void nativeTaskStopping(GenerationTask task, boolean cancelled) {
      if (task != null && TaskOwnership.isOwned(task) && task != controllerStopTask) {
         nativeTaskLifecycle.add(new PlayerCenter.NativeTaskLifecycle(task, false, cancelled));
      }
   }

   private static void markManualPause() {
      manualPause = true;
      requested = false;
      reason = "manual-pause";
   }

   private static boolean sameTaskSelection(GenerationTask first, GenerationTask second) {
      if (first != null && second != null) {
         Selection a = first.getSelection();
         Selection b = second.getSelection();
         return "minecraft:overworld".equals(a.world().getName())
            && "minecraft:overworld".equals(b.world().getName())
            && Double.compare(a.centerX(), b.centerX()) == 0
            && Double.compare(a.centerZ(), b.centerZ()) == 0
            && Double.compare(a.radiusX(), b.radiusX()) == 0
            && Double.compare(a.radiusZ(), b.radiusZ()) == 0
            && Double.compare(a.horizon(), b.horizon()) == 0
            && a.shape().equals(b.shape())
            && a.pattern().toString().equals(b.pattern().toString());
      } else {
         return false;
      }
   }

   private static void applyNativeTaskLifecycle() {
      PlayerCenter.NativeTaskLifecycle event;
      while ((event = nativeTaskLifecycle.poll()) != null) {
         GenerationTask task = event.task();
         if ("minecraft:overworld".equals(task.getSelection().world().getName()) && TaskOwnership.isOwned(task)) {
            if (!event.started()) {
               if (task == lastTask || event.cancelled() && (lastTask == null || sameTaskSelection(lastTask, task))) {
                  markManualPause();
               }
            } else if (manualPause && sameTaskSelection(lastTask, task)) {
               lastTask = task;
               appliedX = (int)task.getSelection().centerX();
               appliedZ = (int)task.getSelection().centerZ();
               manualPause = false;
               requested = false;
               reason = "generating";
            }
         }
      }
   }

   public static void tick(MinecraftServer server) {
      long now = System.currentTimeMillis();
      if (owner != server) {
         initialize(server, now);
      }

      if (now - lastCheck >= 1000L) {
         lastCheck = now;
         if (!settings().followUuid.equals(saved.uuid)) {
            initialize(server, now);
         }

         ServerPlayer player = server.getPlayerList().getPlayer(UUID.fromString(settings().followUuid));
         if (player != null) {
            capture(player);
         }

         if (now - lastSave >= 10000L) {
            save(now);
         }

         if (ChunksmithProvider.isLoaded()) {
            Chunksmith chunky = ChunksmithProvider.get();
            if (chunky != null) {
               applyNativeTaskLifecycle();
               boolean online = server.getPlayerCount() > 0;
               boolean active = enabled();
               if (hadPlayers && !online) {
                  emptySince = now;
                  requested = true;
                  pause(chunky);
               }

               if (!hadPlayers && online) {
                  requested = true;
                  emptySince = 0L;
               }

               hadPlayers = online;
               GenerationTask observedTask = (GenerationTask)chunky.getGenerationTasks().get("minecraft:overworld");
               if (observedTask != null && !TaskOwnership.isOwned(observedTask)) {
                  TaskOwnership.noteExternalActive();
                  reason = "external-task-active";
                  report(server, chunky, now);
               } else if (!active) {
                  if (wasEnabled) {
                     pause(chunky);
                  }

                  wasEnabled = false;
                  reason = "disabled";
                  report(server, chunky, now);
               } else {
                  if (!wasEnabled) {
                     requested = true;
                     wasEnabled = true;
                  }

                  if (manualPause) {
                     reason = "manual-pause";
                     report(server, chunky, now);
                  } else if (online && !settings().generateWhileOnline) {
                     pause(chunky);
                     requested = true;
                     loadPause = false;
                     healthySince = 0L;
                     reason = "players-online-paused";
                     report(server, chunky, now);
                  } else if (now - boot >= 10000L && (online || now - emptySince >= settings().emptyGraceSeconds * 1000L)) {
                     if (online && CpuGovernor.onlineOverload()) {
                        loadPause = true;
                        healthySince = 0L;
                        pause(chunky);
                        reason = "online-overload";
                        report(server, chunky, now);
                     } else {
                        if (loadPause) {
                           if (!CpuGovernor.recovered()) {
                              healthySince = 0L;
                           } else if (healthySince == 0L) {
                              healthySince = now;
                           }

                           if (healthySince == 0L || now - healthySince < 30000L) {
                              reason = "load-recovery-wait";
                              report(server, chunky, now);
                              return;
                           }

                           loadPause = false;
                           requested = true;
                        }

                        if (saved.x != null && saved.z != null) {
                           GenerationTask current = (GenerationTask)chunky.getGenerationTasks().get("minecraft:overworld");
                           TaskOwnership.PersistedOwner persistedOwner = current == null
                              ? TaskOwnership.persistedOwner(chunky, "minecraft:overworld")
                              : TaskOwnership.PersistedOwner.NONE;
                           if (current == null && persistedOwner == TaskOwnership.PersistedOwner.EXTERNAL) {
                              TaskOwnership.noteExternalPersisted();
                              requested = true;
                              reason = "external-task-persisted";
                              report(server, chunky, now);
                           } else {
                              TaskOwnership.noteOwnedOrAbsent();
                              if (current == null && !requested && lastTask != null && lastTask.getCount() < lastTask.getChunkIterator().total()) {
                                 manualPause = true;
                                 reason = "native-safety-pause";
                                 report(server, chunky, now);
                              } else {
                                 if (current == null
                                    && !requested
                                    && appliedX != null
                                    && FollowPolicy.moved(saved.x, saved.z, appliedX, appliedZ)
                                    && FollowPolicy.due(now, lastCenterChange, settings().recenterSeconds)) {
                                    requested = true;
                                 }

                                 if (current != null) {
                                    Selection selection = current.getSelection();
                                    boolean moved = FollowPolicy.moved(saved.x, saved.z, (int)selection.centerX(), (int)selection.centerZ());
                                    if ((moved || selection.radiusX() != settings().followRadius || selection.horizon() != 1.0)
                                       && FollowPolicy.due(now, lastCenterChange, settings().recenterSeconds)) {
                                       requested = true;
                                       pause(chunky);
                                       reason = "draining-before-recenter";
                                    } else {
                                       reason = "generating";
                                    }
                                 } else if (requested && !LodHorizon.isArmed("minecraft:overworld")) {
                                    Optional<World> world = chunky.getServer().getWorld("minecraft:overworld");
                                    if (world.isPresent()) {
                                       try {
                                          GenerationTask task = null;
                                          if (persistedOwner == TaskOwnership.PersistedOwner.CURRENT_SESSION
                                             || persistedOwner == TaskOwnership.PersistedOwner.PREVIOUS_SESSION && chunky.getConfig().getContinueOnRestart()) {
                                             Optional<GenerationTask> prior = chunky.getTaskLoader().loadTask(world.get());
                                             if (prior.isPresent() && !prior.get().isCancelled()) {
                                                Selection selection = prior.get().getSelection();
                                                if (!FollowPolicy.moved(saved.x, saved.z, (int)selection.centerX(), (int)selection.centerZ())
                                                   && selection.radiusX() == settings().followRadius
                                                   && selection.horizon() == 1.0) {
                                                   task = prior.get();
                                                   TaskOwnership.claim(task, "PlayerCenter-persisted");
                                                }
                                             }
                                          } else if (persistedOwner == TaskOwnership.PersistedOwner.PREVIOUS_SESSION) {
                                             TaskOwnership.noteFreshRestart();
                                          }

                                          if (task == null) {
                                             Selection selection = Selection.builder(chunky, world.get())
                                                .center(saved.x.intValue(), saved.z.intValue())
                                                .radius(settings().followRadius)
                                                .radiusZ(settings().followRadius)
                                                .shape("circle")
                                                .pattern(Parameter.of("concentric"))
                                                .horizon(1.0)
                                                .build();
                                             if (selection.radiusX() > chunky.getLimit()) {
                                                throw new IllegalStateException("Configured radius exceeds Chunksmith's limit");
                                             }

                                             task = new GenerationTask(chunky, selection);
                                             TaskOwnership.claim(task, "PlayerCenter-new");
                                          }

                                          if (chunky.getGenerationTasks().putIfAbsent("minecraft:overworld", task) == null) {
                                             try {
                                                chunky.getScheduler().runTask(task);
                                             } catch (RuntimeException e) {
                                                chunky.getGenerationTasks().remove("minecraft:overworld", task);
                                                throw e;
                                             }

                                             lastTask = task;
                                             appliedX = saved.x;
                                             appliedZ = saved.z;
                                             lastCenterChange = now;
                                             requested = false;
                                             reason = "generating";
                                             LOG.info(
                                                "LOD task follows {} at {},{}; radius {}, LOD-only, {} online slots; previous LOD retained",
                                                new Object[]{settings().followPlayer, saved.x, saved.z, settings().followRadius, settings().onlineMax}
                                             );
                                          }
                                       } catch (Exception | LinkageError e) {
                                          requested = false;
                                          manualPause = true;
                                          reason = "start-failed";
                                          LOG.error("LOD follow start failed; paused until explicit resume", e);
                                       }
                                    }
                                 } else if (!requested) {
                                    reason = "task-paused-or-completed";
                                 } else if (LodHorizon.isArmed("minecraft:overworld")) {
                                    reason = "waiting-for-lod-save-protection";
                                 }

                                 report(server, chunky, now);
                              }
                           }
                        } else {
                           reason = "waiting-for-tomo-position";
                           report(server, chunky, now);
                        }
                     }
                  } else {
                     pause(chunky);
                     reason = "grace-period";
                     report(server, chunky, now);
                  }
               }
            }
         }
      }
   }

   private static void report(MinecraftServer server, Chunksmith chunky, long now) {
      if (now - lastReport >= 1000L) {
         lastReport = now;

         try {
            GenerationTask current = (GenerationTask)chunky.getGenerationTasks().get("minecraft:overworld");
            TaskOwnership.PersistedOwner persistedOwner = TaskOwnership.persistedOwner(chunky, "minecraft:overworld");
            int players = server.getPlayerCount();
            boolean onlineBlocked = players > 0 && !settings().generateWhileOnline;
            boolean graceBlocked = now - boot < 10000L || players == 0 && now - emptySince < settings().emptyGraceSeconds * 1000L;
            boolean policyReady = enabled() && !manualPause && !loadPause && !onlineBlocked && !graceBlocked && saved.x != null && saved.z != null;
            boolean canRun = policyReady
               && (
                  current != null && TaskOwnership.isOwned(current) && !current.isStopping()
                     || current == null && requested && persistedOwner != TaskOwnership.PersistedOwner.EXTERNAL && !LodHorizon.isArmed("minecraft:overworld")
               );
            String activeOwner = current == null ? "none" : (TaskOwnership.isOwned(current) ? "follow" : "external");
            String state = reason
               + "|"
               + activeOwner
               + "|"
               + TaskOwnership.status(persistedOwner)
               + "|"
               + requested
               + "|"
               + manualPause
               + "|"
               + loadPause
               + "|"
               + canRun
               + "|"
               + players
               + "|"
               + saved.x
               + "|"
               + saved.z;
            if (!state.equals(lastLoggedState)) {
               LOG.info(
                  "LOD follow state changed: reason={}, activeOwner={}, persistedOwner={}, requested={}, manualPause={}, loadPause={}, canRun={}, players={}, center={},{}",
                  new Object[]{reason, activeOwner, TaskOwnership.status(persistedOwner), requested, manualPause, loadPause, canRun, players, saved.x, saved.z}
               );
               lastLoggedState = state;
               lastStateSummary = now;
            } else if (now - lastStateSummary >= 60000L) {
               LOG.info(
                  "LOD follow summary: reason={}, activeOwner={}, persistedOwner={}, canRun={}, players={}, center={},{}",
                  new Object[]{reason, activeOwner, TaskOwnership.status(persistedOwner), canRun, players, saved.x, saved.z}
               );
               lastStateSummary = now;
            }
         } catch (Exception e) {
            LOG.warn("Could not summarize LOD follow state; controller continues with the last valid state", e);
         }
      }
   }

   private static String actor(CommandSourceStack source) {
      return source.getTextName();
   }

   private static Chunksmith commandChunksmith(CommandSourceStack source) {
      if (!ChunksmithProvider.isLoaded()) {
         source.sendFailure(Component.literal("Chunksmith is not loaded."));
         return null;
      }

      Chunksmith chunky = ChunksmithProvider.get();
      if (chunky == null) {
         source.sendFailure(Component.literal("Chunksmith is not ready."));
      }

      return chunky;
   }

   private static int commandStatus(CommandSourceStack source) {
      if (saved == null) {
         source.sendFailure(Component.literal("LOD follow controller is not initialized yet."));
         return 0;
      } else {
         source.sendSuccess(
            () -> {
               String var10001 = saved.x == null ? "pending" : saved.x + "," + saved.z;
               return Component.literal(
                  "LOD follows "
                     + settings().followPlayer
                     + "; center "
                     + var10001
                     + "; reason="
                     + reason
                     + "; requested="
                     + requested
                     + "; manualPause="
                     + manualPause
                     + "; loadPause="
                     + loadPause
               );
            },
            false
         );
         return commandTaskStatus(source);
      }
   }

   private static int commandTaskStatus(CommandSourceStack source) {
      Chunksmith chunky = commandChunksmith(source);
      if (chunky == null) {
         return 0;
      }

      GenerationTask current = (GenerationTask)chunky.getGenerationTasks().get("minecraft:overworld");
      TaskOwnership.PersistedOwner persistedOwner = TaskOwnership.persistedOwner(chunky, "minecraft:overworld");
      TaskOwnership.PersistedTaskSnapshot snapshot = TaskOwnership.snapshot(chunky, "minecraft:overworld");
      String activeOwner = current == null ? "none" : (TaskOwnership.isOwned(current) ? "follow" : "external");
      source.sendSuccess(
         () -> Component.literal("Chunksmith overworld: activeOwner=" + activeOwner + ", persistedOwner=" + TaskOwnership.status(persistedOwner)), false
      );
      if (snapshot.present()) {
         String modified = snapshot.modifiedAt() >= 0L ? Instant.ofEpochMilli(snapshot.modifiedAt()).toString() : "unknown";
         String details = "persisted: cancelled="
            + snapshot.cancelled()
            + ", chunks="
            + snapshot.chunks()
            + ", time="
            + snapshot.time()
            + ", center="
            + snapshot.centerX()
            + ","
            + snapshot.centerZ()
            + ", radius="
            + snapshot.radius()
            + "/"
            + snapshot.radiusZ()
            + ", horizon="
            + snapshot.lodHorizon()
            + ", shape="
            + snapshot.shape()
            + ", pattern="
            + snapshot.pattern()
            + ", modifiedAt="
            + modified
            + ", sha256="
            + snapshot.sha256();
         source.sendSuccess(() -> Component.literal(details), false);
      }

      String latest = ExternalTaskMigration.latestBackupId(chunky, source.getServer());
      if (latest != null) {
         source.sendSuccess(() -> Component.literal("latest migration backup: " + latest), false);
      }

      if (current != null && !TaskOwnership.isOwned(current)) {
         source.sendSuccess(
            () -> Component.literal("External active task is protected. Let it finish or pause it with Chunksmith first; maki-lod will not stop or replace it."),
            false
         );
      } else if (current == null && persistedOwner == TaskOwnership.PersistedOwner.EXTERNAL) {
         String choices = Boolean.TRUE.equals(snapshot.cancelled())
            ? "Safe choices: /makilod task backup, /makilod task stash, /makilod task switch-follow, /makilod task restore-latest (after stash)."
            : "Safe choices: /makilod task backup, /makilod task resume-external, /makilod task stash, /makilod task switch-follow.";
         source.sendSuccess(() -> Component.literal(choices), false);
      }

      return 1;
   }

   private static int commandResume(CommandSourceStack source) {
      if (saved == null) {
         source.sendFailure(Component.literal("LOD follow controller is not initialized yet."));
         return 0;
      } else {
         Chunksmith chunky = commandChunksmith(source);
         if (chunky == null) {
            return 0;
         } else {
            GenerationTask current = (GenerationTask)chunky.getGenerationTasks().get("minecraft:overworld");
            if (current != null && !TaskOwnership.isOwned(current)) {
               source.sendFailure(
                  Component.literal("Follow resume blocked: an external minecraft:overworld task is active and will not be stopped or adopted.")
               );
               source.sendSuccess(
                  () -> Component.literal(
                     "Use /makilod task status; let the external task finish or pause it with Chunksmith before choosing a migration action."
                  ),
                  false
               );
               return 0;
            } else {
               TaskOwnership.PersistedOwner persistedOwner = TaskOwnership.persistedOwner(chunky, "minecraft:overworld");
               if (current == null && persistedOwner == TaskOwnership.PersistedOwner.EXTERNAL) {
                  TaskOwnership.PersistedTaskSnapshot snapshot = TaskOwnership.snapshot(chunky, "minecraft:overworld");
                  source.sendFailure(
                     Component.literal(
                        "Follow resume blocked by protected external persisted state (chunks="
                           + snapshot.chunks()
                           + ", center="
                           + snapshot.centerX()
                           + ","
                           + snapshot.centerZ()
                           + ", sha256="
                           + snapshot.sha256()
                           + "). No state was overwritten."
                     )
                  );
                  source.sendSuccess(
                     () -> Component.literal(
                        Boolean.TRUE.equals(snapshot.cancelled())
                           ? "Use /makilod task backup, stash, or switch-follow."
                           : "Use /makilod task resume-external to continue it, or /makilod task switch-follow to preserve it and start follow."
                     ),
                     false
                  );
                  return 0;
               } else {
                  saved.disabled = false;
                  manualPause = false;
                  requested = true;
                  emptySince = 0L;
                  save(System.currentTimeMillis());
                  source.sendSuccess(
                     () -> Component.literal(
                        "LOD follow resume requested. This does not assert generation has started; use /makilod status to confirm activeTaskOwner=follow and current blockers."
                     ),
                     false
                  );
                  return 1;
               }
            }
         }
      }
   }

   private static int commandBackup(CommandSourceStack source) {
      Chunksmith chunky = commandChunksmith(source);
      if (chunky == null) {
         return 0;
      } else {
         ExternalTaskMigration.Result result = ExternalTaskMigration.backup(chunky, source.getServer(), actor(source));
         if (!result.ok()) {
            source.sendFailure(Component.literal(result.message()));
            return 0;
         } else {
            source.sendSuccess(() -> Component.literal(result.message()), false);
            return 1;
         }
      }
   }

   private static int commandResumeExternal(CommandSourceStack source) {
      Chunksmith chunky = commandChunksmith(source);
      if (chunky == null) {
         return 0;
      } else {
         ExternalTaskMigration.Result result = ExternalTaskMigration.resumeExternal(chunky, source.getServer(), actor(source));
         if (!result.ok()) {
            source.sendFailure(Component.literal(result.message()));
            return 0;
         } else {
            reason = "external-task-active";
            source.sendSuccess(() -> Component.literal(result.message()), false);
            return 1;
         }
      }
   }

   private static int commandStash(CommandSourceStack source) {
      if (saved == null) {
         source.sendFailure(Component.literal("LOD follow controller is not initialized yet."));
         return 0;
      } else {
         Chunksmith chunky = commandChunksmith(source);
         if (chunky == null) {
            return 0;
         } else {
            ExternalTaskMigration.Result result = ExternalTaskMigration.stash(chunky, source.getServer(), actor(source), "stash");
            if (!result.ok()) {
               source.sendFailure(Component.literal(result.message()));
               return 0;
            } else {
               manualPause = true;
               requested = false;
               reason = "external-task-stashed";
               save(System.currentTimeMillis());
               source.sendSuccess(() -> Component.literal(result.message() + " Follow remains paused; use /makilod resume when ready."), false);
               return 1;
            }
         }
      }
   }

   private static int commandSwitchFollow(CommandSourceStack source) {
      if (saved == null) {
         source.sendFailure(Component.literal("LOD follow controller is not initialized yet."));
         return 0;
      } else {
         Chunksmith chunky = commandChunksmith(source);
         if (chunky == null) {
            return 0;
         } else {
            ExternalTaskMigration.Result result = ExternalTaskMigration.stash(chunky, source.getServer(), actor(source), "switch-follow");
            if (!result.ok()) {
               source.sendFailure(Component.literal(result.message()));
               return 0;
            } else {
               saved.disabled = false;
               manualPause = false;
               requested = true;
               emptySince = 0L;
               reason = "follow-switch-requested";
               save(System.currentTimeMillis());
               source.sendSuccess(
                  () -> Component.literal(
                     result.message()
                        + " Follow is now requested, not assumed started. Confirm /makilod status shows activeTaskOwner=follow and runtime progress."
                  ),
                  false
               );
               return 1;
            }
         }
      }
   }

   private static int commandRestoreLatest(CommandSourceStack source) {
      if (saved == null) {
         source.sendFailure(Component.literal("LOD follow controller is not initialized yet."));
         return 0;
      } else {
         Chunksmith chunky = commandChunksmith(source);
         if (chunky == null) {
            return 0;
         } else {
            ExternalTaskMigration.Result result = ExternalTaskMigration.restoreLatest(chunky, source.getServer(), actor(source));
            if (!result.ok()) {
               source.sendFailure(Component.literal(result.message()));
               return 0;
            } else {
               manualPause = true;
               requested = false;
               reason = "external-task-restored";
               save(System.currentTimeMillis());
               source.sendSuccess(() -> Component.literal(result.message() + " Follow is paused so the restored external state cannot be overwritten."), false);
               return 1;
            }
         }
      }
   }

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("makilod")
                        .requires(s -> s.hasPermission(2)))
                     .then(Commands.literal("status").executes(c -> commandStatus((CommandSourceStack)c.getSource()))))
                  .then(Commands.literal("disable").executes(c -> {
                     if (saved == null) {
                        return 0;
                     }

                     saved.disabled = true;
                     pause(ChunksmithProvider.get());
                     save(System.currentTimeMillis());
                     ((CommandSourceStack)c.getSource())
                        .sendSuccess(() -> Component.literal("LOD follow disabled; external tasks, if any, were not stopped."), false);
                     return 1;
                  })))
               .then(Commands.literal("resume").executes(c -> commandResume((CommandSourceStack)c.getSource()))))
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                                    "task"
                                 )
                                 .then(Commands.literal("status").executes(c -> commandTaskStatus((CommandSourceStack)c.getSource()))))
                              .then(Commands.literal("backup").executes(c -> commandBackup((CommandSourceStack)c.getSource()))))
                           .then(Commands.literal("resume-external").executes(c -> commandResumeExternal((CommandSourceStack)c.getSource()))))
                        .then(Commands.literal("stash").executes(c -> commandStash((CommandSourceStack)c.getSource()))))
                     .then(Commands.literal("switch-follow").executes(c -> commandSwitchFollow((CommandSourceStack)c.getSource()))))
                  .then(Commands.literal("restore-latest").executes(c -> commandRestoreLatest((CommandSourceStack)c.getSource())))
            )
      );
   }

   private record NativeTaskLifecycle(GenerationTask task, boolean started, boolean cancelled) {
   }

   static final class Saved {
      String player;
      String uuid;
      Integer x;
      Integer z;
      boolean disabled;
      long savedAt;
   }
}
