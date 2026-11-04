package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class gk<K, V> extends jd<K, V> implements eY<K, V> {
   protected gk(eY<K, V> var1) {
      super(var1);
   }

   protected eY<K, V> h() {
      return (eY<K, V>)super.i();
   }

   @Override
   public fu<K, V> c() {
      return this.h().c();
   }

   @Override
   public final K a(Object var1) {
      return this.h().a(var1);
   }

   @Override
   public K b(Object var1) {
      return this.h().b(var1);
   }

   @Override
   public eY<V, K> a() {
      return this.h().a();
   }

   @Override
   public Set<V> b() {
      return this.h().b();
   }

   // $VF: synthetic method
   @Override
   public Collection values() {
      return this.b();
   }

   // $VF: synthetic method
   @Override
   public Map i() {
      return this.h();
   }
}
