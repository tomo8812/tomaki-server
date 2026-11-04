package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Deprecated
public final class mQ implements kW {
   private final int a;
   private final List<mQ.a> b;
   private final double c;
   private final Map<tu, Double> d;

   public mQ(double[][] var1, double[] var2, int var3, int var4, zN var5) {
      if (var1 == null || var2 == null) {
         throw new pl();
      }

      if (var1.length == 0) {
         throw new pf();
      }

      if (var1.length != var2.length) {
         throw new oS(var1.length, var2.length);
      }

      if (var1[0] == null) {
         throw new pl();
      }

      this.a = var1[0].length;
      this.c = var3;
      this.d = new HashMap<>(var2.length);

      for (int var7 = 0; var7 < var1.length; var7++) {
         double[] var6;
         if ((var6 = var1[var7]) == null) {
            throw new pl();
         }

         if (var6.length != this.a) {
            throw new oS(var6.length, this.a);
         }

         this.d.put(new sA(var6), var2[var7]);
      }

      this.b = new ArrayList<>(var4);

      for (int var8 = 0; var8 < var4; var8++) {
         this.b.add(new mQ.a(var5.a()));
      }
   }

   @Override
   public final double a(double[] var1) {
      sA var15 = new sA(var1);
      Iterator var3 = this.b.iterator();

      while (var3.hasNext()) {
         mQ.a var8;
         (var8 = (mQ.a)var3.next()).b = 0.0;
         var8.c = null;
      }

      var3 = this.d.entrySet().iterator();

      while (var3.hasNext()) {
         Entry var4;
         tu var5;
         double var6;
         if (BY.w(var6 = (var5 = ((tu)(var4 = (Entry)var3.next()).getKey()).k(var15)).c()) < BY.x(1.0)) {
            return (Double)var4.getValue();
         }

         for (mQ.a var9 : this.b) {
            double var10 = BY.a(var6, -this.c);
            sA var13 = var9.a;
            tu var20 = var5;
            double var10001 = var5.b(var13) / (var20.c() * var13.c()) * var10;
            Entry var22 = var4;
            double var24 = var10001;
            mQ.a var21 = var9;
            if (var24 > var21.b) {
               var21.b = var24;
               var21.c = var22;
            }
         }
      }

      double var18 = 0.0;
      double var19 = 0.0;
      Iterator var7 = this.b.iterator();

      while (var7.hasNext()) {
         mQ.a var16;
         double var23 = (var16 = (mQ.a)var7.next()).b;
         Entry var11 = var16.c;
         if (var16.c != null) {
            var18 += var23 * var11.getValue();
            var19 += var23;
         }
      }

      return var18 / var19;
   }

   private static double a(tu var0, tu var1) {
      return var0.b(var1) / (var0.c() * var1.c());
   }

   static class a {
      final sA a;
      double b;
      Entry<tu, Double> c;

      a(double[] var1) {
         this.a = new sA(var1);
      }

      final tu a() {
         return this.a;
      }

      final void b() {
         this.b = 0.0;
         this.c = null;
      }

      final void a(double var1, Entry<tu, Double> var3) {
         if (var1 > this.b) {
            this.b = var1;
            this.c = var3;
         }
      }

      final double c() {
         return this.b;
      }

      final Entry<tu, Double> d() {
         return this.c;
      }
   }
}
