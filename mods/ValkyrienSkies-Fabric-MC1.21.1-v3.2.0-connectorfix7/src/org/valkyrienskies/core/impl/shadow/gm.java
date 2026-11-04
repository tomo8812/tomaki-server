package org.valkyrienskies.core.impl.shadow;

import java.util.Map;

public abstract class gm<K, V> extends gk<K, V> implements fB<K, V> {
   protected gm(fB<K, V> var1) {
      super(var1);
   }

   protected fB<K, V> j() {
      return (fB<K, V>)super.h();
   }

   @Override
   public fE<K, V> e() {
      return this.j().e();
   }

   @Override
   public K firstKey() {
      return this.j().firstKey();
   }

   @Override
   public K lastKey() {
      return this.j().lastKey();
   }

   @Override
   public final K c(K var1) {
      return this.j().c((K)var1);
   }

   @Override
   public final K d(K var1) {
      return this.j().d((K)var1);
   }

   @Override
   public fB<V, K> d() {
      return this.j().d();
   }

   // $VF: synthetic method
   @Override
   public eY a() {
      return this.d();
   }

   // $VF: synthetic method
   @Override
   public fu c() {
      return this.e();
   }

   // $VF: synthetic method
   @Override
   protected eY h() {
      return this.j();
   }

   // $VF: synthetic method
   @Override
   public Map i() {
      return this.j();
   }
}
