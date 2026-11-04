package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cS extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cS.class);
   private List<String> i = new ArrayList<>();

   public cS(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.REQUIRED, var4);
      if (var2.isArray()) {
         for (var2 : var2) {
            this.i.add(var2.asText());
         }
      }

      this.a(this.e().d());
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      if (!var1.isObject()) {
         return Collections.emptySet();
      }

      LinkedHashSet var7 = new LinkedHashSet();

      for (String var5 : this.i) {
         if (var1.get(var5) == null) {
            var7.add(this.a(var3, var5));
         }
      }

      return Collections.unmodifiableSet(var7);
   }
}
