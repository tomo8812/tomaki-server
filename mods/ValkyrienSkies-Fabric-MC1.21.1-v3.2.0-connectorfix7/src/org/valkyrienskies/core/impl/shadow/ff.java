package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

public final class ff {
   private ff() {
   }

   public static <T> T a(Enumeration<T> var0, int var1) {
      int var2;
      fd.a(var2 = var1);

      while (var0.hasMoreElements()) {
         if (--var2 == -1) {
            return (T)var0.nextElement();
         }

         var0.nextElement();
      }

      throw new IndexOutOfBoundsException("Entry does not exist: " + var2);
   }

   private static <E> List<E> a(Enumeration<? extends E> var0) {
      return fq.c(new ic<>(var0));
   }

   private static List<String> a(StringTokenizer var0) {
      ArrayList var1 = new ArrayList(var0.countTokens());

      while (var0.hasMoreTokens()) {
         var1.add(var0.nextToken());
      }

      return var1;
   }
}
