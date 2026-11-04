package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.text.FieldPosition;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

public class sA extends tu implements Serializable {
   private static final long b = -1097961340710804027L;
   private static final tw c;
   public double[] a;

   public sA() {
      this.a = new double[0];
   }

   public sA(int var1) {
      this.a = new double[var1];
   }

   private sA(int var1, double var2) {
      this.a = new double[var1];
      Arrays.fill(this.a, var2);
   }

   public sA(double[] var1) {
      this.a = (double[])var1.clone();
   }

   public sA(double[] var1, boolean var2) {
      if (var1 == null) {
         throw new pl();
      }

      this.a = var2 ? (double[])var1.clone() : var1;
   }

   private sA(double[] var1, int var2, int var3) {
      if (var1 == null) {
         throw new pl();
      }

      if (var1.length < var2 + var3) {
         throw new pm(var2 + var3, var1.length, true);
      }

      this.a = new double[var3];
      System.arraycopy(var1, var2, this.a, 0, var3);
   }

   private sA(Double[] var1) {
      this.a = new double[var1.length];

      for (int var2 = 0; var2 < var1.length; var2++) {
         this.a[var2] = var1[var2];
      }
   }

   private sA(Double[] var1, int var2, int var3) {
      if (var1 == null) {
         throw new pl();
      }

      if (var1.length < var2 + var3) {
         throw new pm(var2 + var3, var1.length, true);
      }

      this.a = new double[var3];

      for (int var4 = var2; var4 < var2 + var3; var4++) {
         this.a[var4 - var2] = var1[var4];
      }
   }

   public sA(tu var1) {
      if (var1 == null) {
         throw new pl();
      }

      this.a = new double[var1.f()];

      for (int var2 = 0; var2 < this.a.length; var2++) {
         this.a[var2] = var1.a(var2);
      }
   }

   private sA(sA var1) {
      this(var1, true);
   }

   private sA(sA var1, boolean var2) {
      this.a = (double[])var1.a.clone();
   }

   private sA(sA var1, sA var2) {
      this.a = new double[var1.a.length + var2.a.length];
      System.arraycopy(var1.a, 0, this.a, 0, var1.a.length);
      System.arraycopy(var2.a, 0, this.a, var1.a.length, var2.a.length);
   }

   private sA(sA var1, tu var2) {
      int var3 = var1.a.length;
      int var4 = var2.f();
      this.a = new double[var3 + var4];
      System.arraycopy(var1.a, 0, this.a, 0, var3);

      for (int var5 = 0; var5 < var4; var5++) {
         this.a[var3 + var5] = var2.a(var5);
      }
   }

   private sA(tu var1, sA var2) {
      int var3 = var1.f();
      int var4 = var2.a.length;
      this.a = new double[var3 + var4];

      for (int var5 = 0; var5 < var3; var5++) {
         this.a[var5] = var1.a(var5);
      }

      System.arraycopy(var2.a, 0, this.a, var3, var4);
   }

   private sA(sA var1, double[] var2) {
      int var3 = var1.a.length;
      int var4 = var2.length;
      this.a = new double[var3 + var4];
      System.arraycopy(var1.a, 0, this.a, 0, var3);
      System.arraycopy(var2, 0, this.a, var3, var4);
   }

   private sA(double[] var1, sA var2) {
      int var3 = var1.length;
      int var4 = var2.a.length;
      this.a = new double[var3 + var4];
      System.arraycopy(var1, 0, this.a, 0, var3);
      System.arraycopy(var2.a, 0, this.a, var3, var4);
   }

   private sA(double[] var1, double[] var2) {
      int var3 = var1.length;
      int var4 = var2.length;
      this.a = new double[var3 + var4];
      System.arraycopy(var1, 0, this.a, 0, var3);
      System.arraycopy(var2, 0, this.a, var3, var4);
   }

   public final sA a() {
      return new sA(this, true);
   }

   private sA o(tu var1) {
      if (var1 instanceof sA) {
         double[] var8 = ((sA)var1).a;
         int var7 = ((sA)var1).a.length;
         this.b(var7);
         sA var9;
         double[] var4 = (var9 = new sA(var7)).a;

         for (int var5 = 0; var5 < var7; var5++) {
            var4[var5] = this.a[var5] + var8[var5];
         }

         return var9;
      } else {
         this.h(var1);
         double[] var2 = (double[])this.a.clone();
         Iterator var6 = var1.l();

         while (var6.hasNext()) {
            tu.a var3 = (tu.a)var6.next();
            var2[var3.b()] += var3.a();
         }

         return new sA(var2, false);
      }
   }

