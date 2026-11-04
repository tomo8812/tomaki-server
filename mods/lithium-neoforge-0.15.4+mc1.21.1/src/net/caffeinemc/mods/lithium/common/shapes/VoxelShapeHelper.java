package net.caffeinemc.mods.lithium.common.shapes;

import it.unimi.dsi.fastutil.doubles.Double2IntMap;
import it.unimi.dsi.fastutil.doubles.Double2IntOpenHashMap;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleComparators;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import net.caffeinemc.mods.lithium.mixin.minimal_nonvanilla.collisions.empty_space.ArrayVoxelShapeInvoker;
import net.caffeinemc.mods.lithium.mixin.minimal_nonvanilla.collisions.empty_space.BitSetDiscreteVoxelShapeAccessor;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeHelper {
   public static Optional<Vec3> getClosestPointTo(Vec3 target, VoxelShape collidingShape, List<AABB> boxes) {
      DoubleOpenHashSet xPoints = new DoubleOpenHashSet();
      DoubleOpenHashSet yPoints = new DoubleOpenHashSet();
      DoubleOpenHashSet zPoints = new DoubleOpenHashSet();
      xPoints.addAll(collidingShape.getCoords(Axis.X));
      yPoints.addAll(collidingShape.getCoords(Axis.Y));
      zPoints.addAll(collidingShape.getCoords(Axis.Z));
      double minX = collidingShape.min(Axis.X);
      double maxX = collidingShape.max(Axis.X);
      double minY = collidingShape.min(Axis.Y);
      double maxY = collidingShape.max(Axis.Y);
      double minZ = collidingShape.min(Axis.Z);
      double maxZ = collidingShape.max(Axis.Z);

      for (AABB box : boxes) {
         if (box.minX > minX) {
            xPoints.add(box.minX);
         }

         if (box.maxX < maxX) {
            xPoints.add(box.maxX);
         }

         if (box.minY > minY) {
            yPoints.add(box.minY);
         }

         if (box.maxY < maxY) {
            yPoints.add(box.maxY);
         }

         if (box.minZ > minZ) {
            zPoints.add(box.minZ);
         }

         if (box.maxZ < maxZ) {
            zPoints.add(box.maxZ);
         }
      }

      DoubleArrayList xList = new DoubleArrayList(xPoints);
      DoubleList yList = new DoubleArrayList(yPoints);
      DoubleList zList = new DoubleArrayList(zPoints);
      xList.sort(DoubleComparators.NATURAL_COMPARATOR);
      yList.sort(DoubleComparators.NATURAL_COMPARATOR);
      zList.sort(DoubleComparators.NATURAL_COMPARATOR);
      Double2IntMap xIndex = new Double2IntOpenHashMap();
      Double2IntMap yIndex = new Double2IntOpenHashMap();
      Double2IntMap zIndex = new Double2IntOpenHashMap();

      for (int i = 0; i < xList.size(); i++) {
         xIndex.put(xList.getDouble(i), i);
      }

      for (int i = 0; i < yList.size(); i++) {
         yIndex.put(yList.getDouble(i), i);
      }

      for (int i = 0; i < zList.size(); i++) {
         zIndex.put(zList.getDouble(i), i);
      }

      int xSize = xList.size() - 1;
      int ySize = yList.size() - 1;
      int zSize = zList.size() - 1;
      BitSetDiscreteVoxelShape bitSetVoxelSet = new BitSetDiscreteVoxelShape(xSize, ySize, zSize);
      bitSetVoxelSet.fill(0, 0, 0);
      bitSetVoxelSet.fill(xSize, ySize, zSize);
      BitSet bitSet = ((BitSetDiscreteVoxelShapeAccessor)bitSetVoxelSet).getStorage();
      bitSet.clear();
      initVoxelSet(bitSet, collidingShape, boxes, xList, yList, zList, xIndex, yIndex, zIndex, xSize, ySize, zSize);
      VoxelShape shape = ArrayVoxelShapeInvoker.init(bitSetVoxelSet, xList, yList, zList);
      return shape.closestPointTo(target);
   }

   private static void initVoxelSet(
      BitSet voxelSet,
      VoxelShape collidingShape,
      List<AABB> boxes,
      DoubleArrayList xList,
      DoubleList yList,
      DoubleList zList,
      Double2IntMap xIndex,
      Double2IntMap yIndex,
      Double2IntMap zIndex,
      int xSize,
      int ySize,
      int zSize
   ) {
      for (AABB collidingBox : collidingShape.toAabbs()) {
         int minX = xIndex.get(collidingBox.minX);
         int maxX = xIndex.get(collidingBox.maxX);
         int minY = yIndex.get(collidingBox.minY);
         int maxY = yIndex.get(collidingBox.maxY);
         int minZ = zIndex.get(collidingBox.minZ);
         int maxZ = zIndex.get(collidingBox.maxZ);

         for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
               for (int z = minZ; z < maxZ; z++) {
                  voxelSet.set(getIndex(x, y, z, xSize, ySize, zSize), true);
               }
            }
         }
      }

      BitSet remove = new BitSet(voxelSet.size());

      for (AABB box : boxes) {
         int minX = xIndex.getOrDefault(box.minX, 0);
         int maxX = xIndex.getOrDefault(box.maxX, xSize);
         int minY = yIndex.getOrDefault(box.minY, 0);
         int maxY = yIndex.getOrDefault(box.maxY, ySize);
         int minZ = zIndex.getOrDefault(box.minZ, 0);
         int maxZ = zIndex.getOrDefault(box.maxZ, zSize);

         for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
               for (int z = minZ; z < maxZ; z++) {
                  remove.set(getIndex(x, y, z, xSize, ySize, zSize));
               }
            }
         }
      }

      voxelSet.andNot(remove);
   }

   private static int getIndex(int x, int y, int z, int sizeX, int sizeY, int sizeZ) {
      return (x * sizeY + y) * sizeZ + z;
   }
}
