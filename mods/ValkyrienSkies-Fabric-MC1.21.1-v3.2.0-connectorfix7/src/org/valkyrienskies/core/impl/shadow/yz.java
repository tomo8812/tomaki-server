package org.valkyrienskies.core.impl.shadow;

@Deprecated
public abstract class yz<FUNC extends kW> implements xV<FUNC> {
   private Cb b = new Cb();
   private ya<yp> c;
   yf a;
   private double[] d;
   private double[] e;
   private double[] f;
   private kW g;

   @Deprecated
   public yz() {
      this(new yt());
   }

   public yz(ya<yp> var1) {
      this.c = var1;
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

   @Override
   public final ya<yp> c() {
      return this.c;
   }

   protected final double a(double[] var1) {
      try {
         this.b.c();
      } catch (pc var2) {
         throw new pp(var2.a);
      }

      return this.g.a(var1);
   }

   @Deprecated
   @Override
   public yp a(int var1, FUNC var2, yf var3, double[] var4) {
      return this.a(var1, (FUNC)var2, var3, new yg(var4));
   }

   private yp b(int var1, FUNC var2, yf var3, yo... var4) {
      return this.a(var1, (FUNC)var2, var3, var4);
   }

   @Deprecated
   public yp a(int var1, FUNC var2, yf var3, double[] var4) {
      return this.a(var1, (FUNC)var2, var3, new yg(var4));
   }

   public yp a(int var1, FUNC var2, yf var3, yo... var4) {
      this.b.a(var1);
      this.b.d();
      this.g = var2;
      this.a = var3;
      yo[] var14 = var4;
      yz var12 = this;
      yo[] var16 = var14;
      int var8 = var14.length;

      for (int var9 = 0; var9 < var8; var9++) {
         yo var10;
         if ((var10 = var16[var9]) instanceof yg) {
            var12.d = ((yg)var10).a();
         } else if (var10 instanceof yr) {
            yr var11 = (yr)var10;
            var12.e = var11.a();
            var12.f = var11.b();
         }
      }

      yz var13 = this;
      if (this.d != null) {
         int var15 = var13.d.length;
         if (var13.e != null) {
            if (var13.e.length != var15) {
               throw new oS(var13.e.length, var15);
            }

            for (int var17 = 0; var17 < var15; var17++) {
               double var21 = var13.d[var17];
               double var23 = var13.e[var17];
               if (var21 < var23) {
                  throw new pn(var21, var23, true);
               }
            }
         }

         if (var13.f != null) {
            if (var13.f.length != var15) {
               throw new oS(var13.f.length, var15);
            }

            for (int var18 = 0; var18 < var15; var18++) {
               double var22 = var13.d[var18];
               double var24 = var13.f[var18];
               if (var22 > var24) {
                  throw new pm(var22, var24, true);
               }
            }
         }

         if (var13.e == null) {
            var13.e = new double[var15];

            for (int var19 = 0; var19 < var15; var19++) {
               var13.e[var19] = Double.NEGATIVE_INFINITY;
            }
         }

         if (var13.f == null) {
            var13.f = new double[var15];

            for (int var20 = 0; var20 < var15; var20++) {
               var13.f[var20] = Double.POSITIVE_INFINITY;
            }
         }
      }

      return this.d();
   }

   private void a(yo... var1) {
      yo[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         yo var4;
         if ((var4 = var5[var3]) instanceof yg) {
            this.d = ((yg)var4).a();
         } else if (var4 instanceof yr) {
            yr var6 = (yr)var4;
            this.e = var6.a();
            this.f = var6.b();
         }
      }
   }

   public final yf e() {
      return this.a;
   }

   public final double[] f() {
      return this.d == null ? null : (double[])this.d.clone();
   }

   public final double[] g() {
      return this.e == null ? null : (double[])this.e.clone();
   }

   public final double[] h() {
      return this.f == null ? null : (double[])this.f.clone();
   }

   protected abstract yp d();

   private void i() {
      if (this.d != null) {
         int var1 = this.d.length;
         if (this.e != null) {
            if (this.e.length != var1) {
               throw new oS(this.e.length, var1);
            }

            for (int var2 = 0; var2 < var1; var2++) {
               double var3 = this.d[var2];
               double var5 = this.e[var2];
               if (var3 < var5) {
                  throw new pn(var3, var5, true);
               }
            }
         }

         if (this.f != null) {
            if (this.f.length != var1) {
               throw new oS(this.f.length, var1);
            }

            for (int var7 = 0; var7 < var1; var7++) {
               double var10 = this.d[var7];
               double var11 = this.f[var7];
               if (var10 > var11) {
                  throw new pm(var10, var11, true);
               }
            }
         }

         if (this.e == null) {
            this.e = new double[var1];

            for (int var8 = 0; var8 < var1; var8++) {
               this.e[var8] = Double.NEGATIVE_INFINITY;
            }
         }

         if (this.f == null) {
            this.f = new double[var1];

            for (int var9 = 0; var9 < var1; var9++) {
               this.f[var9] = Double.POSITIVE_INFINITY;
            }
         }
      }
   }
}
