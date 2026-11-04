package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;

public final class xe extends wZ {
   private static int b;
   private static double c;
   private static final double d = 1.0E-6;
   private final double e;
   private final int f;
   private final double g;
   private xc h;
   private xg i;

   public xe() {
      this(1.0E-6, 10, 1.0E-10);
   }

   private xe(double var1) {
      this(var1, 10, 1.0E-10);
   }

   private xe(double var1, int var3) {
      this(var1, var3, 1.0E-10);
   }

   private xe(double var1, int var3, double var4) {
      this.e = var1;
      this.f = var3;
      this.g = 1.0E-10;
      this.h = xc.DANTZIG;
   }

   @Override
   public final wQ c(wO... var1) {
      return super.c(var1);
   }

   @Override
   public final void b(wO... var1) {
      super.b(var1);
      this.i = null;
      wO[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         wO var4;
         if ((var4 = var5[var3]) instanceof xg) {
            this.i = (xg)var4;
         } else if (var4 instanceof xc) {
            this.h = (xc)var4;
         }
      }
   }

   private Integer a(xf var1) {
      double var2 = 0.0;
      Integer var4 = null;

      for (int var5 = var1.a(); var5 < var1.a.g() - 1; var5++) {
         double var6;
         if ((var6 = var1.b(0, var5)) < var2) {
            var2 = var6;
            var4 = var5;
            if (this.h == xc.BLAND) {
               int var8 = var5;
               xf var7 = var1;
               xe var14 = this;
               int var9 = var7.a();

               boolean var10000;
               while (true) {
                  if (var9 >= var7.a.f()) {
                     var10000 = false;
                     break;
                  }

                  if (Cq.a(var7.b(var9, var8), 0.0, var14.g) > 0) {
                     var10000 = true;
                     break;
                  }

                  var9++;
               }

               if (var10000) {
                  break;
               }
            }
         }
      }

      return var4;
   }

   private boolean a(xf var1, int var2) {
      for (int var3 = var1.a(); var3 < var1.a.f(); var3++) {
         if (Cq.a(var1.b(var3, var2), 0.0, this.g) > 0) {
            return true;
         }
      }

      return false;
   }

   private Integer b(xf var1, int var2) {
      ArrayList var3 = new ArrayList();
      double var4 = Double.MAX_VALUE;

      for (int var6 = var1.a(); var6 < var1.a.f(); var6++) {
         double var7 = var1.b(var6, var1.a.g() - 1);
         double var9;
         if (Cq.a(var9 = var1.b(var6, var2), 0.0, this.g) > 0) {
            double var11;
            int var16;
            if ((var16 = Double.compare(var11 = BY.w(var7 / var9), var4)) == 0) {
               var3.add(var6);
            } else if (var16 < 0) {
               var4 = var11;
               var3.clear();
               var3.add(var6);
            }
         }
      }

      if (var3.size() == 0) {
         return null;
      }

      if (var3.size() <= 1) {
         return (Integer)var3.get(0);
      }

      if (var1.b > 0) {
         for (Integer var17 : var3) {
            for (int var8 = 0; var8 < var1.b; var8++) {
               int var20 = var8 + var1.g();
               if (Cq.b(var1.b(var17, var20), 1.0, this.f) && var17.equals(var1.a(var20))) {
                  return var17;
               }
            }
         }
      }

      Integer var15 = null;
      int var18 = var1.a.g();

      for (Integer var21 : var3) {
         var2 = var21;
         int var10;
         if ((var10 = var1.c[var2]) < var18) {
            var18 = var10;
            var15 = var21;
         }
      }

      return var15;
   }

