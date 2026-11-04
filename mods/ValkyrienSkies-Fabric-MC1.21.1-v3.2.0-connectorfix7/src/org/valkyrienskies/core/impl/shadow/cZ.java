package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;

public class cZ {
   public static cu a(JsonNode var0) {
      if (var0.isTextual()) {
         String var1 = var0.textValue();
         if ("object".equals(var1)) {
            return cu.OBJECT;
         }

         if ("array".equals(var1)) {
            return cu.ARRAY;
         }

         if ("string".equals(var1)) {
            return cu.STRING;
         }

         if ("number".equals(var1)) {
            return cu.NUMBER;
         }

         if ("integer".equals(var1)) {
            return cu.INTEGER;
         }

         if ("boolean".equals(var1)) {
            return cu.BOOLEAN;
         }

         if ("any".equals(var1)) {
            return cu.ANY;
         }

         if ("null".equals(var1)) {
            return cu.NULL;
         }
      }

      return var0.isArray() ? cu.UNION : cu.UNKNOWN;
   }

   public static cu a(JsonNode var0, cT var1) {
      if (var0.isContainerNode()) {
         if (var0.isObject()) {
            return cu.OBJECT;
         } else {
            return var0.isArray() ? cu.ARRAY : cu.UNKNOWN;
         }
      } else if (var0.isValueNode()) {
         if (var0.isTextual()) {
            return cu.STRING;
         }

         if (var0.isIntegralNumber()) {
            return cu.INTEGER;
         }

         if (var0.isNumber()) {
            if (var1 != null && var1.g() && var0.canConvertToExactIntegral()) {
               return cu.INTEGER;
            } else {
               return var1 != null && var1.l() && var0.canConvertToExactIntegral() ? cu.INTEGER : cu.NUMBER;
            }
         } else if (var0.isBoolean()) {
            return cu.BOOLEAN;
         } else {
            return var0.isNull() ? cu.NULL : cu.UNKNOWN;
         }
      } else {
         return cu.UNKNOWN;
      }
   }
}
