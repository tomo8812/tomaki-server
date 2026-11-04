package org.valkyrienskies.core.impl.shadow;

public final class pQ implements wK<pW.a> {
   private final double a;
   private final double b;

   private pQ(double var1) {
      this(var1, var1);
   }

   private pQ(double var1, double var3) {
      this.a = var1;
      this.b = var3;
   }

   private boolean a(pW.a var1, pW.a var2) {
      double var3 = var1.a();
      double var5 = var2.a();
      return Cq.b(var3, var5, this.b) || Cq.c(var3, var5, this.a);
   }

   // $VF: synthetic method
   @Override
   public final boolean a(int var1, Object var2, Object var3) {
      pW.a var10001 = var2;
      var3 = var3;
      var2 = var10001;
      pQ var11 = this;
      double var7 = var2.a();
      double var9 = var3.a();
      return Cq.b(var7, var9, var11.b) || Cq.c(var7, var9, var11.a);
   }
}
