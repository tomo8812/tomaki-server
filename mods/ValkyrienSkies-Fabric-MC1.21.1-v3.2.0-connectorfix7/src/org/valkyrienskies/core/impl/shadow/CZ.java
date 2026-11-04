package org.valkyrienskies.core.impl.shadow;

import java.util.Map;
import javax.inject.Provider;
import org.valkyrienskies.core.impl.config.VSCoreConfig;

public final class CZ implements ek<CY> {
   private final Provider<VSCoreConfig.Server> a;
   private final Provider<Map<String, DP>> b;

   private CZ(Provider<VSCoreConfig.Server> var1, Provider<Map<String, DP>> var2) {
      this.a = var1;
      this.b = var2;
   }

   private CY a() {
      VSCoreConfig.Server var10000 = this.a.get();
      Map var2 = this.b.get();
      VSCoreConfig.Server var1 = var10000;
      return new CY(var1, var2);
   }

   public static CZ a(Provider<VSCoreConfig.Server> var0, Provider<Map<String, DP>> var1) {
      return new CZ(var0, var1);
   }

   private static CY a(VSCoreConfig.Server var0, Map<String, DP> var1) {
      return new CY(var0, var1);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      CZ var1 = this;
      VSCoreConfig.Server var10000 = this.a.get();
      Map var2 = var1.b.get();
      VSCoreConfig.Server var3 = var10000;
      return new CY(var3, var2);
   }
}
