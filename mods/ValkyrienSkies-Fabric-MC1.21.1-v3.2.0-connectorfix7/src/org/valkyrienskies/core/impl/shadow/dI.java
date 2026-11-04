package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class dI extends dG {
   private Map<String, List<dK>> a;

   public dI(Map<String, List<dK>> var1) {
      this.a = var1;
   }

   protected String a(String var1) {
      return var1.substring(var1.lastIndexOf(47) + 1);
   }

   @Override
   public boolean b(String var1, JsonNode var2, JsonNode var3, String var4, String var5, JsonNode var6, cp var7, df var8, cr var9) {
      var1 = this.a(var1);
      dM var13 = this.a(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      List var11 = this.a.get(var1);
      boolean var12;
      if (var12 = this.a(var11, var13)) {
         List var14 = this.a.get("com.networknt.AllKeywordWalkListener");
         this.a(var14, var13);
      }

      return var12;
   }

   @Override
   public void a(String var1, JsonNode var2, JsonNode var3, String var4, String var5, JsonNode var6, cp var7, df var8, cr var9, Set<dg> var10) {
      var1 = this.a(var1);
      dM var14 = this.a(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      List var12 = this.a.get(var1);
      this.a(var12, var14, var10);
      List var13 = this.a.get("com.networknt.AllKeywordWalkListener");
      this.a(var13, var14, var10);
   }
}
