package org.valkyrienskies.core.impl.shadow;

public final class iB<K, V> implements fU, fu<K, V> {
   private final fu<? extends K, ? extends V> a;

   public static <K, V> fu<K, V> a(fu<? extends K, ? extends V> var0) {
      if (var0 == null) {
         throw new NullPointerException("MapIterator must not be null");
      } else {
         return var0 instanceof fU ? var0 : new iB<>(var0);
      }
   }

   private iB(fu<? extends K, ? extends V> var1) {
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
