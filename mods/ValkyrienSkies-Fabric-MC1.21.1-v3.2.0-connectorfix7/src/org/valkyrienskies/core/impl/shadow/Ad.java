package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Deprecated
public final class Ad<T extends Ac<T>> {
   private final double a;
   private final int b;

   private Ad(double var1, int var3) {
      if (var1 < 0.0) {
         throw new pj(var1);
      }

      if (var3 < 0) {
         throw new pj(var3);
      }

      this.a = var1;
      this.b = var3;
   }

   private double a() {
      return this.a;
   }

   private int b() {
      return this.b;
   }

   private List<Ab<T>> a(Collection<T> var1) {
      Ci.a(var1);
      ArrayList var2 = new ArrayList();
      HashMap var3 = new HashMap();

      for (Ac var5 : var1) {
         if (var3.get(var5) == null) {
            List var6;
            if ((var6 = this.a((T)var5, var1)).size() >= this.b) {
               Ab var7 = new Ab(null);
               HashMap var10 = var3;
               Collection var9 = var1;
               List var8 = var6;
               Ac var18 = var5;
               Ab var17 = var7;
               Ad var16 = this;
               var17.a((T)var18);
               var10.put(var18, Ad.a.PART_OF_CLUSTER);
               ArrayList var19 = new ArrayList(var8);

               for (int var21 = 0; var21 < var19.size(); var21++) {
                  Ac var11 = (Ac)var19.get(var21);
                  Ad.a var12;
                  List var13;
                  if ((var12 = (Ad.a)var10.get(var11)) == null && (var13 = var16.a((T)var11, var9)).size() >= var16.b) {
                     var13 = var13;
                     ArrayList var20 = var19;
                     HashSet var14 = new HashSet(var20);

                     for (Ac var15 : var13) {
                        if (!var14.contains(var15)) {
                           var20.add(var15);
                        }
                     }

                     var19 = var20;
                  }

                  if (var12 != Ad.a.PART_OF_CLUSTER) {
                     var10.put(var11, Ad.a.PART_OF_CLUSTER);
                     var17.a((T)var11);
                  }
               }

               var2.add(var17);
            } else {
               var3.put(var5, Ad.a.NOISE);
            }
         }
      }

      return var2;
   }

   private Ab<T> a(Ab<T> var1, T var2, List<T> var3, Collection<T> var4, Map<Ac<T>, Ad.a> var5) {
      var1.a((T)var2);
      var5.put(var2, Ad.a.PART_OF_CLUSTER);
      List var9 = new ArrayList(var3);

      for (int var10 = 0; var10 < var9.size(); var10++) {
         Ac var6 = (Ac)var9.get(var10);
         Ad.a var7;
         List var8;
         if ((var7 = (Ad.a)var5.get(var6)) == null && (var8 = this.a((T)var6, var4)).size() >= this.b) {
            var9 = a(var9, var8);
         }

         if (var7 != Ad.a.PART_OF_CLUSTER) {
            var5.put(var6, Ad.a.PART_OF_CLUSTER);
            var1.a((T)var6);
         }
      }

      return var1;
   }

   private List<T> a(T var1, Collection<T> var2) {
      ArrayList var3 = new ArrayList();

      for (Ac var4 : var2) {
         if (var1 != var4 && var4.a(var1) <= this.a) {
            var3.add(var4);
         }
      }

      return var3;
   }

   private static List<T> a(List<T> var0, List<T> var1) {
      HashSet var2 = new HashSet(var0);

      for (Ac var3 : var1) {
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
