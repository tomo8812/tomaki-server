package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

@Deprecated
public final class yq extends Co<double[], double[]> implements Serializable {
   private static final long c = 20120513L;

   public yq(double[] var1, double[] var2) {
      this(var1, var2, true);
   }

   public yq(double[] var1, double[] var2, boolean var3) {
      super(var3 ? (var1 == null ? null : (double[])var1.clone()) : var1, var3 ? (var2 == null ? null : (double[])var2.clone()) : var2);
   }

   public final double[] a() {
      double[] var1;
      return (var1 = (double[])this.e()) == null ? null : (double[])var1.clone();
   }

   public final double[] b() {
      return (double[])this.e();
   }

   private double[] h() {
      double[] var1;
      return (var1 = (double[])super.c()) == null ? null : (double[])var1.clone();
   }

   public final double[] d() {
      return (double[])super.c();
   }

   private yq.a i() {
      return new yq.a((double[])this.e(), this.h());
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

      private yq a() {
         return new yq(this.b, this.c, false);
      }
   }
}
