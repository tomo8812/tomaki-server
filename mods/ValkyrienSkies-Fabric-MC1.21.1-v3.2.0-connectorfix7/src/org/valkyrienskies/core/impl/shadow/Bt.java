package org.valkyrienskies.core.impl.shadow;

public abstract class Bt implements Bx {
   private sy a;
   private sA b;
   private boolean c = false;

   protected final tq a() {
      return this.a;
   }

   protected final tu b() {
      return this.b;
   }

   public final boolean c() {
      return this.c;
   }

   private void a(boolean var1) {
      this.c = var1;
   }

   public void a(double[] var1, int var2, int var3) {
      if (var1 == null) {
         throw new pl();
      }

      if (var1.length != var2 * (var3 + 1)) {
         throw new oS(var1.length, var2 * (var3 + 1));
      }

      if (var2 <= var3) {
         throw new oT(px.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE, var2, var3 + 1);
      }

      double[] var4 = new double[var2];
      var3 = this.c ? var3 : var3 + 1;
      double[][] var5 = new double[var2][var3];
      int var6 = 0;

      for (int var7 = 0; var7 < var2; var7++) {
         var4[var7] = var1[var6++];
         if (!this.c) {
            var5[var7][0] = 1.0;
         }

         for (int var8 = this.c ? 0 : 1; var8 < var3; var8++) {
            var5[var7][var8] = var1[var6++];
         }
      }

      this.a = new sy(var5);
      this.b = new sA(var4);
   }

   protected final void a(double[] var1) {
      if (var1 == null) {
         throw new pl();
      }

      if (var1.length == 0) {
         throw new pf();
      }

      this.b = new sA(var1);
   }

   protected void a(double[][] var1) {
      if (var1 == null) {
         throw new pl();
      }

      if (var1.length == 0) {
         throw new pf();
      }

      if (this.c) {
         this.a = new sy(var1, true);
      } else {
         int var2 = var1[0].length;
         double[][] var3 = new double[var1.length][var2 + 1];

         for (int var4 = 0; var4 < var1.length; var4++) {
            if (var1[var4].length != var2) {
               throw new oS(var1[var4].length, var2);
            }

            var3[var4][0] = 1.0;
            System.arraycopy(var1[var4], 0, var3[var4], 1, var2);
         }

         this.a = new sy(var3, false);
      }
   }

   protected static void a(double[][] var0, double[] var1) {
      if (var0 != null && var1 != null) {
         if (var0.length != var1.length) {
            throw new oS(var1.length, var0.length);
         }

         if (var0.length == 0) {
            throw new pf();
         }

         if (var0[0].length + 1 > var0.length) {
            throw new oV(px.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS, var0.length, var0[0].length);
         }
      } else {
         throw new pl();
      }
   }

   protected static void a(double[][] var0, double[][] var1) {
      if (var0.length != var1.length) {
         throw new oS(var0.length, var1.length);
      }

      if (var1.length > 0 && var1.length != var1[0].length) {
         throw new th(var1.length, var1[0].length);
      }
   }

   @Override
   public final double[] d() {
      return this.i().g();
   }

   @Override
   public final double[] e() {
      tu var1 = this.i();
      return this.b.k(this.a.a(var1)).g();
   }

   @Override
   public final double[][] f() {
      return this.j().a();
   }

   @Override
   public final double[] g() {
      double[][] var1 = this.j().a();
      double var2 = this.k();
      int var4;
      double[] var5 = new double[var4 = var1[0].length];

      for (int var6 = 0; var6 < var4; var6++) {
         var5[var6] = BY.a(var2 * var1[var6][var6]);
      }

      return var5;
   }

   @Override
   public final double h() {
      Bt var1 = this;
      return new AM().a(var1.b.g());
   }

   private double m() {
      return this.k();
   }

   private double n() {
      return BY.a(this.k());
   }

   protected abstract tu i();

   protected abstract tq j();

   private double o() {
      return new AM().a(this.b.g());
   }

   protected double k() {
      tu var1;
      return (var1 = this.l()).b(var1) / (this.a.f() - this.a.g());
   }

   protected final tu l() {
      tu var1 = this.i();
      return this.b.k(this.a.a(var1));
   }
}
