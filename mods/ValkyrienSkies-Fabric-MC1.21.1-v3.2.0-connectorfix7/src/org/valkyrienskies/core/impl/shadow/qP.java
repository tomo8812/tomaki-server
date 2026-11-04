package org.valkyrienskies.core.impl.shadow;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public abstract class qP<S extends qN> {
   private static String a;
   private static String b;
   private static String c;
   private final String d;
   private final String e;
   private final String f;
   private final String g;
   private final String h;
   private final String i;
   private final NumberFormat j;

   protected qP() {
      this("{", "}", "; ", BS.a(Locale.getDefault()));
   }

   private qP(NumberFormat var1) {
      this("{", "}", "; ", var1);
   }

   private qP(String var1, String var2, String var3) {
      this(var1, var2, var3, BS.a(Locale.getDefault()));
   }

   public qP(String var1, String var2, String var3, NumberFormat var4) {
      this.d = var1;
      this.e = var2;
      this.f = var3;
      this.g = var1.trim();
      this.h = var2.trim();
      this.i = var3.trim();
      this.j = var4;
   }

   private static Locale[] a() {
      return NumberFormat.getAvailableLocales();
   }

   private String b() {
      return this.d;
   }

   private String c() {
      return this.e;
   }

   private String d() {
      return this.f;
   }

   private NumberFormat e() {
      return this.j;
   }

   public final String a(qO<S> var1) {
      return this.a(var1, new StringBuffer(), new FieldPosition(0)).toString();
   }

   public abstract StringBuffer a(qO<S> var1, StringBuffer var2, FieldPosition var3);

   protected final StringBuffer a(StringBuffer var1, FieldPosition var2, double... var3) {
      var2.setBeginIndex(0);
      var2.setEndIndex(0);
      var1.append(this.d);

      for (int var4 = 0; var4 < var3.length; var4++) {
         if (var4 > 0) {
            var1.append(this.f);
         }

         BS.a(var3[var4], this.j, var1, var2);
      }

      var1.append(this.e);
      return var1;
   }

   public abstract qO<S> a(String var1);

   public abstract qO<S> a(String var1, ParsePosition var2);

   protected final double[] a(int var1, String var2, ParsePosition var3) {
      int var4 = var3.getIndex();
      double[] var5 = new double[var1];
      BS.a(var2, var3);
      if (!BS.a(var2, this.g, var3)) {
         return null;
      }

      for (int var6 = 0; var6 < var1; var6++) {
         BS.a(var2, var3);
         if (var6 > 0 && !BS.a(var2, this.i, var3)) {
            return null;
         }

         BS.a(var2, var3);
         Number var7;
         if ((var7 = BS.a(var2, this.j, var3)) == null) {
            var3.setIndex(var4);
            return null;
         }

         var5[var6] = var7.doubleValue();
      }

      BS.a(var2, var3);
      return !BS.a(var2, this.h, var3) ? null : var5;
   }
}
