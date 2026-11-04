package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class jO<K, V> extends jM<K, V> implements Serializable {
   private static final long b = 20151118L;
   private static final int c = 16;
   private static final int d = 3;
   private final int e;

   public jO() {
      this(16, 3);
   }

   private jO(int var1) {
      this(16, var1);
   }

   private jO(int var1, int var2) {
      super(new HashMap<>(var1));
      this.e = var2;
   }

   private jO(fA<? extends K, ? extends V> var1) {
      this(var1.a(), 3);
      super.a(var1);
   }

   private jO(Map<? extends K, ? extends V> var1) {
      this(var1.size(), 3);
      super.a(var1);
   }

   private HashSet<V> m() {
      return new HashSet<>(this.e);
   }

   private void b(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      this.a(var1);
   }

   private void b(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.b(new HashMap<>());
      this.a(var1);
   }

   // $VF: synthetic method
   @Override
   protected final Set k() {
      return this.m();
   }

   // $VF: synthetic method
   @Override
   protected final Collection l() {
      return this.m();
   }
}
