package org.millenaire.world.sitescore;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.storage.LevelResource;
import org.millenaire.building.BuildingPlan;
import org.millenaire.building.BuildingPlanSet;
import org.millenaire.culture.ModCultures;
import org.millenaire.culture.VillageType;
import org.millenaire.world.SiteValidator;
import org.slf4j.Logger;

public final class SiteScoreLog {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final boolean ENABLED = Boolean.getBoolean("millenaire.siteScoreLog");
   private static final int WINDOW_RADIUS_CAP = 128;
   static final String CHUNKS_HEADER = "chunk_x,chunk_z,min_y,max_y,mean_y,max_step,water_samples,lava_samples,h0,h1,h2,h3,h4,h5,h6,h7,h8,h9,h10,h11,h12,h13,h14,h15,water_mask,lava_mask";
   private static final String ATTEMPTS_FILE = "attempts_v7.csv";
   private static final String ATTEMPTS_HEADER = "tick,x,z,surface_y,type,radius,lone,window_chunks,structure_conflict,old_verdict,old_failed,old_quota,old_min_quota,old_low,old_high,old_water,old_lava,new_verdict,new_good,category,dry_run,dry_run_pass,old_agrees,new_agrees,spawned,samples,min_y,max_y,height_range,centre_y,band_frac,water_frac,lava_frac,gentle1_frac,gentle2_frac,gentle3_frac,centre_usable,centre_weighted_gentle2,connected_gentle2,pad_w,pad_d,pad_usable,pad_range,pad_distance,big_water_frac,big_water_at_edge,water_bodies,small_water_samples,big_lava_frac,large_range,large_max_slope,large_steep_frac,deepest_pit,biggest_pit_samples,ravine,hill_cut_blocks,cover_blocks,blocker_frac,connected_unblocked,marked";
   static final float NEITHER_SAMPLE = neitherSample();
   private static final Random SAMPLER = new Random();
   private static final Long2ObjectOpenHashMap<ChunkTerrainSummary> CACHE = new Long2ObjectOpenHashMap();
   private static final String CHUNKS_FILE = "chunks_v3.csv";
   private static final String LABELS_FILE = "labels.csv";
   private static final String LABELS_HEADER = "tick,x,z,label,category,player";
   private static final Map<String, BufferedWriter> WRITERS = new HashMap<>();
   private static boolean ioFailed;
   static final int FEATURE_COLUMNS = 15;

   private static float neitherSample() {
      try {
         return Float.parseFloat(System.getProperty("millenaire.siteNeitherSample", "0.2"));
      } catch (NumberFormatException e) {
         return 0.2F;
      }
   }

   public static int minVillageDistance(int configured) {
      if (!ENABLED) {
         return configured;
      }

      String v = System.getProperty("millenaire.siteMinVillageDistance");
      if (v != null && !v.isBlank()) {
         try {
            return Math.max(1, Integer.parseInt(v.trim()));
         } catch (NumberFormatException e) {
            return configured;
         }
      } else {
         return configured;
      }
   }

   static boolean shouldMark(String category, float roll) {
      if (category.isEmpty()) {
         return false;
      } else {
         return "neither".equals(category) ? roll < NEITHER_SAMPLE : true;
      }
   }

   private SiteScoreLog() {
   }

   public static int windowChunks(int radius) {
      int r = Math.min(radius, 128);
      return (r + 15) / 16;
   }

   @Nullable
   public static ChunkTerrainSummary summary(ServerLevel level, int chunkX, int chunkZ) {
      long key = ChunkPos.asLong(chunkX, chunkZ);
      ChunkTerrainSummary s = (ChunkTerrainSummary)CACHE.get(key);
      if (s != null) {
         return s;
      }

      if (!level.hasChunk(chunkX, chunkZ)) {
         return null;
      }

      s = ChunkTerrainSummary.compute(level, chunkX, chunkZ);
      CACHE.put(key, s);
      writeChunk(level, s);
      return s;
   }

