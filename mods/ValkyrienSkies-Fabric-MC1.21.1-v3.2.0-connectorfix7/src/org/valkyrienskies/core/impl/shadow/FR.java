package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;

public final class FR implements ek<FQ> {
   private final Provider<Et> a;
   private final Provider<FH> b;
   private final Provider<FN> c;
   private final Provider<FJ> d;
   private final Provider<EX> e;

   private FR(Provider<Et> var1, Provider<FH> var2, Provider<FN> var3, Provider<FJ> var4, Provider<EX> var5) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.e = var5;
   }

   private FQ a() {
      Et var10000 = this.a.get();
      FH var10001 = this.b.get();
      FN var10002 = this.c.get();
      FJ var10003 = this.d.get();
      EX var5 = this.e.get();
      FJ var4 = var10003;
      FN var3 = var10002;
      FH var2 = var10001;
      Et var1 = var10000;
      return new FQ(var1, var2, var3, var4, var5);
   }

   public static FR a(Provider<Et> var0, Provider<FH> var1, Provider<FN> var2, Provider<FJ> var3, Provider<EX> var4) {
      return new FR(var0, var1, var2, var3, var4);
   }

   private static FQ a(Et var0, FH var1, FN var2, FJ var3, EX var4) {
      return new FQ(var0, var1, var2, var3, var4);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      FR var1 = this;
      Et var10000 = this.a.get();
      FH var10001 = var1.b.get();
      FN var10002 = var1.c.get();
      FJ var10003 = var1.d.get();
      EX var5 = var1.e.get();
      FJ var4 = var10003;
      FN var3 = var10002;
      FH var2 = var10001;
      Et var6 = var10000;
      return new FQ(var6, var2, var3, var4, var5);
   }
}
