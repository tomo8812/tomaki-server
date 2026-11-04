package org.valkyrienskies.core.impl.shadow;

public final class zP extends zr {
   private static final long h = 5680173464174485492L;
   private static final int i = 1024;
   private static final int j = 3;
   private static final int k = 24;
   private static final int l = 10;

   public zP() {
      super(1024, 3, 24, 10);
   }

   private zP(int var1) {
      super(1024, 3, 24, 10, var1);
   }

   private zP(int[] var1) {
      super(1024, 3, 24, 10, var1);
   }

   private zP(long var1) {
      super(1024, 3, 24, 10, var1);
   }

   @Override
   protected final int b(int var1) {
      int var2 = super.c[super.a];
      int var3 = super.b[super.a];
      int var4 = super.b[super.e[super.a]];
      int var5 = super.b[super.f[super.a]];
      int var6 = super.b[super.g[super.a]];
      int var7 = super.b[var2];
      var3 ^= var4 ^ var4 >>> 8;
      var4 = var5 ^ var5 << 19 ^ var6 ^ var6 << 14;
      var5 = var3 ^ var4;
      var3 = var7 ^ var7 << 11 ^ var3 ^ var3 << 7 ^ var4 ^ var4 << 13;
      super.b[super.a] = var5;
      super.b[var2] = var3;
      super.a = var2;
      return var3 >>> 32 - var1;
   }
}
