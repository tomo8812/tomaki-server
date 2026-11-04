package org.valkyrienskies.core.impl.shadow;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Map;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.internal.world.VsiPlayer;

public final class Fx {
   public final Map<VsiPlayer, Set<Long>> a;
   private final Long2ObjectOpenHashMap<Set<VsiPlayer>> d;
   public final Map<VsiPlayer, Set<Long>> b;
   public final Map<VsiPlayer, Set<Long>> c;

   public Fx(
      Map<VsiPlayer, ? extends Set<Long>> var1,
      Long2ObjectOpenHashMap<Set<VsiPlayer>> var2,
      Map<VsiPlayer, ? extends Set<Long>> var3,
      Map<VsiPlayer, ? extends Set<Long>> var4
   ) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var4, "");
      super();
      this.a = var1;
      this.d = var2;
      this.b = var3;
      this.c = var4;
   }

   public final Map<VsiPlayer, Set<Long>> a() {
      return this.a;
   }

   private Long2ObjectOpenHashMap<Set<VsiPlayer>> d() {
      return this.d;
   }

   public final Map<VsiPlayer, Set<Long>> b() {
      return this.b;
   }

   public final Map<VsiPlayer, Set<Long>> c() {
      return this.c;
   }

   private Map<VsiPlayer, Set<Long>> e() {
      return this.a;
   }

   private Long2ObjectOpenHashMap<Set<VsiPlayer>> f() {
      return this.d;
   }

   private Map<VsiPlayer, Set<Long>> g() {
      return this.b;
   }

   private Map<VsiPlayer, Set<Long>> h() {
      return this.c;
   }

   private static Fx a(
      Map<VsiPlayer, ? extends Set<Long>> var0,
      Long2ObjectOpenHashMap<Set<VsiPlayer>> var1,
      Map<VsiPlayer, ? extends Set<Long>> var2,
      Map<VsiPlayer, ? extends Set<Long>> var3
   ) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      return new Fx(var0, var1, var2, var3);
   }

   // $VF: synthetic method
   private static Fx a(Fx var0, Map var1, Long2ObjectOpenHashMap var2, Map var3, Map var4, int var5) {
      if ((var5 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.d;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.b;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.c;
      }

      Map var9 = var4;
      Map var8 = var3;
      Long2ObjectOpenHashMap var7 = var2;
      Map var6 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var7, "");
      Intrinsics.checkNotNullParameter(var8, "");
      Intrinsics.checkNotNullParameter(var9, "");
      return new Fx(var6, var7, var8, var9);
   }

   @Override
   public final String toString() {
      return "PhysEntityTrackingInfo(playersToPhysEntitiesWatchingMap="
         + this.a
         + ", physEntitiesToPlayersWatchingMap="
         + this.d
         + ", playersToPhysEntitiesNewlyWatchingMap="
         + this.b
         + ", playersToPhysEntitiesNoLongerWatchingMap="
         + this.c
         + ")";
   }

   @Override
   public final int hashCode() {
      return ((this.a.hashCode() * 31 + this.d.hashCode()) * 31 + this.b.hashCode()) * 31 + this.c.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Fx var2)) {
         return false;
      } else if (!Intrinsics.areEqual(this.a, var2.a)) {
         return false;
      } else if (!Intrinsics.areEqual(this.d, var2.d)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.b, var2.b) ? false : Intrinsics.areEqual(this.c, var2.c);
      }
   }
}
