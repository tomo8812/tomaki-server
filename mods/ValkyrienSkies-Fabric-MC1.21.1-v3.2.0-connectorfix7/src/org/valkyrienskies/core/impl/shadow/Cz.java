package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;

public final class Cz implements ek<Cy> {
   private final Provider<CA> a;

   private Cz(Provider<CA> var1) {
      this.a = var1;
   }

   private Cy a() {
      Provider var1 = this.a;
      return new Cy(var1);
   }

   public static Cz a(Provider<CA> var0) {
      return new Cz(var0);
   }

   private static Cy a(Provider<CA> var0) {
      return new Cy(var0);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      Provider var1 = this.a;
      return new Cy(var1);
   }
}
