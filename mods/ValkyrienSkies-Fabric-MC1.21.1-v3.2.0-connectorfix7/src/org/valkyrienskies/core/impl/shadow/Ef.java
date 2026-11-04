package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3ic;
import org.valkyrienskies.core.internal.physics.blockstates.VsiBlockState;

public final class Ef {
   final Vector3ic a;
   final VsiBlockState b;
   final boolean c;

   public Ef(Vector3ic var1, VsiBlockState var2, boolean var3) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      super();
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   public final Vector3ic a() {
      return this.a;
   }

   public final VsiBlockState b() {
      return this.b;
   }

   public final boolean c() {
      return this.c;
   }

   @Override
   public final String toString() {
      return "AerodynamicBlockUpdateInfo(pos=" + this.a + ", shape=" + this.b + ", remove=" + this.c + ")";
   }

   private Vector3ic d() {
      return this.a;
   }

   private VsiBlockState e() {
      return this.b;
   }

   private boolean f() {
      return this.c;
   }

   private static Ef a(Vector3ic var0, VsiBlockState var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      return new Ef(var0, var1, var2);
   }

   // $VF: synthetic method
   private static Ef a(Ef var0, Vector3ic var1, VsiBlockState var2, boolean var3, int var4) {
      if ((var4 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.b;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.c;
      }

      boolean var7 = var3;
      VsiBlockState var6 = var2;
      Vector3ic var5 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var6, "");
      return new Ef(var5, var6, var7);
   }

   @Override
   public final int hashCode() {
      return (this.a.hashCode() * 31 + this.b.hashCode()) * 31 + Boolean.hashCode(this.c);
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Ef var2)) {
         return false;
      } else if (!Intrinsics.areEqual(this.a, var2.a)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.b, var2.b) ? false : this.c == var2.c;
      }
   }
}
