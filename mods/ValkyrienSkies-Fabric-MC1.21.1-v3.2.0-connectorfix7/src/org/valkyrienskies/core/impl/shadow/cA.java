package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cA extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cA.class);
   private static final String i = "exclusiveMaximum";
   private boolean j = false;
   private final cX k;

   public cA(final String var1, final JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.MAXIMUM, var4);
      super.d = var4;
      if (!var2.isNumber()) {
         throw new cq("maximum value is not a number");
      }

      JsonNode var9;
      if ((var9 = this.d().c().get("exclusiveMaximum")) != null && var9.isBoolean()) {
         this.j = var9.booleanValue();
      }

      this.a(this.e().d());
      var1 = var2.asText();
      if ((var2.isLong() || var2.isInt()) && cu.INTEGER.toString().equals(this.f())) {
         final long var7 = var2.asLong();
         this.k = new cX() {
            @Override
            public boolean a(JsonNode var1x) {
               if (var1x.isBigInteger()) {
                  int var6;
                  return (var6 = var1x.bigIntegerValue().compareTo(new BigInteger(var2.asText()))) > 0 || cA.this.j && var6 == false;
               } else if (var1x.isTextual()) {
                  BigDecimal var5 = new BigDecimal(var1);
                  int var4x;
                  return (var4x = new BigDecimal(var1x.asText()).compareTo(var5)) > 0 || cA.this.j && var4x == false;
               } else {
                  long var2x = var1x.asLong();
                  return var7 < var2x || cA.this.j && var7 == var2x;
               }
            }

            @Override
            public String a() {
               return String.valueOf(var7);
            }
         };
      } else {
         this.k = new cX() {
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
               return (var3 = new BigDecimal(var1x.asText()).compareTo(var2x)) > 0 || cA.this.j && var3 == false;
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
         return this.k.a(var1) ? Collections.singleton(this.a(var3, this.k.a())) : Collections.emptySet();
      }
   }
}
