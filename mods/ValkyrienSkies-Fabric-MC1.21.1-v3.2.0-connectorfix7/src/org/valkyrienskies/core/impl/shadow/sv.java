package org.valkyrienskies.core.impl.shadow;

import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Locale;

public abstract class sv extends tp implements tq {
   private static final ts a;

   protected sv() {
   }

   protected sv(int var1, int var2) {
      if (var1 <= 0) {
         throw new pk(var1);
      }

      if (var2 <= 0) {
         throw new pk(var2);
      }
   }

   @Override
   public tq a(tq var1) {
      td.a((tq)this, var1);
      int var2 = this.f();
      int var3 = this.g();
      sv var4 = this.a(var2, var3);

      for (int var5 = 0; var5 < var2; var5++) {
         for (int var6 = 0; var6 < var3; var6++) {
            var4.c(var5, var6, this.b(var5, var6) + var1.b(var5, var6));
         }
      }

      return var4;
   }

   @Override
   public tq b(tq var1) {
      td.a(this, var1);
      int var2 = this.f();
      int var3 = this.g();
      sv var4 = this.a(var2, var3);

      for (int var5 = 0; var5 < var2; var5++) {
         for (int var6 = 0; var6 < var3; var6++) {
            var4.c(var5, var6, this.b(var5, var6) - var1.b(var5, var6));
         }
      }

      return var4;
   }

   @Override
   public tq a(double var1) {
      int var3 = this.f();
      int var4 = this.g();
      sv var5 = this.a(var3, var4);

      for (int var6 = 0; var6 < var3; var6++) {
         for (int var7 = 0; var7 < var4; var7++) {
            var5.c(var6, var7, this.b(var6, var7) + var1);
         }
      }

      return var5;
   }

   @Override
   public tq b(double var1) {
      int var3 = this.f();
      int var4 = this.g();
      sv var5 = this.a(var3, var4);

      for (int var6 = 0; var6 < var3; var6++) {
         for (int var7 = 0; var7 < var4; var7++) {
            var5.c(var6, var7, this.b(var6, var7) * var1);
         }
      }

      return var5;
   }

   @Override
   public tq c(tq var1) {
      td.b(this, var1);
      int var2 = this.f();
      int var3 = var1.g();
      int var4 = this.g();
      sv var5 = this.a(var2, var3);

      for (int var6 = 0; var6 < var2; var6++) {
         for (int var7 = 0; var7 < var3; var7++) {
            double var8 = 0.0;

            for (int var10 = 0; var10 < var4; var10++) {
               var8 += this.b(var6, var10) * var1.b(var10, var7);
            }

            var5.c(var6, var7, var8);
         }
      }

      return var5;
   }

   @Override
   public final tq d(tq var1) {
      return var1.c(this);
   }

   @Override
   public final tq a(int var1) {
      if (var1 < 0) {
         throw new pj(px.NOT_POSITIVE_EXPONENT, var1);
      }

      if (!this.e()) {
         throw new th(this.f(), this.g());
      }

      if (var1 == 0) {
         return td.a(this.f());
      }

      if (var1 == 1) {
         return this.i();
      }

      char[] var7 = Integer.toBinaryString(--var1).toCharArray();
      ArrayList var2 = new ArrayList();
      int var3 = -1;

      for (int var4 = 0; var4 < var7.length; var4++) {
         if (var7[var4] == '1') {
            int var5 = var7.length - var4 - 1;
            var2.add(var5);
            if (var3 == -1) {
               var3 = var5;
            }
         }
      }

      tq[] var10;
      (var10 = new tq[var3 + 1])[0] = this.i();

      for (int var11 = 1; var11 <= var3; var11++) {
         var10[var11] = var10[var11 - 1].c(var10[var11 - 1]);
      }

      tq var12 = this.i();

      for (Integer var9 : var2) {
         var12 = var12.c(var10[var9]);
      }

      return var12;
   }

   @Override
   public double[][] a() {
      double[][] var1 = new double[this.f()][this.g()];

      for (int var2 = 0; var2 < var1.length; var2++) {
         double[] var3 = var1[var2];

         for (int var4 = 0; var4 < var3.length; var4++) {
            var3[var4] = this.b(var2, var4);
         }
      }

      return var1;
   }

