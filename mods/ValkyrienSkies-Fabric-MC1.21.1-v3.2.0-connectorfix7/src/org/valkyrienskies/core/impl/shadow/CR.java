package org.valkyrienskies.core.impl.shadow;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import java.util.Set;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.impl.api.ServerShipInternal;
import org.valkyrienskies.core.internal.world.VsiPlayer;

public final class CR {
   public final Map<VsiPlayer, Map<ServerShipInternal, LongSet>> a;
   private final Long2ObjectMap<Set<VsiPlayer>> f;
   public final Map<VsiPlayer, Set<ServerShipInternal>> b;
   public final Map<VsiPlayer, Set<ServerShipInternal>> c;
   private final Set<ServerShipInternal> g;
   public final Set<ServerShipInternal> d;
   public final Set<VsiPlayer> e;

   public CR(
      Map<VsiPlayer, ? extends Map<ServerShipInternal, ? extends LongSet>> var1,
      Long2ObjectMap<Set<VsiPlayer>> var2,
      Map<VsiPlayer, ? extends Set<? extends ServerShipInternal>> var3,
      Map<VsiPlayer, ? extends Set<? extends ServerShipInternal>> var4,
      Set<? extends ServerShipInternal> var5,
      Set<? extends ServerShipInternal> var6,
      Set<? extends VsiPlayer> var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var4, "");
      Intrinsics.checkNotNullParameter(var5, "");
      Intrinsics.checkNotNullParameter(var6, "");
      Intrinsics.checkNotNullParameter(var7, "");
      super();
      this.a = var1;
      this.f = var2;
      this.b = var3;
      this.c = var4;
      this.g = var5;
      this.d = var6;
      this.e = var7;
   }

   public final Map<VsiPlayer, Map<ServerShipInternal, LongSet>> a() {
      return this.a;
   }

   private Long2ObjectMap<Set<VsiPlayer>> f() {
      return this.f;
   }

   public final Map<VsiPlayer, Set<ServerShipInternal>> b() {
      return this.b;
   }

   public final Map<VsiPlayer, Set<ServerShipInternal>> c() {
      return this.c;
   }

   private Set<ServerShipInternal> g() {
      return this.g;
   }

   public final Set<ServerShipInternal> d() {
      return this.d;
   }

   public final Set<VsiPlayer> e() {
      return this.e;
   }

   public final Iterable<ServerShipInternal> a(VsiPlayer var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      Map var10000 = this.a.get(var1);
      if (var10000 == null) {
         var10000 = MapsKt.emptyMap();
      }

      return var10000.keySet();
   }

   private Map<VsiPlayer, Map<ServerShipInternal, LongSet>> h() {
      return this.a;
   }

   private Long2ObjectMap<Set<VsiPlayer>> i() {
      return this.f;
   }

   private Map<VsiPlayer, Set<ServerShipInternal>> j() {
      return this.b;
   }

   private Map<VsiPlayer, Set<ServerShipInternal>> k() {
      return this.c;
   }

   private Set<ServerShipInternal> l() {
      return this.g;
   }

   private Set<ServerShipInternal> m() {
      return this.d;
   }

   private Set<VsiPlayer> n() {
      return this.e;
   }

   private static CR a(
      Map<VsiPlayer, ? extends Map<ServerShipInternal, ? extends LongSet>> var0,
      Long2ObjectMap<Set<VsiPlayer>> var1,
      Map<VsiPlayer, ? extends Set<? extends ServerShipInternal>> var2,
      Map<VsiPlayer, ? extends Set<? extends ServerShipInternal>> var3,
      Set<? extends ServerShipInternal> var4,
      Set<? extends ServerShipInternal> var5,
      Set<? extends VsiPlayer> var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var4, "");
      Intrinsics.checkNotNullParameter(var5, "");
      Intrinsics.checkNotNullParameter(var6, "");
      return new CR(var0, var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   private static CR a(CR var0, Map var1, Long2ObjectMap var2, Map var3, Map var4, Set var5, Set var6, Set var7, int var8) {
      if ((var8 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var8 & 2) != 0) {
         var2 = var0.f;
      }

      if ((var8 & 4) != 0) {
         var3 = var0.b;
      }

      if ((var8 & 8) != 0) {
         var4 = var0.c;
      }

      if ((var8 & 16) != 0) {
         var5 = var0.g;
      }

      if ((var8 & 32) != 0) {
         var6 = var0.d;
      }

      if ((var8 & 64) != 0) {
         var7 = var0.e;
      }

      Set var15 = var7;
      Set var14 = var6;
      Set var13 = var5;
      Map var12 = var4;
      Map var11 = var3;
      Long2ObjectMap var10 = var2;
      Map var9 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var10, "");
      Intrinsics.checkNotNullParameter(var11, "");
      Intrinsics.checkNotNullParameter(var12, "");
      Intrinsics.checkNotNullParameter(var13, "");
      Intrinsics.checkNotNullParameter(var14, "");
      Intrinsics.checkNotNullParameter(var15, "");
      return new CR(var9, var10, var11, var12, var13, var14, var15);
   }

   @Override
   public final String toString() {
      return "ChunkTrackingInfo(playersToShipsWatchingMap="
         + this.a
         + ", shipsToPlayersWatchingMap="
         + this.f
         + ", playersToShipsNewlyWatchingMap="
         + this.b
         + ", playersToShipsNoLongerWatchingMap="
         + this.c
         + ", shipsToLoad="
         + this.g
         + ", shipsToUnload="
         + this.d
         + ", newPlayers="
         + this.e
         + ")";
   }

   @Override
   public final int hashCode() {
      return (
               ((((this.a.hashCode() * 31 + this.f.hashCode()) * 31 + this.b.hashCode()) * 31 + this.c.hashCode()) * 31 + this.g.hashCode()) * 31
                  + this.d.hashCode()
            )
            * 31
         + this.e.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof CR var2)) {
         return false;
      } else if (!Intrinsics.areEqual(this.a, var2.a)) {
         return false;
      } else if (!Intrinsics.areEqual(this.f, var2.f)) {
         return false;
      } else if (!Intrinsics.areEqual(this.b, var2.b)) {
         return false;
      } else if (!Intrinsics.areEqual(this.c, var2.c)) {
         return false;
      } else if (!Intrinsics.areEqual(this.g, var2.g)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.d, var2.d) ? false : Intrinsics.areEqual(this.e, var2.e);
      }
   }
}
