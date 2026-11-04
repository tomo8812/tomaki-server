package org.valkyrienskies.core.impl.shadow;

public abstract class xR extends wJ<xS> {
   private lc a;
   private xi b;
   private double c;
   private double d;
   private double e;

   protected xR(wK<xS> var1) {
      super(var1);
   }

   public xS c(wO... var1) {
      return (xS)super.a(var1);
   }

   public final xi b() {
      return this.b;
   }

   @Override
   public final void b(wO... var1) {
      super.b(var1);
      wO[] var6;
      int var2 = (var6 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         wO var4;
         if ((var4 = var6[var3]) instanceof xO) {
            xO var7 = (xO)var4;
            this.d = var7.a;
            this.e = var7.b;
            this.c = var7.c;
         } else if (var4 instanceof xQ) {
            xQ var5;
            this.a = (var5 = (xQ)var4).a;
         } else if (var4 instanceof xi) {
            this.b = (xi)var4;
         }
      }
   }

   public final double d() {
      return this.c;
   }

   public final double e() {
      return this.d;
   }

   public final double f() {
      return this.e;
   }

   protected final double a(double var1) {
      super.k();
      return this.a.a(var1);
   }

   // $VF: synthetic method
   @Override
   public Object a(wO[] var1) {
      return this.c(var1);
   }
}
