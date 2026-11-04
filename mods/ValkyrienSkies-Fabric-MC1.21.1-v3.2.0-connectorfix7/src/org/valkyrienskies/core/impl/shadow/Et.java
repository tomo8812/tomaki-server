package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap.Builder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.DoubleUnaryOperator;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.NotImplementedError;
import kotlin.Pair;
import kotlin.ReplaceWith;
import kotlin.Triple;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference0;
import kotlin.jvm.internal.PropertyReference0Impl;
import kotlin.jvm.internal.PropertyReference1;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import kotlin.reflect.KProperty0;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.bodies.properties.BodyTransform;
import org.valkyrienskies.core.api.event.SingleEvent;
import org.valkyrienskies.core.api.events.MergeEvent;
import org.valkyrienskies.core.api.events.SplitEvent;
import org.valkyrienskies.core.api.ships.DragController;
import org.valkyrienskies.core.api.ships.QueryableShipData;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ChunkClaim;
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet;
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.api.util.AerodynamicUtils;
import org.valkyrienskies.core.api.world.LevelYRange;
import org.valkyrienskies.core.api.world.connectivity.ConnectionStatus;
import org.valkyrienskies.core.api.world.connectivity.DoubleAugmentation;
import org.valkyrienskies.core.api.world.connectivity.DoubleComponentAugmentation;
import org.valkyrienskies.core.api.world.connectivity.SparseVoxelPosition;
import org.valkyrienskies.core.impl.api.ServerShipInternal;
import org.valkyrienskies.core.impl.api.ShipInternal;
import org.valkyrienskies.core.impl.config.VSCoreConfig;
import org.valkyrienskies.core.impl.game.ChunkAllocator;
import org.valkyrienskies.core.impl.game.ChunkAllocatorProvider;
import org.valkyrienskies.core.impl.game.ships.ShipData;
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon;
import org.valkyrienskies.core.impl.game.ships.ShipInertiaDataImpl;
import org.valkyrienskies.core.impl.hooks.VSEvents;
import org.valkyrienskies.core.impl.networking.impl.PacketPhysEntityCreate;
import org.valkyrienskies.core.impl.networking.impl.PacketPhysEntityRemove;
import org.valkyrienskies.core.impl.networking.impl.PacketShipDataCreate;
import org.valkyrienskies.core.impl.networking.impl.PacketShipRemove;
import org.valkyrienskies.core.impl.networking.impl.PhysEntityCreateData;
import org.valkyrienskies.core.impl.program.VSCoreInternal;
import org.valkyrienskies.core.impl.util.serialization.VSJacksonUtil;
import org.valkyrienskies.core.internal.ShipTeleportData;
import org.valkyrienskies.core.internal.VsiGameServer;
import org.valkyrienskies.core.internal.joints.VSJointAndId;
import org.valkyrienskies.core.internal.physics.PhysicsEntityData;
import org.valkyrienskies.core.internal.physics.PhysicsEntityServer;
import org.valkyrienskies.core.internal.physics.blockstates.VsiBlockState;
import org.valkyrienskies.core.internal.ships.VsiMutableQueryableShipData;
import org.valkyrienskies.core.internal.ships.VsiQueryableShipData;
import org.valkyrienskies.core.internal.world.VsiPlayer;
import org.valkyrienskies.core.internal.world.VsiServerShipWorld;
import org.valkyrienskies.core.internal.world.chunks.VsiBlockType;
import org.valkyrienskies.core.internal.world.chunks.VsiChunkUnwatchTask;
import org.valkyrienskies.core.internal.world.chunks.VsiChunkWatchTask;
import org.valkyrienskies.core.internal.world.chunks.VsiChunkWatchTasks;
import org.valkyrienskies.core.internal.world.chunks.VsiTerrainUpdate;
import org.valkyrienskies.core.util.VSCoreUtilKt;
import org.valkyrienskies.core.util.datastructures.BlockPos2ObjectOpenHashMap;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;

public final class Et extends Ev<Es> implements VsiServerShipWorld {
   // $VF: synthetic field
   private static KProperty<Object>[] z;
   public final VsiMutableQueryableShipData<ShipData> a;
   public final Ek b;
   public final ChunkAllocatorProvider c;
   public final EB d;
   private final DN A;
   public final Map<String, DP> e;
   private final DW B;
   private final ShipData.Factory C;
   private final Provider D;
   public final Gi<Et.c> f;
   private Set<? extends VsiPlayer> E;
   private Set<? extends VsiPlayer> F;
   private VsiGameServer G;
   private long H;
   private final Eo<Es> I;
   public final Map<Long, PhysicsEntityServer> g;
   public final Map<String, Long> h;
   public final ArrayList<String> i;
   public final ArrayList<String> j;
   public final ArrayList<Et.b> k;
   private final Set<FB> J;
   public final List<Es> l;
   public final List<PhysicsEntityServer> m;
   public final List<Es> n;
   public final List<PhysicsEntityServer> o;
   public final List<ShipData> p;
   public final List<Long> q;
   public List<ShipData> r;
   public List<VSJointAndId> s;
   public Map<Integer, VSJointAndId> t;
   public List<Integer> u;
   public List<FB> v;
   public List<FB> w;
   private final Fj K;
   public final Long2ObjectMap<BlockPos2ObjectOpenHashMap<Ip>> x;
   public final DL y;

   @Inject
   public Et(
      VsiMutableQueryableShipData<ShipData> var1,
      Ek var2,
      ChunkAllocatorProvider var3,
      EB var4,
      Fl var5,
      DN var6,
      @Named("mutableDimensionInfo") Map<String, DP> var7,
      DW var8,
      ShipData.Factory var9,
      Provider<VSCoreInternal> var10
   ) {
      Intrinsics.checkNotNullParameter(var1, "")
      Intrinsics.checkNotNullParameter(var2, "")
      Intrinsics.checkNotNullParameter(var3, "")
      Intrinsics.checkNotNullParameter(var4, "")
      Intrinsics.checkNotNullParameter(var5, "")
      Intrinsics.checkNotNullParameter(var6, "")
      Intrinsics.checkNotNullParameter(var7, "")
      Intrinsics.checkNotNullParameter(var8, "")
      Intrinsics.checkNotNullParameter(var9, "")
      Intrinsics.checkNotNullParameter(var10, "")
      super(var3, var6)
      this.a = var1
      this.b = var2
      this.c = var3
      this.d = var4
      this.A = var6
      this.e = var7
      this.B = var8
      this.C = var9
      this.D = var10
      this.f = Gl.a(Et.c.PRE_TICK, <unrepresentable>.a)
      this.E = SetsKt.emptySet()
      this.F = SetsKt.emptySet()
      this.I = new Eo<>(null, 1, null)
      this.g = new HashMap<>()
      this.h = new HashMap<>()
      this.i = new ArrayList<>()
      this.j = new ArrayList<>()
      this.k = new ArrayList<>()
      this.J = new HashSet<>()
      this.l = new ArrayList<>()
      this.m = new ArrayList<>()
      this.n = new ArrayList<>()
      this.o = new ArrayList<>()
      this.p = new ArrayList<>()
      this.q = new ArrayList<>()
      this.r = CollectionsKt.emptyList()
      this.s = new ArrayList<>()
      this.t = new HashMap<>()
      this.u = new ArrayList<>()
      this.v = new ArrayList<>()
      this.w = new ArrayList<>()
      this.K = var5.f()
      this.x = EU.a()
      this.y = new DL()
   }

   public final VsiMutableQueryableShipData<ShipData> a() {
      return this.a
   }

   public final Ek b() {
      return this.b
   }

   public final ChunkAllocatorProvider c() {
      return this.c
   }

   private VSCoreInternal l() {
      return FZ.a(this.D, z[0])
   }

   private Set<VsiPlayer> m() {
      return this.E
   }

   @Override
   public final Set<VsiPlayer> getPlayers() {
      return this.F
   }

   @Override
   public final void setPlayers(Set<? extends VsiPlayer> value) {
      Intrinsics.checkNotNullParameter(value, "")
      this.f.a(Et.c.UPDATE_PLAYERS)
      this.E = this.F
      this.F = value
   }

   private long n() {
      return this.H
   }

   @Override
   public final void setGameServer(VsiGameServer gameServer) {
      this.G = gameServer
   }

   public final ImmutableMap<VsiPlayer, ImmutableSet<ServerShipInternal>> d() {
      EB var1 = this.d
      ER var2 = this.d.a
      return this.d.a.g
   }

   private static KProperty0 b(Et var0) {
      return Reflection.property0(
         (PropertyReference0)(
            new PropertyReference0Impl(var0.d, EB.class, "playersToTrackedShips", "getPlayersToTrackedShips()Lcom/google/common/collect/ImmutableMap;", 0)
         )
      )
   }

   public final ImmutableMap<VsiPlayer, ImmutableSet<PhysicsEntityServer>> e() {
      EB var1 = this.d
      ER var2 = this.d.a
      return this.d.a.h
   }

   private static KProperty0 c(Et var0) {
      return Reflection.property0(
         (PropertyReference0)(
            new PropertyReference0Impl(
               var0.d, EB.class, "playersToTrackedPhysicsEntities", "getPlayersToTrackedPhysicsEntities()Lcom/google/common/collect/ImmutableMap;", 0
            )
         )
      )
   }