   public final sA a(tu var1) {
      if (var1 instanceof sA) {
         double[] var8 = ((sA)var1).a;
         int var7 = ((sA)var1).a.length;
         this.b(var7);
         sA var9;
         double[] var4 = (var9 = new sA(var7)).a;

         for (int var5 = 0; var5 < var7; var5++) {
            var4[var5] = this.a[var5] - var8[var5];
         }

         return var9;
      } else {
         this.h(var1);
         double[] var2 = (double[])this.a.clone();
         Iterator var6 = var1.l();

         while (var6.hasNext()) {
            tu.a var3 = (tu.a)var6.next();
            var2[var3.b()] -= var3.a();
         }

         return new sA(var2, false);
      }
   }

   public final sA a(lc var1) {
      return this.a().d(var1);
   }

   private sA d(lc var1) {
      for (int var2 = 0; var2 < this.a.length; var2++) {
         this.a[var2] = var1.a(this.a[var2]);
      }

      return this;
   }

   @Override
   public final tu a(double var1) {
      for (int var3 = 0; var3 < this.a.length; var3++) {
         this.a[var3] = this.a[var3] + var1;
      }

      return this;
   }

   @Override
   public final tu b(double var1) {
      for (int var3 = 0; var3 < this.a.length; var3++) {
         this.a[var3] = this.a[var3] - var1;
      }

      return this;
   }

   @Override
   public final tu c(double var1) {
      for (int var3 = 0; var3 < this.a.length; var3++) {
         this.a[var3] = this.a[var3] * var1;
      }

      return this;
   }

   @Override
   public final tu d(double var1) {
      for (int var3 = 0; var3 < this.a.length; var3++) {
         this.a[var3] = this.a[var3] / var1;
      }

      return this;
   }

   private sA p(tu var1) {
      if (var1 instanceof sA) {
         double[] var7 = var1.a;
         int var8 = var1.a.length;
         this.b(var8);
         double[] var4 = (var1 = new sA(var8)).a;

         for (int var5 = 0; var5 < var8; var5++) {
            var4[var5] = this.a[var5] * var7[var5];
         }

         return var1;
      } else {
         this.h(var1);
         double[] var2 = (double[])this.a.clone();

         for (int var3 = 0; var3 < this.a.length; var3++) {
            var2[var3] *= var1.a(var3);
         }

         return new sA(var2, false);
      }
   }

   private sA q(tu var1) {
      if (var1 instanceof sA) {
         double[] var7 = var1.a;
         int var8 = var1.a.length;
         this.b(var8);
         double[] var4 = (var1 = new sA(var8)).a;

         for (int var5 = 0; var5 < var8; var5++) {
            var4[var5] = this.a[var5] / var7[var5];
         }

         return var1;
      } else {
         this.h(var1);
         double[] var2 = (double[])this.a.clone();

         for (int var3 = 0; var3 < this.a.length; var3++) {
            var2[var3] /= var1.a(var3);
         }

         return new sA(var2, false);
      }
   }

   public final double[] b() {
      return this.a;
   }

   @Override
   public final double b(tu var1) {
      if (!(var1 instanceof sA)) {
         return super.b(var1);
      }

      double[] var5 = ((sA)var1).a;
      this.b(var5.length);
      double var3 = 0.0;

      for (int var2 = 0; var2 < this.a.length; var2++) {
         var3 += this.a[var2] * var5[var2];
      }

      return var3;
   }

   @Override
   public final double c() {
      double var1 = 0.0;
      double[] var3 = this.a;
      int var4 = this.a.length;

      for (int var5 = 0; var5 < var4; var5++) {
         double var6 = var3[var5];
         var1 += var6 * var6;
      }

      return BY.a(var1);
   }

   @Override
   public final double d() {
      double var1 = 0.0;
      double[] var3 = this.a;
      int var4 = this.a.length;

      for (int var5 = 0; var5 < var4; var5++) {
         double var6 = var3[var5];
         var1 += BY.w(var6);
      }

      return var1;
   }

