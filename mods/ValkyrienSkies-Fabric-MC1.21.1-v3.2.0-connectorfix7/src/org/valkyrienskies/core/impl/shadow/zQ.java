package org.valkyrienskies.core.impl.shadow;

public final class zQ extends zr {
   private static final long h = -7462102162223815419L;
   private static final int i = 19937;
   private static final int j = 70;
   private static final int k = 179;
   private static final int l = 449;

   public zQ() {
      super(19937, 70, 179, 449);
   }

   private zQ(int var1) {
      super(19937, 70, 179, 449, var1);
   }

   private zQ(int[] var1) {
      super(19937, 70, 179, 449, var1);
   }

   private zQ(long var1) {
      super(19937, 70, 179, 449, var1);
   }

   @Override
   protected final int b(int var1) {
      int var2 = super.c[super.a];
      int var3 = super.d[super.a];
      int var4 = super.b[super.a];
      int var5 = super.b[super.e[super.a]];
      int var6 = super.b[super.f[super.a]];
      int var7 = super.b[super.g[super.a]];
      int var8 = -2147483648 & super.b[var2] ^ 2147483647 & super.b[var3];
      var4 = var4 ^ var4 << 25 ^ var5 ^ var5 >>> 27;
      var5 = var6 >>> 9 ^ var7 ^ var7 >>> 1;
      var6 = var4 ^ var5;
      var4 = var8 ^ var4 ^ var4 << 9 ^ var5 ^ var5 << 21 ^ var6 ^ var6 >>> 21;
      super.b[super.a] = var6;
      super.b[var2] = var4;
      super.b[var3] = super.b[var3] & -2147483648;
      super.a = var2;
      return var4 >>> 32 - var1;
   }
}
