package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class dG implements dO {
   protected dM a(String var1, JsonNode var2, JsonNode var3, String var4, String var5, JsonNode var6, cp var7, df var8, cr var9) {
      return dM.i().c(var4).b(var1).b(var2).a(var7).c(var3).a(var6).a(var5).a(var9).a(var8).a();
   }

   protected boolean a(List<dK> var1, dM var2) {
      boolean var3 = true;
      if (var1 != null) {
         Iterator var5 = var1.iterator();

         while (var5.hasNext()) {
            dN var4 = ((dK)var5.next()).a(var2);
            if (dN.SKIP.equals(var4) || dN.ABORT.equals(var4)) {
               var3 = false;
               if (dN.ABORT.equals(var4)) {
                  break;
               }
            }
         }
      }

      return var3;
   }

   protected void a(List<dK> var1, dM var2, Set<dg> var3) {
      if (var1 != null) {
         Iterator var5 = var1.iterator();

         while (var5.hasNext()) {
            ((dK)var5.next()).a(var2, var3);
         }
      }
   }
}
