package net.caffeinemc.mods.lithium.common.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class VoxelShapeMatchesAnywhere {
   public static void cuboidMatchesAnywhere(VoxelShape shapeA, VoxelShape shapeB, BooleanOp predicate, CallbackInfoReturnable<Boolean> cir) {
      if (shapeA instanceof VoxelShapeSimpleCube && shapeB instanceof VoxelShapeSimpleCube) {
         if (((VoxelShapeSimpleCube)shapeA).isTiny || ((VoxelShapeSimpleCube)shapeB).isTiny) {
            return;
         }

         if (predicate.apply(true, true)) {
            if (intersects((VoxelShapeSimpleCube)shapeA, (VoxelShapeSimpleCube)shapeB)) {
               cir.setReturnValue(true);
               return;
            }

            cir.setReturnValue(predicate.apply(true, false) || predicate.apply(false, true));
         } else {
            if (predicate.apply(true, false) && exceedsShape((VoxelShapeSimpleCube)shapeA, (VoxelShapeSimpleCube)shapeB)) {
               cir.setReturnValue(true);
               return;
            }

            if (predicate.apply(false, true) && exceedsShape((VoxelShapeSimpleCube)shapeB, (VoxelShapeSimpleCube)shapeA)) {
               cir.setReturnValue(true);
               return;
            }
         }

         cir.setReturnValue(false);
      } else if (shapeA instanceof VoxelShapeSimpleCube || shapeB instanceof VoxelShapeSimpleCube) {
         VoxelShapeSimpleCube simpleCube = (VoxelShapeSimpleCube)(shapeA instanceof VoxelShapeSimpleCube ? shapeA : shapeB);
         VoxelShape otherShape = simpleCube == shapeA ? shapeB : shapeA;
         if (simpleCube.isTiny || isTiny(otherShape)) {
            return;
         }

         boolean acceptSimpleCubeAlone = predicate.apply(shapeA == simpleCube, shapeB == simpleCube);
         if (acceptSimpleCubeAlone
            && exceedsCube(
               simpleCube,
               otherShape.min(Axis.X),
               otherShape.min(Axis.Y),
               otherShape.min(Axis.Z),
               otherShape.max(Axis.X),
               otherShape.max(Axis.Y),
               otherShape.max(Axis.Z)
            )) {
            cir.setReturnValue(true);
            return;
         }

         boolean acceptAnd = predicate.apply(true, true);
         boolean acceptOtherShapeAlone = predicate.apply(shapeA == otherShape, shapeB == otherShape);
         DiscreteVoxelShape voxelSet = otherShape.shape;
         DoubleList pointPositionsX = otherShape.getCoords(Axis.X);
         DoubleList pointPositionsY = otherShape.getCoords(Axis.Y);
         DoubleList pointPositionsZ = otherShape.getCoords(Axis.Z);
         int xMax = voxelSet.lastFull(Axis.X);
         int yMax = voxelSet.lastFull(Axis.Y);
         int zMax = voxelSet.lastFull(Axis.Z);
         double simpleCubeMaxX = simpleCube.max(Axis.X);
         double simpleCubeMinX = simpleCube.min(Axis.X);
         double simpleCubeMaxY = simpleCube.max(Axis.Y);
         double simpleCubeMinY = simpleCube.min(Axis.Y);
         double simpleCubeMaxZ = simpleCube.max(Axis.Z);
         double simpleCubeMinZ = simpleCube.min(Axis.Z);

         for (int x = voxelSet.firstFull(Axis.X); x < xMax; x++) {
            boolean simpleCubeIntersectsXSlice = simpleCubeMaxX - 1.0E-7 > pointPositionsX.getDouble(x)
               && simpleCubeMinX < pointPositionsX.getDouble(x + 1) - 1.0E-7;
            if (acceptOtherShapeAlone || simpleCubeIntersectsXSlice) {
               boolean xSliceExceedsCube = acceptOtherShapeAlone
                  && (!(simpleCubeMaxX >= pointPositionsX.getDouble(x + 1) - 1.0E-7) || !(simpleCubeMinX - 1.0E-7 <= pointPositionsX.getDouble(x)));

               for (int y = voxelSet.firstFull(Axis.Y); y < yMax; y++) {
                  boolean simpleCubeIntersectsYSlice = simpleCubeMaxY - 1.0E-7 > pointPositionsY.getDouble(y)
                     && simpleCubeMinY < pointPositionsY.getDouble(y + 1) - 1.0E-7;
                  if (acceptOtherShapeAlone || simpleCubeIntersectsYSlice) {
                     boolean ySliceExceedsCube = acceptOtherShapeAlone
                        && (!(simpleCubeMaxY >= pointPositionsY.getDouble(y + 1) - 1.0E-7) || !(simpleCubeMinY - 1.0E-7 <= pointPositionsY.getDouble(y)));

                     for (int z = voxelSet.firstFull(Axis.Z); z < zMax; z++) {
                        boolean simpleCubeIntersectsZSlice = simpleCubeMaxZ - 1.0E-7 > pointPositionsZ.getDouble(z)
                           && simpleCubeMinZ < pointPositionsZ.getDouble(z + 1) - 1.0E-7;
                        if (acceptOtherShapeAlone || simpleCubeIntersectsZSlice) {
                           boolean zSliceExceedsCube = acceptOtherShapeAlone
                              && (!(simpleCubeMaxZ >= pointPositionsZ.getDouble(z + 1) - 1.0E-7) || !(simpleCubeMinZ - 1.0E-7 <= pointPositionsZ.getDouble(z)));
                           boolean o = voxelSet.isFullWide(x, y, z);
                           boolean s = simpleCubeIntersectsXSlice && simpleCubeIntersectsYSlice && simpleCubeIntersectsZSlice;
                           if (acceptAnd && o && s
                              || acceptSimpleCubeAlone && !o && s
                              || acceptOtherShapeAlone && o && (xSliceExceedsCube || ySliceExceedsCube || zSliceExceedsCube)) {
                              cir.setReturnValue(true);
                              return;
                           }
                        }
                     }
                  }
               }
            }
         }

         cir.setReturnValue(false);
      }
   }

   private static boolean isTiny(VoxelShape shapeA) {
      return shapeA.min(Axis.X) > shapeA.max(Axis.X) - 3.0E-7
         || shapeA.min(Axis.Y) > shapeA.max(Axis.Y) - 3.0E-7
         || shapeA.min(Axis.Z) > shapeA.max(Axis.Z) - 3.0E-7;
   }

   private static boolean exceedsCube(VoxelShapeSimpleCube a, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      return a.min(Axis.X) < minX - 1.0E-7
         || a.max(Axis.X) > maxX + 1.0E-7
         || a.min(Axis.Y) < minY - 1.0E-7
         || a.max(Axis.Y) > maxY + 1.0E-7
         || a.min(Axis.Z) < minZ - 1.0E-7
         || a.max(Axis.Z) > maxZ + 1.0E-7;
   }

   private static boolean exceedsShape(VoxelShapeSimpleCube a, VoxelShapeSimpleCube b) {
      return a.min(Axis.X) < b.min(Axis.X) - 1.0E-7
         || a.max(Axis.X) > b.max(Axis.X) + 1.0E-7
         || a.min(Axis.Y) < b.min(Axis.Y) - 1.0E-7
         || a.max(Axis.Y) > b.max(Axis.Y) + 1.0E-7
         || a.min(Axis.Z) < b.min(Axis.Z) - 1.0E-7
         || a.max(Axis.Z) > b.max(Axis.Z) + 1.0E-7;
   }

   private static boolean intersects(VoxelShapeSimpleCube a, VoxelShapeSimpleCube b) {
      return a.min(Axis.X) < b.max(Axis.X) - 1.0E-7
         && a.max(Axis.X) > b.min(Axis.X) + 1.0E-7
         && a.min(Axis.Y) < b.max(Axis.Y) - 1.0E-7
         && a.max(Axis.Y) > b.min(Axis.Y) + 1.0E-7
         && a.min(Axis.Z) < b.max(Axis.Z) - 1.0E-7
         && a.max(Axis.Z) > b.min(Axis.Z) + 1.0E-7;
   }
}
