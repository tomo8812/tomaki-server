package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;

public class AT extends Ao implements Serializable {
   private static final long a = -8091216485095130416L;
   private static final int b = 10;
   private static final int c = 512;
   private final Cg d;
   private final AT.a e;
   private final Bp f;
   private double g;
   private int[] h;

   public AT() {
      this(50.0);
   }

   public AT(double var1) {
      this(50.0, AT.a.LEGACY, Bp.REMOVED, new Cg(new Cj()));
   }

   public AT(AT var1) {
      Ci.a(var1);
      this.e = var1.e;
      this.f = var1.f;
      this.d = var1.d;
      this.c(var1.e());
      if (var1.h != null) {
         System.arraycopy(var1.h, 0, this.h, 0, var1.h.length);
      }

      this.a(var1.g);
   }

   protected AT(double var1, AT.a var3, Bp var4, Cg var5) {
      this.a(var1);
      this.h = null;
      Ci.a(var3);
      Ci.a(var4);
      Ci.a(var5);
      this.e = var3;
      this.f = var4;
      this.d = var5;
   }

   @Override
   public final void c(double[] var1) {
      if (var1 == null) {
         this.h = null;
      } else {
         this.h = new int[512];
         Arrays.fill(this.h, -1);
      }

      super.c(var1);
   }

   @Override
   public final void c(double[] var1, int var2, int var3) {
      if (var1 == null) {
         this.h = null;
      } else {
         this.h = new int[512];
         Arrays.fill(this.h, -1);
      }

      super.c(var1, var2, var3);
   }

   private double b(double var1) {
      return this.a(this.e(), var1);
   }

   public final double a(double[] var1, double var2) {
      byte var6 = 0;
      byte var5 = 0;
      Ch.a(var1, var5, var6, false);
      return this.a(var1, 0, var1.length, var2);
   }

   @Override
   public final double a(double[] var1, int var2, int var3) {
      return this.a(var1, var2, var3, this.g);
   }

   public final double a(double[] var1, int var2, int var3, double var4) {
      int var8 = var3;
      int var7 = var2;
      Ch.a(var1, var7, var8, false);
      if (var4 > 100.0 || var4 <= 0.0) {
         throw new po(px.OUT_OF_BOUNDS_QUANTILE_VALUE, var4, 0, 100);
      }

      if (var3 == 0) {
         return Double.NaN;
      }

      if (var3 == 1) {
         return var1[var2];
      }

      double[] var10 = this.e(var1, var2, var3);
      int[] var9 = this.b(var1);
      return var10.length == 0 ? Double.NaN : this.e.a(var10, var9, var4, this.d);
   }

   @Deprecated
   private static int b(double[] var0, int var1, int var2) {
      return new Cj().a(var0, var1, var2);
   }

   private double f() {
      return this.g;
   }

   public final void a(double var1) {
      if (!(var1 <= 0.0) && !(var1 > 100.0)) {
         this.g = var1;
      } else {
         throw new po(px.OUT_OF_BOUNDS_QUANTILE_VALUE, var1, 0, 100);
      }
   }

   private AT g() {
      return new AT(this);
   }

   @Deprecated
   private static void h() {
      throw new pb();
   }

   private double[] e(double[] var1, int var2, int var3) {
      if (var1 == this.e()) {
         var1 = this.e();
      } else {
         switch (this.f) {
            case MAXIMAL:
               var1 = a(var1, var2, var3, Double.NaN, Double.POSITIVE_INFINITY);
               break;
            case MINIMAL:
               var1 = a(var1, var2, var3, Double.NaN, Double.NEGATIVE_INFINITY);
               break;
            case REMOVED:
               var1 = b(var1, var2, var3, Double.NaN);
               break;
            case FAILED:
               Ch.c(var1 = f(var1, var2, var3));
               break;
            default:
               var1 = f(var1, var2, var3);
         }
      }

      return var1;
   }

