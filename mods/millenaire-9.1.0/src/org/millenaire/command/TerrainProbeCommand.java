package org.millenaire.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.millenaire.building.BuildingPlan;
import org.millenaire.building.BuildingPlanSet;
import org.millenaire.building.ClearMargins;
import org.millenaire.culture.ModCultures;
import org.millenaire.culture.VillageType;
import org.millenaire.village.Village;
import org.millenaire.village.VillageGrowthManager;
import org.millenaire.village.VillageSavedData;
import org.millenaire.world.BuildingLocationFinder;
import org.millenaire.world.PlacedLocation;
import org.millenaire.world.PlacementConstraints;
import org.millenaire.world.PlotApron;
import org.millenaire.world.TerrainReachability;
import org.millenaire.world.VillageTerrainMap;

public final class TerrainProbeCommand {
   private TerrainProbeCommand() {
   }

   public static void registerUnder(LiteralArgumentBuilder<CommandSourceStack> parent) {
      parent.then(
         ((LiteralArgumentBuilder)Commands.literal("terrain-probe").executes(ctx -> execute(ctx, "")))
            .then(Commands.argument("plansets", StringArgumentType.greedyString()).executes(ctx -> execute(ctx, StringArgumentType.getString(ctx, "plansets"))))
      );
   }

