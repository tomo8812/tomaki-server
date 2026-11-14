package org.millenaire.world;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import org.millenaire.building.BuildingInstance;
import org.millenaire.building.BuildingPlan;
import org.millenaire.building.ClearMargins;
import org.millenaire.culture.ModCultures;
import org.slf4j.Logger;

public final class BuildingLocationFinder {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int NEIGHBOUR_CLIFF_SCAN_RADIUS = 8;
   private static final int NEIGHBOUR_CLIFF_SLACK = 3;
   private static final ThreadLocal<int[]> DIAG = new ThreadLocal<>();
   static final ThreadLocal<Random> SIDE_OFFSET_RANDOM = new ThreadLocal<>();
   private static final ThreadLocal<String> LAST_FAILURE = new ThreadLocal<>();
   private static final ThreadLocal<String> LAST_NOTE = new ThreadLocal<>();

   private BuildingLocationFinder() {
   }

   @Nullable
   public static PlacedLocation findLocation(
      VillageTerrainMap map,
      BuildingPlan plan,
      BlockPos villageCenter,
      PlacementConstraints constraints,
      ClearMargins clearMargins,
      List<BuildingInstance> existingBuildings
   ) {
      return findLocation(map, plan, villageCenter, constraints, clearMargins, existingBuildings, null);
   }

   @Nullable
   public static PlacedLocation findLocation(
      VillageTerrainMap map,
      BuildingPlan plan,
      BlockPos villageCenter,
      PlacementConstraints constraints,
      ClearMargins clearMargins,
      List<BuildingInstance> existingBuildings,
      @Nullable TerrainReachability reachability
   ) {
      LAST_FAILURE.remove();
      LAST_NOTE.remove();
      int[] counts = PlotApron.LOG ? new int[BuildingLocationFinder.Reject.values().length] : null;
      if (counts != null) {
         DIAG.set(counts);
      }

      PlacedLocation found;
      try {
         found = search(map, plan, villageCenter, constraints, clearMargins, existingBuildings, reachability);
      } finally {
         DIAG.remove();
      }

      if (found == null && PlotApron.ANY_FACING && constraints.fixedOrientation() == null) {
         double bestDist = Double.MAX_VALUE;
         int bestDir = -1;

         for (int dir = 0; dir < 4; dir++) {
            PlacedLocation at = search(map, plan, villageCenter, facing(constraints, dir), clearMargins, existingBuildings, reachability);
            if (at != null) {
               double d = at.position().distSqr(new BlockPos(villageCenter.getX(), at.position().getY(), villageCenter.getZ()));
               if (d < bestDist) {
                  bestDist = d;
                  bestDir = dir;
                  found = at;
               }
            }
         }

         if (found != null) {
            LAST_NOTE.set("facing_fallback=" + "NESW".charAt(bestDir));
         }
      }

      if (found == null && counts != null) {
         StringBuilder why = new StringBuilder();

         for (BuildingLocationFinder.Reject r : BuildingLocationFinder.Reject.values()) {
            if (counts[r.ordinal()] > 0) {
               why.append(why.isEmpty() ? "" : " ").append(r.name().toLowerCase(Locale.ROOT)).append('=').append(counts[r.ordinal()]);
            }
         }

         if (constraints.fixedOrientation() == null) {
            StringBuilder fits = new StringBuilder();

            for (int dir = 0; dir < 4; dir++) {
               if (search(map, plan, villageCenter, facing(constraints, dir), clearMargins, existingBuildings, reachability) != null) {
                  fits.append("NESW".charAt(dir));
               }
            }

            why.append(" fits_facing=").append(fits.isEmpty() ? "none" : fits);
         }

         LAST_FAILURE.set(why.toString().trim());
      }

      return found;
   }

   private static PlacementConstraints facing(PlacementConstraints c, int dir) {
      return new PlacementConstraints(c.minDistance(), c.maxDistance(), c.farFromTags(), c.closeToTags(), c.clearMargin(), dir);
   }

   @Nullable
   public static String takeNote() {
      String note = LAST_NOTE.get();
      LAST_NOTE.remove();
      return note;
   }

