package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

public final class kl<T> implements kf<T> {
   private final List<T> a = new ArrayList<>();
   private final List<T> b = new ArrayList<>();
   private int c = 0;
   private final km<T> d;

   private kl(km<T> var1) {
      this.d = var1;
   }

   @Override
   public final void a(T var1) {
      this.a.add((T)var1);
   }

   @Override
   public final void c(T var1) {
      if (this.b.isEmpty() && this.a.isEmpty()) {
         this.c++;
      } else {
         this.b.clear();
         this.a.clear();
         this.c = 1;
      }
   }

   @Override
   public final void b(T var1) {
      this.b.add((T)var1);
   }
}
