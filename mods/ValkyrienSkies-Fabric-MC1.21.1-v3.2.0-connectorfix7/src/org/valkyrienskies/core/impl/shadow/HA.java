package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;

public final class HA implements Hk {
   private double a;

   public HA(double var1) {
      this.a = var1;
   }

   @Override
   public final double a() {
      return this.a;
   }

   @Override
   public final void a(double var1) {
      this.a = var1;
   }

   @Override
   public final void close() {
   }

   private double b() {
      return this.a;
   }

   private static HA b(double var0) {
      return new HA(var0);
   }

   // $VF: synthetic method
   private static HA a(HA var0, double var1, int var3) {
      if ((var3 & 1) != 0) {
         var1 = var0.a;
      }

      double var4 = var1;
      return new HA(var4);
   }

   @Override
   public final String toString() {
      return "DummySphereShapeReference(radius=" + this.a + ')';
   }

   @Override
   public final int hashCode() {
      return Double.hashCode(this.a);
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof HA)) {
         return false;
      }

      other = (HA)other;
      return Intrinsics.areEqual(this.a, ((HA)other).a);
   }
}
