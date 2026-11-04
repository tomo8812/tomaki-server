package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.EnumSet;

final class ac extends af {
   private ac(JsonNode var1) {
      this(var1, aa.a());
   }

   ac(JsonNode var1, EnumSet<aa> var2) {
      super(var1.deepCopy(), var2);
   }
}
