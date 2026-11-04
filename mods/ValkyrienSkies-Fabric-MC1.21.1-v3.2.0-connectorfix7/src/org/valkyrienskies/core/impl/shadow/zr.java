package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public abstract class zr extends zs implements Serializable {
   private static final long h = -817701723016583596L;
   protected int a;
   protected final int[] b;
   protected final int[] c;
   protected final int[] d;
   protected final int[] e;
   protected final int[] f;
   protected final int[] g;

   protected zr(int var1, int var2, int var3, int var4) {
      this(var1, var2, var3, var4, null);
   }

   protected zr(int var1, int var2, int var3, int var4, int var5) {
      this(var1, var2, var3, var4, new int[]{var5});
   }

   protected zr(int var1, int var2, int var3, int var4, int[] var5) {
      var1 = (var1 + 32 - 1) / 32;
      this.b = new int[var1];
      this.a = 0;
      this.c = new int[var1];
      this.d = new int[var1];
      this.e = new int[var1];
      this.f = new int[var1];
      this.g = new int[var1];

      for (int var6 = 0; var6 < var1; var6++) {
         this.c[var6] = (var6 + var1 - 1) % var1;
         this.d[var6] = (var6 + var1 - 2) % var1;
         this.e[var6] = (var6 + var2) % var1;
         this.f[var6] = (var6 + var3) % var1;
         this.g[var6] = (var6 + var4) % var1;
      }

      this.a(var5);
   }

   protected zr(int var1, int var2, int var3, int var4, long var5) {
      this(var1, var2, var3, var4, new int[]{(int)(var5 >>> 32), (int)var5});
   }

   @Override
   public final void a(int var1) {
      this.a(new int[]{var1});
   }

   @Override
   public final void a(int[] var1) {
      if (var1 == null) {
         this.setSeed(System.currentTimeMillis() + System.identityHashCode(this));
      } else {
         System.arraycopy(var1, 0, this.b, 0, BY.a(var1.length, this.b.length));
         if (var1.length < this.b.length) {
            for (int var2 = var1.length; var2 < this.b.length; var2++) {
               long var3 = this.b[var2 - var1.length];
               this.b[var2] = (int)(1812433253L * (var3 ^ var3 >> 30) + var2);
            }
         }

         this.a = 0;
         this.a();
      }
   }

   @Override
   public void setSeed(long seed) {
      this.a(new int[]{(int)(seed >>> 32), (int)seed});
   }

   @Override
   protected abstract int b(int var1);
}
