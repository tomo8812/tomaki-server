package org.valkyrienskies.core.impl.shadow;

import java.time.DateTimeException;

public abstract class i implements n {
   private static int a;

   protected static void a(int var0) {
      if (var0 > 9) {
         throw new DateTimeException("Maximum supported number of fraction digits in second is 9, got " + var0);
      }
   }
}
