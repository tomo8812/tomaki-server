package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.HashMap;

public final class gx<K, C> extends gv<C> {
   private static final long a = -5512610452568370038L;
   private final fQ<C, K> b;
   private final fw<K, C> c;
   private final boolean d;

   private static <K, C> gx<K, C> a(Collection<C> var0, fQ<C, K> var1) {
      return new gx<>(var0, var1, jv.a(new HashMap<>()), true);
   }

   private static <K, C> gx<K, C> b(Collection<C> var0, fQ<C, K> var1) {
      return new gx<>(var0, var1, jv.a(new HashMap<>()), false);
   }

   private gx(Collection<C> var1, fQ<C, K> var2, fw<K, C> var3, boolean var4) {
      super(var1);
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.a();
   }

   @Override
   public final boolean add(C object) {
      boolean var2;
      if (var2 = super.add(object)) {
         this.c(object);
      }

      return var2;
   }

   @Override
   public final boolean addAll(Collection<? extends C> coll) {
      boolean var2 = false;

      for (Object var3 : coll) {
         var2 |= this.add(var3);
      }

      return var2;
   }

   @Override
   public final void clear() {
      super.clear();
      this.c.clear();
   }

   @Override
   public final boolean contains(Object object) {
      return this.c.containsKey(this.b.a((C)object));
   }

   @Override
   public final boolean containsAll(Collection<?> coll) {
      for (Object var2 : coll) {
         if (!this.contains(var2)) {
            return false;
         }
      }

      return true;
   }

   private C a(K var1) {
      Collection var2;
      return (C)((var2 = (Collection)this.c.get(var1)) == null ? null : var2.iterator().next());
   }

   private Collection<C> b(K var1) {
      return (Collection<C>)this.c.get(var1);
   }

   private void a() {
      this.c.clear();

      for (Object var2 : this.f()) {
         this.c((C)var2);
      }
   }

   @Override
   public final boolean remove(Object object) {
      boolean var2;
      if (var2 = super.remove(object)) {
         Object var3 = object;
         object = this;
         this.c.remove(((gx)object).b.a((C)var3));
      }

      return var2;
   }

   @Override
   public final boolean removeAll(Collection<?> coll) {
      boolean var2 = false;

      for (Object var3 : coll) {
         var2 |= this.remove(var3);
      }

      return var2;
   }

   @Override
   public final boolean retainAll(Collection<?> coll) {
      boolean var2;
      if (var2 = super.retainAll(coll)) {
         this.a();
      }

      return var2;
   }

   private void c(C var1) {
      Object var2 = this.b.a((C)var1);
      if (this.d && this.c.containsKey(var2)) {
         throw new IllegalArgumentException("Duplicate key in uniquely indexed collection.");
      }

      this.c.put((K)var2, var1);
   }

   private void d(C var1) {
      this.c.remove(this.b.a((C)var1));
   }
}
