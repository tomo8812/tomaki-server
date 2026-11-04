package org.valkyrienskies.core.impl.shadow;

public final class zU extends zr {
   private static final long h = -6104179812103820574L;
   private static final int i = 512;
   private static final int j = 13;
   private static final int k = 9;
   private static final int l = 5;

   public zU() {
      super(512, 13, 9, 5);
   }

   private zU(int var1) {
      super(512, 13, 9, 5, var1);
   }

   private zU(int[] var1) {
      super(512, 13, 9, 5, var1);
   }

   private zU(long var1) {
      super(512, 13, 9, 5, var1);
   }

   @Override
   protected final int b(int var1) {
      int var2 = super.c[super.a];
      int var3 = super.b[super.a];
      int var4 = super.b[super.e[super.a]];
      int var5 = super.b[super.f[super.a]];
      int var6 = super.b[var2];
      var3 = var3 ^ var3 << 16 ^ var4 ^ var4 << 15;
      var4 = var5 ^ var5 >>> 11;
      var5 = var3 ^ var4;
      var3 = var6 ^ var6 << 2 ^ var3 ^ var3 << 18 ^ var4 << 28 ^ var5 ^ var5 << 5 & -633066204;
      super.b[super.a] = var5;
      super.b[var2] = var3;
      super.a = var2;
      return var3 >>> 32 - var1;
   }
}
