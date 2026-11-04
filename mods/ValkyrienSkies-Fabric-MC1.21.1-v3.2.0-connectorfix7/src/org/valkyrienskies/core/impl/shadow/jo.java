package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public final class jo<K, V> extends iZ<K, V> implements Serializable, Cloneable {
   private static final long j = -1788199231038721040L;

   public jo() {
      super(16, 0.75F, 12);
   }

   private jo(int var1) {
      super(var1);
   }

   private jo(int var1, float var2) {
      super(var1, var2);
   }

   private jo(Map<? extends K, ? extends V> var1) {
      super(var1);
   }

   private jo<K, V> a() {
      return (jo<K, V>)super.i();
   }

   private void b(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      this.a(var1);
   }

   private void b(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.a(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final iZ i() {
      return super.i();
   }

   // $VF: synthetic method
   @Override
   public final Object clone() {
      return super.i();
   }
}
