package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class Bq implements Br {
   private static Bp b = Bp.FAILED;
   private static int c = Bs.AVERAGE$139d6b26;
   public final Bp a;
   private final int d;
   private final zD e;

   public Bq() {
      this.d = c;
      this.a = b;
      this.e = null;
   }

   private Bq(int var1) {
      this.d = var1;
      this.a = b;
      this.e = new zD();
   }

   private Bq(Bp var1) {
      this.a = var1;
      this.d = c;
      this.e = null;
   }

   public Bq(Bp var1, int var2) {
      this.a = var1;
      this.d = var2;
      this.e = new zD();
   }

   private Bq(zF var1) {
      this.d = Bs.RANDOM$139d6b26;
      this.a = b;
      this.e = new zD(var1);
   }

   private Bq(Bp var1, zF var2) {
      this.a = var1;
      this.d = Bs.RANDOM$139d6b26;
      this.e = new zD(var2);
   }

   public final Bp a() {
      return this.a;
   }

   private int b() {
      return this.d;
   }

   @Override
   public final double[] a(double[] var1) {
      Bq.a[] var2 = new Bq.a[var1.length];

      for (int var3 = 0; var3 < var1.length; var3++) {
         var2[var3] = new Bq.a(var1[var3], var3);
      }

      List var9 = null;
      switch (this.a) {
         case MAXIMAL:
            a(var2, Double.POSITIVE_INFINITY);
            break;
         case MINIMAL:
            a(var2, Double.NEGATIVE_INFINITY);
            break;
         case REMOVED:
            var2 = this.a(var2);
            break;
         case FIXED:
            var9 = c(var2);
            break;
         case FAILED:
            if ((var9 = c(var2)).size() > 0) {
               throw new ph();
            }
            break;
         default:
            throw new oY();
      }

      Arrays.sort(var2);
      var1 = new double[var2.length];
      int var4 = 1;
      Bq.a var7;
      var1[(var7 = var2[0]).b] = 1.0;
      ArrayList var5;
      (var5 = new ArrayList()).add((var7 = var2[0]).b);

      for (int var6 = 1; var6 < var2.length; var6++) {
         if (Double.compare((var7 = var2[var6]).a, (var7 = var2[var6 - 1]).a) > 0) {
            var4 = var6 + 1;
            if (var5.size() > 1) {
               this.a(var1, var5);
            }

            (var5 = new ArrayList()).add((var7 = var2[var6]).b);
         } else {
            var5.add((var7 = var2[var6]).b);
         }

         var1[(var7 = var2[var6]).b] = var4;
      }

      if (var5.size() > 1) {
         this.a(var1, var5);
      }

      if (this.a == Bp.FIXED) {
         b(var1, var9);
      }

      return var1;
   }

   private Bq.a[] a(Bq.a[] var1) {
      if (!b(var1)) {
         return var1;
      }

      Bq.a[] var2 = new Bq.a[var1.length];
      int var3 = 0;

      for (int var4 = 0; var4 < var1.length; var4++) {
         Bq.a var6;
         if (Double.isNaN((var6 = var1[var4]).a)) {
            for (int var5 = var4 + 1; var5 < var1.length; var5++) {
               Bq.a var11;
               var1[var5] = new Bq.a((var6 = var1[var5]).a, (var11 = var1[var5]).b - 1);
            }
         } else {
            Bq.a var9;
            var2[var3] = new Bq.a((var6 = var1[var4]).a, (var9 = var1[var4]).b);
            var3++;
         }
      }

      Bq.a[] var7 = new Bq.a[var3];
      System.arraycopy(var2, 0, var7, 0, var3);
      return var7;
   }

   private static void a(Bq.a[] var0, double var1) {
      for (int var3 = 0; var3 < var0.length; var3++) {
         Bq.a var4;
         if (Double.isNaN((var4 = var0[var3]).a)) {
            var0[var3] = new Bq.a(var1, (var4 = var0[var3]).b);
         }
      }
   }

   private static boolean b(Bq.a[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         Bq.a var2;
         if (Double.isNaN((var2 = var0[var1]).a)) {
            return true;
         }
      }

      return false;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void a(double[] var1, List<Integer> var2) {
      double var3 = var1[var2.get(0)];
      int var5 = var2.size();
      switch (<unrepresentable>.b[this.d - 1]) {
         case 1:
            a(var1, var2, (var3 * 2.0 + var5 - 1.0) / 2.0);
            return;
         case 2:
            a(var1, var2, var3 + var5 - 1.0);
            return;
         case 3:
            a(var1, var2, var3);
            return;
         case 4:
            Iterator var10 = var2.iterator();
            long var12 = BY.B(var3);

            while (var10.hasNext()) {
               var1[var10.next()] = this.e.a(var12, var12 + var5 - 1L);
            }
            break;
         case 5:
            Iterator var9 = var2.iterator();
            long var7 = BY.B(var3);
            int var11 = 0;

            while (var9.hasNext()) {
               var1[var9.next()] = var7 + var11++;
            }
            break;
         default:
            throw new oY();
      }
   }

   private static void a(double[] var0, List<Integer> var1, double var2) {
      Iterator var4 = var1.iterator();

      while (var4.hasNext()) {
         var0[var4.next()] = var2;
      }
   }

   private static void b(double[] var0, List<Integer> var1) {
      if (var1.size() != 0) {
         Iterator var2 = var1.iterator();

         while (var2.hasNext()) {
            var0[var2.next()] = Double.NaN;
         }
      }
   }

   private static List<Integer> c(Bq.a[] var0) {
      ArrayList var1 = new ArrayList();

      for (int var2 = 0; var2 < var0.length; var2++) {
         Bq.a var3;
         if (Double.isNaN((var3 = var0[var2]).a)) {
            var1.add(var2);
         }
      }

      return var1;
   }

   static class a implements Comparable<Bq.a> {
      final double a;
      final int b;

      a(double var1, int var3) {
         this.a = var1;
         this.b = var3;
      }

      private int a(Bq.a var1) {
         return Double.compare(this.a, var1.a);
      }

      public final double a() {
         return this.a;
      }

      public final int b() {
         return this.b;
      }

      // $VF: synthetic method
      @Override
      public final int compareTo(Object var1) {
         Bq.a var2 = (Bq.a)var1;
         return Double.compare(this.a, var2.a);
      }
   }
}
