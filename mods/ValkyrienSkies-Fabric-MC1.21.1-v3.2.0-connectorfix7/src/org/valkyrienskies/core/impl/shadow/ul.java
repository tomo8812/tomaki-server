package org.valkyrienskies.core.impl.shadow;

public final class ul {
   private ul() {
   }

   private static <unrepresentable> a(final double var0, final double var2, final long var4) {
      if (!(var0 <= 0.0) && !(var0 > 1.0)) {
         return new uk() {
            private final uo a = new uo(var0, var2, var4);

            @Override
            public final double a(long var1) {
               return this.a.a(var1);
            }
         };
      } else {
         throw new po(var0, 0, 1);
      }
   }

   private static <unrepresentable> b(final double var0, final double var2, final long var4) {
      if (!(var0 <= 0.0) && !(var0 > 1.0)) {
         return new uk() {
            private final up a = new up(var0, var2, var4);

            @Override
            public final double a(long var1) {
               return this.a.a(var1);
            }
         };
      } else {
         throw new po(var0, 0, 1);
      }
   }
}
