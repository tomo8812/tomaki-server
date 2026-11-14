package org.millenaire.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockState;

public final class TerraformJobCompiler {
   public static final int CARRY_LOAD = 8;
   private static final int MAX_TRUNK_SCAN = 32;
   private static final int HEADROOM = 3;
   private static final Set<Block> NATURAL_SURFACES = Set.of(
      Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND, Blocks.SNOW_BLOCK, Blocks.COARSE_DIRT, Blocks.DIRT
   );
   private static final Set<Block> SOILS = Set.of(
      Blocks.DIRT,
      Blocks.COARSE_DIRT,
      Blocks.ROOTED_DIRT,
      Blocks.GRASS_BLOCK,
      Blocks.PODZOL,
      Blocks.MYCELIUM,
      Blocks.SAND,
      Blocks.RED_SAND,
      Blocks.GRAVEL,
      Blocks.CLAY
   );
   static final byte WATER_UNKNOWN = 0;
   static final byte DRY = 1;
   static final byte WATER = 2;
   static final int HIGH_BANK_OFFSET = 1000000;
   private static final int LOW_BANK_RISE = 1;
   private static final int[][] NEIGHBOURS = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
   private static final Comparator<TerraformJobCompiler.Op> SERPENTINE = Comparator.<TerraformJobCompiler.Op>comparingInt(o -> o.pos().getX())
      .thenComparingInt(o -> (o.pos().getX() & 1) == 0 ? o.pos().getZ() : -o.pos().getZ());
   private static final int MAX_GAP = 8;

   private TerraformJobCompiler() {
   }

   public static TerraformJobCompiler.Job compile(ServerLevel level, TerraformPlanner.Plan plan) {
      List<TerraformJobCompiler.Op> cuts = new ArrayList<>();
      List<TerraformJobCompiler.Op> fills = new ArrayList<>();
      List<TerraformJobCompiler.Op> wetFills = new ArrayList<>();
      int columns = plan.sizeX() * plan.sizeZ();
      byte[] water = new byte[columns];
      boolean[] filledWater = new boolean[columns];
      Map<BlockPos, BlockState> facing = wallFacing(plan);

      for (int dx = 0; dx < plan.sizeX(); dx++) {
         for (int dz = 0; dz < plan.sizeZ(); dz++) {
            int i = dx * plan.sizeZ() + dz;
            int h = plan.before()[i];
            int t = plan.after()[i];
            if (h != t) {
               int x = plan.regionMinX() + dx;
               int z = plan.regionMinZ() + dz;
               int floor = plan.floor()[i];
               boolean wet = !level.getFluidState(new BlockPos(x, h, z)).isEmpty();
               water[i] = (byte)(wet ? 2 : 1);
               BlockState surface = wet ? reclaimedSurface(level, x, z, floor) : surfaceFor(level, x, z, h);
               BlockState subsurface = stable(TerrainPreparer.getSubSurfaceBlock(level, x, z));
               String refusal;
               if (t < h) {
                  refusal = compileCut(level, x, z, h, t, plan.pad()[i], surface, subsurface, cuts, fills);
               } else {
                  refusal = compileFill(level, x, z, wet ? floor : h, t, surface, subsurface, facing, wet ? wetFills : fills);
                  filledWater[i] = wet;
               }

               if (refusal != null) {
                  return new TerraformJobCompiler.Job(List.of(), 0, 0, refusal);
               }
            }
         }
      }

      for (Entry<BlockPos, BlockState> e : facing.entrySet()) {
         BlockState current = level.getBlockState(e.getKey());
         if (SOILS.contains(current.getBlock())) {
            cuts.add(new TerraformJobCompiler.Op(e.getKey(), TerraformJobCompiler.Kind.RESURFACE, e.getValue(), true));
         }
      }

      cuts.sort(Comparator.<TerraformJobCompiler.Op>comparingInt(o -> -o.pos().getY()).thenComparing(SERPENTINE));
      fills.sort(Comparator.<TerraformJobCompiler.Op>comparingInt(o -> o.pos().getY()).thenComparing(SERPENTINE));
      if (!wetFills.isEmpty()) {
         readShoreNeighbours(level, plan, water, filledWater);
         fills.addAll(
            shoreFirst(
               wetFills,
               shoreDistances(water, filledWater, plan.before(), plan.after(), plan.sizeX(), plan.sizeZ()),
               plan.regionMinX(),
               plan.regionMinZ(),
               plan.sizeZ()
            )
         );
      }

      return new TerraformJobCompiler.Job(interleave(cuts, fills), cuts.size(), fills.size(), null);
   }

