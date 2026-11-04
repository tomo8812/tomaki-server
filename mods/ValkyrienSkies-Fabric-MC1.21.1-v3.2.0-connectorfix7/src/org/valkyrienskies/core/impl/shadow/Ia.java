package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;

public final class Ia {
   private final Id a;
   private final Ib b;
   private final int c;

   public Ia(Id var1, Ib var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      super();
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   private Id a() {
      return this.a;
   }

   private Ib b() {
      return this.b;
   }

   private int c() {
      return this.c;
   }

   private Id d() {
      return this.a;
   }

   private Ib e() {
      return this.b;
   }

   private int f() {
      return this.c;
   }

   private static Ia a(Id var0, Ib var1, int var2) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      return new Ia(var0, var1, var2);
   }

   // $VF: synthetic method
   private static Ia a(Ia var0, Id var1, Ib var2, int var3, int var4) {
      if ((var4 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.b;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.c;
      }

      int var7 = var3;
      Ib var6 = var2;
      Id var5 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var6, "");
      return new Ia(var5, var6, var7);
   }

   @Override
   public final String toString() {
      return "Lod1BlockState(lod1SolidBlockState=" + this.a + ", lod1LiquidBlockState=" + this.b + ", lod1BlockStateId=" + this.c + ')';
   }

   @Override
   public final int hashCode() {
      return (this.a.hashCode() * 31 + this.b.hashCode()) * 31 + Integer.hashCode(this.c);
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Ia)) {
         return false;
      } else {
         other = (Ia)other;
         if (!Intrinsics.areEqual(this.a, ((Ia)other).a)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.b, ((Ia)other).b) ? false : this.c == ((Ia)other).c;
         }
      }
   }
}
