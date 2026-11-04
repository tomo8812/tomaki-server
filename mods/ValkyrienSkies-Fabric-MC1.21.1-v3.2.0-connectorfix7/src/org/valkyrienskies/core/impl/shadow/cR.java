package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cR extends bT implements cv {
   private static final Logger i = LoggerFactory.getLogger(cR.class);
   protected cs g;
   private cp j;
   private static final String k = "#";

   public cR(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.REF, var4);
      String var5 = var2.asText();
      this.j = var3;
      this.g = a(var3, var4, var5);
      if (this.g == null) {
         throw new cq(dg.a(dj.REF.a(), bY.a("internal.unresolvedRef", new MessageFormat("{0}: Reference {1} cannot be resolved")), var1, var1, var5));
      }
   }

   static cs a(cp var0, df var1, String var2) {
      String var3 = var2;
      if (!var2.startsWith("#")) {
         String var4;
         int var5;
         if ((var5 = var2.indexOf("#")) > 0) {
            var4 = var2.substring(0, var5);
         } else {
            var4 = var2;
         }

         URI var6;
         if ((var6 = a(var1.a(), var0, var4)) == null) {
            if (var1.b() == null) {
               return null;
            }

            if ((var6 = a(var1.b(), var4)) == null) {
               return null;
            }
         }

         var0 = var1.c().a(var6, var1.d());
         if (var5 < 0) {
            return new cs(var0.j());
         }

         var2 = var2.substring(var5);
      }

      if (var2.equals("#")) {
         return new cs(var0.j());
      }

      JsonNode var8;
      if ((var8 = var0.b(var2)) != null) {
         cs var9;
         if ((var9 = var1.a(var3)) == null) {
            var0 = new cp(var1, var2, var0.i(), var8, var0);
            var9 = new cs(var0);
            var1.a(var3, var9);
         }

         return var9;
      } else {
         return null;
      }
   }

   private static URI a(dx var0, cp var1, String var2) {
      URI var5 = var1.i();

      try {
         if (var5 == null) {
            var4 = var0.a(var2);
         } else {
            var4 = var0.a(var5, var2);
         }
      } catch (IllegalArgumentException var3) {
         var4 = null;
      }

      return var4;
   }

   private static URI a(dD var0, String var1) {
      try {
         var3 = var0.a(var1);
      } catch (IllegalArgumentException var2) {
         var3 = null;
      }

      return var3;
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      Set var4 = new HashSet();
      Object var5 = bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties");
      bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", new ArrayList());

      try {
         this.a(i, (JsonNode)var1, var2, var3);
         this.g.a().h().a(this.j.h().d());
         if (this.g != null) {
            var4 = this.g.a(var1, var2, var3);
         } else {
            var4 = Collections.emptySet();
         }
      } finally {
         if (var4.isEmpty()) {
            List var8;
            (var8 = var5 == null ? new ArrayList() : (List)var5)
               .addAll((List)bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties"));
            bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var8);
         } else {
            bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var5);
         }
      }

      return var4;
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3, boolean var4) {
      Set var5 = new HashSet();
      Object var6 = bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties");
      bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", new ArrayList());

      try {
         this.a(i, (JsonNode)var1, var2, var3);
         this.g.a().h().a(this.j.h().d());
         if (this.g != null) {
            var5 = this.g.a(var1, var2, var3, var4);
         }

         return var5;
      } finally {
         if (var4) {
            if (var5.isEmpty()) {
               List var9;
               (var9 = var6 == null ? new ArrayList() : (List)var6)
                  .addAll((List)bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties"));
               bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var9);
            } else {
               bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var6);
            }
         }
      }
   }

   public cs h() {
      return this.g;
   }

   @Override
   public void a_() {
      this.g.a().o();
   }
}
