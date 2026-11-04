package org.valkyrienskies.core.impl.shadow;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public final class nS {
   private static final String a = "i";
   private final String b;
   private final NumberFormat c;
   private final NumberFormat d;

   public nS() {
      this.b = "i";
      this.c = BS.a(Locale.getDefault());
      this.d = this.c;
   }

   private nS(NumberFormat var1) {
      if (var1 == null) {
         throw new pl(px.IMAGINARY_FORMAT);
      }

      this.b = "i";
      this.c = var1;
      this.d = var1;
   }

   private nS(NumberFormat var1, NumberFormat var2) {
      if (var2 == null) {
         throw new pl(px.IMAGINARY_FORMAT);
      }

      if (var1 == null) {
         throw new pl(px.REAL_FORMAT);
      }

      this.b = "i";
      this.c = var2;
      this.d = var1;
   }

   private nS(String var1) {
      this(var1, BS.a(Locale.getDefault()));
   }

   private nS(String var1, NumberFormat var2) {
      this(var1, var2, var2);
   }

   private nS(String var1, NumberFormat var2, NumberFormat var3) {
      if (var1 == null) {
         throw new pl();
      }

      if (var1.length() == 0) {
         throw new pf();
      }

      if (var3 == null) {
         throw new pl(px.IMAGINARY_FORMAT);
      }

      if (var2 == null) {
         throw new pl(px.REAL_FORMAT);
      }

      this.b = var1;
      this.c = var3;
      this.d = var2;
   }

   private static Locale[] a() {
      return NumberFormat.getAvailableLocales();
   }

   private String a(nQ var1) {
      return this.a(var1, new StringBuffer(), new FieldPosition(0)).toString();
   }

   private String a(Double var1) {
      return this.a(new nQ(var1, 0.0), new StringBuffer(), new FieldPosition(0)).toString();
   }

   private StringBuffer a(nQ var1, StringBuffer var2, FieldPosition var3) {
      var3.setBeginIndex(0);
      var3.setEndIndex(0);
      double var4 = var1.d;
      BS.a(var1.d, this.d, var2, var3);
      double var6 = var1.c;
      if (var1.c < 0.0) {
         var2.append(" - ");
         StringBuffer var8 = this.a(-var6, new StringBuffer(), var3);
         var2.append(var8);
         var2.append(this.b);
      } else if (var6 > 0.0 || Double.isNaN(var6)) {
         var2.append(" + ");
         StringBuffer var9 = this.a(var6, new StringBuffer(), var3);
         var2.append(var9);
         var2.append(this.b);
      }

      return var2;
   }

   private StringBuffer a(double var1, StringBuffer var3, FieldPosition var4) {
      var4.setBeginIndex(0);
      var4.setEndIndex(0);
      BS.a(var1, this.c, var3, var4);
      if (var3.toString().equals("1")) {
         var3.setLength(0);
      }

      return var3;
   }

   private StringBuffer a(Object var1, StringBuffer var2, FieldPosition var3) {
      StringBuffer var4;
      if (var1 instanceof nQ) {
         var4 = this.a((nQ)var1, var2, var3);
      } else {
         if (!(var1 instanceof Number)) {
            throw new oV(px.CANNOT_FORMAT_INSTANCE_AS_COMPLEX, var1.getClass().getName());
         }

         var4 = this.a(new nQ(((Number)var1).doubleValue(), 0.0), var2, var3);
      }

      return var4;
   }

   private String b() {
      return this.b;
   }

   private NumberFormat c() {
      return this.c;
   }

   private static nS d() {
      NumberFormat var0 = BS.a(Locale.getDefault());
      return new nS(var0);
   }

   private static nS a(Locale var0) {
      NumberFormat var1 = BS.a(var0);
      return new nS(var1);
   }

   private static nS a(String var0, Locale var1) {
      NumberFormat var2 = BS.a(var1);
      return new nS(var0, var2);
   }

   private NumberFormat e() {
      return this.d;
   }

   private nQ a(String var1) {
      ParsePosition var2 = new ParsePosition(0);
      ParsePosition var5 = var2;
      String var4 = var1;
      nS var3 = this;
      int var6 = var5.getIndex();
      BS.a(var4, var5);
      Number var7;
      nQ var10000;
      if ((var7 = BS.a(var4, var3.d, var5)) == null) {
         var5.setIndex(var6);
         var10000 = null;
      } else {
         label25: {
            int var8 = var5.getIndex();
            byte var12;
            switch (BS.b(var4, var5)) {
               case '\u0000':
                  var10000 = new nQ(var7.doubleValue(), 0.0);
                  break label25;
               case '+':
                  var12 = 1;
                  break;
               case '-':
                  var12 = -1;
                  break;
               default:
                  var5.setIndex(var6);
                  var5.setErrorIndex(var8);
                  var10000 = null;
                  break label25;
            }

            BS.a(var4, var5);
            Number var9;
            if ((var9 = BS.a(var4, var3.d, var5)) == null) {
               var5.setIndex(var6);
               var10000 = null;
            } else {
               var10000 = !BS.a(var4, var3.b, var5) ? null : new nQ(var7.doubleValue(), var9.doubleValue() * var12);
            }
         }
      }

      nQ var11 = var10000;
      if (var2.getIndex() == 0) {
         throw new oZ(var1, var2.getErrorIndex(), nQ.class);
      } else {
         return var11;
      }
   }

   private nQ a(String var1, ParsePosition var2) {
      int var3 = var2.getIndex();
      BS.a(var1, var2);
      Number var4;
      if ((var4 = BS.a(var1, this.d, var2)) == null) {
         var2.setIndex(var3);
         return null;
      }

      int var5 = var2.getIndex();
      byte var8;
      switch (BS.b(var1, var2)) {
         case '\u0000':
            return new nQ(var4.doubleValue(), 0.0);
         case '+':
            var8 = 1;
            break;
         case '-':
            var8 = -1;
            break;
         default:
            var2.setIndex(var3);
            var2.setErrorIndex(var5);
            return null;
      }

      BS.a(var1, var2);
      Number var6;
      if ((var6 = BS.a(var1, this.d, var2)) == null) {
         var2.setIndex(var3);
         return null;
      } else {
         return !BS.a(var1, this.b, var2) ? null : new nQ(var4.doubleValue(), var6.doubleValue() * var8);
      }
   }
}
