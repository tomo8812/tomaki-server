package org.millenaire.world.sitescore;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.LevelResource;
import org.millenaire.config.MillenaireServerConfig;
import org.millenaire.culture.ModCultures;
import org.millenaire.culture.VillageType;
import org.millenaire.world.SiteValidator;
import org.millenaire.world.StructureAvoidance;
import org.millenaire.world.VillageSpawnQueue;
import org.slf4j.Logger;

public final class SiteSurvey {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final boolean ENABLED = Boolean.getBoolean("millenaire.siteSurvey");
   public static final int RADIUS = radius();
   static final int PER_TICK = 4;
   static final int LOOKS_PER_TICK = 64;
   public static final String FILE = "survey_v2.csv";
   static final String HEADER = "tick,chunk_x,chunk_z,x,z,surface_y,biome,biome_types,biome_best_type,biome_best_ratio,structure_conflict,old_verdict,old_failed,old_quota,old_min_quota,old_low,old_high,old_water,old_lava,new_verdict,near_miss,new_good,samples,min_y,max_y,height_range,centre_y,band_frac,water_frac,lava_frac,gentle1_frac,gentle2_frac,gentle3_frac,centre_usable,centre_weighted_gentle2,connected_gentle2,big_water_frac,big_water_at_edge,water_bodies,small_water_samples,big_lava_frac,large_range,large_max_slope,large_steep_frac,deepest_pit,biggest_pit_samples,ravine,hill_cut_blocks,cover_blocks,blocker_frac,connected_unblocked";
   private static final Long2ObjectOpenHashMap<List<BoundingBox>> PIECES = new Long2ObjectOpenHashMap();
   private static final LongArrayFIFOQueue WAITING = new LongArrayFIFOQueue();
   private static final LongOpenHashSet SEEN = new LongOpenHashSet();
   private static boolean seededFromFile;
   private static boolean settingsWritten;
   static final boolean PREDICT = Boolean.getBoolean("millenaire.sitePredict");
   static final String PREDICTED_FILE = "predicted_v1.csv";
   static final String PREDICTED_HEADER = predictedHeader();

   private SiteSurvey() {
   }

   private static int radius() {
      try {
         return Math.max(16, Integer.parseInt(System.getProperty("millenaire.siteSurveyRadius", "80").trim()));
      } catch (NumberFormatException e) {
         return 80;
      }
   }

   public static void onChunkLoad(ServerLevel level, ChunkPos chunk) {
      sampled(level, chunk.x, chunk.z);
      if (!seededFromFile) {
         seedFromFile(level);
      }

      long key = chunk.toLong();
      if (SEEN.add(key)) {
         Holder<Biome> centre = level.getBiome(chunk.getMiddleBlockPosition(level.getMaxBuildHeight() - 1));
         if (VillageSpawnQueue.couldHostAnyVillage(ModCultures.getAllVillageTypes().values(), centre::is)) {
            WAITING.enqueue(key);
         }
      }
   }

