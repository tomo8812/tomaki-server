package org.valkyrienskies.core.internal.world

import org.joml.Vector3dc

public data class VsiPlayerState(pos: Vector3dc, vel: Vector3dc, dimensionId: String, mountedShip: Long?, mountedPos: Vector3dc?) {
   public final val pos: Vector3dc
   public final val vel: Vector3dc
   public final val dimensionId: String
   public final val mountedShip: Long?
   public final val mountedPos: Vector3dc?

   init {
      this.pos = pos
      this.vel = vel
      this.dimensionId = dimensionId
      this.mountedShip = mountedShip
      this.mountedPos = mountedPos
   }

   public operator fun component1(): Vector3dc {
      return this.pos
   }

   public operator fun component2(): Vector3dc {
      return this.vel
   }

   public operator fun component3(): String {
      return this.dimensionId
   }

   public operator fun component4(): Long? {
      return this.mountedShip
   }

   public operator fun component5(): Vector3dc? {
      return this.mountedPos
   }

   public fun copy(
      pos: Vector3dc = this.pos,
      vel: Vector3dc = this.vel,
      dimensionId: String = this.dimensionId,
      mountedShip: Long? = this.mountedShip,
      mountedPos: Vector3dc? = this.mountedPos
   ): VsiPlayerState {
      return VsiPlayerState(pos, vel, dimensionId, mountedShip, mountedPos)
   }

   public override fun toString(): String {
      return "VsiPlayerState(pos=${this.pos}, vel=${this.vel}, dimensionId=${this.dimensionId}, mountedShip=${this.mountedShip}, mountedPos=${this.mountedPos})"
   }

   public override fun hashCode(): Int {
      return (
               ((this.pos.hashCode() * 31 + this.vel.hashCode()) * 31 + this.dimensionId.hashCode()) * 31
                  + (if (this.mountedShip == null) 0 else this.mountedShip.hashCode())
            )
            * 31
         + (if (this.mountedPos == null) 0 else this.mountedPos.hashCode())
      }

   public override operator fun equals(other: Any?): Boolean {
      label46@
      if (this === other) {
         return true
      } else {
         return other is VsiPlayerState
            && this.pos == (other as VsiPlayerState).pos
            && this.vel == (other as VsiPlayerState).vel
            && this.dimensionId == (other as VsiPlayerState).dimensionId
            && this.mountedShip == (other as VsiPlayerState).mountedShip
            && this.mountedPos == (other as VsiPlayerState).mountedPos
         }
   }
}
