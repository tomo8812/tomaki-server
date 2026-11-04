package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cQ extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cS.class);
   private List<String> i = new ArrayList<>();

   public cQ(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.READ_ONLY, var4);
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
      LinkedHashSet var4 = new LinkedHashSet();

      for (String var6 : this.i) {
         JsonNode var7 = var1.get(var6);
         String var8 = "";
         if (var3.equals("$")) {
            var8 = var8 + "#original." + var6;
         } else {
            var8 = var8 + "#original." + var3.substring(2) + "." + var6;
         }

         JsonNode var9 = this.a(var8, var2);
         if (var7 == null || var9 == null || !var7.equals(var9)) {
            var4.add(this.a(var3));
         }
      }

      return Collections.unmodifiableSet(var4);
   }

   private JsonNode a(String var1, JsonNode var2) {
      String[] var7 = this.a(var1, "$.").split("\\.");
      JsonNode var3 = null;

      for (int var4 = 0; var4 < var7.length; var4++) {
         if (var7[var4].contains("[")) {
            int var8 = var7[var4].indexOf("[");
            int var5 = var7[var4].indexOf("]");
            String var6 = var7[var4].substring(0, var8).trim();
            int var9 = Integer.parseInt(var7[var4].substring(var8 + 1, var5).trim());
            var3 = var2.get(var6).get(var9);
         } else {
            var3 = var2.get(var7[var4]);
         }

         if (var3 == null) {
            break;
         }

         var2 = var3;
      }

      return var3;
   }

   private String a(String var1, String var2) {
      if ((var1 = var1).startsWith(var2)) {
         var1 = var1.substring(2);
      }

      return var1;
   }
}
