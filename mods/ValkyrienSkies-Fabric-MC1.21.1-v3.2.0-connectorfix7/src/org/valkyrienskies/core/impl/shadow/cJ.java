package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cJ extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cJ.class);
   private final List<cJ.a> i = new ArrayList<>();

   public cJ(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.ONE_OF, var4);
      int var8 = var2.size();

      for (int var5 = 0; var5 < var8; var5++) {
         JsonNode var6 = var2.get(var5);
         cp var7 = new cp(var4, this.e().a(), var3.i(), var6, var3);
         this.i.add(new cJ.a(var6, var3, var4, var7));
      }

      this.a(this.e().d());
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      LinkedHashSet var4 = new LinkedHashSet();
      Object var5 = bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties");
      bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", new ArrayList());

      try {
         this.a(g, (JsonNode)var1, var2, var3);
         di var6;
         (var6 = (di)bV.a().a("com.networknt.schema.ValidatorState")).b(true);
         int var7 = 0;
         Set var8 = new LinkedHashSet();

         for (cJ.a var10 : this.i) {
            var6.a(true);
            cp var16 = var10.a;
            Set var11;
            if (!var6.c()) {
               var11 = var16.a(var1, var2, var3);
            } else {
               var11 = var16.a(var1, var2, var3, var6.d());
            }

            if (var11.isEmpty()) {
               if (!var6.a()) {
                  continue;
               }

               var7++;
            }

            if (var7 > 1) {
               bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", new ArrayList());
               break;
            }

            var8.addAll(var11);
         }

         Set var15 = var8.stream().filter(var0 -> !dj.REQUIRED.a().equals(var0.g())).collect(Collectors.toSet());
         if (var7 > 1) {
            dg var17 = this.b(var3);
            if (super.e) {
               throw new cq(var17);
            }

            var4.add(var17);
         } else if (var7 <= 0) {
            if (!var15.isEmpty()) {
               var8 = var15;
            }

            if (!var8.isEmpty()) {
               LinkedHashSet var18;
               if (var8.size() > 1
                  && (var18 = new LinkedHashSet(
                           var8.stream()
                              .filter(var0 -> !dj.ADDITIONAL_PROPERTIES.a().equals(var0.g()))
                              .sorted((var0, var1x) -> a(var0, var1x))
                              .collect(Collectors.toList())
                        ))
                        .size()
                     > 0) {
                  var8 = var18;
               }

               var4.addAll(var8);
            }

            if (super.e) {
               throw new cq(var4.toString());
            }
         }

         if (var4.isEmpty()) {
            var6.a(true);
         }

         this.i();
         return Collections.unmodifiableSet(var4);
      } finally {
         if (var4.isEmpty()) {
            List var14;
            (var14 = var5 == null ? new ArrayList() : (List)var5)
               .addAll((List)bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties"));
            bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var14);
         } else {
            bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var5);
         }
      }
   }

   private static int a(dg var0, dg var1) {
      List var2;
      int var3 = (var2 = Arrays.asList(
            dj.TYPE.a(),
            dj.DATETIME.a(),
            dj.UUID.a(),
            dj.ID.a(),
            dj.EXCLUSIVE_MAXIMUM.a(),
            dj.EXCLUSIVE_MINIMUM.a(),
            dj.TRUE.a(),
            dj.FALSE.a(),
            dj.CONST.a(),
            dj.CONTAINS.a(),
            dj.PROPERTYNAMES.a()
         ))
         .indexOf(var0.g());
      int var4 = var2.indexOf(var1.g());
      if (var3 >= 0) {
         return var4 >= 0 ? Integer.compare(var3, var4) : -1;
      } else {
         return var4 >= 0 ? 1 : var0.a().compareTo(var1.a());
      }
   }

   private void i() {
      di var1;
      (var1 = (di)bV.a().a("com.networknt.schema.ValidatorState")).b(false);
      var1.a(true);
   }

   public List<cp> h() {
      ArrayList var1 = new ArrayList();

      for (cJ.a var3 : this.i) {
         var1.add(var3.a());
      }

      return var1;
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3, boolean var4) {
      LinkedHashSet var5 = new LinkedHashSet();
      if (var4) {
         var5.addAll(this.a(var1, var2, var3));
      } else {
         Iterator var6 = this.i.iterator();

         while (var6.hasNext()) {
            ((cJ.a)var6.next()).a.a(var1, var2, var3, var4);
         }
      }

      return var5;
   }

   private dg b(String var1) {
      String var2 = "";
      Iterator var3 = this.i.iterator();

      while (var3.hasNext()) {
         String var4 = ((cJ.a)var3.next()).a().c().toString();
         var2 = var2.concat(var4);
      }

      return dg.a(this.e().a(), dj.ONE_OF, var1, super.a, var2);
   }

   @Override
   public void a_() {
      Iterator var1 = this.i.iterator();

      while (var1.hasNext()) {
         ((cJ.a)var1.next()).a().o();
      }
   }

   static class a {
      private final cp a;
      private final Map<String, String> b;

      a(JsonNode var1, cp var2, df var3, cp var4) {
         JsonNode var5;
         var2 = (var5 = var1.get(dj.REF.a())) != null && var5.isTextual() ? cR.a(var2, var3, var5.textValue()).a() : null;
         this.b = this.a(var1, var2);
         this.a = var4;
      }

      private Map<String, String> a(JsonNode var1, cp var2) {
         Map var5 = var2 != null ? this.b(var2.c()) : Collections.emptyMap();
         Map var4 = this.b(var1);
         if (var5.isEmpty()) {
            return var4;
         }

         if (var4.isEmpty()) {
            return var5;
         }

         HashMap var3;
         (var3 = new HashMap()).putAll(var4);
         var3.putAll(var5);
         return var3;
      }

      private Map<String, String> b(JsonNode var1) {
         HashMap var2 = new HashMap();
         if (!var1.isObject()) {
            return var2;
         }

         if ((var1 = var1.get("properties")) != null && var1.isObject()) {
            Iterator var3 = var1.fieldNames();

            while (var3.hasNext()) {
               String var4 = (String)var3.next();
               JsonNode var5 = var1.get(var4);
               String var7;
               if ((var7 = this.c(var5)) != null && !var7.isEmpty()) {
                  var2.put(var4, var7);
               }
            }

            return var2;
         } else {
            return var2;
         }
      }

      private String c(JsonNode var1) {
         if (var1 != null && var1.isObject() && var1.has("enum")) {
            if ((var1 = var1.get("enum")) != null && var1.isArray()) {
               if (var1.size() != 1) {
                  return null;
               }

               JsonNode var3;
               return (var3 = var1.get(0)) != null && var3.isTextual() ? var3.textValue() : null;
            } else {
               return null;
            }
         } else {
            return null;
         }
      }

      public boolean a(JsonNode var1) {
         for (Entry var3 : this.b.entrySet()) {
            JsonNode var4;
            if ((var4 = var1.get((String)var3.getKey())) != null && var4.isTextual() && !((String)var3.getValue()).equals(var4.textValue())) {
               return false;
            }
         }

         return true;
      }

      private cp a() {
         return this.a;
      }
   }
}
