package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

class AC extends An implements Serializable {
   private static final long e = 6112755307178490473L;
   protected long a;
   protected double b;
   protected double c;
   protected double d;

   AC() {
      this.a = 0L;
      this.b = Double.NaN;
      this.c = Double.NaN;
      this.d = Double.NaN;
   }

   AC(AH var1) {
      a(var1, this);
   }

   @Override
   public void a(double var1) {
      if (this.a == 0L) {
         this.b = 0.0;
      }

      this.a++;
      double var3 = this.a;
      this.c = var1 - this.b;
      this.d = this.c / var3;
      this.b = this.b + this.d;
   }

   @Override
   public void b() {
      this.b = Double.NaN;
      this.a = 0L;
      this.c = Double.NaN;
      this.d = Double.NaN;
   }

   @Override
   public double c() {
      return this.b;
   }

   @Override
   public long g_() {
      return this.a;
   }

   public AC f() {
      AC var1 = new AC();
      a(this, var1);
      return var1;
   }

   public static void a(AC var0, AC var1) {
      Ci.a(var0);
      Ci.a(var1);
      var1.c(var0.e());
      var1.a = var0.a;
      var1.b = var0.b;
      var1.c = var0.c;
      var1.d = var0.d;
   }

   // $VF: synthetic method
   @Override
   public An a() {
      return this.f();
   }

   // $VF: synthetic method
   @Override
   public AA d() {
      return this.f();
   }
}
