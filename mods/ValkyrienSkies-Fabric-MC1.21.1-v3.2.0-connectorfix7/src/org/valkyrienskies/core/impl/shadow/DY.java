package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;

public final class DY {
   final DT a;
   final DT b;

   public DY(DT var1, DT var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      super();
      this.a = var1;
      this.b = var2;
   }

   public final DT a() {
      return this.a;
   }

   public final DT b() {
      return this.b;
   }

   public final DT c() {
      return this.a;
   }

   public final DT d() {
      return this.b;
   }

   private static DY a(DT var0, DT var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      return new DY(var0, var1);
   }

   // $VF: synthetic method
   private static DY a(DY var0, DT var1, DT var2, int var3) {
      if ((var3 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.b;
      }

      DT var5 = var2;
      DT var4 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var5, "");
      return new DY(var4, var5);
   }

   @Override
   public final String toString() {
      return "DimensionBlockConnGraphs(solidGraph=" + this.a + ", airGraph=" + this.b + ")";
   }

   @Override
   public final int hashCode() {
      return this.a.hashCode() * 31 + this.b.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof DY var2)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.a, var2.a) ? false : Intrinsics.areEqual(this.b, var2.b);
      }
   }
}