   @Override
   public double b() {
      return this.a(new tt() {
         private double a;
         private double b;
         private double c;

         @Override
         public final void a(int var1, int var2, int var3) {
            this.a = var2;
            this.b = 0.0;
            this.c = 0.0;
         }

         @Override
         public final void a(int var1, int var2, double var3) {
            this.b = this.b + BY.w(var3);
            if (var1 == this.a) {
               this.c = BY.e(this.c, this.b);
               this.b = 0.0;
            }
         }

         @Override
         public final double a() {
            return this.c;
         }
      });
   }

   @Override
   public double c() {
      return this.b(new tt() {
         private double a;

         @Override
         public final void a(int var1, int var2, int var3) {
            this.a = 0.0;
         }

         @Override
         public final void a(int var1, int var2, double var3) {
            this.a += var3 * var3;
         }

         @Override
         public final double a() {
            return BY.a(this.a);
         }
      });
   }

   @Override
   public tq a(int var1, int var2, int var3, int var4) {
      td.a(this, var1, var2, var3, var4);
      sv var5 = this.a(var2 - var1 + 1, var4 - var3 + 1);

      for (int var6 = var1; var6 <= var2; var6++) {
         for (int var7 = var3; var7 <= var4; var7++) {
            var5.c(var6 - var1, var7 - var3, this.b(var6, var7));
         }
      }

      return var5;
   }

   @Override
   public final tq a(final int[] var1, final int[] var2) {
      td.a(this, var1, var2);
      sv var3;
      (var3 = this.a(var1.length, var2.length)).a(new sK() {
         @Override
         public final double a(int var1x, int var2x, double var3x) {
            return sv.this.b(var1[var1x], var2[var2x]);
         }
      });
      return var3;
   }

   @Override
   public final void a(int var1, int var2, int var3, int var4, final double[][] var5) {
      td.a(this, 0, var2, var3, var4);
      var1 = var2 + 1;
      int var6 = var4 + 1 - var3;
      if (var5.length >= var1 && var5[0].length >= var6) {
         for (int var7 = 1; var7 < var1; var7++) {
            if (var5[var7].length < var6) {
               throw new tc(var5.length, var5[var7].length, var1, var6);
            }
         }

         this.a(new sL() {
            private int a;
            private int b;

            @Override
            public final void a(int var1, int var2x, int var3x) {
               this.a = var1;
               this.b = var3x;
            }

            @Override
            public final void a(int var1, int var2x, double var3x) {
               var5[var1 - this.a][var2x - this.b] = var3x;
            }
         }, 0, var2, var3, var4);
      } else {
         throw new tc(var5.length, var5[0].length, var1, var6);
      }
   }

   @Override
   public final void a(int[] var1, int[] var2, double[][] var3) {
      td.a(this, var1, var2);
      int var4 = var2.length;
      if (var3.length >= var1.length && var3[0].length >= var4) {
         for (int var5 = 0; var5 < var1.length; var5++) {
            double[] var6;
            if ((var6 = var3[var5]).length < var4) {
               throw new tc(var3.length, var6.length, var1.length, var2.length);
            }

            for (int var7 = 0; var7 < var2.length; var7++) {
               var6[var7] = this.b(var1[var5], var2[var7]);
            }
         }
      } else {
         throw new tc(var3.length, var3[0].length, var1.length, var2.length);
      }
   }

   @Override
   public void a(double[][] var1, int var2, int var3) {
      Ci.a(var1);
      int var4;
      if ((var4 = var1.length) == 0) {
         throw new pf(px.AT_LEAST_ONE_ROW);
      }

      int var5;
      if ((var5 = var1[0].length) == 0) {
         throw new pf(px.AT_LEAST_ONE_COLUMN);
      }

      for (int var6 = 1; var6 < var4; var6++) {
         if (var1[var6].length != var5) {
            throw new oS(var5, var1[var6].length);
         }
      }

      td.a(this, var2);
      td.b(this, var3);
      td.a(this, var4 + var2 - 1);
      td.b(this, var5 + var3 - 1);

      for (int var8 = 0; var8 < var4; var8++) {
         for (int var7 = 0; var7 < var5; var7++) {
            this.c(var2 + var8, var3 + var7, var1[var8][var7]);
         }
      }
   }

