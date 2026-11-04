package org.millenaire.content.validate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public final class Json {
   private Json() {
   }

   public static String string(JsonObject obj, String key) {
      if (obj != null && key != null) {
         JsonElement e = obj.get(key);
         return e != null && e.isJsonPrimitive() ? e.getAsString() : null;
      } else {
         return null;
      }
   }

   public static String string(JsonObject obj, String key, String fallback) {
      String s = string(obj, key);
      return s == null ? fallback : s;
   }

   public static boolean bool(JsonObject obj, String key, boolean fallback) {
      if (obj == null) {
         return fallback;
      }

      JsonElement e = obj.get(key);
      return e != null && e.isJsonPrimitive() && e.getAsJsonPrimitive().isBoolean() ? e.getAsBoolean() : fallback;
   }

   public static Integer integer(JsonObject obj, String key) {
      if (obj == null) {
         return null;
      }

      JsonElement e = obj.get(key);
      return e != null && e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber() ? e.getAsInt() : null;
   }

   public static int integer(JsonObject obj, String key, int fallback) {
      Integer i = integer(obj, key);
      return i == null ? fallback : i;
   }

   public static JsonObject object(JsonObject obj, String key) {
      if (obj == null) {
         return null;
      }

      JsonElement e = obj.get(key);
      return e != null && e.isJsonObject() ? e.getAsJsonObject() : null;
   }

   public static JsonArray array(JsonObject obj, String key) {
      if (obj == null) {
         return null;
      }

      JsonElement e = obj.get(key);
      return e != null && e.isJsonArray() ? e.getAsJsonArray() : null;
   }

   public static List<String> strings(JsonObject obj, String key) {
      List<String> out = new ArrayList<>();
      JsonArray arr = array(obj, key);
      if (arr == null) {
         return out;
      }

      for (JsonElement e : arr) {
         if (e.isJsonPrimitive()) {
            out.add(e.getAsString());
         }
      }

      return out;
   }

   public static List<String> stringOrArray(JsonObject obj, String key) {
      if (obj == null) {
         return List.of();
      } else {
         JsonElement e = obj.get(key);
         if (e == null || e.isJsonNull()) {
            return List.of();
         } else if (e.isJsonArray()) {
            return strings(obj, key);
         } else {
            return e.isJsonPrimitive() ? List.of(e.getAsString()) : List.of();
         }
      }
   }

   public static List<JsonObject> objects(JsonObject obj, String key) {
      List<JsonObject> out = new ArrayList<>();
      JsonArray arr = array(obj, key);
      if (arr == null) {
         return out;
      }

      for (JsonElement e : arr) {
         if (e.isJsonObject()) {
            out.add(e.getAsJsonObject());
         }
      }

      return out;
   }

   public static List<String> keys(JsonObject obj, String key) {
      JsonObject o = object(obj, key);
      return o == null ? List.of() : new ArrayList<>(o.keySet());
   }
}
