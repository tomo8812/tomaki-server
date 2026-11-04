package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;

public final class ix<I, O> implements Iterator<O> {
   private Iterator<? extends I> a;
   private fQ<? super I, ? extends O> b;

   public ix() {
   }

   private ix(Iterator<? extends I> var1) {
      this.a = var1;
   }

   public ix(Iterator<? extends I> var1, fQ<? super I, ? extends O> var2) {
      this.a = var1;
      this.b = var2;
   }

   @Override
   public final boolean hasNext() {
      return this.a.hasNext();
   }

   @Override
   public final O next() {
      Object var2 = this.a.next();
      return (O)this.b.a((I)var2);
   }

   @Override
   public final void remove() {
      this.a.remove();
   }

   private Iterator<? extends I> a() {
      return this.a;
   }

   private void a(Iterator<? extends I> var1) {
      this.a = var1;
   }

   private fQ<? super I, ? extends O> b() {
      return this.b;
   }

   private void a(fQ<? super I, ? extends O> var1) {
      this.b = var1;
   }

   private O a(I var1) {
      return (O)this.b.a((I)var1);
   }
}
