package org.valkyrienskies.core.impl.shadow;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;

public final class td {
   private static ts a;
   private static ts b;

   private td() {
   }

   public static sv a(int var0, int var1) {
      return var0 * var1 <= 4096 ? new sy(var0, var1) : new sD(var0, var1);
   }

   private static <T extends kN<T>> sQ<T> a(kM<T> var0, int var1, int var2) {
      return var1 * var2 <= 4096 ? new sx<>(var0, var1, var2) : new sC<>(var0, var1, var2);
   }

   public static sv a(double[][] var0) {
      if (var0 != null && var0[0] != null) {
         return var0.length * var0[0].length <= 4096 ? new sy(var0) : new sD(var0);
      } else {
         throw new pl();
      }
   }

   private static <T extends kN<T>> sQ<T> a(T[][] var0) {
      if (var0 != null && var0[0] != null) {
         return var0.length * var0[0].length <= 4096 ? new sx<>((T[][])var0) : new sC<>((T[][])var0);
      } else {
         throw new pl();
      }
   }

   public static tq a(int var0) {
      sv var1 = a(var0, var0);

      for (int var2 = 0; var2 < var0; var2++) {
         var1.c(var2, var2, 1.0);
      }

      return var1;
   }

   public static <T extends kN<T>> sQ<T> a(kM<T> var0, int var1) {
      kN var2 = var0.a();
      kN var3 = var0.b();
      kN[][] var4 = Ch.a(var0, var1, var1);

      for (int var5 = 0; var5 < var1; var5++) {
         kN[] var6;
         Arrays.fill(var6 = var4[var5], var2);
         var6[var5] = var3;
      }

      return new sx<>(var0, (T[][])var4, false);
   }

   public static tq a(double[] var0) {
      sv var1 = a(var0.length, var0.length);

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1.c(var2, var2, var0[var2]);
      }

