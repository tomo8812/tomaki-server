package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cx extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cx.class);
   private int i = 0;

   public cx(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.MAX_ITEMS, var4);
      if (var2.canConvertToExactIntegral()) {
         this.i = var2.intValue();
      }

      super.d = var4;
      this.a(this.e().d());
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      if (var1.isArray()) {
         if (var1.size() > this.i) {
            return Collections.singleton(this.a(var3, "" + this.i));
         }
      } else if (super.d.d().a() && this.i <= 0) {
         return Collections.singleton(this.a(var3, "" + this.i));
      }

      return Collections.emptySet();
   }
}
