package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class bR extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cS.class);
   private static final String i = "Remaining validation messages report why candidate schemas didn't match";
   private static final String j = "and the discriminator-selected candidate schema didn't pass validation";
   private final List<cp> k = new ArrayList<>();
   private final df.a l;

   public bR(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.ANY_OF, var4);
      super.d = var4;
      int var6 = var2.size();

      for (int var5 = 0; var5 < var6; var5++) {
         this.k.add(new cp(var4, this.e().a(), var3.i(), var2.get(var5), var3));
      }

      if (super.d.d().m()) {
         this.l = new df.a();
      } else {
         this.l = null;
      }
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      di var4 = (di)bV.a().a("com.networknt.schema.ValidatorState");
      if (super.d.d().m()) {
         super.d.a(this.l, var3);
      }

      Set var5 = new LinkedHashSet();
      String var6 = "anyOf/type";
      Object var7 = bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties");
      bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", new ArrayList());

      try {
         int var8 = 0;

         for (cp var10 : this.k) {
            new HashSet();
            da var11;
            if (var10.n().containsKey(var6) && (var11 = (da)var10.n().get(var6)).h() != cu.UNION && !var11.b(var1)) {
               var5.add(this.a(var3, var11.h().toString()));
            } else {
               Set var15;
               if (!var4.c()) {
                  var15 = var10.a(var1, var2, var3);
               } else {
                  var15 = var10.a(var1, var2, var3, true);
               }

               if (var15.isEmpty()) {
                  if (!var4.a()) {
                     continue;
                  }

                  var8++;
               }

               if (var15.isEmpty() && !super.d.d().m()) {
                  var5.clear();
                  return var15;
               }

               if (super.d.d().m() && this.l.b()) {
                  if (!var15.isEmpty()) {
                     var15.add(this.a(var3, "and the discriminator-selected candidate schema didn't pass validation"));
                     var5.addAll(var15);
                  } else {
                     var5.clear();
                  }

                  return var15;
               }

               var5.addAll(var15);
            }
         }

         Set var14 = var5.stream().filter(var0 -> !dj.REQUIRED.a().equals(var0.g())).collect(Collectors.toSet());
         if (var8 > 0 && var14.isEmpty()) {
            var5 = var14;
         }

         if (super.d.d().m() && this.l.c()) {
            HashSet var16;
            (var16 = new HashSet())
               .add(this.a(var3, "based on the provided discriminator. No alternative could be chosen based on the discriminator property"));
            return Collections.unmodifiableSet(var16);
         }
      } finally {
         if (super.d.d().m()) {
            super.d.b(var3);
         }

         if (var5.isEmpty()) {
            this.a(var7);
         } else {
            bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var7);
         }
      }

      return Collections.unmodifiableSet(var5);
   }

   private void a(Object var1) {
      List var2;
      (var2 = var1 == null ? new ArrayList() : (List)var1).addAll((List)bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties"));
      bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var2);
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3, boolean var4) {
      if (var4) {
         return this.a(var1, var2, var3);
      }

      Iterator var6 = this.k.iterator();

      while (var6.hasNext()) {
         ((cp)var6.next()).a(var1, var2, var3, false);
      }

      return new LinkedHashSet<>();
   }

   @Override
   public void a_() {
      this.a(this.k);
   }
}
