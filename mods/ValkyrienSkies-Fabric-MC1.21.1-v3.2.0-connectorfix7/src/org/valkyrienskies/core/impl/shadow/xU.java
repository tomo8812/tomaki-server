package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;
import java.util.Comparator;

@Deprecated
public class xU<FUNC extends kW> implements xV<FUNC> {
   private final xV<FUNC> a;
   private int b;
   private int c;
   private int d;
   private zH e;
   private yp[] f;

   protected xU(xV<FUNC> var1, int var2, zH var3) {
      if (var1 == null || var3 == null) {
         throw new pl();
      }

      if (var2 <= 0) {
         throw new pk(var2);
      }

      this.a = var1;
      this.d = var2;
      this.e = var3;
   }

   private yp[] d() {
      if (this.f == null) {
         throw new oX(px.NO_OPTIMUM_COMPUTED_YET);
      } else {
         return (yp[])this.f.clone();
      }
   }

   @Override
   public final int a() {
      return this.b;
   }

   @Override
   public final int b() {
      return this.c;
   }

   @Override
   public final ya<yp> c() {
      return this.a.c();
   }

   @Override
   public final yp a(int var1, FUNC var2, yf var3, double[] var4) {
      this.b = var1;
      RuntimeException var5 = null;
      this.f = new yp[this.d];
      this.c = 0;

      for (int var6 = 0; var6 < this.d; var6++) {
         try {
            this.f[var6] = this.a.a(var1 - this.c, (FUNC)var2, var3, var6 == 0 ? var4 : this.e.a());
         } catch (RuntimeException var7) {
            var5 = var7;
            this.f[var6] = null;
         }

         this.c = this.c + this.a.b();
      }

      yf var9 = var3;
      xU var8 = this;
      Arrays.sort(this.f, new Comparator<yp>(var8, var9) {
         // $VF: synthetic field
         private yf a;
         // $VF: synthetic field
         private xU b;

         {
            this.b = var1;
            this.a = var2x;
         }

         private int a(yp var1, yp var2x) {
            if (var1 == null) {
               return var2x == null ? 0 : 1;
            }

            if (var2x == null) {
               return -1;
            }

            double var3x = (Double)var1.c();
            double var5x = (Double)var2x.c();
            return this.a == yf.MINIMIZE ? Double.compare(var3x, var5x) : Double.compare(var5x, var3x);
         }

         // $VF: synthetic method
         @Override
         public final int compare(Object var1, Object var2) {
            yp var10001 = (yp)var1;
            yp var3 = var2;
            var2 = var10001;
            var var10 = this;
            int var10000;
            if (var2 == null) {
               if (var3 != null) {
                  return 1;
               }

               var10000 = 0;
            } else {
               if (var3 == null) {
                  return -1;
               }

               double var6 = (Double)var2.c();
               double var8 = (Double)var3.c();
               if (var10.a == yf.MINIMIZE) {
                  return Double.compare(var6, var8);
               }

               var10000 = Double.compare(var8, var6);
            }

            return var10000;
         }
      });
      if (this.f[0] == null) {
         throw var5;
      } else {
         return this.f[0];
      }
   }

   private void a(yf var1) {
      Arrays.sort(this.f, new Comparator<yp>(this, var1) {
         // $VF: synthetic field
         private yf a;
         // $VF: synthetic field
         private xU b;

         {
            this.b = var1;
            this.a = var2x;
         }

         private int a(yp var1, yp var2x) {
            if (var1 == null) {
               return var2x == null ? 0 : 1;
            }

            if (var2x == null) {
               return -1;
            }

            double var3x = (Double)var1.c();
            double var5x = (Double)var2x.c();
            return this.a == yf.MINIMIZE ? Double.compare(var3x, var5x) : Double.compare(var5x, var3x);
         }

         // $VF: synthetic method
         @Override
         public final int compare(Object var1, Object var2) {
            yp var10001 = (yp)var1;
            yp var3 = var2;
            var2 = var10001;
            var var10 = this;
            int var10000;
            if (var2 == null) {
               if (var3 != null) {
                  return 1;
               }

               var10000 = 0;
            } else {
               if (var3 == null) {
                  return -1;
               }

               double var6 = (Double)var2.c();
               double var8 = (Double)var3.c();
               if (var10.a == yf.MINIMIZE) {
                  return Double.compare(var6, var8);
               }

               var10000 = Double.compare(var8, var6);
            }

            return var10000;
         }
      });
   }
}
