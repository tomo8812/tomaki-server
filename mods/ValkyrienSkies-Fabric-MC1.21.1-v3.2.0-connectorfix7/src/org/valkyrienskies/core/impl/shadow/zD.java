package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collection;

public final class zD implements Serializable, zC {
   private static final long b = -626730818244969716L;
   private zF c = null;
   zF a = null;

   public zD() {
   }

   public zD(zF var1) {
      this.c = var1;
   }

   @Override
   public final String a(int var1) {
      if (var1 <= 0) {
         throw new pk(px.LENGTH, var1);
      }

      zF var2 = this.c();
      StringBuilder var3 = new StringBuilder();
      byte[] var4 = new byte[var1 / 2 + 1];
      var2.nextBytes(var4);

      for (int var6 = 0; var6 < var4.length; var6++) {
         String var5;
         if ((var5 = Integer.toHexString(Integer.valueOf(var4[var6]) + 128)).length() == 1) {
            var5 = "0" + var5;
         }

         var3.append(var5);
      }

      return var3.toString().substring(0, var1);
   }

   @Override
   public final int a(int var1, int var2) {
      return new oM(this.c(), var1, var2).a();
   }

   @Override
   public final long a(long var1, long var3) {
      if (var1 >= var3) {
         throw new pm(px.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, var1, var3, false);
      }

      long var5;
      if ((var5 = var3 - var1 + 1L) > 0L) {
         return var5 < 2147483647L ? var1 + this.c().nextInt((int)var5) : var1 + a(this.c(), var5);
      }

      zF var10 = this.c();

      long var8;
      do {
         while ((var8 = var10.nextLong()) < var1) {
         }
      } while (var8 > var3);

      return var8;
   }

   static long a(zF var0, long var1) {
      if (var1 <= 0L) {
         throw new pk(var1);
      }

      byte[] var3 = new byte[8];

      long var9;
      long var10;
      do {
         var0.nextBytes(var3);
         var9 = 0L;
         byte[] var6 = var3;
         int var10000 = var3.length;

         for (int var7 = 0; var7 < 8; var7++) {
            byte var8 = var6[var7];
            var9 = var9 << 8 | var8 & 255L;
         }

         var10 = (var9 = var9 & Long.MAX_VALUE) % var1;
      } while (var9 - var10 + (var1 - 1L) < 0L);

      return var10;
   }

   @Override
   public final String b(int var1) {
      if (var1 <= 0) {
         throw new pk(px.LENGTH, var1);
      }

      zF var2 = this.d();

      MessageDigest var3;
      try {
         var3 = MessageDigest.getInstance("SHA-1");
      } catch (NoSuchAlgorithmException var10) {
         throw new oY(var10);
      }

      var3.reset();
      int var4 = var1 / 40 + 1;
      StringBuilder var5 = new StringBuilder();

      for (int var6 = 1; var6 < var4 + 1; var6++) {
         byte[] var7 = new byte[40];
         var2.nextBytes(var7);
         var3.update(var7);
         var7 = var3.digest();

         for (int var8 = 0; var8 < var7.length; var8++) {
            String var9;
            if ((var9 = Integer.toHexString(Integer.valueOf(var7[var8]) + 128)).length() == 1) {
               var9 = "0" + var9;
            }

            var5.append(var9);
         }
      }

      return var5.toString().substring(0, var1);
   }

   @Override
   public final int b(int var1, int var2) {
      return new oM(this.d(), var1, var2).a();
   }

   @Override
   public final long b(long var1, long var3) {
      if (var1 >= var3) {
         throw new pm(px.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, var1, var3, false);
      }

      zF var5 = this.d();
      long var6;
      if ((var6 = var3 - var1 + 1L) > 0L) {
         return var6 < 2147483647L ? var1 + var5.nextInt((int)var6) : var1 + a(var5, var6);
      }

      long var8;
      do {
         while ((var8 = var5.nextLong()) < var1) {
         }
      } while (var8 > var3);

      return var8;
   }

   @Override
   public final long a(double var1) {
      return new oH(this.c(), var1, 1.0E-12, 10000000).a();
   }

   @Override
   public final double a(double var1, double var3) {
      if (var3 <= 0.0) {
         throw new pk(px.STANDARD_DEVIATION, var3);
      } else {
         return var3 * this.c().nextGaussian() + var1;
      }
   }

