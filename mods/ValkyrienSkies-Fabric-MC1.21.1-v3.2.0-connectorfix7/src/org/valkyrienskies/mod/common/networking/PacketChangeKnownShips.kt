package org.valkyrienskies.mod.common.networking

import org.valkyrienskies.core.impl.networking.simple.SimplePacket

public data class PacketChangeKnownShips(add: Boolean, shipID: Long) : SimplePacket {
   public final val add: Boolean
   public final val shipID: Long

   init {
      this.add = add
      this.shipID = shipID
   }

   public operator fun component1(): Boolean {
      return this.add
   }

   public operator fun component2(): Long {
      return this.shipID
   }

   public fun copy(add: Boolean = this.add, shipID: Long = this.shipID): PacketChangeKnownShips {
      return PacketChangeKnownShips(add, shipID)
   }

   public override fun toString(): String {
      return "PacketChangeKnownShips(add=${this.add}, shipID=${this.shipID})"
   }

   public override fun hashCode(): Int {
      return java.lang.Boolean.hashCode(this.add) * 31 + java.lang.Long.hashCode(this.shipID)
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is PacketChangeKnownShips && this.add == (other as PacketChangeKnownShips).add && this.shipID == (other as PacketChangeKnownShips).shipID
      }
   }
}
