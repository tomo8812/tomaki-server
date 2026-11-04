package net.caffeinemc.mods.lithium.common.shapes;

import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public class CuboidVoxelSet extends DiscreteVoxelShape {
   private final int minX;
   private final int minY;
   private final int minZ;
   private final int maxX;
   private final int maxY;
   private final int maxZ;

   protected CuboidVoxelSet(int xSize, int ySize, int zSize, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      super(xSize, ySize, zSize);
      this.minX = (int)Math.round(minX * xSize);
      this.maxX = (int)Math.round(maxX * xSize);
      this.minY = (int)Math.round(minY * ySize);
      this.maxY = (int)Math.round(maxY * ySize);
      this.minZ = (int)Math.round(minZ * zSize);
      this.maxZ = (int)Math.round(maxZ * zSize);
   }

   public boolean isFull(int x, int y, int z) {
      return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ;
   }

   public void fill(int x, int y, int z) {
      throw new UnsupportedOperationException();
   }

   public int firstFull(Axis axis) {
      return axis.choose(this.minX, this.minY, this.minZ);
   }

   public int lastFull(Axis axis) {
      return axis.choose(this.maxX, this.maxY, this.maxZ);
   }

   public boolean isEmpty() {
      return this.minX >= this.maxX || this.minY >= this.maxY || this.minZ >= this.maxZ;
   }
}
