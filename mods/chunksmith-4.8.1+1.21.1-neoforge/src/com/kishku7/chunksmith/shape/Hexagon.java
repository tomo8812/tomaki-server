package com.kishku7.chunksmith.shape;

import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.platform.util.Vector2;
import java.util.Arrays;
import java.util.List;

public class Hexagon extends AbstractPolygon {
   private final double p1x = this.centerX + this.radiusX * Math.cos(Math.toRadians(60.0));
   private final double p1z = this.centerZ + this.radiusX * Math.sin(Math.toRadians(60.0));
   private final double p2x = this.centerX + this.radiusX * Math.cos(Math.toRadians(120.0));
   private final double p2z = this.centerZ + this.radiusX * Math.sin(Math.toRadians(120.0));
   private final double p3x = this.centerX + this.radiusX * Math.cos(Math.toRadians(180.0));
   private final double p3z = this.centerZ + this.radiusX * Math.sin(Math.toRadians(180.0));
   private final double p4x = this.centerX + this.radiusX * Math.cos(Math.toRadians(240.0));
   private final double p4z = this.centerZ + this.radiusX * Math.sin(Math.toRadians(240.0));
   private final double p5x = this.centerX + this.radiusX * Math.cos(Math.toRadians(300.0));
   private final double p5z = this.centerZ + this.radiusX * Math.sin(Math.toRadians(300.0));
   private final double p6x = this.centerX + this.radiusX * Math.cos(Math.toRadians(360.0));
   private final double p6z = this.centerZ + this.radiusX * Math.sin(Math.toRadians(360.0));

   public Hexagon(Selection selection, boolean chunkAligned) {
      super(selection, chunkAligned);
   }

   @Override
   public List<Vector2> points() {
      return Arrays.asList(
         Vector2.of(this.p1x, this.p1z),
         Vector2.of(this.p2x, this.p2z),
         Vector2.of(this.p3x, this.p3z),
         Vector2.of(this.p4x, this.p4z),
         Vector2.of(this.p5x, this.p5z),
         Vector2.of(this.p6x, this.p6z)
      );
   }

   @Override
   public boolean isBounding(double x, double z) {
      boolean inside12 = ShapeUtil.insideLine(this.p1x, this.p1z, this.p2x, this.p2z, x, z);
      boolean inside23 = ShapeUtil.insideLine(this.p2x, this.p2z, this.p3x, this.p3z, x, z);
      boolean inside34 = ShapeUtil.insideLine(this.p3x, this.p3z, this.p4x, this.p4z, x, z);
      boolean inside45 = ShapeUtil.insideLine(this.p4x, this.p4z, this.p5x, this.p5z, x, z);
      boolean inside56 = ShapeUtil.insideLine(this.p5x, this.p5z, this.p6x, this.p6z, x, z);
      boolean inside61 = ShapeUtil.insideLine(this.p6x, this.p6z, this.p1x, this.p1z, x, z);
      return inside12 && inside23 && inside34 && inside45 && inside56 && inside61;
   }

   @Override
   public String name() {
      return "hexagon";
   }
}
