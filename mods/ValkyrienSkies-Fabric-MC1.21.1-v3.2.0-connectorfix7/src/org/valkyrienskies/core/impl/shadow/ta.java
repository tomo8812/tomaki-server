package org.valkyrienskies.core.impl.shadow;

public final class ta extends tp {
   private final sA a;

   private ta(double[] var1, boolean var2) {
      this.a = new sA(var1, false);
   }

   private static ta a(tp var0) {
      int var1 = var0.g();
      if (var0.f() != var1) {
         throw new ti(var0.f(), var1);
      }

      double[] var2 = new double[var1];
      if (var0 instanceof sv) {
         sv var3 = (sv)var0;

         for (int var4 = 0; var4 < var1; var4++) {
            var2[var4] = var3.b(var4, var4);
         }
      } else {
         sA var5 = new sA(var1);

         for (int var6 = 0; var6 < var1; var6++) {
            var5.f(0.0);
            var5.a(var6, 1.0);
            var2[var6] = var0.a(var5).a(var6);
         }
      }

      return new ta(var2, false);
   }

   @Override
   public final int g() {
      sA var1 = this.a;
      return this.a.a.length;
   }

   @Override
   public final int f() {
      sA var1 = this.a;
      return this.a.a.length;
   }

   @Override
   public final sA a(tu var1) {
      return new sA(Ch.b(var1.g(), this.a.g()), false);
   }

   private <unrepresentable> a() {
      final sA var1 = this.a.a(new mg());
      return new tp() {
         @Override
         public final sA a(tu var1x) {
            return new sA(Ch.b(var1x.g(), var1.g()), false);
         }

         @Override
         public final int f() {
            return var1.f();
         }

         @Override
         public final int g() {
            return var1.f();
         }
      };
   }
}
