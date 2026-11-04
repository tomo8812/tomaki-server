package net.caffeinemc.mods.lithium.common.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeEmpty extends VoxelShape implements VoxelShapeCaster {
   private static final DoubleList EMPTY_LIST = DoubleArrayList.wrap(new double[]{0.0});

   public VoxelShapeEmpty(DiscreteVoxelShape voxels) {
      super(voxels);
   }

   protected double collideX(AxisCycle axisCycle, AABB box, double maxDist) {
      return maxDist;
   }

   public DoubleList getCoords(Axis axis) {
      return EMPTY_LIST;
   }

   public double min(Axis axis) {
      return Double.POSITIVE_INFINITY;
   }

   public double max(Axis axis) {
      return Double.NEGATIVE_INFINITY;
   }

   public boolean isEmpty() {
      return true;
   }

   @Override
   public boolean intersects(AABB box, double blockX, double blockY, double blockZ) {
      return false;
   }
}
