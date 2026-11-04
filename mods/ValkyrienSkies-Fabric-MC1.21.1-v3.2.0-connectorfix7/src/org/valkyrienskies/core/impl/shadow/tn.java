package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public class tn {
   public double[][] a;
   public double[] b;
   public tq c;
   public tq d;
   private tq e;
   private tq f;
   private final double g;

   public tn(sv var1) {
      this(var1, 0.0);
   }

   public tn(tq var1, double var2) {
      this.g = var2;
      int var4 = var1.f();
      int var3 = var1.g();
      this.a = var1.d().a();
      this.b = new double[BY.a(var4, var3)];
      this.c = null;
      this.d = null;
      this.e = null;
      this.f = null;
      this.a(this.a);
   }

   protected void a(double[][] var1) {
      for (int var2 = 0; var2 < BY.a(var1.length, var1[0].length); var2++) {
         this.a(var2, var1);
      }
   }

   protected void a(int var1, double[][] var2) {
      double[] var3 = var2[var1];
      double var4 = 0.0;

      for (int var6 = var1; var6 < var3.length; var6++) {
         double var7 = var3[var6];
         var4 += var7 * var7;
      }

      double var14 = var3[var1] > 0.0 ? -BY.a(var4) : BY.a(var4);
      this.b[var1] = var14;
      if (var14 != 0.0) {
         var3[var1] -= var14;

         for (int var8 = var1 + 1; var8 < var2.length; var8++) {
            double[] var12 = var2[var8];
            double var10 = 0.0;

            for (int var5 = var1; var5 < var12.length; var5++) {
               var10 -= var12[var5] * var3[var5];
            }

            var10 /= var14 * var3[var1];

            for (int var13 = var1; var13 < var12.length; var13++) {
               var12[var13] -= var10 * var3[var13];
            }
         }
      }
   }

   public final tq a() {
      if (this.e == null) {
         int var1 = this.a.length;
         int var2;
         double[][] var3 = new double[var2 = this.a[0].length][var1];

         for (int var5 = BY.a(var2, var1) - 1; var5 >= 0; var5--) {
            var3[var5][var5] = this.b[var5];

            for (int var4 = var5 + 1; var4 < var1; var4++) {
               var3[var5][var4] = this.a[var4][var5];
            }
         }

         this.e = td.a(var3);
      }

      return this.e;
   }

   public final tq b() {
      if (this.c == null) {
         tn var1 = this;
         if (this.d == null) {
            int var2 = var1.a.length;
            int var3;
            double[][] var4 = new double[var3 = var1.a[0].length][var3];

            for (int var5 = var3 - 1; var5 >= BY.a(var3, var2); var5--) {
               var4[var5][var5] = 1.0;
            }

            for (int var11 = BY.a(var3, var2) - 1; var11 >= 0; var11--) {
               double[] var10 = var1.a[var11];
               var4[var11][var11] = 1.0;
               if (var10[var11] != 0.0) {
                  for (int var6 = var11; var6 < var3; var6++) {
                     double var8 = 0.0;

                     for (int var7 = var11; var7 < var3; var7++) {
                        var8 -= var4[var6][var7] * var10[var7];
                     }

                     var8 /= var1.b[var11] * var10[var11];

                     for (int var12 = var11; var12 < var3; var12++) {
                        var4[var6][var12] = var4[var6][var12] + -var8 * var10[var12];
                     }
                  }
               }
            }

            var1.d = td.a(var4);
         }

         this.c = var1.d.d();
      }

      return this.c;
   }

   private tq d() {
      if (this.d == null) {
         int var1 = this.a.length;
         int var2;
         double[][] var3 = new double[var2 = this.a[0].length][var2];

         for (int var4 = var2 - 1; var4 >= BY.a(var2, var1); var4--) {
            var3[var4][var4] = 1.0;
         }

         for (int var10 = BY.a(var2, var1) - 1; var10 >= 0; var10--) {
            double[] var9 = this.a[var10];
            var3[var10][var10] = 1.0;
            if (var9[var10] != 0.0) {
               for (int var5 = var10; var5 < var2; var5++) {
                  double var7 = 0.0;

                  for (int var6 = var10; var6 < var2; var6++) {
                     var7 -= var3[var5][var6] * var9[var6];
                  }

                  var7 /= this.b[var10] * var9[var10];

                  for (int var11 = var10; var11 < var2; var11++) {
                     var3[var5][var11] = var3[var5][var11] + -var7 * var9[var11];
                  }
               }
            }
         }

         this.d = td.a(var3);
      }

      return this.d;
   }

   private tq e() {
      if (this.f == null) {
         int var1 = this.a.length;
         int var2;
         double[][] var3 = new double[var2 = this.a[0].length][var1];

         for (int var4 = 0; var4 < var2; var4++) {
            for (int var5 = 0; var5 < BY.a(var4 + 1, var1); var5++) {
               var3[var4][var5] = this.a[var5][var4] / -this.b[var5];
            }
         }

         this.f = td.a(var3);
      }

      return this.f;
   }

   public sG c() {
      return new tn.a(this.a, this.b, this.g, (byte)0);
   }

   static class a implements sG {
      private final double[][] a;
      private final double[] b;
      private final double c;

      private a(double[][] var1, double[] var2, double var3) {
         this.a = var1;
         this.b = var2;
         this.c = var3;
      }

      @Override
      public final boolean a() {
         double[] var1 = this.b;
         int var2 = this.b.length;

         for (int var3 = 0; var3 < var2; var3++) {
            double var4;
            if (BY.w(var4 = var1[var3]) <= this.c) {
               return false;
            }
         }

         return true;
      }

      @Override
      public final tu a(tu var1) {
         int var2 = this.a.length;
         int var3 = this.a[0].length;
         if (var1.f() != var3) {
            throw new oS(var1.f(), var3);
         }

         if (!this.a()) {
            throw new tA();
         }

         double[] var4 = new double[var2];
         double[] var10 = var1.g();

         for (int var5 = 0; var5 < BY.a(var3, var2); var5++) {
            double[] var7 = this.a[var5];
            double var8 = 0.0;

            for (int var6 = var5; var6 < var3; var6++) {
               var8 += var10[var6] * var7[var6];
            }

            var8 /= this.b[var5] * var7[var5];

            for (int var12 = var5; var12 < var3; var12++) {
               var10[var12] += var8 * var7[var12];
            }
         }

         for (int var11 = this.b.length - 1; var11 >= 0; var11--) {
            var10[var11] /= this.b[var11];
            double var14 = var10[var11];
            double[] var9 = this.a[var11];
            var4[var11] = var14;

            for (int var13 = 0; var13 < var11; var13++) {
               var10[var13] -= var14 * var9[var13];
            }
         }

         return new sA(var4, false);
      }

      @Override
      public final tq a(tq var1) {
         int var2 = this.a.length;
         int var3 = this.a[0].length;
         if (var1.f() != var3) {
            throw new oS(var1.f(), var3);
         }

         if (!this.a()) {
            throw new tA();
         }

         int var4;
         int var5 = ((var4 = var1.g()) + 52 - 1) / 52;
         double[][] var6 = sD.c(var2, var4);
         double[][] var7 = new double[var1.f()][52];
         double[] var8 = new double[52];

         for (int var9 = 0; var9 < var5; var9++) {
            int var10;
            int var11;
            int var12 = (var11 = BY.a((var10 = var9 * 52) + 52, var4)) - var10;
            var1.a(0, var3 - 1, var10, var11 - 1, var7);

            for (int var25 = 0; var25 < BY.a(var3, var2); var25++) {
               double[] var27 = this.a[var25];
               double var15 = 1.0 / (this.b[var25] * var27[var25]);
               Arrays.fill(var8, 0, var12, 0.0);

               for (int var17 = var25; var17 < var3; var17++) {
                  double var18 = var27[var17];
                  double[] var13 = var7[var17];

                  for (int var14 = 0; var14 < var12; var14++) {
                     var8[var14] += var18 * var13[var14];
                  }
               }

               for (int var38 = 0; var38 < var12; var38++) {
                  var8[var38] *= var15;
               }

               for (int var39 = var25; var39 < var3; var39++) {
                  double var40 = var27[var39];
                  double[] var30 = var7[var39];

                  for (int var33 = 0; var33 < var12; var33++) {
                     var30[var33] += var8[var33] * var40;
                  }
               }
            }

            for (int var26 = this.b.length - 1; var26 >= 0; var26--) {
               int var36 = (var11 = var26 / 52) * 52;
               double var16 = 1.0 / this.b[var26];
               double[] var41 = var7[var26];
               double[] var19 = var6[var11 * var5 + var9];
               int var31 = (var26 - var36) * var12;

               for (int var34 = 0; var34 < var12; var34++) {
                  var41[var34] *= var16;
                  var19[var31++] = var41[var34];
               }

               double[] var35 = this.a[var26];

               for (int var29 = 0; var29 < var26; var29++) {
                  double var23 = var35[var29];
                  double[] var32 = var7[var29];

                  for (int var37 = 0; var37 < var12; var37++) {
                     var32[var37] -= var41[var37] * var23;
                  }
               }
            }
         }

         return new sD(var2, var4, var6, false);
      }

      @Override
      public final tq b() {
         return this.a(td.a(this.a[0].length));
      }

      // $VF: synthetic method
      a(double[][] var1, double[] var2, double var3, byte var5) {
         this(var1, var2, var3);
      }
   }
}
