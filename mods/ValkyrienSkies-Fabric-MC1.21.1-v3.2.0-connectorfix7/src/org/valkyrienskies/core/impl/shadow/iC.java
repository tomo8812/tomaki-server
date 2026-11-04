package org.valkyrienskies.core.impl.shadow;

public final class iC<K, V> implements fE<K, V>, fU {
   private final fE<? extends K, ? extends V> a;

   public static <K, V> fE<K, V> a(fE<K, ? extends V> var0) {
      if (var0 == null) {
         throw new NullPointerException("OrderedMapIterator must not be null");
      } else {
         return var0 instanceof fU ? var0 : new iC<>(var0);
      }
   }

   private iC(fE<K, ? extends V> var1) {
      this.a = var1;
   }

   @Override
   public final boolean hasNext() {
      return this.a.hasNext();
   }

   @Override
   public final K next() {
      return (K)this.a.next();
   }

   @Override
   public final boolean hasPrevious() {
      return this.a.hasPrevious();
   }

   @Override
   public final K previous() {
      return (K)this.a.previous();
   }

   @Override
   public final K a() {
      return (K)this.a.a();
   }

   @Override
   public final V b() {
      return (V)this.a.b();
   }

   @Override
   public final V a(V var1) {
      throw new UnsupportedOperationException("setValue() is not supported");
   }

   @Override
   public final void remove() {
      throw new UnsupportedOperationException("remove() is not supported");
   }
}
