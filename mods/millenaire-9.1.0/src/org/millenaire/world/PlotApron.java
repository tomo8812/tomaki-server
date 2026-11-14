package org.millenaire.world;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Rotation;
import org.millenaire.building.ClearMargins;
import org.millenaire.config.MillenaireServerConfig;

public final class PlotApron {
   public static final int FLAT = Integer.getInteger("millenaire.plotApronFlat", 2) == 3 ? 3 : 2;
   public static final boolean TRIM = MillenaireServerConfig.plotApronTrim();
   public static boolean WALL_BAND = TRIM && flag("millenaire.plotApronWallBand");
   public static final int Y_GIVE = Math.max(0, Integer.getInteger("millenaire.plotApronYGive", 2));
   static boolean ANY_FACING = flag("millenaire.plotApronAnyFacing");
   static boolean BEST_FIT = flag("millenaire.plotApronBestFit");
   static boolean CENTRE_RING = flag("millenaire.plotApronCentreRing");
   public static boolean NATURAL = flag("millenaire.plotApronNatural");
   public static boolean ROUGH = flag("millenaire.plotApronRough");
   public static final int MIN_MARGIN = TRIM ? Math.max(0, Integer.getInteger("millenaire.plotApronMinMargin", 5)) : 0;
   public static int LOOK_AHEAD_RINGS = Integer.getInteger("millenaire.plotApronLookAhead", 2);
   public static int LOOK_AHEAD_SLACK = Math.max(0, Math.min(100, Integer.getInteger("millenaire.plotApronLookAheadSlack", 50)));
   static final int FREE_CAP = 8;
   static final int AHEAD_DEPTH = 8;
   public static final boolean LOG = Boolean.getBoolean("millenaire.plotApronLog");
   public static final boolean ACTIVE = TRIM || Y_GIVE > 0 || LOG;

   private static boolean flag(String name) {
      return !"false".equalsIgnoreCase(System.getProperty(name, "true").trim());
   }

   static boolean lookAhead() {
      return LOOK_AHEAD_RINGS >= 0;
   }

   private PlotApron() {
   }

   public static int marginFloor(int spacing) {
      return Math.max(spacing, MIN_MARGIN);
   }

   public static int offset(int ox, int oz, int flat) {
      int o = Math.max(ox, oz);
      if (flat == 2) {
         if (ox > 0 && oz > 0 && Math.abs(ox - oz) < 3) {
            o++;
         }

         return o;
      } else {
         if (Math.abs(ox - oz) < 3) {
            o++;
         }

         return o - 1;
      }
   }

   public static int tolerance(int offset) {
      return Math.max(0, offset - 2);
   }

   public static int baseY(VillageTerrainMap map, int pivotX, int pivotZ, int width, int depth, ClearMargins margins, Rotation rotation) {
      return pick(map, pivotX, pivotZ, width, depth, margins, rotation, Y_GIVE, lookAhead()).y();
   }

   static PlotApron.Pick pick(
      VillageTerrainMap map, int pivotX, int pivotZ, int width, int depth, ClearMargins margins, Rotation rotation, int give, boolean lookAhead
   ) {
      int avg = map.computeAverageAltitude(pivotX, pivotZ, width, depth, margins, rotation);
      if (lookAhead || give != 0 && map.hasAprons()) {
         PlotApron.Ahead ahead = lookAhead ? ahead(map, pivotX, pivotZ, width, depth, margins, rotation) : null;
         int best = avg;
         int bestClash = map.apronClash(pivotX, pivotZ, width, depth, margins, rotation, avg).sum();
         int bestCost = ahead != null ? ahead.cost(avg) : 0;

         for (int k = 1; k <= give && (bestClash > 0 || bestCost > 0); k++) {
            for (int y : new int[]{avg + k, avg - k}) {
               int c = map.apronClash(pivotX, pivotZ, width, depth, margins, rotation, y).sum();
               int cost = ahead != null ? ahead.cost(y) : 0;
               if (c < bestClash || c == bestClash && cost < bestCost) {
                  best = y;
                  bestClash = c;
                  bestCost = cost;
               }
            }
         }

         return new PlotApron.Pick(best, bestCost);
      } else {
         return new PlotApron.Pick(avg, 0);
      }
   }

   static PlotApron.Ahead ahead(VillageTerrainMap map, int pivotX, int pivotZ, int width, int depth, ClearMargins margins, Rotation rotation) {
      VillageTerrainMap.FootprintRect band = VillageTerrainMap.computeFootprintRect(pivotX, pivotZ, width, depth, margins, rotation);
      ClearMargins w = margins.forRotation(rotation);
      int[] m = new int[]{w.lengthBefore(), w.lengthAfter(), w.widthBefore(), w.widthAfter()};
      int[][] sides = map.sideGround(band, 8);
      int[] allowance = new int[4];

      for (int s = 0; s < 4; s++) {
         allowance[s] = tolerance(offset(m[s], 0, FLAT)) * LOOK_AHEAD_SLACK / 100;
      }

      return new PlotApron.Ahead(sides, allowance);
   }

   static int ringPenalty(int width, int depth) {
      return Math.max(1, (width + depth) / 2);
   }

   @Nullable
   public static ClearMargins margins(VillageTerrainMap map, int pivotX, int pivotZ, int width, int depth, ClearMargins margins, Rotation rotation) {
      return margins(map, pivotX, pivotZ, width, depth, margins, rotation, TRIM ? Math.max(1, MillenaireServerConfig.getDefaultClearMargin()) : 0);
   }

