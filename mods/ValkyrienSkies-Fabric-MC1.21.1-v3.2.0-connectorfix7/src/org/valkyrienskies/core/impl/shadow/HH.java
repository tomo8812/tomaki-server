package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;

public final class HH implements Hq {
   private double a;
   private double b;
   private int c;

   public HH(double var1, double var3, int var5) {
      this.a = var1;
      this.b = var3;
      this.c = var5;
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
   public final double k_() {
      return this.b;
   }

   @Override
   public final void b(double var1) {
      this.b = var1;
   }

   @Override
   public final int c() {
      return this.c;
   }

   @Override
   public final void a(int var1) {
      this.c = var1;
   }

   @Override
   public final void close() {
   }

   private double d() {
      return this.a;
   }

   private double e() {
      return this.b;
   }

   private int f() {
      return this.c;
   }

   private static HH a(double var0, double var2, int var4) {
      return new HH(var0, var2, var4);
   }

   // $VF: synthetic method
   private static HH a(HH var0, double var1, double var3, int var5, int var6) {
      if ((var6 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var6 & 2) != 0) {
         var3 = var0.b;
      }

      if ((var6 & 4) != 0) {
         var5 = var0.c;
      }

      int var11 = var5;
      double var9 = var3;
      double var7 = var1;
      return new HH(var7, var9, var11);
   }

   @Override
   public final String toString() {
      return "DummyWheelShapeReference(wheelRadius=" + this.a + ", halfThickness=" + this.b + ", pointShellSize=" + this.c + ')';
   }

   @Override
   public final int hashCode() {
      return (Double.hashCode(this.a) * 31 + Double.hashCode(this.b)) * 31 + Integer.hashCode(this.c);
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof HH)) {
         return false;
      } else {
         other = (HH)other;
         if (!Intrinsics.areEqual(this.a, ((HH)other).a)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.b, ((HH)other).b) ? false : this.c == ((HH)other).c;
         }
      }
   }
}