   @Override
   public final VsiQueryableShipData<Es> getLoadedShips() {
      return this.I
   }

   public final Map<Long, PhysicsEntityServer> f() {
      return this.g
   }

   @Override
   public final Map<Long, PhysicsEntityServer> retrieveLoadedPhysicsEntities() {
      return this.g
   }

   @Override
   public final Map<String, Long> getDimensionToGroundBodyIdImmutable() {
      return this.h
   }

   private final List<ShipData> o() {
      this.f.a(Et.c.GET_LAST_TICK_CHANGES)
      return this.r
   }

   public final Map<String, DP> g() {
      return this.e
   }

   private static SingleEvent<VSEvents.ShipLoadEvent> p() {
      return VSEvents.INSTANCE.getShipLoadEvent()
   }

   // $VF: synthetic method
   @Deprecated(
      message = "All events moved to VSEvents",
      replaceWith = @ReplaceWith(expression = "ShipLoadEvent", imports = "org.valkyrienskies.core.impl.hooks.VSEvents.ShipLoadEvent")
   )
   private static void q() {
   }

   private static KProperty0 r() {
      return Reflection.property0(
         (PropertyReference0)(
            new PropertyReference0Impl(
               VSEvents.INSTANCE, VSEvents.class, "shipLoadEvent", "getShipLoadEvent()Lorg/valkyrienskies/core/api/event/SingleEvent;", 0
            )
         )
      )
   }

   public final DL h() {
      return this.y
   }

   @Override
   public final ConnectionStatus isConnectedByAir(int x1, int y1, int z1, int x2, int y2, int z2, String dimension) {
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.a(dimension, x1, y1, z1, x2, y2, z2, 0)
   }

   @Override
   public final ConnectionStatus isConnectedBySolid(int x1, int y1, int z1, int x2, int y2, int z2, String dimension) {
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.a(dimension, x1, y1, z1, x2, y2, z2, 1)
   }

   @Override
   public final ConnectionStatus isIsolatedAir(int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.a(dimension, x, y, z, 0)
   }

   @Override
   public final ConnectionStatus isIsolatedSolid(int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.a(dimension, x, y, z, 1)
   }

   @Override
   public final DoubleAugmentation createDoubleSumAugmentation(String modId, String key) {
      Intrinsics.checkNotNullParameter(modId, "")
      Intrinsics.checkNotNullParameter(key, "")
      return this.B.a(modId, key)
   }

   @Override
   public final DoubleComponentAugmentation createDoubleSumComponentAugmentation(String modId, String key) {
      Intrinsics.checkNotNullParameter(modId, "")
      Intrinsics.checkNotNullParameter(key, "")
      return this.B.b(modId, key)
   }

   @Override
   public final double getSolidAugmentation(DoubleAugmentation key, int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.a(key, x, y, z, dimension)
   }

