package org.valkyrienskies.core.impl.shadow;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;

public final class jE<K, V> extends jD<K, V> implements SortedMap<K, V> {
   private static final long d = -8751771676410385778L;

   public static <K, V> jE<K, V> a(SortedMap<K, V> var0, fQ<? super K, ? extends K> var1, fQ<? super V, ? extends V> var2) {
      return new jE<>(var0, var1, var2);
   }

   private static <K, V> jE<K, V> b(SortedMap<K, V> var0, fQ<? super K, ? extends K> var1, fQ<? super V, ? extends V> var2) {
      jE var4 = new jE(var0, var1, var2);
      if (var0.size() > 0) {
         Map var3 = var4.a(var0);
         var4.clear();
         var4.i().putAll(var3);
      }

      return var4;
   }

   private jE(SortedMap<K, V> var1, fQ<? super K, ? extends K> var2, fQ<? super V, ? extends V> var3) {
      super(var1, var2, var3);
   }

   private SortedMap<K, V> b() {
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
      return new jE<>(fromKey, super.b, super.c);
   }

   @Override
   public final SortedMap<K, V> headMap(K toKey) {
      toKey = (K)((SortedMap)super.a).headMap(toKey);
      return new jE<>(toKey, super.b, super.c);
   }

   @Override
   public final SortedMap<K, V> tailMap(K fromKey) {
      fromKey = (K)((SortedMap)super.a).tailMap(fromKey);
      return new jE<>(fromKey, super.b, super.c);
   }
}
