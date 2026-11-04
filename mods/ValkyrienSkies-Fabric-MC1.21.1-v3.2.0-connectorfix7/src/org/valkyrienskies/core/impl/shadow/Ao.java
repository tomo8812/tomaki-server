package org.valkyrienskies.core.impl.shadow;

public abstract class Ao implements AA {
   private double[] a;

   public void c(double[] var1) {
      this.a = var1 == null ? null : (double[])var1.clone();
   }

   private double[] f() {
      return this.a == null ? null : (double[])this.a.clone();
   }

   public final double[] e() {
      return this.a;
   }

   public void c(double[] var1, int var2, int var3) {
      if (var1 == null) {
         throw new pl(px.INPUT_ARRAY);
      }

      if (var2 < 0) {
         throw new pj(px.START_POSITION, var2);
      }

      if (var3 < 0) {
         throw new pj(px.LENGTH, var3);
      }

      if (var2 + var3 > var1.length) {
         throw new pm(px.SUBARRAY_ENDS_AFTER_ARRAY_END, var2 + var3, var1.length, true);
      }

      this.a = new double[var3];
      System.arraycopy(var1, var2, this.a, 0, var3);
   }

   private double g() {
      return this.a(this.a);
   }

   @Override
   public double a(double[] var1) {
      byte var4 = 0;
      byte var3 = 0;
      Ch.a(var1, var3, var4, false);
      return this.a(var1, 0, var1.length);
   }

   @Override
   public abstract double a(double[] var1, int var2, int var3);

   @Override
   public abstract AA d();

   protected static boolean d(double[] var0, int var1, int var2) {
      return Ch.a(var0, var1, var2, false);
   }

   protected static boolean a(double[] var0, int var1, int var2, boolean var3) {
      return Ch.a(var0, var1, var2, true);
   }

   protected static boolean a_(double[] var0, double[] var1, int var2, int var3) {
      return Ch.a(var0, var1, var2, var3, false);
   }

   protected static boolean a(double[] var0, double[] var1, int var2, int var3, boolean var4) {
      return Ch.a(var0, var1, var2, var3, true);
   }
}
