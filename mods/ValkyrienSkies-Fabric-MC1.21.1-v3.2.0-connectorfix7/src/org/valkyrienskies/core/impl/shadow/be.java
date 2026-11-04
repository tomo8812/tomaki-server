package org.valkyrienskies.core.impl.shadow;

import java.lang.reflect.Type;
import java.util.Map;

public final class be implements aD {
   private final as<aV, Type> a;

   public static be a() {
      return new be(var0 -> var0.d.isInstanceOf(Map.class) ? var0.a(Map.class, 1) : null);
   }

   public static be b() {
      return new be(var0 -> var0.v() ? null : Void.class);
   }

   private be(as<aV, Type> var1) {
      this.a = var1;
   }

   @Override
   public final void a(aL var1) {
      var1.b.b(this.a);
   }
}
