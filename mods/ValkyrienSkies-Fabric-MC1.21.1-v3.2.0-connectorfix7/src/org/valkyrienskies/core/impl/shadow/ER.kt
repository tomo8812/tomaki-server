package org.valkyrienskies.core.impl.shadow

import com.fasterxml.jackson.databind.JsonNode
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.google.common.collect.ImmutableMap.Builder
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.util.ArrayList
import java.util.UUID
import java.util.Map.Entry
import javax.inject.Inject
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KProperty
import org.apache.logging.log4j.Logger
import org.valkyrienskies.core.impl.api.ServerShipInternal
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon
import org.valkyrienskies.core.impl.networking.impl.PacketPhysEntityCreate
import org.valkyrienskies.core.impl.networking.impl.PacketPhysEntityRemove
import org.valkyrienskies.core.impl.networking.impl.PacketShipDataCreate
import org.valkyrienskies.core.impl.networking.impl.PacketShipRemove
import org.valkyrienskies.core.impl.networking.impl.PhysEntityCreateData
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking
import org.valkyrienskies.core.impl.util.serialization.VSJacksonUtil
import org.valkyrienskies.core.internal.physics.PhysicsEntityServer
import org.valkyrienskies.core.internal.world.VsiPlayer
import org.valkyrienskies.core.util.VSCoreUtilKt

public class ER @Inject  public constructor(p0: dT<Et>, p1: Fb, p2: Fb, p3: SimplePacketNetworking, p4: Fe) {
   @JvmStatic
   @JvmSynthetic
   private KProperty<Object>[] i;
   private final val h: Fb
   private final val j: Fb
   private final val g: SimplePacketNetworking
   private final val a: Fe

   private final val b: Et
      private final get() {
         return FZ.a(this.l, i[0])
      }


   private final lateinit var d: Iterable<VsiPlayer>
   private final lateinit var i: CR
   private final lateinit var c: Fx
   public final var f: ImmutableMap<VsiPlayer, ImmutableSet<ServerShipInternal>>
   public final var e: ImmutableMap<VsiPlayer, ImmutableSet<PhysicsEntityServer>>

   init {
      this.j = var2
      this.k = var3
      this.b = var4
      this.c = var5
      this.l = var1
      var var10001: ImmutableMap = ImmutableMap.of()
      this.g = var10001
      var10001 = ImmutableMap.of()
      this.h = var10001
      val var6: Fb = this.j
      this.j.b = true
      val var7: Fb = this.k
      this.k.b = true
   }

   public fun a(p0: Iterable<VsiPlayer>, p1: CR, p2: Fx) {
      this.d = var1
      this.e = var2
      this.f = var3
      this.h()
      this.g()
      this.e()
      this.f()
   }

   private fun VsiPlayer.a(): Iterable<ServerShipInternal> {
      var var10000: CR = this.e
      if (this.e == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var10000 = null
      }

      return var10000.a(var1)
   }

   private fun e() {
      val var1: Builder = ImmutableMap.builder()
      var var10000: CR = this.e
      if (this.e == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var10000 = null
      }

      val var2: java.util.Iterator = var10000.a.entrySet().iterator()

      while (var2.hasNext()) {
         val var3: Entry
         var1.put((var3 = var2.next() as Entry).getKey() as VsiPlayer, VSCoreUtilKt.toImmutableSet((var3.getValue() as java.util.Map).keySet()))
      }

      val var10001: ImmutableMap = var1.build()
      this.g = var10001
   }

