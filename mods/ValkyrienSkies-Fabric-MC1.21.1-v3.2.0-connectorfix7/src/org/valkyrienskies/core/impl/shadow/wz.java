package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

public final class wz extends ws {
   private static final long a = -7179861704951334960L;
   private double[] b;
   private double c;
   private double n;
   private double[] o;
   private sy p;

   public wz() {
   }

   private wz(wz var1) {
      super(var1);
      this.c = var1.c;
      this.n = var1.n;
      if (var1.o != null) {
         this.o = (double[])var1.o.clone();
      }

      if (var1.p != null) {
         sy var2 = var1.p;
         this.p = new sy(var1.p.a, true);
      }

      if (var1.b != null) {
         this.b = (double[])var1.b.clone();
      }
   }

   @Override
   protected final ws a() {
      return new wz(this);
   }

   @Override
   public final void a(double[] var1, boolean var2, uE var3, uE[] var4) {
      super.a(var1, var2, var3, var4);
      this.b = new double[var1.length];
   }

   public final void a(double var1, double var3, double[] var5, sy var6) {
      this.n = var1;
      this.c = var3;
      this.o = var5;
      this.p = var6;
      this.d(this.i());
   }

   public final void e(double var1) {
      double var3 = var1 / this.c;

      for (int var5 = 0; var5 < this.o.length; var5++) {
         this.o[var5] = this.o[var5] * var3;
      }

      sy var11 = this.p;
      double[][] var12 = this.p.a;
      double var6 = var3;

      for (int var8 = 0; var8 < var12.length; var8++) {
         var6 *= var3;
         double[] var9 = var12[var8];

         for (int var10 = 0; var10 < var9.length; var10++) {
            var9[var10] *= var6;
         }
      }

      this.c = var1;
   }

   private double[] n() {
      this.k();
      return this.b;
   }

   @Override
   protected final void a(double var1, double var3) {
      double var5;
      double var7 = (var5 = super.f - this.n) / this.c;
      Arrays.fill(this.b, 0.0);
      Arrays.fill(super.h, 0.0);
      sy var18 = this.p;
      double[][] var19 = this.p.a;

      for (int var2 = this.p.a.length - 1; var2 >= 0; var2--) {
         int var21 = var2 + 2;
         double[] var4 = var19[var2];
         double var13 = BY.a(var7, var21);

         for (int var9 = 0; var9 < var4.length; var9++) {
            double var16 = var4[var9] * var13;
            this.b[var9] = this.b[var9] + var16;
            super.h[var9] = super.h[var9] + var21 * var16;
         }
      }

      for (int var20 = 0; var20 < super.e.length; var20++) {
         this.b[var20] = this.b[var20] + this.o[var20] * var7;
         super.g[var20] = super.e[var20] + this.b[var20];
         super.h[var20] = (super.h[var20] + this.o[var20] * var7) / var5;
      }
   }

   @Override
   public final void writeExternal(ObjectOutput out) {
      this.a(out);
      out.writeDouble(this.c);
      out.writeDouble(this.n);
      int var2 = super.e == null ? -1 : super.e.length;
      if (this.o == null) {
         out.writeBoolean(false);
      } else {
         out.writeBoolean(true);

         for (int var3 = 0; var3 < var2; var3++) {
            out.writeDouble(this.o[var3]);
         }
      }

      if (this.p == null) {
         out.writeBoolean(false);
      } else {
         out.writeBoolean(true);
         out.writeObject(this.p);
      }
   }

   @Override
   public final void readExternal(ObjectInput in) {
      double var2 = this.a(in);
      this.c = in.readDouble();
      this.n = in.readDouble();
      int var4 = super.e == null ? -1 : super.e.length;
      boolean var5;
      if (var5 = in.readBoolean()) {
         this.o = new double[var4];

         for (int var6 = 0; var6 < var4; var6++) {
            this.o[var6] = in.readDouble();
         }
      } else {
         this.o = null;
      }

      boolean var7;
      if (var7 = in.readBoolean()) {
         this.p = (sy)in.readObject();
      } else {
         this.p = null;
      }

      if (var5 && var7) {
         this.b = new double[var4];
         this.d(var2);
      } else {
         this.b = null;
      }
   }
}
