package org.valkyrienskies.core.impl.shadow;

public abstract class ns<FUNC extends lc> implements nu<FUNC> {
   private static final double d = 1.0E-14;
   private static final double e = 1.0E-15;
   final double a;
   final double b;
   final double c;
   private Cc.a f;
   private double g;
   private double h;
   private double i;
   private FUNC j;

   protected ns(double var1) {
      this(1.0E-14, var1, 1.0E-15);
   }

   protected ns(double var1, double var3) {
      this(var1, var3, 1.0E-15);
   }

   protected ns(double var1, double var3, double var5) {
      this.b = var3;
      this.c = var1;
      this.a = var5;
      this.f = Cc.a.a();
   }

   @Override
   public final int b() {
      Cc.a var1 = this.f;
      return this.f.b;
   }

   @Override
   public final int c() {
      Cc.a var1 = this.f;
      return this.f.e;
   }

   public final double d() {
      return this.g;
   }

   public final double e() {
      return this.h;
   }

   public final double f() {
      return this.i;
   }

   @Override
   public final double g() {
      return this.b;
   }

   @Override
   public final double h() {
      return this.c;
   }

   @Override
   public final double i() {
      return this.a;
   }

   protected final double b(double var1) {
      this.k();
      return this.j.a(var1);
   }

   protected void a(int var1, FUNC var2, double var3, double var5, double var7) {
      Ci.a(var2);
      this.g = var3;
      this.h = var5;
      this.i = var7;
      this.j = (FUNC)var2;
      this.f = this.f.b(var1).a(0);
   }

   @Override
   public double b(int var1, FUNC var2, double var3, double var5, double var7) {
      this.a(var1, (FUNC)var2, var3, var5, var7);
      return this.j();
   }

   @Override
   public double a(int var1, FUNC var2, double var3, double var5) {
      return this.b(var1, (FUNC)var2, var3, var5, var3 + 0.5 * (var5 - var3));
   }

   @Override
   public final double a(int var1, FUNC var2, double var3) {
      return this.b(var1, (FUNC)var2, Double.NaN, Double.NaN, var3);
   }

   protected abstract double j();

   protected final boolean a(double var1, double var3) {
      return nP.a(this.j, var1, var3);
   }

   protected static boolean a(double var0, double var2, double var4) {
      double var10 = var4;
      double var8 = var2;
      return var0 < var8 && var8 < var10;
   }

   protected static void b(double var0, double var2) {
      nP.b(var0, var2);
   }

   protected static void b(double var0, double var2, double var4) {
      nP.b(var0, var2, var4);
   }

   protected final void c(double var1, double var3) {
      double var8 = var3;
      double var6 = var1;
      lc var10 = this.j;
      if (this.j == null) {
         throw new pl(px.FUNCTION);
      }

      nP.b(var6, var8);
      if (!nP.a(var10, var6, var8)) {
         throw new pe(var6, var8, var10.a(var6), var10.a(var8));
      }
   }

   protected final void k() {
      try {
         Cc.a var1 = this.f;
         this.f.d(1);
      } catch (pc var2) {
         throw new pp(var2.a);
      }
   }
}
