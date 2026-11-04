package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;

public abstract class pD {
   private double[] b(Collection<pM> var1) {
      return new pX().a(this.a(var1)).e().g();
   }

   private static pX a() {
      return new pX();
   }

   protected abstract pW a(Collection<pM> var1);

   protected static class a {
      final kZ a;
      final double[] b;

      public a(kZ var1, Collection<pM> var2) {
         this.a = var1;
         int var4 = var2.size();
         this.b = new double[var4];
         int var5 = 0;

         for (pM var3 : var2) {
            this.b[var5++] = var3.b;
         }
      }

      public final <unrepresentable> a() {
         return new kY() {
            @Override
            public final double[] a(double[] var1) {
               pD.a var5 = a.this;
               int var2;
               double[] var3 = new double[var2 = a.this.b.length];

               for (int var4 = 0; var4 < var2; var4++) {
                  var5 = a.this;
                  var5 = a.this;
                  var3[var4] = a.this.a.a(a.this.b[var4], var1);
               }

               return var3;
            }
         };
      }

      public final <unrepresentable> b() {
         return new kX() {
            @Override
            public final double[][] a(double[] var1) {
               pD.a var5 = a.this;
               int var2;
               double[][] var3 = new double[var2 = a.this.b.length][];

               for (int var4 = 0; var4 < var2; var4++) {
                  var5 = a.this;
                  var5 = a.this;
                  var3[var4] = a.this.a.b(a.this.b[var4], var1);
               }

               return var3;
            }
         };
      }

      // $VF: synthetic method
      static double[] a(pD.a var0) {
         return var0.b;
      }

      // $VF: synthetic method
      static kZ b(pD.a var0) {
         return var0.a;
      }
   }
}
