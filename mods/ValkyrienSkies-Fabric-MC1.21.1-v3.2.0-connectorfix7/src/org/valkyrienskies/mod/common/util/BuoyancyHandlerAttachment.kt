package org.valkyrienskies.mod.common.util

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.annotations.NotNull
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ShipPhysicsListener
import org.valkyrienskies.core.api.world.PhysLevel
import org.valkyrienskies.mod.common.config.VSGameConfig

public class BuoyancyHandlerAttachment : ShipPhysicsListener {
   public final val buoyancyData: org.valkyrienskies.mod.common.util.BuoyancyHandlerAttachment.BuoyancyData =
      BuoyancyHandlerAttachment.BuoyancyData(0.0, null, 3, null)

   public override fun physTick(physShip: PhysShip, physLevel: PhysLevel) {
      if (VSGameConfig.SERVER.enablePocketBuoyancy) {
         if (this.buoyancyData.pocketCenterAverage.lengthSquared() != 0.0) {
            physShip.buoyantFactor = 1.0 + this.buoyancyData.pocketVolumeTotal * VSGameConfig.SERVER.buoyancyFactorPerPocketVolume
         }
      }
   }

   public data class BuoyancyData(pocketVolumeTotal: Double = 0.0, pocketCenterAverage: Vector3dc = Vector3d() as Vector3dc) {
      public final var pocketVolumeTotal: Double

      @JsonIgnore
      @NotNull
      public final var pocketCenterAverage: Vector3dc

      init {
         this.pocketVolumeTotal = pocketVolumeTotal
         this.pocketCenterAverage = pocketCenterAverage
      }

      public operator fun component1(): Double {
         return this.pocketVolumeTotal
      }

      public operator fun component2(): Vector3dc {
         return this.pocketCenterAverage
      }

      public fun copy(pocketVolumeTotal: Double = this.pocketVolumeTotal, pocketCenterAverage: Vector3dc = this.pocketCenterAverage): org.valkyrienskies.mod.common.util.BuoyancyHandlerAttachment.BuoyancyData {
         return BuoyancyHandlerAttachment.BuoyancyData(pocketVolumeTotal, pocketCenterAverage)
      }

      public override fun toString(): String {
         return "BuoyancyData(pocketVolumeTotal=${this.pocketVolumeTotal}, pocketCenterAverage=${this.pocketCenterAverage})"
      }

      public override fun hashCode(): Int {
         return java.lang.Double.hashCode(this.pocketVolumeTotal) * 31 + this.pocketCenterAverage.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is BuoyancyHandlerAttachment.BuoyancyData
               && java.lang.Double.compare(this.pocketVolumeTotal, (other as BuoyancyHandlerAttachment.BuoyancyData).pocketVolumeTotal) == 0
               && this.pocketCenterAverage == (other as BuoyancyHandlerAttachment.BuoyancyData).pocketCenterAverage
            }
      }

      fun BuoyancyData() {
         this(0.0, null, 3, null)
      }
   }
}
