package org.valkyrienskies.core.impl.shadow;

public final class pT {
   public int a;
   public int b;
   private wK<pW.a> d;
   private pY e;
   private tu f;
   private tu g;
   public tq c;
   private boolean h;
   private qa i;

   public final pW a() {
      return pU.a(this.e, this.f, this.g, this.c, this.d, this.a, this.b, this.h, this.i);
   }

   public final pT a(int var1) {
      this.a = Integer.MAX_VALUE;
      return this;
   }

   public final pT b(int var1) {
      this.b = var1;
      return this;
   }

   private pT a(wK<pW.a> var1) {
      this.d = var1;
      return this;
   }

   private pT b(wK<wR> var1) {
      var1 = var1;
      wK var2 = new wK<pW.a>() {
         private boolean a(int var1, pW.a var2x, pW.a var3) {
            return var0.a(var1, new wR(var2x.e().g(), var2x.d().g(), false), new wR(var3.e().g(), var3.d().g(), false));
         }

         // $VF: synthetic method
         @Override
         public final boolean a(int var1, Object var2, Object var3) {
            pW.a var10002 = (pW.a)var2;
            pW.a var4 = var3;
            var3 = var10002;
            int var5 = var1;
            return var0.a(var5, new wR(var3.e().g(), var3.d().g(), false), new wR(var4.e().g(), var4.d().g(), false));
         }
      };
      pT var4 = this;
      this.d = var2;
      return var4;
   }

   public final pT a(kY var1, kX var2) {
      pU.b var4 = pU.a(var1, var2);
      pT var3 = this;
      this.e = var4;
      return var3;
   }

   private pT a(pY var1) {
      this.e = var1;
      return this;
   }

   private pT a(sA var1) {
      this.f = var1;
      return this;
   }

   public final pT a(double[] var1) {
      sA var2 = new sA(var1, false);
      pT var3 = this;
      this.f = var2;
      return var3;
   }

   private pT b(sA var1) {
      this.g = var1;
      return this;
   }

   public final pT b(double[] var1) {
      sA var2 = new sA(var1, false);
      pT var3 = this;
      this.g = var2;
      return var3;
   }

   public final pT a(sM var1) {
      this.c = var1;
      return this;
   }

   private pT a(boolean var1) {
      this.h = var1;
      return this;
   }

   private pT a(qa var1) {
      this.i = var1;
      return this;
   }
}
