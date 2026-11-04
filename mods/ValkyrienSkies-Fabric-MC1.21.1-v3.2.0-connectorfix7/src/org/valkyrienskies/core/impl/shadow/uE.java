package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class uE implements Serializable {
   private static final long c = 20110925L;
   final int a;
   public final int b;

   public uE(int var1, int var2) {
      this.a = var1;
      this.b = var2;
   }

   public final int a() {
      return this.a;
   }

   public final int b() {
      return this.b;
   }

   public final void a(double[] var1, double[] var2) {
      if (var2.length != this.b) {
         throw new oS(var2.length, this.b);
      }

      System.arraycopy(var1, this.a, var2, 0, this.b);
   }

   public final void b(double[] var1, double[] var2) {
      if (var1.length != this.b) {
         throw new oS(var1.length, this.b);
      }

      System.arraycopy(var1, 0, var2, this.a, this.b);
   }
}
