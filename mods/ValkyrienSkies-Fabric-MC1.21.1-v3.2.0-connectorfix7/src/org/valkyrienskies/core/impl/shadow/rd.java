package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class rd<T extends kO<T>> implements Serializable {
   private static final long a = 20130224L;
   private final T b;
   private final T c;
   private final T d;
   private final T e;

   private rd(T var1, T var2, T var3, T var4, boolean var5) {
      this.b = (T)var1;
      this.c = (T)var2;
      this.d = (T)var3;
      this.e = (T)var4;
   }

   @Deprecated
   private rd(re<T> var1, T var2) {
      this(var1, (T)var2, rl.VECTOR_OPERATOR$59e4743c);
   }

   private rd(re<T> var1, T var2, int var3) {
      kO var4;
      if ((var4 = var1.d()).d() == 0.0) {
         throw new oV(px.ZERO_NORM_FOR_ROTATION_AXIS);
      }

      kO var5;
      kO var6 = (var5 = var2.c(var3 == rl.VECTOR_OPERATOR$59e4743c ? -0.5 : 0.5)).r().d(var4);
      this.b = (T)var5.q();
      this.c = (T)var6.c(var1.a);
      this.d = (T)var6.c(var1.b);
      this.e = (T)var6.c(var1.c);
   }

   private rd(T[][] var1, double var2) {
      if (var1.length == 3 && var1[0].length == 3 && var1[1].length == 3 && var1[2].length == 3) {
         double var11 = var2;
         kO[][] var55 = var1;
         kO var58 = var1[0][0];
         kO var3 = var55[0][1];
         kO var4 = var55[0][2];
         kO var5 = var55[1][0];
         kO var6 = var55[1][1];
         kO var7 = var55[1][2];
         kO var8 = var55[2][0];
         kO var9 = var55[2][1];
         kO var10 = var55[2][2];
         double var22 = 0.0;
         kO[][] var13 = Ch.a(var55[0][0].c(), 3, 3);
         int var14 = 0;

         while (++var14 < 11) {
            kO var15 = var55[0][0].c(var58).a(var55[1][0].c(var5)).a(var55[2][0].c(var8));
            kO var16 = var55[0][1].c(var58).a(var55[1][1].c(var5)).a(var55[2][1].c(var8));
            kO var17 = var55[0][2].c(var58).a(var55[1][2].c(var5)).a(var55[2][2].c(var8));
            kO var18 = var55[0][0].c(var3).a(var55[1][0].c(var6)).a(var55[2][0].c(var9));
            kO var19 = var55[0][1].c(var3).a(var55[1][1].c(var6)).a(var55[2][1].c(var9));
            kO var20 = var55[0][2].c(var3).a(var55[1][2].c(var6)).a(var55[2][2].c(var9));
            kO var21 = var55[0][0].c(var4).a(var55[1][0].c(var7)).a(var55[2][0].c(var10));
            kO var24 = var55[0][1].c(var4).a(var55[1][1].c(var7)).a(var55[2][1].c(var10));
            kO var25 = var55[0][2].c(var4).a(var55[1][2].c(var7)).a(var55[2][2].c(var10));
            var13[0][0] = var58.b(var58.c(var15).a(var3.c(var16)).a(var4.c(var17)).b(var55[0][0]).c(0.5));
            var13[0][1] = var3.b(var58.c(var18).a(var3.c(var19)).a(var4.c(var20)).b(var55[0][1]).c(0.5));
            var13[0][2] = var4.b(var58.c(var21).a(var3.c(var24)).a(var4.c(var25)).b(var55[0][2]).c(0.5));
            var13[1][0] = var5.b(var5.c(var15).a(var6.c(var16)).a(var7.c(var17)).b(var55[1][0]).c(0.5));
            var13[1][1] = var6.b(var5.c(var18).a(var6.c(var19)).a(var7.c(var20)).b(var55[1][1]).c(0.5));
            var13[1][2] = var7.b(var5.c(var21).a(var6.c(var24)).a(var7.c(var25)).b(var55[1][2]).c(0.5));
            var13[2][0] = var8.b(var8.c(var15).a(var9.c(var16)).a(var10.c(var17)).b(var55[2][0]).c(0.5));
            var13[2][1] = var9.b(var8.c(var18).a(var9.c(var19)).a(var10.c(var20)).b(var55[2][1]).c(0.5));
            var13[2][2] = var10.b(var8.c(var21).a(var9.c(var24)).a(var10.c(var25)).b(var55[2][2]).c(0.5));
            double var37 = var13[0][0].d() - var55[0][0].d();
            double var39 = var13[0][1].d() - var55[0][1].d();
            double var41 = var13[0][2].d() - var55[0][2].d();
            double var43 = var13[1][0].d() - var55[1][0].d();
            double var45 = var13[1][1].d() - var55[1][1].d();
            double var47 = var13[1][2].d() - var55[1][2].d();
            double var49 = var13[2][0].d() - var55[2][0].d();
            double var51 = var13[2][1].d() - var55[2][1].d();
            double var53 = var13[2][2].d() - var55[2][2].d();
            double var63;
            if (BY.w(
                  (
                        var63 = var37 * var37
                           + var39 * var39
                           + var41 * var41
                           + var43 * var43
                           + var45 * var45
                           + var47 * var47
                           + var49 * var49
                           + var51 * var51
                           + var53 * var53
                     )
                     - var22
               )
               <= var11) {
               var1 = var13;
               kO var59 = var13[1][1].c(var1[2][2]).b(var1[2][1].c(var1[1][2]));
               var3 = var1[0][1].c(var1[2][2]).b(var1[2][1].c(var1[0][2]));
               var4 = var1[0][1].c(var1[1][2]).b(var1[1][1].c(var1[0][2]));
               kO var60;
               if ((var60 = var1[0][0].c(var59).b(var1[1][0].c(var3)).a(var1[2][0].c(var4))).d() < 0.0) {
                  throw new rg(px.CLOSEST_ORTHOGONAL_MATRIX_HAS_NEGATIVE_DETERMINANT, var60);
               }

               kO[] var57 = a((T[][])var1);
               this.b = (T)var57[0];
               this.c = (T)var57[1];
               this.d = (T)var57[2];
               this.e = (T)var57[3];
               return;
            }

            var58 = var13[0][0];
            var3 = var13[0][1];
            var4 = var13[0][2];
            var5 = var13[1][0];
            var6 = var13[1][1];
            var7 = var13[1][2];
            var8 = var13[2][0];
            var9 = var13[2][1];
            var10 = var13[2][2];
            var22 = var63;
         }

         throw new rg(px.UNABLE_TO_ORTHOGONOLIZE_MATRIX, var14 - 1);
      } else {
         throw new rg(px.ROTATION_MATRIX_DIMENSIONS, var1.length, var1[0].length);
      }
   }

   private rd(re<T> var1, re<T> var2, re<T> var3, re<T> var4) {
      re var7 = var2;
      re var5;
      re var10000 = var5 = var1.b(var7).e();
      var7 = var1;
      var2 = var10000.b(var7).e();
      var1 = var1.e();
      var7 = var4;
      re var6;
      var10000 = var6 = var3.b(var7).e();
      var7 = var3;
      var4 = var10000.b(var7).e();
      var3 = var3.e();
      kO[][] var17;
      (var17 = Ch.a(var1.a.c(), 3, 3))[0][0] = var1.a.c(var3.a).a(var2.a.c(var4.a)).a(var5.a.c(var6.a));
      var17[0][1] = var1.b.c(var3.a).a(var2.b.c(var4.a)).a(var5.b.c(var6.a));
      var17[0][2] = var1.c.c(var3.a).a(var2.c.c(var4.a)).a(var5.c.c(var6.a));
      var17[1][0] = var1.a.c(var3.b).a(var2.a.c(var4.b)).a(var5.a.c(var6.b));
      var17[1][1] = var1.b.c(var3.b).a(var2.b.c(var4.b)).a(var5.b.c(var6.b));
      var17[1][2] = var1.c.c(var3.b).a(var2.c.c(var4.b)).a(var5.c.c(var6.b));
      var17[2][0] = var1.a.c(var3.c).a(var2.a.c(var4.c)).a(var5.a.c(var6.c));
      var17[2][1] = var1.b.c(var3.c).a(var2.b.c(var4.c)).a(var5.b.c(var6.c));
      var17[2][2] = var1.c.c(var3.c).a(var2.c.c(var4.c)).a(var5.c.c(var6.c));
      kO[] var10 = a((T[][])var17);
      this.b = (T)var10[0];
      this.c = (T)var10[1];
      this.d = (T)var10[2];
      this.e = (T)var10[3];
   }

   private rd(re<T> var1, re<T> var2) {
      kO var3;
      if ((var3 = var1.d().c(var2.d())).d() == 0.0) {
         throw new oU(px.ZERO_NORM_FOR_ROTATION_DEFINING_VECTOR);
      }

      re var6 = var2;
      kO var4;
      if ((var4 = var1.a(var6)).d() < -0.999999999999998 * var3.d()) {
         var1 = var1;
         double var8;
         if ((var8 = 0.6 * var1.d().d()) == 0.0) {
            throw new oU(px.ZERO_NORM);
         }

         re var10000;
         if (BY.w(var1.a.d()) <= var8) {
            kO var12 = var1.b.c(var1.b).a(var1.c.c(var1.c)).k().b();
            var10000 = new re((T)var12.c().a(), (T)var12.c(var1.c), (T)((kO)var12.c(var1.b)).a());
         } else if (BY.w(var1.b.d()) <= var8) {
            kO var13 = var1.a.c(var1.a).a(var1.c.c(var1.c)).k().b();
            var10000 = new re((T)((kO)var13.c(var1.c)).a(), (T)var13.c().a(), (T)var13.c(var1.a));
         } else {
            kO var14 = var1.a.c(var1.a).a(var1.b.c(var1.b)).k().b();
            var10000 = new re((T)var14.c(var1.b), (T)((kO)var14.c(var1.a)).a(), (T)var14.c().a());
         }

         re var16 = var10000;
         this.b = (T)var3.c().a();
         this.c = var16.a.a();
         this.d = var16.b.a();
         this.e = var16.c.a();
      } else {
         this.b = (T)var4.d(var3).a(1.0).c(0.5).k();
         var4 = this.b.c((T)var3).c(2.0).b();
         var6 = var1;
         var1 = var2.b(var6);
         this.c = (T)var4.c(var1.a);
         this.d = (T)var4.c(var1.b);
         this.e = (T)var4.c(var1.c);
      }
   }

   @Deprecated
   private rd(rm var1, T var2, T var3, T var4) {
      this(var1, rl.VECTOR_OPERATOR$59e4743c, (T)var2, (T)var3, (T)var4);
   }

   private rd(rm var1, int var2, T var3, T var4, T var5) {
      kO var6 = (kO)var3.c().b();
      rd var10 = new rd(new re<>((T)var6, var1.l), (T)var3, var2);
      rd var11 = new rd(new re<>((T)var6, var1.m), (T)var4, var2);
      rd var8 = new rd(new re<>((T)var6, var1.n), (T)var5, var2);
      rd var9 = var10.a(var11.a(var8, var2), var2);
      this.b = var9.b;
      this.c = var9.c;
      this.d = var9.d;
      this.e = var9.e;
   }

   private static T[] a(T[][] var0) {
      kO[] var1 = Ch.a(var0[0][0].c(), 4);
      kO var2;
      if ((var2 = var0[0][0].a(var0[1][1]).a(var0[2][2])).d() > -0.19) {
         var1[0] = var2.a(1.0).k().c(0.5);
         var2 = ((kO)var1[0].b()).c(0.25);
         var1[1] = var2.c(var0[1][2].b(var0[2][1]));
         var1[2] = var2.c(var0[2][0].b(var0[0][2]));
         var1[3] = var2.c(var0[0][1].b(var0[1][0]));
      } else if ((var2 = var0[0][0].b(var0[1][1]).b(var0[2][2])).d() > -0.19) {
         var1[1] = var2.a(1.0).k().c(0.5);
         var2 = ((kO)var1[1].b()).c(0.25);
         var1[0] = var2.c(var0[1][2].b(var0[2][1]));
         var1[2] = var2.c(var0[0][1].a(var0[1][0]));
         var1[3] = var2.c(var0[0][2].a(var0[2][0]));
      } else if ((var2 = var0[1][1].b(var0[0][0]).b(var0[2][2])).d() > -0.19) {
         var1[2] = var2.a(1.0).k().c(0.5);
         var2 = ((kO)var1[2].b()).c(0.25);
         var1[0] = var2.c(var0[2][0].b(var0[0][2]));
         var1[1] = var2.c(var0[0][1].a(var0[1][0]));
         var1[3] = var2.c(var0[2][1].a(var0[1][2]));
      } else {
         var2 = var0[2][2].b(var0[0][0]).b(var0[1][1]);
         var1[3] = var2.a(1.0).k().c(0.5);
         var2 = ((kO)var1[3].b()).c(0.25);
         var1[0] = var2.c(var0[0][1].b(var0[1][0]));
         var1[1] = var2.c(var0[0][2].a(var0[2][0]));
         var1[2] = var2.c(var0[2][1].a(var0[1][2]));
      }

      return (T[])var1;
   }

   private rd<T> a() {
      return new rd<>(this.b.a(), this.c, this.d, this.e, false);
   }

   private T b() {
      return this.b;
   }

   private T c() {
      return this.c;
   }

   private T d() {
      return this.d;
   }

   private T e() {
      return this.e;
   }

   @Deprecated
   private re<T> f() {
      int var2 = rl.VECTOR_OPERATOR$59e4743c;
      rd var1 = this;
      kO var3;
      if ((var3 = this.c.c(var1.c).a(var1.d.c(var1.d)).a(var1.e.c(var1.e))).d() == 0.0) {
         kM var8 = var3.c();
         return new re<>((T)(var2 == rl.VECTOR_OPERATOR$59e4743c ? var8.b() : ((kO)var8.b()).a()), (T)var8.a(), (T)var8.a());
      } else {
         double var4 = var2 == rl.VECTOR_OPERATOR$59e4743c ? 1.0 : -1.0;
         if (var1.b.d() < 0.0) {
            kO var7 = ((kO)var3.k().b()).c(var4);
            return new re<>(var1.c.c((T)var7), var1.d.c((T)var7), var1.e.c((T)var7));
         } else {
            kO var6 = ((kO)((kO)var3.k().b()).a()).c(var4);
            return new re<>(var1.c.c((T)var6), var1.d.c((T)var6), var1.e.c((T)var6));
         }
      }
   }

   private re<T> a(int var1) {
      kO var2;
      if ((var2 = this.c.c(this.c).a(this.d.c(this.d)).a(this.e.c(this.e))).d() == 0.0) {
         kM var7 = var2.c();
         return new re<>((T)(var1 == rl.VECTOR_OPERATOR$59e4743c ? var7.b() : ((kO)var7.b()).a()), (T)var7.a(), (T)var7.a());
      } else {
         double var3 = var1 == rl.VECTOR_OPERATOR$59e4743c ? 1.0 : -1.0;
         if (this.b.d() < 0.0) {
            kO var6 = ((kO)var2.k().b()).c(var3);
            return new re<>(this.c.c((T)var6), this.d.c((T)var6), this.e.c((T)var6));
         } else {
            kO var5 = ((kO)((kO)var2.k().b()).a()).c(var3);
            return new re<>(this.c.c((T)var5), this.d.c((T)var5), this.e.c((T)var5));
         }
      }
   }

   private T g() {
      if (this.b.d() < -0.1 || this.b.d() > 0.1) {
         return this.c.c(this.c).a(this.d.c(this.d)).a(this.e.c(this.e)).k().u().a(2);
      } else {
         return this.b.d() < 0.0 ? this.b.a().t().a(2) : this.b.t().a(2);
      }
   }

   @Deprecated
   private T[] a(rm var1) {
      int var3 = rl.VECTOR_OPERATOR$59e4743c;
      rm var2 = var1;
      rd var4 = this;
      if (var3 == rl.VECTOR_OPERATOR$59e4743c) {
         if (var2 == rm.a) {
            re var52 = var4.a(var4.a(0.0, 0.0, 1.0));
            re var28;
            if (!((var28 = var4.b(var4.a(1.0, 0.0, 0.0))).c.d() < -0.9999999999) && !(var28.c.d() > 0.9999999999)) {
               return (T[])a(var52.b.a().h(var52.c), var28.c.u(), var28.b.a().h(var28.a));
            } else {
               throw new rb(true);
            }
         } else if (var2 == rm.b) {
            re var51 = var4.a(var4.a(0.0, 1.0, 0.0));
            re var27;
            if (!((var27 = var4.b(var4.a(1.0, 0.0, 0.0))).b.d() < -0.9999999999) && !(var27.b.d() > 0.9999999999)) {
               return (T[])a(var51.c.h(var51.b), var27.b.u().a(), var27.c.h(var27.a));
            } else {
               throw new rb(true);
            }
         } else if (var2 == rm.c) {
            re var50 = var4.a(var4.a(0.0, 0.0, 1.0));
            re var26;
            if (!((var26 = var4.b(var4.a(0.0, 1.0, 0.0))).c.d() < -0.9999999999) && !(var26.c.d() > 0.9999999999)) {
               return (T[])a(var50.a.h(var50.c), var26.c.u().a(), var26.a.h(var26.b));
            } else {
               throw new rb(true);
            }
         } else if (var2 == rm.d) {
            re var49 = var4.a(var4.a(1.0, 0.0, 0.0));
            re var25;
            if (!((var25 = var4.b(var4.a(0.0, 1.0, 0.0))).a.d() < -0.9999999999) && !(var25.a.d() > 0.9999999999)) {
               return (T[])a(var49.c.a().h(var49.a), var25.a.u(), var25.c.a().h(var25.b));
            } else {
               throw new rb(true);
            }
         } else if (var2 == rm.e) {
            re var48 = var4.a(var4.a(0.0, 1.0, 0.0));
            re var24;
            if (!((var24 = var4.b(var4.a(0.0, 0.0, 1.0))).b.d() < -0.9999999999) && !(var24.b.d() > 0.9999999999)) {
               return (T[])a(var48.a.a().h(var48.b), var24.b.u(), var24.a.a().h(var24.c));
            } else {
               throw new rb(true);
            }
         } else if (var2 == rm.f) {
            re var47 = var4.a(var4.a(1.0, 0.0, 0.0));
            re var23;
            if (!((var23 = var4.b(var4.a(0.0, 0.0, 1.0))).a.d() < -0.9999999999) && !(var23.a.d() > 0.9999999999)) {
               return (T[])a(var47.b.h(var47.a), var23.a.u().a(), var23.b.h(var23.c));
            } else {
               throw new rb(true);
            }
         } else if (var2 == rm.g) {
            re var46 = var4.a(var4.a(1.0, 0.0, 0.0));
            re var22;
            if (!((var22 = var4.b(var4.a(1.0, 0.0, 0.0))).a.d() < -0.9999999999) && !(var22.a.d() > 0.9999999999)) {
               return (T[])a(var46.b.h(var46.c.a()), var22.a.t(), var22.b.h(var22.c));
            } else {
               throw new rb(false);
            }
         } else if (var2 == rm.h) {
            re var45 = var4.a(var4.a(1.0, 0.0, 0.0));
            re var21;
            if (!((var21 = var4.b(var4.a(1.0, 0.0, 0.0))).a.d() < -0.9999999999) && !(var21.a.d() > 0.9999999999)) {
               return (T[])a(var45.c.h(var45.b), var21.a.t(), var21.c.h(var21.b.a()));
            } else {
               throw new rb(false);
            }
         } else if (var2 == rm.i) {
            re var44 = var4.a(var4.a(0.0, 1.0, 0.0));
            re var20;
            if (!((var20 = var4.b(var4.a(0.0, 1.0, 0.0))).b.d() < -0.9999999999) && !(var20.b.d() > 0.9999999999)) {
               return (T[])a(var44.a.h(var44.c), var20.b.t(), var20.a.h(var20.c.a()));
            } else {
               throw new rb(false);
            }
         } else if (var2 == rm.j) {
            re var43 = var4.a(var4.a(0.0, 1.0, 0.0));
            re var19;
            if (!((var19 = var4.b(var4.a(0.0, 1.0, 0.0))).b.d() < -0.9999999999) && !(var19.b.d() > 0.9999999999)) {
               return (T[])a(var43.c.h(var43.a.a()), var19.b.t(), var19.c.h(var19.a));
            } else {
               throw new rb(false);
            }
         } else if (var2 == rm.k) {
            re var42 = var4.a(var4.a(0.0, 0.0, 1.0));
            re var18;
            if (!((var18 = var4.b(var4.a(0.0, 0.0, 1.0))).c.d() < -0.9999999999) && !(var18.c.d() > 0.9999999999)) {
               return (T[])a(var42.a.h(var42.b.a()), var18.c.t(), var18.a.h(var18.b));
            } else {
               throw new rb(false);
            }
         } else {
            re var41 = var4.a(var4.a(0.0, 0.0, 1.0));
            re var17;
            if (!((var17 = var4.b(var4.a(0.0, 0.0, 1.0))).c.d() < -0.9999999999) && !(var17.c.d() > 0.9999999999)) {
               return (T[])a(var41.b.h(var41.a), var17.c.t(), var17.b.h(var17.a.a()));
            } else {
               throw new rb(false);
            }
         }
      } else if (var2 == rm.a) {
         re var40 = var4.a(rs.b);
         re var16;
         if (!((var16 = var4.b(rs.f)).a.d() < -0.9999999999) && !(var16.a.d() > 0.9999999999)) {
            return (T[])a(var16.b.a().h(var16.c), var16.a.u(), var40.b.a().h(var40.a));
         } else {
            throw new rb(true);
         }
      } else if (var2 == rm.b) {
         re var39 = var4.a(rs.b);
         re var15;
         if (!((var15 = var4.b(rs.d)).a.d() < -0.9999999999) && !(var15.a.d() > 0.9999999999)) {
            return (T[])a(var15.c.h(var15.b), var15.a.u().a(), var39.c.h(var39.a));
         } else {
            throw new rb(true);
         }
      } else if (var2 == rm.c) {
         re var38 = var4.a(rs.d);
         re var14;
         if (!((var14 = var4.b(rs.f)).b.d() < -0.9999999999) && !(var14.b.d() > 0.9999999999)) {
            return (T[])a(var14.a.h(var14.c), var14.b.u().a(), var38.a.h(var38.b));
         } else {
            throw new rb(true);
         }
      } else if (var2 == rm.d) {
         re var37 = var4.a(rs.d);
         re var13;
         if (!((var13 = var4.b(rs.b)).b.d() < -0.9999999999) && !(var13.b.d() > 0.9999999999)) {
            return (T[])a(var13.c.a().h(var13.a), var13.b.u(), var37.c.a().h(var37.b));
         } else {
            throw new rb(true);
         }
      } else if (var2 == rm.e) {
         re var36 = var4.a(rs.f);
         re var12;
         if (!((var12 = var4.b(rs.d)).c.d() < -0.9999999999) && !(var12.c.d() > 0.9999999999)) {
            return (T[])a(var12.a.a().h(var12.b), var12.c.u(), var36.a.a().h(var36.c));
         } else {
            throw new rb(true);
         }
      } else if (var2 == rm.f) {
         re var35 = var4.a(rs.f);
         re var11;
         if (!((var11 = var4.b(rs.b)).c.d() < -0.9999999999) && !(var11.c.d() > 0.9999999999)) {
            return (T[])a(var11.b.h(var11.a), var11.c.u().a(), var35.b.h(var35.c));
         } else {
            throw new rb(true);
         }
      } else if (var2 == rm.g) {
         re var34 = var4.a(rs.b);
         re var10;
         if (!((var10 = var4.b(rs.b)).a.d() < -0.9999999999) && !(var10.a.d() > 0.9999999999)) {
            return (T[])a(var10.b.h(var10.c.a()), var10.a.t(), var34.b.h(var34.c));
         } else {
            throw new rb(false);
         }
      } else if (var2 == rm.h) {
         re var33 = var4.a(rs.b);
         re var9;
         if (!((var9 = var4.b(rs.b)).a.d() < -0.9999999999) && !(var9.a.d() > 0.9999999999)) {
            return (T[])a(var9.c.h(var9.b), var9.a.t(), var33.c.h(var33.b.a()));
         } else {
            throw new rb(false);
         }
      } else if (var2 == rm.i) {
         re var32 = var4.a(rs.d);
         re var8;
         if (!((var8 = var4.b(rs.d)).b.d() < -0.9999999999) && !(var8.b.d() > 0.9999999999)) {
            return (T[])a(var8.a.h(var8.c), var8.b.t(), var32.a.h(var32.c.a()));
         } else {
            throw new rb(false);
         }
      } else if (var2 == rm.j) {
         re var31 = var4.a(rs.d);
         re var7;
         if (!((var7 = var4.b(rs.d)).b.d() < -0.9999999999) && !(var7.b.d() > 0.9999999999)) {
            return (T[])a(var7.c.h(var7.a.a()), var7.b.t(), var31.c.h(var31.a));
         } else {
            throw new rb(false);
         }
      } else if (var2 == rm.k) {
         re var30 = var4.a(rs.f);
         re var6;
         if (!((var6 = var4.b(rs.f)).c.d() < -0.9999999999) && !(var6.c.d() > 0.9999999999)) {
            return (T[])a(var6.a.h(var6.b.a()), var6.c.t(), var30.a.h(var30.b));
         } else {
            throw new rb(false);
         }
      } else {
         re var29 = var4.a(rs.f);
         re var5;
         if (!((var5 = var4.b(rs.f)).c.d() < -0.9999999999) && !(var5.c.d() > 0.9999999999)) {
            return (T[])a(var5.b.h(var5.a), var5.c.t(), var29.b.h(var29.a.a()));
         } else {
            throw new rb(false);
         }
      }
   }

   private T[] a(rm var1, int var2) {
      if (var2 == rl.VECTOR_OPERATOR$59e4743c) {
         if (var1 == rm.a) {
            re var27 = this.a(this.a(0.0, 0.0, 1.0));
            re var51;
            if (!((var51 = this.b(this.a(1.0, 0.0, 0.0))).c.d() < -0.9999999999) && !(var51.c.d() > 0.9999999999)) {
               return (T[])a(var27.b.a().h(var27.c), var51.c.u(), var51.b.a().h(var51.a));
            } else {
               throw new rb(true);
            }
         } else if (var1 == rm.b) {
            re var26 = this.a(this.a(0.0, 1.0, 0.0));
            re var50;
            if (!((var50 = this.b(this.a(1.0, 0.0, 0.0))).b.d() < -0.9999999999) && !(var50.b.d() > 0.9999999999)) {
               return (T[])a(var26.c.h(var26.b), var50.b.u().a(), var50.c.h(var50.a));
            } else {
               throw new rb(true);
            }
         } else if (var1 == rm.c) {
            re var25 = this.a(this.a(0.0, 0.0, 1.0));
            re var49;
            if (!((var49 = this.b(this.a(0.0, 1.0, 0.0))).c.d() < -0.9999999999) && !(var49.c.d() > 0.9999999999)) {
               return (T[])a(var25.a.h(var25.c), var49.c.u().a(), var49.a.h(var49.b));
            } else {
               throw new rb(true);
            }
         } else if (var1 == rm.d) {
            re var24 = this.a(this.a(1.0, 0.0, 0.0));
            re var48;
            if (!((var48 = this.b(this.a(0.0, 1.0, 0.0))).a.d() < -0.9999999999) && !(var48.a.d() > 0.9999999999)) {
               return (T[])a(var24.c.a().h(var24.a), var48.a.u(), var48.c.a().h(var48.b));
            } else {
               throw new rb(true);
            }
         } else if (var1 == rm.e) {
            re var23 = this.a(this.a(0.0, 1.0, 0.0));
            re var47;
            if (!((var47 = this.b(this.a(0.0, 0.0, 1.0))).b.d() < -0.9999999999) && !(var47.b.d() > 0.9999999999)) {
               return (T[])a(var23.a.a().h(var23.b), var47.b.u(), var47.a.a().h(var47.c));
            } else {
               throw new rb(true);
            }
         } else if (var1 == rm.f) {
            re var22 = this.a(this.a(1.0, 0.0, 0.0));
            re var46;
            if (!((var46 = this.b(this.a(0.0, 0.0, 1.0))).a.d() < -0.9999999999) && !(var46.a.d() > 0.9999999999)) {
               return (T[])a(var22.b.h(var22.a), var46.a.u().a(), var46.b.h(var46.c));
            } else {
               throw new rb(true);
            }
         } else if (var1 == rm.g) {
            re var21 = this.a(this.a(1.0, 0.0, 0.0));
            re var45;
            if (!((var45 = this.b(this.a(1.0, 0.0, 0.0))).a.d() < -0.9999999999) && !(var45.a.d() > 0.9999999999)) {
               return (T[])a(var21.b.h(var21.c.a()), var45.a.t(), var45.b.h(var45.c));
            } else {
               throw new rb(false);
            }
         } else if (var1 == rm.h) {
            re var20 = this.a(this.a(1.0, 0.0, 0.0));
            re var44;
            if (!((var44 = this.b(this.a(1.0, 0.0, 0.0))).a.d() < -0.9999999999) && !(var44.a.d() > 0.9999999999)) {
               return (T[])a(var20.c.h(var20.b), var44.a.t(), var44.c.h(var44.b.a()));
            } else {
               throw new rb(false);
            }
         } else if (var1 == rm.i) {
            re var19 = this.a(this.a(0.0, 1.0, 0.0));
            re var43;
            if (!((var43 = this.b(this.a(0.0, 1.0, 0.0))).b.d() < -0.9999999999) && !(var43.b.d() > 0.9999999999)) {
               return (T[])a(var19.a.h(var19.c), var43.b.t(), var43.a.h(var43.c.a()));
            } else {
               throw new rb(false);
            }
         } else if (var1 == rm.j) {
            re var18 = this.a(this.a(0.0, 1.0, 0.0));
            re var42;
            if (!((var42 = this.b(this.a(0.0, 1.0, 0.0))).b.d() < -0.9999999999) && !(var42.b.d() > 0.9999999999)) {
               return (T[])a(var18.c.h(var18.a.a()), var42.b.t(), var42.c.h(var42.a));
            } else {
               throw new rb(false);
            }
         } else if (var1 == rm.k) {
            re var17 = this.a(this.a(0.0, 0.0, 1.0));
            re var41;
            if (!((var41 = this.b(this.a(0.0, 0.0, 1.0))).c.d() < -0.9999999999) && !(var41.c.d() > 0.9999999999)) {
               return (T[])a(var17.a.h(var17.b.a()), var41.c.t(), var41.a.h(var41.b));
            } else {
               throw new rb(false);
            }
         } else {
            re var16 = this.a(this.a(0.0, 0.0, 1.0));
            re var40;
            if (!((var40 = this.b(this.a(0.0, 0.0, 1.0))).c.d() < -0.9999999999) && !(var40.c.d() > 0.9999999999)) {
               return (T[])a(var16.b.h(var16.a), var40.c.t(), var40.b.h(var40.a.a()));
            } else {
               throw new rb(false);
            }
         }
      } else if (var1 == rm.a) {
         re var15 = this.a(rs.b);
         re var39;
         if (!((var39 = this.b(rs.f)).a.d() < -0.9999999999) && !(var39.a.d() > 0.9999999999)) {
            return (T[])a(var39.b.a().h(var39.c), var39.a.u(), var15.b.a().h(var15.a));
         } else {
            throw new rb(true);
         }
      } else if (var1 == rm.b) {
         re var14 = this.a(rs.b);
         re var38;
         if (!((var38 = this.b(rs.d)).a.d() < -0.9999999999) && !(var38.a.d() > 0.9999999999)) {
            return (T[])a(var38.c.h(var38.b), var38.a.u().a(), var14.c.h(var14.a));
         } else {
            throw new rb(true);
         }
      } else if (var1 == rm.c) {
         re var13 = this.a(rs.d);
         re var37;
         if (!((var37 = this.b(rs.f)).b.d() < -0.9999999999) && !(var37.b.d() > 0.9999999999)) {
            return (T[])a(var37.a.h(var37.c), var37.b.u().a(), var13.a.h(var13.b));
         } else {
            throw new rb(true);
         }
      } else if (var1 == rm.d) {
         re var12 = this.a(rs.d);
         re var36;
         if (!((var36 = this.b(rs.b)).b.d() < -0.9999999999) && !(var36.b.d() > 0.9999999999)) {
            return (T[])a(var36.c.a().h(var36.a), var36.b.u(), var12.c.a().h(var12.b));
         } else {
            throw new rb(true);
         }
      } else if (var1 == rm.e) {
         re var11 = this.a(rs.f);
         re var35;
         if (!((var35 = this.b(rs.d)).c.d() < -0.9999999999) && !(var35.c.d() > 0.9999999999)) {
            return (T[])a(var35.a.a().h(var35.b), var35.c.u(), var11.a.a().h(var11.c));
         } else {
            throw new rb(true);
         }
      } else if (var1 == rm.f) {
         re var10 = this.a(rs.f);
         re var34;
         if (!((var34 = this.b(rs.b)).c.d() < -0.9999999999) && !(var34.c.d() > 0.9999999999)) {
            return (T[])a(var34.b.h(var34.a), var34.c.u().a(), var10.b.h(var10.c));
         } else {
            throw new rb(true);
         }
      } else if (var1 == rm.g) {
         re var9 = this.a(rs.b);
         re var33;
         if (!((var33 = this.b(rs.b)).a.d() < -0.9999999999) && !(var33.a.d() > 0.9999999999)) {
            return (T[])a(var33.b.h(var33.c.a()), var33.a.t(), var9.b.h(var9.c));
         } else {
            throw new rb(false);
         }
      } else if (var1 == rm.h) {
         re var8 = this.a(rs.b);
         re var32;
         if (!((var32 = this.b(rs.b)).a.d() < -0.9999999999) && !(var32.a.d() > 0.9999999999)) {
            return (T[])a(var32.c.h(var32.b), var32.a.t(), var8.c.h(var8.b.a()));
         } else {
            throw new rb(false);
         }
      } else if (var1 == rm.i) {
         re var7 = this.a(rs.d);
         re var31;
         if (!((var31 = this.b(rs.d)).b.d() < -0.9999999999) && !(var31.b.d() > 0.9999999999)) {
            return (T[])a(var31.a.h(var31.c), var31.b.t(), var7.a.h(var7.c.a()));
         } else {
            throw new rb(false);
         }
      } else if (var1 == rm.j) {
         re var6 = this.a(rs.d);
         re var30;
         if (!((var30 = this.b(rs.d)).b.d() < -0.9999999999) && !(var30.b.d() > 0.9999999999)) {
            return (T[])a(var30.c.h(var30.a.a()), var30.b.t(), var6.c.h(var6.a));
         } else {
            throw new rb(false);
         }
      } else if (var1 == rm.k) {
         re var5 = this.a(rs.f);
         re var29;
         if (!((var29 = this.b(rs.f)).c.d() < -0.9999999999) && !(var29.c.d() > 0.9999999999)) {
            return (T[])a(var29.a.h(var29.b.a()), var29.c.t(), var5.a.h(var5.b));
         } else {
            throw new rb(false);
         }
      } else {
         re var4 = this.a(rs.f);
         re var28;
         if (!((var28 = this.b(rs.f)).c.d() < -0.9999999999) && !(var28.c.d() > 0.9999999999)) {
            return (T[])a(var28.b.h(var28.a), var28.c.t(), var4.b.h(var4.a.a()));
         } else {
            throw new rb(false);
         }
      }
   }

   private static T[] a(T var0, T var1, T var2) {
      kO[] var3;
      (var3 = Ch.a(var0.c(), 3))[0] = var0;
      var3[1] = var1;
      var3[2] = var2;
      return (T[])var3;
   }

   private re<T> a(double var1, double var3, double var5) {
      kO var7 = this.b.c().a();
      return new re<>((T)var7.a(var1), (T)var7.a(var3), (T)var7.a(var5));
   }

   private T[][] h() {
      kO var1 = this.b.c(this.b);
      kO var2 = this.b.c(this.c);
      kO var3 = this.b.c(this.d);
      kO var4 = this.b.c(this.e);
      kO var5 = this.c.c(this.c);
      kO var6 = this.c.c(this.d);
      kO var7 = this.c.c(this.e);
      kO var8 = this.d.c(this.d);
      kO var9 = this.d.c(this.e);
      kO var10 = this.e.c(this.e);
      kO[][] var11;
      (var11 = Ch.a(this.b.c(), 3, 3))[0][0] = ((kO)var1.a(var5).a(2)).b(1.0);
      var11[1][0] = (kO)var6.b(var4).a(2);
      var11[2][0] = (kO)var7.a(var3).a(2);
      var11[0][1] = (kO)var6.a(var4).a(2);
      var11[1][1] = ((kO)var1.a(var8).a(2)).b(1.0);
      var11[2][1] = (kO)var9.b(var2).a(2);
      var11[0][2] = (kO)var7.b(var3).a(2);
      var11[1][2] = (kO)var9.a(var2).a(2);
      var11[2][2] = ((kO)var1.a(var10).a(2)).b(1.0);
      return (T[][])var11;
   }

   private rk i() {
      return new rk(this.b.d(), this.c.d(), this.d.d(), this.e.d(), false);
   }

   private re<T> a(re<T> var1) {
      kO var2 = var1.a;
      kO var3 = var1.b;
      kO var5 = var1.c;
      kO var4 = this.c.c((T)var2).a(this.d.c((T)var3)).a(this.e.c((T)var5));
      return new re<>(
         this.b.c((T)((kO)var2.c(this.b)).b(this.d.c((T)var5).b(this.e.c((T)var3)))).a((T)var4.c(this.c)).a(2).b((T)var2),
         this.b.c((T)((kO)var3.c(this.b)).b(this.e.c((T)var2).b(this.c.c((T)var5)))).a((T)var4.c(this.d)).a(2).b((T)var3),
         this.b.c((T)((kO)var5.c(this.b)).b(this.c.c((T)var3).b(this.d.c((T)var2)))).a((T)var4.c(this.e)).a(2).b((T)var5)
      );
   }

   private re<T> a(rs var1) {
      double var2 = var1.i;
      double var4 = var1.j;
      double var6 = var1.k;
      kO var8 = this.c.c(var2).a(this.d.c(var4)).a(this.e.c(var6));
      return new re<>(
         this.b.c(this.b.c(var2).b(this.d.c(var6).b(this.e.c(var4)))).a((T)var8.c(this.c)).a(2).b(var2),
         this.b.c(this.b.c(var4).b(this.e.c(var2).b(this.c.c(var6)))).a((T)var8.c(this.d)).a(2).b(var4),
         this.b.c(this.b.c(var6).b(this.c.c(var4).b(this.d.c(var2)))).a((T)var8.c(this.e)).a(2).b(var6)
      );
   }

   private void a(T[] var1, T[] var2) {
      kO var3 = var1[0];
      kO var4 = var1[1];
      kO var6 = var1[2];
      kO var5 = this.c.c((T)var3).a(this.d.c((T)var4)).a(this.e.c((T)var6));
      var2[0] = this.b.c((T)((kO)var3.c(this.b)).b(this.d.c((T)var6).b(this.e.c((T)var4)))).a((T)var5.c(this.c)).a(2).b((T)var3);
      var2[1] = this.b.c((T)((kO)var4.c(this.b)).b(this.e.c((T)var3).b(this.c.c((T)var6)))).a((T)var5.c(this.d)).a(2).b((T)var4);
      var2[2] = this.b.c((T)((kO)var6.c(this.b)).b(this.c.c((T)var4).b(this.d.c((T)var3)))).a((T)var5.c(this.e)).a(2).b((T)var6);
   }

   private void a(double[] var1, T[] var2) {
      double var3 = var1[0];
      double var5 = var1[1];
      double var7 = var1[2];
      kO var9 = this.c.c(var3).a(this.d.c(var5)).a(this.e.c(var7));
      var2[0] = this.b.c(this.b.c(var3).b(this.d.c(var7).b(this.e.c(var5)))).a((T)var9.c(this.c)).a(2).b(var3);
      var2[1] = this.b.c(this.b.c(var5).b(this.e.c(var3).b(this.c.c(var7)))).a((T)var9.c(this.d)).a(2).b(var5);
      var2[2] = this.b.c(this.b.c(var7).b(this.c.c(var5).b(this.d.c(var3)))).a((T)var9.c(this.e)).a(2).b(var7);
   }

   private static <T extends kO<T>> re<T> a(rk var0, re<T> var1) {
      kO var2 = var1.a;
      kO var3 = var1.b;
      kO var6 = var1.c;
      kO var4 = var2.c(var0.b).a(var3.c(var0.c)).a(var6.c(var0.d));
      return new re<>(
         (T)((kO)var2.c(var0.a).b(var6.c(var0.c).b(var3.c(var0.d))).c(var0.a).a(var4.c(var0.b)).a(2)).b(var2),
         (T)((kO)var3.c(var0.a).b(var2.c(var0.d).b(var6.c(var0.b))).c(var0.a).a(var4.c(var0.c)).a(2)).b(var3),
         (T)((kO)var6.c(var0.a).b(var3.c(var0.b).b(var2.c(var0.c))).c(var0.a).a(var4.c(var0.d)).a(2)).b(var6)
      );
   }

   private re<T> b(re<T> var1) {
      kO var2 = var1.a;
      kO var3 = var1.b;
      kO var6 = var1.c;
      kO var4 = this.c.c((T)var2).a(this.d.c((T)var3)).a(this.e.c((T)var6));
      kO var5 = this.b.a();
      return new re<>(
         (T)((kO)((kO)((kO)var5.c(var2.c(var5).b(this.d.c((T)var6).b(this.e.c((T)var3))))).a(var4.c(this.c))).a(2)).b(var2),
         (T)((kO)((kO)((kO)var5.c(var3.c(var5).b(this.e.c((T)var2).b(this.c.c((T)var6))))).a(var4.c(this.d))).a(2)).b(var3),
         (T)((kO)((kO)((kO)var5.c(var6.c(var5).b(this.c.c((T)var3).b(this.d.c((T)var2))))).a(var4.c(this.e))).a(2)).b(var6)
      );
   }

   private re<T> b(rs var1) {
      double var2 = var1.i;
      double var4 = var1.j;
      double var6 = var1.k;
      kO var9 = this.c.c(var2).a(this.d.c(var4)).a(this.e.c(var6));
      kO var8 = this.b.a();
      return new re<>(
         (T)((kO)((kO)((kO)var8.c(var8.c(var2).b(this.d.c(var6).b(this.e.c(var4))))).a(var9.c(this.c))).a(2)).b(var2),
         (T)((kO)((kO)((kO)var8.c(var8.c(var4).b(this.e.c(var2).b(this.c.c(var6))))).a(var9.c(this.d))).a(2)).b(var4),
         (T)((kO)((kO)((kO)var8.c(var8.c(var6).b(this.c.c(var4).b(this.d.c(var2))))).a(var9.c(this.e))).a(2)).b(var6)
      );
   }

   private void b(T[] var1, T[] var2) {
      kO var3 = var1[0];
      kO var4 = var1[1];
      kO var7 = var1[2];
      kO var5 = this.c.c((T)var3).a(this.d.c((T)var4)).a(this.e.c((T)var7));
      kO var6 = this.b.a();
      var2[0] = ((kO)((kO)((kO)var6.c(var3.c(var6).b(this.d.c((T)var7).b(this.e.c((T)var4))))).a(var5.c(this.c))).a(2)).b(var3);
      var2[1] = ((kO)((kO)((kO)var6.c(var4.c(var6).b(this.e.c((T)var3).b(this.c.c((T)var7))))).a(var5.c(this.d))).a(2)).b(var4);
      var2[2] = ((kO)((kO)((kO)var6.c(var7.c(var6).b(this.c.c((T)var4).b(this.d.c((T)var3))))).a(var5.c(this.e))).a(2)).b(var7);
   }

   private void b(double[] var1, T[] var2) {
      double var3 = var1[0];
      double var5 = var1[1];
      double var7 = var1[2];
      kO var10 = this.c.c(var3).a(this.d.c(var5)).a(this.e.c(var7));
      kO var9 = this.b.a();
      var2[0] = ((kO)((kO)((kO)var9.c(var9.c(var3).b(this.d.c(var7).b(this.e.c(var5))))).a(var10.c(this.c))).a(2)).b(var3);
      var2[1] = ((kO)((kO)((kO)var9.c(var9.c(var5).b(this.e.c(var3).b(this.c.c(var7))))).a(var10.c(this.d))).a(2)).b(var5);
      var2[2] = ((kO)((kO)((kO)var9.c(var9.c(var7).b(this.c.c(var5).b(this.d.c(var3))))).a(var10.c(this.e))).a(2)).b(var7);
   }

   private static <T extends kO<T>> re<T> b(rk var0, re<T> var1) {
      kO var2 = var1.a;
      kO var3 = var1.b;
      kO var8 = var1.c;
      kO var4 = var2.c(var0.b).a(var3.c(var0.c)).a(var8.c(var0.d));
      double var6 = -var0.a;
      return new re<>(
         (T)((kO)var2.c(var6).b(var8.c(var0.c).b(var3.c(var0.d))).c(var6).a(var4.c(var0.b)).a(2)).b(var2),
         (T)((kO)var3.c(var6).b(var2.c(var0.d).b(var8.c(var0.b))).c(var6).a(var4.c(var0.c)).a(2)).b(var3),
         (T)((kO)var8.c(var6).b(var3.c(var0.b).b(var2.c(var0.c))).c(var6).a(var4.c(var0.d)).a(2)).b(var8)
      );
   }

   private rd<T> a(rd<T> var1) {
      return this.a(var1, rl.VECTOR_OPERATOR$59e4743c);
   }

   private rd<T> a(rd<T> var1, int var2) {
      return var2 == rl.VECTOR_OPERATOR$59e4743c ? this.b(var1) : var1.b(this);
   }

   private rd<T> b(rd<T> var1) {
      return new rd<>(
         var1.b.c(this.b).b(var1.c.c(this.c).a(var1.d.c(this.d)).a(var1.e.c(this.e))),
         var1.c.c(this.b).a(var1.b.c(this.c)).a(var1.d.c(this.e).b(var1.e.c(this.d))),
         var1.d.c(this.b).a(var1.b.c(this.d)).a(var1.e.c(this.c).b(var1.c.c(this.e))),
         var1.e.c(this.b).a(var1.b.c(this.e)).a(var1.c.c(this.d).b(var1.d.c(this.c))),
         false
      );
   }

   private rd<T> a(rk var1) {
      int var3 = rl.VECTOR_OPERATOR$59e4743c;
      rk var2 = var1;
      rd var4 = this;
      if (var3 == rl.VECTOR_OPERATOR$59e4743c) {
         var2 = var2;
         rd var5 = var4;
         return new rd<>(
            var5.b.c(var2.a).b(var5.c.c(var2.b).a(var5.d.c(var2.c)).a(var5.e.c(var2.d))),
            var5.b.c(var2.b).a(var5.c.c(var2.a)).a(var5.e.c(var2.c).b(var5.d.c(var2.d))),
            var5.b.c(var2.c).a(var5.d.c(var2.a)).a(var5.c.c(var2.d).b(var5.e.c(var2.b))),
            var5.b.c(var2.d).a(var5.e.c(var2.a)).a(var5.d.c(var2.b).b(var5.c.c(var2.c))),
            false
         );
      } else {
         return a(var2, var4);
      }
   }

   private rd<T> a(rk var1, int var2) {
      return var2 == rl.VECTOR_OPERATOR$59e4743c ? this.b(var1) : a(var1, this);
   }

   private rd<T> b(rk var1) {
      return new rd<>(
         this.b.c(var1.a).b(this.c.c(var1.b).a(this.d.c(var1.c)).a(this.e.c(var1.d))),
         this.b.c(var1.b).a(this.c.c(var1.a)).a(this.e.c(var1.c).b(this.d.c(var1.d))),
         this.b.c(var1.c).a(this.d.c(var1.a)).a(this.c.c(var1.d).b(this.e.c(var1.b))),
         this.b.c(var1.d).a(this.e.c(var1.a)).a(this.d.c(var1.b).b(this.c.c(var1.c))),
         false
      );
   }

   private static <T extends kO<T>> rd<T> a(rk var0, rd<T> var1) {
      return new rd<>(
         var1.b.c(var0.a).b(var1.c.c(var0.b).a(var1.d.c(var0.c)).a(var1.e.c(var0.d))),
         var1.c.c(var0.a).a(var1.b.c(var0.b)).a(var1.d.c(var0.d).b(var1.e.c(var0.c))),
         var1.d.c(var0.a).a(var1.b.c(var0.c)).a(var1.e.c(var0.b).b(var1.c.c(var0.d))),
         var1.e.c(var0.a).a(var1.b.c(var0.d)).a(var1.c.c(var0.c).b(var1.d.c(var0.b))),
         false
      );
   }

   private rd<T> c(rd<T> var1) {
      int var3 = rl.VECTOR_OPERATOR$59e4743c;
      rd var2 = var1;
      var1 = this;
      return var3 == rl.VECTOR_OPERATOR$59e4743c ? var1.d(var2) : var2.b(var1.a());
   }

   private rd<T> b(rd<T> var1, int var2) {
      return var2 == rl.VECTOR_OPERATOR$59e4743c ? this.d(var1) : var1.b(this.a());
   }

   private rd<T> d(rd<T> var1) {
      return new rd<>(
         var1.b.c(this.b).a(var1.c.c(this.c).a(var1.d.c(this.d)).a(var1.e.c(this.e))).a(),
         var1.b.c(this.c).a(var1.d.c(this.e).b(var1.e.c(this.d))).b(var1.c.c(this.b)),
         var1.b.c(this.d).a(var1.e.c(this.c).b(var1.c.c(this.e))).b(var1.d.c(this.b)),
         var1.b.c(this.e).a(var1.c.c(this.d).b(var1.d.c(this.c))).b(var1.e.c(this.b)),
         false
      );
   }

   private rd<T> c(rk var1) {
      int var3 = rl.VECTOR_OPERATOR$59e4743c;
      rk var2 = var1;
      rd var4 = this;
      if (var3 == rl.VECTOR_OPERATOR$59e4743c) {
         var2 = var2;
         rd var5 = var4;
         return new rd<>(
            var5.b.c(var2.a).a(var5.c.c(var2.b).a(var5.d.c(var2.c)).a(var5.e.c(var2.d))).a(),
            var5.c.c(var2.a).a(var5.e.c(var2.c).b(var5.d.c(var2.d))).b(var5.b.c(var2.b)),
            var5.d.c(var2.a).a(var5.c.c(var2.d).b(var5.e.c(var2.b))).b(var5.b.c(var2.c)),
            var5.e.c(var2.a).a(var5.d.c(var2.b).b(var5.c.c(var2.c))).b(var5.b.c(var2.d)),
            false
         );
      } else {
         return a(var2, var4.a());
      }
   }

   private rd<T> b(rk var1, int var2) {
      return var2 == rl.VECTOR_OPERATOR$59e4743c ? this.d(var1) : a(var1, this.a());
   }

   private rd<T> d(rk var1) {
      return new rd<>(
         this.b.c(var1.a).a(this.c.c(var1.b).a(this.d.c(var1.c)).a(this.e.c(var1.d))).a(),
         this.c.c(var1.a).a(this.e.c(var1.c).b(this.d.c(var1.d))).b(this.b.c(var1.b)),
         this.d.c(var1.a).a(this.c.c(var1.d).b(this.e.c(var1.b))).b(this.b.c(var1.c)),
         this.e.c(var1.a).a(this.d.c(var1.b).b(this.c.c(var1.c))).b(this.b.c(var1.d)),
         false
      );
   }

   private static <T extends kO<T>> rd<T> b(rk var0, rd<T> var1) {
      return new rd<>(
         var1.b.c(var0.a).a(var1.c.c(var0.b).a(var1.d.c(var0.c)).a(var1.e.c(var0.d))).a(),
         var1.b.c(var0.b).a(var1.d.c(var0.d).b(var1.e.c(var0.c))).b(var1.c.c(var0.a)),
         var1.b.c(var0.c).a(var1.e.c(var0.b).b(var1.c.c(var0.d))).b(var1.d.c(var0.a)),
         var1.b.c(var0.d).a(var1.c.c(var0.c).b(var1.d.c(var0.b))).b(var1.e.c(var0.a)),
         false
      );
   }

   private static T[][] a(T[][] var0, double var1) {
      kO var3 = var0[0][0];
      kO var4 = var0[0][1];
      kO var5 = var0[0][2];
      kO var6 = var0[1][0];
      kO var7 = var0[1][1];
      kO var8 = var0[1][2];
      kO var9 = var0[2][0];
      kO var10 = var0[2][1];
      kO var11 = var0[2][2];
      double var12 = 0.0;
      kO[][] var16 = Ch.a(var0[0][0].c(), 3, 3);
      int var17 = 0;

      while (++var17 < 11) {
         kO var14 = var0[0][0].c(var3).a(var0[1][0].c(var6)).a(var0[2][0].c(var9));
         kO var15 = var0[0][1].c(var3).a(var0[1][1].c(var6)).a(var0[2][1].c(var9));
         kO var18 = var0[0][2].c(var3).a(var0[1][2].c(var6)).a(var0[2][2].c(var9));
         kO var19 = var0[0][0].c(var4).a(var0[1][0].c(var7)).a(var0[2][0].c(var10));
         kO var20 = var0[0][1].c(var4).a(var0[1][1].c(var7)).a(var0[2][1].c(var10));
         kO var21 = var0[0][2].c(var4).a(var0[1][2].c(var7)).a(var0[2][2].c(var10));
         kO var22 = var0[0][0].c(var5).a(var0[1][0].c(var8)).a(var0[2][0].c(var11));
         kO var23 = var0[0][1].c(var5).a(var0[1][1].c(var8)).a(var0[2][1].c(var11));
         kO var24 = var0[0][2].c(var5).a(var0[1][2].c(var8)).a(var0[2][2].c(var11));
         var16[0][0] = var3.b(var3.c(var14).a(var4.c(var15)).a(var5.c(var18)).b(var0[0][0]).c(0.5));
         var16[0][1] = var4.b(var3.c(var19).a(var4.c(var20)).a(var5.c(var21)).b(var0[0][1]).c(0.5));
         var16[0][2] = var5.b(var3.c(var22).a(var4.c(var23)).a(var5.c(var24)).b(var0[0][2]).c(0.5));
         var16[1][0] = var6.b(var6.c(var14).a(var7.c(var15)).a(var8.c(var18)).b(var0[1][0]).c(0.5));
         var16[1][1] = var7.b(var6.c(var19).a(var7.c(var20)).a(var8.c(var21)).b(var0[1][1]).c(0.5));
         var16[1][2] = var8.b(var6.c(var22).a(var7.c(var23)).a(var8.c(var24)).b(var0[1][2]).c(0.5));
         var16[2][0] = var9.b(var9.c(var14).a(var10.c(var15)).a(var11.c(var18)).b(var0[2][0]).c(0.5));
         var16[2][1] = var10.b(var9.c(var19).a(var10.c(var20)).a(var11.c(var21)).b(var0[2][1]).c(0.5));
         var16[2][2] = var11.b(var9.c(var22).a(var10.c(var23)).a(var11.c(var24)).b(var0[2][2]).c(0.5));
         double var27 = var16[0][0].d() - var0[0][0].d();
         double var29 = var16[0][1].d() - var0[0][1].d();
         double var31 = var16[0][2].d() - var0[0][2].d();
         double var33 = var16[1][0].d() - var0[1][0].d();
         double var35 = var16[1][1].d() - var0[1][1].d();
         double var37 = var16[1][2].d() - var0[1][2].d();
         double var39 = var16[2][0].d() - var0[2][0].d();
         double var41 = var16[2][1].d() - var0[2][1].d();
         double var43 = var16[2][2].d() - var0[2][2].d();
         double var45;
         if (BY.w(
               (
                     var45 = var27 * var27
                        + var29 * var29
                        + var31 * var31
                        + var33 * var33
                        + var35 * var35
                        + var37 * var37
                        + var39 * var39
                        + var41 * var41
                        + var43 * var43
                  )
                  - var12
            )
            <= var1) {
            return (T[][])var16;
         }

         var3 = var16[0][0];
         var4 = var16[0][1];
         var5 = var16[0][2];
         var6 = var16[1][0];
         var7 = var16[1][1];
         var8 = var16[1][2];
         var9 = var16[2][0];
         var10 = var16[2][1];
         var11 = var16[2][2];
         var12 = var45;
      }

      throw new rg(px.UNABLE_TO_ORTHOGONOLIZE_MATRIX, var17 - 1);
   }

   private static <T extends kO<T>> T a(rd<T> var0, rd<T> var1) {
      if ((var0 = var0.d(var1)).b.d() < -0.1 || var0.b.d() > 0.1) {
         return var0.c.c(var0.c).a(var0.d.c(var0.d)).a(var0.e.c(var0.e)).k().u().a(2);
      } else {
         return var0.b.d() < 0.0 ? var0.b.a().t().a(2) : var0.b.t().a(2);
      }
   }
}
