package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class db extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(db.class);
   private final String i;
   private static final String j = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

   public db(String var1, JsonNode var2, cp var3, df var4, String var5) {
      super(var1, var2, var3, dj.UUID, var4);
      this.i = var5;
      this.a(this.e().d());
      super.d = var4;
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

   public boolean b(String var1) {
      return var1.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
   }
}
