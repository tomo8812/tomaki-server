package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class bP extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(bP.class);
   private final boolean i;
   private final cp j;
   private final Set<String> k;
   private final List<Pattern> l = new ArrayList<>();

   public bP(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.ADDITIONAL_PROPERTIES, var4);
      if (var2.isBoolean()) {
         this.i = var2.booleanValue();
         this.j = null;
      } else if (var2.isObject()) {
         this.i = true;
         this.j = new cp(var4, this.e().a(), var3.i(), var2, var3);
      } else {
         this.i = false;
         this.j = null;
      }

      this.k = new HashSet<>();
      JsonNode var5;
      if ((var5 = var3.c().get("properties")) != null) {
         Iterator var6 = var5.fieldNames();

         while (var6.hasNext()) {
            this.k.add((String)var6.next());
         }
      }

      JsonNode var7;
      if ((var7 = var3.c().get("patternProperties")) != null) {
         Iterator var8 = var7.fieldNames();

         while (var8.hasNext()) {
            this.l.add(Pattern.compile((String)var8.next()));
         }
      }

      this.a(this.e().d());
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
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      if (g.isDebugEnabled()) {
         this.a(g, (JsonNode)var1, var2, var3);
      }

      LinkedHashSet var4 = new LinkedHashSet();
      if (!var1.isObject()) {
         return var4;
      }

      if (this.i) {
         Iterator var5 = var1.fieldNames();

         while (var5.hasNext()) {
            this.b(var3 + "." + (String)var5.next());
         }
      }

      Iterator var10 = var1.fieldNames();

      while (var10.hasNext()) {
         String var6;
         if (!(var6 = (String)var10.next()).startsWith("#")) {
            boolean var7 = false;
            Iterator var8 = this.l.iterator();

            while (var8.hasNext()) {
               if (((Pattern)var8.next()).matcher(var6).find()) {
                  var7 = true;
                  break;
               }
            }

            if (!this.k.contains(var6) && !var7) {
               if (!this.i) {
                  var4.add(this.a(var3, var6));
               } else if (this.j != null) {
                  di var11;
                  if ((var11 = (di)bV.a().a("com.networknt.schema.ValidatorState")) != null && var11.c()) {
                     var4.addAll(this.j.a(var1.get(var6), var2, var3 + "." + var6, var11.d()));
                  } else {
                     var4.addAll(this.j.a(var1.get(var6), var2, var3 + "." + var6));
                  }
               }
            }
         }
      }

      return Collections.unmodifiableSet(var4);
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3, boolean var4) {
      if (var4) {
         return this.a(var1, var2, var3);
      }

      if (var1 != null && var1.isObject()) {
         Iterator var9 = var1.fieldNames();

         while (var9.hasNext()) {
            String var5;
            if (!(var5 = (String)var9.next()).startsWith("#")) {
               boolean var6 = false;
               Iterator var7 = this.l.iterator();

               while (var7.hasNext()) {
                  if (((Pattern)var7.next()).matcher(var5).find()) {
                     var6 = true;
                     break;
                  }
               }

               di var10;
               if (!this.k.contains(var5)
                  && !var6
                  && this.i
                  && this.j != null
                  && (var10 = (di)bV.a().a("com.networknt.schema.ValidatorState")) != null
                  && var10.c()) {
                  this.j.a(var1.get(var5), var2, var3 + "." + var5, var10.d());
               }
            }
         }

         return Collections.emptySet();
      } else {
         return Collections.emptySet();
      }
   }

   @Override
   public void a_() {
      if (this.j != null) {
         this.j.o();
      }
   }
}
