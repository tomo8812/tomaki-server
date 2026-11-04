package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.imifou.jsonschema.module.addon.TypeFormat;
import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

public final class ar implements aD {
   @Override
   public final void a(aL var1) {
      this.a(var1.c);
      this.a(var1.d);
   }

   private void a(aM<?> var1) {
      aM var10000 = var1.d(this::i).e(this::j).j(this::k).f(this::l).b(this::m).a(this::n);
      az var2 = this::a;
      var10000.b(var2).p(this::a).l(this::b).n(this::c).m(this::d).o(this::e).h(this::f).i(this::g).k(this::h);
   }

   private String i(aB<?, ?> var1) {
      return a(var1, JsonSchema.class).map(JsonSchema::title).filter(var0 -> !var0.isEmpty()).orElse(null);
   }

   private String j(aB<?, ?> var1) {
      return a(var1, JsonSchema.class).map(JsonSchema::description).filter(var0 -> !var0.isEmpty()).orElse(null);
   }

   private String k(aB<?, ?> var1) {
      return a(var1, JsonSchema.class).map(JsonSchema::format).filter(var0 -> !TypeFormat.NONE.equals(var0)).map(TypeFormat::toString).orElse(null);
   }

   private String l(aB<?, ?> var1) {
      return a(var1, JsonSchema.class).map(JsonSchema::defaultValue).filter(var0 -> !var0.isEmpty()).orElse(null);
   }

   private boolean m(aB<?, ?> var1) {
      return a(var1, JsonSchema.class).map(JsonSchema::required).orElse(Boolean.FALSE);
   }

   private boolean n(aB<?, ?> var1) {
      return a(var1, JsonSchema.class).map(JsonSchema::ignore).orElse(Boolean.FALSE);
   }

   protected final void a(ObjectNode var1, aB<?, ?> var2) {
      a(var2, JsonSchema.class)
         .map(JsonSchema::metadata)
         .ifPresent(
            var1x -> Stream.of(var1x)
               .filter(var0x -> !var0x.key().isEmpty())
               .filter(var0x -> !var0x.value().isEmpty())
               .forEach(var1xx -> var1.put(var1xx.key(), var1xx.value()))
         );
   }

   protected final BigDecimal a(aB<?, ?> var1) {
      return a(var1, JsonSchema.class).map(JsonSchema::multipleOf).filter(var0 -> var0 != 0.0).map(BigDecimal::new).orElse(null);
   }

   protected final BigDecimal b(aB<?, ?> var1) {
      return a(var1, JsonSchema.class)
         .filter(var0 -> !var0.exclusiveMin())
         .map(JsonSchema::min)
         .filter(var0 -> var0 != Double.MIN_VALUE)
         .map(BigDecimal::new)
         .orElse(null);
   }

   protected final BigDecimal c(aB<?, ?> var1) {
      return a(var1, JsonSchema.class)
         .filter(var0 -> !var0.exclusiveMax())
         .map(JsonSchema::max)
         .filter(var0 -> var0 != Double.MAX_VALUE)
         .map(BigDecimal::new)
         .orElse(null);
   }

   protected final BigDecimal d(aB<?, ?> var1) {
      return a(var1, JsonSchema.class)
         .filter(JsonSchema::exclusiveMin)
         .map(JsonSchema::min)
         .filter(var0 -> var0 != Double.MIN_VALUE)
         .map(BigDecimal::new)
         .orElse(null);
   }

   protected final BigDecimal e(aB<?, ?> var1) {
      return a(var1, JsonSchema.class)
         .filter(JsonSchema::exclusiveMax)
         .map(JsonSchema::max)
         .filter(var0 -> var0 != Double.MAX_VALUE)
         .map(BigDecimal::new)
         .orElse(null);
   }

   protected final Integer f(aB<?, ?> var1) {
      return a(var1, JsonSchema.class).map(JsonSchema::minLength).filter(var0 -> var0 > 0).orElse(null);
   }

   protected final Integer g(aB<?, ?> var1) {
      return a(var1, JsonSchema.class).map(JsonSchema::maxLength).filter(var0 -> var0 != Integer.MAX_VALUE).orElse(null);
   }

   protected final String h(aB<?, ?> var1) {
      return a(var1, JsonSchema.class).map(JsonSchema::pattern).filter(var0 -> !var0.isEmpty()).orElse(null);
   }

   private static <A extends Annotation> Optional<A> a(aB<?, ?> var0, Class<A> var1) {
      Annotation var2;
      if ((var2 = var0.d(var1)) == null) {
         if (var0 instanceof ay) {
            var0 = ((ay)var0).c();
         } else if (var0 instanceof aC) {
            var0 = ((aC)var0).c();
         } else {
            var0 = null;
         }

         var2 = var0 == null ? null : var0.d(var1);
      }

      return Optional.ofNullable((A)var2);
   }
}
