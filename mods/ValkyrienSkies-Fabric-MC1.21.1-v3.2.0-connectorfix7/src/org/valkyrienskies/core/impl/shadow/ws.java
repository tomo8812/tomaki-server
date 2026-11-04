package org.valkyrienskies.core.impl.shadow;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class ws implements wB {
   protected double d;
   protected double[] e;
   protected double f;
   protected double[] g;
   protected double[] h;
   private double[] a;
   private double[] b;
   private double[][] c;
   private double[][] n;
   public double i;
   public double j;
   public double k;
   public double l;
   private boolean o;
   public boolean m;
   private boolean p;
   private uE q;
   private uE[] r;

   public ws() {
      this.i = Double.NaN;
      this.j = Double.NaN;
      this.k = Double.NaN;
      this.l = Double.NaN;
      this.d = Double.NaN;
      this.f = Double.NaN;
      this.e = null;
      this.o = false;
      this.m = true;
      this.p = true;
      this.q = null;
      this.r = null;
      this.c(-1);
   }

   public ws(double[] var1, boolean var2, uE var3, uE[] var4) {
      this.i = Double.NaN;
      this.j = Double.NaN;
      this.k = Double.NaN;
      this.l = Double.NaN;
      this.d = Double.NaN;
      this.f = Double.NaN;
      this.e = var1;
      this.o = false;
      this.m = var2;
      this.p = true;
      this.q = var3;
      this.r = var4 == null ? null : (uE[])var4.clone();
      this.c(var1.length);
   }

   public ws(ws var1) {
      this.i = var1.i;
      this.j = var1.j;
      this.k = var1.k;
      this.l = var1.l;
      this.d = var1.d;
      this.f = var1.f;
      if (var1.e == null) {
         this.e = null;
         this.q = null;
         this.r = null;
         this.c(-1);
      } else {
         this.e = (double[])var1.e.clone();
         this.g = (double[])var1.g.clone();
         this.h = (double[])var1.h.clone();
         this.a = (double[])var1.a.clone();
         this.b = (double[])var1.b.clone();
         this.c = new double[var1.c.length][];
         this.n = new double[var1.n.length][];

         for (int var2 = 0; var2 < this.c.length; var2++) {
            this.c[var2] = (double[])var1.c[var2].clone();
            this.n[var2] = (double[])var1.n[var2].clone();
         }
      }

      this.o = var1.o;
      this.m = var1.m;
      this.p = var1.p;
      this.q = var1.q;
      this.r = var1.r == null ? null : (uE[])var1.r.clone();
   }

   private void c(int var1) {
      if (var1 < 0) {
         this.g = null;
         this.h = null;
         this.a = null;
         this.b = null;
         this.c = null;
         this.n = null;
      } else {
         this.g = new double[var1];
         this.h = new double[var1];
         uE var2 = this.q;
         this.a = new double[this.q.b];
         var2 = this.q;
         this.b = new double[this.q.b];
         if (this.r == null) {
            this.c = null;
            this.n = null;
         } else {
            this.c = new double[this.r.length][];
            this.n = new double[this.r.length][];

            for (int var3 = 0; var3 < this.r.length; var3++) {
               this.c[var3] = new double[(var2 = this.r[var3]).b];
               this.n[var3] = new double[(var2 = this.r[var3]).b];
            }
         }
      }
   }

   protected void a(double[] var1, boolean var2, uE var3, uE[] var4) {
      this.i = Double.NaN;
      this.j = Double.NaN;
      this.k = Double.NaN;
      this.l = Double.NaN;
      this.d = Double.NaN;
      this.f = Double.NaN;
      this.e = var1;
      this.o = false;
      this.m = var2;
      this.p = true;
      this.q = var3;
      this.r = (uE[])var4.clone();
      this.c(var1.length);
   }

   @Override
   public final wB d() {
      this.m();
      return this.a();
   }

   protected abstract ws a();

   public void c() {
      this.i = this.j;
      this.k = this.i;
      this.l = this.j;
   }

   public void a(double var1) {
      this.j = var1;
      this.l = this.j;
      this.d = this.j - this.i;
      this.d(var1);
      this.o = false;
   }

   public final void b(double var1) {
      this.k = var1;
   }

   public final void c(double var1) {
      this.l = var1;
   }

   public final double e() {
      return this.i;
   }

   public final double f() {
      return this.j;
   }

   @Override
   public final double g() {
      return this.k;
   }

   @Override
   public final double h() {
      return this.l;
   }

   @Override
   public final double i() {
      return this.f;
   }

   @Override
   public final void d(double var1) {
      this.f = var1;
      this.p = true;
   }

   @Override
   public final boolean j() {
      return this.m;
   }

   protected abstract void a(double var1, double var3);

   private void n() {
      if (this.p) {
         double var1 = this.j - this.f;
         double var3 = this.d == 0.0 ? 0.0 : (this.d - var1) / this.d;
         this.a(var3, var1);
         this.p = false;
      }
   }

   @Override
   public final double[] k() {
      this.n();
      this.q.a(this.g, this.a);
      return this.a;
   }

   @Override
   public final double[] l() {
      this.n();
      this.q.a(this.h, this.b);
      return this.b;
   }

   @Override
   public final double[] a(int var1) {
      this.n();
      this.r[var1].a(this.g, this.c[var1]);
      return this.c[var1];
   }

   @Override
   public final double[] b(int var1) {
      this.n();
      this.r[var1].a(this.h, this.n[var1]);
      return this.n[var1];
   }

   public final void m() {
      if (!this.o) {
         this.b();
         this.o = true;
      }
   }

   protected void b() {
   }

   @Override
   public abstract void writeExternal(ObjectOutput var1);

   @Override
   public abstract void readExternal(ObjectInput var1);

   protected final void a(ObjectOutput var1) {
      if (this.e == null) {
         var1.writeInt(-1);
      } else {
         var1.writeInt(this.e.length);
      }

      var1.writeDouble(this.i);
      var1.writeDouble(this.j);
      var1.writeDouble(this.k);
      var1.writeDouble(this.l);
      var1.writeDouble(this.d);
      var1.writeBoolean(this.m);
      var1.writeObject(this.q);
      var1.write(this.r.length);
      uE[] var2 = this.r;
      int var3 = this.r.length;

      for (int var4 = 0; var4 < var3; var4++) {
         uE var5 = var2[var4];
         var1.writeObject(var5);
      }

      if (this.e != null) {
         for (int var7 = 0; var7 < this.e.length; var7++) {
            var1.writeDouble(this.e[var7]);
         }
      }

      var1.writeDouble(this.f);

      try {
         this.m();
      } catch (pc var6) {
         IOException var8;
         (var8 = new IOException(var6.getLocalizedMessage())).initCause(var6);
         throw var8;
      }
   }

   protected final double a(ObjectInput var1) {
      int var2 = var1.readInt();
      this.i = var1.readDouble();
      this.j = var1.readDouble();
      this.k = var1.readDouble();
      this.l = var1.readDouble();
      this.d = var1.readDouble();
      this.m = var1.readBoolean();
      this.q = (uE)var1.readObject();
      this.r = new uE[var1.read()];

      for (int var3 = 0; var3 < this.r.length; var3++) {
         this.r[var3] = (uE)var1.readObject();
      }

      this.p = true;
      if (var2 < 0) {
         this.e = null;
      } else {
         this.e = new double[var2];

         for (int var4 = 0; var4 < this.e.length; var4++) {
            this.e[var4] = var1.readDouble();
         }
      }

      this.f = Double.NaN;
      this.c(var2);
      this.o = true;
      return var1.readDouble();
   }
}
