package org.valkyrienskies.core.impl.shadow;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import javax.inject.Inject;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference0;
import kotlin.jvm.internal.MutablePropertyReference0Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KMutableProperty0;
import org.valkyrienskies.core.impl.api.ServerShipInternal;
import org.valkyrienskies.core.impl.game.ChunkAllocatorProvider;
import org.valkyrienskies.core.impl.game.ships.ShipData;
import org.valkyrienskies.core.internal.physics.PhysicsEntityServer;
import org.valkyrienskies.core.internal.world.VsiPlayer;
import org.valkyrienskies.core.internal.world.chunks.VsiChunkUnwatchTask;
import org.valkyrienskies.core.internal.world.chunks.VsiChunkWatchTask;
import org.valkyrienskies.core.internal.world.chunks.VsiChunkWatchTasks;

public final class EB {
   public final ER a;
   public final CY b;
   public final ChunkAllocatorProvider c;
   public final Fy d;
   public final Gi<EB.a> e;
   public Iterable<? extends VsiChunkWatchTask> f;
   public Iterable<? extends VsiChunkUnwatchTask> g;
   public VsiChunkWatchTasks h;
   public Fx i;

   @Inject
   public EB(ER var1, CY var2, ChunkAllocatorProvider var3, Fy var4) {
      Intrinsics.checkNotNullParameter(var1, "")
      Intrinsics.checkNotNullParameter(var2, "")
      Intrinsics.checkNotNullParameter(var3, "")
      Intrinsics.checkNotNullParameter(var4, "")
      super()
      this.a = var1
      this.b = var2
      this.c = var3
      this.d = var4
      this.e = Gl.a(EB.a.PRE_TICK, <unrepresentable>.a)
   }

   public final ImmutableMap<VsiPlayer, ImmutableSet<ServerShipInternal>> a() {
      ER var1 = this.a
      return this.a.g
   }

   private static KMutableProperty0 a(EB var0) {
      return Reflection.mutableProperty0(
         (MutablePropertyReference0)(
            new MutablePropertyReference0Impl(
               var0.a, ER.class, "playersToTrackedShips", "getPlayersToTrackedShips()Lcom/google/common/collect/ImmutableMap;", 0
            )
         )
      )
   }

   public final ImmutableMap<VsiPlayer, ImmutableSet<PhysicsEntityServer>> b() {
      ER var1 = this.a
      return this.a.h
   }

   private static KMutableProperty0 b(EB var0) {
      return Reflection.mutableProperty0(
         (MutablePropertyReference0)(
            new MutablePropertyReference0Impl(
               var0.a, ER.class, "playersToTrackedPhysicsEntities", "getPlayersToTrackedPhysicsEntities()Lcom/google/common/collect/ImmutableMap;", 0
            )
         )
      )
   }

   public final VsiChunkWatchTasks c() {
      if (this.h != null) {
         return this.h
      }

      Intrinsics.throwUninitializedPropertyAccessException("")
      return null
   }

   public final void a(Set<? extends VsiPlayer> var1, Set<? extends VsiPlayer> var2, Iterable<ShipData> var3, Iterable<ShipData> var4) {
      Intrinsics.checkNotNullParameter(var1, "")
      Intrinsics.checkNotNullParameter(var2, "")
      Intrinsics.checkNotNullParameter(var3, "")
      Intrinsics.checkNotNullParameter(var4, "")
      this.e.a(EB.a.PRE_TICK)
      this.h = this.b.a(var1, var2, var3, var4)
   }

   public final void b(Set<? extends VsiPlayer> var1, Set<? extends VsiPlayer> var2, Iterable<PhysicsEntityServer> var3, Iterable<Long> var4) {
      Intrinsics.checkNotNullParameter(var1, "")
      Intrinsics.checkNotNullParameter(var2, "")
      Intrinsics.checkNotNullParameter(var3, "")
      Intrinsics.checkNotNullParameter(var4, "")
      this.e.a(EB.a.POST_TICK)
      this.i = this.d.a(var1, var2, var3, var4)
      Iterable var10001 = this.f
      if (this.f == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var10001 = null
      }

      Iterable var10002 = this.g
      if (this.g == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var10002 = null
      }

      CR var5 = this.b.a(var10001, var10002)
      var10001 = var1
      Fx var10003 = this.i
      if (this.i == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var10003 = null
      }

      this.a.a(var10001, var5, var10003)
   }

   public final void a(Iterable<? extends VsiChunkWatchTask> var1, Iterable<? extends VsiChunkUnwatchTask> var2) {
      Intrinsics.checkNotNullParameter(var1, "")
      Intrinsics.checkNotNullParameter(var2, "")
      this.e.a(EB.a.SET_EXECUTED)
      this.f = var1
      this.g = var2
   }

   public final Iterator<VsiPlayer> a(int var1, int var2, String var3) {
      Intrinsics.checkNotNullParameter(var3, "")
      if (this.c.forDimension(var3).isChunkInShipyard(var1, var2)) {
         return this.b.a(var1, var2, var3).iterator()
      }

      Iterator var10000 = Collections.emptyIterator()
      Intrinsics.checkNotNullExpressionValue(var10000, "")
      return var10000
   }

   @Metadata(
      mv = {1, 8, 0},
      k = 1,
      xi = 48,
      d1 = "\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0082\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006",
      d2 = {"Lorg/valkyrienskies/core/impl/shadow/EB$a;", "", "<init>", "(Ljava/lang/String;I)V", "PRE_TICK", "SET_EXECUTED", "POST_TICK"}
   )
   public enum a {
      PRE_TICK,
      SET_EXECUTED,
      POST_TICK;

      // $VF: synthetic method
      private static final EB.a[] a() {
         EB.a[] var0
         (var0 = new EB.a[3])[0] = PRE_TICK
         var0[1] = SET_EXECUTED
         var0[2] = POST_TICK
         return var0
      }

      static {
         EB.a[] var0
         (var0 = new EB.a[3])[0] = PRE_TICK
         var0[1] = SET_EXECUTED
         var0[2] = POST_TICK
      }
   }
}
