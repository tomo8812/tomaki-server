package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.EnumSet;

class af implements al {
   JsonNode a;
   private EnumSet<aa> b;

   private af(JsonNode var1) {
      this(var1, aa.a());
   }

   af(JsonNode var1, EnumSet<aa> var2) {
      this.a = var1;
      this.b = var2;
   }

   public final JsonNode a() {
      return this.a;
   }

   @Override
   public final void a(am var1, am var2) {
      JsonNode var3 = var1.a(this.a);
      this.a(var1);
      this.a(var2, var3, aq.MOVE);
   }

   @Override
   public final void b(am var1, am var2) {
      JsonNode var3;
      JsonNode var4 = (var3 = var1.a(this.a)) != null ? var3.deepCopy() : null;
      this.a(var2, var4, aq.COPY);
   }

   private static String a(JsonNode var0) {
      if (var0 == null || var0.isNull()) {
         return "null";
      } else if (var0.isArray()) {
         return "array";
      } else {
         return var0.isObject() ? "object" : "value " + var0.toString();
      }
   }

   @Override
   public final void a(am var1, JsonNode var2) {
      JsonNode var3;
      if (!(var3 = var1.a(this.a)).equals(var2)) {
         throw new ak("Expected " + a(var2) + " but found " + a(var3), aq.TEST, var1);
      }
   }

   @Override
   public final void b(am var1, JsonNode var2) {
      this.a(var1, var2, aq.ADD);
   }

   @Override
   public final void c(am var1, JsonNode var2) {
      if (var1.a()) {
         this.a = var2;
      } else {
         JsonNode var3 = var1.e().a(this.a);
         am.a var4 = var1.d();
         if (var3.isObject()) {
            if (!this.b.contains(aa.ALLOW_MISSING_TARGET_OBJECT_ON_REPLACE) && !var3.has(var4.a)) {
               throw new ak("Missing field \"" + var4.a + "\"", aq.REPLACE, var1.e());
            }

            ((ObjectNode)var3).replace(var4.a, var2);
         } else if (var3.isArray()) {
            if (var4.b() >= var3.size()) {
               throw new ak("Array index " + var4.b() + " out of bounds", aq.REPLACE, var1.e());
            }

            ((ArrayNode)var3).set(var4.b(), var2);
         } else {
            throw new ak("Can't reference past scalar value", aq.REPLACE, var1.e());
         }
      }
   }

   @Override
   public final void a(am var1) {
      if (var1.a()) {
         throw new ak("Cannot remove document root", aq.REMOVE, var1);
      }

      JsonNode var2 = var1.e().a(this.a);
      am.a var3 = var1.d();
      if (var2.isObject()) {
         ((ObjectNode)var2).remove(var3.a);
      } else if (var2.isArray()) {
         if (!this.b.contains(aa.REMOVE_NONE_EXISTING_ARRAY_ELEMENT) && var3.b() >= var2.size()) {
            throw new ak("Array index " + var3.b() + " out of bounds", aq.REPLACE, var1.e());
         }

         ((ArrayNode)var2).remove(var3.b());
      } else {
         throw new ak("Cannot reference past scalar value", aq.REPLACE, var1.e());
      }
   }

   private void a(am var1, JsonNode var2, aq var3) {
      if (var1.a()) {
         this.a = var2;
      } else {
         JsonNode var4;
         if (!(var4 = var1.e().a(this.a)).isContainerNode()) {
            throw new ak("Cannot reference past scalar value", var3, var1.e());
         }

         if (var4.isArray()) {
            b(var1, var2, var4);
         } else {
            a(var1, var4, var2);
         }
      }
   }

   private static void a(am var0, JsonNode var1, JsonNode var2) {
      var1 = var1;
      String var3 = var0.d().a;
      var1.set(var3, var2);
   }

   private static void b(am var0, JsonNode var1, JsonNode var2) {
      var2 = var2;
      int var3;
      if ((var3 = var0.d().b()) == Integer.MIN_VALUE) {
         var2.add(var1);
      } else {
         if (var3 > var2.size()) {
            throw new ak("Array index " + var3 + " out of bounds", aq.ADD, var0.e());
         }

         var2.insert(var3, var1);
      }
   }
}
