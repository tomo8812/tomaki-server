package com.kishku7.chunksmith.shape;

import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.platform.util.Vector2;
import java.util.Arrays;
import java.util.List;

public class Star extends AbstractPolygon {
   private final double p1x = this.centerX + this.radiusX * Math.cos(Math.toRadians(54.0));
   private final double p1z = this.centerZ + this.radiusX * Math.sin(Math.toRadians(54.0));
   private final double p2x = this.centerX + this.radiusX * Math.cos(Math.toRadians(126.0));
   private final double p2z = this.centerZ + this.radiusX * Math.sin(Math.toRadians(126.0));
   private final double p3x = this.centerX + this.radiusX * Math.cos(Math.toRadians(198.0));
   private final double p3z = this.centerZ + this.radiusX * Math.sin(Math.toRadians(198.0));
   private final double p4x = this.centerX + this.radiusX * Math.cos(Math.toRadians(270.0));
   private final double p4z = this.centerZ + this.radiusX * Math.sin(Math.toRadians(270.0));
   private final double p5x = this.centerX + this.radiusX * Math.cos(Math.toRadians(342.0));
   private final double p5z = this.centerZ + this.radiusX * Math.sin(Math.toRadians(342.0));
   private final double i1x;
   private final double i1z;
   private final double i2x;
   private final double i2z;
   private final double i3x;
   private final double i3z;
   private final double i4x;
   private final double i4z;
   private final double i5x;
   private final double i5z;

   public Star(Selection selection, boolean chunkAligned) {
      super(selection, chunkAligned);
      Vector2 i1 = ShapeUtil.intersection(this.p1x, this.p1z, this.p3x, this.p3z, this.p2x, this.p2z, this.p5x, this.p5z)
         .orElse(Vector2.of(this.p1x, this.p1z));
      Vector2 i2 = ShapeUtil.intersection(this.p1x, this.p1z, this.p3x, this.p3z, this.p2x, this.p2z, this.p4x, this.p4z)
         .orElse(Vector2.of(this.p2x, this.p2z));
      Vector2 i3 = ShapeUtil.intersection(this.p2x, this.p2z, this.p4x, this.p4z, this.p3x, this.p3z, this.p5x, this.p5z)
         .orElse(Vector2.of(this.p3x, this.p3z));
      Vector2 i4 = ShapeUtil.intersection(this.p3x, this.p3z, this.p5x, this.p5z, this.p1x, this.p1z, this.p4x, this.p4z)
         .orElse(Vector2.of(this.p4x, this.p4z));
      Vector2 i5 = ShapeUtil.intersection(this.p1x, this.p1z, this.p4x, this.p4z, this.p2x, this.p2z, this.p5x, this.p5z)
         .orElse(Vector2.of(this.p5x, this.p5z));
      this.i1x = i1.getX();
      this.i1z = i1.getZ();
      this.i2x = i2.getX();
      this.i2z = i2.getZ();
      this.i3x = i3.getX();
      this.i3z = i3.getZ();
      this.i4x = i4.getX();
      this.i4z = i4.getZ();
      this.i5x = i5.getX();
      this.i5z = i5.getZ();
   }

   @Override
   public List<Vector2> points() {
      return Arrays.asList(
         Vector2.of(this.p1x, this.p1z),
         Vector2.of(this.i1x, this.i1z),
         Vector2.of(this.p2x, this.p2z),
         Vector2.of(this.i2x, this.i2z),
         Vector2.of(this.p3x, this.p3z),
         Vector2.of(this.i3x, this.i3z),
         Vector2.of(this.p4x, this.p4z),
         Vector2.of(this.i4x, this.i4z),
         Vector2.of(this.p5x, this.p5z),
         Vector2.of(this.i5x, this.i5z)
      );
   }

   @Override
   public boolean isBounding(double x, double z) {
      boolean inside13 = ShapeUtil.insideLine(this.p1x, this.p1z, this.p3x, this.p3z, x, z);
      boolean inside24 = ShapeUtil.insideLine(this.p2x, this.p2z, this.p4x, this.p4z, x, z);
      boolean inside35 = ShapeUtil.insideLine(this.p3x, this.p3z, this.p5x, this.p5z, x, z);
      boolean inside41 = ShapeUtil.insideLine(this.p4x, this.p4z, this.p1x, this.p1z, x, z);
      if (inside13 && inside24 && inside35 && inside41) {
         return true;
      } else {
         boolean inside52 = ShapeUtil.insideLine(this.p5x, this.p5z, this.p2x, this.p2z, x, z);
         if (inside24 && inside35 && inside41 && inside52) {
            return true;
         } else if (inside35 && inside41 && inside52 && inside13) {
            return true;
         } else {
            return inside41 && inside52 && inside13 && inside24 ? true : inside52 && inside13 && inside24 && inside35;
         }
      }
   }

   @Override
   public String name() {
      return "star";
   }
}
