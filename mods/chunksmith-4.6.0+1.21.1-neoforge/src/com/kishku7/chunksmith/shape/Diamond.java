package com.kishku7.chunksmith.shape;

import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.platform.util.Vector2;
import java.util.Arrays;
import java.util.List;

public class Diamond extends AbstractPolygon {
   double p1x = this.centerX;
   double p1z = this.centerZ + this.radiusX;
   double p2x = this.centerX - this.radiusX;
   double p2z = this.centerZ;
   double p3x = this.centerX;
   double p3z = this.centerZ - this.radiusX;
   double p4x = this.centerX + this.radiusX;
   double p4z = this.centerZ;

   public Diamond(Selection selection, boolean chunkAligned) {
      super(selection, chunkAligned);
   }

   @Override
   public List<Vector2> points() {
      return Arrays.asList(Vector2.of(this.p1x, this.p1z), Vector2.of(this.p2x, this.p2z), Vector2.of(this.p3x, this.p3z), Vector2.of(this.p4x, this.p4z));
   }

   @Override
   public boolean isBounding(double x, double z) {
      if (!ShapeUtil.insideLine(this.p1x, this.p1z, this.p2x, this.p2z, x, z)) {
         return false;
      } else if (!ShapeUtil.insideLine(this.p2x, this.p2z, this.p3x, this.p3z, x, z)) {
         return false;
      } else {
         return !ShapeUtil.insideLine(this.p3x, this.p3z, this.p4x, this.p4z, x, z)
            ? false
            : ShapeUtil.insideLine(this.p4x, this.p4z, this.p1x, this.p1z, x, z);
      }
   }

   @Override
   public String name() {
      return "diamond";
   }
}
