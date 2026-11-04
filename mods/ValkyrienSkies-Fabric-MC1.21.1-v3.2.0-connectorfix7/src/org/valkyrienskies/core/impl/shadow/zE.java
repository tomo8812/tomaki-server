package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Collection;

@Deprecated
public final class zE implements Serializable, zC {
   private static final long b = -626730818244969716L;
   final zD a;

   public zE() {
      this.a = new zD();
   }

   private zE(zF var1) {
      this.a = new zD(var1);
   }

   @Deprecated
   final zD a() {
      return this.a;
   }

   @Override
   public final String a(int var1) {
      int var2 = var1;
      zD var6 = this.a;
      if (var2 <= 0) {
         throw new pk(px.LENGTH, var2);
      }

      zF var7 = var6.c();
      StringBuilder var3 = new StringBuilder();
      byte[] var4 = new byte[var2 / 2 + 1];
      var7.nextBytes(var4);

      for (int var8 = 0; var8 < var4.length; var8++) {
         String var5;
         if ((var5 = Integer.toHexString(Integer.valueOf(var4[var8]) + 128)).length() == 1) {
            var5 = "0" + var5;
         }

         var3.append(var5);
      }

      return var3.toString().substring(0, var2);
   }

   @Override
   public final int a(int var1, int var2) {
      int var3 = var2;
      var2 = var1;
      zD var4 = this.a;
      return new oM(var4.c(), var2, var3).a();
   }

   @Override
   public final long a(long var1, long var3) {
      return this.a.a(var1, var3);
   }

   @Override
   public final String b(int var1) {
      return this.a.b(var1);
   }

   @Override
   public final int b(int var1, int var2) {
      int var3 = var2;
      var2 = var1;
      zD var4 = this.a;
      return new oM(var4.d(), var2, var3).a();
   }

   @Override
   public final long b(long var1, long var3) {
      long var8 = var3;
      long var6 = var1;
      zD var15 = this.a;
      if (var6 >= var8) {
         throw new pm(px.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, var6, var8, false);
      }

      zF var16 = var15.d();
      long var11;
      if ((var11 = var8 - var6 + 1L) > 0L) {
         return var11 < 2147483647L ? var6 + var16.nextInt((int)var11) : var6 + zD.a(var16, var11);
      }

      long var13;
      do {
         while ((var13 = var16.nextLong()) < var6) {
         }
      } while (var13 > var8);

      return var13;
   }

   @Override
   public final long a(double var1) {
      double var4 = var1;
      zD var6 = this.a;
      return new oH(var6.c(), var4, 1.0E-12, 10000000).a();
   }

   @Override
   public final double a(double var1, double var3) {
      return this.a.a(var1, var3);
   }

   @Override
   public final double b(double var1) {
      return this.a.b(var1);
   }

   @Override
   public final double b(double var1, double var3) {
      double var8 = var3;
      double var6 = var1;
      zD var10 = this.a;
      return this.a.a(var6, var8, false);
   }

   @Override
   public final double a(double var1, double var3, boolean var5) {
      return this.a.a(var1, var3, var5);
   }

   private double c(double var1, double var3) {
      double var8 = var3;
      double var6 = var1;
      zD var10 = this.a;
      return new of(var10.c(), var6, var8, 1.0E-9).b();
   }

   private int a(int var1, double var2) {
      double var6 = var2;
      int var9 = var1;
      zD var8 = this.a;
      return new og(var8.c(), var9, var6).a();
   }

   private double d(double var1, double var3) {
      double var8 = var3;
      double var6 = var1;
      zD var10 = this.a;
      return new oh(var10.c(), var6, var8, 1.0E-9).b();
   }

   private double c(double var1) {
      double var4 = var1;
      zD var6 = this.a;
      return new oi(var6.c(), var4, 1.0E-9).b();
   }

   private double e(double var1, double var3) {
      double var8 = var3;
      double var6 = var1;
      zD var10 = this.a;
      return new oo(var10.c(), var6, var8, 1.0E-9).b();
   }

   private double f(double var1, double var3) {
      double var8 = var3;
      double var6 = var1;
      zD var10 = this.a;
      return new op(var10.c(), var6, var8, 1.0E-9).b();
   }

   private int a(int var1, int var2, int var3) {
      int var4 = var3;
      var3 = var2;
      var2 = var1;
      zD var5 = this.a;
      return new os(var5.c(), var2, var3, var4).a();
   }

   private int b(int var1, double var2) {
      double var6 = var2;
      int var9 = var1;
      zD var8 = this.a;
      return new oG(var8.c(), var9, var6).a();
   }

   private double d(double var1) {
      double var4 = var1;
      zD var6 = this.a;
      return new oK(var6.c(), var4, 1.0E-9).b();
   }

   private double g(double var1, double var3) {
      double var8 = var3;
      double var6 = var1;
      zD var10 = this.a;
      return new oO(var10.c(), var6, var8, 1.0E-9).b();
   }

   private int c(int var1, double var2) {
      double var6 = var2;
      int var9 = var1;
      zD var8 = this.a;
      return new oP(var8.c(), var9, var6).a();
   }

   public final void a(long var1) {
      this.a.a(var1);
   }

   private void b() {
      zD var1 = this.a;
      this.a.d().setSeed(System.currentTimeMillis());
   }

   private void b(long var1) {
      long var4 = var1;
      zD var6 = this.a;
      this.a.d().setSeed(var4);
   }

   private void c() {
      zD var1 = this.a;
      this.a.c().setSeed(System.currentTimeMillis() + System.identityHashCode(var1));
   }

   private void a(String var1, String var2) {
      String var3 = var2;
      var2 = var1;
      zD var4 = this.a;
      this.a.a = zG.a(SecureRandom.getInstance(var2, var3));
   }

   @Override
   public final int[] c(int var1, int var2) {
      return this.a.c(var1, var2);
   }

   @Override
   public final Object[] a(Collection<?> var1, int var2) {
      int var3 = var2;
      Collection var8 = var1;
      zD var6 = this.a;
      int var4 = var8.size();
      if (var3 > var4) {
         throw new pm(px.SAMPLE_SIZE_EXCEEDS_COLLECTION_SIZE, var3, var4, true);
      }

      if (var3 <= 0) {
         throw new pk(px.NUMBER_OF_SAMPLES, var3);
      }

      Object[] var9 = var8.toArray();
      int[] var7 = var6.c(var4, var3);
      Object[] var10 = new Object[var3];

      for (int var5 = 0; var5 < var3; var5++) {
         var10[var5] = var9[var7[var5]];
      }

      return var10;
   }

   @Deprecated
   private double a(oI var1) {
      return var1.a(this.b(0.0, 1.0));
   }

   @Deprecated
   private int a(ot var1) {
      return var1.a(this.b(0.0, 1.0));
   }
}
