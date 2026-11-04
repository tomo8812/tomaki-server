package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3fc;

public final class HX {
   public final Vector3fc a;
   public final float b;

   public HX(Vector3fc var1, float var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.a = var1;
      this.b = var2;
   }

   public final Vector3fc a() {
      return this.a;
   }

   public final float b() {
      return this.b;
   }

   private Vector3fc c() {
      return this.a;
   }

   private float d() {
      return this.b;
   }

   private static HX a(Vector3fc var0, float var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      return new HX(var0, var1);
   }

   // $VF: synthetic method
   private static HX a(HX var0, Vector3fc var1, float var2, int var3) {
      if ((var3 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.b;
      }

      float var5 = var2;
      Vector3fc var4 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      return new HX(var4, var5);
   }

   @Override
   public final String toString() {
      return "CollisionPoint(pos=" + this.a + ", radius=" + this.b + ')';
   }

   @Override
   public final int hashCode() {
      return this.a.hashCode() * 31 + Float.hashCode(this.b);
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof HX)) {
         return false;
      }

      other = (HX)other;
      return !Intrinsics.areEqual(this.a, ((HX)other).a) ? false : Intrinsics.areEqual(this.b, ((HX)other).b);
   }
}
