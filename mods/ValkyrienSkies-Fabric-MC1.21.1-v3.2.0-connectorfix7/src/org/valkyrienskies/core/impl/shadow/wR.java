package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class wR extends Co<double[], double[]> implements Serializable {
   private static final long c = 20120513L;

   public wR(double[] var1, double[] var2) {
      this(var1, var2, true);
   }

   public wR(double[] var1, double[] var2, boolean var3) {
      super(var3 ? (var1 == null ? null : (double[])var1.clone()) : var1, var3 ? (var2 == null ? null : (double[])var2.clone()) : var2);
   }

   private double[] d() {
      double[] var1;
      return (var1 = (double[])this.e()) == null ? null : (double[])var1.clone();
   }

   public final double[] a() {
      return (double[])this.e();
   }

   private double[] h() {
      double[] var1;
      return (var1 = (double[])super.c()) == null ? null : (double[])var1.clone();
   }

   public final double[] b() {
      return (double[])super.c();
   }

   private wR.a i() {
      return new wR.a((double[])this.e(), this.h());
   }

   // $VF: synthetic method
   @Override
   public final Object c() {
      return this.h();
   }

   static class a implements Serializable {
      private static final long a = 20120513L;
      private final double[] b;
      private final double[] c;

      a(double[] var1, double[] var2) {
         this.b = (double[])var1.clone();
         this.c = (double[])var2.clone();
      }

      private wR a() {
         return new wR(this.b, this.c, false);
      }
   }
}
