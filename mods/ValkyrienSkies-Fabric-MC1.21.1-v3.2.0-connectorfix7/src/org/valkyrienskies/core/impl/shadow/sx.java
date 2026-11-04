package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class sx<T extends kN<T>> extends su<T> implements Serializable {
   private static final long b = 7260756672015356458L;
   public T[][] a;

   private sx(kM<T> var1) {
      super(var1);
   }

   public sx(kM<T> var1, int var2, int var3) {
      super(var1, var2, var3);
      this.a = (T[][])Ch.a(var1, var2, var3);
   }

   public sx(T[][] var1) {
      this(a((T[][])var1), (T[][])var1);
   }

   public sx(kM<T> var1, T[][] var2) {
      super(var1);
      var2 = var2;
      this.a((T[][])var2, 0, 0);
   }

   public sx(T[][] var1, boolean var2) {
      this(a((T[][])var1), (T[][])var1, false);
   }

   public sx(kM<T> var1, T[][] var2, boolean var3) {
      super(var1);
      if (var3) {
         var2 = var2;
         this.a((T[][])var2, 0, 0);
      } else {
         Ci.a(var2);
         int var5;
         if ((var5 = var2.length) == 0) {
            throw new pf(px.AT_LEAST_ONE_ROW);
         }

         if ((var3 = var2[0].length) == 0) {
            throw new pf(px.AT_LEAST_ONE_COLUMN);
         }

         for (int var4 = 1; var4 < var5; var4++) {
            if (var2[var4].length != var3) {
               throw new oS(var3, var2[var4].length);
            }
         }

         this.a = (T[][])var2;
      }
   }

   private sx(T[] var1) {
      kN[] var2 = var1;
      if (var1.length == 0) {
         throw new pf(px.AT_LEAST_ONE_ROW);
      }

      this(var2[0].c(), (T[])var1);
   }

   private sx(kM<T> var1, T[] var2) {
      super(var1);
      int var4 = var2.length;
      this.a = Ch.a(this.a(), var4, 1);

      for (int var3 = 0; var3 < var4; var3++) {
         this.a[var3][0] = (T)var2[var3];
      }
   }

   @Override
   public final sQ<T> a(int var1, int var2) {
      return new sx<>(this.a(), var1, var2);
   }

   @Override
   public final sQ<T> b() {
      return new sx<>(this.a(), this.j(), false);
   }

   private sx<T> b(sx<T> var1) {
      this.e(var1);
      int var2 = this.f();
      int var3 = this.g();
      kN[][] var4 = Ch.a(this.a(), var2, var3);

      for (int var5 = 0; var5 < var2; var5++) {
         kN[] var6 = this.a[var5];
         kN[] var7 = var1.a[var5];
         kN[] var8 = var4[var5];

         for (int var9 = 0; var9 < var3; var9++) {
            var8[var9] = var6[var9].a(var7[var9]);
         }
      }

      return new sx<>(this.a(), (T[][])var4, false);
   }

   private sx<T> c(sx<T> var1) {
      this.f(var1);
      int var2 = this.f();
      int var3 = this.g();
      kN[][] var4 = Ch.a(this.a(), var2, var3);

      for (int var5 = 0; var5 < var2; var5++) {
         kN[] var6 = this.a[var5];
         kN[] var7 = var1.a[var5];
         kN[] var8 = var4[var5];

         for (int var9 = 0; var9 < var3; var9++) {
            var8[var9] = var6[var9].b(var7[var9]);
         }
      }

      return new sx<>(this.a(), (T[][])var4, false);
   }

   public final sx<T> a(sx<T> var1) {
      this.g(var1);
      int var2 = this.f();
      int var3 = var1.g();
      int var4 = this.g();
      kN[][] var5 = Ch.a(this.a(), var2, var3);

      for (int var6 = 0; var6 < var2; var6++) {
         kN[] var7 = this.a[var6];
         kN[] var8 = var5[var6];

         for (int var9 = 0; var9 < var3; var9++) {
            kN var10 = this.a().a();

            for (int var11 = 0; var11 < var4; var11++) {
               var10 = var10.a(var7[var11].c(var1.a[var11][var9]));
            }

            var8[var9] = var10;
         }
      }

      return new sx<>(this.a(), (T[][])var5, false);
   }

   @Override
   public final T[][] c() {
      return this.j();
   }

   public final T[][] i() {
      return this.a;
   }

   @Override
   public final void a(T[][] var1, int var2, int var3) {
      if (this.a == null) {
         if (var2 > 0) {
            throw new oX(px.FIRST_ROWS_NOT_INITIALIZED_YET, var2);
         }

         if (var3 > 0) {
            throw new oX(px.FIRST_COLUMNS_NOT_INITIALIZED_YET, var3);
         }

         int var4;
         if ((var4 = var1.length) == 0) {
            throw new pf(px.AT_LEAST_ONE_ROW);
         }

         if ((var4 = var1[0].length) == 0) {
            throw new pf(px.AT_LEAST_ONE_COLUMN);
         }

         this.a = Ch.a(this.a(), var1.length, var4);

         for (int var5 = 0; var5 < this.a.length; var5++) {
            if (var1[var5].length != var4) {
               throw new oS(var4, var1[var5].length);
            }

            System.arraycopy(var1[var5], 0, this.a[var5 + var2], var3, var4);
         }
      } else {
         super.a((T[][])var1, var2, var3);
      }
   }

   @Override
   public final T b(int var1, int var2) {
      this.h(var1);
      this.i(var2);
      return this.a[var1][var2];
   }

   @Override
   public final void a(int var1, int var2, T var3) {
      this.h(var1);
      this.i(var2);
      this.a[var1][var2] = (T)var3;
   }

   @Override
   public final void b(int var1, int var2, T var3) {
      this.h(var1);
      this.i(var2);
      this.a[var1][var2] = this.a[var1][var2].a((T)var3);
   }

   @Override
   public final void c(int var1, int var2, T var3) {
      this.h(var1);
      this.i(var2);
      this.a[var1][var2] = this.a[var1][var2].c((T)var3);
   }

   @Override
   public final int f() {
      return this.a == null ? 0 : this.a.length;
   }

   @Override
   public final int g() {
      return this.a != null && this.a[0] != null ? this.a[0].length : 0;
   }

   @Override
   public final T[] b(T[] var1) {
      int var2 = this.f();
      int var3 = this.g();
      if (var1.length != var3) {
         throw new oS(var1.length, var3);
      }

      kN[] var4 = Ch.a(this.a(), var2);

      for (int var5 = 0; var5 < var2; var5++) {
         kN[] var6 = this.a[var5];
         kN var7 = this.a().a();

         for (int var8 = 0; var8 < var3; var8++) {
            var7 = var7.a(var6[var8].c(var1[var8]));
         }

         var4[var5] = var7;
      }

      return (T[])var4;
   }

   @Override
   public final T[] c(T[] var1) {
      int var2 = this.f();
      int var3 = this.g();
      if (var1.length != var2) {
         throw new oS(var1.length, var2);
      }

      kN[] var4 = Ch.a(this.a(), var3);

      for (int var5 = 0; var5 < var3; var5++) {
         kN var6 = this.a().a();

         for (int var7 = 0; var7 < var2; var7++) {
            var6 = var6.a(this.a[var7][var5].c((T)var1[var7]));
         }

         var4[var5] = var6;
      }

      return (T[])var4;
   }

   @Override
   public final T a(sR<T> var1) {
      int var2 = this.f();
      int var3 = this.g();

      for (int var4 = 0; var4 < var2; var4++) {
         kN[] var5 = this.a[var4];

         for (int var6 = 0; var6 < var3; var6++) {
            var5[var6] = var1.a(var4, var6, (T)var5[var6]);
         }
      }

      return (T)var1.b();
   }

   @Override
   public final T a(sS<T> var1) {
      int var2 = this.f();
      int var3 = this.g();
      var1.a(var2, var3, 0, 0);

      for (int var4 = 0; var4 < var2; var4++) {
         kN[] var5 = this.a[var4];

         for (int var6 = 0; var6 < var3; var6++) {
            var1.a(var4, var6, (T)var5[var6]);
         }
      }

      return (T)var1.a();
   }

   @Override
   public final T a(sR<T> var1, int var2, int var3, int var4, int var5) {
      this.b(var2, var3, var4, var5);
      this.f();
      this.g();

      for (int var8 = var2; var8 <= var3; var8++) {
         kN[] var6 = this.a[var8];

         for (int var7 = var4; var7 <= var5; var7++) {
            var6[var7] = var1.a(var8, var7, (T)var6[var7]);
         }
      }

      return (T)var1.b();
   }

   @Override
   public final T a(sS<T> var1, int var2, int var3, int var4, int var5) {
      this.b(var2, var3, var4, var5);
      var1.a(this.f(), this.g(), var2, var4);

      for (int var8 = var2; var8 <= var3; var8++) {
         kN[] var6 = this.a[var8];

         for (int var7 = var4; var7 <= var5; var7++) {
            var1.a(var8, var7, (T)var6[var7]);
         }
      }

      return (T)var1.a();
   }

   @Override
   public final T b(sR<T> var1) {
      int var2 = this.f();
      int var3 = this.g();

      for (int var4 = 0; var4 < var3; var4++) {
         for (int var5 = 0; var5 < var2; var5++) {
            kN[] var6;
            (var6 = this.a[var5])[var4] = var1.a(var5, var4, (T)var6[var4]);
         }
      }

      return (T)var1.b();
   }

   @Override
   public final T b(sS<T> var1) {
      int var2 = this.f();
      int var3 = this.g();
      var1.a(var2, var3, 0, 0);

      for (int var4 = 0; var4 < var3; var4++) {
         for (int var5 = 0; var5 < var2; var5++) {
            var1.a(var5, var4, this.a[var5][var4]);
         }
      }

      return (T)var1.a();
   }

   @Override
   public final T b(sR<T> var1, int var2, int var3, int var4, int var5) {
      this.b(var2, var3, var4, var5);
      this.f();
      this.g();

      for (int var8 = var4; var8 <= var5; var8++) {
         for (int var6 = var2; var6 <= var3; var6++) {
            kN[] var7;
            (var7 = this.a[var6])[var8] = var1.a(var6, var8, (T)var7[var8]);
         }
      }

      return (T)var1.b();
   }

   @Override
   public final T b(sS<T> var1, int var2, int var3, int var4, int var5) {
      this.b(var2, var3, var4, var5);
      var1.a(this.f(), this.g(), var2, var4);

      for (int var7 = var4; var7 <= var5; var7++) {
         for (int var6 = var2; var6 <= var3; var6++) {
            var1.a(var6, var7, this.a[var6][var7]);
         }
      }

      return (T)var1.a();
   }

   private T[][] j() {
      int var1 = this.f();
      kN[][] var2 = Ch.a(this.a(), var1, this.g());

      for (int var3 = 0; var3 < var1; var3++) {
         System.arraycopy(this.a[var3], 0, var2[var3], 0, this.a[var3].length);
      }

      return (T[][])var2;
   }

   private void b(T[][] var1) {
      this.a((T[][])var1, 0, 0);
   }
}
