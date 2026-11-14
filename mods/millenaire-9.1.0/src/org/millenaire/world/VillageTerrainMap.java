package org.millenaire.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.millenaire.building.ClearMargins;
import org.millenaire.tag.ModTags;

public class VillageTerrainMap {
   private static final int ALTITUDE_TOLERANCE = 10;
   private static final int SCAN_RANGE = 25;
   private static final int SMALL_BUILDING_FIXED_ERRORS = 10;
   private static final int MEDIUM_BUILDING_THRESHOLD = 200;
   private static final int HUGE_BUILDING_THRESHOLD = 2000;
   private final BlockPos origin;
   private final int sizeX;
   private final int sizeZ;
   private final int baseline;
   private final int[][] topGround;
   private final boolean[][] canBuild;
   private final boolean[][] danger;
   private final boolean[][] water;
   private final boolean[][] buildingForbidden;
   private final short[][] spaceAbove;
   private final boolean[][] occupied;
   private final int[][] waterFloor;
   private int scanRange = 25;
   private int[][] apronLo;
   private int[][] apronHi;
   private int[][] apronOwner;
   private final List<VillageTerrainMap.ApronOwner> apronOwners = new ArrayList<>();
   private boolean trackAprons;
   private int apronFlat = PlotApron.FLAT;
   private boolean[][] trimBarrier;
   private boolean[][] wallBand;
   static final int MAP_MARGIN = 5;
   static final int BUILDING_MARGIN = 5;

   void trackAprons(int flat) {
      this.trackAprons = true;
      this.apronFlat = flat;
   }

   private VillageTerrainMap(
      BlockPos origin,
      int sizeX,
      int sizeZ,
      int baseline,
      int[][] topGround,
      boolean[][] canBuild,
      boolean[][] danger,
      boolean[][] water,
      boolean[][] buildingForbidden,
      short[][] spaceAbove
   ) {
      this(origin, sizeX, sizeZ, baseline, topGround, canBuild, danger, water, buildingForbidden, spaceAbove, deepCopy(topGround));
   }

   private VillageTerrainMap(
      BlockPos origin,
      int sizeX,
      int sizeZ,
      int baseline,
      int[][] topGround,
      boolean[][] canBuild,
      boolean[][] danger,
      boolean[][] water,
      boolean[][] buildingForbidden,
      short[][] spaceAbove,
      int[][] waterFloor
   ) {
      this.origin = origin;
      this.sizeX = sizeX;
      this.sizeZ = sizeZ;
      this.baseline = baseline;
      this.topGround = topGround;
      this.canBuild = canBuild;
      this.danger = danger;
      this.water = water;
      this.buildingForbidden = buildingForbidden;
      this.spaceAbove = spaceAbove;
      this.occupied = new boolean[sizeX][sizeZ];
      this.waterFloor = waterFloor;
   }

   static VillageTerrainMap forTest(BlockPos origin, int size, int baseline, int[][] topGround, boolean[][] canBuild) {
      boolean[][] danger = new boolean[size][size];
      boolean[][] water = new boolean[size][size];
      boolean[][] forbidden = new boolean[size][size];
      short[][] spaceAbove = new short[size][size];

      for (int x = 0; x < size; x++) {
         for (int z = 0; z < size; z++) {
            spaceAbove[x][z] = 3;
         }
      }

      return new VillageTerrainMap(origin, size, size, baseline, topGround, canBuild, danger, water, forbidden, spaceAbove);
   }

   static VillageTerrainMap forTest(
      BlockPos origin,
      int size,
      int baseline,
      int[][] topGround,
      boolean[][] canBuild,
      boolean[][] danger,
      boolean[][] water,
      boolean[][] buildingForbidden,
      short[][] spaceAbove
   ) {
      return new VillageTerrainMap(origin, size, size, baseline, topGround, canBuild, danger, water, buildingForbidden, spaceAbove);
   }

   static VillageTerrainMap forTest(
      BlockPos origin,
      int sizeX,
      int sizeZ,
      int baseline,
      int[][] topGround,
      boolean[][] canBuild,
      boolean[][] danger,
      boolean[][] water,
      boolean[][] buildingForbidden,
      short[][] spaceAbove
   ) {
      return new VillageTerrainMap(origin, sizeX, sizeZ, baseline, topGround, canBuild, danger, water, buildingForbidden, spaceAbove);
   }

   VillageTerrainMap cropCentre(int radius) {
      int off = this.sizeX / 2 - radius;
      if (off < 0) {
         throw new IllegalArgumentException("radius " + radius + " exceeds map");
      }

      int n = 2 * radius + 1;
      return this.crop(this.origin.getX() + off, this.origin.getZ() + off, n, n);
   }

