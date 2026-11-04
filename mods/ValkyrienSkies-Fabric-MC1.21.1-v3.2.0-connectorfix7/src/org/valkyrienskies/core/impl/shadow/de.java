package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class de extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(de.class);
   private boolean i = false;

   public de(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.UNIQUE_ITEMS, var4);
      if (var2.isBoolean()) {
         this.i = var2.booleanValue();
      }

      this.a(this.e().d());
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      if (this.i) {
         HashSet var6 = new HashSet();

         for (JsonNode var4 : var1) {
            if (!var6.add(var4)) {
               return Collections.singleton(this.a(var3));
            }
         }
      }

      return Collections.emptySet();
   }
}
