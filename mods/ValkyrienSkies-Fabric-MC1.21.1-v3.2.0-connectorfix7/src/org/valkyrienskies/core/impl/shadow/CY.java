package org.valkyrienskies.core.impl.shadow;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.api.world.LevelYRange;
import org.valkyrienskies.core.impl.api.ServerShipInternal;
import org.valkyrienskies.core.impl.config.VSCoreConfig;
import org.valkyrienskies.core.internal.world.VsiPlayer;
import org.valkyrienskies.core.internal.world.chunks.VsiChunkUnwatchTask;
import org.valkyrienskies.core.internal.world.chunks.VsiChunkWatchTask;
import org.valkyrienskies.core.internal.world.chunks.VsiChunkWatchTasks;
import org.valkyrienskies.core.util.AABBdUtilKt;

public final class CY {
   private final VSCoreConfig.Server f;
   private final Map<String, DP> g;
   private final Long2ObjectMap<Set<VsiPlayer>> h;
   private final HashMap<VsiPlayer, Map<ServerShipInternal, LongSet>> i;
   final Long2ObjectOpenHashMap<Set<VsiPlayer>> a;
   final HashMap<VsiPlayer, Set<ServerShipInternal>> b;
   final HashMap<VsiPlayer, Set<ServerShipInternal>> c;
   final HashSet<ServerShipInternal> d;
   final HashSet<ServerShipInternal> e;
   private Set<? extends VsiPlayer> j;

   @Inject
   public CY(VSCoreConfig.Server var1, @Named("dimensionInfo") Map<String, DP> var2) {
      Intrinsics.checkNotNullParameter(var1, "")
      Intrinsics.checkNotNullParameter(var2, "")
      super()
      this.f = var1
      this.g = var2
      this.h = (Long2ObjectMap<Set<VsiPlayer>>)(new Long2ObjectOpenHashMap())
      this.i = new HashMap<>()
      this.a = new Long2ObjectOpenHashMap()
      this.b = new HashMap<>()
      this.c = new HashMap<>()
      this.d = new HashSet<>()
      this.e = new HashSet<>()
      this.j = SetsKt.emptySet()
   }

   private VSCoreConfig.Server a() {
      return this.f
   }

   private Map<String, DP> b() {
      return this.g
   }

   private final void a(Iterable<? extends ServerShipInternal> var1) {
      for (ServerShipInternal var2 : var1) {
         Collection var10000 = this.i.values()
         Intrinsics.checkNotNullExpressionValue(var10000, "")
         Iterator var3 = var10000.iterator()

         while (var3.hasNext()) {
            ((Map)var3.next()).remove(var2)
         }

         this.a.remove(var2.getId())
         this.e.add(var2)
         var2.getActiveChunksSet().forEach(CY::a)
      }
   }

   private final void c() {
      this.d.clear()
      this.e.clear()
      this.b.clear()
      this.c.clear()
   }

   public final VsiChunkWatchTasks a(
      Set<? extends VsiPlayer> var1, Set<? extends VsiPlayer> var2, Iterable<? extends ServerShipInternal> var3, Iterable<? extends ServerShipInternal> var4
   ) {
      Intrinsics.checkNotNullParameter(var1, "")
      Intrinsics.checkNotNullParameter(var2, "")
      Intrinsics.checkNotNullParameter(var3, "")
      Intrinsics.checkNotNullParameter(var4, "")
      CY var5 = this
      this.d.clear()
      var5.e.clear()
      var5.b.clear()
      var5.c.clear()
      SortedSet var6 = var4
      var5 = this

      for (ServerShipInternal var23 : var6) {
         Collection var10000 = var5.i.values()
         Intrinsics.checkNotNullExpressionValue(var10000, "")
         Iterator var9 = var10000.iterator()

         while (var9.hasNext()) {
            ((Map)var9.next()).remove(var23)
         }

         var5.a.remove(var23.getId())
         var5.e.add(var23)
         var23.getActiveChunksSet().forEach(CY::a)
      }

      var6 = SetsKt.minus(var2, var1)
      var5 = this
      if (!var6.isEmpty()) {
         Iterable var27
         for (VsiPlayer var33 : var27 = var6) {
            var5.i.remove(var33)
         }

         Map var28
         Iterator var31 = (var28 = (Map)var5.a).entrySet().iterator()

         while (var31.hasNext()) {
            ((Set)((Entry)var31.next()).getValue()).removeAll(var6)
         }

         Map var29
         var31 = (var29 = (Map)var5.h).entrySet().iterator()

         while (var31.hasNext()) {
            ((Set)((Entry)var31.next()).getValue()).removeAll(var6)
         }
      }

      this.j = SetsKt.minus(var1, var2)
      var2 = new TreeSet()
      TreeSet var21 = new TreeSet()
      double var30 = this.f.getShipLoadDistance()
      double var34 = this.f.getShipUnloadDistance()
      Vector3d var25 = new Vector3d()
      AABBd var11 = new AABBd()
      Iterator var17 = var3.iterator()

      while (var17.hasNext()) {
         ServerShipInternal var12
         ShipTransform var13 = (var12 = (ServerShipInternal)var17.next()).getTransform()
         AABBic var14 = var12.getShipAABB()
         LevelYRange var15 = ((DP)MapsKt.getValue(this.g, var12.getChunkClaimDimension())).b
         var12.getActiveChunksSet().forEach(CY::a)
      }

      Iterator var18 = var4.iterator()

      while (var18.hasNext()) {
         ServerShipInternal var35
         (var35 = (ServerShipInternal)var18.next()).getActiveChunksSet().forEach(CY::a)
      }

      SortedSet var36 = var2
      var6 = var21
      SortedSet var22 = var36
      Intrinsics.checkNotNullParameter(var36, "")
      Intrinsics.checkNotNullParameter(var6, "")
      return new CW(var22, var6)
   }

