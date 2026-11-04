package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class gw<E> implements Serializable, Collection<E> {
   private static final long a = 8417515734108306801L;
   private gw.a<E> b;
   private final List<Collection<E>> c = new ArrayList<>();

   public gw() {
   }

   private gw(Collection<E> var1) {
      this.a(var1);
   }

   private gw(Collection<E> var1, Collection<E> var2) {
      Collection var3 = var2;
      var2 = var1;
      var1 = this;
      this.c.add(var2);
      var1.c.add(var3);
   }

   private gw(Collection<E>... var1) {
      Collection[] var2 = var1;
      this.c.addAll(Arrays.asList(var2));
   }

   @Override
   public final int size() {
      int var1 = 0;

      for (Collection var3 : this.c) {
         var1 += var3.size();
      }

      return var1;
   }

   @Override
   public final boolean isEmpty() {
      Iterator var1 = this.c.iterator();

      while (var1.hasNext()) {
         if (!((Collection)var1.next()).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public final boolean contains(Object obj) {
      Iterator var2 = this.c.iterator();

      while (var2.hasNext()) {
         if (((Collection)var2.next()).contains(obj)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final Iterator<E> iterator() {
      if (this.c.isEmpty()) {
         return hW.b();
      }

      if var1 = new if();

      for (Collection var3 : this.c) {
         var1.a(var3.iterator());
      }

      return var1;
   }

   @Override
   public final Object[] toArray() {
      Object[] var1 = new Object[this.size()];
      int var2 = 0;

      for (Iterator var3 = this.iterator(); var3.hasNext(); var2++) {
         var1[var2] = var3.next();
      }

      return var1;
   }

   @Override
   public final <T> T[] toArray(T[] array) {
      int var2 = this.size();
      if (array.length >= var2) {
         array = array;
      } else {
         array = (T[])Array.newInstance(array.getClass().getComponentType(), var2);
      }

      int var3 = 0;
      Iterator var4 = this.c.iterator();

      while (var4.hasNext()) {
         for (Object var6 : (Collection)var4.next()) {
            array[var3++] = (T)var6;
         }
      }

      if (array.length > var2) {
         array[var2] = null;
      }

      return array;
   }

   @Override
   public final boolean add(E obj) {
      if (this.b == null) {
         throw new UnsupportedOperationException("add() is not supported on CompositeCollection without a CollectionMutator strategy");
      } else {
         return this.b.a();
      }
   }

   @Override
   public final boolean remove(Object obj) {
      if (this.b == null) {
         throw new UnsupportedOperationException("remove() is not supported on CompositeCollection without a CollectionMutator strategy");
      } else {
         return this.b.c();
      }
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

   @Override
   public final boolean addAll(Collection<? extends E> coll) {
      if (this.b == null) {
         throw new UnsupportedOperationException("addAll() is not supported on CompositeCollection without a CollectionMutator strategy");
      } else {
         return this.b.b();
      }
   }

   @Override
   public final boolean removeAll(Collection<?> coll) {
      if (coll.size() == 0) {
         return false;
      }

      boolean var2 = false;

      for (Collection var4 : this.c) {
         var2 |= var4.removeAll(coll);
      }

      return var2;
   }

   @Override
   public final boolean retainAll(Collection<?> coll) {
      boolean var2 = false;

      for (Collection var4 : this.c) {
         var2 |= var4.retainAll(coll);
      }

      return var2;
   }

   @Override
   public final void clear() {
      Iterator var1 = this.c.iterator();

      while (var1.hasNext()) {
         ((Collection)var1.next()).clear();
      }
   }

   private void a(gw.a<E> var1) {
      this.b = var1;
   }

   public final void a(Collection<E> var1) {
      this.c.add(var1);
   }

   private void a(Collection<E> var1, Collection<E> var2) {
      this.c.add(var1);
      this.c.add(var2);
   }

   private void a(Collection<E>... var1) {
      this.c.addAll(Arrays.asList(var1));
   }

   private void b(Collection<E> var1) {
      this.c.remove(var1);
   }

   private Collection<E> a() {
      return new ArrayList<>(this);
   }

   private List<Collection<E>> b() {
      return iY.a(this.c);
   }

   private gw.a<E> c() {
      return this.b;
   }

   public interface a<E> extends Serializable {
      boolean a();

      boolean b();

      boolean c();
   }
}
