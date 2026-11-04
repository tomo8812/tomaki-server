package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;
import org.valkyrienskies.core.internal.hooks.VsiCoreHooksOut;

public final class EZ implements ek<EX> {
   private final Provider<VsiCoreHooksOut> a;
   private final Provider<Fb> b;

   private EZ(Provider<VsiCoreHooksOut> var1, Provider<Fb> var2) {
      this.a = var1;
      this.b = var2;
   }

   private EX a() {
      VsiCoreHooksOut var10000 = this.a.get();
      Fb var2 = this.b.get();
      VsiCoreHooksOut var1 = var10000;
      return new EX(var1, var2);
   }

   public static EZ a(Provider<VsiCoreHooksOut> var0, Provider<Fb> var1) {
      return new EZ(var0, var1);
   }

   private static EX a(VsiCoreHooksOut var0, Fb var1) {
      return new EX(var0, var1);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      EZ var1 = this;
      VsiCoreHooksOut var10000 = this.a.get();
      Fb var2 = var1.b.get();
      VsiCoreHooksOut var3 = var10000;
      return new EX(var3, var2);
   }
}
