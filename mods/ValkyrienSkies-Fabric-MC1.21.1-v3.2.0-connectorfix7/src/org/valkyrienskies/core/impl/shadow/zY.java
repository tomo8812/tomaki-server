package org.valkyrienskies.core.impl.shadow;

public final class zY {
   private static double a;
   private static double b;
   private static final double c = 1.0E-14;
   private static final double[] d = new double[]{
      0.9999999999999971,
      57.15623566586292,
      -59.59796035547549,
      14.136097974741746,
      -0.4919138160976202,
      3.399464998481189E-5,
      4.652362892704858E-5,
      -9.837447530487956E-5,
      1.580887032249125E-4,
      -2.1026444172410488E-4,
      2.1743961811521265E-4,
      -1.643181065367639E-4,
      8.441822398385275E-5,
      -2.6190838401581408E-5,
      3.6899182659531625E-6
   };
   private static final double e = 0.5 * BY.k(Math.PI * 2);
   private static final double f = 2.5066282746310007;
   private static final double g = 49.0;
   private static final double h = 1.0E-5;
   private static final double i = 6.116095104481416E-9;
   private static final double j = 6.247308301164655E-9;
   private static final double k = 0.203610414066807;
   private static final double l = 0.026620534842894922;
   private static final double m = 4.939449793824468E-4;
   private static final double n = -8.514194324403149E-6;
   private static final double o = -6.4304548177935305E-6;
   private static final double p = 9.926418406727737E-7;
   private static final double q = -6.077618957228252E-8;
   private static final double r = 1.9575583661463974E-10;
   private static final double s = 6.116095104481416E-9;
   private static final double t = 6.8716741130671986E-9;
   private static final double u = 6.820161668496171E-10;
   private static final double v = 4.686843322948848E-11;
   private static final double w = 1.5728330277104463E-12;
   private static final double x = -1.2494415722763663E-13;
   private static final double y = 4.343529937408594E-15;
   private static final double z = 0.3056961078365221;
   private static final double A = 0.054642130860422966;
   private static final double B = 0.004956830093825887;
   private static final double C = 2.6923694661863613E-4;
   private static final double D = -0.42278433509846713;
   private static final double E = 0.5772156649015329;
   private static final double F = -0.6558780715202539;
   private static final double G = -0.04200263503409524;
   private static final double H = 0.16653861138229148;
   private static final double I = -0.04219773455554433;
   private static final double J = -0.009621971527876973;
   private static final double K = 0.0072189432466631;
   private static final double L = -0.0011651675918590652;
   private static final double M = -2.1524167411495098E-4;
   private static final double N = 1.280502823881162E-4;
   private static final double O = -2.013485478078824E-5;
   private static final double P = -1.2504934821426706E-6;
   private static final double Q = 1.133027231981696E-6;
   private static final double R = -2.056338416977607E-7;

   private zY() {
   }

   public static double a(double var0) {
      double var2;
      if (!Double.isNaN(var0) && !(var0 <= 0.0)) {
         if (var0 < 0.5) {
            return c(var0) - BY.k(var0);
         }

         if (var0 <= 2.5) {
            return c(var0 - 0.5 - 0.5);
         }

         if (var0 <= 8.0) {
            int var8 = (int)BY.y(var0 - 1.5);
            double var5 = 1.0;

            for (int var7 = 1; var7 <= var8; var7++) {
               var5 *= var0 - var7;
            }

            return c(var0 - (var8 + 1)) + BY.k(var5);
         }

         double var4 = b(var0);
         double var6 = var0 + 4.7421875 + 0.5;
         var2 = (var0 + 0.5) * BY.k(var6) - var6 + e + BY.k(var4 / var0);
      } else {
         var2 = Double.NaN;
      }

      return var2;
   }

   public static double a(double var0, double var2) {
      return a(var0, var2, 1.0E-14, Integer.MAX_VALUE);
   }

