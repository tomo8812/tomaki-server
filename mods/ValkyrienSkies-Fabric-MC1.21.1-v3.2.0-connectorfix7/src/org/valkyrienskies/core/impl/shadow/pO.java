package org.valkyrienskies.core.impl.shadow;

public abstract class pO implements pW.a {
   private final int a;

   pO(int var1) {
      this.a = var1;
   }

   @Override
   public final tq a(double var1) {
      tq var3;
      var3 = (var3 = this.c()).d().c(var3);
      return new tn(var3, var1).c().b();
   }

   @Override
   public final tu b(double var1) {
      tq var5;
      int var2 = (var5 = this.a(var1)).g();
      sA var3 = new sA(var2);

      for (int var4 = 0; var4 < var2; var4++) {
         var3.a(var4, BY.a(var5.b(var4, var4)));
      }

      return var3;
   }

   @Override
   public final double a() {
      double var1;
      return BY.a((var1 = this.b()) * var1 / this.a);
   }

   @Override
   public final double b() {
      sA var1;
      return BY.a((var1 = new sA(this.d())).b(var1));
   }
}
