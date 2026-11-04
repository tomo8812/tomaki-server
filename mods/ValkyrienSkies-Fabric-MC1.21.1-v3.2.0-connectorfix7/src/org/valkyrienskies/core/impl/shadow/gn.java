package org.valkyrienskies.core.impl.shadow;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;

public abstract class gn<K, V> extends gm<K, V> implements fO<K, V> {
   public gn(fO<K, V> var1) {
      super(var1);
   }

   protected final fO<K, V> k() {
      return (fO<K, V>)super.j();
   }

   @Override
   public fO<V, K> f() {
      return ((fO)super.j()).f();
   }

   @Override
   public Comparator<? super K> comparator() {
      return ((fO)super.j()).comparator();
   }

   @Override
   public final Comparator<? super V> g() {
      return ((fO)super.j()).g();
   }

   @Override
   public SortedMap<K, V> subMap(K fromKey, K toKey) {
      return ((fO)super.j()).subMap(fromKey, toKey);
   }

   @Override
   public SortedMap<K, V> headMap(K toKey) {
      return ((fO)super.j()).headMap(toKey);
   }

   @Override
   public SortedMap<K, V> tailMap(K fromKey) {
      return ((fO)super.j()).tailMap(fromKey);
   }

   // $VF: synthetic method
   @Override
   public fB d() {
      return this.f();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected final fB j() {
      return super.j();
   }

   // $VF: synthetic method
   @Override
   public eY a() {
      return this.f();
   }

   // $VF: synthetic method
   @Override
   protected final eY h() {
      return super.j();
   }

   // $VF: synthetic method
   @Override
   public final Map i() {
      return super.j();
   }
}
