package org.valkyrienskies.core.api.ships

import org.joml.Vector3dc
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.ships.properties.ShipTransform

public interface ServerShipTransformProvider {
   public abstract fun provideNextTransformAndVelocity(prevShipTransform: ShipTransform, shipTransform: ShipTransform): org.valkyrienskies.core.api.ships.ServerShipTransformProvider.NextTransformAndVelocityData? {
   }

   public data class NextTransformAndVelocityData(nextTransform: BodyTransform, nextVel: Vector3dc?, nextOmega: Vector3dc?) {
      public final val nextTransform: BodyTransform
      public final val nextVel: Vector3dc?
      public final val nextOmega: Vector3dc?

      init {
         this.nextTransform = nextTransform
         this.nextVel = nextVel
         this.nextOmega = nextOmega
      }

      public operator fun component1(): BodyTransform {
         return this.nextTransform
      }

      public operator fun component2(): Vector3dc? {
         return this.nextVel
      }

      public operator fun component3(): Vector3dc? {
         return this.nextOmega
      }

      public fun copy(nextTransform: BodyTransform = this.nextTransform, nextVel: Vector3dc? = this.nextVel, nextOmega: Vector3dc? = this.nextOmega): org.valkyrienskies.core.api.ships.ServerShipTransformProvider.NextTransformAndVelocityData {
         return ServerShipTransformProvider.NextTransformAndVelocityData(nextTransform, nextVel, nextOmega)
      }

      public override fun toString(): String {
         return "NextTransformAndVelocityData(nextTransform=${this.nextTransform}, nextVel=${this.nextVel}, nextOmega=${this.nextOmega})"
      }

      public override fun hashCode(): Int {
         return (this.nextTransform.hashCode() * 31 + (if (this.nextVel == null) 0 else this.nextVel.hashCode())) * 31
            + (if (this.nextOmega == null) 0 else this.nextOmega.hashCode())
         }

      public override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is ServerShipTransformProvider.NextTransformAndVelocityData
               && this.nextTransform == (other as ServerShipTransformProvider.NextTransformAndVelocityData).nextTransform
               && this.nextVel == (other as ServerShipTransformProvider.NextTransformAndVelocityData).nextVel
               && this.nextOmega == (other as ServerShipTransformProvider.NextTransformAndVelocityData).nextOmega
            }
      }
   }
}
