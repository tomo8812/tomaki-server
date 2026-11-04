package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class AG extends An implements Serializable, AB {
   private static final long a = -1296043746617791564L;
   private AC b;
   private boolean c;

   public AG() {
      this.c = true;
      this.b = new AC();
   }

   public AG(AH var1) {
      this.b = var1;
      this.c = false;
   }

   private AG(AG var1) {
      a(var1, this);
   }

   @Override
   public final void a(double var1) {
      if (this.c) {
         this.b.a(var1);
      }
   }

   @Override
   public final void b() {
      if (this.c) {
         this.b.b();
      }
   }

   @Override
   public final double c() {
      return this.b.b;
   }

   @Override
   public final long g_() {
      return this.b.g_();
   }

   @Override
   public final double a(double[] var1, int var2, int var3) {
      int var6 = var3;
      int var5 = var2;
      if (!Ch.a(var1, var5, var6, false)) {
         return Double.NaN;
      }

      AV var4 = new AV();
      double var12 = var3;
      double var7 = var4.a(var1, var2, var3) / var12;
      double var9 = 0.0;

      for (int var11 = var2; var11 < var2 + var3; var11++) {
         var9 += var1[var11] - var7;
      }

      return var7 + var9 / var12;
   }

   @Override
   public final double a(double[] var1, double[] var2, int var3, int var4) {
      if (!a_(var1, var2, var3, var4)) {
         return Double.NaN;
      }

      AV var5;
      double var6 = (var5 = new AV()).a(var2, var3, var4);
      double var8 = var5.b(var1, var2, var3, var4) / var6;
      double var10 = 0.0;

      for (int var12 = var3; var12 < var3 + var4; var12++) {
         var10 += var2[var12] * (var1[var12] - var8);
      }

      return var8 + var10 / var6;
   }

   @Override
   public final double a(double[] var1, double[] var2) {
      return this.a(var1, var2, 0, var1.length);
   }

   private AG f() {
      AG var1 = new AG();
      a(this, var1);
      return var1;
   }

   public static void a(AG var0, AG var1) {
      Ci.a(var0);
      Ci.a(var1);
      var1.c(var0.e());
      var1.c = var0.c;
      var1.b = var0.b.f();
   }

   // $VF: synthetic method
   @Override
   public final An a() {
      return this.f();
   }

   // $VF: synthetic method
   @Override
   public final AA d() {
      return this.f();
   }
}
