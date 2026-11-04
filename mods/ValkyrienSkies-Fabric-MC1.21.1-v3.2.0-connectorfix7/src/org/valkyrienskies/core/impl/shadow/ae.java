package org.valkyrienskies.core.impl.shadow;

import java.util.EnumSet;

public enum ae {
   OMIT_VALUE_ON_REMOVE,
   OMIT_MOVE_OPERATION,
   OMIT_COPY_OPERATION,
   ADD_ORIGINAL_VALUE_ON_REPLACE,
   ADD_EXPLICIT_REMOVE_ADD_ON_REPLACE,
   EMIT_TEST_OPERATIONS;

   public static EnumSet<ae> a() {
      return EnumSet.of(OMIT_VALUE_ON_REMOVE);
   }

   private static EnumSet<ae> b() {
      return EnumSet.of(OMIT_MOVE_OPERATION, OMIT_COPY_OPERATION);
   }
}
