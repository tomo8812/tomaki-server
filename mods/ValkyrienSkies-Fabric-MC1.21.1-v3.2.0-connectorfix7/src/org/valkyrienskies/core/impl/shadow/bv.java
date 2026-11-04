package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class bv implements aR, av {
   @Override
   public final List<ResolvedType> b(ResolvedType var1, aI var2) {
      JsonSubTypes var3 = var1.getErasedType().getAnnotation(JsonSubTypes.class);
      return this.a(var1, var3, var2.b());
   }

   public final List<ResolvedType> a(aB<?, ?> var1) {
      if (var1.d == null) {
         return null;
      }

      JsonSubTypes var2 = var1.b(JsonSubTypes.class);
      return this.a(var1.d, var2, var1.e);
   }

   private List<ResolvedType> a(ResolvedType var1, JsonSubTypes var2, aU var3) {
      return var2 == null ? null : Stream.of(var2.value()).map(var3x -> a(var1, var3x, var3)).collect(Collectors.toList());
   }

   private static ResolvedType a(ResolvedType var0, JsonSubTypes.Type var1, aU var2) {
      try {
         Class var4 = var1.value();
         ResolvedType var3 = var0;
         return var2.a.resolveSubtype(var3, var4);
      } catch (IllegalArgumentException var5) {
         return var2.a(var1.value());
      }
   }

   @Override
   public final at a(ResolvedType var1, aI var2) {
      Class var3 = var1.getErasedType();

      Class var10000;
      while (true) {
         if (var3.getAnnotation(JsonTypeInfo.class) != null) {
            var10000 = var3;
            break;
         }

         Optional var4;
         if ((var4 = Stream.of(var3.getInterfaces()).filter(var0x -> var0x.getAnnotation(JsonTypeInfo.class) != null).findFirst()).isPresent()) {
            var10000 = (Class)var4.get();
            break;
         }

         if ((var3 = var3.getSuperclass()) == null) {
            var10000 = null;
            break;
         }
      }

      var3 = var10000;
      if (var10000 != null && var1.getErasedType().getAnnotation(JsonSubTypes.class) == null) {
         JsonTypeInfo var8 = var3.getAnnotation(JsonTypeInfo.class);
         JsonSubTypes var7 = var3.getAnnotation(JsonSubTypes.class);
         ObjectNode var5;
         return (var5 = this.a(var1, var8, var7, null, var2)) == null ? null : new at(var5, at.b.STANDARD, at.a.NO);
      } else {
         return null;
      }
   }

   public final aw a(aB<?, ?> var1, aI var2) {
      if (var1.d == null || var1.d.getErasedType().getDeclaredAnnotation(JsonSubTypes.class) != null) {
         return null;
      }

      JsonTypeInfo var3;
      if ((var3 = var1.b(JsonTypeInfo.class)) == null) {
         return null;
      }

      if (var3.use() == JsonTypeInfo.Id.NONE) {
         ObjectNode var8;
         (var8 = var2.a().o()).withArray(var2.a(aP.TAG_ALLOF)).add(var2.a(var1.d, this));
         return new aw(var8, at.a.YES);
      }

      ObjectNode var4;
      if (var1 instanceof ay) {
         var4 = aW.a((ay)var1, var2);
      } else if (var1 instanceof aC) {
         var4 = aW.a((aC)var1, var2);
      } else {
         var4 = null;
      }

      JsonSubTypes var5 = var1.b(JsonSubTypes.class);
      ObjectNode var7;
      return (var7 = this.a(var1.d, var3, var5, var4, var2)) == null ? null : new aw(var7, at.a.NO);
   }

   private static Class<?> a(Class<?> var0) {
      var0 = var0;

      while (var0.getAnnotation(JsonTypeInfo.class) == null) {
         Optional var1;
         if ((var1 = Stream.of(var0.getInterfaces()).filter(var0x -> var0x.getAnnotation(JsonTypeInfo.class) != null).findFirst()).isPresent()) {
            return (Class<?>)var1.get();
         }

         if ((var0 = var0.getSuperclass()) == null) {
            return null;
         }
      }

      return var0;
   }

   private static String a(ResolvedType var0, JsonTypeInfo var1, JsonSubTypes var2) {
      Class var3 = var0.getErasedType();
      String var4;
      switch (var1.use()) {
         case NAME:
            var2 = var2;
            Class var5 = var3;
            var4 = (var2 == null
                  ? Optional.empty()
                  : Stream.of(var2.value())
                     .filter(var1x -> var5.equals(var1x.value()))
                     .findFirst()
                     .map(var0x -> var0x.name())
                     .filter(var0x -> !var0x.isEmpty()))
               .orElseGet(
                  () -> Optional.ofNullable(var3.getAnnotation(JsonTypeName.class))
                     .map(JsonTypeName::value)
                     .filter(var0x -> !var0x.isEmpty())
                     .orElseGet(() -> c(var3))
               );
            break;
         case CLASS:
            var4 = var3.getName();
            break;
         default:
            var4 = null;
      }

      return var4;
   }

   private static Optional<String> a(Class<?> var0, JsonSubTypes var1) {
      return var1 == null
         ? Optional.empty()
         : Stream.of(var1.value()).filter(var1x -> var5.equals(var1x.value())).findFirst().map(var0x -> var0x.name()).filter(var0x -> !var0x.isEmpty());
   }

   private static Optional<String> b(Class<?> var0) {
      return Optional.ofNullable(var0.getAnnotation(JsonTypeName.class)).map(JsonTypeName::value).filter(var0x -> !var0x.isEmpty());
   }

   private static String c(Class<?> var0) {
      Class var1;
      return (var1 = var0.getDeclaringClass()) == null ? var0.getSimpleName() : c(var1) + '$' + var0.getSimpleName();
   }

   private ObjectNode a(ResolvedType var1, JsonTypeInfo var2, JsonSubTypes var3, ObjectNode var4, aI var5) {
      String var11;
      if ((var11 = a(var1, var2, var3)) == null) {
         return null;
      }

      ObjectNode var6 = var5.a().o();
      switch (var2.include()) {
         case WRAPPER_ARRAY:
            var6.put(var5.a(aP.TAG_TYPE), var5.a(aP.TAG_TYPE_ARRAY));
            ArrayNode var10;
            (var10 = var6.withArray(var5.a(aP.TAG_ITEMS))).addObject().put(var5.a(aP.TAG_TYPE), var5.a(aP.TAG_TYPE_STRING)).put(var5.a(aP.TAG_CONST), var11);
            if (var4 != null && !var4.isEmpty()) {
               var10.addObject().withArray(var5.a(aP.TAG_ALLOF)).add(var5.a(var1, this)).add(var4);
            } else {
               var10.add(var5.a(var1, this));
            }
            break;
         case WRAPPER_OBJECT:
            var6.put(var5.a(aP.TAG_TYPE), var5.a(aP.TAG_TYPE_OBJECT));
            ObjectNode var9 = var6.with(var5.a(aP.TAG_PROPERTIES));
            if (var4 != null && !var4.isEmpty()) {
               var9.with(var11).withArray(var5.a(aP.TAG_ALLOF)).add(var5.a(var1, this)).add(var4);
            } else {
               var9.set(var11, var5.a(var1, this));
            }

            var6.withArray(var5.a(aP.TAG_REQUIRED)).add(var11);
            break;
         case PROPERTY:
         case EXISTING_PROPERTY:
            String var8 = Optional.ofNullable(var2.property()).filter(var0 -> !var0.isEmpty()).orElseGet(() -> var2.use().getDefaultPropertyName());
            ObjectNode var7 = var6.withArray(var5.a(aP.TAG_ALLOF)).add(var5.a(var1, this)).addObject();
            if (var4 != null && !var4.isEmpty()) {
               var7.setAll(var4);
            }

            var7.put(var5.a(aP.TAG_TYPE), var5.a(aP.TAG_TYPE_OBJECT)).with(var5.a(aP.TAG_PROPERTIES)).with(var8).put(var5.a(aP.TAG_CONST), var11);
            var7.withArray(var5.a(aP.TAG_REQUIRED)).add(var8);
            break;
         default:
            return null;
      }

      return var6;
   }
}
