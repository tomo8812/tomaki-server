package org.valkyrienskies.mod.common.entity

import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.LoadedShip

public data class ShipMountedToData(shipMountedTo: LoadedShip, mountPosInShip: Vector3dc) {
   public final val shipMountedTo: LoadedShip
   public final val mountPosInShip: Vector3dc

   init {
      this.shipMountedTo = shipMountedTo
      this.mountPosInShip = mountPosInShip
   }

   public operator fun component1(): LoadedShip {
      return this.shipMountedTo
   }

   public operator fun component2(): Vector3dc {
      return this.mountPosInShip
   }

   public fun copy(shipMountedTo: LoadedShip = this.shipMountedTo, mountPosInShip: Vector3dc = this.mountPosInShip): ShipMountedToData {
      return ShipMountedToData(shipMountedTo, mountPosInShip)
   }

   public override fun toString(): String {
      return "ShipMountedToData(shipMountedTo=${this.shipMountedTo}, mountPosInShip=${this.mountPosInShip})"
   }

   public override fun hashCode(): Int {
      return this.shipMountedTo.hashCode() * 31 + this.mountPosInShip.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is ShipMountedToData
            && this.shipMountedTo == (other as ShipMountedToData).shipMountedTo
            && this.mountPosInShip == (other as ShipMountedToData).mountPosInShip
         }
   }
}