   @Nullable
   public static String lastFailure() {
      return LAST_FAILURE.get();
   }

   @Nullable
   private static String reject(BuildingLocationFinder.Reject reason, @Nullable String name) {
      int[] counts = DIAG.get();
      if (counts != null) {
         counts[reason.ordinal()]++;
      }

      return name;
   }

   @Nullable
   private static PlacedLocation search(
      VillageTerrainMap map,
      BuildingPlan plan,
      BlockPos villageCenter,
      PlacementConstraints constraints,
      ClearMargins clearMargins,
      List<BuildingInstance> existingBuildings,
      @Nullable TerrainReachability reachability
   ) {
      int centerX = villageCenter.getX();
      int centerZ = villageCenter.getZ();
      int minDist = constraints.minDistance();
      int maxDist = constraints.maxDistance();
      Random seeded = SIDE_OFFSET_RANDOM.get();
      int sideOffset = seeded != null ? seeded.nextInt(4) : ThreadLocalRandom.current().nextInt(4);
      BuildingLocationFinder.BestFit window = PlotApron.lookAhead() ? new BuildingLocationFinder.BestFit() : null;

      for (int r = minDist; r <= maxDist; r++) {
         BuildingLocationFinder.BestFit best = window != null ? window : (PlotApron.BEST_FIT ? new BuildingLocationFinder.BestFit() : null);

         for (int sideIdx = 0; sideIdx < 4; sideIdx++) {
            int side = (sideIdx + sideOffset) % 4;
            PlacedLocation result = scanSide(
               side, r, centerX, centerZ, map, plan, villageCenter, constraints, clearMargins, existingBuildings, reachability, best
            );
            if (result != null) {
               return result;
            }
         }

         if (window != null) {
            if (window.location != null && r >= window.firstRing + PlotApron.LOOK_AHEAD_RINGS) {
               return window.chosen();
            }
         } else if (best != null && best.location != null) {
            return best.location;
         }
      }

      return window != null ? window.chosen() : null;
   }

   private static boolean accept(
      @Nullable BuildingLocationFinder.BestFit best, PlacedLocation found, VillageTerrainMap map, BuildingPlan plan, int r, ClearMargins clearMargins
   ) {
      if (best == null) {
         return true;
      }

      int px = found.position().getX();
      int pz = found.position().getZ();
      int free = PlotApron.BEST_FIT ? PlotApron.freeAround(map, px, pz, plan.width(), plan.depth(), found.rotation()) : 0;
      long score = 0L;
      if (PlotApron.lookAhead()) {
         if (best.firstRing < 0) {
            best.firstRing = r;
         }

         ClearMargins margins = PlotApron.margins(map, px, pz, plan.width(), plan.depth(), clearMargins, found.rotation());
         if (margins == null) {
            margins = clearMargins;
         }

         int cost = PlotApron.pick(map, px, pz, plan.width(), plan.depth(), margins, found.rotation(), PlotApron.Y_GIVE, true).cost();
         score = cost + (long)(r - best.firstRing) * PlotApron.ringPenalty(plan.width(), plan.depth());
         if (r == best.firstRing && (best.plain == null || PlotApron.BEST_FIT && free < best.plainFree)) {
            best.plain = found;
            best.plainFree = free;
         }
      }

      if (score < best.score || score == best.score && free < best.freeCells) {
         best.score = score;
         best.freeCells = free;
         best.location = found;
         best.ring = r;
      }

      return false;
   }

