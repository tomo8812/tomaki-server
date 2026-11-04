package org.valkyrienskies.core.impl.shadow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.Temporal;

public interface f<T> {
   default T a(LocalDateTime var1) {
      return this.a((Temporal)var1);
   }

   default T a(LocalDate var1) {
      return this.a((Temporal)var1);
   }

   default T a(YearMonth var1) {
      return this.a((Temporal)var1);
   }

   default T a(Year var1) {
      return this.a((Temporal)var1);
   }

   default T a(OffsetDateTime var1) {
      return this.a((Temporal)var1);
   }

   default T a(Temporal var1) {
      throw new UnsupportedOperationException("Unhandled type " + var1.getClass());
   }
}
