package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Set;

public class dJ extends dG {
   private List<dK> a;

   public dJ(List<dK> var1) {
      this.a = var1;
   }

   @Override
   public boolean b(String var1, JsonNode var2, JsonNode var3, String var4, String var5, JsonNode var6, cp var7, df var8, cr var9) {
      dM var10 = this.a(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      return this.a(this.a, var10);
   }

   @Override
   public void a(String var1, JsonNode var2, JsonNode var3, String var4, String var5, JsonNode var6, cp var7, df var8, cr var9, Set<dg> var10) {
      dM var11 = this.a(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      this.a(this.a, var11, var10);
   }
}