   @Override
   public final double e() {
      double var1 = 0.0;
      double[] var3 = this.a;
      int var4 = this.a.length;

      for (int var5 = 0; var5 < var4; var5++) {
         double var6 = var3[var5];
         var1 = BY.e(var1, BY.w(var6));
      }

      return var1;
   }

   @Override
   public final double c(tu var1) {
      if (var1 instanceof sA) {
         double[] var8 = ((sA)var1).a;
         this.b(var8.length);
         double var3 = 0.0;

         for (int var9 = 0; var9 < this.a.length; var9++) {
            double var6 = this.a[var9] - var8[var9];
            var3 += var6 * var6;
         }

         return BY.a(var3);
      } else {
         this.h(var1);
         double var2 = 0.0;

         for (int var4 = 0; var4 < this.a.length; var4++) {
            double var5 = this.a[var4] - var1.a(var4);
            var2 += var5 * var5;
         }

         return BY.a(var2);
      }
   }

   @Override
   public final double d(tu var1) {
      if (var1 instanceof sA) {
         double[] var8 = ((sA)var1).a;
         this.b(var8.length);
         double var3 = 0.0;

         for (int var9 = 0; var9 < this.a.length; var9++) {
            double var6 = this.a[var9] - var8[var9];
            var3 += BY.w(var6);
         }

         return var3;
      } else {
         this.h(var1);
         double var2 = 0.0;

         for (int var4 = 0; var4 < this.a.length; var4++) {
            double var5 = this.a[var4] - var1.a(var4);
            var2 += BY.w(var5);
         }

         return var2;
      }
   }

   @Override
   public final double e(tu var1) {
      if (var1 instanceof sA) {
         double[] var8 = ((sA)var1).a;
         this.b(var8.length);
         double var3 = 0.0;

         for (int var9 = 0; var9 < this.a.length; var9++) {
            double var6 = this.a[var9] - var8[var9];
            var3 = BY.e(var3, BY.w(var6));
         }

         return var3;
      } else {
         this.h(var1);
         double var2 = 0.0;

         for (int var4 = 0; var4 < this.a.length; var4++) {
            double var5 = this.a[var4] - var1.a(var4);
            var2 = BY.e(var2, BY.w(var5));
         }

         return var2;
      }
   }

   @Override
   public final tq f(tu var1) {
      if (var1 instanceof sA) {
         double[] var8 = ((sA)var1).a;
         int var9 = this.a.length;
         int var10 = var8.length;
         sv var11 = td.a(var9, var10);

         for (int var12 = 0; var12 < var9; var12++) {
            for (int var7 = 0; var7 < var10; var7++) {
               var11.c(var12, var7, this.a[var12] * var8[var7]);
            }
         }

         return var11;
      } else {
         int var2 = this.a.length;
         int var3 = var1.f();
         sv var4 = td.a(var2, var3);

         for (int var5 = 0; var5 < var2; var5++) {
            for (int var6 = 0; var6 < var3; var6++) {
               var4.c(var5, var6, this.a[var5] * var1.a(var6));
            }
         }

         return var4;
      }
   }

   @Override
   public final double a(int var1) {
      try {
         return this.a[var1];
      } catch (IndexOutOfBoundsException var2) {
         throw new po(px.INDEX, var1, 0, this.a.length - 1);
      }
   }

   @Override
   public final int f() {
      return this.a.length;
   }

   @Override
   public final tu g(tu var1) {
      try {
         return new sA(this, (sA)var1);
      } catch (ClassCastException var2) {
         return new sA(this, var1);
      }
   }

   private sA a(sA var1) {
      return new sA(this, var1);
   }

   @Override
   public final tu e(double var1) {
      double[] var3 = new double[this.a.length + 1];
      System.arraycopy(this.a, 0, var3, 0, this.a.length);
      var3[this.a.length] = var1;
      return new sA(var3, false);
   }

   @Override
   public final tu a(int var1, int var2) {
      if (var2 < 0) {
         throw new pj(px.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, var2);
      }

      sA var3 = new sA(var2);

      try {
         System.arraycopy(this.a, var1, var3.a, 0, var2);
      } catch (IndexOutOfBoundsException var4) {
         this.c(var1);
         this.c(var1 + var2 - 1);
      }

      return var3;
   }

   @Override
   public final void a(int var1, double var2) {
      try {
         this.a[var1] = var2;
      } catch (IndexOutOfBoundsException var4) {
         this.c(var1);
      }
   }

