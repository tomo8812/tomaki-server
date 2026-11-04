package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public final class Cn<T extends kN<T>> implements Serializable {
   private static byte a;
   private static byte b;
   private static byte c;
   private static final long d = -9179080286849120720L;
   private static final float e = 0.5F;
   private static final int f = 16;
   private static final int g = 2;
   private static final int h = 5;
   private final kM<T> i;
   private int[] j;
   private T[] k;
   private byte[] l;
   private final T m;
   private int n;
   private int o;
   private transient int p;

   public Cn(kM<T> var1) {
      this(var1, 16, (T)var1.a());
   }

   private Cn(kM<T> var1, T var2) {
      this(var1, 16, (T)var2);
   }

   public Cn(kM<T> var1, int var2) {
      this(var1, var2, (T)var1.a());
   }

   private Cn(kM<T> var1, int var2, T var3) {
      this.i = var1;
      int var4 = var2;
      int var5;
      int var6 = var2 == 0 ? 1 : (Integer.highestOneBit(var5 = (int)BY.z(var4 / 0.5F)) == var5 ? var5 : Integer.highestOneBit(var5) << 1);
      this.j = new int[var6];
      this.k = this.k(var6);
      this.l = new byte[var6];
      this.m = (T)var3;
      this.o = var6 - 1;
   }

   public Cn(Cn<T> var1) {
      this.i = var1.i;
      int var2 = var1.j.length;
      this.j = new int[var2];
      System.arraycopy(var1.j, 0, this.j, 0, var2);
      this.k = this.k(var2);
      System.arraycopy(var1.k, 0, this.k, 0, var2);
      this.l = new byte[var2];
      System.arraycopy(var1.l, 0, this.l, 0, var2);
      this.m = var1.m;
      this.n = var1.n;
      this.o = var1.o;
      this.p = var1.p;
   }

   private static int d(int var0) {
      if (var0 == 0) {
         return 1;
      }

      int var2;
      return Integer.highestOneBit(var2 = (int)BY.z(var0 / 0.5F)) == var2 ? var2 : Integer.highestOneBit(var2) << 1;
   }

   private static int e(int var0) {
      return Integer.highestOneBit(var0) << 1;
   }

   public final T a(int var1) {
      int var2;
      int var3 = (var2 = j(var1)) & this.o;
      if (this.b(var1, var3)) {
         return this.k[var3];
      }

      if (this.l[var3] == 0) {
         return this.m;
      }

      int var4 = var3;

      for (int var5 = var2 & 2147483647; this.l[var3] != 0; var5 >>= 5) {
         var3 = (var4 = a(var5, var4)) & this.o;
         if (this.b(var1, var3)) {
            return this.k[var3];
         }
      }

      return this.m;
   }

   public final boolean b(int var1) {
      int var2;
      int var3 = (var2 = j(var1)) & this.o;
      if (this.b(var1, var3)) {
         return true;
      }

      if (this.l[var3] == 0) {
         return false;
      }

      int var4 = var3;

      for (int var5 = var2 & 2147483647; this.l[var3] != 0; var5 >>= 5) {
         var3 = (var4 = a(var5, var4)) & this.o;
         if (this.b(var1, var3)) {
            return true;
         }
      }

      return false;
   }

   public final Cn<T>.a a() {
      return new Cn.a((byte)0);
   }

   private static int f(int var0) {
      return var0 & 2147483647;
   }

   private int g(int var1) {
      return a(this.j, this.l, var1, this.o);
   }

   private static int a(int[] var0, byte[] var1, int var2, int var3) {
      int var4;
      int var5 = (var4 = j(var2)) & var3;
      if (var1[var5] == 0) {
         return var5;
      }

      if (var1[var5] == 1 && var0[var5] == var2) {
         return -var5 - 1;
      }

      var4 &= Integer.MAX_VALUE;
      int var6 = var5;
      if (var1[var5] == 1) {
         do {
            var5 = (var6 = a(var4, var6)) & var3;
            var4 >>= 5;
         } while (var1[var5] == 1 && var0[var5] != var2);
      }

      if (var1[var5] == 0) {
         return var5;
      }

      if (var1[var5] == 1) {
         return -var5 - 1;
      }

      int var7 = var5;

      while (true) {
         var5 = (var6 = a(var4, var6)) & var3;
         if (var1[var5] == 0) {
            return var7;
         }

         if (var1[var5] == 1 && var0[var5] == var2) {
            return -var5 - 1;
         }

         var4 >>= 5;
      }
   }

   private static int a(int var0, int var1) {
      return (var1 << 2) + var1 + var0 + 1;
   }

   private static int h(int var0) {
      return -var0 - 1;
   }

   private int b() {
      return this.n;
   }

   public final T c(int var1) {
      int var2;
      int var3 = (var2 = j(var1)) & this.o;
      if (this.b(var1, var3)) {
         return this.i(var3);
      }

      if (this.l[var3] == 0) {
         return this.m;
      }

      int var4 = var3;

      for (int var5 = var2 & 2147483647; this.l[var3] != 0; var5 >>= 5) {
         var3 = (var4 = a(var5, var4)) & this.o;
         if (this.b(var1, var3)) {
            return this.i(var3);
         }
      }

      return this.m;
   }

   private boolean b(int var1, int var2) {
      return (var1 != 0 || this.l[var2] == 1) && this.j[var2] == var1;
   }

   private T i(int var1) {
      this.j[var1] = 0;
      this.l[var1] = 2;
      kN var2 = this.k[var1];
      this.k[var1] = this.m;
      this.n--;
      this.p++;
      return (T)var2;
   }

   public final T a(int var1, T var2) {
      int var3 = this.g(var1);
      kN var4 = this.m;
      boolean var5 = true;
      if (var3 < 0) {
         var3 = -var3 - 1;
         var4 = this.k[var3];
         var5 = false;
      }

      this.j[var3] = var1;
      this.l[var3] = 1;
      this.k[var3] = (T)var2;
      if (var5) {
         this.n++;
         if (this.d()) {
            this.c();
         }

         this.p++;
      }

      return (T)var4;
   }

   private void c() {
      int var1 = this.l.length;
      int[] var2 = this.j;
      kN[] var3 = this.k;
      byte[] var4 = this.l;
      int var5;
      int[] var6 = new int[var5 = 2 * var1];
      kN[] var7 = this.k(var5);
      byte[] var8 = new byte[var5];
      var5--;

      for (int var9 = 0; var9 < var1; var9++) {
         if (var4[var9] == 1) {
            int var10 = var2[var9];
            int var11 = a(var6, var8, var10, var5);
            var6[var11] = var10;
            var7[var11] = var3[var9];
            var8[var11] = 1;
         }
      }

      this.o = var5;
      this.j = var6;
      this.k = (T[])var7;
      this.l = var8;
   }

   private boolean d() {
      return this.n > (this.o + 1) * 0.5F;
   }

   private static int j(int var0) {
      int var1;
      return (var1 = var0 ^ var0 >>> 20 ^ var0 >>> 12) ^ var1 >>> 7 ^ var1 >>> 4;
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.p = 0;
   }

   private T[] k(int var1) {
      return (T[])((kN[])Array.newInstance(this.i.c(), var1));
   }

   public class a {
      private final int a = Cn.this.p;
      private int b;
      private int c = -1;

      private a() {
         try {
            this.d();
         } catch (NoSuchElementException var2) {
         }
      }

      public final boolean a() {
         return this.c >= 0;
      }

      public final int b() {
         if (this.a != Cn.this.p) {
            throw new ConcurrentModificationException();
         } else if (this.b < 0) {
            throw new NoSuchElementException();
         } else {
            return Cn.this.j[this.b];
         }
      }

      public final T c() {
         if (this.a != Cn.this.p) {
            throw new ConcurrentModificationException();
         } else if (this.b < 0) {
            throw new NoSuchElementException();
         } else {
            return Cn.this.k[this.b];
         }
      }

      public final void d() {
         if (this.a != Cn.this.p) {
            throw new ConcurrentModificationException();
         }

         this.b = this.c;

         try {
            while (Cn.this.l[++this.c] != 1) {
            }
         } catch (ArrayIndexOutOfBoundsException var1) {
            this.c = -2;
            if (this.b < 0) {
               throw new NoSuchElementException();
            }
         }
      }

      // $VF: synthetic method
      a(byte var2) {
         this();
      }
   }
}
