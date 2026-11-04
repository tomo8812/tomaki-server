package org.valkyrienskies.core.impl.shadow;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Locale;

public final class tw {
   private static final String e = "{";
   private static final String f = "}";
   private static final String g = "; ";
   final String a;
   final String b;
   final String c;
   private final String h;
   private final String i;
   private final String j;
   final NumberFormat d;

   public tw() {
      this("{", "}", "; ", BS.a(Locale.getDefault()));
   }

   public tw(NumberFormat var1) {
      this("{", "}", "; ", var1);
   }

   private tw(String var1, String var2, String var3) {
      this(var1, var2, var3, BS.a(Locale.getDefault()));
   }

   private tw(String var1, String var2, String var3, NumberFormat var4) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.h = var1.trim();
      this.i = var2.trim();
      this.j = var3.trim();
      this.d = var4;
   }

   private static Locale[] b() {
      return NumberFormat.getAvailableLocales();
   }

   private String c() {
      return this.a;
   }

   private String d() {
      return this.b;
   }

   private String e() {
      return this.c;
   }

   private NumberFormat f() {
      return this.d;
   }

   public static tw a() {
      return a(Locale.getDefault());
   }

   private static tw a(Locale var0) {
      return new tw(BS.a(var0));
   }

   public final String a(sA var1) {
      return this.a(var1, new StringBuffer(), new FieldPosition(0)).toString();
   }

   private StringBuffer a(tu var1, StringBuffer var2, FieldPosition var3) {
      var3.setBeginIndex(0);
      var3.setEndIndex(0);
      var2.append(this.a);

      for (int var4 = 0; var4 < var1.f(); var4++) {
         if (var4 > 0) {
            var2.append(this.c);
         }

         BS.a(var1.a(var4), this.d, var2, var3);
      }

      var2.append(this.b);
      return var2;
   }

   private sA a(String var1) {
      ParsePosition var2 = new ParsePosition(0);
      ParsePosition var5 = var2;
      String var4 = var1;
      tw var3 = this;
      int var6 = var5.getIndex();
      BS.a(var4, var5);
      sA var10000;
      if (!BS.a(var4, var3.h, var5)) {
         var10000 = null;
      } else {
         label49: {
            ArrayList var7 = new ArrayList();
            boolean var8 = true;

            while (var8) {
               if (!var7.isEmpty()) {
                  BS.a(var4, var5);
                  if (!BS.a(var4, var3.j, var5)) {
                     var8 = false;
                  }
               }

               if (var8) {
                  BS.a(var4, var5);
                  Number var9;
                  if ((var9 = BS.a(var4, var3.d, var5)) == null) {
                     var5.setIndex(var6);
                     var10000 = null;
                     break label49;
                  }

                  var7.add(var9);
               }
            }

            BS.a(var4, var5);
            if (!BS.a(var4, var3.i, var5)) {
               var10000 = null;
            } else {
               double[] var11 = new double[var7.size()];

               for (int var12 = 0; var12 < var11.length; var12++) {
                  var11[var12] = ((Number)var7.get(var12)).doubleValue();
               }

               var10000 = new sA(var11, false);
            }
         }
      }

      sA var10 = var10000;
      if (var2.getIndex() == 0) {
         throw new oZ(var1, var2.getErrorIndex(), sA.class);
      } else {
         return var10;
      }
   }

   private sA a(String var1, ParsePosition var2) {
      int var3 = var2.getIndex();
      BS.a(var1, var2);
      if (!BS.a(var1, this.h, var2)) {
         return null;
      }

      ArrayList var4 = new ArrayList();
      boolean var5 = true;

      while (var5) {
         if (!var4.isEmpty()) {
            BS.a(var1, var2);
            if (!BS.a(var1, this.j, var2)) {
               var5 = false;
            }
         }

         if (var5) {
            BS.a(var1, var2);
            Number var6;
            if ((var6 = BS.a(var1, this.d, var2)) == null) {
               var2.setIndex(var3);
               return null;
            }

            var4.add(var6);
         }
      }

      BS.a(var1, var2);
      if (!BS.a(var1, this.i, var2)) {
         return null;
      }

      double[] var7 = new double[var4.size()];

      for (int var8 = 0; var8 < var7.length; var8++) {
         var7[var8] = ((Number)var4.get(var8)).doubleValue();
      }

      return new sA(var7, false);
   }
}
