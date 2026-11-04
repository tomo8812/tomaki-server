package org.valkyrienskies.core.impl.shadow;

public final class hP<K, V> implements fu<K, V> {
   private final fu<K, V> a;

   private hP(fu<K, V> var1) {
      if (var1 == null) {
         throw new NullPointerException("MapIterator must not be null");
      }

      this.a = var1;
   }

   private fu<K, V> c() {
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
