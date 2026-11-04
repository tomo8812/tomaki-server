package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public final class if<E> implements Iterator<E> {
   private final Queue<Iterator<? extends E>> a = new LinkedList<>();
   private Iterator<? extends E> b = null;
   private Iterator<? extends E> c = null;
   private boolean d = false;

   public if() {
   }

   private if(Iterator<? extends E> var1) {
      this.a(var1);
   }

   public if(Iterator<? extends E> var1, Iterator<? extends E> var2) {
      this.a(var1);
      this.a(var2);
   }

   public if(Iterator<? extends E>... var1) {
      Iterator[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         Iterator var4 = var5[var3];
         this.a(var4);
      }
   }

   public if(Collection<Iterator<? extends E>> var1) {
      for (Iterator var2 : var1) {
         this.a(var2);
      }
   }

   public final void a(Iterator<? extends E> var1) {
      this.c();
      if (var1 == null) {
         throw new NullPointerException("Iterator must not be null");
      }

      this.a.add(var1);
   }

   private int a() {
      return this.a.size();
   }

   private boolean b() {
      return this.d;
   }

   private void c() {
      if (this.d) {
         throw new UnsupportedOperationException("IteratorChain cannot be changed after the first use of a method from the Iterator interface");
      }
   }

   private void d() {
      if (!this.d) {
         this.d = true;
      }
   }

   private void e() {
      if (this.b == null) {
         if (this.a.isEmpty()) {
            this.b = hW.b();
         } else {
            this.b = this.a.remove();
         }

         this.c = this.b;
      }

      while (!this.b.hasNext() && !this.a.isEmpty()) {
         this.b = this.a.remove();
      }
   }

   @Override
   public final boolean hasNext() {
      this.d();
      this.e();
      this.c = this.b;
      return this.b.hasNext();
   }

   @Override
   public final E next() {
      this.d();
      this.e();
      this.c = this.b;
      return (E)this.b.next();
   }

   @Override
   public final void remove() {
      this.d();
      if (this.b == null) {
         this.e();
      }

      this.c.remove();
   }
}
