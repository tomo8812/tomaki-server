package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;

public final class FK implements ek<FJ> {
   private final Provider<Et> a;
   private final Provider<Fl> b;
   private final Provider<Fe> c;

   private FK(Provider<Et> var1, Provider<Fl> var2, Provider<Fe> var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   private FJ a() {
      Et var10000 = this.a.get();
      Fl var10001 = this.b.get();
      Fe var3 = this.c.get();
      Fl var2 = var10001;
      Et var1 = var10000;
      return new FJ(var1, var2, var3);
   }

   public static FK a(Provider<Et> var0, Provider<Fl> var1, Provider<Fe> var2) {
      return new FK(var0, var1, var2);
   }

   private static FJ a(Et var0, Fl var1, Fe var2) {
      return new FJ(var0, var1, var2);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      FK var1 = this;
      Et var10000 = this.a.get();
      Fl var10001 = var1.b.get();
      Fe var3 = var1.c.get();
      Fl var2 = var10001;
      Et var4 = var10000;
      return new FJ(var4, var2, var3);
   }
}