   @Nullable
   private static PlacedLocation scanSide(
      int side,
      int r,
      int centerX,
      int centerZ,
      VillageTerrainMap map,
      BuildingPlan plan,
      BlockPos villageCenter,
      PlacementConstraints constraints,
      ClearMargins clearMargins,
      List<BuildingInstance> existingBuildings,
      @Nullable TerrainReachability reachability,
      @Nullable BuildingLocationFinder.BestFit best
   ) {
      switch (side) {
         case 0:
            for (int x = -r; x <= r; x++) {
               PlacedLocation result = tryPosition(
                  centerX + x, centerZ - r, map, plan, villageCenter, constraints, clearMargins, existingBuildings, reachability
               );
               if (result != null && accept(best, result, map, plan, r, clearMargins)) {
                  return result;
               }
            }
            break;
         case 1:
            for (int z = -r + 1; z <= r; z++) {
               PlacedLocation result = tryPosition(
                  centerX + r, centerZ + z, map, plan, villageCenter, constraints, clearMargins, existingBuildings, reachability
               );
               if (result != null && accept(best, result, map, plan, r, clearMargins)) {
                  return result;
               }
            }
            break;
         case 2:
            for (int x = r - 1; x >= -r; x--) {
               PlacedLocation result = tryPosition(
                  centerX + x, centerZ + r, map, plan, villageCenter, constraints, clearMargins, existingBuildings, reachability
               );
               if (result != null && accept(best, result, map, plan, r, clearMargins)) {
                  return result;
               }
            }
            break;
         case 3:
            for (int z = r - 1; z >= -r + 1; z--) {
               PlacedLocation result = tryPosition(
                  centerX - r, centerZ + z, map, plan, villageCenter, constraints, clearMargins, existingBuildings, reachability
               );
               if (result != null && accept(best, result, map, plan, r, clearMargins)) {
                  return result;
               }
            }
      }

      return null;
   }

   @Nullable
   static PlacedLocation tryPosition(
      int worldX,
      int worldZ,
      VillageTerrainMap map,
      BuildingPlan plan,
      BlockPos villageCenter,
      PlacementConstraints constraints,
      ClearMargins clearMargins,
      List<BuildingInstance> existingBuildings,
      @Nullable TerrainReachability reachability
   ) {
      PlacedLocation candidate = candidateForCorner(worldX, worldZ, plan, constraints, villageCenter);
      Rotation rotation = candidate.rotation();
      int pivotX = candidate.position().getX();
      int pivotZ = candidate.position().getZ();
      if (rejectionReason(pivotX, pivotZ, rotation, map, plan, constraints, clearMargins, existingBuildings, reachability) != null) {
         return null;
      }

      BlockPos pos = new BlockPos(pivotX, villageCenter.getY(), pivotZ);
      if (LOGGER.isDebugEnabled()) {
         int halfFootprint = (plan.width() + plan.depth()) / 4;
         LOGGER.debug(
            "[Orient] plan={} bo={} fixed={} nwCorner=({},{}) wh={}x{} center=({},{}) thCenter=({},{}) rot={} pivot=({},{})",
            new Object[]{
               plan.id(),
               plan.buildingOrientation(),
               constraints.fixedOrientation(),
               worldX,
               worldZ,
               plan.width(),
               plan.depth(),
               worldX + halfFootprint,
               worldZ + halfFootprint,
               villageCenter.getX(),
               villageCenter.getZ(),
               rotation,
               pivotX,
               pivotZ
            }
         );
      }

      return new PlacedLocation(pos, rotation);
   }

   @Nullable
   static PlacedLocation tryPivot(
      int pivotX,
      int pivotZ,
      Rotation rotation,
      VillageTerrainMap map,
      BuildingPlan plan,
      BlockPos villageCenter,
      PlacementConstraints constraints,
      ClearMargins clearMargins,
      List<BuildingInstance> existingBuildings,
      @Nullable TerrainReachability reachability
   ) {
      return rejectionReason(pivotX, pivotZ, rotation, map, plan, constraints, clearMargins, existingBuildings, reachability) != null
         ? null
         : new PlacedLocation(new BlockPos(pivotX, villageCenter.getY(), pivotZ), rotation);
   }

   static PlacedLocation candidateForCorner(int worldX, int worldZ, BuildingPlan plan, PlacementConstraints constraints, BlockPos villageCenter) {
      Rotation rotation = resolveRotation(worldX, worldZ, plan, villageCenter, constraints);
      if (PlotApron.CENTRE_RING) {
         boolean quarterTurn = rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90;
         worldX -= (quarterTurn ? plan.depth() : plan.width()) / 2;
         worldZ -= (quarterTurn ? plan.width() : plan.depth()) / 2;
      }

      return new PlacedLocation(PlacedLocation.pivotForNwCorner(worldX, worldZ, plan.width(), plan.depth(), rotation), rotation);
   }

