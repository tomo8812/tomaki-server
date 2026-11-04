package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cP extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cP.class);
   private final cp i;

   public cP(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.PROPERTYNAMES, var4);
      this.i = new cp(var4, var1, var3.i(), var2, var3);
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      LinkedHashSet var9 = new LinkedHashSet();
      Iterator var4 = var1.fieldNames();

      while (var4.hasNext()) {
         String var5;
         TextNode var6 = TextNode.valueOf(var5 = (String)var4.next());
         Iterator var10 = this.i.a(var6, var1, var3 + "." + var5).iterator();

         while (var10.hasNext()) {
            dg var11;
            String var7 = (var11 = (dg)var10.next()).b();
            String var8;
            if ((var8 = var11.f()).startsWith(var7)) {
               var8 = var8.substring(var7.length()).replaceFirst("^:\\s*", "");
            }

            var9.add(this.a(var11.b(), var8));
         }
      }

      return Collections.unmodifiableSet(var9);
   }

   @Override
   public void a_() {
      this.i.o();
   }
}
