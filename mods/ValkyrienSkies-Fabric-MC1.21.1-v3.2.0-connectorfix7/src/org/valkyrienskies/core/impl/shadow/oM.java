package org.valkyrienskies.core.impl.shadow;

public final class oM extends oc {
   private static final long b = 20120109L;
   private final int c;
   private final int d;

   private oM(int var1, int var2) {
      this(new zR(), var1, var2);
   }

   public oM(zF var1, int var2, int var3) {
      super(var1);
      if (var2 > var3) {
         throw new pm(px.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, var2, var3, true);
      }

      this.c = var2;
      this.d = var3;
   }

   @Override
   public final double c(int var1) {
      return var1 >= this.c && var1 <= this.d ? 1.0 / (this.d - this.c + 1) : 0.0;
   }

   @Override
   public final double d(int var1) {
      if (var1 < this.c) {
         return 0.0;
      } else {
         return var1 > this.d ? 1.0 : (var1 - this.c + 1.0) / (this.d - this.c + 1.0);
      }
   }

   @Override
   public final double b() {
      return 0.5 * (this.c + this.d);
   }

   @Override
   public final double c() {
      double var1;
      return ((var1 = this.d - this.c + 1) * var1 - 1.0) / 12.0;
   }

   @Override
   public final int d() {
      return this.c;
   }

   @Override
   public final int e() {
      return this.d;
   }

   @Override
   public final boolean f() {
      return true;
   }

   @Override
   public final int a() {
      int var1;
      if ((var1 = this.d - this.c + 1) > 0) {
         return this.c + super.a.nextInt(var1);
      }

      do {
         while ((var1 = super.a.nextInt()) < this.c) {
         }
      } while (var1 > this.d);

      return var1;
   }
}
