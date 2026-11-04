package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.impl.game.ships.ShipData;

public final class EA {
   private final Collection<ShipData> a;
   private final Collection<ShipData> b;

   private EA(Collection<ShipData> var1, Collection<ShipData> var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      super();
      this.a = var1;
      this.b = var2;
   }

   private Collection<ShipData> a() {
      return this.a;
   }

   private Collection<ShipData> b() {
      return this.b;
   }

   private Collection<ShipData> c() {
      return this.a;
   }

   private Collection<ShipData> d() {
      return this.b;
   }

   private static EA a(Collection<ShipData> var0, Collection<ShipData> var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      return new EA(var0, var1);
   }

   // $VF: synthetic method
   private static EA a(EA var0, Collection var1, Collection var2, int var3) {
      if ((var3 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.b;
      }

      Collection var5 = var2;
      Collection var4 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var5, "");
      return new EA(var4, var5);
   }

   @Override
   public final String toString() {
      return "ShipLoadInfo(shipsToLoad=" + this.a + ", shipsToUnload=" + this.b + ")";
   }

   @Override
   public final int hashCode() {
      return this.a.hashCode() * 31 + this.b.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof EA var2)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.a, var2.a) ? false : Intrinsics.areEqual(this.b, var2.b);
      }
   }
}
