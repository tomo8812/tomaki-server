package de.maxhenkel.gravestone.corelib;

import java.util.function.Supplier;

public class CachedValue<T> {
   private T value;
   private Supplier<T> supplier;

   public CachedValue(Supplier<T> supplier) {
      this.supplier = supplier;
   }

   public T get() {
      if (this.value == null) {
         this.value = this.supplier.get();
      }

      return this.value;
   }

   public void invalidate() {
      this.value = null;
   }
}
