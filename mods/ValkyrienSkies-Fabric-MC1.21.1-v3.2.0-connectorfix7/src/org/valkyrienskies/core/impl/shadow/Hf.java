package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.joml.Quaterniondc;
import org.joml.Vector3dc;

public final class Hf {
   public final Vector3dc a;
   public final Quaterniondc b;

   public Hf(Vector3dc var1, Quaterniondc var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      super();
      this.a = var1;
      this.b = var2;
   }

   public final Vector3dc a() {
      return this.a;
   }

   public final Quaterniondc b() {
      return this.b;
   }

   private Vector3dc c() {
      return this.a;
   }

   private Quaterniondc d() {
      return this.b;
   }

   private static Hf a(Vector3dc var0, Quaterniondc var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      return new Hf(var0, var1);
   }

   // $VF: synthetic method
   private static Hf a(Hf var0, Vector3dc var1, Quaterniondc var2, int var3) {
      if ((var3 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.b;
      }

      Quaterniondc var5 = var2;
      Vector3dc var4 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var5, "");
      return new Hf(var4, var5);
   }

   @Override
   public final String toString() {
      return "Pose(pos=" + this.a + ", rot=" + this.b + ')';
   }

   @Override
   public final int hashCode() {
      return this.a.hashCode() * 31 + this.b.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof Hf)) {
         return false;
      }

      other = (Hf)other;
      return !Intrinsics.areEqual(this.a, ((Hf)other).a) ? false : Intrinsics.areEqual(this.b, ((Hf)other).b);
   }
}
