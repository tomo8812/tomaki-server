package org.valkyrienskies.core.impl.shadow;

public final class nY extends nX {
   private nY(nZ var1) {
      super(var1);
   }

   private nY(nZ var1, byte var2) {
      super(var1, var2);
   }

   private nY(nZ var1, int var2) {
      super(var1, var2);
   }

   private nY(nZ var1, long var2) {
      super(var1, var2);
   }

   private nY(nZ var1, double var2) {
      super(var1, var2);
      this.h(0);
   }

   private nY(nX var1) {
      super(var1);
      this.h(0);
   }

   private nY(nZ var1, String var2) {
      super(var1, var2);
      this.h(0);
   }

   private nY(nZ var1, byte var2, byte var3) {
      super(var1, var2, var3);
   }

   @Override
   public final nX C() {
      return new nY(this.D());
   }

   @Override
   public final nX a(byte var1) {
      return new nY(this.D(), var1);
   }

   @Override
   public final nX e(int var1) {
      return new nY(this.D(), var1);
   }

   @Override
   public final nX a(long var1) {
      return new nY(this.D(), 4503599627370496L);
   }

   @Override
   public final nX h(double var1) {
      return new nY(this.D(), var1);
   }

   @Override
   public final nX a(nX var1) {
      if (this.D().a != var1.D().a) {
         this.D().a(1);
         nX var2;
         (var2 = this.a(this.F())).d = 3;
         return this.a(1, "newInstance", var1, var2);
      } else {
         return new nY(var1);
      }
   }

   @Override
   public final nX a(String var1) {
      return new nY(this.D(), var1);
   }

   @Override
   public final nX a(byte var1, byte var2) {
      return new nY(this.D(), var1, var2);
   }

   private int S() {
      return (this.E() << 2) - 3;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final int h(int var1) {
      int var2;
      if ((var2 = super.a[super.a.length - 1]) == 0) {
         return 0;
      }

      int var3 = super.a.length << 2;

      for (short var4 = 1000; var4 > var2; var3--) {
         var4 /= 10;
      }

      var2 = this.S();
      int var11;
      int var5 = (var11 = var3 - var2) / 4;
      byte var15 = 1;

      for (int var6 = 0; var6 < var11 % 4; var6++) {
         var15 *= 10;
      }

      int var18 = super.a[var5];
      if (var15 <= 1 && var2 == 4 * super.a.length - 3) {
         return super.h(var1);
      }

      var1 = var1;
      if (var15 == 1) {
         var2 = super.a[var5 - 1] / 1000 % 10;
         super.a[var5 - 1] = super.a[var5 - 1] % 1000;
         var1 |= super.a[var5 - 1];
      } else {
         var2 = var18 * 10 / var15 % 10;
         var1 |= var18 % (var15 / 10);
      }

      for (int var12 = 0; var12 < var5; var12++) {
         var1 |= super.a[var12];
         super.a[var12] = 0;
      }

      label157: {
         super.a[var5] = var18 / var15 * var15;
         boolean var10000;
         switch (<unrepresentable>.a[this.D().g - 1]) {
            case 1:
               var10000 = false;
               break;
            case 2:
               var13 = var2 != 0 || var1 != 0;
               break label157;
            case 3:
               var13 = var2 >= 5;
               break label157;
            case 4:
               var13 = var2 > 5;
               break label157;
            case 5:
               var13 = var2 > 5 || var2 == 5 && var1 != 0 || var2 == 5 && var1 == 0 && (var18 / var15 & 1) == 1;
               break label157;
            case 6:
               var13 = var2 > 5 || var2 == 5 && var1 != 0 || var2 == 5 && var1 == 0 && (var18 / var15 & 1) == 0;
               break label157;
            case 7:
               var13 = super.b == 1 && (var2 != 0 || var1 != 0);
               break label157;
            default:
               var10000 = super.b == -1 && (var2 != 0 || var1 != 0);
         }

         var13 = var10000;
      }

      if (var13) {
         var3 = var15;

         for (int var16 = var5; var16 < super.a.length; var16++) {
            var3 = (var5 = super.a[var16] + var3) / 10000;
            super.a[var16] = var5 % 10000;
         }

         if (var3 != 0) {
            this.I();
            super.a[super.a.length - 1] = var3;
         }
      }

      if (super.c < -32767) {
         this.D().a(8);
         return 8;
      }

      if (super.c > 32768) {
         this.D().a(4);
         return 4;
      }

      if (var2 == 0 && var1 == 0) {
         return 0;
      }

      this.D().a(16);
      return 16;
   }

   @Override
   public final nX j(nX var1) {
      if (this.D().a != var1.D().a) {
         this.D().a(1);
         nX var7;
         (var7 = this.a(this.F())).d = 3;
         return this.a(1, "nextAfter", var1, var7);
      }

      boolean var2 = false;
      if (this.b(var1)) {
         var2 = true;
      }

      if (this.equals(var1)) {
         return this.a(var1);
      }

      if (this.b(this.F())) {
         var2 = !var2;
      }

      nX var4;
      if (var2) {
         nX var3 = a(this.g(this.O() - this.S() + 1), this);
         if (this.equals(this.F())) {
            var3 = this.f(-32767 - super.a.length - 1);
         }

         if (var3.equals(this.F())) {
            var4 = a(this.a(this.F()), this);
         } else {
            var4 = this.f(var3);
         }
      } else {
         nX var5 = a(this.g(this.O()), this);
         nX var6;
         if (this.equals(var5)) {
            var6 = var5.i(this.g(this.S()));
         } else {
            var6 = var5.i(this.g(this.S() - 1));
         }

         if (this.equals(this.F())) {
            var6 = this.f(-32767 - super.a.length - 1);
         }

         if (var6.equals(this.F())) {
            var4 = a(this.a(this.F()), this);
         } else {
            var4 = this.g(var6);
         }
      }

      if (var4.R() == 1 && this.R() != 1) {
         this.D().a(16);
         var4 = this.a(16, "nextAfter", var1, var4);
      }

      if (var4.equals(this.F()) && !this.equals(this.F())) {
         this.D().a(16);
         var4 = this.a(16, "nextAfter", var1, var4);
      }

      return var4;
   }
}
