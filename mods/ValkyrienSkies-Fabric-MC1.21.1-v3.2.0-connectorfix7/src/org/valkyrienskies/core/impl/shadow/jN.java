package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class jN<K, V> extends jJ<K, V> implements Serializable {
   private static final long b = 20151118L;
   private static final int c = 16;
   private static final int d = 3;
   private final int e;

   public jN() {
      this(16, 3);
   }

   private jN(int var1) {
      this(16, var1);
   }

   public jN(int var1, int var2) {
      super(new HashMap<>(var1));
      this.e = var2;
   }

   public jN(fA<? extends K, ? extends V> var1) {
      this(var1.a(), 3);
      super.a(var1);
   }

   private jN(Map<? extends K, ? extends V> var1) {
      this(var1.size(), 3);
      super.a(var1);
   }

   private ArrayList<V> m() {
      return new ArrayList<>(this.e);
   }

   private void n() {
      Iterator var1 = this.j().values().iterator();

      while (var1.hasNext()) {
         ((ArrayList)((Collection)var1.next())).trimToSize();
      }
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
   protected final List k() {
      return this.m();
   }

   // $VF: synthetic method
   @Override
   protected final Collection l() {
      return this.m();
   }
}
