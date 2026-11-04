package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public final class Cm implements Serializable {
   private static byte b;
   private static byte c;
   private static byte d;
   private static final long e = -3646337053166149105L;
   private static final float f = 0.5F;
   private static final int g = 16;
   private static final int h = 2;
   private static final int i = 5;
   private int[] j;
   private double[] k;
   private byte[] l;
   private final double m;
   public int a;
   private int n;
   private transient int o;

   public Cm() {
      this(16, Double.NaN);
   }

   public Cm(double var1) {
      this(16, 0.0);
   }

   private Cm(int var1) {
      this(var1, Double.NaN);
   }

   public Cm(int var1, double var2) {
      int var5;
      int var6;
      var1 = (var5 = var1) == 0 ? 1 : (Integer.highestOneBit(var6 = (int)BY.z(var5 / 0.5F)) == var6 ? var6 : Integer.highestOneBit(var6) << 1);
      this.j = new int[var1];
      this.k = new double[var1];
      this.l = new byte[var1];
      this.m = var2;
      this.n = var1 - 1;
   }

   public Cm(Cm var1) {
      int var2 = var1.j.length;
      this.j = new int[var2];
      System.arraycopy(var1.j, 0, this.j, 0, var2);
      this.k = new double[var2];
      System.arraycopy(var1.k, 0, this.k, 0, var2);
      this.l = new byte[var2];
      System.arraycopy(var1.l, 0, this.l, 0, var2);
      this.m = var1.m;
      this.a = var1.a;
      this.n = var1.n;
      this.o = var1.o;
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

   public final double a(int var1) {
      int var2;
      int var3 = (var2 = j(var1)) & this.n;
      if (this.b(var1, var3)) {
         return this.k[var3];
      }

      if (this.l[var3] == 0) {
         return this.m;
      }

      int var4 = var3;

      for (int var5 = var2 & 2147483647; this.l[var3] != 0; var5 >>= 5) {
         var3 = (var4 = a(var5, var4)) & this.n;
         if (this.b(var1, var3)) {
            return this.k[var3];
         }
      }

      return this.m;
   }

   public final boolean b(int var1) {
      int var2;
      int var3 = (var2 = j(var1)) & this.n;
      if (this.b(var1, var3)) {
         return true;
      }

      if (this.l[var3] == 0) {
         return false;
      }

      int var4 = var3;

      for (int var5 = var2 & 2147483647; this.l[var3] != 0; var5 >>= 5) {
         var3 = (var4 = a(var5, var4)) & this.n;
         if (this.b(var1, var3)) {
            return true;
         }
      }

      return false;
   }

   public final Cm.a a() {
      return new Cm.a((byte)0);
   }

   private static int f(int var0) {
      return var0 & 2147483647;
   }

   private int g(int var1) {
      return a(this.j, this.l, var1, this.n);
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

   public final int b() {
      return this.a;
   }

   public final double c(int var1) {
      int var2;
      int var3 = (var2 = j(var1)) & this.n;
      if (this.b(var1, var3)) {
         return this.i(var3);
      }

      if (this.l[var3] == 0) {
         return this.m;
      }

      int var4 = var3;

      for (int var5 = var2 & 2147483647; this.l[var3] != 0; var5 >>= 5) {
         var3 = (var4 = a(var5, var4)) & this.n;
         if (this.b(var1, var3)) {
            return this.i(var3);
         }
      }

      return this.m;
   }

   private boolean b(int var1, int var2) {
      return (var1 != 0 || this.l[var2] == 1) && this.j[var2] == var1;
   }

   private double i(int var1) {
      this.j[var1] = 0;
      this.l[var1] = 2;
      double var2 = this.k[var1];
      this.k[var1] = this.m;
      this.a--;
      this.o++;
      return var2;
   }

   public final double a(int var1, double var2) {
      int var4 = this.g(var1);
      double var5 = this.m;
      boolean var7 = true;
      if (var4 < 0) {
         var4 = -var4 - 1;
         var5 = this.k[var4];
         var7 = false;
      }

      this.j[var4] = var1;
      this.l[var4] = 1;
      this.k[var4] = var2;
      if (var7) {
         this.a++;
         if (this.d()) {
            this.c();
         }

         this.o++;
      }

      return var5;
   }

   private void c() {
      int var1 = this.l.length;
      int[] var2 = this.j;
      double[] var3 = this.k;
      byte[] var4 = this.l;
      int var5;
      int[] var6 = new int[var5 = 2 * var1];
      double[] var7 = new double[var5];
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

      this.n = var5;
      this.j = var6;
      this.k = var7;
      this.l = var8;
   }

   private boolean d() {
      return this.a > (this.n + 1) * 0.5F;
   }

   private static int j(int var0) {
      int var1;
      return (var1 = var0 ^ var0 >>> 20 ^ var0 >>> 12) ^ var1 >>> 7 ^ var1 >>> 4;
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.o = 0;
   }

   public class a {
      private final int a = Cm.this.o;
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
         if (this.a != Cm.this.o) {
            throw new ConcurrentModificationException();
         } else if (this.b < 0) {
            throw new NoSuchElementException();
         } else {
            return Cm.this.j[this.b];
         }
      }

      public final double c() {
         if (this.a != Cm.this.o) {
            throw new ConcurrentModificationException();
         } else if (this.b < 0) {
            throw new NoSuchElementException();
         } else {
            return Cm.this.k[this.b];
         }
      }

      public final void d() {
         if (this.a != Cm.this.o) {
            throw new ConcurrentModificationException();
         }

         this.b = this.c;

         try {
            while (Cm.this.l[++this.c] != 1) {
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
