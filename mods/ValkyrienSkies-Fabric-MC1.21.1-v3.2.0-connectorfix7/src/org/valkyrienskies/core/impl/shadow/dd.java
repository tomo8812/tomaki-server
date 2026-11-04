package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class dd extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(dd.class);
   private final List<cv> i = new ArrayList<>();
   private final String j;

   public dd(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.UNION_TYPE, var4);
      super.d = var4;
      StringBuilder var5 = new StringBuilder();
      String var6 = "";
      var5.append('[');
      if (!var2.isArray()) {
         throw new cq("Expected array for type property on Union Type Definition.");
      }

      int var7 = 0;

      for (Iterator var10 = var2.iterator(); var10.hasNext(); var7++) {
         JsonNode var8;
         cu var9 = cZ.a(var8 = (JsonNode)var10.next());
         var5.append(var6).append(var9);
         var6 = ", ";
         if (var8.isObject()) {
            this.i.add(new cp(var4, dj.TYPE.a(), var3.i(), var8, var3));
         } else {
            this.i.add(new da(var1 + "/" + var7, var8, var3, var4));
         }
      }

      var5.append(']');
      this.j = var5.toString();
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      cu var4 = cZ.a(var1, super.d.d());
      boolean var5 = false;
      Iterator var6 = this.i.iterator();

      while (var6.hasNext()) {
         Set var7;
         if ((var7 = ((cv)var6.next()).a(var1, var2, var3)) == null || var7.isEmpty()) {
            var5 = true;
            break;
         }
      }

      return !var5 ? Collections.singleton(this.a(var3, var4.toString(), this.j)) : Collections.emptySet();
   }

   @Override
   public void a_() {
      Iterator var1 = this.i.iterator();

      while (var1.hasNext()) {
         ((cv)var1.next()).a_();
      }
   }
}
