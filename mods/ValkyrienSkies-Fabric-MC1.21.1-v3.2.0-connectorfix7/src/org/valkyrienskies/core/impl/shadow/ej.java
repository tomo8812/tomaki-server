package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;

public final class ej<T> implements dT<T>, ew<T> {
   private static final Object a = new Object();
   private volatile @Cx ew<T> b;
   private volatile @Cx Object c = a;
   // $VF: synthetic field
   private static boolean d = !ej.class.desiredAssertionStatus();

   private ej(ew<T> provider) {
      assert provider != null;
      this.b = provider;
   }

   @Override
   public final T get() {
      Object var1 = this.c;
      if (this.c == a) {
         var1 = this.a();
      }

      return (T)var1;
   }

   private synchronized Object a() {
      Object var1 = this.c;
      if (this.c == a) {
         var1 = this.b.get();
         Object var3 = var1;
         Object var2 = this.c;
         boolean var4;
         if ((var4 = this.c != a) && var2 != var3) {
            throw new IllegalStateException(
               "Scoped provider was invoked recursively returning different results: " + var2 + " & " + var3 + ". This is likely due to a circular dependency."
            );
         }

         this.c = var3;
         this.b = null;
      }

      return var1;
   }

   private static @Cx Object a(@Cx Object currentInstance, @Cx Object newInstance) {
      boolean var2;
      if ((var2 = currentInstance != a) && currentInstance != newInstance) {
         throw new IllegalStateException(
            "Scoped provider was invoked recursively returning different results: "
               + currentInstance
               + " & "
               + newInstance
               + ". This is likely due to a circular dependency."
         );
      } else {
         return newInstance;
      }
   }

   public static <T> ew<T> a(ew<T> delegate) {
      ev.a(delegate);
      return delegate instanceof ej ? delegate : new ej<>(delegate);
   }

   @Deprecated
   private static <P extends Provider<T>, T> Provider<T> b(P delegate) {
      return a(ey.a(delegate));
   }

   public static <T> dT<T> b(ew<T> provider) {
      dT var1;
      return provider instanceof dT ? (var1 = (dT)provider) : new ej<>(ev.a(provider));
   }

   public static <P extends Provider<T>, T> dT<T> a(P provider) {
      return b(ey.a(provider));
   }
}
