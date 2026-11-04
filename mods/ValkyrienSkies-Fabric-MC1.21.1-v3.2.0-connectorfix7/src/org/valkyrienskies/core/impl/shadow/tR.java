package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.List;

public final class tR<T extends tL> extends tM<T> {
   private final tQ<T> a;
   private final int b;
   private final tS<T> c;

   private tR(tQ<T> var1, int var2) {
      this(var1, var2, new tT<>(var1.a()));
   }

   private tR(tQ<T> var1, int var2, tS<T> var3) {
      super(var1.a());
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   private tQ<T> b() {
      return this.a;
   }

   private int c() {
      return this.b;
   }

   private tS<T> d() {
      return this.c;
   }

   @Override
   public final List<tJ<T>> a(Collection<T> var1) {
      List var2 = null;
      double var3 = Double.POSITIVE_INFINITY;

      for (int var5 = 0; var5 < this.b; var5++) {
         List var6 = this.a.a(var1);
         double var7 = this.c.a(var6);
         double var11 = var3;
         if (var7 < var11) {
            var2 = var6;
            var3 = var7;
         }
      }

      return var2;
   }
}
