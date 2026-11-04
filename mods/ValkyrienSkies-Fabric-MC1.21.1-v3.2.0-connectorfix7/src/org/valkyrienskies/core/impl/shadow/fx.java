package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class fx {
   private static jQ a = jQ.b(new jN(0, 0));

   private fx() {
   }

   private static <K, V> fA<K, V> a() {
      return a;
   }

   private static <K, V> fA<K, V> a(fA<K, V> var0) {
      return var0 == null ? a : var0;
   }

   private static boolean b(fA<?, ?> var0) {
      return var0 == null || var0.b();
   }

   private static <K, V> Collection<V> a(fA<K, V> var0, K var1) {
      return var0 != null ? var0.e(var1) : null;
   }

   private static <K, V> List<V> b(fA<K, V> var0, K var1) {
      if (var0 != null) {
         Collection var2;
         return (var2 = var0.e(var1)) instanceof List ? (List)var2 : new ArrayList<>(var2);
      } else {
         return null;
      }
   }

   private static <K, V> Set<V> c(fA<K, V> var0, K var1) {
      if (var0 != null) {
         Collection var2;
         return (var2 = var0.e(var1)) instanceof Set ? (Set)var2 : new HashSet<>(var2);
      } else {
         return null;
      }
   }

   private static <K, V> eW<V> d(fA<K, V> var0, K var1) {
      if (var0 != null) {
         Collection var2;
         return (var2 = var0.e(var1)) instanceof eW ? (eW)var2 : new ga<>(var2);
      } else {
         return null;
      }
   }

   private static <K, V> ft<K, V> b() {
      return new jN<>();
   }

   private static <K, V> fM<K, V> c() {
      return new jO<>();
   }

   private static <K, V> fA<K, V> c(fA<? extends K, ? extends V> var0) {
      return jQ.b(var0);
   }

   private static <K, V> fA<K, V> a(fA<K, V> var0, fQ<? super K, ? extends K> var1, fQ<? super V, ? extends V> var2) {
      return jP.a(var0, var1, var2);
   }
}
