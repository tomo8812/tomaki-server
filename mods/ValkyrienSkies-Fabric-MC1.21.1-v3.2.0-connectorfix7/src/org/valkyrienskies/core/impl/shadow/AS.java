package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class AS extends An implements Serializable, Av {
   private static final int a = 5;
   private static final double b = 50.0;
   private static final long c = 2283912083175715479L;
   private static final DecimalFormat d = new DecimalFormat("00.00");
   private final AS.a e = new AS.a(5);
   private final double f;
   private transient double g;
   private AS.d h = null;
   private double i = Double.NaN;
   private long j;

   private AS(double var1) {
      if (!(var1 > 100.0) && !(var1 < 0.0)) {
         this.f = var1 / 100.0;
      } else {
         throw new po(px.OUT_OF_RANGE, var1, 0, 100);
      }
   }

   AS() {
      this(50.0);
   }

   @Override
   public final int hashCode() {
      double var1;
      var1 = Double.isNaN(var1 = this.c()) ? 37.0 : var1;
      double var3 = this.h == null ? 0.0 : this.h.hashCode();
      return Arrays.hashCode(new double[]{var1, this.f, var3, this.j});
   }

   @Override
   public final boolean equals(Object o) {
      boolean var2 = false;
      boolean var10000;
      if (this == o) {
         var10000 = true;
      } else {
         if (o == null || !(o instanceof AS)) {
            return var2;
         }

         o = (AS)o;
         var2 = this.h != null && ((AS)o).h != null;
         boolean var3 = this.h == null && ((AS)o).h == null;
         var10000 = (var2 ? this.h.equals(((AS)o).h) : var3) && this.j == ((AS)o).j;
      }

      return var10000;
   }

   @Override
   public final void a(double var1) {
      this.j++;
      this.g = var1;
      if (this.h == null) {
         if (this.e.add(var1)) {
            Collections.sort(this.e);
            this.i = (Double)this.e.get((int)(this.f * (this.e.size() - 1)));
            return;
         }

         double var4 = this.f;
         AS.a var3 = this.e;
         this.h = new AS.c(var3, var4, (byte)0);
      }

      this.i = this.h.a(var1);
   }

   @Override
   public final String toString() {
      return this.h == null
         ? String.format("obs=%s pValue=%s", d.format(this.g), d.format(this.i))
         : String.format("obs=%s markers=%s", d.format(this.g), this.h.toString());
   }

   @Override
   public final long g_() {
      return this.j;
   }

   @Override
   public final An a() {
      AS var1 = new AS(100.0 * this.f);
      if (this.h != null) {
         var1.h = (AS.d)this.h.clone();
      }

      var1.j = this.j;
      var1.i = this.i;
      var1.e.clear();
      var1.e.addAll(this.e);
      return var1;
   }

   private double f() {
      return this.f;
   }

   @Override
   public final void b() {
      this.h = null;
      this.e.clear();
      this.j = 0L;
      this.i = Double.NaN;
   }

   @Override
   public final double c() {
      if (Double.compare(this.f, 1.0) == 0) {
         this.i = this.g();
      } else if (Double.compare(this.f, 0.0) == 0) {
         this.i = this.h();
      }

      return this.i;
   }

   private double g() {
      double var1 = Double.NaN;
      if (this.h != null) {
         var1 = this.h.b(5);
      } else if (!this.e.isEmpty()) {
         var1 = (Double)this.e.get(this.e.size() - 1);
      }

      return var1;
   }

   private double h() {
      double var1 = Double.NaN;
      if (this.h != null) {
         var1 = this.h.b(1);
      } else if (!this.e.isEmpty()) {
         var1 = (Double)this.e.get(0);
      }

      return var1;
   }

   private static AS.d a(List<Double> var0, double var1) {
      return new AS.c(var0, var1, (byte)0);
   }

   // $VF: synthetic method
   @Override
   public final AA d() {
      return this.a();
   }

   static class a<E> extends ArrayList<E> implements Serializable {
      private static final long a = 2283952083075725479L;
      private final int b = 5;

      a(int var1) {
         super(5);
      }

      @Override
      public final boolean add(E e) {
         return this.size() < this.b ? super.add(e) : false;
      }

      @Override
      public final boolean addAll(Collection<? extends E> collection) {
         return collection != null && collection.size() + this.size() <= this.b ? super.addAll(collection) : false;
      }
   }

   static class b implements Serializable, Cloneable {
      private static final long i = -3575879478288538431L;
      private int j;
      double a;
      double b;
      double c;
      double d;
      transient AS.b e;
      transient AS.b f;
      final mU g = new mU();
      transient mO h = new mO();

      private b() {
         this.e = this.f = this;
      }

      private b(double var1, double var3, double var5, double var7) {
         this();
         this.c = var1;
         this.b = var3;
         this.d = var5;
         this.a = var7;
      }

      private AS.b d(AS.b var1) {
         Ci.a(var1);
         this.f = var1;
         return this;
      }

      private AS.b e(AS.b var1) {
         Ci.a(var1);
         this.e = var1;
         return this;
      }

      private AS.b b(int var1) {
         this.j = var1;
         return this;
      }

      private void a() {
         this.b = this.b + this.d;
      }

      void a(int var1) {
         this.a += var1;
      }

      private double b() {
         return this.b - this.a;
      }

      private double c() {
         AS.b var4 = this;
         double var1 = this.b - var4.a;
         boolean var3 = this.e.a - this.a > 1.0;
         boolean var21 = this.f.a - this.a < -1.0;
         if (var1 >= 1.0 && var3 || var1 <= -1.0 && var21) {
            int var17 = var1 >= 0.0 ? 1 : -1;
            double[] var2 = new double[]{this.f.a, this.a, this.e.a};
            double[] var19 = new double[]{this.f.c, this.c, this.e.c};
            double var8 = this.a + var17;
            lc var22 = this.g.b(var2, var19);
            this.c = var22.a(var8);
            double var15 = this.c;
            double[] var23 = var19;
            if (var15 <= var23[0] || var15 >= var23[2]) {
               int var24 = var8 - var2[1] > 0.0 ? 1 : -1;
               var2 = new double[]{var2[1], var2[var24 + 1]};
               double[] var20 = new double[]{var19[1], var19[var24 + 1]};
               Ch.a(var2, new double[][]{var20});
               lc var25 = this.h.b(var2, var20);
               this.c = var25.a(var8);
            }

            this.a(var17);
         }

         return this.c;
      }

      private static boolean a(double[] var0, double var1) {
         return var1 <= var0[0] || var1 >= var0[2];
      }

      @Override
      public final boolean equals(Object o) {
         boolean var2 = false;
         boolean var10000;
         if (this == o) {
            var10000 = true;
         } else {
            if (o == null || !(o instanceof AS.b)) {
               return var2;
            }

            o = (AS.b)o;
            var10000 = Double.compare(this.c, ((AS.b)o).c) == 0
               && Double.compare(this.a, ((AS.b)o).a) == 0
               && Double.compare(this.b, ((AS.b)o).b) == 0
               && Double.compare(this.d, ((AS.b)o).d) == 0
               && this.e.j == ((AS.b)o).e.j
               && this.f.j == ((AS.b)o).f.j;
         }

         return var10000;
      }

      @Override
      public final int hashCode() {
         return Arrays.hashCode(new double[]{this.c, this.a, this.d, this.b, this.f.j, this.e.j});
      }

      private void a(ObjectInputStream var1) {
         var1.defaultReadObject();
         this.f = this.e = this;
         this.h = new mO();
      }

      @Override
      public final Object clone() {
         return new AS.b(this.c, this.b, this.d, this.a);
      }

      @Override
      public final String toString() {
         return String.format(
            "index=%.0f,n=%.0f,np=%.2f,q=%.2f,dn=%.2f,prev=%d,next=%d",
            (double)this.j,
            Cq.a(this.a, 0),
            Cq.a(this.b, 2),
            Cq.a(this.c, 2),
            Cq.a(this.d, 2),
            this.f.j,
            this.e.j
         );
      }

      // $VF: synthetic method
      static AS.b a(AS.b var0, AS.b var1) {
         var1 = var1;
         var0 = var0;
         Ci.a(var1);
         var0.f = var1;
         return var0;
      }

      // $VF: synthetic method
      static AS.b b(AS.b var0, AS.b var1) {
         var1 = var1;
         var0 = var0;
         Ci.a(var1);
         var0.e = var1;
         return var0;
      }

      // $VF: synthetic method
      static AS.b a(AS.b var0, int var1) {
         var1 = var1;
         AS.b var2 = var0;
         var0.j = var1;
         return var2;
      }

      // $VF: synthetic method
      b(byte var1) {
         this();
      }

      // $VF: synthetic method
      b(double var1, double var3, double var5, double var7, byte var9) {
         this(var1, var3, var5, var7);
      }

      // $VF: synthetic method
      static double a(AS.b var0, double var1) {
         return var0.c = var1;
      }

      // $VF: synthetic method
      static double a(AS.b var0) {
         AS.b var4;
         AS.b var18;
         double var2 = (var4 = var18 = var0).b - var4.a;
         int var1 = var18.e.a - var18.a > 1.0;
         boolean var23 = var18.f.a - var18.a < -1.0;
         if (var2 >= 1.0 && var1 || var2 <= -1.0 && var23) {
            var1 = var2 >= 0.0 ? 1 : -1;
            double[] var20 = new double[]{var18.f.a, var18.a, var18.e.a};
            double[] var3 = new double[]{var18.f.c, var18.c, var18.e.c};
            double var9 = var18.a + var1;
            lc var24 = var18.g.b(var20, var3);
            var18.c = var24.a(var9);
            double var16 = var18.c;
            double[] var25 = var3;
            if (var16 <= var25[0] || var16 >= var25[2]) {
               int var26 = var9 - var20[1] > 0.0 ? 1 : -1;
               double[] var21 = new double[]{var20[1], var20[var26 + 1]};
               var3 = new double[]{var3[1], var3[var26 + 1]};
               Ch.a(var21, new double[][]{var3});
               lc var27 = var18.h.b(var21, var3);
               var18.c = var27.a(var9);
            }

            var18.a(var1);
         }

         return var18.c;
      }

      // $VF: synthetic method
      static void b(AS.b var0, int var1) {
         var0.a(var1);
      }

      // $VF: synthetic method
      static void b(AS.b var0) {
         AS.b var1;
         AS.b var10000 = var1 = var0;
         var10000.b = var10000.b + var1.d;
      }

      // $VF: synthetic method
      static double c(AS.b var0) {
         return var0.c;
      }
   }

   static class c implements Serializable, AS.d {
      private static final long a = 1L;
      private static final int b = 2;
      private static final int c = 4;
      private final AS.b[] d;
      private transient int e = -1;

      private c(AS.b[] var1) {
         Ci.a(var1);
         this.d = var1;

         for (int var2 = 1; var2 < 5; var2++) {
            AS.b.a(AS.b.b(AS.b.a(this.d[var2], this.d[var2 - 1]), this.d[var2 + 1]), var2);
         }

         AS.b.a(AS.b.b(AS.b.a(this.d[0], this.d[0]), this.d[1]), 0);
         AS.b.a(AS.b.b(AS.b.a(this.d[5], this.d[4]), this.d[5]), 5);
      }

      private c(List<Double> var1, double var2) {
         double var5 = var2;
         var1 = var1;
         int var8;
         if ((var8 = var1 == null ? -1 : var1.size()) < 5) {
            throw new oT(px.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE, var8, 5);
         }

         Collections.sort(var1);
         this(
            new AS.b[]{
               new AS.b((byte)0),
               new AS.b((Double)var1.get(0), 1.0, 0.0, 1.0, (byte)0),
               new AS.b((Double)var1.get(1), 1.0 + var5 * 2.0, var5 / 2.0, 2.0, (byte)0),
               new AS.b((Double)var1.get(2), 1.0 + var5 * 4.0, var5, 3.0, (byte)0),
               new AS.b((Double)var1.get(3), 3.0 + var5 * 2.0, (var5 + 1.0) / 2.0, 4.0, (byte)0),
               new AS.b((Double)var1.get(4), 5.0, 1.0, 5.0, (byte)0)
            }
         );
      }

      private static AS.b[] a(List<Double> var0, double var1) {
         int var3;
         if ((var3 = var0 == null ? -1 : var0.size()) < 5) {
            throw new oT(px.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE, var3, 5);
         }

         Collections.sort(var0);
         return new AS.b[]{
            new AS.b((byte)0),
            new AS.b((Double)var0.get(0), 1.0, 0.0, 1.0, (byte)0),
            new AS.b((Double)var0.get(1), 1.0 + var1 * 2.0, var1 / 2.0, 2.0, (byte)0),
            new AS.b((Double)var0.get(2), 1.0 + var1 * 4.0, var1, 3.0, (byte)0),
            new AS.b((Double)var0.get(3), 3.0 + var1 * 2.0, (var1 + 1.0) / 2.0, 4.0, (byte)0),
            new AS.b((Double)var0.get(4), 5.0, 1.0, 5.0, (byte)0)
         };
      }

      @Override
      public final int hashCode() {
         return Arrays.deepHashCode(this.d);
      }

      @Override
      public final boolean equals(Object o) {
         boolean var2 = false;
         if (this == o) {
            var2 = true;
         } else if (o != null && o instanceof AS.c) {
            o = (AS.c)o;
            var2 = Arrays.deepEquals(this.d, ((AS.c)o).d);
         }

         return var2;
      }

      @Override
      public final double a(double var1) {
         double var5 = var1;
         AS.c var29 = this;
         this.e = -1;
         if (var5 < var29.b(1)) {
            AS.b var10000 = var29.d[1];
            double var27 = var5;
            var10000.c = var27;
            var29.e = 1;
         } else if (var5 < var29.b(2)) {
            var29.e = 1;
         } else if (var5 < var29.b(3)) {
            var29.e = 2;
         } else if (var5 < var29.b(4)) {
            var29.e = 3;
         } else if (var5 <= var29.b(5)) {
            var29.e = 4;
         } else {
            AS.b var55 = var29.d[5];
            double var54 = var5;
            var55.c = var54;
            var29.e = 4;
         }

         int var30 = var29.e;
         int var10002 = var30 + 1;
         int var2 = 5;
         int var6 = var10002;
         boolean var42 = true;
         AS.c var31 = this;

         for (int var34 = var6; var34 <= 5; var34++) {
            AS.b var56 = var31.d[var34];
            byte var10 = 1;
            var56.a(var10);
         }

         AS.c var32 = this;

         for (int var43 = 1; var43 < var32.d.length; var43++) {
            AS.b var35;
            AS.b var45;
            AS.b var57 = var35 = var45 = var32.d[var43];
            var57.b = var57.b + var35.d;
         }

         AS.c var33 = this;

         for (int var44 = 2; var44 <= 4; var44++) {
            var2 = var44;
            AS.c var46 = var33;
            if (var2 < 2 || var2 > 4) {
               throw new po(var2, 2, 4);
            }

            AS.b var3;
            AS.b var7;
            AS.b var37;
            double var53 = (var7 = var3 = var37 = var46.d[var2]).b - var7.a;
            int var38 = var3.e.a - var3.a > 1.0;
            boolean var4 = var3.f.a - var3.a < -1.0;
            if (var53 >= 1.0 && var38 || var53 <= -1.0 && var4) {
               var38 = var53 >= 0.0 ? 1 : -1;
               double[] var40 = new double[]{var3.f.a, var3.a, var3.e.a};
               double[] var47 = new double[]{var3.f.c, var3.c, var3.e.c};
               double var17 = var3.a + var38;
               lc var49 = var3.g.b(var40, var47);
               var3.c = var49.a(var17);
               double var24 = var3.c;
               double[] var50 = var47;
               if (var24 <= var50[0] || var24 >= var50[2]) {
                  int var51 = var17 - var40[1] > 0.0 ? 1 : -1;
                  double[] var41 = new double[]{var40[1], var40[var51 + 1]};
                  double[] var48 = new double[]{var47[1], var47[var51 + 1]};
                  Ch.a(var41, new double[][]{var48});
                  lc var52 = var3.h.b(var41, var48);
                  var3.c = var52.a(var17);
               }

               var3.a(var38);
            }
         }

         return this.b(3);
      }

      @Override
      public final double a() {
         return this.b(3);
      }

      private int b(double var1) {
         this.e = -1;
         if (var1 < this.b(1)) {
            AS.b var10000 = this.d[1];
            double var4 = var1;
            var10000.c = var4;
            this.e = 1;
         } else if (var1 < this.b(2)) {
            this.e = 1;
         } else if (var1 < this.b(3)) {
            this.e = 2;
         } else if (var1 < this.b(4)) {
            this.e = 3;
         } else if (var1 <= this.b(5)) {
            this.e = 4;
         } else {
            AS.b var7 = this.d[5];
            double var6 = var1;
            var7.c = var6;
            this.e = 4;
         }

         return this.e;
      }

      private void b() {
         for (int var1 = 2; var1 <= 4; var1++) {
            this.a(var1);
         }
      }

      @Override
      public final double a(int var1) {
         if (var1 >= 2 && var1 <= 4) {
            return AS.b.a(this.d[var1]);
         } else {
            throw new po(var1, 2, 4);
         }
      }

      private void a(int var1, int var2, int var3) {
         for (int var4 = var2; var4 <= 5; var4++) {
            AS.b var10000 = this.d[var4];
            byte var5 = 1;
            var10000.a(var5);
         }
      }

      private void c() {
         for (int var1 = 1; var1 < this.d.length; var1++) {
            AS.b.b(this.d[var1]);
         }
      }

      private void a(ObjectInputStream var1) {
         var1.defaultReadObject();

         for (int var2 = 1; var2 < 5; var2++) {
            AS.b.a(AS.b.b(AS.b.a(this.d[var2], this.d[var2 - 1]), this.d[var2 + 1]), var2);
         }

         AS.b.a(AS.b.b(AS.b.a(this.d[0], this.d[0]), this.d[1]), 0);
         AS.b.a(AS.b.b(AS.b.a(this.d[5], this.d[4]), this.d[5]), 5);
      }

      @Override
      public final double b(int var1) {
         if (var1 < this.d.length && var1 > 0) {
            AS.b var2;
            return (var2 = this.d[var1]).c;
         } else {
            throw new po(var1, 1, this.d.length);
         }
      }

      @Override
      public final Object clone() {
         return new AS.c(
            new AS.b[]{
               new AS.b((byte)0), (AS.b)this.d[1].clone(), (AS.b)this.d[2].clone(), (AS.b)this.d[3].clone(), (AS.b)this.d[4].clone(), (AS.b)this.d[5].clone()
            }
         );
      }

      @Override
      public final String toString() {
         return String.format(
            "m1=[%s],m2=[%s],m3=[%s],m4=[%s],m5=[%s]",
            this.d[1].toString(),
            this.d[2].toString(),
            this.d[3].toString(),
            this.d[4].toString(),
            this.d[5].toString()
         );
      }

      // $VF: synthetic method
      c(List var1, double var2, byte var4) {
         this(var1, var2);
      }
   }

   protected interface d extends Cloneable {
      double a();

      @Override
      Object clone();

      double b(int var1);

      double a(double var1);

      double a(int var1);
   }
}
