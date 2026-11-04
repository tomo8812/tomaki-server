package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;

public final class ih<E> implements Iterable<E> {
   private final Iterator<? extends E> a;
   private final Iterator<E> b;

   private static <E> Iterator<E> a(Iterator<? extends E> var0) {
      return new Iterator<E>(var0) {
         // $VF: synthetic field
         private Iterator a;

         {
            this.a = var1;
         }

         @Override
         public final boolean hasNext() {
            return this.a.hasNext();
         }

         @Override
         public final E next() {
            return (E)this.a.next();
         }

         @Override
         public final void remove() {
            this.a.remove();
         }
      };
   }

   private ih(Iterator<? extends E> var1) {
      this(var1, false);
   }

   public ih(Iterator<? extends E> var1, boolean var2) {
      if (var2 && !(var1 instanceof fJ)) {
         this.a = new ij<>(var1);
      } else {
         this.a = var1;
      }

      var1 = this.a;
      this.b = new Iterator<E>(var1) {
         // $VF: synthetic field
         private Iterator a;

         {
            this.a = var1;
         }

         @Override
         public final boolean hasNext() {
            return this.a.hasNext();
         }

         @Override
         public final E next() {
            return (E)this.a.next();
         }

         @Override
         public final void remove() {
            this.a.remove();
         }
      };
   }

   @Override
   public final Iterator<E> iterator() {
      if (this.a instanceof fJ) {
         ((fJ)this.a).c();
      }

      return this.b;
   }
}
