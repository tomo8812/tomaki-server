package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public abstract class bN implements cv {
   private final String a;

   protected bN(String var1) {
      this.a = var1;
   }

   @Override
   public Set<dg> a(JsonNode var1) {
      return this.a(var1, var1, "$");
   }

   protected dg a(ce var1, String var2, String... var3) {
      return dg.a(this.a, var1, var2, null, var3);
   }

   protected dg a(ce var1, String var2, Map<String, Object> var3) {
      return dg.a(this.a, var1, var2, null, var3);
   }

   protected Set<dg> a() {
      return Collections.emptySet();
   }

   protected Set<dg> b(ce var1, String var2, Map<String, Object> var3) {
      return Collections.singleton(this.a(var1, var2, var3));
   }

   protected Set<dg> b(ce var1, String var2, String... var3) {
      return Collections.singleton(this.a(var1, var2, var3));
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3, boolean var4) {
      Set var5 = Collections.emptySet();
      if (var4) {
         var5 = this.a(var1, var2, var3);
      }

      return var5;
   }
}
