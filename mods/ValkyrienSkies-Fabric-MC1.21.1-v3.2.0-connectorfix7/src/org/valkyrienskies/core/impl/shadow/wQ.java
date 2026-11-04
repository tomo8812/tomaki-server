package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class wQ extends Co<double[], Double> implements Serializable {
   private static final long c = 20120513L;

   public wQ(double[] var1, double var2) {
      this(var1, var2, true);
   }

   public wQ(double[] var1, double var2, boolean var4) {
      super(var4 ? (var1 == null ? null : (double[])var1.clone()) : var1, var2);
   }

   public final double[] a() {
      double[] var1;
      return (var1 = (double[])this.e()) == null ? null : (double[])var1.clone();
   }

   public final double[] b() {
      return (double[])this.e();
   }

   private wQ.a d() {
      return new wQ.a((double[])this.e(), (Double)this.c());
   }

   static class a implements Serializable {
      private static final long a = 20120513L;
      private final double[] b;
      private final double c;

      a(double[] var1, double var2) {
         this.b = (double[])var1.clone();
         this.c = var2;
      }

      private wQ a() {
         return new wQ(this.b, this.c, false);
      }
   }
}
