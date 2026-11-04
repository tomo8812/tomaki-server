package maki.shiptracking;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import maki.shiptracking.mixin.ChunkMapAccess;
import maki.shiptracking.mixin.TrackedEntityAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("maki_assembly_tracking")
public final class AssemblyTracking {
   private static final Logger LOG = LoggerFactory.getLogger("MakiShipTrackingRepair");
   private static final List<AssemblyTracking.Job> JOBS = new ArrayList<>();
   private static final Map<AssemblyTracking.Pair, Long> LAST = new HashMap<>();
   private static MinecraftServer owner;
   private static long sequence;
   private static long stamp = Long.MIN_VALUE;
   private static long lastConfigRead;
   private static boolean enabled = true;

   public AssemblyTracking() {
      NeoForge.EVENT_BUS.addListener(var0 -> tick(var0.getServer()));
      NeoForge.EVENT_BUS.addListener(var0 -> {
         JOBS.clear();
         LAST.clear();
         owner = null;
      });
   }

   public static void assembled(ServerLevel var0, Set<BlockPos> var1) {
      if (enabled && !var1.isEmpty()) {
         int var2 = Integer.MAX_VALUE;
         int var3 = var2;
         int var4 = var2;
         int var5 = Integer.MIN_VALUE;
         int var6 = var5;
         int var7 = var5;

         for (BlockPos var9 : var1) {
            var2 = Math.min(var2, var9.getX());
            var3 = Math.min(var3, var9.getY());
            var4 = Math.min(var4, var9.getZ());
            var5 = Math.max(var5, var9.getX());
            var6 = Math.max(var6, var9.getY());
            var7 = Math.max(var7, var9.getZ());
         }

         AABB var14 = new AABB(var2, var3, var4, var5 + 1, var6 + 1, var7 + 1).inflate(4.0);
         HashSet var15 = new HashSet();

         for (ServerPlayer var11 : var0.players()) {
            if (var14.intersects(var11.getBoundingBox())) {
               var15.add(var11.getUUID());
            }
         }

         if (!var15.isEmpty()) {
            while (JOBS.size() > 60) {
               JOBS.removeFirst();
            }

            for (int var13 : new int[]{20, 100}) {
               JOBS.add(new AssemblyTracking.Job(var0, Set.copyOf(var15), sequence + var13, var13));
            }

            LOG.info("Assembly near {} player(s); scheduling tracked-player synchronization at 20 and 100 ticks", var15.size());
         }
      }
   }

   public static boolean repairPair(boolean var0, boolean var1, boolean var2, boolean var3) {
      return !var0 && var1 && var2 && var3;
   }

   private static void reload() {
      long var0 = System.currentTimeMillis();
      if (var0 - lastConfigRead >= 5000L) {
         lastConfigRead = var0;
         Path var2 = Path.of("config/maki-assembly-tracking.properties");

         try {
            if (!Files.exists(var2)) {
               return;
            }

            long var3 = Files.getLastModifiedTime(var2).toMillis();
            if (var3 == stamp) {
               return;
            }

            Properties var5 = new Properties();

            try (BufferedReader var6 = Files.newBufferedReader(var2)) {
               var5.load(var6);
            }

            String var12 = var5.getProperty("enabled", "true");
            if (!var12.equals("true") && !var12.equals("false")) {
               throw new IllegalArgumentException("enabled");
            }

            enabled = Boolean.parseBoolean(var12);
            stamp = var3;
            LOG.info("Assembly tracking resync enabled={}", enabled);
         } catch (Exception var11) {
            enabled = false;
            LOG.warn("Invalid assembly tracking settings; repair disabled", var11);
         }
      }
   }

   private static void tick(MinecraftServer var0) {
      if (owner != var0) {
         owner = var0;
         JOBS.clear();
         LAST.clear();
         sequence = 0L;
      }

      sequence++;
      reload();
      if (!enabled) {
         JOBS.clear();
      } else {
         Iterator var1 = JOBS.iterator();

         while (var1.hasNext()) {
            AssemblyTracking.Job var2 = (AssemblyTracking.Job)var1.next();
            if (sequence >= var2.due()) {
               var1.remove();
               int var3 = 0;
               Int2ObjectMap var4 = ((ChunkMapAccess)var2.level().getChunkSource().chunkMap).maki$entities();

               for (ServerPlayer var6 : var2.level().players()) {
                  if (var4.get(var6.getId()) instanceof TrackedEntityAccess var8) {
                     for (ServerPlayer var10 : var2.level().players()) {
                        if ((var2.affected().contains(var6.getUUID()) || var2.affected().contains(var10.getUUID()))
                           && repairPair(
                              var6 == var10, var6.level() == var10.level(), var8.maki$seenBy().contains(var10.connection), var6.isAlive() && var10.isAlive()
                           )) {
                           AssemblyTracking.Pair var11 = new AssemblyTracking.Pair(var6.getUUID(), var10.getUUID());
                           if (sequence - LAST.getOrDefault(var11, -4611686018427387904L) >= 20L) {
                              var8.maki$serverEntity().removePairing(var10);
                              var8.maki$serverEntity().addPairing(var10);
                              LAST.put(var11, sequence);
                              var3++;
                           }
                        }
                     }
                  }
               }

               LOG.info("Post-assembly synchronization: delay={} ticks, refreshed={} existing player tracking pair(s)", var2.delay(), var3);
            }
         }

         if (sequence % 200L == 0L) {
            LAST.entrySet().removeIf(var0x -> sequence - var0x.getValue() > 200L);
         }
      }
   }

   private record Job(ServerLevel level, Set<UUID> affected, long due, int delay) {
   }

   private record Pair(UUID target, UUID viewer) {
   }
}
