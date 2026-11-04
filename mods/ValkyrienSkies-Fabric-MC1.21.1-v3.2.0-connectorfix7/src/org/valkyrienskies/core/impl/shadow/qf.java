package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class qf extends qc implements Serializable {
   private static final long a = -2932167925527338976L;

   public qf() {
   }

   private qf(NumberFormat var1) {
      super(var1);
   }

   public qf(NumberFormat var1, NumberFormat var2) {
      super(var1, var2);
   }

   private static Locale[] d() {
      return NumberFormat.getAvailableLocales();
   }

   private static String a(qd var0) {
      Locale var1 = Locale.getDefault();
      return new qf(a(var1)).format(var0);
   }

   private static qf e() {
      return b(Locale.getDefault());
   }

   private static qf b(Locale var0) {
      return new qf(a(var0));
   }

   private static qf f() {
      Locale var0 = Locale.getDefault();
      return new qk(a(var0));
   }

   private static qk c(Locale var0) {
      return new qk(a(var0));
   }

   public StringBuffer a(qd var1, StringBuffer var2, FieldPosition var3) {
      var3.setBeginIndex(0);
      var3.setEndIndex(0);
      this.c().format(var1.f, var2, var3);
      var2.append(" / ");
      this.b().format(var1.g, var2, var3);
      return var2;
   }

   @Override
   public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
      StringBuffer var4;
      if (obj instanceof qd) {
         var4 = this.a((qd)obj, toAppendTo, pos);
      } else if (obj instanceof BigInteger) {
         var4 = this.a(new qd((BigInteger)obj), toAppendTo, pos);
      } else {
         if (!(obj instanceof Number)) {
            throw new oV(px.CANNOT_FORMAT_OBJECT_TO_FRACTION);
         }

         var4 = this.a(new qd(((Number)obj).doubleValue()), toAppendTo, pos);
      }

      return var4;
   }

   private qd a(String var1) {
      ParsePosition var2 = new ParsePosition(0);
      qd var3 = this.c(var1, var2);
      if (var2.getIndex() == 0) {
         throw new oZ(var1, var2.getErrorIndex(), qd.class);
      } else {
         return var3;
      }
   }

   public qd c(String var1, ParsePosition var2) {
      int var3 = var2.getIndex();
      a(var1, var2);
      BigInteger var4;
      if ((var4 = d(var1, var2)) == null) {
         var2.setIndex(var3);
         return null;
      }

      int var5 = var2.getIndex();
      switch (b(var1, var2)) {
         case '\u0000':
            return new qd(var4);
         case '/':
            a(var1, var2);
            BigInteger var7;
            if ((var7 = d(var1, var2)) == null) {
               var2.setIndex(var3);
               return null;
            }

            return new qd(var4, var7);
         default:
            var2.setIndex(var3);
            var2.setErrorIndex(var5);
            return null;
      }
   }

   protected static BigInteger d(String var0, ParsePosition var1) {
      int var2 = var1.getIndex();
      int var3 = var0.charAt(var2) == '-' ? var2 + 1 : var2;

      while (var3 < var0.length() && Character.isDigit(var0.charAt(var3))) {
         var3++;
      }

      try {
         BigInteger var5 = new BigInteger(var0.substring(var2, var3));
         var1.setIndex(var3);
         return var5;
      } catch (NumberFormatException var4) {
         var1.setErrorIndex(var2);
         return null;
      }
   }

   // $VF: synthetic method
   @Override
   public Number parse(String var1) {
      String var2 = var1;
      qf var4 = this;
      ParsePosition var3 = new ParsePosition(0);
      qd var5 = var4.c(var2, var3);
      if (var3.getIndex() == 0) {
         throw new oZ(var2, var3.getErrorIndex(), qd.class);
      } else {
         return var5;
      }
   }

   // $VF: synthetic method
   @Override
   public Number parse(String var1, ParsePosition var2) {
      return this.c(var1, var2);
   }
}
