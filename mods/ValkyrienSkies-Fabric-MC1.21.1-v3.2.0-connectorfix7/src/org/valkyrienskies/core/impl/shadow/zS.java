package org.valkyrienskies.core.impl.shadow;

public final class zS extends zr {
   private static final long h = -3859207588353972099L;
   private static final int i = 44497;
   private static final int j = 23;
   private static final int k = 481;
   private static final int l = 229;

   public zS() {
      super(44497, 23, 481, 229);
   }

   private zS(int var1) {
      super(44497, 23, 481, 229, var1);
   }

   private zS(int[] var1) {
      super(44497, 23, 481, 229, var1);
   }

   private zS(long var1) {
      super(44497, 23, 481, 229, var1);
   }

   @Override
   protected final int b(int var1) {
      int var2 = super.c[super.a];
      int var3 = super.d[super.a];
      int var4 = super.b[super.a];
      int var5 = super.b[super.e[super.a]];
      int var6 = super.b[super.f[super.a]];
      int var7 = super.b[super.g[super.a]];
      int var8 = -32768 & super.b[var2] ^ 32767 & super.b[var3];
      var4 = var4 ^ var4 << 24 ^ var5 ^ var5 >>> 30;
      var5 = var6 ^ var6 << 10 ^ var7 << 26;
      var6 = var4 ^ var5;
      var7 = (var5 << 9 ^ var5 >>> 23) & -67108865;
      var5 = (var5 & 131072) != 0 ? var7 ^ -1221985044 : var7;
      var4 = var8 ^ var4 ^ var4 >>> 20 ^ var5 ^ var6;
      super.b[super.a] = var6;
      super.b[var2] = var4;
      super.b[var3] = super.b[var3] & -32768;
      super.a = var2;
      return var4 >>> 32 - var1;
   }
}
