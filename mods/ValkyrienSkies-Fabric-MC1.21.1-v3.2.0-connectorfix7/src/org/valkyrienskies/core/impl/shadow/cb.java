package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cb extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cb.class);
   private final Map<String, List<String>> i = new HashMap<>();

   public cb(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.DEPENDENT_REQUIRED, var4);
      Iterator var6 = var2.fieldNames();

      while (var6.hasNext()) {
         String var7 = (String)var6.next();
         JsonNode var9;
         if ((var9 = var2.get(var7)).isArray()) {
            List var8 = this.i.computeIfAbsent(var7, var0 -> new ArrayList<>());

            for (int var5 = 0; var5 < var9.size(); var5++) {
               var8.add(var9.get(var5).asText());
            }
         }
      }

      this.a(this.e().d());
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      LinkedHashSet var7 = new LinkedHashSet();
      Iterator var4 = var1.fieldNames();

      while (var4.hasNext()) {
         String var5 = (String)var4.next();
         List var8;
         if ((var8 = this.i.get(var5)) != null && !var8.isEmpty()) {
            for (String var6 : var8) {
               if (var1.get(var6) == null) {
                  var7.add(this.a(var3, this.i.toString()));
               }
            }
         }
      }

      return Collections.unmodifiableSet(var7);
   }
}
