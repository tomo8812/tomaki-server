package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

class AL extends AH implements Serializable {
   private static final long h = -7818711964045118679L;
   protected double f;
   protected double g;

   AL() {
      this.f = Double.NaN;
      this.g = Double.NaN;
   }

   AL(AL var1) {
      a(var1, this);
   }

   @Override
   public void a(double var1) {
      if (super.a < 1L) {
         this.f = super.e = super.b = 0.0;
      }

      double var3 = super.e;
      super.a(var1);
      this.g = super.d * super.d;
      double var5 = super.a;
      this.f = this.f - 3.0 * super.d * var3 + (var5 - 1.0) * (var5 - 2.0) * this.g * super.c;
   }

   @Override
   public double c() {
      return this.f;
   }

   @Override
   public void b() {
      super.b();
      this.f = Double.NaN;
      this.g = Double.NaN;
   }

   public AL h() {
      AL var1 = new AL();
      a(this, var1);
      return var1;
   }

   public static void a(AL var0, AL var1) {
      Ci.a(var0);
      Ci.a(var1);
      AH.a(var0, var1);
      var1.f = var0.f;
      var1.g = var0.g;
   }

   // $VF: synthetic method
   @Override
   public AH i() {
      return this.h();
   }

   // $VF: synthetic method
   @Override
   public AC f() {
      return this.h();
   }

   // $VF: synthetic method
   @Override
   public An a() {
      return this.h();
   }

   // $VF: synthetic method
   @Override
   public AA d() {
      return this.h();
   }
}
