package org.valkyrienskies.core.impl.game.ships

import org.joml.Quaterniondc
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.properties.PhysPose

public data class PhysPoseImpl(pos: Vector3dc, rot: Quaterniondc) : PhysPose {
   public open val pos: Vector3dc
   public open val rot: Quaterniondc

   init {
      this.pos = pos
      this.rot = rot
   }

   public operator fun component1(): Vector3dc {
      return this.pos
   }

   public operator fun component2(): Quaterniondc {
      return this.rot
   }

   public fun copy(pos: Vector3dc = this.pos, rot: Quaterniondc = this.rot): PhysPoseImpl {
      return PhysPoseImpl(pos, rot)
   }

   public override fun toString(): String {
      return "PhysPoseImpl(pos=${this.pos}, rot=${this.rot})"
   }

   public override fun hashCode(): Int {
      return this.pos.hashCode() * 31 + this.rot.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is PhysPoseImpl && this.pos == (other as PhysPoseImpl).pos && this.rot == (other as PhysPoseImpl).rot
      }
   }
}
