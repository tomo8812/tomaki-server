package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class AE extends An implements Serializable {
   private static final long b = -8178734905303459453L;
   public Av a;

   public AE() {
      this.a = new AW();
   }

   private AE(AE var1) {
      a(var1, this);
   }

   public AE(AW var1) {
      this.a = var1;
   }

   private AE f() {
      AE var1 = new AE();
      a(this, var1);
      return var1;
   }

   @Override
   public final void a(double var1) {
      this.a.a(var1);
   }

   @Override
   public final double c() {
      return this.a.g_() > 0L ? BY.i(this.a.c() / this.a.g_()) : Double.NaN;
   }

   @Override
   public final void b() {
      this.a.b();
   }

   @Override
   public final double a(double[] var1, int var2, int var3) {
      return BY.i(this.a.a(var1, var2, var3) / var3);
   }

   @Override
   public final long g_() {
      return this.a.g_();
   }

   public final void a(Av var1) {
      this.h();
      this.a = var1;
   }

   private Av g() {
      return this.a;
   }

   public static void a(AE var0, AE var1) {
      Ci.a(var0);
      Ci.a(var1);
      var1.c(var0.e());
      var1.a = var0.a.a();
   }

   private void h() {
      if (this.g_() > 0L) {
         throw new oX(px.VALUES_ADDED_BEFORE_CONFIGURING_STATISTIC, this.g_());
      }
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