   @Override
   public final double b(double var1) {
      return new on(this.c(), var1, 1.0E-9).b();
   }

   public final double c(double var1, double var3) {
      return new op(this.c(), var1, var3, 1.0E-9).b();
   }

   public final int a(int var1, int var2, int var3) {
      return new os(this.c(), var1, var2, var3).a();
   }

   public final int a(int var1, double var2) {
      return new oG(this.c(), var1, var2).a();
   }

   public final double c(double var1) {
      return new oK(this.c(), var1, 1.0E-9).b();
   }

   public final double d(double var1, double var3) {
      return new oO(this.c(), var1, var3, 1.0E-9).b();
   }

   public final int b(int var1, double var2) {
      return new oP(this.c(), var1, var2).a();
   }

   public final double e(double var1, double var3) {
      return new of(this.c(), var1, var3, 1.0E-9).b();
   }

   public final int c(int var1, double var2) {
      return new og(this.c(), var1, var2).a();
   }

   public final double f(double var1, double var3) {
      return new oh(this.c(), var1, var3, 1.0E-9).b();
   }

   public final double d(double var1) {
      return new oi(this.c(), var1, 1.0E-9).b();
   }

   public final double g(double var1, double var3) {
      return new oo(this.c(), var1, var3, 1.0E-9).b();
   }

   @Override
   public final double b(double var1, double var3) {
      return this.a(var1, var3, false);
   }

   @Override
   public final double a(double var1, double var3, boolean var5) {
      if (var1 >= var3) {
         throw new pm(px.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, var1, var3, false);
      }

      if (Double.isInfinite(var1)) {
         throw new pi(px.INFINITE_BOUND, var1);
      }

      if (Double.isInfinite(var3)) {
         throw new pi(px.INFINITE_BOUND, var3);
      }

      if (!Double.isNaN(var1) && !Double.isNaN(var3)) {
         zF var6;
         double var7 = (var6 = this.c()).nextDouble();

         while (!var5 && var7 <= 0.0) {
            var7 = var6.nextDouble();
         }

         return var7 * var3 + (1.0 - var7) * var1;
      } else {
         throw new ph();
      }
   }

   @Override
   public final int[] c(int var1, int var2) {
      if (var2 > var1) {
         throw new pm(px.PERMUTATION_EXCEEDS_N, var2, var1, true);
      }

      if (var2 <= 0) {
         throw new pk(px.PERMUTATION_SIZE, var2);
      }

      int[] var3;
      Ch.a(var3 = Ch.a(var1, 0, 1), this.c());
      return Ch.a(var3, var2);
   }

   @Override
   public final Object[] a(Collection<?> var1, int var2) {
      int var3 = var1.size();
      if (var2 > var3) {
         throw new pm(px.SAMPLE_SIZE_EXCEEDS_COLLECTION_SIZE, var2, var3, true);
      }

      if (var2 <= 0) {
         throw new pk(px.NUMBER_OF_SAMPLES, var2);
      }

      Object[] var6 = var1.toArray();
      int[] var7 = this.c(var3, var2);
      Object[] var4 = new Object[var2];

      for (int var5 = 0; var5 < var2; var5++) {
         var4[var5] = var6[var7[var5]];
      }

      return var4;
   }

   public final void a(long var1) {
      this.c().setSeed(var1);
   }

   public final void a() {
      this.d().setSeed(System.currentTimeMillis());
   }

   public final void b(long var1) {
      this.d().setSeed(var1);
   }

   public final void b() {
      this.c().setSeed(System.currentTimeMillis() + System.identityHashCode(this));
   }

   public final void a(String var1, String var2) {
      this.a = zG.a(SecureRandom.getInstance(var1, var2));
   }

   public final zF c() {
      if (this.c == null) {
         this.e();
      }

      return this.c;
   }

   private void e() {
      this.c = new zR(System.currentTimeMillis() + System.identityHashCode(this));
   }

   zF d() {
      if (this.a == null) {
         this.a = zG.a(new SecureRandom());
         this.a.setSeed(System.currentTimeMillis() + System.identityHashCode(this));
      }

      return this.a;
   }
}
