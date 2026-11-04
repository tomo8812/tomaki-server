package org.valkyrienskies.core.impl.shadow;

public final class hQ<K, V> implements fE<K, V> {
   private final fE<K, V> a;

   private hQ(fE<K, V> var1) {
      if (var1 == null) {
         throw new NullPointerException("OrderedMapIterator must not be null");
      }

      this.a = var1;
   }

   private fE<K, V> c() {
      return this.a;
   }

   @Override
   public final boolean hasNext() {
      return this.a.hasNext();
   }

   @Override
   public final K next() {
      return this.a.next();
   }

   @Override
   public final boolean hasPrevious() {
      return this.a.hasPrevious();
   }

   @Override
   public final K previous() {
      return this.a.previous();
   }

   @Override
   public final void remove() {
      this.a.remove();
   }

   @Override
   public final K a() {
      return this.a.a();
   }

   @Override
   public final V b() {
      return this.a.b();
   }

   @Override
   public final V a(V var1) {
      return this.a.a((V)var1);
   }
}
