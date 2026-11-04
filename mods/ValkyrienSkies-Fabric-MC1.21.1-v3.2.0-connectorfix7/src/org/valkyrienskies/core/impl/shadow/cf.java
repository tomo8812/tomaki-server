package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cf extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cf.class);
   private final cX i;

   public cf(final String var1, final JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.EXCLUSIVE_MAXIMUM, var4);
      super.d = var4;
      if (!var2.isNumber()) {
         throw new cq("exclusiveMaximum value is not a number");
      }

      this.a(this.e().d());
      var1 = var2.asText();
      if ((var2.isLong() || var2.isInt()) && cu.INTEGER.toString().equals(this.f())) {
         final long var6 = var2.asLong();
         this.i = new cX() {
            @Override
            public boolean a(JsonNode var1x) {
               if (var1x.isBigInteger()) {
                  int var6x;
                  return (var6x = var1x.bigIntegerValue().compareTo(new BigInteger(var2.asText()))) > 0 || var6x == false;
               } else if (var1x.isTextual()) {
                  BigDecimal var5 = new BigDecimal(var1);
                  int var4x;
                  return (var4x = new BigDecimal(var1x.asText()).compareTo(var5)) > 0 || var4x == false;
               } else {
                  long var2x = var1x.asLong();
                  return var6 < var2x || var6 == var2x;
               }
            }

            @Override
            public String a() {
               return String.valueOf(var6);
            }
         };
      } else {
         this.i = new cX() {
            @Override
            public boolean a(JsonNode var1x) {
               if (var2.isDouble() && var2.doubleValue() == Double.POSITIVE_INFINITY) {
                  return false;
               }

               if (var2.isDouble() && var2.doubleValue() == Double.NEGATIVE_INFINITY) {
                  return true;
               }

               if (var1x.isDouble() && var1x.doubleValue() == Double.NEGATIVE_INFINITY) {
                  return false;
               }

               if (var1x.isDouble() && var1x.doubleValue() == Double.POSITIVE_INFINITY) {
                  return true;
               }

               BigDecimal var2x = new BigDecimal(var1);
               int var3;
               return (var3 = new BigDecimal(var1x.asText()).compareTo(var2x)) > 0 || var3 == false;
            }

            @Override
            public String a() {
               return var1;
            }
         };
      }
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      if (!da.a(var1, super.d.d())) {
         return Collections.emptySet();
      } else {
         return this.i.a(var1) ? Collections.singleton(this.a(var3, this.i.a())) : Collections.emptySet();
      }
   }
}
