package org.valkyrienskies.core.impl.shadow;

@Deprecated
public abstract class xI extends xB {
   private tq a;
   private double b;

   protected xI(wK<wR> var1) {
      super(var1);
   }

   protected final tq c(double[] var1) {
      return this.a.c(td.a(this.a(var1)));
   }

   protected final double d(double[] var1) {
      sA var2;
      return BY.a((var2 = new sA(var1)).b(this.b().a(var2)));
   }

   private double o() {
      xI var1 = this;
      return BY.a(this.b * var1.b / this.m());
   }

   private double p() {
      return this.b * this.b;
   }

   public final tq n() {
      return this.a.i();
   }

   protected final void a(double var1) {
      this.b = var1;
   }

   private double[][] a(double[] var1, double var2) {
      tq var4;
      tq var5 = (var4 = this.c(var1)).d().c(var4);
      return new tn(var5, var2).c().b().a();
   }

   private double[] b(double[] var1, double var2) {
      int var4;
      double[] var5 = new double[var4 = var1.length];
      double var10 = var2;
      double[] var15 = var1;
      tq var12;
      tq var13 = (var12 = this.c(var15)).d().c(var12);
      double[][] var14 = new tn(var13, var10).c().b().a();

      for (int var16 = 0; var16 < var4; var16++) {
         var5[var16] = BY.a(var14[var16][var16]);
      }

      return var5;
   }

   @Override
   public final wR c(wO... var1) {
      return super.c(var1);
   }

   protected final double[] e(double[] var1) {
      double[] var2 = this.d();
      if (var1.length != var2.length) {
         throw new oS(var2.length, var1.length);
      }

      double[] var3 = new double[var2.length];

      for (int var4 = 0; var4 < var2.length; var4++) {
         var3[var4] = var2[var4] - var1[var4];
      }

      return var3;
   }

   @Override
   public final void b(wO... var1) {
      super.b(var1);
      wO[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         wO var4;
         if ((var4 = var5[var3]) instanceof xH) {
            tq var6;
            tq var10001;
            if ((var6 = ((xH)var4).a()) instanceof sM) {
               var2 = var6.f();
               sM var8 = new sM(var2);

               for (int var9 = 0; var9 < var2; var9++) {
                  var8.c(var9, var9, BY.a(var6.b(var9, var9)));
               }

               var10001 = var8;
            } else {
               var10001 = new sN(var6).e();
            }

            this.a = var10001;
            return;
         }
      }
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
   @Override
   public final Object a(wO[] var1) {
      wO[] var2 = var1;
      return super.c(var2);
   }
}
