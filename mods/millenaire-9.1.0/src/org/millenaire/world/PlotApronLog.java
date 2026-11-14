package org.millenaire.world;

import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.storage.LevelResource;
import org.millenaire.building.ClearMargins;
import org.slf4j.Logger;

public final class PlotApronLog {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String HEADER = "tick,village,plan,result,x,z,rotation,avg_y,y,dist_centre,flat,trim,y_give,shared,clash_cells,worst,sum,nb_dy,nb_gap,nb,margins,margins_full,why";
   private static BufferedWriter out;
   private static Path outFile;
   private static boolean ioFailed;

   private PlotApronLog() {
   }

   public static void placed(
      ServerLevel level,
      String village,
      BlockPos villageCentre,
      String plan,
      VillageTerrainMap map,
      int pivotX,
      int pivotZ,
      int width,
      int depth,
      ClearMargins margins,
      ClearMargins full,
      Rotation rotation,
      int y
   ) {
      if (PlotApron.LOG) {
         int avg = map.computeAverageAltitude(pivotX, pivotZ, width, depth, margins, rotation);
         VillageTerrainMap.FootprintRect core = VillageTerrainMap.computeFootprintRect(pivotX, pivotZ, width, depth, ClearMargins.symmetric(0), rotation);
         int cx = core.startX() + core.width() / 2;
         int cz = core.startZ() + core.depth() / 2;
         StringBuilder row = new StringBuilder();
         row.append(level.getGameTime())
            .append(',')
            .append(csv(village))
            .append(',')
            .append(csv(plan))
            .append(",placed,")
            .append(cx)
            .append(',')
            .append(cz)
            .append(',')
            .append(rotation.name())
            .append(',')
            .append(avg)
            .append(',')
            .append(y)
            .append(',')
            .append(Math.round(Math.hypot(cx - villageCentre.getX(), cz - villageCentre.getZ())))
            .append(',')
            .append(PlotApron.FLAT)
            .append(',')
            .append(PlotApron.TRIM)
            .append(',')
            .append(PlotApron.Y_GIVE);
         PlotApron.Clash c = map.apronClash(pivotX, pivotZ, width, depth, margins, rotation, y);
         row.append(',').append(c.shared()).append(',').append(c.clashing()).append(',').append(c.worst()).append(',').append(c.sum());
         if (c.owner() >= 0) {
            VillageTerrainMap.ApronOwner nb = map.apronOwner(c.owner());
            row.append(',').append(nb.y() - y).append(',').append(freeCellsBetween(core, nb.core())).append(',').append(csv(nb.label()));
         } else {
            row.append(",,,");
         }

         String note = BuildingLocationFinder.takeNote();
         row.append(',')
            .append(world(margins, rotation))
            .append(',')
            .append(world(full, rotation))
            .append(',')
            .append("free=")
            .append(PlotApron.freeAround(map, pivotX, pivotZ, width, depth, rotation))
            .append(note != null ? " " + note : "");
         if (PlotApron.lookAhead()) {
            PlotApron.Ahead ahead = PlotApron.ahead(map, pivotX, pivotZ, width, depth, margins, rotation);
            row.append(" ahead=").append(ahead.describe()).append(" cost=").append(ahead.cost(y));
            int plainY = PlotApron.pick(map, pivotX, pivotZ, width, depth, margins, rotation, PlotApron.Y_GIVE, false).y();
            if (plainY != y) {
               row.append(" ahead_y_plain=").append(plainY);
            }
         }

         write(level, row.toString());
      }
   }

   public static void noLocation(ServerLevel level, String village, String plan) {
      if (PlotApron.LOG) {
         String why = BuildingLocationFinder.lastFailure();
         write(
            level,
            level.getGameTime()
               + ","
               + csv(village)
               + ","
               + csv(plan)
               + ",no_location,,,,,,,"
               + PlotApron.FLAT
               + ","
               + PlotApron.TRIM
               + ","
               + PlotApron.Y_GIVE
               + ",,,,,,,,,,"
               + csv(why != null ? why : "")
         );
      }
   }

   public static void wall(
      ServerLevel level,
      String village,
      BlockPos villageCentre,
      String plan,
      int pivotX,
      int pivotZ,
      int width,
      int depth,
      ClearMargins margins,
      Rotation rotation,
      int y,
      boolean built
   ) {
      if (PlotApron.LOG) {
         VillageTerrainMap.FootprintRect core = VillageTerrainMap.computeFootprintRect(pivotX, pivotZ, width, depth, ClearMargins.symmetric(0), rotation);
         int cx = core.startX() + core.width() / 2;
         int cz = core.startZ() + core.depth() / 2;
         String m = world(margins, rotation);
         write(
            level,
            level.getGameTime()
               + ","
               + csv(village)
               + ","
               + csv(plan)
               + ","
               + (built ? "wall" : "wall_planned")
               + ","
               + cx
               + ","
               + cz
               + ","
               + rotation.name()
               + ","
               + y
               + ","
               + y
               + ","
               + Math.round(Math.hypot(cx - villageCentre.getX(), cz - villageCentre.getZ()))
               + ","
               + PlotApron.FLAT
               + ","
               + PlotApron.TRIM
               + ","
               + PlotApron.Y_GIVE
               + ",,,,,,,,"
               + m
               + ","
               + m
               + ","
         );
      }
   }

   private static String world(ClearMargins m, Rotation rotation) {
      ClearMargins w = m.forRotation(rotation);
      return w.lengthBefore() + "/" + w.lengthAfter() + "/" + w.widthBefore() + "/" + w.widthAfter();
   }

   static int freeCellsBetween(VillageTerrainMap.FootprintRect a, VillageTerrainMap.FootprintRect b) {
      int gx = Math.max(b.startX() - (a.startX() + a.width()), a.startX() - (b.startX() + b.width()));
      int gz = Math.max(b.startZ() - (a.startZ() + a.depth()), a.startZ() - (b.startZ() + b.depth()));
      return Math.max(0, Math.max(gx, gz));
   }

   private static String csv(String s) {
      return s.indexOf(44) < 0 && s.indexOf(34) < 0 ? s : "\"" + s.replace("\"", "\"\"") + "\"";
   }

   private static synchronized void write(ServerLevel level, String row) {
      if (!ioFailed) {
         try {
            Path dir = level.getServer().getWorldPath(LevelResource.ROOT).resolve("millenaire-plot-aprons");
            Path file = dir.resolve("placements.csv");
            if (out != null && !file.equals(outFile)) {
               out.close();
               out = null;
            }

            if (out == null) {
               Files.createDirectories(dir);
               outFile = file;
               boolean fresh = !Files.exists(file);
               out = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
               if (fresh) {
                  out.write(
                     "tick,village,plan,result,x,z,rotation,avg_y,y,dist_centre,flat,trim,y_give,shared,clash_cells,worst,sum,nb_dy,nb_gap,nb,margins,margins_full,why"
                  );
                  out.newLine();
               }

               LOGGER.info(
                  "[Millenaire plot aprons] writing {} (flat {}, trim {}, y give {})",
                  new Object[]{file.toAbsolutePath(), PlotApron.FLAT, PlotApron.TRIM, PlotApron.Y_GIVE}
               );
            }

            out.write(row);
            out.newLine();
            out.flush();
         } catch (IOException e) {
            ioFailed = true;
            LOGGER.error("[Millenaire plot aprons] cannot write the CSV log, logging disabled", e);
         }
      }
   }
}