   public final CR a(Iterable<? extends VsiChunkWatchTask> var1, Iterable<? extends VsiChunkUnwatchTask> var2) {
      Intrinsics.checkNotNullParameter(var1, "")
      Intrinsics.checkNotNullParameter(var2, "")

      for (VsiChunkWatchTask var3 : var1) {
         ServerShipInternal var10001 = var3.getShip()
         Intrinsics.checkNotNull(var10001)
         var10001 = var10001
         Iterable var5 = var3.getPlayersNeedWatching()
         long var7 = var3.getChunkPos()
         ServerShipInternal var4 = var10001
         CY var11 = this
         Object var10000 = this.h.computeIfAbsent(var7, CY::a)
         Intrinsics.checkNotNullExpressionValue(var10000, "")
         CollectionsKt.addAll((Collection)var10000, var5)

         for (VsiPlayer var6 : var5) {
            var11.i.computeIfAbsent(var6, CY::a).compute(var4, CY::a)
         }
      }

      for (VsiChunkUnwatchTask var12 : var2) {
         ServerShipInternal var21 = var12.getShip()
         Intrinsics.checkNotNull(var21)
         var21 = var21
         Iterable var16 = var12.getPlayersNeedUnwatching()
         long var19 = var12.getChunkPos()
         ServerShipInternal var14 = var21
         CY var13 = this
         this.h.computeIfPresent(var19, CY::b)

         for (VsiPlayer var18 : var16) {
            var13.i.computeIfPresent(var18, CY::c)
         }
      }

      return new CR(this.i, (Long2ObjectMap<Set<VsiPlayer>>)this.a, this.b, this.c, this.d, this.e, this.j)
   }

   public final Set<VsiPlayer> a(int var1, int var2, String var3) {
      Intrinsics.checkNotNullParameter(var3, "")
      long var4 = IShipActiveChunksSet.Companion.chunkPosToLong(var1, var2)
      Set var10000 = (Set)this.h.get(var4)
      if (var10000 == null) {
         var10000 = SetsKt.emptySet()
      }

      return var10000
   }

   private final void a(ServerShipInternal var1, long var2, Iterable<? extends VsiPlayer> var4) {
      Object var10000 = this.h.computeIfAbsent(var2, CY::a)
      Intrinsics.checkNotNullExpressionValue(var10000, "")
      CollectionsKt.addAll((Collection)var10000, var4)

      for (VsiPlayer var5 : var4) {
         this.i.computeIfAbsent(var5, CY::a).compute(var1, CY::a)
      }
   }

   private final void b(ServerShipInternal var1, long var2, Iterable<? extends VsiPlayer> var4) {
      this.h.computeIfPresent(var2, CY::b)

      for (VsiPlayer var5 : var4) {
         this.i.computeIfPresent(var5, CY::c)
      }
   }

   private final void a(Set<? extends VsiPlayer> var1) {
      if (!var1.isEmpty()) {
         Iterable var2
         for (VsiPlayer var3 : var2 = var1) {
            this.i.remove(var3)
         }

         Map var5
         Iterator var6 = (var5 = (Map)this.a).entrySet().iterator()

         while (var6.hasNext()) {
            ((Set)((Entry)var6.next()).getValue()).removeAll(var1)
         }

         Map var7
         Iterator var8 = (var7 = (Map)this.h).entrySet().iterator()

         while (var8.hasNext()) {
            ((Set)((Entry)var8.next()).getValue()).removeAll(var1)
         }
      }
   }

