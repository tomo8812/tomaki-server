package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cI extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cS.class);
   private final cp i;

   public cI(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.NOT, var4);
      this.i = new cp(var4, this.e().a(), var3.i(), var2, var3);
      this.a(this.e().d());
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      Set var4 = new HashSet();
      Object var5 = bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties");
      bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", new ArrayList());

      try {
         this.a(g, (JsonNode)var1, var2, var3);
         return (var4 = this.i.a(var1, var2, var3)).isEmpty() ? Collections.singleton(this.a(var3, this.i.toString())) : Collections.emptySet();
      } finally {
         if (var4.isEmpty()) {
            List var8;
            (var8 = var5 == null ? new ArrayList() : (List)var5)
               .addAll((List)bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties"));
            bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var8);
         } else {
            bV.a().a("com.networknt.schema.UnEvaluatedPropertiesValidator.EvaluatedProperties", var5);
         }
      }
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3, boolean var4) {
      return var4 ? this.a(var1, var2, var3) : this.i.a(var1, var2, var3, var4);
   }

   @Override
   public void a_() {
      if (this.i != null) {
         this.i.o();
      }
   }
}
