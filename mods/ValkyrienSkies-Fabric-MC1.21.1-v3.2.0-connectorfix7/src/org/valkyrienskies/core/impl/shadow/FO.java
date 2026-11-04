package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.program.VSCoreInternal;

public final class FO implements ek<FN> {
   private final Provider<DN> a;
   private final Provider<FV> b;
   private final Provider<Et> c;
   private final Provider<VSCoreInternal> d;

   private FO(Provider<DN> var1, Provider<FV> var2, Provider<Et> var3, Provider<VSCoreInternal> var4) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
   }

   private FN a() {
      DN var10000 = this.a.get();
      FV var10001 = this.b.get();
      Et var10002 = this.c.get();
      Provider var4 = this.d;
      Et var3 = var10002;
      FV var2 = var10001;
      DN var1 = var10000;
      return new FN(var1, var2, var3, var4);
   }

   public static FO a(Provider<DN> var0, Provider<FV> var1, Provider<Et> var2, Provider<VSCoreInternal> var3) {
      return new FO(var0, var1, var2, var3);
   }

   private static FN a(DN var0, FV var1, Et var2, Provider<VSCoreInternal> var3) {
      return new FN(var0, var1, var2, var3);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      FO var1 = this;
      DN var10000 = this.a.get();
      FV var10001 = var1.b.get();
      Et var10002 = var1.c.get();
      Provider var4 = var1.d;
      Et var3 = var10002;
      FV var2 = var10001;
      DN var5 = var10000;
      return new FN(var5, var2, var3, var4);
   }
}