   @Override
   public final void setSolidAugmentation(DoubleAugmentation key, int x, int y, int z, String dimension, double value) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      this.B.a(key, x, y, z, dimension, value)
   }

   @Override
   public final void updateSolidAugmentation(DoubleAugmentation key, int x, int y, int z, String dimension, DoubleUnaryOperator update) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      Intrinsics.checkNotNullParameter(update, "")
      this.B.a(key, x, y, z, dimension, update)
   }

   @Override
   public final void removeSolidAugmentation(DoubleAugmentation key, int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      this.B.b(key, x, y, z, dimension)
   }

   @Override
   public final double collectSolidAugmentation(DoubleAugmentation key, int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.c(key, x, y, z, dimension)
   }

   @Override
   public final void setSolidComponentAugmentation(DoubleComponentAugmentation key, int x, int y, int z, String dimension, double value) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      this.B.a(key, x, y, z, dimension, value)
   }

   @Override
   public final double getSolidComponentAugmentation(DoubleComponentAugmentation key, int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.a(key, x, y, z, dimension)
   }

   @Override
   public final void removeSolidComponentAugmentation(DoubleComponentAugmentation key, int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      this.B.b(key, x, y, z, dimension)
   }

   @Override
   public final Map<Triple<Integer, Integer, Integer>, Double> getFromEachSolidComponent(DoubleAugmentation key, String dimension, ChunkClaim chunkClaim) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.a(key, dimension, chunkClaim)
   }

   @Override
   public final Map<Triple<Integer, Integer, Integer>, Double> getFromEachSolidComponentRoot(
      DoubleComponentAugmentation key, String dimension, ChunkClaim chunkClaim
   ) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.a(key, dimension, chunkClaim)
   }

   @Override
   public final Set<Vector3ic> getAllSolidComponentsFromClaim(String dimensionId, ChunkClaim chunkClaim) {
      Intrinsics.checkNotNullParameter(dimensionId, "")
      Intrinsics.checkNotNullParameter(chunkClaim, "")
      return this.B.a(chunkClaim, dimensionId)
   }

   @Override
   public final long getSolidComponentSize(int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.a(x, y, z, dimension)
   }

   @Override
   public final void traverseSolidComponent(
      int x, int y, int z, Function4<? super Integer, ? super Integer, ? super Integer, ? super Long, Unit> action, String dimension
   ) {
      Intrinsics.checkNotNullParameter(action, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      this.B.a(x, y, z, action, dimension)
   }

   @Override
   public final Set<Vector3ic> indexSolidComponent(int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(dimension, "")
      return DW.a$default(this.B, x, y, z, (Set)null, dimension, 8)
   }

   @Override
   public final Set<Vector3ic> indexSolidComponent(int x, int y, int z, Set<? extends Vector3ic> out, String dimension) {
      Intrinsics.checkNotNullParameter(out, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.a(x, y, z, out, dimension)
   }

   @Override
   public final Set<SparseVoxelPosition> indexSolidComponentVoxels(int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.b(x, y, z, new HashSet<>(), dimension)
   }

   @Override
   public final Set<SparseVoxelPosition> indexSolidComponentVoxels(int x, int y, int z, Set<SparseVoxelPosition> out, String dimension) {
      Intrinsics.checkNotNullParameter(out, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.b(x, y, z, out, dimension)
   }

   @Override
   public final double getAirAugmentation(DoubleAugmentation key, int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.d(key, x, y, z, dimension)
   }

   @Override
   public final void setAirAugmentation(DoubleAugmentation key, int x, int y, int z, String dimension, double value) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      this.B.b(key, x, y, z, dimension, value)
   }

   @Override
   public final void updateAirAugmentation(DoubleAugmentation key, int x, int y, int z, String dimension, DoubleUnaryOperator update) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      Intrinsics.checkNotNullParameter(update, "")
      this.B.b(key, x, y, z, dimension, update)
   }

   @Override
   public final void removeAirAugmentation(DoubleAugmentation key, int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      this.B.e(key, x, y, z, dimension)
   }

   @Override
   public final double collectAirAugmentation(DoubleAugmentation key, int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.f(key, x, y, z, dimension)
   }

   @Override
   public final void setAirComponentAugmentation(DoubleComponentAugmentation key, int x, int y, int z, String dimension, double value) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      this.B.b(key, x, y, z, dimension, value)
   }

   @Override
   public final double getAirComponentAugmentation(DoubleComponentAugmentation key, int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.c(key, x, y, z, dimension)
   }

   @Override
   public final void removeAirComponentAugmentation(DoubleComponentAugmentation key, int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      this.B.d(key, x, y, z, dimension)
   }

   @Override
   public final long getAirComponentSize(int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.b(x, y, z, dimension)
   }

   @Override
   public final Map<Triple<Integer, Integer, Integer>, Double> getFromEachAirComponent(DoubleAugmentation key, String dimension, ChunkClaim chunkClaim) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.b(key, dimension, chunkClaim)
   }

   @Override
   public final Map<Triple<Integer, Integer, Integer>, Double> getFromEachAirComponentRoot(
      DoubleComponentAugmentation key, String dimension, ChunkClaim chunkClaim
   ) {
      Intrinsics.checkNotNullParameter(key, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.b(key, dimension, chunkClaim)
   }

   @Override
   public final Set<Vector3ic> getAllAirComponentsFromClaim(String dimensionId, ChunkClaim chunkClaim) {
      Intrinsics.checkNotNullParameter(dimensionId, "")
      Intrinsics.checkNotNullParameter(chunkClaim, "")
      return this.B.b(chunkClaim, dimensionId)
   }

   @Override
   public final void traverseAirComponent(
      int x, int y, int z, Function4<? super Integer, ? super Integer, ? super Integer, ? super Long, Unit> action, String dimension
   ) {
      Intrinsics.checkNotNullParameter(action, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      this.B.b(x, y, z, action, dimension)
   }

   @Override
   public final Set<Vector3ic> indexAirComponent(int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(dimension, "")
      return DW.a$default(this.B, x, y, z, (DenseBlockPosSet)null, dimension, 8)
   }

   @Override
   public final Set<Vector3ic> indexAirComponent(int x, int y, int z, Set<? extends Vector3ic> out, String dimension) {
      Intrinsics.checkNotNullParameter(out, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.c(x, y, z, out, dimension)
   }

   @Override
   public final Set<SparseVoxelPosition> indexAirComponentVoxels(int x, int y, int z, String dimension) {
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.d(x, y, z, new HashSet<>(), dimension)
   }

   @Override
   public final Set<SparseVoxelPosition> indexAirComponentVoxels(int x, int y, int z, Set<SparseVoxelPosition> out, String dimension) {
      Intrinsics.checkNotNullParameter(out, "")
      Intrinsics.checkNotNullParameter(dimension, "")
      return this.B.d(x, y, z, out, dimension)
   }

   @Override
   public final void onSetBlock(
      int posX, int posY, int posZ, String dimensionId, VsiBlockType oldBlockType, VsiBlockType newBlockType, double oldBlockMass, double newBlockMass
   ) {
      Intrinsics.checkNotNullParameter(dimensionId, "")
      Intrinsics.checkNotNullParameter(oldBlockType, "")
      Intrinsics.checkNotNullParameter(newBlockType, "")
      super.onSetBlock(posX, posY, posZ, dimensionId, oldBlockType, newBlockType, oldBlockMass, newBlockMass)
      if (!Intrinsics.areEqual(oldBlockType, newBlockType)) {
         final Vector3ic var13 = new Vector3i(posX >> 4, posY >> 4, posZ >> 4)
         ShipData var10000 = this.a.getByChunkPos(var13.x(), var13.z(), dimensionId)
         Long var14
         if ((var14 = var10000 != null ? var10000.getId() : this.h.get(dimensionId)) == null) {
            Fb.a.a().error("Could not find ship or dimension body for block update at " + posX + ", " + posY + ", " + posZ + " in dimension " + dimensionId)
            return
         }

         Object var11
         Map var15
         Object var27
         if ((var11 = (Es)(var15 = (Map)this.x).get(var14)) == null) {
            BlockPos2ObjectOpenHashMap var12 = new BlockPos2ObjectOpenHashMap(0, 0.0F, 3, null)
            var15.put(var14, var12)
            var27 = var12
         } else {
            var27 = var11
         }

         BlockPos2ObjectOpenHashMap var8
         Ip var16 = (Ip)(var8 = (BlockPos2ObjectOpenHashMap)var27).getOrPut(var13, (Function0)(new Function0<Ip>() {
            public final Ip a() {
               return Ir.a.a(var13)
            }

            // $VF: synthetic method
            public final Object invoke() {
               return this.a()
            }
         }))
         DM var21
         int var10 = (var21 = (DM)newBlockType).a
         if ((var11 = (Es)this.getLoadedShips().getById(var14)) != null) {
            DN var22 = this.A
            CP var23 = this.A.b
            VsiBlockState var24 = (VsiBlockState)this.A.b.d.get(var10)
            Eh var28 = var11.getDragController()
            Intrinsics.checkNotNull(var28)
            var28 = var28
            Vector3ic var10003 = new Vector3i(posX, posY, posZ)
            Intrinsics.checkNotNullExpressionValue(var24, "")
            var28.a(new Ef(var10003, var24, var24.getSolidState() == null))
         }

         Ip var25 = var16
         if (var16 instanceof Ir) {
            ((Ir)var16).a(posX & 15, posY & 15, posZ & 15, var10)
         } else if (var25 instanceof In) {
            ((In)var16).a(posX & 15, posY & 15, posZ & 15, var10)
         } else {
            if (!(var25 instanceof Io)) {
               throw new NotImplementedError(null, 1, null)
            }

            Ir var17
            (var17 = Ir.a.a(var13)).a(posX & 15, posY & 15, posZ & 15, var10)
            var8.set(var13, var17)
         }

         VsiBlockType var18 = newBlockType
         int var26 = Intrinsics.areEqual(newBlockType, this.A.getAir())
            ? 0
            : ((Intrinsics.areEqual(var18, this.A.getWater()) ? true : Intrinsics.areEqual(var18, this.A.getLava())) ? 2 : 1)
            Long var19 = Intrinsics.areEqual(var14, this.h.get(dimensionId)) ? null : var14
         DW var30 = this.B
         Long var31 = this.h.get(dimensionId)
         var30.a(dimensionId, var19, posX, posY, posZ, var26)
      }
   }

   @Override
   public final void addTerrainUpdates(String dimensionId, List<? extends VsiTerrainUpdate> terrainUpdates) {
      Intrinsics.checkNotNullParameter(dimensionId, "")
      Intrinsics.checkNotNullParameter(terrainUpdates, "")
      this.f.a(Et.c.UPDATE_CHUNKS)
      Iterable var17
      Iterable var3 = var17 = terrainUpdates
      terrainUpdates = new ArrayList(CollectionsKt.collectionSizeOrDefault(var17, 10))

      for (Object var4 : var3) {
         VsiTerrainUpdate var5 = (VsiTerrainUpdate)var4
         ArrayList var25 = terrainUpdates
         Intrinsics.checkNotNull(var5)
         EW var16
         var25.add((var16 = (EW)var5).a)
      }

      terrainUpdates = terrainUpdates
      this.k.add(new Et.b(dimensionId, terrainUpdates))

      for (Ip var21 : var20 = terrainUpdates) {
         ShipData var10000 = this.a.getByChunkPos(var21.a(), var21.c(), dimensionId)
         if (var10000 != null) {
            ShipData var26 = var10000
            Es var28 = (Es)this.getLoadedShips().getById(var26.getId())
            Ip var6 = var21
            if (var21 instanceof In ? true : var6 instanceof Io) {
               var26.onLoadChunk(var21.a(), var21.c())
               this.b(var26)
               if (VSCoreConfig.SERVER.getSp().getEnableConnectivity()) {
                  this.B.a(dimensionId, var21.a(), var21.b(), var21.c(), var26.getId(), var21)
               }
            } else {
               if (!(var6 instanceof Im)) {
                  throw new NotImplementedError(null, 1, null)
               }

               var26.onUnloadChunk(var21.a(), var21.c())
               if (VSCoreConfig.SERVER.getSp().getEnableConnectivity()) {
                  this.B.a(dimensionId, var21.a(), var21.b(), var21.c())
               }
            }

            if (var21 instanceof In var30) {
               int var7 = 0

               while (var7 < 16) {
                  int var8 = var7++
                  int var9 = 0

                  while (var9 < 16) {
                     int var10 = var9++
                     int var11 = 0

                     while (var11 < 16) {
                        int var12 = var11++
                        int var50 = var10
                        int var13 = var8
                        int var14 = var12
                        int var51 = var30.a(var14, var13, var50)
                        int var55 = var50
                        var14 = var13
                        int var15 = var12
                        var26.updateShipAABBGenerator(
                           (var21.a() << 4) + var15, (var21.b() << 4) + var14, (var21.c() << 4) + var55, var51 != this.A.getAir().toInt()
                        )
                        if (var28 != null && var28.getDragController() != null) {
                           DragController var78 = var28.getDragController()
                           Intrinsics.checkNotNull(var78)
                           Eh var79 = (Eh)var78
                           AABBic var52 = var28.getShipAABB()
                           var79.b = var52
                        }
                     }
                  }
               }
            }

            if (var28 != null && var28.getDragController() != null) {
               DragController var80 = var28.getDragController()
               Intrinsics.checkNotNull(var80)
               Eh var81 = (Eh)var80
               DN var29 = this.A
               Ip var27 = var21
               Eh var22 = var81
               Intrinsics.checkNotNullParameter(var27, "")
               Intrinsics.checkNotNullParameter(var29, "")
               var6 = var27
               if (var27 instanceof In var33) {
                  int var44 = 0

                  while (var44 < 16) {
                     int var47 = var44++
                     int var49 = 0

                     while (var49 < 16) {
                        int var35 = var49++
                        int var54 = 0

                        while (var54 < 16) {
                           int var37 = var54++
                           int var58 = var35
                           int var63 = var47
                           int var69 = var37
                           int var59 = var33.a(var69, var63, var58)
                           int var64 = var58
                           int var38 = var63
                           var69 = var37
                           CP var74 = var29.b
                           Object var85 = var29.b.d.get(var59)
                           Intrinsics.checkNotNullExpressionValue(var85, "")
                           if (Eh.a((VsiBlockState)var85) && var59 != var29.getEmpty().toInt()) {
                              Vector3i var66 = new Vector3i((var27.a() << 4) + var69, (var27.b() << 4) + var38, (var27.c() << 4) + var64)
                              Vector3ic var90 = var66
                              var74 = var29.b
                              Object var93 = var29.b.d.get(var59)
                              Intrinsics.checkNotNull(var93)
                              var22.a(new Ef(var90, (VsiBlockState)var93, false))
                           } else {
                              Vector3i var65 = new Vector3i((var27.a() << 4) + var69, (var27.b() << 4) + var38, (var27.c() << 4) + var64)
                              Vector3ic var89 = var65
                              var74 = var29.b
                              Object var92 = var29.b.d.get(var59)
                              Intrinsics.checkNotNull(var92)
                              var22.a(new Ef(var89, (VsiBlockState)var92, true))
                           }
                        }
                     }
                  }
               } else if (var6 instanceof Ir) {
                  var6 = (Ir)var27
                  int var34 = 0
                  int var36 = var6.e.size()

                  while (var34 < var36) {
                     int var39 = var34++
                     int var45 = var6.e.getShort(var39)
                     int var48 = var6.f.getInt(var39)
                     short var53 = (short)var45
                     int var56 = var45 >> 8 & 15
                     int var61 = var53 >> 4 & 15
                     int var67 = var53 & 15
                     var39 = var67
                     int var57 = var61
                     var67 = var48
                     var39 = var39
                     var61 = var57
                     var45 = var56
                     CP var71 = var29.b
                     Object var83 = var29.b.d.get(var67)
                     Intrinsics.checkNotNullExpressionValue(var83, "")
                     if (Eh.a((VsiBlockState)var83) && var67 != var29.getEmpty().toInt()) {
                        Vector3i var43 = new Vector3i((var27.a() << 4) + var45, (var27.b() << 4) + var61, (var27.c() << 4) + var39)
                        Vector3ic var88 = var43
                        var71 = var29.b
                        Object var91 = var29.b.d.get(var67)
                        Intrinsics.checkNotNull(var91)
                        var22.a(new Ef(var88, (VsiBlockState)var91, false))
                     } else {
                        Vector3i var42 = new Vector3i((var27.a() << 4) + var45, (var27.b() << 4) + var61, (var27.c() << 4) + var39)
                        Vector3ic var10003 = var42
                        var71 = var29.b
                        Object var10004 = var29.b.d.get(var67)
                        Intrinsics.checkNotNull(var10004)
                        var22.a(new Ef(var10003, (VsiBlockState)var10004, true))
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public final void forceUpdateConnectivityChunk(String dimensionId, int chunkX, int chunkY, int chunkZ, VsiTerrainUpdate update) {
      Intrinsics.checkNotNullParameter(dimensionId, "")
      Intrinsics.checkNotNullParameter(update, "")
      this.B.a(dimensionId, chunkX, chunkY, chunkZ)
      DW var10000 = this.B
      ShipData var10005 = this.a.getByChunkPos(chunkX, chunkZ, dimensionId)
      EW var6
      var10000.a(dimensionId, chunkX, chunkY, chunkZ, var10005 != null ? var10005.getId() : null, (var6 = (EW)update).a)
   }

   public final Es a(ServerShipInternal var1) {
      Intrinsics.checkNotNullParameter(var1, "")
      return (Es)this.getLoadedShips().getById(var1.getId())
   }

   @Override
   public final void preTick() {
      this.f.a(Et.c.PRE_TICK)
      super.preTick()
      EB var10000 = this.d
      Set var10001 = this.getPlayers()
      Set var10002 = this.E
      Iterable var10003 = this.a
      Et var1 = this
      this.f.a(Et.c.GET_LAST_TICK_CHANGES)
      Iterable var5 = var1.r
      Iterable var4 = var10003
      Set var3 = var10002
      Set var2 = var10001
      EB var6 = var10000
      Intrinsics.checkNotNullParameter(var2, "")
      Intrinsics.checkNotNullParameter(var3, "")
      Intrinsics.checkNotNullParameter(var4, "")
      Intrinsics.checkNotNullParameter(var5, "")
      var6.e.a(EB.a.PRE_TICK)
      var6.h = var6.b.a(var2, var3, var4, var5)
   }

   public final void i() {
      this.f.a(Et.c.POST_TICK_START)
      this.H++
      Iterable var1
      Iterable var3 = var1 = (Iterable)this.a
      var var4 = new ArrayList()
      Iterator var6 = var3.iterator()

      while (var6.hasNext()) {
         Object var7
         if (((ShipData)(var7 = var6.next())).getInertiaData().getMass() < 1.0E-8) {
            var4.add(var7)
         }
      }

      for (ShipData var5 : var1 = var4) {
         this.deleteShip(var5)
      }

      Map var47 = this.g
      Iterator var67 = this.g.entrySet().iterator()

      while (var67.hasNext()) {
         PhysicsEntityServer var139
         if ((var139 = (PhysicsEntityServer)((Entry)var67.next()).getValue()).getNeedsUpdating()) {
            this.o.add(var139)
            var139.setNeedsUpdating(false)
         }
      }

      Iterator var68 = this.getLoadedShips().iterator()

      while (var68.hasNext()) {
         Es var110
         ShipInertiaData var10000 = (var110 = (Es)var68.next()).getInertiaData()
         Intrinsics.checkNotNull(var10000)
         ((ShipInertiaDataImpl)var10000).setCmChangedThisTick(false)
         var110.b.setNeedsUpdating(false)
         this.n.add(var110)
      }

      var1 = new ArrayList()
      Iterator var2 = this.a.iterator()

      while (var2.hasNext()) {
         ShipData var69
         long var80 = (var69 = (ShipData)var2.next()).getId()
         if (var69.getInertiaData().getMass() == 0.0) {
            Fb.a.a().warn("Ship with ID " + var80 + " has a mass of 0.0, this should NOT be possible!")
         } else if (!this.I.contains(var80)) {
            Es var130 = new Es(var69)
            this.l.add(var130)
            this.I.add(var130)
            var1.add(var130)
            this.b(var69)
         }
      }

      Et var111 = this
      long var18
      if ((var18 = s()) > 0L) {
         long var20 = var111.H + var18
         HashSet var49 = new HashSet()
         Iterator var81 = (var3 = var111.a).iterator()

         while (var81.hasNext()) {
            long var29 = ((ShipData)var81.next()).getId()
            if (var49.add(var29)) {
               Set var140
               boolean var227
               label459: {
                  var140 = var111.b.a(var29)
                  var49.addAll(var140)
                  Iterable var8
                  if (!((Collection)(var8 = var140)).isEmpty()) {
                     Iterator var9 = var8.iterator()

                     while (var9.hasNext()) {
                        long var36 = ((Number)var9.next()).longValue()
                        ShipData var226 = var111.a.getById(var36)
                        if (var226 != null ? !var226.areVoxelsFullyLoaded() : true) {
                           var227 = true
                           break label459;
                        }
                     }
                  }

                  var227 = false
               }

               if (var227) {
                  var111.a(var140, var20)
               }
            }
         }
      }

      var2 = this.k.iterator()

      while (var2.hasNext()) {
         Et.b var71
         String var82 = (var71 = (Et.b)var2.next()).a
         List var112 = var71.b

         for (Ip var141 : var71.b) {
            Vector3ic var148 = new Vector3i(var141.a(), var141.b(), var141.c())
            ShipData var228 = this.a.getByChunkPos(var148.x(), var148.z(), var82)
            long var229
            if (var228 != null) {
               var229 = var228.getId()
            } else {
               Object var230 = this.h.get(var82)
               Intrinsics.checkNotNull(var230)
               var229 = ((Number)var230).longValue()
            }

            long var10 = var229
            Long var10001 = this.h.get(var82)
            if (var10001 != null && var229 == var10001 && VSCoreConfig.SERVER.getSp().getEnableWorldConnectivity()) {
               Ip var113 = var141
               if (var141 instanceof In ? true : var113 instanceof Io) {
                  if (VSCoreConfig.SERVER.getSp().getEnableConnectivity()) {
                     this.B.a(var82, var141.a(), var141.b(), var141.c(), null, var141)
                  }
               } else if (var113 instanceof Im && VSCoreConfig.SERVER.getSp().getEnableConnectivity()) {
                  this.B.a(var82, var141.a(), var141.b(), var141.c())
               }
            }

            Map var12 = (Map)this.x
            Long var176 = var10
            Object var72
            Object var231
            if ((var72 = var12.get(var176)) == null) {
               var72 = new BlockPos2ObjectOpenHashMap(0, 0.0F, 3, null)
               var12.put(var176, var72)
               var231 = var72
            } else {
               var231 = var72
            }

            ((BlockPos2ObjectOpenHashMap)var231).set(var148, var141)
         }
      }

      for (String var74 : this.e.keySet()) {
         Pair var83
         HashSet var114 = (HashSet)(var83 = this.B.a(var74)).component1()
         HashSet var132 = (HashSet)var83.component2()
         Pair var142
         HashSet var149 = (HashSet)(var142 = this.B.b(var74)).component1()
         HashSet var163 = (HashSet)var142.component2()
         Iterable var177
         Iterator var115 = (var177 = var114).iterator()

         while (var115.hasNext()) {
            SplitEvent var178
            (var178 = (SplitEvent)var115.next()).setDimensionId(var74)
            this.l().getSplitEvent().emit(var178)
         }

         Iterator var116 = (var177 = var132).iterator()

         while (var116.hasNext()) {
            MergeEvent var180
            (var180 = (MergeEvent)var116.next()).setDimensionId(var74)
            this.l().getMergeEvent().emit(var180)
         }

         Iterator var117 = (var177 = var149).iterator()

         while (var117.hasNext()) {
            SplitEvent var182
            (var182 = (SplitEvent)var117.next()).setDimensionId(var74)
            this.l().getSplitEvent().emit(var182)
         }

         Iterator var118 = (var177 = var163).iterator()

         while (var118.hasNext()) {
            MergeEvent var184
            (var184 = (MergeEvent)var118.next()).setDimensionId(var74)
            this.l().getMergeEvent().emit(var184)
         }
      }

      this.f.a(Et.c.POST_TICK_GENERATED)
      EB var232 = this.d
      Set var257 = this.getPlayers()
      Set var10002 = this.E
      Iterable var10003 = this.g.values()
      Iterable var21 = this.q
      Iterable var196 = var10003
      Set var19 = var10002
      Set var195 = var257
      EB var119 = var232
      Intrinsics.checkNotNullParameter(var195, "")
      Intrinsics.checkNotNullParameter(var19, "")
      Intrinsics.checkNotNullParameter(var196, "")
      Intrinsics.checkNotNullParameter(var21, "")
      var119.e.a(EB.a.POST_TICK)
      var119.i = var119.d.a(var195, var19, var196, var21)
      Iterable var258 = var119.f
      if (var119.f == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var258 = null
      }

      Iterable var262 = var119.g
      if (var119.g == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var262 = null
      }

      CR var52 = var119.b.a(var258, var262)
      var258 = var195
      Fx var265 = var119.i
      if (var119.i == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var265 = null
      }

      Fx var133 = var265
      CR var84 = var52
      Iterable var53 = var258
      ER var75 = var119.a
      Intrinsics.checkNotNullParameter(var53, "")
      Intrinsics.checkNotNullParameter(var84, "")
      Intrinsics.checkNotNullParameter(var133, "")
      var75.d = var53
      var75.e = var84
      var75.f = var133
      ER var134 = var75
      Iterable var233 = var75.d
      if (var75.d == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var233 = null
      }

      for (VsiPlayer var197 : var233) {
         ByteBuf var30 = Unpooled.buffer()
         CR var234 = var134.e
         if (var134.e == null) {
            Intrinsics.throwUninitializedPropertyAccessException("")
            var234 = null
         }

         Set var235 = var234.b.get(var197)
         if (var235 == null) {
            var235 = SetsKt.emptySet()
         }

         Set var143 = var235
         VsiPlayer var164 = var197
         CR var236 = var134.e
         if (var134.e == null) {
            Intrinsics.throwUninitializedPropertyAccessException("")
            var236 = null
         }

         VsiPlayer var150 = var164
         CR var185 = var236
         Intrinsics.checkNotNullParameter(var150, "")
         Map var237 = var185.a.get(var150)
         if (var237 == null) {
            var237 = MapsKt.emptyMap()
         }

         Iterable var151 = var237.keySet()
         var var203 = new ArrayList()
         Iterator var85 = var151.iterator()

         while (var85.hasNext()) {
            Object var165
            ServerShipInternal var120 = (ServerShipInternal)(var165 = var85.next())
            if (!var143.contains(var120)) {
               var203.add(var165)
            }
         }

         Iterable var86
         Iterable var152 = var86 = var203
         var203 = new ArrayList()

         for (ServerShipInternal var44 : var152) {
            Et var238 = var134.a()
            ServerShipInternal var166 = var44
            Et var87 = var238
            Intrinsics.checkNotNullParameter(var166, "")
            Es var239 = (Es)var87.getLoadedShips().getById(var166.getId())
            if (var239 != null) {
               Es var88 = var239
               var203.add(var88)
            }
         }

         List var153
         if (!(var153 = var203).isEmpty()) {
            for (Es var37 : var86 = var153) {
               var30.writeLong(var37.b.getId())
               JsonNode var167 = VSJacksonUtil.INSTANCE.getDeltaMapper().valueToTree(var37.b)
               Fs var240 = var37.d
               Intrinsics.checkNotNullExpressionValue(var167, "")
               Intrinsics.checkNotNullExpressionValue(var30, "")
               ByteBuf var186 = var30
               JsonNode var168 = var167
               Fs var90 = var240
               Intrinsics.checkNotNullParameter(var186, "")
               var90.a.a(var90.b, var168, var186)
               var90.b = (T)var168
            }

            Fe var187 = var134.c
            Fd var241 = var134.c.c
            Intrinsics.checkNotNullExpressionValue(var30, "")
            var241.a(var30, var197)
         }
      }

      ER var135 = var75
      Iterable var242 = var75.d
      if (var75.d == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var242 = null
      }

      for (VsiPlayer var198 : var242) {
         CR var243 = var135.e
         if (var135.e == null) {
            Intrinsics.throwUninitializedPropertyAccessException("")
            var243 = null
         }

         Set var244 = var243.c.get(var198)
         if (var244 == null) {
            var244 = SetsKt.emptySet()
         }

         Set var200 = var244
         CR var266 = var135.e
         if (var135.e == null) {
            Intrinsics.throwUninitializedPropertyAccessException("")
            var266 = null
         }

         Collection var91 = SetsKt.plus(var200, var266.d)
         VsiPlayer var210 = var198
         ER var205 = var135
         Iterable var122 = var91
         Iterable var42 = var91
         var var43 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var122, 10))

         for (Object var92 : var42) {
            ServerShipInternal var93 = (ServerShipInternal)var92
            ArrayList var188 = var43
            var188.add(var93.getId())
         }

         List var169
         if (!(var169 = var43).isEmpty()) {
            ER.a.a(ER.a).debug("{} unwatched ships {}", var210.getUuid(), var169)
            var205.b.sendToClient(new PacketShipRemove(var169), var210)
         }

         var210 = var198
         var205 = var135
         CR var245 = var135.e
         if (var135.e == null) {
            Intrinsics.throwUninitializedPropertyAccessException("")
            var245 = null
         }

         if (var245.e.contains(var210)) {
            CR var246 = var205.e
            if (var205.e == null) {
               Intrinsics.throwUninitializedPropertyAccessException("")
               var246 = null
            }

            if ((var91 = var246.b.get(var210)) == null || var91.isEmpty()) {
               var205.b.sendToClient(new PacketShipDataCreate(CollectionsKt.emptyList()), var210)
            }
         }
      }

      CR var247 = var135.e
      if (var135.e == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var247 = null
      }

      Iterator var201 = var247.b.entrySet().iterator()

      while (var201.hasNext()) {
         Entry var155
         VsiPlayer var170 = (VsiPlayer)(var155 = (Entry)var201.next()).getKey()
         Set var156 = (Set)var155.getValue()
         Iterable var95 = var156
         VsiPlayer var212 = var170
         ER var207 = var135
         Iterable var123 = var95
         Iterable var215 = var95
         var var219 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var123, 10))

         for (Object var96 : var215) {
            ServerShipInternal var97 = (ServerShipInternal)var96
            ArrayList var189 = var219
            var189.add(var97.asShipDataCommon())
         }

         List var171
         if (!(var171 = var219).isEmpty()) {
            Logger var248 = ER.a.a(ER.a)
            UUID var263 = var212.getUuid()
            Iterable var124 = var171
            UUID var56 = var263
            String var157 = "{} watched ships: {}"
            Logger var190 = var248
            var215 = var124
            var219 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var124, 10))

            for (Object var98 : var215) {
               ShipDataCommon var99 = (ShipDataCommon)var98
               ArrayList var125 = var219
               var125.add(var99.getId())
            }

            List var126 = var219
            var190.debug(var157, var56, var126)
            var207.b.sendToClient(new PacketShipDataCreate(var171), var212)
         }
      }

      ER var136 = var75
      Builder var57 = ImmutableMap.builder()
      CR var249 = var136.e
      if (var136.e == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var249 = null
      }

      Iterator var144 = var249.a.entrySet().iterator()

      while (var144.hasNext()) {
         Entry var100
         VsiPlayer var158 = (VsiPlayer)(var100 = (Entry)var144.next()).getKey()
         Map var208 = (Map)var100.getValue()
         var57.put(var158, VSCoreUtilKt.toImmutableSet(var208.keySet()))
      }

      ImmutableMap var260 = var57.build()
      Intrinsics.checkNotNullExpressionValue(var260, "")
      var136.g = var260
      ER var137 = var75
      Builder var58 = ImmutableMap.builder()
      Fx var250 = var137.f
      if (var137.f == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var250 = null
      }

      var144 = var250.a.entrySet().iterator()

      while (var144.hasNext()) {
         Entry var101
         VsiPlayer var159 = (VsiPlayer)(var101 = (Entry)var144.next()).getKey()
         Set var209 = (Set)var101.getValue()
         com.google.common.collect.ImmutableSet.Builder var213 = ImmutableSet.builder()
         Iterable var102
         Iterator var127 = (var102 = var209).iterator()

         while (var127.hasNext()) {
            long var217 = ((Number)var127.next()).longValue()
            PhysicsEntityServer var225
            if ((var225 = var137.a().g.get(var217)) != null) {
               var213.add(var225)
            }
         }

         var58.put(var159, var213.build())
      }

      ImmutableMap var261 = var58.build()
      Intrinsics.checkNotNullExpressionValue(var261, "")
      var137.h = var261
      Iterable var251 = var137.d
      if (var137.d == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var251 = null
      }

      for (VsiPlayer var202 : var251) {
         Fx var252 = var137.f
         if (var137.f == null) {
            Intrinsics.throwUninitializedPropertyAccessException("")
            var252 = null
         }

         Set var253 = var252.c.get(var202)
         if (var253 == null) {
            var253 = SetsKt.emptySet()
         }

         Set var146 = var253
         Iterable var59 = var146
         VsiPlayer var160 = var202
         ER var191 = var137
         if (var59.iterator().hasNext()) {
            ER.a.a(ER.a).debug("{} unwatched physics entities {}", var160.getUuid(), var59)
            var191.b.sendToClient(new PacketPhysEntityRemove(CollectionsKt.toList(var59)), var160)
         }
      }

      Fx var254 = var137.f
      if (var137.f == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var254 = null
      }

      var144 = var254.b.entrySet().iterator()

      while (var144.hasNext()) {
         Entry var103
         VsiPlayer var161 = (VsiPlayer)(var103 = (Entry)var144.next()).getKey()
         Iterable var214
         ArrayList var172 = var214 = (Iterable & Set)var103.getValue()
         var var128 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var214, 10))

         for (Object var221 : var172) {
            long var222 = ((Number)var221).longValue()
            var4 = var128
            Object var255 = var137.a().g.get(var222)
            Intrinsics.checkNotNull(var255)
            var4.add((PhysicsEntityServer)var255)
         }

         var172 = var128
         Iterable var60 = var172
         var161 = var161
         ER var192 = var137
         Iterable var61
         var3 = var61 = var60
         var var62 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var61, 10))

         for (Object var105 : var3) {
            PhysicsEntityServer var106 = (PhysicsEntityServer)var105
            var172 = var62
            var172.add(new PhysEntityCreateData(var106.getId(), var106.getDimensionId(), var106.getShipTransform(), var106.getCollisionShapeData()))
         }

         List var129
         if (!(var129 = var62).isEmpty()) {
            Logger var256 = ER.a.a(ER.a)
            UUID var264 = var161.getUuid()
            Iterable var63 = var129
            UUID var193 = var264
            String var11 = "{} watched physics entities: {}"
            Logger var175 = var256
            var3 = var63
            var var64 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var63, 10))

            for (Object var107 : var3) {
               PhysEntityCreateData var108 = (PhysEntityCreateData)var107
               ArrayList var13 = var64
               var13.add(var108.getId())
            }

            List var194 = var64
            var175.debug(var11, var193, var194)
            var192.b.sendToClient(new PacketPhysEntityCreate(var129), var161)
         }
      }

      for (Es var138 : var65 = var1) {
         VSEvents.INSTANCE.getShipLoadEvent().emit(new VSEvents.ShipLoadEvent(var138))
      }

      VSEvents.INSTANCE.getTickEndEvent().emit(new VSEvents.TickEndEvent(this))
      this.f.a(Et.c.POST_TICK_FINISH)
   }

   @Override
   public final Iterator<VsiPlayer> getIPlayersWatchingShipChunk(int chunkX, int chunkZ, String dimensionId) {
      Intrinsics.checkNotNullParameter(dimensionId, "")
      String var4 = dimensionId
      int var7 = chunkZ
      chunkZ = chunkX
      EB var5 = this.d
      Intrinsics.checkNotNullParameter(var4, "")
      if (var5.c.forDimension(var4).isChunkInShipyard(chunkZ, var7)) {
         return var5.b.a(chunkZ, var7, var4).iterator()
      }

      Iterator var10000 = Collections.emptyIterator()
      Intrinsics.checkNotNullExpressionValue(var10000, "")
      return var10000
   }

   @Override
   public final VsiChunkWatchTasks getChunkWatchTasks() {
      EB var1 = this.d
      VsiChunkWatchTasks var10000 = this.d.h
      if (this.d.h == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var10000 = null
      }

      return var10000
   }

   @Override
   public final void setExecutedChunkWatchTasks(Iterable<? extends VsiChunkWatchTask> watchTasks, Iterable<? extends VsiChunkUnwatchTask> unwatchTasks) {
      Intrinsics.checkNotNullParameter(watchTasks, "")
      Intrinsics.checkNotNullParameter(unwatchTasks, "")
      Iterable var3 = unwatchTasks
      unwatchTasks = watchTasks
      EB var4 = this.d
      Intrinsics.checkNotNullParameter(unwatchTasks, "")
      Intrinsics.checkNotNullParameter(var3, "")
      var4.e.a(EB.a.SET_EXECUTED)
      var4.f = unwatchTasks
      var4.g = var3
   }

   private CompletableFuture<ServerShip> a(Vector3ic var1, boolean var2, double var3, String var5) {
      Intrinsics.checkNotNullParameter(var1, "")
      Intrinsics.checkNotNullParameter(var5, "")
      CompletableFuture var6 = new CompletableFuture()
      ShipData var7 = this.b(var1, var2, var3, var5)
      VSEvents.ShipLoadEvent.Companion.once(Et::a, Et::b)
      return var6
   }

   // $VF: synthetic method
   private static CompletableFuture a(Et var0, Vector3ic var1, boolean var2, double var3, String var5, int var6) {
      if ((var6 & 4) != 0) {
         var3 = 1.0
      }

      String var16 = var5
      double var10 = var3
      var2 = var2
      var1 = var1
      var0 = var0
      Intrinsics.checkNotNullParameter(var1, "")
      Intrinsics.checkNotNullParameter(var16, "")
      CompletableFuture var4 = new CompletableFuture()
      ShipData var13 = var0.b(var1, var2, var10, var16)
      VSEvents.ShipLoadEvent.Companion.once(Et::a, Et::b)
      return var4
   }

   private ShipData b(Vector3ic var1, boolean var2, double var3, String var5) {
      Intrinsics.checkNotNullParameter(var1, "")
      Intrinsics.checkNotNullParameter(var5, "")
      ChunkAllocator var6
      ChunkClaim var7 = (var6 = this.c.forDimension(var5)).allocateNewChunkClaim()
      String var8 = GA.INSTANCE.a()
      Vector3d var10000 = new Vector3d(var1).add(0.5, 0.5, 0.5)
      Intrinsics.checkNotNullExpressionValue(var10000, "")
      Vector3dc var10 = var10000
      Vector3ic var9 = ChunkClaim.getCenterBlockCoordinates$default(var7, this.a(var5), null, 2, null)
      var10000 = new Vector3d(var9).add(0.5, 0.5, 0.5)
      Intrinsics.checkNotNullExpressionValue(var10000, "")
      Vector3dc var13 = var10000
      ShipData var11 = ShipData.Factory.createEmpty$default(this.C, var8, var6.allocateShipId(), var7, var5, var10, var13, var3, false, 128, null)
      this.a.add(var11)
      if (var2) {
         String var12 = "Not implemented"
         throw new NotImplementedError("An operation is not implemented: " + var12)
      } else {
         return var11
      }
   }

   @Override
   public final PhysicsEntityServer createPhysicsEntity(PhysicsEntityData physicsEntityData, String dimensionId) {
      Intrinsics.checkNotNullParameter(physicsEntityData, "")
      Intrinsics.checkNotNullParameter(dimensionId, "")
      if (this.g.containsKey(physicsEntityData.getShipId())) {
         throw new IllegalArgumentException("We already have a physics entity registered to id " + physicsEntityData.getShipId() + "!")
      }

      PhysicsEntityServer var3 = new PhysicsEntityServer(
         physicsEntityData.getShipId(),
         dimensionId,
         physicsEntityData.getLinearVelocity(),
         physicsEntityData.getAngularVelocity(),
         physicsEntityData.getInertiaData(),
         physicsEntityData.getTransform(),
         physicsEntityData.getTransform(),
         0,
         physicsEntityData.getCollisionShapeData(),
         physicsEntityData.getCollisionMask(),
         physicsEntityData.getStaticFrictionCoefficient(),
         physicsEntityData.getDynamicFrictionCoefficient(),
         physicsEntityData.getRestitutionCoefficient(),
         physicsEntityData.isStatic(),
         false,
         null,
         49152,
         null
      )
      this.g.put(physicsEntityData.getShipId(), var3)
      this.m.add(var3)
      return var3
   }

   @Override
   public final void deletePhysicsEntity(long id) {
      Long var3 = id
      if (!this.g.containsKey(var3)) {
         throw new IllegalArgumentException("Trying to remove a physics entity that doesn't exist with id " + id + "!")
      }

      this.g.remove(id)
      this.q.add(id)
   }

   @Override
   public final long allocateShipId(String dimensionId) {
      Intrinsics.checkNotNullParameter(dimensionId, "")
      return this.c.forDimension(dimensionId).allocateShipId()
   }

   private final boolean a(long var1) {
      return this.a.contains(var1) || this.g.containsKey(var1) || this.getDimensionToGroundBodyIdImmutable().containsValue(var1)
   }

   public final LevelYRange a(String var1) {
      Intrinsics.checkNotNullParameter(var1, "")
      return ((DP)MapsKt.getValue(this.e, var1)).b
   }

   @Override
   public final void destroyWorld() {
   }

   public final Et.a j() {
      this.f.a(Et.c.GET_CURRENT_TICK_CHANGES)
      return new Et.a(this.l, this.m, this.n, this.o, this.p, this.q, this.x, this.i, this.j, this.s, this.t.values(), this.u, this.v, this.w)
   }

   public final void k() {
      this.f.a(Et.c.CLEAR_FOR_RESET)
      this.l.clear()
      this.m.clear()
      this.n.clear()
      this.o.clear()
      this.r = CollectionsKt.toList(this.p)
      this.p.clear()
      this.q.clear()
      this.x.clear()
      this.k.clear()
      this.i.clear()

      Iterable var1
      for (String var2 : var1 = this.j) {
         if (this.h.remove(var2) == null) {
            throw new IllegalStateException("Check failed.")
         }
      }

      this.j.clear()
      this.s = new ArrayList<>()
      this.t = new HashMap<>()
      this.u = new ArrayList<>()
      this.v = new ArrayList<>()
      this.w = new ArrayList<>()
   }

   public final boolean a(ShipData var1) {
      Intrinsics.checkNotNullParameter(var1, "")
      return var1.getLoadFreezeUntilGameTick() > this.H
   }

   private void b(ShipData var1) {
      Intrinsics.checkNotNullParameter(var1, "")
      long var2
      if ((var2 = s()) > 0L) {
         this.a(this.b.a(var1.getId()), this.H + var2)
      }
   }

   private static long s() {
      double var0
      return (var0 = VSCoreConfig.SERVER.getPhysics().getShipLoadFreezeSeconds()) <= 0.0 ? 0L : (long)Math.ceil(var0 * 20.0)
   }

   private final void t() {
      long var1
      if ((var1 = s()) > 0L) {
         long var3 = this.H + var1
         HashSet var18 = new HashSet()
         Iterable var2
         Iterator var19 = (var2 = this.a).iterator()

         while (var19.hasNext()) {
            long var10 = ((ShipData)var19.next()).getId()
            if (var18.add(var10)) {
               Set var5
               boolean var21
               label48: {
                  var5 = this.b.a(var10)
                  var18.addAll(var5)
                  Iterable var6
                  if (!((Collection)(var6 = var5)).isEmpty()) {
                     Iterator var20 = var6.iterator()

                     while (var20.hasNext()) {
                        long var16 = ((Number)var20.next()).longValue()
                        ShipData var10000 = this.a.getById(var16)
                        if (var10000 != null ? !var10000.areVoxelsFullyLoaded() : true) {
                           var21 = true
                           break label48;
                        }
                     }
                  }

                  var21 = false
               }

               if (var21) {
                  this.a(var5, var3)
               }
            }
         }
      }
   }

   private final void a(Iterable<Long> var1, long var2) {
      Iterator var9 = var1.iterator()

      while (var9.hasNext()) {
         long var7 = ((Number)var9.next()).longValue()
         ShipData var10000 = this.a.getById(var7)
         if (var10000 != null) {
            ShipData var4 = var10000
            var10000.setLoadFreezeUntilGameTick(Math.max(var4.getLoadFreezeUntilGameTick(), var2))
         }
      }
   }

   @Override
   public final void addDimension(String dimensionId, LevelYRange yRange, Vector3dc gravity, double seaLevel, double maxY) {
      Intrinsics.checkNotNullParameter(dimensionId, "")
      Intrinsics.checkNotNullParameter(yRange, "")
      Intrinsics.checkNotNullParameter(gravity, "")
      this.f.a(Et.c.UPDATE_DIMENSIONS)
      if (this.e.containsKey(dimensionId)) {
         dimensionId = "Failed requirement."
         throw new IllegalArgumentException(dimensionId.toString())
      }

      if (this.h.containsKey(dimensionId)) {
         dimensionId = "Failed requirement."
         throw new IllegalArgumentException(dimensionId.toString())
      }

      this.i.add(dimensionId)
      this.h.put(dimensionId, this.c.forDimension(dimensionId).allocateShipId())
      this.e.put(dimensionId, new DP(dimensionId, yRange, new Vector3d(gravity)))
      this.y.registerDimension(dimensionId, maxY, seaLevel, -gravity.y())
   }

   @Override
   public final void updateDimension(String dimensionId, Vector3dc gravity, Double seaLevel, Double maxY) {
      Intrinsics.checkNotNullParameter(dimensionId, "")
      Intrinsics.checkNotNullParameter(gravity, "")
      this.f.a(Et.c.UPDATE_DIMENSIONS)
      DP var10000 = this.e.get(dimensionId)
      if (var10000 == null) {
         throw new IllegalArgumentException("No existing dimension info found for dimensionId: " + dimensionId)
      }

      DP var5 = var10000
      var5 = new DP(var5.a, var5.b, gravity)
      this.e.put(dimensionId, var5)
      this.y.updateGravityForDimension(dimensionId, -gravity.y())
      if (seaLevel != null || maxY != null) {
         this.y.updateAerodynamicInfoForDimension(dimensionId, maxY, seaLevel)
      }
   }

   @Override
   public final void removeDimension(String dimensionId) {
      Intrinsics.checkNotNullParameter(dimensionId, "")
      this.f.a(Et.c.UPDATE_DIMENSIONS)
      if (!this.e.containsKey(dimensionId)) {
         dimensionId = "Failed requirement."
         throw new IllegalArgumentException(dimensionId.toString())
      }

      if (!this.h.containsKey(dimensionId)) {
         dimensionId = "Failed requirement."
         throw new IllegalArgumentException(dimensionId.toString())
      }

      this.j.add(dimensionId)
      this.e.remove(dimensionId)
      DL var2 = this.y
      this.y.a.remove(dimensionId)
   }

   @Override
   public final void onDisconnect(VsiPlayer player) {
      Intrinsics.checkNotNullParameter(player, "")
      if (this.K != null) {
         VsiPlayer var2 = player
         Fj var3 = this.K
         Intrinsics.checkNotNullParameter(var2, "")
         var3.b.inverse().remove(var2)
         ((ObjectCollection)var3.c.values()).remove(var2)
         var3.d.remove(var2)
      }
   }

   @Override
   public final void deleteShip(ServerShip ship) {
      Intrinsics.checkNotNullParameter(ship, "")
      if (this.I.contains(ship.getId())) {
         Es var2
         ShipData var3 = ship instanceof Es ? (var2 = (Es)ship).b : (ShipData)ship
         this.p.add(var3)
         this.I.remove(ship.getId())
      }

      this.a.remove(ship.getId())
      this.x.remove(ship.getId())
   }

   @Override
   public final void teleportShip(ServerShip ship, ShipTeleportData teleportData) {
      Intrinsics.checkNotNullParameter(ship, "")
      Intrinsics.checkNotNullParameter(teleportData, "")
      Es var3 = (Es)this.getLoadedShips().getById(ship.getId())
      String var4
      boolean var5 = (var4 = teleportData.getNewDimension()) != null && !Intrinsics.areEqual(ship.getChunkClaimDimension(), var4)
      if (var3 != null) {
         var3.teleportShip(teleportData)
         if (var5) {
            VsiGameServer var10000 = this.G
            Intrinsics.checkNotNull(this.G)
            IShipActiveChunksSet var10001 = ship.getActiveChunksSet()
            String var10002 = ship.getChunkClaimDimension()
            Intrinsics.checkNotNull(var4)
            var10000.moveTerrainAcrossDimensions(var10001, var10002, var4)
            this.I.remove(ship.getId())
            this.p.add(var3.b)
            ship.getActiveChunksSet().forEach(Et::a)
            ((ShipInternal)ship).setChunkClaimDimension(var4)
            return
         }
      } else {
         Ship var8 = this.a.getById(ship.getId())
         Intrinsics.checkNotNull(var8)
         ShipData var7
         (var7 = (ShipData)var8).setKinematics(teleportData.createNewShipTransform(ship.getTransform()))
         var7.setNeedsUpdating(true)
         if (var5) {
            VsiGameServer var9 = this.G
            Intrinsics.checkNotNull(this.G)
            IShipActiveChunksSet var10 = ship.getActiveChunksSet()
            String var11 = ship.getChunkClaimDimension()
            Intrinsics.checkNotNull(var4)
            var9.moveTerrainAcrossDimensions(var10, var11, var4)
            var7.setChunkClaimDimension(var4)
         }
      }
   }

   @Override
   public final void teleportPhysicsEntity(PhysicsEntityServer physicsEntityServer, ShipTeleportData teleportData) {
      Intrinsics.checkNotNullParameter(physicsEntityServer, "")
      Intrinsics.checkNotNullParameter(teleportData, "")
      int var3 = physicsEntityServer.getShipTeleportId()
      physicsEntityServer.setShipTeleportId(var3 + 1)
      BodyTransform var10001 = teleportData.createNewShipTransform(physicsEntityServer.getShipTransform()).getTransform()
      Intrinsics.checkNotNull(var10001)
      physicsEntityServer.setShipTransform((ShipTransform)var10001)
      physicsEntityServer.setLinearVelocity(teleportData.getNewVel())
      physicsEntityServer.setAngularVelocity(teleportData.getNewOmega())
      physicsEntityServer.setNeedsUpdating(true)
   }

   private static final boolean a(Function1 var0, Object var1) {
      return (Boolean)var0.invoke(var1)
   }

   private static final void b(Function1 var0, Object var1) {
      var0.invoke(var1)
   }

   private static final void a(Es var0, int var1, int var2) {
      var0.onUnloadChunk(var1, var2)
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final VsiQueryableShipData getAllShips() {
      return this.a
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final QueryableShipData getAllShips() {
      return this.a
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final QueryableShipData getLoadedShips() {
      return this.getLoadedShips()
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final AerodynamicUtils getAerodynamicUtils() {
      return this.y
   }

   // $VF: synthetic method
   @Override
   public final ServerShip createNewShipAtBlock(Vector3ic blockPosInWorldCoordinates, boolean createShipObjectImmediately, double scaling, String dimensionId) {
      return this.b(blockPosInWorldCoordinates, createShipObjectImmediately, scaling, dimensionId)
   }

   static {
      KProperty[] var0
      (var0 = new KProperty[1])[0] = Reflection.property1(
         (PropertyReference1)(new PropertyReference1Impl(Et.class, "vsCore", "getVsCore()Lorg/valkyrienskies/core/impl/program/VSCoreInternal;", 0))
      )
      z = var0
   }

   public final class a {
      public final Collection<Es> a;
      public final Collection<PhysicsEntityServer> b;
      public final Collection<Es> c;
      public final Collection<PhysicsEntityServer> d;
      public final Collection<ShipData> e;
      public final Collection<Long> f;
      public final Long2ObjectMap<? extends BlockPos2ObjectOpenHashMap<Ip>> g;
      public final Collection<String> h;
      public final Collection<String> i;
      public final Collection<VSJointAndId> j;
      public final Collection<VSJointAndId> k;
      public final Collection<Integer> l;
      public final List<FB> m;
      public final List<FB> n;

      public a(
         Collection<Es> var2,
         Collection<PhysicsEntityServer> var3,
         Collection<Es> var4,
         Collection<PhysicsEntityServer> var5,
         Collection<ShipData> var6,
         Collection<Long> var7,
         Long2ObjectMap<? extends BlockPos2ObjectOpenHashMap<Ip>> var8,
         Collection<String> var9,
         Collection<String> var10,
         Collection<VSJointAndId> var11,
         Collection<VSJointAndId> var12,
         Collection<Integer> var13,
         List<FB> var14,
         List<FB> var15
      ) {
         Intrinsics.checkNotNullParameter(var2, "")
         Intrinsics.checkNotNullParameter(var3, "")
         Intrinsics.checkNotNullParameter(var4, "")
         Intrinsics.checkNotNullParameter(var5, "")
         Intrinsics.checkNotNullParameter(var6, "")
         Intrinsics.checkNotNullParameter(var7, "")
         Intrinsics.checkNotNullParameter(var8, "")
         Intrinsics.checkNotNullParameter(var9, "")
         Intrinsics.checkNotNullParameter(var10, "")
         Intrinsics.checkNotNullParameter(var11, "")
         Intrinsics.checkNotNullParameter(var12, "")
         Intrinsics.checkNotNullParameter(var13, "")
         Intrinsics.checkNotNullParameter(var14, "")
         Intrinsics.checkNotNullParameter(var15, "")
         super()
         this.a = var2
         this.b = var3
         this.c = var4
         this.d = var5
         this.e = var6
         this.f = var7
         this.g = var8
         this.h = var9
         this.i = var10
         this.j = var11
         this.k = var12
         this.l = var13
         this.m = var14
         this.n = var15
      }

      public final Collection<Es> a() {
         return this.a
      }

      public final Collection<PhysicsEntityServer> b() {
         return this.b
      }

      public final Collection<Es> c() {
         return this.c
      }

      public final Collection<PhysicsEntityServer> d() {
         return this.d
      }

      private Collection<ShipData> m() {
         return this.e
      }

      private Collection<Long> n() {
         return this.f
      }

      public final Long2ObjectMap<? extends BlockPos2ObjectOpenHashMap<Ip>> e() {
         return this.g
      }

      private Collection<String> o() {
         return this.h
      }

      private Collection<String> p() {
         return this.i
      }

      public final Collection<VSJointAndId> f() {
         return this.j
      }

      public final Collection<VSJointAndId> g() {
         return this.k
      }

      public final Collection<Integer> h() {
         return this.l
      }

      public final List<FB> i() {
         return this.m
      }

      public final List<FB> j() {
         return this.n
      }

      public final List<Pair<String, Long>> k() {
         List var1 = (Iterable)this.h
         Et var2 = Et.this
         Iterable var3 = var1
         var1 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var1, 10))

         for (Object var4 : var3) {
            String var8 = (String)var4
            ArrayList var5 = var1
            Object var10003 = var2.h.get(var8)
            Intrinsics.checkNotNull(var10003)
            var5.add(new Pair(var8, var10003))
         }

         return var1
      }

      public final List<Long> l() {
         Iterable var1 = this.i
         Et var2 = Et.this
         Iterable var3 = var1
         var1 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var1, 10))

         for (Object var5 : var3) {
            String var13 = (String)var5
            ArrayList var6 = var1
            Object var10000 = var2.h.get(var13)
            Intrinsics.checkNotNull(var10000)
            var6.add(((Number)var10000).longValue())
         }

         List var8
         Collection var18 = var8 = var1
         var1 = this.e
         Collection var17 = var18
         Iterable var11 = var1
         var3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var1, 10))

         for (Object var15 : var11) {
            ShipData var16 = (ShipData)var15
            ArrayList var12 = var3
            var12.add(var16.getId())
         }

         return CollectionsKt.plus(CollectionsKt.plus(var17, (Iterable & List)var3), this.f)
      }
   }

   public static final class b {
      final String a;
      final List<Ip> b;

      public b(String var1, List<? extends Ip> var2) {
         Intrinsics.checkNotNullParameter(var1, "")
         Intrinsics.checkNotNullParameter(var2, "")
         super()
         this.a = var1
         this.b = var2
      }

      private String c() {
         return this.a
      }

      private List<Ip> d() {
         return this.b
      }

      public final String a() {
         return this.a
      }

      public final List<Ip> b() {
         return this.b
      }

      private static Et.b a(String var0, List<? extends Ip> var1) {
         Intrinsics.checkNotNullParameter(var0, "")
         Intrinsics.checkNotNullParameter(var1, "")
         return new Et.b(var0, var1)
      }

      // $VF: synthetic method
      private static Et.b a(Et.b var0, String var1, List var2, int var3) {
         if ((var3 & 1) != 0) {
            var1 = var0.a
         }

         if ((var3 & 2) != 0) {
            var2 = var0.b
         }

         List var5 = var2
         String var4 = var1
         Intrinsics.checkNotNullParameter(var1, "")
         Intrinsics.checkNotNullParameter(var5, "")
         return new Et.b(var4, var5)
      }

      @Override
      public final String toString() {
         return "LevelVoxelUpdates(dimensionId=" + this.a + ", updates=" + this.b + ")"
      }

      @Override
      public final int hashCode() {
         return this.a.hashCode() * 31 + this.b.hashCode()
      }

      @Override
      public final boolean equals(Object other) {
         if (this == other) {
            return true
         } else if (!(other instanceof Et.b var2)) {
            return false
         } else {
            return !Intrinsics.areEqual(this.a, var2.a) ? false : Intrinsics.areEqual(this.b, var2.b)
         }
      }
   }

   @Metadata(
      mv = {1, 8, 0},
      k = 1,
      xi = 48,
      d1 = "\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\r\b\u0082\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000e",
      d2 = {
            "Lorg/valkyrienskies/core/impl/shadow/Et$c;",
            "",
            "<init>",
            "(Ljava/lang/String;I)V",
            "PRE_TICK",
            "POST_TICK_START",
            "POST_TICK_GENERATED",
            "POST_TICK_FINISH",
            "GET_CURRENT_TICK_CHANGES",
            "GET_LAST_TICK_CHANGES",
            "UPDATE_DIMENSIONS",
            "UPDATE_BLOCKS",
            "UPDATE_CHUNKS",
            "UPDATE_PLAYERS",
            "CLEAR_FOR_RESET"
      }
   )
   public enum c {
      PRE_TICK,
      POST_TICK_START,
      POST_TICK_GENERATED,
      POST_TICK_FINISH,
      GET_CURRENT_TICK_CHANGES,
      GET_LAST_TICK_CHANGES,
      UPDATE_DIMENSIONS,
      UPDATE_BLOCKS,
      UPDATE_CHUNKS,
      UPDATE_PLAYERS,
      CLEAR_FOR_RESET;

      // $VF: synthetic method
      private static final Et.c[] a() {
         Et.c[] var0
         (var0 = new Et.c[11])[0] = PRE_TICK
         var0[1] = POST_TICK_START
         var0[2] = POST_TICK_GENERATED
         var0[3] = POST_TICK_FINISH
         var0[4] = GET_CURRENT_TICK_CHANGES
         var0[5] = GET_LAST_TICK_CHANGES
         var0[6] = UPDATE_DIMENSIONS
         var0[7] = UPDATE_BLOCKS
         var0[8] = UPDATE_CHUNKS
         var0[9] = UPDATE_PLAYERS
         var0[10] = CLEAR_FOR_RESET
         return var0
      }

      static {
         Et.c[] var0
         (var0 = new Et.c[11])[0] = PRE_TICK
         var0[1] = POST_TICK_START
         var0[2] = POST_TICK_GENERATED
         var0[3] = POST_TICK_FINISH
         var0[4] = GET_CURRENT_TICK_CHANGES
         var0[5] = GET_LAST_TICK_CHANGES
         var0[6] = UPDATE_DIMENSIONS
         var0[7] = UPDATE_BLOCKS
         var0[8] = UPDATE_CHUNKS
         var0[9] = UPDATE_PLAYERS
         var0[10] = CLEAR_FOR_RESET
      }
   }
}
