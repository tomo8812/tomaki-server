package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cY extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cY.class);

   public cY(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.TRUE, var4);
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      return Collections.emptySet();
   }
}
