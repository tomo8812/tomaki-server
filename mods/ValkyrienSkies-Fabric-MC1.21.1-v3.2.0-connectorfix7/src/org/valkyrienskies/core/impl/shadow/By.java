package org.valkyrienskies.core.impl.shadow;

public final class By extends Bt {
   private tn a = null;
   private final double b = 0.0;

   public By() {
      this(0.0);
   }

   private By(double var1) {
   }

   private void a(double[] var1, double[][] var2) {
      a(var2, var1);
      this.a(var1);
      this.a(var2);
   }

   @Override
   public final void a(double[] var1, int var2, int var3) {
      super.a(var1, var2, var3);
      this.a = new tn(this.a(), this.b);
   }

   private tq m() {
      tn var1 = this.a;
      if (this.a.c == null) {
         tn var2 = var1;
         if (var1.d == null) {
            int var3 = var2.a.length;
            int var4;
            double[][] var5 = new double[var4 = var2.a[0].length][var4];

            for (int var6 = var4 - 1; var6 >= BY.a(var4, var3); var6--) {
               var5[var6][var6] = 1.0;
            }

            for (int var24 = BY.a(var4, var3) - 1; var24 >= 0; var24--) {
               double[] var20 = var2.a[var24];
               var5[var24][var24] = 1.0;
               if (var20[var24] != 0.0) {
                  for (int var7 = var24; var7 < var4; var7++) {
                     double var16 = 0.0;

                     for (int var8 = var24; var8 < var4; var8++) {
                        var16 -= var5[var7][var8] * var20[var8];
                     }

                     var16 /= var2.b[var24] * var20[var24];

                     for (int var27 = var24; var27 < var4; var27++) {
                        var5[var7][var27] = var5[var7][var27] + -var16 * var20[var27];
                     }
                  }
               }
            }

            var2.d = td.a(var5);
         }

         var1.c = var2.d.d();
      }

      tq var18 = var1.c;
      int var19 = this.a.a().g();
      int var21 = var18.g();
      sy var22;
      double[][] var23 = (var22 = new sy(var21, var21)).a;

      for (int var25 = 0; var25 < var21; var25++) {
         for (int var26 = 0; var26 < var21; var26++) {
            if (var25 == var26 && var25 < var19) {
               var23[var25][var26] = 1.0;
            } else {
               var23[var25][var26] = 0.0;
            }
         }
      }

      return var18.c(var22).c(var18.d());
   }

   private double n() {
      return this.c() ? Aa.a(this.b().g()) : new AH().a(this.b().g());
   }

   private double o() {
      tu var1;
      return (var1 = this.l()).b(var1);
   }

   private double p() {
      return 1.0 - this.o() / this.n();
   }

   private double q() {
      double var1 = this.a().f();
      if (this.c()) {
         By var3 = this;
         return 1.0 - (1.0 - (1.0 - var3.o() / var3.n())) * (var1 / (var1 - this.a().g()));
      } else {
         return 1.0 - this.o() * (var1 - 1.0) / (this.n() * (var1 - this.a().g()));
      }
   }

   @Override
   protected final void a(double[][] var1) {
      super.a(var1);
      this.a = new tn(this.a(), this.b);
   }

   @Override
   protected final tu i() {
      return this.a.c().a(this.b());
   }

   @Override
   protected final tq j() {
      int var1 = this.a().g();
      tq var2 = this.a.a().a(0, var1 - 1, 0, var1 - 1);
      tq var3;
      return (var3 = new tb(var2).a().b()).c(var3.d());
   }
}
