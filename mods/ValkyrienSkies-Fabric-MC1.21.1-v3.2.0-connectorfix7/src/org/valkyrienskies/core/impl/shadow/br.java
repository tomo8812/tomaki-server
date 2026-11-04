package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class br implements av {
   private final boolean a;
   private final boolean b;

   public br(boolean var1, boolean var2) {
      this.a = var1;
      this.b = var2;
   }

   @Override
   public final at a(ResolvedType var1, aI var2) {
      Object[] var3;
      if ((var3 = var1.getErasedType().getEnumConstants()) != null && var3.length != 0) {
         List var4 = null;
         if (this.a) {
            var4 = this.a(var1, var3, var2);
         }

         if (var4 == null && this.b) {
            var4 = a(var1, var3);
         }

         if (var4 == null) {
            return null;
         }

         ObjectNode var5 = var2.a().o().put(var2.a(aP.TAG_TYPE), var2.a(aP.TAG_TYPE_STRING));
         new aW(var2.a().n()).a(var5, var4, var2);
         return new at(var5);
      } else {
         return null;
      }
   }

   private List<Object> a(ResolvedType var1, Object[] var2, aI var3) {
      var3 = var3;
      var1 = var1;
      Set var9;
      ResolvedMethod var10;
      if ((
            var10 = (var9 = Stream.of(var3.b().a(var1).getMemberMethods())
                        .filter(var0x -> var0x.getArgumentCount() == 0)
                        .filter(
                           var0x -> Optional.ofNullable((JsonValue)var0x.getAnnotations().get(JsonValue.class)).map(JsonValue::value).orElse(Boolean.FALSE)
                        )
                        .collect(Collectors.toSet()))
                     .size()
                  == 1
               ? (ResolvedMethod)var9.iterator().next()
               : null
         )
         == null) {
         return null;
      }

      try {
         ArrayList var13 = new ArrayList(var2.length);
         Object[] var11;
         int var4 = (var11 = var2).length;

         for (int var5 = 0; var5 < var4; var5++) {
            Object var6 = var11[var5];
            var13.add(((Method)var10.getRawMember()).invoke(var6));
         }

         return var13;
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException var7) {
         return null;
      }
   }

   private static ResolvedMethod b(ResolvedType var0, aI var1) {
      Set var2;
      return (var2 = Stream.of(var1.b().a(var0).getMemberMethods())
                  .filter(var0x -> var0x.getArgumentCount() == 0)
                  .filter(var0x -> Optional.ofNullable((JsonValue)var0x.getAnnotations().get(JsonValue.class)).map(JsonValue::value).orElse(Boolean.FALSE))
                  .collect(Collectors.toSet()))
               .size()
            == 1
         ? (ResolvedMethod)var2.iterator().next()
         : null;
   }

   private static List<String> a(ResolvedType var0, Object[] var1) {
      try {
         ArrayList var2 = new ArrayList(var1.length);
         Object[] var8;
         int var3 = (var8 = var1).length;

         for (int var4 = 0; var4 < var3; var4++) {
            Object var5;
            var5 = ((Enum)(var5 = (String)var8[var4])).name();
            JsonProperty var6;
            if ((var6 = var0.getErasedType().getDeclaredField(var5).getAnnotation(JsonProperty.class)) == null) {
               return null;
            }

            var2.add("".equals(var6.value()) ? var5 : var6.value());
         }

         return var2;
      } catch (NoSuchFieldException | SecurityException var7) {
         return null;
      }
   }
}