   private static double[] f(double[] var0, int var1, int var2) {
      int var5 = var2;
      int var4 = var1;
      Ch.a(var0, var4, var5, false);
      return Ch.a(var0, var1, var1 + var2);
   }

   private static double[] a(double[] var0, int var1, int var2, double var3, double var5) {
      var0 = f(var0, var1, var2);

      for (int var8 = 0; var8 < var2; var8++) {
         var0[var8] = Cq.b(Double.NaN, var0[var8]) ? var5 : var0[var8];
      }

      return var0;
   }

   private static double[] b(double[] var0, int var1, int var2, double var3) {
      int var5 = var2;
      int var4 = var1;
      Ch.a(var0, var4, var5, false);
      BitSet var10 = new BitSet(var2);

      for (int var11 = var1; var11 < var1 + var2; var11++) {
         if (Cq.b(Double.NaN, var0[var11])) {
            var10.set(var11 - var1);
         }
      }

      double[] var9;
      if (var10.isEmpty()) {
         var9 = f(var0, var1, var2);
      } else if (var10.cardinality() == var2) {
         var9 = new double[0];
      } else {
         var9 = new double[var2 - var10.cardinality()];
         var5 = var1;
         int var6 = 0;
         int var8 = 0;

         int var7;
         while ((var7 = var10.nextSetBit(var8)) != -1) {
            int var13 = var7 - var8;
            System.arraycopy(var0, var5, var9, var6, var13);
            var6 += var13;
            var5 = var1 + (var8 = var10.nextClearBit(var7));
         }

         if (var5 < var1 + var2) {
            System.arraycopy(var0, var5, var9, var6, var1 + var2 - var5);
         }
      }

      return var9;
   }

   private int[] b(double[] var1) {
      int[] var2;
      if (var1 == this.e()) {
         var2 = this.h;
      } else {
         Arrays.fill(var2 = new int[512], -1);
      }

      return var2;
   }

   public final AT.a a() {
      return this.e;
   }

   public AT a(AT.a var1) {
      return new AT(this.g, var1, this.f, this.d);
   }

   public final Bp b() {
      return this.f;
   }

   public AT a(Bp var1) {
      return new AT(this.g, this.e, var1, this.d);
   }

   public final Cg c() {
      return this.d;
   }

   private Cp i() {
      Cg var1 = this.d;
      return this.d.a;
   }

   public AT a(Cg var1) {
      return new AT(this.g, this.e, this.f, var1);
   }

   // $VF: synthetic method
   @Override
   public final AA d() {
      AT var1 = this;
      return new AT(var1);
   }

