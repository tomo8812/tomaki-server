package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.world.LevelYRange;

public final class DP {
   public final String a;
   public final LevelYRange b;
   public Vector3dc c;

   public DP(String var1, LevelYRange var2, Vector3dc var3) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      super();
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   public final String a() {
      return this.a;
   }

   public final LevelYRange b() {
      return this.b;
   }

   public final Vector3dc c() {
      return this.c;
   }

   private void a(Vector3dc var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.c = var1;
   }

   private String d() {
      return this.a;
   }

   private LevelYRange e() {
      return this.b;
   }

   private Vector3dc f() {
      return this.c;
   }

   private static DP a(String var0, LevelYRange var1, Vector3dc var2) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      return new DP(var0, var1, var2);
   }

   // $VF: synthetic method
   private static DP a(DP var0, String var1, LevelYRange var2, Vector3dc var3, int var4) {
      if ((var4 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.b;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.c;
      }

      Vector3dc var7 = var3;
      LevelYRange var6 = var2;
      String var5 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var6, "");
      Intrinsics.checkNotNullParameter(var7, "");
      return new DP(var5, var6, var7);
   }

   @Override
   public final String toString() {
      return "DimensionInfo(id=" + this.a + ", yRange=" + this.b + ", gravity=" + this.c + ")";
   }

   @Override
   public final int hashCode() {
      return (this.a.hashCode() * 31 + this.b.hashCode()) * 31 + this.c.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof DP var2)) {
         return false;
      } else if (!Intrinsics.areEqual(this.a, var2.a)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.b, var2.b) ? false : Intrinsics.areEqual(this.c, var2.c);
      }
   }
}
