package org.valkyrienskies.core.impl.shadow;

import java.math.BigInteger;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public final class qk extends qf {
   private static final long a = -6337346779577272307L;
   private NumberFormat b;

   public qk() {
      this(a());
   }

   public qk(NumberFormat var1) {
      this(var1, (NumberFormat)var1.clone(), (NumberFormat)var1.clone());
   }

   private qk(NumberFormat var1, NumberFormat var2, NumberFormat var3) {
      super(var2, var3);
      var2 = var1;
      var1 = this;
      if (var2 == null) {
         throw new pl(px.WHOLE_FORMAT);
      }

      var1.b = var2;
   }

   @Override
   public final StringBuffer a(qd var1, StringBuffer var2, FieldPosition var3) {
      var3.setBeginIndex(0);
      var3.setEndIndex(0);
      BigInteger var4 = var1.f;
      BigInteger var7 = var1.g;
      BigInteger var5 = var4.divide(var7);
      var4 = var4.remainder(var7);
      if (!BigInteger.ZERO.equals(var5)) {
         this.b.format(var5, var2, var3);
         var2.append(' ');
         if (var4.compareTo(BigInteger.ZERO) < 0) {
            var4 = var4.negate();
         }
      }

      this.c().format(var4, var2, var3);
      var2.append(" / ");
      this.b().format(var7, var2, var3);
      return var2;
   }

   private NumberFormat d() {
      return this.b;
   }

   @Override
   public final qd c(String var1, ParsePosition var2) {
      qd var3;
      if ((var3 = super.c(var1, var2)) != null) {
         return var3;
      }

      int var10 = var2.getIndex();
      a(var1, var2);
      BigInteger var4;
      if ((var4 = d(var1, var2)) == null) {
         var2.setIndex(var10);
         return null;
      }

      a(var1, var2);
      BigInteger var5;
      if ((var5 = d(var1, var2)) == null) {
         var2.setIndex(var10);
         return null;
      }

      if (var5.compareTo(BigInteger.ZERO) < 0) {
         var2.setIndex(var10);
         return null;
      }

      int var6 = var2.getIndex();
      switch (b(var1, var2)) {
         case '\u0000':
            return new qd(var5);
         case '/':
            a(var1, var2);
            BigInteger var8;
            if ((var8 = d(var1, var2)) == null) {
               var2.setIndex(var10);
               return null;
            } else {
               if (var8.compareTo(BigInteger.ZERO) < 0) {
                  var2.setIndex(var10);
                  return null;
               }

               boolean var9;
               if (var9 = var4.compareTo(BigInteger.ZERO) < 0) {
                  var4 = var4.negate();
               }

               var5 = var4.multiply(var8).add(var5);
               if (var9) {
                  var5 = var5.negate();
               }

               return new qd(var5, var8);
            }
         default:
            var2.setIndex(var10);
            var2.setErrorIndex(var6);
            return null;
      }
   }

   private void a(NumberFormat var1) {
      if (var1 == null) {
         throw new pl(px.WHOLE_FORMAT);
      }

      this.b = var1;
   }

   // $VF: synthetic method
   @Override
   public final Number parse(String var1, ParsePosition var2) {
      return this.c(var1, var2);
   }
}
