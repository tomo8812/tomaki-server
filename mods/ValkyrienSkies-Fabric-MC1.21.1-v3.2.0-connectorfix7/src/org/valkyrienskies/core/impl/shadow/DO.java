package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;
import org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape;
import org.valkyrienskies.core.api.physics.blockstates.LiquidState;
import org.valkyrienskies.core.api.physics.blockstates.SolidState;

public final class DO implements ek<DN> {
   private final Provider<CP> a;
   private final Provider<LiquidState.Builder> b;
   private final Provider<SolidState.Builder> c;
   private final Provider<BoxesBlockShape.Builder> d;

   private DO(Provider<CP> var1, Provider<LiquidState.Builder> var2, Provider<SolidState.Builder> var3, Provider<BoxesBlockShape.Builder> var4) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
   }

   private DN a() {
      CP var10000 = this.a.get();
      Provider var4 = this.d;
      Provider var3 = this.c;
      Provider var2 = this.b;
      CP var1 = var10000;
      return new DN(var1, var2, var3, var4);
   }

   public static DO a(Provider<CP> var0, Provider<LiquidState.Builder> var1, Provider<SolidState.Builder> var2, Provider<BoxesBlockShape.Builder> var3) {
      return new DO(var0, var1, var2, var3);
   }

   private static DN a(CP var0, Provider<LiquidState.Builder> var1, Provider<SolidState.Builder> var2, Provider<BoxesBlockShape.Builder> var3) {
      return new DN(var0, var1, var2, var3);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      DO var1 = this;
      CP var10000 = this.a.get();
      Provider var4 = var1.d;
      Provider var3 = var1.c;
      Provider var2 = var1.b;
      CP var5 = var10000;
      return new DN(var5, var2, var3, var4);
   }
}
