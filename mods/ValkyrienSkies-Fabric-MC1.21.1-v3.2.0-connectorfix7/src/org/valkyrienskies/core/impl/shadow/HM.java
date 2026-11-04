package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;

public final class HM {
   public final int a;
   public final HN b;

   public HM(int var1, HN var2) {
      Intrinsics.checkNotNullParameter(var2, "");
      super();
      this.a = var1;
      this.b = var2;
   }

   public final int a() {
      return this.a;
   }

   public final HN b() {
      return this.b;
   }

   private int c() {
      return this.a;
   }

   private HN d() {
      return this.b;
   }

   private static HM a(int var0, HN var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      return new HM(var0, var1);
   }

   // $VF: synthetic method
   private static HM a(HM var0, int var1, HN var2, int var3) {
      if ((var3 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.b;
      }

      HN var5 = var2;
      int var4 = var1;
      Intrinsics.checkNotNullParameter(var5, "");
      return new HM(var4, var5);
   }

   @Override
   public final String toString() {
      return "JointAndId(jointId=" + this.a + ", jointData=" + this.b + ')';
   }

   @Override
   public final int hashCode() {
      return Integer.hashCode(this.a) * 31 + this.b.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof HM)) {
         return false;
      }

      other = (HM)other;
      return this.a != ((HM)other).a ? false : Intrinsics.areEqual(this.b, ((HM)other).b);
   }
}
