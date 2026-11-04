package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;
import java.util.Comparator;

@Deprecated
public final class zk<FUNC extends lc> implements zg<FUNC> {
   private final zg<FUNC> a;
   private int b;
   private int c;
   private int d;
   private zF e;
   private zm[] f;

   private zk(zg<FUNC> var1, int var2, zF var3) {
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

   @Override
   public final ya<zm> c() {
      return this.a.c();
   }

   @Override
   public final int a() {
      return this.b;
   }

   @Override
   public final int b() {
      return this.c;
   }

   private zm[] d() {
      if (this.f == null) {
         throw new oX(px.NO_OPTIMUM_COMPUTED_YET);
      } else {
         return (zm[])this.f.clone();
      }
   }

   @Override
   public final zm a(int var1, FUNC var2, yf var3, double var4, double var6) {
      return this.a(var1, (FUNC)var2, var3, var4, var6, var4 + 0.5 * (var6 - var4));
   }

   @Override
   public final zm a(int var1, FUNC var2, yf var3, double var4, double var6, double var8) {
      RuntimeException var10 = null;
      this.f = new zm[this.d];
      this.c = 0;

      for (int var11 = 0; var11 < this.d; var11++) {
         try {
            double var12 = var11 == 0 ? var8 : var4 + this.e.nextDouble() * (var6 - var4);
            this.f[var11] = this.a.a(var1 - this.c, (FUNC)var2, var3, var4, var6, var12);
         } catch (RuntimeException var14) {
            var10 = var14;
            this.f[var11] = null;
         }

         this.c = this.c + this.a.b();
      }

      yf var16 = var3;
      zk var15 = this;
      Arrays.sort(this.f, new Comparator<zm>(var15, var16) {
         // $VF: synthetic field
         private yf a;
         // $VF: synthetic field
         private zk b;

         {
            this.b = var1;
            this.a = var2x;
         }

         private int a(zm var1, zm var2x) {
            if (var1 == null) {
               return var2x == null ? 0 : 1;
            }

            if (var2x == null) {
               return -1;
            }

            double var3x = var1.b;
            double var5 = var2x.b;
            return this.a == yf.MINIMIZE ? Double.compare(var3x, var5) : Double.compare(var5, var3x);
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public final int compare(Object var1, Object var2) {
            zm var10001 = (zm)var1;
            zm var3 = var2;
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

               double var6 = var2.b;
               double var8 = var3.b;
               if (var10.a == yf.MINIMIZE) {
                  return Double.compare(var6, var8);
               }

               var10000 = Double.compare(var8, var6);
            }

            return var10000;
         }
      });
      if (this.f[0] == null) {
         throw var10;
      } else {
         return this.f[0];
      }
   }

   private void a(yf var1) {
      Arrays.sort(this.f, new Comparator<zm>(this, var1) {
         // $VF: synthetic field
         private yf a;
         // $VF: synthetic field
         private zk b;

         {
            this.b = var1;
            this.a = var2x;
         }

         private int a(zm var1, zm var2x) {
            if (var1 == null) {
               return var2x == null ? 0 : 1;
            }

            if (var2x == null) {
               return -1;
            }

            double var3x = var1.b;
            double var5 = var2x.b;
            return this.a == yf.MINIMIZE ? Double.compare(var3x, var5) : Double.compare(var5, var3x);
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public final int compare(Object var1, Object var2) {
            zm var10001 = (zm)var1;
            zm var3 = var2;
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

               double var6 = var2.b;
               double var8 = var3.b;
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
