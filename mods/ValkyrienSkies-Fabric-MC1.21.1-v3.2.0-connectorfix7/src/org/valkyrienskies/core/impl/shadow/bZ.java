package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class bZ extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(bZ.class);
   private final String i;
   private final String j = "date";
   private final String k = "date-time";

   public bZ(String var1, JsonNode var2, cp var3, df var4, String var5) {
      super(var1, var2, var3, dj.DATETIME, var4);
      this.i = var5;
      super.d = var4;
      this.a(this.e().d());
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      LinkedHashSet var5 = new LinkedHashSet();
      if (cZ.a(var1, super.d.d()) != cu.STRING) {
         return var5;
      }

      if (!this.b(var1.textValue())) {
         var5.add(this.a(var3, var1.textValue(), this.i));
      }

      return Collections.unmodifiableSet(var5);
   }

   private boolean b(String var1) {
      if (this.i.equals("date")) {
         return this.a(() -> LocalDate.parse(var1));
      } else if (this.i.equals("date-time")) {
         return this.a(() -> {
            try {
               org.valkyrienskies.core.impl.shadow.c.a(var1);
            } catch (d var2) {
               if (!var2.a) {
                  throw var2;
               }
            }
         });
      } else {
         throw new IllegalStateException("Unknown format: " + this.i);
      }
   }

   private boolean a(Runnable var1) {
      try {
         var1.run();
         return true;
      } catch (Exception var2) {
         g.error("Invalid " + this.i + ": " + var2.getMessage());
         return false;
      }
   }
}
