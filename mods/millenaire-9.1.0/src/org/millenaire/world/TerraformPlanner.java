package org.millenaire.world;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import org.millenaire.building.BuildingInstance;
import org.millenaire.building.BuildingPlan;
import org.millenaire.building.ClearMargins;

public final class TerraformPlanner {
   public static final byte FREE = 0;
   public static final byte SOFT = 1;
   public static final byte HARD = 2;
   static final byte WATER = 3;
   static final int ENCLOSURE_RADIUS = 6;
   static final double MIN_ENCLOSURE = 0.5;
   private static final int PAD_CORNER_RADIUS = 3;
   private static final double SHORE_RIM_MAX = 5.5;
   private static final double SHORE_FILLET = 12.0;
   private static final int SHORE_WAVELENGTH = 7;
   private static final long SHORE_SEED = 103379420427180L;
   private static final int MAX_RAMP_ATTEMPTS = 24;
   private static final int RAMP_EARTH_WEIGHT = 2;
   private static final int RAMP_HEIGHT_SPAN = 30;
   private static final double WALL_FACE_WEIGHT = 4.0;

   private TerraformPlanner() {
   }

   public static TerraformPlanner.Result plan(
      VillageTerrainMap map,
      BuildingPlan plan,
      BlockPos villageCenter,
      PlacementConstraints constraints,
      ClearMargins margins,
      List<BuildingInstance> existingBuildings,
      @Nullable byte[][] protection,
      TerraformPlanner.Settings settings
   ) {
      long start = System.nanoTime();
      int sizeX = map.getSizeX();
      int sizeZ = map.getSizeZ();
      int originX = map.getOriginX();
      int originZ = map.getOriginZ();
      int baseline = map.getBaseline();
      TerraformPlanner.Extensions ext = settings.extensions();
      int minPad = baseline - ext.padGate();
      int maxPad = baseline + ext.padGate();
      byte[][] fixed = computeProtection(map, protection, ext);
      float[][] landDist = ext.fillWater() ? landDistance(map) : null;
      int stride1 = sizeZ + 1;
      long[] satH = new long[(sizeX + 1) * stride1];
      long[] satH2 = new long[(sizeX + 1) * stride1];
      int[] satFixed = new int[(sizeX + 1) * stride1];
      int[] satOccupied = new int[(sizeX + 1) * stride1];

      for (int lx = 0; lx < sizeX; lx++) {
         for (int lz = 0; lz < sizeZ; lz++) {
            long h = map.getTopGround(lx, lz);
            int i = (lx + 1) * stride1 + lz + 1;
            int up = lx * stride1 + lz + 1;
            int left = (lx + 1) * stride1 + lz;
            int diag = lx * stride1 + lz;
            satH[i] = h + satH[up] + satH[left] - satH[diag];
            satH2[i] = h * h + satH2[up] + satH2[left] - satH2[diag];
            satFixed[i] = (fixed[lx][lz] == 2 ? 1 : 0) + satFixed[up] + satFixed[left] - satFixed[diag];
            satOccupied[i] = (map.isOccupied(lx, lz) ? 1 : 0) + satOccupied[up] + satOccupied[left] - satOccupied[diag];
         }
      }

      int cx = villageCenter.getX();
      int cz = villageCenter.getZ();
      int minDist = constraints.minDistance();
      int maxDist = constraints.maxDistance();
      List<TerraformPlanner.Candidate> screened = new ArrayList<>();
      int scanned = 0;

      for (int nwX = cx - maxDist; nwX <= cx + maxDist; nwX += settings.stride()) {
         for (int nwZ = cz - maxDist; nwZ <= cz + maxDist; nwZ += settings.stride()) {
            int cheb = Math.max(Math.abs(nwX - cx), Math.abs(nwZ - cz));
            if (cheb >= minDist && cheb <= maxDist) {
               scanned++;
               PlacedLocation at = BuildingLocationFinder.candidateForCorner(nwX, nwZ, plan, constraints, villageCenter);
               Rotation rotation = at.rotation();
               BlockPos pivot = at.position();
               ClearMargins tested = PlotApron.margins(map, pivot.getX(), pivot.getZ(), plan.width(), plan.depth(), margins, rotation);
               if (tested != null) {
                  VillageTerrainMap.FootprintRect rect = VillageTerrainMap.computeFootprintRect(
                     pivot.getX(), pivot.getZ(), plan.width(), plan.depth(), tested, rotation
                  );
                  int lx0 = rect.startX() - originX;
                  int lz0 = rect.startZ() - originZ;
                  int lx1 = lx0 + rect.width();
                  int lz1 = lz0 + rect.depth();
                  if (lx0 >= 0 && lz0 >= 0 && lx1 <= sizeX && lz1 <= sizeZ) {
                     int n = rect.width() * rect.depth();
                     if (sat(satOccupied, stride1, lx0, lz0, lx1, lz1) <= 0 && sat(satFixed, stride1, lx0, lz0, lx1, lz1) * 4 <= n) {
                        long s1 = sat(satH, stride1, lx0, lz0, lx1, lz1);
                        long s2 = sat(satH2, stride1, lx0, lz0, lx1, lz1);
                        int padY = clamp((int)Math.round((double)s1 / n), minPad, maxPad);
                        double sq = s2 - 2.0 * padY * s1 + (double)n * padY * padY;
                        double volumeProxy = Math.sqrt(Math.max(0.0, n * sq));
                        if (!(volumeProxy > settings.maxVolume() * 2.0)) {
                           screened.add(
                              new TerraformPlanner.Candidate(nwX, nwZ, pivot, rotation, rect, padY, cheb, volumeProxy + settings.distanceWeight() * cheb)
                           );
                        }
                     }
                  }
               }
            }
         }
      }

      screened.sort(Comparator.comparingDouble(TerraformPlanner.Candidate::score));
      TerraformPlanner.Counters counters = new TerraformPlanner.Counters();
      List<TerraformPlanner.Plan> exact = new ArrayList<>();
      int evaluated = 0;

      for (TerraformPlanner.Candidate c : screened) {
         if (evaluated >= settings.exactBudget()) {
            break;
         }

         evaluated++;
         int[] rim = rimQuartiles(map, c.rect());
         int[] heights = new int[]{c.padY(), rim[1], rim[0], rim[2]};

         for (int k = 0; k < heights.length; k++) {
            int padY = clamp(heights[k], minPad, maxPad);
            boolean seen = false;

            for (int j = 0; j < k; j++) {
               seen |= clamp(heights[j], minPad, maxPad) == padY;
            }

            if (!seen) {
               if (BuildingLocationFinder.findNeighbourCliff(map, c.pivot().getX(), c.pivot().getZ(), plan.width(), plan.depth(), c.rotation(), padY) != null) {
                  veto(counters, TerraformPlanner.Veto.CLIFF);
               } else {
                  TerraformPlanner.Plan p = adaptive(map, c, padY, fixed, landDist, settings, originX, originZ, counters);
                  if (p != null) {
                     exact.add(p);
                  }
               }
            }
         }
      }

      exact.sort(Comparator.comparingDouble(TerraformPlanner.Plan::score));
      TerrainReachability villageReach = exact.isEmpty() ? null : TerrainReachability.compute(map, villageCenter);
      int suppression = Math.max(3, settings.stride() * 2);
      List<TerraformPlanner.Plan> tried = new ArrayList<>();
      int validated = 0;
      String firstFailure = null;
      TerraformPlanner.Plan bestRejected = null;
      VillageTerrainMap scratch = exact.isEmpty() ? null : map.copy();
      int ramps = 0;

      for (TerraformPlanner.Plan candidate : exact) {
         TerraformPlanner.Plan p = candidate;
         if (validated >= settings.maxValidations()) {
            break;
         }

         boolean tooClose = false;

         for (TerraformPlanner.Plan k : tried) {
            if (k.padY() == p.padY() && Math.max(Math.abs(k.nwX() - p.nwX()), Math.abs(k.nwZ() - p.nwZ())) < suppression) {
               tooClose = true;
               break;
            }
         }

         if (!tooClose) {
            tried.add(p);
            TerraformPlanner.Validation v = validate(
               scratch, p, plan, villageCenter, constraints, margins, existingBuildings, originX, originZ, villageReach, counters
            );
            if (v.location() == null
               && settings.extensions().wallHeight() > 0
               && ramps < 24
               && (v.failure().startsWith("local:") || v.failure().equals("not reachable from the town hall"))) {
               ramps++;
               TerraformPlanner.Plan ramped = withAccessRamp(map, p, fixed, villageReach, settings);
               if (ramped != null) {
                  TerraformPlanner.Validation rv = validate(
                     scratch, ramped, plan, villageCenter, constraints, margins, existingBuildings, originX, originZ, villageReach, counters
                  );
                  if (rv.location() != null || !rv.failure().startsWith("local:")) {
                     p = ramped;
                     v = rv;
                  }
               }
            }

            if (v.failure() == null || !v.failure().startsWith("local:")) {
               validated++;
            }

            PlacedLocation location = v.location();
            if (location == null && firstFailure == null) {
               firstFailure = v.failure();
               bestRejected = p;
            }

            if (location != null) {
               TerraformPlanner.Plan accepted = new TerraformPlanner.Plan(
                  new PlacedLocation(new BlockPos(location.position().getX(), p.padY(), location.position().getZ()), location.rotation()),
                  p.nwX(),
                  p.nwZ(),
                  p.padY(),
                  p.regionMinX(),
                  p.regionMinZ(),
                  p.sizeX(),
                  p.sizeZ(),
                  p.before(),
                  p.after(),
                  p.pad(),
                  p.floor(),
                  p.cutVolume(),
                  p.fillVolume(),
                  p.modifiedColumns(),
                  p.distance(),
                  p.cutSlope(),
                  p.fillSlope(),
                  p.cutWall(),
                  p.fillWall(),
                  p.rampColumns(),
                  p.gate(),
                  p.waterColumns(),
                  p.score()
               );
               return new TerraformPlanner.Result(accepted, stats(scanned, screened.size(), evaluated, counters, validated, start), "ok", null);
            }
         }
      }

      TerraformPlanner.Stats stats = stats(scanned, screened.size(), evaluated, counters, validated, start);
      String reason;
      if (screened.isEmpty()) {
         reason = "no candidate footprint inside the search band";
      } else if (exact.isEmpty()) {
         reason = "every candidate site needs too much earthwork or touches protected ground";
      } else {
         reason = "no reshaped site passes the location finder (best: " + firstFailure + ")";
      }

      return new TerraformPlanner.Result(null, stats, reason, bestRejected);
   }

