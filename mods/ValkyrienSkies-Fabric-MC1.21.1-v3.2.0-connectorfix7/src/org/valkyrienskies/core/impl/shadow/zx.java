package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class zx extends zs implements Serializable {
   private static final long a = 7288197941165002400L;
   private static final int b = 8;
   private static final int c = 256;
   private static final int d = 128;
   private static final int e = 1020;
   private static final int f = -1640531527;
   private final int[] g = new int[256];
   private final int[] h = new int[256];
   private int i;
   private int j;
   private int k;
   private int l;
   private final int[] m = new int[8];
   private int n;
   private int o;
   private int p;

   public zx() {
      this.setSeed(System.currentTimeMillis() + System.identityHashCode(this));
   }

   private zx(long var1) {
      this.setSeed(var1);
   }

   private zx(int[] var1) {
      this.a(var1);
   }

   @Override
   public final void a(int var1) {
      this.a(new int[]{var1});
   }

   @Override
   public final void setSeed(long seed) {
      this.a(new int[]{(int)(seed >>> 32), (int)seed});
   }

   @Override
   public final void a(int[] var1) {
      if (var1 == null) {
         this.setSeed(System.currentTimeMillis() + System.identityHashCode(this));
      } else {
         int var2 = var1.length;
         int var3 = this.g.length;
         System.arraycopy(var1, 0, this.g, 0, BY.a(var2, var3));
         if (var2 < var3) {
            for (int var7 = var2; var7 < var3; var7++) {
               long var5 = this.g[var7 - var2];
               this.g[var7] = (int)(1812433253L * (var5 ^ var5 >> 30) + var7);
            }
         }

         this.e();
      }
   }

   @Override
   protected final int b(int var1) {
      if (this.i < 0) {
         this.b();
         this.i = 255;
      }

      return this.g[this.i--] >>> 32 - var1;
   }

   private void b() {
      this.o = 0;
      this.p = 128;
      this.k = this.k + ++this.l;

      while (this.o < 128) {
         this.c();
      }

      this.p = 0;

      while (this.p < 128) {
         this.c();
      }
   }

   private void c() {
      this.n = this.h[this.o];
      this.j = this.j ^ this.j << 13;
      this.j = this.j + this.h[this.p++];
      this.d();
      this.n = this.h[this.o];
      this.j = this.j ^ this.j >>> 6;
      this.j = this.j + this.h[this.p++];
      this.d();
      this.n = this.h[this.o];
      this.j = this.j ^ this.j << 2;
      this.j = this.j + this.h[this.p++];
      this.d();
      this.n = this.h[this.o];
      this.j = this.j ^ this.j >>> 16;
      this.j = this.j + this.h[this.p++];
      this.d();
   }

   private void d() {
      this.h[this.o] = this.h[(this.n & 1020) >> 2] + this.j + this.k;
      this.k = this.h[(this.h[this.o] >> 8 & 1020) >> 2] + this.n;
      this.g[this.o++] = this.k;
   }

   private void e() {
      this.j = 0;
      this.k = 0;
      this.l = 0;

      for (int var1 = 0; var1 < this.m.length; var1++) {
         this.m[var1] = -1640531527;
      }

      for (int var2 = 0; var2 < 4; var2++) {
         this.f();
      }

      for (byte var3 = 0; var3 < 256; var3 += 8) {
         this.m[0] = this.m[0] + this.g[var3];
         this.m[1] = this.m[1] + this.g[var3 + 1];
         this.m[2] = this.m[2] + this.g[var3 + 2];
         this.m[3] = this.m[3] + this.g[var3 + 3];
         this.m[4] = this.m[4] + this.g[var3 + 4];
         this.m[5] = this.m[5] + this.g[var3 + 5];
         this.m[6] = this.m[6] + this.g[var3 + 6];
         this.m[7] = this.m[7] + this.g[var3 + 7];
         this.f();
         this.c(var3);
      }

      for (byte var4 = 0; var4 < 256; var4 += 8) {
         this.m[0] = this.m[0] + this.h[var4];
         this.m[1] = this.m[1] + this.h[var4 + 1];
         this.m[2] = this.m[2] + this.h[var4 + 2];
         this.m[3] = this.m[3] + this.h[var4 + 3];
         this.m[4] = this.m[4] + this.h[var4 + 4];
         this.m[5] = this.m[5] + this.h[var4 + 5];
         this.m[6] = this.m[6] + this.h[var4 + 6];
         this.m[7] = this.m[7] + this.h[var4 + 7];
         this.f();
         this.c(var4);
      }

      this.b();
      this.i = 255;
      this.a();
   }

   private void f() {
      this.m[0] = this.m[0] ^ this.m[1] << 11;
      this.m[3] = this.m[3] + this.m[0];
      this.m[1] = this.m[1] + this.m[2];
      this.m[1] = this.m[1] ^ this.m[2] >>> 2;
      this.m[4] = this.m[4] + this.m[1];
      this.m[2] = this.m[2] + this.m[3];
      this.m[2] = this.m[2] ^ this.m[3] << 8;
      this.m[5] = this.m[5] + this.m[2];
      this.m[3] = this.m[3] + this.m[4];
      this.m[3] = this.m[3] ^ this.m[4] >>> 16;
      this.m[6] = this.m[6] + this.m[3];
      this.m[4] = this.m[4] + this.m[5];
      this.m[4] = this.m[4] ^ this.m[5] << 10;
      this.m[7] = this.m[7] + this.m[4];
      this.m[5] = this.m[5] + this.m[6];
      this.m[5] = this.m[5] ^ this.m[6] >>> 4;
      this.m[0] = this.m[0] + this.m[5];
      this.m[6] = this.m[6] + this.m[7];
      this.m[6] = this.m[6] ^ this.m[7] << 8;
      this.m[1] = this.m[1] + this.m[6];
      this.m[7] = this.m[7] + this.m[0];
      this.m[7] = this.m[7] ^ this.m[0] >>> 9;
      this.m[2] = this.m[2] + this.m[7];
      this.m[0] = this.m[0] + this.m[1];
   }

   private void c(int var1) {
      this.h[var1] = this.m[0];
      this.h[var1 + 1] = this.m[1];
      this.h[var1 + 2] = this.m[2];
      this.h[var1 + 3] = this.m[3];
      this.h[var1 + 4] = this.m[4];
      this.h[var1 + 5] = this.m[5];
      this.h[var1 + 6] = this.m[6];
      this.h[var1 + 7] = this.m[7];
   }
}