   private static void seedFromFile(ServerLevel level) {
      seededFromFile = true;
      Path file = level.getServer().getWorldPath(LevelResource.ROOT).resolve("millenaire-site-scores").resolve("survey_v2.csv");
      if (Files.exists(file)) {
         try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            lines.skip(1L).forEach(line -> {
               String[] c = line.split(",", 4);

               try {
                  SEEN.add(ChunkPos.asLong(Integer.parseInt(c[1]), Integer.parseInt(c[2])));
               } catch (RuntimeException var3) {
               }
            });
         } catch (IOException e) {
            LOGGER.warn("[Millenaire site survey] cannot read {}, centres may be judged twice", file, e);
         }
      }
   }

   private static boolean sampled(ServerLevel level, int cx, int cz) {
      long key = ChunkPos.asLong(cx, cz);
      if (PIECES.containsKey(key)) {
         return true;
      }

      if (SiteScoreLog.summary(level, cx, cz) == null) {
         return false;
      }

      List<BoundingBox> boxes = StructureAvoidance.avoidedPieces(level, new ChunkPos(cx, cz));
      PIECES.put(key, boxes.isEmpty() ? List.of() : boxes);
      if (PREDICT) {
         writePredicted(level, cx, cz);
      }

      return true;
   }

   private static String predictedHeader() {
      StringBuilder h = new StringBuilder("chunk_x,chunk_z");
      int n = 16;

      for (int k = 0; k < n; k++) {
         h.append(",s").append(k);
      }

      for (int k = 0; k < n; k++) {
         h.append(",f").append(k);
      }

      return h.toString();
   }

   private static void writePredicted(ServerLevel level, int cx, int cz) {
      ChunkGenerator generator = level.getChunkSource().getGenerator();
      RandomState random = level.getChunkSource().randomState();
      int side = 4;
      int[] surface = new int[side * side];
      int[] floor = new int[side * side];

      for (int j = 0; j < side; j++) {
         for (int i = 0; i < side; i++) {
            int x = (cx << 4) + 2 + 4 * i;
            int z = (cz << 4) + 2 + 4 * j;
            surface[j * side + i] = generator.getBaseHeight(x, z, Types.WORLD_SURFACE_WG, level, random);
            floor[j * side + i] = generator.getBaseHeight(x, z, Types.OCEAN_FLOOR_WG, level, random);
         }
      }

      SiteScoreLog.write(level, "predicted_v1.csv", PREDICTED_HEADER, predictedRow(cx, cz, surface, floor));
   }

   static String predictedRow(int cx, int cz, int[] surface, int[] floor) {
      StringBuilder row = new StringBuilder().append(cx).append(',').append(cz);

      for (int v : surface) {
         row.append(',').append(v);
      }

      for (int v : floor) {
         row.append(',').append(v);
      }

      return row.toString();
   }

   private static boolean windowSampled(ServerLevel level, BlockPos c) {
      int half = RADIUS + 3;

      for (int cx = c.getX() - half >> 4; cx <= c.getX() + half >> 4; cx++) {
         for (int cz = c.getZ() - half >> 4; cz <= c.getZ() + half >> 4; cz++) {
            if (!sampled(level, cx, cz)) {
               return false;
            }
         }
      }

      return true;
   }

   @Nullable
   static Boolean conflict(BlockPos c) {
      int half = RADIUS + 3;
      int minX = c.getX() - half;
      int maxX = c.getX() + half;
      int minZ = c.getZ() - half;
      int maxZ = c.getZ() + half;
      boolean found = false;

      for (int cx = minX >> 4; cx <= maxX >> 4; cx++) {
         for (int cz = minZ >> 4; cz <= maxZ >> 4; cz++) {
            List<BoundingBox> boxes = (List<BoundingBox>)PIECES.get(ChunkPos.asLong(cx, cz));
            if (boxes == null) {
               return null;
            }

            for (BoundingBox b : boxes) {
               if (StructureAvoidance.overlapsXZ(b, minX, minZ, maxX, maxZ)) {
                  found = true;
               }
            }
         }
      }

      return found;
   }

   public static void processQueue(ServerLevel level, Queue<BlockPos> candidates) {
      candidates.clear();
      int budget = 4;
      int looked = 0;
      int limit = Math.min(WAITING.size(), 64);

      while (budget > 0 && looked < limit) {
         long key = WAITING.dequeueLong();
         looked++;
         BlockPos c = new ChunkPos(key).getMiddleBlockPosition(0);
         if (!windowSampled(level, c)) {
            WAITING.enqueue(key);
         } else {
            survey(level, c);
            budget--;
         }
      }
   }

   public static void survey(ServerLevel level, BlockPos chunkCentre) {
      BlockPos probe = new BlockPos(chunkCentre.getX(), 0, chunkCentre.getZ());
      SiteMeasures m = measure(level, probe);
      int r = RADIUS;
      boolean loaded = level.hasChunksAt(probe.offset(-r, 0, -r), probe.offset(r, 0, r));
      int y = m != null ? m.centreY() : (loaded ? level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, probe.getX(), probe.getZ()) : 0);
      BlockPos pos = new BlockPos(chunkCentre.getX(), y, chunkCentre.getZ());
      Holder<Biome> centreBiome = level.getBiome(pos);
      String biome = centreBiome.unwrapKey().map(k -> k.location().toString()).orElse("");
      SiteSurvey.BiomeFit fit = biomeFit(level, pos, centreBiome);
      Boolean conflict = conflict(pos);
      SiteValidator.Breakdown old = loaded ? SiteValidator.evaluate(level, pos, RADIUS, false) : null;
      if (!settingsWritten) {
         writeSettings(level);
      }

      SiteScoreLog.write(
         level,
         "survey_v2.csv",
         "tick,chunk_x,chunk_z,x,z,surface_y,biome,biome_types,biome_best_type,biome_best_ratio,structure_conflict,old_verdict,old_failed,old_quota,old_min_quota,old_low,old_high,old_water,old_lava,new_verdict,near_miss,new_good,samples,min_y,max_y,height_range,centre_y,band_frac,water_frac,lava_frac,gentle1_frac,gentle2_frac,gentle3_frac,centre_usable,centre_weighted_gentle2,connected_gentle2,big_water_frac,big_water_at_edge,water_bodies,small_water_samples,big_lava_frac,large_range,large_max_slope,large_steep_frac,deepest_pit,biggest_pit_samples,ravine,hill_cut_blocks,cover_blocks,blocker_frac,connected_unblocked",
         row(level.getGameTime(), pos, biome, fit, conflict, old, m)
      );
   }

   static SiteSurvey.BiomeFit biomeFit(ServerLevel level, BlockPos pos, Holder<Biome> centreBiome) {
      boolean hamlets = (Boolean)MillenaireServerConfig.SERVER.generateHamlets.get();
      Map<Integer, List<Holder<Biome>>> samplesByRadius = new HashMap<>();
      int types = 0;
      String bestType = "";
      float bestRatio = 0.0F;

      for (VillageType vt : ModCultures.getAllVillageTypes().values()) {
         if (vt.weight() > 0 && (vt.loneBuilding() || hamlets || vt.hamlets().isEmpty())) {
            float ratio;
            if (vt.biomeTags().isEmpty()) {
               ratio = 1.0F;
            } else {
               if (!VillageSpawnQueue.matchesAnyTag(centreBiome, vt.biomeTags())) {
                  continue;
               }

               List<Holder<Biome>> samples = samplesByRadius.computeIfAbsent(
                  VillageSpawnQueue.biomeSampleRadius(vt), r -> VillageSpawnQueue.sampleBiomes(level, pos, r)
               );
               float validity = (float)VillageSpawnQueue.countMatching(samples, vt) / samples.size();
               float required = VillageSpawnQueue.requiredBiomeValidity(vt);
               ratio = required <= 0.0F ? 1.0F : validity / required;
            }

            if (ratio >= 1.0F) {
               types++;
            }

            if (ratio > bestRatio) {
               bestRatio = ratio;
               bestType = vt.id().toString();
            }
         }
      }

      return new SiteSurvey.BiomeFit(types, bestType, bestRatio);
   }

   @Nullable
   private static SiteMeasures measure(ServerLevel level, BlockPos pos) {
      int r = RADIUS;
      return SiteMeasures.fromSummariesCropped((cx, cz) -> {
         ChunkTerrainSummary s = SiteScoreLog.summary(level, cx, cz);
         return s == null && SiteScoreLog.outsideSquare(cx, cz, pos, r) ? ChunkTerrainSummary.filler(cx, cz) : s;
      }, pos.getX(), pos.getZ(), SiteScoreLog.windowChunks(r), r);
   }

   static String row(
      long tick,
      BlockPos pos,
      String biome,
      SiteSurvey.BiomeFit fit,
      @Nullable Boolean conflict,
      @Nullable SiteValidator.Breakdown old,
      @Nullable SiteMeasures m
   ) {
      StringBuilder row = new StringBuilder();
      row.append(tick)
         .append(',')
         .append(pos.getX() >> 4)
         .append(',')
         .append(pos.getZ() >> 4)
         .append(',')
         .append(pos.getX())
         .append(',')
         .append(pos.getZ())
         .append(',')
         .append(pos.getY())
         .append(',')
         .append(SiteScoreLog.csv(biome))
         .append(',')
         .append(fit.types())
         .append(',')
         .append(SiteScoreLog.csv(fit.bestType()))
         .append(',')
         .append(SiteScoreLog.f(fit.bestRatio()))
         .append(',')
         .append(conflict == null ? "" : conflict.toString());
      if (old == null) {
         row.append(",".repeat(8));
      } else {
         row.append(',')
            .append(old.passed() ? "PASS" : "FAIL")
            .append(',')
            .append(old.failures())
            .append(',')
            .append(SiteScoreLog.f(old.quota()))
            .append(',')
            .append(SiteScoreLog.f(old.minQuota()))
            .append(',')
            .append(old.low())
            .append(',')
            .append(old.high())
            .append(',')
            .append(old.water())
            .append(',')
            .append(old.lava());
      }

      if (m == null) {
         row.append(",,,").append(",".repeat(14)).append(",".repeat(15));
         return row.toString();
      } else {
         row.append(',')
            .append(m.passed() ? "PASS" : "FAIL")
            .append(',')
            .append(m.nearMiss())
            .append(',')
            .append(m.goodMeasures())
            .append(',')
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
            .append(SiteScoreLog.f(m.bandFraction()))
            .append(',')
            .append(SiteScoreLog.f(m.waterFraction()))
            .append(',')
            .append(SiteScoreLog.f(m.lavaFraction()))
            .append(',')
            .append(SiteScoreLog.f(m.gentle1Fraction()))
            .append(',')
            .append(SiteScoreLog.f(m.gentle2Fraction()))
            .append(',')
            .append(SiteScoreLog.f(m.gentle3Fraction()))
            .append(',')
            .append(m.centreUsable())
            .append(',')
            .append(SiteScoreLog.f(m.centreWeightedGentle2()))
            .append(',')
            .append(SiteScoreLog.f(m.connectedGentle2()));
         SiteScoreLog.appendFeatures(row, m.features());
         return row.toString();
      }
   }

   private static void writeSettings(ServerLevel level) {
      settingsWritten = true;

      try {
         Path dir = level.getServer().getWorldPath(LevelResource.ROOT).resolve("millenaire-site-scores");
         Files.createDirectories(dir);
         Files.writeString(
            dir.resolve("survey.txt"),
            "tick " + level.getGameTime() + ": radius=" + RADIUS + " " + SiteMeasures.thresholds() + System.lineSeparator(),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
         );
         LOGGER.info("[Millenaire site survey] survey mode on: nothing spawns, every chunk centre judged at radius {}", RADIUS);
      } catch (IOException e) {
         LOGGER.error("[Millenaire site survey] cannot write survey.txt", e);
      }
   }

   public static void reset() {
      PIECES.clear();
      WAITING.clear();
      SEEN.clear();
      seededFromFile = false;
      settingsWritten = false;
   }

   record BiomeFit(int types, String bestType, float bestRatio) {
   }
}
