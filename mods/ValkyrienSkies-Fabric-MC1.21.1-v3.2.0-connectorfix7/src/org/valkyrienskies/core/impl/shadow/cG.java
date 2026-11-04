package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.Set;

public class cG extends bO {
   public cG(String var1) {
      super(var1);
   }

   @Override
   public cv a(String var1, JsonNode var2, cp var3, df var4) {
      return new cG.a(this.a());
   }

   static final class a extends bN {
      private a(String var1) {
         super(var1);
      }

      @Override
      public final Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
         return Collections.emptySet();
      }
   }
}