   @Nullable
   public static PlacedLocation revalidate(
      VillageTerrainMap map,
      BuildingPlan plan,
      PlacedLocation planned,
      BlockPos villageCenter,
      PlacementConstraints constraints,
      ClearMargins margins,
      List<BuildingInstance> existingBuildings,
      @Nullable TerrainReachability reachability
   ) {
      return revalidate(map, plan, planned, villageCenter, constraints, margins, existingBuildings, reachability, TerraformPlanner.Extensions.LEGACY.padGate());
   }

   @Nullable
   public static PlacedLocation revalidate(
      VillageTerrainMap map,
      BuildingPlan plan,
      PlacedLocation planned,
      BlockPos villageCenter,
      PlacementConstraints constraints,
      ClearMargins margins,
      List<BuildingInstance> existingBuildings,
      @Nullable TerrainReachability reachability,
      int gate
   ) {
      map.relaxAltitudeGate(
         VillageTerrainMap.computeFootprintRect(planned.position().getX(), planned.position().getZ(), plan.width(), plan.depth(), margins, planned.rotation()),
         gate
      );
      return BuildingLocationFinder.tryPivot(
         planned.position().getX(),
         planned.position().getZ(),
         planned.rotation(),
         map,
         plan,
         villageCenter,
         constraints,
         margins,
         existingBuildings,
         reachability
      );
   }

   private static TerraformPlanner.Stats stats(int scanned, int screened, int evaluated, TerraformPlanner.Counters c, int validated, long start) {
      return new TerraformPlanner.Stats(
         scanned,
         screened,
         evaluated,
         c.vetoes[TerraformPlanner.Veto.PROTECTED.ordinal()],
         c.vetoes[TerraformPlanner.Veto.VOLUME.ordinal()],
         c.vetoes[TerraformPlanner.Veto.REACH.ordinal()],
         c.vetoes[TerraformPlanner.Veto.NOOP.ordinal()],
         c.vetoes[TerraformPlanner.Veto.CLIFF.ordinal()],
         c.vetoes[TerraformPlanner.Veto.POCKET.ordinal()],
         c.vetoes[TerraformPlanner.Veto.SHORE.ordinal()],
         validated,
         System.nanoTime() - start
      );
   }

   public static void protectFootprint(
      byte[][] mask, VillageTerrainMap map, int pivotX, int pivotZ, int width, int depth, ClearMargins margins, Rotation rotation, boolean complete
   ) {
      raise(mask, map, VillageTerrainMap.computeFootprintRect(pivotX, pivotZ, width, depth, margins, rotation), (byte)(complete ? 1 : 2));
      raise(mask, map, VillageTerrainMap.computeFootprintRect(pivotX, pivotZ, width, depth, ClearMargins.symmetric(1), rotation), (byte)2);
   }

