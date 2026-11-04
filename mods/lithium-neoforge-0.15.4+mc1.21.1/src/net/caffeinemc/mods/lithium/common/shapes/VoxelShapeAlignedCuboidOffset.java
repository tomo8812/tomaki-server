package net.caffeinemc.mods.lithium.common.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.caffeinemc.mods.lithium.common.shapes.lists.OffsetFractionalDoubleList;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeAlignedCuboidOffset extends VoxelShapeAlignedCuboid {
   private final double xOffset;
   private final double yOffset;
   private final double zOffset;

   public VoxelShapeAlignedCuboidOffset(VoxelShapeAlignedCuboid originalShape, DiscreteVoxelShape voxels, double xOffset, double yOffset, double zOffset) {
      super(
         voxels,
         originalShape.minX + xOffset,
         originalShape.minY + yOffset,
         originalShape.minZ + zOffset,
         originalShape.maxX + xOffset,
         originalShape.maxY + yOffset,
         originalShape.maxZ + zOffset,
         originalShape.xyzResolution
      );
      if (originalShape instanceof VoxelShapeAlignedCuboidOffset) {
         this.xOffset = ((VoxelShapeAlignedCuboidOffset)originalShape).xOffset + xOffset;
         this.yOffset = ((VoxelShapeAlignedCuboidOffset)originalShape).yOffset + yOffset;
         this.zOffset = ((VoxelShapeAlignedCuboidOffset)originalShape).zOffset + zOffset;
      } else {
         this.xOffset = xOffset;
         this.yOffset = yOffset;
         this.zOffset = zOffset;
      }
   }

   @Override
   public VoxelShape move(double x, double y, double z) {
      return new VoxelShapeAlignedCuboidOffset(this, this.shape, x, y, z);
   }

   @Override
   public double collideX(AxisCycle cycleDirection, AABB box, double maxDist) {
      if (Math.abs(maxDist) < 1.0E-7) {
         return 0.0;
      }

      double penetration = this.calculatePenetration(cycleDirection, box, maxDist);
      return penetration != maxDist && this.intersects(cycleDirection, box) ? penetration : maxDist;
   }

   private double calculatePenetration(AxisCycle dir, AABB box, double maxDist) {
      switch (dir) {
         case NONE:
            return calculatePenetration(this.minX, this.maxX, this.getXSegments(), this.xOffset, box.minX, box.maxX, maxDist);
         case FORWARD:
            return calculatePenetration(this.minZ, this.maxZ, this.getZSegments(), this.zOffset, box.minZ, box.maxZ, maxDist);
         case BACKWARD:
            return calculatePenetration(this.minY, this.maxY, this.getYSegments(), this.yOffset, box.minY, box.maxY, maxDist);
         default:
            throw new IllegalArgumentException();
      }
   }

   private static double calculatePenetration(double aMin, double aMax, int segmentsPerUnit, double shapeOffset, double bMin, double bMax, double maxDist) {
      if (maxDist > 0.0) {
         double gap = aMin - bMax;
         if (gap >= -1.0E-7) {
            return Math.min(gap, maxDist);
         }

         if (segmentsPerUnit == 1) {
            return maxDist;
         }

         int segment = Mth.ceil((bMax - 1.0E-6 - shapeOffset) * segmentsPerUnit);
         double wallPos = (double)segment / segmentsPerUnit + shapeOffset;
         if (wallPos < bMax - 1.0E-7) {
            segment++;
            wallPos = (double)segment / segmentsPerUnit + shapeOffset;
         }

         return wallPos < aMax - 1.0E-6 ? Math.min(maxDist, wallPos - bMax) : maxDist;
      } else {
         double gap = aMax - bMin;
         if (gap <= 1.0E-7) {
            return Math.max(gap, maxDist);
         }

         if (segmentsPerUnit == 1) {
            return maxDist;
         }

         int segment = Mth.floor((bMin + 1.0E-6 - shapeOffset) * segmentsPerUnit);
         double wallPos = (double)segment / segmentsPerUnit + shapeOffset;
         if (wallPos > bMin + 1.0E-7) {
            segment--;
            wallPos = (double)segment / segmentsPerUnit + shapeOffset;
         }

         return wallPos > aMin + 1.0E-6 ? Math.max(maxDist, wallPos - bMin) : maxDist;
      }
   }

   @Override
   public DoubleList getCoords(Axis axis) {
      return switch (axis) {
         case X -> new OffsetFractionalDoubleList(this.getXSegments(), this.xOffset);
         case Y -> new OffsetFractionalDoubleList(this.getYSegments(), this.yOffset);
         case Z -> new OffsetFractionalDoubleList(this.getZSegments(), this.zOffset);
         default -> throw new MatchException(null, null);
      };
   }

   @Override
   protected double get(Axis axis, int index) {
      return switch (axis) {
         case X -> this.xOffset + (double)index / this.getXSegments();
         case Y -> this.yOffset + (double)index / this.getYSegments();
         case Z -> this.zOffset + (double)index / this.getZSegments();
         default -> throw new MatchException(null, null);
      };
   }

   @Override
   protected int findIndex(Axis axis, double coord) {
      int numSegments;

      coord = switch (axis) {
         case X -> (coord - this.xOffset) * (numSegments = this.getXSegments());
         case Y -> (coord - this.yOffset) * (numSegments = this.getYSegments());
         case Z -> (coord - this.zOffset) * (numSegments = this.getZSegments());
         default -> throw new MatchException(null, null);
      };
      return Mth.clamp(Mth.floor(coord), -1, numSegments);
   }
}
