package org.valkyrienskies.core.impl.shadow;

import java.time.DateTimeException;
import java.time.ZoneOffset;
import java.util.Objects;

public final class h {
   public static final h a = new h(0, 0);
   public final int b;
   public final int c;

   private h(int var1, int var2) {
      if (var1 > 0 && var2 < 0) {
         throw new DateTimeException("Zone offset minutes must be positive because hours is positive");
      }

      if (var1 < 0 && var2 > 0) {
         throw new DateTimeException("Zone offset minutes must be negative because hours is negative");
      }

      this.b = var1;
      this.c = var2;
   }

   public static h a(int var0, int var1) {
      return new h(var0, var1);
   }

   public static h a(ZoneOffset var0) {
      int var2;
      int var1 = (var2 = var0.getTotalSeconds()) / 3600;
      int var3;
      int var4 = (var3 = var2 % 3600) / 60;
      return a(var1, var4);
   }

   public final int a() {
      return this.b;
   }

   public final int b() {
      return this.c;
   }

   public final int c() {
      return this.b * 60 * 60 + this.c * 60;
   }

   public final ZoneOffset d() {
      return this.equals(a) ? ZoneOffset.UTC : ZoneOffset.ofHoursMinutes(this.b, this.c);
   }

   @Override
   public final boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         o = (h)o;
         return this.b == ((h)o).b && this.c == ((h)o).c;
      } else {
         return false;
      }
   }

   @Override
   public final int hashCode() {
      return Objects.hash(this.b, this.c);
   }

   @Override
   public final String toString() {
      return "TimezoneOffset{hours=" + this.b + ", minutes=" + this.c + '}';
   }
}
