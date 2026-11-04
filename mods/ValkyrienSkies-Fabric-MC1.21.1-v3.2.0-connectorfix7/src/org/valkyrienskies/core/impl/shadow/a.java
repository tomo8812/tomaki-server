package org.valkyrienskies.core.impl.shadow;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.Objects;
import java.util.Optional;

public final class a {
   private final b c;
   final int a;
   private final int d;
   private final int e;
   private final int f;
   private final int g;
   private final int h;
   private final int i;
   final h b;
   private final int j;

   private a(b var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, h var9, int var10) {
      this.c = var1;
      this.a = var2;
      this.d = a(var3, 1, 12, org.valkyrienskies.core.impl.shadow.b.MONTH);
      this.e = a(var4, 1, 31, org.valkyrienskies.core.impl.shadow.b.DAY);
      this.f = a(var5, 0, 23, org.valkyrienskies.core.impl.shadow.b.HOUR);
      this.g = a(var6, 0, 59, org.valkyrienskies.core.impl.shadow.b.MINUTE);
      this.h = a(var7, 0, 60, org.valkyrienskies.core.impl.shadow.b.SECOND);
      this.i = a(var8, 0, 999999999, org.valkyrienskies.core.impl.shadow.b.NANO);
      this.b = var9;
      this.j = var10;
   }

   public static a a(int var0, int var1, int var2, int var3, int var4, int var5, h var6) {
      return new a(org.valkyrienskies.core.impl.shadow.b.SECOND, var0, var1, var2, var3, var4, var5, 0, var6, 0);
   }

