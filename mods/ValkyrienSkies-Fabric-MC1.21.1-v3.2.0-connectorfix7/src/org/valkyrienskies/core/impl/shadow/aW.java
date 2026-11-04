package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class aW {
   private static final Logger a = LoggerFactory.getLogger(aW.class);
   private final ObjectMapper b;

   public aW(ObjectMapper var1) {
      this.b = var1;
   }

   public static ObjectNode a(ay var0, aI var1) {
      aK var2;
      ObjectNode var3 = (var2 = var1.a()).o();
      aW var4;
      (var4 = new aW(var2.n())).c(var3, var2.i(var0), var1);
      var4.d(var3, var2.j(var0), var1);
      var4.a(var3, var2.k(var0), var1);
      var4.a(var3, var2.l(var0), var1);
      var4.a(var3, var2.c(var0), var1);
      var4.b(var3, var2.d(var0), var1);
      var4.a(var3, var2.m(var0), var1);
      var4.a(var3, var2.n(var0), var1);
      var4.a(var3, var2.o(var0), var1);
      var4.b(var3, var2.p(var0), var1);
      var4.e(var3, var2.q(var0), var1);
      var4.f(var3, var2.r(var0), var1);
      var4.a(var3, var2.s(var0), var1);
      var4.b(var3, var2.t(var0), var1);
      var4.c(var3, var2.u(var0), var1);
      var4.d(var3, var2.v(var0), var1);
      var4.e(var3, var2.w(var0), var1);
      var4.c(var3, var2.x(var0), var1);
      var4.d(var3, var2.y(var0), var1);
      var4.a(var3, var2.z(var0), var1);
      var2.s().forEach(var3x -> var3x.overrideInstanceAttributes(var3, var0, var1));
      return var3;
   }

   public static ObjectNode a(aC var0, aI var1) {
      aK var2;
      ObjectNode var3 = (var2 = var1.a()).o();
      aW var4;
      (var4 = new aW(var2.n())).c(var3, var2.i(var0), var1);
      var4.d(var3, var2.j(var0), var1);
      var4.a(var3, var2.k(var0), var1);
      var4.a(var3, var2.l(var0), var1);
      var4.a(var3, var2.c(var0), var1);
      var4.b(var3, var2.d(var0), var1);
      var4.a(var3, var2.m(var0), var1);
      var4.a(var3, var2.n(var0), var1);
      var4.a(var3, var2.o(var0), var1);
      var4.b(var3, var2.p(var0), var1);
      var4.e(var3, var2.q(var0), var1);
      var4.f(var3, var2.r(var0), var1);
      var4.a(var3, var2.s(var0), var1);
      var4.b(var3, var2.t(var0), var1);
      var4.c(var3, var2.u(var0), var1);
      var4.d(var3, var2.v(var0), var1);
      var4.e(var3, var2.w(var0), var1);
      var4.c(var3, var2.x(var0), var1);
      var4.d(var3, var2.y(var0), var1);
      var4.a(var3, var2.z(var0), var1);
      var2.t().forEach(var3x -> var3x.overrideInstanceAttributes(var3, var0, var1));
      return var3;
   }

   public static ObjectNode a(aV var0, aI var1, Set<String> var2) {
      aK var3;
      ObjectNode var4 = (var3 = var1.a()).o();
      aW var5 = new aW(var3.n());
      String var10002 = var3.a(var0);
      bb var9 = var1;
      String var8 = var10002;
      ObjectNode var7 = var4;
      if (var8 != null) {
         var7.put(var9.a(aP.TAG_ID), var8);
      }

      var10002 = var3.b(var0);
      var9 = var1;
      var8 = var10002;
      var7 = var4;
      if (var8 != null) {
         var7.put(var9.a(aP.TAG_ANCHOR), var8);
      }

      var5.c(var4, var3.c(var0), var1);
      var5.d(var4, var3.d(var0), var1);
      var5.a(var4, var3.e(var0), var1);
      var5.a(var4, var3.f(var0), var1);
      if (var2.isEmpty() || var2.contains(var3.a(aP.TAG_TYPE_OBJECT))) {
         var5.a(var4, var3.g(var0), var1);
         var5.a(var4, var3.h(var0), var1);
      }

      if (var2.isEmpty() || var2.contains(var3.a(aP.TAG_TYPE_STRING))) {
         var5.a(var4, var3.i(var0), var1);
         var5.b(var4, var3.j(var0), var1);
         var5.e(var4, var3.k(var0), var1);
         var5.f(var4, var3.l(var0), var1);
      }

      if (var2.isEmpty() || var2.contains(var3.a(aP.TAG_TYPE_INTEGER)) || var2.contains(var3.a(aP.TAG_TYPE_NUMBER))) {
         var5.a(var4, var3.m(var0), var1);
         var5.b(var4, var3.n(var0), var1);
         var5.c(var4, var3.o(var0), var1);
         var5.d(var4, var3.p(var0), var1);
         var5.e(var4, var3.q(var0), var1);
      }

      if (var2.isEmpty() || var2.contains(var3.a(aP.TAG_TYPE_ARRAY))) {
         var5.c(var4, var3.r(var0), var1);
         var5.d(var4, var3.s(var0), var1);
         var5.a(var4, var3.t(var0), var1);
      }

      return var4;
   }

   public static void a(ObjectNode var0, ObjectNode var1) {
      if (var1 != null) {
         Iterator var3 = var1.fields();

         while (var3.hasNext()) {
            Entry var2 = (Entry)var3.next();
            if (!var0.has((String)var2.getKey())) {
               var0.set((String)var2.getKey(), (JsonNode)var2.getValue());
            }
         }
      }
   }

   private aW a(ObjectNode var1, String var2, aI var3) {
      if (var2 != null) {
         var1.put(var3.a(aP.TAG_ID), var2);
      }

      return this;
   }

   private aW b(ObjectNode var1, String var2, aI var3) {
      if (var2 != null) {
         var1.put(var3.a(aP.TAG_ANCHOR), var2);
      }

      return this;
   }

   @Deprecated
   private aW a(ObjectNode var1, String var2) {
      if (var2 != null) {
         var1.put(aP.TAG_TITLE.a(aQ.DRAFT_7), var2);
      }

      return this;
   }

   private aW c(ObjectNode var1, String var2, aI var3) {
      if (var2 != null) {
         var1.put(var3.a(aP.TAG_TITLE), var2);
      }

      return this;
   }

   @Deprecated
   private aW b(ObjectNode var1, String var2) {
      if (var2 != null) {
         var1.put(aP.TAG_DESCRIPTION.a(aQ.DRAFT_7), var2);
      }

      return this;
   }

   private aW d(ObjectNode var1, String var2, aI var3) {
      if (var2 != null) {
         var1.put(var3.a(aP.TAG_DESCRIPTION), var2);
      }

      return this;
   }

   @Deprecated
   private aW a(ObjectNode var1, Object var2) {
      if (var2 != null) {
         String var3 = aP.TAG_DEFAULT.a(aQ.DRAFT_7);
         if (var2 instanceof String) {
            var1.put(var3, (String)var2);
         } else if (var2 instanceof BigDecimal) {
            var1.put(var3, (BigDecimal)var2);
         } else if (var2 instanceof BigInteger) {
            var1.put(var3, (BigInteger)var2);
         } else if (var2 instanceof Boolean) {
            var1.put(var3, (Boolean)var2);
         } else if (var2 instanceof Double) {
            var1.put(var3, (Double)var2);
         } else if (var2 instanceof Float) {
            var1.put(var3, (Float)var2);
         } else if (var2 instanceof Integer) {
            var1.put(var3, (Integer)var2);
         } else {
            var1.putPOJO(var3, var2);
         }
      }

      return this;
   }

   private aW a(ObjectNode var1, Object var2, aI var3) {
      if (var2 != null) {
         String var4 = var3.a(aP.TAG_DEFAULT);
         if (var2 instanceof String) {
            var1.put(var4, (String)var2);
         } else if (var2 instanceof BigDecimal) {
            var1.put(var4, (BigDecimal)var2);
         } else if (var2 instanceof BigInteger) {
            var1.put(var4, (BigInteger)var2);
         } else if (var2 instanceof Boolean) {
            var1.put(var4, (Boolean)var2);
         } else if (var2 instanceof Double) {
            var1.put(var4, (Double)var2);
         } else if (var2 instanceof Float) {
            var1.put(var4, (Float)var2);
         } else if (var2 instanceof Integer) {
            var1.put(var4, (Integer)var2);
         } else {
            var1.putPOJO(var4, var2);
         }
      }

      return this;
   }

   @Deprecated
   private aW a(ObjectNode var1, Collection<?> var2) {
      if (var2 != null) {
         aQ var3 = aQ.DRAFT_7;
         if ((var2 = var2.stream().filter(this::a).filter(this::b).collect(Collectors.toList())).size() == 1) {
            Object var4;
            if ((var4 = var2.get(0)) instanceof String) {
               var1.put(aP.TAG_CONST.a(var3), (String)var4);
            } else {
               var1.putPOJO(aP.TAG_CONST.a(var3), var4);
            }
         } else if (!var2.isEmpty()) {
            ArrayNode var8 = var1.arrayNode();
            Iterator var7 = var2.iterator();

            while (var7.hasNext()) {
               Object var5;
               if ((var5 = var7.next()) instanceof String) {
                  var8.add((String)var5);
               } else {
                  var8.addPOJO(var5);
               }
            }

            var1.set(aP.TAG_ENUM.a(var3), var8);
         }
      }

      return this;
   }

   public final aW a(ObjectNode var1, Collection<?> var2, aI var3) {
      if (var2 != null) {
         if ((var2 = var2.stream().filter(this::a).filter(this::b).collect(Collectors.toList())).size() == 1 && var3.a().l()) {
            Object var8;
            if ((var8 = var2.get(0)) instanceof String) {
               var1.put(var3.a(aP.TAG_CONST), (String)var8);
            } else {
               var1.putPOJO(var3.a(aP.TAG_CONST), var8);
            }
         } else if (!var2.isEmpty()) {
            ArrayNode var4 = var1.arrayNode();
            Iterator var7 = var2.iterator();

            while (var7.hasNext()) {
               Object var5;
               if ((var5 = var7.next()) instanceof String) {
                  var4.add((String)var5);
               } else {
                  var4.addPOJO(var5);
               }
            }

            var1.set(var3.a(aP.TAG_ENUM), var4);
         }
      }

      return this;
   }

   private boolean a(Object var1) {
      Class var2;
      return var1 == null
         ? true
         : (var2 = var1.getClass()).isPrimitive()
            || Number.class.isAssignableFrom(var2)
            || CharSequence.class.isAssignableFrom(var2)
            || Enum.class.isAssignableFrom(var2);
   }

   private boolean b(Object var1) {
      try {
         return var1 == null || this.b.writeValueAsString(var1) != null;
      } catch (JsonProcessingException var3) {
         a.warn("Failed to convert value to string via ObjectMapper: {}", var1, var3);
         return false;
      }
   }

   private aW a(ObjectNode var1, boolean var2, aI var3) {
      if (var2) {
         var1.put(var3.a(aP.TAG_READ_ONLY), var2);
      }

      return this;
   }

   private aW b(ObjectNode var1, boolean var2, aI var3) {
      if (var2) {
         var1.put(var3.a(aP.TAG_WRITE_ONLY), var2);
      }

      return this;
   }

   private aW a(ObjectNode var1, Type var2, aI var3) {
      if (var2 == Void.class || var2 == void.class) {
         var1.put(var3.a(aP.TAG_ADDITIONAL_PROPERTIES), false);
      } else {
         ResolvedType var4;
         if (var2 != null && (var4 = var3.b().a(var2)).getErasedType() != Object.class) {
            ObjectNode var5 = var3.b(var4);
            var1.set(var3.a(aP.TAG_ADDITIONAL_PROPERTIES), var5);
         }
      }

      return this;
   }

   private aW a(ObjectNode var1, Map<String, Type> var2, aI var3) {
      if (var2 != null && !var2.isEmpty()) {
         ObjectNode var4 = this.b.createObjectNode();

         for (Entry var5 : var2.entrySet()) {
            ResolvedType var6 = var3.b().a((Type)var5.getValue());
            ObjectNode var8 = var3.b(var6);
            var4.set((String)var5.getKey(), var8);
         }

         var1.set(var3.a(aP.TAG_PATTERN_PROPERTIES), var4);
      }

      return this;
   }

   @Deprecated
   private aW a(ObjectNode var1, Integer var2) {
      if (var2 != null) {
         var1.put(aP.TAG_LENGTH_MIN.a(aQ.DRAFT_7), var2);
      }

      return this;
   }

   private aW a(ObjectNode var1, Integer var2, aI var3) {
      if (var2 != null) {
         var1.put(var3.a(aP.TAG_LENGTH_MIN), var2);
      }

      return this;
   }

   @Deprecated
   private aW b(ObjectNode var1, Integer var2) {
      if (var2 != null) {
         var1.put(aP.TAG_LENGTH_MAX.a(aQ.DRAFT_7), var2);
      }

      return this;
   }

   private aW b(ObjectNode var1, Integer var2, aI var3) {
      if (var2 != null) {
         var1.put(var3.a(aP.TAG_LENGTH_MAX), var2);
      }

      return this;
   }

   @Deprecated
   private aW c(ObjectNode var1, String var2) {
      if (var2 != null) {
         var1.put(aP.TAG_FORMAT.a(aQ.DRAFT_7), var2);
      }

      return this;
   }

   private aW e(ObjectNode var1, String var2, aI var3) {
      if (var2 != null) {
         var1.put(var3.a(aP.TAG_FORMAT), var2);
      }

      return this;
   }

   @Deprecated
   private aW d(ObjectNode var1, String var2) {
      if (var2 != null) {
         var1.put(aP.TAG_PATTERN.a(aQ.DRAFT_7), var2);
      }

      return this;
   }

   private aW f(ObjectNode var1, String var2, aI var3) {
      if (var2 != null) {
         var1.put(var3.a(aP.TAG_PATTERN), var2);
      }

      return this;
   }

   @Deprecated
   private aW a(ObjectNode var1, BigDecimal var2) {
      if (var2 != null) {
         var1.put(aP.TAG_MINIMUM.a(aQ.DRAFT_7), var2);
      }

      return this;
   }

   private aW a(ObjectNode var1, BigDecimal var2, aI var3) {
      if (var2 != null) {
         var1.put(var3.a(aP.TAG_MINIMUM), var2);
      }

      return this;
   }

   @Deprecated
   private aW b(ObjectNode var1, BigDecimal var2) {
      if (var2 != null) {
         var1.put(aP.TAG_MINIMUM_EXCLUSIVE.a(aQ.DRAFT_7), var2);
      }

      return this;
   }

   private aW b(ObjectNode var1, BigDecimal var2, aI var3) {
      if (var2 != null) {
         var1.put(var3.a(aP.TAG_MINIMUM_EXCLUSIVE), var2);
      }

      return this;
   }

   @Deprecated
   private aW c(ObjectNode var1, BigDecimal var2) {
      if (var2 != null) {
         var1.put(aP.TAG_MAXIMUM.a(aQ.DRAFT_7), var2);
      }

      return this;
   }

   private aW c(ObjectNode var1, BigDecimal var2, aI var3) {
      if (var2 != null) {
         var1.put(var3.a(aP.TAG_MAXIMUM), var2);
      }

      return this;
   }

   @Deprecated
   private aW d(ObjectNode var1, BigDecimal var2) {
      if (var2 != null) {
         var1.put(aP.TAG_MAXIMUM_EXCLUSIVE.a(aQ.DRAFT_7), var2);
      }

      return this;
   }

   private aW d(ObjectNode var1, BigDecimal var2, aI var3) {
      if (var2 != null) {
         var1.put(var3.a(aP.TAG_MAXIMUM_EXCLUSIVE), var2);
      }

      return this;
   }

   @Deprecated
   private aW e(ObjectNode var1, BigDecimal var2) {
      if (var2 != null) {
         var1.put(aP.TAG_MULTIPLE_OF.a(aQ.DRAFT_7), var2);
      }

      return this;
   }

   private aW e(ObjectNode var1, BigDecimal var2, aI var3) {
      if (var2 != null) {
         var1.put(var3.a(aP.TAG_MULTIPLE_OF), var2);
      }

      return this;
   }

   @Deprecated
   private aW c(ObjectNode var1, Integer var2) {
      if (var2 != null) {
         var1.put(aP.TAG_ITEMS_MIN.a(aQ.DRAFT_7), var2);
      }

      return this;
   }

   private aW c(ObjectNode var1, Integer var2, aI var3) {
      if (var2 != null) {
         var1.put(var3.a(aP.TAG_ITEMS_MIN), var2);
      }

      return this;
   }

   @Deprecated
   private aW d(ObjectNode var1, Integer var2) {
      if (var2 != null) {
         var1.put(aP.TAG_ITEMS_MAX.a(aQ.DRAFT_7), var2);
      }

      return this;
   }

   private aW d(ObjectNode var1, Integer var2, aI var3) {
      if (var2 != null) {
         var1.put(var3.a(aP.TAG_ITEMS_MAX), var2);
      }

      return this;
   }

   @Deprecated
   private aW a(ObjectNode var1, Boolean var2) {
      if (var2 != null) {
         var1.put(aP.TAG_ITEMS_UNIQUE.a(aQ.DRAFT_7), var2);
      }

      return this;
   }

   private aW a(ObjectNode var1, Boolean var2, aI var3) {
      if (var2 != null) {
         var1.put(var3.a(aP.TAG_ITEMS_UNIQUE), var2);
      }

      return this;
   }
}
