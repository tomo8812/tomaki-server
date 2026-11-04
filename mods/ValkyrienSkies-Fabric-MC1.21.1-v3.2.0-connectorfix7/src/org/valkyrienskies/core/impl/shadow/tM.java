package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.List;

public abstract class tM<T extends tL> {
   private tW a;

   protected tM(tW var1) {
      this.a = var1;
   }

   public abstract List<? extends tK<T>> a(Collection<T> var1);

   public final tW a() {
      return this.a;
   }

   protected final double a(tL var1, tL var2) {
      return this.a.a(var1.a(), var2.a());
   }
}
