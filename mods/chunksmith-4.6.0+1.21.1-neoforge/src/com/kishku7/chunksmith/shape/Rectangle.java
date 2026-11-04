package com.kishku7.chunksmith.shape;

import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.platform.util.Vector2;
import java.util.Arrays;
import java.util.List;

public class Rectangle extends AbstractPolygon {
   double b1x = this.centerX - this.radiusX;
   double b1z = this.centerZ - this.radiusZ;
   double b2x = this.centerX + this.radiusX;
   double b2z = this.centerZ + this.radiusZ;
   double p1x = this.centerX + this.radiusX;
   double p1z = this.centerZ - this.radiusZ;
   double p2x = this.centerX - this.radiusX;
   double p2z = this.centerZ - this.radiusZ;
   double p3x = this.centerX - this.radiusX;
   double p3z = this.centerZ + this.radiusZ;
   double p4x = this.centerX + this.radiusX;
   double p4z = this.centerZ + this.radiusZ;

   protected Rectangle(Selection selection, boolean chunkAligned) {
      super(selection, chunkAligned);
   }

   @Override
   public List<Vector2> points() {
      return Arrays.asList(Vector2.of(this.p1x, this.p1z), Vector2.of(this.p2x, this.p2z), Vector2.of(this.p3x, this.p3z), Vector2.of(this.p4x, this.p4z));
   }

   @Override
   public boolean isBounding(double x, double z) {
      return x >= this.b1x && x <= this.b2x && z >= this.b1z && z <= this.b2z;
   }

   @Override
   public String name() {
      return "rectangle";
   }
}
