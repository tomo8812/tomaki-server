package org.valkyrienskies.core.impl.shadow;

public final class zt implements zH {
   private final double[] a;
   private final zA b;
   private final double[] c;
   private final tq d;

   private zt(double[] var1, tq var2, double var3, zA var5) {
      int var6 = var2.f();
      if (var1.length != var6) {
         throw new oS(var1.length, var6);
      }

      this.a = (double[])var1.clone();
      ty var7 = new ty(var2, var3);
      this.d = var7.a;
      this.b = var5;
      this.c = new double[var7.b];
   }

   private zt(tq var1, double var2, zA var4) {
      int var5 = var1.f();
      this.a = new double[var5];

      for (int var6 = 0; var6 < var5; var6++) {
         this.a[var6] = 0.0;
      }

      ty var7 = new ty(var1, var2);
      this.d = var7.a;
      this.b = var4;
      this.c = new double[var7.b];
   }

   private zA b() {
      return this.b;
   }

   private int c() {
      return this.c.length;
   }

   private tq d() {
      return this.d;
   }

   @Override
   public final double[] a() {
      for (int var1 = 0; var1 < this.c.length; var1++) {
         this.c[var1] = this.b.a();
      }

      double[] var4 = new double[this.a.length];

      for (int var2 = 0; var2 < var4.length; var2++) {
         var4[var2] = this.a[var2];

         for (int var3 = 0; var3 < this.d.g(); var3++) {
            var4[var2] += this.d.b(var2, var3) * this.c[var3];
         }
      }

      return var4;
   }
}
