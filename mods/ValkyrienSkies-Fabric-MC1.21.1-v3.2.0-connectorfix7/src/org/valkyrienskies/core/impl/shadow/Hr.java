package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3dc;

public final class Hr implements GT {
   private Vector3dc a;

   public Hr(Vector3dc var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.a = var1;
   }

   @Override
   public final Vector3dc a() {
      return this.a;
   }

   @Override
   public final void a(Vector3dc var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.a = var1;
   }

   @Override
   public final void close() {
   }

   private Vector3dc b() {
      return this.a;
   }

   private static Hr b(Vector3dc var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      return new Hr(var0);
   }

   // $VF: synthetic method
   private static Hr a(Hr var0, Vector3dc var1, int var2) {
      if ((var2 & 1) != 0) {
         var1 = var0.a;
      }

      Vector3dc var3 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      return new Hr(var3);
   }

   @Override
   public final String toString() {
      return "DummyBoxShapeReference(lengths=" + this.a + ')';
   }

   @Override
   public final int hashCode() {
      return this.a.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof Hr)) {
         return false;
      }

      other = (Hr)other;
      return Intrinsics.areEqual(this.a, ((Hr)other).a);
   }
}
