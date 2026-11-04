package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class bQ extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(bQ.class);
   private final List<cp> i = new ArrayList<>();

   public bQ(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.ALL_OF, var4);
      super.d = var4;
      int var6 = var2.size();

      for (int var5 = 0; var5 < var6; var5++) {
         this.i.add(new cp(var4, this.e().a(), var3.i(), var2.get(var5), var3));
      }
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      di var4 = (di)bV.a().a("com.networknt.schema.ValidatorState");
      LinkedHashSet var5 = new LinkedHashSet();
      Object var6 = bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties");
      ArrayList var7 = new ArrayList();

      for (cp var9 : this.i) {
         try {
            bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", new ArrayList());
            new HashSet();
            Set var17;
            if (!var4.c()) {
               var17 = var9.a(var1, var2, var3);
            } else {
               var17 = var9.a(var1, var2, var3, true);
            }

            var5.addAll(var17);
            if (var17.isEmpty()) {
               var7.addAll((List)bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties"));
            }

            if (super.d.d().m()) {
               Iterator var18 = super.b.elements();

               while (var18.hasNext()) {
                  ObjectNode var10;
                  JsonNode var11 = (var10 = (ObjectNode)var18.next()).get("$ref");
                  if (var11 != null) {
                     df.a var20 = super.d.e();
                     if (var20 != null) {
                        var10 = var20.a(var10.get("$ref").asText());
                        if (var10 != null) {
                           a(var20, var10, super.c, var3);
                           String var12 = var10.get("propertyName").asText();
                           JsonNode var21;
                           var12 = (var21 = var1.get(var12)) == null ? null : var21.textValue();
                           cp var13 = super.c;
                           a(var20, var10, var12, var13);
                        }
                     }
                  }
               }
            }
         } finally {
            if (var5.isEmpty()) {
               List var16;
               (var16 = var6 == null ? new ArrayList() : (List)var6).addAll(var7);
               bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var16);
            } else {
               bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var6);
            }
         }
      }

      return Collections.unmodifiableSet(var5);
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3, boolean var4) {
      if (var4) {
         return this.a(var1, var2, var3);
      }

      Iterator var6 = this.i.iterator();

      while (var6.hasNext()) {
         ((cp)var6.next()).a(var1, var2, var3, false);
      }

      return Collections.emptySet();
   }

   @Override
   public void a_() {
      this.a(this.i);
   }
}
