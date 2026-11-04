package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

final class AD extends AL implements Serializable {
   private static final long h = 4763990447117157611L;
   private double i;

   AD() {
      this.i = Double.NaN;
   }

   private AD(AD var1) {
      a(var1, this);
   }

   @Override
   public final void a(double var1) {
      if (super.a < 1L) {
         this.i = 0.0;
         super.f = 0.0;
         super.e = 0.0;
         super.b = 0.0;
      }

      double var3 = super.f;
      double var5 = super.e;
      super.a(var1);
      double var7 = super.a;
      this.i = this.i - 4.0 * super.d * var3 + 6.0 * super.g * var5 + (var7 * var7 - 3.0 * (var7 - 1.0)) * (super.g * super.g * (var7 - 1.0) * var7);
   }

   @Override
   public final double c() {
      return this.i;
   }

   @Override
   public final void b() {
      super.b();
      this.i = Double.NaN;
   }

   public final AD g() {
      AD var1 = new AD();
      a(this, var1);
      return var1;
   }

   private static void a(AD var0, AD var1) {
      Ci.a(var0);
      Ci.a(var1);
      AL.a(var0, var1);
      var1.i = var0.i;
   }

   // $VF: synthetic method
   @Override
   public final AL h() {
      return this.g();
   }

   // $VF: synthetic method
   @Override
   public final AH i() {
      return this.g();
   }

   // $VF: synthetic method
   @Override
   public final AC f() {
      return this.g();
   }

   // $VF: synthetic method
   @Override
   public final An a() {
      return this.g();
   }

   // $VF: synthetic method
   @Override
   public final AA d() {
      return this.g();
   }
}
