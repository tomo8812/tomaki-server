package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Arrays;

public final class sz<T extends kN<T>> implements Serializable, sT<T> {
   private static final long b = 7648186910365927050L;
   T[] a;
   private final kM<T> c;

   private sz(kM<T> var1) {
      this(var1, 0);
   }

   private sz(kM<T> var1, int var2) {
      this.c = var1;
      this.a = (T[])Ch.a(var1, var2);
   }

   private sz(int var1, T var2) {
      this(var2.c(), var1);
      Arrays.fill(this.a, var2);
   }

   private sz(T[] var1) {
      Ci.a(var1);

      try {
         this.c = var1[0].c();
         this.a = (T[])((kN[])var1.clone());
      } catch (ArrayIndexOutOfBoundsException var2) {
         throw new pr(px.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
      }
   }

   private sz(kM<T> var1, T[] var2) {
      Ci.a(var2);
      this.c = var1;
      this.a = (T[])((kN[])var2.clone());
   }

   public sz(T[] var1, boolean var2) {
      Ci.a(var1);
      if (var1.length == 0) {
         throw new pr(px.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
      }

      this.c = var1[0].c();
      this.a = (T[])var1;
   }

   public sz(kM<T> var1, T[] var2, boolean var3) {
      Ci.a(var2);
      this.c = var1;
      this.a = (T[])(var3 ? (kN[])var2.clone() : var2);
   }

   private sz(T[] var1, int var2, int var3) {
      Ci.a(var1);
      if (var1.length < var2 + var3) {
         throw new pm(var2 + var3, var1.length, true);
      }

      this.c = var1[0].c();
      this.a = Ch.a(this.c, var3);
      System.arraycopy(var1, var2, this.a, 0, var3);
   }

   private sz(kM<T> var1, T[] var2, int var3, int var4) {
      Ci.a(var2);
      if (var2.length < var3 + var4) {
         throw new pm(var3 + var4, var2.length, true);
      }

      this.c = var1;
      this.a = (T[])Ch.a(var1, var4);
      System.arraycopy(var2, var3, this.a, 0, var4);
   }

   private sz(sT<T> var1) {
      Ci.a(var1);
      this.c = var1.a();
      this.a = Ch.a(this.c, var1.g());

      for (int var2 = 0; var2 < this.a.length; var2++) {
         this.a[var2] = (T)var1.a(var2);
      }
   }

   private sz(sz<T> var1) {
      Ci.a(var1);
      this.c = var1.c;
      this.a = (T[])((kN[])var1.a.clone());
   }

   private sz(sz<T> var1, boolean var2) {
      Ci.a(var1);
      this.c = var1.c;
      this.a = (T[])((kN[])var1.a.clone());
   }

   @Deprecated
   private sz(sz<T> var1, sz<T> var2) {
      this(var1, var2, (byte)0);
   }

   private sz(sT<T> var1, sT<T> var2, byte var3) {
      Ci.a(var1);
      Ci.a(var2);
      this.c = var1.a();
      kN[] var5 = (kN[])(var1 instanceof sz ? var1.a : var1.h());
      kN[] var4 = (kN[])(var2 instanceof sz ? var2.a : var2.h());
      this.a = Ch.a(this.c, var5.length + var4.length);
      System.arraycopy(var5, 0, this.a, 0, var5.length);
      System.arraycopy(var4, 0, this.a, var5.length, var4.length);
   }

   @Deprecated
   private sz(sz<T> var1, T[] var2) {
      this(var1, (T[])var2, (byte)0);
   }

   private sz(sT<T> var1, T[] var2, byte var3) {
      Ci.a(var1);
      Ci.a(var2);
      this.c = var1.a();
      kN[] var4 = (kN[])(var1 instanceof sz ? var1.a : var1.h());
      this.a = Ch.a(this.c, var4.length + var2.length);
      System.arraycopy(var4, 0, this.a, 0, var4.length);
      System.arraycopy(var2, 0, this.a, var4.length, var2.length);
   }

   @Deprecated
   private sz(T[] var1, sz<T> var2) {
      this((T[])var1, var2, (byte)0);
   }

   private sz(T[] var1, sT<T> var2, byte var3) {
      Ci.a(var1);
      Ci.a(var2);
      this.c = var2.a();
      kN[] var4 = (kN[])(var2 instanceof sz ? var2.a : var2.h());
      this.a = Ch.a(this.c, var1.length + var4.length);
      System.arraycopy(var1, 0, this.a, 0, var1.length);
      System.arraycopy(var4, 0, this.a, var1.length, var4.length);
   }

   private sz(T[] var1, T[] var2) {
      Ci.a(var1);
      Ci.a(var2);
      if (var1.length + var2.length == 0) {
         throw new pr(px.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
      }

      this.a = (T[])Ch.a(var1[0].c(), var1.length + var2.length);
      System.arraycopy(var1, 0, this.a, 0, var1.length);
      System.arraycopy(var2, 0, this.a, var1.length, var2.length);
      this.c = this.a[0].c();
   }

   private sz(kM<T> var1, T[] var2, T[] var3) {
      Ci.a(var2);
      Ci.a(var3);
      if (var2.length + var3.length == 0) {
         throw new pr(px.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
      }

      this.a = (T[])Ch.a(var1, var2.length + var3.length);
      System.arraycopy(var2, 0, this.a, 0, var2.length);
      System.arraycopy(var3, 0, this.a, var2.length, var3.length);
      this.c = var1;
   }

   @Override
   public final kM<T> a() {
      return this.c;
   }

   @Override
   public final sT<T> b() {
      return new sz<>(this, true);
   }

   @Override
   public final sT<T> a(sT<T> var1) {
      try {
         sz var8 = (sz)var1;
         sz var7 = this;
         this.b(var8.a.length);
         kN[] var4 = Ch.a(var7.c, var7.a.length);

         for (int var5 = 0; var5 < var7.a.length; var5++) {
            var4[var5] = var7.a[var5].a(var8.a[var5]);
         }

         return new sz<>(var7.c, (T[])var4, false);
      } catch (ClassCastException var6) {
         this.i(var1);
         kN[] var2 = Ch.a(this.c, this.a.length);

         for (int var3 = 0; var3 < this.a.length; var3++) {
            var2[var3] = this.a[var3].a((T)var1.a(var3));
         }

         return new sz<>(this.c, (T[])var2, false);
      }
   }

   private sz<T> a(sz<T> var1) {
      this.b(var1.a.length);
      kN[] var2 = Ch.a(this.c, this.a.length);

      for (int var3 = 0; var3 < this.a.length; var3++) {
         var2[var3] = this.a[var3].a(var1.a[var3]);
      }

      return new sz<>(this.c, (T[])var2, false);
   }

   @Override
   public final sT<T> b(sT<T> var1) {
      try {
         sz var8 = (sz)var1;
         sz var7 = this;
         this.b(var8.a.length);
         kN[] var4 = Ch.a(var7.c, var7.a.length);

         for (int var5 = 0; var5 < var7.a.length; var5++) {
            var4[var5] = var7.a[var5].b(var8.a[var5]);
         }

         return new sz<>(var7.c, (T[])var4, false);
      } catch (ClassCastException var6) {
         this.i(var1);
         kN[] var2 = Ch.a(this.c, this.a.length);

         for (int var3 = 0; var3 < this.a.length; var3++) {
            var2[var3] = this.a[var3].b((T)var1.a(var3));
         }

         return new sz<>(this.c, (T[])var2, false);
      }
   }

   private sz<T> b(sz<T> var1) {
      this.b(var1.a.length);
      kN[] var2 = Ch.a(this.c, this.a.length);

      for (int var3 = 0; var3 < this.a.length; var3++) {
         var2[var3] = this.a[var3].b(var1.a[var3]);
      }

      return new sz<>(this.c, (T[])var2, false);
   }

   @Override
   public final sT<T> a(T var1) {
      kN[] var2 = Ch.a(this.c, this.a.length);

      for (int var3 = 0; var3 < this.a.length; var3++) {
         var2[var3] = this.a[var3].a((T)var1);
      }

      return new sz<>(this.c, (T[])var2, false);
   }

   @Override
   public final sT<T> b(T var1) {
      for (int var2 = 0; var2 < this.a.length; var2++) {
         this.a[var2] = this.a[var2].a((T)var1);
      }

      return this;
   }

   @Override
   public final sT<T> c(T var1) {
      kN[] var2 = Ch.a(this.c, this.a.length);

      for (int var3 = 0; var3 < this.a.length; var3++) {
         var2[var3] = this.a[var3].b((T)var1);
      }

      return new sz<>(this.c, (T[])var2, false);
   }

   @Override
   public final sT<T> d(T var1) {
      for (int var2 = 0; var2 < this.a.length; var2++) {
         this.a[var2] = this.a[var2].b((T)var1);
      }

      return this;
   }

   @Override
   public final sT<T> e(T var1) {
      kN[] var2 = Ch.a(this.c, this.a.length);

      for (int var3 = 0; var3 < this.a.length; var3++) {
         var2[var3] = this.a[var3].c((T)var1);
      }

      return new sz<>(this.c, (T[])var2, false);
   }

   @Override
   public final sT<T> f(T var1) {
      for (int var2 = 0; var2 < this.a.length; var2++) {
         this.a[var2] = this.a[var2].c((T)var1);
      }

      return this;
   }

   @Override
   public final sT<T> g(T var1) {
      Ci.a(var1);
      kN[] var2 = Ch.a(this.c, this.a.length);

      for (int var3 = 0; var3 < this.a.length; var3++) {
         var2[var3] = this.a[var3].d((T)var1);
      }

      return new sz<>(this.c, (T[])var2, false);
   }

   @Override
   public final sT<T> h(T var1) {
      Ci.a(var1);

      for (int var2 = 0; var2 < this.a.length; var2++) {
         this.a[var2] = this.a[var2].d((T)var1);
      }

      return this;
   }

   @Override
   public final sT<T> c() {
      kN[] var1 = Ch.a(this.c, this.a.length);
      kN var2 = this.c.b();

      for (int var3 = 0; var3 < this.a.length; var3++) {
         try {
            var1[var3] = var2.d(this.a[var3]);
         } catch (oU var4) {
            throw new oU(px.INDEX, var3);
         }
      }

      return new sz<>(this.c, (T[])var1, false);
   }

   @Override
   public final sT<T> d() {
      kN var1 = this.c.b();

      for (int var2 = 0; var2 < this.a.length; var2++) {
         try {
            this.a[var2] = (T)var1.d(this.a[var2]);
         } catch (oU var3) {
            throw new oU(px.INDEX, var2);
         }
      }

      return this;
   }

   @Override
   public final sT<T> c(sT<T> var1) {
      try {
         sz var8 = (sz)var1;
         sz var7 = this;
         this.b(var8.a.length);
         kN[] var4 = Ch.a(var7.c, var7.a.length);

         for (int var5 = 0; var5 < var7.a.length; var5++) {
            var4[var5] = var7.a[var5].c(var8.a[var5]);
         }

         return new sz<>(var7.c, (T[])var4, false);
      } catch (ClassCastException var6) {
         this.i(var1);
         kN[] var2 = Ch.a(this.c, this.a.length);

         for (int var3 = 0; var3 < this.a.length; var3++) {
            var2[var3] = this.a[var3].c((T)var1.a(var3));
         }

         return new sz<>(this.c, (T[])var2, false);
      }
   }

   private sz<T> c(sz<T> var1) {
      this.b(var1.a.length);
      kN[] var2 = Ch.a(this.c, this.a.length);

      for (int var3 = 0; var3 < this.a.length; var3++) {
         var2[var3] = this.a[var3].c(var1.a[var3]);
      }

      return new sz<>(this.c, (T[])var2, false);
   }

   @Override
   public final sT<T> d(sT<T> var1) {
      try {
         return this.d((sz<T>)var1);
      } catch (ClassCastException var5) {
         this.i(var1);
         kN[] var2 = Ch.a(this.c, this.a.length);

         for (int var3 = 0; var3 < this.a.length; var3++) {
            try {
               var2[var3] = this.a[var3].d((T)var1.a(var3));
            } catch (oU var4) {
               throw new oU(px.INDEX, var3);
            }
         }

         return new sz<>(this.c, (T[])var2, false);
      }
   }

   private sz<T> d(sz<T> var1) {
      this.b(var1.a.length);
      kN[] var2 = Ch.a(this.c, this.a.length);

      for (int var3 = 0; var3 < this.a.length; var3++) {
         try {
            var2[var3] = this.a[var3].d(var1.a[var3]);
         } catch (oU var4) {
            throw new oU(px.INDEX, var3);
         }
      }

      return new sz<>(this.c, (T[])var2, false);
   }

   @Override
   public final T[] e() {
      return (T[])((kN[])this.a.clone());
   }

   public final T[] f() {
      return this.a;
   }

   @Override
   public final T e(sT<T> var1) {
      try {
         return this.e((sz<T>)var1);
      } catch (ClassCastException var4) {
         this.i(var1);
         kN var2 = this.c.a();

         for (int var3 = 0; var3 < this.a.length; var3++) {
            var2 = var2.a(this.a[var3].c((T)var1.a(var3)));
         }

         return (T)var2;
      }
   }

   private T e(sz<T> var1) {
      this.b(var1.a.length);
      kN var2 = this.c.a();

      for (int var3 = 0; var3 < this.a.length; var3++) {
         var2 = var2.a(this.a[var3].c(var1.a[var3]));
      }

      return (T)var2;
   }

   @Override
   public final sT<T> f(sT<T> var1) {
      return var1.e(this.e(var1).d((T)var1.e(var1)));
   }

   private sz<T> f(sz<T> var1) {
      return (sz<T>)var1.e(this.e(var1).d((T)var1.e(var1)));
   }

   @Override
   public final sQ<T> g(sT<T> var1) {
      try {
         sz var11 = (sz)var1;
         sz var10 = this;
         int var12 = this.a.length;
         int var13 = var11.a.length;
         sx var14 = new sx<>(var10.c, var12, var13);

         for (int var7 = 0; var7 < var12; var7++) {
            for (int var8 = 0; var8 < var13; var8++) {
               var14.a(var7, var8, var10.a[var7].c(var11.a[var8]));
            }
         }

         return var14;
      } catch (ClassCastException var9) {
         int var2 = this.a.length;
         int var3 = var1.g();
         sx var4 = new sx<>(this.c, var2, var3);

         for (int var5 = 0; var5 < var2; var5++) {
            for (int var6 = 0; var6 < var3; var6++) {
               var4.a(var5, var6, this.a[var5].c((T)var1.a(var6)));
            }
         }

         return var4;
      }
   }

   private sQ<T> g(sz<T> var1) {
      int var2 = this.a.length;
      int var3 = var1.a.length;
      sx var4 = new sx<>(this.c, var2, var3);

      for (int var5 = 0; var5 < var2; var5++) {
         for (int var6 = 0; var6 < var3; var6++) {
            var4.a(var5, var6, this.a[var5].c(var1.a[var6]));
         }
      }

      return var4;
   }

   @Override
   public final T a(int var1) {
      return this.a[var1];
   }

   @Override
   public final int g() {
      return this.a.length;
   }

   @Override
   public final sT<T> h(sT<T> var1) {
      try {
         sz var3 = (sz)var1;
         sz var2 = this;
         return new sz<>(var2, var3);
      } catch (ClassCastException var4) {
         return new sz<>(this, new sz<>(var1));
      }
   }

   private sz<T> h(sz<T> var1) {
      return new sz<>(this, var1);
   }

   @Override
   public final sT<T> i(T var1) {
      kN[] var2 = Ch.a(this.c, this.a.length + 1);
      System.arraycopy(this.a, 0, var2, 0, this.a.length);
      var2[this.a.length] = var1;
      return new sz<>(this.c, (T[])var2, false);
   }

   @Override
   public final sT<T> a(int var1, int var2) {
      if (var2 < 0) {
         throw new pj(px.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, var2);
      }

      sz var3 = new sz<>(this.c, var2);

      try {
         System.arraycopy(this.a, var1, var3.a, 0, var2);
      } catch (IndexOutOfBoundsException var4) {
         this.c(var1);
         this.c(var1 + var2 - 1);
      }

      return var3;
   }

   @Override
   public final void a(int var1, T var2) {
      try {
         this.a[var1] = (T)var2;
      } catch (IndexOutOfBoundsException var3) {
         this.c(var1);
      }
   }

   @Override
   public final void a(int var1, sT<T> var2) {
      try {
         try {
            sz var5 = (sz)var2;
            int var4 = var1;
            sz var9 = this;

            try {
               System.arraycopy(var5.a, 0, var9.a, var4, var5.a.length);
            } catch (IndexOutOfBoundsException var6) {
               var9.c(var4);
               var9.c(var4 + var5.a.length - 1);
               return;
            }
         } catch (ClassCastException var7) {
            for (int var3 = var1; var3 < var1 + var2.g(); var3++) {
               this.a[var3] = (T)var2.a(var3 - var1);
            }
         }
      } catch (IndexOutOfBoundsException var8) {
         this.c(var1);
         this.c(var1 + var2.g() - 1);
      }
   }

   private void a(int var1, sz<T> var2) {
      try {
         System.arraycopy(var2.a, 0, this.a, var1, var2.a.length);
      } catch (IndexOutOfBoundsException var3) {
         this.c(var1);
         this.c(var1 + var2.a.length - 1);
      }
   }

   @Override
   public final void j(T var1) {
      Arrays.fill(this.a, var1);
   }

   @Override
   public final T[] h() {
      return (T[])((kN[])this.a.clone());
   }

   private void i(sT<T> var1) {
      this.b(var1.g());
   }

   private void b(int var1) {
      if (this.a.length != var1) {
         throw new oS(this.a.length, var1);
      }
   }

   private T a(sV<T> var1) {
      int var2 = this.a.length;

      for (int var3 = 0; var3 < var2; var3++) {
         int var5 = var3;
         kN var10000 = this.a[var5];
      }

      return (T)var1.c();
   }

   private T a(sV<T> var1, int var2, int var3) {
      this.b(var2, var3);
      int var10000 = this.a.length;

      for (int var6 = var2; var6 <= var3; var6++) {
         int var5 = var6;
         kN var7 = this.a[var5];
      }

      return (T)var1.c();
   }

   private T b(sV<T> var1) {
      sV var2 = var1;
      sz var7 = this;
      int var3 = this.a.length;

      for (int var4 = 0; var4 < var3; var4++) {
         int var6 = var4;
         kN var10000 = var7.a[var6];
      }

      return (T)var2.c();
   }

   private T b(sV<T> var1, int var2, int var3) {
      int var4 = var3;
      var3 = var2;
      sV var8 = var1;
      sz var7 = this;
      this.b(var3, var4);
      int var10000 = var7.a.length;

      for (int var10 = var3; var10 <= var4; var10++) {
         int var6 = var10;
         kN var11 = var7.a[var6];
      }

      return (T)var8.c();
   }

   private T a(sU<T> var1) {
      int var2 = this.a.length;

      for (int var3 = 0; var3 < var2; var3++) {
         int var5 = var3;
         kN var10003 = this.a[var5];
         this.a(var3, (T)var1.b());
      }

      return (T)var1.c();
   }

   private T a(sU<T> var1, int var2, int var3) {
      this.b(var2, var3);
      int var10000 = this.a.length;

      for (int var6 = var2; var6 <= var3; var6++) {
         int var5 = var6;
         kN var10003 = this.a[var5];
         this.a(var6, (T)var1.b());
      }

      return (T)var1.c();
   }

   private T b(sU<T> var1) {
      return this.a(var1);
   }

   private T b(sU<T> var1, int var2, int var3) {
      return this.a(var1, var2, var3);
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (other == null) {
         return false;
      }

      try {
         other = (sT)other;
         if (this.a.length != ((sT)other).g()) {
            return false;
         }
      } catch (ClassCastException var5) {
         return false;
      }

      int var2;
      try {
         var2 = 0;
      } catch (ClassCastException var3) {
         return false;
      }

      while (true) {
         try {
            if (var2 >= this.a.length) {
               return true;
            }

            if (!this.a[var2].equals(((sT)other).a(var2))) {
               return false;
            }
         } catch (ClassCastException var4) {
            return false;
         }

         var2++;
      }
   }

   @Override
   public final int hashCode() {
      int var1 = 3542;
      kN[] var2 = this.a;
      int var3 = this.a.length;

      for (int var4 = 0; var4 < var3; var4++) {
         kN var5 = var2[var4];
         var1 ^= var5.hashCode();
      }

      return var1;
   }

   private void c(int var1) {
      if (var1 < 0 || var1 >= this.a.length) {
         throw new po(px.INDEX, var1, 0, this.a.length - 1);
      }
   }

   private void b(int var1, int var2) {
      int var3 = this.a.length;
      if (var1 < 0 || var1 >= var3) {
         throw new po(px.INDEX, var1, 0, var3 - 1);
      }

      if (var2 < 0 || var2 >= var3) {
         throw new po(px.INDEX, var2, 0, var3 - 1);
      }

      if (var2 < var1) {
         throw new pn(px.INITIAL_ROW_AFTER_FINAL_ROW, var2, var1, false);
      }
   }
}
