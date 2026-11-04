package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class AK extends An implements Serializable {
   private static final long a = 5728716329662425188L;
   private AM b = null;

   public AK() {
      this.b = new AM();
   }

   private AK(AH var1) {
      this.b = new AM(var1);
   }

   private AK(AK var1) {
      a(var1, this);
   }

   private AK(boolean var1) {
      this.b = new AM(var1);
   }

   private AK(boolean var1, AH var2) {
      this.b = new AM(var1, var2);
   }

   @Override
   public final void a(double var1) {
      this.b.a(var1);
   }

   @Override
   public final long g_() {
      return this.b.g_();
   }

   @Override
   public final double c() {
      return BY.a(this.b.c());
   }

   @Override
   public final void b() {
      this.b.b();
   }

   @Override
   public final double a(double[] var1) {
      return BY.a(this.b.a(var1));
   }

   @Override
   public final double a(double[] var1, int var2, int var3) {
      return BY.a(this.b.a(var1, var2, var3));
   }

   private double a(double[] var1, double var2, int var4, int var5) {
      return BY.a(this.b.a(var1, var2, var4, var5));
   }

   private double a(double[] var1, double var2) {
      return BY.a(this.b.a(var1, var2));
   }

   private boolean f() {
      AM var1 = this.b;
      return this.b.b;
   }

   private void a(boolean var1) {
      boolean var2 = var1;
      AM var3 = this.b;
      this.b.b = var2;
   }

   private AK g() {
      AK var1 = new AK();
      a(this, var1);
      return var1;
   }

   private static void a(AK var0, AK var1) {
      Ci.a(var0);
      Ci.a(var1);
      var1.c(var0.e());
      var1.b = var0.b.g();
   }

   // $VF: synthetic method
   @Override
   public final An a() {
      return this.g();
   }

   // $VF: synthetic method
   @Override
   public final AA d() {
      return this.g();
   }
}
