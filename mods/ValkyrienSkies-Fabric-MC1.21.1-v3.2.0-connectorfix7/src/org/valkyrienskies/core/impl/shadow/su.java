package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;

public abstract class su<T extends kN<T>> implements sQ<T> {
   private final kM<T> a;

   protected su() {
      this.a = null;
   }

   protected su(kM<T> var1) {
      this.a = var1;
   }

   protected su(kM<T> var1, int var2, int var3) {
      if (var2 <= 0) {
         throw new pk(px.DIMENSION, var2);
      }

      if (var3 <= 0) {
         throw new pk(px.DIMENSION, var3);
      }

      this.a = var1;
   }

   protected static <T extends kN<T>> kM<T> a(T[][] var0) {
      if (var0 == null) {
         throw new pl();
      } else if (var0.length == 0) {
         throw new pf(px.AT_LEAST_ONE_ROW);
      } else if (var0[0].length == 0) {
         throw new pf(px.AT_LEAST_ONE_COLUMN);
      } else {
         return var0[0][0].c();
      }
   }

   protected static <T extends kN<T>> kM<T> a(T[] var0) {
      if (var0.length == 0) {
         throw new pf(px.AT_LEAST_ONE_ROW);
      } else {
         return var0[0].c();
      }
   }

   @Deprecated
   private static <T extends kN<T>> T[][] a(kM<T> var0, int var1, int var2) {
      return (T[][])Ch.a(var0, var1, var2);
   }

   @Deprecated
   private static <T extends kN<T>> T[] a(kM<T> var0, int var1) {
      return (T[])Ch.a(var0, var1);
   }

   @Override
   public final kM<T> a() {
      return this.a;
   }

   @Override
   public abstract sQ<T> a(int var1, int var2);

   @Override
   public abstract sQ<T> b();

   @Override
   public sQ<T> a(sQ<T> var1) {
      this.e(var1);
      int var2 = this.f();
      int var3 = this.g();
      su var4 = this.a(var2, var3);

      for (int var5 = 0; var5 < var2; var5++) {
         for (int var6 = 0; var6 < var3; var6++) {
            var4.a(var5, var6, this.b(var5, var6).a((T)var1.b(var5, var6)));
         }
      }

      return var4;
   }

   @Override
   public sQ<T> b(sQ<T> var1) {
      this.f(var1);
      int var2 = this.f();
      int var3 = this.g();
      su var4 = this.a(var2, var3);

      for (int var5 = 0; var5 < var2; var5++) {
         for (int var6 = 0; var6 < var3; var6++) {
            var4.a(var5, var6, this.b(var5, var6).b((T)var1.b(var5, var6)));
         }
      }

      return var4;
   }

   @Override
   public sQ<T> a(T var1) {
      int var2 = this.f();
      int var3 = this.g();
      su var4 = this.a(var2, var3);

      for (int var5 = 0; var5 < var2; var5++) {
         for (int var6 = 0; var6 < var3; var6++) {
            var4.a(var5, var6, this.b(var5, var6).a((T)var1));
         }
      }

      return var4;
   }

   @Override
   public sQ<T> b(T var1) {
      int var2 = this.f();
      int var3 = this.g();
      su var4 = this.a(var2, var3);

      for (int var5 = 0; var5 < var2; var5++) {
         for (int var6 = 0; var6 < var3; var6++) {
            var4.a(var5, var6, this.b(var5, var6).c((T)var1));
         }
      }

      return var4;
   }

   @Override
   public sQ<T> c(sQ<T> var1) {
      this.g(var1);
      int var2 = this.f();
      int var3 = var1.g();
      int var4 = this.g();
      su var5 = this.a(var2, var3);

      for (int var6 = 0; var6 < var2; var6++) {
         for (int var7 = 0; var7 < var3; var7++) {
            kN var8 = this.a.a();

            for (int var9 = 0; var9 < var4; var9++) {
               var8 = var8.a(this.b(var6, var9).c((T)var1.b(var9, var7)));
            }

            var5.a(var6, var7, (T)var8);
         }
      }

      return var5;
   }

