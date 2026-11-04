package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.HierarchicType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class bs implements aD {
   private final Set<bt> a;
   private ObjectMapper b;
   private final Map<Class<?>, BeanDescription> c = Collections.synchronizedMap(new HashMap<>());
   private final Map<Class<?>, PropertyNamingStrategy> d = Collections.synchronizedMap(new HashMap<>());

   public bs() {
      this.a = Collections.emptySet();
   }

   private bs(bt... var1) {
      this.a = var1 == null ? Collections.emptySet() : new HashSet<>(Arrays.asList(var1));
   }

   @Override
   public final void a(aL var1) {
      this.b = var1.a;
      aM var2 = var1.c;
      aM var3 = var1.d;
      this.a(var2);
      this.a(var3);
      var2.a(this::b);
      var3.a(this::a);
      if (!this.a.contains(bt.IGNORE_PROPERTY_NAMING_STRATEGY)) {
         var2.c(this::a);
      }

      aN var8;
      (var8 = var1.b).a(this::a);
      boolean var4 = this.a.contains(bt.FLATTENED_ENUMS_FROM_JSONVALUE);
      boolean var5 = this.a.contains(bt.FLATTENED_ENUMS_FROM_JSONPROPERTY);
      if (var4 || var5) {
         var8.a(new br(var4, var5));
      }

      if (this.a.contains(bt.RESPECT_JSONPROPERTY_ORDER)) {
         bu var7 = new bu(true);
         var8.a = var7;
      }

      var4 = !this.a.contains(bt.SKIP_SUBTYPE_LOOKUP);
      var5 = !this.a.contains(bt.IGNORE_TYPE_INFO_TRANSFORM);
      if (var4 || var5) {
         bv var6 = new bv();
         if (var4) {
            bv var11 = var6;
            var8.d.add(var11);
            var2.b(var6::a);
            var3.b(var6::a);
         }

         if (var5) {
            var8.a((av)var6);
            var2.a(var6::a);
            var3.a(var6::a);
         }
      }
   }

   private void a(aM<?> var1) {
      var1.e(this::a);
      var1.c(this::b);
      var1.c(this::d);
      var1.d(this::e);
      if (this.a.contains(bt.RESPECT_JSONPROPERTY_REQUIRED)) {
         var1.b(this::c);
      }
   }

   protected final String a(aB<?, ?> var1) {
      JsonPropertyDescription var2;
      return (var2 = var1.e(JsonPropertyDescription.class)) != null ? var2.value() : null;
   }

   protected final String a(aV var1) {
      JsonClassDescription var2;
      return (var2 = var1.d.getErasedType().getAnnotation(JsonClassDescription.class)) != null ? var2.value() : null;
   }

   protected final String b(aB<?, ?> var1) {
      JsonProperty var2;
      String var3;
      return (var2 = var1.b(JsonProperty.class)) != null && (var3 = var2.value()) != null && !var3.isEmpty() && !var3.equals(var1.k()) ? var3 : null;
   }

   protected final String a(ay var1) {
      PropertyNamingStrategy var2;
      synchronized (this.d) {
         var2 = this.d.computeIfAbsent(var1.a.getDeclaringType().getErasedType(), this::a);
      }

      return var2 == null ? null : var2.nameForField(null, null, var1.m());
   }

   private PropertyNamingStrategy a(Class<?> var1) {
      return Optional.ofNullable(var1.getAnnotation(JsonNaming.class)).map(JsonNaming::value).map(var0 -> {
         try {
            return var0.getConstructor().newInstance();
         } catch (ReflectiveOperationException | SecurityException var1x) {
            return null;
         }
      }).orElse(null);
   }

   private BeanDescription a(ResolvedType var1) {
      synchronized (this.c) {
         return this.c.computeIfAbsent(var1.getErasedType(), var1x -> this.b.getSerializationConfig().introspect(this.b.getTypeFactory().constructType(var1x)));
      }
   }

   protected final boolean b(ay var1) {
      if (var1.e(JsonBackReference.class) != null) {
         return true;
      }

      HierarchicType var2 = (HierarchicType)var1.b.allTypesAndOverrides().get(0);
      BeanDescription var5 = this.a(var2.getType());
      Set var3 = this.b.getSerializationConfig().getAnnotationIntrospector().findPropertyIgnoralByName(null, var5.getClassInfo()).getIgnored();
      String var4 = var1.m();
      return var3.contains(var4) ? true : var5.findProperties().stream().noneMatch(var1x -> var4.equals(var1x.getInternalName()));
   }

   protected final boolean a(aC var1) {
      ay var2;
      if ((var2 = var1.c()) != null && this.b(var2)) {
         return true;
      } else {
         return var2 == null && var1.e(JsonBackReference.class) != null
            ? true
            : this.a.contains(bt.INCLUDE_ONLY_JSONPROPERTY_ANNOTATED_METHODS) && var1.b(JsonProperty.class) == null;
      }
   }

   protected final boolean c(aB<?, ?> var1) {
      JsonProperty var2;
      return (var2 = var1.e(JsonProperty.class)) != null && var2.required();
   }

   protected final boolean d(aB<?, ?> var1) {
      JsonProperty var2;
      return (var2 = var1.b(JsonProperty.class)) != null && var2.access() == JsonProperty.Access.READ_ONLY;
   }

   protected final boolean e(aB<?, ?> var1) {
      JsonProperty var2;
      return (var2 = var1.b(JsonProperty.class)) != null && var2.access() == JsonProperty.Access.WRITE_ONLY;
   }
}
