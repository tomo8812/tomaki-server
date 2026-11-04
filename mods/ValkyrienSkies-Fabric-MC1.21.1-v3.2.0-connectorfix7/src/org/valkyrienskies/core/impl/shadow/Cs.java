package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Arrays;

public final class Cs implements Serializable, BX {
   @Deprecated
   private static int a;
   @Deprecated
   private static int b;
   private static final long c = -3485529955529426875L;
   private static final int d = 16;
   private static final double e = 2.0;
   private static final double f = 0.5;
   private double g = 2.5;
   private double h = 2.0;
   private Cs.a i = Cs.a.MULTIPLICATIVE;
   private double[] j;
   private int k = 0;
   private int l = 0;

   public Cs() {
      this(16);
   }

   public Cs(int var1) {
      this(var1, 2.0);
   }

   public Cs(double[] var1) {
      this(16, 2.0, 2.5, Cs.a.MULTIPLICATIVE, var1);
   }

   @Deprecated
   private Cs(int var1, float var2) {
      this(var1, (double)var2);
   }

   private Cs(int var1, double var2) {
      this(var1, var2, var2 + 0.5);
   }

   @Deprecated
   private Cs(int var1, float var2, float var3) {
      this(var1, (double)var2, (double)var3);
   }

   private Cs(int var1, double var2, double var4) {
      this(var1, var2, var4, Cs.a.MULTIPLICATIVE, null);
   }

   @Deprecated
   private Cs(int var1, float var2, float var3, int var4) {
      this(var1, var2, var3, var4 == 1 ? Cs.a.ADDITIVE : Cs.a.MULTIPLICATIVE, null);
      int var7 = var4;
      Cs var6 = this;
      if (var7 != 0 && var7 != 1) {
         throw new oV(px.UNSUPPORTED_EXPANSION_MODE, var7, 0, "MULTIPLICATIVE_MODE", 1, "ADDITIVE_MODE");
      }

      synchronized (var6) {
         if (var7 == 0) {
            var6.a(Cs.a.MULTIPLICATIVE);
         } else if (var7 == 1) {
            var6.a(Cs.a.ADDITIVE);
         }
      }
   }

   private Cs(int var1, double var2, double var4, Cs.a var6, double... var7) {
      if (var1 <= 0) {
         throw new pk(px.INITIAL_CAPACITY_NOT_POSITIVE, var1);
      }

      a(var4, var2);
      this.h = var2;
      this.g = var4;
      this.i = var6;
      this.j = new double[var1];
      this.k = 0;
      this.l = 0;
      if (var7 != null && var7.length > 0) {
         this.a(var7);
      }
   }

   private Cs(Cs var1) {
      Ci.a(var1);
      a(var1, this);
   }

   @Override
   public final synchronized void a(double var1) {
      if (this.j.length <= this.l + this.k) {
         this.f();
      }

      this.j[this.l + this.k++] = var1;
   }

   @Override
   public final synchronized void a(double[] var1) {
      double[] var2 = new double[this.k + var1.length + 1];
      System.arraycopy(this.j, this.l, var2, 0, this.k);
      System.arraycopy(var1, 0, var2, this.k, var1.length);
      this.j = var2;
      this.l = 0;
      this.k += var1.length;
   }

   @Override
   public final synchronized double b(double var1) {
      double var3 = this.j[this.l];
      if (this.l + this.k + 1 > this.j.length) {
         this.f();
      }

      this.l++;
      this.j[this.l + (this.k - 1)] = var1;
      if (this.q()) {
         this.e();
      }

      return var3;
   }

   public final synchronized double c(double var1) {
      if (this.k <= 0) {
         throw new oX(px.CANNOT_SUBSTITUTE_ELEMENT_FROM_EMPTY_ARRAY);
      }

      int var3 = this.l + (this.k - 1);
      double var4 = this.j[var3];
      this.j[var3] = var1;
      return var4;
   }

   @Deprecated
   private void a(float var1, float var2) {
      a((double)var1, (double)var2);
   }

   private static void a(double var0, double var2) {
      if (var0 < var2) {
         pn var7;
         (var7 = new pn(var0, 1, true)).a.a(px.CONTRACTION_CRITERIA_SMALLER_THAN_EXPANSION_FACTOR, var0, var2);
         throw var7;
      }

      if (var0 <= 1.0) {
         pn var6;
         (var6 = new pn(var0, 1, false)).a.a(px.CONTRACTION_CRITERIA_SMALLER_THAN_ONE, new Object[]{var0});
         throw var6;
      }

      if (var2 <= 1.0) {
         pn var4;
         (var4 = new pn(var0, 1, false)).a.a(px.EXPANSION_FACTOR_SMALLER_THAN_ONE, new Object[]{var2});
         throw var4;
      }
   }

   @Override
   public final synchronized void c() {
      this.k = 0;
      this.l = 0;
   }

   private synchronized void e() {
      double[] var1 = new double[this.k + 1];
      System.arraycopy(this.j, this.l, var1, 0, this.k);
      this.j = var1;
      this.l = 0;
   }

   public final synchronized void b(int var1) {
      this.a(var1, true);
   }

   public final synchronized void c(int var1) {
      this.a(1, false);
   }

   private synchronized void a(int var1, boolean var2) {
      if (var1 > this.k) {
         throw new oV(px.TOO_MANY_ELEMENTS_TO_DISCARD_FROM_ARRAY, var1, this.k);
      }

      if (var1 < 0) {
         throw new oV(px.CANNOT_DISCARD_NEGATIVE_NUMBER_OF_ELEMENTS, var1);
      }

      this.k -= var1;
      if (var2) {
         this.l += var1;
      }

      if (this.q()) {
         this.e();
      }
   }

