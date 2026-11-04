package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public final class om extends oe {
   private static final long b = 20130308L;
   private ok<Double> c;

   private om(double[] var1, double[] var2) {
      this(new zR(), var1, var2);
   }

   private om(zR var1, double[] var2, double[] var3) {
      super(var1);
      this.c = new ok<>(var1, a(var2, var3));
   }

   public om(zF var1, double[] var2) {
      super(var1);
      HashMap var3 = new HashMap();
      double[] var4 = var2;
      int var5 = var2.length;

      for (int var6 = 0; var6 < var5; var6++) {
         double var7 = var4[var6];
         Integer var9;
         if ((var9 = (Integer)var3.get(var7)) == null) {
            var9 = 0;
         }

         var3.put(var7, var9 + 1);
      }

      int var12 = var3.size();
      double var13 = var2.length;
      double[] var14 = new double[var12];
      double[] var8 = new double[var12];
      int var15 = 0;

      for (Entry var11 : var3.entrySet()) {
         var14[var15] = (Double)var11.getKey();
         var8[var15] = ((Integer)var11.getValue()).intValue() / var13;
         var15++;
      }

      this.c = new ok<>(var1, a(var14, var8));
   }

   private om(double[] var1) {
      this(new zR(), var1);
   }

   private static List<Co<Double, Double>> a(double[] var0, double[] var1) {
      if (var0.length != var1.length) {
         throw new oS(var1.length, var0.length);
      }

      ArrayList var2 = new ArrayList(var0.length);

      for (int var3 = 0; var3 < var0.length; var3++) {
         var2.add(new Co<>(var0[var3], var1[var3]));
      }

      return var2;
   }

   @Override
   public final double b(double var1) {
      return this.c.a(var1);
   }

   @Override
   public final double d(double var1) {
      double var4 = var1;
      return this.c.a(var4);
   }

   @Override
   public final double e(double var1) {
      double var3 = 0.0;
      Iterator var5 = this.c.a().iterator();

      while (var5.hasNext()) {
         Co var6;
         if ((Double)(var6 = (Co)var5.next()).a <= var1) {
            var3 += var6.c();
         }
      }

      return var3;
   }

   @Override
   public final double a(double var1) {
      if (!(var1 < 0.0) && !(var1 > 1.0)) {
         double var3 = 0.0;
         double var5 = this.e();
         Iterator var7 = this.c.a().iterator();

         while (var7.hasNext()) {
            Co var8;
            if ((Double)(var8 = (Co)var7.next()).c() != 0.0) {
               var3 += var8.c();
               var5 = (Double)var8.a;
               if (!(var3 >= var1)) {
                  continue;
               }
               break;
            }
         }

         return var5;
      } else {
         throw new po(var1, 0, 1);
      }
   }

   @Override
   public final double c() {
      double var1 = 0.0;

      for (Co var4 : this.c.a()) {
         var1 += var4.c() * (Double)var4.a;
      }

      return var1;
   }

   @Override
   public final double d() {
      double var1 = 0.0;
      double var3 = 0.0;

      for (Co var6 : this.c.a()) {
         var1 += var6.c() * (Double)var6.a;
         var3 += var6.c() * (Double)var6.a * (Double)var6.a;
      }

      return var3 - var1 * var1;
   }

   @Override
   public final double e() {
      double var1 = Double.POSITIVE_INFINITY;
      Iterator var3 = this.c.a().iterator();

      while (var3.hasNext()) {
         Co var4;
         if ((Double)(var4 = (Co)var3.next()).a < var1 && (Double)var4.c() > 0.0) {
            var1 = (Double)var4.a;
         }
      }

      return var1;
   }

   @Override
   public final double f() {
      double var1 = Double.NEGATIVE_INFINITY;
      Iterator var3 = this.c.a().iterator();

      while (var3.hasNext()) {
         Co var4;
         if ((Double)(var4 = (Co)var3.next()).a > var1 && (Double)var4.c() > 0.0) {
            var1 = (Double)var4.a;
         }
      }

      return var1;
   }

   @Override
   public final boolean g() {
      return true;
   }

   @Override
   public final boolean h() {
      return true;
   }

   @Override
   public final boolean i() {
      return true;
   }

   @Override
   public final double b() {
      return this.c.b();
   }
}
