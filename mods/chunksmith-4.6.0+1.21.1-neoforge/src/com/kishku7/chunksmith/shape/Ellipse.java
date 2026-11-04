package com.kishku7.chunksmith.shape;

import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.platform.util.Vector2;

public class Ellipse extends AbstractEllipse {
   public Ellipse(Selection selection, boolean chunkAligned) {
      super(selection, chunkAligned);
   }

   @Override
   public boolean isBounding(double x, double z) {
      return Math.pow(x - this.centerX, 2.0) / Math.pow(this.radiusX, 2.0) + Math.pow(z - this.centerZ, 2.0) / Math.pow(this.radiusZ, 2.0) <= 1.0;
   }

   @Override
   public String name() {
      return "ellipse";
   }

   @Override
   public Vector2 radii() {
      return Vector2.of(this.radiusX, this.radiusZ);
   }
}