   @Override
   public final void b(int var1, double var2) {
      try {
         this.a[var1] = this.a[var1] + var2;
      } catch (IndexOutOfBoundsException var4) {
         throw new po(px.INDEX, var1, 0, this.a.length - 1);
      }
   }

   @Override
   public final void a(int var1, tu var2) {
      if (var2 instanceof sA) {
         double[] var3 = ((sA)var2).a;
         byte var8 = 0;
         sA var6 = this;

         try {
            System.arraycopy(var3, 0, var6.a, var8, var3.length);
         } catch (IndexOutOfBoundsException var4) {
            var6.c(var8);
            var6.c(var8 + var3.length - 1);
            return;
         }
      } else {
         try {
            for (int var7 = 0; var7 < 0 + var2.f(); var7++) {
               this.a[var7] = var2.a(var7);
            }

            return;
         } catch (IndexOutOfBoundsException var5) {
            this.c(0);
            this.c(0 + var2.f() - 1);
         }
      }
   }

   private void a(int var1, double[] var2) {
      try {
         System.arraycopy(var2, 0, this.a, var1, var2.length);
      } catch (IndexOutOfBoundsException var3) {
         this.c(var1);
         this.c(var1 + var2.length - 1);
      }
   }

   @Override
   public final void f(double var1) {
      Arrays.fill(this.a, 0.0);
   }

   @Override
   public final double[] g() {
      return (double[])this.a.clone();
   }

   @Override
   public String toString() {
      tw var10000 = c;
      sA var2 = this;
      tw var1 = c;
      StringBuffer var10002 = new StringBuffer();
      FieldPosition var4 = new FieldPosition(0);
      StringBuffer var3 = var10002;
      var2 = var2;
      var1 = var10000;
      var4.setBeginIndex(0);
      var4.setEndIndex(0);
      var3.append(var1.a);

      for (int var5 = 0; var5 < var2.f(); var5++) {
         if (var5 > 0) {
            var3.append(var1.c);
         }

         BS.a(var2.a(var5), var1.d, var3, var4);
      }

      var3.append(var1.b);
      return var3.toString();
   }

   @Override
   protected final void h(tu var1) {
      this.b(var1.f());
   }

   @Override
   protected final void b(int var1) {
      if (this.a.length != var1) {
         throw new oS(this.a.length, var1);
      }
   }

