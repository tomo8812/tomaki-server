package org.valkyrienskies.core.impl.shadow;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

public final class FL {
   final Long2ObjectMap<? extends FE> a;
   private final Long2ObjectMap<? extends List<Ip>> c;
   final int b;

   public FL(Long2ObjectMap<? extends FE> var1, Long2ObjectMap<? extends List<? extends Ip>> var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      super();
      this.a = var1;
      this.c = var2;
      this.b = var3;
   }

   public final Long2ObjectMap<? extends FE> a() {
      return this.a;
   }

   private Long2ObjectMap<? extends List<Ip>> c() {
      return this.c;
   }

   public final int b() {
      return this.b;
   }

   private Long2ObjectMap<? extends FE> d() {
      return this.a;
   }

   private Long2ObjectMap<? extends List<Ip>> e() {
      return this.c;
   }

   private int f() {
      return this.b;
   }

   private static FL a(Long2ObjectMap<? extends FE> var0, Long2ObjectMap<? extends List<? extends Ip>> var1, int var2) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      return new FL(var0, var1, var2);
   }

   // $VF: synthetic method
   private static FL a(FL var0, Long2ObjectMap var1, Long2ObjectMap var2, int var3, int var4) {
      if ((var4 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.c;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.b;
      }

      int var7 = var3;
      Long2ObjectMap var6 = var2;
      Long2ObjectMap var5 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var6, "");
      return new FL(var5, var6, var7);
   }

   @Override
   public final String toString() {
      return "VSPhysicsFrame(shipDataMap=" + this.a + ", voxelUpdatesMap=" + this.c + ", physTickNumber=" + this.b + ")";
   }

   @Override
   public final int hashCode() {
      return (this.a.hashCode() * 31 + this.c.hashCode()) * 31 + Integer.hashCode(this.b);
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof FL var2)) {
         return false;
      } else if (!Intrinsics.areEqual(this.a, var2.a)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.c, var2.c) ? false : this.b == var2.b;
      }
   }
}
