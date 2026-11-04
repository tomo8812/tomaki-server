package org.valkyrienskies.core.impl.shadow;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class Gc {
   private static String a;
   private static final MethodHandle b;

   private Gc() {
   }

   public static void a() {
      if (b != null) {
         try {
            b.invokeExact();
            return;
         } catch (Throwable var0) {
         }
      }
   }

   static {
      MethodHandle var0 = null;
      if (!Boolean.getBoolean("org.agrona.hints.disable.onSpinWait")) {
         try {
            var0 = MethodHandles.lookup().findStatic(Thread.class, "onSpinWait", MethodType.methodType(void.class));
         } catch (Exception var2) {
         }
      }

      b = var0;
   }
}