   private synchronized void f() {
      int var1;
      if (this.i == Cs.a.MULTIPLICATIVE) {
         var1 = (int)BY.z(this.j.length * this.h);
      } else {
         var1 = (int)(this.j.length + BY.B(this.h));
      }

      double[] var2 = new double[var1];
      System.arraycopy(this.j, 0, var2, 0, this.j.length);
      this.j = var2;
   }

   private synchronized void d(int var1) {
      double[] var2 = new double[var1];
      System.arraycopy(this.j, 0, var2, 0, this.j.length);
      this.j = var2;
   }

   @Deprecated
   private float g() {
      return (float)this.g;
   }

   private double h() {
      return this.g;
   }

   @Override
   public final synchronized double a(int var1) {
      if (var1 >= this.k) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else if (var1 >= 0) {
         return this.j[this.l + var1];
      } else {
         throw new ArrayIndexOutOfBoundsException(var1);
      }
   }

   @Override
   public final synchronized double[] b() {
      double[] var1 = new double[this.k];
      System.arraycopy(this.j, this.l, var1, 0, this.k);
      return var1;
   }

   @Deprecated
   private float i() {
      return (float)this.h;
   }

   @Deprecated
   private int j() {
      synchronized (this) {
         switch (this.i) {
            case MULTIPLICATIVE:
               return 0;
            case ADDITIVE:
               return 1;
            default:
               throw new oY();
         }
      }
   }

   @Deprecated
   private synchronized int k() {
      return this.j.length;
   }

   private int l() {
      return this.j.length;
   }

   @Override
   public final synchronized int a() {
      return this.k;
   }

   @Deprecated
   private synchronized double[] m() {
      return this.j;
   }

   private double[] n() {
      return this.j;
   }

   private int o() {
      return this.l;
   }

   @Deprecated
   private void a(float var1) {
      float var4 = (float)this.h;
      float var3 = var1;
      a((double)var3, (double)var4);
      synchronized (this) {
         this.g = var1;
      }
   }

   public final double a(AA var1) {
      double[] var2;
      int var3;
      int var4;
      synchronized (this) {
         var2 = this.j;
         var3 = this.l;
         var4 = this.k;
      }

      return var1.a(var2, var3, var4);
   }

   @Override
   public final synchronized void a(int var1, double var2) {
      if (var1 < 0) {
         throw new ArrayIndexOutOfBoundsException(var1);
      }

      if (var1 + 1 > this.k) {
         this.k = var1 + 1;
      }

      if (this.l + var1 >= this.j.length) {
         this.d(this.l + var1 + 1);
      }

      this.j[this.l + var1] = var2;
   }

   @Deprecated
   private void b(float var1) {
      a(this.g, var1);
      synchronized (this) {
         this.h = var1;
      }
   }

   @Deprecated
   private void e(int var1) {
      if (var1 != 0 && var1 != 1) {
         throw new oV(px.UNSUPPORTED_EXPANSION_MODE, var1, 0, "MULTIPLICATIVE_MODE", 1, "ADDITIVE_MODE");
      }

      synchronized (this) {
         if (var1 == 0) {
            this.a(Cs.a.MULTIPLICATIVE);
         } else if (var1 == 1) {
            this.a(Cs.a.ADDITIVE);
         }
      }
   }

   @Deprecated
   private void a(Cs.a var1) {
      synchronized (this) {
         this.i = var1;
      }
   }

   @Deprecated
   private static void p() {
   }

   private synchronized void f(int var1) {
      if (var1 < 0) {
         throw new oV(px.INDEX_NOT_POSITIVE, var1);
      }

      int var2;
      if ((var2 = this.l + var1) > this.j.length) {
         this.d(var2);
      }

      this.k = var1;
   }

   private synchronized boolean q() {
      return this.i == Cs.a.MULTIPLICATIVE ? (float)this.j.length / this.k > this.g : this.j.length - this.k > this.g;
   }

   @Deprecated
   private synchronized int r() {
      return this.l;
   }

   private static void a(Cs var0, Cs var1) {
      Ci.a(var0);
      Ci.a(var1);
      synchronized (var0) {
         synchronized (var1) {
            var1.g = var0.g;
            var1.h = var0.h;
            var1.i = var0.i;
            var1.j = new double[var0.j.length];
            System.arraycopy(var0.j, 0, var1.j, 0, var1.j.length);
            var1.k = var0.k;
            var1.l = var0.l;
         }
      }
   }

   public final synchronized Cs d() {
      Cs var1 = new Cs();
      a(this, var1);
      return var1;
   }

   @Override
   public final boolean equals(Object object) {
      if (object == this) {
         return true;
      }

      if (!(object instanceof Cs)) {
         return false;
      }

      synchronized (this) {
         boolean var10000;
         synchronized (object) {
            Cs var4;
            if ((var4 = (Cs)object).g != this.g || var4.h != this.h || var4.i != this.i || var4.k != this.k || var4.l != this.l) {
               return false;
            }

            var10000 = Arrays.equals(this.j, var4.j);
         }

         return var10000;
      }
   }

   @Override
   public final synchronized int hashCode() {
      int[] var1;
      (var1 = new int[6])[0] = Double.valueOf(this.h).hashCode();
      var1[1] = Double.valueOf(this.g).hashCode();
      var1[2] = this.i.hashCode();
      var1[3] = Arrays.hashCode(this.j);
      var1[4] = this.k;
      var1[5] = this.l;
      return Arrays.hashCode(var1);
   }

   public enum a {
      MULTIPLICATIVE,
      ADDITIVE;
   }
}
