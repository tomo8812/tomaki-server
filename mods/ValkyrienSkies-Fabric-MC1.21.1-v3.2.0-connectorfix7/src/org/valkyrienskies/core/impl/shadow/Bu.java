package org.valkyrienskies.core.impl.shadow;

public final class Bu extends Bt {
   private sy a;
   private tq b;

   private void a(double[] var1, double[][] var2, double[][] var3) {
      a(var2, var1);
      this.a(var1);
      this.a(var2);
      double[][] var6 = var3;
      double[][] var4 = var2;
      if (var2.length != var6.length) {
         throw new oS(var4.length, var6.length);
      }

      if (var6.length > 0 && var6.length != var6[0].length) {
         throw new th(var6.length, var6[0].length);
      }

      var2 = var3;
      Bu var5 = this;
      this.a = new sy(var2);
      var5.b = null;
   }

   private void b(double[][] var1) {
      this.a = new sy(var1);
      this.b = null;
   }

   private tq m() {
      if (this.b == null) {
         this.b = new tb(this.a).a().b();
      }

      return this.b;
   }

   @Override
   protected final tu i() {
      tq var1 = this.m();
      tq var2;
      tq var3 = (var2 = this.a().d()).c(var1).c(this.a());
      return new tb(var3).a().b().c(var2).c(var1).a(this.b());
   }

   @Override
   protected final tq j() {
      tq var1 = this.m();
      var1 = this.a().d().c(var1).c(this.a());
      return new tb(var1).a().b();
   }

   @Override
   protected final double k() {
      tu var1;
      return (var1 = this.l()).b(this.m().a(var1)) / (this.a().f() - this.a().g());
   }
}
