package org.valkyrienskies.core.impl.shadow;

public abstract class zq implements zF {
   private double a = Double.NaN;

   private void a() {
      this.a = Double.NaN;
   }

   @Override
   public final void a(int var1) {
      this.setSeed(var1);
   }

   @Override
   public final void a(int[] var1) {
      long var2 = 0L;
      int[] var7;
      int var4 = (var7 = var1).length;

      for (int var5 = 0; var5 < var4; var5++) {
         int var6 = var7[var5];
         var2 = var2 * 4294967291L + var6;
      }

      this.setSeed(var2);
   }

   @Override
   public abstract void setSeed(long var1);

   @Override
   public void nextBytes(byte[] bytes) {
      int var2 = 0;

      while (var2 < bytes.length) {
         int var3 = this.nextInt();

         for (int var4 = 0; var4 < 3; var4++) {
            if (var4 > 0) {
               var3 >>= 8;
            }

            bytes[var2++] = (byte)var3;
            if (var2 == bytes.length) {
               return;
            }
         }
      }
   }

   @Override
   public int nextInt() {
      return (int)((2.0 * this.nextDouble() - 1.0) * 2.147483647E9);
   }

   @Override
   public int nextInt(int n) {
      if (n <= 0) {
         throw new pk(n);
      }

      int var2;
      return (var2 = (int)(this.nextDouble() * n)) < n ? var2 : n - 1;
   }

   @Override
   public long nextLong() {
      return (long)((2.0 * this.nextDouble() - 1.0) * 9.223372E18F);
   }

   @Override
   public boolean nextBoolean() {
      return this.nextDouble() <= 0.5;
   }

   @Override
   public float nextFloat() {
      return (float)this.nextDouble();
   }

   @Override
   public abstract double nextDouble();

   @Override
   public double nextGaussian() {
      if (!Double.isNaN(this.a)) {
         double var7 = this.a;
         this.a = Double.NaN;
         return var7;
      }

      double var1 = 0.0;
      double var3 = 0.0;

      double var5;
      for (var5 = 1.0; var5 >= 1.0; var5 = var1 * var1 + var3 * var3) {
         var1 = 2.0 * this.nextDouble() - 1.0;
         var3 = 2.0 * this.nextDouble() - 1.0;
      }

      if (var5 != 0.0) {
         var5 = BY.a(-2.0 * BY.k(var5) / var5);
      }

      this.a = var3 * var5;
      return var1 * var5;
   }
}
