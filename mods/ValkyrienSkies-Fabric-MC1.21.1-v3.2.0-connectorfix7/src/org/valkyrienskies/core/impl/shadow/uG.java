package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class uG<T extends kO<T>> implements Serializable {
   private static final long b = 20151114L;
   final int[] a;

   uG(uG<T> var1, int var2) {
      int var3 = var1 == null ? 0 : var1.a.length - 1;
      this.a = new int[var3 + 2];
      if (var1 == null) {
         this.a[0] = 0;
      } else {
         System.arraycopy(var1.a, 0, this.a, 0, var3 + 1);
      }

      this.a[var3 + 1] = this.a[var3] + var2;
   }

   public final int a() {
      return this.a.length - 1;
   }

   public final int b() {
      return this.a[this.a.length - 1];
   }

   public final T[] a(uI<T> var1) {
      kO[] var2 = Ch.a(var1.a.c(), this.b());
      int var3 = 0;
      this.a(0, (T[])var1.c(), (T[])var2);

      while (++var3 < this.a.length - 1) {
         this.a(var3, (T[])var1.b(var3), (T[])var2);
      }

      return (T[])var2;
   }

   public final T[] a(uJ<T> var1) {
      kO[] var2 = Ch.a(var1.a.c(), this.b());
      int var3 = 0;
      this.a(0, (T[])var1.e(), (T[])var2);

      while (++var3 < this.a.length - 1) {
         this.a(var3, (T[])var1.c(var3), (T[])var2);
      }

      return (T[])var2;
   }

   public final uJ<T> a(T var1, T[] var2, T[] var3) {
      if (var2.length != this.b()) {
         throw new oS(var2.length, this.b());
      }

      if (var3.length != this.b()) {
         throw new oS(var3.length, this.b());
      }

      int var4 = this.a.length - 1;
      int var5 = 0;
      kO[] var6 = this.a(0, (T[])var2);
      kO[] var7 = this.a(0, (T[])var3);
      if (var4 < 2) {
         return new uJ<>((T)var1, (T[])var6, (T[])var7);
      }

      kO[][] var8 = Ch.a(var1.c(), var4 - 1, -1);

      for (var10 = Ch.a(var1.c(), var4 - 1, -1); ++var5 < this.a.length - 1; var10[var5 - 1] = this.a(var5, (T[])var3)) {
         var8[var5 - 1] = this.a(var5, (T[])var2);
      }

      return new uJ<>((T)var1, (T[])var6, (T[])var7, (T[][])var8, (T[][])var10);
   }

   public final T[] a(int var1, T[] var2) {
      this.a(var1);
      int var3 = this.a[var1];
      var1 = this.a[var1 + 1];
      if (var2.length < var1) {
         throw new oS(var2.length, var1);
      }

      var1 -= var3;
      kO[] var4 = Ch.a(var2[0].c(), var1);
      System.arraycopy(var2, var3, var4, 0, var1);
      return (T[])var4;
   }

   public final void a(int var1, T[] var2, T[] var3) {
      this.a(var1);
      int var4 = this.a[var1];
      int var6;
      int var5 = (var6 = this.a[var1 + 1]) - var4;
      if (var3.length < var6) {
         throw new oS(var3.length, var6);
      }

      if (var2.length != var5) {
         throw new oS(var2.length, var5);
      }

      System.arraycopy(var2, 0, var3, var4, var5);
   }

   private void a(int var1) {
      if (var1 < 0 || var1 > this.a.length - 2) {
         throw new oV(px.ARGUMENT_OUTSIDE_DOMAIN, var1, 0, this.a.length - 2);
      }
   }
}
