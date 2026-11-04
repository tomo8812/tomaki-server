package org.valkyrienskies.core.impl.shadow;

public final class pU {
   private pU() {
   }

   public static pW a(pY var0, tu var1, tu var2, tq var3, wK<pW.a> var4, int var5, int var6, boolean var7, qa var8) {
      pU.a var9 = new pU.a(var0, var1, var2, var4, var5, var6, var7, var8);
      return var3 != null ? a(var9, var3) : var9;
   }

   private static pW a(pY var0, tu var1, tu var2, wK<pW.a> var3, int var4, int var5) {
      return a(var0, var1, var2, null, var3, var4, var5, false, null);
   }

   private static pW a(pY var0, tu var1, tu var2, tq var3, wK<pW.a> var4, int var5, int var6) {
      var6 = var6;
      var5 = var5;
      var4 = var4;
      var2 = var2;
      var1 = var1;
      return a(a(var0, var1, var2, null, var4, var5, var6, false, null), var3);
   }

   private static pW a(kY var0, kX var1, double[] var2, double[] var3, tq var4, wK<pW.a> var5, int var6, int var7) {
      pU.b var10000 = a(var0, var1);
      sA var10001 = new sA(var2, false);
      sA var10002 = new sA(var3, false);
      int var17 = var7;
      int var15 = var6;
      wK var13 = var5;
      tq var12 = var4;
      sA var10 = var10002;
      sA var8 = var10001;
      var6 = var17;
      int var16 = var15;
      wK var14 = var13;
      sA var11 = var10;
      sA var9 = var8;
      return a(a(var10000, var9, var11, null, var14, var16, var6, false, null), var12);
   }

   private static <unrepresentable> a(pW var0, final tq var1) {
      var1 = a(var1);
      return new pS(var0) {
         @Override
         public final pW.a a(tu var1x) {
            return new pP(super.a(var1x), var1);
         }
      };
   }

   private static pW a(pW var0, tu var1) {
      return a(var0, new sM(var1.g()));
   }

   private static <unrepresentable> a(pW var0, final Cb var1) {
      return new pS(var0) {
         @Override
         public final pW.a a(tu var1x) {
            var1.c();
            return super.a(var1x);
         }
      };
   }

   public static wK<pW.a> a(final wK<wR> var0) {
      return new wK<pW.a>() {
         private boolean a(int var1, pW.a var2x, pW.a var3) {
            return var0.a(var1, new wR(var2x.e().g(), var2x.d().g(), false), new wR(var3.e().g(), var3.d().g(), false));
         }

         // $VF: synthetic method
         @Override
         public final boolean a(int var1, Object var2, Object var3) {
            pW.a var10002 = (pW.a)var2;
            pW.a var4 = var3;
            var3 = var10002;
            int var5 = var1;
            return var0.a(var5, new wR(var3.e().g(), var3.d().g(), false), new wR(var4.e().g(), var4.d().g(), false));
         }
      };
   }

   private static tq a(tq var0) {
      if (!(var0 instanceof sM)) {
         return new sN(var0).e();
      }

      int var1 = var0.f();
      sM var2 = new sM(var1);

      for (int var3 = 0; var3 < var1; var3++) {
         var2.c(var3, var3, BY.a(var0.b(var3, var3)));
      }

      return var2;
   }

   public static pU.b a(kY var0, kX var1) {
      return new pU.b(var0, var1);
   }

   static class a extends wG<pW.a> implements pW {
      private final tu a;
      private final pY b;
      private final tu c;
      private final boolean d;
      private final qa e;

      a(pY var1, tu var2, tu var3, wK<pW.a> var4, int var5, int var6, boolean var7, qa var8) {
         super(var5, var6, var4);
         this.a = var2;
         this.b = var1;
         this.c = var3;
         this.d = var7;
         this.e = var8;
         if (var7 && !(var1 instanceof qb)) {
            throw new oX(px.INVALID_IMPLEMENTATION, var1.getClass().getName());
         }
      }

      @Override
      public final int b() {
         return this.a.f();
      }

      @Override
      public final int c() {
         return this.c.f();
      }

      @Override
      public final tu a() {
         return this.c == null ? null : this.c.j();
      }

      @Override
      public final pW.a a(tu var1) {
         tu var10000;
         if (this.e == null) {
            var10000 = var1.j();
         } else {
            qa var5 = this.e;
            var1.j();
            var10000 = var5.a();
         }

         var1 = var10000;
         if (this.d) {
            return new pU.a.a((qb)this.b, this.a, var1, (byte)0);
         }

         Co var2 = this.b.a(var1);
         return new pU.a.b((tu)var2.a, (tq)var2.b, this.a, var1, (byte)0);
      }

      static class a extends pO {
         private final tu a;
         private final qb b;
         private final tu c;

         private a(qb var1, tu var2, tu var3) {
            super(var2.f());
            this.b = var1;
            this.a = var3;
            this.c = var2;
         }

         @Override
         public final tq c() {
            return this.b.b(this.a.g());
         }

         @Override
         public final tu e() {
            return this.a;
         }

         @Override
         public final tu d() {
            return this.c.k(this.b.a(this.a.g()));
         }

         // $VF: synthetic method
         a(qb var1, tu var2, tu var3, byte var4) {
            this(var1, var2, var3);
         }
      }

      static class b extends pO {
         private final tu a;
         private final tq b;
         private final tu c;

         private b(tu var1, tq var2, tu var3, tu var4) {
            super(var3.f());
            this.b = var2;
            this.a = var4;
            this.c = var3.k(var1);
         }

         @Override
         public final tq c() {
            return this.b;
         }

         @Override
         public final tu e() {
            return this.a;
         }

         @Override
         public final tu d() {
            return this.c;
         }

         // $VF: synthetic method
         b(tu var1, tq var2, tu var3, tu var4, byte var5) {
            this(var1, var2, var3, var4);
         }
      }
   }

   static class b implements qb {
      private final kY a;
      private final kX b;

      b(kY var1, kX var2) {
         this.a = var1;
         this.b = var2;
      }

      @Override
      public final Co<tu, tq> a(tu var1) {
         double[] var2 = var1.g();
         return new Co<>(this.a(var2), this.b(var2));
      }

      @Override
      public final sA a(double[] var1) {
         return new sA(this.a.a(var1), false);
      }

      @Override
      public final sy b(double[] var1) {
         return new sy(this.b.a(var1), false);
      }
   }
}
