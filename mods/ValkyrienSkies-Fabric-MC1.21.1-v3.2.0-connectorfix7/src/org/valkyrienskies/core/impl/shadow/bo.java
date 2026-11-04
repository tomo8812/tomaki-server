package org.valkyrienskies.core.impl.shadow;

import java.util.Map;
import java.util.function.Function;

public final class bo implements bq {
   private final bq a;
   private final Function<String, String> b;

   public bo(bq var1, Function<String, String> var2) {
      this.a = var1;
      this.b = var2;
   }

   @Override
   public final String a(aX var1, aI var2) {
      String var3 = this.a.a(var1, var2);
      return this.b.apply(var3);
   }

   @Override
   public final void a(Map<aX, String> var1, aI var2) {
      this.a.a(var1, var2);
      var1.entrySet().forEach(var1x -> var1x.setValue(this.b.apply((String)var1x.getValue())));
   }

   @Override
   public final String a(aX var1, String var2, aI var3) {
      String var4 = this.a.a(var1, var2, var3);
      return this.b.apply(var4);
   }
}