   @Nullable
   public static SiteMeasures measure(ServerLevel level, BlockPos centre, VillageType type, boolean lone) {
      int r = Math.min(type.radius(), 128);
      BiFunction<Integer, Integer, ChunkTerrainSummary> lookup = (cx, cz) -> {
         ChunkTerrainSummary s = summary(level, cx, cz);
         return s == null && outsideSquare(cx, cz, centre, r) ? ChunkTerrainSummary.filler(cx, cz) : s;
      };
      SiteMeasures m;
      if (!lone) {
         m = SiteMeasures.fromSummariesCropped(lookup, centre.getX(), centre.getZ(), windowChunks(r), r);
      } else {
         int[] footprint = centreFootprint(type);
         m = SiteMeasures.fromSummariesLone(lookup, centre.getX(), centre.getZ(), windowChunks(r), r, footprint[0], footprint[1]);
      }

      if (m == null) {
         LOGGER.warn(
            "[Millenaire site scores] no measures for {} at {} (radius {}): a chunk inside the radius is not loaded",
            new Object[]{type.id(), centre.toShortString(), r}
         );
      }

      return m;
   }

   public static boolean outsideSquare(int cx, int cz, BlockPos centre, int half) {
      int x0 = cx << 4;
      int x1 = x0 + 15;
      int z0 = cz << 4;
      int z1 = z0 + 15;
      return x1 < centre.getX() - half || x0 > centre.getX() + half || z1 < centre.getZ() - half || z0 > centre.getZ() + half;
   }

   static int[] centreFootprint(VillageType type) {
      int[] size = centreBuildingSize(type);
      if (size != null) {
         return size;
      }

      int side = Math.max(1, type.radius());
      return new int[]{side, side};
   }

   @Nullable
   public static int[] centreBuildingSize(VillageType type) {
      int w = 0;
      int d = 0;

      for (VillageType.LayoutSlot slot : type.layout()) {
         if ("centre".equals(slot.role()) || "townhall".equals(slot.role())) {
            BuildingPlanSet set = ModCultures.getBuildingPlanSet(slot.plan());
            if (set != null) {
               for (String variant : set.variants().keySet()) {
                  BuildingPlanSet.LevelDef l0 = set.getLevel(variant, 0);
                  if (l0 != null) {
                     w = Math.max(w, l0.width());
                     d = Math.max(d, l0.depth());
                  }
               }
            } else {
               BuildingPlan plan = ModCultures.getBuildingPlan(slot.plan());
               if (plan != null) {
                  w = Math.max(w, plan.width());
                  d = Math.max(d, plan.depth());
               }
            }
         }
      }

      return w != 0 && d != 0 ? new int[]{w, d} : null;
   }

