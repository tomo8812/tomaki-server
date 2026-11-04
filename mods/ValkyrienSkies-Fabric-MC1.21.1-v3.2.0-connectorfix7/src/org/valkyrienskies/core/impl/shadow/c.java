package org.valkyrienskies.core.impl.shadow;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Optional;

public final class c {
   private static final k a = k.a();

   private c() {
   }

   public static OffsetDateTime a(String var0) {
      String var1 = var0;
      k var2 = a;
      return k.a(a.b(var1)).e();
   }

   private static a b(String var0) {
      return a.b(var0);
   }

   private static boolean c(String var0) {
      try {
         a(var0);
         return true;
      } catch (DateTimeException var1) {
         return false;
      }
   }

   private static String a(OffsetDateTime var0, int var1) {
      return a.a(var0, var1);
   }

   private static String a(OffsetDateTime var0, b var1) {
      b var2 = var1;
      OffsetDateTime var4 = var0;
      k var3 = a;
      return a.a(var4, ZoneOffset.UTC, var2, 0);
   }

   private static String a(OffsetDateTime var0) {
      return a.a(var0, var0.getOffset(), 0);
   }

   private static String b(OffsetDateTime var0, int var1) {
      return a.a(var0, var0.getOffset(), var1);
   }

   private static String b(OffsetDateTime var0) {
      OffsetDateTime var1 = var0;
      k var2 = a;
      return a.a(var1, 0);
   }

   private static String c(OffsetDateTime var0) {
      OffsetDateTime var1 = var0;
      k var2 = a;
      return a.a(var1, 3);
   }

   private static String d(OffsetDateTime var0) {
      OffsetDateTime var1 = var0;
      k var2 = a;
      return a.a(var1, 6);
   }

   private static String e(OffsetDateTime var0) {
      OffsetDateTime var1 = var0;
      k var2 = a;
      return a.a(var1, 9);
   }

   private static void a(String var0, e var1) {
      a var3;
      if ((var3 = a.b(var0)).a(b.MINUTE)) {
         if (Optional.ofNullable(var3.b).isPresent()) {
            var1.a(var3.e());
         } else {
            var1.a(var3.d());
         }
      } else if (var3.a(b.DAY)) {
         var1.a(var3.f());
      } else if (var3.a(b.MONTH)) {
         var1.a(var3.c());
      } else {
         var1.a(Year.of(var3.a));
      }
   }

   private static <T> T a(String var0, f<T> var1) {
      a var3;
      if ((var3 = a.b(var0)).a(b.MINUTE)) {
         return (T)(Optional.ofNullable(var3.b).isPresent() ? var1.a(var3.e()) : var1.a(var3.d()));
      } else if (var3.a(b.DAY)) {
         return (T)var1.a(var3.f());
      } else {
         return (T)(var3.a(b.MONTH) ? var1.a(var3.c()) : var1.a(Year.of(var3.a)));
      }
   }

   private static boolean a(String var0, final g... var1) {
      try {
         f var6 = new f<Boolean>() {
            private Boolean a() {
               return c.a(g.LOCAL_DATE, var1);
            }

            private Boolean b() {
               return c.a(g.OFFSET_DATE_TIME, var1);
            }

            private Boolean c() {
               return c.a(g.LOCAL_DATE_TIME, var1);
            }

            private Boolean d() {
               return c.a(g.YEAR_MONTH, var1);
            }

            private Boolean e() {
               return c.a(g.YEAR, var1);
            }

            // $VF: synthetic method
            @Override
            public final Object a(OffsetDateTime var1x) {
               var var2 = this;
               return c.a(g.OFFSET_DATE_TIME, var1);
            }

            // $VF: synthetic method
            @Override
            public final Object a(Year var1x) {
               var var2 = this;
               return c.a(g.YEAR, var1);
            }

            // $VF: synthetic method
            @Override
            public final Object a(YearMonth var1x) {
               var var2 = this;
               return c.a(g.YEAR_MONTH, var1);
            }

            // $VF: synthetic method
            @Override
            public final Object a(LocalDate var1x) {
               var var2 = this;
               return c.a(g.LOCAL_DATE, var1);
            }

            // $VF: synthetic method
            @Override
            public final Object a(LocalDateTime var1x) {
               var var2 = this;
               return c.a(g.LOCAL_DATE_TIME, var1);
            }
         };
         var0 = var0;
         a var5;
         return (Boolean)(
            (var5 = a.b(var0)).a(b.MINUTE)
               ? (Optional.ofNullable(var5.b).isPresent() ? var6.a(var5.e()) : var6.a(var5.d()))
               : (var5.a(b.DAY) ? var6.a(var5.f()) : (var5.a(b.MONTH) ? var6.a(var5.c()) : var6.a(Year.of(var5.a))))
         );
      } catch (DateTimeException var3) {
         return false;
      }
   }

   private static boolean b(g var0, g... var1) {
      g[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         g var4;
         if ((var4 = var5[var3]).equals(var0)) {
            return true;
         }
      }

      return false;
   }

   // $VF: synthetic method
   static boolean a(g var0, g[] var1) {
      var1 = var1;
      var0 = var0;
      g[] var7;
      int var2 = (var7 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         g var4;
         if ((var4 = var7[var3]).equals(var0)) {
            return true;
         }
      }

      return false;
   }
}