   public static double a(double var0, double var2, double var4, int var6) {
      double var7;
      if (Double.isNaN(var0) || Double.isNaN(var2) || var0 <= 0.0 || var2 < 0.0) {
         var7 = Double.NaN;
      } else if (var2 == 0.0) {
         var7 = 0.0;
      } else if (var2 >= var0 + 1.0) {
         var7 = 1.0 - b(var0, var2, var4, var6);
      } else {
         double var9 = 0.0;

         double var11;
         double var13;
         for (var13 = var11 = 1.0 / var0; BY.w(var11 / var13) > var4 && var9 < var6 && var13 < Double.POSITIVE_INFINITY; var13 += var11) {
            var9++;
            var11 *= var2 / (var0 + var9);
         }

         if (var9 >= var6) {
            throw new pc(var6);
         }

         if (Double.isInfinite(var13)) {
            var7 = 1.0;
         } else {
            var7 = BY.i(-var2 + var0 * BY.k(var2) - a(var0)) * var13;
         }
      }

      return var7;
   }

   private static double b(double var0, double var2) {
      return b(var0, var2, 1.0E-14, Integer.MAX_VALUE);
   }

   public static double b(final double var0, double var2, double var4, int var6) {
      double var7;
      if (Double.isNaN(var0) || Double.isNaN(var2) || var0 <= 0.0 || var2 < 0.0) {
         var7 = Double.NaN;
      } else if (var2 == 0.0) {
         var7 = 1.0;
      } else if (var2 < var0 + 1.0) {
         var7 = 1.0 - a(var0, var2, var4, var6);
      } else {
         BT var9 = new BT() {
            @Override
            public final double b(int var1, double var2x) {
               return 2.0 * var1 + 1.0 - var0 + var2x;
            }

            @Override
            public final double a(int var1, double var2x) {
               return var1 * (var0 - var1);
            }
         };
         var7 = 1.0 / var9.a(var2, var4, var6);
         var7 = BY.i(-var2 + var0 * BY.k(var2) - a(var0)) * var7;
      }

      return var7;
   }

   private static double e(double var0) {
      if (Double.isNaN(var0) || Double.isInfinite(var0)) {
         return var0;
      } else if (var0 > 0.0 && var0 <= 1.0E-5) {
         return -0.5772156649015329 - 1.0 / var0;
      } else if (var0 >= 49.0) {
         double var2 = 1.0 / (var0 * var0);
         return BY.k(var0) - 0.5 / var0 - var2 * (0.08333333333333333 + var2 * (0.008333333333333333 - var2 / 252.0));
      } else {
         return e(var0 + 1.0) - 1.0 / var0;
      }
   }

   private static double f(double var0) {
      if (Double.isNaN(var0) || Double.isInfinite(var0)) {
         return var0;
      } else if (var0 > 0.0 && var0 <= 1.0E-5) {
         return 1.0 / (var0 * var0);
      } else if (var0 >= 49.0) {
         double var2 = 1.0 / (var0 * var0);
         return 1.0 / var0 + var2 / 2.0 + var2 / var0 * (0.16666666666666666 - var2 * (0.03333333333333333 + var2 / 42.0));
      } else {
         return f(var0 + 1.0) + 1.0 / (var0 * var0);
      }
   }

   public static double b(double var0) {
      double var2 = 0.0;

      for (int var4 = d.length - 1; var4 > 0; var4--) {
         var2 += d[var4] / (var0 + var4);
      }

      return var2 + d[0];
   }

