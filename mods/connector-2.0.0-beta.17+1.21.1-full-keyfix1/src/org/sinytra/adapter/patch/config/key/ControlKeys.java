package org.sinytra.adapter.patch.config.key;

import java.util.List;
import org.objectweb.asm.Type;
import org.sinytra.adapter.env.param.MethodParameters;
import org.sinytra.adapter.patch.config.PropertyKey;

public final class ControlKeys {
   public static final PropertyKey<String> MIXIN_TYPE = PropertyKey.create("mixin_type");
   public static final PropertyKey<String> TARGET_CLASS = PropertyKey.create("target_class");
   public static final PropertyKey<MethodParameters> PARAMETERS = PropertyKey.create("parameters");
   public static final PropertyKey<Type> RETURN_TYPE = PropertyKey.create("return_type");
   public static final PropertyKey<Boolean> DELETE = PropertyKey.create("delete");

   private ControlKeys() {
   }

   public static <T> T parseSingle(Object obj, Class<T> cls) {
      if (obj instanceof List list) {
         return (T)(list.size() == 1 ? list.getFirst() : null);
      } else {
         return (T)(cls.isInstance(obj) ? obj : null);
      }
   }
}
