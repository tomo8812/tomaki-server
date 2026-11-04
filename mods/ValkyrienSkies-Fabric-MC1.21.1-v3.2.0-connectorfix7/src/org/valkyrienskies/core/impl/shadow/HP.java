package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;

public final class HP {
   public final float a;
   public final float b;

   public HP(float var1, float var2) {
      this.a = var1;
      this.b = var2;
   }

   public final float a() {
      return this.a;
   }

   public final float b() {
      return this.b;
   }

   private float c() {
      return this.a;
   }

   private float d() {
      return this.b;
   }

   private static HP a(float var0, float var1) {
      return new HP(var0, var1);
   }

   // $VF: synthetic method
   private static HP a(HP var0, float var1, float var2, int var3) {
      if ((var3 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.b;
      }

      float var5 = var2;
      float var4 = var1;
      return new HP(var4, var5);
   }

   @Override
   public final String toString() {
      return "MaxForceTorque(maxForce=" + this.a + ", maxTorque=" + this.b + ')';
   }

   @Override
   public final int hashCode() {
      return Float.hashCode(this.a) * 31 + Float.hashCode(this.b);
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof HP)) {
         return false;
      }

      other = (HP)other;
      return !Intrinsics.areEqual(this.a, ((HP)other).a) ? false : Intrinsics.areEqual(this.b, ((HP)other).b);
   }
}
