package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class jQ<K, V> extends jL<K, V> implements fU {
   private static final long a = 20150612L;

   public static <K, V> jQ<K, V> b(fA<? extends K, ? extends V> var0) {
      return var0 instanceof fU ? (jQ)var0 : new jQ<>(var0);
   }

   private jQ(fA<? extends K, ? extends V> var1) {
      super(var1);
   }

   @Override
   public final Collection<V> f(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean c(Object var1, Object var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final void c() {
      throw new UnsupportedOperationException();
   }

   @Override
   public final Collection<V> e(K var1) {
      return gC.b(this.j().e((K)var1));
   }

   @Override
   public final boolean b(K var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final Set<K> f() {
      return kC.a(this.j().f());
   }

   @Override
   public final Collection<Entry<K, V>> d() {
      return gC.b(this.j().d());
   }

   @Override
   public final fy<K> e() {
      return jX.a(this.j().e());
   }

   @Override
   public final Collection<V> g() {
      return gC.b(this.j().g());
   }

   @Override
   public final Map<K, Collection<V>> h() {
      return jG.a(this.j().h());
   }

   @Override
   public final fu<K, V> i() {
      return iB.a(this.j().i());
   }

   @Override
   public final boolean a(K var1, Iterable<? extends V> var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean a(Map<? extends K, ? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean a(fA<? extends K, ? extends V> var1) {
      throw new UnsupportedOperationException();
   }
}
