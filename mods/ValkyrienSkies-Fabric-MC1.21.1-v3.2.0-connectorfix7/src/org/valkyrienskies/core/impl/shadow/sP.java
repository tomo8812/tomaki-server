package org.valkyrienskies.core.impl.shadow;

public final class sP<T extends kN<T>> {
   private final kM<T> a;
   private T[][] b;
   private int[] c;
   private boolean d;
   private boolean e;
   private sQ<T> f;
   private sQ<T> g;
   private sQ<T> h;

   public sP(sQ<T> var1) {
      if (!var1.e()) {
         throw new th(var1.f(), var1.g());
      }

      int var2 = var1.g();
      this.a = var1.a();
      this.b = (T[][])var1.c();
      this.c = new int[var2];
      this.f = null;
      this.g = null;
      this.h = null;
      int var8 = 0;

      while (var8 < var2) {
         this.c[var8] = var8++;
      }

      this.d = true;
      this.e = false;

      for (int var9 = 0; var9 < var2; var9++) {
         kN var10000 = this.a.a();

         for (int var4 = 0; var4 < var9; var4++) {
            kN[] var5;
            kN var3 = (var5 = this.b[var4])[var9];

            for (int var6 = 0; var6 < var4; var6++) {
               var3 = var3.b(var5[var6].c(this.b[var6][var9]));
            }

            var5[var9] = var3;
         }

         int var11 = var9;

         for (int var12 = var9; var12 < var2; var12++) {
            kN[] var15;
            kN var10 = (var15 = this.b[var12])[var9];

            for (int var7 = 0; var7 < var9; var7++) {
               var10 = var10.b(var15[var7].c(this.b[var7][var9]));
            }

            var15[var9] = var10;
            if (this.b[var11][var9].equals(this.a.a())) {
               var11++;
            }
         }

         if (var11 >= var2) {
            this.e = true;
            return;
         }

         if (var11 != var9) {
            var10000 = this.a.a();

            for (int var16 = 0; var16 < var2; var16++) {
               kN var13 = this.b[var11][var16];
               this.b[var11][var16] = this.b[var9][var16];
               this.b[var9][var16] = (T)var13;
            }

            int var17 = this.c[var11];
            this.c[var11] = this.c[var9];
            this.c[var9] = var17;
            this.d = !this.d;
         }

         kN var14 = this.b[var9][var9];

         for (int var18 = var9 + 1; var18 < var2; var18++) {
            kN[] var19;
            (var19 = this.b[var18])[var9] = var19[var9].d(var14);
         }
      }
   }

   private sQ<T> b() {
      if (this.f == null && !this.e) {
         int var1 = this.c.length;
         this.f = new sx<>(this.a, var1, var1);

         for (int var2 = 0; var2 < var1; var2++) {
            kN[] var3 = this.b[var2];

            for (int var4 = 0; var4 < var2; var4++) {
               this.f.a(var2, var4, (T)var3[var4]);
            }

            this.f.a(var2, var2, this.a.b());
         }
      }

      return this.f;
   }

   private sQ<T> c() {
      if (this.g == null && !this.e) {
         int var1 = this.c.length;
         this.g = new sx<>(this.a, var1, var1);

         for (int var2 = 0; var2 < var1; var2++) {
            kN[] var3 = this.b[var2];

            for (int var4 = var2; var4 < var1; var4++) {
               this.g.a(var2, var4, (T)var3[var4]);
            }
         }
      }

      return this.g;
   }

   private sQ<T> d() {
      if (this.h == null && !this.e) {
         int var1 = this.c.length;
         this.h = new sx<>(this.a, var1, var1);

         for (int var2 = 0; var2 < var1; var2++) {
            this.h.a(var2, this.c[var2], this.a.b());
         }
      }

      return this.h;
   }

   private int[] e() {
      return (int[])this.c.clone();
   }

   private T f() {
      if (this.e) {
         return this.a.a();
      }

      int var1 = this.c.length;
      kN var2 = this.d ? this.a.b() : this.a.a().b(this.a.b());

      for (int var3 = 0; var3 < var1; var3++) {
         var2 = var2.c(this.b[var3][var3]);
      }

      return (T)var2;
   }

   public final sO<T> a() {
      return new sP.a<>(this.a, this.b, this.c, this.e, (byte)0);
   }

   static class a<T extends kN<T>> implements sO<T> {
      private final kM<T> a;
      private final T[][] b;
      private final int[] c;
      private final boolean d;

      private a(kM<T> var1, T[][] var2, int[] var3, boolean var4) {
         this.a = var1;
         this.b = (T[][])var2;
         this.c = var3;
         this.d = var4;
      }

      @Override
      public final boolean a() {
         return !this.d;
      }

