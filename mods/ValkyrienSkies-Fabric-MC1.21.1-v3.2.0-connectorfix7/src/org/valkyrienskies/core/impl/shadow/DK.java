package org.valkyrienskies.core.impl.shadow;

public final class DK<T> extends DH<DK<T>> {
   public T a;
   public int b;

   public DK(T var1) {
      this.a = (T)var1;
   }

   @Override
   public final boolean b() {
      int var1;
      if ((var1 = ((DK)super.j).b + ((DK)super.k).b + 1) == this.b) {
         return false;
      }

      this.b = var1;
      return true;
   }

   @Override
   public final void c() {
      int var1;
      if (this.d()) {
         var1 = 0;
      } else {
         var1 = ((DK)super.j).b + ((DK)super.k).b + 1;
      }

      if (this.b != var1) {
         throw new RuntimeException("The node's size does not match that of the children");
      }
   }
}
