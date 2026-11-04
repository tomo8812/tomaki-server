package org.valkyrienskies.core.impl.shadow;

public abstract class od implements oC {
   protected final zF a;
   private final int b;

   protected od(zF var1, int var2) {
      this.a = var1;
      this.b = var2;
   }

   @Override
   public void a(long var1) {
      this.a.setSeed(var1);
   }

   @Override
   public final int a() {
      return this.b;
   }

   @Override
   public abstract double[] b();

   @Override
   public final double[][] a(int var1) {
      if (var1 <= 0) {
         throw new pk(px.NUMBER_OF_SAMPLES, var1);
      }

      double[][] var2 = new double[var1][this.b];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = this.b();
      }

      return var2;
   }
}
