package org.valkyrienskies.core.impl.shadow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class zu extends oe {
   private static int e;
   private static final String f = "US-ASCII";
   private static final long g = 5729073523949762654L;
   private zD h;
   final List<Aw> b;
   Aw c = null;
   private double i = Double.NEGATIVE_INFINITY;
   private double j = Double.POSITIVE_INFINITY;
   private double k = 0.0;
   private final int l;
   boolean d = false;
   private double[] m = null;

   public zu() {
      this(1000);
   }

   private zu(int var1) {
      this(1000, new zD());
   }

   public zu(int var1, zF var2) {
      this(var1, new zD(var2));
   }

   private zu(zF var1) {
      this(1000, var1);
   }

   @Deprecated
   private zu(int var1, zE var2) {
      this(1000, var2.a);
   }

   @Deprecated
   private zu(zE var1) {
      this(1000, var1);
   }

   private zu(int var1, zD var2) {
      super(var2.c());
      if (var1 <= 0) {
         throw new pk(var1);
      }

      this.l = var1;
      this.h = var2;
      this.b = new ArrayList<>();
   }

   private void a(double[] var1) {
      zu.a var2 = new zu.a(var1);

      try {
         var2.a();
         this.a(new zu.a(var1));
      } catch (IOException var3) {
         throw new oY();
      }

      this.d = true;
   }

   public final void a(URL var1) {
      Ci.a(var1);
      Charset var2 = Charset.forName("US-ASCII");
      BufferedReader var3 = new BufferedReader(new InputStreamReader(var1.openStream(), var2));

      try {
         new zu.c(var3).a();
         if (this.c.d() == 0L) {
            throw new pr(px.URL_CONTAINS_NO_DATA, var1);
         }

         var3 = new BufferedReader(new InputStreamReader(var1.openStream(), var2));
         this.a(new zu.c(var3));
         this.d = true;
      } finally {
         try {
            var3.close();
         } catch (IOException var8) {
         }
      }
   }

   private void a(File var1) {
      Ci.a(var1);
      Charset var2 = Charset.forName("US-ASCII");
      FileInputStream var3 = new FileInputStream(var1);
      BufferedReader var4 = new BufferedReader(new InputStreamReader(var3, var2));

      try {
         new zu.c(var4).a();
         var3 = new FileInputStream(var1);
         var4 = new BufferedReader(new InputStreamReader(var3, var2));
         this.a(new zu.c(var4));
         this.d = true;
      } finally {
         try {
            var4.close();
         } catch (IOException var8) {
         }
      }
   }

   private void a(zu.b var1) {
      this.j = this.c.c();
      this.i = this.c.a();
      this.k = (this.i - this.j) / this.l;
      if (!this.b.isEmpty()) {
         this.b.clear();
      }

      for (int var2 = 0; var2 < this.l; var2++) {
         Aw var3 = new Aw();
         this.b.add(var2, var3);
      }

      var1.b();
      this.m = new double[this.l];
      this.m[0] = (double)this.b.get(0).d() / this.c.d();

      for (int var4 = 1; var4 < this.l - 1; var4++) {
         this.m[var4] = this.m[var4 - 1] + (double)this.b.get(var4).d() / this.c.d();
      }

      this.m[this.l - 1] = 1.0;
   }

   private int f(double var1) {
      return BY.a(BY.b((int)BY.z((var1 - this.j) / this.k) - 1, 0), this.l - 1);
   }

   public final double j() {
      if (!this.d) {
         throw new oX(px.DISTRIBUTION_NOT_LOADED);
      } else {
         return this.b();
      }
   }

   public final Aw k() {
      return this.c;
   }

   private int m() {
      return this.l;
   }

   public final List<Aw> l() {
      return this.b;
   }

   private double[] n() {
      double[] var1 = new double[this.l];

      for (int var2 = 0; var2 < this.l - 1; var2++) {
         var1[var2] = this.j + this.k * (var2 + 1);
      }

      var1[this.l - 1] = this.i;
      return var1;
   }

   private double[] o() {
      int var1;
      double[] var2 = new double[var1 = this.m.length];
      System.arraycopy(this.m, 0, var2, 0, var1);
      return var2;
   }

   private boolean p() {
      return this.d;
   }

   private void b(long var1) {
      this.h.a(var1);
   }

   @Override
   public final double b(double var1) {
      return 0.0;
   }

   @Override
   public final double d(double var1) {
      if (!(var1 < this.j) && !(var1 > this.i)) {
         int var3 = this.f(var1);
         return this.a(this.b.get(var3)).d(var1) * this.b(var3) / this.d(var3);
      } else {
         return 0.0;
      }
   }

   @Override
   public final double e(double var1) {
      if (var1 < this.j) {
         return 0.0;
      }

      if (var1 >= this.i) {
         return 1.0;
      }

      int var3 = this.f(var1);
      double var4 = this.c(var3);
      double var6 = this.b(var3);
      double var17 = var1;
      zu var8 = this;
      int var9 = this.f(var17);
      oe var19;
      if ((var19 = var8.a(var8.b.get(var9))) instanceof oj) {
         return var1 < var19.c() ? var4 : var4 + var6;
      }

      double[] var20 = this.n();
      double var10 = this.d(var3);
      double var12 = var3 == 0 ? this.j : var20[var3 - 1];
      double var14 = (var19.e(var1) - var19.e(var12)) / var10;
      return var4 + var6 * var14;
   }

   @Override
   public final double a(double var1) {
      if (var1 < 0.0 || var1 > 1.0) {
         throw new po(var1, 0, 1);
      }

      if (var1 == 0.0) {
         return this.j;
      }

      if (var1 == 1.0) {
         return this.i;
      }

      int var3 = 0;

      while (true) {
         int var5 = var3;
         if (!(this.m[var5] < var1)) {
            oe var4 = this.a(this.b.get(var3));
            double var18 = this.d(var3);
            double[] var7 = this.n();
            double var8 = var3 == 0 ? this.j : var7[var3 - 1];
            double var10 = var4.e(var8);
            double var12 = this.b(var3);
            double var14 = this.c(var3);
            double var16;
            return (var16 = var1 - var14) <= 0.0 ? var8 : var4.a(var10 + var16 * var18 / var12);
         }

         var3++;
      }
   }

   @Override
   public final double c() {
      return this.c.b();
   }

   @Override
   public final double d() {
      return this.c.g();
   }

   @Override
   public final double e() {
      return this.j;
   }

   @Override
   public final double f() {
      return this.i;
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
   public final void a(long var1) {
      this.h.a(var1);
   }

   private double b(int var1) {
      return var1 == 0 ? this.m[0] : this.m[var1] - this.m[var1 - 1];
   }

   private double c(int var1) {
      return var1 == 0 ? 0.0 : this.m[var1 - 1];
   }

   private double d(int var1) {
      double[] var2 = this.n();
      oe var3 = this.a(this.b.get(var1));
      return var1 == 0 ? var3.a(this.j, var2[0]) : var3.a(var2[var1 - 1], var2[var1]);
   }

   private oI g(double var1) {
      int var3 = this.f(var1);
      return this.a(this.b.get(var3));
   }

   private double e(int var1) {
      return this.m[var1];
   }

   private oe a(Aw var1) {
      return var1.d() != 1L && var1.g() != 0.0 ? new oE(this.h.c(), var1.b(), var1.e(), 1.0E-9) : new oj(var1.b());
   }

   class a extends zu.b {
      private double[] a;

      a(double[] var2) {
         super((byte)0);
         Ci.a(var2);
         this.a = var2;
      }

      @Override
      public final void a() {
         zu.this.c = new Aw();

         for (int var1 = 0; var1 < this.a.length; var1++) {
            zu.this.c.a(this.a[var1]);
         }
      }

      @Override
      public final void b() {
         for (int var1 = 0; var1 < this.a.length; var1++) {
            zu.this.b.get(zu.this.f(this.a[var1])).a(this.a[var1]);
         }
      }
   }

   abstract class b {
      private b() {
      }

      public abstract void b();

      public abstract void a();

      // $VF: synthetic method
      b(byte var2) {
         this();
      }
   }

   class c extends zu.b {
      private BufferedReader a;

      c(BufferedReader var2) {
         super((byte)0);
         this.a = var2;
      }

      @Override
      public final void b() {
         String var1;
         while ((var1 = this.a.readLine()) != null) {
            double var2 = Double.parseDouble(var1);
            zu.this.b.get(zu.this.f(var2)).a(var2);
         }

         this.a.close();
         this.a = null;
      }

      @Override
      public final void a() {
         zu.this.c = new Aw();

         String var1;
         while ((var1 = this.a.readLine()) != null) {
            double var2 = Double.parseDouble(var1);
            zu.this.c.a(var2);
         }

         this.a.close();
         this.a = null;
      }
   }
}