   private fun f() {
      val var1: Builder = ImmutableMap.builder()
      var var10000: Fx = this.f
      if (this.f == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var10000 = null
      }

      var var3: java.util.Iterator = var10000.a.entrySet().iterator()

      while (var3.hasNext()) {
         val var2: Entry
         val var4: VsiPlayer = (var2 = var3.next() as Entry).getKey() as VsiPlayer
         val var22: java.util.Set = var2.getValue() as java.util.Set
         val var5: com.google.common.collect.ImmutableSet.Builder = ImmutableSet.builder()
         val var24: java.util.Iterator = var22.iterator()

         while (var24.hasNext()) {
            val var17: PhysicsEntityServer
            if ((var17 = this.b.g.get((var24.next() as java.lang.Number).longValue())) != null) {
               var5.add(var17)
            }
         }

         var1.put(var4, var5.build())
      }

      val var10001: ImmutableMap = var1.build()
      this.h = var10001
      var var33: java.lang.Iterable = this.d
      if (this.d == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var33 = null
      }

      for (var18 in var33) {
         var10000 = this.f
         if (this.f == null) {
            Intrinsics.throwUninitializedPropertyAccessException("")
            var10000 = null
         }

         var var35: java.util.Set = var10000.c.get(var18)
         if (var35 == null) {
            var35 = SetsKt.emptySet()
         }

         this.c(var18, var35)
      }

      var10000 = this.f
      if (this.f == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var10000 = null
      }

      var3 = var10000.b.entrySet().iterator()

      while (var3.hasNext()) {
         val var26: Entry
         val var30: VsiPlayer = (var26 = var3.next() as Entry).getKey() as VsiPlayer
         val var31: java.lang.Iterable
         val var19: java.lang.Iterable = var31 = var26.getValue() as java.util.Set
         val var27: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var31, 10))

         for (var15 in var19) {
            val var37: Any = this.b.g.get((var15 as java.lang.Number).longValue())
            var27.add(var37 as PhysicsEntityServer)
         }

