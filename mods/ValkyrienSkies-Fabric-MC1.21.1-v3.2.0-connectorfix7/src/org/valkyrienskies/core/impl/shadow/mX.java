package org.valkyrienskies.core.impl.shadow;

@Deprecated
public final class mX extends mH {
   private final pK a;
   private final int b;
   private final pK c;
   private final int d;

   public mX() {
      this(3);
   }

   private mX(int var1) {
      this(3, 3);
   }

   private mX(int var1, int var2) {
      if (var1 < 0) {
         throw new pj(var1);
      }

      if (var2 < 0) {
         throw new pj(var2);
      }

      this.b = var1;
      this.d = var2;
      wV var3 = new wV(100.0 * Cq.a, 100.0 * Cq.b);
      this.a = new pK(new xJ(false, var3));
      this.c = new pK(new xJ(false, var3));
   }

   @Override
   public final mG b(double[] var1, double[] var2, double[][] var3) {
      if (var1.length != 0 && var2.length != 0 && var3.length != 0) {
         if (var1.length != var3.length) {
            throw new oS(var1.length, var3.length);
         }

         int var4 = var1.length;
         int var5 = var2.length;

         for (int var6 = 0; var6 < var4; var6++) {
            if (var3[var6].length != var5) {
               throw new oS(var3[var6].length, var5);
            }
         }

         Ch.a(var1);
         Ch.a(var2);
         ni[] var13 = new ni[var5];

         for (int var7 = 0; var7 < var5; var7++) {
            this.a.b();

            for (int var8 = 0; var8 < var4; var8++) {
               this.a.a(1.0, var1[var8], var3[var8][var7]);
            }

            var13[var7] = new ni(this.a.a(new double[this.b + 1]));
         }

         double[][] var15 = new double[var4][var5];

         for (int var17 = 0; var17 < var5; var17++) {
            ni var10 = var13[var17];

            for (int var9 = 0; var9 < var4; var9++) {
               var15[var9][var17] = var10.a(var1[var9]);
            }
         }

         ni[] var18 = new ni[var4];

         for (int var11 = 0; var11 < var4; var11++) {
            this.c.b();

            for (int var19 = 0; var19 < var5; var19++) {
               this.c.a(1.0, var2[var19], var15[var11][var19]);
            }

            var18[var11] = new ni(this.c.a(new double[this.d + 1]));
         }

         var3 = new double[var4][var5];

         for (int var20 = 0; var20 < var4; var20++) {
            ni var14 = var18[var20];

            for (int var16 = 0; var16 < var5; var16++) {
               var3[var20][var16] = var14.a(var2[var16]);
            }
         }

         return super.b(var1, var2, var3);
      } else {
         throw new pf();
      }
   }

   // $VF: synthetic method
   @Override
   public final kP a(double[] var1, double[] var2, double[][] var3) {
      return this.b(var1, var2, var3);
   }
}
