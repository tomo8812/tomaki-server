package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cL extends bT implements cv {
   public static final String g = "patternProperties";
   private static final Logger i = LoggerFactory.getLogger(cL.class);
   private final Map<Pattern, cp> j = new IdentityHashMap<>();

   public cL(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.PATTERN_PROPERTIES, var4);
      if (!var2.isObject()) {
         throw new cq("patternProperties must be an object node");
      }

      Iterator var6 = var2.fieldNames();

      while (var6.hasNext()) {
         String var5 = (String)var6.next();
         this.j.put(Pattern.compile(var5), new cp(var4, var5, var3.i(), var2.get(var5), var3));
      }
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(i, (JsonNode)var1, var2, var3);
      LinkedHashSet var4 = new LinkedHashSet();
      if (!var1.isObject()) {
         return var4;
      }

      Iterator var5 = var1.fieldNames();

      while (var5.hasNext()) {
         String var6 = (String)var5.next();
         JsonNode var7 = var1.get(var6);
         Iterator var8 = this.j.entrySet().iterator();

         while (var8.hasNext()) {
            Entry var9;
            if (((Pattern)(var9 = (Entry)var8.next()).getKey()).matcher(var6).find()) {
               this.b(var3 + "." + var6);
               var4.addAll(((cp)var9.getValue()).a(var7, var2, var3 + "." + var6));
            }
         }
      }

      return Collections.unmodifiableSet(var4);
   }

   @Override
   public void a_() {
      this.a(this.j.values());
   }

   private void b(String var1) {
      Object var2;
      if ((var2 = (List)bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties")) == null) {
         var2 = new ArrayList();
         bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var2);
      } else {
         var2 = var2;
      }

      var2.add(var1);
   }
}
