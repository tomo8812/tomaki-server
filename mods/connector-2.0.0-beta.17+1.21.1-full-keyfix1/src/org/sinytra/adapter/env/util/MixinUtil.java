package org.sinytra.adapter.env.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Locale;
import java.util.Map;
import org.spongepowered.asm.mixin.injection.InjectionPoint;

public class MixinUtil {
   private static final VarHandle TYPES_HANDLE;
   private static final String CLS_REGEX = "^([A-Za-z_][A-Za-z0-9_]*[\\.\\$])+[A-Za-z_][A-Za-z0-9_]*$";

   public static boolean isKnownInjectionPointType(String value) {
      Map<String, Class<? extends InjectionPoint>> types = (Map)TYPES_HANDLE.get();
      return types.containsKey(value.toUpperCase(Locale.ROOT)) || value.matches("^([A-Za-z_][A-Za-z0-9_]*[\\.\\$])+[A-Za-z_][A-Za-z0-9_]*$");
   }

   static {
      try {
         TYPES_HANDLE = MethodHandles.privateLookupIn(InjectionPoint.class, MethodHandles.lookup())
            .findStaticVarHandle(InjectionPoint.class, "types", Map.class);
      } catch (Exception e) {
         throw new RuntimeException("Error unreflecting Injection Point types", e);
      }
   }
}
