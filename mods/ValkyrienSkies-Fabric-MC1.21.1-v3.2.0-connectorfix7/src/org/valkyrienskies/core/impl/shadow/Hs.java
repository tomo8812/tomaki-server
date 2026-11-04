package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;

public final class Hs implements GU {
   private double a;
   private double b;

   public Hs(double var1, double var3) {
      this.a = var1;
      this.b = var3;
   }

   @Override
   public final double a() {
      return this.a;
   }

   @Override
   public final void a(double var1) {
      this.a = var1;
   }

   @Override
   public final double h_() {
      return this.b;
   }

   @Override
   public final void b(double var1) {
      this.b = var1;
   }

   @Override
   public final void close() {
   }

   private double c() {
      return this.a;
   }

   private double d() {
      return this.b;
   }

   private static Hs a(double var0, double var2) {
      return new Hs(var0, var2);
   }

   // $VF: synthetic method
   private static Hs a(Hs var0, double var1, double var3, int var5) {
      if ((var5 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var5 & 2) != 0) {
         var3 = var0.b;
      }

      double var8 = var3;
      double var6 = var1;
      return new Hs(var6, var8);
   }

   @Override
   public final String toString() {
      return "DummyCapsuleShapeReference(radius=" + this.a + ", halfLength=" + this.b + ')';
   }

   @Override
   public final int hashCode() {
      return Double.hashCode(this.a) * 31 + Double.hashCode(this.b);
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof Hs)) {
         return false;
      }

      other = (Hs)other;
      return !Intrinsics.areEqual(this.a, ((Hs)other).a) ? false : Intrinsics.areEqual(this.b, ((Hs)other).b);
   }
}
