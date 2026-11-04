package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class sM extends sv implements Serializable {
   private static final long a = 20121229L;
   private final double[] b;

   public sM(int var1) {
      super(var1, var1);
      this.b = new double[var1];
   }

   public sM(double[] var1) {
      this(var1, true);
   }

   private sM(double[] var1, boolean var2) {
      Ci.a(var1);
      this.b = var2 ? (double[])var1.clone() : var1;
   }

   @Override
   public final sv a(int var1, int var2) {
      if (var1 != var2) {
         throw new oS(var1, var2);
      } else {
         return new sM(var1);
      }
   }

   @Override
   public final sv i() {
      return new sM(this.b);
   }

   private sM a(sM var1) {
      td.a(this, var1);
      int var2;
      double[] var3 = new double[var2 = this.b.length];

      for (int var4 = 0; var4 < var2; var4++) {
         var3[var4] = this.b[var4] + var1.b[var4];
      }

      return new sM(var3, false);
   }

   private sM b(sM var1) {
      td.a(this, var1);
      int var2;
      double[] var3 = new double[var2 = this.b.length];

      for (int var4 = 0; var4 < var2; var4++) {
         var3[var4] = this.b[var4] - var1.b[var4];
      }

      return new sM(var3, false);
   }

   private sM c(sM var1) {
      td.b(this, var1);
      int var2;
      double[] var3 = new double[var2 = this.b.length];

      for (int var4 = 0; var4 < var2; var4++) {
         var3[var4] = this.b[var4] * var1.b[var4];
      }

      return new sM(var3, false);
   }

   @Override
   public final tq c(tq var1) {
      if (var1 instanceof sM) {
         return this.c((sM)var1);
      }

      td.b(this, var1);
      int var2 = var1.f();
      int var3 = var1.g();
      double[][] var4 = new double[var2][var3];

      for (int var5 = 0; var5 < var2; var5++) {
         for (int var6 = 0; var6 < var3; var6++) {
            var4[var5][var6] = this.b[var5] * var1.b(var5, var6);
         }
      }

      return new sy(var4, false);
   }

   @Override
   public final double[][] a() {
      int var1;
      double[][] var2 = new double[var1 = this.b.length][var1];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3][var3] = this.b[var3];
      }

      return var2;
   }

   private double[] j() {
      return this.b;
   }

   @Override
   public final double b(int var1, int var2) {
      td.a(this, var1, var2);
      return var1 == var2 ? this.b[var1] : 0.0;
   }

   @Override
   public final void c(int var1, int var2, double var3) {
      if (var1 == var2) {
         td.a(this, var1);
         this.b[var1] = var3;
      } else {
         d(var3);
      }
   }

   @Override
   public final void a(int var1, int var2, double var3) {
      if (var1 == var2) {
         td.a(this, var1);
         this.b[var1] = this.b[var1] + var3;
      } else {
         d(var3);
      }
   }

   @Override
   public final void b(int var1, int var2, double var3) {
      if (var1 == var2) {
         td.a(this, var1);
         this.b[var1] = this.b[var1] * var3;
      }
   }

   @Override
   public final int f() {
      return this.b.length;
   }

   @Override
   public final int g() {
      return this.b.length;
   }

   @Override
   public final double[] a(double[] var1) {
      return this.c(new sM(var1, false)).b;
   }

   @Override
   public final double[] b(double[] var1) {
      return this.a(var1);
   }

   @Override
   public final tu b(tu var1) {
      double[] var4;
      if (var1 instanceof sA) {
         sA var3;
         var4 = (var3 = (sA)var1).a;
      } else {
         var4 = var1.g();
      }

      double[] var2 = var4;
      return td.b(this.a(var2));
   }

   private static void d(double var0) {
      if (!Cq.b(0.0, var0, 1)) {
         throw new pm(BY.w(var0), 0, true);
      }
   }

   private sM k() {
      return this.c(0.0);
   }

   public final sM c(double var1) {
      if (this.e(var1)) {
         throw new tA();
      }

      double[] var3 = new double[this.b.length];

      for (int var2 = 0; var2 < this.b.length; var2++) {
         var3[var2] = 1.0 / this.b[var2];
      }

      return new sM(var3, false);
   }

   private boolean e(double var1) {
      for (int var3 = 0; var3 < this.b.length; var3++) {
         if (Cq.b(this.b[var3], 0.0, var1)) {
            return true;
         }
      }

      return false;
   }
}
