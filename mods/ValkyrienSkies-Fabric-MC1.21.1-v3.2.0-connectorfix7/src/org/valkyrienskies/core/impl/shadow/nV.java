package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class nV implements Serializable {
   private static final long a = 20120201L;
   private int b = 0;
   private double[] c = null;
   private double[] d = null;
   private double[] e = null;
   private boolean f = true;

   private boolean a() {
      if (this.b == 0) {
         throw new oX(px.ROOTS_OF_UNITY_NOT_COMPUTED_YET);
      } else {
         return this.f;
      }
   }

   private void a(int var1) {
      if (var1 == 0) {
         throw new pr(px.CANNOT_COMPUTE_0TH_ROOT_OF_UNITY);
      }

      this.f = var1 > 0;
      if ((var1 = BY.a(var1)) != this.b) {
         double var3;
         double var5 = BY.o(var3 = (Math.PI * 2) / var1);
         double var7 = BY.n(var3);
         this.c = new double[var1];
         this.d = new double[var1];
         this.e = new double[var1];
         this.c[0] = 1.0;
         this.d[0] = 0.0;
         this.e[0] = 0.0;

         for (int var2 = 1; var2 < var1; var2++) {
            this.c[var2] = this.c[var2 - 1] * var5 - this.d[var2 - 1] * var7;
            this.d[var2] = this.c[var2 - 1] * var7 + this.d[var2 - 1] * var5;
            this.e[var2] = -this.d[var2];
         }

         this.b = var1;
      }
   }

   private double b(int var1) {
      if (this.b == 0) {
         throw new oX(px.ROOTS_OF_UNITY_NOT_COMPUTED_YET);
      } else if (var1 >= 0 && var1 < this.b) {
         return this.c[var1];
      } else {
         throw new po(px.OUT_OF_RANGE_ROOT_OF_UNITY_INDEX, var1, 0, this.b - 1);
      }
   }

   private double c(int var1) {
      if (this.b == 0) {
         throw new oX(px.ROOTS_OF_UNITY_NOT_COMPUTED_YET);
      } else if (var1 < 0 || var1 >= this.b) {
         throw new po(px.OUT_OF_RANGE_ROOT_OF_UNITY_INDEX, var1, 0, this.b - 1);
      } else {
         return this.f ? this.d[var1] : this.e[var1];
      }
   }

   private int b() {
      return this.b;
   }
}
