package org.valkyrienskies.core.impl.shadow;

public final class to extends tn {
   private int[] e;
   private tq f;

   private to(tq var1) {
      this(var1, 0.0);
   }

   private to(tq var1, double var2) {
      super(var1, 0.0);
   }

   @Override
   protected final void a(double[][] var1) {
      this.e = new int[var1.length];
      int var2 = 0;

      while (var2 < this.e.length) {
         this.e[var2] = var2++;
      }

      super.a(var1);
   }

   @Override
   protected final void a(int var1, double[][] var2) {
      double var3 = 0.0;
      int var5 = var1;

      for (int var6 = var1; var6 < var2.length; var6++) {
         double var7 = 0.0;

         for (int var9 = 0; var9 < var2[var6].length; var9++) {
            var7 += var2[var6][var9] * var2[var6][var9];
         }

         if (var7 > var3) {
            var3 = var7;
            var5 = var6;
         }
      }

      if (var5 != var1) {
         double[] var10 = var2[var1];
         var2[var1] = var2[var5];
         var2[var5] = var10;
         int var11 = this.e[var1];
         this.e[var1] = this.e[var5];
         this.e[var5] = var11;
      }

      super.a(var1, var2);
   }

   private tq d() {
      if (this.f == null) {
         int var1 = this.e.length;
         this.f = td.a(var1, var1);

         for (int var2 = 0; var2 < var1; var2++) {
            this.f.c(this.e[var2], var2, 1.0);
         }
      }

      return this.f;
   }

   private int a(double var1) {
      tq var3;
      int var4 = (var3 = this.a()).f();
      int var5 = var3.g();
      int var6 = 1;

      double var7;
      double var11;
      for (double var9 = var7 = var3.c();
         var6 < BY.a(var4, var5) && (var11 = var3.a(var6, var4 - 1, var6, var5 - 1).c()) != 0.0 && !(var11 / var7 * var9 < var1);
         var6++
      ) {
         var7 = var11;
      }

      return var6;
   }

   @Override
   public final sG c() {
      return new to.a(super.c(), this.d(), (byte)0);
   }

   static class a implements sG {
      private final sG a;
      private tq b;

      private a(sG var1, tq var2) {
         this.a = var1;
         this.b = var2;
      }

      @Override
      public final boolean a() {
         return this.a.a();
      }

      @Override
      public final tu a(tu var1) {
         return this.b.a(this.a.a(var1));
      }

      @Override
      public final tq a(tq var1) {
         return this.b.c(this.a.a(var1));
      }

      @Override
      public final tq b() {
         return this.a(td.a(this.b.f()));
      }

      // $VF: synthetic method
      a(sG var1, tq var2, byte var3) {
         this(var1, var2);
      }
   }
}
