package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class sC<T extends kN<T>> extends su<T> implements Serializable {
   private static int a;
   private static final long b = -4602336630143123183L;
   private final T[][] c;
   private final int d;
   private final int e;
   private final int f;
   private final int g;

   public sC(kM<T> var1, int var2, int var3) {
      super(var1, var2, var3);
      this.d = var2;
      this.e = var3;
      this.f = (var2 + 36 - 1) / 36;
      this.g = (var3 + 36 - 1) / 36;
      var3 = var3;
      var2 = var2;
      var1 = var1;
      int var4 = (var2 + 36 - 1) / 36;
      int var5 = (var3 + 36 - 1) / 36;
      kN[][] var6 = Ch.a(var1, var4 * var5, -1);
      int var7 = 0;

      for (int var8 = 0; var8 < var4; var8++) {
         int var9;
         var9 = BY.a((var9 = var8 * 36) + 36, var2) - var9;

         for (int var10 = 0; var10 < var5; var10++) {
            int var11;
            var11 = BY.a((var11 = var10 * 36) + 36, var3) - var11;
            var6[var7] = Ch.a(var1, var9 * var11);
            var7++;
         }
      }

      this.c = (T[][])var6;
   }

   public sC(T[][] var1) {
      this(var1.length, var1[0].length, (T[][])b((T[][])var1), false);
   }

   private sC(int var1, int var2, T[][] var3, boolean var4) {
      super(a((T[][])var3), var1, var2);
      this.d = var1;
      this.e = var2;
      this.f = (var1 + 36 - 1) / 36;
      this.g = (var2 + 36 - 1) / 36;
      this.c = (T[][])var3;
      var1 = 0;

      for (int var7 = 0; var7 < this.f; var7++) {
         var4 = this.j(var7);

         for (int var5 = 0; var5 < this.g; var1++) {
            if (var3[var1].length != var4 * this.k(var5)) {
               throw new oS(var3[var1].length, var4 * this.k(var5));
            }

            var5++;
         }
      }
   }

   private static <T extends kN<T>> T[][] b(T[][] var0) {
      int var1 = var0.length;
      int var2 = var0[0].length;
      int var3 = (var1 + 36 - 1) / 36;
      int var4 = (var2 + 36 - 1) / 36;

      for (int var5 = 0; var5 < var0.length; var5++) {
         int var6;
         if ((var6 = var0[var5].length) != var2) {
            throw new oS(var2, var6);
         }
      }

      kM var18;
      kN[][] var19 = Ch.a(var18 = a((T[][])var0), var3 * var4, -1);
      int var7 = 0;

      for (int var8 = 0; var8 < var3; var8++) {
         int var9;
         int var10;
         int var11 = (var10 = BY.a((var9 = var8 * 36) + 36, var1)) - var9;

         for (int var12 = 0; var12 < var4; var12++) {
            int var13;
            int var14 = BY.a((var13 = var12 * 36) + 36, var2) - var13;
            kN[] var15 = Ch.a(var18, var11 * var14);
            var19[var7] = var15;
            int var16 = 0;

            for (int var17 = var9; var17 < var10; var17++) {
               System.arraycopy(var0[var17], var13, var15, var16, var14);
               var16 += var14;
            }

            var7++;
         }
      }

      return (T[][])var19;
   }

   private static <T extends kN<T>> T[][] a(kM<T> var0, int var1, int var2) {
      int var3 = (var1 + 36 - 1) / 36;
      int var4 = (var2 + 36 - 1) / 36;
      kN[][] var5 = Ch.a(var0, var3 * var4, -1);
      int var6 = 0;

      for (int var7 = 0; var7 < var3; var7++) {
         int var8;
         var8 = BY.a((var8 = var7 * 36) + 36, var1) - var8;

         for (int var9 = 0; var9 < var4; var9++) {
            int var10;
            var10 = BY.a((var10 = var9 * 36) + 36, var2) - var10;
            var5[var6] = Ch.a(var0, var8 * var10);
            var6++;
         }
      }

      return (T[][])var5;
   }

   @Override
   public final sQ<T> a(int var1, int var2) {
      return new sC<>(this.a(), var1, var2);
   }

   @Override
   public final sQ<T> b() {
      sC var1 = new sC<>(this.a(), this.d, this.e);

      for (int var2 = 0; var2 < this.c.length; var2++) {
         System.arraycopy(this.c[var2], 0, var1.c[var2], 0, this.c[var2].length);
      }

      return var1;
   }

   @Override
   public final sQ<T> a(sQ<T> var1) {
      try {
         sC var16 = (sC)var1;
         sC var15 = this;
         this.e(var16);
         sC var17 = new sC(var15.a(), var15.d, var15.e);

         for (int var18 = 0; var18 < var17.c.length; var18++) {
            kN[] var19 = var17.c[var18];
            kN[] var20 = var15.c[var18];
            kN[] var22 = var16.c[var18];

            for (int var23 = 0; var23 < var19.length; var23++) {
               var19[var23] = var20[var23].a(var22[var23]);
            }
         }

         return var17;
      } catch (ClassCastException var14) {
         this.e(var1);
         sC var2 = new sC<>(this.a(), this.d, this.e);
         int var3 = 0;

         for (int var4 = 0; var4 < var2.f; var4++) {
            for (int var5 = 0; var5 < var2.g; var5++) {
               kN[] var6 = var2.c[var3];
               kN[] var7 = this.c[var3];
               int var8;
               int var9 = BY.a((var8 = var4 * 36) + 36, this.d);
               int var10;
               int var11 = BY.a((var10 = var5 * 36) + 36, this.e);
               int var12 = 0;

               for (int var21 = var8; var21 < var9; var21++) {
                  for (int var13 = var10; var13 < var11; var13++) {
                     var6[var12] = var7[var12].a(var1.b(var21, var13));
                     var12++;
                  }
               }

               var3++;
            }
         }

         return var2;
      }
   }

   private sC<T> a(sC<T> var1) {
      this.e(var1);
      sC var2 = new sC<>(this.a(), this.d, this.e);

      for (int var3 = 0; var3 < var2.c.length; var3++) {
         kN[] var4 = var2.c[var3];
         kN[] var5 = this.c[var3];
         kN[] var6 = var1.c[var3];

         for (int var7 = 0; var7 < var4.length; var7++) {
            var4[var7] = var5[var7].a(var6[var7]);
         }
      }

      return var2;
   }

   @Override
   public final sQ<T> b(sQ<T> var1) {
      try {
         sC var16 = (sC)var1;
         sC var15 = this;
         this.f(var16);
         sC var17 = new sC(var15.a(), var15.d, var15.e);

         for (int var18 = 0; var18 < var17.c.length; var18++) {
            kN[] var19 = var17.c[var18];
            kN[] var20 = var15.c[var18];
            kN[] var22 = var16.c[var18];

            for (int var23 = 0; var23 < var19.length; var23++) {
               var19[var23] = var20[var23].b(var22[var23]);
            }
         }

         return var17;
      } catch (ClassCastException var14) {
         this.f(var1);
         sC var2 = new sC<>(this.a(), this.d, this.e);
         int var3 = 0;

         for (int var4 = 0; var4 < var2.f; var4++) {
            for (int var5 = 0; var5 < var2.g; var5++) {
               kN[] var6 = var2.c[var3];
               kN[] var7 = this.c[var3];
               int var8;
               int var9 = BY.a((var8 = var4 * 36) + 36, this.d);
               int var10;
               int var11 = BY.a((var10 = var5 * 36) + 36, this.e);
               int var12 = 0;

               for (int var21 = var8; var21 < var9; var21++) {
                  for (int var13 = var10; var13 < var11; var13++) {
                     var6[var12] = var7[var12].b(var1.b(var21, var13));
                     var12++;
                  }
               }

               var3++;
            }
         }

         return var2;
      }
   }

   private sC<T> b(sC<T> var1) {
      this.f(var1);
      sC var2 = new sC<>(this.a(), this.d, this.e);

      for (int var3 = 0; var3 < var2.c.length; var3++) {
         kN[] var4 = var2.c[var3];
         kN[] var5 = this.c[var3];
         kN[] var6 = var1.c[var3];

         for (int var7 = 0; var7 < var4.length; var7++) {
            var4[var7] = var5[var7].b(var6[var7]);
         }
      }

      return var2;
   }

   @Override
   public final sQ<T> a(T var1) {
      sC var2 = new sC<>(this.a(), this.d, this.e);

      for (int var3 = 0; var3 < var2.c.length; var3++) {
         kN[] var4 = var2.c[var3];
         kN[] var5 = this.c[var3];

         for (int var6 = 0; var6 < var4.length; var6++) {
            var4[var6] = var5[var6].a(var1);
         }
      }

      return var2;
   }

   @Override
   public final sQ<T> b(T var1) {
      sC var2 = new sC<>(this.a(), this.d, this.e);

      for (int var3 = 0; var3 < var2.c.length; var3++) {
         kN[] var4 = var2.c[var3];
         kN[] var5 = this.c[var3];

         for (int var6 = 0; var6 < var4.length; var6++) {
            var4[var6] = var5[var6].c(var1);
         }
      }

      return var2;
   }

   @Override
   public final sQ<T> c(sQ<T> var1) {
      try {
         sC var30 = (sC)var1;
         sC var29 = this;
         this.g(var30);
         sC var31 = new sC(var29.a(), var29.d, var30.e);
         kN var32 = var29.a().a();
         int var33 = 0;

         for (int var34 = 0; var34 < var31.f; var34++) {
            int var35;
            int var36 = BY.a((var35 = var34 * 36) + 36, var29.d);

            for (int var37 = 0; var37 < var31.g; var37++) {
               int var38;
               int var39;
               int var40;
               int var41 = (var40 = (var39 = (var38 = var31.k(var37)) + var38) + var38) + var38;
               kN[] var42 = var31.c[var33];

               for (int var43 = 0; var43 < var29.g; var43++) {
                  int var44 = var29.k(var43);
                  kN[] var45 = var29.c[var34 * var29.g + var43];
                  kN[] var46 = var30.c[var43 * var30.g + var37];
                  int var47 = 0;

                  for (int var48 = var35; var48 < var36; var48++) {
                     int var49;
                     int var50 = (var49 = (var48 - var35) * var44) + var44;

                     for (int var24 = 0; var24 < var38; var24++) {
                        kN var25 = var32;
                        int var26 = var49;

                        int var27;
                        for (var27 = var24; var26 < var50 - 3; var27 += var41) {
                           var25 = var25.a(var45[var26].c(var46[var27]))
                              .a(var45[var26 + 1].c(var46[var27 + var38]))
                              .a(var45[var26 + 2].c(var46[var27 + var39]))
                              .a(var45[var26 + 3].c(var46[var27 + var40]));
                           var26 += 4;
                        }

                        while (var26 < var50) {
                           var25 = var25.a(var45[var26++].c(var46[var27]));
                           var27 += var38;
                        }

                        var42[var47] = var42[var47].a(var25);
                        var47++;
                     }
                  }
               }

               var33++;
            }
         }

         return var31;
      } catch (ClassCastException var28) {
         this.g(var1);
         sC var2 = new sC<>(this.a(), this.d, var1.g());
         kN var3 = this.a().a();
         int var4 = 0;

         for (int var5 = 0; var5 < var2.f; var5++) {
            int var6;
            int var7 = BY.a((var6 = var5 * 36) + 36, this.d);

            for (int var8 = 0; var8 < var2.g; var8++) {
               int var9;
               int var10 = BY.a((var9 = var8 * 36) + 36, var1.g());
               kN[] var11 = var2.c[var4];

               for (int var12 = 0; var12 < this.g; var12++) {
                  int var13 = this.k(var12);
                  kN[] var14 = this.c[var5 * this.g + var12];
                  int var15 = var12 * 36;
                  int var16 = 0;

                  for (int var17 = var6; var17 < var7; var17++) {
                     int var18;
                     int var19 = (var18 = (var17 - var6) * var13) + var13;

                     for (int var20 = var9; var20 < var10; var20++) {
                        kN var21 = var3;
                        int var22 = var15;

                        for (int var23 = var18; var23 < var19; var23++) {
                           var21 = var21.a(var14[var23].c(var1.b(var22, var20)));
                           var22++;
                        }

                        var11[var16] = var11[var16].a(var21);
                        var16++;
                     }
                  }
               }

               var4++;
            }
         }

         return var2;
      }
   }

   private sC<T> c(sC<T> var1) {
      this.g(var1);
      sC var2 = new sC<>(this.a(), this.d, var1.e);
      kN var3 = this.a().a();
      int var4 = 0;

      for (int var5 = 0; var5 < var2.f; var5++) {
         int var6;
         int var7 = BY.a((var6 = var5 * 36) + 36, this.d);

         for (int var8 = 0; var8 < var2.g; var8++) {
            int var9;
            int var10;
            int var11;
            int var12 = (var11 = (var10 = (var9 = var2.k(var8)) + var9) + var9) + var9;
            kN[] var13 = var2.c[var4];

            for (int var14 = 0; var14 < this.g; var14++) {
               int var15 = this.k(var14);
               kN[] var16 = this.c[var5 * this.g + var14];
               kN[] var17 = var1.c[var14 * var1.g + var8];
               int var18 = 0;

               for (int var19 = var6; var19 < var7; var19++) {
                  int var20;
                  int var21 = (var20 = (var19 - var6) * var15) + var15;

                  for (int var22 = 0; var22 < var9; var22++) {
                     kN var23 = var3;
                     int var24 = var20;

                     int var25;
                     for (var25 = var22; var24 < var21 - 3; var25 += var12) {
                        var23 = var23.a(var16[var24].c(var17[var25]))
                           .a(var16[var24 + 1].c(var17[var25 + var9]))
                           .a(var16[var24 + 2].c(var17[var25 + var10]))
                           .a(var16[var24 + 3].c(var17[var25 + var11]));
                        var24 += 4;
                     }

                     while (var24 < var21) {
                        var23 = var23.a(var16[var24++].c(var17[var25]));
                        var25 += var9;
                     }

                     var13[var18] = var13[var18].a(var23);
                     var18++;
                  }
               }
            }

            var4++;
         }
      }

      return var2;
   }

   @Override
   public final T[][] c() {
      kN[][] var1 = Ch.a(this.a(), this.d, this.e);
      int var2 = this.e - (this.g - 1) * 36;

      for (int var3 = 0; var3 < this.f; var3++) {
         int var4;
         int var5 = BY.a((var4 = var3 * 36) + 36, this.d);
         byte var6 = 0;
         int var7 = 0;

         for (int var12 = var4; var12 < var5; var12++) {
            kN[] var8 = var1[var12];
            int var9 = var3 * this.g;
            byte var10 = 0;

            for (int var11 = 0; var11 < this.g - 1; var11++) {
               System.arraycopy(this.c[var9++], var6, var8, var10, 36);
               var10 += 36;
            }

            System.arraycopy(this.c[var9], var7, var8, var10, var2);
            var6 += 36;
            var7 += var2;
         }
      }

      return (T[][])var1;
   }

   @Override
   public final sQ<T> a(int var1, int var2, int var3, int var4) {
      this.b(var1, var2, var3, var4);
      sC var18 = new sC<>(this.a(), var2 - var1 + 1, var4 - var3 + 1);
      var4 = var1 / 36;
      var1 %= 36;
      int var5 = var3 / 36;
      var3 %= 36;
      var4 = var4;

      for (int var6 = 0; var6 < var18.f; var6++) {
         int var7 = var18.j(var6);
         int var8 = var5;

         for (int var9 = 0; var9 < var18.g; var9++) {
            int var10 = var18.k(var9);
            int var11 = var6 * var18.g + var9;
            kN[] var22 = var18.c[var11];
            int var12 = var4 * this.g + var8;
            int var13 = this.k(var8);
            int var14 = var7 + var1 - 36;
            int var15 = var10 + var3 - 36;
            if (var14 > 0) {
               if (var15 > 0) {
                  int var16 = this.k(var8 + 1);
                  a(this.c[var12], var13, var1, 36, var3, 36, (T[])var22, var10, 0, 0);
                  a(this.c[var12 + 1], var16, var1, 36, 0, var15, (T[])var22, var10, 0, var10 - var15);
                  a(this.c[var12 + this.g], var13, 0, var14, var3, 36, (T[])var22, var10, var7 - var14, 0);
                  a(this.c[var12 + this.g + 1], var16, 0, var14, 0, var15, (T[])var22, var10, var7 - var14, var10 - var15);
               } else {
                  a(this.c[var12], var13, var1, 36, var3, var10 + var3, (T[])var22, var10, 0, 0);
                  a(this.c[var12 + this.g], var13, 0, var14, var3, var10 + var3, (T[])var22, var10, var7 - var14, 0);
               }
            } else if (var15 > 0) {
               int var23 = this.k(var8 + 1);
               a(this.c[var12], var13, var1, var7 + var1, var3, 36, (T[])var22, var10, 0, 0);
               a(this.c[var12 + 1], var23, var1, var7 + var1, 0, var15, (T[])var22, var10, 0, var10 - var15);
            } else {
               a(this.c[var12], var13, var1, var7 + var1, var3, var10 + var3, (T[])var22, var10, 0, 0);
            }

            var8++;
         }

         var4++;
      }

      return var18;
   }

   private static void a(T[] var0, int var1, int var2, int var3, int var4, int var5, T[] var6, int var7, int var8, int var9) {
      var5 -= var4;
      var4 = var2 * var1 + var4;
      var8 = var8 * var7 + var9;

      for (int var10 = var2; var10 < var3; var10++) {
         System.arraycopy(var0, var4, var6, var8, var5);
         var4 += var1;
         var8 += var7;
      }
   }

   @Override
   public final void a(T[][] var1, int var2, int var3) {
      Ci.a(var1);
      int var4;
      if ((var4 = var1[0].length) == 0) {
         throw new pf(px.AT_LEAST_ONE_COLUMN);
      }

      int var5 = var2 + var1.length - 1;
      int var6 = var3 + var4 - 1;
      this.b(var2, var5, var3, var6);
      kN[][] var7 = var1;
      int var8 = var1.length;

      for (int var9 = 0; var9 < var8; var9++) {
         kN[] var10;
         if ((var10 = var7[var9]).length != var4) {
            throw new oS(var4, var10.length);
         }
      }

      int var21 = var2 / 36;
      var8 = (var5 + 36) / 36;
      int var25 = var3 / 36;
      int var26 = (var6 + 36) / 36;

      for (int var20 = var21; var20 < var8; var20++) {
         int var22 = this.j(var20);
         int var11 = var20 * 36;
         int var12 = BY.b(var2, var11);
         int var23 = BY.a(var5 + 1, var11 + var22);

         for (int var13 = var25; var13 < var26; var13++) {
            int var14 = this.k(var13);
            int var15 = var13 * 36;
            int var16 = BY.b(var3, var15);
            int var17 = BY.a(var6 + 1, var15 + var14) - var16;
            kN[] var18 = this.c[var20 * this.g + var13];

            for (int var19 = var12; var19 < var23; var19++) {
               System.arraycopy(var1[var19 - var2], var16 - var3, var18, (var19 - var11) * var14 + (var16 - var15), var17);
            }
         }
      }
   }

   @Override
   public final sQ<T> b(int var1) {
      this.h(var1);
      sC var2 = new sC<>(this.a(), 1, this.e);
      int var3 = var1 / 36;
      var1 -= var3 * 36;
      int var4 = 0;
      int var5 = 0;
      kN[] var6 = var2.c[0];

      for (int var7 = 0; var7 < this.g; var7++) {
         int var8 = this.k(var7);
         kN[] var9 = this.c[var3 * this.g + var7];
         int var10 = var6.length - var5;
         if (var8 > var10) {
            System.arraycopy(var9, var1 * var8, var6, var5, var10);
            var6 = var2.c[++var4];
            System.arraycopy(var9, var1 * var8, var6, 0, var8 - var10);
            var5 = var8 - var10;
         } else {
            System.arraycopy(var9, var1 * var8, var6, var5, var8);
            var5 += var8;
         }
      }

      return var2;
   }

   @Override
   public final void a(int var1, sQ<T> var2) {
      try {
         sC var5 = (sC)var2;
         int var4 = var1;
         sC var3 = this;
         this.h(var4);
         int var6 = var3.e;
         if (var5.d == 1 && var5.e == var6) {
            var6 = var4 / 36;
            var4 -= var6 * 36;
            int var7 = 0;
            int var8 = 0;
            kN[] var9 = var5.c[0];

            for (int var10 = 0; var10 < var3.g; var10++) {
               int var11 = var3.k(var10);
               kN[] var12 = var3.c[var6 * var3.g + var10];
               int var13 = var9.length - var8;
               if (var11 > var13) {
                  System.arraycopy(var9, var8, var12, var4 * var11, var13);
                  System.arraycopy(var9 = var5.c[++var7], 0, var12, var4 * var11, var11 - var13);
                  var8 = var11 - var13;
               } else {
                  System.arraycopy(var9, var8, var12, var4 * var11, var11);
                  var8 += var11;
               }
            }
         } else {
            throw new tc(var5.d, var5.e, 1, var6);
         }
      } catch (ClassCastException var14) {
         super.a(var1, var2);
      }
   }

   private void a(int var1, sC<T> var2) {
      this.h(var1);
      int var3 = this.e;
      if (var2.d == 1 && var2.e == var3) {
         var3 = var1 / 36;
         var1 -= var3 * 36;
         int var4 = 0;
         int var5 = 0;
         kN[] var6 = var2.c[0];

         for (int var7 = 0; var7 < this.g; var7++) {
            int var8 = this.k(var7);
            kN[] var9 = this.c[var3 * this.g + var7];
            int var10 = var6.length - var5;
            if (var8 > var10) {
               System.arraycopy(var6, var5, var9, var1 * var8, var10);
               System.arraycopy(var6 = var2.c[++var4], 0, var9, var1 * var8, var8 - var10);
               var5 = var8 - var10;
            } else {
               System.arraycopy(var6, var5, var9, var1 * var8, var8);
               var5 += var8;
            }
         }
      } else {
         throw new tc(var2.d, var2.e, 1, var3);
      }
   }

   @Override
   public final sQ<T> c(int var1) {
      this.i(var1);
      sC var2 = new sC<>(this.a(), this.d, 1);
      int var3 = var1 / 36;
      var1 -= var3 * 36;
      int var4 = this.k(var3);
      int var5 = 0;
      int var6 = 0;
      kN[] var7 = var2.c[0];

      for (int var8 = 0; var8 < this.f; var8++) {
         int var9 = this.j(var8);
         kN[] var10 = this.c[var8 * this.g + var3];

         for (int var11 = 0; var11 < var9; var11++) {
            if (var6 >= var7.length) {
               var7 = var2.c[++var5];
               var6 = 0;
            }

            var7[var6++] = var10[var11 * var4 + var1];
         }
      }

      return var2;
   }

   @Override
   public final void b(int var1, sQ<T> var2) {
      try {
         sC var5 = (sC)var2;
         int var4 = var1;
         sC var3 = this;
         this.i(var4);
         int var6 = var3.d;
         if (var5.d == var6 && var5.e == 1) {
            var6 = var4 / 36;
            var4 -= var6 * 36;
            int var7 = var3.k(var6);
            int var8 = 0;
            int var9 = 0;
            kN[] var10 = var5.c[0];

            for (int var11 = 0; var11 < var3.f; var11++) {
               int var12 = var3.j(var11);
               kN[] var13 = var3.c[var11 * var3.g + var6];

               for (int var14 = 0; var14 < var12; var14++) {
                  if (var9 >= var10.length) {
                     var10 = var5.c[++var8];
                     var9 = 0;
                  }

                  var13[var14 * var7 + var4] = var10[var9++];
               }
            }
         } else {
            throw new tc(var5.d, var5.e, var6, 1);
         }
      } catch (ClassCastException var15) {
         super.b(var1, var2);
      }
   }

   private void b(int var1, sC<T> var2) {
      this.i(var1);
      int var3 = this.d;
      if (var2.d == var3 && var2.e == 1) {
         var3 = var1 / 36;
         var1 -= var3 * 36;
         int var4 = this.k(var3);
         int var5 = 0;
         int var6 = 0;
         kN[] var7 = var2.c[0];

         for (int var8 = 0; var8 < this.f; var8++) {
            int var9 = this.j(var8);
            kN[] var10 = this.c[var8 * this.g + var3];

            for (int var11 = 0; var11 < var9; var11++) {
               if (var6 >= var7.length) {
                  var7 = var2.c[++var5];
                  var6 = 0;
               }

               var10[var11 * var4 + var1] = var7[var6++];
            }
         }
      } else {
         throw new tc(var2.d, var2.e, var3, 1);
      }
   }

   @Override
   public final sT<T> d(int var1) {
      this.h(var1);
      kN[] var2 = Ch.a(this.a(), this.e);
      int var3 = var1 / 36;
      var1 -= var3 * 36;
      int var4 = 0;

      for (int var5 = 0; var5 < this.g; var5++) {
         int var6 = this.k(var5);
         kN[] var7;
         System.arraycopy(var7 = this.c[var3 * this.g + var5], var1 * var6, var2, var4, var6);
         var4 += var6;
      }

      return new sz<>(this.a(), (T[])var2, false);
   }

   @Override
   public final void a(int var1, sT<T> var2) {
      try {
         sz var6;
         kN[] var5 = (var6 = (sz)var2).a;
         int var4 = var1;
         sC var3 = this;
         this.h(var4);
         int var13 = var3.e;
         if (var5.length != var13) {
            throw new tc(1, var5.length, 1, var13);
         }

         int var14 = var4 / 36;
         var4 -= var14 * 36;
         int var7 = 0;

         for (int var8 = 0; var8 < var3.g; var8++) {
            int var9 = var3.k(var8);
            kN[] var10 = var3.c[var14 * var3.g + var8];
            System.arraycopy(var5, var7, var10, var4 * var9, var9);
            var7 += var9;
         }
      } catch (ClassCastException var11) {
         super.a(var1, var2);
      }
   }

   @Override
   public final sT<T> e(int var1) {
      this.i(var1);
      kN[] var2 = Ch.a(this.a(), this.d);
      int var3 = var1 / 36;
      var1 -= var3 * 36;
      int var4 = this.k(var3);
      int var5 = 0;

      for (int var6 = 0; var6 < this.f; var6++) {
         int var7 = this.j(var6);
         kN[] var8 = this.c[var6 * this.g + var3];

         for (int var9 = 0; var9 < var7; var9++) {
            var2[var5++] = var8[var9 * var4 + var1];
         }
      }

      return new sz<>(this.a(), (T[])var2, false);
   }

   @Override
   public final void b(int var1, sT<T> var2) {
      try {
         sz var6;
         kN[] var5 = (var6 = (sz)var2).a;
         int var4 = var1;
         sC var3 = this;
         this.i(var4);
         int var15 = var3.d;
         if (var5.length != var15) {
            throw new tc(var5.length, 1, var15, 1);
         }

         int var16 = var4 / 36;
         var4 -= var16 * 36;
         int var7 = var3.k(var16);
         int var8 = 0;

         for (int var9 = 0; var9 < var3.f; var9++) {
            int var10 = var3.j(var9);
            kN[] var11 = var3.c[var9 * var3.g + var16];

            for (int var12 = 0; var12 < var10; var12++) {
               var11[var12 * var7 + var4] = var5[var8++];
            }
         }
      } catch (ClassCastException var13) {
         super.b(var1, var2);
      }
   }

   @Override
   public final T[] f(int var1) {
      this.h(var1);
      kN[] var2 = Ch.a(this.a(), this.e);
      int var3 = var1 / 36;
      var1 -= var3 * 36;
      int var4 = 0;

      for (int var5 = 0; var5 < this.g; var5++) {
         int var6 = this.k(var5);
         kN[] var7;
         System.arraycopy(var7 = this.c[var3 * this.g + var5], var1 * var6, var2, var4, var6);
         var4 += var6;
      }

      return (T[])var2;
   }

   @Override
   public final void a(int var1, T[] var2) {
      this.h(var1);
      int var3 = this.e;
      if (var2.length != var3) {
         throw new tc(1, var2.length, 1, var3);
      }

      var3 = var1 / 36;
      var1 -= var3 * 36;
      int var4 = 0;

      for (int var5 = 0; var5 < this.g; var5++) {
         int var6 = this.k(var5);
         kN[] var7 = this.c[var3 * this.g + var5];
         System.arraycopy(var2, var4, var7, var1 * var6, var6);
         var4 += var6;
      }
   }

   @Override
   public final T[] g(int var1) {
      this.i(var1);
      kN[] var2 = Ch.a(this.a(), this.d);
      int var3 = var1 / 36;
      var1 -= var3 * 36;
      int var4 = this.k(var3);
      int var5 = 0;

      for (int var6 = 0; var6 < this.f; var6++) {
         int var7 = this.j(var6);
         kN[] var8 = this.c[var6 * this.g + var3];

         for (int var9 = 0; var9 < var7; var9++) {
            var2[var5++] = var8[var9 * var4 + var1];
         }
      }

      return (T[])var2;
   }

   @Override
   public final void b(int var1, T[] var2) {
      this.i(var1);
      int var3 = this.d;
      if (var2.length != var3) {
         throw new tc(var2.length, 1, var3, 1);
      }

      var3 = var1 / 36;
      var1 -= var3 * 36;
      int var4 = this.k(var3);
      int var5 = 0;

      for (int var6 = 0; var6 < this.f; var6++) {
         int var7 = this.j(var6);
         kN[] var8 = this.c[var6 * this.g + var3];

         for (int var9 = 0; var9 < var7; var9++) {
            var8[var9 * var4 + var1] = var2[var5++];
         }
      }
   }

   @Override
   public final T b(int var1, int var2) {
      this.h(var1);
      this.i(var2);
      int var3 = var1 / 36;
      int var4 = var2 / 36;
      var1 = (var1 - var3 * 36) * this.k(var4) + (var2 - var4 * 36);
      return this.c[var3 * this.g + var4][var1];
   }

   @Override
   public final void a(int var1, int var2, T var3) {
      this.h(var1);
      this.i(var2);
      int var4 = var1 / 36;
      int var5 = var2 / 36;
      var1 = (var1 - var4 * 36) * this.k(var5) + (var2 - var5 * 36);
      this.c[var4 * this.g + var5][var1] = (T)var3;
   }

   @Override
   public final void b(int var1, int var2, T var3) {
      this.h(var1);
      this.i(var2);
      int var4 = var1 / 36;
      int var5 = var2 / 36;
      var1 = (var1 - var4 * 36) * this.k(var5) + (var2 - var5 * 36);
      kN[] var7;
      (var7 = this.c[var4 * this.g + var5])[var1] = var7[var1].a(var3);
   }

   @Override
   public final void c(int var1, int var2, T var3) {
      this.h(var1);
      this.i(var2);
      int var4 = var1 / 36;
      int var5 = var2 / 36;
      var1 = (var1 - var4 * 36) * this.k(var5) + (var2 - var5 * 36);
      kN[] var7;
      (var7 = this.c[var4 * this.g + var5])[var1] = var7[var1].c(var3);
   }

   @Override
   public final sQ<T> d() {
      int var1 = this.d;
      int var2 = this.e;
      sC var16 = new sC<>(this.a(), var2, var1);
      var2 = 0;

      for (int var3 = 0; var3 < this.g; var3++) {
         for (int var4 = 0; var4 < this.f; var4++) {
            kN[] var5 = var16.c[var2];
            kN[] var6 = this.c[var4 * this.g + var3];
            int var7;
            int var8 = BY.a((var7 = var3 * 36) + 36, this.e);
            int var9;
            int var10 = BY.a((var9 = var4 * 36) + 36, this.d);
            int var11 = 0;

            for (int var12 = var7; var12 < var8; var12++) {
               int var13 = var8 - var7;
               int var14 = var12 - var7;

               for (int var15 = var9; var15 < var10; var15++) {
                  var5[var11] = var6[var14];
                  var11++;
                  var14 += var13;
               }
            }

            var2++;
         }
      }

      return var16;
   }

   @Override
   public final int f() {
      return this.d;
   }

   @Override
   public final int g() {
      return this.e;
   }

   @Override
   public final T[] b(T[] var1) {
      if (var1.length != this.e) {
         throw new oS(var1.length, this.e);
      }

      kN[] var2 = Ch.a(this.a(), this.d);
      kN var3 = this.a().a();

      for (int var4 = 0; var4 < this.f; var4++) {
         int var5;
         int var6 = BY.a((var5 = var4 * 36) + 36, this.d);

         for (int var7 = 0; var7 < this.g; var7++) {
            kN[] var8 = this.c[var4 * this.g + var7];
            int var9;
            int var10 = BY.a((var9 = var7 * 36) + 36, this.e);
            int var11 = 0;

            for (int var12 = var5; var12 < var6; var12++) {
               kN var13 = var3;

               int var14;
               for (var14 = var9; var14 < var10 - 3; var14 += 4) {
                  var13 = var13.a(var8[var11].c(var1[var14]))
                     .a(var8[var11 + 1].c(var1[var14 + 1]))
                     .a(var8[var11 + 2].c(var1[var14 + 2]))
                     .a(var8[var11 + 3].c(var1[var14 + 3]));
                  var11 += 4;
               }

               while (var14 < var10) {
                  var13 = var13.a(var8[var11++].c(var1[var14++]));
               }

               var2[var12] = var2[var12].a(var13);
            }
         }
      }

      return (T[])var2;
   }

   @Override
   public final T[] c(T[] var1) {
      if (var1.length != this.d) {
         throw new oS(var1.length, this.d);
      }

      kN[] var2 = Ch.a(this.a(), this.e);
      kN var3 = this.a().a();

      for (int var4 = 0; var4 < this.g; var4++) {
         int var5;
         int var6;
         int var7;
         int var8 = (var7 = (var6 = (var5 = this.k(var4)) + var5) + var5) + var5;
         int var9;
         int var10 = BY.a((var9 = var4 * 36) + 36, this.e);

         for (int var11 = 0; var11 < this.f; var11++) {
            kN[] var12 = this.c[var11 * this.g + var4];
            int var13;
            int var14 = BY.a((var13 = var11 * 36) + 36, this.d);

            for (int var15 = var9; var15 < var10; var15++) {
               int var16 = var15 - var9;
               kN var17 = var3;

               int var18;
               for (var18 = var13; var18 < var14 - 3; var18 += 4) {
                  var17 = var17.a(var12[var16].c(var1[var18]))
                     .a(var12[var16 + var5].c(var1[var18 + 1]))
                     .a(var12[var16 + var6].c(var1[var18 + 2]))
                     .a(var12[var16 + var7].c(var1[var18 + 3]));
                  var16 += var8;
               }

               while (var18 < var14) {
                  var17 = var17.a(var12[var16].c(var1[var18++]));
                  var16 += var5;
               }

               var2[var15] = var2[var15].a(var17);
            }
         }
      }

      return (T[])var2;
   }

   @Override
   public final T a(sR<T> var1) {
      for (int var2 = 0; var2 < this.f; var2++) {
         int var3;
         int var4 = BY.a((var3 = var2 * 36) + 36, this.d);

         for (int var5 = var3; var5 < var4; var5++) {
            for (int var6 = 0; var6 < this.g; var6++) {
               int var7 = this.k(var6);
               int var8;
               int var9 = BY.a((var8 = var6 * 36) + 36, this.e);
               kN[] var10 = this.c[var2 * this.g + var6];
               var7 = (var5 - var3) * var7;

               for (int var12 = var8; var12 < var9; var12++) {
                  var10[var7] = var1.a(var5, var12, (T)var10[var7]);
                  var7++;
               }
            }
         }
      }

      return (T)var1.b();
   }

   @Override
   public final T a(sS<T> var1) {
      var1.a(this.d, this.e, 0, 0);

      for (int var2 = 0; var2 < this.f; var2++) {
         int var3;
         int var4 = BY.a((var3 = var2 * 36) + 36, this.d);

         for (int var5 = var3; var5 < var4; var5++) {
            for (int var6 = 0; var6 < this.g; var6++) {
               int var7 = this.k(var6);
               int var8;
               int var9 = BY.a((var8 = var6 * 36) + 36, this.e);
               kN[] var10 = this.c[var2 * this.g + var6];
               var7 = (var5 - var3) * var7;

               for (int var12 = var8; var12 < var9; var12++) {
                  var1.a(var5, var12, (T)var10[var7]);
                  var7++;
               }
            }
         }
      }

      return (T)var1.a();
   }

   @Override
   public final T a(sR<T> var1, int var2, int var3, int var4, int var5) {
      this.b(var2, var3, var4, var5);

      for (int var6 = var2 / 36; var6 < 1 + var3 / 36; var6++) {
         int var7 = var6 * 36;
         int var8 = BY.b(var2, var7);
         int var9 = BY.a((var6 + 1) * 36, var3 + 1);

         for (int var16 = var8; var16 < var9; var16++) {
            for (int var10 = var4 / 36; var10 < 1 + var5 / 36; var10++) {
               int var11 = this.k(var10);
               int var12 = var10 * 36;
               int var13 = BY.b(var4, var12);
               int var14 = BY.a((var10 + 1) * 36, var5 + 1);
               kN[] var15 = this.c[var6 * this.g + var10];
               var11 = (var16 - var7) * var11 + var13 - var12;

               for (int var18 = var13; var18 < var14; var18++) {
                  var15[var11] = var1.a(var16, var18, (T)var15[var11]);
                  var11++;
               }
            }
         }
      }

      return (T)var1.b();
   }

   @Override
   public final T a(sS<T> var1, int var2, int var3, int var4, int var5) {
      this.b(var2, var3, var4, var5);
      var1.a(this.d, this.e, var2, var4);

      for (int var6 = var2 / 36; var6 < 1 + var3 / 36; var6++) {
         int var7 = var6 * 36;
         int var8 = BY.b(var2, var7);
         int var9 = BY.a((var6 + 1) * 36, var3 + 1);

         for (int var16 = var8; var16 < var9; var16++) {
            for (int var10 = var4 / 36; var10 < 1 + var5 / 36; var10++) {
               int var11 = this.k(var10);
               int var12 = var10 * 36;
               int var13 = BY.b(var4, var12);
               int var14 = BY.a((var10 + 1) * 36, var5 + 1);
               kN[] var15 = this.c[var6 * this.g + var10];
               var11 = (var16 - var7) * var11 + var13 - var12;

               for (int var18 = var13; var18 < var14; var18++) {
                  var1.a(var16, var18, (T)var15[var11]);
                  var11++;
               }
            }
         }
      }

      return (T)var1.a();
   }

   @Override
   public final T a(sR<T> var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < this.f; var3++) {
         int var4;
         int var5 = BY.a((var4 = var3 * 36) + 36, this.d);

         for (int var6 = 0; var6 < this.g; var6++) {
            int var7;
            int var8 = BY.a((var7 = var6 * 36) + 36, this.e);
            kN[] var9 = this.c[var2];
            int var10 = 0;

            for (int var11 = var4; var11 < var5; var11++) {
               for (int var12 = var7; var12 < var8; var12++) {
                  var9[var10] = var1.a(var11, var12, (T)var9[var10]);
                  var10++;
               }
            }

            var2++;
         }
      }

      return (T)var1.b();
   }

   @Override
   public final T c(sS<T> var1) {
      var1.a(this.d, this.e, 0, 0);
      int var2 = 0;

      for (int var3 = 0; var3 < this.f; var3++) {
         int var4;
         int var5 = BY.a((var4 = var3 * 36) + 36, this.d);

         for (int var6 = 0; var6 < this.g; var6++) {
            int var7;
            int var8 = BY.a((var7 = var6 * 36) + 36, this.e);
            kN[] var9 = this.c[var2];
            int var10 = 0;

            for (int var11 = var4; var11 < var5; var11++) {
               for (int var12 = var7; var12 < var8; var12++) {
                  var1.a(var11, var12, (T)var9[var10]);
                  var10++;
               }
            }

            var2++;
         }
      }

      return (T)var1.a();
   }

   @Override
   public final T c(sR<T> var1, int var2, int var3, int var4, int var5) {
      this.b(var2, var3, var4, var5);

      for (int var6 = var2 / 36; var6 < 1 + var3 / 36; var6++) {
         int var7 = var6 * 36;
         int var8 = BY.b(var2, var7);
         int var9 = BY.a((var6 + 1) * 36, var3 + 1);

         for (int var10 = var4 / 36; var10 < 1 + var5 / 36; var10++) {
            int var11 = this.k(var10);
            int var12 = var10 * 36;
            int var13 = BY.b(var4, var12);
            int var14 = BY.a((var10 + 1) * 36, var5 + 1);
            kN[] var15 = this.c[var6 * this.g + var10];

            for (int var16 = var8; var16 < var9; var16++) {
               int var17 = (var16 - var7) * var11 + var13 - var12;

               for (int var18 = var13; var18 < var14; var18++) {
                  var15[var17] = var1.a(var16, var18, (T)var15[var17]);
                  var17++;
               }
            }
         }
      }

      return (T)var1.b();
   }

   @Override
   public final T a(sS<T> var1, int var2, int var3, int var4, int var5) {
      this.b(var2, var3, var4, var5);
      var1.a(this.d, this.e, var2, var4);

      for (int var6 = var2 / 36; var6 < 1 + var3 / 36; var6++) {
         int var7 = var6 * 36;
         int var8 = BY.b(var2, var7);
         int var9 = BY.a((var6 + 1) * 36, var3 + 1);

         for (int var10 = var4 / 36; var10 < 1 + var5 / 36; var10++) {
            int var11 = this.k(var10);
            int var12 = var10 * 36;
            int var13 = BY.b(var4, var12);
            int var14 = BY.a((var10 + 1) * 36, var5 + 1);
            kN[] var15 = this.c[var6 * this.g + var10];

            for (int var16 = var8; var16 < var9; var16++) {
               int var17 = (var16 - var7) * var11 + var13 - var12;

               for (int var18 = var13; var18 < var14; var18++) {
                  var1.a(var16, var18, (T)var15[var17]);
                  var17++;
               }
            }
         }
      }

      return (T)var1.a();
   }

   private int j(int var1) {
      return var1 == this.f - 1 ? this.d - var1 * 36 : 36;
   }

   private int k(int var1) {
      return var1 == this.g - 1 ? this.e - var1 * 36 : 36;
   }
}
