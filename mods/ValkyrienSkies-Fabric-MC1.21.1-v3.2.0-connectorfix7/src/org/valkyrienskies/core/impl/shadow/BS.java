package org.valkyrienskies.core.impl.shadow;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public final class BS {
   private BS() {
   }

   public static NumberFormat a() {
      return a(Locale.getDefault());
   }

   public static NumberFormat a(Locale var0) {
      NumberFormat var1;
      (var1 = NumberFormat.getInstance(var0)).setMaximumFractionDigits(10);
      return var1;
   }

   public static void a(String var0, ParsePosition var1) {
      b(var0, var1);
      var1.setIndex(var1.getIndex() - 1);
   }

   public static char b(String var0, ParsePosition var1) {
      int var2 = var1.getIndex();
      int var3 = var0.length();
      char var4 = 0;
      if (var2 < var3) {
         char var5;
         while (Character.isWhitespace(var5 = var0.charAt(var2++)) && var2 < var3) {
         }

         var1.setIndex(var2);
         if (var2 < var3) {
            var4 = var5;
         }
      }

      return var4;
   }

   private static Number a(String var0, double var1, ParsePosition var3) {
      Double var4 = null;
      StringBuilder var5;
      (var5 = new StringBuilder()).append('(');
      var5.append(var1);
      var5.append(')');
      int var6 = var5.length();
      int var7;
      if ((var6 = (var7 = var3.getIndex()) + var6) < var0.length() && var0.substring(var7, var6).compareTo(var5.toString()) == 0) {
         var4 = var1;
         var3.setIndex(var6);
      }

      return var4;
   }

   public static Number a(String var0, NumberFormat var1, ParsePosition var2) {
      int var3 = var2.getIndex();
      Number var5 = var1.parse(var0, var2);
      int var4 = var2.getIndex();
      if (var3 == var4) {
         double[] var6 = new double[]{Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
         var4 = 0;

         while (var4 < var6.length && (var5 = a(var0, var6[var4], var2)) == null) {
            var4++;
         }
      }

      return var5;
   }

   public static boolean a(String var0, String var1, ParsePosition var2) {
      int var3;
      int var4 = (var3 = var2.getIndex()) + var1.length();
      if (var3 < var0.length() && var4 <= var0.length() && var0.substring(var3, var4).compareTo(var1) == 0) {
         var2.setIndex(var4);
         return true;
      } else {
         var2.setIndex(var3);
         var2.setErrorIndex(var3);
         return false;
      }
   }

   public static StringBuffer a(double var0, NumberFormat var2, StringBuffer var3, FieldPosition var4) {
      if (!Double.isNaN(var0) && !Double.isInfinite(var0)) {
         var2.format(var0, var3, var4);
      } else {
         var3.append('(');
         var3.append(var0);
         var3.append(')');
      }

      return var3;
   }
}
