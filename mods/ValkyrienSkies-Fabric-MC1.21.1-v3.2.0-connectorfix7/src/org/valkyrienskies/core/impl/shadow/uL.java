package org.valkyrienskies.core.impl.shadow;

public final class uL implements uM {
   private final vb a;
   private final int b;
   private final double[] c;
   private final double[] d;
   private final double[] e;

   private uL(vb var1) {
      this.a = var1;
      this.b = var1.a();
      this.c = new double[this.b];
      this.d = new double[this.b];
      this.e = new double[this.b];
   }

   @Override
   public final int a() {
      return 2 * this.b;
   }

   @Override
   public final void a(double var1, double[] var3, double[] var4) {
      System.arraycopy(var3, 0, this.c, 0, this.b);
      System.arraycopy(var3, this.b, this.d, 0, this.b);
      System.arraycopy(this.d, 0, var4, 0, this.b);
      System.arraycopy(this.e, 0, var4, this.b, this.b);
   }
}
