package dev.diorama.drawing;

import dev.diorama.Facing;
import dev.diorama.Projection;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

public final class Drawing {
   public UUID id = UUID.randomUUID();
   public Drawing.Kind kind = Drawing.Kind.STROKE;
   public String dimension = "";
   public int color;
   public double[] points = new double[0];
   public int turns;
   private static final int TURN_Y = 64;
   private transient double @Nullable [] shown;
   private transient int shownTurns = -1;
   public String text = "";
   public @Nullable String map;

   public Drawing() {
   }

   public double[] shown() {
      Facing now = Projection.facing();
      if (now.turns == this.turns) {
         return this.points;
      }

      if (this.shown == null || this.shownTurns != now.turns) {
         Facing drawn = Facing.values()[Math.floorMod(this.turns, 4)];
         double[] out = new double[this.points.length];

         for (int i = 0; i + 1 < out.length; i += 2) {
            double u = this.points[i];
            double z = Projection.zAt(this.points[i + 1], 64.0);
            double x = drawn.worldX(u, z);
            double worldZ = drawn.worldZ(u, z);
            out[i] = Projection.u(x, worldZ);
            out[i + 1] = Projection.v(x, 64.0, worldZ);
         }

         this.shown = out;
         this.shownTurns = now.turns;
      }

      return this.shown;
   }

   public Drawing fixed() {
      Drawing copy = new Drawing(this.kind, this.dimension, this.color, (double[])this.shown().clone(), this.text);
      copy.turns = Projection.facing().turns;
      return copy;
   }

   public Drawing(Drawing.Kind kind, String dimension, int color, double[] points, String text) {
      this.kind = kind;
      this.dimension = dimension;
      this.color = color;
      this.points = points;
      this.turns = Projection.facing().turns;
      this.text = text;
   }

   public int pointCount() {
      return this.points.length / 2;
   }

   public boolean complete() {
      return switch (this.kind) {
         case STROKE -> this.pointCount() >= 2;
         case AREA -> this.pointCount() >= 3;
         case NOTE -> this.pointCount() == 1 && !this.text.isBlank();
      };
   }

   public enum Kind {
      STROKE,
      AREA,
      NOTE;

      // $VF: synthetic method
      private static Drawing.Kind[] $values() {
         return new Drawing.Kind[]{STROKE, AREA, NOTE};
      }
   }
}
