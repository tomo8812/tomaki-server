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
      LOG.info(
         "Maki Assembly Tracking initialized; bounded post-assembly player tracking repair is enabled by default and follows config/maki-assembly-tracking.properties."
      );
      NeoForge.EVENT_BUS.addListener(e -> tick(e.getServer()));
      NeoForge.EVENT_BUS.addListener(e -> {
         LOG.info("Maki Assembly Tracking stopping; clearing {} pending repair job(s).", JOBS.size());
         JOBS.clear();
         LAST.clear();
         owner = null;
      });
   }

   public static void assembled(ServerLevel level, Set<BlockPos> blocks) {
      if (enabled && !blocks.isEmpty()) {
         int minX = Integer.MAX_VALUE;
         int minY = minX;
         int minZ = minX;
         int maxX = Integer.MIN_VALUE;
         int maxY = maxX;
         int maxZ = maxX;

         for (BlockPos p : blocks) {
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
            minZ = Math.min(minZ, p.getZ());
            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
            maxZ = Math.max(maxZ, p.getZ());
         }

         AABB area = new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1).inflate(4.0);
         Set<UUID> affected = new HashSet<>();

         for (ServerPlayer p : level.players()) {
            if (area.intersects(p.getBoundingBox())) {
               affected.add(p.getUUID());
            }
         }

         if (!affected.isEmpty()) {
            while (JOBS.size() > 60) {
               JOBS.removeFirst();
            }

            for (int delay : new int[]{20, 100}) {
               JOBS.add(new AssemblyTracking.Job(level, Set.copyOf(affected), sequence + delay, delay));
            }

            LOG.info("Assembly near {} player(s); scheduling tracked-player synchronization at 20 and 100 ticks", affected.size());
         }
      }
   }

   public static boolean repairPair(boolean samePlayer, boolean sameLevel, boolean alreadyTracked, boolean alive) {
      return !samePlayer && sameLevel && alreadyTracked && alive;
   }

   private static void reload() {
      long now = System.currentTimeMillis();
      if (now - lastConfigRead >= 5000L) {
         lastConfigRead = now;
         Path path = Path.of("config/maki-assembly-tracking.properties");

         try {
            if (!Files.exists(path)) {
               return;
            }

            long next = Files.getLastModifiedTime(path).toMillis();
            if (next == stamp) {
               return;
            }

            Properties p = new Properties();

            try (BufferedReader r = Files.newBufferedReader(path)) {
               p.load(r);
            }

            String value = p.getProperty("enabled", "true");
            if (!value.equals("true") && !value.equals("false")) {
               throw new IllegalArgumentException("enabled");
            }

            enabled = Boolean.parseBoolean(value);
            stamp = next;
            LOG.info("Assembly tracking resync enabled={}", enabled);
         } catch (Exception e) {
            enabled = false;
            LOG.warn("Invalid assembly tracking settings; repair disabled", e);
         }
      }
   }

   private static void tick(MinecraftServer server) {
      if (owner != server) {
         owner = server;
         JOBS.clear();
         LAST.clear();
         sequence = 0L;
      }

      sequence++;
      reload();
      if (!enabled) {
         JOBS.clear();
      } else {
         Iterator<AssemblyTracking.Job> iterator = JOBS.iterator();

         while (iterator.hasNext()) {
            AssemblyTracking.Job job = iterator.next();
            if (sequence >= job.due()) {
               iterator.remove();
               int refreshed = 0;
               Int2ObjectMap<Object> map = ((ChunkMapAccess)job.level().getChunkSource().chunkMap).maki$entities();

               for (ServerPlayer target : job.level().players()) {
                  if (map.get(target.getId()) instanceof TrackedEntityAccess tracked) {
                     for (ServerPlayer viewer : job.level().players()) {
                        if ((job.affected().contains(target.getUUID()) || job.affected().contains(viewer.getUUID()))
                           && repairPair(
                              target == viewer,
                              target.level() == viewer.level(),
                              tracked.maki$seenBy().contains(viewer.connection),
                              target.isAlive() && viewer.isAlive()
                           )) {
                           AssemblyTracking.Pair pair = new AssemblyTracking.Pair(target.getUUID(), viewer.getUUID());
                           if (sequence - LAST.getOrDefault(pair, -4611686018427387904L) >= 20L) {
                              tracked.maki$serverEntity().removePairing(viewer);
                              tracked.maki$serverEntity().addPairing(viewer);
                              LAST.put(pair, sequence);
                              refreshed++;
                           }
                        }
                     }
                  }
               }

               LOG.info("Post-assembly synchronization: delay={} ticks, refreshed={} existing player tracking pair(s)", job.delay(), refreshed);
            }
         }

         if (sequence % 200L == 0L) {
            LAST.entrySet().removeIf(e -> sequence - e.getValue() > 200L);
         }
      }
   }

   private record Job(ServerLevel level, Set<UUID> affected, long due, int delay) {
   }

   private record Pair(UUID target, UUID viewer) {
   }
}