   public static void logAttempt(
      ServerLevel level,
      BlockPos pos,
      String type,
      int radius,
      boolean lone,
      boolean structureConflict,
      @Nullable SiteValidator.Breakdown old,
      @Nullable String dryRun,
      @Nullable Boolean spawned,
      @Nullable SiteMeasures m
   ) {
      Boolean dryPass = dryRun == null ? null : "PASS".equals(dryRun);
      StringBuilder row = new StringBuilder();
      row.append(level.getGameTime())
         .append(',')
         .append(pos.getX())
         .append(',')
         .append(pos.getZ())
         .append(',')
         .append(pos.getY())
         .append(',')
         .append(csv(type))
         .append(',')
         .append(radius)
         .append(',')
         .append(lone)
         .append(',')
         .append(windowChunks(radius))
         .append(',')
         .append(structureConflict);
      if (old == null) {
         row.append(",,,,,,,,");
      } else {
         row.append(',')
            .append(old.passed() ? "PASS" : "FAIL")
            .append(',')
            .append(old.failures())
            .append(',')
            .append(f(old.quota()))
            .append(',')
            .append(f(old.minQuota()))
            .append(',')
            .append(old.low())
            .append(',')
            .append(old.high())
            .append(',')
            .append(old.water())
            .append(',')
            .append(old.lava());
      }

      row.append(',')
         .append(m == null ? "" : (m.passed() ? "PASS" : "FAIL"))
         .append(',')
         .append(m == null ? "" : m.goodMeasures())
         .append(',')
         .append(category(old, m))
         .append(',')
         .append(dryRun == null ? "" : csv(dryRun))
         .append(',')
         .append(dryPass == null ? "" : dryPass)
         .append(',')
         .append(old != null && dryPass != null ? old.passed() == dryPass : "")
         .append(',')
         .append(m != null && dryPass != null ? m.passed() == dryPass : "")
         .append(',')
         .append(spawned == null ? "" : spawned);
      if (m == null) {
         row.append(",,,,,,,,,,,,,,,,,,,").append(",".repeat(15));
      } else {
         row.append(',')
            .append(m.samples())
            .append(',')
            .append(m.minY())
            .append(',')
            .append(m.maxY())
            .append(',')
            .append(m.heightRange())
            .append(',')
            .append(m.centreY())
            .append(',')
            .append(f(m.bandFraction()))
            .append(',')
            .append(f(m.waterFraction()))
            .append(',')
            .append(f(m.lavaFraction()))
            .append(',')
            .append(f(m.gentle1Fraction()))
            .append(',')
            .append(f(m.gentle2Fraction()))
            .append(',')
            .append(f(m.gentle3Fraction()))
            .append(',')
            .append(m.centreUsable())
            .append(',')
            .append(f(m.centreWeightedGentle2()))
            .append(',')
            .append(f(m.connectedGentle2()));
         SiteMeasures.LonePad p = m.lonePad();
         if (p != null && p.distance() >= 0) {
            row.append(',')
               .append(p.padW())
               .append(',')
               .append(p.padD())
               .append(',')
               .append(f(p.bestUsable()))
               .append(',')
               .append(p.range())
               .append(',')
               .append(p.distance());
         } else {
            row.append(p == null ? ",,,,," : "," + p.padW() + "," + p.padD() + ",0.000,,");
         }

         appendFeatures(row, m.features());
      }

      String category = category(old, m);
      boolean marked = shouldMark(category, SAMPLER.nextFloat());
      row.append(',').append(marked);
      write(
         level,
         "attempts_v7.csv",
         "tick,x,z,surface_y,type,radius,lone,window_chunks,structure_conflict,old_verdict,old_failed,old_quota,old_min_quota,old_low,old_high,old_water,old_lava,new_verdict,new_good,category,dry_run,dry_run_pass,old_agrees,new_agrees,spawned,samples,min_y,max_y,height_range,centre_y,band_frac,water_frac,lava_frac,gentle1_frac,gentle2_frac,gentle3_frac,centre_usable,centre_weighted_gentle2,connected_gentle2,pad_w,pad_d,pad_usable,pad_range,pad_distance,big_water_frac,big_water_at_edge,water_bodies,small_water_samples,big_lava_frac,large_range,large_max_slope,large_steep_frac,deepest_pit,biggest_pit_samples,ravine,hill_cut_blocks,cover_blocks,blocker_frac,connected_unblocked,marked",
         row.toString()
      );
      if (marked) {
         SiteMarkers.mark(level, pos, category, dryRun, m);
      }
   }

   static void appendFeatures(StringBuilder row, @Nullable TerrainFeatures t) {
      if (t == null) {
         row.append(",".repeat(15));
      } else {
         row.append(',')
            .append(f(t.bigWaterFraction()))
            .append(',')
            .append(t.bigWaterAtEdge())
            .append(',')
            .append(t.waterBodies())
            .append(',')
            .append(t.smallWaterSamples())
            .append(',')
            .append(f(t.bigLavaFraction()))
            .append(',')
            .append(t.largeRange())
            .append(',')
            .append(t.largeMaxSlope())
            .append(',')
            .append(f(t.largeSteepFraction()))
            .append(',')
            .append(t.deepestPit())
            .append(',')
            .append(t.biggestPitSamples())
            .append(',')
            .append(t.ravine())
            .append(',')
            .append(t.hillCutBlocks())
            .append(',')
            .append(t.coverBlocks())
            .append(',')
            .append(f(t.blockerFraction()))
            .append(',')
            .append(f(t.connectedUnblocked()));
      }
   }

   static String category(@Nullable SiteValidator.Breakdown old, @Nullable SiteMeasures m) {
      if (old != null && m != null) {
         boolean o = old.passed();
         boolean n = m.passed();
         if (o && n) {
            return "both_pass";
         } else if (o) {
            return "old_only";
         } else if (n) {
            return "new_only";
         } else {
            return m.nearMiss() ? "near_miss" : "neither";
         }
      } else {
         return "";
      }
   }

   public static void logLabel(ServerLevel level, int x, int z, String label, String category, String player) {
      write(
         level,
         "labels.csv",
         "tick,x,z,label,category,player",
         level.getGameTime() + "," + x + "," + z + "," + csv(label) + "," + csv(category) + "," + csv(player)
      );
   }

   public static void reset() {
      CACHE.clear();
      WRITERS.values().forEach(SiteScoreLog::closeQuietly);
      WRITERS.clear();
      ioFailed = false;
   }

