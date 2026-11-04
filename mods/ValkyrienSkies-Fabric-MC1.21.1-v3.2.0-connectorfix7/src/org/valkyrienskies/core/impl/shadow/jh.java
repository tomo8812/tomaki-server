package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public final class jh<K, V> extends iZ<K, V> implements Serializable, Cloneable {
   private static final long j = -7074655917369299456L;

   public jh() {
      super(16, 0.75F, 12);
   }

   private jh(int var1) {
      super(var1);
   }

   private jh(int var1, float var2) {
      super(var1, var2);
   }

   private jh(Map<? extends K, ? extends V> var1) {
      super(var1);
   }

   @Override
   protected final Object a(Object var1) {
      if (var1 == null) {
         return iZ.a;
      }

      for (int var2 = (var1 = var1.toString().toCharArray()).length - 1; var2 >= 0; var2--) {
         var1[var2] = Character.toLowerCase(Character.toUpperCase(var1[var2]));
      }

      return new String(var1);
   }

   private jh<K, V> a() {
      return (jh<K, V>)super.i();
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
