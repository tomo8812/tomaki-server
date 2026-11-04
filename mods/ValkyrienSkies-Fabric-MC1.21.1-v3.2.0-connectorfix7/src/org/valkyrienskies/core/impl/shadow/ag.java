package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

final class ag {
   static List<JsonNode> a(ArrayNode var0) {
      int var1 = var0.size();
      ArrayList var2 = new ArrayList(var1);

      for (int var3 = 0; var3 < var1; var3++) {
         var2.add(var0.get(var3));
      }

      return var2;
   }

   private static List<JsonNode> a(List<JsonNode> var0, List<JsonNode> var1) {
      if (var0 != null && var1 != null) {
         LinkedList var2 = new LinkedList();
         int var3 = var0.size();
         int var4 = var1.size();
         int[][] var5 = new int[var3 + 1][var4 + 1];

         for (int var6 = 1; var6 <= var3; var6++) {
            for (int var7 = 1; var7 <= var4; var7++) {
               if (var6 != 0 && var7 != 0) {
                  if (((JsonNode)var0.get(var6 - 1)).equals(var1.get(var7 - 1))) {
                     var5[var6][var7] = var5[var6 - 1][var7 - 1] + 1;
                  } else {
                     var5[var6][var7] = Math.max(var5[var6][var7 - 1], var5[var6 - 1][var7]);
                  }
               } else {
                  var5[var6][var7] = 0;
               }
            }
         }

         int var8 = var3;
         int var9 = var4;

         while (var8 > 0 && var9 > 0) {
            if (((JsonNode)var0.get(var8 - 1)).equals(var1.get(var9 - 1))) {
               var2.add(var0.get(var8 - 1));
               var8--;
               var9--;
            } else if (var5[var8 - 1][var9] > var5[var8][var9 - 1]) {
               var8--;
            } else {
               var9--;
            }
         }

         Collections.reverse(var2);
         return var2;
      } else {
         throw new NullPointerException("List must not be null for longestCommonSubsequence");
      }
   }
}
