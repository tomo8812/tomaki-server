package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class tP<T extends tL> extends tM<T> {
   private static final double a = 0.001;
   private final int b;
   private final int c;
   private final double d;
   private final double e;
   private final zF f;
   private double[][] g;
   private List<T> h;
   private List<tJ<T>> i;

   private tP(int var1, double var2) {
      this(var1, var2, -1, new tY());
   }

   private tP(int var1, double var2, int var4, tY var5) {
      this(var1, var2, -1, var5, 0.001, new zy());
   }

   private tP(int var1, double var2, int var4, tW var5, double var6, zy var8) {
      super(var5);
      if (var2 <= 1.0) {
         throw new pn(var2, 1.0, false);
      }

      this.b = var1;
      this.d = var2;
      this.c = var4;
      this.e = 0.001;
      this.f = var8;
      this.g = null;
      this.h = null;
      this.i = null;
   }

   private int b() {
      return this.b;
   }

   private double c() {
      return this.d;
   }

   private int d() {
      return this.c;
   }

   private double e() {
      return this.e;
   }

   private zF f() {
      return this.f;
   }

   private tq g() {
      if (this.g == null) {
         throw new oX();
      } else {
         return td.a(this.g);
      }
   }

   private List<T> h() {
      return this.h;
   }

   private List<tJ<T>> i() {
      return this.i;
   }

   private double j() {
      if (this.h != null && this.i != null) {
         int var1 = 0;
         double var2 = 0.0;

         for (tL var5 : this.h) {
            int var6 = 0;

            for (tJ var8 : this.i) {
               double var9 = this.a(var5, var8.a);
               var2 += var9 * var9 * BY.a(this.g[var1][var6], this.d);
               var6++;
            }

            var1++;
         }

         return var2;
      } else {
         throw new oX();
      }
   }

   @Override
   public final List<tJ<T>> a(Collection<T> var1) {
      Ci.a(var1);
      int var2;
      if ((var2 = var1.size()) < this.b) {
         throw new pn(var2, this.b, false);
      }

      this.h = Collections.unmodifiableList(new ArrayList<>(var1));
      this.i = new ArrayList<>();
      this.g = new double[var2][this.b];
      double[][] var24 = new double[var2][this.b];
      if (var2 == 0) {
         return this.i;
      }

      tP var4 = this;

      for (int var5 = 0; var5 < var4.h.size(); var5++) {
         for (int var11 = 0; var11 < var4.b; var11++) {
            var4.g[var5][var11] = var4.f.nextDouble();
         }

         var4.g[var5] = Ch.a(var4.g[var5], 1.0);
      }

      var2 = this.h.get(0).a().length;

      for (int var3 = 0; var3 < this.b; var3++) {
         this.i.add(new tJ<>(new tO(new double[var2])));
      }

      int var27 = 0;
      var2 = this.c < 0 ? Integer.MAX_VALUE : this.c;

      double var42;
      do {
         double[][] var32 = var24;
         var4 = this;

         for (int var39 = 0; var39 < var4.h.size(); var39++) {
            System.arraycopy(var4.g[var39], 0, var32[var39], 0, var4.i.size());
         }

         var4 = this;
         int var33 = 0;
         ArrayList var40 = new ArrayList(var4.b);

         for (Iterator var12 = var4.i.iterator(); var12.hasNext(); var33++) {
            tL var6 = ((tJ)var12.next()).a;
            int var15 = 0;
            double[] var16 = new double[var6.a().length];
            double var17 = 0.0;

            for (tL var20 : var4.h) {
               double var21 = BY.a(var4.g[var15][var33], var4.d);
               double[] var23 = var20.a();

               for (int var36 = 0; var36 < var16.length; var36++) {
                  var16[var36] += var21 * var23[var36];
               }

               var17 += var21;
               var15++;
            }

            Ch.a(1.0 / var17, var16);
            var40.add(new tJ(new tO(var16)));
         }

         var4.i.clear();
         var4.i = var40;
         var4 = this;

         for (int var34 = 0; var34 < var4.h.size(); var34++) {
            tL var41 = var4.h.get(var34);
            double var43 = Double.MIN_VALUE;
            int var37 = -1;

            for (int var44 = 0; var44 < var4.i.size(); var44++) {
               double var45 = 0.0;
               double var18;
               if ((var18 = BY.w(var4.a(var41, var4.i.get(var44).a))) != 0.0) {
                  for (tJ var48 : var4.i) {
                     double var22;
                     if ((var22 = BY.w(var4.a(var41, var48.a))) == 0.0) {
                        var45 = Double.POSITIVE_INFINITY;
                        break;
                     }

                     var45 += BY.a(var18 / var22, 2.0 / (var4.d - 1.0));
                  }
               }

               double var47;
               if (var45 == 0.0) {
                  var47 = 1.0;
               } else if (var45 == Double.POSITIVE_INFINITY) {
                  var47 = 0.0;
               } else {
                  var47 = 1.0 / var45;
               }

               var4.g[var34][var44] = var47;
               if (var4.g[var34][var44] > var43) {
                  var43 = var4.g[var34][var44];
                  var37 = var44;
               }
            }

            var4.i.get(var37).a((T)var41);
         }

         var32 = var24;
         var4 = this;
         var42 = 0.0;

         for (int var13 = 0; var13 < var4.h.size(); var13++) {
            for (int var38 = 0; var38 < var4.i.size(); var38++) {
               var42 = BY.e(BY.w(var4.g[var13][var38] - var32[var13][var38]), var42);
            }
         }
      } while (var42 > this.e && ++var27 < var2);

      return this.i;
   }

   private void k() {
      int var1 = 0;
      ArrayList var2 = new ArrayList(this.b);

      for (Iterator var3 = this.i.iterator(); var3.hasNext(); var1++) {
         tL var4 = ((tJ)var3.next()).a;
         int var5 = 0;
         double[] var14 = new double[var4.a().length];
         double var8 = 0.0;

         for (tL var7 : this.h) {
            double var12 = BY.a(this.g[var5][var1], this.d);
            double[] var15 = var7.a();

            for (int var10 = 0; var10 < var14.length; var10++) {
               var14[var10] += var12 * var15[var10];
            }

            var8 += var12;
            var5++;
         }

         Ch.a(1.0 / var8, var14);
         var2.add(new tJ(new tO(var14)));
      }

      this.i.clear();
      this.i = var2;
   }

   private void l() {
      for (int var1 = 0; var1 < this.h.size(); var1++) {
         tL var2 = this.h.get(var1);
         double var3 = Double.MIN_VALUE;
         int var5 = -1;

         for (int var6 = 0; var6 < this.i.size(); var6++) {
            double var7 = 0.0;
            double var9;
            if ((var9 = BY.w(this.a(var2, this.i.get(var6).a))) != 0.0) {
               for (tJ var12 : this.i) {
                  double var13;
                  if ((var13 = BY.w(this.a(var2, var12.a))) == 0.0) {
                     var7 = Double.POSITIVE_INFINITY;
                     break;
                  }

                  var7 += BY.a(var9 / var13, 2.0 / (this.d - 1.0));
               }
            }

            double var15;
            if (var7 == 0.0) {
               var15 = 1.0;
            } else if (var7 == Double.POSITIVE_INFINITY) {
               var15 = 0.0;
            } else {
               var15 = 1.0 / var7;
            }

            this.g[var1][var6] = var15;
            if (this.g[var1][var6] > var3) {
               var3 = this.g[var1][var6];
               var5 = var6;
            }
         }

         this.i.get(var5).a((T)var2);
      }
   }

   private void m() {
      for (int var1 = 0; var1 < this.h.size(); var1++) {
         for (int var2 = 0; var2 < this.b; var2++) {
            this.g[var1][var2] = this.f.nextDouble();
         }

         this.g[var1] = Ch.a(this.g[var1], 1.0);
      }
   }

   private double a(double[][] var1) {
      double var2 = 0.0;

      for (int var4 = 0; var4 < this.h.size(); var4++) {
         for (int var5 = 0; var5 < this.i.size(); var5++) {
            var2 = BY.e(BY.w(this.g[var4][var5] - var1[var4][var5]), var2);
         }
      }

      return var2;
   }

   private void b(double[][] var1) {
      for (int var2 = 0; var2 < this.h.size(); var2++) {
         System.arraycopy(this.g[var2], 0, var1[var2], 0, this.i.size());
      }
   }
}
