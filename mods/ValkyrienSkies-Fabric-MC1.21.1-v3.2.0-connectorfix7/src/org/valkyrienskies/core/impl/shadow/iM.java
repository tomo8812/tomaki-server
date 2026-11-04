package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public abstract class iM<E> implements List<E> {
   transient iM.d<E> a;
   transient int b;
   transient int c;

   protected iM() {
   }

   protected iM(Collection<? extends E> var1) {
      this.a();
      this.addAll(var1);
   }

   protected void a() {
      this.a = new iM.d<>();
   }

   @Override
   public int size() {
      return this.b;
   }

   @Override
   public boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public E get(int index) {
      return this.a(index, false).a();
   }

   @Override
   public Iterator<E> iterator() {
      return this.listIterator();
   }

   @Override
   public ListIterator<E> listIterator() {
      return new iM.a<>(this, 0);
   }

   @Override
   public ListIterator<E> listIterator(int fromIndex) {
      return new iM.a<>(this, fromIndex);
   }

   @Override
   public int indexOf(Object value) {
      int var2 = 0;

      for (iM.d var3 = this.a.b; var3 != this.a; var3 = var3.b) {
         if (a(var3.a(), value)) {
            return var2;
         }

         var2++;
      }

      return -1;
   }

   @Override
   public int lastIndexOf(Object value) {
      int var2 = this.b - 1;

      for (iM.d var3 = this.a.a; var3 != this.a; var3 = var3.a) {
         if (a(var3.a(), value)) {
            return var2;
         }

         var2--;
      }

      return -1;
   }

   @Override
   public boolean contains(Object value) {
      return this.indexOf(value) != -1;
   }

   @Override
   public boolean containsAll(Collection<?> coll) {
      for (Object var2 : coll) {
         if (!this.contains(var2)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public Object[] toArray() {
      return this.toArray(new Object[this.b]);
   }

   @Override
   public <T> T[] toArray(T[] array) {
      if (array.length < this.b) {
         array = (T[])Array.newInstance(array.getClass().getComponentType(), this.b);
      }

      int var2 = 0;

      for (iM.d var3 = this.a.b; var3 != this.a; var2++) {
         array[var2] = (T)var3.a();
         var3 = var3.b;
      }

      if (array.length > this.b) {
         array[this.b] = null;
      }

      return array;
   }

   @Override
   public List<E> subList(int fromIndexInclusive, int toIndexExclusive) {
      return new iM.b<>(this, fromIndexInclusive, toIndexExclusive);
   }

   @Override
   public boolean add(E value) {
      Object var2 = value;
      value = (E)this;
      this.b(value.a, (E)var2);
      boolean var10000 = true;
      return true;
   }

   @Override
   public void add(int index, E value) {
      iM.d var3 = this.a(index, true);
      this.b(var3, value);
   }

   @Override
   public boolean addAll(Collection<? extends E> coll) {
      return this.addAll(this.b, coll);
   }

   @Override
   public boolean addAll(int index, Collection<? extends E> coll) {
      iM.d var4 = this.a(index, true);

      for (Object var3 : coll) {
         this.b(var4, (E)var3);
      }

      return true;
   }

   @Override
   public E remove(int index) {
      iM.d var3;
      Object var2 = (var3 = this.a(index, false)).a();
      this.a(var3);
      return (E)var2;
   }

   @Override
   public boolean remove(Object value) {
      for (iM.d var2 = this.a.b; var2 != this.a; var2 = var2.b) {
         if (a(var2.a(), value)) {
            this.a(var2);
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean removeAll(Collection<?> coll) {
      boolean var2 = false;
      Iterator var3 = this.iterator();

      while (var3.hasNext()) {
         if (coll.contains(var3.next())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public boolean retainAll(Collection<?> coll) {
      boolean var2 = false;
      Iterator var3 = this.iterator();

      while (var3.hasNext()) {
         if (!coll.contains(var3.next())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public E set(int index, E value) {
      iM.d var4;
      Object var3 = (var4 = this.a(index, false)).a();
      this.a(var4, value);
      return (E)var3;
   }

   @Override
   public void clear() {
      this.b();
   }

   private E c() {
      iM.d var1 = this.a.b;
      if (this.a.b == this.a) {
         throw new NoSuchElementException();
      } else {
         return (E)var1.a();
      }
   }

   private E d() {
      iM.d var1 = this.a.a;
      if (this.a.a == this.a) {
         throw new NoSuchElementException();
      } else {
         return (E)var1.a();
      }
   }

   private boolean b(E var1) {
      iM.d var3 = var1;
      iM.d var2 = this.a;
      var1 = this;
      var3 = this.a((E)var3);
      var1.a(var3, var2.b);
      return true;
   }

   private boolean c(E var1) {
      this.b(this.a, (E)var1);
      return true;
   }

   private E e() {
      iM.d var1 = this.a.b;
      if (this.a.b == this.a) {
         throw new NoSuchElementException();
      }

      Object var2 = var1.a();
      this.a(var1);
      return (E)var2;
   }

   private E f() {
      iM.d var1 = this.a.a;
      if (this.a.a == this.a) {
         throw new NoSuchElementException();
      }

      Object var2 = var1.a();
      this.a(var1);
      return (E)var2;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (!(obj instanceof List)) {
         return false;
      }

      if ((obj = (List)obj).size() != this.size()) {
         return false;
      }

      ListIterator var2 = this.listIterator();
      ListIterator var6 = obj.listIterator();

      while (var2.hasNext() && var6.hasNext()) {
         Object var3 = var2.next();
         Object var4 = var6.next();
         if (var3 == null ? var4 != null : !var3.equals(var4)) {
            return false;
         }
      }

      return !var2.hasNext() && !var6.hasNext();
   }

   @Override
   public int hashCode() {
      int var1 = 1;

      for (Object var3 : this) {
         var1 = var1 * 31 + (var3 == null ? 0 : var3.hashCode());
      }

      return var1;
   }

   @Override
   public String toString() {
      if (this.size() == 0) {
         return "[]";
      }

      StringBuilder var1;
      (var1 = new StringBuilder(16 * this.size())).append('[');
      Iterator var2;
      boolean var3 = (var2 = this.iterator()).hasNext();

      while (var3) {
         Object var4 = var2.next();
         var1.append(var4 == this ? "(this Collection)" : var4);
         if (var3 = var2.hasNext()) {
            var1.append(", ");
         }
      }

      var1.append(']');
      return var1.toString();
   }

   private static boolean a(Object var0, Object var1) {
      return var0 == var1 || var0 != null && var0.equals(var1);
   }

   protected void a(iM.d<E> var1, E var2) {
      var1.a(var2);
   }

   private static iM.d<E> g() {
      return new iM.d<>();
   }

   protected iM.d<E> a(E var1) {
      return new iM.d<>((E)var1);
   }

   protected final void b(iM.d<E> var1, E var2) {
      var2 = this.a((E)var2);
      this.a(var2, var1);
   }

   private void c(iM.d<E> var1, E var2) {
      var2 = this.a((E)var2);
      this.a(var2, var1.b);
   }

   protected void a(iM.d<E> var1, iM.d<E> var2) {
      var1.b = var2;
      var1.a = var2.a;
      var2.a.b = var1;
      var2.a = var1;
      this.b++;
      this.c++;
   }

   protected void a(iM.d<E> var1) {
      var1.a.b = var1.b;
      var1.b.a = var1.a;
      this.b--;
      this.c++;
   }

   protected void b() {
      this.a.b = this.a;
      this.a.a = this.a;
      this.b = 0;
      this.c++;
   }

   protected final iM.d<E> a(int var1, boolean var2) {
      if (var1 < 0) {
         throw new IndexOutOfBoundsException("Couldn't get the node: index (" + var1 + ") less than zero.");
      }

      if (!var2 && var1 == this.b) {
         throw new IndexOutOfBoundsException("Couldn't get the node: index (" + var1 + ") is the size of the list.");
      }

      if (var1 > this.b) {
         throw new IndexOutOfBoundsException("Couldn't get the node: index (" + var1 + ") greater than the size of the list (" + this.b + ").");
      }

      iM.d var4;
      if (var1 < this.b / 2) {
         var4 = this.a.b;

         for (int var3 = 0; var3 < var1; var3++) {
            var4 = var4.b;
         }
      } else {
         var4 = this.a;

         for (int var5 = this.b; var5 > var1; var5--) {
            var4 = var4.a;
         }
      }

      return var4;
   }

   protected final Iterator<E> a(iM.b<E> var1) {
      return this.a(var1, 0);
   }

   protected ListIterator<E> a(iM.b<E> var1, int var2) {
      return new iM.c<>(var1, var2);
   }

   protected final void a(ObjectOutputStream var1) {
      var1.writeInt(this.size());

      for (Object var3 : this) {
         var1.writeObject(var3);
      }
   }

   protected final void a(ObjectInputStream var1) {
      this.a();
      int var2 = var1.readInt();

      for (int var3 = 0; var3 < var2; var3++) {
         this.add((E)var1.readObject());
      }
   }

   protected static class a<E> implements ListIterator<E>, fC<E> {
      protected final iM<E> a;
      protected iM.d<E> b;
      protected int c;
      protected iM.d<E> d;
      private int e;

      protected a(iM<E> var1, int var2) {
         this.a = var1;
         this.e = var1.c;
         this.b = var1.a(var2, true);
         this.c = var2;
      }

      protected void a() {
         if (this.a.c != this.e) {
            throw new ConcurrentModificationException();
         }
      }

      protected final iM.d<E> b() {
         if (this.d == null) {
            throw new IllegalStateException();
         } else {
            return this.d;
         }
      }

      @Override
      public boolean hasNext() {
         return this.b != this.a.a;
      }

      @Override
      public E next() {
         this.a();
         if (!this.hasNext()) {
            throw new NoSuchElementException("No element at index " + this.c + ".");
         }

         Object var1 = this.b.a();
         this.d = this.b;
         this.b = this.b.b;
         this.c++;
         return (E)var1;
      }

      @Override
      public boolean hasPrevious() {
         return this.b.a != this.a.a;
      }

      @Override
      public E previous() {
         this.a();
         if (!this.hasPrevious()) {
            throw new NoSuchElementException("Already at start of list.");
         }

         this.b = this.b.a;
         Object var1 = this.b.a();
         this.d = this.b;
         this.c--;
         return (E)var1;
      }

      @Override
      public int nextIndex() {
         return this.c;
      }

      @Override
      public int previousIndex() {
         return this.nextIndex() - 1;
      }

      @Override
      public void remove() {
         this.a();
         if (this.d == this.b) {
            this.b = this.b.b;
            this.a.a(this.b());
         } else {
            this.a.a(this.b());
            this.c--;
         }

         this.d = null;
         this.e++;
      }

      @Override
      public void set(E obj) {
         this.a();
         this.b().a(obj);
      }

      @Override
      public void add(E obj) {
         this.a();
         this.a.b(this.b, obj);
         this.d = null;
         this.c++;
         this.e++;
      }
   }

   protected static class b<E> extends AbstractList<E> {
      iM<E> a;
      int b;
      int c;
      int d;

      protected b(iM<E> var1, int var2, int var3) {
         if (var2 < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + var2);
         }

         if (var3 > var1.size()) {
            throw new IndexOutOfBoundsException("toIndex = " + var3);
         }

         if (var2 > var3) {
            throw new IllegalArgumentException("fromIndex(" + var2 + ") > toIndex(" + var3 + ")");
         }

         this.a = var1;
         this.b = var2;
         this.c = var3 - var2;
         this.d = var1.c;
      }

      @Override
      public final int size() {
         this.a();
         return this.c;
      }

      @Override
      public final E get(int index) {
         this.a(index, this.c);
         this.a();
         return this.a.get(index + this.b);
      }

      @Override
      public final void add(int index, E obj) {
         this.a(index, this.c + 1);
         this.a();
         this.a.add(index + this.b, obj);
         this.d = this.a.c;
         this.c++;
         this.modCount++;
      }

      @Override
      public final E remove(int index) {
         this.a(index, this.c);
         this.a();
         Object var2 = this.a.remove(index + this.b);
         this.d = this.a.c;
         this.c--;
         this.modCount++;
         return (E)var2;
      }

      @Override
      public final boolean addAll(Collection<? extends E> coll) {
         return this.addAll(this.c, coll);
      }

      @Override
      public final boolean addAll(int index, Collection<? extends E> coll) {
         this.a(index, this.c + 1);
         int var3;
         if ((var3 = coll.size()) == 0) {
            return false;
         }

         this.a();
         this.a.addAll(this.b + index, coll);
         this.d = this.a.c;
         this.c += var3;
         this.modCount++;
         return true;
      }

      @Override
      public final E set(int index, E obj) {
         this.a(index, this.c);
         this.a();
         return this.a.set(index + this.b, obj);
      }

      @Override
      public final void clear() {
         this.a();
         Iterator var1 = this.iterator();

         while (var1.hasNext()) {
            var1.next();
            var1.remove();
         }
      }

      @Override
      public final Iterator<E> iterator() {
         this.a();
         return this.a.a(this);
      }

      @Override
      public final ListIterator<E> listIterator(int index) {
         this.a(index, this.c + 1);
         this.a();
         return this.a.a(this, index);
      }

      @Override
      public final List<E> subList(int fromIndexInclusive, int toIndexExclusive) {
         return new iM.b<>(this.a, fromIndexInclusive + this.b, toIndexExclusive + this.b);
      }

      private void a(int var1, int var2) {
         if (var1 < 0 || var1 >= var2) {
            throw new IndexOutOfBoundsException("Index '" + var1 + "' out of bounds for size '" + this.c + "'");
         }
      }

      private void a() {
         if (this.a.c != this.d) {
            throw new ConcurrentModificationException();
         }
      }
   }

   protected static class c<E> extends iM.a<E> {
      private iM.b<E> e;

      protected c(iM.b<E> var1, int var2) {
         super(var1.a, var2 + var1.b);
         this.e = var1;
      }

      @Override
      public final boolean hasNext() {
         return this.nextIndex() < this.e.c;
      }

      @Override
      public final boolean hasPrevious() {
         return this.previousIndex() >= 0;
      }

      @Override
      public final int nextIndex() {
         return super.nextIndex() - this.e.b;
      }

      @Override
      public final void add(E obj) {
         super.add(obj);
         this.e.d = super.a.c;
         this.e.c++;
      }

      @Override
      public final void remove() {
         super.remove();
         this.e.d = super.a.c;
         this.e.c--;
      }
   }

   protected static class d<E> {
      protected iM.d<E> a;
      protected iM.d<E> b;
      private E c;

      protected d() {
         this.a = this;
         this.b = this;
      }

      protected d(E var1) {
         this.c = (E)var1;
      }

      private d(iM.d<E> var1, iM.d<E> var2, E var3) {
         this.a = var1;
         this.b = var2;
         this.c = (E)var3;
      }

      protected final E a() {
         return this.c;
      }

      protected final void a(E var1) {
         this.c = (E)var1;
      }

      private iM.d<E> b() {
         return this.a;
      }

      private void a(iM.d<E> var1) {
         this.a = var1;
      }

      private iM.d<E> c() {
         return this.b;
      }

      private void b(iM.d<E> var1) {
         this.b = var1;
      }
   }
}
