package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;

public final class CJ implements ek<CH> {
   private final Provider<CN> a;

   private CJ(Provider<CN> var1) {
      this.a = var1;
   }

   private CH a() {
      CN var1 = this.a.get();
      return new CH(var1);
   }

   public static CJ a(Provider<CN> var0) {
      return new CJ(var0);
   }

   private static CH a(CN var0) {
      return new CH(var0);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      CN var1 = this.a.get();
      return new CH(var1);
   }
}
