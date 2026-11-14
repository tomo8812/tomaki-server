package org.millenaire.world;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import org.millenaire.building.ClearMargins;

public final class TerrainTraversal {
   public static final int CLEAR_ABOVE_MARGIN = 50;
   public static final int FOUNDATION_DEPTH = 10;
   private static final int NATURAL_SCAN = 16;

   private TerrainTraversal() {
   }

   public static int computeOffsetComponent(int worldCoord, int originCoord, int size) {
      if (worldCoord < originCoord) {
         return originCoord - worldCoord;
      } else {
         return worldCoord >= originCoord + size ? worldCoord - (originCoord + size) + 1 : 0;
      }
   }

   public static boolean isInsideFootprint(int wx, int wz, BlockPos origin, int width, int depth) {
      return wx >= origin.getX() && wx < origin.getX() + width && wz >= origin.getZ() && wz < origin.getZ() + depth;
   }

   private static int[][] naturalTargets(
      ServerLevel level,
      int startX,
      int startZ,
      int gridW,
      int gridD,
      BlockPos effOrigin,
      int effWidth,
      int effDepth,
      int baseY,
      PlotApron.Neighbours neighbours
   ) {
      int[][] natural = new int[gridW][gridD];
      int[][] tolerance = new int[gridW][gridD];
      boolean[][] inside = new boolean[gridW][gridD];
      boolean[][] rough = PlotApron.ROUGH ? new boolean[gridW][gridD] : null;
      int[][] pinned = neighbours != null ? new int[gridW][gridD] : null;

      for (int ix = 0; ix < gridW; ix++) {
         int wx = startX + ix;
         int ox = computeOffsetComponent(wx, effOrigin.getX(), effWidth);

         for (int iz = 0; iz < gridD; iz++) {
            int wz = startZ + iz;
            int oz = computeOffsetComponent(wz, effOrigin.getZ(), effDepth);
            inside[ix][iz] = isInsideFootprint(wx, wz, effOrigin, effWidth, effDepth);
            natural[ix][iz] = naturalWalkLevel(level, wx, wz, baseY);
            int t = PlotApron.tolerance(PlotApron.offset(ox, oz, PlotApron.FLAT));
            if (pinned != null) {
               pinned[ix][iz] = Integer.MIN_VALUE;
               int[] r = inside[ix][iz] ? null : neighbours.range(wx, wz);
               if (r != null && Math.max(baseY - t, r[0]) <= Math.min(baseY + t, r[1])) {
                  pinned[ix][iz] = Math.max(Math.max(baseY - t, r[0]), Math.min(Math.min(baseY + t, r[1]), natural[ix][iz]));
                  tolerance[ix][iz] = t;
                  continue;
               }
            }

            if (rough != null && t >= 2 && !inside[ix][iz] && PlotApronTarget.rough(wx, wz)) {
               rough[ix][iz] = true;
               tolerance[ix][iz] = t;
            } else {
               tolerance[ix][iz] = Math.max(0, t - PlotApronTarget.noise(wx, wz));
            }
         }
      }

      return PlotApronTarget.compute(natural, tolerance, inside, rough, pinned, baseY);
   }

   private static int naturalWalkLevel(ServerLevel level, int x, int z, int baseY) {
      for (int y = baseY + 16; y >= baseY - 16; y--) {
         if (VillageTerrainMap.isGround(level.getBlockState(new BlockPos(x, y, z)))) {
            return y + 1;
         }
      }

      return baseY - 16;
   }

   public static void traverse(
      ServerLevel level,
      BlockPos effOrigin,
      int effWidth,
      int effDepth,
      int height,
      int baseY,
      int groundLevel,
      ClearMargins effectiveMargins,
      TerrainTraversal.BlockVisitor visitor
   ) {
      traverse(level, effOrigin, effWidth, effDepth, height, baseY, groundLevel, effectiveMargins, null, visitor);
   }