   private static void readShoreNeighbours(ServerLevel level, TerraformPlanner.Plan plan, byte[] water, boolean[] filledWater) {
      int sizeX = plan.sizeX();
      int sizeZ = plan.sizeZ();

      for (int i = 0; i < filledWater.length; i++) {
         if (filledWater[i]) {
            int dx = i / sizeZ;
            int dz = i % sizeZ;

            for (int[] step : NEIGHBOURS) {
               int nx = dx + step[0];
               int nz = dz + step[1];
               if (nx >= 0 && nz >= 0 && nx < sizeX && nz < sizeZ) {
                  int n = nx * sizeZ + nz;
                  if (water[n] == 0) {
                     BlockPos at = new BlockPos(plan.regionMinX() + nx, plan.before()[n], plan.regionMinZ() + nz);
                     water[n] = (byte)(level.getFluidState(at).isEmpty() ? 1 : 2);
                  }
               }
            }
         }
      }
   }

   static int[] shoreDistances(byte[] water, boolean[] filledWater, int[] before, int[] after, int sizeX, int sizeZ) {
      int[] dist = new int[filledWater.length];
      Arrays.fill(dist, Integer.MAX_VALUE);
      int[] queue = new int[filledWater.length];

      for (int pass = 0; pass < 2; pass++) {
         int head = 0;
         int tail = 0;
         int base = pass == 0 ? 1 : 1000001;

         for (int i = 0; i < filledWater.length; i++) {
            if (filledWater[i] && dist[i] == Integer.MAX_VALUE && touchesBank(i, pass == 0, water, before, after, sizeX, sizeZ)) {
               dist[i] = base;
               queue[tail++] = i;
            }
         }

         while (head < tail) {
            int i = queue[head++];
            int dx = i / sizeZ;
            int dz = i % sizeZ;

            for (int[] step : NEIGHBOURS) {
               int nx = dx + step[0];
               int nz = dz + step[1];
               if (nx >= 0 && nz >= 0 && nx < sizeX && nz < sizeZ) {
                  int n = nx * sizeZ + nz;
                  if (filledWater[n] && dist[n] == Integer.MAX_VALUE) {
                     dist[n] = dist[i] + 1;
                     queue[tail++] = n;
                  }
               }
            }
         }
      }

      return dist;
   }

