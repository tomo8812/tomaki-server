package org.valkyrienskies.core.impl.shadow;

import java.util.function.Supplier;

public final class aY<T> {
   private final Supplier<? extends T> a;
   private boolean b;
   private T c;

   public aY(Supplier<? extends T> var1) {
      this.a = var1;
      this.b = true;
   }

   public final T a() {
      if (this.b) {
         this.c = (T)this.a.get();
         this.b = false;
      }

      return this.c;
   }
}
