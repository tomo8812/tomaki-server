package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class cj implements cw {
   private final dj a;
   private final Map<String, ci> b;
   private final String c = "date";
   private final String d = "date-time";
   private final String e = "uuid";
   private final String f = "email";

   public cj(dj var1, Map<String, ci> var2) {
      this.a = var1;
      this.b = var2;
   }

   Collection<ci> b() {
      return Collections.unmodifiableCollection(this.b.values());
   }

   @Override
   public cv a(String var1, JsonNode var2, cp var3, df var4) {
      ci var5 = null;
      if (var2 != null && var2.isTextual()) {
         String var6 = var2.textValue();
         if ((var5 = this.b.get(var6)) != null) {
            return new ck(var1, var2, var3, var4, var5);
         }

         if (var6.equals("date") || var6.equals("date-time")) {
            return new bZ(var1, var2, var3, var4, var6);
         }

         if (var6.equals("uuid")) {
            return new db(var1, var2, var3, var4, var6);
         }

         if (var6.equals("email")) {
            return new dr(var1, var2, var3, var4, var6);
         }
      }

      return new ck(var1, var2, var3, var4, var5);
   }

   @Override
   public String a() {
      return this.a.a();
   }

   @Override
   public void a(String var1) {
      this.a.a(var1);
   }
}
