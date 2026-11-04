package com.kishku7.chunksmith.shape;

import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.platform.util.Vector2;

public class Circle extends AbstractEllipse {
   public Circle(Selection selection, boolean chunkAligned) {
      super(selection, chunkAligned);
   }

   @Override
   public boolean isBounding(double x, double z) {
      return Math.hypot(this.centerX - x, this.centerZ - z) <= this.radiusX;
   }

   @Override
   public String name() {
      return "circle";
   }

   @Override
   public Vector2 radii() {
      return Vector2.of(this.radiusX, this.radiusX);
   }
}
