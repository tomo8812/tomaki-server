package org.valkyrienskies.core.impl.shadow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.Temporal;

public interface e {
   default void a(LocalDateTime var1) {
      this.a((Temporal)var1);
   }

   default void a(LocalDate var1) {
      this.a((Temporal)var1);
   }

   default void a(YearMonth var1) {
      this.a((Temporal)var1);
   }

   default void a(Year var1) {
      this.a((Temporal)var1);
   }

   default void a(OffsetDateTime var1) {
      this.a((Temporal)var1);
   }

   default void a(Temporal var1) {
      throw new UnsupportedOperationException("Unhandled type " + var1.getClass());
   }
}
