package org.valkyrienskies.core.impl.shadow;

import java.util.Comparator;
import java.util.SortedMap;

public final class jy<K, V> extends jx<K, V> implements SortedMap<K, V> {
   private static final long d = 3359846175935304332L;

   public static <K, V> jy<K, V> a(SortedMap<K, V> var0, fF<? super K> var1, fF<? super V> var2) {
      return new jy<>(var0, var1, var2);
   }

   private jy(SortedMap<K, V> var1, fF<? super K> var2, fF<? super V> var3) {
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
      return new jy<>(fromKey, super.b, super.c);
   }

   @Override
   public final SortedMap<K, V> headMap(K toKey) {
      toKey = (K)((SortedMap)super.a).headMap(toKey);
      return new jy<>(toKey, super.b, super.c);
   }

   @Override
   public final SortedMap<K, V> tailMap(K fromKey) {
      fromKey = (K)((SortedMap)super.a).tailMap(fromKey);
      return new jy<>(fromKey, super.b, super.c);
   }
}
