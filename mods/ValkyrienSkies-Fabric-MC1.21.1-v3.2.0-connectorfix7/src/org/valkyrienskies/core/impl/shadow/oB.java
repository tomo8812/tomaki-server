package org.valkyrienskies.core.impl.shadow;

public final class oB extends od {
   private final double[] b;
   private final sy c;
   private final tq d;
   private final double e;
   private final tq f;

   public oB(double[] var1, double[][] var2) {
      this(new zR(), var1, var2);
   }

   private oB(zR var1, double[] var2, double[][] var3) {
      super(var1, var2.length);
      int var21 = var2.length;
      if (var3.length != var21) {
         throw new oS(var3.length, var21);
      }

      for (int var4 = 0; var4 < var21; var4++) {
         if (var21 != var3[var4].length) {
            throw new oS(var3[var4].length, var21);
         }
      }

      double[] var22;
      this.b = Ch.a(var22 = var2, var22.length);
      this.c = new sy(var3);
      sN var28 = new sN(this.c);
      sN var6 = var28;
      sN var14 = var28;
      int var15 = 0;

      boolean var10001;
      while (true) {
         if (var15 >= var14.b.length) {
            var10001 = false;
            break;
         }

         if (!Cq.b(var14.b[var15], 0.0, 1.0E-12)) {
            var10001 = true;
            break;
         }

         var15++;
      }

      if (var10001) {
         throw new pb();
      }

      this.d = new sN.a(var6.a, var6.b, var6.c, (byte)0).b();
      var6 = var28;
      double var35 = 1.0;
      var2 = var6.a;
      int var25 = var6.a.length;

      for (int var5 = 0; var5 < var25; var5++) {
         double var19 = var2[var5];
         var35 *= var19;
      }

      this.e = var35;
      var2 = var28.c();

      for (int var26 = 0; var26 < var2.length; var26++) {
         if (var2[var26] < 0.0) {
            throw new te(var2[var26], var26, 0.0);
         }
      }

      sy var27 = new sy(var21, var21);

      for (int var30 = 0; var30 < var21; var30++) {
         int var36 = var30;
         double[] var33 = var28.c[var36].a().g();
         var27.b(var30, var33);
      }

      tq var31 = var27.d();

      for (int var34 = 0; var34 < var21; var34++) {
         double var10 = BY.a(var2[var34]);

         for (int var29 = 0; var29 < var21; var29++) {
            var31.b(var34, var29, var10);
         }
      }

      this.f = var27.c(var31);
   }

   public final double[] c() {
      double[] var1 = this.b;
      return Ch.a(this.b, var1.length);
   }

   private tq d() {
      return this.c.i();
   }

   @Override
   public final double a(double[] var1) {
      int var2 = this.a();
      if (var1.length != var2) {
         throw new oS(var1.length, var2);
      }

      double var10000 = BY.a(Math.PI * 2, -0.5 * var2) * BY.a(this.e, -0.5);
      double[] var11 = var1;
      oB var9 = this;
      double[] var3 = new double[var11.length];

      for (int var4 = 0; var4 < var3.length; var4++) {
         var3[var4] = var11[var4] - var9.c()[var4];
      }

      double[] var12 = var9.d.b(var3);
      double var7 = 0.0;

      for (int var10 = 0; var10 < var12.length; var10++) {
         var7 += var12[var10] * var3[var10];
      }

      return var10000 * BY.i(var7 * -0.5);
   }

   private double[] e() {
      int var1;
      double[] var2 = new double[var1 = this.a()];
      double[][] var3 = this.c.a();

      for (int var4 = 0; var4 < var1; var4++) {
         var2[var4] = BY.a(var3[var4][var4]);
      }

      return var2;
   }

   @Override
   public final double[] b() {
      int var1;
      double[] var2 = new double[var1 = this.a()];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = super.a.nextGaussian();
      }

      double[] var5 = this.f.a(var2);

      for (int var4 = 0; var4 < var1; var4++) {
         var5[var4] += this.b[var4];
      }

      return var5;
   }

   private double b(double[] var1) {
      double[] var2 = new double[var1.length];

      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = var1[var3] - this.c()[var3];
      }

      double[] var7 = this.d.b(var2);
      double var4 = 0.0;

      for (int var6 = 0; var6 < var7.length; var6++) {
         var4 += var7[var6] * var2[var6];
      }

      return BY.i(var4 * -0.5);
   }
}
