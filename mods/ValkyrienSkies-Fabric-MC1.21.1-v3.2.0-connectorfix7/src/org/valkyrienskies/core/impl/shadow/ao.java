package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.EnumMap;
import java.util.Map;

enum ao {
   ARRAY("array"),
   BOOLEAN("boolean"),
   INTEGER("integer"),
   NULL("null"),
   NUMBER("number"),
   OBJECT("object"),
   STRING("string");

   private final String name;
   private static final Map<JsonToken, ao> TOKEN_MAP;

   ao(String var3) {
      this.name = var3;
   }

   @Override
   public final String toString() {
      return this.name;
   }

   public static ao a(JsonNode var0) {
      JsonToken var2 = var0.asToken();
      ao var1;
      if ((var1 = TOKEN_MAP.get(var2)) == null) {
         throw new NullPointerException("unhandled token type " + var2);
      } else {
         return var1;
      }
   }

   static {
      (TOKEN_MAP = new EnumMap<>(JsonToken.class)).put(JsonToken.START_ARRAY, ARRAY);
      TOKEN_MAP.put(JsonToken.VALUE_TRUE, BOOLEAN);
      TOKEN_MAP.put(JsonToken.VALUE_FALSE, BOOLEAN);
      TOKEN_MAP.put(JsonToken.VALUE_NUMBER_INT, INTEGER);
      TOKEN_MAP.put(JsonToken.VALUE_NUMBER_FLOAT, NUMBER);
      TOKEN_MAP.put(JsonToken.VALUE_NULL, NULL);
      TOKEN_MAP.put(JsonToken.START_OBJECT, OBJECT);
      TOKEN_MAP.put(JsonToken.VALUE_STRING, STRING);
   }
}
