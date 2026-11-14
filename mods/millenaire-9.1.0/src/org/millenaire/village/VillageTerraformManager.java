package org.millenaire.village;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.millenaire.block.MillPathBlock;
import org.millenaire.block.MillPathSlabBlock;
import org.millenaire.building.BuildingInstance;
import org.millenaire.building.BuildingPlan;
import org.millenaire.building.BuildingPlanSet;
import org.millenaire.building.ClearMargins;
import org.millenaire.config.MillenaireServerConfig;
import org.millenaire.culture.ModCultures;
import org.millenaire.culture.VillageType;
import org.millenaire.goal.impl.TerraformGoal;
import org.millenaire.world.BuildingLocationFinder;
import org.millenaire.world.PlacedLocation;
import org.millenaire.world.PlacementConstraints;
import org.millenaire.world.PlotApron;
import org.millenaire.world.TerraformJobCompiler;
import org.millenaire.world.TerraformPlanner;
import org.millenaire.world.TerrainReachability;
import org.millenaire.world.TerrainSnapshot;
import org.millenaire.world.VillageTerrainMap;
import org.slf4j.Logger;

public final class VillageTerraformManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   static final long PLANNING_BACKOFF_TICKS = 12000L;
   static final long ABANDON_TICKS = 72000L;
   static final long STALE_CLAIM_TICKS = 1200L;
   static final int WALL_SCAN_RANGE = 40;

   private VillageTerraformManager() {
   }

   public static boolean isEnabled() {
      return (Boolean)MillenaireServerConfig.SERVER.villagerTerraforming.get();
   }

   public static void onPlacementFailure(
      ServerLevel level,
      Village village,
      BuildingPlanSet planSet,
      BuildingPlanSet.LevelDef levelDef,
      String variant,
      BuildingPlan plan,
      VillageTerrainMap map,
      PlacementConstraints constraints,
      ClearMargins margins
   ) {
      if (isEnabled()) {
         if (village.getTerraformProject() == null && village.getTerraformPlanning() == null) {
            if (level.getGameTime() >= village.getTerraformPlanningBlockedUntil(planSet.id())) {
               VillageTerrainMap planning = planningMap(level, map);
               byte[][] protection = computeProtection(level, village, planning);
               TerraformPlanner.Settings settings = settingsFromConfig(seedOf(village));
               List<BuildingInstance> buildings = new ArrayList<>(village.getBuildings());
               BlockPos center = village.getCenter();
               CompletableFuture<TerraformPlanner.Result> result = CompletableFuture.supplyAsync(
                  () -> TerraformPlanner.plan(planning, plan, center, constraints, margins, buildings, protection, settings), Util.backgroundExecutor()
               );
               village.setTerraformPlanning(new VillageTerraformManager.Planning(planSet, levelDef, variant, plan, layoutStamp(village), result));
            }
         }
      }
   }

   private static void finishPlanning(ServerLevel level, Village village, VillageTerraformManager.Planning planning) {
      if (isEnabled() && village.getTerraformProject() == null) {
         long now = level.getGameTime();

         TerraformPlanner.Result result;
         try {
            result = planning.result().join();
         } catch (CompletionException | CancellationException e) {
            LOGGER.warn("[Millenaire] Terraform: {} — planning for {} failed", new Object[]{village.getVillageName(), planning.planSet().id(), e});
            village.setTerraformPlanningBlockedUntil(planning.planSet().id(), now + 12000L);
            return;
         }

         if (layoutStamp(village) != planning.layoutStamp()) {
            LOGGER.info("[Millenaire] Terraform: {} — village changed while planning for {}, plan dropped", village.getVillageName(), planning.planSet().id());
         } else {
            install(level, village, planning.planSet(), planning.levelDef(), planning.variant(), planning.plan(), result, now);
         }
      }
   }

   private static int layoutStamp(Village village) {
      Village.PendingProject pending = village.getPendingProject();
      int queued = 0;

      for (ControlledQueuedProject q : village.getControlledQueue()) {
         queued = 31 * queued + Objects.hashCode(q.plannedLocation());
      }

      return Objects.hash(village.getBuildings().size(), pending != null ? pending.plannedLocation() : null, queued);
   }

   @Nullable
   public static TerraformProject plan(
      ServerLevel level,
      Village village,
      BuildingPlanSet planSet,
      BuildingPlanSet.LevelDef levelDef,
      String variant,
      BuildingPlan plan,
      VillageTerrainMap map,
      PlacementConstraints constraints,
      ClearMargins margins,
      long now
   ) {
      return install(level, village, planSet, levelDef, variant, plan, planOnly(level, village, plan, map, constraints, margins), now);
   }

   @Nullable
   private static TerraformProject install(
      ServerLevel level,
      Village village,
      BuildingPlanSet planSet,
      BuildingPlanSet.LevelDef levelDef,
      String variant,
      BuildingPlan plan,
      TerraformPlanner.Result result,
      long now
   ) {
      TerraformPlanner.Plan p = result.plan();
      if (p == null) {
         village.setTerraformPlanningBlockedUntil(planSet.id(), now + 12000L);
         LOGGER.info(
            "[Millenaire] Terraform: {} — no site for {} ({}) | {}", new Object[]{village.getVillageName(), planSet.id(), result.reason(), result.stats()}
         );
         return null;
      } else {
         long compileStart = System.nanoTime();
         TerraformJobCompiler.Job job = TerraformJobCompiler.compile(level, p);
         double compileMs = (System.nanoTime() - compileStart) / 1000000.0;
         if (job.refused()) {
            village.setTerraformPlanningBlockedUntil(planSet.id(), now + 12000L);
            LOGGER.info(
               "[Millenaire] Terraform: {} — site for {} refused at compile time: {}", new Object[]{village.getVillageName(), planSet.id(), job.refusal()}
            );
            return null;
         } else {
            PlacedLocation location = new PlacedLocation(
               new BlockPos(p.location().position().getX(), p.padY() + plan.groundLevel(), p.location().position().getZ()), p.location().rotation()
            );
            TerraformProject project = new TerraformProject(
               planSet.id(), variant, levelDef.level(), location, p.padY(), p.cutVolume(), p.fillVolume(), now, job.ops(), p.gate()
            );
            village.setTerraformProject(project);
            village.getTerraformHistory()[0]++;
            village.recordEvent(
               level,
               "Terraform: preparing a site for "
                  + planSet.id().getPath()
                  + " at "
                  + location.position().toShortString()
                  + " (cut "
                  + p.cutVolume()
                  + ", fill "
                  + p.fillVolume()
                  + ")"
            );
            LOGGER.info(
               "[Millenaire] Terraform: {} — site for {} at {} pad y={} ({}{}) cut={} fill={} water={} walls={}/{} ramp={} ops={} compile={}ms | {}",
               new Object[]{
                  village.getVillageName(),
                  planSet.id(),
                  location.position().toShortString(),
                  p.padY(),
                  p.padY() >= village.getCenter().getY() ? "+" : "",
                  p.padY() - village.getCenter().getY(),
                  p.cutVolume(),
                  p.fillVolume(),
                  p.waterColumns(),
                  p.cutWall(),
                  p.fillWall(),
                  p.rampColumns(),
                  job.ops().size(),
                  String.format("%.1f", compileMs),
                  result.stats()
               }
            );
            return project;
         }
      }
   }

   static VillageTerrainMap planningMap(ServerLevel level, VillageTerrainMap failed) {
      if ((Integer)MillenaireServerConfig.SERVER.terraformRetainingWall.get() > 0) {
         failed.widenScanRange(level, 40);
      }

      return failed;
   }

   public static TerraformPlanner.Result planOnly(
      ServerLevel level, Village village, BuildingPlan plan, VillageTerrainMap map, PlacementConstraints constraints, ClearMargins margins
   ) {
      byte[][] protection = computeProtection(level, village, map);
      TerraformPlanner.Settings settings = settingsFromConfig(seedOf(village));
      return TerraformPlanner.plan(map, plan, village.getCenter(), constraints, margins, new ArrayList<>(village.getBuildings()), protection, settings);
   }

   private static long seedOf(Village village) {
      return village.getId().uuid().getMostSignificantBits() ^ village.getId().uuid().getLeastSignificantBits();
   }

   @Nullable
   public static VillageTerraformManager.SiteQuery buildQuery(ServerLevel level, Village village, ResourceLocation planSetId) {
      BuildingPlanSet planSet = ModCultures.getBuildingPlanSet(planSetId);
      if (planSet != null && !planSet.variants().isEmpty()) {
         String variant = planSet.variants().keySet().iterator().next();
         BuildingPlanSet.LevelDef levelDef = planSet.getLevel(variant, 0);
         BuildingPlan plan = levelDef != null ? ModCultures.getBuildingPlan(levelDef.planId()) : null;
         if (plan == null) {
            return null;
         }

         VillageType vt = ModCultures.getVillageType(village.getVillageTypeId());
         int radius = vt != null ? vt.radius() : 90;
         VillageTerrainMap map = planningMap(level, VillageGrowthManager.placementMap(level, village, radius));
         VillageGrowthManager.markExistingBuildingsOccupied(map, village);
         VillageType.LayoutSlot slot = null;
         if (vt != null) {
            for (VillageType.LayoutSlot s : vt.layout()) {
               if (planSetId.equals(s.plan())) {
                  slot = s;
                  break;
               }
            }
         }

         PlacementConstraints constraints = PlacementConstraints.resolve(planSet, slot, radius);
         ClearMargins margins = planSet.clearMargins().atLeast(PlotApron.marginFloor(constraints.clearMargin()));
         return new VillageTerraformManager.SiteQuery(planSet, levelDef, variant, plan, map, constraints, margins);
      } else {
         return null;
      }
   }

   public static int rush(ServerLevel level, Village village) {
      TerraformProject project = village.getTerraformProject();
      if (project == null) {
         return 0;
      }

      int changed = 0;

      for (TerraformJobCompiler.Op op : project.ops()) {
         if (TerraformGoal.applyOp(level, op, null, false)) {
            changed++;
         }
      }

      project.completeAll(level.getGameTime());
      VillageGrowthManager.onBuildingCompleted(village);
      village.markWaypointGraphDirty();
      village.markDirty();
      return changed;
   }

   public static String describeReadyBlocker(ServerLevel level, Village village) {
      TerraformProject project = village.getTerraformProject();
      if (project == null || project.status() != TerraformProject.Status.READY) {
         return "not ready";
      } else if (level.getGameTime() < village.getNoProjectsLeftUntil()) {
         return "growth 'nothing to build' cache";
      } else {
         VillageType vt = ModCultures.getVillageType(village.getVillageTypeId());
         if (!VillageGrowthManager.canStartNewConstruction(village, vt)) {
            return "construction slots full";
         } else {
            BuildingPlanSet ps = ModCultures.getBuildingPlanSet(project.planSetId());
            BuildingPlanSet.LevelDef ld = ps != null ? ps.getLevel(project.variant(), project.level()) : null;
            if (ld == null) {
               return "plan set gone";
            } else {
               return !VillageGrowthManager.hasResourcesForProject(level, village, ld)
                  ? "waiting for resources " + ld.requiredResources()
                  : "launches on the next growth tick";
            }
         }
      }
   }

   public static String verify(ServerLevel level, TerraformProject project) {
      int flooded = 0;
      int left = 0;
      int unfilled = 0;
      int surface = 0;
      int paths = 0;
      Map<String, String> samples = new LinkedHashMap<>();

      for (TerraformJobCompiler.Op op : project.ops()) {
         BlockState state = level.getBlockState(op.pos());
         if (op.kind() == TerraformJobCompiler.Kind.PLACE || !isPath(state.getBlock()) || TerraformGoal.isFillable(level.getBlockState(op.pos().below()))) {
            String defect = switch (op.kind()) {
               case REMOVE -> {
                  if (!state.isAir() && (!state.canBeReplaced() || !state.getFluidState().isEmpty())) {
                     if (!state.getFluidState().isEmpty()) {
                        flooded++;
                        yield "flooded";
                     } else {
                        left++;
                        yield "left";
                     }
                  } else {
                     yield null;
                  }
               }
               case PLACE -> {
                  if (!TerraformGoal.isFillable(state)) {
                     yield null;
                  } else {
                     unfilled++;
                     yield "unfilled";
                  }
               }
               case RESURFACE -> {
                  if (op.state() != null && !state.is(op.state().getBlock()) && !state.is(BlockTags.DIRT) && !state.is(Blocks.SAND) && !state.is(Blocks.GRAVEL)
                     )
                   {
                     surface++;
                     yield "surface";
                  } else {
                     yield null;
                  }
               }
            };
            if (defect != null && !samples.containsKey(defect)) {
               samples.put(
                  defect,
                  defect
                     + ": "
                     + op.kind()
                     + " "
                     + op.pos().toShortString()
                     + " found "
                     + BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath()
                     + (op.state() != null ? " (wanted " + BuiltInRegistries.BLOCK.getKey(op.state().getBlock()).getPath() + ")" : "")
               );
            }
         } else {
            paths++;
         }
      }

      int bad = flooded + left + unfilled + surface;
      String laid = paths > 0 ? ", " + paths + " under village paths since" : "";
      return bad == 0
         ? "ok (" + project.totalOps() + " ops" + laid + ")"
         : String.format(
            "%d/%d ops off target (flooded %d, left %d, unfilled %d, surface %d%s; e.g. %s)",
            bad,
            project.totalOps(),
            flooded,
            left,
            unfilled,
            surface,
            laid,
            String.join("; ", samples.values())
         );
   }

   public static TerraformPlanner.Settings settingsFromConfig(long seed) {
      MillenaireServerConfig c = MillenaireServerConfig.SERVER;
      return TerraformPlanner.Settings.defaults(seed)
         .withMaxVolume((Integer)c.terraformMaxVolume.get())
         .withMaxReach((Integer)c.terraformMaxReach.get())
         .withExtensions(
            new TerraformPlanner.Extensions(
               (Integer)c.terraformAltitudeGate.get(),
               (Boolean)c.terraformFillWater.get(),
               (Integer)c.terraformMaxWaterDepth.get(),
               (Boolean)c.terraformReclaimOpenWater.get(),
               (Integer)c.terraformRetainingWall.get()
            )
         );
   }

   public static boolean applyProfile(String name) {
      MillenaireServerConfig c = MillenaireServerConfig.SERVER;
      switch (name) {
         case "base":
            set(c, 4000, 12, 9, false, 3, false, 0);
            break;
         case "extended":
            set(c, 10000, 20, 16, true, 4, false, 0);
            break;
         case "extended-open":
            set(c, 10000, 20, 16, true, 4, true, 0);
            break;
         case "mountain":
            set(c, 10000, 20, 16, true, 4, true, 6);
            break;
         case "max":
            set(c, 20000, 30, 22, true, 8, true, 6);
            break;
         default:
            return false;
      }

      return true;
   }

   private static void set(MillenaireServerConfig c, int volume, int reach, int gate, boolean water, int depth, boolean openWater, int wall) {
      c.terraformMaxVolume.set(volume);
      c.terraformMaxReach.set(reach);
      c.terraformAltitudeGate.set(gate);
      c.terraformFillWater.set(water);
      c.terraformMaxWaterDepth.set(depth);
      c.terraformReclaimOpenWater.set(openWater);
      c.terraformRetainingWall.set(wall);
      c.terraformMaxWaterDepth.save();
   }

   public static void tick(ServerLevel level, Village village) {
      VillageTerraformManager.Planning planning = village.getTerraformPlanning();
      if (planning != null && planning.result().isDone()) {
         village.setTerraformPlanning(null);
         finishPlanning(level, village, planning);
      }

      TerraformProject project = village.getTerraformProject();
      if (project != null) {
         project.releaseStale(level.getGameTime(), 1200L);
         if (project.status() == TerraformProject.Status.READY && !project.verified()) {
            project.markVerified();
            String check = verify(level, project);
            village.getTerraformHistory()[1]++;
            if (check.startsWith("ok")) {
               village.getTerraformHistory()[2]++;
            }

            LOGGER.info(
               "[Millenaire] Terraform: {} — site for {} finished after {} ticks, world check: {}",
               new Object[]{village.getVillageName(), project.planSetId(), level.getGameTime() - project.createdTick(), check}
            );
         }

         if (!isEnabled()) {
            LOGGER.info("[Millenaire] Terraform: {} — config disabled, dropping the project for {}", village.getVillageName(), project.planSetId());
            village.setTerraformProject(null);
         } else {
            if (project.status() == TerraformProject.Status.ACTIVE && level.getGameTime() - project.lastProgressTick() > 72000L) {
               LOGGER.info(
                  "[Millenaire] Terraform: {} — no progress for {} ticks, abandoning the site for {} ({}/{} done)",
                  new Object[]{village.getVillageName(), 72000L, project.planSetId(), project.doneOps(), project.totalOps()}
               );
               village.setTerraformProject(null);
            }
         }
      }
   }

   static byte[][] computeProtection(ServerLevel level, Village village, VillageTerrainMap map) {
      byte[][] mask = new byte[map.getSizeX()][map.getSizeZ()];

      for (BuildingInstance b : village.getBuildings()) {
         BuildingPlan bp = ModCultures.getBuildingPlan(b.getPlanId());
         if (bp != null) {
            BuildingPlanSet bps = ModCultures.getBuildingPlanSet(b.getPlanSetId());
            ClearMargins m = bps != null
               ? bps.clearMargins().atLeast(PlotApron.marginFloor(PlacementConstraints.getDefaultClearMargin()))
               : ClearMargins.symmetric(PlotApron.marginFloor(PlacementConstraints.getDefaultClearMargin()));
            TerraformPlanner.protectFootprint(
               mask,
               map,
               b.getOrigin().getX(),
               b.getOrigin().getZ(),
               bp.width(),
               bp.depth(),
               m,
               b.getRotation(),
               b.getStatus() == BuildingInstance.Status.COMPLETE
            );
         }
      }

      for (ControlledQueuedProject q : village.getControlledQueue()) {
         protectPlanned(mask, map, q.planSetId(), q.variant(), q.level(), q.plannedLocation());
      }

      Village.PendingProject pending = village.getPendingProject();
      if (pending != null && pending.plannedLocation() != null) {
         protectPlanned(mask, map, pending.planSetId(), pending.variant(), pending.level(), pending.plannedLocation());
      }

      for (int lx = 0; lx < map.getSizeX(); lx++) {
         for (int lz = 0; lz < map.getSizeZ(); lz++) {
            int x = map.getOriginX() + lx;
            int z = map.getOriginZ() + lz;
            int top = map.getTopGround(lx, lz);
            if (isPath(level.getBlockState(new BlockPos(x, top - 1, z)).getBlock()) || isPath(level.getBlockState(new BlockPos(x, top, z)).getBlock())) {
               mask[lx][lz] = 2;
            }
         }
      }

      return mask;
   }

   private static void protectPlanned(
      byte[][] mask, VillageTerrainMap map, ResourceLocation planSetId, String variant, int levelIndex, @Nullable PlacedLocation planned
   ) {
      if (planned != null) {
         BuildingPlanSet ps = ModCultures.getBuildingPlanSet(planSetId);
         if (ps != null) {
            BuildingPlanSet.LevelDef ld = ps.getLevel(variant, levelIndex);
            BuildingPlan bp = ld != null ? ModCultures.getBuildingPlan(ld.planId()) : null;
            if (bp != null) {
               ClearMargins m = ps.clearMargins().atLeast(PlotApron.marginFloor(PlacementConstraints.getDefaultClearMargin()));
               TerraformPlanner.protectFootprint(
                  mask, map, planned.position().getX(), planned.position().getZ(), bp.width(), bp.depth(), m, planned.rotation(), false
               );
            }
         }
      }
   }

   private static boolean isPath(Block block) {
      return block instanceof MillPathBlock || block instanceof MillPathSlabBlock;
   }

   record Planning(
      BuildingPlanSet planSet,
      BuildingPlanSet.LevelDef levelDef,
      String variant,
      BuildingPlan plan,
      int layoutStamp,
      CompletableFuture<TerraformPlanner.Result> result
   ) {
   }

   public record SiteQuery(
      BuildingPlanSet planSet,
      BuildingPlanSet.LevelDef levelDef,
      String variant,
      BuildingPlan plan,
      VillageTerrainMap map,
      PlacementConstraints constraints,
      ClearMargins margins
   ) {
      @Nullable
      public PlacedLocation findWithoutEarthwork(Village village) {
         return BuildingLocationFinder.findLocation(
            this.map,
            this.plan,
            village.getCenter(),
            this.constraints,
            this.margins,
            new ArrayList<>(village.getBuildings()),
            TerrainReachability.compute(this.map, village.getCenter())
         );
      }

      public TerrainSnapshot snapshot(ServerLevel level, Village village) {
         return new TerrainSnapshot(
            village.getVillageName(),
            village.getCenter(),
            this.map,
            VillageTerraformManager.computeProtection(level, village, this.map),
            new TerrainSnapshot.Query(
               this.planSet.id(),
               this.plan.width(),
               this.plan.depth(),
               this.plan.groundLevel(),
               this.plan.buildingOrientation(),
               this.margins,
               this.constraints.minDistance(),
               this.constraints.maxDistance(),
               this.constraints.fixedOrientation()
            )
         );
      }
   }
}
