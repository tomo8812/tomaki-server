package org.valkyrienskies.core.impl.shadow;

import java.util.function.Predicate;

public final class bl implements aD {
   private final Predicate<aC> a;

   public static bl a() {
      return new bl(aC::b);
   }

   public static bl b() {
      return new bl(aC::d);
   }

   public static bl c() {
      return new bl(var0 -> !var0.a.isStatic() && !var0.b() && !var0.d());
   }

   private bl(Predicate<aC> var1) {
      this.a = var1;
   }

   @Override
   public final void a(aL var1) {
      var1.d.a(this.a);
   }
}
