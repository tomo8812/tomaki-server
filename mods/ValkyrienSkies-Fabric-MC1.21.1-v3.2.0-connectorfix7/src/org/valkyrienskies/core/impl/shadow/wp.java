package org.valkyrienskies.core.impl.shadow;

public final class wp extends wl {
   private static final double[] f = new double[]{0.3333333333333333, 0.6666666666666666, 1.0};
   private static final double[][] g = new double[][]{{0.3333333333333333}, {-0.3333333333333333, 1.0}, {1.0, -1.0, 1.0}};
   private static final double[] h = new double[]{0.125, 0.375, 0.375, 0.125};

   private wp(double var1) {
      super("3/8", f, g, h, new wq(), var1);
   }
}
