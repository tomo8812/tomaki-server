package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;
import java.util.Comparator;

public final class xN extends xR {
   private final xR a;
   private int b;
   private int c;
   private zF d;
   private xS[] e;
   private wO[] f;
   private int g = -1;
   private int h = -1;

   private xN(xR var1, int var2, zF var3) {
      super(var1.j());
      if (var2 <= 0) {
         throw new pk(var2);
      }

      this.a = var1;
      this.c = var2;
      this.d = var3;
   }

   @Override
   public final int a() {
      return this.b;
   }

   private xS[] g() {
      if (this.e == null) {
         throw new oX(px.NO_OPTIMUM_COMPUTED_YET);
      } else {
         return (xS[])this.e.clone();
      }
   }

   @Override
   public final xS c(wO... var1) {
      this.f = var1;
      return super.c(var1);
   }

   private xS m() {
      for (int var1 = 0; var1 < this.f.length; var1++) {
         if (this.f[var1] instanceof wM) {
            this.f[var1] = null;
            this.g = var1;
         } else if (this.f[var1] instanceof xO) {
            this.f[var1] = null;
            this.h = var1;
         }
      }

      if (this.g == -1) {
         throw new oX();
      }

      if (this.h == -1) {
         throw new oX();
      }

      RuntimeException var13 = null;
      this.e = new xS[this.c];
      this.b = 0;
      int var2 = this.h();
      double var3 = this.e();
      double var5 = this.f();
      double var7 = this.d();

      for (int var9 = 0; var9 < this.c; var9++) {
         try {
            this.f[this.g] = new wM(var2 - this.b);
            double var10 = var9 == 0 ? var7 : var3 + this.d.nextDouble() * (var5 - var3);
            this.f[this.h] = new xO(var3, var5, var10);
            this.e[var9] = this.a.c(this.f);
         } catch (RuntimeException var12) {
            var13 = var12;
            this.e[var9] = null;
         }

         this.b = this.b + this.a.a();
      }

      xi var15 = this.b();
      xN var14 = this;
      Arrays.sort(this.e, new Comparator<xS>(var14, var15) {
         // $VF: synthetic field
         private xi a;
         // $VF: synthetic field
         private xN b;

         {
            this.b = var1;
            this.a = var2x;
         }

         private int a(xS var1, xS var2x) {
            if (var1 == null) {
               return var2x == null ? 0 : 1;
            }

            if (var2x == null) {
               return -1;
            }

            double var3x = var1.b;
            double var5x = var2x.b;
            return this.a == xi.MINIMIZE ? Double.compare(var3x, var5x) : Double.compare(var5x, var3x);
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public final int compare(Object var1, Object var2) {
            xS var10001 = (xS)var1;
            xS var3 = var2;
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
               if (var10.a == xi.MINIMIZE) {
                  return Double.compare(var6, var8);
               }

               var10000 = Double.compare(var8, var6);
            }

            return var10000;
         }
      });
      if (this.e[0] == null) {
         throw var13;
      } else {
         return this.e[0];
      }
   }

   private void a(xi var1) {
      Arrays.sort(this.e, new Comparator<xS>(this, var1) {
         // $VF: synthetic field
         private xi a;
         // $VF: synthetic field
         private xN b;

         {
            this.b = var1;
            this.a = var2x;
         }

         private int a(xS var1, xS var2x) {
            if (var1 == null) {
               return var2x == null ? 0 : 1;
            }

            if (var2x == null) {
               return -1;
            }

            double var3x = var1.b;
            double var5x = var2x.b;
            return this.a == xi.MINIMIZE ? Double.compare(var3x, var5x) : Double.compare(var5x, var3x);
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public final int compare(Object var1, Object var2) {
            xS var10001 = (xS)var1;
            xS var3 = var2;
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
               if (var10.a == xi.MINIMIZE) {
                  return Double.compare(var6, var8);
               }

               var10000 = Double.compare(var8, var6);
            }

            return var10000;
         }
      });
   }

   // $VF: synthetic method
   @Override
   public final Object c() {
      return this.m();
   }

   // $VF: synthetic method
   @Override
   public final Object a(wO[] var1) {
      return this.c(var1);
   }
}