   public static a a(int var0, int var1, int var2, int var3, int var4, int var5, int var6, h var7, int var8) {
      return new a(org.valkyrienskies.core.impl.shadow.b.NANO, var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static a a(int var0) {
      return new a(org.valkyrienskies.core.impl.shadow.b.YEAR, var0, 0, 0, 0, 0, 0, 0, null, 0);
   }

   public static a a(int var0, int var1) {
      return new a(org.valkyrienskies.core.impl.shadow.b.MONTH, var0, var1, 0, 0, 0, 0, 0, null, 0);
   }

   public static a a(int var0, int var1, int var2) {
      return new a(org.valkyrienskies.core.impl.shadow.b.DAY, var0, var1, var2, 0, 0, 0, 0, null, 0);
   }

   public static a a(int var0, int var1, int var2, int var3, int var4, h var5) {
      return new a(org.valkyrienskies.core.impl.shadow.b.MINUTE, var0, var1, var2, var3, var4, 0, 0, var5, 0);
   }

   private static a a(OffsetDateTime var0) {
      return a(
         var0.getYear(),
         var0.getMonthValue(),
         var0.getDayOfMonth(),
         var0.getHour(),
         var0.getMinute(),
         var0.getSecond(),
         var0.getNano(),
         org.valkyrienskies.core.impl.shadow.h.a(var0.getOffset()),
         9
      );
   }

   private static int a(int var0, int var1, int var2, b var3) {
      if (var0 > var2) {
         throw new DateTimeException("Field " + var3.name() + " out of bounds. Expected " + var1 + "-" + var2 + ", got " + var0);
      } else {
         return var0;
      }
   }

   public final boolean a(b var1) {
      return var1.ordinal() <= this.c.ordinal();
   }

   public final int a() {
      return this.a;
   }

   private int g() {
      return this.d;
   }

   private int h() {
      return this.e;
   }

   private int i() {
      return this.f;
   }

   private int j() {
      return this.g;
   }

   private int k() {
      return this.h;
   }

   private int l() {
      return this.i;
   }

   public final Optional<h> b() {
      return Optional.ofNullable(this.b);
   }

   private Year m() {
      return Year.of(this.a);
   }

   public final YearMonth c() {
      this.b(org.valkyrienskies.core.impl.shadow.b.MONTH);
      return YearMonth.of(this.a, this.d);
   }

   public final LocalDateTime d() {
      this.b(org.valkyrienskies.core.impl.shadow.b.MINUTE);
      return LocalDateTime.of(this.a, this.d, this.e, this.f, this.g, this.h, this.i);
   }

   public final OffsetDateTime e() {
      this.b(org.valkyrienskies.core.impl.shadow.b.MINUTE);
      if (this.b != null) {
         return OffsetDateTime.of(this.a, this.d, this.e, this.f, this.g, this.h, this.i, this.b.d());
      } else {
         throw new DateTimeException("No zone offset information found");
      }
   }

   public final LocalDate f() {
      this.b(org.valkyrienskies.core.impl.shadow.b.DAY);
      return LocalDate.of(this.a, this.d, this.e);
   }

   private b n() {
      return this.c;
   }

   private void b(b var1) {
      if (!this.a(var1)) {
         throw new DateTimeException("No " + var1.name() + " field found");
      }
   }

   private String c(b var1) {
      return a(this, var1, 0);
   }

   private String b(int var1) {
      return a(this, org.valkyrienskies.core.impl.shadow.b.NANO, var1);
   }

   private static String a(a var0, b var1, int var2) {
      if (var1.ordinal() > var0.c.ordinal()) {
         throw new DateTimeException("Requested granularity was " + var1.name() + ", but contains only granularity " + var0.c.name());
      }

      h var3 = Optional.ofNullable(var0.b).orElse(null);
      char[] var4 = new char[35];
      m.a(var0.a, var4, 0, 4);
      if (var1 == org.valkyrienskies.core.impl.shadow.b.YEAR) {
         b var10 = org.valkyrienskies.core.impl.shadow.b.YEAR;
         return k.a(var4, org.valkyrienskies.core.impl.shadow.b.YEAR.requiredLength, null);
      }

      if (var1.ordinal() >= org.valkyrienskies.core.impl.shadow.b.MONTH.ordinal()) {
         var4[4] = '-';
         m.a(var0.d, var4, 5, 2);
      }

      if (var1 == org.valkyrienskies.core.impl.shadow.b.MONTH) {
         b var9 = org.valkyrienskies.core.impl.shadow.b.MONTH;
         return k.a(var4, org.valkyrienskies.core.impl.shadow.b.MONTH.requiredLength, null);
      }

      if (var1.ordinal() >= org.valkyrienskies.core.impl.shadow.b.DAY.ordinal()) {
         var4[7] = '-';
         m.a(var0.e, var4, 8, 2);
      }

      if (var1 == org.valkyrienskies.core.impl.shadow.b.DAY) {
         b var8 = org.valkyrienskies.core.impl.shadow.b.DAY;
         return k.a(var4, org.valkyrienskies.core.impl.shadow.b.DAY.requiredLength, null);
      }

      if (var1.ordinal() >= org.valkyrienskies.core.impl.shadow.b.HOUR.ordinal()) {
         var4[10] = 'T';
         m.a(var0.f, var4, 11, 2);
      }

      if (var1 == org.valkyrienskies.core.impl.shadow.b.HOUR) {
         b var7 = org.valkyrienskies.core.impl.shadow.b.HOUR;
         return k.a(var4, org.valkyrienskies.core.impl.shadow.b.HOUR.requiredLength, var3);
      }

      if (var1.ordinal() >= org.valkyrienskies.core.impl.shadow.b.MINUTE.ordinal()) {
         var4[13] = ':';
         m.a(var0.g, var4, 14, 2);
      }

      if (var1 == org.valkyrienskies.core.impl.shadow.b.MINUTE) {
         b var6 = org.valkyrienskies.core.impl.shadow.b.MINUTE;
         return k.a(var4, org.valkyrienskies.core.impl.shadow.b.MINUTE.requiredLength, var3);
      }

      if (var1.ordinal() >= org.valkyrienskies.core.impl.shadow.b.SECOND.ordinal()) {
         var4[16] = ':';
         m.a(var0.h, var4, 17, 2);
      }

      if (var1 == org.valkyrienskies.core.impl.shadow.b.SECOND) {
         b var5 = org.valkyrienskies.core.impl.shadow.b.SECOND;
         return k.a(var4, org.valkyrienskies.core.impl.shadow.b.SECOND.requiredLength, var3);
      }

      if (var1.ordinal() >= org.valkyrienskies.core.impl.shadow.b.NANO.ordinal()) {
         var4[19] = '.';
         m.a(var0.i, var4, 20, var2);
      }

      return k.a(var4, var2 + 20, var3);
   }

   private int o() {
      return this.j;
   }

   @Override
   public final String toString() {
      if (this.j > 0) {
         int var3 = this.j;
         return a(this, org.valkyrienskies.core.impl.shadow.b.NANO, var3);
      } else {
         b var2 = this.c;
         return a(this, var2, 0);
      }
   }

   @Override
   public final boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         o = (a)o;
         return this.a == ((a)o).a
            && this.d == ((a)o).d
            && this.e == ((a)o).e
            && this.f == ((a)o).f
            && this.g == ((a)o).g
            && this.h == ((a)o).h
            && this.i == ((a)o).i
            && this.j == ((a)o).j
            && this.c == ((a)o).c
            && Objects.equals(this.b, ((a)o).b);
      } else {
         return false;
      }
   }

   @Override
   public final int hashCode() {
      return Objects.hash(this.c.ordinal(), this.a, this.d, this.e, this.f, this.g, this.h, this.i, this.b, this.j);
   }
}
