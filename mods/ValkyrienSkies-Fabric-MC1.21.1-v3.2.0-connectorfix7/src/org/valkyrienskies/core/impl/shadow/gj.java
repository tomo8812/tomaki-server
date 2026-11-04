package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public final class gj<E> extends fX<E> implements fU {
   private static final long a = -3190437252665717841L;

   public static <E> fN<E> a(fN<E> var0) {
      return var0 instanceof fU ? var0 : new gj<>(var0);
   }

   private gj(fN<E> var1) {
      super(var1);
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(this.g());
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.a((Collection<E>)var1.readObject());
   }

   @Override
   public final Iterator<E> iterator() {
      return iz.a(this.g().iterator());
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
   public final boolean a(E var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean b(Object var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final Set<E> a() {
      return kC.a(this.g().a());
   }
}