   static Rotation resolveRotation(int worldX, int worldZ, BuildingPlan plan, BlockPos villageCenter, PlacementConstraints constraints) {
      int halfFootprint = PlotApron.CENTRE_RING ? 0 : (plan.width() + plan.depth()) / 4;
      int candidateCenterX = worldX + halfFootprint;
      int candidateCenterZ = worldZ + halfFootprint;
      int targetDirection = constraints.fixedOrientation() != null
         ? constraints.fixedOrientation()
         : directionToVillageCenter(candidateCenterX, candidateCenterZ, villageCenter);
      return Rotation.values()[Math.floorMod(targetDirection - plan.buildingOrientation(), 4)];
   }

   @Nullable
   static String rejectionReason(
      int pivotX,
      int pivotZ,
      Rotation rotation,
      VillageTerrainMap map,
      BuildingPlan plan,
      PlacementConstraints constraints,
      ClearMargins clearMargins,
      List<BuildingInstance> existingBuildings,
      @Nullable TerrainReachability reachability
   ) {
      if (!checkTagConstraints(pivotX, pivotZ, plan, rotation, constraints, existingBuildings)) {
         return reject(BuildingLocationFinder.Reject.TAGS, "tags");
      }

      ClearMargins margins = PlotApron.margins(map, pivotX, pivotZ, plan.width(), plan.depth(), clearMargins, rotation);
      if (margins == null) {
         return reject(BuildingLocationFinder.Reject.OCCUPIED, "occupied");
      }

      if (map.testFootprint(pivotX, pivotZ, plan.width(), plan.depth(), margins, rotation) < 0) {
         if (DIAG.get() != null) {
            reject(
               map.footprintHitsOccupied(pivotX, pivotZ, plan.width(), plan.depth(), margins, rotation)
                  ? BuildingLocationFinder.Reject.OCCUPIED
                  : BuildingLocationFinder.Reject.TERRAIN,
               null
            );
         }

         return "footprint";
      } else {
         if (reachability != null && !reachability.isFootprintReachable(pivotX, pivotZ, plan.width(), plan.depth(), rotation)) {
            return reject(BuildingLocationFinder.Reject.UNREACHABLE, "unreachable");
         }

         if (findNeighbourCliff(map, pivotX, pivotZ, plan.width(), plan.depth(), margins, rotation) != null) {
            return reject(BuildingLocationFinder.Reject.CLIFF, "cliff(GH-244)");
         }

         if (PlotApron.TRIM && map.hasAprons()) {
            int y = PlotApron.baseY(map, pivotX, pivotZ, plan.width(), plan.depth(), margins, rotation);
            if (map.apronClash(pivotX, pivotZ, plan.width(), plan.depth(), margins, rotation, y).clashing() > 0) {
               return reject(BuildingLocationFinder.Reject.APRON, "apron");
            }
         }

         return null;
      }
   }

