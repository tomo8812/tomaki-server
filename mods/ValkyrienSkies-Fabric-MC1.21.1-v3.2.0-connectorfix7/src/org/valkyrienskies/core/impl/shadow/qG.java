package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class qG<T> extends qm<Double> implements qE<T> {
   private final List<Double> b;
   private final List<Integer> c;

   private qG(List<Double> var1) {
      super(var1);
      Collections.sort(var1 = new ArrayList<>(this.a()));
      this.b = Collections.unmodifiableList(var1);
      int var5 = this.b();
      ArrayList var2 = new ArrayList(var5);

      for (int var3 = 0; var3 < var5; var3++) {
         var2.add(var3);
      }

      this.c = Collections.unmodifiableList(a(var2, this.a(), this.b));
   }

   private qG(Double[] var1) {
      this(Arrays.asList(var1));
   }

   @Override
   public final List<T> b(List<T> var1) {
      return a(var1, this.a(), this.b);
   }

   private static <S> List<S> a(List<S> var0, List<Double> var1, List<Double> var2) {
      int var3 = var0.size();
      if (var1.size() != var3) {
         throw new oS(var1.size(), var3);
      }

      if (var2.size() != var3) {
         throw new oS(var2.size(), var3);
      }

      var1 = new ArrayList(var1);
      ArrayList var4 = new ArrayList(var3);

      for (int var5 = 0; var5 < var3; var5++) {
         int var6 = var1.indexOf(var2.get(var5));
         var4.add(var0.get(var6));
         var1.set(var6, null);
      }

      return var4;
   }

   @Override
   protected final boolean a(qp var1) {
      if (!(var1 instanceof qG)) {
         return false;
      }

      var1 = var1;
      if (this.b() != var1.b()) {
         return false;
      }

      List var2 = this.c;
      List var5 = var1.c;

      for (int var3 = 0; var3 < this.b(); var3++) {
         if (var2.get(var3) != var5.get(var3)) {
            return false;
         }
      }

      return true;
   }

   @Override
   protected final void a(List<Double> var1) {
      Iterator var5 = var1.iterator();

      while (var5.hasNext()) {
         double var3;
         if ((var3 = (Double)var5.next()) < 0.0 || var3 > 1.0) {
            throw new qy(px.OUT_OF_RANGE_SIMPLE, var3, 0, 1);
         }
      }
   }

   private static List<Double> a(int var0) {
      ArrayList var1 = new ArrayList(var0);

      for (int var2 = 0; var2 < var0; var2++) {
         var1.add(qx.a().nextDouble());
      }

      return var1;
   }

   private static List<Double> b(int var0) {
      ArrayList var1 = new ArrayList(var0);

      for (int var2 = 0; var2 < var0; var2++) {
         var1.add((double)var2 / var0);
      }

      return var1;
   }

   private static <S> List<Double> a(List<S> var0, Comparator<S> var1) {
      ArrayList var2;
      Collections.sort(var2 = new ArrayList(var0), var1);
      ArrayList var8 = var2;
      var0 = var0;
      if (var0.size() != var8.size()) {
         throw new oS(var8.size(), var0.size());
      }

      int var9 = var0.size();
      var0 = new ArrayList(var0);
      Double[] var3 = new Double[var9];

      for (int var4 = 0; var4 < var9; var4++) {
         int var5;
         if ((var5 = var0.indexOf(var8.get(var4))) == -1) {
            throw new oV(px.DIFFERENT_ORIG_AND_PERMUTED_DATA);
         }

         var3[var5] = (double)var4 / var9;
         var0.set(var5, null);
      }

      return Arrays.asList(var3);
   }

   private static <S> List<Double> a(List<S> var0, List<S> var1) {
      if (var0.size() != var1.size()) {
         throw new oS(var1.size(), var0.size());
      }

      int var2 = var0.size();
      var0 = new ArrayList(var0);
      Double[] var3 = new Double[var2];

      for (int var4 = 0; var4 < var2; var4++) {
         int var5;
         if ((var5 = var0.indexOf(var1.get(var4))) == -1) {
            throw new oV(px.DIFFERENT_ORIG_AND_PERMUTED_DATA);
         }

         var3[var5] = (double)var4 / var2;
         var0.set(var5, null);
      }

      return Arrays.asList(var3);
   }

   @Override
   public String toString() {
      return String.format("(f=%s pi=(%s))", this.d(), this.c);
   }

   private static List<Integer> c(int var0) {
      ArrayList var1 = new ArrayList(var0);

      for (int var2 = 0; var2 < var0; var2++) {
         var1.add(var2);
      }

      return var1;
   }
}
