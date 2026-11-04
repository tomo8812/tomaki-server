package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class zz extends zs implements Serializable {
   private static final long a = 8661194735290153518L;
   private static final int b = 624;
   private static final int c = 397;
   private static final int[] d = new int[]{0, -1727483681};
   private int[] e = new int[624];
   private int f;

   public zz() {
      this.setSeed(System.currentTimeMillis() + System.identityHashCode(this));
   }

   private zz(int var1) {
      this.a(var1);
   }

   private zz(int[] var1) {
      this.a(var1);
   }

   private zz(long var1) {
      this.setSeed(var1);
   }

   @Override
   public final void a(int var1) {
      long var2 = var1;
      this.e[0] = (int)var2;

      for (this.f = 1; this.f < 624; this.f++) {
         var2 = 1812433253L * (var2 ^ var2 >> 30) + this.f & 4294967295L;
         this.e[this.f] = (int)var2;
      }

      this.a();
   }

   @Override
   public final void a(int[] var1) {
      if (var1 == null) {
         this.setSeed(System.currentTimeMillis() + System.identityHashCode(this));
      } else {
         this.a(19650218);
         int var2 = 1;
         int var3 = 0;

         for (int var4 = BY.b(624, var1.length); var4 != 0; var4--) {
            long var5 = this.e[var2] & 2147483647L | (this.e[var2] < 0 ? 2147483648L : 0L);
            long var7 = this.e[var2 - 1] & 2147483647L | (this.e[var2 - 1] < 0 ? 2147483648L : 0L);
            long var9 = (var5 ^ (var7 ^ var7 >> 30) * 1664525L) + var1[var3] + var3;
            this.e[var2] = (int)var9;
            var2++;
            var3++;
            if (var2 >= 624) {
               this.e[0] = this.e[623];
               var2 = 1;
            }

            if (var3 >= var1.length) {
               var3 = 0;
            }
         }

         for (int var11 = 623; var11 != 0; var11--) {
            long var12 = this.e[var2] & 2147483647L | (this.e[var2] < 0 ? 2147483648L : 0L);
            long var13 = this.e[var2 - 1] & 2147483647L | (this.e[var2 - 1] < 0 ? 2147483648L : 0L);
            long var14 = (var12 ^ (var13 ^ var13 >> 30) * 1566083941L) - var2;
            this.e[var2] = (int)var14;
            if (++var2 >= 624) {
               this.e[0] = this.e[623];
               var2 = 1;
            }
         }

         this.e[0] = Integer.MIN_VALUE;
         this.a();
      }
   }

   @Override
   public final void setSeed(long seed) {
      this.a(new int[]{(int)(seed >>> 32), (int)seed});
   }

   @Override
   protected final int b(int var1) {
      if (this.f >= 624) {
         int var3 = this.e[0];

         for (int var4 = 0; var4 < 227; var4++) {
            int var2 = var3;
            var3 = this.e[var4 + 1];
            var2 = var2 & -2147483648 | var3 & 2147483647;
            this.e[var4] = this.e[var4 + 397] ^ var2 >>> 1 ^ d[var2 & 1];
         }

         for (int var13 = 227; var13 < 623; var13++) {
            int var6 = var3;
            var3 = this.e[var13 + 1];
            var6 = var6 & -2147483648 | var3 & 2147483647;
            this.e[var13] = this.e[var13 + -227] ^ var6 >>> 1 ^ d[var6 & 1];
         }

         int var8 = var3 & -2147483648 | this.e[0] & 2147483647;
         this.e[623] = this.e[396] ^ var8 >>> 1 ^ d[var8 & 1];
         this.f = 0;
      }

      int var9;
      int var10;
      int var11;
      int var12;
      return ((var12 = (var11 = (var10 = (var9 = this.e[this.f++]) ^ var9 >>> 11) ^ var10 << 7 & -1658038656) ^ var11 << 15 & -272236544) ^ var12 >>> 18)
         >>> 32 - var1;
   }
}
