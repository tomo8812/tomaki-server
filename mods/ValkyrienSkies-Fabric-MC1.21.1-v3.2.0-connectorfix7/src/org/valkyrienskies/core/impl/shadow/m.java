package org.valkyrienskies.core.impl.shadow;

import java.time.DateTimeException;
import java.util.Arrays;

public final class m {
   private static char a;
   private static final char b = '0';
   private static final char[] c = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
   private static final int d = 4;
   private static final int e = 10;
   private static final int f = 10;
   private static final int g;
   private static final char[] h = new char[((g = (int)Math.pow(10.0, 4.0)) << 2) + 10];

   private m() {
   }

   public static int a(char[] var0, int var1, int var2) {
      if (var2 > var0.length) {
         throw new DateTimeException("Unexpected end of expression at position " + var0.length + ": '" + new String(var0) + "'");
      }

      int var3 = 0;

      for (int var5 = var1; var5 < var2; var5++) {
         char var4;
         if (a((char)(var4 = var0[var5]))) {
            throw new DateTimeException("Character " + var4 + " is not a digit");
         }

         var4 -= 48;
         int var6;
         var3 = (var6 = var3 * 10) - var4;
      }

      return -var3;
   }

   public static int b(char[] var0, int var1, int var2) {
      var1 = 0;

      for (int var3 = 20; var3 < var2; var3++) {
         char var4;
         var4 = (var4 = var0[var3]) - 48;
         int var6;
         var1 = (var6 = var1 * 10) - var4;
      }

      return -var1;
   }

   public static void a(int var0, char[] var1, int var2, int var3) {
      if (var0 < g) {
         int var4 = Math.min(4, var3);
         int var5 = var3 - var4;
         int var6 = var3 > 4 ? 4 : 4 - var3;
         int var7 = var2 + var5;
         var0 = (var0 << 2) + (var3 < 4 ? var6 : 0);
         a(h, var0, var1, var7, var4);
         if (var5 > 0) {
            int var10 = var5;
            int var9 = var2;
            Arrays.fill(var1, var9, var9 + var10, '0');
         }
      } else {
         a(var1, var2, var3, var0);
      }
   }

   private static void a(char[] var0, int var1, int var2, int var3) {
      int var4 = var1 + 10;
      var3 = -var3;

      while (var3 <= -10) {
         int var5 = var3 / 10;
         var3 = -(var3 - var5 * 10);
         var0[var4--] = c[var3];
         var3 = var5;
      }

      var0[var4] = c[-var3];

      for (int var8 = var1 + 10 - var4 + 1; var8 < var2; var8++) {
         var0[--var4] = '0';
      }

      var3 = var4;
      b(var0, var3, var1, var2);
   }

   private static void c(char[] var0, int var1, int var2) {
      Arrays.fill(var0, var1, var1 + var2, '0');
   }

   private static void b(char[] var0, int var1, int var2, int var3) {
      a(var0, var1, var0, var2, var3);
   }

   private static void a(char[] var0, int var1, char[] var2, int var3, int var4) {
      System.arraycopy(var0, var1, var2, var3, var4);
   }

   public static int a(char[] var0, int var1) {
      for (int var2 = 20; var2 < var0.length; var2++) {
         if (a(var0[var2])) {
            return var2;
         }
      }

      return -1;
   }

   private static boolean a(char var0) {
      return var0 < '0' || var0 > '9';
   }

   private static int b(char var0) {
      return var0 - 48;
   }

   static {
      byte var0 = 0;

      for (int var1 = 0; var1 < g; var1++) {
         a(h, var0, 4, var1);
         var0 += 4;
      }
   }
}