   private static final void a(CY var0, int var1, int var2) {
      long var3 = IShipActiveChunksSet.Companion.chunkPosToLong(var1, var2)
      var0.h.remove(var3)
   }

   private static final void a(
      AABBd var0,
      AABBic var1,
      LevelYRange var2,
      ShipTransform var3,
      CY var4,
      ServerShipInternal var5,
      Set var6,
      Vector3d var7,
      double var8,
      double var10,
      TreeSet var12,
      TreeSet var13,
      int var14,
      int var15
   ) {
      var0 = AABBdUtilKt.set(
            var0,
            var14 << 4,
            var1 != null ? var1.minY() : var2.getMinY(),
            var15 << 4,
            (var14 << 4) + 16.0,
            (var1 != null ? var1.maxY() : var2.getMaxY()) + 1.0,
            (var15 << 4) + 16.0
         )
         .transform(var3.getShipToWorld())
         List var41 = new ArrayList()
      List var43 = new ArrayList()
      double var19 = Double.MAX_VALUE
      double var21 = Double.MAX_VALUE
      Set var44 = var4.a(var14, var15, var5.getChunkClaimDimension())
      long var24 = IShipActiveChunksSet.Companion.chunkPosToLong(var14, var15)
      Iterator var45 = var6.iterator()

      while (var45.hasNext()) {
         VsiPlayer var49
         Vector3dc var51 = (var49 = (VsiPlayer)var45.next()).getPosition(var7)
         Intrinsics.checkNotNullExpressionValue(var0, "")
         double var29 = AABBdUtilKt.signedDistanceTo(var0, var51)
         boolean var52 = var44.contains(var49)
         if (!Intrinsics.areEqual(var5.getChunkClaimDimension(), var49.getDimension())) {
            if (var52) {
               var43.add(var49)
            }
         } else {
            boolean var53 = var49.getForceWatchingShips().contains(var5.getId())
            if (!(var29 < var8) && !var53) {
               if (var29 > var10 && var52) {
                  var43.add(var49)
                  var21 = Math.min(var21, var29)
               }
            } else if (!var52) {
               var41.add(var49)
               var19 = Math.min(var19, var29)
            }
         }
      }

      if (!var41.isEmpty()) {
         String var10001 = var5.getChunkClaimDimension()
         Iterable var10002 = var41
         ServerShip var46 = var5
         double var37 = var19
         Iterable var42 = var10002
         String var40 = var10001
         long var33 = var24
         Intrinsics.checkNotNullParameter(var40, "")
         Intrinsics.checkNotNullParameter(var42, "")
         Intrinsics.checkNotNullParameter(var46, "")
         CU var47 = new CU(var33, var40, var42, var37, var46)
         var12.add(var47)
      }

      if (!var43.isEmpty()) {
         boolean var48 = var44.size() == var43.size()
         VsiChunkUnwatchTask var50 = CT.a(var24, var5.getChunkClaimDimension(), var43, var48, var21, var5)
         var13.add(var50)
      }
   }

   private static final void a(TreeSet var0, ServerShipInternal var1, Set var2, int var3, int var4) {
      var0.add(CT.a(IShipActiveChunksSet.Companion.chunkPosToLong(var3, var4), var1.getChunkClaimDimension(), var2, true, Double.POSITIVE_INFINITY, var1))
   }

   private static final Set a(long var0) {
      return new HashSet()
   }

   private static final Map a(Function1 var0, Object var1) {
      return (Map<ServerShipInternal, LongSet>)var0.invoke(var1)
   }

   private static final LongSet a(Function2 var0, Object var1, Object var2) {
      return (LongSet)var0.invoke(var1, var2)
   }

   private static final Set b(Function2 var0, Object var1, Object var2) {
      return (Set)var0.invoke(var1, var2)
   }

   private static final Map c(Function2 var0, Object var1, Object var2) {
      return (Map<ServerShipInternal, LongSet>)var0.invoke(var1, var2)
   }

   // $VF: synthetic method
   public static final HashMap a(CY var0) {
      return var0.b
   }

   // $VF: synthetic method
   public static final Long2ObjectOpenHashMap b(CY var0) {
      return var0.a
   }

   // $VF: synthetic method
   public static final HashSet c(CY var0) {
      return var0.d
   }

   // $VF: synthetic method
   public static final HashMap d(CY var0) {
      return var0.c
   }

   // $VF: synthetic method
   public static final HashSet e(CY var0) {
      return var0.e
   }
}
