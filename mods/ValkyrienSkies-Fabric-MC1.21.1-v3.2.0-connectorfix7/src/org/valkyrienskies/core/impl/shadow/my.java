package org.valkyrienskies.core.impl.shadow;

import java.math.BigDecimal;
import java.math.MathContext;

public final class my extends mu<BigDecimal> {
   private final MathContext a;
   private final BigDecimal b;
   private final BigDecimal c;
   private final BigDecimal d;

   public my() {
      this(MathContext.DECIMAL128);
   }

   private my(MathContext var1) {
      this.a = var1;
      this.b = new BigDecimal("2", var1);
      this.c = new BigDecimal("-1", var1);
      this.d = new BigDecimal("0.5", var1);
   }

   @Override
   protected final Co<BigDecimal[], BigDecimal[]> c(int var1) {
      if (var1 == 1) {
         return new Co<>(new BigDecimal[]{BigDecimal.ZERO}, new BigDecimal[]{this.b});
      }

      BigDecimal[] var2 = this.b(var1 - 1).a;
      BigDecimal[] var3 = new BigDecimal[var1];
      BigDecimal[] var4 = new BigDecimal[var1];
      int var5 = var1 / 2;

      for (int var6 = 0; var6 < var5; var6++) {
         BigDecimal var7 = var6 == 0 ? this.c : var2[var6 - 1];
         BigDecimal var8 = var5 == 1 ? BigDecimal.ONE : var2[var6];
         BigDecimal var9 = BigDecimal.ONE;
         BigDecimal var10 = var7;
         BigDecimal var11 = BigDecimal.ONE;
         BigDecimal var12 = var8;

         for (int var13 = 1; var13 < var1; var13++) {
            BigDecimal var14 = new BigDecimal(2 * var13 + 1, this.a);
            BigDecimal var15 = new BigDecimal(var13, this.a);
            BigDecimal var16 = new BigDecimal(var13 + 1, this.a);
            BigDecimal var17 = var7.multiply(var14, this.a);
            var17 = var10.multiply(var17, this.a);
            var9 = var9.multiply(var15, this.a);
            BigDecimal var18 = var17.subtract(var9, this.a).divide(var16, this.a);
            var17 = var8.multiply(var14, this.a);
            var17 = var12.multiply(var17, this.a);
            var9 = var11.multiply(var15, this.a);
            var17 = var17.subtract(var9, this.a).divide(var16, this.a);
            var9 = var10;
            var10 = var18;
            var11 = var12;
            var12 = var17;
         }

         BigDecimal var34 = var7.add(var8, this.a).multiply(this.d, this.a);
         BigDecimal var35 = BigDecimal.ONE;
         BigDecimal var36 = var34;
         boolean var37 = false;

         while (!var37) {
            BigDecimal var42 = var8.subtract(var7, this.a);
            var9 = var34.ulp().multiply(BigDecimal.TEN, this.a);
            var37 = var42.compareTo(var9) <= 0;
            var35 = BigDecimal.ONE;
            var36 = var34;

            for (int var48 = 1; var48 < var1; var48++) {
               var42 = new BigDecimal(2 * var48 + 1, this.a);
               var9 = new BigDecimal(var48, this.a);
               var11 = new BigDecimal(var48 + 1, this.a);
               var42 = var34.multiply(var42, this.a);
               var42 = var36.multiply(var42, this.a);
               var9 = var35.multiply(var9, this.a);
               var9 = var42.subtract(var9, this.a).divide(var11, this.a);
               var35 = var36;
               var36 = var9;
            }

            if (!var37) {
               if (var10.signum() * var36.signum() <= 0) {
                  var8 = var34;
               } else {
                  var7 = var34;
                  var10 = var36;
               }

               var34 = var7.add(var8, this.a).multiply(this.d, this.a);
            }
         }

         BigDecimal var46 = new BigDecimal(var1, this.a);
         var9 = var35.subtract(var34.multiply(var36, this.a), this.a).multiply(var46).pow(2, this.a);
         BigDecimal var49 = var34.pow(2, this.a);
         var49 = BigDecimal.ONE.subtract(var49, this.a).multiply(this.b, this.a).divide(var9, this.a);
         var3[var6] = var34;
         var4[var6] = var49;
         int var47 = var1 - var6 - 1;
         var3[var47] = var34.negate(this.a);
         var4[var47] = var49;
      }

      if (var1 % 2 != 0) {
         BigDecimal var19 = BigDecimal.ONE;

         for (byte var20 = 1; var20 < var1; var20 += 2) {
            BigDecimal var22 = new BigDecimal(var20, this.a);
            BigDecimal var31 = new BigDecimal(var20 + 1, this.a);
            var19 = var19.multiply(var22, this.a).divide(var31, this.a).negate(this.a);
         }

         BigDecimal var21 = new BigDecimal(var1, this.a);
         BigDecimal var23 = var19.multiply(var21, this.a).pow(2, this.a);
         BigDecimal var32 = this.b.divide(var23, this.a);
         var3[var5] = BigDecimal.ZERO;
         var4[var5] = var32;
      }

      return new Co<>(var3, var4);
   }
}
