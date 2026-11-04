package org.valkyrienskies.core.impl.shadow;

public final class xn implements kW {
   private final kW a;
   private final xn.c[] b;

   private xn(kW var1, double[] var2, double[] var3) {
      Ci.a(var2);
      Ci.a(var3);
      if (var2.length != var3.length) {
         throw new oS(var2.length, var3.length);
      }

      for (int var4 = 0; var4 < var2.length; var4++) {
         if (!(var3[var4] >= var2[var4])) {
            throw new pn(var3[var4], var2[var4], true);
         }
      }

      this.a = var1;
      this.b = new xn.c[var2.length];

      for (int var5 = 0; var5 < this.b.length; var5++) {
         if (Double.isInfinite(var2[var5])) {
            if (Double.isInfinite(var3[var5])) {
               this.b[var5] = new xn.d((byte)0);
            } else {
               this.b[var5] = new xn.e(var3[var5]);
            }
         } else if (Double.isInfinite(var3[var5])) {
            this.b[var5] = new xn.a(var2[var5]);
         } else {
            this.b[var5] = new xn.b(var2[var5], var3[var5]);
         }
      }
   }

   private double[] b(double[] var1) {
      double[] var2 = new double[this.b.length];

      for (int var3 = 0; var3 < this.b.length; var3++) {
         var2[var3] = this.b[var3].a(var1[var3]);
      }

      return var2;
   }

   private double[] c(double[] var1) {
      double[] var2 = new double[this.b.length];

      for (int var3 = 0; var3 < this.b.length; var3++) {
         var2[var3] = this.b[var3].b(var1[var3]);
      }

      return var2;
   }

   @Override
   public final double a(double[] var1) {
      double[] var2 = var1;
      xn var5 = this;
      double[] var3 = new double[this.b.length];

      for (int var4 = 0; var4 < var5.b.length; var4++) {
         var3[var4] = var5.b[var4].a(var2[var4]);
      }

      return this.a.a(var3);
   }

   static class a implements xn.c {
      private final double a;

      a(double var1) {
         this.a = var1;
      }

      @Override
      public final double a(double var1) {
         return this.a + BY.i(var1);
      }

      @Override
      public final double b(double var1) {
         return BY.k(var1 - this.a);
      }
   }

   static class b implements xn.c {
      private final mb a;
      private final lT b;

      b(double var1, double var3) {
         this.a = new mb(var1, var3);
         this.b = new lT(var1, var3);
      }

      @Override
      public final double a(double var1) {
         return this.a.a(var1);
      }

      @Override
      public final double b(double var1) {
         return this.b.a(var1);
      }
   }

   interface c {
      double a(double var1);

      double b(double var1);
   }

   static class d implements xn.c {
      private d() {
      }

      @Override
      public final double a(double var1) {
         return var1;
      }

      @Override
      public final double b(double var1) {
         return var1;
      }

      // $VF: synthetic method
      d(byte var1) {
         this();
      }
   }

   static class e implements xn.c {
      private final double a;

      e(double var1) {
         this.a = var1;
      }

      @Override
      public final double a(double var1) {
         return this.a - BY.i(-var1);
      }

      @Override
      public final double b(double var1) {
         return -BY.k(this.a - var1);
      }
   }
}
