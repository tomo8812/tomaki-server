package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;

public final class eD<T> implements ew<T> {
   private static final Object a = new Object();
   private volatile @Cx ew<T> b;
   private volatile @Cx Object c = a;
   // $VF: synthetic field
   private static boolean d = !eD.class.desiredAssertionStatus();

   private eD(ew<T> provider) {
      assert provider != null;
      this.b = provider;
   }

   @Override
   public final T get() {
      ew var1 = (ew)this.c;
      if (this.c == a) {
         var1 = this.b;
         if (this.b == null) {
            var1 = (ew)this.c;
         } else {
            var1 = (ew)var1.get();
            this.c = var1;
            this.b = null;
         }
      }

      return (T)var1;
   }

   public static <T> ew<T> a(ew<T> provider) {
      return !(provider instanceof eD) && !(provider instanceof ej) ? new eD<>(ev.a(provider)) : provider;
   }

   private static <P extends Provider<T>, T> Provider<T> a(P delegate) {
      return a(ey.a(delegate));
   }
}
