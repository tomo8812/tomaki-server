package org.valkyrienskies.core.impl.shadow;

import java.text.NumberFormat;
import java.util.Locale;

public class qZ implements qO<qU> {
   public static final qZ a = new qZ(0.0);
   public static final qZ b = new qZ(1.0);
   public static final qZ c = new qZ(Double.NaN);
   private static qZ e;
   private static qZ f;
   private static final long g = 7556674948671647925L;
   public final double d;

   public qZ(double var1) {
      this.d = var1;
   }

   private qZ(double var1, qZ var3) {
      this.d = var1 * var3.d;
   }

   private qZ(double var1, qZ var3, double var4, qZ var6) {
      this.d = var1 * var3.d + var4 * var6.d;
   }

   private qZ(double var1, qZ var3, double var4, qZ var6, double var7, qZ var9) {
      this.d = var1 * var3.d + var4 * var6.d + var7 * var9.d;
   }

   private qZ(double var1, qZ var3, double var4, qZ var6, double var7, qZ var9, double var10, qZ var12) {
      this.d = var1 * var3.d + var4 * var6.d + var7 * var9.d + var10 * var12.d;
   }

   public final double k() {
      return this.d;
   }

   @Override
   public final qN a() {
      return qU.a.a();
   }

   private static qZ l() {
      return a;
   }

   @Override
   public final double d() {
      return BY.w(this.d);
   }

   @Override
   public final double e() {
      return BY.w(this.d);
   }

   @Override
   public final double f() {
      return this.d * this.d;
   }

   @Override
   public final double g() {
      return BY.w(this.d);
   }

   private qZ g(qO<qU> var1) {
      var1 = var1;
      return new qZ(this.d + var1.d);
   }

   private qZ c(double var1, qO<qU> var3) {
      var3 = var3;
      return new qZ(this.d + var1 * var3.d);
   }

   private qZ h(qO<qU> var1) {
      var1 = var1;
      return new qZ(this.d - var1.d);
   }

   private qZ d(double var1, qO<qU> var3) {
      var3 = var3;
      return new qZ(this.d - var1 * var3.d);
   }

   private qZ m() {
      double var1;
      if ((var1 = BY.w(this.d)) == 0.0) {
         throw new oU(px.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR);
      } else {
         return this.b(1.0 / var1);
      }
   }

   private qZ n() {
      return new qZ(-this.d);
   }

   private qZ b(double var1) {
      return new qZ(var1 * this.d);
   }

   @Override
   public final boolean b() {
      return Double.isNaN(this.d);
   }

   @Override
   public final boolean j() {
      return !Double.isNaN(this.d) && Double.isInfinite(this.d);
   }

   @Override
   public final double a(qO<qU> var1) {
      qZ var5;
      return BY.w((var5 = (qZ)var1).d - this.d);
   }

   @Deprecated
   @Override
   public final double c(qO<qU> var1) {
      return this.a((qM<qU>)var1);
   }

   @Override
   public final double a(qM<qU> var1) {
      qZ var5;
      return BY.w((var5 = (qZ)var1).d - this.d);
   }

   @Override
   public final double d(qO<qU> var1) {
      qZ var5;
      return BY.w((var5 = (qZ)var1).d - this.d);
   }

   @Override
   public final double e(qO<qU> var1) {
      double var3;
      qZ var5;
      return (var3 = (var5 = (qZ)var1).d - this.d) * var3;
   }

   @Override
   public final double f(qO<qU> var1) {
      var1 = var1;
      return this.d * var1.d;
   }

   private static double a(qZ var0, qZ var1) {
      var1 = var1;
      return var0.a(var1);
   }

   private static double b(qZ var0, qZ var1) {
      return var0.d(var1);
   }

   private static double c(qZ var0, qZ var1) {
      return var0.e(var1);
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof qZ) {
         qZ var3;
         return Double.isNaN((var3 = (qZ)other).d) ? Double.isNaN(this.d) : this.d == var3.d;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Double.isNaN(this.d) ? 7785 : 997 * Ci.a(this.d);
   }

   @Override
   public String toString() {
      Locale var1 = Locale.getDefault();
      return new ra(BS.a(var1)).a(this);
   }

   @Override
   public final String a(NumberFormat var1) {
      return new ra(var1).a(this);
   }

   // $VF: synthetic method
   @Override
   public final qO a(double var1) {
      return this.b(var1);
   }

   // $VF: synthetic method
   @Override
   public final qO i() {
      qZ var1 = this;
      double var2;
      if ((var2 = BY.w(this.d)) == 0.0) {
         throw new oU(px.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR);
      } else {
         return var1.b(1.0 / var2);
      }
   }

   // $VF: synthetic method
   @Override
   public final qO h() {
      qZ var1 = this;
      return new qZ(-var1.d);
   }

   // $VF: synthetic method
   @Override
   public final qO b(double var1, qO var3) {
      qZ var2 = var3;
      double var5 = var1;
      qZ var7 = this;
      var2 = var2;
      return new qZ(var7.d - var5 * var2.d);
   }

   // $VF: synthetic method
   @Override
   public final qO b(qO var1) {
      qZ var2 = var1;
      var1 = this;
      var2 = var2;
      return new qZ(var1.d - var2.d);
   }

   // $VF: synthetic method
   @Override
   public final qO a(double var1, qO var3) {
      qZ var2 = var3;
      double var5 = var1;
      qZ var7 = this;
      var2 = var2;
      return new qZ(var7.d + var5 * var2.d);
   }

   // $VF: synthetic method
   @Override
   public final qO a(qO var1) {
      qZ var2 = var1;
      var1 = this;
      var2 = var2;
      return new qZ(var1.d + var2.d);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final qO c() {
      return a;
   }

   static {
      new qZ(Double.POSITIVE_INFINITY);
      new qZ(Double.NEGATIVE_INFINITY);
   }
}
