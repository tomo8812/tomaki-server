package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

public class jk<K, V> implements fJ<K>, fu<K, V> {
   private Set<Entry<K, V>> c;
   transient Iterator<Entry<K, V>> a;
   transient Entry<K, V> b;

   public jk(Set<Entry<K, V>> var1) {
      this.c = var1;
      this.c();
   }

   @Override
   public final K a() {
      return this.d().getKey();
   }

   @Override
   public final V b() {
      return this.d().getValue();
   }

   @Override
   public final V a(V var1) {
      return this.d().setValue((V)var1);
   }

   @Override
   public boolean hasNext() {
      return this.a.hasNext();
   }

   @Override
   public K next() {
      this.b = this.a.next();
      return this.a();
   }

   @Override
   public synchronized void c() {
      this.a = this.c.iterator();
   }

   @Override
   public void remove() {
      this.a.remove();
      this.b = null;
   }

   private synchronized Entry<K, V> d() {
      if (this.b == null) {
         throw new IllegalStateException();
      } else {
         return this.b;
      }
   }
}
