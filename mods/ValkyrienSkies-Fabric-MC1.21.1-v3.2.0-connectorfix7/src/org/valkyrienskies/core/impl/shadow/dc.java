package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class dc extends bT implements cv {
   private static final Logger j = LoggerFactory.getLogger(dc.class);
   public static final String g = "com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties";
   public static final String i = "com.networknt.schema.UnEvaluatedPropertiesValidator.UnevaluatedProperties";
   private JsonNode k = null;

   public dc(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.UNEVALUATED_PROPERTIES, var4);
      this.k = var2;
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      if (!this.k.isBoolean()) {
         return Collections.emptySet();
      }

      boolean var6 = this.k.booleanValue();
      ArrayList var4 = new ArrayList();
      this.a(var1, var3, var4);
      if (!var6) {
         List var5;
         if (!(var5 = this.a(var4)).isEmpty()) {
            bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.UnevaluatedProperties", var5);
            return Collections.singleton(this.a(String.join(", ", var5)));
         }
      } else {
         bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var4);
      }

      return Collections.emptySet();
   }

   private List<String> a(List<String> var1) {
      ArrayList var2 = new ArrayList();
      Object var3;
      if ((var3 = (List)bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties")) != null) {
         var3 = var3;
         var1.forEach(var2x -> {
            if (!var3.contains(var2x)) {
               var2.add(var2x);
            }
         });
      } else {
         var2.addAll(var1);
      }

      return var2;
   }

   public void a(JsonNode var1, String var2, List<String> var3) {
      Iterator var4 = var1.fieldNames();

      while (var4.hasNext()) {
         String var5 = (String)var4.next();
         JsonNode var6;
         if ((var6 = var1.get(var5)).isObject()) {
            this.a(var6, var2 + "." + var5, var3);
         }

         var3.add(var2 + "." + var5);
      }
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3, boolean var4) {
      return var4 ? this.a(var1, var2, var3) : Collections.emptySet();
   }
}
