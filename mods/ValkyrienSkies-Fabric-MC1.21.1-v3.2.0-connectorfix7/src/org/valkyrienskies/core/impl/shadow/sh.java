package org.valkyrienskies.core.impl.shadow;

public final class sh implements rV<sj> {
   si a;
   boolean b;
   private final double c;

   public sh(si var1, boolean var2, double var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   private sh h() {
      return this;
   }

   @Override
   public final double a(qM<sj> var1) {
      si var5 = this.a;
      si var4;
      double var2 = (var4 = (si)var1).b - this.a.b;
      return this.b ? var2 : -var2;
   }

   public final boolean a() {
      return this.b;
   }

   private sh i() {
      return new sh(this.a, !this.b, this.c);
   }

   public final sk c() {
      return new sk(this, null);
   }

   private sg j() {
      return new sg(this.c);
   }

   @Override
   public final boolean a(rV<sj> var1) {
      return !(this.b ^ ((sh)var1).b);
   }

   public final si d() {
      return this.a;
   }

   @Override
   public final qM<sj> b(qM<sj> var1) {
      return this.a;
   }

   @Override
   public final double b() {
      return this.c;
   }

   // $VF: synthetic method
   @Override
   public final rK e() {
      sh var1 = this;
      return new sg(var1.c);
   }

   // $VF: synthetic method
   @Override
   public final rL f() {
      return this.c();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final rV g() {
      return this;
   }
}
