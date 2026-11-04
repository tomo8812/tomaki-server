package org.valkyrienskies.core.impl.shadow;

public final class he<E> implements fb<E> {
   private final int a;
   private final fb<? super E> b;

   public static <E> fb<E> a(int var0, fb<? super E> var1) {
      if (var0 <= 0 || var1 == null) {
         return ho.a();
      } else {
         return var0 == 1 ? var1 : new he<>(var0, var1);
      }
   }

   public he(int var1, fb<? super E> var2) {
      this.a = var1;
      this.b = var2;
   }

   @Override
   public final void a(E var1) {
      for (int var2 = 0; var2 < this.a; var2++) {
         this.b.a((E)var1);
      }
   }

   private fb<? super E> a() {
      return this.b;
   }

   private int b() {
      return this.a;
   }
}
