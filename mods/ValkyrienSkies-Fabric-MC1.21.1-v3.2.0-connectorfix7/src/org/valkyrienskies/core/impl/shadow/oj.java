package org.valkyrienskies.core.impl.shadow;

public final class oj extends oe {
   private static final long b = -4157745166772046273L;
   private final double c;

   public oj(double var1) {
      super(null);
      this.c = var1;
   }

   @Override
   public final double d(double var1) {
      return var1 == this.c ? 1.0 : 0.0;
   }

   @Override
   public final double e(double var1) {
      return var1 < this.c ? 0.0 : 1.0;
   }

   @Override
   public final double a(double var1) {
      if (!(var1 < 0.0) && !(var1 > 1.0)) {
         return this.c;
      } else {
         throw new po(var1, 0, 1);
      }
   }

   @Override
   public final double c() {
      return this.c;
   }

   @Override
   public final double d() {
      return 0.0;
   }

   @Override
   public final double e() {
      return this.c;
   }

   @Override
   public final double f() {
      return this.c;
   }

   @Override
   public final boolean g() {
      return true;
   }

   @Override
   public final boolean h() {
      return true;
   }

   @Override
   public final boolean i() {
      return true;
   }

   @Override
   public final double b() {
      return this.c;
   }

   @Override
   public final void a(long var1) {
   }
}
