package org.valkyrienskies.core.impl.shadow;

@Deprecated
public abstract class yB<FUNC extends kY> implements xY<FUNC> {
   private Cb a = new Cb();
   private ya<yq> b;
   private double[] c;
   private tq d;
   @Deprecated
   private double[] e;
   private double[] f;
   private FUNC g;

   @Deprecated
   public yB() {
      this(new yu());
   }

   public yB(ya<yq> var1) {
      this.b = var1;
   }

   @Override
   public final int a() {
      Cb var1 = this.a;
      return this.a.a;
   }

   @Override
   public final int b() {
      Cb var1 = this.a;
      return this.a.b;
   }

   @Override
   public final ya<yq> c() {
      return this.b;
   }

   protected final double[] a(double[] var1) {
      try {
         this.a.c();
      } catch (pc var2) {
         throw new pp(var2.a);
      }

      return this.g.a(var1);
   }

   @Deprecated
   @Override
   public yq a(int var1, FUNC var2, double[] var3, double[] var4, double[] var5) {
      double[] var6 = var5;
      var5 = var4;
      var4 = var3;
      kY var9 = var2;
      int var8 = var1;
      yB var7 = this;
      if (var9 == null) {
         throw new pl();
      } else if (var4 == null) {
         throw new pl();
      } else if (var5 == null) {
         throw new pl();
      } else if (var6 == null) {
         throw new pl();
      } else if (var4.length != var5.length) {
         throw new oS(var4.length, var5.length);
      } else {
         return var7.a(var8, var9, new yv(var4), new yw(var5), new yg(var6));
      }
   }

   private yq b(int var1, FUNC var2, yo... var3) {
      return this.a(var1, (FUNC)var2, var3);
   }

   @Deprecated
   private yq b(int var1, FUNC var2, double[] var3, double[] var4, double[] var5) {
      if (var2 == null) {
         throw new pl();
      } else if (var3 == null) {
         throw new pl();
      } else if (var4 == null) {
         throw new pl();
      } else if (var5 == null) {
         throw new pl();
      } else if (var3.length != var4.length) {
         throw new oS(var3.length, var4.length);
      } else {
         return this.a(var1, (FUNC)var2, new yv(var3), new yw(var4), new yg(var5));
      }
   }

   public final yq a(int var1, FUNC var2, yo... var3) {
      this.a.a(var1);
      this.a.d();
      this.g = (FUNC)var2;
      this.a(var3);
      this.l();
      this.i();
      return this.h();
   }

   public final double[] d() {
      return (double[])this.f.clone();
   }

   public final tq e() {
      return this.d.i();
   }

   public final double[] f() {
      return (double[])this.c.clone();
   }

   protected final FUNC g() {
      return this.g;
   }

   protected abstract yq h();

   @Deprecated
   private double[] j() {
      return this.c;
   }

   @Deprecated
   private double[] k() {
      return this.e;
   }

   public void i() {
      int var1 = this.c.length;
      this.e = new double[var1];

      for (int var2 = 0; var2 < var1; var2++) {
         this.e[var2] = this.d.b(var2, var2);
      }
   }

   private void a(yo... var1) {
      yo[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         yo var4;
         if ((var4 = var5[var3]) instanceof yv) {
            this.c = ((yv)var4).a();
         } else if (var4 instanceof yw) {
            this.d = ((yw)var4).a();
         } else if (var4 instanceof yg) {
            this.f = ((yg)var4).a();
         }
      }
   }

   private void l() {
      if (this.c.length != this.d.g()) {
         throw new oS(this.c.length, this.d.g());
      }
   }
}
