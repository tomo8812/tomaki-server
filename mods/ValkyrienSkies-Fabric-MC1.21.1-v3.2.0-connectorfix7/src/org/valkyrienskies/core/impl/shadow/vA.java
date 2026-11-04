package org.valkyrienskies.core.impl.shadow;

public final class vA extends wl {
   private static final double[] f = new double[]{0.5, 0.5, 1.0};
   private static final double[][] g = new double[][]{{0.5}, {0.0, 0.5}, {0.0, 0.0, 1.0}};
   private static final double[] h = new double[]{0.16666666666666666, 0.3333333333333333, 0.3333333333333333, 0.16666666666666666};

   private vA(double var1) {
      super("classical Runge-Kutta", f, g, h, new vB(), var1);
   }
}
