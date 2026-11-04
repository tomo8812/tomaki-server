package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;

@Deprecated
public final class zc extends yW {
   private static final double a = 1.0E-6;
   private static final int b = 10;
   private final double c = 1.0E-6;
   private final int d = 10;

   public zc() {
      this(1.0E-6, 10);
   }

   private zc(double var1, int var3) {
   }

   private static Integer a(zd var0) {
      double var1 = 0.0;
      Integer var3 = null;

      for (int var4 = var0.a(); var4 < var0.e() - 1; var4++) {
         double var5;
         if ((var5 = var0.a(0, var4)) < var1) {
            var1 = var5;
            var3 = var4;
         }
      }

      return var3;
   }

   private Integer a(zd var1, int var2) {
      ArrayList var3 = new ArrayList();
      double var4 = Double.MAX_VALUE;

      for (int var6 = var1.a(); var6 < var1.f(); var6++) {
         double var7 = var1.a(var6, var1.e() - 1);
         double var9;
         if (Cq.a(var9 = var1.a(var6, var2), 0.0, this.d) > 0) {
            double var11;
            int var13;
            if ((var13 = Double.compare(var11 = var7 / var9, var4)) == 0) {
               var3.add(var6);
            } else if (var13 < 0) {
               var4 = var11;
               (var3 = new ArrayList()).add(var6);
            }
         }
      }

      if (var3.size() == 0) {
         return null;
      }

      if (var3.size() > 1) {
         if (var1.a > 0) {
            for (Integer var16 : var3) {
               for (int var8 = 0; var8 < var1.a; var8++) {
                  int var19 = var8 + var1.g();
                  if (Cq.b(var1.a(var16, var19), 1.0, this.d) && var16.equals(var1.a(var19))) {
                     return var16;
                  }
               }
            }
         }

         if (this.f() < this.e() / 2) {
            Integer var15 = null;
            int var17 = var1.e();
            int var18 = var1.a();
            int var20 = var1.e() - 1;

            for (Integer var21 : var3) {
               for (int var12 = var18; var12 < var20 && !var21.equals(var15); var12++) {
                  Integer var22;
                  if ((var22 = var1.a(var12)) != null && var22.equals(var21) && var12 < var17) {
                     var17 = var12;
                     var15 = var21;
                  }
               }
            }

            return var15;
         }
      }

      return (Integer)var3.get(0);
   }

   private void b(zd var1) {
      this.g();
      zd var3 = var1;
      double var11 = 0.0;
      Integer var13 = null;

      for (int var14 = var3.a(); var14 < var3.e() - 1; var14++) {
         double var15;
         if ((var15 = var3.a(0, var14)) < var11) {
            var11 = var15;
            var13 = var14;
         }
      }

      Integer var2 = var13;
      int var27 = var2;
      zd var4 = var1;
      zc var22 = this;
      ArrayList var12 = new ArrayList();
      double var28 = Double.MAX_VALUE;

      for (int var29 = var4.a(); var29 < var4.f(); var29++) {
         double var16 = var4.a(var29, var4.e() - 1);
         double var18;
         if (Cq.a(var18 = var4.a(var29, var27), 0.0, var22.d) > 0) {
            int var5;
            double var20;
            if ((var5 = Double.compare(var20 = var16 / var18, var28)) == 0) {
               var12.add(var29);
            } else if (var5 < 0) {
               var28 = var20;
               (var12 = new ArrayList()).add(var29);
            }
         }
      }

      Integer var10000;
      if (var12.size() == 0) {
         var10000 = null;
      } else {
         label102: {
            if (var12.size() > 1) {
               if (var4.a > 0) {
                  for (Integer var32 : var12) {
                     for (int var17 = 0; var17 < var4.a; var17++) {
                        int var35 = var17 + var4.g();
                        if (Cq.b(var4.a(var32, var35), 1.0, var22.d) && var32.equals(var4.a(var35))) {
                           var10000 = var32;
                           break label102;
                        }
                     }
                  }
               }

               if (var22.f() < var22.e() / 2) {
                  Integer var31 = null;
                  int var33 = var4.e();
                  int var34 = var4.a();
                  int var36 = var4.e() - 1;

                  for (Integer var37 : var12) {
                     for (int var21 = var34; var21 < var36 && !var37.equals(var31); var21++) {
                        Integer var26;
                        if ((var26 = var4.a(var21)) != null && var26.equals(var37) && var21 < var33) {
                           var33 = var21;
                           var31 = var37;
                        }
                     }
                  }

                  var10000 = var31;
                  break label102;
               }
            }

            var10000 = (Integer)var12.get(0);
         }
      }

      Integer var23 = var10000;
      if (var10000 == null) {
         throw new ze();
      }

      double var24 = var1.a(var23, var2);
      var1.a(var23, var24);

      for (int var25 = 0; var25 < var1.f(); var25++) {
         if (var25 != var23) {
            double var7 = var1.a(var25, var2);
            var1.a(var25, var23, var7);
         }
      }
   }

   private void c(zd var1) {
      if (var1.a != 0) {
         while (!var1.c()) {
            this.b(var1);
         }

         if (!Cq.b(var1.a(0, var1.h()), 0.0, this.c)) {
            throw new za();
         }
      }
   }

   @Override
   public final yp h() {
      zd var1 = new zd(this.c(), this.d(), this.b(), this.a(), this.c, this.d);
      zd var3 = var1;
      zc var2 = this;
      if (var3.a != 0) {
         while (!var3.c()) {
            var2.b(var3);
         }

         if (!Cq.b(var3.a(0, var3.h()), 0.0, var2.c)) {
            throw new za();
         }
      }

      var1.b();

      while (!var1.c()) {
         this.b(var1);
      }

      return var1.d();
   }
}
