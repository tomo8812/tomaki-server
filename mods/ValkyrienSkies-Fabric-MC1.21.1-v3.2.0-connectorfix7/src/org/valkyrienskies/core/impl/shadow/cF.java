package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cF extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cF.class);
   private double i = 0.0;

   public cF(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.MULTIPLE_OF, var4);
      if (var2.isNumber()) {
         this.i = var2.doubleValue();
      }

      this.a(this.e().d());
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      if (var1.isNumber()) {
         double var4 = var1.doubleValue();
         if (this.i != 0.0) {
            BigDecimal var6 = var1.isBigDecimal() ? var1.decimalValue() : new BigDecimal(String.valueOf(var4));
            BigDecimal var7 = new BigDecimal(String.valueOf(this.i));
            if (var6.divideAndRemainder(var7)[1].abs().compareTo(BigDecimal.ZERO) > 0) {
               return Collections.singleton(this.a(var3, "" + this.i));
            }
         }
      }

      return Collections.emptySet();
   }
}
