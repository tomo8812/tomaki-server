package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public final class ol extends oc {
   private static final long b = 20130308L;
   private ok<Integer> c;

   private ol(int[] var1, double[] var2) {
      this(new zR(), var1, var2);
   }

   private ol(zR var1, int[] var2, double[] var3) {
      super(var1);
      this.c = new ok<>(var1, a(var2, var3));
   }

   private ol(zR var1, int[] var2) {
      super(var1);
      HashMap var3 = new HashMap();
      int[] var4 = var2;
      int var5 = var2.length;

      for (int var6 = 0; var6 < var5; var6++) {
         int var7 = var4[var6];
         Integer var8;
         if ((var8 = (Integer)var3.get(var7)) == null) {
            var8 = 0;
         }

         var3.put(var7, var8 + 1);
      }

      int var11 = var3.size();
      double var13 = var2.length;
      int[] var14 = new int[var11];
      double[] var15 = new double[var11];
      int var9 = 0;

      for (Entry var12 : var3.entrySet()) {
         var14[var9] = (Integer)var12.getKey();
         var15[var9] = ((Integer)var12.getValue()).intValue() / var13;
         var9++;
      }

      this.c = new ok<>(var1, a(var14, var15));
   }

   private ol(int[] var1) {
      this(new zR(), var1);
   }

   private static List<Co<Integer, Double>> a(int[] var0, double[] var1) {
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
   public final double c(int var1) {
      return this.c.a(var1);
   }

   @Override
   public final double d(int var1) {
      double var2 = 0.0;
      Iterator var4 = this.c.a().iterator();

      while (var4.hasNext()) {
         Co var5;
         if ((Integer)(var5 = (Co)var4.next()).a <= var1) {
            var2 += var5.c();
         }
      }

      return var2;
   }

   @Override
   public final double b() {
      double var1 = 0.0;

      for (Co var4 : this.c.a()) {
         var1 += var4.c() * ((Integer)var4.a).intValue();
      }

      return var1;
   }

   @Override
   public final double c() {
      double var1 = 0.0;
      double var3 = 0.0;

      for (Co var6 : this.c.a()) {
         var1 += var6.c() * ((Integer)var6.a).intValue();
         var3 += var6.c() * ((Integer)var6.a).intValue() * ((Integer)var6.a).intValue();
      }

      return var3 - var1 * var1;
   }

   @Override
   public final int d() {
      int var1 = Integer.MAX_VALUE;
      Iterator var2 = this.c.a().iterator();

      while (var2.hasNext()) {
         Co var3;
         if ((Integer)(var3 = (Co)var2.next()).a < var1 && (Double)var3.c() > 0.0) {
            var1 = (Integer)var3.a;
         }
      }

      return var1;
   }

   @Override
   public final int e() {
      int var1 = Integer.MIN_VALUE;
      Iterator var2 = this.c.a().iterator();

      while (var2.hasNext()) {
         Co var3;
         if ((Integer)(var3 = (Co)var2.next()).a > var1 && (Double)var3.c() > 0.0) {
            var1 = (Integer)var3.a;
         }
      }

      return var1;
   }

   @Override
   public final boolean f() {
      return true;
   }

   @Override
   public final int a() {
      return this.c.b();
   }
}
