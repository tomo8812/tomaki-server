package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;

public final class ex<T> implements ew<dT<T>> {
   private final ew<T> a;
   // $VF: synthetic field
   private static boolean b = !ex.class.desiredAssertionStatus();

   private ex(ew<T> provider) {
      assert provider != null;
      this.a = provider;
   }

   private dT<T> a() {
      return ej.b(this.a);
   }

   private static <T> ew<dT<T>> a(ew<T> provider) {
      return new ex<>(ev.a(provider));
   }

   @Deprecated
   private static <T> ew<dT<T>> a(Provider<T> provider) {
      Provider var1 = ey.a(provider);
      return new ex<>(ev.a(var1));
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      return ej.b(this.a);
   }
}
