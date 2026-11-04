package org.valkyrienskies.core.impl.shadow;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public final class ql extends qj {
   private static final long a = 760934726031766749L;
   private NumberFormat b;

   public ql() {
      this(d());
   }

   public ql(NumberFormat var1) {
      this(var1, (NumberFormat)var1.clone(), (NumberFormat)var1.clone());
   }

   private ql(NumberFormat var1, NumberFormat var2, NumberFormat var3) {
      super(var2, var3);
      var2 = var1;
      var1 = this;
      if (var2 == null) {
         throw new pl(px.WHOLE_FORMAT);
      }

      var1.b = var2;
   }

   @Override
   public final StringBuffer a(qg var1, StringBuffer var2, FieldPosition var3) {
      var3.setBeginIndex(0);
      var3.setEndIndex(0);
      int var4 = var1.d;
      int var7 = var1.c;
      int var5 = var4 / var7;
      var4 %= var7;
      if (var5 != 0) {
         this.b.format(var5, var2, var3);
         var2.append(' ');
         var4 = BY.a(var4);
      }

      this.c().format(var4, var2, var3);
      var2.append(" / ");
      this.b().format(var7, var2, var3);
      return var2;
   }

   private NumberFormat e() {
      return this.b;
   }

   @Override
   public final qg c(String var1, ParsePosition var2) {
      qg var3;
      if ((var3 = super.c(var1, var2)) != null) {
         return var3;
      }

      int var12 = var2.getIndex();
      a(var1, var2);
      Number var4;
      if ((var4 = this.b.parse(var1, var2)) == null) {
         var2.setIndex(var12);
         return null;
      }

      a(var1, var2);
      Number var5;
      if ((var5 = this.c().parse(var1, var2)) == null) {
         var2.setIndex(var12);
         return null;
      }

      if (var5.intValue() < 0) {
         var2.setIndex(var12);
         return null;
      }

      int var6 = var2.getIndex();
      switch (b(var1, var2)) {
         case '\u0000':
            return new qg(var5.intValue(), 1);
         case '/':
            a(var1, var2);
            Number var8;
            if ((var8 = this.b().parse(var1, var2)) == null) {
               var2.setIndex(var12);
               return null;
            } else {
               if (var8.intValue() < 0) {
                  var2.setIndex(var12);
                  return null;
               }

               int var10 = var4.intValue();
               int var13 = var5.intValue();
               int var9 = var8.intValue();
               int var10002 = BY.a(var10) * var9 + var13;
               int var14 = var10;
               boolean var11 = true;
               return new qg(var10002 * (var14 >= 0 ? 1 : -1), var9);
            }
         default:
            var2.setIndex(var12);
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
