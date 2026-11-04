package org.valkyrienskies.mod.common.util

import java.lang.ref.WeakReference
import java.util.HashSet
import java.util.UUID
import net.minecraft.class_1297
import net.minecraft.class_1657
import net.minecraft.class_1937
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.LoadedShip
import org.valkyrienskies.core.internal.world.VsiPlayer
import org.valkyrienskies.core.internal.world.VsiPlayerState
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.entity.ShipMountedToData

public class MinecraftPlayer(playerObject: class_1657) : VsiPlayer {
   public open val uuid: UUID
   public final val playerEntityReference: WeakReference<class_1657>
   public open val forceWatchingShips: HashSet<Long>

   init {
      val var10001: UUID = playerObject.method_5667()
      this.uuid = var10001
      this.playerEntityReference = WeakReference<>(playerObject)
      this.forceWatchingShips = HashSet<>()
   }

   public final val player: class_1657
      public final get() {
         val var10000: Any = this.playerEntityReference.get()
         return var10000 as class_1657
      }


   public open val isAdmin: Boolean
      public open get() {
         return this.player.method_5687(4)
      }


   public open val canModifyServerConfig: Boolean
      public open get() {
         return vsCore.hooks.isPhysicalClient() || this.player.method_5687(4)
      }


   public open val dimension: String
      public open get() {
         val var10000: class_1937 = this.player.method_37908()
         return dimensionId
      }


   public override fun getPosition(dest: Vector3d): Vector3d {
      val var10000: Vector3d = dest.set(this.player.method_23317(), this.player.method_23318(), this.player.method_23321())
      return var10000
   }

   public override fun getPlayerState(): VsiPlayerState {
      var mountedShipAndPos: ShipMountedToData
      var var2: java.lang.Long
      var var10000: VsiPlayerState
      var var10002: Vector3dc
      var var10003: Vector3dc
      var var10004: java.lang.String
      run label23@{
         mountedShipAndPos = VSGameUtilsKt.getShipMountedToData$default(this.player as class_1297, null, 2, null)
         var10000 = VsiPlayerState
         var10002 = Vector3d(this.player.method_23317(), this.player.method_23318(), this.player.method_23321())
         var10003 = Vector3d(
            Vector3d(
               this.player.method_23317() - this.player.field_6014,
               this.player.method_23318() - this.player.field_6036,
               this.player.method_23321() - this.player.field_5969
            )
         )
         var10004 = this.dimension
         if (mountedShipAndPos != null) {
            val var10005: LoadedShip = mountedShipAndPos.shipMountedTo
            if (var10005 != null) {
               var2 = var10005.getId()
               return@label23
            }
         }

         var2 = null
      }

      var10000./* $VF: Unable to resugar constructor */<init>(
         var10002, var10003, var10004, var2, if (mountedShipAndPos != null) mountedShipAndPos.mountPosInShip else null
      )
      return var10000
   }

   public override fun hashCode(): Int {
      return this.uuid.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      return super.equals(other) || other is MinecraftPlayer && this.uuid == (other as MinecraftPlayer).uuid
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun getForceWatchingShips(): java.util.Set {
      this.forceWatchingShips as java.util.Set
   }
}
