package org.valkyrienskies.core.impl.shadow;

public final class qX implements rV<qU> {
   private static final double c = 1.0E-10;
   public qZ a;
   public boolean b;
   private final double d;

   public qX(qZ var1, boolean var2, double var3) {
      this.a = var1;
      this.b = var2;
      this.d = var3;
   }

   @Deprecated
   private qX(qZ var1, boolean var2) {
      this(var1, var2, 1.0E-10);
   }

   private qX h() {
      return this;
   }

   private double a(qO<qU> var1) {
      return this.a((qM<qU>)var1);
   }

   @Override
   public final double a(qM<qU> var1) {
      qZ var5 = this.a;
      qZ var4;
      double var2 = (var4 = (qZ)var1).d - this.a.d;
      return this.b ? var2 : -var2;
   }

   public final qY a() {
      return new qY(this, null);
   }

   private qW i() {
      return new qW(this.d);
   }

   @Override
   public final boolean a(rV<qU> var1) {
      return !(this.b ^ ((qX)var1).b);
   }

   @Override
   public final qM<qU> b(qM<qU> var1) {
      return this.a;
   }

   @Override
   public final double b() {
      return this.d;
   }

   public final qZ c() {
      return this.a;
   }

   public final boolean d() {
      return this.b;
   }

   private void j() {
      this.b = !this.b;
   }

   // $VF: synthetic method
   @Override
   public final rK e() {
      qX var1 = this;
      return new qW(var1.d);
   }

   // $VF: synthetic method
   @Override
   public final rL f() {
      return this.a();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final rV g() {
      return this;
   }
}
