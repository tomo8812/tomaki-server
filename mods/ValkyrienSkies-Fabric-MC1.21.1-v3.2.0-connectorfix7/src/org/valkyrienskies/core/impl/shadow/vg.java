package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public final class vg implements vh {
   private static final int a = 100;
   private final vh b;
   private final vl c;
   private final vm[] d;
   private final double[] e;
   private boolean f;
   private double g;

   private vg(vh var1, vl var2) {
      this.b = var1;
      this.c = var2;
      this.d = new vm[100];
      this.e = new double[100];
   }

   @Override
   public final void a(double var1, double[] var3, double var4) {
      this.b.a(var1, var3, var4);
      this.f = var4 >= var1;
      this.g = this.f ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
      Arrays.fill(this.d, vm.UNINITIALIZED);
      Arrays.fill(this.e, this.g);
   }

   @Override
   public final double a(double var1, double[] var3) {
      double var4 = this.b.a(var1, var3);
      if (this.f) {
         int var10 = this.d.length - 1;
         if (this.g < var1) {
            vm var12 = this.d[var10];
            vm var7;
            if ((var7 = this.c.a(var12, var4, this.f)) != var12) {
               System.arraycopy(this.e, 1, this.e, 0, var10);
               System.arraycopy(this.d, 1, this.d, 0, var10);
               this.e[var10] = this.g;
               this.d[var10] = var7;
            }

            this.g = var1;
            return var7.a(var4);
         } else {
            for (int var11 = var10; var11 > 0; var11--) {
               if (this.e[var11] <= var1) {
                  return this.d[var11].a(var4);
               }
            }

            return this.d[0].a(var4);
         }
      } else if (var1 < this.g) {
         vm var9 = this.d[0];
         vm var6;
         if ((var6 = this.c.a(var9, var4, this.f)) != var9) {
            System.arraycopy(this.e, 0, this.e, 1, this.e.length - 1);
            System.arraycopy(this.d, 0, this.d, 1, this.d.length - 1);
            this.e[0] = this.g;
            this.d[0] = var6;
         }

         this.g = var1;
         return var6.a(var4);
      } else {
         for (int var8 = 0; var8 < this.e.length - 1; var8++) {
            if (var1 <= this.e[var8]) {
               return this.d[var8].a(var4);
            }
         }

         return this.d[this.e.length - 1].a(var4);
      }
   }

   @Override
   public final int b(double var1, double[] var3) {
      return this.b.b(var1, var3);
   }

   @Override
   public final void c(double var1, double[] var3) {
   }
}
