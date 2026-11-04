package org.valkyrienskies.core.impl.shadow;

import java.lang.reflect.InvocationTargetException;

public final class hl<T> implements fQ<Class<? extends T>, T> {
   private static final hl a = new hl();
   private final Class<?>[] b;
   private final Object[] c;

   public static <T> fQ<Class<? extends T>, T> a() {
      return a;
   }

   public static <T> fQ<Class<? extends T>, T> a(Class<?>[] var0, Object[] var1) {
      if ((var0 != null || var1 == null) && (var0 == null || var1 != null) && (var0 == null || var1 == null || var0.length == var1.length)) {
         return var0 != null && var0.length != 0 ? new hl<>(var0, var1) : new hl<>();
      } else {
         throw new IllegalArgumentException("Parameter types must match the arguments");
      }
   }

   private hl() {
      this.b = null;
      this.c = null;
   }

   private hl(Class<?>[] var1, Object[] var2) {
      this.b = var1 != null ? (Class[])var1.clone() : null;
      this.c = var2 != null ? (Object[])var2.clone() : null;
   }

   private T a(Class<? extends T> var1) {
      try {
         if (var1 == null) {
            throw new fk("InstantiateTransformer: Input object was not an instanceof Class, it was a null object");
         } else {
            return (T)var1.getConstructor(this.b).newInstance(this.c);
         }
      } catch (NoSuchMethodException var2) {
         throw new fk("InstantiateTransformer: The constructor must exist and be public ");
      } catch (InstantiationException var3) {
         throw new fk("InstantiateTransformer: InstantiationException", var3);
      } catch (IllegalAccessException var4) {
         throw new fk("InstantiateTransformer: Constructor must be public", var4);
      } catch (InvocationTargetException var5) {
         throw new fk("InstantiateTransformer: Constructor threw an exception", var5);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object a(Object var1) {
      return this.a((Class<? extends T>)var1);
   }
}
