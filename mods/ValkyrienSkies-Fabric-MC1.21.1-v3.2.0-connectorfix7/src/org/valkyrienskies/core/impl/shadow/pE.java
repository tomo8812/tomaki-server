package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class pE<T extends kZ> {
   private final xF b;
   final List<pM> a;

   public pE(xF var1) {
      this.b = var1;
      this.a = new ArrayList<>();
   }

   private void a(double var1, double var3) {
      this.a(1.0, var1, var3);
   }

   public final void a(double var1, double var3, double var5) {
      this.a.add(new pM(1.0, var3, var5));
   }

   private void a(pM var1) {
      this.a.add(var1);
   }

   public final pM[] a() {
      return this.a.toArray(new pM[this.a.size()]);
   }

   public final void b() {
      this.a.clear();
   }

   public final double[] a(T var1, double[] var2) {
      return this.a(Integer.MAX_VALUE, (T)var1, var2);
   }

   public final double[] a(int var1, T var2, double[] var3) {
      double[] var4 = new double[this.a.size()];
      double[] var5 = new double[this.a.size()];
      int var6 = 0;

      for (pM var8 : this.a) {
         var4[var6] = var8.c;
         var5[var6] = var8.a;
         var6++;
      }

      pE.a var10 = new pE.a(var2);
      return (double[])this.b.c(new wM(var1), var10.a(), var10.b(), new xG(var4), new xH(var5), new wL(var3)).a;
   }

   // $VF: synthetic method
   static List a(pE var0) {
      return var0.a;
   }

   class a {
      final kZ a;

      a(kZ var2) {
         this.a = var2;
      }

      public final xC a() {
         return new xC(new kY() {
            @Override
            public final double[] a(double[] var1) {
               pE var6 = pE.this;
               double[] var2 = new double[pE.this.a.size()];
               int var3 = 0;
               var6 = pE.this;

               for (pM var5 : pE.this.a) {
                  int var10001 = var3++;
                  pE.a var8 = a.this;
                  var2[var10001] = a.this.a.a(var5.b, var1);
               }

               return var2;
            }
         });
      }

      public final xD b() {
         return new xD(new kX() {
            @Override
            public final double[][] a(double[] var1) {
               pE var6 = pE.this;
               double[][] var2 = new double[pE.this.a.size()][];
               int var3 = 0;
               var6 = pE.this;

               for (pM var5 : pE.this.a) {
                  int var10001 = var3++;
                  pE.a var8 = a.this;
                  var2[var10001] = a.this.a.b(var5.b, var1);
               }

               return var2;
            }
         });
      }

      // $VF: synthetic method
      static kZ a(pE.a var0) {
         return var0.a;
      }
   }
}
