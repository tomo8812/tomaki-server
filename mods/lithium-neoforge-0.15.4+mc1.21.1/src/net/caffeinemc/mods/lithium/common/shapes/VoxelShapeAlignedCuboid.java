package net.caffeinemc.mods.lithium.common.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CubePointRange;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeAlignedCuboid extends VoxelShapeSimpleCube {
   static final double LARGE_EPSILON = 1.0E-6;
   protected final byte xyzResolution;

   public VoxelShapeAlignedCuboid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int xRes, int yRes, int zRes) {
      super(new CuboidVoxelSet(1 << xRes, 1 << yRes, 1 << zRes, minX, minY, minZ, maxX, maxY, maxZ), minX, minY, minZ, maxX, maxY, maxZ);
      if (xRes <= 3 && yRes <= 3 && zRes <= 3 && xRes >= 0 && yRes >= 0 && zRes >= 0) {
         this.xyzResolution = (byte)(xRes << 4 | yRes << 2 | zRes);
      } else {
         throw new IllegalArgumentException("Resolution must be between 0 and 3");
      }
   }

   public VoxelShapeAlignedCuboid(DiscreteVoxelShape voxels, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, byte xyzResolution) {
      super(voxels, minX, minY, minZ, maxX, maxY, maxZ);
      this.xyzResolution = xyzResolution;
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
            return calculatePenetration(this.minX, this.maxX, this.getXSegments(), box.minX, box.maxX, maxDist);
         case FORWARD:
            return calculatePenetration(this.minZ, this.maxZ, this.getZSegments(), box.minZ, box.maxZ, maxDist);
         case BACKWARD:
            return calculatePenetration(this.minY, this.maxY, this.getYSegments(), box.minY, box.maxY, maxDist);
         default:
            throw new IllegalArgumentException();
      }
   }

   private static double calculatePenetration(double aMin, double aMax, int segmentsPerUnit, double bMin, double bMax, double maxDist) {
      if (maxDist > 0.0) {
         double gap = aMin - bMax;
         if (gap >= -1.0E-7) {
            return Math.min(gap, maxDist);
         }

         if (segmentsPerUnit == 1) {
            return maxDist;
         }

         double wallPos = (double)Mth.ceil((bMax - 1.0E-7) * segmentsPerUnit) / segmentsPerUnit;
         return wallPos < aMax - 1.0E-6 ? Math.min(maxDist, wallPos - bMax) : maxDist;
      } else {
         double gap = aMax - bMin;
         if (gap <= 1.0E-7) {
            return Math.max(gap, maxDist);
         }

         if (segmentsPerUnit == 1) {
            return maxDist;
         }

         double wallPos = (double)Mth.floor((bMin + 1.0E-7) * segmentsPerUnit) / segmentsPerUnit;
         return wallPos > aMin + 1.0E-6 ? Math.max(maxDist, wallPos - bMin) : maxDist;
      }
   }

   @Override
   public DoubleList getCoords(Axis axis) {
      return switch (axis) {
         case X -> new CubePointRange(this.getXSegments());
         case Y -> new CubePointRange(this.getYSegments());
         case Z -> new CubePointRange(this.getZSegments());
         default -> throw new MatchException(null, null);
      };
   }

   @Override
   protected double get(Axis axis, int index) {
      return switch (axis) {
         case X -> (double)index / this.getXSegments();
         case Y -> (double)index / this.getYSegments();
         case Z -> (double)index / this.getZSegments();
         default -> throw new MatchException(null, null);
      };
   }

   @Override
   protected int findIndex(Axis axis, double coord) {
      int i = switch (axis) {
         case X -> this.getXSegments();
         case Y -> this.getYSegments();
         case Z -> this.getZSegments();
         default -> throw new MatchException(null, null);
      };
      return Mth.clamp(Mth.floor(coord * i), -1, i);
   }

   protected int getXSegments() {
      return 1 << (this.xyzResolution >>> 4);
   }

   protected int getYSegments() {
      return 1 << (this.xyzResolution >>> 2 & 3);
   }

   protected int getZSegments() {
      return 1 << (this.xyzResolution & 3);
   }
}
