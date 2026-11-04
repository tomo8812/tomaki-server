package org.valkyrienskies.core.impl.shadow;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class hk<T> implements fh<T> {
   private final Class<T> a;
   private final Class<?>[] b;
   private final Object[] c;
   private transient Constructor<T> d = null;

   public static <T> fh<T> a(Class<T> var0, Class<?>[] var1, Object[] var2) {
      if (var0 == null) {
         throw new NullPointerException("Class to instantiate must not be null");
      } else if ((var1 != null || var2 == null) && (var1 == null || var2 != null) && (var1 == null || var2 == null || var1.length == var2.length)) {
         return var1 != null && var1.length != 0 ? new hk<>(var0, var1, var2) : new hk<>(var0);
      } else {
         throw new IllegalArgumentException("Parameter types must match the arguments");
      }
   }

   private hk(Class<T> var1) {
      this.a = var1;
      this.b = null;
      this.c = null;
      this.b();
   }

   public hk(Class<T> var1, Class<?>[] var2, Object[] var3) {
      this.a = var1;
      this.b = (Class<?>[])var2.clone();
      this.c = (Object[])var3.clone();
      this.b();
   }

   private void b() {
      try {
         this.d = this.a.getConstructor(this.b);
      } catch (NoSuchMethodException var1) {
         throw new IllegalArgumentException("InstantiateFactory: The constructor must exist and be public ");
      }
   }

   @Override
   public final T a() {
      if (this.d == null) {
         this.b();
      }

      try {
         return this.d.newInstance(this.c);
      } catch (InstantiationException var2) {
         throw new fk("InstantiateFactory: InstantiationException", var2);
      } catch (IllegalAccessException var3) {
         throw new fk("InstantiateFactory: Constructor must be public", var3);
      } catch (InvocationTargetException var4) {
         throw new fk("InstantiateFactory: Constructor threw an exception", var4);
      }
   }
}