   VillageTerrainMap crop(int startX, int startZ, int sizeX, int sizeZ) {
      int offX = startX - this.origin.getX();
      int offZ = startZ - this.origin.getZ();
      if (offX >= 0 && offZ >= 0 && offX + sizeX <= this.sizeX && offZ + sizeZ <= this.sizeZ) {
         int[][] t = new int[sizeX][sizeZ];
         boolean[][] cb = new boolean[sizeX][sizeZ];
         boolean[][] d = new boolean[sizeX][sizeZ];
         boolean[][] w = new boolean[sizeX][sizeZ];
         boolean[][] f = new boolean[sizeX][sizeZ];
         short[][] s = new short[sizeX][sizeZ];
         int[][] wf = new int[sizeX][sizeZ];

         for (int x = 0; x < sizeX; x++) {
            System.arraycopy(this.topGround[x + offX], offZ, t[x], 0, sizeZ);
            System.arraycopy(this.canBuild[x + offX], offZ, cb[x], 0, sizeZ);
            System.arraycopy(this.danger[x + offX], offZ, d[x], 0, sizeZ);
            System.arraycopy(this.water[x + offX], offZ, w[x], 0, sizeZ);
            System.arraycopy(this.buildingForbidden[x + offX], offZ, f[x], 0, sizeZ);
            System.arraycopy(this.spaceAbove[x + offX], offZ, s[x], 0, sizeZ);
            System.arraycopy(this.waterFloor[x + offX], offZ, wf[x], 0, sizeZ);
         }

         VillageTerrainMap copy = new VillageTerrainMap(new BlockPos(startX, this.origin.getY(), startZ), sizeX, sizeZ, this.baseline, t, cb, d, w, f, s, wf);

         for (int x = 0; x < sizeX; x++) {
            System.arraycopy(this.occupied[x + offX], offZ, copy.occupied[x], 0, sizeZ);
         }

         copy.scanRange = this.scanRange;
         return copy;
      } else {
         throw new IllegalArgumentException(
            "crop "
               + startX
               + "/"
               + startZ
               + " "
               + sizeX
               + "x"
               + sizeZ
               + " exceeds map "
               + this.origin.getX()
               + "/"
               + this.origin.getZ()
               + " "
               + this.sizeX
               + "x"
               + this.sizeZ
         );
      }
   }

   private static boolean isDangerous(BlockState state) {
      return state.is(Blocks.LAVA)
         || state.is(Blocks.FIRE)
         || state.is(Blocks.SOUL_FIRE)
         || state.is(Blocks.CACTUS)
         || state.is(Blocks.TNT)
         || state.is(Blocks.MAGMA_BLOCK)
         || state.is(Blocks.SWEET_BERRY_BUSH)
         || state.is(Blocks.POWDER_SNOW);
   }

   private static boolean isForbidden(ServerLevel level, BlockPos pos, BlockState state) {
      if (state.isAir()) {
         return false;
      } else if (state.is(ModTags.Blocks.FORBIDDEN_EXCEPTIONS)) {
         return false;
      } else if (level.getBlockEntity(pos) != null) {
         return true;
      } else {
         return state.getBlock() instanceof StainedGlassBlock || state.getBlock() instanceof StainedGlassPaneBlock
            ? true
            : state.is(ModTags.Blocks.ARTIFICIAL_BLOCKS);
      }
   }

