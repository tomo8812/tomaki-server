package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.PatternSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ck extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(ck.class);
   private final ci i;

   public ck(String var1, JsonNode var2, cp var3, df var4, ci var5) {
      super(var1, var2, var3, dj.FORMAT, var4);
      this.i = var5;
      super.d = var4;
      this.a(this.e().d());
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      LinkedHashSet var6 = new LinkedHashSet();
      if (cZ.a(var1, super.d.d()) != cu.STRING) {
         return var6;
      }

      if (this.i != null) {
         if (this.i.a().equals("ipv6")) {
            if (!var1.textValue().trim().equals(var1.textValue())) {
               var6.add(this.a(var3, this.i.a(), this.i.b()));
            } else if (var1.textValue().contains("%")) {
               var6.add(this.a(var3, this.i.a(), this.i.b()));
            }
         }

         try {
            if (!this.i.a(var1.textValue())) {
               var6.add(this.a(var3, this.i.a(), this.i.b()));
            }
         } catch (PatternSyntaxException var5) {
            g.error("Failed to apply pattern on " + var3 + ": Invalid RE syntax [" + this.i.a() + "]", var5);
         }
      }

      return Collections.unmodifiableSet(var6);
   }
}
