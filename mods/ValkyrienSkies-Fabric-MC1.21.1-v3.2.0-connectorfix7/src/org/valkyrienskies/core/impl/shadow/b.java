package org.valkyrienskies.core.impl.shadow;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.Temporal;

public enum b {
   YEAR(4),
   MONTH(7),
   DAY(10),
   HOUR(13),
   MINUTE(16),
   SECOND(19),
   NANO(20);

   public final int requiredLength;

   b(int var3) {
      this.requiredLength = var3;
   }

   private static b a(Class<? extends Temporal> var0) {
      if (Year.class.equals(var0)) {
         return YEAR;
      } else if (YearMonth.class.equals(var0)) {
         return MONTH;
      } else if (LocalDate.class.equals(var0)) {
         return DAY;
      } else if (OffsetDateTime.class.equals(var0)) {
         return SECOND;
      } else {
         throw new IllegalArgumentException("Type " + var0.getSimpleName() + " is not supported");
      }
   }

   public final int a() {
      return this.requiredLength;
   }
}
