package org.valkyrienskies.core.impl.shadow;

@Deprecated
public abstract class yQ extends yz<kQ> implements yc {
   private kY b;

   @Deprecated
   protected yQ() {
   }

   protected yQ(ya<yp> var1) {
      super(var1);
   }

   protected final double[] b(double[] var1) {
      return this.b.a(var1);
   }

   private yp b(int var1, kQ var2, yf var3, double[] var4) {
      this.b = var2.a();
      return super.a(var1, var2, var3, var4);
   }

   private yp a(int var1, lk var2, yf var3, double[] var4) {
      lk var5 = var2;
      return this.b(var1, new kQ() {
         @Override
         public final double a(double[] var1) {
            return var0.a(var1);
         }

         @Override
         public final <unrepresentable> a(final int var1) {
            return new kW() {
               @Override
               public final double a(double[] var1x) {
                  int var2;
                  lg[] var3 = new lg[var2 = var1x.length];

                  for (int var4 = 0; var4 < var2; var4++) {
                     if (var4 == var1) {
                        var3[var4] = new lg(1, 1, 0, var1x[var4]);
                     } else {
                        var3[var4] = new lg(1, 1, var1x[var4]);
                     }
                  }

                  return var0.a(var3).a(1);
               }
            };
         }

         @Override
         public final <unrepresentable> a() {
            return new kY() {
               @Override
               public final double[] a(double[] var1) {
                  int var2x;
                  lg[] var3x = new lg[var2x = var1.length];

                  for (int var4x = 0; var4x < var2x; var4x++) {
                     var3x[var4x] = new lg(var2x, 1, var4x, var1[var4x]);
                  }

                  lg var8 = var0.a(var3x);
                  var1 = new double[var2x];
                  int[] var7 = new int[var2x];

                  for (int var5x = 0; var5x < var2x; var5x++) {
                     var7[var5x] = 1;
                     var1[var5x] = var8.a(var7);
                     var7[var5x] = 0;
                  }

                  return var1;
               }
            };
         }
      }, var3, var4);
   }

   // $VF: synthetic method
   @Override
   public final yp a(int var1, kQ var2, yf var3, double[] var4) {
      return this.b(var1, var2, var3, var4);
   }
}
