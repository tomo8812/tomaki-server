package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import java.util.EnumSet;
import java.util.Iterator;

public final class aj {
   private aj() {
   }

   private static JsonNode a(JsonNode var0, String var1) {
      if ((var0 = var0.get(var1)) == null) {
         throw new ah("Invalid JSON Patch payload (missing '" + var1 + "' field)");
      } else {
         return var0;
      }
   }

   private static JsonNode a(JsonNode var0, String var1, NullNode var2) {
      JsonNode var3;
      return (var3 = var0.get(var1)) == null ? var2 : var3;
   }

   private static void a(JsonNode var0, al var1, EnumSet<aa> var2) {
      if (!var0.isArray()) {
         throw new ah("Invalid JSON Patch payload (not an array)");
      }

      Iterator var7 = var0.iterator();

      while (var7.hasNext()) {
         JsonNode var3;
         if (!(var3 = (JsonNode)var7.next()).isObject()) {
            throw new ah("Invalid JSON Patch payload (not an object)");
         }

         aq var4 = aq.a(a(var3, "op").textValue());
         am var5 = am.a(a(var3, "path").textValue());

         try {
            switch (var4) {
               case REMOVE:
                  var1.a(var5);
                  break;
               case ADD:
                  if (!var2.contains(aa.MISSING_VALUES_AS_NULLS)) {
                     var3 = a(var3, "value");
                  } else {
                     var3 = a(var3, "value", NullNode.getInstance());
                  }

                  var1.b(var5, var3.deepCopy());
                  break;
               case REPLACE:
                  if (!var2.contains(aa.MISSING_VALUES_AS_NULLS)) {
                     var3 = a(var3, "value");
                  } else {
                     var3 = a(var3, "value", NullNode.getInstance());
                  }

                  var1.c(var5, var3.deepCopy());
                  break;
               case MOVE:
                  am var10 = am.a(a(var3, "from").textValue());
                  var1.a(var10, var5);
                  break;
               case COPY:
                  am var9 = am.a(a(var3, "from").textValue());
                  var1.b(var9, var5);
                  break;
               case TEST:
                  if (!var2.contains(aa.MISSING_VALUES_AS_NULLS)) {
                     var3 = a(var3, "value");
                  } else {
                     var3 = a(var3, "value", NullNode.getInstance());
                  }

                  var1.a(var5, var3.deepCopy());
            }
         } catch (an var6) {
            throw new ak(var6.getMessage(), var4, var6.a);
         }
      }
   }

   private static void a(JsonNode var0, EnumSet<aa> var1) {
      a(var0, ap.a, var1);
   }

   private static void a(JsonNode var0) {
      EnumSet var1 = aa.a();
      a(var0, ap.a, var1);
   }

   public static JsonNode a(JsonNode var0, JsonNode var1, EnumSet<aa> var2) {
      ac var3 = new ac(var1, var2);
      a(var0, var3, var2);
      return var3.a;
   }

   public static JsonNode a(JsonNode var0, JsonNode var1) {
      return a(var0, var1, aa.a());
   }

   private static void b(JsonNode var0, JsonNode var1) {
      EnumSet var2 = aa.a();
      var1 = var1;
      var0 = var0;
      af var5 = new af(var1, var2);
      a(var0, var5, var2);
   }

   private static void b(JsonNode var0, JsonNode var1, EnumSet<aa> var2) {
      af var3 = new af(var1, var2);
      a(var0, var3, var2);
   }
}
