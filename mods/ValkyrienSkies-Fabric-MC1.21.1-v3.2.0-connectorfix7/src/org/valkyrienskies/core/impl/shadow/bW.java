package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class bW extends bT implements cv {
   private static final Logger i = LoggerFactory.getLogger(bW.class);
   JsonNode g;

   public bW(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.CONST, var4);
      this.g = var2;
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(i, (JsonNode)var1, var2, var3);
      LinkedHashSet var4 = new LinkedHashSet();
      if (this.g.isNumber() && var1.isNumber()) {
         if (this.g.decimalValue().compareTo(var1.decimalValue()) != 0) {
            var4.add(this.a(var3, this.g.asText()));
         }
      } else if (!this.g.equals(var1)) {
         var4.add(this.a(var3, this.g.asText()));
      }

      return Collections.unmodifiableSet(var4);
   }
}
