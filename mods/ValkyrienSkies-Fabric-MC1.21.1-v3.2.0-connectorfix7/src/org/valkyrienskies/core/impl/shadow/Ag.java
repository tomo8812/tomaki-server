package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Deprecated
public final class Ag<T extends Ac<T>> {
   private final Random a;
   private final int b;

   private Ag(Random var1) {
      this(var1, Ag.a.LARGEST_VARIANCE$338d5e9a);
   }

   private Ag(Random var1, int var2) {
      this.a = var1;
      this.b = var2;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private List<Ab<T>> a(Collection<T> var1, int var2, int var3, int var4) {
      ArrayList var5 = null;
      double var6 = Double.POSITIVE_INFINITY;

      for (int var8 = 0; var8 < var3; var8++) {
         int var12 = var4;
         int var11 = var2;
         Collection var10 = var1;
         Ag var9 = this;
         Ci.a(var10);
         if (var10.size() < var11) {
            throw new pn(var10.size(), var11, false);
         }

         Random var33 = var9.a;
         int var18 = var11;
         ArrayList var15 = var10;
         int var19;
         List var34;
         boolean[] var20 = new boolean[var19 = (var34 = Collections.unmodifiableList(new ArrayList<>(var15))).size()];
         ArrayList var21 = new ArrayList();
         int var22 = var33.nextInt(var19);
         Ac var23 = (Ac)var34.get(var22);
         var21.add(new Ab((T)var23));
         var20[var22] = true;
         double[] var40 = new double[var19];

         for (int var41 = 0; var41 < var19; var41++) {
            if (var41 != var22) {
               double var42 = var23.a(var34.get(var41));
               var40[var41] = var42 * var42;
            }
         }

         while (var21.size() < var18) {
            double var100 = 0.0;

            for (int var43 = 0; var43 < var19; var43++) {
               if (!var20[var43]) {
                  var100 += var40[var43];
               }
            }

            double var104 = var33.nextDouble() * var100;
            var11 = -1;
            double var46 = 0.0;

            for (int var13 = 0; var13 < var19; var13++) {
               if (!var20[var13] && (var46 += var40[var13]) >= var104) {
                  var11 = var13;
                  break;
               }
            }

            if (var11 == -1) {
               for (int var61 = var19 - 1; var61 >= 0; var61--) {
                  if (!var20[var61]) {
                     var11 = var61;
                     break;
                  }
               }
            }

            if (var11 < 0) {
               break;
            }

            Ac var62 = (Ac)var34.get(var11);
            var21.add(new Ab((T)var62));
            var20[var11] = true;
            if (var21.size() < var18) {
               for (int var57 = 0; var57 < var19; var57++) {
                  double var50;
                  double var52;
                  if (!var20[var57] && (var52 = (var50 = var62.a(var34.get(var57))) * var50) < var40[var57]) {
                     var40[var57] = var52;
                  }
               }
            }
         }

         ArrayList var58 = var21;
         int[] var63 = new int[var10.size()];
         a(var58, var10, var63);
         var12 = var12 < 0 ? Integer.MAX_VALUE : var12;

         for (int var14 = 0; var14 < var12; var14++) {
            boolean var67 = false;
            ArrayList var16 = new ArrayList();
            Iterator var17 = var58.iterator();

            while (var17.hasNext()) {
               Ab var74;
               Ac var75;
               if (!(var74 = (Ab)var17.next()).a.isEmpty()) {
                  var75 = var74.b.a(var74.a);
               } else {
                  switch (<unrepresentable>.a[var9.b - 1]) {
                     case 1:
                        ArrayList var77 = var58;
                        Ag var70 = var9;
                        double var95 = Double.NEGATIVE_INFINITY;
                        Ab var81 = null;
                        Iterator var84 = var77.iterator();

                        while (var84.hasNext()) {
                           Ab var88;
                           if (!(var88 = (Ab)var84.next()).a.isEmpty()) {
                              Ac var90 = var88.b;
                              AM var92 = new AM();

                              for (Ac var102 : var88.a) {
                                 var92.a(var102.a(var90));
                              }

                              double var99;
                              if ((var99 = var92.c()) > var95) {
                                 var95 = var99;
                                 var81 = var88;
                              }
                           }
                        }

                        if (var81 == null) {
                           throw new oR(px.EMPTY_CLUSTER_IN_K_MEANS);
                        }

                        List var85;
                        var75 = (Ac)(var85 = var81.a).remove(var70.a.nextInt(var85.size()));
                        break;
                     case 2:
                        ArrayList var76 = var58;
                        Ag var69 = var9;
                        int var94 = 0;
                        Ab var96 = null;
                        Iterator var79 = var76.iterator();

                        while (var79.hasNext()) {
                           Ab var83;
                           int var87;
                           if ((var87 = (var83 = (Ab)var79.next()).a.size()) > var94) {
                              var94 = var87;
                              var96 = var83;
                           }
                        }

                        if (var96 == null) {
                           throw new oR(px.EMPTY_CLUSTER_IN_K_MEANS);
                        }

                        List var80;
                        var75 = (Ac)(var80 = var96.a).remove(var69.a.nextInt(var80.size()));
                        break;
                     case 3:
                        var15 = var58;
                        double var93 = Double.NEGATIVE_INFINITY;
                        Ab var78 = null;
                        int var82 = -1;
                        Iterator var86 = var15.iterator();

                        while (var86.hasNext()) {
                           Ab var89;
                           var23 = (var89 = (Ab)var86.next()).b;
                           List var97 = var89.a;

                           for (int var101 = 0; var101 < var97.size(); var101++) {
                              double var103;
                              if ((var103 = ((Ac)var97.get(var101)).a(var23)) > var93) {
                                 var93 = var103;
                                 var78 = var89;
                                 var82 = var101;
                              }
                           }
                        }

                        if (var78 == null) {
                           throw new oR(px.EMPTY_CLUSTER_IN_K_MEANS);
                        }

                        var75 = var78.a.remove(var82);
                        break;
                     default:
                        throw new oR(px.EMPTY_CLUSTER_IN_K_MEANS);
                  }

                  var67 = true;
               }

               var16.add(new Ab((T)var75));
            }

            int var73 = a(var16, var10, var63);
            var58 = var16;
            if (var73 == 0 && !var67) {
               break;
            }
         }

         ArrayList var54 = var58;
         double var55 = 0.0;
         Iterator var60 = var54.iterator();

         while (var60.hasNext()) {
            Ab var64;
            if (!(var64 = (Ab)var60.next()).a.isEmpty()) {
               Ac var66 = var64.b;
               AM var71 = new AM();

               for (Ac var72 : var64.a) {
                  var71.a(var72.a(var66));
               }

               var55 += var71.c();
            }
         }

         if (var55 <= var6) {
            var5 = var54;
            var6 = var55;
         }
      }

      return var5;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private List<Ab<T>> a(Collection<T> var1, int var2, int var3) {
      Ci.a(var1);
      if (var1.size() < var2) {
         throw new pn(var1.size(), var2, false);
      }

      List var11 = a(var1, var2, this.a);
      int[] var4 = new int[var1.size()];
      a(var11, var1, var4);
      var3 = var3 < 0 ? Integer.MAX_VALUE : var3;

      for (int var5 = 0; var5 < var3; var5++) {
         boolean var6 = false;
         ArrayList var7 = new ArrayList();
         Iterator var8 = var11.iterator();

         while (var8.hasNext()) {
            Ab var9;
            Ac var14;
            if ((var9 = (Ab)var8.next()).a.isEmpty()) {
               switch (<unrepresentable>.a[this.b - 1]) {
                  case 1:
                     var14 = this.a(var11);
                     break;
                  case 2:
                     var14 = this.b(var11);
                     break;
                  case 3:
                     var14 = c(var11);
                     break;
                  default:
                     throw new oR(px.EMPTY_CLUSTER_IN_K_MEANS);
               }

               var6 = true;
            } else {
               var14 = var9.b.a(var9.a);
            }

            var7.add(new Ab((T)var14));
         }

         int var13 = a(var7, var1, var4);
         var11 = var7;
         if (var13 == 0 && !var6) {
            return var11;
         }
      }

      return var11;
   }

   private static <T extends Ac<T>> int a(List<Ab<T>> var0, Collection<T> var1, int[] var2) {
      int var3 = 0;
      int var4 = 0;

      for (Ac var5 : var1) {
         int var6;
         if ((var6 = a(var0, (T)var5)) != var2[var4]) {
            var3++;
         }

         ((Ab)var0.get(var6)).a((T)var5);
         var2[var4++] = var6;
      }

      return var3;
   }

   private static <T extends Ac<T>> List<Ab<T>> a(Collection<T> var0, int var1, Random var2) {
      int var3;
      List var23;
      boolean[] var4 = new boolean[var3 = (var23 = Collections.unmodifiableList(new ArrayList<>(var0))).size()];
      ArrayList var5 = new ArrayList();
      int var6 = var2.nextInt(var3);
      Ac var7 = (Ac)var23.get(var6);
      var5.add(new Ab((T)var7));
      var4[var6] = true;
      double[] var8 = new double[var3];

      for (int var10 = 0; var10 < var3; var10++) {
         if (var10 != var6) {
            double var11 = var7.a(var23.get(var10));
            var8[var10] = var11 * var11;
         }
      }

      while (var5.size() < var1) {
         double var29 = 0.0;

         for (int var12 = 0; var12 < var3; var12++) {
            if (!var4[var12]) {
               var29 += var8[var12];
            }
         }

         double var30 = var2.nextDouble() * var29;
         var6 = -1;
         double var15 = 0.0;

         for (int var26 = 0; var26 < var3; var26++) {
            if (!var4[var26] && (var15 += var8[var26]) >= var30) {
               var6 = var26;
               break;
            }
         }

         if (var6 == -1) {
            for (int var27 = var3 - 1; var27 >= 0; var27--) {
               if (!var4[var27]) {
                  var6 = var27;
                  break;
               }
            }
         }

         if (var6 < 0) {
            break;
         }

         var7 = (Ac)var23.get(var6);
         var5.add(new Ab((T)var7));
         var4[var6] = true;
         if (var5.size() < var1) {
            for (int var25 = 0; var25 < var3; var25++) {
               double var19;
               double var21;
               if (!var4[var25] && (var21 = (var19 = var7.a(var23.get(var25))) * var19) < var8[var25]) {
                  var8[var25] = var21;
               }
            }
         }
      }

      return var5;
   }

   private T a(Collection<Ab<T>> var1) {
      double var2 = Double.NEGATIVE_INFINITY;
      Ab var4 = null;
      Iterator var11 = var1.iterator();

      while (var11.hasNext()) {
         Ab var5;
         if (!(var5 = (Ab)var11.next()).a.isEmpty()) {
            Ac var6 = var5.b;
            AM var7 = new AM();

            for (Ac var10 : var5.a) {
               var7.a(var10.a(var6));
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
         return (T)(var1 = var4.a).remove(this.a.nextInt(var1.size()));
      }
   }

   private T b(Collection<Ab<T>> var1) {
      int var2 = 0;
      Ab var3 = null;
      Iterator var6 = var1.iterator();

      while (var6.hasNext()) {
         Ab var4;
         int var5;
         if ((var5 = (var4 = (Ab)var6.next()).a.size()) > var2) {
            var2 = var5;
            var3 = var4;
         }
      }

      if (var3 == null) {
         throw new oR(px.EMPTY_CLUSTER_IN_K_MEANS);
      } else {
         return (T)(var1 = var3.a).remove(this.a.nextInt(var1.size()));
      }
   }

   private static T c(Collection<Ab<T>> var0) {
      double var1 = Double.NEGATIVE_INFINITY;
      Ab var3 = null;
      int var4 = -1;
      Iterator var12 = var0.iterator();

      while (var12.hasNext()) {
         Ab var5;
         Ac var6 = (var5 = (Ab)var12.next()).b;
         List var7 = var5.a;

         for (int var8 = 0; var8 < var7.size(); var8++) {
            double var10;
            if ((var10 = ((Ac)var7.get(var8)).a(var6)) > var1) {
               var1 = var10;
               var3 = var5;
               var4 = var8;
            }
         }
      }

      if (var3 == null) {
         throw new oR(px.EMPTY_CLUSTER_IN_K_MEANS);
      } else {
         return var3.a.remove(var4);
      }
   }

   private static <T extends Ac<T>> int a(Collection<Ab<T>> var0, T var1) {
      double var2 = Double.MAX_VALUE;
      int var4 = 0;
      int var5 = 0;

      for (Ab var6 : var0) {
         double var8;
         if ((var8 = var1.a(var6.b)) < var2) {
            var2 = var8;
            var5 = var4;
         }

         var4++;
      }

      return var5;
   }

   public enum a {
      public static final int LARGEST_VARIANCE$338d5e9a = 1;
      public static final int LARGEST_POINTS_NUMBER$338d5e9a = 2;
      public static final int FARTHEST_POINT$338d5e9a = 3;
      public static final int ERROR$338d5e9a = 4;
      // $VF: synthetic field
      private static final int[] $VALUES$3d2c3d55 = new int[]{
         LARGEST_VARIANCE$338d5e9a, LARGEST_POINTS_NUMBER$338d5e9a, FARTHEST_POINT$338d5e9a, ERROR$338d5e9a
      };

      public static int[] values$2d4b7354() {
         return (int[])$VALUES$3d2c3d55.clone();
      }

      private a(String var1, int var2) {
         super(var1, var2);
      }
   }
}
