package org.valkyrienskies.core.impl.shadow;

public final class eo<T> implements dT<T>, ek<T> {
   private static final eo<@Cx Object> a = new eo<>(null);
   private final T b;

   public static <T> ek<T> a(T instance) {
      return new eo<>(ev.a((T)instance, "instance cannot be null"));
   }

   private static <T> ek<T> b(T instance) {
      return (ek<T>)(instance == null ? a : new eo<>((T)instance));
   }

   private static <T> eo<T> a() {
      return (eo<T>)a;
   }

   private eo(T instance) {
      this.b = (T)instance;
   }

   @Override
   public final T get() {
      return this.b;
   }
}
