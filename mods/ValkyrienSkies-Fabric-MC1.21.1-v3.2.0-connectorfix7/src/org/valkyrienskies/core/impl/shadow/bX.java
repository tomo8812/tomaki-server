package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class bX extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(bX.class);
   private final cp i;

   public bX(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.CONTAINS, var4);
      if (!var2.isObject() && !var2.isBoolean()) {
         this.i = null;
      } else {
         this.i = new cp(var4, this.e().a(), var3.i(), var2, var3);
      }

      this.a(this.e().d());
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      if (!var1.isArray()) {
         return Collections.emptySet();
      }

      if (var1.size() == 0) {
         return this.b(var3);
      }

      if (var1.isArray()) {
         int var4 = 0;

         for (JsonNode var5 : var1) {
            if (this.i.a(var5, var2, var3 + "[" + var4 + "]").isEmpty()) {
               return Collections.emptySet();
            }

            var4++;
         }

         return this.b(var3);
      } else {
         return Collections.emptySet();
      }
   }

   private Set<dg> b(String var1) {
      return Collections.singleton(this.a(var1, this.i.c().toString()));
   }

   @Override
   public void a_() {
      if (this.i != null) {
         this.i.o();
      }
   }
}
