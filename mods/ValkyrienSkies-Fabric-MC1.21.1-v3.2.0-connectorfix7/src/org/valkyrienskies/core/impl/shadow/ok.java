package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ok<T> implements Serializable {
   private static final long a = 20123308L;
   private zF b;
   private final List<T> c;
   private final double[] d;
   private final double[] e;

   private ok(List<Co<T, Double>> var1) {
      this(new zR(), var1);
   }

   public ok(zF var1, List<Co<T, Double>> var2) {
      this.b = var1;
      this.c = new ArrayList<>(var2.size());
      double[] var8 = new double[var2.size()];

      for (int var4 = 0; var4 < var2.size(); var4++) {
         Co var5 = (Co)var2.get(var4);
         this.c.add((T)var5.a);
         double var6;
         if ((var6 = (Double)var5.c()) < 0.0) {
            throw new pj((Number)var5.c());
         }

         if (Double.isInfinite(var6)) {
            throw new pi(var6);
         }

         if (Double.isNaN(var6)) {
            throw new ph();
         }

         var8[var4] = var6;
      }

      this.d = Ch.a(var8, 1.0);
      this.e = new double[this.d.length];
      double var9 = 0.0;

      for (int var10 = 0; var10 < this.d.length; var10++) {
         var9 += this.d[var10];
         this.e[var10] = var9;
      }
   }

   private void a(long var1) {
      this.b.setSeed(var1);
   }

   final double a(T var1) {
      double var2 = 0.0;

      for (int var4 = 0; var4 < this.d.length; var4++) {
         if (var1 == null && this.c.get(var4) == null || var1 != null && var1.equals(this.c.get(var4))) {
            var2 += this.d[var4];
         }
      }

      return var2;
   }

   public final List<Co<T, Double>> a() {
      ArrayList var1 = new ArrayList(this.d.length);

      for (int var2 = 0; var2 < this.d.length; var2++) {
         var1.add(new Co<>(this.c.get(var2), this.d[var2]));
      }

      return var1;
   }

   public final T b() {
      double var1 = this.b.nextDouble();
      int var3;
      if ((var3 = Arrays.binarySearch(this.e, var1)) < 0) {
         var3 = -var3 - 1;
      }

      return var3 >= 0 && var3 < this.d.length && var1 < this.e[var3] ? this.c.get(var3) : this.c.get(this.c.size() - 1);
   }

   private Object[] a(int var1) {
      if (var1 <= 0) {
         throw new pk(px.NUMBER_OF_SAMPLES, var1);
      }

      Object[] var2 = new Object[var1];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = this.b();
      }

      return var2;
   }

   private T[] a(int var1, T[] var2) {
      if (var1 <= 0) {
         throw new pk(px.NUMBER_OF_SAMPLES, var1);
      }

      if (var2 == null) {
         throw new pl(px.INPUT_ARRAY);
      }

      if (var2.length < var1) {
         var2 = (Object[])Array.newInstance(var2.getClass().getComponentType(), var1);
      } else {
         var2 = var2;
      }

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = this.b();
      }

      return (T[])var2;
   }
}
