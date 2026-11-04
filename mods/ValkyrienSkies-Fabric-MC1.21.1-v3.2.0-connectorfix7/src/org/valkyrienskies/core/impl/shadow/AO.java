package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Arrays;

public final class AO implements Serializable {
   private static final long a = 8223009086481006892L;
   private final AG[] b;

   private AO(int var1) {
      this.b = new AG[var1];

      for (int var2 = 0; var2 < var1; var2++) {
         this.b[var2] = new AG();
      }
   }

   private void a(double[] var1) {
      if (var1.length != this.b.length) {
         throw new oS(var1.length, this.b.length);
      }

      for (int var2 = 0; var2 < var1.length; var2++) {
         this.b[var2].a(var1[var2]);
      }
   }

   private double[] a() {
      double[] var1 = new double[this.b.length];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = this.b[var2].c();
      }

      return var1;
   }

   private long b() {
      return this.b.length == 0 ? 0L : this.b[0].g_();
   }

   @Override
   public final int hashCode() {
      return 31 + Arrays.hashCode(this.b);
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof AO)) {
         return false;
      }

      obj = (AO)obj;
      return Arrays.equals(this.b, ((AO)obj).b);
   }
}