   @Override
   public final boolean h() {
      double[] var1 = this.a;
      int var2 = this.a.length;

      for (int var3 = 0; var3 < var2; var3++) {
         double var4;
         if (Double.isNaN(var4 = var1[var3])) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final boolean i() {
      if (this.h()) {
         return false;
      }

      double[] var1 = this.a;
      int var2 = this.a.length;

      for (int var3 = 0; var3 < var2; var3++) {
         double var4;
         if (Double.isInfinite(var4 = var1[var3])) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof tu)) {
         return false;
      }

      other = (tu)other;
      if (this.a.length != ((tu)other).f()) {
         return false;
      }

      if (((tu)other).h()) {
         return this.h();
      }

      for (int var2 = 0; var2 < this.a.length; var2++) {
         if (this.a[var2] != ((tu)other).a(var2)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public int hashCode() {
      if (this.h()) {
         return 9;
      }

      double[] var1 = this.a;
      return Arrays.hashCode(this.a);
   }

   private sA c(double var1, double var3, tu var5) {
      return this.a().d(var1, var3, var5);
   }

   private sA d(double var1, double var3, tu var5) {
      if (var5 instanceof sA) {
         double[] var6 = ((sA)var5).a;
         this.b(var6.length);

         for (int var7 = 0; var7 < this.a.length; var7++) {
            this.a[var7] = var1 * this.a[var7] + var3 * var6[var7];
         }
      } else {
         this.h(var5);

         for (int var8 = 0; var8 < this.a.length; var8++) {
            this.a[var8] = var1 * this.a[var8] + var3 * var5.a(var8);
         }
      }

      return this;
   }

   @Override
   public final double a(tx var1) {
      int var10000 = this.a.length;
      var10000 = this.a.length;

      for (int var2 = 0; var2 < this.a.length; var2++) {
         double var4 = this.a[var2];
      }

      return var1.c();
   }

   @Override
   public final double a(tx var1, int var2, int var3) {
      this.b(var2, var3);
      int var10000 = this.a.length;

      for (int var4 = var2; var4 <= var3; var4++) {
         double var5 = this.a[var4];
      }

      return var1.c();
   }

   @Override
   public final double b(tx var1) {
      return this.a(var1);
   }

   @Override
   public final double b(tx var1, int var2, int var3) {
      return this.a(var1, var2, var3);
   }

   @Override
   public final double a(tv var1) {
      int var10000 = this.a.length;
      var10000 = this.a.length;

      for (int var2 = 0; var2 < this.a.length; var2++) {
         double var10003 = this.a[var2];
         this.a[var2] = var1.b();
      }

      return var1.c();
   }

   @Override
   public final double a(tv var1, int var2, int var3) {
      this.b(var2, var3);
      int var10000 = this.a.length;

      for (int var4 = var2; var4 <= var3; var4++) {
         double var10003 = this.a[var4];
         this.a[var4] = var1.b();
      }

      return var1.c();
   }

   @Override
   public final double b(tv var1) {
      return this.a(var1);
   }

   @Override
   public final double b(tv var1, int var2, int var3) {
      return this.a(var1, var2, var3);
   }

   // $VF: synthetic method
   @Override
   public final tu a(double var1, double var3, tu var5) {
      return this.d(var1, var3, var5);
   }

   // $VF: synthetic method
   @Override
   public final tu b(double var1, double var3, tu var5) {
      tu var2 = var5;
      double var9 = var3;
      double var7 = var1;
      return this.a().d(var7, var9, var2);
   }

   // $VF: synthetic method
   @Override
   public final tu b(lc var1) {
      return this.d(var1);
   }

   // $VF: synthetic method
   @Override
   public final tu c(lc var1) {
      return this.a(var1);
   }

   // $VF: synthetic method
   @Override
   public final tu i(tu var1) {
      tu var2 = var1;
      var1 = this;
      if (var2 instanceof sA) {
         double[] var9 = ((sA)var2).a;
         int var10 = ((sA)var2).a.length;
         var1.b(var10);
         sA var8;
         double[] var5 = (var8 = new sA(var10)).a;

         for (int var6 = 0; var6 < var10; var6++) {
            var5[var6] = var1.a[var6] * var9[var6];
         }

         return var8;
      } else {
         var1.h(var2);
         double[] var3 = (double[])var1.a.clone();

         for (int var4 = 0; var4 < var1.a.length; var4++) {
            var3[var4] *= var2.a(var4);
         }

         return new sA(var3, false);
      }
   }

   // $VF: synthetic method
   @Override
   public final tu j(tu var1) {
      tu var2 = var1;
      var1 = this;
      if (var2 instanceof sA) {
         double[] var9 = ((sA)var2).a;
         int var10 = ((sA)var2).a.length;
         var1.b(var10);
         sA var8;
         double[] var5 = (var8 = new sA(var10)).a;

         for (int var6 = 0; var6 < var10; var6++) {
            var5[var6] = var1.a[var6] / var9[var6];
         }

         return var8;
      } else {
         var1.h(var2);
         double[] var3 = (double[])var1.a.clone();

         for (int var4 = 0; var4 < var1.a.length; var4++) {
            var3[var4] /= var2.a(var4);
         }

         return new sA(var3, false);
      }
   }

   // $VF: synthetic method
   @Override
   public final tu j() {
      return this.a();
   }

   // $VF: synthetic method
   @Override
   public final tu k(tu var1) {
      return this.a(var1);
   }

   // $VF: synthetic method
   @Override
   public final tu l(tu var1) {
      tu var2 = var1;
      var1 = this;
      if (var2 instanceof sA) {
         double[] var10 = ((sA)var2).a;
         int var9 = ((sA)var2).a.length;
         var1.b(var9);
         sA var11;
         double[] var5 = (var11 = new sA(var9)).a;

         for (int var6 = 0; var6 < var9; var6++) {
            var5[var6] = var1.a[var6] + var10[var6];
         }

         return var11;
      } else {
         var1.h(var2);
         double[] var3 = (double[])var1.a.clone();
         Iterator var8 = var2.l();

         while (var8.hasNext()) {
            tu.a var4 = (tu.a)var8.next();
            var3[var4.b()] += var4.a();
         }

         return new sA(var3, false);
      }
   }

   static {
      Locale var0 = Locale.getDefault();
      c = new tw(BS.a(var0));
   }
}
