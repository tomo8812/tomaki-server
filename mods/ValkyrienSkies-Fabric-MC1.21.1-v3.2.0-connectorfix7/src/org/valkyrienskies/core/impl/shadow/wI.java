package org.valkyrienskies.core.impl.shadow;

public abstract class wI<PAIR> extends wJ<PAIR> {
   private double[] a;
   private double[] b;
   private double[] c;

   public wI(wK<PAIR> var1) {
      super(var1);
   }

   @Override
   public PAIR a(wO... var1) {
      return super.a(var1);
   }

   @Override
   public void b(wO... var1) {
      super.b(var1);
      wO[] var14;
      int var2 = (var14 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         wO var4;
         if ((var4 = var14[var3]) instanceof wL) {
            this.a = ((wL)var4).a();
         } else if (var4 instanceof wS) {
            wS var19 = (wS)var4;
            this.b = var19.a();
            this.c = var19.b();
         }
      }

      wI var15 = this;
      if (this.a != null) {
         var2 = var15.a.length;
         if (var15.b != null) {
            if (var15.b.length != var2) {
               throw new oS(var15.b.length, var2);
            }

            for (int var17 = 0; var17 < var2; var17++) {
               double var10 = var15.a[var17];
               double var12 = var15.b[var17];
               if (var10 < var12) {
                  throw new pn(var10, var12, true);
               }
            }
         }

         if (var15.c != null) {
            if (var15.c.length != var2) {
               throw new oS(var15.c.length, var2);
            }

            for (int var18 = 0; var18 < var2; var18++) {
               double var20 = var15.a[var18];
               double var21 = var15.c[var18];
               if (var20 > var21) {
                  throw new pm(var20, var21, true);
               }
            }
         }
      }
   }

   public final double[] e() {
      return this.a == null ? null : (double[])this.a.clone();
   }

   public final double[] f() {
      return this.b == null ? null : (double[])this.b.clone();
   }

   public final double[] g() {
      return this.c == null ? null : (double[])this.c.clone();
   }

   private void b() {
      if (this.a != null) {
         int var1 = this.a.length;
         if (this.b != null) {
            if (this.b.length != var1) {
               throw new oS(this.b.length, var1);
            }

            for (int var2 = 0; var2 < var1; var2++) {
               double var3 = this.a[var2];
               double var5 = this.b[var2];
               if (var3 < var5) {
                  throw new pn(var3, var5, true);
               }
            }
         }

         if (this.c != null) {
            if (this.c.length != var1) {
               throw new oS(this.c.length, var1);
            }

            for (int var7 = 0; var7 < var1; var7++) {
               double var8 = this.a[var7];
               double var9 = this.c[var7];
               if (var8 > var9) {
                  throw new pm(var8, var9, true);
               }
            }
         }
      }
   }
}
