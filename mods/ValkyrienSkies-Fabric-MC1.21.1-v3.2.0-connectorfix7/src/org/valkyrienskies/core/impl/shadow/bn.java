package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class bn implements aD {
   private static String[] a = new String[]{"get", "orElse", "isPresent"};
   private final List<String> b;

   public bn(String... var1) {
      int var10000 = var1.length;
      this.b = Arrays.asList(a);
   }

   @Override
   public final void a(aL var1) {
      var1.c.a(var0 -> a(var0.a.getDeclaringType()));
      var1.d.a(var1x -> a(var1x.a.getDeclaringType()) && !this.b.contains(var1x.m()));
   }

   private static boolean a(ResolvedType var0) {
      return var0.getErasedType() == Optional.class;
   }
}