      @Override
      public final sT<T> a(sT<T> var1) {
         try {
            sz var13 = var1;
            sP.a var12 = this;
            int var18 = this.c.length;
            int var20;
            if ((var20 = var13.a.length) != var18) {
               throw new oS(var20, var18);
            }

            if (var12.d) {
               throw new tA();
            }

            kN[] var21 = Ch.a(var12.a, var18);

            for (int var6 = 0; var6 < var18; var6++) {
               int var8 = var12.c[var6];
               var21[var6] = var13.a[var8];
            }

            for (int var22 = 0; var22 < var18; var22++) {
               kN var14 = var21[var22];

               for (int var7 = var22 + 1; var7 < var18; var7++) {
                  var21[var7] = var21[var7].b(var14.c(var12.b[var7][var22]));
               }
            }

            for (int var23 = var18 - 1; var23 >= 0; var23--) {
               var21[var23] = var21[var23].d(var12.b[var23][var23]);
               kN var15 = var21[var23];

               for (int var24 = 0; var24 < var23; var24++) {
                  var21[var24] = var21[var24].b(var15.c(var12.b[var24][var23]));
               }
            }

            return new sz<>((T[])var21, false);
         } catch (ClassCastException var9) {
            int var2 = this.c.length;
            if (var1.g() != var2) {
               throw new oS(var1.g(), var2);
            }

            if (this.d) {
               throw new tA();
            }

            kN[] var3 = Ch.a(this.a, var2);

            for (int var4 = 0; var4 < var2; var4++) {
               var3[var4] = var1.a(this.c[var4]);
            }

            for (int var16 = 0; var16 < var2; var16++) {
               kN var10 = var3[var16];

               for (int var5 = var16 + 1; var5 < var2; var5++) {
                  var3[var5] = var3[var5].b(var10.c(this.b[var5][var16]));
               }
            }

            for (int var17 = var2 - 1; var17 >= 0; var17--) {
               var3[var17] = var3[var17].d(this.b[var17][var17]);
               kN var11 = var3[var17];

               for (int var19 = 0; var19 < var17; var19++) {
                  var3[var19] = var3[var19].b(var11.c(this.b[var19][var17]));
               }
            }

            return new sz<>(this.a, (T[])var3, false);
         }
      }

      private sz<T> b(sz<T> var1) {
         int var2 = this.c.length;
         int var3;
         if ((var3 = var1.a.length) != var2) {
            throw new oS(var3, var2);
         }

         if (this.d) {
            throw new tA();
         }

         kN[] var9 = Ch.a(this.a, var2);

         for (int var4 = 0; var4 < var2; var4++) {
            int var6 = this.c[var4];
            var9[var4] = var1.a[var6];
         }

         for (int var10 = 0; var10 < var2; var10++) {
            kN var7 = var9[var10];

            for (int var5 = var10 + 1; var5 < var2; var5++) {
               var9[var5] = var9[var5].b(var7.c(this.b[var5][var10]));
            }
         }

         for (int var11 = var2 - 1; var11 >= 0; var11--) {
            var9[var11] = var9[var11].d(this.b[var11][var11]);
            kN var8 = var9[var11];

            for (int var12 = 0; var12 < var11; var12++) {
               var9[var12] = var9[var12].b(var8.c(this.b[var12][var11]));
            }
         }

         return new sz<>((T[])var9, false);
      }

      @Override
      public final sQ<T> a(sQ<T> var1) {
         int var2 = this.c.length;
         if (var1.f() != var2) {
            throw new oS(var1.f(), var2);
         }

         if (this.d) {
            throw new tA();
         }

         int var3 = var1.g();
         kN[][] var4 = Ch.a(this.a, var2, var3);

         for (int var5 = 0; var5 < var2; var5++) {
            kN[] var6 = var4[var5];
            int var7 = this.c[var5];

            for (int var8 = 0; var8 < var3; var8++) {
               var6[var8] = var1.b(var7, var8);
            }
         }

         for (int var13 = 0; var13 < var2; var13++) {
            kN[] var15 = var4[var13];

            for (int var17 = var13 + 1; var17 < var2; var17++) {
               kN[] var19 = var4[var17];
               kN var10 = this.b[var17][var13];

               for (int var9 = 0; var9 < var3; var9++) {
                  var19[var9] = var19[var9].b(var15[var9].c(var10));
               }
            }
         }

         for (int var14 = var2 - 1; var14 >= 0; var14--) {
            kN[] var16 = var4[var14];
            kN var18 = this.b[var14][var14];

            for (int var20 = 0; var20 < var3; var20++) {
               var16[var20] = var16[var20].d(var18);
            }

            for (int var21 = 0; var21 < var14; var21++) {
               kN[] var11 = var4[var21];
               kN var22 = this.b[var21][var14];

               for (int var12 = 0; var12 < var3; var12++) {
                  var11[var12] = var11[var12].b(var16[var12].c(var22));
               }
            }
         }

         return new sx<>(this.a, (T[][])var4, false);
      }

      @Override
      public final sQ<T> b() {
         int var1 = this.c.length;
         kN var2 = this.a.b();
         sx var3 = new sx<>(this.a, var1, var1);

         for (int var4 = 0; var4 < var1; var4++) {
            var3.a(var4, var4, (T)var2);
         }

         return this.a(var3);
      }

      // $VF: synthetic method
      a(kM var1, kN[][] var2, int[] var3, boolean var4, byte var5) {
         this(var1, (T[][])var2, var3, var4);
      }
   }
}
