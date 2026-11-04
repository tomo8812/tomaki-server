package org.valkyrienskies.core.impl.shadow;

public final class pz implements pC {
   private tq a;
   private tq b;
   private tq c;
   private tu d;
   private tq e;

   private pz(double[][] var1, double[][] var2, double[][] var3, double[] var4, double[][] var5) {
      this(new sy(var1), new sy(var2), new sy(var3), new sA(var4), new sy(var5));
   }

   private pz(double[][] var1, double[][] var2, double[][] var3) {
      this(new sy(var1), new sy(var2), new sy(var3), null, null);
   }

   private pz(sy var1, sy var2, sy var3, sA var4, sy var5) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.e = var5;
   }

   @Override
   public final tq a() {
      return this.a;
   }

   @Override
   public final tq b() {
      return this.b;
   }

   @Override
   public final tq c() {
      return this.c;
   }

   @Override
   public final tu d() {
      return this.d;
   }

   @Override
   public final tq e() {
      return this.e;
   }
}
