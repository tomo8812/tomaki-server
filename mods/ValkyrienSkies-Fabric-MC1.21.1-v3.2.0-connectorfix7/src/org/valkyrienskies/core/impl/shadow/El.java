package org.valkyrienskies.core.impl.shadow;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.internal.joints.VSJointAndId;

public final class El {
   public final int a;
   public final List<VSJointAndId> b;

   public El(int var1, List<VSJointAndId> var2) {
      Intrinsics.checkNotNullParameter(var2, "");
      super();
      this.a = var1;
      this.b = var2;
   }

   public final int a() {
      return this.a;
   }

   public final List<VSJointAndId> b() {
      return this.b;
   }

   private int c() {
      return this.a;
   }

   private List<VSJointAndId> d() {
      return this.b;
   }

   private static El a(int var0, List<VSJointAndId> var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      return new El(var0, var1);
   }

   // $VF: synthetic method
   private static El a(El var0, int var1, List var2, int var3) {
      if ((var3 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.b;
      }

      List var5 = var2;
      int var4 = var1;
      Intrinsics.checkNotNullParameter(var5, "");
      return new El(var4, var5);
   }

   @Override
   public final String toString() {
      return "PersistedJointStoreSnapshot(nextJointId=" + this.a + ", joints=" + this.b + ")";
   }

   @Override
   public final int hashCode() {
      return Integer.hashCode(this.a) * 31 + this.b.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof El var2)) {
         return false;
      } else {
         return this.a != var2.a ? false : Intrinsics.areEqual(this.b, var2.b);
      }
   }
}
