package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;
import java.util.Comparator;

@Deprecated
public class xX<FUNC extends kY> implements xY<FUNC> {
   private final xY<FUNC> a;
   private int b;
   private int c;
   private int d;
   private zH e;
   private yq[] f;

   protected xX(xY<FUNC> var1, int var2, zH var3) {
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

   private yq[] d() {
      if (this.f == null) {
         throw new oX(px.NO_OPTIMUM_COMPUTED_YET);
      } else {
         return (yq[])this.f.clone();
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
   public final ya<yq> c() {
      return this.a.c();
   }

   @Override
   public final yq a(int var1, FUNC var2, double[] var3, double[] var4, double[] var5) {
      this.b = var1;
      RuntimeException var6 = null;
      this.f = new yq[this.d];
      this.c = 0;

      for (int var7 = 0; var7 < this.d; var7++) {
         try {
            this.f[var7] = this.a.a(var1 - this.c, (FUNC)var2, var3, var4, var7 == 0 ? var5 : this.e.a());
         } catch (oR var8) {
            this.f[var7] = null;
         } catch (RuntimeException var9) {
            var6 = var9;
            this.f[var7] = null;
         }

         this.c = this.c + this.a.b();
      }

      double[] var12 = var4;
      double[] var11 = var3;
      xX var10 = this;
      Arrays.sort(this.f, new Comparator<yq>(var10, var11, var12) {
         // $VF: synthetic field
         private double[] a;
         // $VF: synthetic field
         private double[] b;
         // $VF: synthetic field
         private xX c;

         {
            this.c = var1;
            this.a = var2x;
            this.b = var3x;
         }

         private int a(yq var1, yq var2x) {
            if (var1 == null) {
               return var2x == null ? 0 : 1;
            } else {
               return var2x == null ? -1 : Double.compare(this.a(var1), this.a(var2x));
            }
         }

         private double a(yq var1) {
            double[] var8 = var1.d();
            double var3x = 0.0;

            for (int var2x = 0; var2x < var8.length; var2x++) {
               double var6x = var8[var2x] - this.a[var2x];
               var3x += this.b[var2x] * var6x * var6x;
            }

            return var3x;
         }

         // $VF: synthetic method
         @Override
         public final int compare(Object var1, Object var2) {
            yq var10001 = (yq)var1;
            yq var3 = var2;
            var2 = var10001;
            var var4 = this;
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

               var10000 = Double.compare(var4.a(var2), var4.a(var3));
            }

            return var10000;
         }
      });
      if (this.f[0] == null) {
         throw var6;
      } else {
         return this.f[0];
      }
   }

   private void a(double[] var1, double[] var2) {
      Arrays.sort(this.f, new Comparator<yq>(this, var1, var2) {
         // $VF: synthetic field
         private double[] a;
         // $VF: synthetic field
         private double[] b;
         // $VF: synthetic field
         private xX c;

         {
            this.c = var1;
            this.a = var2x;
            this.b = var3x;
         }

         private int a(yq var1, yq var2x) {
            if (var1 == null) {
               return var2x == null ? 0 : 1;
            } else {
               return var2x == null ? -1 : Double.compare(this.a(var1), this.a(var2x));
            }
         }

         private double a(yq var1) {
            double[] var8 = var1.d();
            double var3x = 0.0;

            for (int var2x = 0; var2x < var8.length; var2x++) {
               double var6x = var8[var2x] - this.a[var2x];
               var3x += this.b[var2x] * var6x * var6x;
            }

            return var3x;
         }

         // $VF: synthetic method
         @Override
         public final int compare(Object var1, Object var2) {
            yq var10001 = (yq)var1;
            yq var3 = var2;
            var2 = var10001;
            var var4 = this;
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

               var10000 = Double.compare(var4.a(var2), var4.a(var3));
            }

            return var10000;
         }
      });
   }
}
