package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class ks<E> implements Serializable, Set<E> {
   private static final long a = 5185069727540378940L;
   private ks.a<E> b;
   private final List<Set<E>> c = new ArrayList<>();

   public ks() {
   }

   private ks(Set<E> var1) {
      this.a(var1);
   }

   private ks(Set<E>... var1) {
      Set[] var2 = var1;
      ks var6 = this;
      Set[] var7;
      int var3 = (var7 = var2).length;

      for (int var4 = 0; var4 < var3; var4++) {
         Set var5 = var7[var4];
         var6.a(var5);
      }
   }

   @Override
   public final int size() {
      int var1 = 0;

      for (Set var3 : this.c) {
         var1 += var3.size();
      }

      return var1;
   }

   @Override
   public final boolean isEmpty() {
      Iterator var1 = this.c.iterator();

      while (var1.hasNext()) {
         if (!((Set)var1.next()).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public final boolean contains(Object obj) {
      Iterator var2 = this.c.iterator();

      while (var2.hasNext()) {
         if (((Set)var2.next()).contains(obj)) {
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

      for (Set var3 : this.c) {
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
         throw new UnsupportedOperationException("add() is not supported on CompositeSet without a SetMutator strategy");
      } else {
         return this.b.a();
      }
   }

   @Override
   public final boolean remove(Object obj) {
      Iterator var2 = iY.a(this.c).iterator();

      while (var2.hasNext()) {
         Set var3;
         if ((var3 = (Set)var2.next()).contains(obj)) {
            return var3.remove(obj);
         }
      }

      return false;
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
         throw new UnsupportedOperationException("addAll() is not supported on CompositeSet without a SetMutator strategy");
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

   private void a(ks.a<E> var1) {
      this.b = var1;
   }

   public final synchronized void a(Set<E> var1) {
      Iterator var2 = iY.a(this.c).iterator();

      while (var2.hasNext()) {
         Set var3;
         if (fd.a(var3 = (Set)var2.next(), var1).size() > 0) {
            if (this.b == null) {
               throw new UnsupportedOperationException("Collision adding composited set with no SetMutator set");
            }

            if (fd.a(var3, var1).size() > 0) {
               throw new IllegalArgumentException("Attempt to add illegal entry unresolved by SetMutator.resolveCollision()");
            }
         }
      }

      this.c.add(var1);
   }

   private void a(Set<E> var1, Set<E> var2) {
      this.a(var1);
      this.a(var2);
   }

   private void a(Set<E>... var1) {
      Set[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         Set var4 = var5[var3];
         this.a(var4);
      }
   }

   private void b(Set<E> var1) {
      this.c.remove(var1);
   }

   private Set<E> a() {
      return new HashSet<>(this);
   }

   private List<Set<E>> b() {
      return iY.a(this.c);
   }

   private ks.a<E> c() {
      return this.b;
   }

   @Override
   public final boolean equals(Object obj) {
      Set var2;
      return obj instanceof Set ? (var2 = (Set)obj).size() == this.size() && var2.containsAll(this) : false;
   }

   @Override
   public final int hashCode() {
      byte var1 = 0;

      for (Object var3 : this) {
         var1 += var3 == null ? 0 : var3.hashCode();
      }

      return var1;
   }

   public interface a<E> extends Serializable {
      boolean a();

      boolean b();

      void c();
   }
}