   @Override
   public tq b(int var1) {
      td.a(this, var1);
      int var2 = this.g();
      sv var3 = this.a(1, var2);

      for (int var4 = 0; var4 < var2; var4++) {
         var3.c(0, var4, this.b(var1, var4));
      }

      return var3;
   }

   @Override
   public void a(int var1, tq var2) {
      td.a(this, var1);
      int var3 = this.g();
      if (var2.f() == 1 && var2.g() == var3) {
         for (int var4 = 0; var4 < var3; var4++) {
            this.c(var1, var4, var2.b(0, var4));
         }
      } else {
         throw new tc(var2.f(), var2.g(), 1, var3);
      }
   }

   @Override
   public tq c(int var1) {
      td.b(this, var1);
      int var2 = this.f();
      sv var3 = this.a(var2, 1);

      for (int var4 = 0; var4 < var2; var4++) {
         var3.c(var4, 0, this.b(var4, var1));
      }

      return var3;
   }

   @Override
   public void b(int var1, tq var2) {
      td.b(this, var1);
      int var3 = this.f();
      if (var2.f() == var3 && var2.g() == 1) {
         for (int var4 = 0; var4 < var3; var4++) {
            this.c(var4, var1, var2.b(var4, 0));
         }
      } else {
         throw new tc(var2.f(), var2.g(), var3, 1);
      }
   }

   @Override
   public sA d(int var1) {
      return new sA(this.f(var1), false);
   }

   @Override
   public void a(int var1, tu var2) {
      td.a(this, var1);
      int var3 = this.g();
      if (var2.f() != var3) {
         throw new tc(1, var2.f(), 1, var3);
      }

      for (int var4 = 0; var4 < var3; var4++) {
         this.c(var1, var4, var2.a(var4));
      }
   }

   @Override
   public sA e(int var1) {
      return new sA(this.g(var1), false);
   }

   @Override
   public void b(int var1, tu var2) {
      td.b(this, var1);
      int var3 = this.f();
      if (var2.f() != var3) {
         throw new tc(var2.f(), 1, var3, 1);
      }

      for (int var4 = 0; var4 < var3; var4++) {
         this.c(var4, var1, var2.a(var4));
      }
   }

   @Override
   public double[] f(int var1) {
      td.a(this, var1);
      int var2;
      double[] var3 = new double[var2 = this.g()];

      for (int var4 = 0; var4 < var2; var4++) {
         var3[var4] = this.b(var1, var4);
      }

      return var3;
   }

   @Override
   public void a(int var1, double[] var2) {
      td.a(this, var1);
      int var3 = this.g();
      if (var2.length != var3) {
         throw new tc(1, var2.length, 1, var3);
      }

      for (int var4 = 0; var4 < var3; var4++) {
         this.c(var1, var4, var2[var4]);
      }
   }

   @Override
   public double[] g(int var1) {
      td.b(this, var1);
      int var2;
      double[] var3 = new double[var2 = this.f()];

      for (int var4 = 0; var4 < var2; var4++) {
         var3[var4] = this.b(var4, var1);
      }

      return var3;
   }

   @Override
   public void b(int var1, double[] var2) {
      td.b(this, var1);
      int var3 = this.f();
      if (var2.length != var3) {
         throw new tc(var2.length, 1, var3, 1);
      }

      for (int var4 = 0; var4 < var3; var4++) {
         this.c(var4, var1, var2[var4]);
      }
   }

   @Override
   public void a(int var1, int var2, double var3) {
      td.a(this, var1, var2);
      this.c(var1, var2, this.b(var1, var2) + var3);
   }

   @Override
   public void b(int var1, int var2, double var3) {
      td.a(this, var1, var2);
      this.c(var1, var2, this.b(var1, var2) * var3);
   }

   @Override
   public tq d() {
      int var1 = this.f();
      int var2 = this.g();
      final sv var3 = this.a(var2, var1);
      this.b(new sL() {
         @Override
         public final void a(int var1, int var2x, double var3x) {
            var3.c(var2x, var1, var3x);
         }
      });
      return var3;
   }

   @Override
   public final boolean e() {
      return this.g() == this.f();
   }

   @Override
   public abstract int f();

   @Override
   public abstract int g();

