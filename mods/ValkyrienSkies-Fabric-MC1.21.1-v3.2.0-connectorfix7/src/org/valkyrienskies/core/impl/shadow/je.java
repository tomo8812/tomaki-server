package org.valkyrienskies.core.impl.shadow;

import java.util.Map;

public abstract class je<K, V> extends jd<K, V> implements fD<K, V> {
   protected je() {
   }

   public je(fD<K, V> var1) {
      super(var1);
   }

   protected final fD<K, V> a() {
      return (fD<K, V>)super.i();
   }

   @Override
   public K firstKey() {
      return ((fD)super.i()).firstKey();
   }

   @Override
   public K lastKey() {
      return ((fD)super.i()).lastKey();
   }

   @Override
   public final K c(K var1) {
      return ((fD)super.i()).c((K)var1);
   }

   @Override
   public final K d(K var1) {
      return ((fD)super.i()).d((K)var1);
   }

   @Override
   public fE<K, V> e() {
      return ((fD)super.i()).e();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected final Map i() {
      return super.i();
   }

   // $VF: synthetic method
   @Override
   public fu c() {
      return this.e();
   }
}
