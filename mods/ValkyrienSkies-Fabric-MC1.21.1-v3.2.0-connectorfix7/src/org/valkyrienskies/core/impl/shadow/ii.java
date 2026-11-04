package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;

public abstract class ii<E> implements Iterator<E> {
   private int a = 0;
   private boolean b = false;
   private Iterator<? extends E> c = null;
   private Iterator<? extends E> d = null;

   protected abstract Iterator<? extends E> a(int var1);

   private void a() {
      if (this.a == 0) {
         this.c = this.a(++this.a);
         if (this.c == null) {
            this.c = hW.b();
            this.b = true;
         }

         this.d = this.c;
      }

      while (!this.c.hasNext() && !this.b) {
         Iterator var1;
         if ((var1 = this.a(++this.a)) != null) {
            this.c = var1;
         } else {
            this.b = true;
         }
      }
   }

   @Override
   public boolean hasNext() {
      this.a();
      this.d = this.c;
      return this.c.hasNext();
   }

   @Override
   public E next() {
      this.a();
      this.d = this.c;
      return (E)this.c.next();
   }

   @Override
   public void remove() {
      if (this.c == null) {
         this.a();
      }

      this.d.remove();
   }
}