   public static BuildingLocationFinder.AnchorEvaluation evaluateAnchor(
      int worldX,
      int worldZ,
      VillageTerrainMap map,
      BuildingPlan plan,
      BlockPos villageCenter,
      PlacementConstraints constraints,
      ClearMargins clearMargins,
      List<BuildingInstance> existingBuildings,
      @Nullable TerrainReachability reachability,
      @Nullable Integer playerRotation
   ) {
      Rotation rotation;
      if (constraints.fixedOrientation() != null) {
         int rotationOrdinal = Math.floorMod(constraints.fixedOrientation() - plan.buildingOrientation(), 4);
         rotation = Rotation.values()[rotationOrdinal];
      } else if (playerRotation != null) {
         rotation = Rotation.values()[Math.floorMod(playerRotation, 4)];
      } else {
         int halfFootprint = (plan.width() + plan.depth()) / 4;
         int candidateCenterX = worldX + halfFootprint;
         int candidateCenterZ = worldZ + halfFootprint;
         int computedDir = directionToVillageCenter(candidateCenterX, candidateCenterZ, villageCenter);
         int rotationOrdinal = Math.floorMod(computedDir - plan.buildingOrientation(), 4);
         rotation = Rotation.values()[rotationOrdinal];
      }

      BlockPos pivot = PlacedLocation.pivotForNwCorner(worldX, worldZ, plan.width(), plan.depth(), rotation);
      int pivotX = pivot.getX();
      int pivotZ = pivot.getZ();
      if (!map.inBounds(map.toLocalX(pivotX), map.toLocalZ(pivotZ))) {
         return new BuildingLocationFinder.AnchorEvaluation(
            null, BuildingLocationFinder.FailureReason.OUTSIDE_RADIUS, new BlockPos(worldX, villageCenter.getY(), worldZ)
         );
      }

      BuildingLocationFinder.AnchorEvaluation footprintFailure = classifyFootprint(pivotX, pivotZ, plan, clearMargins, rotation, map, villageCenter);
      if (footprintFailure != null) {
         return footprintFailure;
      }

      if (reachability != null && !reachability.isFootprintReachable(pivotX, pivotZ, plan.width(), plan.depth(), rotation)) {
         return new BuildingLocationFinder.AnchorEvaluation(
            null, BuildingLocationFinder.FailureReason.NOT_REACHABLE, new BlockPos(pivotX, villageCenter.getY(), pivotZ)
         );
      }

      BlockPos cliffCell = findNeighbourCliff(map, pivotX, pivotZ, plan.width(), plan.depth(), clearMargins, rotation);
      if (cliffCell != null) {
         return new BuildingLocationFinder.AnchorEvaluation(null, BuildingLocationFinder.FailureReason.NOT_REACHABLE, cliffCell);
      }

      BlockPos pos = new BlockPos(pivotX, villageCenter.getY(), pivotZ);
      return new BuildingLocationFinder.AnchorEvaluation(new PlacedLocation(pos, rotation), null, null);
   }

   @Nullable
   static BlockPos findNeighbourCliff(VillageTerrainMap map, int pivotX, int pivotZ, int width, int depth, ClearMargins clearMargins, Rotation rotation) {
      int candidateAltitude = map.computeAverageAltitude(pivotX, pivotZ, width, depth, clearMargins, rotation);
      return findNeighbourCliff(map, pivotX, pivotZ, width, depth, rotation, candidateAltitude);
   }

   @Nullable
   static BlockPos findNeighbourCliff(VillageTerrainMap map, int pivotX, int pivotZ, int width, int depth, Rotation rotation, int candidateAltitude) {
      VillageTerrainMap.FootprintRect core = VillageTerrainMap.computeFootprintRect(pivotX, pivotZ, width, depth, ClearMargins.symmetric(0), rotation);
      int coreMinX = core.startX();
      int coreMaxX = core.startX() + core.width() - 1;
      int coreMinZ = core.startZ();
      int coreMaxZ = core.startZ() + core.depth() - 1;

      for (int wx = coreMinX - 8; wx <= coreMaxX + 8; wx++) {
         int gapX = wx < coreMinX ? coreMinX - wx : Math.max(wx - coreMaxX, 0);

         for (int wz = coreMinZ - 8; wz <= coreMaxZ + 8; wz++) {
            int gapZ = wz < coreMinZ ? coreMinZ - wz : Math.max(wz - coreMaxZ, 0);
            int gap = Math.max(gapX, gapZ);
            if (gap != 0) {
               int lx = map.toLocalX(wx);
               int lz = map.toLocalZ(wz);
               if (map.isOccupied(lx, lz)) {
                  int neighbourY = map.getTopGround(lx, lz);
                  if (Math.abs(candidateAltitude - neighbourY) > gap + 3) {
                     return new BlockPos(wx, neighbourY, wz);
                  }
               }
            }
         }
      }

      return null;
   }

