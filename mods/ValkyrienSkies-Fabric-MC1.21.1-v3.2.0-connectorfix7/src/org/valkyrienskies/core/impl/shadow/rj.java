package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class rj extends rK<rc, rv> {
   private static final double b = 1.0E-10;

   public rj(double var1) {
      super(var1);
   }

   private rj(rM<rc> var1, double var2) {
      super(var1, var2);
   }

   private rj(Collection<sb<rc>> var1, double var2) {
      super(var1, 1.0E-10);
   }

   private rj(List<rs> var1, List<int[]> var2, double var3) {
      super(a(var1, var2, var3), var3);
   }

   private rj(double var1, double var3, double var5, double var7, double var9, double var11, double var13) {
      double var27 = 1.0E-10;
      double var25 = var11;
      double var23 = var9;
      double var21 = var7;
      double var19 = var5;
      double var17 = var3;
      double var15 = var1;
      rM var10001;
      if (!(var1 >= var17 - var27) && !(var19 >= var21 - var27) && !(var23 >= var25 - var27)) {
         ri var29 = new ri(new rs(var15, 0.0, 0.0), rs.c, var27);
         ri var2 = new ri(new rs(var17, 0.0, 0.0), rs.b, var27);
         ri var30 = new ri(new rs(0.0, var19, 0.0), rs.e, var27);
         ri var4 = new ri(new rs(0.0, var21, 0.0), rs.d, var27);
         ri var31 = new ri(new rs(0.0, 0.0, var23), rs.g, var27);
         ri var6 = new ri(new rs(0.0, 0.0, var25), rs.f, var27);
         var10001 = new rZ().a(var29, var2, var30, var4, var31, var6).a(false);
      } else {
         var10001 = new rM(Boolean.FALSE);
      }

      super(var10001, 1.0E-10);
   }

   @Deprecated
   public rj() {
      this(1.0E-10);
   }

   @Deprecated
   private rj(rM<rc> var1) {
      this(var1, 1.0E-10);
   }

   @Deprecated
   private rj(Collection<sb<rc>> var1) {
      this(var1, 1.0E-10);
   }

   @Deprecated
   private rj(double var1, double var3, double var5, double var7, double var9, double var11) {
      this(var1, var3, var5, var7, var9, var11, 1.0E-10);
   }

   private static rM<rc> a(double var0, double var2, double var4, double var6, double var8, double var10, double var12) {
      if (!(var0 >= var2 - var12) && !(var4 >= var6 - var12) && !(var8 >= var10 - var12)) {
         ri var14 = new ri(new rs(var0, 0.0, 0.0), rs.c, var12);
         ri var1 = new ri(new rs(var2, 0.0, 0.0), rs.b, var12);
         ri var15 = new ri(new rs(0.0, var4, 0.0), rs.e, var12);
         ri var3 = new ri(new rs(0.0, var6, 0.0), rs.d, var12);
         ri var16 = new ri(new rs(0.0, 0.0, var8), rs.g, var12);
         ri var5 = new ri(new rs(0.0, 0.0, var10), rs.f, var12);
         return new rZ<rc>().a(var14, var1, var15, var3, var16, var5).a(false);
      } else {
         return new rM<>(Boolean.FALSE);
      }
   }

   private static List<sb<rc>> a(List<rs> var0, List<int[]> var1, double var2) {
      for (int var4 = 0; var4 < var0.size() - 1; var4++) {
         rs var5 = (rs)var0.get(var4);

         for (int var6 = var4 + 1; var6 < var0.size(); var6++) {
            if (rs.d(var5, (rs)var0.get(var6)) <= var2) {
               throw new oV(px.CLOSE_VERTICES, var5.i, var5.j, var5.k);
            }
         }
      }

      List var12 = var1;
      List var22 = var0;
      int[] var13 = new int[var0.size()];
      int var14 = 0;
      Iterator var15 = var12.iterator();

      while (var15.hasNext()) {
         int[] var29;
         if ((var29 = (int[])var15.next()).length < 3) {
            throw new pn(px.WRONG_NUMBER_OF_POINTS, 3, var29.length, true);
         }

         int[] var7 = var29;
         int var8 = var29.length;

         for (int var9 = 0; var9 < var8; var9++) {
            int var10 = var7[var9];
            var14 = BY.b(var14, ++var13[var10]);
         }
      }

      int[][] var16;
      int[][] var30;
      int var36 = (var30 = var16 = new int[var22.size()][var14]).length;

      for (int var42 = 0; var42 < var36; var42++) {
         int[] var48;
         Arrays.fill(var48 = var30[var42], -1);
      }

      for (int var31 = 0; var31 < var12.size(); var31++) {
         int[] var37;
         int var43 = (var37 = (int[])var12.get(var31)).length;

         for (int var49 = 0; var49 < var43; var49++) {
            int var53 = var37[var49];
            int var23 = 0;

            while (var23 < var14 && var16[var53][var23] >= 0) {
               var23++;
            }

            var16[var53][var23] = var31;
         }
      }

      int[][] var17 = var16;
      int[][] var63 = var17;
      var12 = var1;
      var22 = var0;
      int[][] var18;
      int[][] var65;
      int var32 = (var18 = var65 = new int[var0.size()][var63[0].length]).length;

      for (int var38 = 0; var38 < var32; var38++) {
         int[] var44;
         Arrays.fill(var44 = var18[var38], -1);
      }

      for (int var19 = 0; var19 < var22.size(); var19++) {
         for (int var33 = 0; var33 < var65[var19].length && var63[var19][var33] >= 0; var33++) {
            int[] var39 = (int[])var12.get(var63[var19][var33]);
            int var45 = 0;

            while (var45 < var39.length && var39[var45] != var19) {
               var45++;
            }

            var65[var19][var33] = var39[(var45 + 1) % var39.length];

            for (int var50 = 0; var50 < var33; var50++) {
               if (var65[var19][var50] == var65[var19][var33]) {
                  rs var54 = (rs)var22.get(var19);
                  rs var25 = (rs)var22.get(var65[var19][var33]);
                  throw new oV(px.FACET_ORIENTATION_MISMATCH, var54.i, var54.j, var54.k, var25.i, var25.j, var25.k);
               }
            }
         }
      }

      int[][] var26 = var65;

      for (int var34 = 0; var34 < var0.size(); var34++) {
         int[] var20;
         var36 = (var20 = var26[var34]).length;

         for (int var46 = 0; var46 < var36; var46++) {
            int var51;
            if ((var51 = var20[var46]) >= 0) {
               boolean var55 = false;
               int[] var11;
               int var60 = (var11 = var26[var51]).length;

               for (int var64 = 0; var64 < var60; var64++) {
                  var14 = var11[var64];
                  var55 = var55 || var14 == var34;
               }

               if (!var55) {
                  rs var57 = (rs)var0.get(var34);
                  rs var61 = (rs)var0.get(var51);
                  throw new oV(px.EDGE_CONNECTED_TO_ONE_FACET, var57.i, var57.j, var57.k, var61.i, var61.j, var61.k);
               }
            }
         }
      }

      ArrayList var35 = new ArrayList();

      for (int[] var41 : var1) {
         ri var47 = new ri((rs)var0.get(var41[0]), (rs)var0.get(var41[1]), (rs)var0.get(var41[2]), var2);
         rB[] var52 = new rB[var41.length];

         for (int var56 = 0; var56 < var41.length; var56++) {
            rs var58 = (rs)var0.get(var41[var56]);
            rs var62 = var58;
            ri var27 = var47;
            rs var67 = var62;
            if (!(BY.w(var47.a((qM<rc>)var67)) < var27.f)) {
               throw new oV(px.OUT_OF_PLANE, var58.i, var58.j, var58.k);
            }

            rs var28 = var58;
            var52[var56] = var47.e(var28);
         }

         var35.add(new rr(var47, new ry(var2, var52)));
      }

      return var35;
   }

   private static int[][] a(List<rs> var0, List<int[]> var1) {
      int[] var2 = new int[var0.size()];
      int var3 = 0;
      Iterator var4 = var1.iterator();

      while (var4.hasNext()) {
         int[] var5;
         if ((var5 = (int[])var4.next()).length < 3) {
            throw new pn(px.WRONG_NUMBER_OF_POINTS, 3, var5.length, true);
         }

         int[] var6 = var5;
         int var7 = var5.length;

         for (int var8 = 0; var8 < var7; var8++) {
            int var9 = var6[var8];
            var3 = BY.b(var3, ++var2[var9]);
         }
      }

      int[][] var11;
      int[][] var12;
      int var14 = (var12 = var11 = new int[var0.size()][var3]).length;

      for (int var16 = 0; var16 < var14; var16++) {
         int[] var18;
         Arrays.fill(var18 = var12[var16], -1);
      }

      for (int var13 = 0; var13 < var1.size(); var13++) {
         int[] var15;
         int var17 = (var15 = (int[])var1.get(var13)).length;

         for (int var19 = 0; var19 < var17; var19++) {
            int var20 = var15[var19];
            int var10 = 0;

            while (var10 < var3 && var11[var20][var10] >= 0) {
               var10++;
            }

            var11[var20][var10] = var13;
         }
      }

      return var11;
   }

   private static int[][] a(List<rs> var0, List<int[]> var1, int[][] var2) {
      int[][] var3;
      int[][] var4;
      int var5 = (var4 = var3 = new int[var0.size()][var2[0].length]).length;

      for (int var6 = 0; var6 < var5; var6++) {
         int[] var7;
         Arrays.fill(var7 = var4[var6], -1);
      }

      for (int var10 = 0; var10 < var0.size(); var10++) {
         for (int var11 = 0; var11 < var3[var10].length && var2[var10][var11] >= 0; var11++) {
            int[] var12 = (int[])var1.get(var2[var10][var11]);
            int var14 = 0;

            while (var14 < var12.length && var12[var14] != var10) {
               var14++;
            }

            var3[var10][var11] = var12[(var14 + 1) % var12.length];

            for (int var13 = 0; var13 < var11; var13++) {
               if (var3[var10][var13] == var3[var10][var11]) {
                  rs var9 = (rs)var0.get(var10);
                  rs var8 = (rs)var0.get(var3[var10][var11]);
                  throw new oV(px.FACET_ORIENTATION_MISMATCH, var9.i, var9.j, var9.k, var8.i, var8.j, var8.k);
               }
            }
         }
      }

      return var3;
   }

   private rj e(rM<rc> var1) {
      return new rj(var1, this.c());
   }

   @Override
   public final void a() {
      this.a(true).a(new rj.a());
      if (this.g() < 0.0) {
         this.a(Double.POSITIVE_INFINITY);
         this.c(rs.h);
      } else {
         this.a(this.g() / 3.0);
         this.c(new rs(1.0 / (4.0 * this.g()), (rs)this.h()));
      }
   }

   private sb<rc> a(rs var1, rf var2) {
      return this.a(this.a(true), var1, var2);
   }

   private sb<rc> a(rM<rc> var1, rs var2, rf var3) {
      while (true) {
         sb var4 = var1.a;
         if (var1.a == null) {
            return null;
         }

         rM var5 = var1.c;
         rM var6 = var1.b;
         double var8;
         ri var11;
         boolean var7 = BY.w(var8 = (var11 = (ri)var4.c()).a((qM<rc>)var2)) < this.c();
         rM var16;
         if (var8 < 0.0) {
            var16 = var5;
            var5 = var6;
         } else {
            var16 = var6;
            var5 = var5;
         }

         sb var14;
         if (var7 && (var14 = a(var2, var1)) != null) {
            return var14;
         }

         sb var15;
         if ((var15 = this.a(var16, var2, var3)) != null) {
            return var15;
         }

         sb var10;
         rs var12;
         if (!var7 && (var12 = var11.a(var3)) != null && var3.a(var12) > var3.a(var2) && (var10 = a(var12, var1)) != null) {
            return var10;
         }

         var2 = var2;
         var1 = var5;
         this = this;
      }
   }

   private static sb<rc> a(rs var0, rM<rc> var1) {
      rB var3 = ((ri)var1.a.c()).e(var0);
      rr var2;
      rO var4;
      if ((var4 = (rO)var1.e).a != null && (var2 = (rr)var4.a).b.b(var3) == rY.a.INSIDE$3ad793e5) {
         return var4.a;
      } else {
         return var4.b != null && (var2 = (rr)var4.b).b.b(var3) == rY.a.INSIDE$3ad793e5 ? var4.b : null;
      }
   }

   private rj a(rs var1, rk var2) {
      return (rj)this.a(new rj.b(var1, var2));
   }

   private rj a(rs var1) {
      return (rj)this.a(new rj.c(var1));
   }

   // $VF: synthetic method
   @Override
   public final rK a(rM var1) {
      return this.e(var1);
   }

   // $VF: synthetic method
   @Override
   public final rK b(rM var1) {
      return this.e(var1);
   }

   // $VF: synthetic method
   static void a(rj var0, double var1) {
      var0.a(0.0);
   }

   // $VF: synthetic method
   static void b(rj var0, double var1) {
      var0.a(Double.POSITIVE_INFINITY);
   }

   class a implements rN<rc> {
      a() {
         rj.a(rj.this, 0.0);
         rj.this.c(new rs(0.0, 0.0, 0.0));
      }

      @Override
      public final int a(rM<rc> var1) {
         return rN.a.MINUS_SUB_PLUS$672d2271;
      }

      @Override
      public final void b(rM<rc> var1) {
         rO var3;
         if ((var3 = (rO)var1.e).a != null) {
            this.a(var3.a, false);
         }

         if (var3.b != null) {
            this.a(var3.b, true);
         }
      }

      @Override
      public final void c(rM<rc> var1) {
      }

      private void a(sb<rc> var1, boolean var2) {
         rY var3;
         rr var4;
         double var12;
         if (Double.isInfinite(var12 = (var3 = (var4 = (rr)var1).b).g())) {
            rj.b(rj.this, Double.POSITIVE_INFINITY);
            rj.this.c(rs.h);
         } else {
            ri var10;
            rs var11 = (var10 = (ri)var1.c()).f(var3.h());
            double var8 = var12 * var11.f(var10.e);
            if (var2) {
               var8 = -var8;
            }

            rj.this.a(rj.this.g() + var8);
            rj.this.c(new rs(1.0, (rs)rj.this.h(), var8, var11));
         }
      }
   }

   static class b implements sc<rc, rv> {
      private rs a;
      private rk b;
      private ri c;
      private sc<rv, qU> d;

      b(rs var1, rk var2) {
         this.a = var1;
         this.b = var2;
      }

      private rs b(qM<rc> var1) {
         var1 = var1.h(this.a);
         return new rs(1.0, this.a, 1.0, this.b.a(var1));
      }

      private ri b(rV<rc> var1) {
         ri var10000 = var1;
         rk var3 = this.b;
         rs var2 = this.a;
         var1 = var10000;
         rs var4 = var10000.b.h(var2);
         ri var6;
         (var6 = new ri(var2.g(var3.a(var4)), var3.a(var1.e), var1.f)).c = var3.a(var1.c);
         var6.d = var3.a(var1.d);
         return var6;
      }

      @Override
      public final sb<rv> a(sb<rv> var1, rV<rc> var2, rV<rc> var3) {
         if (var2 != this.c) {
            ri var4 = (ri)var2;
            var3 = var3;
            rs var5 = var4.b;
            rs var6 = var4.f(new rB(1.0, 0.0));
            rs var10 = var4.f(new rB(0.0, 1.0));
            rB var11 = var3.e(this.b(var5));
            rB var12 = var3.e(this.b(var6));
            rB var9 = var3.e(this.b(var10));
            this.c = (ri)var2;
            this.d = rw.a(var12.c - var11.c, var12.d - var11.d, var9.c - var11.c, var9.d - var11.d, var11.c, var11.d);
         }

         return ((rA)var1).a(this.d);
      }

      // $VF: synthetic method
      @Override
      public final rV a(rV var1) {
         ri var2 = var1;
         rj.b var5 = this;
         ri var10000 = var2;
         rk var3 = var5.b;
         rs var7 = var5.a;
         var1 = var10000;
         rs var4 = var10000.b.h(var7);
         (var2 = new ri(var7.g(var3.a(var4)), var3.a(var1.e), var1.f)).c = var3.a(var1.c);
         var2.d = var3.a(var1.d);
         return var2;
      }

      // $VF: synthetic method
      @Override
      public final qM a(qM var1) {
         return this.b(var1);
      }
   }

   static class c implements sc<rc, rv> {
      private rs a;
      private ri b;
      private sc<rv, qU> c;

      c(rs var1) {
         this.a = var1;
      }

      private rs b(qM<rc> var1) {
         return new rs(1.0, (rs)var1, 1.0, this.a);
      }

      private ri b(rV<rc> var1) {
         ri var10000 = var1;
         rs var2 = this.a;
         var1 = var10000;
         ri var4;
         (var4 = new ri(var1.b.g(var2), var1.e, var1.f)).c = var1.c;
         var4.d = var1.d;
         return var4;
      }

      @Override
      public final sb<rv> a(sb<rv> var1, rV<rc> var2, rV<rc> var3) {
         if (var2 != this.b) {
            ri var4 = (ri)var2;
            ri var5;
            rB var6 = (var5 = (ri)var3).e(this.b(var4.b));
            this.b = (ri)var2;
            this.c = rw.a(1.0, 0.0, 0.0, 1.0, var6.c, var6.d);
         }

         return ((rA)var1).a(this.c);
      }

      // $VF: synthetic method
      @Override
      public final rV a(rV var1) {
         ri var2 = var1;
         rj.c var3 = this;
         ri var10000 = var2;
         rs var5 = var3.a;
         var1 = var10000;
         (var2 = new ri(var1.b.g(var5), var1.e, var1.f)).c = var1.c;
         var2.d = var1.d;
         return var2;
      }

      // $VF: synthetic method
      @Override
      public final qM a(qM var1) {
         return this.b(var1);
      }
   }
}
