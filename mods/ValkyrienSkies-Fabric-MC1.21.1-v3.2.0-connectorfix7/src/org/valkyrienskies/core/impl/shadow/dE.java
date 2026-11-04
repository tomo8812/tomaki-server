package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.Iterator;

public class dE {
   private static final String a = "type";
   private static final String b = "enum";
   private static final String c = "$ref";
   private static final String d = "nullable";

   public static boolean a(JsonNode var0) {
      JsonNode var1;
      return (var1 = var0.get("nullable")) != null && var1.asBoolean();
   }

   public static boolean a(JsonNode var0, cT var1) {
      return var1.e() ? a(var0) : false;
   }

   public static boolean a(ArrayNode var0, cT var1) {
      Iterator var2 = var0.elements();

      while (var2.hasNext()) {
         if (a((JsonNode)var2.next(), var1)) {
            return true;
         }
      }

      return false;
   }

   public static boolean a(JsonNode var0, cu var1) {
      Iterator var3 = var0.elements();

      while (var3.hasNext()) {
         JsonNode var2;
         if ((var2 = ((JsonNode)var3.next()).get("type")) != null && var2.asText().equals(var1.toString())) {
            return true;
         }
      }

      return false;
   }

   public static boolean a(JsonNode var0, cp var1, cT var2) {
      cu var3 = a(var1);
      return a(var0, var3, var1.d(), var2);
   }

   public static cu a(cp var0) {
      JsonNode var1;
      return (var1 = var0.c().get("type")) != null ? cu.valueOf(var1.asText().toUpperCase()) : cu.UNKNOWN;
   }

   public static boolean a(JsonNode var0, cu var1, cp var2, cT var3) {
      cu var4;
      if ((var4 = cZ.a(var0, var3)) == var1) {
         return true;
      }

      if (var1 == cu.ANY) {
         return true;
      }

      if (var1 == cu.NUMBER && var4 == cu.INTEGER) {
         return true;
      }

      if (!cu.NULL.equals(var4) || var2 == null || (var2.d() == null || !a(var2.d().c(), var3)) && !a(var2.c())) {
         if (b(var2)) {
            return true;
         }

         if (var3 != null && var3.a()) {
            if (var1 == cu.ARRAY) {
               return true;
            }

            if (var4 == cu.STRING) {
               if (var1 == cu.INTEGER) {
                  if (dF.a(var0.textValue())) {
                     return true;
                  }
               } else if (var1 == cu.BOOLEAN) {
                  if (dF.b(var0.textValue())) {
                     return true;
                  }
               } else if (var1 == cu.NUMBER && dF.c(var0.textValue())) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public static boolean b(JsonNode var0, cT var1) {
      if (var0.isNumber()) {
         return true;
      } else {
         return var1.a() && cZ.a(var0, var1) == cu.STRING ? dF.c(var0.textValue()) : false;
      }
   }

   private static boolean b(cp var0) {
      JsonNode var1 = null;
      JsonNode var2 = null;
      JsonNode var3 = null;
      if (var0 != null) {
         if (var0.c() != null) {
            var1 = var0.c().get("type");
            var2 = var0.c().get("enum");
         }

         if (var0.d() != null && var0.d().c() != null) {
            var3 = var0.d().c().get("$ref");
         }
      }

      return var1 != null && var2 != null && var3 != null ? cZ.a(var1) == cu.OBJECT && var2.isArray() : false;
   }
}
