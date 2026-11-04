package org.valkyrienskies.core.impl.shadow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public final class zI implements zH {
   private static final int a = 52;
   private static final double b = BY.a(2.0, 52);
   private static final int c = 1000;
   private static final String d = "/assets/org/apache/commons/math3/random/new-joe-kuo-6.1000";
   private static final String e = "US-ASCII";
   private final int f;
   private int g = 0;
   private final long[][] h;
   private final long[] i;

   private zI(int var1) {
      if (var1 > 0 && var1 <= 1000) {
         InputStream var2;
         if ((var2 = this.getClass().getResourceAsStream("/assets/org/apache/commons/math3/random/new-joe-kuo-6.1000")) == null) {
            throw new oY();
         }

         this.f = var1;
         this.h = new long[var1][53];
         this.i = new long[var1];

         try {
            this.a(var2);
         } catch (IOException var9) {
            throw new oY();
         } catch (oZ var10) {
            throw new oY();
         } finally {
            try {
               var2.close();
            } catch (IOException var8) {
            }
         }
      } else {
         throw new po(var1, 1, 1000);
      }
   }

   private zI(int var1, InputStream var2) {
      if (var1 <= 0) {
         throw new pk(var1);
      }

      this.f = var1;
      this.h = new long[var1][53];
      this.i = new long[var1];
      int var3;
      if ((var3 = this.a(var2)) < var1) {
         throw new po(var1, 1, var3);
      }
   }

   private int a(InputStream var1) {
      for (int var2 = 1; var2 <= 52; var2++) {
         this.h[0][var2] = 1L << 52 - var2;
      }

      Charset var19 = Charset.forName("US-ASCII");
      BufferedReader var18 = new BufferedReader(new InputStreamReader(var1, var19));
      int var20 = -1;

      try {
         var18.readLine();
         int var3 = 2;
         int var4 = 1;

         String var5;
         while ((var5 = var18.readLine()) != null) {
            StringTokenizer var6 = new StringTokenizer(var5, " ");

            try {
               if ((var20 = Integer.parseInt(var6.nextToken())) >= 2 && var20 <= this.f) {
                  int var7 = Integer.parseInt(var6.nextToken());
                  int var8 = Integer.parseInt(var6.nextToken());
                  int[] var9 = new int[var7 + 1];

                  for (int var10 = 1; var10 <= var7; var10++) {
                     var9[var10] = Integer.parseInt(var6.nextToken());
                  }

                  int var10001 = var4++;
                  var9 = var9;
                  var8 = var8;
                  var7 = var10001;
                  zI var21 = this;
                  int var26 = var9.length - 1;

                  for (int var11 = 1; var11 <= var26; var11++) {
                     var21.h[var7][var11] = (long)var9[var11] << 52 - var11;
                  }

                  for (int var27 = var26 + 1; var27 <= 52; var27++) {
                     var21.h[var7][var27] = var21.h[var7][var27 - var26] ^ var21.h[var7][var27 - var26] >> var26;

                     for (int var25 = 1; var25 <= var26 - 1; var25++) {
                        var21.h[var7][var27] = var21.h[var7][var27] ^ (var8 >> var26 - 1 - var25 & 1) * var21.h[var7][var27 - var25];
                     }
                  }
               }

               if (var20 > this.f) {
                  return var20;
               }
            } catch (NoSuchElementException var15) {
               throw new oZ(var5, var3);
            } catch (NumberFormatException var16) {
               throw new oZ(var5, var3);
            }

            var3++;
         }
      } finally {
         var18.close();
      }

      return var20;
   }

   private void a(int var1, int var2, int[] var3) {
      int var4 = var3.length - 1;

      for (int var5 = 1; var5 <= var4; var5++) {
         this.h[var1][var5] = (long)var3[var5] << 52 - var5;
      }

      for (int var7 = var4 + 1; var7 <= 52; var7++) {
         this.h[var1][var7] = this.h[var1][var7 - var4] ^ this.h[var1][var7 - var4] >> var4;

         for (int var6 = 1; var6 <= var4 - 1; var6++) {
            this.h[var1][var7] = this.h[var1][var7] ^ (var2 >> var4 - 1 - var6 & 1) * this.h[var1][var7 - var6];
         }
      }
   }

   @Override
   public final double[] a() {
      double[] var1 = new double[this.f];
      if (this.g == 0) {
         this.g++;
         return var1;
      }

      int var2 = 1;

      for (int var3 = this.g - 1; (var3 & 1) == 1; var2++) {
         var3 >>= 1;
      }

      for (int var4 = 0; var4 < this.f; var4++) {
         this.i[var4] = this.i[var4] ^ this.h[var4][var2];
         var1[var4] = this.i[var4] / b;
      }

      this.g++;
      return var1;
   }

   private double[] a(int var1) {
      if (var1 == 0) {
         Arrays.fill(this.i, 0L);
      } else {
         int var2;
         long var3 = (var2 = var1 - 1) ^ var2 >> 1;

         for (int var13 = 0; var13 < this.f; var13++) {
            long var6 = 0L;

            long var9;
            for (int var5 = 1; var5 <= 52 && (var9 = var3 >> var5 - 1) != 0L; var5++) {
               long var11 = var9 & 1L;
               var6 ^= var11 * this.h[var13][var5];
            }

            this.i[var13] = var6;
         }
      }

      this.g = var1;
      return this.a();
   }

   private int b() {
      return this.g;
   }
}
