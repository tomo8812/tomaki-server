package org.valkyrienskies.core.impl.shadow;

import java.util.Comparator;
import java.util.SortedMap;

public final class jr<K, V> extends jq<K, V> implements SortedMap<K, V> {
   private static final long c = 2715322183617658933L;

   public static <K, V> jr<K, V> a(SortedMap<K, V> var0, fh<? extends V> var1) {
      return new jr<>(var0, var1);
   }

   public static <K, V> jr<K, V> a(SortedMap<K, V> var0, fQ<? super K, ? extends V> var1) {
      return new jr<>(var0, var1);
   }

   private jr(SortedMap<K, V> var1, fh<? extends V> var2) {
      super(var1, var2);
   }

   private jr(SortedMap<K, V> var1, fQ<? super K, ? extends V> var2) {
      super(var1, var2);
   }

   private SortedMap<K, V> a() {
      return (SortedMap<K, V>)super.a;
   }

   @Override
   public final K firstKey() {
      return ((SortedMap)super.a).firstKey();
   }

   @Override
   public final K lastKey() {
      return ((SortedMap)super.a).lastKey();
   }

   @Override
   public final Comparator<? super K> comparator() {
      return ((SortedMap)super.a).comparator();
   }

   @Override
   public final SortedMap<K, V> subMap(K fromKey, K toKey) {
      fromKey = (K)((SortedMap)super.a).subMap(fromKey, toKey);
      return new jr<>(fromKey, super.b);
   }

   @Override
   public final SortedMap<K, V> headMap(K toKey) {
      toKey = (K)((SortedMap)super.a).headMap(toKey);
      return new jr<>(toKey, super.b);
   }

   @Override
   public final SortedMap<K, V> tailMap(K fromKey) {
      fromKey = (K)((SortedMap)super.a).tailMap(fromKey);
      return new jr<>(fromKey, super.b);
   }
}
