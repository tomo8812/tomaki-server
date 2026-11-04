package org.valkyrienskies.core.impl.shadow;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class uQ {
   private uF h = null;
   private int i = -1;
   uR a;
   uZ b;
   int c;
   uV[] d;
   List<uW> e;
   int f;
   boolean g;
   private double[] j;

   private uQ(uM var1, double[] var2, String... var3) {
      this(new uQ.b(var1, var2), var3);
   }

   private uQ(uQ.b var1, String... var2) {
      this.a = var1;
      this.b = null;
      this.c = var1.a();
      if (var2 == null) {
         this.d = null;
         this.f = 0;
      } else {
         this.d = new uV[var2.length];

         for (int var3 = 0; var3 < var2.length; var3++) {
            this.d[var3] = new uV(var2[var3], Double.NaN);
         }

         this.f = var2.length;
      }

      this.g = false;
      this.e = new ArrayList<>();
      this.j = new double[(this.c + this.f) * this.c];

      for (int var4 = 0; var4 < this.c; var4++) {
         this.j[var4 * (this.c + 1)] = 1.0;
      }
   }

   private void a(uF var1) {
      uQ.b var3;
      uM var2 = this.a instanceof uQ.b ? (var3 = (uQ.b)this.a).a : this.a;
      if (var1.a != var2) {
         throw new uQ.c();
      }

      this.h = var1;
      uF var10001 = this.h;
      uQ.a var5 = new uQ.a((byte)0);
      uF var8 = var10001;
      int var6;
      if (var10001.e.isEmpty()) {
         var8.e = new ArrayList<>();
         var6 = var8.a.a();
      } else {
         uE var4 = var2.b;
         uF.a var7;
         var6 = (var7 = var8.e.get(var8.e.size() - 1)).b.a + var7.b.b;
      }

      var8.e.add(new uF.a(var5, var6));
      this.i = var8.e.size() - 1;
      this.h.a(this.i, this.j);
   }

   private void a(uW var1) {
      this.e.add(var1);
   }

   private void a(uZ var1) {
      this.b = var1;
      this.g = true;
   }

   private void a(String var1, double var2) {
      uV[] var4 = this.d;
      int var5 = this.d.length;

      for (int var6 = 0; var6 < var5; var6++) {
         uV var7 = var4[var6];
         if (var1.equals(var7.a)) {
            double var9 = var2;
            var7.b = var9;
            this.g = true;
            return;
         }
      }

      throw new ve(var1);
   }

   private void a(double[][] var1) {
      a(this.c, var1);
      a(this.c, var1[0]);
      int var2 = 0;
      double[][] var6;
      int var3 = (var6 = var1).length;

      for (int var4 = 0; var4 < var3; var4++) {
         double[] var5;
         System.arraycopy(var5 = var6[var4], 0, this.j, var2, this.c);
         var2 += this.c;
      }

      if (this.h != null) {
         this.h.a(this.i, this.j);
      }
   }

   private void a(String var1, double[] var2) {
      a(this.c, var2);
      int var3 = this.c * this.c;
      uV[] var4 = this.d;
      int var5 = this.d.length;

      for (int var6 = 0; var6 < var5; var6++) {
         uV var7 = var4[var6];
         if (var1.equals(var7.a)) {
            System.arraycopy(var2, 0, this.j, var3, this.c);
            if (this.h != null) {
               this.h.a(this.i, this.j);
            }

            return;
         }

         var3 += this.c;
      }

      throw new ve(var1);
   }

   private void b(double[][] var1) {
      double[] var2 = this.h.a(this.i);
      int var3 = 0;

      for (int var4 = 0; var4 < this.c; var4++) {
         System.arraycopy(var2, var3, var1[var4], 0, this.c);
         var3 += this.c;
      }
   }

   private void b(String var1, double[] var2) {
      double[] var3 = this.h.a(this.i);
      int var4 = this.c * this.c;
      uV[] var5 = this.d;
      int var6 = this.d.length;

      for (int var7 = 0; var7 < var6; var7++) {
         uV var8;
         if ((var8 = var5[var7]).a.equals(var1)) {
            System.arraycopy(var3, var4, var2, 0, this.c);
            return;
         }

         var4 += this.c;
      }
   }

   private static void a(int var0, Object var1) {
      if ((var1 = var1 == null ? 0 : Array.getLength(var1)) != var0) {
         throw new oS(var1, var0);
      }
   }

   // $VF: synthetic method
   static int a(uQ var0) {
      return var0.c;
   }

   // $VF: synthetic method
   static int b(uQ var0) {
      return var0.f;
   }

   // $VF: synthetic method
   static boolean c(uQ var0) {
      return var0.g;
   }

   // $VF: synthetic method
   static uR d(uQ var0) {
      return var0.a;
   }

   // $VF: synthetic method
   static uZ e(uQ var0) {
      return var0.b;
   }

   // $VF: synthetic method
   static uV[] f(uQ var0) {
      return var0.d;
   }

   // $VF: synthetic method
   static List g(uQ var0) {
      return var0.e;
   }

   // $VF: synthetic method
   static boolean a(uQ var0, boolean var1) {
      return var0.g = false;
   }

   class a implements vd {
      private a() {
      }

      @Override
      public final int a() {
         uQ var1 = uQ.this;
         var1 = uQ.this;
         var1 = uQ.this;
         return uQ.this.c * (uQ.this.c + uQ.this.f);
      }

      @Override
      public final void a(double var1, double[] var3, double[] var4, double[] var5, double[] var6) {
         uQ var7 = uQ.this;
         if (uQ.this.g) {
            uQ var19 = uQ.this;
            if (uQ.this.f != 0) {
               var19 = uQ.this;
               var19 = uQ.this;
               var7 = uQ.this;
               var19 = uQ.this;
               uQ.this.e.add(new uX(uQ.this.a, uQ.this.b, uQ.this.d));
               boolean var23 = false;
               var7 = uQ.this;
               uQ.this.g = false;
            }
         }

         uQ var38 = uQ.this;
         var38 = uQ.this;
         double[][] var25 = new double[uQ.this.c][uQ.this.c];
         var38 = uQ.this;
         uQ.this.a.a(var1, var3, var4, var25);
         int var8 = 0;

         while (true) {
            var38 = uQ.this;
            if (var8 >= uQ.this.c) {
               var38 = uQ.this;
               if (uQ.this.f != 0) {
                  var38 = uQ.this;
                  double[] var26 = new double[uQ.this.c];
                  var38 = uQ.this;
                  var38 = uQ.this;
                  int var27 = uQ.this.c * uQ.this.c;
                  var38 = uQ.this;
                  uV[] var28 = uQ.this.d;
                  int var29 = uQ.this.d.length;

                  for (int var12 = 0; var12 < var29; var12++) {
                     uV var30 = var28[var12];
                     int var31 = 0;

                     for (int var33 = 0; !var31; var33++) {
                        var38 = uQ.this;
                        if (var33 >= uQ.this.e.size()) {
                           break;
                        }

                        var38 = uQ.this;
                        uW var16;
                        if ((var16 = uQ.this.e.get(var33)).a(var30.a)) {
                           var16.a(var1, var3, var4, var30.a, var26);
                           var31 = 0;

                           while (true) {
                              var38 = uQ.this;
                              if (var31 >= uQ.this.c) {
                                 var31 = 1;
                                 break;
                              }

                              double[] var34 = var25[var31];
                              int var17 = var27;
                              double var20 = var26[var31];
                              int var18 = 0;

                              while (true) {
                                 var38 = uQ.this;
                                 if (var18 >= uQ.this.c) {
                                    var6[var27 + var31] = var20;
                                    var31++;
                                    break;
                                 }

                                 var20 += var34[var18] * var5[var17];
                                 var17++;
                                 var18++;
                              }
                           }
                        }
                     }

                     if (!var31) {
                        var38 = uQ.this;
                        Arrays.fill(var6, var27, var27 + uQ.this.c, 0.0);
                     }

                     var38 = uQ.this;
                     var27 += uQ.this.c;
                  }
               }

               return;
            }

            double[] var9 = var25[var8];
            int var10 = 0;

            while (true) {
               var38 = uQ.this;
               if (var10 >= uQ.this.c) {
                  var8++;
                  break;
               }

               double var11 = 0.0;
               int var13 = var10;
               int var14 = var10;
               int var15 = 0;

               while (true) {
                  var38 = uQ.this;
                  if (var15 >= uQ.this.c) {
                     var38 = uQ.this;
                     var6[var13 + var8 * uQ.this.c] = var11;
                     var10++;
                     break;
                  }

                  var11 += var9[var15] * var5[var14];
                  var38 = uQ.this;
                  var14 += uQ.this.c;
                  var15++;
               }
            }
         }
      }

      // $VF: synthetic method
      a(byte var2) {
         this();
      }
   }

   static class b implements uR {
      final uM a;
      private final double[] b;

      b(uM var1, double[] var2) {
         this.a = var1;
         this.b = (double[])var2.clone();
         if (var2.length != var1.a()) {
            throw new oS(var1.a(), var2.length);
         }
      }

      @Override
      public final int a() {
         return this.a.a();
      }

      @Override
      public final void a(double var1, double[] var3, double[] var4) {
         this.a.a(var1, var3, var4);
      }

      @Override
      public final void a(double var1, double[] var3, double[] var4, double[][] var5) {
         int var6;
         double[] var7 = new double[var6 = this.a.a()];

         for (int var8 = 0; var8 < var6; var8++) {
            double var9 = var3[var8];
            var3[var8] += this.b[var8];
            this.a.a(var1, var3, var7);

            for (int var11 = 0; var11 < var6; var11++) {
               var5[var11][var8] = (var7[var11] - var4[var11]) / this.b[var8];
            }

            var3[var8] = var9;
         }
      }

      // $VF: synthetic method
      static uM a(uQ.b var0) {
         return var0.a;
      }
   }

   public static class c extends oV {
      private static final long b = 20120902L;

      public c() {
         super(px.UNMATCHED_ODE_IN_EXPANDED_SET);
      }
   }
}
