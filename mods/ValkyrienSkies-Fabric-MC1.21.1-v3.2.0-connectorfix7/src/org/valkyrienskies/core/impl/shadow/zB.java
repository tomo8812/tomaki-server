package org.valkyrienskies.core.impl.shadow;

import java.util.Random;

public final class zB extends Random implements zF {
   private static final long a = 2306581345647615033L;
   private final zF b;

   private zB() {
      this.b = null;
   }

   private zB(zF var1) {
      this.b = var1;
   }

   private static zB a(zF var0) {
      return new zB(var0);
   }

   @Override
   public final boolean nextBoolean() {
      return this.b.nextBoolean();
   }

   @Override
   public final void nextBytes(byte[] bytes) {
      this.b.nextBytes(bytes);
   }

   @Override
   public final double nextDouble() {
      return this.b.nextDouble();
   }

   @Override
   public final float nextFloat() {
      return this.b.nextFloat();
   }

   @Override
   public final double nextGaussian() {
      return this.b.nextGaussian();
   }

   @Override
   public final int nextInt() {
      return this.b.nextInt();
   }

   @Override
   public final int nextInt(int n) {
      return this.b.nextInt(n);
   }

   @Override
   public final long nextLong() {
      return this.b.nextLong();
   }

   @Override
   public final void a(int var1) {
      if (this.b != null) {
         this.b.a(var1);
      }
   }

   @Override
   public final void a(int[] var1) {
      if (this.b != null) {
         this.b.a(var1);
      }
   }

   @Override
   public final void setSeed(long seed) {
      if (this.b != null) {
         this.b.setSeed(seed);
      }
   }
}
