package org.valkyrienskies.core.impl.shadow;

public abstract class An extends Ao implements Av {
   @Override
   public double a(double[] var1) {
      if (var1 == null) {
         throw new pl(px.INPUT_ARRAY);
      } else {
         return this.a(var1, 0, var1.length);
      }
   }

   @Override
   public double a(double[] var1, int var2, int var3) {
      int var6 = var3;
      int var5 = var2;
      if (Ch.a(var1, var5, var6, false)) {
         this.b();
         this.b(var1, var2, var3);
      }

      return this.c();
   }

   @Override
   public abstract An a();

   @Override
   public abstract void b();

   @Override
   public abstract double c();

   @Override
   public abstract void a(double var1);

   @Override
   public final void b(double[] var1) {
      if (var1 == null) {
         throw new pl(px.INPUT_ARRAY);
      }

      this.b(var1, 0, var1.length);
   }

   @Override
   public final void b(double[] var1, int var2, int var3) {
      int var6 = var3;
      int var5 = var2;
      if (Ch.a(var1, var5, var6, false)) {
         var3 = var2 + var3;

         for (int var7 = var2; var7 < var3; var7++) {
            this.a(var1[var7]);
         }
      }
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      }

      An var2;
      return !(object instanceof An) ? false : Cq.b((var2 = (An)object).c(), this.c()) && Cq.b((float)var2.g_(), (float)this.g_());
   }

   @Override
   public int hashCode() {
      return 31 * (31 + Ci.a(this.c())) + Ci.a(this.g_());
   }

   // $VF: synthetic method
   @Override
   public AA d() {
      return this.a();
   }
}
