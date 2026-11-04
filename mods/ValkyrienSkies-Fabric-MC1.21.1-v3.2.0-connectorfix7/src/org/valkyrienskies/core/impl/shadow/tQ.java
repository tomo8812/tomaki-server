package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class tQ<T extends tL> extends tM<T> {
   private final int a;
   private final int b;
   private final zF c;
   private final int d;

   private tQ(int var1) {
      this(var1, -1);
   }

   private tQ(int var1, int var2) {
      this(var1, -1, new tY());
   }

   private tQ(int var1, int var2, tY var3) {
      this(var1, var2, var3, new zy());
   }

   private tQ(int var1, int var2, tW var3, zy var4) {
      this(var1, var2, var3, var4, tQ.a.LARGEST_VARIANCE$5922d8c5);
   }

   private tQ(int var1, int var2, tW var3, zF var4, int var5) {
      super(var3);
      this.a = var1;
      this.b = var2;
      this.c = var4;
      this.d = var5;
   }

   private int b() {
      return this.a;
   }

   private int c() {
      return this.b;
   }

   private zF d() {
      return this.c;
   }

   private int e() {
      return this.d;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final List<tJ<T>> a(Collection<T> var1) {
      Ci.a(var1);
      if (var1.size() < this.a) {
         throw new pn(var1.size(), this.a, false);
      }

      ArrayList var10 = var1;
      tQ var9 = this;
      List var13;
      int var14;
      boolean[] var11 = new boolean[var14 = (var13 = Collections.unmodifiableList(new ArrayList<>(var10))).size()];
      ArrayList var12 = new ArrayList();
      int var48 = var9.c.nextInt(var14);
      tL var15 = (tL)var13.get(var48);
      var12.add(new tJ(var15));
      var11[var48] = true;
      double[] var6 = new double[var14];

      for (int var20 = 0; var20 < var14; var20++) {
         if (var20 != var48) {
            double var21 = var9.a(var15, (tL)var13.get(var20));
            var6[var20] = var21 * var21;
         }
      }

      while (var12.size() < var9.a) {
         double var75 = 0.0;

         for (int var22 = 0; var22 < var14; var22++) {
            if (!var11[var22]) {
               var75 += var6[var22];
            }
         }

         double var81 = var9.c.nextDouble() * var75;
         int var2 = -1;
         double var25 = 0.0;

         for (int var3 = 0; var3 < var14; var3++) {
            if (!var11[var3] && (var25 += var6[var3]) >= var81) {
               var2 = var3;
               break;
            }
         }

         if (var2 == -1) {
            for (int var35 = var14 - 1; var35 >= 0; var35--) {
               if (!var11[var35]) {
                  var2 = var35;
                  break;
               }
            }
         }

         if (var2 < 0) {
            break;
         }

         tL var36 = (tL)var13.get(var2);
         var12.add(new tJ(var36));
         var11[var2] = true;
         if (var12.size() < var9.a) {
            for (int var33 = 0; var33 < var14; var33++) {
               double var29;
               double var31;
               if (!var11[var33] && (var31 = (var29 = var9.a(var36, (tL)var13.get(var33))) * var29) < var6[var33]) {
                  var6[var33] = var31;
               }
            }
         }
      }

      ArrayList var34 = var12;
      int[] var37 = new int[var1.size()];
      this.a(var34, var1, var37);
      int var4 = this.b < 0 ? Integer.MAX_VALUE : this.b;

      for (int var5 = 0; var5 < var4; var5++) {
         boolean var38 = false;
         ArrayList var7 = new ArrayList();
         Iterator var8 = var34.iterator();

         while (var8.hasNext()) {
            tJ var42;
            tL var44;
            if ((var42 = (tJ)var8.next()).b.isEmpty()) {
               switch (<unrepresentable>.a[this.d - 1]) {
                  case 1:
                     var10 = var34;
                     var9 = this;
                     double var69 = Double.NEGATIVE_INFINITY;
                     tJ var62 = null;
                     Iterator var65 = var10.iterator();

                     while (var65.hasNext()) {
                        tJ var56;
                        if (!(var56 = (tJ)var65.next()).b.isEmpty()) {
                           var15 = var56.a;
                           AM var40 = new AM();

                           for (tL var80 : var56.b) {
                              var40.a(var9.a(var80, var15));
                           }

                           double var78;
                           if ((var78 = var40.c()) > var69) {
                              var69 = var78;
                              var62 = var56;
                           }
                        }
                     }

                     if (var62 == null) {
                        throw new oR(px.EMPTY_CLUSTER_IN_K_MEANS);
                     }

                     List var66;
                     var44 = (tL)(var66 = var62.b).remove(var9.c.nextInt(var66.size()));
                     break;
                  case 2:
                     var10 = var34;
                     var9 = this;
                     int var68 = 0;
                     tK var71 = null;
                     Iterator var60 = var10.iterator();

                     while (var60.hasNext()) {
                        int var54;
                        tK var64;
                        if ((var54 = (var64 = (tK)var60.next()).b.size()) > var68) {
                           var68 = var54;
                           var71 = var64;
                        }
                     }

                     if (var71 == null) {
                        throw new oR(px.EMPTY_CLUSTER_IN_K_MEANS);
                     }

                     List var61;
                     var44 = (tL)(var61 = var71.b).remove(var9.c.nextInt(var61.size()));
                     break;
                  case 3:
                     var10 = var34;
                     var9 = this;
                     double var67 = Double.NEGATIVE_INFINITY;
                     tJ var59 = null;
                     int var63 = -1;
                     Iterator var52 = var10.iterator();

                     while (var52.hasNext()) {
                        tJ var73;
                        tL var39 = (var73 = (tJ)var52.next()).a;
                        List var76 = var73.b;

                        for (int var79 = 0; var79 < var76.size(); var79++) {
                           double var82;
                           if ((var82 = var9.a((tL)var76.get(var79), var39)) > var67) {
                              var67 = var82;
                              var59 = var73;
                              var63 = var79;
                           }
                        }
                     }

                     if (var59 == null) {
                        throw new oR(px.EMPTY_CLUSTER_IN_K_MEANS);
                     }

                     var44 = var59.b.remove(var63);
                     break;
                  default:
                     throw new oR(px.EMPTY_CLUSTER_IN_K_MEANS);
               }

               var38 = true;
            } else {
               List var10000 = var42.b;
               int var49 = var42.a.a().length;
               List var43 = var10000;
               double[] var70 = new double[var49];
               Iterator var57 = var43.iterator();

               while (var57.hasNext()) {
                  double[] var50 = ((tL)var57.next()).a();

                  for (int var72 = 0; var72 < var70.length; var72++) {
                     var70[var72] += var50[var72];
                  }
               }

               for (int var58 = 0; var58 < var70.length; var58++) {
                  var70[var58] /= var43.size();
               }

               var44 = new tO(var70);
            }

            var7.add(new tJ(var44));
         }

         int var41 = this.a(var7, var1, var37);
         var34 = var7;
         if (var41 == 0 && !var38) {
            return var34;
         }
      }

      return var34;
   }

   private int a(List<tJ<T>> var1, Collection<T> var2, int[] var3) {
      int var4 = 0;
      int var5 = 0;

      for (tL var6 : var2) {
         int var7;
         if ((var7 = this.a(var1, (T)var6)) != var3[var5]) {
            var4++;
         }

         ((tJ)var1.get(var7)).a(var6);
         var3[var5++] = var7;
      }

      return var4;
   }

   private List<tJ<T>> b(Collection<T> var1) {
      int var2;
      List var22;
      boolean[] var3 = new boolean[var2 = (var22 = Collections.unmodifiableList(new ArrayList<>(var1))).size()];
      ArrayList var4 = new ArrayList();
      int var5 = this.c.nextInt(var2);
      tL var6 = (tL)var22.get(var5);
      var4.add(new tJ(var6));
      var3[var5] = true;
      double[] var7 = new double[var2];

      for (int var9 = 0; var9 < var2; var9++) {
         if (var9 != var5) {
            double var10 = this.a(var6, (tL)var22.get(var9));
            var7[var9] = var10 * var10;
         }
      }

      while (var4.size() < this.a) {
         double var28 = 0.0;

         for (int var11 = 0; var11 < var2; var11++) {
            if (!var3[var11]) {
               var28 += var7[var11];
            }
         }

         double var29 = this.c.nextDouble() * var28;
         var5 = -1;
         double var14 = 0.0;

         for (int var25 = 0; var25 < var2; var25++) {
            if (!var3[var25] && (var14 += var7[var25]) >= var29) {
               var5 = var25;
               break;
            }
         }

         if (var5 == -1) {
            for (int var26 = var2 - 1; var26 >= 0; var26--) {
               if (!var3[var26]) {
                  var5 = var26;
                  break;
               }
            }
         }

         if (var5 < 0) {
            break;
         }

         var6 = (tL)var22.get(var5);
         var4.add(new tJ(var6));
         var3[var5] = true;
         if (var4.size() < this.a) {
            for (int var24 = 0; var24 < var2; var24++) {
               double var18;
               double var20;
               if (!var3[var24] && (var20 = (var18 = this.a(var6, (tL)var22.get(var24))) * var18) < var7[var24]) {
                  var7[var24] = var20;
               }
            }
         }
      }

      return var4;
   }

   private T c(Collection<tJ<T>> var1) {
      double var2 = Double.NEGATIVE_INFINITY;
      tJ var4 = null;
      Iterator var11 = var1.iterator();

      while (var11.hasNext()) {
         tJ var5;
         if (!(var5 = (tJ)var11.next()).b.isEmpty()) {
            tL var6 = var5.a;
            AM var7 = new AM();

            for (tL var10 : var5.b) {
               var7.a(this.a(var10, var6));
            }

            double var13;
            if ((var13 = var7.c()) > var2) {
               var2 = var13;
               var4 = var5;
            }
         }
      }

      if (var4 == null) {
         throw new oR(px.EMPTY_CLUSTER_IN_K_MEANS);
      } else {
         return (T)(var1 = var4.b).remove(this.c.nextInt(var1.size()));
      }
   }

   private T d(Collection<? extends tK<T>> var1) {
      int var2 = 0;
      tK var3 = null;
      Iterator var6 = var1.iterator();

      while (var6.hasNext()) {
         tK var4;
         int var5;
         if ((var5 = (var4 = (tK)var6.next()).b.size()) > var2) {
            var2 = var5;
            var3 = var4;
         }
      }

      if (var3 == null) {
         throw new oR(px.EMPTY_CLUSTER_IN_K_MEANS);
      } else {
         return (T)(var1 = var3.b).remove(this.c.nextInt(var1.size()));
      }
   }

   private T e(Collection<tJ<T>> var1) {
      double var2 = Double.NEGATIVE_INFINITY;
      tJ var4 = null;
      int var5 = -1;
      Iterator var13 = var1.iterator();

      while (var13.hasNext()) {
         tJ var6;
         tL var7 = (var6 = (tJ)var13.next()).a;
         List var8 = var6.b;

         for (int var9 = 0; var9 < var8.size(); var9++) {
            double var11;
            if ((var11 = this.a((tL)var8.get(var9), var7)) > var2) {
               var2 = var11;
               var4 = var6;
               var5 = var9;
            }
         }
      }

      if (var4 == null) {
         throw new oR(px.EMPTY_CLUSTER_IN_K_MEANS);
      } else {
         return var4.b.remove(var5);
      }
   }

   private int a(Collection<tJ<T>> var1, T var2) {
      double var3 = Double.MAX_VALUE;
      int var5 = 0;
      int var6 = 0;

      for (tJ var7 : var1) {
         double var9;
         if ((var9 = this.a(var2, var7.a)) < var3) {
            var3 = var9;
            var6 = var5;
         }

         var5++;
      }

      return var6;
   }

   private static tL a(Collection<T> var0, int var1) {
      double[] var5 = new double[var1];
      Iterator var2 = var0.iterator();

      while (var2.hasNext()) {
         double[] var3 = ((tL)var2.next()).a();

         for (int var4 = 0; var4 < var5.length; var4++) {
            var5[var4] += var3[var4];
         }
      }

      for (int var6 = 0; var6 < var5.length; var6++) {
         var5[var6] /= var0.size();
      }

      return new tO(var5);
   }

   public enum a {
      public static final int LARGEST_VARIANCE$5922d8c5 = 1;
      public static final int LARGEST_POINTS_NUMBER$5922d8c5 = 2;
      public static final int FARTHEST_POINT$5922d8c5 = 3;
      public static final int ERROR$5922d8c5 = 4;
      // $VF: synthetic field
      private static final int[] $VALUES$672fd5c0 = new int[]{
         LARGEST_VARIANCE$5922d8c5, LARGEST_POINTS_NUMBER$5922d8c5, FARTHEST_POINT$5922d8c5, ERROR$5922d8c5
      };

      public static int[] values$790d2c01() {
         return (int[])$VALUES$672fd5c0.clone();
      }

      private a(String var1, int var2) {
         super(var1, var2);
      }
   }
}
