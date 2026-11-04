package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public final class ib<K, V> implements fJ<K>, fu<K, V> {
   private final Map<K, V> a;
   private Iterator<Entry<K, V>> b;
   private Entry<K, V> c;
   private boolean d = false;

   public ib(Map<K, V> var1) {
      this.a = var1;
      this.b = var1.entrySet().iterator();
   }

   @Override
   public final boolean hasNext() {
      return this.b.hasNext();
   }

   @Override
   public final K next() {
      this.c = this.b.next();
      this.d = true;
      return this.c.getKey();
   }

   @Override
   public final void remove() {
      if (!this.d) {
         throw new IllegalStateException("Iterator remove() can only be called once after next()");
      }

      this.b.remove();
      this.c = null;
      this.d = false;
   }

   @Override
   public final K a() {
      if (this.c == null) {
         throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
      } else {
         return this.c.getKey();
      }
   }

   @Override
   public final V b() {
      if (this.c == null) {
         throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
      } else {
         return this.c.getValue();
      }
   }

   @Override
   public final V a(V var1) {
      if (this.c == null) {
         throw new IllegalStateException("Iterator setValue() can only be called after next() and before remove()");
      } else {
         return this.c.setValue((V)var1);
      }
   }

   @Override
   public final void c() {
      this.b = this.a.entrySet().iterator();
      this.c = null;
      this.d = false;
   }

   @Override
   public final String toString() {
      return this.c != null ? "MapIterator[" + this.a() + "=" + this.b() + "]" : "MapIterator[]";
   }
}
