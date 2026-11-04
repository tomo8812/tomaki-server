package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cO extends bT implements cv {
   public static final String g = "properties";
   private static final Logger i = LoggerFactory.getLogger(cO.class);
   private final Map<String, cp> j = new HashMap<>();

   public cO(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.PROPERTIES, var4);
      super.d = var4;
      Iterator var5 = var2.fieldNames();

      while (var5.hasNext()) {
         String var6 = (String)var5.next();
         this.j.put(var6, new cp(var4, var1 + "/" + var6, var3.i(), var2.get(var6), var3));
      }
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(i, (JsonNode)var1, var2, var3);
      dJ var4 = new dJ(super.d.d().h());
      LinkedHashSet var5 = new LinkedHashSet();
      di var6 = (di)bV.a().a("com.networknt.schema.ValidatorState");
      Iterator var7 = this.j.entrySet().iterator();

      while (var7.hasNext()) {
         Entry var8;
         cp var9 = (cp)(var8 = (Entry)var7.next()).getValue();
         JsonNode var10;
         if ((var10 = var1.get((String)var8.getKey())) != null) {
            this.b(var3 + "." + (String)var8.getKey());
            boolean var11 = var6.b();
            if (var6.b()) {
               var6.a(true);
            }

            var6.b(false);
            if (!var6.c()) {
               var5.addAll(var9.a(var10, var2, var3 + "." + (String)var8.getKey()));
            } else {
               this.a(var8, var1, var2, var3, var6.d(), var5, var4);
            }

            var6.b(var11);
            if (var6.b()) {
               var6.a(true);
            }
         } else {
            Set var12;
            if (this.d().l() && !(var12 = this.d().m().a(var1, var2, var3)).isEmpty()) {
               if (var6.b()) {
                  var6.a(false);
                  return Collections.unmodifiableSet(new LinkedHashSet<>());
               }

               var5.addAll(var12);
            }
         }
      }

      return Collections.unmodifiableSet(var5);
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

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3, boolean var4) {
      LinkedHashSet var5 = new LinkedHashSet();
      if (super.f.a() && var1.getNodeType() == JsonNodeType.OBJECT) {
         this.a((ObjectNode)var1);
      }

      if (var4) {
         var5.addAll(this.a(var1, var2, var3));
      } else {
         dJ var6 = new dJ(super.d.d().h());

         for (Entry var8 : this.j.entrySet()) {
            this.a(var8, var1, var2, var3, var4, var5, var6);
         }
      }

      return var5;
   }

   private void a(ObjectNode var1) {
      for (Entry var3 : this.j.entrySet()) {
         JsonNode var4 = var1.get((String)var3.getKey());
         JsonNode var5;
         if ((var5 = this.a(var3)) != null && (var4 == null || var4.isNull() && super.f.b())) {
            var1.set((String)var3.getKey(), var5);
         }
      }
   }

   private JsonNode a(Entry<String, cp> var1) {
      return ((cp)var1.getValue()).c().get("default");
   }

   private void a(Entry<String, cp> var1, JsonNode var2, JsonNode var3, String var4, boolean var5, Set<dg> var6, dO var7) {
      cp var8 = (cp)var1.getValue();
      var2 = var2 == null ? null : var2.get((String)var1.getKey());
      if (var7.b(dj.PROPERTIES.a(), var2, var3, var4 + "." + (String)var1.getKey(), var8.b(), var8.c(), var8.d(), super.d, super.d.c())) {
         var6.addAll(var8.a(var2, var3, var4 + "." + (String)var1.getKey(), var5));
      }

      var7.a(dj.PROPERTIES.a(), var2, var3, var4 + "." + (String)var1.getKey(), var8.b(), var8.c(), var8.d(), super.d, super.d.c(), var6);
   }

   public Map<String, cp> h() {
      return this.j;
   }

   @Override
   public void a_() {
      this.a(this.j.values());
   }
}
