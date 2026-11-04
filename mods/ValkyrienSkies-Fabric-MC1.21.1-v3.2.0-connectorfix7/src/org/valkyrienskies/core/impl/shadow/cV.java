package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;

public class cV {
   private static final String a = "$schema";

   public static cU.a a(JsonNode var0) {
      if (!var0.has("$schema")) {
         throw new cq("Schema tag not present");
      } else {
         String var1;
         if ((var1 = cr.a(var0.get("$schema").asText(), true, true)).equals(co.a().e())) {
            return cU.a.V4;
         } else if (var1.equals(co.b().e())) {
            return cU.a.V6;
         } else if (var1.equals(co.c().e())) {
            return cU.a.V7;
         } else if (var1.equals(co.d().e())) {
            return cU.a.V201909;
         } else {
            throw new cq("Unrecognizable schema");
         }
      }
   }
}
