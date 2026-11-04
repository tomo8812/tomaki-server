package org.valkyrienskies.core.impl.shadow;

public final class os extends oc {
   private static final long b = -436928820673516179L;
   private final int c;
   private final int d;
   private final int e;
   private double f = Double.NaN;
   private boolean g = false;

   private os(int var1, int var2, int var3) {
      this(new zR(), var1, var2, var3);
   }

   public os(zF var1, int var2, int var3, int var4) {
      super(var1);
      if (var2 <= 0) {
         throw new pk(px.POPULATION_SIZE, var2);
      }

      if (var3 < 0) {
         throw new pj(px.NUMBER_OF_SUCCESSES, var3);
      }

      if (var4 < 0) {
         throw new pj(px.NUMBER_OF_SAMPLES, var4);
      }

      if (var3 > var2) {
         throw new pm(px.NUMBER_OF_SUCCESS_LARGER_THAN_POPULATION_SIZE, var3, var2, true);
      }

      if (var4 > var2) {
         throw new pm(px.SAMPLE_SIZE_LARGER_THAN_POPULATION_SIZE, var4, var2, true);
      }

      this.c = var3;
      this.d = var2;
      this.e = var4;
   }

   @Override
   public final double d(int var1) {
      int[] var2 = this.a(this.d, this.c, this.e);
      double var4;
      if (var1 < var2[0]) {
         var4 = 0.0;
      } else if (var1 >= var2[1]) {
         var4 = 1.0;
      } else {
         var4 = this.c(var2[0], var1, 1);
      }

      return var4;
   }

   private int[] a(int var1, int var2, int var3) {
      int[] var10000 = new int[]{b(var1, var2, var3), 0};
      int var5 = var3;
      var1 = var2;
      var10000[1] = BY.a(var5, var1);
      return var10000;
   }

   private static int b(int var0, int var1, int var2) {
      return BY.b(0, var1 - (var0 - var2));
   }

   private int g() {
      return this.c;
   }

   private int h() {
      return this.d;
   }

   private int i() {
      return this.e;
   }

   private static int b(int var0, int var1) {
      return BY.a(var1, var0);
   }

   @Override
   public final double c(int var1) {
      double var2;
      return (var2 = this.b(var1)) == Double.NEGATIVE_INFINITY ? 0.0 : BY.i(var2);
   }

   @Override
   public final double b(int var1) {
      int[] var2 = this.a(this.d, this.c, this.e);
      double var15;
      if (var1 >= var2[0] && var1 <= var2[1]) {
         double var5 = (double)this.e / this.d;
         double var7 = (double)(this.d - this.e) / this.d;
         double var9 = oJ.a(var1, this.c, var5, var7);
         double var11 = oJ.a(this.e - var1, this.d - this.c, var5, var7);
         double var13 = oJ.a(this.e, this.d, var5, var7);
         var15 = var9 + var11 - var13;
      } else {
         var15 = Double.NEGATIVE_INFINITY;
      }

      return var15;
   }

   private double e(int var1) {
      int[] var2 = this.a(this.d, this.c, this.e);
      double var4;
      if (var1 <= var2[0]) {
         var4 = 1.0;
      } else if (var1 > var2[1]) {
         var4 = 0.0;
      } else {
         var4 = this.c(var2[1], var1, -1);
      }

      return var4;
   }

   private double c(int var1, int var2, int var3) {
      double var4;
      for (var4 = this.c(var1); var1 != var2; var4 += this.c(var1)) {
         var1 += var3;
      }

      return var4;
   }

   @Override
   public final double b() {
      return this.e * ((double)this.c / this.d);
   }

   @Override
   public final double c() {
      if (!this.g) {
         os var1 = this;
         double var2 = this.d;
         double var4 = var1.c;
         double var6;
         this.f = (var6 = var1.e) * var4 * (var2 - var6) * (var2 - var4) / (var2 * var2 * (var2 - 1.0));
         this.g = true;
      }

      return this.f;
   }

   private double j() {
      double var1 = this.d;
      double var3 = this.c;
      double var5;
      return (var5 = this.e) * var3 * (var1 - var5) * (var1 - var3) / (var1 * var1 * (var1 - 1.0));
   }

   @Override
   public final int d() {
      return BY.b(0, this.e + this.c - this.d);
   }

   @Override
   public final int e() {
      return BY.a(this.c, this.e);
   }

   @Override
   public final boolean f() {
      return true;
   }
}
