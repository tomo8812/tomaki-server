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
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;
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

   private static Settings settings() {
      return CpuGovernor.settings();
   }

   private static boolean enabled() {
      return CpuGovernor.healthyConfig() && settings().followEnabled && !saved.disabled;
   }

   private static void initialize(MinecraftServer var0, long var1) {
      owner = var0;
      boot = var1;
      healthySince = 0L;
      lastCenterChange = 0L;
      lastReport = 0L;
      lastSave = 0L;
      lastCheck = 0L;
      emptySince = var0.getPlayerCount() == 0 ? var1 : 0L;
      hadPlayers = var0.getPlayerCount() > 0;
      loadPause = false;
      manualPause = false;
      requested = true;
      positionError = false;
      reason = "startup-wait";
      appliedZ = null;
      appliedX = null;
      lastTask = null;
      stateFile = var0.getWorldPath(LevelResource.ROOT).resolve("data/maki_lod_follow.json");
      saved = new PlayerCenter.Saved();
      saved.player = settings().followPlayer;
      saved.uuid = settings().followUuid;

      try {
         if (Files.exists(stateFile)) {
            PlayerCenter.Saved var3 = (PlayerCenter.Saved)JSON.fromJson(Files.readString(stateFile), PlayerCenter.Saved.class);
            if (var3 != null && settings().followUuid.equals(var3.uuid)) {
               saved.disabled = var3.disabled;
               if (var3.x != null && var3.z != null) {
                  FollowPolicy.center(var3.x.intValue());
                  FollowPolicy.center(var3.z.intValue());
                  saved = var3;
               }
            }
         }

         Path var7 = var0.getWorldPath(LevelResource.ROOT).resolve("playerdata").resolve(settings().followUuid + ".dat");
         if (Files.exists(var7) && Files.getLastModifiedTime(var7).toMillis() >= saved.savedAt) {
            CompoundTag var4 = NbtIo.readCompressed(var7, NbtAccounter.create(33554432L));
            if (var4.getString("Dimension").equals("minecraft:overworld")) {
               ListTag var5 = var4.getList("Pos", 6);
               if (var5.size() == 3) {
                  accept(var0.overworld(), new Vec3(var5.getDouble(0), var5.getDouble(1), var5.getDouble(2)));
               }
            }
         }
      } catch (Exception | LinkageError var6) {
         LOG.warn("Could not load latest follow position; waiting for the named player when necessary", var6);
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

   private static Vec3 worldPosition(Level var0, Vec3 var1) throws ReflectiveOperationException {
      if (!checkedVs) {
         try {
            Class var2 = Class.forName("org.valkyrienskies.mod.common.VSGameUtilsKt", false, PlayerCenter.class.getClassLoader());
            MethodHandle var3 = ExactStatic.find(var2, "toWorldCoordinates", Vec3.class, Level.class, Vec3.class);
            MethodHandle var4 = ExactStatic.find(var2, "isBlockInShipyard", boolean.class, Level.class, Vec3.class);
            toWorld = var3;
            isShipyard = var4;
            checkedVs = true;
         } catch (ClassNotFoundException var5) {
            checkedVs = true;
         }
      }

      try {
         if (toWorld != null && isShipyard != null && (boolean)isShipyard.invokeExact((Level)var0, (Vec3)var1)) {
            var1 = (Vec3)toWorld.invokeExact((Level)var0, (Vec3)var1);
            if ((boolean)isShipyard.invokeExact((Level)var0, (Vec3)var1)) {
               throw new IllegalStateException("Ship world transform is not available yet");
            }
         }

         return var1;
      } catch (Throwable var6) {
         if (var6 instanceof Error var7) {
            throw var7;
         } else {
            throw new ReflectiveOperationException("Could not resolve the player's ship-to-world position", var6);
         }
      }
   }

   private static void accept(Level var0, Vec3 var1) throws ReflectiveOperationException {
      Vec3 var2 = worldPosition(var0, var1);
      int var3 = FollowPolicy.center(var2.x);
      int var4 = FollowPolicy.center(var2.z);
      saved.x = var3;
      saved.z = var4;
      saved.player = settings().followPlayer;
      saved.uuid = settings().followUuid;
   }

   private static void capture(ServerPlayer var0) {
      if (var0.getUUID().toString().equals(settings().followUuid) && var0.level().dimension() == Level.OVERWORLD) {
         try {
            accept(var0.level(), var0.position());
            positionError = false;
         } catch (Exception | LinkageError var2) {
            if (!positionError) {
               LOG.warn("Keeping the previous LOD center until this player's world position is available", var2);
               positionError = true;
            }
         }
      }
   }

   public static void logout(ServerPlayer var0) {
      if (owner == var0.getServer() && saved != null) {
         capture(var0);
         save(System.currentTimeMillis());
      }
   }

   public static void login(ServerPlayer var0) {
      if (owner == var0.getServer() && saved != null && enabled() && !settings().generateWhileOnline) {
         capture(var0);
         if (ChunksmithProvider.isLoaded()) {
            pause(ChunksmithProvider.get());
         }

         reason = "players-online-paused";
         save(System.currentTimeMillis());
      }
   }

   public static void shutdown(MinecraftServer var0) {
      if (owner == var0 && saved != null) {
         save(System.currentTimeMillis());
      }

      owner = null;
   }

   private static void save(long var0) {
      if (stateFile != null) {
         try {
            saved.savedAt = var0;
            Files.createDirectories(stateFile.getParent());
            Path var2 = stateFile.resolveSibling(stateFile.getFileName() + ".tmp");
            Files.writeString(var2, JSON.toJson(saved));

            try {
               Files.move(var2, stateFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException var4) {
               Files.move(var2, stateFile, StandardCopyOption.REPLACE_EXISTING);
            }

            lastSave = var0;
         } catch (Exception var5) {
            LOG.warn("Could not persist LOD follow center", var5);
         }
      }
   }

   private static void pause(Chunksmith var0) {
      if (var0 != null) {
         GenerationTask var1 = (GenerationTask)var0.getGenerationTasks().get("minecraft:overworld");
         if (var1 != null && !var1.isStopping()) {
            var1.stop(false);
         }
      }
   }

   public static void nativePause() {
      manualPause = true;
      requested = false;
      reason = "manual-pause";
   }

   public static void nativeResume() {
      manualPause = false;
      requested = true;
   }

   public static void tick(MinecraftServer var0) {
      long var1 = System.currentTimeMillis();
      if (owner != var0) {
         initialize(var0, var1);
      }

      if (var1 - lastCheck >= 1000L) {
         lastCheck = var1;
         if (!settings().followUuid.equals(saved.uuid)) {
            initialize(var0, var1);
         }

         ServerPlayer var3 = var0.getPlayerList().getPlayer(UUID.fromString(settings().followUuid));
         if (var3 != null) {
            capture(var3);
         }

         if (var1 - lastSave >= 10000L) {
            save(var1);
         }

         if (ChunksmithProvider.isLoaded()) {
            Chunksmith var4 = ChunksmithProvider.get();
            if (var4 != null) {
               boolean var5 = var0.getPlayerCount() > 0;
               boolean var6 = enabled();
               if (hadPlayers && !var5) {
                  emptySince = var1;
                  requested = true;
                  pause(var4);
               }

               if (!hadPlayers && var5) {
                  requested = true;
                  emptySince = 0L;
               }

               hadPlayers = var5;
               if (!var6) {
                  if (wasEnabled) {
                     pause(var4);
                  }

                  wasEnabled = false;
                  reason = "disabled";
                  report(var0, var4, var1);
               } else {
                  if (!wasEnabled) {
                     requested = true;
                     wasEnabled = true;
                  }

                  if (manualPause) {
                     reason = "manual-pause";
                     report(var0, var4, var1);
                  } else if (var5 && !settings().generateWhileOnline) {
                     pause(var4);
                     requested = true;
                     loadPause = false;
                     healthySince = 0L;
                     reason = "players-online-paused";
                     report(var0, var4, var1);
                  } else if (var1 - boot >= 10000L && (var5 || var1 - emptySince >= settings().emptyGraceSeconds * 1000L)) {
                     if (var5 && CpuGovernor.onlineOverload()) {
                        loadPause = true;
                        healthySince = 0L;
                        pause(var4);
                        reason = "online-overload";
                        report(var0, var4, var1);
                     } else {
                        if (loadPause) {
                           if (!CpuGovernor.recovered()) {
                              healthySince = 0L;
                           } else if (healthySince == 0L) {
                              healthySince = var1;
                           }

                           if (healthySince == 0L || var1 - healthySince < 30000L) {
                              reason = "load-recovery-wait";
                              report(var0, var4, var1);
                              return;
                           }

                           loadPause = false;
                           requested = true;
                        }

                        if (saved.x != null && saved.z != null) {
                           GenerationTask var7 = (GenerationTask)var4.getGenerationTasks().get("minecraft:overworld");
                           if (var7 == null && !requested && lastTask != null && lastTask.getCount() < lastTask.getChunkIterator().total()) {
                              manualPause = true;
                              reason = "native-safety-pause";
                              report(var0, var4, var1);
                           } else {
                              if (var7 == null
                                 && !requested
                                 && appliedX != null
                                 && FollowPolicy.moved(saved.x, saved.z, appliedX, appliedZ)
                                 && FollowPolicy.due(var1, lastCenterChange, settings().recenterSeconds)) {
                                 requested = true;
                              }

                              if (var7 != null) {
                                 Selection var8 = var7.getSelection();
                                 boolean var9 = FollowPolicy.moved(saved.x, saved.z, (int)var8.centerX(), (int)var8.centerZ());
                                 if ((var9 || var8.radiusX() != settings().followRadius || var8.horizon() != 1.0)
                                    && FollowPolicy.due(var1, lastCenterChange, settings().recenterSeconds)) {
                                    requested = true;
                                    pause(var4);
                                    reason = "draining-before-recenter";
                                 } else {
                                    reason = "generating";
                                 }
                              } else if (requested && !LodHorizon.isArmed("minecraft:overworld")) {
                                 Optional var14 = var4.getServer().getWorld("minecraft:overworld");
                                 if (var14.isPresent()) {
                                    try {
                                       Optional var15 = var4.getTaskLoader().loadTask((World)var14.get());
                                       GenerationTask var10 = null;
                                       if (var15.isPresent() && !((GenerationTask)var15.get()).isCancelled()) {
                                          Selection var11 = ((GenerationTask)var15.get()).getSelection();
                                          if (!FollowPolicy.moved(saved.x, saved.z, (int)var11.centerX(), (int)var11.centerZ())
                                             && var11.radiusX() == settings().followRadius
                                             && var11.horizon() == 1.0) {
                                             var10 = (GenerationTask)var15.get();
                                          }
                                       }

                                       if (var10 == null) {
                                          Selection var16 = Selection.builder(var4, (World)var14.get())
                                             .center(saved.x.intValue(), saved.z.intValue())
                                             .radius(settings().followRadius)
                                             .radiusZ(settings().followRadius)
                                             .shape("circle")
                                             .pattern(Parameter.of("concentric"))
                                             .horizon(1.0)
                                             .build();
                                          if (var16.radiusX() > var4.getLimit()) {
                                             throw new IllegalStateException("Configured radius exceeds Chunksmith's limit");
                                          }

                                          var10 = new GenerationTask(var4, var16);
                                       }

                                       if (var4.getGenerationTasks().putIfAbsent("minecraft:overworld", var10) == null) {
                                          try {
                                             var4.getScheduler().runTask(var10);
                                          } catch (RuntimeException var12) {
                                             var4.getGenerationTasks().remove("minecraft:overworld", var10);
                                             throw var12;
                                          }

                                          lastTask = var10;
                                          appliedX = saved.x;
                                          appliedZ = saved.z;
                                          lastCenterChange = var1;
                                          requested = false;
                                          reason = "generating";
                                          LOG.info(
                                             "LOD task follows {} at {},{}; radius {}, LOD-only, {} online slots; previous LOD retained",
                                             new Object[]{settings().followPlayer, saved.x, saved.z, settings().followRadius, settings().onlineMax}
                                          );
                                       }
                                    } catch (Exception | LinkageError var13) {
                                       requested = false;
                                       manualPause = true;
                                       reason = "start-failed";
                                       LOG.error("LOD follow start failed; paused until explicit resume", var13);
                                    }
                                 }
                              } else if (!requested) {
                                 reason = "task-paused-or-completed";
                              } else if (LodHorizon.isArmed("minecraft:overworld")) {
                                 reason = "waiting-for-lod-save-protection";
                              }

                              report(var0, var4, var1);
                           }
                        } else {
                           reason = "waiting-for-tomo-position";
                           report(var0, var4, var1);
                        }
                     }
                  } else {
                     pause(var4);
                     reason = "grace-period";
                     report(var0, var4, var1);
                  }
               }
            }
         }
      }
   }

   private static void report(MinecraftServer var0, Chunksmith var1, long var2) {
      if (var2 - lastReport >= 10000L) {
         lastReport = var2;

         try {
            GenerationTask var4 = (GenerationTask)var1.getGenerationTasks().get("minecraft:overworld");
            LinkedHashMap var5 = new LinkedHashMap();
            var5.put("time", Instant.now().toString());
            var5.put("player", settings().followPlayer);
            var5.put("targetOnline", var0.getPlayerList().getPlayer(UUID.fromString(settings().followUuid)) != null);
            var5.put("centerX", saved.x);
            var5.put("centerZ", saved.z);
            var5.put("radius", settings().followRadius);
            var5.put("players", var0.getPlayerCount());
            var5.put("onlineSlots", settings().generateWhileOnline ? settings().onlineMax : 0);
            var5.put("generateWhileOnline", settings().generateWhileOnline);
            var5.put("reason", reason);
            var5.put("activeCenterX", var4 == null ? null : var4.getSelection().centerX());
            var5.put("activeCenterZ", var4 == null ? null : var4.getSelection().centerZ());
            Files.writeString(Path.of("logs/maki-lod-follow-status.json"), JSON.toJson(var5));
         } catch (Exception var6) {
            LOG.debug("Could not report LOD follow status", var6);
         }
      }
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("makilod")
                     .requires(var0x -> var0x.hasPermission(2)))
                  .then(Commands.literal("status").executes(var0x -> {
                     ((CommandSourceStack)var0x.getSource()).sendSuccess(() -> {
                        String var10001 = saved == null ? "pending" : saved.x + "," + saved.z;
                        return Component.literal("LOD follows " + settings().followPlayer + "; center " + var10001 + "; " + reason);
                     }, false);
                     return 1;
                  })))
               .then(Commands.literal("disable").executes(var0x -> {
                  if (saved == null) {
                     return 0;
                  }

                  saved.disabled = true;
                  pause(ChunksmithProvider.get());
                  save(System.currentTimeMillis());
                  return 1;
               })))
            .then(Commands.literal("resume").executes(var0x -> {
               if (saved == null) {
                  return 0;
               }

               saved.disabled = false;
               manualPause = false;
               requested = true;
               emptySince = 0L;
               save(System.currentTimeMillis());
               return 1;
            }))
      );
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