   static boolean isGround(BlockState state) {
      if (state.isAir()) {
         return false;
      } else if (!state.getFluidState().isEmpty()) {
         return false;
      } else if (state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS)) {
         return false;
      } else if (TerrainPreparer.isHugeMushroomBlock(state)) {
         return false;
      } else {
         return state.is(BlockTags.REPLACEABLE) ? false : state.canOcclude();
      }
   }

   public static VillageTerrainMap compute(ServerLevel level, BlockPos villageCenter, int radius) {
      return compute(level, villageCenter, radius, 25);
   }

   public static VillageTerrainMap compute(ServerLevel level, BlockPos villageCenter, int radius, int scanRange) {
      int size = 2 * radius + 1;
      return scan(level, villageCenter, villageCenter.getX() - radius, villageCenter.getZ() - radius, size, size, scanRange);
   }

   public static VillageTerrainMap computeForPlacement(
      ServerLevel level, BlockPos villageCenter, int radius, Collection<VillageTerrainMap.FootprintRect> buildings
   ) {
      int[] b = placementBounds(villageCenter.getX(), villageCenter.getZ(), radius, buildings);
      return scan(level, villageCenter, b[0], b[1], b[2], b[3], 25);
   }

   public static boolean placementAreaLoaded(ServerLevel level, int x, int z, int radius) {
      int[] b = placementBounds(x, z, radius, List.of());
      return level.hasChunksAt(new BlockPos(b[0] - 16, 0, b[1] - 16), new BlockPos(b[0] + b[2] + 15, 0, b[1] + b[3] + 15));
   }

   static int[] placementBounds(int centreX, int centreZ, int radius, Collection<VillageTerrainMap.FootprintRect> buildings) {
      int startX = centreX;
      int startZ = centreZ;
      int endX = centreX;
      int endZ = centreZ;

      for (VillageTerrainMap.FootprintRect r : buildings) {
         startX = Math.min(startX, r.startX());
         startZ = Math.min(startZ, r.startZ());
         endX = Math.max(endX, r.startX() + r.width() - 1);
         endZ = Math.max(endZ, r.startZ() + r.depth() - 1);
      }

      startX = Math.min(startX - 5, centreX - radius - 5);
      startZ = Math.min(startZ - 5, centreZ - radius - 5);
      endX = Math.max(endX + 5, centreX + radius + 5);
      endZ = Math.max(endZ + 5, centreZ + radius + 5);
      int mapStartX = startX >> 4 << 4;
      int mapStartZ = startZ >> 4 << 4;
      return new int[]{mapStartX, mapStartZ, ((endX >> 4) + 1 << 4) - mapStartX, ((endZ >> 4) + 1 << 4) - mapStartZ};
   }

   private static VillageTerrainMap scan(ServerLevel level, BlockPos villageCenter, int originX, int originZ, int sizeX, int sizeZ, int scanRange) {
      BlockPos origin = new BlockPos(originX, villageCenter.getY(), originZ);
      int baseline = villageCenter.getY();
      int miny = Math.max(baseline - scanRange, level.getMinBuildHeight());
      int maxy = Math.min(baseline + scanRange, level.getMaxBuildHeight());
      VillageTerrainMap map = new VillageTerrainMap(
         origin,
         sizeX,
         sizeZ,
         baseline,
         new int[sizeX][sizeZ],
         new boolean[sizeX][sizeZ],
         new boolean[sizeX][sizeZ],
         new boolean[sizeX][sizeZ],
         new boolean[sizeX][sizeZ],
         new short[sizeX][sizeZ],
         new int[sizeX][sizeZ]
      );
      map.scanRange = scanRange;

      for (int lx = 0; lx < sizeX; lx++) {
         for (int lz = 0; lz < sizeZ; lz++) {
            map.scanColumn(level, lx, lz, miny, maxy);
         }
      }

      return map;
   }

   private void scanColumn(ServerLevel level, int lx, int lz, int miny, int maxy) {
      int wx = this.origin.getX() + lx;
      int wz = this.origin.getZ() + lz;
      boolean hasForbidden = false;
      int groundY = miny;

      for (int y = maxy; y >= miny; y--) {
         BlockPos pos = new BlockPos(wx, y, wz);
         BlockState state = level.getBlockState(pos);
         if (isForbidden(level, pos, state)) {
            hasForbidden = true;
         }

         if (isGround(state)) {
            groundY = y;
            break;
         }
      }

      boolean onGround = true;
      int lastLiquid = groundY;
      int surfaceY = groundY;

      for (int y = groundY + 1; y <= maxy; surfaceY = y++) {
         BlockState state = level.getBlockState(new BlockPos(wx, y, wz));
         if (!state.getFluidState().isEmpty()) {
            onGround = false;
            lastLiquid = y;
         } else {
            if (!state.canOcclude() || state.is(BlockTags.REPLACEABLE)) {
               surfaceY = y;
               break;
            }

            onGround = true;
         }
      }

      if (!onGround) {
         surfaceY = lastLiquid;
      }

      this.topGround[lx][lz] = surfaceY;
      BlockState groundState = level.getBlockState(new BlockPos(wx, groundY, wz));
      boolean hasDanger = isDangerous(groundState);
      if (!hasDanger) {
         BlockState surfaceState = level.getBlockState(new BlockPos(wx, surfaceY, wz));
         hasDanger = isDangerous(surfaceState);
      }

      boolean hasWater = level.getFluidState(new BlockPos(wx, surfaceY, wz)).is(FluidTags.WATER);

      for (int y = surfaceY; y <= maxy; y++) {
         BlockPos pos = new BlockPos(wx, y, wz);
         BlockState state = level.getBlockState(pos);
         if (isForbidden(level, pos, state)) {
            hasForbidden = true;
         }

         if (isDangerous(state)) {
            hasDanger = true;
         }
      }

      short sa = 0;
      boolean blocked = false;

      for (int y = surfaceY; y < surfaceY + 3 && y <= maxy; y++) {
         BlockState state = level.getBlockState(new BlockPos(wx, y, wz));
         if (blocked || state.canOcclude() && !state.is(BlockTags.REPLACEABLE)) {
            blocked = true;
         } else {
            sa++;
         }
      }

      this.danger[lx][lz] = hasDanger;
      this.water[lx][lz] = hasWater;
      this.waterFloor[lx][lz] = hasWater ? groundY + 1 : surfaceY;
      this.buildingForbidden[lx][lz] = hasForbidden;
      this.spaceAbove[lx][lz] = sa;
      this.canBuild[lx][lz] = isAltitudeBuildable(surfaceY, this.baseline) && !hasDanger && !hasForbidden;
   }

   public int widenScanRange(ServerLevel level, int wider) {
      if (wider <= this.scanRange) {
         return 0;
      }

      int oldMin = Math.max(this.baseline - this.scanRange, level.getMinBuildHeight());
      int oldMax = Math.min(this.baseline + this.scanRange, level.getMaxBuildHeight());
      int miny = Math.max(this.baseline - wider, level.getMinBuildHeight());
      int maxy = Math.min(this.baseline + wider, level.getMaxBuildHeight());
      int rescanned = 0;

      for (int lx = 0; lx < this.sizeX; lx++) {
         for (int lz = 0; lz < this.sizeZ; lz++) {
            if (!this.occupied[lx][lz]) {
               boolean clipped = this.topGround[lx][lz] >= oldMax || this.waterFloor[lx][lz] <= oldMin + 1;
               if (clipped) {
                  this.scanColumn(level, lx, lz, miny, maxy);
                  rescanned++;
               }
            }
         }
      }

      this.scanRange = wider;
      return rescanned;
   }

   static boolean isAltitudeBuildable(int surfaceY, int baseline) {
      return surfaceY > baseline - 10 && surfaceY < baseline + 10;
   }

   static int minBuildableSurface(int baseline) {
      return baseline - 10 + 1;
   }

   static int maxBuildableSurface(int baseline) {
      return baseline + 10 - 1;
   }

   static int scanRange() {
      return 25;
   }

   public int getScanRange() {
      return this.scanRange;
   }

   void setScanRange(int scanRange) {
      this.scanRange = scanRange;
   }

   VillageTerrainMap copy() {
      VillageTerrainMap c = new VillageTerrainMap(
         this.origin,
         this.sizeX,
         this.sizeZ,
         this.baseline,
         deepCopy(this.topGround),
         deepCopy(this.canBuild),
         deepCopy(this.danger),
         deepCopy(this.water),
         deepCopy(this.buildingForbidden),
         deepCopy(this.spaceAbove),
         deepCopy(this.waterFloor)
      );

      for (int x = 0; x < this.sizeX; x++) {
         System.arraycopy(this.occupied[x], 0, c.occupied[x], 0, this.sizeZ);
      }

      c.scanRange = this.scanRange;
      c.apronLo = this.apronLo == null ? null : deepCopy(this.apronLo);
      c.apronHi = this.apronHi == null ? null : deepCopy(this.apronHi);
      c.apronOwner = this.apronOwner == null ? null : deepCopy(this.apronOwner);
      c.apronOwners.addAll(this.apronOwners);
      c.trackAprons = this.trackAprons;
      c.apronFlat = this.apronFlat;
      c.trimBarrier = this.trimBarrier == null ? null : deepCopy(this.trimBarrier);
      c.wallBand = this.wallBand == null ? null : deepCopy(this.wallBand);
      return c;
   }

   void markCellOccupied(int lx, int lz) {
      this.occupied[lx][lz] = true;
   }

   VillageTerrainMap.CellSnapshot snapshotCell(int lx, int lz) {
      return new VillageTerrainMap.CellSnapshot(
         lx, lz, this.topGround[lx][lz], this.spaceAbove[lx][lz], this.canBuild[lx][lz], this.water[lx][lz], this.waterFloor[lx][lz]
      );
   }

   void restoreCell(VillageTerrainMap.CellSnapshot s) {
      this.topGround[s.lx()][s.lz()] = s.topGround();
      this.spaceAbove[s.lx()][s.lz()] = s.spaceAbove();
      this.canBuild[s.lx()][s.lz()] = s.canBuild();
      this.water[s.lx()][s.lz()] = s.water();
      this.waterFloor[s.lx()][s.lz()] = s.waterFloor();
   }

   void simulateSurface(int lx, int lz, int surfaceY) {
      this.simulateSurface(lx, lz, surfaceY, 9);
   }

   void simulateSurface(int lx, int lz, int surfaceY, int gate) {
      this.topGround[lx][lz] = surfaceY;
      this.waterFloor[lx][lz] = surfaceY;
      this.spaceAbove[lx][lz] = 3;
      this.water[lx][lz] = false;
      this.canBuild[lx][lz] = Math.abs(surfaceY - this.baseline) <= gate && !this.danger[lx][lz] && !this.buildingForbidden[lx][lz];
   }

   public void relaxAltitudeGate(VillageTerrainMap.FootprintRect rect, int gate) {
      if (gate > 9) {
         for (int dx = 0; dx < rect.width(); dx++) {
            for (int dz = 0; dz < rect.depth(); dz++) {
               int lx = this.toLocalX(rect.startX() + dx);
               int lz = this.toLocalZ(rect.startZ() + dz);
               if (this.inBounds(lx, lz)
                  && !this.occupied[lx][lz]
                  && Math.abs(this.topGround[lx][lz] - this.baseline) <= gate
                  && !this.danger[lx][lz]
                  && !this.buildingForbidden[lx][lz]
                  && !this.water[lx][lz]) {
                  this.canBuild[lx][lz] = true;
               }
            }
         }
      }
   }

   public int getWaterFloor(int localX, int localZ) {
      if (!this.inBounds(localX, localZ)) {
         return 0;
      } else {
         return this.water[localX][localZ] ? this.waterFloor[localX][localZ] : this.topGround[localX][localZ];
      }
   }

   void setWaterFloor(int lx, int lz, int floor) {
      this.waterFloor[lx][lz] = floor;
   }

   private static int[][] deepCopy(int[][] a) {
      int[][] c = new int[a.length][];

      for (int i = 0; i < a.length; i++) {
         c[i] = (int[])a[i].clone();
      }

      return c;
   }

   private static boolean[][] deepCopy(boolean[][] a) {
      boolean[][] c = new boolean[a.length][];

      for (int i = 0; i < a.length; i++) {
         c[i] = (boolean[])a[i].clone();
      }

      return c;
   }

   private static short[][] deepCopy(short[][] a) {
      short[][] c = new short[a.length][];

      for (int i = 0; i < a.length; i++) {
         c[i] = (short[])a[i].clone();
      }

      return c;
   }

   public int toLocalX(int worldX) {
      return worldX - this.origin.getX();
   }

   public int toLocalZ(int worldZ) {
      return worldZ - this.origin.getZ();
   }

   public boolean inBounds(int localX, int localZ) {
      return localX >= 0 && localX < this.sizeX && localZ >= 0 && localZ < this.sizeZ;
   }

   public int testFootprint(int worldX, int worldZ, int width, int depth, ClearMargins margins, Rotation rotation) {
      VillageTerrainMap.FootprintRect rect = computeFootprintRect(worldX, worldZ, width, depth, margins, rotation);
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
            int lx = this.toLocalX(rect.startX() + dx);
            int lz = this.toLocalZ(rect.startZ() + dz);
            if (!this.inBounds(lx, lz)) {
               return -1;
            }

            if (this.occupied[lx][lz]) {
               return -1;
            }

            if (this.buildingForbidden[lx][lz]) {
               if (!hugeBuilding || nbError > allowedErrors) {
                  return -1;
               }

               nbError++;
            } else if (this.danger[lx][lz]) {
               if (nbError > allowedErrors) {
                  return -1;
               }

               nbError++;
            } else if (this.water[lx][lz]) {
               if (nbError > allowedErrors) {
                  return -1;
               }

               nbError++;
            } else if (!this.canBuild[lx][lz]) {
               if (nbError > allowedErrors) {
                  return -1;
               }

               nbError++;
            }
         }
      }

      return nbError;
   }

   public int computeAverageAltitude(int worldX, int worldZ, int width, int depth, ClearMargins margins, Rotation rotation) {
      VillageTerrainMap.FootprintRect rect = computeFootprintRect(worldX, worldZ, width, depth, margins, rotation);
      long altitudeTotal = 0L;
      int nbPoints = 0;

      for (int dx = 0; dx < rect.width(); dx++) {
         for (int dz = 0; dz < rect.depth(); dz++) {
            int lx = this.toLocalX(rect.startX() + dx);
            int lz = this.toLocalZ(rect.startZ() + dz);
            if (this.inBounds(lx, lz)) {
               altitudeTotal += this.topGround[lx][lz];
               nbPoints++;
            }
         }
      }

      return nbPoints == 0 ? this.baseline : Math.round((float)altitudeTotal / nbPoints);
   }

   public void markBuildingFootprint(int worldX, int worldZ, int width, int depth, ClearMargins margins, Rotation rotation, int buildingY) {
      this.markBuildingFootprint(worldX, worldZ, width, depth, margins, rotation, buildingY, "?");
   }

   public void markBuildingFootprint(int worldX, int worldZ, int width, int depth, ClearMargins margins, Rotation rotation, int buildingY, String label) {
      if (PlotApron.ACTIVE || this.trackAprons) {
         this.markApron(worldX, worldZ, width, depth, margins, rotation, buildingY, label);
      }

      VillageTerrainMap.FootprintRect fullRect = computeFootprintRect(worldX, worldZ, width, depth, margins, rotation);
      VillageTerrainMap.FootprintRect coreRect = computeFootprintRect(worldX, worldZ, width, depth, ClearMargins.symmetric(0), rotation);

      for (int dx = 0; dx < coreRect.width(); dx++) {
         for (int dz = 0; dz < coreRect.depth(); dz++) {
            int lx = this.toLocalX(coreRect.startX() + dx);
            int lz = this.toLocalZ(coreRect.startZ() + dz);
            if (this.inBounds(lx, lz)) {
               this.occupied[lx][lz] = true;
            }
         }
      }

      for (int dx = 0; dx < coreRect.width(); dx++) {
         for (int dz = 0; dz < coreRect.depth(); dz++) {
            int lx = this.toLocalX(coreRect.startX() + dx);
            int lz = this.toLocalZ(coreRect.startZ() + dz);
            if (this.inBounds(lx, lz)) {
               this.topGround[lx][lz] = buildingY;
               this.spaceAbove[lx][lz] = 3;
               this.water[lx][lz] = false;
               this.danger[lx][lz] = false;
               this.canBuild[lx][lz] = true;
            }
         }
      }

      for (int dx = 0; dx < fullRect.width(); dx++) {
         for (int dz = 0; dz < fullRect.depth(); dz++) {
            int wx = fullRect.startX() + dx;
            int wz = fullRect.startZ() + dz;
            if (wx < coreRect.startX() || wx >= coreRect.startX() + coreRect.width() || wz < coreRect.startZ() || wz >= coreRect.startZ() + coreRect.depth()) {
               int lx = this.toLocalX(wx);
               int lz = this.toLocalZ(wz);
               if (this.inBounds(lx, lz) && !this.occupied[lx][lz]) {
                  this.canBuild[lx][lz] = true;
                  this.water[lx][lz] = false;
                  this.danger[lx][lz] = false;
               }
            }
         }
      }
   }

   public void markOccupied(int worldX, int worldZ, int width, int depth, Rotation rotation) {
      this.markOccupied(worldX, worldZ, width, depth, ClearMargins.symmetric(0), rotation);
   }

   public void markOccupied(int worldX, int worldZ, int width, int depth, ClearMargins margins, Rotation rotation) {
      VillageTerrainMap.FootprintRect rect = computeFootprintRect(worldX, worldZ, width, depth, margins, rotation);

      for (int dx = 0; dx < rect.width(); dx++) {
         for (int dz = 0; dz < rect.depth(); dz++) {
            int lx = this.toLocalX(rect.startX() + dx);
            int lz = this.toLocalZ(rect.startZ() + dz);
            if (this.inBounds(lx, lz)) {
               this.occupied[lx][lz] = true;
            }
         }
      }
   }

   boolean footprintHitsOccupied(int worldX, int worldZ, int width, int depth, ClearMargins margins, Rotation rotation) {
      VillageTerrainMap.FootprintRect rect = computeFootprintRect(worldX, worldZ, width, depth, margins, rotation);

      for (int wx = rect.startX(); wx < rect.startX() + rect.width(); wx++) {
         for (int wz = rect.startZ(); wz < rect.startZ() + rect.depth(); wz++) {
            if (this.isOccupied(this.toLocalX(wx), this.toLocalZ(wz))) {
               return true;
            }
         }
      }

      return false;
   }

   public void markTrimBarrier(int worldX, int worldZ, int width, int depth, ClearMargins margins, Rotation rotation) {
      if (PlotApron.TRIM || this.trackAprons) {
         if (this.trimBarrier == null) {
            this.trimBarrier = new boolean[this.sizeX][this.sizeZ];
         }

         VillageTerrainMap.FootprintRect rect = computeFootprintRect(worldX, worldZ, width, depth, margins, rotation);

         for (int wx = rect.startX(); wx < rect.startX() + rect.width(); wx++) {
            for (int wz = rect.startZ(); wz < rect.startZ() + rect.depth(); wz++) {
               int lx = this.toLocalX(wx);
               int lz = this.toLocalZ(wz);
               if (this.inBounds(lx, lz)) {
                  this.trimBarrier[lx][lz] = true;
               }
            }
         }
      }
   }

   int freeCells(int start, int dirX, int dirZ, int perpLo, int perpHi, int max) {
      for (int k = 0; k < max; k++) {
         int c = start + k * (dirX != 0 ? dirX : dirZ);

         for (int p = perpLo; p <= perpHi; p++) {
            int lx = this.toLocalX(dirX != 0 ? c : p);
            int lz = this.toLocalZ(dirX != 0 ? p : c);
            if (this.inBounds(lx, lz) && (this.occupied[lx][lz] || this.trimBarrier != null && this.trimBarrier[lx][lz])) {
               return k;
            }
         }
      }

      return max;
   }

   public int[][] sideGround(VillageTerrainMap.FootprintRect band, int depth) {
      int x0 = band.startX();
      int x1 = band.startX() + band.width() - 1;
      int z0 = band.startZ();
      int z1 = band.startZ() + band.depth() - 1;
      return new int[][]{
         this.stripGround(x0 - depth, x0 - 1, z0, z1),
         this.stripGround(x1 + 1, x1 + depth, z0, z1),
         this.stripGround(x0, x1, z0 - depth, z0 - 1),
         this.stripGround(x0, x1, z1 + 1, z1 + depth)
      };
   }

   private int[] stripGround(int xa, int xb, int za, int zb) {
      long total = 0L;
      int open = 0;

      for (int wx = xa; wx <= xb; wx++) {
         for (int wz = za; wz <= zb; wz++) {
            int lx = this.toLocalX(wx);
            int lz = this.toLocalZ(wz);
            if (this.inBounds(lx, lz)
               && this.canBuild[lx][lz]
               && !this.occupied[lx][lz]
               && !this.water[lx][lz]
               && !this.buildingForbidden[lx][lz]
               && (this.apronOwner == null || this.apronOwner[lx][lz] < 0)) {
               total += this.topGround[lx][lz];
               open++;
            }
         }
      }

      int cells = (xb - xa + 1) * (zb - za + 1);
      return open != 0 && open * 2 >= cells ? new int[]{Math.round((float)total / open), open} : new int[]{0, 0};
   }

   public boolean hasAprons() {
      return this.apronLo != null;
   }

   public VillageTerrainMap.ApronOwner apronOwner(int index) {
      return this.apronOwners.get(index);
   }

   private void markApron(int worldX, int worldZ, int width, int depth, ClearMargins margins, Rotation rotation, int y, String label) {
      if (this.apronLo == null) {
         this.apronLo = new int[this.sizeX][this.sizeZ];
         this.apronHi = new int[this.sizeX][this.sizeZ];
         this.apronOwner = new int[this.sizeX][this.sizeZ];

         for (int x = 0; x < this.sizeX; x++) {
            Arrays.fill(this.apronLo[x], Integer.MIN_VALUE);
            Arrays.fill(this.apronHi[x], Integer.MAX_VALUE);
            Arrays.fill(this.apronOwner[x], -1);
         }
      }

      VillageTerrainMap.FootprintRect core = computeFootprintRect(worldX, worldZ, width, depth, ClearMargins.symmetric(0), rotation);
      VillageTerrainMap.FootprintRect full = computeFootprintRect(worldX, worldZ, width, depth, margins, rotation);
      int owner = this.apronOwners.size();
      this.apronOwners.add(new VillageTerrainMap.ApronOwner(label, core, full, y));

      for (int wx = full.startX(); wx < full.startX() + full.width(); wx++) {
         int ox = TerrainTraversal.computeOffsetComponent(wx, core.startX(), core.width());

         for (int wz = full.startZ(); wz < full.startZ() + full.depth(); wz++) {
            int lx = this.toLocalX(wx);
            int lz = this.toLocalZ(wz);
            if (this.inBounds(lx, lz)) {
               int oz = TerrainTraversal.computeOffsetComponent(wz, core.startZ(), core.depth());
               int t = PlotApron.tolerance(PlotApron.offset(ox, oz, this.apronFlat));
               int lo = Math.max(this.apronLo[lx][lz], y - t);
               int hi = Math.min(this.apronHi[lx][lz], y + t);
               if (lo <= hi) {
                  if (lo != this.apronLo[lx][lz] || hi != this.apronHi[lx][lz]) {
                     this.apronOwner[lx][lz] = owner;
                  }

                  this.apronLo[lx][lz] = lo;
                  this.apronHi[lx][lz] = hi;
               }
            }
         }
      }
   }

   @Nullable
   public PlotApron.Neighbours neighboursOf(int worldX, int worldZ, int width, int depth, Rotation rotation) {
      if (this.apronLo == null) {
         return null;
      }

      VillageTerrainMap.FootprintRect self = computeFootprintRect(worldX, worldZ, width, depth, ClearMargins.symmetric(0), rotation);
      List<PlotApron.Band> others = new ArrayList<>();

      for (VillageTerrainMap.ApronOwner o : this.apronOwners) {
         if (!o.core().equals(self)) {
            others.add(new PlotApron.Band(o.core(), o.band(), o.y()));
         }
      }

      return PlotApron.neighbours(others);
   }

   public void markWallBand(int worldX, int worldZ, int width, int depth, ClearMargins margins, Rotation rotation) {
      if (this.wallBand == null) {
         this.wallBand = new boolean[this.sizeX][this.sizeZ];
      }

      VillageTerrainMap.FootprintRect rect = computeFootprintRect(worldX, worldZ, width, depth, margins, rotation);

      for (int wx = rect.startX(); wx < rect.startX() + rect.width(); wx++) {
         for (int wz = rect.startZ(); wz < rect.startZ() + rect.depth(); wz++) {
            int lx = this.toLocalX(wx);
            int lz = this.toLocalZ(wz);
            if (this.inBounds(lx, lz)) {
               this.wallBand[lx][lz] = true;
            }
         }
      }
   }

   public void markPlannedWall(int worldX, int worldZ, int width, int depth, ClearMargins margins, Rotation rotation, int y, String label) {
      if (PlotApron.ACTIVE || this.trackAprons) {
         this.markApron(worldX, worldZ, width, depth, margins, rotation, y, label);
      }

      this.markOccupied(worldX, worldZ, width, depth, rotation);
      this.markWallBand(worldX, worldZ, width, depth, margins, rotation);
   }

   public boolean coreHitsWallBand(int worldX, int worldZ, int width, int depth, Rotation rotation) {
      if (this.wallBand == null) {
         return false;
      }

      VillageTerrainMap.FootprintRect core = computeFootprintRect(worldX, worldZ, width, depth, ClearMargins.symmetric(0), rotation);

      for (int wx = core.startX(); wx < core.startX() + core.width(); wx++) {
         for (int wz = core.startZ(); wz < core.startZ() + core.depth(); wz++) {
            int lx = this.toLocalX(wx);
            int lz = this.toLocalZ(wz);
            if (this.inBounds(lx, lz) && this.wallBand[lx][lz]) {
               return true;
            }
         }
      }

      return false;
   }

   public PlotApron.Clash apronClash(int worldX, int worldZ, int width, int depth, ClearMargins margins, Rotation rotation, int y) {
      if (this.apronLo == null) {
         return PlotApron.Clash.NONE;
      }

      VillageTerrainMap.FootprintRect core = computeFootprintRect(worldX, worldZ, width, depth, ClearMargins.symmetric(0), rotation);
      VillageTerrainMap.FootprintRect full = computeFootprintRect(worldX, worldZ, width, depth, margins, rotation);
      int shared = 0;
      int clashing = 0;
      int worst = 0;
      int sum = 0;
      int owner = -1;

      for (int wx = full.startX(); wx < full.startX() + full.width(); wx++) {
         int ox = TerrainTraversal.computeOffsetComponent(wx, core.startX(), core.width());

         for (int wz = full.startZ(); wz < full.startZ() + full.depth(); wz++) {
            int lx = this.toLocalX(wx);
            int lz = this.toLocalZ(wz);
            if (this.inBounds(lx, lz) && this.apronOwner[lx][lz] >= 0) {
               int oz = TerrainTraversal.computeOffsetComponent(wz, core.startZ(), core.depth());
               int t = PlotApron.tolerance(PlotApron.offset(ox, oz, this.apronFlat));
               shared++;
               int gap = Math.max(this.apronLo[lx][lz] - (y + t), y - t - this.apronHi[lx][lz]);
               if (gap > 0) {
                  clashing++;
                  sum += gap;
                  if (gap > worst) {
                     worst = gap;
                     owner = this.apronOwner[lx][lz];
                  }
               }
            }
         }
      }

      return new PlotApron.Clash(shared, clashing, worst, sum, owner);
   }

   public int getSize() {
      return this.sizeX;
   }

   public int getSizeX() {
      return this.sizeX;
   }

   public int getSizeZ() {
      return this.sizeZ;
   }

   public int getOriginX() {
      return this.origin.getX();
   }

   public int getOriginZ() {
      return this.origin.getZ();
   }

   public int getBaseline() {
      return this.baseline;
   }

   public int getGroundHeight(int worldX, int worldZ) {
      int lx = this.toLocalX(worldX);
      int lz = this.toLocalZ(worldZ);
      return !this.inBounds(lx, lz) ? this.baseline : this.topGround[lx][lz];
   }

   public int getTopGround(int localX, int localZ) {
      return this.inBounds(localX, localZ) ? this.topGround[localX][localZ] : 0;
   }

   public boolean canBuildAt(int localX, int localZ) {
      return this.inBounds(localX, localZ) && this.canBuild[localX][localZ];
   }

   public boolean isDangerAt(int localX, int localZ) {
      return this.inBounds(localX, localZ) && this.danger[localX][localZ];
   }

   public boolean isWaterAt(int localX, int localZ) {
      return this.inBounds(localX, localZ) && this.water[localX][localZ];
   }

   public boolean isBuildingForbiddenAt(int localX, int localZ) {
      return this.inBounds(localX, localZ) && this.buildingForbidden[localX][localZ];
   }

   public short getSpaceAbove(int localX, int localZ) {
      return this.inBounds(localX, localZ) ? this.spaceAbove[localX][localZ] : 0;
   }

   public boolean isOccupied(int localX, int localZ) {
      return this.inBounds(localX, localZ) && this.occupied[localX][localZ];
   }

   public static VillageTerrainMap.FootprintRect computeFootprintRect(int worldX, int worldZ, int width, int depth, ClearMargins margins, Rotation rotation) {
      ClearMargins effective = margins.forRotation(rotation);
      int startX;
      int startZ;
      int effectiveWidth;
      int effectiveDepth;
      switch (rotation) {
         case CLOCKWISE_90:
            startX = worldX - depth + 1 - effective.lengthBefore();
            startZ = worldZ - effective.widthBefore();
            effectiveWidth = depth + effective.lengthBefore() + effective.lengthAfter();
            effectiveDepth = width + effective.widthBefore() + effective.widthAfter();
            break;
         case CLOCKWISE_180:
            startX = worldX - width + 1 - effective.lengthBefore();
            startZ = worldZ - depth + 1 - effective.widthBefore();
            effectiveWidth = width + effective.lengthBefore() + effective.lengthAfter();
            effectiveDepth = depth + effective.widthBefore() + effective.widthAfter();
            break;
         case COUNTERCLOCKWISE_90:
            startX = worldX - effective.lengthBefore();
            startZ = worldZ - width + 1 - effective.widthBefore();
            effectiveWidth = depth + effective.lengthBefore() + effective.lengthAfter();
            effectiveDepth = width + effective.widthBefore() + effective.widthAfter();
            break;
         default:
            startX = worldX - effective.lengthBefore();
            startZ = worldZ - effective.widthBefore();
            effectiveWidth = width + effective.lengthBefore() + effective.lengthAfter();
            effectiveDepth = depth + effective.widthBefore() + effective.widthAfter();
      }

      return new VillageTerrainMap.FootprintRect(startX, startZ, effectiveWidth, effectiveDepth);
   }

   public record ApronOwner(String label, VillageTerrainMap.FootprintRect core, VillageTerrainMap.FootprintRect band, int y) {
   }

   record CellSnapshot(int lx, int lz, int topGround, short spaceAbove, boolean canBuild, boolean water, int waterFloor) {
   }

   public record FootprintRect(int startX, int startZ, int width, int depth) {
   }
}
