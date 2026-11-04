package org.valkyrienskies.core.impl.shadow;

public abstract class mm implements mt {
   private static double a;
   private static double b;
   private static int c;
   private static int d;
   @Deprecated
   private Cb e;
   private Cc.a f;
   private final double g;
   private final double h;
   private final int i;
   private Cc.a j;
   private lc k;
   private double l;
   private double m;

   protected mm(double var1, double var3, int var5, int var6) {
      this.h = var1;
      this.g = var3;
      if (var5 <= 0) {
         throw new pk(var5);
      }

      if (var6 <= var5) {
         throw new pn(var6, var5, false);
      }

      this.i = var5;
      this.f = Cc.a.a().b(var6);
      var var7 = Cb.a(this.f);
      this.e = var7;
      this.j = Cc.a.a();
   }

   private mm(double var1, double var3) {
      this(var1, var3, 3, Integer.MAX_VALUE);
   }

   protected mm(int var1, int var2) {
      this(1.0E-6, 1.0E-15, var1, var2);
   }

   @Override
   public final double a() {
      return this.h;
   }

   @Override
   public final double b() {
      return this.g;
   }

   @Override
   public final int c() {
      return this.i;
   }

   @Override
   public final int d() {
      Cc.a var1 = this.f;
      return this.f.b;
   }

   @Override
   public final int e() {
      Cc.a var1 = this.j;
      return this.j.e;
   }

   @Override
   public final int f() {
      Cc.a var1 = this.f;
      return this.f.e;
   }

   protected final void g() {
      Cc.a var1 = this.f;
      this.f.d(1);
   }

   protected final double h() {
      return this.l;
   }

   protected final double i() {
      return this.m;
   }

   protected final double a(double var1) {
      try {
         Cc.a var3 = this.j;
         this.j.d(1);
      } catch (pc var4) {
         throw new pp(var4.a);
      }

      return this.k.a(var1);
   }

   private void b(int var1, lc var2, double var3, double var5) {
      Ci.a(var2);
      nP.b(var3, var5);
      this.l = var3;
      this.m = var5;
      this.k = var2;
      this.j = this.j.b(var1).a(0);
      this.f = this.f.a(0);
   }

   @Override
   public final double a(int var1, lc var2, double var3, double var5) {
      double var12 = var5;
      double var10 = var3;
      lc var16 = var2;
      int var15 = var1;
      mm var14 = this;
      Ci.a(var16);
      nP.b(var10, var12);
      var14.l = var10;
      var14.m = var12;
      var14.k = var16;
      var14.j = var14.j.b(var15).a(0);
      var14.f = var14.f.a(0);
      return this.j();
   }

   protected abstract double j();
}
