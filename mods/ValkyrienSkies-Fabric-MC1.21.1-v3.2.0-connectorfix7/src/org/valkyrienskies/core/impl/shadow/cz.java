package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cz extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cz.class);
   private int i;

   public cz(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.MAX_PROPERTIES, var4);
      if (var2.canConvertToExactIntegral()) {
         this.i = var2.intValue();
      }

      this.a(this.e().d());
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      return var1.isObject() && var1.size() > this.i ? Collections.singleton(this.a(var3, "" + this.i)) : Collections.emptySet();
   }
}