   private static void raise(byte[][] mask, VillageTerrainMap map, VillageTerrainMap.FootprintRect rect, byte level) {
      for (int dx = 0; dx < rect.width(); dx++) {
         for (int dz = 0; dz < rect.depth(); dz++) {
            int lx = map.toLocalX(rect.startX() + dx);
            int lz = map.toLocalZ(rect.startZ() + dz);
            if (map.inBounds(lx, lz) && mask[lx][lz] < level) {
               mask[lx][lz] = level;
            }
         }
      }
   }

   static byte[][] computeProtection(VillageTerrainMap map, @Nullable byte[][] protection) {
      return computeProtection(map, protection, TerraformPlanner.Extensions.LEGACY);
   }

   static byte[][] computeProtection(VillageTerrainMap map, @Nullable byte[][] protection, TerraformPlanner.Extensions ext) {
      int sizeX = map.getSizeX();
      int sizeZ = map.getSizeZ();
      int baseline = map.getBaseline();
      int range = map.getScanRange();
      boolean[][] reclaimable = ext.fillWater() ? reclaimableWater(map, ext) : null;
      byte[][] fixed = new byte[sizeX][sizeZ];

      for (int lx = 0; lx < sizeX; lx++) {
         for (int lz = 0; lz < sizeZ; lz++) {
            int h = map.getTopGround(lx, lz);
            boolean water = map.isWaterAt(lx, lz);
            boolean hard = map.isOccupied(lx, lz)
               || map.isBuildingForbiddenAt(lx, lz)
               || map.isDangerAt(lx, lz)
               || h <= baseline - range
               || h >= baseline + range;
            byte caller = protection != null ? protection[lx][lz] : 0;
            if (hard || caller == 2) {
               fixed[lx][lz] = 2;
            } else if (water) {
               fixed[lx][lz] = (byte)(reclaimable != null && reclaimable[lx][lz] ? 3 : 2);
            } else if (isBank(map, lx, lz)) {
               fixed[lx][lz] = ext.fillWater() ? caller : 2;
            } else {
               fixed[lx][lz] = caller;
            }
         }
      }

      return fixed;
   }

   static boolean[][] reclaimableWater(VillageTerrainMap map, TerraformPlanner.Extensions ext) {
      int sizeX = map.getSizeX();
      int sizeZ = map.getSizeZ();
      int stride = sizeZ + 1;
      int[] satDry = new int[(sizeX + 1) * stride];

      for (int x = 0; x < sizeX; x++) {
         for (int z = 0; z < sizeZ; z++) {
            satDry[(x + 1) * stride + z + 1] = (map.isWaterAt(x, z) ? 0 : 1)
               + satDry[x * stride + z + 1]
               + satDry[(x + 1) * stride + z]
               - satDry[x * stride + z];
         }
      }

      boolean[][] out = new boolean[sizeX][sizeZ];

      for (int x = 0; x < sizeX; x++) {
         for (int z = 0; z < sizeZ; z++) {
            if (map.isWaterAt(x, z) && waterDepth(map, x, z) <= ext.maxWaterDepth()) {
               if (ext.openWater()) {
                  out[x][z] = true;
               } else {
                  int x0 = Math.max(0, x - 6);
                  int z0 = Math.max(0, z - 6);
                  int x1 = Math.min(sizeX, x + 6 + 1);
                  int z1 = Math.min(sizeZ, z + 6 + 1);
                  int dry = satDry[x1 * stride + z1] - satDry[x0 * stride + z1] - satDry[x1 * stride + z0] + satDry[x0 * stride + z0];
                  out[x][z] = dry >= 0.5 * (x1 - x0) * (z1 - z0);
               }
            }
         }
      }

      return out;
   }

