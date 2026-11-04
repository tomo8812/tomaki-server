package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Arrays;

public final class AN implements Serializable {
   private static final long d = 4118372414238930270L;
   public final double[] a;
   public final double[] b;
   private final boolean e;
   public long c;

   public AN(int var1, boolean var2) {
      this.a = new double[var1];
      this.b = new double[var1 * (var1 + 1) / 2];
      this.c = 0L;
      this.e = var2;
   }

   public final void a(double[] var1) {
      if (var1.length != this.a.length) {
         throw new oS(var1.length, this.a.length);
      }

      int var2 = 0;

      for (int var3 = 0; var3 < var1.length; var3++) {
         this.a[var3] = this.a[var3] + var1[var3];

         for (int var4 = 0; var4 <= var3; var4++) {
            int var10001 = var2++;
            this.b[var10001] = this.b[var10001] + var1[var3] * var1[var4];
         }
      }

      this.c++;
   }

   public final tq a() {
      int var1;
      sv var2 = td.a(var1 = this.a.length, var1);
      if (this.c > 1L) {
         double var3 = 1.0 / (this.c * (this.e ? this.c - 1L : this.c));
         int var5 = 0;

         for (int var6 = 0; var6 < var1; var6++) {
            for (int var7 = 0; var7 <= var6; var7++) {
               double var8 = var3 * (this.c * this.b[var5++] - this.a[var6] * this.a[var7]);
               var2.c(var6, var7, var8);
               var2.c(var7, var6, var8);
            }
         }
      }

      return var2;
   }

   private long c() {
      return this.c;
   }

   public final void b() {
      this.c = 0L;
      Arrays.fill(this.a, 0.0);
      Arrays.fill(this.b, 0.0);
   }

   @Override
   public final int hashCode() {
      int var1 = 31 + (this.e ? 1231 : 1237);
      var1 = var1 * 31 + (int)(this.c ^ this.c >>> 32);
      var1 = var1 * 31 + Arrays.hashCode(this.b);
      return var1 * 31 + Arrays.hashCode(this.a);
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof AN)) {
         return false;
      } else {
         obj = (AN)obj;
         if (this.e != ((AN)obj).e) {
            return false;
         } else if (this.c != ((AN)obj).c) {
            return false;
         } else {
            return !Arrays.equals(this.b, ((AN)obj).b) ? false : Arrays.equals(this.a, ((AN)obj).a);
         }
      }
   }
}
