package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Ak {
   private final tq a;
   private final Br b;
   private final Aj c;

   public Ak() {
      this(new Bq());
   }

   private Ak(Bq var1) {
      this.a = null;
      this.b = var1;
      this.c = null;
   }

   private Ak(tq var1) {
      this(var1, new Bq());
   }

   private Ak(tq var1, Bq var2) {
      this.b = var2;
      this.a = this.a(var1);
      this.c = new Aj(this.a);
   }

   private tq a() {
      Aj var1 = this.c;
      return this.c.a;
   }

   private Aj b() {
      return this.c;
   }

   private tq a(sD var1) {
      tq var2 = this.a((tq)var1);
      return new Aj().a(var2);
   }

   private tq a(double[][] var1) {
      sD var2 = new sD(var1);
      tq var3 = this.a((tq)var2);
      return new Aj().a(var3);
   }

   private double a(double[] var1, double[] var2) {
      if (var1.length != var2.length) {
         throw new oS(var1.length, var2.length);
      }

      if (var1.length < 2) {
         throw new oV(px.INSUFFICIENT_DIMENSION, var1.length, 2);
      }

      double[] var3 = var1;
      double[] var4 = var2;
      Bq var5;
      if (this.b instanceof Bq && Bp.REMOVED == (var5 = (Bq)this.b).a) {
         HashSet var6;
         (var6 = new HashSet()).addAll(a(var1));
         var6.addAll(a(var2));
         var3 = a(var1, var6);
         var4 = a(var2, var6);
      }

      new Aj();
      return Aj.a(this.b.a(var3), this.b.a(var4));
   }

   private tq a(tq var1) {
      sv var2 = null;
      Bq var3;
      if (this.b instanceof Bq && (var3 = (Bq)this.b).a == Bp.REMOVED) {
         HashSet var5 = new HashSet();

         for (int var4 = 0; var4 < var1.g(); var4++) {
            var5.addAll(a(var1.g(var4)));
         }

         if (!var5.isEmpty()) {
            var2 = new sD(var1.f() - var5.size(), var1.g());

            for (int var7 = 0; var7 < var2.g(); var7++) {
               var2.b(var7, a(var1.g(var7), var5));
            }
         }
      }

      if (var2 == null) {
         var2 = var1.i();
      }

      for (int var6 = 0; var6 < var2.g(); var6++) {
         var2.b(var6, this.b.a(var2.g(var6)));
      }

      return var2;
   }

   private static List<Integer> a(double[] var0) {
      ArrayList var1 = new ArrayList();

      for (int var2 = 0; var2 < var0.length; var2++) {
         if (Double.isNaN(var0[var2])) {
            var1.add(var2);
         }
      }

      return var1;
   }

   private static double[] a(double[] var0, Set<Integer> var1) {
      if (var1.isEmpty()) {
         return var0;
      }

      double[] var2 = new double[var0.length - var1.size()];
      int var3 = 0;
      int var4 = 0;

      while (var3 < var0.length) {
         if (!var1.contains(var3)) {
            var2[var4++] = var0[var3];
         }

         var3++;
      }

      return var2;
   }
}
