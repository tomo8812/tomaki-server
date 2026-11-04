package org.valkyrienskies.mod.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public final class AdvancedBlockWalker implements Iterator<AdvancedBlockWalker.BlockPosWithDistance> {
   private final class_243 from;
   private final class_243 to;
   private final boolean reversed;
   private final boolean useFurtherestClip;
   private final double maxDist;
   private final ArrayList<AdvancedBlockWalker.BlockWalkerWithShip> walkers = new ArrayList<>();

   public AdvancedBlockWalker(class_1937 level, class_243 from, class_243 to, boolean reversed, boolean useFurtherestClip) {
      if (reversed) {
         class_243 tmp = from;
         from = to;
         to = tmp;
      }

      class_243 var18;
      this.from = var18 = VSGameUtilsKt.toWorldCoordinates(level, from);
      class_243 var19;
      this.to = var19 = VSGameUtilsKt.toWorldCoordinates(level, to);
      this.reversed = reversed;
      this.useFurtherestClip = useFurtherestClip;
      this.maxDist = var18.method_1022(var19);
      AdvancedBlockWalker.BlockWalkerWithShip walker = new AdvancedBlockWalker.BlockWalkerWithShip(new BlockWalker(var18, var19), null);
      if (walker.value() != null) {
         this.walkers.add(walker);
      }

      class_238 worldBox = new class_238(var18, var19);
      AABBd tmpBox = new AABBd();
      AABBd tmpBox2 = new AABBd();
      Vector3d fromVec = new Vector3d();
      Vector3d toVec = new Vector3d();
      Vector2d tmp2d = new Vector2d();

      for (Ship ship : VSGameUtilsKt.getAllShips(level)) {
         AABBdc shipBox1 = ship.getWorldAABB();
         if (tmpBox2.setMin(shipBox1.minX() - 1.0, shipBox1.minY() - 1.0, shipBox1.minZ() - 1.0)
            .setMax(shipBox1.maxX() + 1.0, shipBox1.maxY() + 1.0, shipBox1.maxZ() + 1.0)
            .intersectsAABB(
               tmpBox.setMin(worldBox.field_1323, worldBox.field_1322, worldBox.field_1321)
                  .setMax(worldBox.field_1320, worldBox.field_1325, worldBox.field_1324)
            )) {
            AABBic shipBox2 = ship.getShipAABB();
            if (shipBox2 != null) {
               ship.getWorldToShip().transformPosition(fromVec.set(var18.field_1352, var18.field_1351, var18.field_1350));
               ship.getWorldToShip().transformPosition(toVec.set(var19.field_1352, var19.field_1351, var19.field_1350));
               int intersectResult = tmpBox.setMin(shipBox2.minX() - 1, shipBox2.minY() - 1, shipBox2.minZ() - 1)
                  .setMax(shipBox2.maxX() + 2, shipBox2.maxY() + 2, shipBox2.maxZ() + 2)
                  .intersectsLineSegment(fromVec.x, fromVec.y, fromVec.z, toVec.x, toVec.y, toVec.z, tmp2d);
               if (intersectResult != -1) {
                  AdvancedBlockWalker.BlockWalkerWithShip walkerx = new AdvancedBlockWalker.BlockWalkerWithShip(
                     new BlockWalker(new class_243(fromVec.x, fromVec.y, fromVec.z), new class_243(toVec.x, toVec.y, toVec.z)), ship
                  );
                  if (walkerx.value() != null) {
                     this.walkers.add(walkerx);
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean hasNext() {
      return !this.walkers.isEmpty();
   }

   public AdvancedBlockWalker.BlockPosWithDistance next() {
      int size = this.walkers.size();
      if (size == 0) {
         throw new NoSuchElementException();
      }

      AdvancedBlockWalker.BlockWalkerWithShip walker = this.walkers.get(0);
      int walkerIndex = 0;
      double dist = this.calcDistance(walker.value(), walker.ship());

      for (int i = 1; i < size; i++) {
         AdvancedBlockWalker.BlockWalkerWithShip w = this.walkers.get(i);
         double d = this.calcDistance(w.value(), w.ship());
         if (d < dist) {
            walker = w;
            walkerIndex = i;
            dist = d;
         }
      }

      class_2338 pos = walker.value().method_10062();
      if (!walker.next()) {
         int lastIndex = size - 1;
         this.walkers.set(walkerIndex, this.walkers.get(lastIndex));
         this.walkers.remove(lastIndex);
      }

      double dist0 = Math.sqrt(dist);
      return new AdvancedBlockWalker.BlockPosWithDistance(pos, this.reversed ? Math.max(this.maxDist - dist0, 0.0) : Math.min(dist0, this.maxDist));
   }

   private double calcDistance(class_2338 pos, Ship ship) {
      class_238 block = new class_238(pos).method_1014(1.0E-7);
      class_243 from = this.from;
      class_243 to = this.to;
      if (this.reversed != this.useFurtherestClip) {
         class_243 tmp = from;
         from = to;
         to = tmp;
      }

      if (ship != null) {
         Vector3d fromVec = ship.getWorldToShip().transformPosition(new Vector3d(from.field_1352, from.field_1351, from.field_1350));
         Vector3d toVec = ship.getWorldToShip().transformPosition(new Vector3d(to.field_1352, to.field_1351, to.field_1350));
         from = new class_243(fromVec.x, fromVec.y, fromVec.z);
         to = new class_243(toVec.x, toVec.y, toVec.z);
      }

      if (block.method_1006(from)) {
         return this.reversed == this.useFurtherestClip ? 0.0 : this.maxDist * this.maxDist;
      }

      class_243 point = (class_243)block.method_992(from, to).orElse(null);
      if (point == null) {
         return Double.POSITIVE_INFINITY;
      }

      if (ship != null) {
         Vector3d pointVec = ship.getShipToWorld().transformPosition(new Vector3d(point.field_1352, point.field_1351, point.field_1350));
         point = new class_243(pointVec.x, pointVec.y, pointVec.z);
      }

      return point.method_1025(this.from);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object next() {
      return this.next();
   }

   public record BlockPosWithDistance(class_2338 pos, double distance) {
   }

   private record BlockWalkerWithShip(BlockWalker walker, Ship ship) {
      BlockWalkerWithShip {
         if (walker.value() != null) {
            while (!withinShipBorder(ship, walker.value()) && walker.next()) {
            }
         }
      }

      private boolean withinBorder(class_2338 pos) {
         return withinShipBorder(this.ship, pos);
      }

      private static boolean withinShipBorder(Ship ship, class_2338 pos) {
         if (ship == null) {
            return true;
         }

         AABBic shipBox = ship.getShipAABB();
         if (shipBox == null) {
            return false;
         }

         int x = pos.method_10263();
         int y = pos.method_10264();
         int z = pos.method_10260();
         return shipBox.minX() - 1 <= x
            && x < shipBox.maxX() + 2
            && shipBox.minY() - 1 <= y
            && y < shipBox.maxY() + 2
            && shipBox.minZ() - 1 <= z
            && z < shipBox.maxZ() + 2;
      }

      public class_2338 value() {
         return this.walker.value();
      }

      public boolean next() {
         while (this.walker.next()) {
            if (this.withinBorder(this.value())) {
               return true;
            }
         }

         return false;
      }
   }
}
