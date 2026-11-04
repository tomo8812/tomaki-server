package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class uh implements Serializable {
   private static final long a = 1L;
   private final ud b;
   private final int c;
   private final boolean d;
   private final long[] e;

   uh(boolean var1, double[][] var2) {
      this.c = var2.length;
      if (this.c < 2) {
         throw new pn(this.c, 2, true);
      }

      this.d = (boolean)var1;
      var1 = var2[0].length;
      this.b = new ud(0L, var1);
      this.e = new long[this.c];

      for (int var4 = 0; var4 < this.c; var4++) {
         this.e[var4] = this.b.a(var2[var4]);
      }

      this.c();
   }

   private uh(int var1, boolean var2, ua[] var3) {
      if (var1 < 2) {
         throw new pn(var1, 2, true);
      }

      this.c = var1;
      this.d = (boolean)var2;
      this.e = new long[var1];
      var2 = var3.length;
      this.b = new ud(0L, var2);

      for (int var4 = 0; var4 < var1; var4++) {
         double[] var5 = new double[var2];

         for (int var6 = 0; var6 < var2; var6++) {
            var5[var6] = var3[var6].a();
         }

         this.e[var4] = this.b.a(var5);
      }

      this.c();
   }

   private ud a() {
      return this.b;
   }

   private int b() {
      return this.c;
   }

   private double[] a(int var1) {
      if (var1 >= 0 && var1 < this.c) {
         return this.b.a(this.e[var1]).c();
      } else {
         throw new po(var1, 0, this.c - 1);
      }
   }

   private void c() {
      for (int var1 = 0; var1 < this.c - 1; var1++) {
         this.b.a(this.b.a(var1), this.b.a(var1 + 1));
      }

      for (int var2 = this.c - 1; var2 > 0; var2--) {
         this.b.a(this.b.a(var2), this.b.a(var2 - 1));
      }

      if (this.d) {
         this.b.a(this.b.a(0L), this.b.a(this.c - 1));
         this.b.a(this.b.a(this.c - 1), this.b.a(0L));
      }
   }

   private static void d() {
      throw new IllegalStateException();
   }

   private uh.a e() {
      double[][] var1 = new double[this.c][];

      for (int var2 = 0; var2 < this.c; var2++) {
         int var4 = var2;
         uh var3 = this;
         if (var4 < 0 || var4 >= var3.c) {
            throw new po(var4, 0, var3.c - 1);
         }

         var1[var2] = var3.b.a(var3.e[var4]).c();
      }

      return new uh.a(this.d, var1);
   }

   static class a implements Serializable {
      private static final long a = 20130226L;
      private final boolean b;
      private final double[][] c;

      a(boolean var1, double[][] var2) {
         this.b = var1;
         this.c = var2;
      }

      private uh a() {
         return new uh(this.b, this.c);
      }
   }
}