      return var1;
   }

   private static <T extends kN<T>> sQ<T> a(T[] var0) {
      su var1 = a(var0[0].c(), var0.length, var0.length);

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1.a(var2, var2, (T)var0[var2]);
      }

      return var1;
   }

   public static sA b(double[] var0) {
      if (var0 == null) {
         throw new pl();
      } else {
         return new sA(var0, true);
      }
   }

   private static <T extends kN<T>> sT<T> b(T[] var0) {
      if (var0 == null) {
         throw new pl();
      } else if (var0.length == 0) {
         throw new pr(px.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
      } else {
         return new sz<>(var0[0].c(), (T[])var0, true);
      }
   }

   private static tq d(double[] var0) {
      if (var0 == null) {
         throw new pl();
      }

      int var1 = var0.length;
      sv var2 = a(1, var1);

      for (int var3 = 0; var3 < var1; var3++) {
         var2.c(0, var3, var0[var3]);
      }

      return var2;
   }

   private static <T extends kN<T>> sQ<T> c(T[] var0) {
      if (var0 == null) {
         throw new pl();
      }

      int var1;
      if ((var1 = var0.length) == 0) {
         throw new pf(px.AT_LEAST_ONE_COLUMN);
      }

      su var2 = a(var0[0].c(), 1, var1);

      for (int var3 = 0; var3 < var1; var3++) {
         var2.a(0, var3, (T)var0[var3]);
      }

      return var2;
   }

   public static tq c(double[] var0) {
      if (var0 == null) {
         throw new pl();
      }

      int var1;
      sv var2 = a(var1 = var0.length, 1);

      for (int var3 = 0; var3 < var1; var3++) {
         var2.c(var3, 0, var0[var3]);
      }

      return var2;
   }

   private static <T extends kN<T>> sQ<T> d(T[] var0) {
      if (var0 == null) {
         throw new pl();
      }

      int var1;
      if ((var1 = var0.length) == 0) {
         throw new pf(px.AT_LEAST_ONE_ROW);
      }

      su var2 = a(var0[0].c(), var1, 1);

      for (int var3 = 0; var3 < var1; var3++) {
         var2.a(var3, 0, (T)var0[var3]);
      }

      return var2;
   }

   private static boolean a(tq var0, double var1, boolean var3) {
      int var4;
      if ((var4 = var0.f()) != var0.g()) {
         if (var3) {
            throw new th(var4, var0.g());
         } else {
            return false;
         }
      } else {
         for (int var5 = 0; var5 < var4; var5++) {
            for (int var6 = var5 + 1; var6 < var4; var6++) {
               double var7 = var0.b(var5, var6);
               double var9 = var0.b(var6, var5);
               if (BY.w(var7 - var9) > BY.e(BY.w(var7), BY.w(var9)) * var1) {
                  if (var3) {
                     throw new tj(var5, var6, var1);
                  }

                  return false;
               }
            }
         }

         return true;
      }
   }

   private static void b(tq var0, double var1) {
      a(var0, var1, true);
   }

   public static boolean a(tq var0, double var1) {
      return a(var0, var1, false);
   }

   public static void a(sv var0, int var1, int var2) {
      a(var0, var1);
      b(var0, var2);
   }

   public static void a(sw var0, int var1) {
      if (var1 < 0 || var1 >= var0.f()) {
         throw new po(px.ROW_INDEX, var1, 0, var0.f() - 1);
      }
   }

   public static void b(sw var0, int var1) {
      if (var1 < 0 || var1 >= var0.g()) {
         throw new po(px.COLUMN_INDEX, var1, 0, var0.g() - 1);
      }
   }

   public static void a(sv var0, int var1, int var2, int var3, int var4) {
      a(var0, var1);
      a(var0, var2);
      if (var2 < var1) {
         throw new pn(px.INITIAL_ROW_AFTER_FINAL_ROW, var2, var1, false);
      }

      b(var0, var3);
      b(var0, var4);
      if (var4 < var3) {
         throw new pn(px.INITIAL_COLUMN_AFTER_FINAL_COLUMN, var4, var3, false);
      }
   }

   public static void a(sv var0, int[] var1, int[] var2) {
      if (var1 == null) {
         throw new pl();
      }

      if (var2 == null) {
         throw new pl();
      }

      if (var1.length == 0) {
         throw new pf(px.EMPTY_SELECTED_ROW_INDEX_ARRAY);
      }

      if (var2.length == 0) {
         throw new pf(px.EMPTY_SELECTED_COLUMN_INDEX_ARRAY);
      }

      int[] var6;
      int var3 = (var6 = var1).length;

      for (int var4 = 0; var4 < var3; var4++) {
         int var5 = var6[var4];
         a(var0, var5);
      }

      var1 = var2;
      var3 = var2.length;

      for (int var9 = 0; var9 < var3; var9++) {
         int var10 = var1[var9];
         b(var0, var10);
      }
   }

   public static void a(tq var0, tq var1) {
      if (var0.f() != var1.f() || var0.g() != var1.g()) {
         throw new tc(var0.f(), var0.g(), var1.f(), var1.g());
      }
   }

   public static void a(sv var0, tq var1) {
      if (var0.f() != var1.f() || var0.g() != var1.g()) {
         throw new tc(var0.f(), var0.g(), var1.f(), var1.g());
      }
   }

   public static void b(sv var0, tq var1) {
      if (var0.g() != var1.f()) {
         throw new oS(var0.g(), var1.f());
      }
   }

   private static sy b(sQ<qg> var0) {
      td.b var1 = new td.b();
      var0.c(var1);
      td.b var2 = var1;
      return new sy(var2.a, false);
   }

   public static sy a(sQ<qd> var0) {
      td.a var1 = new td.a();
      var0.c(var1);
      td.a var2 = var1;
      return new sy(var2.a, false);
   }

   public static void a(tu var0, ObjectOutputStream var1) {
      int var2 = var0.f();
      var1.writeInt(var2);

      for (int var3 = 0; var3 < var2; var3++) {
         var1.writeDouble(var0.a(var3));
      }
   }

   public static void a(Object var0, String var1, ObjectInputStream var2) {
      try {
         int var3;
         double[] var10 = new double[var3 = var2.readInt()];

         for (int var5 = 0; var5 < var3; var5++) {
            var10[var5] = var2.readDouble();
         }

         sA var11 = new sA(var10, false);
         Field var8;
         (var8 = var0.getClass().getDeclaredField(var1)).setAccessible(true);
         var8.set(var0, var11);
      } catch (NoSuchFieldException var6) {
         IOException var9;
         (var9 = new IOException()).initCause(var6);
         throw var9;
      } catch (IllegalAccessException var7) {
         IOException var4;
         (var4 = new IOException()).initCause(var7);
         throw var4;
      }
   }

   public static void a(tq var0, ObjectOutputStream var1) {
      int var2 = var0.f();
      int var3 = var0.g();
      var1.writeInt(var2);
      var1.writeInt(var3);

      for (int var4 = 0; var4 < var2; var4++) {
         for (int var5 = 0; var5 < var3; var5++) {
            var1.writeDouble(var0.b(var4, var5));
         }
      }
   }

   public static void b(Object var0, String var1, ObjectInputStream var2) {
      try {
         int var3 = var2.readInt();
         int var12 = var2.readInt();
         double[][] var5 = new double[var3][var12];

         for (int var6 = 0; var6 < var3; var6++) {
            double[] var7 = var5[var6];

            for (int var8 = 0; var8 < var12; var8++) {
               var7[var8] = var2.readDouble();
            }
         }

         sy var13 = new sy(var5, false);
         Field var14;
         (var14 = var0.getClass().getDeclaredField(var1)).setAccessible(true);
         var14.set(var0, var13);
      } catch (NoSuchFieldException var9) {
         IOException var11;
         (var11 = new IOException()).initCause(var9);
         throw var11;
      } catch (IllegalAccessException var10) {
         IOException var4;
         (var4 = new IOException()).initCause(var10);
         throw var4;
      }
   }

   private static void a(tq var0, tu var1) {
      if (var0 != null && var1 != null && var0.f() == var1.f()) {
         if (var0.g() != var0.f()) {
            throw new th(var0.f(), var0.g());
         }

         int var2 = var0.f();

         for (int var3 = 0; var3 < var2; var3++) {
            double var4;
            if (BY.w(var4 = var0.b(var3, var3)) < Cq.b) {
               throw new oU(px.ZERO_DENOMINATOR);
            }

            double var6 = var1.a(var3) / var4;
            var1.a(var3, var6);

            for (int var8 = var3 + 1; var8 < var2; var8++) {
               var1.a(var8, var1.a(var8) - var6 * var0.b(var8, var3));
            }
         }
      } else {
         throw new oS(var0 == null ? 0 : var0.f(), var1 == null ? 0 : var1.f());
      }
   }

   private static void b(tq var0, tu var1) {
      if (var0 != null && var1 != null && var0.f() == var1.f()) {
         if (var0.g() != var0.f()) {
            throw new th(var0.f(), var0.g());
         }

         for (int var2 = var0.f() - 1; var2 >= 0; var2--) {
            double var4;
            if (BY.w(var4 = var0.b(var2, var2)) < Cq.b) {
               throw new oU(px.ZERO_DENOMINATOR);
            }

            double var6 = var1.a(var2) / var4;
            var1.a(var2, var6);

            for (int var3 = var2 - 1; var3 >= 0; var3--) {
               var1.a(var3, var1.a(var3) - var6 * var0.b(var3, var2));
            }
         }
      } else {
         throw new oS(var0 == null ? 0 : var0.f(), var1 == null ? 0 : var1.f());
      }
   }

   private static sy a(tq var0, int var1) {
      int var2 = var0.f();
      if (var0.g() != var2) {
         throw new th(var0.f(), var0.g());
      }

      int var3 = var1 + 1;
      tq var4 = var0.a(0, var1, 0, var1);
      tq var5 = var0.a(0, var1, var3, var2 - 1);
      tq var12 = var0.a(var3, var2 - 1, 0, var1);
      var0 = var0.a(var3, var2 - 1, var3, var2 - 1);
      tC.a var6;
      if (!(var6 = new tC(var4).a()).a()) {
         throw new tA();
      }

      tq var19 = var6.b();
      tC.a var7;
      if (!(var7 = new tC(var0).a()).a()) {
         throw new tA();
      }

      tq var20 = var7.b();
      var4 = var4.b(var5.c(var20).c(var12));
      tC.a var16;
      if (!(var16 = new tC(var4).a()).a()) {
         throw new tA();
      }

      var4 = var16.b();
      var0 = var0.b(var12.c(var19).c(var5));
      tC.a var10;
      if (!(var10 = new tC(var0).a()).a()) {
         throw new tA();
      }

      var0 = var10.b();
      var5 = var19.c(var5).c(var0).b(-1.0);
      tq var13 = var20.c(var12).c(var4).b(-1.0);
      sy var14;
      (var14 = new sy(var2, var2)).a(var4.a(), 0, 0);
      var14.a(var5.a(), 0, var3);
      var14.a(var13.a(), var3, 0);
      var14.a(var0.a(), var3, var3);
      return var14;
   }

   private static tq a(tq var0) {
      double var2 = 0.0;
      tq var4 = var0;
      Ci.a(var0);
      if (!var4.e()) {
         throw new th(var4.f(), var4.g());
      } else {
         return var4 instanceof sM ? ((sM)var4).c(0.0) : new tn(var4, 0.0).c().b();
      }
   }

   private static tq c(tq var0, double var1) {
      Ci.a(var0);
      if (!var0.e()) {
         throw new th(var0.f(), var0.g());
      } else {
         return var0 instanceof sM ? ((sM)var0).c(0.0) : new tn(var0, 0.0).c().b();
      }
   }

   static {
      ts.a(Locale.getDefault());
      new ts("[", "]", "", "", "; ", ", ");
   }

   static class a extends sI<qd> {
      double[][] a;

      a() {
         super(qd.c);
      }

      @Override
      public final void a(int var1, int var2, int var3, int var4) {
         this.a = new double[var1][var2];
      }

      private void a(int var1, int var2, qd var3) {
         this.a[var1][var2] = var3.doubleValue();
      }

      final sy b() {
         return new sy(this.a, false);
      }

      // $VF: synthetic method
      @Override
      public final void a(int var1, int var2, kN var3) {
         qd var4 = (qd)var3;
         int var6 = var2;
         var2 = var1;
         this.a[var2][var6] = var4.doubleValue();
      }
   }

   static class b extends sI<qg> {
      double[][] a;

      b() {
         super(qg.b);
      }

      @Override
      public final void a(int var1, int var2, int var3, int var4) {
         this.a = new double[var1][var2];
      }

      private void a(int var1, int var2, qg var3) {
         this.a[var1][var2] = var3.doubleValue();
      }

      final sy b() {
         return new sy(this.a, false);
      }

      // $VF: synthetic method
      @Override
      public final void a(int var1, int var2, kN var3) {
         qg var4 = (qg)var3;
         int var6 = var2;
         var2 = var1;
         this.a[var2][var6] = var4.doubleValue();
      }
   }
}
