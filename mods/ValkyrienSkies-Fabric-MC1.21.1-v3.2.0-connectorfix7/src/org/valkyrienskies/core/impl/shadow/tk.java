package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class tk extends sv implements Serializable, tF {
   private static final long a = -5962461716457143437L;
   private final int b;
   private final int c;
   private final Cm d;

   public tk(int var1, int var2) {
      super(var1, var2);
      long var3 = var1;
      long var5 = var2;
      if (var3 * var5 >= 2147483647L) {
         throw new pm(var3 * var5, Integer.MAX_VALUE, false);
      }

      this.b = var1;
      this.c = var2;
      this.d = new Cm(0.0);
   }

   private tk(tk var1) {
      this.b = var1.b;
      this.c = var1.c;
      this.d = new Cm(var1.d);
   }

   private tk j() {
      return new tk(this);
   }

   private static tk c(int var0, int var1) {
      return new tk(var0, var1);
   }

   @Override
   public final int g() {
      return this.c;
   }

   private tk a(tk var1) {
      td.a(this, var1);
      tk var2 = new tk(this);
      Cm.a var5 = var1.d.a();

      while (var5.a()) {
         var5.d();
         int var3 = var5.b() / this.c;
         int var4 = var5.b() - var3 * this.c;
         var2.c(var3, var4, this.b(var3, var4) + var5.c());
      }

      return var2;
   }

   private tk e(tq var1) {
      try {
         tk var3 = (tk)var1;
         tk var2 = this;
         td.a(this, var3);
         tk var4 = new tk(var2);
         Cm.a var8 = var3.d.a();

         while (var8.a()) {
            var8.d();
            int var5 = var8.b() / var2.c;
            int var6 = var8.b() - var5 * var2.c;
            var4.c(var5, var6, var2.b(var5, var6) - var8.c());
         }

         return var4;
      } catch (ClassCastException var7) {
         return (tk)super.b(var1);
      }
   }

   private tk b(tk var1) {
      td.a(this, var1);
      tk var2 = new tk(this);
      Cm.a var5 = var1.d.a();

      while (var5.a()) {
         var5.d();
         int var3 = var5.b() / this.c;
         int var4 = var5.b() - var3 * this.c;
         var2.c(var3, var4, this.b(var3, var4) - var5.c());
      }

      return var2;
   }

   @Override
   public final tq c(tq var1) {
      try {
         tk var33 = (tk)var1;
         tk var32 = this;
         td.b(this, var33);
         int var34 = var33.c;
         tk var36 = new tk(var32.b, var34);
         Cm.a var15 = var32.d.a();

         while (var15.a()) {
            var15.d();
            double var37 = var15.c();
            int var35;
            int var27 = (var35 = var15.b()) / var32.c;
            int var28 = var35 % var32.c;

            for (int var29 = 0; var29 < var34; var29++) {
               int var30 = var33.d(var28, var29);
               if (var33.d.b(var30)) {
                  int var6 = var36.d(var27, var29);
                  double var24;
                  if ((var24 = var36.d.a(var6) + var37 * var33.d.a(var30)) == 0.0) {
                     var36.d.c(var6);
                  } else {
                     var36.d.a(var6, var24);
                  }
               }
            }
         }

         return var36;
      } catch (ClassCastException var26) {
         td.b(this, var1);
         int var2 = var1.g();
         sD var3 = new sD(this.b, var2);
         Cm.a var4 = this.d.a();

         while (var4.a()) {
            var4.d();
            double var5 = var4.c();
            int var7;
            int var8 = (var7 = var4.b()) / this.c;
            var7 %= this.c;

            for (int var9 = 0; var9 < var2; var9++) {
               double var14 = var5 * var1.b(var7, var9);
               int var12 = var9;
               int var11 = var8;
               sD var10 = var3;
               td.a(var3, var11, var12);
               int var16 = var11 / 52;
               int var17 = var12 / 52;
               int var13 = (var11 - var16 * 52) * var10.h(var17) + (var12 - var17 * 52);
               var10.a[var16 * var10.b + var17][var13] = var10.a[var16 * var10.b + var17][var13] + var14;
            }
         }

         return var3;
      }
   }

   private tk c(tk var1) {
      td.b(this, var1);
      int var2 = var1.c;
      tk var3 = new tk(this.b, var2);
      Cm.a var4 = this.d.a();

      while (var4.a()) {
         var4.d();
         double var5 = var4.c();
         int var7;
         int var8 = (var7 = var4.b()) / this.c;
         var7 %= this.c;

         for (int var9 = 0; var9 < var2; var9++) {
            int var10 = var1.d(var7, var9);
            if (var1.d.b(var10)) {
               int var11 = var3.d(var8, var9);
               double var13;
               if ((var13 = var3.d.a(var11) + var5 * var1.d.a(var10)) == 0.0) {
                  var3.d.c(var11);
               } else {
                  var3.d.a(var11, var13);
               }
            }
         }
      }

      return var3;
   }

   @Override
   public final double b(int var1, int var2) {
      td.a(this, var1);
      td.b(this, var2);
      return this.d.a(this.d(var1, var2));
   }

   @Override
   public final int f() {
      return this.b;
   }

   @Override
   public final void c(int var1, int var2, double var3) {
      td.a(this, var1);
      td.b(this, var2);
      if (var3 == 0.0) {
         this.d.c(this.d(var1, var2));
      } else {
         this.d.a(this.d(var1, var2), var3);
      }
   }

   @Override
   public final void a(int var1, int var2, double var3) {
      td.a(this, var1);
      td.b(this, var2);
      var1 = this.d(var1, var2);
      double var6;
      if ((var6 = this.d.a(var1) + var3) == 0.0) {
         this.d.c(var1);
      } else {
         this.d.a(var1, var6);
      }
   }

   @Override
   public final void b(int var1, int var2, double var3) {
      td.a(this, var1);
      td.b(this, var2);
      var1 = this.d(var1, var2);
      double var6;
      if ((var6 = this.d.a(var1) * var3) == 0.0) {
         this.d.c(var1);
      } else {
         this.d.a(var1, var6);
      }
   }

   private int d(int var1, int var2) {
      return var1 * this.c + var2;
   }

   // $VF: synthetic method
   @Override
   public final sv i() {
      tk var1 = this;
      return new tk(var1);
   }

   // $VF: synthetic method
   @Override
   public final sv a(int var1, int var2) {
      var2 = var2;
      var1 = var1;
      return new tk(var1, var2);
   }

   // $VF: synthetic method
   @Override
   public final tq b(tq var1) {
      return this.e(var1);
   }
}
