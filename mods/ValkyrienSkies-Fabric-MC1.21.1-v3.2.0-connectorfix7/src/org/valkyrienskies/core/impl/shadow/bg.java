package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class bg implements aD {
   private final Function<Enum<?>, String> a;

   public static bg a() {
      return new bg(Enum::name);
   }

   public static bg b() {
      return new bg(Enum::toString);
   }

   public static bg c() {
      return new bg(null);
   }

   private bg(Function<Enum<?>, String> var1) {
      this.a = var1;
   }

   @Override
   public final void a(aL var1) {
      if (this.a == null) {
         var1.d.a(var0 -> a(var0.a.getDeclaringType()) && !"name".equals(var0.m())).a(var0 -> a(var0.a.getDeclaringType()) ? Boolean.FALSE : null).g(bg::a);
         var1.c.a(var0 -> ((Field)var0.a.getRawMember()).isEnumConstant());
      } else {
         var1.b.a(new bg.a(this.a));
      }
   }

   private static boolean a(ResolvedType var0) {
      return var0.getErasedType() == Enum.class;
   }

   private static List<String> a(aC var0) {
      ResolvedType var1;
      return a(var1 = var0.a.getDeclaringType()) ? b((ResolvedType)var1.getTypeParameters().get(0), Enum::name) : null;
   }

   private static List<String> b(ResolvedType var0, Function<Enum<?>, String> var1) {
      Class var2;
      return (var2 = var0.getErasedType()).getEnumConstants() == null
         ? null
         : Stream.of(var2.getEnumConstants()).map(var1x -> (String)var1.apply(var1x)).collect(Collectors.toList());
   }

   static class a implements av {
      private final Function<Enum<?>, String> a;

      a(Function<Enum<?>, String> var1) {
         this.a = var1;
      }

      @Override
      public final at a(ResolvedType var1, aI var2) {
         if (var1.isInstanceOf(Enum.class)) {
            ObjectNode var3 = var2.a().o().put(var2.a(aP.TAG_TYPE), var2.a(aP.TAG_TYPE_STRING));
            new aW(var2.a().n()).a(var3, bg.b(var1, this.a), var2);
            return new at(var3);
         } else {
            return null;
         }
      }
   }
}
