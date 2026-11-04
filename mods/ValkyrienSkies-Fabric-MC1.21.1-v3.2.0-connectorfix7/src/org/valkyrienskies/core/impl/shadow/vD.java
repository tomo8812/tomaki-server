package org.valkyrienskies.core.impl.shadow;

final class vD<T extends kO<T>> extends wk<T> {
   private final T d;
   private final T e;
   private final T f;
   private final T g;
   private final T h;
   private final T i;
   private final T j;
   private final T k;
   private final T l;
   private final T m;
   private final T n;

   vD(kM<T> var1, boolean var2, T[][] var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uJ<T> var7, uG<T> var8) {
      super(var1, var2, (T[][])var3, var4, var5, var6, var7, var8);
      kO var9 = (kO)var1.b();
      this.d = (T)var9.c(35.0).d(384.0);
      this.e = (T)var9.c(500.0).d(1113.0);
      this.f = (T)var9.c(125.0).d(192.0);
      this.g = (T)var9.c(-2187.0).d(6784.0);
      this.h = (T)var9.c(11.0).d(84.0);
      this.i = (T)var9.c(-1.2715105075E10).d(1.1282082432E10);
      this.j = (T)var9.c(8.74874797E10).d(3.2700410799E10);
      this.k = (T)var9.c(-1.0690763975E10).d(1.880347072E9);
      this.l = (T)var9.c(7.01980252875E11).d(1.99316789632E11);
      this.m = (T)var9.c(-1.453857185E9).d(8.22651844E8);
      this.n = (T)var9.c(6.9997945E7).d(2.9380423E7);
   }

   private static vD<T> b(kM<T> var0, boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uG<T> var7) {
      return new vD<>(var0, var1, (T[][])var2, var3, var4, var5, var6, var7);
   }

   @Override
   public final uJ<T> a(T var1, T var2, T var3, T var4) {
      kO var5;
      kO var6 = (var5 = (kO)var1.c().b()).b(var2);
      kO var7 = (kO)var2.a(2);
      var5 = var5.b(var7);
      kO var8 = var2.c(((kO)var2.a(-3)).a(2.0));
      var7 = var7.c(var2.c(var7.b(3.0)).a(1.0));
      kO[] var19;
      kO[] var24;
      if (this.a() != null && var2.d() <= 0.5) {
         kO var20;
         kO var25;
         var6 = (var20 = (var4 = (var25 = var3).c(var6)).c(var2)).c(var6);
         kO var41 = (kO)((kO)((kO)((kO)var25.c(this.d)).b(var4.c(this.d.b(1.0)))).a(var20.c(this.d.a(2).b(1.0)))).a(var6.c(this.i));
         kO var42 = (kO)var1.c().a();
         kO var43 = (kO)((kO)((kO)((kO)var25.c(this.e)).b(var4.c(this.e))).a(var20.c(this.e.a(2)))).a(var6.c(this.j));
         kO var44 = (kO)((kO)((kO)((kO)var25.c(this.f)).b(var4.c(this.f))).a(var20.c(this.f.a(2)))).a(var6.c(this.k));
         kO var45 = (kO)((kO)((kO)((kO)var25.c(this.g)).b(var4.c(this.g))).a(var20.c(this.g.a(2)))).a(var6.c(this.l));
         var3 = (kO)((kO)((kO)((kO)var25.c(this.h)).b(var4.c(this.h))).a(var20.c(this.h.a(2)))).a(var6.c(this.m));
         var2 = ((kO)var6.c(this.n)).b(var20);
         var4 = this.d.b((T)var5.c(this.d.b(1.0))).a((T)var8.c(this.d.a(2).b(1.0))).a((T)var7.c(this.i));
         var6 = (kO)var1.c().a();
         kO var46 = this.e.b((T)var5.c(this.e)).a((T)var8.c(this.e.a(2))).a((T)var7.c(this.j));
         kO var47 = this.f.b((T)var5.c(this.f)).a((T)var8.c(this.f.a(2))).a((T)var7.c(this.k));
         kO var48 = this.g.b((T)var5.c(this.g)).a((T)var8.c(this.g.a(2))).a((T)var7.c(this.l));
         var5 = this.h.b((T)var5.c(this.h)).a((T)var8.c(this.h.a(2))).a((T)var7.c(this.m));
         var7 = ((kO)var7.c(this.n)).b(var8);
         var19 = this.a((T[])(new kO[]{var41, var42, var43, var44, var45, var3, var2}));
         var24 = this.c((T[])(new kO[]{var4, var6, var46, var47, var48, var5, var7}));
      } else {
         var3 = (kO)var4.a();
         kO var17;
         kO var27;
         var6 = (var17 = (var27 = var4.c(var2)).c(var2)).c(var6);
         kO var9 = (kO)((kO)((kO)((kO)var3.c(this.d)).b(var27.c(this.d.b(1.0)))).a(var17.c(this.d.a(2).b(1.0)))).a(var6.c(this.i));
         kO var10 = (kO)var1.c().a();
         kO var11 = (kO)((kO)((kO)((kO)var3.c(this.e)).b(var27.c(this.e))).a(var17.c(this.e.a(2)))).a(var6.c(this.j));
         kO var12 = (kO)((kO)((kO)((kO)var3.c(this.f)).b(var27.c(this.f))).a(var17.c(this.f.a(2)))).a(var6.c(this.k));
         kO var13 = (kO)((kO)((kO)((kO)var3.c(this.g)).b(var27.c(this.g))).a(var17.c(this.g.a(2)))).a(var6.c(this.l));
         var3 = (kO)((kO)((kO)((kO)var3.c(this.h)).b(var27.c(this.h))).a(var17.c(this.h.a(2)))).a(var6.c(this.m));
         var2 = ((kO)var6.c(this.n)).b(var17);
         var4 = this.d.b((T)var5.c(this.d.b(1.0))).a((T)var8.c(this.d.a(2).b(1.0))).a((T)var7.c(this.i));
         var6 = (kO)var1.c().a();
         kO var14 = this.e.b((T)var5.c(this.e)).a((T)var8.c(this.e.a(2))).a((T)var7.c(this.j));
         kO var15 = this.f.b((T)var5.c(this.f)).a((T)var8.c(this.f.a(2))).a((T)var7.c(this.k));
         kO var16 = this.g.b((T)var5.c(this.g)).a((T)var8.c(this.g.a(2))).a((T)var7.c(this.l));
         var5 = this.h.b((T)var5.c(this.h)).a((T)var8.c(this.h.a(2))).a((T)var7.c(this.m));
         var7 = ((kO)var7.c(this.n)).b(var8);
         var19 = this.b((T[])(new kO[]{var9, var10, var11, var12, var13, var3, var2}));
         var24 = this.c((T[])(new kO[]{var4, var6, var14, var15, var16, var5, var7}));
      }

      return new uJ<>((T)var1, (T[])var19, (T[])var24);
   }

   // $VF: synthetic method
   @Override
   protected final wk a(kM var1, boolean var2, kO[][] var3, uJ var4, uJ var5, uJ var6, uJ var7, uG var8) {
      var8 = var8;
      var7 = var7;
      var6 = var6;
      var5 = var5;
      var4 = var4;
      var3 = var3;
      var2 = var2;
      var1 = var1;
      return new vD(var1, var2, (T[][])var3, var4, var5, var6, var7, var8);
   }
}
