package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public abstract class zs implements Serializable, zF {
   private static final long a = 20130104L;
   private double b = Double.NaN;

   @Override
   public abstract void a(int var1);

   @Override
   public abstract void a(int[] var1);

   @Override
   public abstract void setSeed(long var1);

   protected abstract int b(int var1);

   @Override
   public boolean nextBoolean() {
      return this.b(1) != 0;
   }

   @Override
   public double nextDouble() {
      long var1 = (long)this.b(26) << 26;
      int var3 = this.b(26);
      return (var1 | var3) * 2.220446E-16F;
   }

   @Override
   public float nextFloat() {
      return this.b(23) * 1.1920929E-7F;
   }

   @Override
   public double nextGaussian() {
      double var1;
      if (Double.isNaN(this.b)) {
         double var3 = this.nextDouble();
         double var5 = this.nextDouble();
         double var7 = var3 * (Math.PI * 2);
         double var9;
         var1 = (var9 = BY.a(-2.0 * BY.k(var5))) * BY.o(var7);
         this.b = var9 * BY.n(var7);
      } else {
         var1 = this.b;
         this.b = Double.NaN;
      }

      return var1;
   }

   @Override
   public int nextInt() {
      return this.b(32);
   }

   @Override
   public int nextInt(int n) {
      if (n <= 0) {
         throw new pk(n);
      }

      if ((n & -n) == n) {
         return (int)((long)n * this.b(31) >> 31);
      }

      int var2;
      int var3;
      do {
         var3 = (var2 = this.b(31)) % n;
      } while (var2 - var3 + (n - 1) < 0);

      return var3;
   }

   @Override
   public long nextLong() {
      long var1 = (long)this.b(32) << 32;
      long var3 = this.b(32) & 4294967295L;
      return var1 | var3;
   }

   private long a(long var1) {
      if (var1 <= 0L) {
         throw new pk(var1);
      }

      long var3;
      long var5;
      do {
         var5 = (var3 = (long)this.b(31) << 32 | this.b(32) & 4294967295L) % var1;
      } while (var3 - var5 + (var1 - 1L) < 0L);

      return var5;
   }

   public final void a() {
      this.b = Double.NaN;
   }

   @Override
   public void nextBytes(byte[] bytes) {
      this.b(bytes, 0, bytes.length);
   }

   private void a(byte[] var1, int var2, int var3) {
      if (var2 >= 0 && var2 < var1.length) {
         if (var3 >= 0 && var3 <= var1.length - var2) {
            this.b(var1, var2, var3);
         } else {
            throw new po(var3, 0, var1.length - var2);
         }
      } else {
         throw new po(var2, 0, var1.length);
      }
   }

   private void b(byte[] var1, int var2, int var3) {
      int var4 = var2;
      int var5 = var2 + (var3 & 2147483644);

      while (var4 < var5) {
         int var6 = this.b(32);
         var1[var4++] = (byte)var6;
         var1[var4++] = (byte)(var6 >>> 8);
         var1[var4++] = (byte)(var6 >>> 16);
         var1[var4++] = (byte)(var6 >>> 24);
      }

      int var11 = var2 + var3;
      if (var4 < var11) {
         var2 = this.b(32);

         while (true) {
            var1[var4++] = (byte)var2;
            if (var4 >= var11) {
               break;
            }

            var2 >>>= 8;
         }
      }
   }
}
