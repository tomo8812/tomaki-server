package org.valkyrienskies.core.impl.shadow;

public abstract class nt extends nq implements nx<lc> {
   private static double d;
   private int e = nr.ANY_SIDE$37d0779;
   private final int f;

   protected nt(double var1, int var3) {
      super(var1);
      this.f = var3;
   }

   protected nt(double var1, double var3, int var5) {
      super(var1, var3);
      this.f = var5;
   }

   protected nt(double var1, double var3, double var5, int var7) {
      super(var1, var3, var5);
      this.f = var7;
   }

   @Override
   public final double a(int var1, <unrepresentable> var2, double var3, double var5, int var7) {
      return this.a(var1, var2, var3, var5, var3 + 0.5 * (var5 - var3), var7);
   }

   @Override
   public final double a(int var1, lc var2, double var3, double var5, double var7, int var9) {
      this.e = var9;
      return super.b(var1, var2, var3, var5, var7);
   }

   @Override
   public final double b(int var1, lc var2, double var3, double var5, double var7) {
      return this.a(var1, var2, var3, var5, var7, nr.ANY_SIDE$37d0779);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final double j() {
      double var1 = this.d();
      double var3 = this.e();
      double var5 = this.b(var1);
      double var7 = this.b(var3);
      if (var5 == 0.0) {
         return var1;
      }

      if (var7 == 0.0) {
         return var3;
      }

      this.c(var1, var3);
      double var9 = this.i();
      double var11 = this.g();
      double var13 = this.h();
      boolean var15 = false;

      do {
         double var16 = var3 - var7 * (var3 - var1) / (var7 - var5);
         double var18;
         if ((var18 = this.b(var16)) == 0.0) {
            return var16;
         }

         if (var7 * var18 < 0.0) {
            var1 = var3;
            var5 = var7;
            var15 = !var15;
         } else {
            switch (<unrepresentable>.a[this.f - 1]) {
               case 1:
                  var5 *= 0.5;
                  break;
               case 2:
                  var5 *= var7 / (var7 + var18);
                  break;
               case 3:
                  if (var16 == var3) {
                     throw new oR();
                  }
                  break;
               default:
                  throw new oY();
            }
         }

         var3 = var16;
         var7 = var18;
         if (BY.w(var18) <= var9) {
            switch (<unrepresentable>.b[this.e - 1]) {
               case 1:
                  return var3;
               case 2:
                  if (var15) {
                     return var3;
                  }
                  break;
               case 3:
                  if (!var15) {
                     return var3;
                  }
                  break;
               case 4:
                  if (var7 <= 0.0) {
                     return var3;
                  }
                  break;
               case 5:
                  if (var7 >= 0.0) {
                     return var3;
                  }
                  break;
               default:
                  throw new oY();
            }
         }
      } while (!(BY.w(var3 - var1) < BY.e(var13 * BY.w(var3), var11)));

      switch (<unrepresentable>.b[this.e - 1]) {
         case 1:
            return var3;
         case 2:
            if (var15) {
               return var3;
            }

            return var1;
         case 3:
            if (var15) {
               return var1;
            }

            return var3;
         case 4:
            if (var7 <= 0.0) {
               return var3;
            }

            return var1;
         case 5:
            if (var7 >= 0.0) {
               return var3;
            }

            return var1;
         default:
            throw new oY();
      }
   }

   protected enum a {
      public static final int REGULA_FALSI$31c873c3 = 1;
      public static final int ILLINOIS$31c873c3 = 2;
      public static final int PEGASUS$31c873c3 = 3;
      // $VF: synthetic field
      private static final int[] $VALUES$69504202 = new int[]{REGULA_FALSI$31c873c3, ILLINOIS$31c873c3, PEGASUS$31c873c3};

      public static int[] values$d90a97d() {
         return (int[])$VALUES$69504202.clone();
      }

      private a(String var1, int var2) {
         super(var1, var2);
      }
   }
}
