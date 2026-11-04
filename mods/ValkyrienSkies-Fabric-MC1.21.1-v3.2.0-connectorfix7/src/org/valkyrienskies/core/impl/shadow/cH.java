package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cH extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cH.class);
   private List<String> i = new ArrayList<>();

   public cH(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.NOT_ALLOWED, var4);
      if (var2.isArray()) {
         int var5 = var2.size();

         for (int var6 = 0; var6 < var5; var6++) {
            this.i.add(var2.get(var6).asText());
         }
      }

      this.a(this.e().d());
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      LinkedHashSet var7 = new LinkedHashSet();

      for (String var5 : this.i) {
         if (var1.get(var5) != null) {
            var7.add(this.a(var3, var5));
         }
      }

      return Collections.unmodifiableSet(var7);
   }
}