   @Override
   public final sQ<T> d(sQ<T> var1) {
      return var1.c(this);
   }

   @Override
   public final sQ<T> a(int var1) {
      if (var1 < 0) {
         throw new pj(var1);
      }

      if (!this.e()) {
         throw new th(this.f(), this.g());
      }

      if (var1 == 0) {
         return td.a(this.a, this.f());
      }

      if (var1 == 1) {
         return this.b();
      }

      char[] var8 = Integer.toBinaryString(--var1).toCharArray();
      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 < var8.length; var3++) {
         if (var8[var3] == '1') {
            int var4 = var8.length - var3 - 1;
            var2.add(var4);
         }
      }

      ArrayList var9;
      (var9 = new ArrayList(var8.length)).add(0, this.b());

      for (int var10 = 1; var10 < var8.length; var10++) {
         sQ var5;
         sQ var6 = (var5 = (sQ)var9.get(var10 - 1)).c(var5);
         var9.add(var10, var6);
      }

      sQ var11 = this.b();

      for (Integer var13 : var2) {
         var11 = var11.c((sQ<T>)var9.get(var13));
      }

      return var11;
   }

   @Override
   public T[][] c() {
      kN[][] var1 = Ch.a(this.a, this.f(), this.g());

      for (int var2 = 0; var2 < var1.length; var2++) {
         kN[] var3 = var1[var2];

         for (int var4 = 0; var4 < var3.length; var4++) {
            var3[var4] = this.b(var2, var4);
         }
      }

      return (T[][])var1;
   }

   @Override
   public sQ<T> a(int var1, int var2, int var3, int var4) {
      this.b(var1, var2, var3, var4);
      su var5 = this.a(var2 - var1 + 1, var4 - var3 + 1);

      for (int var6 = var1; var6 <= var2; var6++) {
         for (int var7 = var3; var7 <= var4; var7++) {
            var5.a(var6 - var1, var7 - var3, this.b(var6, var7));
         }
      }

      return var5;
   }

   @Override
   public final sQ<T> a(final int[] var1, final int[] var2) {
      this.b(var1, var2);
      su var3;
      (var3 = this.a(var1.length, var2.length)).a(new sH<T>(this.a.a()) {
         @Override
         public final T a(int var1x, int var2x, T var3x) {
            return (T)su.this.b(var1[var1x], var2[var2x]);
         }
      });
      return var3;
   }

   @Override
   public final void a(int var1, int var2, int var3, int var4, final T[][] var5) {
      this.b(var1, var2, var3, var4);
      int var6 = var2 + 1 - var1;
      int var7 = var4 + 1 - var3;
      if (var5.length >= var6 && var5[0].length >= var7) {
         this.a(new sI<T>(this.a.a()) {
            private int a;
            private int b;

            @Override
            public final void a(int var1, int var2x, int var3x, int var4x) {
               this.a = var3x;
               this.b = var4x;
            }

            @Override
            public final void a(int var1, int var2x, T var3x) {
               var5[var1 - this.a][var2x - this.b] = var3x;
            }
         }, var1, var2, var3, var4);
      } else {
         throw new tc(var5.length, var5[0].length, var6, var7);
      }
   }

   @Override
   public final void a(int[] var1, int[] var2, T[][] var3) {
      this.b(var1, var2);
      if (var3.length >= var1.length && var3[0].length >= var2.length) {
         for (int var4 = 0; var4 < var1.length; var4++) {
            kN[] var5 = var3[var4];

            for (int var6 = 0; var6 < var2.length; var6++) {
               var5[var6] = this.b(var1[var4], var2[var6]);
            }
         }
      } else {
         throw new tc(var3.length, var3[0].length, var1.length, var2.length);
      }
   }

   @Override
   public void a(T[][] var1, int var2, int var3) {
      if (var1 == null) {
         throw new pl();
      }

      int var4;
      if ((var4 = var1.length) == 0) {
         throw new pf(px.AT_LEAST_ONE_ROW);
      }

      int var5;
      if ((var5 = var1[0].length) == 0) {
         throw new pf(px.AT_LEAST_ONE_COLUMN);
      }

      for (int var6 = 1; var6 < var4; var6++) {
         if (var1[var6].length != var5) {
            throw new oS(var5, var1[var6].length);
         }
      }

      this.h(var2);
      this.i(var3);
      this.h(var4 + var2 - 1);
      this.i(var5 + var3 - 1);

      for (int var8 = 0; var8 < var4; var8++) {
         for (int var7 = 0; var7 < var5; var7++) {
            this.a(var2 + var8, var3 + var7, (T)var1[var8][var7]);
         }
      }
   }

   @Override
   public sQ<T> b(int var1) {
      this.h(var1);
      int var2 = this.g();
      su var3 = this.a(1, var2);

      for (int var4 = 0; var4 < var2; var4++) {
         var3.a(0, var4, this.b(var1, var4));
      }

      return var3;
   }

   @Override
   public void a(int var1, sQ<T> var2) {
      this.h(var1);
      int var3 = this.g();
      if (var2.f() == 1 && var2.g() == var3) {
         for (int var4 = 0; var4 < var3; var4++) {
            this.a(var1, var4, (T)var2.b(0, var4));
         }
      } else {
         throw new tc(var2.f(), var2.g(), 1, var3);
      }
   }

   @Override
   public sQ<T> c(int var1) {
      this.i(var1);
      int var2 = this.f();
      su var3 = this.a(var2, 1);

      for (int var4 = 0; var4 < var2; var4++) {
         var3.a(var4, 0, this.b(var4, var1));
      }

      return var3;
   }

   @Override
   public void b(int var1, sQ<T> var2) {
      this.i(var1);
      int var3 = this.f();
      if (var2.f() == var3 && var2.g() == 1) {
         for (int var4 = 0; var4 < var3; var4++) {
            this.a(var4, var1, (T)var2.b(var4, 0));
         }
      } else {
         throw new tc(var2.f(), var2.g(), var3, 1);
      }
   }

   @Override
   public sT<T> d(int var1) {
      return new sz<>(this.a, this.f(var1), false);
   }

   @Override
   public void a(int var1, sT<T> var2) {
      this.h(var1);
      int var3 = this.g();
      if (var2.g() != var3) {
         throw new tc(1, var2.g(), 1, var3);
      }

      for (int var4 = 0; var4 < var3; var4++) {
         this.a(var1, var4, (T)var2.a(var4));
      }
   }

   @Override
   public sT<T> e(int var1) {
      return new sz<>(this.a, this.g(var1), false);
   }

   @Override
   public void b(int var1, sT<T> var2) {
      this.i(var1);
      int var3 = this.f();
      if (var2.g() != var3) {
         throw new tc(var2.g(), 1, var3, 1);
      }

      for (int var4 = 0; var4 < var3; var4++) {
         this.a(var4, var1, (T)var2.a(var4));
      }
   }

   @Override
   public T[] f(int var1) {
      this.h(var1);
      int var2 = this.g();
      kN[] var3 = Ch.a(this.a, var2);

      for (int var4 = 0; var4 < var2; var4++) {
         var3[var4] = this.b(var1, var4);
      }

      return (T[])var3;
   }

   @Override
   public void a(int var1, T[] var2) {
      this.h(var1);
      int var3 = this.g();
      if (var2.length != var3) {
         throw new tc(1, var2.length, 1, var3);
      }

      for (int var4 = 0; var4 < var3; var4++) {
         this.a(var1, var4, (T)var2[var4]);
      }
   }

   @Override
   public T[] g(int var1) {
      this.i(var1);
      int var2 = this.f();
      kN[] var3 = Ch.a(this.a, var2);

      for (int var4 = 0; var4 < var2; var4++) {
         var3[var4] = this.b(var4, var1);
      }

      return (T[])var3;
   }

   @Override
   public void b(int var1, T[] var2) {
      this.i(var1);
      int var3 = this.f();
      if (var2.length != var3) {
         throw new tc(var2.length, 1, var3, 1);
      }

      for (int var4 = 0; var4 < var3; var4++) {
         this.a(var4, var1, (T)var2[var4]);
      }
   }

   @Override
   public abstract T b(int var1, int var2);

   @Override
   public abstract void a(int var1, int var2, T var3);

   @Override
   public abstract void b(int var1, int var2, T var3);

   @Override
   public abstract void c(int var1, int var2, T var3);

   @Override
   public sQ<T> d() {
      int var1 = this.f();
      int var2 = this.g();
      final su var3 = this.a(var2, var1);
      this.c(new sI<T>(this.a.a()) {
         @Override
         public final void a(int var1, int var2x, T var3x) {
            var3.a(var2x, var1, (T)var3x);
         }
      });
      return var3;
   }

   @Override
   public final boolean e() {
      return this.g() == this.f();
   }

   @Override
   public abstract int f();

   @Override
   public abstract int g();

   @Override
   public final T h() {
      int var1 = this.f();
      int var2 = this.g();
      if (var1 != var2) {
         throw new th(var1, var2);
      }

      kN var4 = this.a.a();

      for (int var3 = 0; var3 < var1; var3++) {
         var4 = var4.a(this.b(var3, var3));
      }

      return (T)var4;
   }

   @Override
   public T[] b(T[] var1) {
      int var2 = this.f();
      int var3 = this.g();
      if (var1.length != var3) {
         throw new oS(var1.length, var3);
      }

      kN[] var4 = Ch.a(this.a, var2);

      for (int var5 = 0; var5 < var2; var5++) {
         kN var6 = this.a.a();

         for (int var7 = 0; var7 < var3; var7++) {
            var6 = var6.a(this.b(var5, var7).c((T)var1[var7]));
         }

         var4[var5] = var6;
      }

      return (T[])var4;
   }

   @Override
   public final sT<T> a(sT<T> var1) {
      try {
         sz var9;
         return new sz<>(this.a, this.b((var9 = (sz)var1).a), false);
      } catch (ClassCastException var8) {
         int var2 = this.f();
         int var3 = this.g();
         if (var1.g() != var3) {
            throw new oS(var1.g(), var3);
         }

         kN[] var4 = Ch.a(this.a, var2);

         for (int var5 = 0; var5 < var2; var5++) {
            kN var6 = this.a.a();

            for (int var7 = 0; var7 < var3; var7++) {
               var6 = var6.a(this.b(var5, var7).c((T)var1.a(var7)));
            }

            var4[var5] = var6;
         }

         return new sz<>(this.a, (T[])var4, false);
      }
   }

   @Override
   public T[] c(T[] var1) {
      int var2 = this.f();
      int var3 = this.g();
      if (var1.length != var2) {
         throw new oS(var1.length, var2);
      }

      kN[] var4 = Ch.a(this.a, var3);

      for (int var5 = 0; var5 < var3; var5++) {
         kN var6 = this.a.a();

         for (int var7 = 0; var7 < var2; var7++) {
            var6 = var6.a(this.b(var7, var5).c((T)var1[var7]));
         }

         var4[var5] = var6;
      }

      return (T[])var4;
   }

   @Override
   public final sT<T> b(sT<T> var1) {
      try {
         sz var9;
         return new sz<>(this.a, this.c((var9 = (sz)var1).a), false);
      } catch (ClassCastException var8) {
         int var2 = this.f();
         int var3 = this.g();
         if (var1.g() != var2) {
            throw new oS(var1.g(), var2);
         }

         kN[] var4 = Ch.a(this.a, var3);

         for (int var5 = 0; var5 < var3; var5++) {
            kN var6 = this.a.a();

            for (int var7 = 0; var7 < var2; var7++) {
               var6 = var6.a(this.b(var7, var5).c((T)var1.a(var7)));
            }

            var4[var5] = var6;
         }

         return new sz<>(this.a, (T[])var4, false);
      }
   }

   @Override
   public T a(sR<T> var1) {
      int var2 = this.f();
      int var3 = this.g();

      for (int var4 = 0; var4 < var2; var4++) {
         for (int var5 = 0; var5 < var3; var5++) {
            kN var6 = this.b(var4, var5);
            var6 = var1.a(var4, var5, (T)var6);
            this.a(var4, var5, (T)var6);
         }
      }

      return (T)var1.b();
   }

   @Override
   public T a(sS<T> var1) {
      int var2 = this.f();
      int var3 = this.g();
      var1.a(var2, var3, 0, 0);

      for (int var4 = 0; var4 < var2; var4++) {
         for (int var5 = 0; var5 < var3; var5++) {
            var1.a(var4, var5, this.b(var4, var5));
         }
      }

      return (T)var1.a();
   }

   @Override
   public T a(sR<T> var1, int var2, int var3, int var4, int var5) {
      this.b(var2, var3, var4, var5);
      this.f();
      this.g();

      for (int var8 = var2; var8 <= var3; var8++) {
         for (int var6 = var4; var6 <= var5; var6++) {
            kN var7 = this.b(var8, var6);
            var7 = var1.a(var8, var6, (T)var7);
            this.a(var8, var6, (T)var7);
         }
      }

      return (T)var1.b();
   }

   @Override
   public T a(sS<T> var1, int var2, int var3, int var4, int var5) {
      this.b(var2, var3, var4, var5);
      var1.a(this.f(), this.g(), var2, var4);

      for (int var7 = var2; var7 <= var3; var7++) {
         for (int var6 = var4; var6 <= var5; var6++) {
            var1.a(var7, var6, this.b(var7, var6));
         }
      }

      return (T)var1.a();
   }

   @Override
   public T b(sR<T> var1) {
      int var2 = this.f();
      int var3 = this.g();

      for (int var4 = 0; var4 < var3; var4++) {
         for (int var5 = 0; var5 < var2; var5++) {
            kN var6 = this.b(var5, var4);
            var6 = var1.a(var5, var4, (T)var6);
            this.a(var5, var4, (T)var6);
         }
      }

      return (T)var1.b();
   }

   @Override
   public T b(sS<T> var1) {
      int var2 = this.f();
      int var3 = this.g();
      var1.a(var2, var3, 0, 0);

      for (int var4 = 0; var4 < var3; var4++) {
         for (int var5 = 0; var5 < var2; var5++) {
            var1.a(var5, var4, this.b(var5, var4));
         }
      }

      return (T)var1.a();
   }

   @Override
   public T b(sR<T> var1, int var2, int var3, int var4, int var5) {
      this.b(var2, var3, var4, var5);
      this.f();
      this.g();

      for (int var8 = var4; var8 <= var5; var8++) {
         for (int var6 = var2; var6 <= var3; var6++) {
            kN var7 = this.b(var6, var8);
            var7 = var1.a(var6, var8, (T)var7);
            this.a(var6, var8, (T)var7);
         }
      }

      return (T)var1.b();
   }

   @Override
   public T b(sS<T> var1, int var2, int var3, int var4, int var5) {
      this.b(var2, var3, var4, var5);
      var1.a(this.f(), this.g(), var2, var4);

      for (int var7 = var4; var7 <= var5; var7++) {
         for (int var6 = var2; var6 <= var3; var6++) {
            var1.a(var6, var7, this.b(var6, var7));
         }
      }

      return (T)var1.a();
   }

   @Override
   public T a(sR<T> var1) {
      return this.a((sR<T>)var1);
   }

   @Override
   public T c(sS<T> var1) {
      return this.a(var1);
   }

   @Override
   public T c(sR<T> var1, int var2, int var3, int var4, int var5) {
      return this.a(var1, var2, var3, var4, var5);
   }

   @Override
   public T a(sS<T> var1, int var2, int var3, int var4, int var5) {
      return this.a((sS<T>)var1, var2, var3, var4, var5);
   }

   @Override
   public String toString() {
      int var1 = this.f();
      int var2 = this.g();
      StringBuffer var3 = new StringBuffer();
      String var4;
      var4 = (var4 = this.getClass().getName()).substring(var4.lastIndexOf(46) + 1);
      var3.append(var4).append("{");

      for (int var7 = 0; var7 < var1; var7++) {
         if (var7 > 0) {
            var3.append(",");
         }

         var3.append("{");

         for (int var5 = 0; var5 < var2; var5++) {
            if (var5 > 0) {
               var3.append(",");
            }

            var3.append(this.b(var7, var5));
         }

         var3.append("}");
      }

      var3.append("}");
      return var3.toString();
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      }

      if (!(object instanceof sQ)) {
         return false;
      }

      object = (sQ)object;
      int var2 = this.f();
      int var3 = this.g();
      if (((sQ)object).g() == var3 && ((sQ)object).f() == var2) {
         for (int var4 = 0; var4 < var2; var4++) {
            for (int var5 = 0; var5 < var3; var5++) {
               if (!this.b(var4, var5).equals(((sQ)object).b(var4, var5))) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var2 = this.f();
      int var3 = this.g();
      int var1;
      var1 = (var1 = var2 + 9999422) * 31 + var3;

      for (int var4 = 0; var4 < var2; var4++) {
         for (int var5 = 0; var5 < var3; var5++) {
            var1 = var1 * 31 + (11 * (var4 + 1) + 17 * (var5 + 1)) * this.b(var4, var5).hashCode();
         }
      }

      return var1;
   }

   protected final void h(int var1) {
      if (var1 < 0 || var1 >= this.f()) {
         throw new po(px.ROW_INDEX, var1, 0, this.f() - 1);
      }
   }

   protected final void i(int var1) {
      if (var1 < 0 || var1 >= this.g()) {
         throw new po(px.COLUMN_INDEX, var1, 0, this.g() - 1);
      }
   }

   protected final void b(int var1, int var2, int var3, int var4) {
      this.h(var1);
      this.h(var2);
      if (var2 < var1) {
         throw new pn(px.INITIAL_ROW_AFTER_FINAL_ROW, var2, var1, true);
      }

      this.i(var3);
      this.i(var4);
      if (var4 < var3) {
         throw new pn(px.INITIAL_COLUMN_AFTER_FINAL_COLUMN, var4, var3, true);
      }
   }

   private void b(int[] var1, int[] var2) {
      if (var1 == null || var2 == null) {
         throw new pl();
      }

      if (var1.length != 0 && var2.length != 0) {
         int[] var6;
         int var3 = (var6 = var1).length;

         for (int var4 = 0; var4 < var3; var4++) {
            int var5 = var6[var4];
            this.h(var5);
         }

         var1 = var2;
         var3 = var2.length;

         for (int var9 = 0; var9 < var3; var9++) {
            int var10 = var1[var9];
            this.i(var10);
         }
      } else {
         throw new pf();
      }
   }

   protected final void e(sQ<T> var1) {
      if (this.f() != var1.f() || this.g() != var1.g()) {
         throw new tc(var1.f(), var1.g(), this.f(), this.g());
      }
   }

   protected final void f(sQ<T> var1) {
      if (this.f() != var1.f() || this.g() != var1.g()) {
         throw new tc(var1.f(), var1.g(), this.f(), this.g());
      }
   }

   protected final void g(sQ<T> var1) {
      if (this.g() != var1.f()) {
         throw new oS(var1.f(), this.g());
      }
   }
}
