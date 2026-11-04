package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

@Deprecated
public final class Ae implements Serializable, Ac<Ae> {
   private static final long a = 8026472786091227632L;
   private final double[] b;

   private Ae(double[] var1) {
      this.b = var1;
   }

   private Ae b(Collection<Ae> var1) {
      double[] var2 = new double[this.b.length];

      for (Ae var4 : var1) {
         for (int var5 = 0; var5 < var2.length; var5++) {
            var2[var5] += var4.b[var5];
         }
      }

      for (int var7 = 0; var7 < var2.length; var7++) {
         var2[var7] /= var1.size();
      }

      return new Ae(var2);
   }

   private double a(Ae var1) {
      return Ch.d(this.b, var1.b);
   }

   @Override
   public final boolean equals(Object other) {
      return !(other instanceof Ae) ? false : Arrays.equals(this.b, ((Ae)other).b);
   }

   private double[] a() {
      return this.b;
   }

   @Override
   public final int hashCode() {
      return Arrays.hashCode(this.b);
   }

   @Override
   public final String toString() {
      return Arrays.toString(this.b);
   }

   // $VF: synthetic method
   @Override
   public final Object a(Collection var1) {
      Collection var2 = var1;
      double[] var7 = new double[this.b.length];

      for (Ae var4 : var2) {
         for (int var5 = 0; var5 < var7.length; var5++) {
            var7[var5] += var4.b[var5];
         }
      }

      for (int var8 = 0; var8 < var7.length; var8++) {
         var7[var8] /= var2.size();
      }

      return new Ae(var7);
   }

   // $VF: synthetic method
   @Override
   public final double a(Object var1) {
      Ae var2 = (Ae)var1;
      return Ch.d(this.b, var2.b);
   }
}
