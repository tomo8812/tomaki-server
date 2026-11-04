package org.valkyrienskies.core.impl.shadow;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ts {
   private static final String h = "{";
   private static final String i = "}";
   private static final String j = "{";
   private static final String k = "}";
   private static final String l = ",";
   private static final String m = ",";
   final String a;
   final String b;
   final String c;
   final String d;
   final String e;
   final String f;
   final NumberFormat g;

   public ts() {
      this("{", "}", "{", "}", ",", ",", BS.a(Locale.getDefault()));
   }

   private ts(NumberFormat var1) {
      this("{", "}", "{", "}", ",", ",", var1);
   }

   public ts(String var1, String var2, String var3, String var4, String var5, String var6) {
      this(var1, var2, var3, var4, var5, var6, BS.a(Locale.getDefault()));
   }

   private ts(String var1, String var2, String var3, String var4, String var5, String var6, NumberFormat var7) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.e = var5;
      this.f = var6;
      this.g = var7;
      this.g.setGroupingUsed(false);
   }

   private static Locale[] c() {
      return NumberFormat.getAvailableLocales();
   }

   private String d() {
      return this.a;
   }

   private String e() {
      return this.b;
   }

   private String f() {
      return this.c;
   }

   private String g() {
      return this.d;
   }

   private String h() {
      return this.e;
   }

   private String i() {
      return this.f;
   }

   public final NumberFormat a() {
      return this.g;
   }

   public static ts b() {
      return a(Locale.getDefault());
   }

   public static ts a(Locale var0) {
      return new ts(BS.a(var0));
   }

   public final String a(sv var1) {
      return this.a(var1, new StringBuffer(), new FieldPosition(0)).toString();
   }

   private StringBuffer a(tq var1, StringBuffer var2, FieldPosition var3) {
      var3.setBeginIndex(0);
      var3.setEndIndex(0);
      var2.append(this.a);
      int var4 = var1.f();

      for (int var5 = 0; var5 < var4; var5++) {
         var2.append(this.c);

         for (int var6 = 0; var6 < var1.g(); var6++) {
            if (var6 > 0) {
               var2.append(this.f);
            }

            BS.a(var1.b(var5, var6), this.g, var2, var3);
         }

         var2.append(this.d);
         if (var5 < var4 - 1) {
            var2.append(this.e);
         }
      }

      var2.append(this.b);
      return var2;
   }

   private tq a(String var1) {
      ParsePosition var2 = new ParsePosition(0);
      ParsePosition var5 = var2;
      String var4 = var1;
      ts var3 = this;
      int var6 = var5.getIndex();
      String var7 = var3.a.trim();
      String var8 = var3.b.trim();
      String var9 = var3.c.trim();
      String var10 = var3.d.trim();
      String var11 = var3.f.trim();
      String var12 = var3.e.trim();
      BS.a(var4, var5);
      sv var10000;
      if (!BS.a(var4, var7, var5)) {
         var10000 = null;
      } else {
         ArrayList var20 = new ArrayList();
         ArrayList var13 = new ArrayList();
         boolean var14 = true;

         while (true) {
            if (!var14) {
               if (!var13.isEmpty()) {
                  var20.add(var13);
               }

               BS.a(var4, var5);
               if (!BS.a(var4, var8, var5)) {
                  var10000 = null;
               } else if (var20.isEmpty()) {
                  var5.setIndex(var6);
                  var10000 = null;
               } else {
                  double[][] var21 = new double[var20.size()][];
                  int var22 = 0;

                  for (List var18 : var20) {
                     var21[var22] = new double[var18.size()];

                     for (int var19 = 0; var19 < var18.size(); var19++) {
                        var21[var22][var19] = ((Number)var18.get(var19)).doubleValue();
                     }

                     var22++;
                  }

                  var10000 = td.a(var21);
               }
               break;
            }

            if (!var13.isEmpty()) {
               BS.a(var4, var5);
               if (!BS.a(var4, var11, var5)) {
                  if (var10.length() != 0 && !BS.a(var4, var10, var5)) {
                     var10000 = null;
                     break;
                  }

                  BS.a(var4, var5);
                  if (BS.a(var4, var12, var5)) {
                     var20.add(var13);
                     var13 = new ArrayList();
                     continue;
                  }

                  var14 = false;
               }
            } else {
               BS.a(var4, var5);
               if (var9.length() != 0 && !BS.a(var4, var9, var5)) {
                  var10000 = null;
                  break;
               }
            }

            if (var14) {
               BS.a(var4, var5);
               Number var15;
               if ((var15 = BS.a(var4, var3.g, var5)) != null) {
                  var13.add(var15);
               } else {
                  if (!var13.isEmpty()) {
                     var5.setIndex(var6);
                     var10000 = null;
                     break;
                  }

                  var14 = false;
               }
            }
         }
      }

      sv var17 = var10000;
      if (var2.getIndex() == 0) {
         throw new oZ(var1, var2.getErrorIndex(), sy.class);
      } else {
         return var17;
      }
   }

   private tq a(String var1, ParsePosition var2) {
      int var3 = var2.getIndex();
      String var4 = this.a.trim();
      String var5 = this.b.trim();
      String var6 = this.c.trim();
      String var7 = this.d.trim();
      String var8 = this.f.trim();
      String var9 = this.e.trim();
      BS.a(var1, var2);
      if (!BS.a(var1, var4, var2)) {
         return null;
      }

      ArrayList var16 = new ArrayList();
      ArrayList var10 = new ArrayList();
      boolean var11 = true;

      while (var11) {
         if (!var10.isEmpty()) {
            BS.a(var1, var2);
            if (!BS.a(var1, var8, var2)) {
               if (var7.length() != 0 && !BS.a(var1, var7, var2)) {
                  return null;
               }

               BS.a(var1, var2);
               if (BS.a(var1, var9, var2)) {
                  var16.add(var10);
                  var10 = new ArrayList();
                  continue;
               }

               var11 = false;
            }
         } else {
            BS.a(var1, var2);
            if (var6.length() != 0 && !BS.a(var1, var6, var2)) {
               return null;
            }
         }

         if (var11) {
            BS.a(var1, var2);
            Number var12;
            if ((var12 = BS.a(var1, this.g, var2)) != null) {
               var10.add(var12);
            } else {
               if (!var10.isEmpty()) {
                  var2.setIndex(var3);
                  return null;
               }

               var11 = false;
            }
         }
      }

      if (!var10.isEmpty()) {
         var16.add(var10);
      }

      BS.a(var1, var2);
      if (!BS.a(var1, var5, var2)) {
         return null;
      }

      if (var16.isEmpty()) {
         var2.setIndex(var3);
         return null;
      }

      double[][] var17 = new double[var16.size()][];
      int var18 = 0;

      for (List var14 : var16) {
         var17[var18] = new double[var14.size()];

         for (int var15 = 0; var15 < var14.size(); var15++) {
            var17[var18][var15] = ((Number)var14.get(var15)).doubleValue();
         }

         var18++;
      }

      return td.a(var17);
   }
}
