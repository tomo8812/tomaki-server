package org.valkyrienskies.core.impl.shadow;

import java.util.Locale;

public class pb extends UnsupportedOperationException implements pv {
   private static final long a = -6024911025449780478L;
   private final pu b = new pu(this);

   public pb() {
      this(px.UNSUPPORTED_OPERATION);
   }

   public pb(px var1, Object... var2) {
      this.b.a(var1, var2);
   }

   @Override
   public final pu a() {
      return this.b;
   }

   @Override
   public String getMessage() {
      pu var1 = this.b;
      Locale var2 = Locale.US;
      var1 = this.b;
      return this.b.a(var2, ": ");
   }

   @Override
   public String getLocalizedMessage() {
      pu var1;
      pu var10000 = var1 = this.b;
      Locale var2 = Locale.getDefault();
      return var10000.a(var2, ": ");
   }
}
