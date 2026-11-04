package org.valkyrienskies.core.impl.shadow;

public final class zK implements zF {
   private final zF a;

   private zK(zF var1) {
      this.a = var1;
   }

   @Override
   public final void a(int var1) {
      this.a.a(var1);
   }

   @Override
   public final void a(int[] var1) {
      this.a.a(var1);
   }

   @Override
   public final synchronized void setSeed(long seed) {
      this.a.setSeed(seed);
   }

   @Override
   public final synchronized void nextBytes(byte[] bytes) {
      this.a.nextBytes(bytes);
   }

   @Override
   public final synchronized int nextInt() {
      return this.a.nextInt();
   }

   @Override
   public final synchronized int nextInt(int n) {
      return this.a.nextInt(n);
   }

   @Override
   public final synchronized long nextLong() {
      return this.a.nextLong();
   }

   @Override
   public final synchronized boolean nextBoolean() {
      return this.a.nextBoolean();
   }

   @Override
   public final synchronized float nextFloat() {
      return this.a.nextFloat();
   }

   @Override
   public final synchronized double nextDouble() {
      return this.a.nextDouble();
   }

   @Override
   public final synchronized double nextGaussian() {
      return this.a.nextGaussian();
   }
}