   private static int execute(CommandContext<CommandSourceStack> ctx, String planSetsArg) {
      CommandSourceStack src = (CommandSourceStack)ctx.getSource();
      ServerLevel level = src.getLevel();
      BlockPos searchPos = BlockPos.containing(src.getPosition());
      Village v = VillageSavedData.get(level).getVillageManager().findNearestVillage(searchPos, 5000.0);
      if (v == null) {
         src.sendFailure(Component.literal("No village within 5000"));
         return 0;
      }

      VillageType vt = ModCultures.getVillageType(v.getVillageTypeId());
      int radius = vt != null ? vt.radius() : 90;
      BlockPos center = v.getCenter();
      VillageTerrainMap map = VillageGrowthManager.placementMap(level, v, radius);
      VillageGrowthManager.markExistingBuildingsOccupied(map, v);
      TerrainReachability reach = TerrainReachability.compute(map, center);
      int sizeX = map.getSizeX();
      int sizeZ = map.getSizeZ();
      int thRegion = reach.thRegion();
      Map<Integer, Integer> regionSizes = new HashMap<>();
      Map<Integer, int[]> regionSample = new HashMap<>();
      int buildableReachable = 0;
      int buildableUnreachable = 0;

      for (int lx = 0; lx < sizeX; lx++) {
         for (int lz = 0; lz < sizeZ; lz++) {
            int r = reach.regionAt(lx, lz);
            if (r != 0) {
               regionSizes.merge(r, 1, Integer::sum);
               regionSample.putIfAbsent(r, new int[]{lx, lz});
            }

            if (map.canBuildAt(lx, lz) && !map.isOccupied(lx, lz)) {
               if (r != 0 && r == thRegion) {
                  buildableReachable++;
               } else {
                  buildableUnreachable++;
               }
            }
         }
      }

      StringBuilder sb = new StringBuilder();
      sb.append("village=")
         .append(v.getVillageName())
         .append(" type=")
         .append(v.getVillageTypeId())
         .append(" center=")
         .append(center.toShortString())
         .append(" radius=")
         .append(radius)
         .append(" baseline=")
         .append(map.getBaseline())
         .append('\n');
      sb.append("thRegion=")
         .append(thRegion)
         .append(" size=")
         .append(regionSizes.getOrDefault(thRegion, 0))
         .append(" regions=")
         .append(regionSizes.size())
         .append('\n');
      sb.append("free buildable cells: reachable=").append(buildableReachable).append(" unreachable=").append(buildableUnreachable).append('\n');
      sb.append("largest regions (id size sampleWorldXZ topGround):\n");
      regionSizes.entrySet()
         .stream()
         .sorted((a, b) -> b.getValue() - a.getValue())
         .limit(12L)
         .forEach(
            e -> {
               int[] s = regionSample.get(e.getKey());
               sb.append("  ")
                  .append(e.getKey())
                  .append(e.getKey() == thRegion ? " (TH)" : "")
                  .append(' ')
                  .append(e.getValue())
                  .append(" @")
                  .append(map.getOriginX() + s[0])
                  .append(',')
                  .append(map.getOriginZ() + s[1])
                  .append(" y=")
                  .append(map.getTopGround(s[0], s[1]))
                  .append('\n');
            }
         );
      if (!planSetsArg.isBlank()) {
         sb.append("\nplacement attempts:\n");

         for (String id : planSetsArg.split(",")) {
            ResourceLocation rl = ResourceLocation.tryParse(id.trim());
            BuildingPlanSet ps = rl != null ? ModCultures.getBuildingPlanSet(rl) : null;
            if (ps == null) {
               sb.append("  ").append(id).append(": unknown plan set\n");
            } else {
               String variant = ps.pickRandomVariant(new Random(0L));
               BuildingPlanSet.LevelDef ld = ps.getLevel(variant, 0);
               BuildingPlan plan = ld != null ? ModCultures.getBuildingPlan(ld.planId()) : null;
               if (plan == null) {
                  sb.append("  ").append(id).append(": no level-0 plan\n");
               } else {
                  VillageType.LayoutSlot slot = vt == null ? null : vt.layout().stream().filter(s -> s.plan().equals(rl)).findFirst().orElse(null);
                  PlacementConstraints c = PlacementConstraints.resolve(ps, slot, radius);
                  ClearMargins margins = ps.clearMargins().atLeast(PlotApron.marginFloor(c.clearMargin()));
                  PlacedLocation withReach = BuildingLocationFinder.findLocation(map, plan, center, c, margins, new ArrayList<>(v.getBuildings()), reach);
                  PlacedLocation noReach = BuildingLocationFinder.findLocation(map, plan, center, c, margins, new ArrayList<>(v.getBuildings()), null);
                  sb.append("  ")
                     .append(id)
                     .append(" (")
                     .append(slot != null ? "slot " + slot.role() : "no slot")
                     .append(", ")
                     .append(plan.width())
                     .append('x')
                     .append(plan.depth())
                     .append(", dist ")
                     .append(c.minDistance())
                     .append("..")
                     .append(c.maxDistance())
                     .append(", margins ")
                     .append(margins)
                     .append("): with reach=")
                     .append(withReach != null ? withReach.position().toShortString() : "NONE")
                     .append(" | without reach=")
                     .append(
                        noReach != null
                           ? noReach.position().toShortString()
                              + " rot="
                              + noReach.rotation()
                              + " reachable="
                              + reach.isFootprintReachable(noReach.position().getX(), noReach.position().getZ(), plan.width(), plan.depth(), noReach.rotation())
                           : "NONE"
                     )
                     .append('\n');
                  sb.append("    rejections: ")
                     .append(BuildingLocationFinder.explainRejections(map, plan, center, c, margins, new ArrayList<>(v.getBuildings()), reach))
                     .append('\n');
               }
            }
         }
      }

      sb.append("\nregion map (T=TH region, #=building core, ~=water, !=danger, ^=no headroom, ?=impassable, a-z other regions by id mod 26; C=center)\n");
      appendGrid(sb, sizeX, sizeZ, (lx, lz) -> {
         if (lx == map.toLocalX(center.getX()) && lz == map.toLocalZ(center.getZ())) {
            return 'C';
         } else if (map.isOccupied(lx, lz)) {
            return '#';
         } else if (map.isWaterAt(lx, lz)) {
            return '~';
         } else if (map.isDangerAt(lx, lz)) {
            return '!';
         } else if (map.getSpaceAbove(lx, lz) <= 1) {
            return '^';
         } else {
            int r = reach.regionAt(lx, lz);
            if (r == 0) {
               return '?';
            } else {
               return r == thRegion ? 'T' : (char)(97 + (r - 1) % 26);
            }
         }
      });
      sb.append("\nbuildability (.=free+reachable, o=free unreachable, x=canBuild false, #=building core)\n");
      appendGrid(sb, sizeX, sizeZ, (lx, lz) -> {
         if (map.isOccupied(lx, lz)) {
            return '#';
         }

         if (!map.canBuildAt(lx, lz)) {
            return 'x';
         }

         int r = reach.regionAt(lx, lz);
         return (char)(r != 0 && r == thRegion ? '.' : 'o');
      });
      sb.append("\naltitude vs baseline (0-9 = +0..+9, a-i = -1..-9, + above, - below)\n");
      int baseline = map.getBaseline();
      appendGrid(sb, sizeX, sizeZ, (lx, lz) -> {
         int d = map.getTopGround(lx, lz) - baseline;
         if (d > 9) {
            return '+';
         } else if (d < -9) {
            return '-';
         } else {
            return d >= 0 ? (char)(48 + d) : (char)(97 + (-d - 1));
         }
      });
      Path out = Paths.get("debug", "terrain-probe-" + v.getVillageName().replaceAll("[^A-Za-z0-9_-]", "_") + ".txt");

      try {
         Files.createDirectories(out.getParent());
         Files.writeString(out, sb.toString());
      } catch (IOException ex) {
         src.sendFailure(Component.literal("terrain-probe failed: " + ex.getMessage()));
         return 0;
      }

      String summary = "terrain-probe: "
         + out
         + " — thRegion size "
         + regionSizes.getOrDefault(thRegion, 0)
         + ", free buildable reachable="
         + buildableReachable
         + " unreachable="
         + buildableUnreachable;
      src.sendSuccess(() -> Component.literal(summary), false);
      return 1;
   }

   private static void appendGrid(StringBuilder sb, int sizeX, int sizeZ, TerrainProbeCommand.CellChar f) {
      for (int lz = 0; lz < sizeZ; lz++) {
         for (int lx = 0; lx < sizeX; lx++) {
            sb.append(f.at(lx, lz));
         }

         sb.append('\n');
      }
   }

   private interface CellChar {
      char at(int var1, int var2);
   }
}