   @Nullable
   private static BuildingLocationFinder.AnchorEvaluation classifyFootprint(
      int pivotX, int pivotZ, BuildingPlan plan, ClearMargins clearMargins, Rotation rotation, VillageTerrainMap map, BlockPos villageCenter
   ) {
      VillageTerrainMap.FootprintRect rect = VillageTerrainMap.computeFootprintRect(pivotX, pivotZ, plan.width(), plan.depth(), clearMargins, rotation);
      int surface = rect.width() * rect.depth();
      boolean hugeBuilding = surface > 2000;
      int allowedErrors;
      if (surface > 2000) {
         allowedErrors = surface / 10;
      } else if (surface > 200) {
         allowedErrors = surface / 20;
      } else {
         allowedErrors = 10;
      }

      int nbError = 0;

      for (int dx = 0; dx < rect.width(); dx++) {
         for (int dz = 0; dz < rect.depth(); dz++) {
            int wx = rect.startX() + dx;
            int wz = rect.startZ() + dz;
            int lx = map.toLocalX(wx);
            int lz = map.toLocalZ(wz);
            BlockPos errorPos = new BlockPos(wx, villageCenter.getY(), wz);
            if (!map.inBounds(lx, lz)) {
               return new BuildingLocationFinder.AnchorEvaluation(null, BuildingLocationFinder.FailureReason.OUTSIDE_RADIUS, errorPos);
            }

            if (map.isOccupied(lx, lz)) {
               return new BuildingLocationFinder.AnchorEvaluation(null, BuildingLocationFinder.FailureReason.LOCATION_CLASH, errorPos);
            }

            if (map.isBuildingForbiddenAt(lx, lz)) {
               if (!hugeBuilding || nbError > allowedErrors) {
                  return new BuildingLocationFinder.AnchorEvaluation(null, BuildingLocationFinder.FailureReason.CONSTRUCTION_FORBIDDEN, errorPos);
               }

               nbError++;
            } else if (map.isDangerAt(lx, lz)) {
               if (nbError > allowedErrors) {
                  return new BuildingLocationFinder.AnchorEvaluation(null, BuildingLocationFinder.FailureReason.DANGER, errorPos);
               }

               nbError++;
            } else if (!map.canBuildAt(lx, lz)) {
               if (nbError > allowedErrors) {
                  return new BuildingLocationFinder.AnchorEvaluation(null, BuildingLocationFinder.FailureReason.WRONG_ALTITUDE, errorPos);
               }

               nbError++;
            }
         }
      }

      return null;
   }

   static int directionToVillageCenter(int buildingX, int buildingZ, BlockPos villageCenter) {
      int relx = villageCenter.getX() - buildingX;
      int relz = villageCenter.getZ() - buildingZ;
      if (relx * relx > relz * relz) {
         return relx > 0 ? 1 : 3;
      } else {
         return relz > 0 ? 2 : 0;
      }
   }

   private static boolean checkTagConstraints(
      int pivotX, int pivotZ, BuildingPlan plan, Rotation rotation, PlacementConstraints constraints, List<BuildingInstance> existingBuildings
   ) {
      if (constraints.farFromTags().isEmpty() && constraints.closeToTags().isEmpty()) {
         return true;
      }

      BlockPos candidateCentre = footprintCentre(pivotX, pivotZ, plan, rotation);
      int worldX = candidateCentre.getX();
      int worldZ = candidateCentre.getZ();

      for (Entry<String, Integer> entry : constraints.farFromTags().entrySet()) {
         String tag = entry.getKey();
         int distMin = entry.getValue();

         for (BuildingInstance building : existingBuildings) {
            BuildingPlan existingPlan = ModCultures.getBuildingPlan(building.getPlanId());
            if (existingPlan != null && existingPlan.hasTag(tag)) {
               double dist = horizontalDistance(
                  worldX, worldZ, footprintCentre(building.getOrigin().getX(), building.getOrigin().getZ(), existingPlan, building.getRotation())
               );
               if (dist < distMin) {
                  return false;
               }
            }
         }
      }

      for (Entry<String, Integer> entry : constraints.closeToTags().entrySet()) {
         String tag = entry.getKey();
         int distMax = entry.getValue();
         boolean found = false;

         for (BuildingInstance building : existingBuildings) {
            BuildingPlan existingPlan = ModCultures.getBuildingPlan(building.getPlanId());
            if (existingPlan != null && existingPlan.hasTag(tag)) {
               double dist = horizontalDistance(
                  worldX, worldZ, footprintCentre(building.getOrigin().getX(), building.getOrigin().getZ(), existingPlan, building.getRotation())
               );
               if (dist < distMax) {
                  found = true;
                  break;
               }
            }
         }

         if (!found) {
            return false;
         }
      }

      return true;
   }

