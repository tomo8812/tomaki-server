package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cD extends bT implements cv {
   private static final Logger i = LoggerFactory.getLogger(cD.class);
   protected int g;

   public cD(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.MIN_PROPERTIES, var4);
      if (var2.canConvertToExactIntegral()) {
         this.g = var2.intValue();
      }

      this.a(this.e().d());
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(i, (JsonNode)var1, var2, var3);
      return var1.isObject() && var1.size() < this.g ? Collections.singleton(this.a(var3, "" + this.g)) : Collections.emptySet();
   }
}
