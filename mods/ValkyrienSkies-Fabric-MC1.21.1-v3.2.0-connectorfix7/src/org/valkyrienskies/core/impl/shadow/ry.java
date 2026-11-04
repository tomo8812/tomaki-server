package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class ry extends rK<rv, qU> {
   private static final double b = 1.0E-10;
   private rB[][] c;

   public ry(double var1) {
      super(var1);
   }

   public ry(rM<rv> var1, double var2) {
      super(var1, var2);
   }

   public ry(Collection<sb<rv>> var1, double var2) {
      super(var1, var2);
   }

   private ry(double var1, double var3, double var5, double var7, double var9) {
      double var19 = 1.0E-10;
      double var17 = var7;
      double var15 = var5;
      double var13 = var3;
      double var11 = var1;
      rw[] var10001;
      if (!(var1 >= var13 - var19) && !(var15 >= var17 - var19)) {
         rB var21 = new rB(var11, var15);
         rB var2 = new rB(var11, var17);
         rB var22 = new rB(var13, var15);
         rB var4 = new rB(var13, var17);
         var10001 = new rw[]{new rw(var21, var22, var19), new rw(var22, var4, var19), new rw(var4, var2, var19), new rw(var2, var21, var19)};
      } else {
         var10001 = null;
      }

      super(var10001, 1.0E-10);
   }

   public ry(double var1, rB... var3) {
      super(a(var1, var3), var1);
   }

   @Deprecated
   public ry() {
      this(1.0E-10);
   }

   @Deprecated
   private ry(rM<rv> var1) {
      this(var1, 1.0E-10);
   }

   @Deprecated
   private ry(Collection<sb<rv>> var1) {
      this(var1, 1.0E-10);
   }

   @Deprecated
   private ry(double var1, double var3, double var5, double var7) {
      this(var1, var3, var5, var7, 1.0E-10);
   }

   private static rw[] a(double var0, double var2, double var4, double var6, double var8) {
      if (!(var0 >= var2 - var8) && !(var4 >= var6 - var8)) {
         rB var10 = new rB(var0, var4);
         rB var11 = new rB(var0, var6);
         rB var1 = new rB(var2, var4);
         rB var12 = new rB(var2, var6);
         return new rw[]{new rw(var10, var1, var8), new rw(var1, var12, var8), new rw(var12, var11, var8), new rw(var11, var10, var8)};
      } else {
         return null;
      }
   }

   private static rM<rv> a(double var0, rB... var2) {
      int var3;
      if ((var3 = var2.length) == 0) {
         return new rM<>(Boolean.TRUE);
      }

      ry.d[] var4 = new ry.d[var3];

      for (int var5 = 0; var5 < var3; var5++) {
         var4[var5] = new ry.d(var2[var5]);
      }

      ArrayList var16 = new ArrayList(var3);

      for (int var14 = 0; var14 < var3; var14++) {
         ry.d var6 = var4[var14];
         ry.d var7 = var4[(var14 + 1) % var3];
         ry.d var9 = var7;
         Iterator var8 = var6.d.iterator();

         rw var10000;
         label50:
         while (true) {
            if (!var8.hasNext()) {
               var10000 = null;
               break;
            }

            rw var10 = (rw)var8.next();

            for (rw var12 : var9.d) {
               if (var10 == var12) {
                  var10000 = var10;
                  break label50;
               }
            }
         }

         rw var17 = var10000;
         if (var10000 == null) {
            var17 = new rw(var6.a, var7.a, var0);
         }

         var16.add(new ry.b(var6, var7, var17));
         ry.d[] var18 = var4;
         int var19 = var4.length;

         for (int var20 = 0; var20 < var19; var20++) {
            ry.d var21;
            if ((var21 = var18[var20]) != var6 && var21 != var7 && BY.w(var17.a((qM<rv>)var21.a)) <= var0) {
               var21.a(var17);
            }
         }
      }

      rM var15 = new rM();
      a(var0, var15, var16);
      return var15;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static void a(double var0, rM<rv> var2, List<ry.b> var3) {
      int var4 = 0;
      ry.b var5 = null;

      while (var5 == null && var4 < var3.size()) {
         if ((var5 = (ry.b)var3.get(var4++)).d == null) {
            if (var2.a(var5.c)) {
               rM var7 = var2;
               var5.d = var7;
            } else {
               var5 = null;
            }
         } else {
            var5 = null;
         }
      }

      if (var5 == null) {
         rM var16 = var2.d;
         if (var2.d != null && var2 != var16.c) {
            Boolean var23 = Boolean.FALSE;
            var2.e = var23;
         } else {
            Boolean var22 = Boolean.TRUE;
            var2.e = var22;
         }
      } else {
         ArrayList var15 = new ArrayList();
         ArrayList var6 = new ArrayList();
         Iterator var14 = var3.iterator();

         while (var14.hasNext()) {
            ry.b var17;
            if ((var17 = (ry.b)var14.next()) != var5) {
               ry.d var8 = var17.a;
               double var10 = var5.c.a((qM<rv>)var17.a.a);
               var8 = var17.b;
               double var12 = var5.c.a((qM<rv>)var17.b.a);
               int var25 = BY.w(var10) <= var0 ? sa.HYPER$7c70dd6b : (var10 < 0.0 ? sa.MINUS$7c70dd6b : sa.PLUS$7c70dd6b);
               int var9 = BY.w(var12) <= var0 ? sa.HYPER$7c70dd6b : (var12 < 0.0 ? sa.MINUS$7c70dd6b : sa.PLUS$7c70dd6b);
               switch (<unrepresentable>.a[var25 - 1]) {
                  case 1:
                     if (var9 == sa.MINUS$7c70dd6b) {
                        ry.d var19 = var17.a(var5.c);
                        var6.add(var19.c);
                        var15.add(var19.b);
                     } else {
                        var15.add(var17);
                     }
                     break;
                  case 2:
                     if (var9 == sa.PLUS$7c70dd6b) {
                        ry.d var18 = var17.a(var5.c);
                        var6.add(var18.b);
                        var15.add(var18.c);
                     } else {
                        var6.add(var17);
                     }
                     break;
                  default:
                     if (var9 == sa.PLUS$7c70dd6b) {
                        var15.add(var17);
                     } else if (var9 == sa.MINUS$7c70dd6b) {
                        var6.add(var17);
                     }
               }
            }
         }

         if (!var15.isEmpty()) {
            a(var0, var2.b, var15);
         } else {
            Boolean var20 = Boolean.FALSE;
            rM var26 = var2.b;
            var2.b.e = var20;
         }

         if (!var6.isEmpty()) {
            a(var0, var2.c, var6);
         } else {
            Boolean var21 = Boolean.TRUE;
            rM var27 = var2.c;
            var2.c.e = var21;
         }
      }
   }

   private ry e(rM<rv> var1) {
      return new ry(var1, this.c());
   }

   @Override
   public final void a() {
      rB[][] var1;
      if ((var1 = this.b()).length == 0) {
         rM var2;
         if ((var2 = this.a(false)).a != null || !(Boolean)var2.e) {
            this.a(0.0);
            this.c(new rB(0.0, 0.0));
            return;
         }

         this.a(Double.POSITIVE_INFINITY);
         this.c(rB.b);
      } else {
         if (var1[0][0] == null) {
            this.a(Double.POSITIVE_INFINITY);
            this.c(rB.b);
            return;
         }

         double var27 = 0.0;
         double var4 = 0.0;
         double var6 = 0.0;
         rB[][] var26;
         int var8 = (var26 = var1).length;

         for (int var9 = 0; var9 < var8; var9++) {
            rB[] var10;
            double var12 = (var10 = var26[var9])[var10.length - 1].c;
            rB var14;
            double var29 = (var14 = var10[var10.length - 1]).d;
            rB[] var28;
            int var11 = (var28 = var10).length;

            for (int var16 = 0; var16 < var11; var16++) {
               rB var17 = var28[var16];
               double var20 = var12;
               double var22 = var29;
               var12 = var17.c;
               var29 = var17.d;
               double var24 = var20 * var29 - var22 * var12;
               var27 += var24;
               var4 += var24 * (var20 + var12);
               var6 += var24 * (var22 + var29);
            }
         }

         if (var27 < 0.0) {
            this.a(Double.POSITIVE_INFINITY);
            this.c(rB.b);
            return;
         }

         this.a(var27 / 2.0);
         this.c(new rB(var4 / (var27 * 3.0), var6 / (var27 * 3.0)));
      }
   }

   public final rB[][] b() {
      if (this.c == null) {
         if (this.a(false).a == null) {
            this.c = new rB[0][];
         } else {
            ry.c var1 = new ry.c(this.c());
            this.a(true).a(var1);
            int var2;
            List var14;
            if ((var2 = (var14 = var1.a).size() - a(var14)) > 0) {
               var2 -= b(var14);
            }

            if (var2 > 0) {
               this.c(var14);
            }

            ArrayList var18 = new ArrayList();

            for (ry.a var3 = d(var14); var3 != null; var3 = d(var14)) {
               List var4;
               if ((var4 = this.a(var3)) != null) {
                  if (((rz)var4.get(0)).g == null) {
                     var18.add(0, var4);
                  } else {
                     var18.add(var4);
                  }
               }
            }

            this.c = new rB[var18.size()][];
            int var22 = 0;
            Iterator var23 = var18.iterator();

            while (var23.hasNext()) {
               List var15;
               if ((var15 = (List)var23.next()).size() < 2 || var15.size() == 2 && ((rz)var15.get(0)).g == null && ((rz)var15.get(1)).h == null) {
                  rw var19 = ((rz)var15.get(0)).i;
                  this.c[var22++] = new rB[]{null, var19.f(new qZ(-Float.MAX_VALUE)), var19.f(new qZ(Float.MAX_VALUE))};
               } else if (((rz)var15.get(0)).g == null) {
                  rB[] var21 = new rB[var15.size() + 2];
                  int var24 = 0;

                  for (rz var26 : var15) {
                     if (var24 == 0) {
                        double var12;
                        var12 = (var12 = var26.i.e(var26.h).d) - BY.e(1.0, BY.w(var12 / 2.0));
                        var21[var24++] = null;
                        var21[var24++] = var26.i.f(new qZ(var12));
                     }

                     if (var24 < var21.length - 1) {
                        var21[var24++] = var26.h;
                     }

                     if (var24 == var21.length - 1) {
                        double var28;
                        var28 = (var28 = var26.i.e(var26.g).d) + BY.e(1.0, BY.w(var28 / 2.0));
                        var21[var24++] = var26.i.f(new qZ(var28));
                     }
                  }

                  this.c[var22++] = var21;
               } else {
                  rB[] var20 = new rB[var15.size()];
                  int var5 = 0;

                  for (rz var6 : var15) {
                     var20[var5++] = var6.g;
                  }

                  this.c[var22++] = var20;
               }
            }
         }
      }

      return (rB[][])this.c.clone();
   }

   private static int a(List<ry.a> var0) {
      int var1 = 0;
      Iterator var2 = var0.iterator();

      while (var2.hasNext()) {
         ry.a var3;
         if ((var3 = (ry.a)var2.next()).e == null) {
            rM var4 = var3.a;
            rM var5 = var3.c;
            Iterator var6 = var0.iterator();

            while (var6.hasNext()) {
               ry.a var7;
               if ((var7 = (ry.a)var6.next()).d == null && var7.a == var5 && var7.b == var4) {
                  ry.a var9 = var7;
                  var3.e = var9;
                  ry.a var10 = var3;
                  var7.d = var10;
                  var1++;
                  break;
               }
            }
         }
      }

      return var1;
   }

   private static int b(List<ry.a> var0) {
      int var1 = 0;
      Iterator var2 = var0.iterator();

      while (var2.hasNext()) {
         ry.a var3;
         if ((var3 = (ry.a)var2.next()).e == null) {
            rM var8 = var3.a;
            rV var4 = var3.a.a.c();
            rM var5 = var3.c;
            Iterator var6 = var0.iterator();

            while (var6.hasNext()) {
               ry.a var7;
               if ((var7 = (ry.a)var6.next()).d == null) {
                  var8 = var7.a;
                  if (var7.a.a.c() == var4 && var7.b == var5) {
                     ry.a var9 = var7;
                     var3.e = var9;
                     ry.a var10 = var3;
                     var7.d = var10;
                     var1++;
                     break;
                  }
               }
            }
         }
      }

      return var1;
   }

   private int c(List<ry.a> var1) {
      int var2 = 0;
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         ry.a var4;
         if ((var4 = (ry.a)var3.next()).e == null && var4.h != null) {
            rB var5 = var4.h;
            ry.a var6 = null;
            double var7 = Double.POSITIVE_INFINITY;
            Iterator var9 = var1.iterator();

            while (var9.hasNext()) {
               ry.a var10;
               double var11;
               if ((var10 = (ry.a)var9.next()).d == null && var10.g != null && (var11 = rB.c(var5, var10.g)) < var7) {
                  var6 = var10;
                  var7 = var11;
               }
            }

            if (var7 <= this.c()) {
               ry.a var13 = var6;
               var4.e = var13;
               ry.a var14 = var4;
               var6.d = var14;
               var2++;
            }
         }
      }

      return var2;
   }

   private static ry.a d(List<ry.a> var0) {
      Iterator var3 = var0.iterator();

      while (var3.hasNext()) {
         ry.a var1;
         if (!(var1 = (ry.a)var3.next()).f) {
            return var1;
         }
      }

      return null;
   }

   private List<rz> a(ry.a var1) {
      ArrayList var2;
      (var2 = new ArrayList()).add(var1);
      boolean var4 = true;
      var1.f = true;

      ry.a var3;
      for (var3 = var1.e; var3 != var1 && var3 != null; var3 = var3.e) {
         var2.add(var3);
         var4 = true;
         var3.f = true;
      }

      if (var3 == null) {
         for (ry.a var5 = var1.d; var5 != null; var5 = var5.d) {
            var2.add(0, var5);
            var4 = true;
            var5.f = true;
         }
      }

      e(var2);
      return var2.size() == 2 && ((rz)var2.get(0)).g != null ? null : var2;
   }

   private static void e(List<rz> var0) {
      for (int var1 = 0; var1 < var0.size(); var1++) {
         rz var2 = (rz)var0.get(var1);
         int var3 = (var1 + 1) % var0.size();
         rz var4;
         if ((var4 = (rz)var0.get(var3)) != null && Cq.b(var2.i.h(), var4.i.h(), Cq.a)) {
            var0.set(var3, new rz(var2.g, var4.h, var2.i));
            var0.remove(var1--);
         }
      }
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

   static class a extends rz {
      final rM<rv> a;
      final rM<rv> b;
      final rM<rv> c;
      ry.a d;
      ry.a e;
      boolean f;

      a(rB var1, rB var2, rw var3, rM<rv> var4, rM<rv> var5, rM<rv> var6) {
         super(var1, var2, var3);
         this.a = var4;
         this.b = var5;
         this.c = var6;
         this.d = null;
         this.e = null;
         this.f = false;
      }

      public final rM<rv> a() {
         return this.a;
      }

      public final rM<rv> b() {
         return this.b;
      }

      public final rM<rv> c() {
         return this.c;
      }

      public final ry.a d() {
         return this.d;
      }

      public final void a(ry.a var1) {
         this.d = var1;
      }

      public final ry.a e() {
         return this.e;
      }

      public final void b(ry.a var1) {
         this.e = var1;
      }

      public final void a(boolean var1) {
         this.f = true;
      }

      public final boolean f() {
         return this.f;
      }
   }

   static class b {
      final ry.d a;
      final ry.d b;
      final rw c;
      rM<rv> d;

      b(ry.d var1, ry.d var2, rw var3) {
         this.a = var1;
         this.b = var2;
         this.c = var3;
         this.d = null;
         ry.b var6 = this;
         ry.d var4 = var1;
         var1.c = var6;
         var4.a(var6.c);
         ry.b var7 = this;
         var1 = var2;
         var2.b = var7;
         var1.a(var7.c);
      }

      public final ry.d a() {
         return this.a;
      }

      public final ry.d b() {
         return this.b;
      }

      public final rw c() {
         return this.c;
      }

      public final void a(rM<rv> var1) {
         this.d = var1;
      }

      public final rM<rv> d() {
         return this.d;
      }

      public final ry.d a(rw var1) {
         ry.d var2;
         (var2 = new ry.d(this.c.a(var1))).a(var1);
         ry.b var4 = new ry.b(this.a, var2, this.c);
         ry.b var3 = new ry.b(var2, this.b, this.c);
         var4.d = this.d;
         var3.d = this.d;
         return var2;
      }
   }

   static class c implements rN<rv> {
      private final double b;
      final List<ry.a> a;

      c(double var1) {
         this.b = var1;
         this.a = new ArrayList<>();
      }

      @Override
      public final int a(rM<rv> var1) {
         return rN.a.MINUS_SUB_PLUS$672d2271;
      }

      @Override
      public final void b(rM<rv> var1) {
         rO var2;
         rX var3 = (var2 = (rO)var1.e).c;
         if (var2.a != null) {
            this.a(var2.a, var1, var3, false);
         }

         if (var2.b != null) {
            this.a(var2.b, var1, var3, true);
         }
      }

      @Override
      public final void c(rM<rv> var1) {
      }

      private void a(sb<rv> var1, rM<rv> var2, Iterable<rM<rv>> var3, boolean var4) {
         rL var5 = (rL)var1;
         rw var10 = (rw)var1.c();
         Iterator var11 = ((qW)var5.b).b().iterator();

         while (var11.hasNext()) {
            qV var6;
            rB var7 = Double.isInfinite((var6 = (qV)var11.next()).a) ? null : var10.f(new qZ(var6.a));
            rB var12 = Double.isInfinite(var6.b) ? null : var10.f(new qZ(var6.b));
            rM var8 = this.a(var7, var3);
            rM var9 = this.a(var12, var3);
            if (var4) {
               this.a.add(new ry.a(var12, var7, var10.c(), var2, var9, var8));
            } else {
               this.a.add(new ry.a(var7, var12, var10, var2, var8, var9));
            }
         }
      }

      private rM<rv> a(rB var1, Iterable<rM<rv>> var2) {
         rM var3 = null;
         double var4 = Double.POSITIVE_INFINITY;
         Iterator var10 = var2.iterator();

         while (var10.hasNext()) {
            rM var6;
            double var8;
            if ((var8 = BY.w((var6 = (rM)var10.next()).a.c().a(var1))) < var4) {
               var3 = var6;
               var4 = var8;
            }
         }

         return var4 <= this.b ? var3 : null;
      }

      public final List<ry.a> a() {
         return this.a;
      }
   }

   static class d {
      final rB a;
      ry.b b;
      ry.b c;
      final List<rw> d;

      d(rB var1) {
         this.a = var1;
         this.b = null;
         this.c = null;
         this.d = new ArrayList<>();
      }

      public final rB a() {
         return this.a;
      }

      public final void a(rw var1) {
         this.d.add(var1);
      }

      public final rw a(ry.d var1) {
         for (rw var3 : this.d) {
            for (rw var5 : var1.d) {
               if (var3 == var5) {
                  return var3;
               }
            }
         }

         return null;
      }

      public final void a(ry.b var1) {
         this.b = var1;
         this.a(var1.c);
      }

      public final ry.b b() {
         return this.b;
      }

      public final void b(ry.b var1) {
         this.c = var1;
         this.a(var1.c);
      }

      public final ry.b c() {
         return this.c;
      }
   }
}
