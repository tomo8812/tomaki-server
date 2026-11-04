package org.valkyrienskies.core.impl.shadow;

public final class sJ extends sZ {
   private static final long a = 20120129L;
   private final tu b;
   private final tu c;
   private final double d;
   private final tu e;

   public sJ(sF var1, int var2, tu var3, tu var4, tu var5, double var6) {
      super(var1, var2);
      this.e = var3;
      this.b = var4;
      this.c = var5;
      this.d = var6;
   }

   public sJ(tH var1, int var2, tu var3, tu var4, double var5) {
      super(var1, var2);
      this.e = var3;
      this.b = var4;
      this.c = null;
      this.d = var5;
   }

   @Override
   public final double a() {
      return this.d;
   }

   @Override
   public final tu b() {
      if (this.c != null) {
         return this.c;
      } else {
         throw new pb();
      }
   }

   @Override
   public final tu c() {
      return this.b;
   }

   @Override
   public final tu d() {
      return this.e;
   }

   @Override
   public final boolean e() {
      return this.c != null;
   }
}
