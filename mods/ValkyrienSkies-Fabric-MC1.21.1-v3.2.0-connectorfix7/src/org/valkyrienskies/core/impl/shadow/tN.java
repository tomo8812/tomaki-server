package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class tN<T extends tL> extends tM<T> {
   private final double a;
   private final int b;

   private tN(double var1, int var3) {
      this(var1, var3, new tY());
   }

   private tN(double var1, int var3, tY var4) {
      super(var4);
      if (var1 < 0.0) {
         throw new pj(var1);
      }

      if (var3 < 0) {
         throw new pj(var3);
      }

      this.a = var1;
      this.b = var3;
   }

   private double b() {
      return this.a;
   }

   private int c() {
      return this.b;
   }

   @Override
   public final List<tK<T>> a(Collection<T> var1) {
      Ci.a(var1);
      ArrayList var2 = new ArrayList();
      HashMap var3 = new HashMap();

      for (tL var5 : var1) {
         if (var3.get(var5) == null) {
            List var6;
            if ((var6 = this.a((T)var5, var1)).size() >= this.b) {
               tK var7 = new tK();
               HashMap var10 = var3;
               Collection var9 = var1;
               List var8 = var6;
               tL var18 = var5;
               tK var17 = var7;
               tN var16 = this;
               var17.a(var18);
               var10.put(var18, tN.a.PART_OF_CLUSTER);
               ArrayList var19 = new ArrayList(var8);

               for (int var21 = 0; var21 < var19.size(); var21++) {
                  tL var11 = (tL)var19.get(var21);
                  tN.a var12;
                  List var13;
                  if ((var12 = (tN.a)var10.get(var11)) == null && (var13 = var16.a(var11, var9)).size() >= var16.b) {
                     var13 = var13;
                     ArrayList var20 = var19;
                     HashSet var14 = new HashSet(var20);

                     for (tL var15 : var13) {
                        if (!var14.contains(var15)) {
                           var20.add(var15);
                        }
                     }

                     var19 = var20;
                  }

                  if (var12 != tN.a.PART_OF_CLUSTER) {
                     var10.put(var11, tN.a.PART_OF_CLUSTER);
                     var17.a(var11);
                  }
               }

               var2.add(var17);
            } else {
               var3.put(var5, tN.a.NOISE);
            }
         }
      }

      return var2;
   }

   private tK<T> a(tK<T> var1, T var2, List<T> var3, Collection<T> var4, Map<tL, tN.a> var5) {
      var1.a(var2);
      var5.put(var2, tN.a.PART_OF_CLUSTER);
      List var9 = new ArrayList(var3);

      for (int var10 = 0; var10 < var9.size(); var10++) {
         tL var6 = (tL)var9.get(var10);
         tN.a var7;
         List var8;
         if ((var7 = (tN.a)var5.get(var6)) == null && (var8 = this.a((T)var6, var4)).size() >= this.b) {
            var9 = a(var9, var8);
         }

         if (var7 != tN.a.PART_OF_CLUSTER) {
            var5.put(var6, tN.a.PART_OF_CLUSTER);
            var1.a(var6);
         }
      }

      return var1;
   }

   private List<T> a(T var1, Collection<T> var2) {
      ArrayList var3 = new ArrayList();

      for (tL var4 : var2) {
         if (var1 != var4 && this.a(var4, var1) <= this.a) {
            var3.add(var4);
         }
      }

      return var3;
   }

   private static List<T> a(List<T> var0, List<T> var1) {
      HashSet var2 = new HashSet(var0);

      for (tL var3 : var1) {
         if (!var2.contains(var3)) {
            var0.add(var3);
         }
      }

      return var0;
   }

   enum a {
      NOISE,
      PART_OF_CLUSTER;
   }
}