   @Override
   public final double h() {
      int var1 = this.f();
      int var2 = this.g();
      if (var1 != var2) {
         throw new th(var1, var2);
      }

      double var3 = 0.0;

      for (int var5 = 0; var5 < var1; var5++) {
         var3 += this.b(var5, var5);
      }

      return var3;
   }

   @Override
   public double[] a(double[] var1) {
      int var2 = this.f();
      int var3 = this.g();
      if (var1.length != var3) {
         throw new oS(var1.length, var3);
      }

      double[] var4 = new double[var2];

      for (int var5 = 0; var5 < var2; var5++) {
         double var6 = 0.0;

         for (int var8 = 0; var8 < var3; var8++) {
            var6 += this.b(var5, var8) * var1[var8];
         }

         var4[var5] = var6;
      }

      return var4;
   }

   @Override
   public final sA a(tu var1) {
      try {
         sA var10;
         return new sA(this.a((var10 = (sA)var1).a), false);
      } catch (ClassCastException var9) {
         int var2 = this.f();
         int var3 = this.g();
         if (var1.f() != var3) {
            throw new oS(var1.f(), var3);
         }

         double[] var4 = new double[var2];

         for (int var5 = 0; var5 < var2; var5++) {
            double var6 = 0.0;

            for (int var8 = 0; var8 < var3; var8++) {
               var6 += this.b(var5, var8) * var1.a(var8);
            }

            var4[var5] = var6;
         }

         return new sA(var4, false);
      }
   }

   @Override
   public double[] b(double[] var1) {
      int var2 = this.f();
      int var3 = this.g();
      if (var1.length != var2) {
         throw new oS(var1.length, var2);
      }

      double[] var4 = new double[var3];

      for (int var5 = 0; var5 < var3; var5++) {
         double var6 = 0.0;

         for (int var8 = 0; var8 < var2; var8++) {
            var6 += this.b(var8, var5) * var1[var8];
         }

         var4[var5] = var6;
      }

      return var4;
   }

   @Override
   public tu b(tu var1) {
      try {
         sA var10;
         return new sA(this.b((var10 = (sA)var1).a), false);
      } catch (ClassCastException var9) {
         int var2 = this.f();
         int var3 = this.g();
         if (var1.f() != var2) {
            throw new oS(var1.f(), var2);
         }

         double[] var4 = new double[var3];

         for (int var5 = 0; var5 < var3; var5++) {
            double var6 = 0.0;

            for (int var8 = 0; var8 < var2; var8++) {
               var6 += this.b(var8, var5) * var1.a(var8);
            }

            var4[var5] = var6;
         }

         return new sA(var4, false);
      }
   }

   @Override
   public double a(tr var1) {
      int var2 = this.f();
      int var3 = this.g();

      for (int var4 = 0; var4 < var2; var4++) {
         for (int var5 = 0; var5 < var3; var5++) {
            double var6 = this.b(var4, var5);
            double var8 = var1.a(var4, var5, var6);
            this.c(var4, var5, var8);
         }
      }

      return var1.b();
   }

   @Override
   public double a(tt var1) {
      int var2 = this.f();
      int var3 = this.g();
      var1.a(0, var2 - 1, 0);

      for (int var4 = 0; var4 < var2; var4++) {
         for (int var5 = 0; var5 < var3; var5++) {
            var1.a(var4, var5, this.b(var4, var5));
         }
      }

      return var1.a();
   }

   @Override
   public double a(tr var1, int var2, int var3, int var4, int var5) {
      td.a(this, var2, var3, var4, var5);
      this.f();
      this.g();

      for (int var12 = var2; var12 <= var3; var12++) {
         for (int var6 = var4; var6 <= var5; var6++) {
            double var8 = this.b(var12, var6);
            double var10 = var1.a(var12, var6, var8);
            this.c(var12, var6, var10);
         }
      }

      return var1.b();
   }

   @Override
   public double a(tt var1, int var2, int var3, int var4, int var5) {
      td.a(this, var2, var3, var4, var5);
      this.f();
      this.g();
      var1.a(var2, var3, var4);

      for (int var7 = var2; var7 <= var3; var7++) {
         for (int var6 = var4; var6 <= var5; var6++) {
            var1.a(var7, var6, this.b(var7, var6));
         }
      }

      return var1.a();
   }

