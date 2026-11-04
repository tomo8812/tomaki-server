package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public final class bm implements aD {
   final Map<Class<?>, aP> a = new HashMap<>();
   final Map<Class<?>, String> b = new HashMap<>();

   public static bm a() {
      bm var0;
      bm var10000 = var0 = new bm();
      Class<Object> var2 = Object.class;
      var10000.a(var2, aP.TAG_TYPE_NULL, null);
      Stream.of(String.class, Character.class, char.class, CharSequence.class, Byte.class, byte.class).forEach(var0::a);
      Stream.of(Boolean.class, boolean.class).forEach(var0::b);
      Stream.of(Integer.class, int.class).forEach(var1 -> var0.b((Class<?>)var1, "int32"));
      Stream.of(Long.class, long.class).forEach(var1 -> var0.b((Class<?>)var1, "int64"));
      Stream.of(Short.class, short.class).forEach(var0::c);
      Stream.of(Double.class, double.class).forEach(var1 -> var0.c((Class<?>)var1, "double"));
      Stream.of(Float.class, float.class).forEach(var1 -> var0.c((Class<?>)var1, "float"));
      return var0;
   }

   public static bm b() {
      bm var0;
      (var0 = a()).a(LocalDate.class, "date");
      Stream.of(LocalDateTime.class, LocalTime.class, ZonedDateTime.class, OffsetDateTime.class, OffsetTime.class, Instant.class, Date.class, Calendar.class)
         .forEach(var1 -> var0.a((Class<?>)var1, "date-time"));
      var0.a(UUID.class, "uuid");
      var0.a(URI.class, "uri");
      var0.a(ZoneId.class);
      var0.c(BigInteger.class);
      Stream.of(BigDecimal.class, Number.class).forEach(var0::d);
      return var0;
   }

   private bm a(Class<?> var1, aP var2, String var3) {
      this.a.put(var1, var2);
      if (var3 != null) {
         this.b.put(var1, var3);
      }

      return this;
   }

   private bm e(Class<?> var1) {
      return this.a(var1, aP.TAG_TYPE_NULL, null);
   }

   @Deprecated
   private bm f(Class<?> var1) {
      return this.a(var1, aP.TAG_TYPE_OBJECT, null);
   }

   public final bm a(Class<?> var1) {
      return this.a((Class<?>)var1, null);
   }

   private bm a(Class<?> var1, String var2) {
      return this.a(var1, aP.TAG_TYPE_STRING, var2);
   }

   public final bm b(Class<?> var1) {
      return this.a((Class<?>)var1, aP.TAG_TYPE_BOOLEAN, null);
   }

   public final bm c(Class<?> var1) {
      return this.b((Class<?>)var1, null);
   }

   private bm b(Class<?> var1, String var2) {
      return this.a(var1, aP.TAG_TYPE_INTEGER, var2);
   }

   public final bm d(Class<?> var1) {
      return this.c((Class<?>)var1, null);
   }

   private bm c(Class<?> var1, String var2) {
      return this.a(var1, aP.TAG_TYPE_NUMBER, var2);
   }

   private Boolean a(aB<?, ?> var1) {
      return var1.d.isPrimitive() ? Boolean.FALSE : null;
   }

   @Override
   public final void a(aL var1) {
      var1.c.a(this::a);
      var1.d.a(this::a);
      var1.b.b(this::a).c(this::b).a(new bm.a((byte)0));
   }

   private Type a(aV var1) {
      return var1.d.getTypeParameters().isEmpty() && aP.TAG_TYPE_NULL == this.a.get(var1.d.getErasedType()) ? Object.class : null;
   }

   private Map<String, Type> b(aV var1) {
      return var1.d.getTypeParameters().isEmpty() && aP.TAG_TYPE_NULL == this.a.get(var1.d.getErasedType()) ? Collections.emptyMap() : null;
   }

   // $VF: synthetic method
   static Map a(bm var0) {
      return var0.a;
   }

   // $VF: synthetic method
   static Map b(bm var0) {
      return var0.b;
   }

   class a implements av {
      private a() {
      }

      @Override
      public final at a(ResolvedType var1, aI var2) {
         if (!var1.getTypeParameters().isEmpty()) {
            return null;
         }

         bm var3 = bm.this;
         aP var6;
         if ((var6 = bm.this.a.get(var1.getErasedType())) == null) {
            return null;
         }

         ObjectNode var4 = var2.a().o();
         if (var6 != aP.TAG_TYPE_NULL) {
            var4.put(var2.a(aP.TAG_TYPE), var2.a(var6));
         }

         if (var2.a().g()) {
            var3 = bm.this;
            String var5;
            if ((var5 = bm.this.b.get(var1.getErasedType())) != null) {
               var4.put(var2.a(aP.TAG_FORMAT), var5);
            }
         }

         return new at(var4, at.b.INLINE, at.a.YES);
      }

      // $VF: synthetic method
      a(byte var2) {
         this();
      }
   }
}
