package org.valkyrienskies.core.impl.shadow;

import java.time.DateTimeException;
import java.time.OffsetDateTime;

public final class d extends DateTimeException {
   private final int b;
   public final boolean a;
   private final OffsetDateTime c;

   public d(OffsetDateTime var1, int var2, boolean var3) {
      super("Leap second detected in input");
      this.c = var1;
      this.b = var2;
      this.a = var3;
   }

   private int b() {
      return this.b;
   }

   private OffsetDateTime c() {
      return this.c;
   }

   public final boolean a() {
      return this.a;
   }
}
