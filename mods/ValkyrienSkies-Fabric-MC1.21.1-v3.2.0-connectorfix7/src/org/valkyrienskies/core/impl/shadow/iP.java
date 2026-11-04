package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class iP<E> extends iM<E> implements Serializable {
   private static final long e = 8836393098519411393L;
   transient List<WeakReference<iP.a<E>>> d;

   public iP() {
      this.a();
   }

   private iP(Collection<? extends E> var1) {
      super(var1);
   }

   @Override
   protected final void a() {
      super.a();
      this.d = new ArrayList<>();
   }

   @Override
   public final Iterator<E> iterator() {
      return super.listIterator(0);
   }

   @Override
   public final ListIterator<E> listIterator() {
      return this.a(0);
   }

   @Override
   public final ListIterator<E> listIterator(int fromIndex) {
      return this.a(fromIndex);
   }

   private iP.a<E> c() {
      return this.a(0);
   }

   private iP.a<E> a(int var1) {
      iP.a var2 = new iP.a<>(this, var1);
      this.b(var2);
      return var2;
   }

   @Override
   protected final void a(iM.d<E> var1, E var2) {
      super.a(var1, (E)var2);
      Iterator var3 = this.d.iterator();

      while (var3.hasNext()) {
         if ((iP.a)((WeakReference)var3.next()).get() == null) {
            var3.remove();
         }
      }
   }

   @Override
   protected final void a(iM.d<E> var1, iM.d<E> var2) {
      super.a(var1, var2);
      var2 = var1;
      Iterator var4 = this.d.iterator();

      while (var4.hasNext()) {
         iP.a var3;
         if ((var3 = (iP.a)((WeakReference)var4.next()).get()) == null) {
            var4.remove();
         } else {
            var3.b(var2);
         }
      }
   }

   @Override
   protected final void a(iM.d<E> var1) {
      super.a(var1);
      iM.d var2 = var1;
      Iterator var4 = this.d.iterator();

      while (var4.hasNext()) {
         iP.a var3;
         if ((var3 = (iP.a)((WeakReference)var4.next()).get()) == null) {
            var4.remove();
         } else {
            var3.a(var2);
         }
      }
   }

   @Override
   protected final void b() {
      if (this.size() > 0) {
         Iterator var1 = this.iterator();

         while (var1.hasNext()) {
            var1.next();
            var1.remove();
         }
      }
   }

   private void b(iP.a<E> var1) {
      Iterator var2 = this.d.iterator();

      while (var2.hasNext()) {
         if (((WeakReference)var2.next()).get() == null) {
            var2.remove();
         }
      }

      this.d.add(new WeakReference<>(var1));
   }

   protected final void a(iP.a<E> var1) {
      Iterator var2 = this.d.iterator();

      while (var2.hasNext()) {
         WeakReference var3;
         iP.a var4;
         if ((var4 = (iP.a)(var3 = (WeakReference)var2.next()).get()) == null) {
            var2.remove();
         } else if (var4 == var1) {
            var3.clear();
            var2.remove();
            return;
         }
      }
   }

   private void b(iM.d<E> var1) {
      Iterator var3 = this.d.iterator();

      while (var3.hasNext()) {
         if ((iP.a)((WeakReference)var3.next()).get() == null) {
            var3.remove();
         }
      }
   }

   private void c(iM.d<E> var1) {
      Iterator var2 = this.d.iterator();

      while (var2.hasNext()) {
         iP.a var3;
         if ((var3 = (iP.a)((WeakReference)var2.next()).get()) == null) {
            var2.remove();
         } else {
            var3.a(var1);
         }
      }
   }

   private void d(iM.d<E> var1) {
      Iterator var2 = this.d.iterator();

      while (var2.hasNext()) {
         iP.a var3;
         if ((var3 = (iP.a)((WeakReference)var2.next()).get()) == null) {
            var2.remove();
         } else {
            var3.b(var1);
         }
      }
   }

   private void b(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      this.a(var1);
   }

   private void b(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.a(var1);
   }

   @Override
   protected final ListIterator<E> a(iM.b<E> var1, int var2) {
      iP.b var3 = new iP.b(var1, var2);
      this.b(var3);
      return var3;
   }

   public static class a<E> extends iM.a<E> {
      private boolean e = true;
      private boolean f = true;
      private boolean g = false;

      protected a(iP<E> var1, int var2) {
         super(var1, var2);
         this.e = true;
      }

      @Override
      public void remove() {
         if (super.d != null || !this.g) {
            this.a();
            super.a.a(this.b());
         }

         this.g = false;
      }

      @Override
      public void add(E obj) {
         super.add(obj);
         super.b = super.b.b;
      }

      @Override
      public int nextIndex() {
         if (!this.f) {
            if (super.b == super.a.a) {
               super.c = super.a.size();
            } else {
               int var1 = 0;

               for (iM.d var2 = super.a.a.b; var2 != super.b; var2 = var2.b) {
                  var1++;
               }

               super.c = var1;
            }

            this.f = true;
         }

         return super.c;
      }

      protected static void c() {
      }

      protected final void a(iM.d<E> var1) {
         if (var1 == super.b && var1 == super.d) {
            super.b = var1.b;
            super.d = null;
            this.g = true;
         } else if (var1 == super.b) {
            super.b = var1.b;
            this.g = false;
         } else if (var1 == super.d) {
            super.d = null;
            this.g = true;
            super.c--;
         } else {
            this.f = false;
            this.g = false;
         }
      }

      protected final void b(iM.d<E> var1) {
         if (var1.a == super.d) {
            super.b = var1;
         } else if (super.b.a == var1) {
            super.b = var1;
         } else {
            this.f = false;
         }
      }

      @Override
      protected final void a() {
         if (!this.e) {
            throw new ConcurrentModificationException("Cursor closed");
         }
      }

      private void d() {
         if (this.e) {
            iP var10000 = (iP)super.a;
            iP.a var2 = this;
            Iterator var1 = var10000.d.iterator();

            while (var1.hasNext()) {
               WeakReference var3;
               iP.a var4;
               if ((var4 = (iP.a)(var3 = (WeakReference)var1.next()).get()) == null) {
                  var1.remove();
               } else if (var4 == var2) {
                  var3.clear();
                  var1.remove();
                  break;
               }
            }

            this.e = false;
         }
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public void set(Object var1) {
         super.set((E)var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public int previousIndex() {
         return super.previousIndex();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Object previous() {
         return super.previous();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public boolean hasPrevious() {
         return super.hasPrevious();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Object next() {
         return super.next();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public boolean hasNext() {
         return super.hasNext();
      }
   }

   protected static class b<E> extends iP.a<E> {
      private iM.b<E> e;

      protected b(iM.b<E> var1, int var2) {
         super((iP<E>)var1.a, var2 + var1.b);
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
}
