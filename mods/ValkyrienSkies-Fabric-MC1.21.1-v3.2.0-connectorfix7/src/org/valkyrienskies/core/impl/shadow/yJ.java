package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class yJ<T extends kZ> {
   @Deprecated
   private final ye b;
   private final yl c;
   final List<yN> a;

   @Deprecated
   public yJ(ye var1) {
      this.b = var1;
      this.c = null;
      this.a = new ArrayList<>();
   }

   private yJ(yl var1) {
      this.b = null;
      this.c = var1;
      this.a = new ArrayList<>();
   }

   private void a(double var1, double var3) {
      double var10 = var3;
      double var8 = var1;
      double var6 = 1.0;
      this.a.add(new yN(1.0, var8, var10));
   }

   private void a(double var1, double var3, double var5) {
      this.a.add(new yN(1.0, var3, var5));
   }

   private void a(yN var1) {
      this.a.add(var1);
   }

   public final yN[] a() {
      return this.a.toArray(new yN[this.a.size()]);
   }

   private void b() {
      this.a.clear();
   }

   public final double[] a(T var1, double[] var2) {
      return this.a(Integer.MAX_VALUE, (T)var1, var2);
   }

   public final double[] a(int var1, T var2, double[] var3) {
      double[] var4 = new double[this.a.size()];
      double[] var5 = new double[this.a.size()];
      int var6 = 0;

      for (yN var8 : this.a) {
         var4[var6] = var8.c;
         var5[var6] = var8.a;
         var6++;
      }

      yq var10;
      if (this.c == null) {
         var10 = this.b.a(var1, new yJ.a(var2), var4, var5, var3);
      } else {
         var10 = this.c.a(var1, new yJ.b(var2), var4, var5, var3);
      }

      return (double[])var10.a;
   }

   // $VF: synthetic method
   static List a(yJ var0) {
      return var0.a;
   }

   @Deprecated
   class a implements kR {
      final kZ a;

      a(kZ var2) {
         this.a = var2;
      }

      @Override
      public final kX a() {
         return new kX() {
            @Override
            public final double[][] a(double[] var1) {
               yJ var5 = yJ.this;
               double[][] var2 = new double[yJ.this.a.size()][];
               int var3 = 0;
               var5 = yJ.this;

               for (yN var8 : yJ.this.a) {
                  int var10001 = var3++;
                  yJ.a var6 = a.this;
                  var2[var10001] = a.this.a.b(var8.b, var1);
               }

               return var2;
            }
         };
      }

      @Override
      public final double[] a(double[] var1) {
         yJ var5 = yJ.this;
         double[] var2 = new double[yJ.this.a.size()];
         int var3 = 0;
         var5 = yJ.this;

         for (yN var7 : yJ.this.a) {
            var2[var3++] = this.a.a(var7.b, var1);
         }

         return var2;
      }

      // $VF: synthetic method
      static kZ a(yJ.a var0) {
         return var0.a;
      }
   }

   class b implements ll {
      private final kZ a;

      b(kZ var2) {
         this.a = var2;
      }

      @Override
      public final double[] a(double[] var1) {
         yJ var5 = yJ.this;
         double[] var2 = new double[yJ.this.a.size()];
         int var3 = 0;
         var5 = yJ.this;

         for (yN var7 : yJ.this.a) {
            var2[var3++] = this.a.a(var7.b, var1);
         }

         return var2;
      }

      @Override
      public final lg[] a(lg[] var1) {
         double[] var2 = new double[var1.length];

         for (int var3 = 0; var3 < var1.length; var3++) {
            lg var6;
            var2[var3] = (var6 = var1[var3]).b[0];
         }

         yJ var9 = yJ.this;
         lg[] var8 = new lg[yJ.this.a.size()];
         int var4 = 0;
         var9 = yJ.this;

         for (yN var11 : yJ.this.a) {
            lg var12 = new lg(var1.length, 1, this.a.a(var11.b, var2));

            for (int var7 = 0; var7 < var1.length; var7++) {
               var12 = var12.a(new lg(var1.length, 1, var7, 0.0));
            }

            var8[var4++] = var12;
         }

         return var8;
      }
   }
}
