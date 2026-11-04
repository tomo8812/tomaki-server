package org.valkyrienskies.core.impl.shadow;

import java.util.Random;

public final class zG {
   private zG() {
   }

   public static <unrepresentable> a(final Random var0) {
      return new zF() {
         @Override
         public final void a(int var1) {
            var0.setSeed(var1);
         }

         @Override
         public final void a(int[] var1) {
            var0.setSeed(zG.a(var1));
         }

         @Override
         public final void setSeed(long seed) {
            var0.setSeed(seed);
         }

         @Override
         public final void nextBytes(byte[] bytes) {
            var0.nextBytes(bytes);
         }

         @Override
         public final int nextInt() {
            return var0.nextInt();
         }

         @Override
         public final int nextInt(int n) {
            if (n <= 0) {
               throw new pk(n);
            } else {
               return var0.nextInt(n);
            }
         }

         @Override
         public final long nextLong() {
            return var0.nextLong();
         }

         @Override
         public final boolean nextBoolean() {
            return var0.nextBoolean();
         }

         @Override
         public final float nextFloat() {
            return var0.nextFloat();
         }

         @Override
         public final double nextDouble() {
            return var0.nextDouble();
         }

         @Override
         public final double nextGaussian() {
            return var0.nextGaussian();
         }
      };
   }

   public static long a(int[] var0) {
      long var1 = 0L;
      int[] var6;
      int var3 = (var6 = var0).length;

      for (int var4 = 0; var4 < var3; var4++) {
         int var5 = var6[var4];
         var1 = var1 * 4294967291L + var5;
      }

      return var1;
   }
}