   private void b(xf var1) {
      this.l();
      xf var4 = var1;
      xe var3 = this;
      double var6 = 0.0;
      Integer var8 = null;

      for (int var9 = var4.a(); var9 < var4.a.g() - 1; var9++) {
         double var10;
         if ((var10 = var4.b(0, var9)) < var6) {
            var6 = var10;
            var8 = var9;
            if (var3.h == xc.BLAND) {
               int var14 = var9;
               xf var13 = var4;
               xe var12 = var3;
               int var15 = var13.a();

               boolean var10000;
               while (true) {
                  if (var15 >= var13.a.f()) {
                     var10000 = false;
                     break;
                  }

                  if (Cq.a(var13.b(var15, var14), 0.0, var12.g) > 0) {
                     var10000 = true;
                     break;
                  }

                  var15++;
               }

               if (var10000) {
                  break;
               }
            }
         }
      }

      Integer var2 = var8;
      int var22 = var2;
      var4 = var1;
      var3 = this;
      ArrayList var7 = new ArrayList();
      double var23 = Double.MAX_VALUE;

      for (int var24 = var4.a(); var24 < var4.a.f(); var24++) {
         double var11 = var4.b(var24, var4.a.g() - 1);
         double var31;
         if (Cq.a(var31 = var4.b(var24, var22), 0.0, var3.g) > 0) {
            int var17;
            double var35;
            if ((var17 = Double.compare(var35 = BY.w(var11 / var31), var23)) == 0) {
               var7.add(var24);
            } else if (var17 < 0) {
               var23 = var35;
               var7.clear();
               var7.add(var24);
            }
         }
      }

      Integer var36;
      if (var7.size() == 0) {
         var36 = null;
      } else {
         label76:
         if (var7.size() > 1) {
            if (var4.b > 0) {
               for (Integer var27 : var7) {
                  for (int var29 = 0; var29 < var4.b; var29++) {
                     int var32 = var29 + var4.g();
                     if (Cq.b(var4.b(var27, var32), 1.0, var3.f) && var27.equals(var4.a(var32))) {
                        var36 = var27;
                        break label76;
                     }
                  }
               }
            }

            Integer var26 = null;
            int var28 = var4.a.g();

            for (Integer var33 : var7) {
               int var19 = var33;
               int var34;
               if ((var34 = var4.c[var19]) < var28) {
                  var28 = var34;
                  var26 = var33;
               }
            }

            var36 = var26;
         } else {
            var36 = (Integer)var7.get(0);
         }
      }

      Integer var20 = var36;
      if (var36 == null) {
         throw new xh();
      }

      var1.a(var2, var20);
   }

   private void c(xf var1) {
      if (var1.b != 0) {
         while (!var1.c()) {
            this.b(var1);
         }

         if (!Cq.b(var1.b(0, var1.h()), 0.0, this.e)) {
            throw new xa();
         }
      }
   }

   private wQ o() {
      if (this.i != null) {
         Object var2 = null;
         xg var4 = this.i;
         this.i.a = (xf)var2;
      }

      xf var1 = new xf(this.d(), this.m(), this.n(), this.b(), this.e, this.f);
      xf var3 = var1;
      xe var6 = this;
      if (var3.b != 0) {
         while (!var3.c()) {
            var6.b(var3);
         }

         if (!Cq.b(var3.b(0, var3.h()), 0.0, var6.e)) {
            throw new xa();
         }
      }

      var1.b();
      if (this.i != null) {
         xf var7 = var1;
         xg var10 = this.i;
         this.i.a = var7;
      }

      while (!var1.c()) {
         this.b(var1);
      }

      wQ var5 = var1.d();
      if (this.b()) {
         double[] var8 = var5.a();

         for (int var9 = 0; var9 < var8.length; var9++) {
            if (Cq.a(var8[var9], 0.0, this.e) < 0) {
               throw new xa();
            }
         }
      }

      return var5;
   }

   // $VF: synthetic method
   @Override
   public final Object a(wO[] var1) {
      wO[] var2 = var1;
      return super.c(var2);
   }

   // $VF: synthetic method
   @Override
   public final Object c() {
      xe var1 = this;
      if (this.i != null) {
         Object var3 = null;
         xg var5 = var1.i;
         var1.i.a = (xf)var3;
      }

      xf var2 = new xf(var1.d(), var1.m(), var1.n(), var1.b(), var1.e, var1.f);
      xf var4 = var2;
      xe var7 = var1;
      if (var4.b != 0) {
         while (!var4.c()) {
            var7.b(var4);
         }

         if (!Cq.b(var4.b(0, var4.h()), 0.0, var7.e)) {
            throw new xa();
         }
      }

      var2.b();
      if (var1.i != null) {
         xf var8 = var2;
         xg var11 = var1.i;
         var1.i.a = var8;
      }

      while (!var2.c()) {
         var1.b(var2);
      }

      wQ var6 = var2.d();
      if (var1.b()) {
         double[] var9 = var6.a();

         for (int var10 = 0; var10 < var9.length; var10++) {
            if (Cq.a(var9[var10], 0.0, var1.e) < 0) {
               throw new xa();
            }
         }
      }

      return var6;
   }
}
