package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.joml.Matrix3dc;

public final class Hb {
   public final double a;
   public final Matrix3dc b;

   public Hb(double var1, Matrix3dc var3) {
      Intrinsics.checkNotNullParameter(var3, "");
      super();
      this.a = var1;
      this.b = var3;
   }

   public final double a() {
      return this.a;
   }

   public final Matrix3dc b() {
      return this.b;
   }

   private double c() {
      return this.a;
   }

   private Matrix3dc d() {
      return this.b;
   }

   private static Hb a(double var0, Matrix3dc var2) {
      Intrinsics.checkNotNullParameter(var2, "");
      return new Hb(var0, var2);
   }

   // $VF: synthetic method
   private static Hb a(Hb var0, double var1, Matrix3dc var3, int var4) {
      if ((var4 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var4 & 2) != 0) {
         var3 = var0.b;
      }

      Matrix3dc var7 = var3;
      double var5 = var1;
      Intrinsics.checkNotNullParameter(var7, "");
      return new Hb(var5, var7);
   }

   @Override
   public final String toString() {
      return "PhysicsBodyInertiaData(invMass=" + this.a + ", invMOI=" + this.b + ')';
   }

   @Override
   public final int hashCode() {
      return Double.hashCode(this.a) * 31 + this.b.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof Hb)) {
         return false;
      }

      other = (Hb)other;
      return !Intrinsics.areEqual(this.a, ((Hb)other).a) ? false : Intrinsics.areEqual(this.b, ((Hb)other).b);
   }
}