   @Override
   public double b(tr var1) {
      int var2 = this.f();
      int var3 = this.g();

      for (int var4 = 0; var4 < var3; var4++) {
         for (int var5 = 0; var5 < var2; var5++) {
            double var6 = this.b(var5, var4);
            double var8 = var1.a(var5, var4, var6);
            this.c(var5, var4, var8);
         }
      }

      return var1.b();
   }

   @Override
   public double a(<unrepresentable> var1) {
      int var2 = this.f();
      int var3 = this.g();
      var1.a(0, var2 - 1, 0);

      for (int var4 = 0; var4 < var3; var4++) {
         for (int var5 = 0; var5 < var2; var5++) {
            var1.a(var5, var4, this.b(var5, var4));
         }
      }

      return var1.a();
   }

   @Override
   public double b(tr var1, int var2, int var3, int var4, int var5) {
      td.a(this, var2, var3, var4, var5);
      this.f();
      this.g();

      for (int var12 = var4; var12 <= var5; var12++) {
         for (int var6 = var2; var6 <= var3; var6++) {
            double var8 = this.b(var6, var12);
            double var10 = var1.a(var6, var12, var8);
            this.c(var6, var12, var10);
         }
      }

      return var1.b();
   }

   @Override
   public double b(tt var1, int var2, int var3, int var4, int var5) {
      td.a(this, var2, var3, var4, var5);
      this.f();
      this.g();
      var1.a(var2, var3, var4);

      for (int var7 = var4; var7 <= var5; var7++) {
         for (int var6 = var2; var6 <= var3; var6++) {
            var1.a(var6, var7, this.b(var6, var7));
         }
      }

      return var1.a();
   }

   @Override
   public double a(<unrepresentable> var1) {
      return this.a((tr)var1);
   }

   @Override
   public double b(tt var1) {
      return this.a(var1);
   }

   @Override
   public double c(tr var1, int var2, int var3, int var4, int var5) {
      return this.a(var1, var2, var3, var4, var5);
   }

   @Override
   public double a(sL var1, int var2, int var3, int var4, int var5) {
      return this.a((tt)var1, var2, var3, var4, var5);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2;
      var2 = (var2 = this.getClass().getName()).substring(var2.lastIndexOf(46) + 1);
      var1.append(var2);
      ts var10001 = a;
      sv var3 = this;
      ts var10 = a;
      StringBuffer var10003 = new StringBuffer();
      FieldPosition var5 = new FieldPosition(0);
      StringBuffer var4 = var10003;
      var3 = var3;
      ts var11 = var10001;
      var5.setBeginIndex(0);
      var5.setEndIndex(0);
      var4.append(var11.a);
      int var6 = var3.f();

      for (int var7 = 0; var7 < var6; var7++) {
         var4.append(var11.c);

         for (int var8 = 0; var8 < var3.g(); var8++) {
            if (var8 > 0) {
               var4.append(var11.f);
            }

            BS.a(var3.b(var7, var8), var11.g, var4, var5);
         }

         var4.append(var11.d);
         if (var7 < var6 - 1) {
            var4.append(var11.e);
         }
      }

      var4.append(var11.b);
      var1.append(var4.toString());
      return var1.toString();
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      }

      if (!(object instanceof tq)) {
         return false;
      }

      object = (tq)object;
      int var2 = this.f();
      int var3 = this.g();
      if (((tq)object).g() == var3 && ((tq)object).f() == var2) {
         for (int var4 = 0; var4 < var2; var4++) {
            for (int var5 = 0; var5 < var3; var5++) {
               if (this.b(var4, var5) != ((tq)object).b(var4, var5)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var2 = this.f();
      int var3 = this.g();
      int var1;
      var1 = (var1 = var2 + 217) * 31 + var3;

      for (int var4 = 0; var4 < var2; var4++) {
         for (int var5 = 0; var5 < var3; var5++) {
            var1 = var1 * 31 + (11 * (var4 + 1) + 17 * (var5 + 1)) * Ci.a(this.b(var4, var5));
         }
      }

      return var1;
   }

   @Override
   public abstract sv a(int var1, int var2);

   @Override
   public abstract sv i();

   @Override
   public abstract double b(int var1, int var2);

   @Override
   public abstract void c(int var1, int var2, double var3);

   static {
      (a = ts.a(Locale.US)).g.setMinimumFractionDigits(1);
   }
}
