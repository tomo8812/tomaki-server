package org.valkyrienskies.core.impl.shadow;

public final class eu {
   private static <T> dV<T> a() {
      return eu.a.INSTANCE;
   }

   private eu() {
   }

   enum a implements dV<Object> {
      INSTANCE;

      @Override
      public final void a(Object instance) {
         ev.a(instance, "Cannot inject members into a null reference");
      }

      // $VF: synthetic method
      private static eu.a[] a() {
         return new eu.a[]{INSTANCE};
      }
   }
}
