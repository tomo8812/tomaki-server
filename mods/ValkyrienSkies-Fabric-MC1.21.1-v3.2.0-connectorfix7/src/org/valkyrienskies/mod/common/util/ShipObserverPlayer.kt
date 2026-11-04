package org.valkyrienskies.mod.common.util

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.jvm.internal.SourceDebugExtension
import org.joml.Vector3d
import org.valkyrienskies.core.internal.world.VsiPlayer
import org.valkyrienskies.core.internal.world.VsiPlayerState

public class ShipObserverPlayer(shipId: Long, dimensionId: String, posX: Double, posY: Double, posZ: Double, generation: Int = 0) : VsiPlayer {
   private final val shipId: Long
   private final val dimensionId: String
   private final val posX: Double
   private final val posY: Double
   private final val posZ: Double
   private final val generation: Int
   public open val uuid: UUID
   public open val forceWatchingShips: Set<Long>

   init {
      this.shipId = shipId
      this.dimensionId = dimensionId
      this.posX = posX
      this.posY = posY
      this.posZ = posZ
      this.generation = generation
      this.uuid = Companion.observerUuid(this.shipId, this.generation)
      this.forceWatchingShips = SetsKt.emptySet()
   }

   public open val isAdmin: Boolean
      public open get() {
         return false
      }


   public open val canModifyServerConfig: Boolean
      public open get() {
         return false
      }


   public open val dimension: String
      public open get() {
         return this.dimensionId
      }


   public override fun getPosition(dest: Vector3d): Vector3d {
      val var10000: Vector3d = dest.set(this.posX, this.posY, this.posZ)
      return var10000
   }

   public override fun getPlayerState(): VsiPlayerState {
      return VsiPlayerState(Vector3d(this.posX, this.posY, this.posZ), Vector3d(), this.dimensionId, null, null)
   }

   public override fun hashCode(): Int {
      return this.uuid.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is ShipObserverPlayer && (other as ShipObserverPlayer).uuid == this.uuid
   }

   @SourceDebugExtension(["SMAP\nShipObserverPlayer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShipObserverPlayer.kt\norg/valkyrienskies/mod/common/util/ShipObserverPlayer$Companion\n+ 2 MapsJVM.kt\nkotlin/collections/MapsKt__MapsJVMKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,80:1\n72#2,2:81\n1#3:83\n*S KotlinDebug\n*F\n+ 1 ShipObserverPlayer.kt\norg/valkyrienskies/mod/common/util/ShipObserverPlayer$Companion\n*L\n75#1:81,2\n75#1:83\n*E\n"])
   public companion object {
      private final val uuidCache: ConcurrentHashMap<Pair<Long, Int>, UUID>

      public fun observerUuid(shipId: Long, generation: Int = 0): UUID {
         val `$this$getOrPut$iv`: ConcurrentMap = ShipObserverPlayer.uuidCache
         val `key$iv`: Any = TuplesKt.to(shipId, generation)
         var var10000: Any = `$this$getOrPut$iv`.get(`key$iv`)
         if (var10000 == null) {
            val var10: ByteArray = ("valkyrienskies:ship-observer:$shipId:$generation").getBytes(Charsets.UTF_8)
            val `default$iv`: Any = UUID.nameUUIDFromBytes(var10)
            var10000 = `$this$getOrPut$iv`.putIfAbsent(`key$iv`, `default$iv`)
            if (var10000 == null) {
               var10000 = `default$iv`
            }
         }

         return var10000 as UUID
      }
   }
}