   private static boolean touchesBank(int i, boolean lowOnly, byte[] water, int[] before, int[] after, int sizeX, int sizeZ) {
      int dx = i / sizeZ;
      int dz = i % sizeZ;

      for (int[] step : NEIGHBOURS) {
         int nx = dx + step[0];
         int nz = dz + step[1];
         if (nx >= 0 && nz >= 0 && nx < sizeX && nz < sizeZ) {
            int n = nx * sizeZ + nz;
            if (water[n] == 1) {
               int rise = after[n] - 1 - before[i];
               if (!lowOnly || rise >= 0 && rise <= 1) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   static List<TerraformJobCompiler.Op> shoreFirst(List<TerraformJobCompiler.Op> wetOps, int[] shoreDistance, int regionMinX, int regionMinZ, int sizeZ) {
      List<TerraformJobCompiler.Op> sorted = new ArrayList<>(wetOps);
      sorted.sort(
         Comparator.<TerraformJobCompiler.Op>comparingInt(o -> shoreDistance[(o.pos().getX() - regionMinX) * sizeZ + (o.pos().getZ() - regionMinZ)])
            .thenComparing(SERPENTINE)
            .thenComparingInt(o -> o.pos().getY())
      );
      return sorted;
   }

   @Nullable
   private static String compileCut(
      ServerLevel level,
      int x,
      int z,
      int h,
      int t,
      boolean yard,
      BlockState surface,
      BlockState subsurface,
      List<TerraformJobCompiler.Op> out,
      List<TerraformJobCompiler.Op> fills
   ) {
      for (int y = h; y < h + 32; y++) {
         BlockPos pos = new BlockPos(x, y, z);
         BlockState state = level.getBlockState(pos);
         if (state.isAir()) {
            if (y >= t + 3) {
               break;
            }
         } else {
            if (!state.is(BlockTags.LOGS)
               && !TerrainPreparer.isHugeMushroomBlock(state)
               && (y >= h + 3 || !state.is(BlockTags.LEAVES) && !state.canBeReplaced())) {
               if (!state.getFluidState().isEmpty()) {
                  return "fluid above a cut column at " + pos.toShortString();
               }
               break;
            }

            out.add(new TerraformJobCompiler.Op(pos, TerraformJobCompiler.Kind.REMOVE, null, true));
         }
      }

      for (int y = h - 1; y >= t; y--) {
         BlockPos pos = new BlockPos(x, y, z);
         BlockState state = level.getBlockState(pos);
         if (!state.getFluidState().isEmpty()) {
            return "fluid inside a cut column at " + pos.toShortString();
         }

         for (Direction side : Plane.HORIZONTAL) {
            BlockPos beside = pos.relative(side);
            if (!level.getFluidState(beside).isEmpty()) {
               return "fluid beside a cut column at " + beside.toShortString();
            }
         }

         if (!state.isAir()) {
            out.add(new TerraformJobCompiler.Op(pos, TerraformJobCompiler.Kind.REMOVE, null, true));
         }
      }

      BlockPos top = new BlockPos(x, t - 1, z);
      BlockState exposed = level.getBlockState(top);
      if (!exposed.getFluidState().isEmpty()) {
         if (!exposed.getFluidState().is(FluidTags.WATER)) {
            return "lava under a cut column at " + top.toShortString();
         }

         out.add(new TerraformJobCompiler.Op(top, TerraformJobCompiler.Kind.RESURFACE, surface, true));
         return null;
      } else if (isFillable(exposed)) {
         String refusal = supportFrom(level, x, z, t - 1, subsurface, fills);
         if (refusal != null) {
            return refusal;
         }

         fills.add(new TerraformJobCompiler.Op(top, TerraformJobCompiler.Kind.PLACE, surface, false));
         return null;
      } else {
         if (yard || SOILS.contains(exposed.getBlock())) {
            out.add(new TerraformJobCompiler.Op(top, TerraformJobCompiler.Kind.RESURFACE, surface, true));
         }

         return null;
      }
   }

   @Nullable
   private static String supportFrom(ServerLevel level, int x, int z, int aboveY, BlockState subsurface, List<TerraformJobCompiler.Op> fills) {
      int y = aboveY - 1;
      int bottom = aboveY - 8;

      while (y >= bottom && isFillable(level.getBlockState(new BlockPos(x, y, z)))) {
         y--;
      }

      if (y < bottom) {
         return "cave under a reshaped column at " + new BlockPos(x, aboveY, z).toShortString();
      }

      for (int fy = y + 1; fy < aboveY; fy++) {
         fills.add(new TerraformJobCompiler.Op(new BlockPos(x, fy, z), TerraformJobCompiler.Kind.PLACE, subsurface, false));
      }

      return null;
   }

   private static boolean isFillable(BlockState state) {
      return state.isAir() || state.canBeReplaced() && state.getFluidState().isEmpty();
   }

   public static BlockState stable(BlockState state) {
      if (!(state.getBlock() instanceof Fallable)) {
         return state;
      } else if (state.is(Blocks.SAND)) {
         return Blocks.SANDSTONE.defaultBlockState();
      } else {
         return state.is(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.defaultBlockState() : Blocks.STONE.defaultBlockState();
      }
   }

   @Nullable
   private static String compileFill(
      ServerLevel level,
      int x,
      int z,
      int h,
      int t,
      BlockState surface,
      BlockState subsurface,
      Map<BlockPos, BlockState> facing,
      List<TerraformJobCompiler.Op> out
   ) {
      if (isFillable(level.getBlockState(new BlockPos(x, h - 1, z)))) {
         String refusal = supportFrom(level, x, z, h, subsurface, out);
         if (refusal != null) {
            return refusal;
         }
      }

      for (int y = h; y < t; y++) {
         BlockPos pos = new BlockPos(x, y, z);
         BlockState wall = facing.remove(pos);
         out.add(new TerraformJobCompiler.Op(pos, TerraformJobCompiler.Kind.PLACE, y == t - 1 ? surface : (wall != null ? wall : subsurface), false));
      }

      for (int y = t; y < t + 3; y++) {
         BlockPos pos = new BlockPos(x, y, z);
         BlockState state = level.getBlockState(pos);
         if (!state.isAir() && (state.is(BlockTags.LEAVES) || state.canBeReplaced()) && state.getFluidState().isEmpty()) {
            out.add(new TerraformJobCompiler.Op(pos, TerraformJobCompiler.Kind.REMOVE, null, false));
         }
      }

      return null;
   }

   private static BlockState surfaceFor(ServerLevel level, int x, int z, int h) {
      BlockState top = level.getBlockState(new BlockPos(x, h - 1, z));
      if (!NATURAL_SURFACES.contains(top.getBlock())) {
         return TerrainPreparer.getSurfaceBlock(level, x, z);
      } else {
         return !top.is(Blocks.DIRT) && !top.is(Blocks.COARSE_DIRT) ? top : Blocks.GRASS_BLOCK.defaultBlockState();
      }
   }

   private static BlockState reclaimedSurface(ServerLevel level, int x, int z, int floor) {
      BlockState bed = level.getBlockState(new BlockPos(x, floor - 1, z));
      return !bed.is(Blocks.SAND) && !bed.is(Blocks.RED_SAND) && !bed.is(Blocks.GRAVEL) ? Blocks.GRASS_BLOCK.defaultBlockState() : bed;
   }

   public static Map<BlockPos, BlockState> wallFacing(TerraformPlanner.Plan plan) {
      Map<BlockPos, BlockState> out = new HashMap<>();
      if (plan.cutWall() == 0 && plan.fillWall() == 0 && plan.rampColumns() == 0) {
         return out;
      }

      int[] before = plan.before();
      int[] after = plan.after();

      for (int dx = 0; dx < plan.sizeX(); dx++) {
         for (int dz = 0; dz < plan.sizeZ(); dz++) {
            int i = dx * plan.sizeZ() + dz;

            for (int k = 0; k < 4; k++) {
               int nx = dx + (k == 0 ? 1 : (k == 1 ? -1 : 0));
               int nz = dz + (k == 2 ? 1 : (k == 3 ? -1 : 0));
               if (nx >= 0 && nz >= 0 && nx < plan.sizeX() && nz < plan.sizeZ()) {
                  int j = nx * plan.sizeZ() + nz;
                  int step = after[i] - after[j];
                  if (step >= 2 && step > Math.abs(before[i] - before[j])) {
                     int x = plan.regionMinX() + dx;
                     int z = plan.regionMinZ() + dz;

                     for (int y = after[j]; y < after[i] - 1; y++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        out.put(
                           pos,
                           Math.floorMod(x * 31 + y * 17 + z * 7, 4) == 0
                              ? Blocks.MOSSY_COBBLESTONE.defaultBlockState()
                              : Blocks.COBBLESTONE.defaultBlockState()
                        );
                     }
                  }
               }
            }
         }
      }

      return out;
   }

   public static List<TerraformJobCompiler.Op> interleave(List<TerraformJobCompiler.Op> cuts, List<TerraformJobCompiler.Op> fills) {
      List<TerraformJobCompiler.Op> out = new ArrayList<>(cuts.size() + fills.size());
      int ci = 0;
      int fi = 0;

      while (ci < cuts.size() || fi < fills.size()) {
         for (int k = 0; k < 8 && ci < cuts.size(); k++) {
            out.add(cuts.get(ci++));
         }

         for (int k = 0; k < 8 && fi < fills.size(); k++) {
            out.add(fills.get(fi++));
         }
      }

      return out;
   }

   public record Job(List<TerraformJobCompiler.Op> ops, int cutOps, int fillOps, @Nullable String refusal) {
      public boolean refused() {
         return this.refusal != null;
      }
   }

   public enum Kind {
      REMOVE,
      PLACE,
      RESURFACE;

      // $VF: synthetic method
      private static TerraformJobCompiler.Kind[] $values() {
         return new TerraformJobCompiler.Kind[]{REMOVE, PLACE, RESURFACE};
      }
   }

   public record Op(BlockPos pos, TerraformJobCompiler.Kind kind, @Nullable BlockState state, boolean cut) {
   }
}
