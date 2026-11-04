package maki.lodcpu;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

final class ExactStatic {
   private ExactStatic() {
   }

   static MethodHandle find(Class<?> var0, String var1, Class<?> var2, Class<?>... var3) throws NoSuchMethodException, IllegalAccessException {
      return MethodHandles.publicLookup().findStatic(var0, var1, MethodType.methodType(var2, var3));
   }
}