   public static void traverse(
      ServerLevel level,
      BlockPos effOrigin,
      int effWidth,
      int effDepth,
      int height,
      int baseY,
      int groundLevel,
      ClearMargins effectiveMargins,
      @Nullable PlotApron.Neighbours neighbours,
      TerrainTraversal.BlockVisitor visitor
   ) {
      int startX = effOrigin.getX() - effectiveMargins.lengthBefore();
      int endX = effOrigin.getX() + effWidth + effectiveMargins.lengthAfter();
      int startZ = effOrigin.getZ() - effectiveMargins.widthBefore();
      int endZ = effOrigin.getZ() + effDepth + effectiveMargins.widthAfter();
      int gridW = endX - startX;
      int gridD = endZ - startZ;
      BlockState[][] surfaceBlocks = new BlockState[gridW][gridD];
      BlockState[][] subSurfaceBlocks = new BlockState[gridW][gridD];

      for (int ix = 0; ix < gridW; ix++) {
         for (int iz = 0; iz < gridD; iz++) {
            surfaceBlocks[ix][iz] = TerrainPreparer.getSurfaceBlock(level, startX + ix, startZ + iz);
            subSurfaceBlocks[ix][iz] = TerrainPreparer.getSubSurfaceBlock(level, startX + ix, startZ + iz);
         }
      }

      int[][] target = PlotApron.NATURAL ? naturalTargets(level, startX, startZ, gridW, gridD, effOrigin, effWidth, effDepth, baseY, neighbours) : null;

      for (int wx = startX; wx < endX; wx++) {
         for (int wz = startZ; wz < endZ; wz++) {
            int offsetX = computeOffsetComponent(wx, effOrigin.getX(), effWidth);
            int offsetZ = computeOffsetComponent(wz, effOrigin.getZ(), effDepth);
            int offset = PlotApron.offset(offsetX, offsetZ, PlotApron.FLAT);
            boolean isInside = isInsideFootprint(wx, wz, effOrigin, effWidth, effDepth);
            if (target != null && !isInside) {
               offset = target[wx - startX][wz - startZ] - baseY + 2;
            }

            boolean isSideBorder = wx == startX || wx == endX - 1 || wz == startZ || wz == endZ - 1;
            BlockState surfaceBlock = surfaceBlocks[wx - startX][wz - startZ];
            BlockState subSurfaceBlock = subSurfaceBlocks[wx - startX][wz - startZ];
            int topDeltaY = height + 50;

            for (int deltaY = topDeltaY; deltaY >= 0; deltaY--) {
               int y = baseY + deltaY;
               BlockPos pos = new BlockPos(wx, y, wz);
               BlockState existing = level.getBlockState(pos);
               if (!existing.isAir()) {
                  TerrainTraversal.BlockContext ctx = new TerrainTraversal.BlockContext(wx, wz, y, existing, isInside, surfaceBlock, subSurfaceBlock);
                  if (deltaY >= offset - 2) {
                     if (!isInside) {
                        boolean isBorder = deltaY == offset - 2 || deltaY == 0 || isSideBorder;
                        if (isBorder) {
                           if (!TerrainPreparer.isDecorativePlant(existing) && !TerrainPreparer.isLeaves(existing)) {
                              if (TerrainPreparer.isAdjacentToWater(level, pos)) {
                                 visitor.visit(ctx, TerrainTraversal.Action.BORDER_ANTIFLOOD);
                              } else {
                                 visitor.visit(ctx, TerrainTraversal.Action.CLEAR_AIR);
                              }
                           }
                        } else if (!TerrainPreparer.isDecorativePlant(existing) && !TerrainPreparer.isLeaves(existing)) {
                           visitor.visit(ctx, TerrainTraversal.Action.CLEAR_AIR);
                        }
                     } else if (!TerrainPreparer.isDecorativePlant(existing)) {
                        visitor.visit(ctx, TerrainTraversal.Action.CLEAR_AIR);
                     }
                  } else if (TerrainPreparer.isLog(existing) || TerrainPreparer.isHugeMushroomBlock(existing)) {
                     visitor.visit(ctx, TerrainTraversal.Action.CLEAR_TREE);
                  }
               }
            }

            if (groundLevel < 0 && isInside) {
               int clearBottom = baseY + groundLevel + 1;

               for (int y = baseY - 1; y >= clearBottom; y--) {
                  BlockPos pos = new BlockPos(wx, y, wz);
                  BlockState existing = level.getBlockState(pos);
                  if (!existing.isAir()) {
                     TerrainTraversal.BlockContext ctx = new TerrainTraversal.BlockContext(wx, wz, y, existing, true, surfaceBlock, subSurfaceBlock);
                     visitor.visit(ctx, TerrainTraversal.Action.CLEAR_SUBGROUND);
                  }
               }
            }
         }
      }

      int foundationBottom = baseY - 10;
      int insideFoundationTop = baseY + Math.min(groundLevel, 0);

      for (int wx = startX; wx < endX; wx++) {
         for (int wz = startZ; wz < endZ; wz++) {
            int offsetX = computeOffsetComponent(wx, effOrigin.getX(), effWidth);
            int offsetZ = computeOffsetComponent(wz, effOrigin.getZ(), effDepth);
            int offset = PlotApron.offset(offsetX, offsetZ, PlotApron.FLAT);
            boolean isInside = isInsideFootprint(wx, wz, effOrigin, effWidth, effDepth);
            int columnTop = isInside ? insideFoundationTop : baseY;
            if (target != null && !isInside) {
               offset = baseY - target[wx - startX][wz - startZ] + 2;
            }

            BlockState surfaceBlock = surfaceBlocks[wx - startX][wz - startZ];
            BlockState subSurfaceBlock = subSurfaceBlocks[wx - startX][wz - startZ];

            for (int y = foundationBottom; y < columnTop; y++) {
               int depthBelowBase = baseY - y;
               BlockPos pos = new BlockPos(wx, y, wz);
               BlockState existing = level.getBlockState(pos);
               TerrainTraversal.BlockContext ctx = new TerrainTraversal.BlockContext(wx, wz, y, existing, isInside, surfaceBlock, subSurfaceBlock);
               if (depthBelowBase > offset) {
                  if (TerrainPreparer.isPreservedFoundationBlock(existing)) {
                     if (groundLevel < 0 && isInside && TerrainPreparer.isFallingBlock(existing)) {
                        visitor.visit(ctx, TerrainTraversal.Action.STABILIZE_FALLING);
                     }
                  } else if (existing.isAir()
                     || !existing.getFluidState().isEmpty()
                     || TerrainPreparer.isLeaves(existing)
                     || TerrainPreparer.isLog(existing)
                     || TerrainPreparer.isHugeMushroomBlock(existing)
                     || existing.is(BlockTags.REPLACEABLE)
                     || !existing.canOcclude()) {
                     visitor.visit(ctx, TerrainTraversal.Action.FILL_SUBSURFACE);
                  }
               } else if (depthBelowBase >= offset - 1) {
                  if (TerrainPreparer.isPreservedFoundationBlock(existing)) {
                     if (groundLevel < 0 && isInside && TerrainPreparer.isFallingBlock(existing)) {
                        visitor.visit(ctx, TerrainTraversal.Action.STABILIZE_FALLING);
                     }
                  } else if (existing.isAir()
                     || !existing.getFluidState().isEmpty()
                     || TerrainPreparer.isLeaves(existing)
                     || TerrainPreparer.isLog(existing)
                     || TerrainPreparer.isHugeMushroomBlock(existing)
                     || existing.is(BlockTags.REPLACEABLE)
                     || !existing.canOcclude()) {
                     visitor.visit(ctx, TerrainTraversal.Action.FILL_SURFACE);
                  }
               } else if (TerrainPreparer.isLog(existing) || TerrainPreparer.isHugeMushroomBlock(existing)) {
                  visitor.visit(ctx, TerrainTraversal.Action.CLEAR_TREE);
               }
            }
         }
      }
   }

   public enum Action {
      CLEAR_AIR,
      CLEAR_TREE,
      FILL_SUBSURFACE,
      FILL_SURFACE,
      BORDER_ANTIFLOOD,
      CLEAR_SUBGROUND,
      STABILIZE_FALLING;

      // $VF: synthetic method
      private static TerrainTraversal.Action[] $values() {
         return new TerrainTraversal.Action[]{CLEAR_AIR, CLEAR_TREE, FILL_SUBSURFACE, FILL_SURFACE, BORDER_ANTIFLOOD, CLEAR_SUBGROUND, STABILIZE_FALLING};
      }
   }

   public record BlockContext(int wx, int wz, int y, BlockState existing, boolean isInsideBuilding, BlockState surfaceBlock, BlockState subSurfaceBlock) {
   }

   @FunctionalInterface
   public interface BlockVisitor {
      void visit(TerrainTraversal.BlockContext var1, TerrainTraversal.Action var2);
   }
}