   @Nullable
   static ClearMargins margins(VillageTerrainMap map, int pivotX, int pivotZ, int width, int depth, ClearMargins margins, Rotation rotation, int trim) {
      if (trim == 0) {
         return margins;
      }

      if (WALL_BAND && map.coreHitsWallBand(pivotX, pivotZ, width, depth, rotation)) {
         return null;
      }

      ClearMargins w = margins.forRotation(rotation);
      VillageTerrainMap.FootprintRect core = VillageTerrainMap.computeFootprintRect(pivotX, pivotZ, width, depth, ClearMargins.symmetric(0), rotation);
      int x0 = core.startX();
      int x1 = core.startX() + core.width() - 1;
      int z0 = core.startZ();
      int z1 = core.startZ() + core.depth() - 1;
      int west = map.freeCells(x0 - 1, -1, 0, z0, z1, w.lengthBefore());
      int east = map.freeCells(x1 + 1, 1, 0, z0, z1, w.lengthAfter());
      int north = map.freeCells(z0 - 1, 0, -1, x0 - west, x1 + east, w.widthBefore());
      int south = map.freeCells(z1 + 1, 0, 1, x0 - west, x1 + east, w.widthAfter());
      return west >= Math.min(trim, w.lengthBefore())
            && east >= Math.min(trim, w.lengthAfter())
            && north >= Math.min(trim, w.widthBefore())
            && south >= Math.min(trim, w.widthAfter())
         ? toPlan(new ClearMargins(west, east, north, south), rotation)
         : null;
   }

   public static int freeAround(VillageTerrainMap map, int pivotX, int pivotZ, int width, int depth, Rotation rotation) {
      VillageTerrainMap.FootprintRect core = VillageTerrainMap.computeFootprintRect(pivotX, pivotZ, width, depth, ClearMargins.symmetric(0), rotation);
      int x0 = core.startX();
      int x1 = core.startX() + core.width() - 1;
      int z0 = core.startZ();
      int z1 = core.startZ() + core.depth() - 1;
      return map.freeCells(x0 - 1, -1, 0, z0, z1, 8)
         + map.freeCells(x1 + 1, 1, 0, z0, z1, 8)
         + map.freeCells(z0 - 1, 0, -1, x0, x1, 8)
         + map.freeCells(z1 + 1, 0, 1, x0, x1, 8);
   }

   static ClearMargins toPlan(ClearMargins world, Rotation rotation) {
      return switch (rotation) {
         case CLOCKWISE_90 -> world.forRotation(Rotation.COUNTERCLOCKWISE_90);
         case COUNTERCLOCKWISE_90 -> world.forRotation(Rotation.CLOCKWISE_90);
         default -> world.forRotation(rotation);
      };
   }

   @Nullable
   public static PlotApron.Neighbours neighbours(@Nullable VillageTerrainMap map, int pivotX, int pivotZ, int width, int depth, Rotation rotation) {
      return NATURAL && map != null ? map.neighboursOf(pivotX, pivotZ, width, depth, rotation) : null;
   }

   public static PlotApron.Neighbours neighbours(List<PlotApron.Band> bands) {
      return (x, z) -> {
         int lo = Integer.MIN_VALUE;
         int hi = Integer.MAX_VALUE;
         boolean any = false;

         for (PlotApron.Band b : bands) {
            VillageTerrainMap.FootprintRect r = b.band();
            if (x >= r.startX() && x < r.startX() + r.width() && z >= r.startZ() && z < r.startZ() + r.depth()) {
               int ox = TerrainTraversal.computeOffsetComponent(x, b.core().startX(), b.core().width());
               int oz = TerrainTraversal.computeOffsetComponent(z, b.core().startZ(), b.core().depth());
               int t = tolerance(offset(ox, oz, FLAT));
               int nlo = Math.max(lo, b.y() - t);
               int nhi = Math.min(hi, b.y() + t);
               if (nlo <= nhi) {
                  lo = nlo;
                  hi = nhi;
                  any = true;
               }
            }
         }

         return any ? new int[]{lo, hi} : null;
      };
   }

   record Ahead(int[][] sides, int[] allowance) {
      int cost(int y) {
         int cost = 0;

         for (int s = 0; s < 4; s++) {
            if (this.sides[s][1] != 0) {
               cost += this.sides[s][1] * Math.max(0, Math.abs(y - this.sides[s][0]) - this.allowance[s]);
            }
         }

         return cost;
      }

      String describe() {
         StringBuilder b = new StringBuilder();
         String names = "WENS";

         for (int s = 0; s < 4; s++) {
            if (s > 0) {
               b.append('/');
            }

            b.append(names.charAt(s));
            if (this.sides[s][1] == 0) {
               b.append('-');
            } else {
               b.append(this.sides[s][0]).append('x').append(this.sides[s][1]);
            }
         }

         return b.toString();
      }
   }

   public record Band(VillageTerrainMap.FootprintRect core, VillageTerrainMap.FootprintRect band, int y) {
      public static PlotApron.Band of(int pivotX, int pivotZ, int width, int depth, ClearMargins margins, Rotation rotation, int y) {
         return new PlotApron.Band(
            VillageTerrainMap.computeFootprintRect(pivotX, pivotZ, width, depth, ClearMargins.symmetric(0), rotation),
            VillageTerrainMap.computeFootprintRect(pivotX, pivotZ, width, depth, margins, rotation),
            y
         );
      }
   }

   public record Clash(int shared, int clashing, int worst, int sum, int owner) {
      public static final PlotApron.Clash NONE = new PlotApron.Clash(0, 0, 0, 0, -1);
   }

   @FunctionalInterface
   public interface Neighbours {
      @Nullable
      int[] range(int var1, int var2);
   }

   record Pick(int y, int cost) {
   }
}
