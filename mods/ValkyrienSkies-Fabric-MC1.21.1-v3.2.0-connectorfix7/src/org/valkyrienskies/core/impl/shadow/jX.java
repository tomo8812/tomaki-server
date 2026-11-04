package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public final class jX<E> extends jT<E> implements fU {
   private static final long a = 20150611L;

   public static <E> fy<E> a(fy<? extends E> var0) {
      return var0 instanceof fU ? var0 : new jX<>(var0);
   }

   private jX(fy<? extends E> var1) {
      super(var1);
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(this.c());
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.a((Collection<E>)var1.readObject());
   }

   @Override
   public final Iterator<E> iterator() {
      return iz.a(this.c().iterator());
   }

   @Override
   public final boolean add(E object) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean addAll(Collection<? extends E> coll) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean remove(Object object) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean removeAll(Collection<?> coll) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean retainAll(Collection<?> coll) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final int a(E var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final int b(E var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final int c(Object var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final Set<E> a() {
      return kC.a(this.c().a());
   }

   @Override
   public final Set<fy.a<E>> b() {
      return kC.a(this.c().b());
   }
}