         this.d(var30, var27)
      }
   }

   private fun g() {
      var var10000: java.lang.Iterable = this.d
      if (this.d == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var10000 = null
      }

      for (var2 in var10000) {
         var var8: CR = this.e
         if (this.e == null) {
            Intrinsics.throwUninitializedPropertyAccessException("")
            var8 = null
         }

         var10000 = var8.c.get(var2)
         if (var10000 == null) {
            var10000 = SetsKt.emptySet()
         }

         var var10003: CR = this.e
         if (this.e == null) {
            Intrinsics.throwUninitializedPropertyAccessException("")
            var10003 = null
         }

         this.a(var2, SetsKt.plus(var10000, var10003.d))
         this.b(var2)
      }

      var var10: CR = this.e
      if (this.e == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var10 = null
      }

      val var7: java.util.Iterator = var10.b.entrySet().iterator()

      while (var7.hasNext()) {
         val var4: Entry
         this.b((var4 = var7.next() as Entry).getKey() as VsiPlayer, var4.getValue() as java.util.Set)
      }
   }

   private fun b(p0: VsiPlayer) {
      var var10000: CR = this.e
      if (this.e == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var10000 = null
      }

      if (var10000.e.contains(var1)) {
         var10000 = this.e
         if (this.e == null) {
            Intrinsics.throwUninitializedPropertyAccessException("")
            var10000 = null
         }

         val var2: java.util.Collection
         if ((var2 = var10000.b.get(var1)) == null || var2.isEmpty()) {
            this.b.sendToClient(PacketShipDataCreate(CollectionsKt.emptyList()), var1)
         }
      }
   }

   private fun a(p0: VsiPlayer, p1: Iterable<ServerShipInternal>) {
      val var6: java.lang.Iterable
      val var3: java.lang.Iterable = var6 = var2
      var2 = ArrayList(CollectionsKt.collectionSizeOrDefault(var6, 10))

      for (var4 in var3) {
         var2.add((var4 as ServerShipInternal).getId())
      }

      if (!(var2 = var2).isEmpty()) {
         ER.a.a(a).debug("{} unwatched ships {}", var1.uuid, var2)
         this.b.sendToClient(PacketShipRemove(var2), var1)
      }
   }

   private fun b(p0: VsiPlayer, p1: Iterable<ServerShipInternal>) {
      val var11: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var2, 10))

      for (var5 in var2) {
         var11.add((var5 as ServerShipInternal).asShipDataCommon())
      }

      if (!(var2 = var11 as java.util.List).isEmpty()) {
         val var10000: Logger = ER.a.a(a)
         val var10002: UUID = var1.uuid
         var var12: java.util.List = var2
         var12 = ArrayList(CollectionsKt.collectionSizeOrDefault(var2, 10))

         for (var18 in var12) {
            var12.add((var18 as ShipDataCommon).id)
         }

         var10000.debug("{} watched ships: {}", var10002, var12)
         this.b.sendToClient(PacketShipDataCreate(var2), var1)
      }
   }

   private fun c(p0: VsiPlayer, p1: Iterable<Long>) {
      if (var2.iterator().hasNext()) {
         ER.a.a(a).debug("{} unwatched physics entities {}", var1.uuid, var2)
         this.b.sendToClient(PacketPhysEntityRemove(CollectionsKt.toList(var2)), var1)
      }
   }

   private fun d(p0: VsiPlayer, p1: Iterable<PhysicsEntityServer>) {
      val var11: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var2, 10))

      for (var5 in var2) {
         var11.add(
            PhysEntityCreateData(
               (var5 as PhysicsEntityServer).id,
               (var5 as PhysicsEntityServer).dimensionId,
               (var5 as PhysicsEntityServer).shipTransform,
               (var5 as PhysicsEntityServer).collisionShapeData
            )
         )
      }

      if (!(var2 = var11 as java.util.List).isEmpty()) {
         val var10000: Logger = ER.a.a(a)
         val var10002: UUID = var1.uuid
         var var12: java.util.List = var2
         var12 = ArrayList(CollectionsKt.collectionSizeOrDefault(var2, 10))

         for (var18 in var12) {
            var12.add((var18 as PhysEntityCreateData).id)
         }

         var10000.debug("{} watched physics entities: {}", var10002, var12)
         this.b.sendToClient(PacketPhysEntityCreate(var2), var1)
      }
   }

   private fun h() {
      var var10000: java.lang.Iterable = this.d
      if (this.d == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var10000 = null
      }

      for (var2 in var10000) {
         val var3: ByteBuf = Unpooled.buffer()
         var var22: CR = this.e
         if (this.e == null) {
            Intrinsics.throwUninitializedPropertyAccessException("")
            var22 = null
         }

         var10000 = var22.b.get(var2)
         if (var10000 == null) {
            var10000 = SetsKt.emptySet()
         }

         val var4: java.util.Set = var10000
         var var5: java.lang.Iterable = this.a(var2)
         var var6: java.util.Collection = ArrayList()
         val var13: java.util.Iterator = var5.iterator()

         while (var13.hasNext()) {
            val var7: Any
            if (!var4.contains((var7 = var13.next()) as ServerShipInternal)) {
               var6.add(var7)
            }
         }

         var5 = var6 as java.util.List
         var6 = ArrayList()

         for (var9 in var5) {
            val var24: Es = this.b.a(var9)
            if (var24 != null) {
               var6.add(var24)
            }
         }

         val var11: java.util.List
         if (!(var11 = var6 as java.util.List).isEmpty()) {
            for (var12 in var5 = var11) {
               var3.writeLong(var12.b.id)
               val var20: JsonNode = VSJacksonUtil.INSTANCE.deltaMapper.valueToTree(var12.b)
               val var25: Fs = var12.d
               var25.a(var20, var3)
            }

            val var19: Fe = this.c
            val var26: Fd = this.c.c
            var26.a(var3, var2)
         }
      }
   }

   public companion object a {
      @JvmStatic
      @JvmSynthetic
      private KProperty<Object>[] a;

      private final val a: Logger
         private final get() {
            val var10000: Logger = ER.m
            return var10000
         }


      @JvmStatic
      @JvmSynthetic
      fun a(var0: ER.a): Logger {
         val var10000: Logger = ER.m
         var10000
      }

      @JvmStatic
      fun {
         val var0: Array<KProperty>
         (var0 = arrayOfNulls(1))[0] = Reflection.property1(
            PropertyReference1Impl(ER.a.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
         )
         a = var0
      }
   }
}