   private static void writeChunk(ServerLevel level, ChunkTerrainSummary s) {
      write(
         level,
         "chunks_v3.csv",
         "chunk_x,chunk_z,min_y,max_y,mean_y,max_step,water_samples,lava_samples,h0,h1,h2,h3,h4,h5,h6,h7,h8,h9,h10,h11,h12,h13,h14,h15,water_mask,lava_mask",
         chunkRow(s)
      );
   }

   static String chunkRow(ChunkTerrainSummary s) {
      int min = Integer.MAX_VALUE;
      int max = Integer.MIN_VALUE;
      int sum = 0;
      int step = 0;
      int side = 4;

      for (int j = 0; j < side; j++) {
         for (int i = 0; i < side; i++) {
            int y = s.height(i, j);
            min = Math.min(min, y);
            max = Math.max(max, y);
            sum += y;
            if (i > 0) {
               step = Math.max(step, Math.abs(y - s.height(i - 1, j)));
            }

            if (j > 0) {
               step = Math.max(step, Math.abs(y - s.height(i, j - 1)));
            }
         }
      }

      StringBuilder row = new StringBuilder();
      row.append(s.chunkX())
         .append(',')
         .append(s.chunkZ())
         .append(',')
         .append(min)
         .append(',')
         .append(max)
         .append(',')
         .append(f((float)sum / (side * side)))
         .append(',')
         .append(step)
         .append(',')
         .append(Integer.bitCount(s.waterMask()))
         .append(',')
         .append(Integer.bitCount(s.lavaMask()));

      for (int k = 0; k < side * side; k++) {
         row.append(',').append(s.heights()[k]);
      }

      row.append(',').append(s.waterMask()).append(',').append(s.lavaMask());
      return row.toString();
   }

   public static ChunkTerrainSummary parseChunkRow(String row) {
      String[] c = row.split(",", -1);
      int n = 16;
      int first = 8;
      if (c.length != first + n + 2) {
         throw new IllegalArgumentException("not a chunks_v3 row (" + c.length + " columns): " + row);
      }

      int[] heights = new int[n];

      for (int k = 0; k < n; k++) {
         heights[k] = Integer.parseInt(c[first + k]);
      }

      return new ChunkTerrainSummary(
         Integer.parseInt(c[0]), Integer.parseInt(c[1]), heights, Integer.parseInt(c[first + n]), Integer.parseInt(c[first + n + 1])
      );
   }

   public static void write(ServerLevel level, String fileName, String header, String row) {
      if (!ioFailed) {
         try {
            BufferedWriter out = WRITERS.get(fileName);
            if (out == null) {
               Path dir = level.getServer().getWorldPath(LevelResource.ROOT).resolve("millenaire-site-scores");
               Files.createDirectories(dir);
               Path file = dir.resolve(fileName);
               boolean fresh = !Files.exists(file);
               out = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
               if (fresh) {
                  out.write(header);
                  out.newLine();
               }

               WRITERS.put(fileName, out);
               LOGGER.info("[Millenaire site scores] writing {} ({})", file.toAbsolutePath(), SiteMeasures.thresholds());
               if (fileName.equals("attempts_v7.csv")) {
                  Files.writeString(
                     dir.resolve("thresholds.txt"),
                     "tick "
                        + level.getGameTime()
                        + ": "
                        + SiteMeasures.thresholds()
                        + " neither_sample="
                        + NEITHER_SAMPLE
                        + " min_village_distance_override="
                        + System.getProperty("millenaire.siteMinVillageDistance", "none")
                        + " measure_radius_cap=128"
                        + System.lineSeparator(),
                     StandardCharsets.UTF_8,
                     StandardOpenOption.CREATE,
                     StandardOpenOption.APPEND
                  );
               }
            }

            out.write(row);
            out.newLine();
            out.flush();
         } catch (IOException e) {
            ioFailed = true;
            LOGGER.error("[Millenaire site scores] cannot write the CSV log, logging disabled", e);
         }
      }
   }

   public static String f(float v) {
      return String.format(Locale.ROOT, "%.3f", v);
   }

   public static String csv(String s) {
      return s.indexOf(44) < 0 && s.indexOf(34) < 0 && s.indexOf(10) < 0 ? s : "\"" + s.replace("\"", "\"\"").replace('\n', ' ') + "\"";
   }

   private static void closeQuietly(@Nullable BufferedWriter w) {
      if (w != null) {
         try {
            w.close();
         } catch (IOException var2) {
         }
      }
   }
}
