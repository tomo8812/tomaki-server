package org.valkyrienskies.core.impl.shadow;

@Deprecated
public abstract class yP extends yB<kR> implements ye {
   @Deprecated
   private static final double c = 1.0E-14;
   @Deprecated
   private double[][] d;
   @Deprecated
   private int e;
   @Deprecated
   private int f;
   @Deprecated
   protected double[] a;
   @Deprecated
   private double[] g;
   @Deprecated
   private double[] h;
   @Deprecated
   protected double b;
   private ll i;
   private int j;
   private tq k;

   @Deprecated
   protected yP() {
   }

   protected yP(ya<yq> var1) {
      super(var1);
   }

   private int k() {
      return this.j;
   }

   @Deprecated
   private void l() {
      tq var1 = this.b(this.a);
      this.d = var1.b(-1.0).a();
   }

   protected final tq b(double[] var1) {
      this.j++;
      lg[] var2 = new lg[var1.length];
      int var3 = var1.length;

      for (int var4 = 0; var4 < var3; var4++) {
         var2[var4] = new lg(var3, 1, var4, var1[var4]);
      }

      lg[] var10 = this.i.a(var2);
      int var8 = this.f().length;
      if (var10.length != var8) {
         throw new oS(var10.length, var8);
      }

      double[][] var9 = new double[var8][var3];

      for (int var5 = 0; var5 < var8; var5++) {
         int[] var6 = new int[var3];

         for (int var7 = 0; var7 < var3; var7++) {
            var6[var7] = 1;
            var9[var5][var7] = var10[var5].a(var6);
            var6[var7] = 0;
         }
      }

      return this.k.c(td.a(var9));
   }

   @Deprecated
   private void m() {
      this.g = this.a(this.a);
      double[] var1 = this.d(this.g);
      this.b = this.c(var1);
      sA var2 = new sA(var1);
      this.h = this.k.a(var2).g();
   }

   protected final double c(double[] var1) {
      sA var2;
      return BY.a((var2 = new sA(var1)).b(this.e().a(var2)));
   }

   private double n() {
      return BY.a(this.o() / this.f);
   }

   private double o() {
      return this.b * this.b;
   }

   public final tq j() {
      return this.k.i();
   }

   protected final void a(double var1) {
      this.b = var1;
   }

   @Deprecated
   private double[][] p() {
      double var2 = 1.0E-14;
      yP var1 = this;
      return this.a(var1.a, 1.0E-14);
   }

   @Deprecated
   private double[][] b(double var1) {
      return this.a(this.a, 1.0E-14);
   }

   private double[][] a(double[] var1, double var2) {
      tq var4;
      tq var5 = (var4 = this.b(var1)).d().c(var4);
      return new tn(var5, var2).c().b().a();
   }

   @Deprecated
   private double[] q() {
      if (this.f <= this.e) {
         throw new pn(px.NO_DEGREES_OF_FREEDOM, this.f, this.e, false);
      }

      double[] var1 = new double[this.e];
      double var2 = BY.a(this.o() / (this.f - this.e));
      double[][] var4 = this.a(this.a, 1.0E-14);

      for (int var5 = 0; var5 < var1.length; var5++) {
         var1[var5] = BY.a(var4[var5][var5]) * var2;
      }

      return var1;
   }

   private double[] b(double[] var1, double var2) {
      int var4;
      double[] var5 = new double[var4 = var1.length];
      double[][] var6 = this.a(var1, var2);

      for (int var7 = 0; var7 < var4; var7++) {
         var5[var7] = BY.a(var6[var7][var7]);
      }

      return var5;
   }

   @Deprecated
   private yq a(int var1, kR var2, double[] var3, double[] var4, double[] var5) {
      return this.a(var1, kV.a(var2), new yv(var3), new yw(var4), new yg(var5));
   }

   @Deprecated
   private yq a(int var1, ll var2, double[] var3, double[] var4, double[] var5) {
      return this.a(var1, var2, new yv(var3), new yw(var4), new yg(var5));
   }

   @Deprecated
   private yq a(int var1, ll var2, yo... var3) {
      return super.a(var1, kV.a(var2), var3);
   }

   @Override
   public final void i() {
      super.i();
      this.j = 0;
      tq var1;
      tq var10001;
      if ((var1 = this.e()) instanceof sM) {
         int var2 = var1.f();
         sM var3 = new sM(var2);

         for (int var4 = 0; var4 < var2; var4++) {
            var3.c(var4, var4, BY.a(var1.b(var4, var4)));
         }

         var10001 = var3;
      } else {
         var10001 = new sN(var1).e();
      }

      this.k = var10001;
      this.i = kV.a(this.g());
      this.a = this.d();
      this.f = this.f().length;
      this.e = this.a.length;
   }

   protected final double[] d(double[] var1) {
      double[] var2 = this.f();
      if (var1.length != var2.length) {
         throw new oS(var2.length, var1.length);
      }

      double[] var3 = new double[var2.length];

      for (int var4 = 0; var4 < var2.length; var4++) {
         var3[var4] = var2[var4] - var1[var4];
      }

      return var3;
   }

   private static tq a(tq var0) {
      if (!(var0 instanceof sM)) {
         return new sN(var0).e();
      }

      int var1 = var0.f();
      sM var2 = new sM(var1);

      for (int var3 = 0; var3 < var1; var3++) {
         var2.c(var3, var3, BY.a(var0.b(var3, var3)));
      }

      return var2;
   }

   // $VF: synthetic method
   @Deprecated
   @Override
   public final yq a(int var1, kY var2, double[] var3, double[] var4, double[] var5) {
      kR var10002 = (kR)var2;
      double[] var6 = var5;
      var5 = var4;
      var4 = var3;
      kR var8 = var10002;
      int var7 = var1;
      return this.a(var7, kV.a(var8), new yv(var4), new yw(var5), new yg(var6));
   }
}