   public enum a {
      LEGACY("Legacy Apache Commons Math") {
         @Override
         protected final double a(double var1, int var3) {
            if (Double.compare(var1, 0.0) == 0) {
               return 0.0;
            } else {
               return Double.compare(var1, 1.0) == 0 ? var3 : var1 * (var3 + 1);
            }
         }
      },
      R_1("R-1") {
         @Override
         protected final double a(double var1, int var3) {
            return Double.compare(var1, 0.0) == 0 ? 0.0 : var3 * var1 + 0.5;
         }

         @Override
         protected final double a(double[] var1, int[] var2, double var3, int var5, Cg var6) {
            return super.a(var1, var2, BY.z(var3 - 0.5), var5, var6);
         }
      },
      R_2("R-2") {
         @Override
         protected final double a(double var1, int var3) {
            if (Double.compare(var1, 1.0) == 0) {
               return var3;
            } else {
               return Double.compare(var1, 0.0) == 0 ? 0.0 : var3 * var1 + 0.5;
            }
         }

         @Override
         protected final double a(double[] var1, int[] var2, double var3, int var5, Cg var6) {
            double var7 = super.a(var1, var2, BY.z(var3 - 0.5), var5, var6);
            double var9 = super.a(var1, var2, BY.y(var3 + 0.5), var5, var6);
            return (var7 + var9) / 2.0;
         }
      },
      R_3("R-3") {
         @Override
         protected final double a(double var1, int var3) {
            double var4 = 0.5 / var3;
            return Double.compare(var1, var4) <= 0 ? 0.0 : BY.A(var3 * var1);
         }
      },
      R_4("R-4") {
         @Override
         protected final double a(double var1, int var3) {
            double var4 = 1.0 / var3;
            if (Double.compare(var1, var4) < 0) {
               return 0.0;
            } else {
               return Double.compare(var1, 1.0) == 0 ? var3 : var3 * var1;
            }
         }
      },
      R_5("R-5") {
         @Override
         protected final double a(double var1, int var3) {
            double var4 = 0.5 / var3;
            double var6 = (var3 - 0.5) / var3;
            if (Double.compare(var1, var4) < 0) {
               return 0.0;
            } else {
               return Double.compare(var1, var6) >= 0 ? var3 : var3 * var1 + 0.5;
            }
         }
      },
      R_6("R-6") {
         @Override
         protected final double a(double var1, int var3) {
            double var4 = 1.0 / (var3 + 1);
            double var6 = 1.0 * var3 / (var3 + 1);
            if (Double.compare(var1, var4) < 0) {
               return 0.0;
            } else {
               return Double.compare(var1, var6) >= 0 ? var3 : (var3 + 1) * var1;
            }
         }
      },
      R_7("R-7") {
         @Override
         protected final double a(double var1, int var3) {
            if (Double.compare(var1, 0.0) == 0) {
               return 0.0;
            } else {
               return Double.compare(var1, 1.0) == 0 ? var3 : 1.0 + (var3 - 1) * var1;
            }
         }
      },
      R_8("R-8") {
         @Override
         protected final double a(double var1, int var3) {
            double var4 = 0.6666666666666666 / (var3 + 0.3333333333333333);
            double var6 = (var3 - 0.3333333333333333) / (var3 + 0.3333333333333333);
            if (Double.compare(var1, var4) < 0) {
               return 0.0;
            } else {
               return Double.compare(var1, var6) >= 0 ? var3 : (var3 + 0.3333333333333333) * var1 + 0.3333333333333333;
            }
         }
      },
      R_9("R-9") {
         @Override
         protected final double a(double var1, int var3) {
            double var4 = 0.625 / (var3 + 0.25);
            double var6 = (var3 - 0.375) / (var3 + 0.25);
            if (Double.compare(var1, var4) < 0) {
               return 0.0;
            } else {
               return Double.compare(var1, var6) >= 0 ? var3 : (var3 + 0.25) * var1 + 0.375;
            }
         }
      };

      private final String name;

      a(String var3) {
         this.name = var3;
      }

      protected abstract double a(double var1, int var3);

      protected double a(double[] var1, int[] var2, double var3, int var5, Cg var6) {
         double var7;
         int var9 = (int)(var7 = BY.y(var3));
         double var10 = var3 - var7;
         if (var3 < 1.0) {
            return var6.a(var1, var2, 0);
         }

         if (var3 >= var5) {
            return var6.a(var1, var2, var5 - 1);
         }

         double var12 = var6.a(var1, var2, var9 - 1);
         double var14 = var6.a(var1, var2, var9);
         return var12 + var10 * (var14 - var12);
      }

      protected final double a(double[] var1, int[] var2, double var3, Cg var5) {
         Ci.a(var1);
         if (!(var3 > 100.0) && !(var3 <= 0.0)) {
            return this.a(var1, var2, this.a(var3 / 100.0, var1.length), var1.length, var5);
         } else {
            throw new po(px.OUT_OF_BOUNDS_QUANTILE_VALUE, var3, 0, 100);
         }
      }

      private double a(double[] var1, double var2, Cg var4) {
         return this.a(var1, null, var2, var4);
      }

      private String a() {
         return this.name;
      }

      // $VF: synthetic method
      a(String var3, byte var4) {
         this(var3);
      }
   }
}
