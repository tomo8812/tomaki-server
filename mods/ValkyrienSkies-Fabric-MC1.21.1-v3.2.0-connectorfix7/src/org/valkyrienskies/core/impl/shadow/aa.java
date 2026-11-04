package org.valkyrienskies.core.impl.shadow;

import java.util.EnumSet;

public enum aa {
   MISSING_VALUES_AS_NULLS,
   REMOVE_NONE_EXISTING_ARRAY_ELEMENT,
   ALLOW_MISSING_TARGET_OBJECT_ON_REPLACE;

   public static EnumSet<aa> a() {
      return EnumSet.noneOf(aa.class);
   }
}
