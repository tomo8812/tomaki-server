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

public class ca extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(ca.class);
   private final Map<String, List<String>> i = new HashMap<>();
   private final Map<String, cp> j = new HashMap<>();

   public ca(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.DEPENDENCIES, var4);
      Iterator var8 = var2.fieldNames();

      while (var8.hasNext()) {
         String var5 = (String)var8.next();
         JsonNode var6;
         if ((var6 = var2.get(var5)).isArray()) {
            List var7;
            if ((var7 = this.i.get(var5)) == null) {
               var7 = new ArrayList();
               this.i.put(var5, var7);
            }

            for (int var9 = 0; var9 < var6.size(); var9++) {
               var7.add(var6.get(var9).asText());
            }
         } else if (var6.isObject() || var6.isBoolean()) {
            this.j.put(var5, new cp(var4, var5, var3.i(), var6, var3));
         }
      }

      this.a(this.e().d());
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      LinkedHashSet var4 = new LinkedHashSet();
      Iterator var5 = var1.fieldNames();

      while (var5.hasNext()) {
         String var6 = (String)var5.next();
         List var7;
         if ((var7 = this.i.get(var6)) != null && !var7.isEmpty()) {
            for (String var8 : var7) {
               if (var1.get(var8) == null) {
                  var4.add(this.a(var3, this.i.toString()));
               }
            }
         }

         cp var10;
         if ((var10 = this.j.get(var6)) != null) {
            var4.addAll(var10.a(var1, var2, var3));
         }
      }

      return Collections.unmodifiableSet(var4);
   }

   @Override
   public void a_() {
      this.a(this.j.values());
   }
}
