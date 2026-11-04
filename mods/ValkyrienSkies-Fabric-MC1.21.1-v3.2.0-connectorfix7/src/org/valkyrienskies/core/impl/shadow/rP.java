package org.valkyrienskies.core.impl.shadow;

final class rP<S extends qN> implements rN<S> {
   @Override
   public final int a(rM<S> var1) {
      return rN.a.PLUS_MINUS_SUB$672d2271;
   }

   @Override
   public final void b(rM<S> var1) {
      sb var2 = null;
      sb var3 = null;
      rX var4 = null;
      rT var5;
      rT var6;
      if ((var5 = new rT<>(var1.b, var1.a.e())).a() && (var6 = new rT<>(var1.c, var5.a)).d()) {
         var2 = var6.b;
         (var4 = new rX()).a(var6.d);
         var4.a(var5.c);
      }

      if (var5.d() && (var6 = new rT<>(var1.c, var5.b)).a()) {
         var3 = var6.a;
         if (var4 == null) {
            var4 = new rX();
         }

         var4.a(var6.c);
         var4.a(var5.d);
      }

      if (var4 != null) {
         for (rM var10 = var1.d; var10 != null; var10 = var10.d) {
            var4.a(var10);
         }
      }

      rO var8 = new rO(var2, var3, var4);
      var1.e = var8;
   }

   @Override
   public final void c(rM<S> var1) {
   }
}
