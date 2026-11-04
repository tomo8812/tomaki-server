package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class uF {
   final uM a;
   public final uE b;
   public double c;
   final double[] d;
   private final double[] f;
   List<uF.a> e;

   public uF(uM var1) {
      int var2 = var1.a();
      this.a = var1;
      this.b = new uE(0, var2);
      this.c = Double.NaN;
      this.d = new double[var2];
      this.f = new double[var2];
      this.e = new ArrayList<>();
   }

   public final uM a() {
      return this.a;
   }

   public final int b() {
      if (this.e.isEmpty()) {
         uE var2 = this.b;
         return this.b.b;
      } else {
         uE var1;
         return (var1 = this.e.get(this.e.size() - 1).b).a + var1.b;
      }
   }

   public final void a(double var1, double[] var3, double[] var4) {
      this.b.a(var3, this.d);
      this.a.a(var1, this.d, this.f);
      Iterator var5 = this.e.iterator();

      while (var5.hasNext()) {
         uF.a var6;
         (var6 = (uF.a)var5.next()).b.a(var3, var6.c);
         var6.a.a(var1, this.d, this.f, var6.c, var6.d);
         var6.b.b(var6.d, var4);
      }

      this.b.b(this.f, var4);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final int a(uQ.a var1) {
      int var2;
      if (this.e.isEmpty()) {
         this.e = new ArrayList<>();
         var2 = this.a.a();
      } else {
         uE var3 = var2_2.b;
         uF.a var4;
         var2 = (var4 = this.e.get(this.e.size() - 1)).b.a + var4.b.b;
      }

      this.e.add(new uF.a(var1, var2));
      return this.e.size() - 1;
   }

   public final uE c() {
      return this.b;
   }

   public final uE[] d() {
      uE[] var1 = new uE[this.e.size()];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = this.e.get(var2).b;
      }

      return var1;
   }

   public final void a(double var1) {
      this.c = var1;
   }

   public final double e() {
      return this.c;
   }

   public final void a(double[] var1) {
      if (var1.length != this.d.length) {
         throw new oS(var1.length, this.d.length);
      }

      System.arraycopy(var1, 0, this.d, 0, var1.length);
   }

   public final double[] f() {
      return (double[])this.d.clone();
   }

   private double[] h() {
      return (double[])this.f.clone();
   }

   public final void a(int var1, double[] var2) {
      double[] var3 = this.e.get(var1).c;
      if (var2.length != var3.length) {
         throw new oS(var2.length, var3.length);
      }

      System.arraycopy(var2, 0, var3, 0, var2.length);
   }

   public final double[] a(int var1) {
      return (double[])this.e.get(var1).c.clone();
   }

   private double[] b(int var1) {
      return (double[])this.e.get(var1).d.clone();
   }

   public final void b(double[] var1) {
      if (var1.length != this.b()) {
         throw new oS(var1.length, this.b());
      }

      this.b.a(var1, this.d);
      Iterator var2 = this.e.iterator();

      while (var2.hasNext()) {
         uF.a var3;
         (var3 = (uF.a)var2.next()).b.a(var1, var3.c);
      }
   }

   public final double[] g() {
      double[] var1 = new double[this.b()];
      this.b.b(this.d, var1);
      Iterator var2 = this.e.iterator();

      while (var2.hasNext()) {
         uF.a var3;
         (var3 = (uF.a)var2.next()).b.b(var3.c, var1);
      }

      return var1;
   }

   static class a {
      final vd a;
      final uE b;
      final double[] c;
      final double[] d;

      a(vd var1, int var2) {
         int var3 = var1.a();
         this.a = var1;
         this.b = new uE(var2, var3);
         this.c = new double[var3];
         this.d = new double[var3];
      }

      // $VF: synthetic method
      static uE a(uF.a var0) {
         return var0.b;
      }

      // $VF: synthetic method
      static double[] b(uF.a var0) {
         return var0.c;
      }

      // $VF: synthetic method
      static double[] c(uF.a var0) {
         return var0.d;
      }

      // $VF: synthetic method
      static vd d(uF.a var0) {
         return var0.a;
      }
   }
}
