package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.members.HierarchicType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class bu implements Comparator<aB<?, ?>> {
   private final boolean a;
   private final Map<Class<?>, List<String>> b = new HashMap<>();
   private final Map<Class<?>, Boolean> c = new HashMap<>();

   public bu(boolean var1) {
      this.a = true;
   }

   private int a(aB<?, ?> var1, aB<?, ?> var2) {
      int var3;
      if ((var3 = aZ.a.compare(var1, var2)) == 0) {
         var3 = this.a(var1) - this.a(var2);
      }

      if (var3 == 0
         && Stream.of(var1, var2).map(var0 -> var0.a.getDeclaringType().getErasedType()).anyMatch(var1x -> this.c.computeIfAbsent((Class<?>)var1x, this::a))) {
         var3 = aZ.b.compare(var1, var2);
      }

      return var3;
   }

   private int a(aB<?, ?> var1) {
      HierarchicType var2 = (HierarchicType)var1.b.allTypesAndOverrides().get(0);
      List var5 = this.b.computeIfAbsent(var2.getErasedType(), this::b);
      String var3;
      if (var1 instanceof aC) {
         var3 = Optional.ofNullable(((aC)var1).c()).map(aB::s).orElse(null);
      } else {
         var3 = var1.s();
      }

      int var4;
      if ((var4 = var5.indexOf(var3)) == -1) {
         var4 = Integer.MAX_VALUE;
      }

      return var4;
   }

   protected final boolean a(Class<?> var1) {
      return Optional.ofNullable(var1.getAnnotation(JsonPropertyOrder.class)).map(JsonPropertyOrder::alphabetic).orElse(this.a);
   }

   private List<String> b(Class<?> var1) {
      return Optional.ofNullable(var1.getAnnotation(JsonPropertyOrder.class))
         .map(JsonPropertyOrder::value)
         .filter(var0 -> var0.length != 0)
         .map(Arrays::asList)
         .orElseGet(Collections::emptyList);
   }

   // $VF: synthetic method
   @Override
   public final int compare(Object var1, Object var2) {
      aB var10001 = (aB)var1;
      aB var3 = var2;
      var2 = var10001;
      bu var5 = this;
      int var4;
      if ((var4 = aZ.a.compare(var2, var3)) == 0) {
         var4 = var5.a(var2) - var5.a(var3);
      }

      if (var4 == 0
         && Stream.of(var2, var3).map(var0 -> var0.a.getDeclaringType().getErasedType()).anyMatch(var1x -> this.c.computeIfAbsent((Class<?>)var1x, this::a))) {
         var4 = aZ.b.compare(var2, var3);
      }

      return var4;
   }
}
