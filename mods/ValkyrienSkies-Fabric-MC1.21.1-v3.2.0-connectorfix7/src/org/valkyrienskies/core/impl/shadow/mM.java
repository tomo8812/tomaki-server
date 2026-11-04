package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class mM {
   final List<mM.a> a;
   final List<mM.b> b;
   final int c;
   final int d;
   final double e;
   final double f;
   final double g;

   protected mM(int var1, int var2, double var3, double var5, double var7) {
      if (var1 <= 0) {
         throw new pk(var1);
      }

      if (var2 <= 0) {
         throw new pk(var2);
      }

      if (var3 < 0.0 || var3 > 1.0) {
         throw new po(var3, 0, 1);
      }

      if (var5 < 0.0) {
         throw new pj(var5);
      }

      this.c = var1;
      this.d = var2;
      this.e = var3;
      this.f = var5;
      this.g = var7;
      this.a = new ArrayList<>(var2);
      this.b = new ArrayList<>(var2);
   }

   public mM(int var1, int var2, double var3, double var5, double var7, zN var9) {
      this(var1, var2, var3, var5, var7);

      for (int var10 = 0; var10 < var2; var10++) {
         this.a(var9.a(), false);
      }
   }

   protected mM(mM var1) {
      this.c = var1.c;
      this.d = var1.d;
      this.e = var1.e;
      this.f = var1.f;
      this.g = var1.g;
      this.a = var1.a;
      this.b = new ArrayList<>(this.d);

      for (mM.b var2 : var1.b) {
         this.b.add(new mM.b(var2.a, var2.b));
      }
   }

   public mM a() {
      return new mM(this);
   }

   public final int b() {
      return this.c;
   }

   private int c() {
      return this.d;
   }

   public final double a(double[] var1, double[][] var2, double[] var3, double var4, double var6) {
      if (var4 < 0.0) {
         throw new pj(var4);
      }

      mM var10 = this;

      for (int var11 = 0; var11 < var10.d; var11++) {
         var10.b.set(var11, new mM.b(0.0, 0.0));
      }

      int var8 = var2.length;

      for (int var9 = 0; var9 < var8; var9++) {
         double[] var27;
         double var30;
         if (BY.w(var30 = Ch.d(var27 = Ch.a(var2[var9], var1))) < var6) {
            return var3[var9];
         }

         double var13 = BY.a(var30, -var4);
         double var10002 = var3[var9];
         double var19 = var13;
         double var17 = var10002;
         double[] var31 = var27;
         var10 = this;

         for (int var21 = 0; var21 < var10.d; var21++) {
            double[] var10000 = var10.a.get(var21).a;
            double[] var33 = var31;
            double[] var12 = var10000;
            double var23;
            double var25;
            if ((var23 = Ch.h(var10000, var33) / (Ch.d(var12) * Ch.d(var33))) > 0.0 && (var25 = var23 * var19) > var10.f && var25 > var10.b.get(var21).a) {
               var10.b.set(var21, new mM.b(var25, var17));
            }
         }
      }

      var10 = this;
      int var32 = 0;
      double var34 = 0.0;
      double var35 = 0.0;
      Iterator var36 = var10.b.iterator();

      while (var36.hasNext()) {
         mM.b var22;
         double var37;
         if ((var37 = (var22 = (mM.b)var36.next()).a) != 0.0) {
            var34 += var37 * var22.b;
            var35 += var37;
         } else {
            var32++;
         }
      }

      return (double)var32 / var10.d <= var10.e ? var34 / var35 : var10.g;
   }

   protected final void a(double[] var1, boolean var2) {
      if (this.a.size() >= this.d) {
         throw new pc(this.d);
      }

      if (var1.length > this.c) {
         throw new oS(var1.length, this.c);
      }

      this.a.add(new mM.a(var1));
      this.b.add(new mM.b(0.0, 0.0));
   }

   private double d() {
      int var1 = 0;
      double var2 = 0.0;
      double var4 = 0.0;
      Iterator var6 = this.b.iterator();

      while (var6.hasNext()) {
         mM.b var7;
         double var8;
         if ((var8 = (var7 = (mM.b)var6.next()).a) != 0.0) {
            var2 += var8 * var7.b;
            var4 += var8;
         } else {
            var1++;
         }
      }

      return (double)var1 / this.d <= this.e ? var2 / var4 : this.g;
   }

   private void a(double[] var1, double var2, double var4) {
      for (int var6 = 0; var6 < this.d; var6++) {
         double var8;
         double var10;
         if ((var8 = Ch.e(this.a.get(var6).a, var1)) > 0.0 && (var10 = var8 * var4) > this.f && var10 > this.b.get(var6).a) {
            this.b.set(var6, new mM.b(var10, var2));
         }
      }
   }

   private void e() {
      for (int var1 = 0; var1 < this.d; var1++) {
         this.b.set(var1, new mM.b(0.0, 0.0));
      }
   }

   static class a {
      final double[] a;

      a(double[] var1) {
         this.a = var1;
      }

      public final double[] a() {
         return this.a;
      }
   }

   static class b {
      final double a;
      final double b;

      b(double var1, double var3) {
         this.a = var1;
         this.b = var3;
      }

      public final double a() {
         return this.a;
      }

      public final double b() {
         return this.b;
      }
   }
}
