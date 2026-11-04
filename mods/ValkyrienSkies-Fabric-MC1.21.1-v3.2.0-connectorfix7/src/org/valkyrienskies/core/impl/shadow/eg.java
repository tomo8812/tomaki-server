package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

public final class eg {
   private static final int a = 1073741824;

   private eg() {
   }

   public static <T> List<T> a(int size) {
      return size == 0 ? Collections.emptyList() : new ArrayList<>(size);
   }

   public static <T> boolean a(List<T> list) {
      if (list.size() < 2) {
         return false;
      }

      HashSet var1 = new HashSet(list);
      return list.size() != var1.size();
   }

   static <T> HashSet<T> b(int expectedSize) {
      return new HashSet<>(d(expectedSize));
   }

   public static <K, V> LinkedHashMap<K, V> c(int expectedSize) {
      return new LinkedHashMap<>(d(expectedSize));
   }

   static int d(int expectedSize) {
      if (expectedSize < 3) {
         return expectedSize + 1;
      } else {
         return expectedSize < 1073741824 ? (int)(expectedSize / 0.75F + 1.0F) : Integer.MAX_VALUE;
      }
   }
}