   static boolean fillsIn(VillageTerrainMap map, int minX, int minZ, int sizeX, int sizeZ, int[] before, int[] after) {
      int n = sizeX * sizeZ;
      boolean[] wetBefore = new boolean[n];
      boolean[] wetAfter = new boolean[n];

      for (int dx = 0; dx < sizeX; dx++) {
         for (int dz = 0; dz < sizeZ; dz++) {
            int i = dx * sizeZ + dz;
            wetBefore[i] = map.isWaterAt(map.toLocalX(minX + dx), map.toLocalZ(minZ + dz));
            wetAfter[i] = wetBefore[i] && after[i] <= before[i];
         }
      }

      int[] bodyBefore = label(wetBefore, sizeX, sizeZ);
      int[] bodyAfter = label(wetAfter, sizeX, sizeZ);
      Map<Integer, Integer> joined = new HashMap<>();

      for (int dx = 0; dx < sizeX; dx++) {
         for (int dz = 0; dz < sizeZ; dz++) {
            boolean border = dx == 0 || dz == 0 || dx == sizeX - 1 || dz == sizeZ - 1;
            int i = dx * sizeZ + dz;
            if (border && wetAfter[i]) {
               Integer seen = joined.putIfAbsent(bodyBefore[i], bodyAfter[i]);
               if (seen != null && seen != bodyAfter[i]) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private static int[] label(boolean[] wet, int sizeX, int sizeZ) {
      int[] labels = new int[wet.length];
      int[] queue = new int[wet.length];
      int next = 0;

      for (int start = 0; start < wet.length; start++) {
         if (wet[start] && labels[start] == 0) {
            next++;
            int head = 0;
            int tail = 0;
            labels[start] = next;
            queue[tail++] = start;

            while (head < tail) {
               int i = queue[head++];
               int dx = i / sizeZ;
               int dz = i % sizeZ;
               if (dx > 0) {
                  tail = visit(wet, labels, queue, tail, i - sizeZ, next);
               }

               if (dx < sizeX - 1) {
                  tail = visit(wet, labels, queue, tail, i + sizeZ, next);
               }

               if (dz > 0) {
                  tail = visit(wet, labels, queue, tail, i - 1, next);
               }

               if (dz < sizeZ - 1) {
                  tail = visit(wet, labels, queue, tail, i + 1, next);
               }
            }
         }
      }

      return labels;
   }

   private static int visit(boolean[] wet, int[] labels, int[] queue, int tail, int j, int label) {
      if (wet[j] && labels[j] == 0) {
         labels[j] = label;
         queue[tail++] = j;
      }

      return tail;
   }

   static double smoothMin(double a, double b, double k) {
      double h = Math.max(k - Math.abs(a - b), 0.0) / k;
      return Math.min(a, b) - h * h * k * 0.25;
   }

   static float[][] landDistance(VillageTerrainMap map) {
      int sizeX = map.getSizeX();
      int sizeZ = map.getSizeZ();
      int inf = 1073741823;
      int[][] d = new int[sizeX][sizeZ];

      for (int x = 0; x < sizeX; x++) {
         for (int z = 0; z < sizeZ; z++) {
            d[x][z] = map.isWaterAt(x, z) ? inf : 0;
         }
      }

      for (int x = 0; x < sizeX; x++) {
         for (int z = 0; z < sizeZ; z++) {
            int v = d[x][z];
            if (x > 0) {
               v = Math.min(v, d[x - 1][z] + 3);
            }

            if (z > 0) {
               v = Math.min(v, d[x][z - 1] + 3);
            }

            if (x > 0 && z > 0) {
               v = Math.min(v, d[x - 1][z - 1] + 4);
            }

            if (x > 0 && z < sizeZ - 1) {
               v = Math.min(v, d[x - 1][z + 1] + 4);
            }

            d[x][z] = v;
         }
      }

      for (int x = sizeX - 1; x >= 0; x--) {
         for (int z = sizeZ - 1; z >= 0; z--) {
            int v = d[x][z];
            if (x < sizeX - 1) {
               v = Math.min(v, d[x + 1][z] + 3);
            }

            if (z < sizeZ - 1) {
               v = Math.min(v, d[x][z + 1] + 3);
            }

            if (x < sizeX - 1 && z < sizeZ - 1) {
               v = Math.min(v, d[x + 1][z + 1] + 4);
            }

            if (x < sizeX - 1 && z > 0) {
               v = Math.min(v, d[x + 1][z - 1] + 4);
            }

            d[x][z] = v;
         }
      }

      float[][] out = new float[sizeX][sizeZ];

      for (int x = 0; x < sizeX; x++) {
         for (int z = 0; z < sizeZ; z++) {
            out[x][z] = d[x][z] >= inf ? Float.MAX_VALUE : d[x][z] / 3.0F;
         }
      }

      return out;
   }

   static boolean isBank(VillageTerrainMap map, int lx, int lz) {
      return !map.isWaterAt(lx, lz) && (map.isWaterAt(lx - 1, lz) || map.isWaterAt(lx + 1, lz) || map.isWaterAt(lx, lz - 1) || map.isWaterAt(lx, lz + 1));
   }

   static int waterDepth(VillageTerrainMap map, int lx, int lz) {
      return map.getTopGround(lx, lz) + 1 - map.getWaterFloor(lx, lz);
   }

   @Nullable
   private static TerraformPlanner.Plan adaptive(
      VillageTerrainMap map,
      TerraformPlanner.Candidate c,
      int padY,
      byte[][] fixed,
      @Nullable float[][] landDist,
      TerraformPlanner.Settings s,
      int originX,
      int originZ,
      TerraformPlanner.Counters counters
   ) {
      double cutSlope = s.cutSlope();
      double fillSlope = s.fillSlope();
      int cutWall = 0;
      int fillWall = 0;
      TerraformPlanner.Extensions ext = s.extensions();
      TerraformPlanner.Plan p = null;

      for (int attempt = 0; attempt < 5; attempt++) {
         p = buildTarget(map, c, padY, fixed, landDist, s, cutSlope, fillSlope, cutWall, fillWall, originX, originZ, counters);
         if (p != null || counters.lastVeto != TerraformPlanner.Veto.REACH) {
            break;
         }

         if (counters.reachOnCut && cutSlope < s.steepCutSlope()) {
            cutSlope = s.steepCutSlope();
         } else if (counters.reachOnCut && cutWall < ext.wallHeight()) {
            cutWall = ext.wallHeight();
         } else if (!counters.reachOnCut && fillSlope < s.steepFillSlope()) {
            fillSlope = s.steepFillSlope();
         } else {
            if (counters.reachOnCut || fillWall >= ext.fillWallHeight()) {
               break;
            }

            fillWall = ext.fillWallHeight();
         }
      }

      return p;
   }

   static int[] rimQuartiles(VillageTerrainMap map, VillageTerrainMap.FootprintRect rect) {
      int x0 = rect.startX() - 1;
      int z0 = rect.startZ() - 1;
      int x1 = rect.startX() + rect.width();
      int z1 = rect.startZ() + rect.depth();
      int[] heights = new int[2 * (rect.width() + rect.depth()) + 4];
      int n = 0;

      for (int wx = x0; wx <= x1; wx++) {
         for (int wz = z0; wz <= z1; wz++) {
            if (wx == x0 || wx == x1 || wz == z0 || wz == z1) {
               int lx = map.toLocalX(wx);
               int lz = map.toLocalZ(wz);
               if (map.inBounds(lx, lz)) {
                  heights[n++] = map.getTopGround(lx, lz);
               }
            }
         }
      }

      if (n == 0) {
         return new int[]{map.getBaseline(), map.getBaseline(), map.getBaseline()};
      }

      Arrays.sort(heights, 0, n);
      return new int[]{heights[n / 4], heights[n / 2], heights[3 * n / 4]};
   }

   @Nullable
   private static TerraformPlanner.Plan buildTarget(
      VillageTerrainMap map,
      TerraformPlanner.Candidate c,
      int padY,
      byte[][] fixed,
      @Nullable float[][] landDist,
      TerraformPlanner.Settings s,
      double cutSlope,
      double fillSlope,
      int cutWall,
      int fillWall,
      int originX,
      int originZ,
      TerraformPlanner.Counters counters
   ) {
      counters.lastVeto = null;
      VillageTerrainMap.FootprintRect rect = c.rect();
      int padMinX = rect.startX();
      int padMaxX = rect.startX() + rect.width() - 1;
      int padMinZ = rect.startZ();
      int padMaxZ = rect.startZ() + rect.depth() - 1;
      int corner = Math.min(3, Math.min(rect.width(), rect.depth()) / 2);
      int reach = s.maxReach() + 1;
      int minX = Math.max(padMinX - reach, originX);
      int minZ = Math.max(padMinZ - reach, originZ);
      int maxX = Math.min(padMaxX + reach, originX + map.getSizeX() - 1);
      int maxZ = Math.min(padMaxZ + reach, originZ + map.getSizeZ() - 1);
      int sizeX = maxX - minX + 1;
      int sizeZ = maxZ - minZ + 1;
      int[] before = new int[sizeX * sizeZ];
      int[] after = new int[sizeX * sizeZ];
      int[] floor = new int[sizeX * sizeZ];
      boolean[] pad = new boolean[sizeX * sizeZ];
      int cut = 0;
      int fill = 0;
      int soft = 0;
      int away = 0;
      int modified = 0;
      int waterColumns = 0;
      int baseline = map.getBaseline();
      TerraformPlanner.Extensions ext = s.extensions();

      for (int wx = minX; wx <= maxX; wx++) {
         int ex = Math.max(0, Math.max(padMinX + corner - wx, wx - (padMaxX - corner)));

         for (int wz = minZ; wz <= maxZ; wz++) {
            int ez = Math.max(0, Math.max(padMinZ + corner - wz, wz - (padMaxZ - corner)));
            int lx = wx - originX;
            int lz = wz - originZ;
            int i = (wx - minX) * sizeZ + (wz - minZ);
            int h = map.getTopGround(lx, lz);
            before[i] = h;
            double d = Math.max(0.0, Math.sqrt((double)ex * ex + (double)ez * ez) - corner);
            int t;
            if (d == 0.0) {
               t = padY;
               pad[i] = true;
            } else {
               double dj = Math.max(0.0, d - s.jitter() * valueNoise(s.seed(), wx, wz, s.noiseWavelength()));
               pad[i] = dj == 0.0;
               double lo = padY - (dj > 0.0 ? fillWall : 0) - fillSlope * dj;
               double hi = padY + (dj > 0.0 ? cutWall : 0) + cutSlope * dj;
               if (h < lo) {
                  t = (int)Math.floor(lo + 0.5);
               } else if (h > hi) {
                  t = (int)Math.floor(hi + 0.5);
               } else {
                  t = h;
               }

               if (t != h && d > s.maxReach()) {
                  counters.reachOnCut = t < h;
                  return veto(counters, TerraformPlanner.Veto.REACH);
               }
            }

            boolean wet = map.isWaterAt(lx, lz);
            if (wet && t <= h && d > 0.0 && ext.fillWater() && fixed[lx][lz] == 3 && landDist != null) {
               double rim = 5.5 * (0.3 + 0.7 * valueNoise(s.seed() ^ 103379420427180L, wx, wz, 7));
               if (smoothMin(d - rim, landDist[lx][lz], 12.0) <= 0.0) {
                  t = h + 1;
               }
            }

            after[i] = t;
            floor[i] = wet ? map.getWaterFloor(lx, lz) : h;
            if (ext.fillWater() && wet && pad[i] && t <= h) {
               return veto(counters, TerraformPlanner.Veto.PROTECTED);
            }

            if (t != h) {
               byte level = fixed[lx][lz];
               if (level == 2 || level == 1 && Math.abs(t - h) > s.softTolerance() || level == 3 && t < h || ext.fillWater() && t < h && isBank(map, lx, lz)) {
                  return veto(counters, TerraformPlanner.Veto.PROTECTED);
               }

               if (level == 1) {
                  soft += Math.abs(t - h);
               }

               int base = level == 3 ? floor[i] : h;
               if (level == 3) {
                  waterColumns++;
               }

               away += t < h ? Math.max(0, Math.min(h, baseline) - t) : Math.max(0, t - Math.max(base, baseline));
               if (lx == 0 || lz == 0 || lx == map.getSizeX() - 1 || lz == map.getSizeZ() - 1) {
                  counters.reachOnCut = t < h;
                  return veto(counters, TerraformPlanner.Veto.REACH);
               }

               modified++;
               if (t < h) {
                  cut += h - t;
               } else {
                  fill += t - base;
               }

               if (cut + fill > s.maxVolume()) {
                  return veto(counters, TerraformPlanner.Veto.VOLUME);
               }
            }
         }
      }

      if (modified == 0) {
         return veto(counters, TerraformPlanner.Veto.NOOP);
      }

      if (waterColumns > 0 && !fillsIn(map, minX, minZ, sizeX, sizeZ, before, after)) {
         return veto(counters, TerraformPlanner.Veto.SHORE);
      }

      double score = cut + fill
         + (s.softWeight() - 1.0) * soft
         + s.awayWeight() * away
         + s.imbalanceWeight() * Math.abs(cut - fill)
         + s.distanceWeight() * c.distance();
      if (cutWall > 0 || fillWall > 0) {
         score += 4.0 * wallFaces(sizeX, sizeZ, before, after);
      }

      BlockPos location = new BlockPos(c.pivot().getX(), padY, c.pivot().getZ());
      return new TerraformPlanner.Plan(
         new PlacedLocation(location, c.rotation()),
         c.nwX(),
         c.nwZ(),
         padY,
         minX,
         minZ,
         sizeX,
         sizeZ,
         before,
         after,
         pad,
         floor,
         cut,
         fill,
         modified,
         c.distance(),
         cutSlope,
         fillSlope,
         cutWall,
         fillWall,
         0,
         ext.padGate(),
         waterColumns,
         score
      );
   }

   @Nullable
   static TerraformPlanner.Plan withAccessRamp(
      VillageTerrainMap map, TerraformPlanner.Plan p, byte[][] fixed, TerrainReachability villageReach, TerraformPlanner.Settings s
   ) {
      TerraformPlanner.Extensions ext = s.extensions();
      int sx = p.sizeX();
      int sz = p.sizeZ();
      int span = 61;
      int base = p.padY() - 30;
      int[] dist = new int[sx * sz * span];
      int[] prev = new int[dist.length];
      Arrays.fill(dist, Integer.MAX_VALUE);
      PriorityQueue<Long> queue = new PriorityQueue<>();

      for (int i = 0; i < sx * sz; i++) {
         if (p.pad()[i]) {
            int st = i * span + 30;
            dist[st] = 0;
            prev[st] = -1;
            queue.add((long)st);
         }
      }

      int landing = -1;
      int landingFrom = -1;
      int[][] dirs = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

      label138:
      while (!queue.isEmpty()) {
         long e = queue.poll();
         int cost = (int)(e >>> 32);
         int st = (int)e;
         if (cost <= dist[st]) {
            int i = st / span;
            int r = base + st % span;
            int dx = i / sz;
            int dz = i % sz;

            for (int[] dir : dirs) {
               int ndx = dx + dir[0];
               int ndz = dz + dir[1];
               if (ndx >= 0 && ndz >= 0 && ndx < sx && ndz < sz) {
                  int n = ndx * sz + ndz;
                  if (!p.pad()[n]) {
                     int hn = p.after()[n];
                     int r2 = clamp(hn, r - 1, r + 1);
                     int lx = map.toLocalX(p.regionMinX() + ndx);
                     int lz = map.toLocalZ(p.regionMinZ() + ndz);
                     if (!map.isDangerAt(lx, lz)) {
                        int orig = p.before()[n];
                        if (r2 == hn && hn == orig && !map.isWaterAt(lx, lz) && villageReach.isReachable(p.regionMinX() + ndx, p.regionMinZ() + ndz)) {
                           landing = n;
                           landingFrom = st;
                           break label138;
                        }

                        if (ndx != 0
                           && ndz != 0
                           && ndx != sx - 1
                           && ndz != sz - 1
                           && r2 >= base
                           && r2 < base + span
                           && hn - r2 <= ext.wallHeight()
                           && r2 - hn <= ext.fillWallHeight()
                           && (r2 == orig || rampMayReshape(map, fixed, lx, lz, orig, r2, s))) {
                           int ns = n * span + (r2 - base);
                           int nc = cost + 1 + 2 * Math.abs(r2 - hn);
                           if (nc < dist[ns]) {
                              dist[ns] = nc;
                              prev[ns] = st;
                              queue.add((long)nc << 32 | ns);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      if (landing < 0) {
         return null;
      }

      int[] after = (int[])p.after().clone();
      int rampColumns = 0;

      for (int st = landingFrom; st >= 0 && !p.pad()[st / span]; st = prev[st]) {
         int i = st / span;
         int r = base + st % span;
         if (after[i] != r) {
            after[i] = r;
            rampColumns++;
         }
      }

      if (rampColumns == 0) {
         return null;
      }

      int cut = 0;
      int fill = 0;
      int modified = 0;
      int water = 0;

      for (int i = 0; i < sx * sz; i++) {
         if (after[i] != p.before()[i]) {
            modified++;
            int lx = map.toLocalX(p.regionMinX() + i / sz);
            int lz = map.toLocalZ(p.regionMinZ() + i % sz);
            if (map.isWaterAt(lx, lz)) {
               water++;
               fill += after[i] - p.floor()[i];
            } else if (after[i] < p.before()[i]) {
               cut += p.before()[i] - after[i];
            } else {
               fill += after[i] - p.before()[i];
            }
         }
      }

      if (cut + fill > s.maxVolume()) {
         return null;
      }

      if (water > 0 && !fillsIn(map, p.regionMinX(), p.regionMinZ(), sx, sz, p.before(), after)) {
         return null;
      }

      double score = p.score() + (cut + fill - p.totalVolume()) + 4.0 * rampColumns;
      return new TerraformPlanner.Plan(
         p.location(),
         p.nwX(),
         p.nwZ(),
         p.padY(),
         p.regionMinX(),
         p.regionMinZ(),
         sx,
         sz,
         p.before(),
         after,
         p.pad(),
         p.floor(),
         cut,
         fill,
         modified,
         p.distance(),
         p.cutSlope(),
         p.fillSlope(),
         p.cutWall(),
         p.fillWall(),
         rampColumns,
         p.gate(),
         water,
         score
      );
   }

   private static boolean rampMayReshape(VillageTerrainMap map, byte[][] fixed, int lx, int lz, int orig, int target, TerraformPlanner.Settings s) {
      if (lx > 0 && lz > 0 && lx < map.getSizeX() - 1 && lz < map.getSizeZ() - 1) {
         byte level = fixed[lx][lz];
         if (level == 2) {
            return false;
         } else if (level == 1 && Math.abs(target - orig) > s.softTolerance()) {
            return false;
         } else {
            return level == 3 ? target > orig : !s.extensions().fillWater() || target >= orig || !isBank(map, lx, lz);
         }
      } else {
         return false;
      }
   }

   static int wallFaces(int sizeX, int sizeZ, int[] before, int[] after) {
      int faces = 0;

      for (int dx = 0; dx < sizeX; dx++) {
         for (int dz = 0; dz < sizeZ; dz++) {
            int i = dx * sizeZ + dz;
            if (dx + 1 < sizeX) {
               faces += newFace(before, after, i, i + sizeZ);
            }

            if (dz + 1 < sizeZ) {
               faces += newFace(before, after, i, i + 1);
            }
         }
      }

      return faces;
   }

   private static int newFace(int[] before, int[] after, int a, int b) {
      int planned = Math.abs(after[a] - after[b]);
      int natural = Math.abs(before[a] - before[b]);
      return planned > 1 && planned > natural ? planned - Math.max(1, natural) : 0;
   }

   @Nullable
   private static TerraformPlanner.Plan veto(TerraformPlanner.Counters counters, TerraformPlanner.Veto veto) {
      counters.vetoes[veto.ordinal()]++;
      counters.lastVeto = veto;
      return null;
   }

   private static TerraformPlanner.Validation validate(
      VillageTerrainMap scratch,
      TerraformPlanner.Plan p,
      BuildingPlan plan,
      BlockPos villageCenter,
      PlacementConstraints constraints,
      ClearMargins margins,
      List<BuildingInstance> existingBuildings,
      int originX,
      int originZ,
      TerrainReachability villageReach,
      TerraformPlanner.Counters counters
   ) {
      List<VillageTerrainMap.CellSnapshot> saved = new ArrayList<>(p.modifiedColumns());
      boolean wideGate = p.gate() > 9;

      for (int dx = 0; dx < p.sizeX(); dx++) {
         for (int dz = 0; dz < p.sizeZ(); dz++) {
            int i = dx * p.sizeZ() + dz;
            if (p.after()[i] != p.before()[i] || wideGate && p.pad()[i]) {
               int lx = p.regionMinX() + dx - originX;
               int lz = p.regionMinZ() + dz - originZ;
               saved.add(scratch.snapshotCell(lx, lz));
               scratch.simulateSurface(lx, lz, p.after()[i], p.gate());
            }
         }
      }

      try {
         if (!reachesVillageLocally(scratch, p, villageReach)) {
            counters.vetoes[TerraformPlanner.Veto.POCKET.ordinal()]++;
            return new TerraformPlanner.Validation(null, "local: pocket not connected to the village");
         }

         TerrainReachability reachability = TerrainReachability.compute(scratch, villageCenter);
         PlacedLocation location = BuildingLocationFinder.tryPosition(
            p.nwX(), p.nwZ(), scratch, plan, villageCenter, constraints, margins, existingBuildings, reachability
         );
         if (location != null) {
            return new TerraformPlanner.Validation(location, null);
         }

         BlockPos pivot = p.location().position();
         Rotation rotation = p.location().rotation();
         int errors = scratch.testFootprint(pivot.getX(), pivot.getZ(), plan.width(), plan.depth(), margins, rotation);
         String failure;
         if (errors < 0) {
            failure = "footprint";
         } else if (!reachability.isFootprintReachable(pivot.getX(), pivot.getZ(), plan.width(), plan.depth(), rotation)) {
            failure = "not reachable from the town hall";
         } else if (BuildingLocationFinder.findNeighbourCliff(scratch, pivot.getX(), pivot.getZ(), plan.width(), plan.depth(), margins, rotation) != null) {
            failure = "neighbour cliff";
         } else {
            failure = "tag constraints, trimmed margins or plot-apron band clash";
         }

         return new TerraformPlanner.Validation(null, failure);
      } finally {
         for (VillageTerrainMap.CellSnapshot cell : saved) {
            scratch.restoreCell(cell);
         }
      }
   }

   private static boolean reachesVillageLocally(VillageTerrainMap scratch, TerraformPlanner.Plan p, TerrainReachability villageReach) {
      int pad = 2;
      int x0 = p.regionMinX() - pad;
      int z0 = p.regionMinZ() - pad;
      int w = p.sizeX() + 2 * pad;
      int d = p.sizeZ() + 2 * pad;
      boolean[] seen = new boolean[w * d];
      ArrayDeque<int[]> queue = new ArrayDeque<>();

      for (int dx = 0; dx < p.sizeX(); dx++) {
         for (int dz = 0; dz < p.sizeZ(); dz++) {
            if (p.pad()[dx * p.sizeZ() + dz]) {
               int lx = scratch.toLocalX(p.regionMinX() + dx);
               int lz = scratch.toLocalZ(p.regionMinZ() + dz);
               if (TerrainReachability.isCellPassable(scratch, lx, lz)) {
                  seen[(dx + pad) * d + dz + pad] = true;
                  queue.add(new int[]{lx, lz});
               }
            }
         }
      }

      int[][] dirs = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

      while (!queue.isEmpty()) {
         int[] cell = queue.poll();

         for (int[] dir : dirs) {
            int nx = cell[0] + dir[0];
            int nz = cell[1] + dir[1];
            int bx = scratch.getOriginX() + nx - x0;
            int bz = scratch.getOriginZ() + nz - z0;
            if (bx >= 0 && bz >= 0 && bx < w && bz < d && !seen[bx * d + bz] && TerrainReachability.isConnected(scratch, cell[0], cell[1], nx, nz)) {
               int wx = scratch.getOriginX() + nx;
               int wz = scratch.getOriginZ() + nz;
               boolean reshaped = p.contains(wx, wz) && p.after()[p.index(wx, wz)] != p.before()[p.index(wx, wz)];
               if (!reshaped && villageReach.isReachable(wx, wz)) {
                  return true;
               }

               seen[bx * d + bz] = true;
               queue.add(new int[]{nx, nz});
            }
         }
      }

      return false;
   }

   private static long sat(long[] t, int stride, int x0, int z0, int x1, int z1) {
      return t[x1 * stride + z1] - t[x0 * stride + z1] - t[x1 * stride + z0] + t[x0 * stride + z0];
   }

   private static int sat(int[] t, int stride, int x0, int z0, int x1, int z1) {
      return t[x1 * stride + z1] - t[x0 * stride + z1] - t[x1 * stride + z0] + t[x0 * stride + z0];
   }

   private static int clamp(int v, int lo, int hi) {
      return v < lo ? lo : Math.min(v, hi);
   }

   static double valueNoise(long seed, int x, int z, int wavelength) {
      int x0 = Math.floorDiv(x, wavelength);
      int z0 = Math.floorDiv(z, wavelength);
      double tx = smoothstep((x - (double)x0 * wavelength) / wavelength);
      double tz = smoothstep((z - (double)z0 * wavelength) / wavelength);
      double v00 = lattice(seed, x0, z0);
      double v10 = lattice(seed, x0 + 1, z0);
      double v01 = lattice(seed, x0, z0 + 1);
      double v11 = lattice(seed, x0 + 1, z0 + 1);
      double a = v00 + (v10 - v00) * tx;
      double b = v01 + (v11 - v01) * tx;
      return a + (b - a) * tz;
   }

   private static double smoothstep(double t) {
      return t * t * (3.0 - 2.0 * t);
   }

   private static double lattice(long seed, int x, int z) {
      long h = seed ^ x * -7046029254386353131L ^ z * -4417276706812531889L;
      h ^= h >>> 33;
      h *= -49064778989728563L;
      h ^= h >>> 33;
      h *= -4265267296055464877L;
      h ^= h >>> 33;
      return (h >>> 11) * 1.110223E-16F;
   }

   private record Candidate(int nwX, int nwZ, BlockPos pivot, Rotation rotation, VillageTerrainMap.FootprintRect rect, int padY, int distance, double score) {
   }

   private static final class Counters {
      final int[] vetoes = new int[TerraformPlanner.Veto.values().length];
      @Nullable
      TerraformPlanner.Veto lastVeto;
      boolean reachOnCut;
   }

   public record Extensions(int padGate, boolean fillWater, int maxWaterDepth, boolean openWater, int wallHeight) {
      public static final TerraformPlanner.Extensions LEGACY = new TerraformPlanner.Extensions(9, false, 0);
      public static final int FILL_WALL_MAX = 3;

      public Extensions(int padGate, boolean fillWater, int maxWaterDepth) {
         this(padGate, fillWater, maxWaterDepth, false, 0);
      }

      public Extensions(int padGate, boolean fillWater, int maxWaterDepth, boolean openWater) {
         this(padGate, fillWater, maxWaterDepth, openWater, 0);
      }

      public Extensions(int padGate, boolean fillWater, int maxWaterDepth, boolean openWater, int wallHeight) {
         if (padGate >= 9 && padGate <= 22 && maxWaterDepth >= 0 && wallHeight >= 0 && wallHeight <= 8) {
            this.padGate = padGate;
            this.fillWater = fillWater;
            this.maxWaterDepth = maxWaterDepth;
            this.openWater = openWater;
            this.wallHeight = wallHeight;
         } else {
            throw new IllegalArgumentException("padGate must lie in [9, 22], maxWaterDepth >= 0, wallHeight in [0, 8]");
         }
      }

      public int fillWallHeight() {
         return Math.min(3, this.wallHeight);
      }

      public boolean beyondGate() {
         return this.padGate > 9;
      }
   }

   public record Plan(
      PlacedLocation location,
      int nwX,
      int nwZ,
      int padY,
      int regionMinX,
      int regionMinZ,
      int sizeX,
      int sizeZ,
      int[] before,
      int[] after,
      boolean[] pad,
      int[] floor,
      int cutVolume,
      int fillVolume,
      int modifiedColumns,
      int distance,
      double cutSlope,
      double fillSlope,
      int cutWall,
      int fillWall,
      int rampColumns,
      int gate,
      int waterColumns,
      double score
   ) {
      public int index(int wx, int wz) {
         return (wx - this.regionMinX) * this.sizeZ + (wz - this.regionMinZ);
      }

      public boolean contains(int wx, int wz) {
         return wx >= this.regionMinX && wx < this.regionMinX + this.sizeX && wz >= this.regionMinZ && wz < this.regionMinZ + this.sizeZ;
      }

      public int totalVolume() {
         return this.cutVolume + this.fillVolume;
      }
   }

   public record Result(@Nullable TerraformPlanner.Plan plan, TerraformPlanner.Stats stats, String reason, @Nullable TerraformPlanner.Plan bestRejected) {
   }

   public record Settings(
      int stride,
      int maxVolume,
      int maxReach,
      double cutSlope,
      double steepCutSlope,
      double fillSlope,
      double steepFillSlope,
      double jitter,
      int noiseWavelength,
      int exactBudget,
      int maxValidations,
      double distanceWeight,
      double imbalanceWeight,
      double awayWeight,
      int softTolerance,
      double softWeight,
      TerraformPlanner.Extensions extensions,
      long seed
   ) {
      public Settings(
         int stride,
         int maxVolume,
         int maxReach,
         double cutSlope,
         double steepCutSlope,
         double fillSlope,
         double steepFillSlope,
         double jitter,
         int noiseWavelength,
         int exactBudget,
         int maxValidations,
         double distanceWeight,
         double imbalanceWeight,
         double awayWeight,
         int softTolerance,
         double softWeight,
         TerraformPlanner.Extensions extensions,
         long seed
      ) {
         if (stride >= 1
            && maxReach >= 1
            && exactBudget >= 1
            && maxValidations >= 1
            && noiseWavelength >= 2
            && softTolerance >= 0
            && !(softWeight < 1.0)
            && !(awayWeight < 0.0)
            && extensions != null) {
            double lipschitz = 1.0 + 1.5 * jitter / noiseWavelength;
            if (cutSlope <= 0.0 || fillSlope <= 0.0 || cutSlope * lipschitz > 1.000000001 || fillSlope * lipschitz > 1.000000001) {
               throw new IllegalArgumentException("slope * (1 + 1.5*jitter/wavelength) must stay <= 1 to keep the slope walkable");
            }

            if (!(steepCutSlope < cutSlope) && !(steepCutSlope > 4.0) && !(steepFillSlope < fillSlope) && !(steepFillSlope > 4.0)) {
               this.stride = stride;
               this.maxVolume = maxVolume;
               this.maxReach = maxReach;
               this.cutSlope = cutSlope;
               this.steepCutSlope = steepCutSlope;
               this.fillSlope = fillSlope;
               this.steepFillSlope = steepFillSlope;
               this.jitter = jitter;
               this.noiseWavelength = noiseWavelength;
               this.exactBudget = exactBudget;
               this.maxValidations = maxValidations;
               this.distanceWeight = distanceWeight;
               this.imbalanceWeight = imbalanceWeight;
               this.awayWeight = awayWeight;
               this.softTolerance = softTolerance;
               this.softWeight = softWeight;
               this.extensions = extensions;
               this.seed = seed;
            } else {
               throw new IllegalArgumentException("steep slopes must lie in [gentle slope, 4]");
            }
         } else {
            throw new IllegalArgumentException("invalid terraform settings");
         }
      }

      public static TerraformPlanner.Settings defaults(long seed) {
         return new TerraformPlanner.Settings(
            2, 4000, 12, 0.75, 2.0, 0.6, 1.25, 1.5, 8, 2000, 8, 4.0, 0.25, 1.5, 2, 3.0, TerraformPlanner.Extensions.LEGACY, seed
         );
      }

      public double distanceLipschitz() {
         return 1.0 + 1.5 * this.jitter / this.noiseWavelength;
      }

      public TerraformPlanner.Settings withMaxVolume(int volume) {
         return new TerraformPlanner.Settings(
            this.stride,
            volume,
            this.maxReach,
            this.cutSlope,
            this.steepCutSlope,
            this.fillSlope,
            this.steepFillSlope,
            this.jitter,
            this.noiseWavelength,
            this.exactBudget,
            this.maxValidations,
            this.distanceWeight,
            this.imbalanceWeight,
            this.awayWeight,
            this.softTolerance,
            this.softWeight,
            this.extensions,
            this.seed
         );
      }

      public TerraformPlanner.Settings withMaxReach(int reach) {
         return new TerraformPlanner.Settings(
            this.stride,
            this.maxVolume,
            reach,
            this.cutSlope,
            this.steepCutSlope,
            this.fillSlope,
            this.steepFillSlope,
            this.jitter,
            this.noiseWavelength,
            this.exactBudget,
            this.maxValidations,
            this.distanceWeight,
            this.imbalanceWeight,
            this.awayWeight,
            this.softTolerance,
            this.softWeight,
            this.extensions,
            this.seed
         );
      }

      public TerraformPlanner.Settings withExtensions(TerraformPlanner.Extensions ext) {
         return new TerraformPlanner.Settings(
            this.stride,
            this.maxVolume,
            this.maxReach,
            this.cutSlope,
            this.steepCutSlope,
            this.fillSlope,
            this.steepFillSlope,
            this.jitter,
            this.noiseWavelength,
            this.exactBudget,
            this.maxValidations,
            this.distanceWeight,
            this.imbalanceWeight,
            this.awayWeight,
            this.softTolerance,
            this.softWeight,
            ext,
            this.seed
         );
      }
   }

   public record Stats(
      int scanned,
      int screened,
      int evaluated,
      int rejectedProtected,
      int rejectedVolume,
      int rejectedReach,
      int rejectedNoop,
      int rejectedCliff,
      int rejectedPocket,
      int rejectedShore,
      int validated,
      long nanos
   ) {
      @Override
      public String toString() {
         return String.format(
            "scanned=%d screened=%d evaluated=%d rejected[protected=%d volume=%d reach=%d noop=%d cliff=%d pocket=%d shore=%d] validated=%d %.1fms",
            this.scanned,
            this.screened,
            this.evaluated,
            this.rejectedProtected,
            this.rejectedVolume,
            this.rejectedReach,
            this.rejectedNoop,
            this.rejectedCliff,
            this.rejectedPocket,
            this.rejectedShore,
            this.validated,
            this.nanos / 1000000.0
         );
      }
   }

   private record Validation(@Nullable PlacedLocation location, @Nullable String failure) {
   }

   private enum Veto {
      PROTECTED,
      VOLUME,
      REACH,
      NOOP,
      CLIFF,
      POCKET,
      SHORE;

      // $VF: synthetic method
      private static TerraformPlanner.Veto[] $values() {
         return new TerraformPlanner.Veto[]{PROTECTED, VOLUME, REACH, NOOP, CLIFF, POCKET, SHORE};
      }
   }
}
