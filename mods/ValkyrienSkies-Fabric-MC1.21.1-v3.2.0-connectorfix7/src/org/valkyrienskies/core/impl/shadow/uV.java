package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

final class uV implements Serializable {
   private static final long c = 2247518849090889379L;
   String a;
   double b;

   uV(String var1, double var2) {
      this.a = var1;
      this.b = Double.NaN;
   }

   public final String a() {
      return this.a;
   }

   public final double b() {
      return this.b;
   }

   public final void a(double var1) {
      this.b = var1;
   }
}
