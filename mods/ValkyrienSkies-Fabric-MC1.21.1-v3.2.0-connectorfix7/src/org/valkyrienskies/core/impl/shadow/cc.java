package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cc extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cc.class);
   private final Map<String, cp> i = new HashMap<>();

   public cc(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.DEPENDENT_SCHEMAS, var4);
      Iterator var7 = var2.fieldNames();

      while (var7.hasNext()) {
         String var5 = (String)var7.next();
         JsonNode var6;
         if ((var6 = var2.get(var5)).isObject() || var6.isBoolean()) {
            this.i.put(var5, new cp(var4, var5, var3.i(), var6, var3));
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
         cp var7;
         if ((var7 = this.i.get(var6)) != null) {
            var4.addAll(var7.a(var1, var2, var3));
         }
      }

      return Collections.unmodifiableSet(var4);
   }

   @Override
   public void a_() {
      this.a(this.i.values());
   }
}
