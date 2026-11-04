package org.valkyrienskies.core.impl.shadow;

final class pP extends pO {
   private final pW.a a;
   private final tq b;

   pP(pW.a var1, tq var2) {
      super(var2.g());
      this.a = var1;
      this.b = var2;
   }

   @Override
   public final tq c() {
      return this.b.c(this.a.c());
   }

   @Override
   public final tu d() {
      return this.b.a(this.a.d());
   }

   @Override
   public final tu e() {
      return this.a.e();
   }
}
