package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class wW implements Serializable {
   private static final long d = -764632794033034092L;
   final transient tu a;
   final xd b;
   final double c;

   private wW(double[] var1, xd var2, double var3) {
      this(new sA(var1), var2, var3);
   }

   public wW(tu var1, xd var2, double var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   private wW(double[] var1, double var2, xd var4, double[] var5, double var6) {
      double[] var8 = new double[var1.length];

      for (int var9 = 0; var9 < var8.length; var9++) {
         var8[var9] = var1[var9] - var5[var9];
      }

      this.a = new sA(var8, false);
      this.b = var4;
      this.c = var6 - var2;
   }

   private wW(tu var1, double var2, xd var4, tu var5, double var6) {
      this.a = var1.k(var5);
      this.b = var4;
      this.c = var6 - var2;
   }

   public final tu a() {
      return this.a;
   }

   public final xd b() {
      return this.b;
   }

   public final double c() {
      return this.c;
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof wW) {
         other = (wW)other;
         return this.b == ((wW)other).b && this.c == ((wW)other).c && this.a.equals(((wW)other).a);
      } else {
         return false;
      }
   }

   @Override
   public final int hashCode() {
      return this.b.hashCode() ^ Double.valueOf(this.c).hashCode() ^ this.a.hashCode();
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      td.a(this.a, var1);
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      td.a(this, "coefficients", var1);
   }
}
