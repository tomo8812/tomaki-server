package org.valkyrienskies.core.impl.shadow;

public final class kL extends kI<String> {
   private static final long a = -7032449491269434877L;
   private static kL b;
   private static int c;
   private static final int d = 32768;

   private static int e(int var0) {
      return 32768 >>> var0;
   }

   @Override
   public final int a() {
      return 16;
   }

   private static int a(String var0) {
      return var0 != null ? var0.length() << 4 : 0;
   }

   private static int a(String var0, int var1, int var2, String var3, int var4, int var5) {
      boolean var6 = true;
      if (var1 % 16 == 0 && var4 % 16 == 0 && var2 % 16 == 0 && var5 % 16 == 0) {
         var1 /= 16;
         var4 /= 16;
         var2 = var1 + var2 / 16;
         var5 = var4 + var5 / 16;
         int var7 = Math.max(var2, var5);

         for (int var10 = 0; var10 < var7; var10++) {
            int var8 = var1 + var10;
            int var9 = var4 + var10;
            char var16;
            if (var8 >= var2) {
               var16 = 0;
            } else {
               var16 = var0.charAt(var8);
            }

            char var17;
            if (var3 != null && var9 < var5) {
               var17 = var3.charAt(var9);
            } else {
               var17 = 0;
            }

            if (var16 != var17) {
               int var11 = var16 ^ var17;
               return (var10 << 4) + Integer.numberOfLeadingZeros(var11) - 16;
            }

            if (var16 != 0) {
               var6 = false;
            }
         }

         return var6 ? -1 : -2;
      } else {
         throw new IllegalArgumentException("The offsets and lengths must be at Character boundaries");
      }
   }

   private static boolean a(String var0, int var1, int var2) {
      if (var0 != null && var1 < var2) {
         var2 = var1 / 16;
         var1 %= 16;
         char var10000 = var0.charAt(var2);
         int var3 = var1;
         return (var10000 & 32768 >>> var3) != 0;
      } else {
         return false;
      }
   }

   private static boolean a(String var0, int var1, int var2, String var3) {
      if (var1 % 16 == 0 && var2 % 16 == 0) {
         var0 = var0.substring(var1 / 16, var2 / 16);
         return var3.startsWith(var0);
      } else {
         throw new IllegalArgumentException("Cannot determine prefix outside of Character boundaries");
      }
   }

   // $VF: synthetic method
   @Override
   public final boolean a(Object var1, int var2, int var3, Object var4) {
      String var10000 = var1;
      var4 = var4;
      var3 = var3;
      var2 = var2;
      var1 = var10000;
      if (var2 % 16 == 0 && var3 % 16 == 0) {
         var1 = var1.substring(var2 / 16, var3 / 16);
         return var4.startsWith(var1);
      } else {
         throw new IllegalArgumentException("Cannot determine prefix outside of Character boundaries");
      }
   }

   // $VF: synthetic method
   @Override
   public final int a(Object var1, int var2, int var3, Object var4, int var5, int var6) {
      String var10000 = var1;
      String var10003 = var4;
      var6 = var6;
      int var19 = 0;
      var4 = var10003;
      var3 = var3;
      var2 = var2;
      var1 = var10000;
      boolean var7 = true;
      if (var2 % 16 == 0 && var19 % 16 == 0 && var3 % 16 == 0 && var6 % 16 == 0) {
         var2 /= 16;
         var19 /= 16;
         var3 = var2 + var3 / 16;
         var6 = var19 + var6 / 16;
         int var8 = Math.max(var3, var6);

         for (int var11 = 0; var11 < var8; var11++) {
            int var9 = var2 + var11;
            int var10 = var19 + var11;
            char var23;
            if (var9 >= var3) {
               var23 = 0;
            } else {
               var23 = var1.charAt(var9);
            }

            char var24;
            if (var4 != null && var10 < var6) {
               var24 = var4.charAt(var10);
            } else {
               var24 = 0;
            }

            if (var23 != var24) {
               int var13 = var23 ^ var24;
               return (var11 << 4) + Integer.numberOfLeadingZeros(var13) - 16;
            }

            if (var23 != 0) {
               var7 = false;
            }
         }

         return var7 ? -1 : -2;
      } else {
         throw new IllegalArgumentException("The offsets and lengths must be at Character boundaries");
      }
   }

   // $VF: synthetic method
   @Override
   public final boolean a(Object var1, int var2, int var3) {
      String var10000 = var1;
      var3 = var3;
      var2 = var2;
      var1 = var10000;
      if (var10000 != null && var2 < var3) {
         var3 = var2 / 16;
         var2 %= 16;
         char var10 = var1.charAt(var3);
         int var5 = var2;
         return (var10 & 32768 >>> var5) != 0;
      } else {
         return false;
      }
   }

   // $VF: synthetic method
   @Override
   public final int a(Object var1) {
      String var2;
      return (var2 = (String)var1) != null ? var2.length() << 4 : 0;
   }

   static {
      new kL();
   }
}