   private static double g(double var0) {
      if (var0 < -0.5) {
         throw new pn(var0, -0.5, true);
      }

      if (var0 > 1.5) {
         throw new pm(var0, 1.5, true);
      }

      double var2;
      double var4;
      if ((var4 = var0 <= 0.5 ? var0 : var0 - 0.5 - 0.5) < 0.0) {
         double var6 = 6.116095104481416E-9 + var4 * 6.247308301164655E-9;
         double var8 = -6.077618957228252E-8 + var4 * 1.9575583661463974E-10;
         var8 = 9.926418406727737E-7 + var4 * var8;
         var8 = -6.4304548177935305E-6 + var4 * var8;
         var8 = -8.514194324403149E-6 + var4 * var8;
         var8 = 4.939449793824468E-4 + var4 * var8;
         var8 = 0.026620534842894922 + var4 * var8;
         var8 = 0.203610414066807 + var4 * var8;
         var8 = 1.0 + var4 * var8;
         double var10 = -2.056338416977607E-7 + var4 * (var6 / var8);
         var10 = 1.133027231981696E-6 + var4 * var10;
         var10 = -1.2504934821426706E-6 + var4 * var10;
         var10 = -2.013485478078824E-5 + var4 * var10;
         var10 = 1.280502823881162E-4 + var4 * var10;
         var10 = -2.1524167411495098E-4 + var4 * var10;
         var10 = -0.0011651675918590652 + var4 * var10;
         var10 = 0.0072189432466631 + var4 * var10;
         var10 = -0.009621971527876973 + var4 * var10;
         var10 = -0.04219773455554433 + var4 * var10;
         var10 = 0.16653861138229148 + var4 * var10;
         var10 = -0.04200263503409524 + var4 * var10;
         var10 = -0.6558780715202539 + var4 * var10;
         var10 = -0.42278433509846713 + var4 * var10;
         if (var0 > 0.5) {
            var2 = var4 * var10 / var0;
         } else {
            var2 = var0 * (var10 + 0.5 + 0.5);
         }
      } else {
         double var12 = -1.2494415722763663E-13 + var4 * 4.343529937408594E-15;
         var12 = 1.5728330277104463E-12 + var4 * var12;
         var12 = 4.686843322948848E-11 + var4 * var12;
         var12 = 6.820161668496171E-10 + var4 * var12;
         var12 = 6.8716741130671986E-9 + var4 * var12;
         var12 = 6.116095104481416E-9 + var4 * var12;
         double var25 = 0.004956830093825887 + var4 * 2.6923694661863613E-4;
         var25 = 0.054642130860422966 + var4 * var25;
         var25 = 0.3056961078365221 + var4 * var25;
         var25 = 1.0 + var4 * var25;
         double var42 = -2.056338416977607E-7 + var12 / var25 * var4;
         var42 = 1.133027231981696E-6 + var4 * var42;
         var42 = -1.2504934821426706E-6 + var4 * var42;
         var42 = -2.013485478078824E-5 + var4 * var42;
         var42 = 1.280502823881162E-4 + var4 * var42;
         var42 = -2.1524167411495098E-4 + var4 * var42;
         var42 = -0.0011651675918590652 + var4 * var42;
         var42 = 0.0072189432466631 + var4 * var42;
         var42 = -0.009621971527876973 + var4 * var42;
         var42 = -0.04219773455554433 + var4 * var42;
         var42 = 0.16653861138229148 + var4 * var42;
         var42 = -0.04200263503409524 + var4 * var42;
         var42 = -0.6558780715202539 + var4 * var42;
         var42 = 0.5772156649015329 + var4 * var42;
         if (var0 > 0.5) {
            var2 = var4 / var0 * (var42 - 0.5 - 0.5);
         } else {
            var2 = var0 * var42;
         }
      }

      return var2;
   }

   public static double c(double var0) {
      if (var0 < -0.5) {
         throw new pn(var0, -0.5, true);
      } else if (var0 > 1.5) {
         throw new pm(var0, 1.5, true);
      } else {
         return -BY.l(g(var0));
      }
   }

   public static double d(double var0) {
      if (var0 == BY.A(var0) && var0 <= 0.0) {
         return Double.NaN;
      }

      double var2;
      double var4;
      if ((var4 = BY.w(var0)) <= 20.0) {
         if (var0 >= 1.0) {
            double var6 = 1.0;
            double var8 = var0;

            while (var8 > 2.5) {
               var6 *= --var8;
            }

            var2 = var6 / (1.0 + g(var8 - 1.0));
         } else {
            double var10 = var0;
            double var12 = var0;

            while (var12 < -0.5) {
               var10 *= ++var12;
            }

            var2 = 1.0 / (var10 * (1.0 + g(var12)));
         }
      } else {
         double var11 = var4 + 4.7421875 + 0.5;
         double var13 = 2.5066282746310007 / var4 * BY.a(var11, var4 + 0.5) * BY.i(-var11) * b(var4);
         if (var0 > 0.0) {
            var2 = var13;
         } else {
            var2 = -Math.PI / (var0 * BY.n(Math.PI * var0) * var13);
         }
      }

      return var2;
   }
}
