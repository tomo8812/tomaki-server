package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

@Deprecated
public final class Af implements Serializable, Ac<Af> {
   private static final long a = 3946024775784901369L;
   private final int[] b;

   private Af(int[] var1) {
      this.b = var1;
   }

   private int[] a() {
      return this.b;
   }

   private double a(Af var1) {
      int[] var2 = var1.b;
      int[] var9 = this.b;
      int[] var4 = var2;
      int[] var3 = this.b;
      Ch.a(this.b, var4, true);
      double var11 = 0.0;

      for (int var10 = 0; var10 < var9.length; var10++) {
         double var7 = var9[var10] - var2[var10];
         var11 += var7 * var7;
      }

      return BY.a(var11);
   }

   private Af b(Collection<Af> var1) {
      int[] var2 = new int[this.b.length];

      for (Af var4 : var1) {
         for (int var5 = 0; var5 < var2.length; var5++) {
            var2[var5] += var4.b[var5];
         }
      }

      for (int var7 = 0; var7 < var2.length; var7++) {
         var2[var7] /= var1.size();
      }

      return new Af(var2);
   }

   @Override
   public final boolean equals(Object other) {
      return !(other instanceof Af) ? false : Arrays.equals(this.b, ((Af)other).b);
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
      int[] var7 = new int[this.b.length];

      for (Af var4 : var2) {
         for (int var5 = 0; var5 < var7.length; var5++) {
            var7[var5] += var4.b[var5];
         }
      }

      for (int var8 = 0; var8 < var7.length; var8++) {
         var7[var8] /= var2.size();
      }

      return new Af(var7);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final double a(Object var1) {
      Af var2 = (Af)var1;
      int[] var12 = var2.b;
      int[] var11 = this.b;
      int[] var4 = var12;
      int[] var3 = this.b;
      Ch.a(this.b, var4, true);
      double var6 = 0.0;

      for (int var13 = 0; var13 < var11.length; var13++) {
         double var9 = var11[var13] - var12[var13];
         var6 += var9 * var9;
      }

      return BY.a(var6);
   }
}
