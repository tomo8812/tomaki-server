package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class uD implements Serializable, wA {
   private static final long a = -1417964919405031606L;
   private double b;
   private double c;
   private boolean d;
   private int e;
   private List<wB> f = new ArrayList<>();

   public uD() {
      this.b = Double.NaN;
      this.c = Double.NaN;
      this.d = true;
      this.e = 0;
   }

   private void a(uD var1) {
      if (var1.f.size() != 0) {
         if (this.f.size() == 0) {
            this.b = var1.b;
            this.d = var1.d;
         } else {
            if (this.d().length != var1.d().length) {
               throw new oS(var1.d().length, this.d().length);
            }

            if (this.d ^ var1.d) {
               throw new oV(px.PROPAGATION_DIRECTION_MISMATCH);
            }

            wB var2;
            double var3 = (var2 = this.f.get(this.e)).h();
            double var5 = var2.g();
            double var7 = var3 - var5;
            double var9;
            if (BY.w(var9 = var1.b - var3) > 0.001 * BY.w(var7)) {
               throw new oV(px.HOLE_BETWEEN_MODELS_TIME_RANGES, BY.w(var9));
            }
         }

         for (wB var12 : var1.f) {
            this.f.add(var12.d());
         }

         this.e = this.f.size() - 1;
         this.c = this.f.get(this.e).h();
      }
   }

   @Override
   public final void a(double var1, double[] var3, double var4) {
      this.b = Double.NaN;
      this.c = Double.NaN;
      this.d = true;
      this.e = 0;
      this.f.clear();
   }

   @Override
   public final void a(ws var1, boolean var2) {
      if (this.f.size() == 0) {
         this.b = var1.g();
         this.d = var1.j();
      }

      this.f.add(var1.d());
      if (var2) {
         this.c = var1.h();
         this.e = this.f.size() - 1;
      }
   }

   private double a() {
      return this.b;
   }

   private double b() {
      return this.c;
   }

   private double c() {
      return this.f.get(this.e).i();
   }

   private void a(double var1) {
      int var3 = 0;
      wB var4 = this.f.get(0);
      double var5 = 0.5 * (var4.g() + var4.h());
      int var7 = this.f.size() - 1;
      wB var8 = this.f.get(var7);
      double var9 = 0.5 * (var8.g() + var8.h());
      if (this.a(var1, var4) <= 0) {
         this.e = 0;
         var4.d(var1);
      } else if (this.a(var1, var8) >= 0) {
         this.e = var7;
         var8.d(var1);
      } else {
         while (var7 - var3 > 5) {
            var4 = this.f.get(this.e);
            int var33;
            if ((var33 = this.a(var1, var4)) < 0) {
               var7 = this.e;
               var9 = 0.5 * (var4.g() + var4.h());
            } else {
               if (var33 <= 0) {
                  var4.d(var1);
                  return;
               }

               var3 = this.e;
               var5 = 0.5 * (var4.g() + var4.h());
            }

            int var32 = (var3 + var7) / 2;
            var8 = this.f.get(var32);
            double var15;
            if (!(BY.w((var15 = 0.5 * (var8.g() + var8.h())) - var5) < 1.0E-6) && !(BY.w(var9 - var15) < 1.0E-6)) {
               double var17 = var9 - var15;
               double var19 = var15 - var5;
               double var21 = var9 - var5;
               double var23 = var1 - var9;
               double var25 = var1 - var15;
               double var27 = var1 - var5;
               double var29 = (var25 * var27 * var19 * var7 - var23 * var27 * var21 * var32 + var23 * var25 * var17 * var3) / (var17 * var19 * var21);
               this.e = (int)BY.A(var29);
            } else {
               this.e = var32;
            }

            int var35 = BY.b(var3 + 1, (var3 * 9 + var7) / 10);
            int var18 = BY.a(var7 - 1, (var3 + var7 * 9) / 10);
            if (this.e < var35) {
               this.e = var35;
            } else if (this.e > var18) {
               this.e = var18;
            }
         }

         this.e = var3;

         while (this.e <= var7 && this.a(var1, this.f.get(this.e)) > 0) {
            this.e++;
         }

         this.f.get(this.e).d(var1);
      }
   }

   private double[] d() {
      return this.f.get(this.e).k();
   }

   private double[] e() {
      return this.f.get(this.e).l();
   }

   private double[] a(int var1) {
      return this.f.get(this.e).a(var1);
   }

   private double[] b(int var1) {
      return this.f.get(this.e).b(var1);
   }

   private int a(double var1, wB var3) {
      if (this.d) {
         if (var1 < var3.g()) {
            return -1;
         } else {
            return var1 > var3.h() ? 1 : 0;
         }
      } else if (var1 > var3.g()) {
         return -1;
      } else {
         return var1 < var3.h() ? 1 : 0;
      }
   }
}
