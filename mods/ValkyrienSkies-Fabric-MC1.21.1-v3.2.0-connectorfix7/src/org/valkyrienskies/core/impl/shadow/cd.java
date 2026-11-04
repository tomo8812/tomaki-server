package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.NullNode;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cd extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cd.class);
   private final Set<JsonNode> i;
   private final String j;

   public cd(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.ENUM, var4);
      super.d = var4;
      if (var2 != null && var2.isArray()) {
         this.i = new HashSet<>();
         StringBuilder var7;
         (var7 = new StringBuilder()).append('[');
         String var5 = "";

         for (Iterator var8 = var2.iterator(); var8.hasNext(); var5 = ", ") {
            JsonNode var6;
            if ((var6 = (JsonNode)var8.next()).isNumber()) {
               this.i.add(DecimalNode.valueOf(var6.decimalValue()));
            } else {
               this.i.add(var6);
            }

            var7.append(var5);
            var7.append(var6.asText());
         }

         if (var4.d().e() && (var2 = var3.c().get("nullable")) != null && var2.asBoolean()) {
            this.i.add(NullNode.getInstance());
            var5 = ", ";
            var7.append(var5);
            var7.append("null");
         }

         var7.append(']');
         this.j = var7.toString();
      } else {
         this.i = Collections.emptySet();
         this.j = "[none]";
      }

      this.a(this.e().d());
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      LinkedHashSet var4 = new LinkedHashSet();
      if (var1.isNumber()) {
         var1 = DecimalNode.valueOf(var1.decimalValue());
      }

      if (!this.i.contains(var1) && (!super.d.d().a() || !this.b(var1))) {
         var4.add(this.a(var3, this.j));
      }

      return Collections.unmodifiableSet(var4);
   }

   private boolean b(JsonNode var1) {
      if (cZ.a(var1, super.d.d()) == cu.STRING) {
         String var4 = var1.textValue();
         Iterator var2 = this.i.iterator();

         while (var2.hasNext()) {
            String var3;
            if ((var3 = ((JsonNode)var2.next()).asText()) != null && var3.equals(var4)) {
               return true;
            }
         }
      }

      return false;
   }
}
