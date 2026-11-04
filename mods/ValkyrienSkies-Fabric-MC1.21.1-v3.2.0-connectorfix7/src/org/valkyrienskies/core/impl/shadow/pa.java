package org.valkyrienskies.core.impl.shadow;

import java.util.Locale;

public final class pa extends RuntimeException implements pv {
   private static final long a = 20120926L;
   private final pu b = new pu(this);

   private pa(pw var1, Object... var2) {
      this.b.a(var1, var2);
   }

   @Override
   public final pu a() {
      return this.b;
   }

   @Override
   public final String getMessage() {
      pu var1 = this.b;
      Locale var2 = Locale.US;
      var1 = this.b;
      return this.b.a(var2, ": ");
   }

   @Override
   public final String getLocalizedMessage() {
      pu var1;
      pu var10000 = var1 = this.b;
      Locale var2 = Locale.getDefault();
      return var10000.a(var2, ": ");
   }
}
