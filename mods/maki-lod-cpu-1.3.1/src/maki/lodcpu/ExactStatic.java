package maki.lodcpu;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

final class ExactStatic {
   private ExactStatic() {
   }

   static MethodHandle find(Class<?> owner, String name, Class<?> result, Class<?>... parameters) throws NoSuchMethodException, IllegalAccessException {
      return MethodHandles.publicLookup().findStatic(owner, name, MethodType.methodType(result, parameters));
   }
}
