package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInput;
import java.io.ObjectOutput;

abstract class wm extends ws {
   protected double[] a;
   protected double[][] b;
   protected uA c;

   protected wm() {
      this.a = null;
      this.b = null;
      this.c = null;
   }

   wm(wm var1) {
      super(var1);
      if (var1.e != null) {
         this.a = (double[])var1.a.clone();
         this.b = new double[var1.b.length][];

         for (int var2 = 0; var2 < var1.b.length; var2++) {
            this.b[var2] = (double[])var1.b[var2].clone();
         }
      } else {
         this.a = null;
         this.b = null;
      }

      this.c = null;
   }

   public void a(uA var1, double[] var2, double[][] var3, boolean var4, uE var5, uE[] var6) {
      this.a(var2, var4, var5, var6);
      this.a = null;
      this.b = var3;
      this.c = var1;
   }

   @Override
   public final void c() {
      this.a = (double[])super.e.clone();
      super.c();
   }

   @Override
   public void writeExternal(ObjectOutput out) {
      this.a(out);
      int var2 = super.e == null ? -1 : super.e.length;

      for (int var3 = 0; var3 < var2; var3++) {
         out.writeDouble(this.a[var3]);
      }

      int var6 = this.b == null ? -1 : this.b.length;
      out.writeInt(var6);

      for (int var4 = 0; var4 < var6; var4++) {
         for (int var5 = 0; var5 < var2; var5++) {
            out.writeDouble(this.b[var4][var5]);
         }
      }
   }

   @Override
   public void readExternal(ObjectInput in) {
      double var2 = this.a(in);
      int var4;
      if ((var4 = super.e == null ? -1 : super.e.length) < 0) {
         this.a = null;
      } else {
         this.a = new double[var4];

         for (int var5 = 0; var5 < var4; var5++) {
            this.a[var5] = in.readDouble();
         }
      }

      int var8 = in.readInt();
      this.b = var8 < 0 ? null : new double[var8][];

      for (int var6 = 0; var6 < var8; var6++) {
         this.b[var6] = var4 < 0 ? null : new double[var4];

         for (int var7 = 0; var7 < var4; var7++) {
            this.b[var6][var7] = in.readDouble();
         }
      }

      this.c = null;
      if (super.e != null) {
         this.d(var2);
      } else {
         super.f = var2;
      }
   }
}
