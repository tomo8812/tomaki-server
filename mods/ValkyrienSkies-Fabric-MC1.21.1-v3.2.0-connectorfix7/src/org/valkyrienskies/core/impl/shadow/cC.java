package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cC extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cC.class);
   private int i = Integer.MIN_VALUE;

   public cC(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.MIN_LENGTH, var4);
      if (var2 != null && var2.canConvertToExactIntegral()) {
         this.i = var2.intValue();
      }

      super.d = var4;
      this.a(this.e().d());
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      if (cZ.a(var1, super.d.d()) != cu.STRING) {
         return Collections.emptySet();
      } else {
         return var1.textValue().codePointCount(0, var1.textValue().length()) < this.i
            ? Collections.singleton(this.a(var3, "" + this.i))
            : Collections.emptySet();
      }
   }
}
