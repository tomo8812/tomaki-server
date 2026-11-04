package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.program.VSCoreInternal;

public final class FI implements ek<FH> {
   private final Provider<Et> a;
   private final Provider<VSCoreInternal> b;

   private FI(Provider<Et> var1, Provider<VSCoreInternal> var2) {
      this.a = var1;
      this.b = var2;
   }

   private FH a() {
      Et var10000 = this.a.get();
      Provider var2 = this.b;
      Et var1 = var10000;
      return new FH(var1, var2);
   }

   public static FI a(Provider<Et> var0, Provider<VSCoreInternal> var1) {
      return new FI(var0, var1);
   }

   private static FH a(Et var0, Provider<VSCoreInternal> var1) {
      return new FH(var0, var1);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      FI var1 = this;
      Et var10000 = this.a.get();
      Provider var2 = var1.b;
      Et var3 = var10000;
      return new FH(var3, var2);
   }
}
