package org.millenaire.world;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.millenaire.building.BuildingPlan;
import org.millenaire.building.ClearMargins;

public record TerrainSnapshot(
   String villageName, BlockPos center, VillageTerrainMap map, byte[][] protection, @Nullable TerrainSnapshot.Query query, boolean waterDepthKnown
) {
   private static final String MAGIC = "millenaire-terrain-snapshot 2";
   private static final String MAGIC_V1 = "millenaire-terrain-snapshot 1";

   public TerrainSnapshot(String villageName, BlockPos center, VillageTerrainMap map, byte[][] protection, @Nullable TerrainSnapshot.Query query) {
      this(villageName, center, map, protection, query, true);
   }

   public void write(Path file) throws IOException {
      Files.createDirectories(file.toAbsolutePath().getParent());

      try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(file)), StandardCharsets.UTF_8))) {
         int sizeX = this.map.getSizeX();
         int sizeZ = this.map.getSizeZ();
         w.write("millenaire-terrain-snapshot 2");
         w.newLine();
         w.write("village " + this.villageName.replace('\n', ' '));
         w.newLine();
         w.write("center " + this.center.getX() + " " + this.center.getY() + " " + this.center.getZ());
         w.newLine();
         w.write("origin " + this.map.getOriginX() + " " + this.map.getOriginZ());
         w.newLine();
         w.write("size " + sizeX + (sizeX == sizeZ ? "" : " " + sizeZ));
         w.newLine();
         w.write("baseline " + this.map.getBaseline());
         w.newLine();
         w.write("range " + this.map.getScanRange());
         w.newLine();
         if (this.query != null) {
            ClearMargins m = this.query.margins();
            w.write(
               "query "
                  + this.query.planSetId()
                  + " "
                  + this.query.width()
                  + " "
                  + this.query.depth()
                  + " "
                  + this.query.groundLevel()
                  + " "
                  + this.query.buildingOrientation()
                  + " "
                  + m.lengthBefore()
                  + " "
                  + m.lengthAfter()
                  + " "
                  + m.widthBefore()
                  + " "
                  + m.widthAfter()
                  + " "
                  + this.query.minDistance()
                  + " "
                  + this.query.maxDistance()
                  + " "
                  + (this.query.fixedOrientation() == null ? "-" : this.query.fixedOrientation())
            );
            w.newLine();
         }

         w.write("rows");
         w.newLine();
         StringBuilder line = new StringBuilder(sizeZ * 7);

         for (int lx = 0; lx < sizeX; lx++) {
            line.setLength(0);

            for (int lz = 0; lz < sizeZ; lz++) {
               int flags = (this.map.canBuildAt(lx, lz) ? 1 : 0)
                  | (this.map.isDangerAt(lx, lz) ? 2 : 0)
                  | (this.map.isWaterAt(lx, lz) ? 4 : 0)
                  | (this.map.isBuildingForbiddenAt(lx, lz) ? 8 : 0)
                  | (this.map.isOccupied(lx, lz) ? 16 : 0)
                  | Math.min(3, this.map.getSpaceAbove(lx, lz)) << 5
                  | (this.protection[lx][lz] & 3) << 7;
               if (lz > 0) {
                  line.append(' ');
               }

               line.append(this.map.getTopGround(lx, lz)).append('/').append(Integer.toHexString(flags));
               if (this.map.isWaterAt(lx, lz)) {
                  line.append('/').append(this.map.getWaterFloor(lx, lz));
               }
            }

            w.write(line.toString());
            w.newLine();
         }
      }
   }

   public static TerrainSnapshot read(Path file) throws IOException {
      try (BufferedReader r = new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(file)), StandardCharsets.UTF_8))) {
         String magic = r.readLine();
         if (!"millenaire-terrain-snapshot 2".equals(magic) && !"millenaire-terrain-snapshot 1".equals(magic)) {
            throw new IOException("not a terrain snapshot: " + file);
         }

         String name = "";
         BlockPos center = null;
         int originX = 0;
         int originZ = 0;
         int sizeX = -1;
         int sizeZ = -1;
         int baseline = 0;
         int range = VillageTerrainMap.scanRange();
         TerrainSnapshot.Query query = null;

         String l;
         while ((l = r.readLine()) != null && !l.equals("rows")) {
            String[] t = l.split(" ");
            switch (t[0]) {
               case "village":
                  name = l.substring(Math.min(l.length(), 8));
                  break;
               case "center":
                  center = new BlockPos(Integer.parseInt(t[1]), Integer.parseInt(t[2]), Integer.parseInt(t[3]));
                  break;
               case "origin":
                  originX = Integer.parseInt(t[1]);
                  originZ = Integer.parseInt(t[2]);
                  break;
               case "size":
                  sizeX = Integer.parseInt(t[1]);
                  sizeZ = t.length > 2 ? Integer.parseInt(t[2]) : sizeX;
                  break;
               case "baseline":
                  baseline = Integer.parseInt(t[1]);
                  break;
               case "range":
                  range = Integer.parseInt(t[1]);
                  break;
               case "query":
                  query = new TerrainSnapshot.Query(
                     ResourceLocation.parse(t[1]),
                     Integer.parseInt(t[2]),
                     Integer.parseInt(t[3]),
                     Integer.parseInt(t[4]),
                     Integer.parseInt(t[5]),
                     new ClearMargins(Integer.parseInt(t[6]), Integer.parseInt(t[7]), Integer.parseInt(t[8]), Integer.parseInt(t[9])),
                     Integer.parseInt(t[10]),
                     Integer.parseInt(t[11]),
                     "-".equals(t[12]) ? null : Integer.parseInt(t[12])
                  );
            }
         }

         if (sizeX <= 0 || sizeZ <= 0 || center == null) {
            throw new IOException("incomplete snapshot header: " + file);
         }

         int[][] top = new int[sizeX][sizeZ];
         boolean[][] canBuild = new boolean[sizeX][sizeZ];
         boolean[][] danger = new boolean[sizeX][sizeZ];
         boolean[][] water = new boolean[sizeX][sizeZ];
         boolean[][] forbidden = new boolean[sizeX][sizeZ];
         short[][] space = new short[sizeX][sizeZ];
         boolean[][] occupied = new boolean[sizeX][sizeZ];
         byte[][] protection = new byte[sizeX][sizeZ];
         int[][] floor = new int[sizeX][sizeZ];

         for (int lx = 0; lx < sizeX; lx++) {
            String row = r.readLine();
            if (row == null) {
               throw new IOException("truncated snapshot: " + file);
            }

            String[] cells = row.split(" ");

            for (int lz = 0; lz < sizeZ; lz++) {
               String[] parts = cells[lz].split("/");
               top[lx][lz] = Integer.parseInt(parts[0]);
               int flags = Integer.parseInt(parts[1], 16);
               floor[lx][lz] = parts.length > 2 ? Integer.parseInt(parts[2]) : top[lx][lz];
               canBuild[lx][lz] = (flags & 1) != 0;
               danger[lx][lz] = (flags & 2) != 0;
               water[lx][lz] = (flags & 4) != 0;
               forbidden[lx][lz] = (flags & 8) != 0;
               occupied[lx][lz] = (flags & 16) != 0;
               space[lx][lz] = (short)(flags >> 5 & 3);
               protection[lx][lz] = (byte)(flags >> 7 & 3);
            }
         }

         VillageTerrainMap map = VillageTerrainMap.forTest(
            new BlockPos(originX, baseline, originZ), sizeX, sizeZ, baseline, top, canBuild, danger, water, forbidden, space
         );
         map.setScanRange(range);

         for (int lx = 0; lx < sizeX; lx++) {
            for (int lz = 0; lz < sizeZ; lz++) {
               if (occupied[lx][lz]) {
                  map.markCellOccupied(lx, lz);
               }

               map.setWaterFloor(lx, lz, floor[lx][lz]);
            }
         }

         return new TerrainSnapshot(name, center, map, protection, query, "millenaire-terrain-snapshot 2".equals(magic));
      }
   }

   public record Query(
      ResourceLocation planSetId,
      int width,
      int depth,
      int groundLevel,
      int buildingOrientation,
      ClearMargins margins,
      int minDistance,
      int maxDistance,
      @Nullable Integer fixedOrientation
   ) {
      public BuildingPlan plan() {
         return new BuildingPlan(
            this.planSetId,
            this.planSetId,
            "snapshot",
            this.width,
            10,
            this.depth,
            this.groundLevel,
            this.buildingOrientation,
            BlockPos.ZERO,
            List.of(),
            "default",
            "flat",
            List.of(),
            null
         );
      }

      public PlacementConstraints constraints() {
         return new PlacementConstraints(
            this.minDistance,
            this.maxDistance,
            Map.of(),
            Map.of(),
            Math.min(Math.min(this.margins.lengthBefore(), this.margins.lengthAfter()), Math.min(this.margins.widthBefore(), this.margins.widthAfter())),
            this.fixedOrientation
         );
      }
   }
}
