package org.valkyrienskies.core.impl.shadow;

public final class qx {
   private static zF a = new zy();
   private final qr b;
   private final double c;
   private final qA d;
   private final double e;
   private final qI f;
   private int g = 0;

   private qx(qr var1, double var2, qA var4, double var5, qI var7) {
      if (var2 < 0.0 || var2 > 1.0) {
         throw new po(px.CROSSOVER_RATE, var2, 0, 1);
      }

      if (!(var5 < 0.0) && !(var5 > 1.0)) {
         this.b = var1;
         this.c = var2;
         this.d = var4;
         this.e = var5;
         this.f = var7;
      } else {
         throw new po(px.MUTATION_RATE, var5, 0, 1);
      }
   }

   private static synchronized void a(zF var0) {
      a = var0;
   }

   public static synchronized zF a() {
      return a;
   }

   private qF a(qF var1, qJ var2) {
      var1 = var1;

      for (this.g = 0; !var2.a(); this.g++) {
         qF var3 = var1;
         qx var9 = this;
         qt var4 = var3.a();
         zF var5 = a();

         while (var4.f() < var4.e()) {
            qq var6 = var9.f.a(var3);
            if (var5.nextDouble() < var9.c) {
               var6 = var9.b.a(var6.a, var6.b);
            }

            if (var5.nextDouble() < var9.e) {
               var6 = new qq(var9.d.a(var6.a), var9.d.a(var6.b));
            }

            var4.a(var6.a);
            if (var4.f() < var4.e()) {
               var4.a(var6.b);
            }
         }

         var1 = var4;
      }

      return var1;
   }

   private qF a(qF var1) {
      qt var2 = var1.a();
      zF var3 = a();

      while (var2.f() < var2.e()) {
         qq var4 = this.f.a(var1);
         if (var3.nextDouble() < this.c) {
            var4 = this.b.a(var4.a, var4.b);
         }

         if (var3.nextDouble() < this.e) {
            var4 = new qq(this.d.a(var4.a), this.d.a(var4.b));
         }

         var2.a(var4.a);
         if (var2.f() < var2.e()) {
            var2.a(var4.b);
         }
      }

      return var2;
   }

   private qr b() {
      return this.b;
   }

   private double c() {
      return this.c;
   }

   private qA d() {
      return this.d;
   }

   private double e() {
      return this.e;
   }

   private qI f() {
      return this.f;
   }

   private int g() {
      return this.g;
   }
}
