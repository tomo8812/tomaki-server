package org.valkyrienskies.core.impl.shadow;

import java.lang.reflect.InvocationTargetException;

public final class hm<I, O> implements fQ<I, O> {
   private final String a;
   private final Class<?>[] b;
   private final Object[] c;

   public static <I, O> fQ<I, O> a(String var0) {
      if (var0 == null) {
         throw new NullPointerException("The method to invoke must not be null");
      } else {
         return new hm<>(var0);
      }
   }

   public static <I, O> fQ<I, O> a(String var0, Class<?>[] var1, Object[] var2) {
      if (var0 == null) {
         throw new NullPointerException("The method to invoke must not be null");
      } else if ((var1 != null || var2 == null) && (var1 == null || var2 != null) && (var1 == null || var2 == null || var1.length == var2.length)) {
         return var1 != null && var1.length != 0 ? new hm<>(var0, var1, var2) : new hm<>(var0);
      } else {
         throw new IllegalArgumentException("The parameter types must match the arguments");
      }
   }

   private hm(String var1) {
      this.a = var1;
      this.b = null;
      this.c = null;
   }

   private hm(String var1, Class<?>[] var2, Object[] var3) {
      this.a = var1;
      this.b = var2 != null ? (Class[])var2.clone() : null;
      this.c = var3 != null ? (Object[])var3.clone() : null;
   }

   @Override
   public final O a(Object var1) {
      if (var1 == null) {
         return null;
      }

      try {
         return (O)var1.getClass().getMethod(this.a, this.b).invoke(var1, this.c);
      } catch (NoSuchMethodException var3) {
         throw new fk("InvokerTransformer: The method '" + this.a + "' on '" + var1.getClass() + "' does not exist");
      } catch (IllegalAccessException var4) {
         throw new fk("InvokerTransformer: The method '" + this.a + "' on '" + var1.getClass() + "' cannot be accessed");
      } catch (InvocationTargetException var5) {
         throw new fk("InvokerTransformer: The method '" + this.a + "' on '" + var1.getClass() + "' threw an exception", var5);
      }
   }
}
