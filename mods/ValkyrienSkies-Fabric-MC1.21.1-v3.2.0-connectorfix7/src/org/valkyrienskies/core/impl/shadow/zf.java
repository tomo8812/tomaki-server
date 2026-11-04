package org.valkyrienskies.core.impl.shadow;

@Deprecated
public abstract class zf implements zl {
   private final ya<zm> a;
   private final Cb b = new Cb();
   private yf c;
   private double d;
   private double e;
   private double f;
   private lc g;

   protected zf(ya<zm> var1) {
      this.a = var1;
   }

   @Override
   public final int a() {
      Cb var1 = this.b;
      return this.b.a;
   }

   @Override
   public final int b() {
      Cb var1 = this.b;
      return this.b.b;
   }

   public final yf d() {
      return this.c;
   }

   public final double e() {
      return this.d;
   }

   public final double f() {
      return this.e;
   }

   public final double g() {
      return this.f;
   }

   protected final double a(double var1) {
      try {
         this.b.c();
      } catch (pc var3) {
         throw new pp(var3.a);
      }

      return this.g.a(var1);
   }

   @Override
   public final zm a(int var1, lc var2, yf var3, double var4, double var6, double var8) {
      if (var2 == null) {
         throw new pl();
      }

      if (var3 == null) {
         throw new pl();
      }

      this.d = var4;
      this.e = var6;
      this.f = var8;
      this.c = var3;
      this.g = var2;
      this.b.a(var1);
      this.b.d();
      return this.h();
   }

   @Override
   public final zm a(int var1, lc var2, yf var3, double var4, double var6) {
      return this.a(var1, var2, var3, var4, var6, var4 + 0.5 * (var6 - var4));
   }

   @Override
   public final ya<zm> c() {
      return this.a;
   }

   protected abstract zm h();
}