   public static BlockPos footprintCentre(int pivotX, int pivotZ, BuildingPlan plan, Rotation rotation) {
      VillageTerrainMap.FootprintRect core = VillageTerrainMap.computeFootprintRect(
         pivotX, pivotZ, plan.width(), plan.depth(), ClearMargins.symmetric(0), rotation
      );
      return new BlockPos(core.startX() + core.width() / 2, 0, core.startZ() + core.depth() / 2);
   }

   private static double horizontalDistance(int x, int z, BlockPos pos) {
      int dx = x - pos.getX();
      int dz = z - pos.getZ();
      return Math.sqrt(dx * dx + dz * dz);
   }

   public static Map<String, Integer> explainRejections(
      VillageTerrainMap map,
      BuildingPlan plan,
      BlockPos villageCenter,
      PlacementConstraints constraints,
      ClearMargins clearMargins,
      List<BuildingInstance> existingBuildings,
      @Nullable TerrainReachability reachability
   ) {
      Map<String, Integer> counts = new TreeMap<>();
      int cx = villageCenter.getX();
      int cz = villageCenter.getZ();

      for (int r = constraints.minDistance(); r <= constraints.maxDistance(); r++) {
         for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
               if (Math.max(Math.abs(x), Math.abs(z)) == r) {
                  int worldX = cx + x;
                  int worldZ = cz + z;
                  PlacedLocation candidate = candidateForCorner(worldX, worldZ, plan, constraints, villageCenter);
                  Rotation rotation = candidate.rotation();
                  BlockPos pivot = candidate.position();
                  String reason = rejectionReason(pivot.getX(), pivot.getZ(), rotation, map, plan, constraints, clearMargins, existingBuildings, reachability);
                  if (reason == null) {
                     reason = "ok";
                  } else if (reason.equals("footprint")) {
                     BuildingLocationFinder.AnchorEvaluation eval = classifyFootprint(
                        pivot.getX(), pivot.getZ(), plan, clearMargins, rotation, map, villageCenter
                     );
                     reason = eval != null ? "footprint:" + eval.reason() : "footprint:other";
                  }

                  counts.merge(reason, 1, Integer::sum);
               }
            }
         }
      }

      return counts;
   }

   public record AnchorEvaluation(@Nullable PlacedLocation location, @Nullable BuildingLocationFinder.FailureReason reason, @Nullable BlockPos errorPos) {
      public boolean isSuccess() {
         return this.location != null;
      }
   }

   private static final class BestFit {
      @Nullable
      PlacedLocation location;
      int freeCells = Integer.MAX_VALUE;
      long score = Long.MAX_VALUE;
      int firstRing = -1;
      int ring;
      @Nullable
      PlacedLocation plain;
      int plainFree = Integer.MAX_VALUE;

      @Nullable
      PlacedLocation chosen() {
         if (this.location != null && this.location != this.plain) {
            BuildingLocationFinder.LAST_NOTE.set("ahead_moved=+" + (this.ring - this.firstRing) + "ring");
         }

         return this.location;
      }
   }

   public enum FailureReason {
      OUTSIDE_RADIUS,
      LOCATION_CLASH,
      CONSTRUCTION_FORBIDDEN,
      WRONG_ALTITUDE,
      DANGER,
      NOT_REACHABLE,
      GENERIC;

      // $VF: synthetic method
      private static BuildingLocationFinder.FailureReason[] $values() {
         return new BuildingLocationFinder.FailureReason[]{
            OUTSIDE_RADIUS, LOCATION_CLASH, CONSTRUCTION_FORBIDDEN, WRONG_ALTITUDE, DANGER, NOT_REACHABLE, GENERIC
         };
      }
   }

   enum Reject {
      TAGS,
      OCCUPIED,
      TERRAIN,
      UNREACHABLE,
      CLIFF,
      APRON;

      // $VF: synthetic method
      private static BuildingLocationFinder.Reject[] $values() {
         return new BuildingLocationFinder.Reject[]{TAGS, OCCUPIED, TERRAIN, UNREACHABLE, CLIFF, APRON};
      }
   }
}
