package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;

public final class ei<T> implements ek<T> {
   @Cx ew<T> a;

   @Override
   public final T get() {
      if (this.a == null) {
         throw new IllegalStateException();
      } else {
         return this.a.get();
      }
   }

   @Deprecated
   private void a(ew<T> delegate) {
      a((ew<T>)this, delegate);
   }

   @Deprecated
   private void a(Provider<T> delegate) {
      var var2 = ey.a(delegate);
      a(this, var2);
   }

   public static <T> void a(ew<T> delegateFactory, ew<T> delegate) {
      ei var2;
      a(var2 = (ei)delegateFactory, delegate);
   }

   @Deprecated
   private static <T> void a(Provider<T> delegateFactory, Provider<T> delegate) {
      ei var2;
      a(var2 = (ei)delegateFactory, ey.a(delegate));
   }

   private static <T> void a(ei<T> delegateFactory, ew<T> delegate) {
      ev.a(delegate);
      if (delegateFactory.a != null) {
         throw new IllegalStateException();
      }

      delegateFactory.a = delegate;
   }

   final ew<T> a() {
      return ev.a(this.a);
   }
}
