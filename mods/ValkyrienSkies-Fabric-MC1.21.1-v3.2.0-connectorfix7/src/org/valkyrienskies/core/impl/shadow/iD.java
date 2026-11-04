package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class iD<E> implements Iterator<E> {
   private final Iterator<Iterator<? extends E>> a;
   private Iterator<? extends E> b = null;
   private Iterator<? extends E> c = null;

   public iD(Iterator<? extends E> var1, Iterator<? extends E> var2) {
      this(var1, var2);
   }

   public iD(Iterator<? extends E> var1, Iterator<? extends E> var2, Iterator<? extends E> var3) {
      this(var1, var2, var3);
   }

   public iD(Iterator<? extends E>... var1) {
      ArrayList var2 = new ArrayList();
      Iterator[] var6;
      int var3 = (var6 = var1).length;

      for (int var4 = 0; var4 < var3; var4++) {
         Iterator var5;
         if ((var5 = var6[var4]) == null) {
            throw new NullPointerException("Iterator must not be null.");
         }

         var2.add(var5);
      }

      this.a = fj.a(fp.a(fj.a(var2).a)).iterator();
   }

   @Override
   public final boolean hasNext() {
      if (this.b != null) {
         return true;
      }

      while (this.a.hasNext()) {
         Iterator var1;
         if ((var1 = this.a.next()).hasNext()) {
            this.b = var1;
            return true;
         }

         this.a.remove();
      }

      return false;
   }

   @Override
   public final E next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      }

      Object var1 = this.b.next();
      this.c = this.b;
      this.b = null;
      return (E)var1;
   }

   @Override
   public final void remove() {
      if (this.c == null) {
         throw new IllegalStateException("No value can be removed at present");
      }

      this.c.remove();
      this.c = null;
   }
}
