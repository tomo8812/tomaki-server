package org.valkyrienskies.core.impl.networking.impl

import org.valkyrienskies.core.impl.game.ships.ShipDataCommon
import org.valkyrienskies.core.impl.networking.simple.SimplePacket

public data class PacketShipDataCreate(toCreate: List<ShipDataCommon>) : SimplePacket {
   public final val toCreate: List<ShipDataCommon>

   init {
      this.toCreate = toCreate
   }

   public operator fun component1(): List<ShipDataCommon> {
      return this.toCreate
   }

   public fun copy(toCreate: List<ShipDataCommon> = this.toCreate): PacketShipDataCreate {
      return PacketShipDataCreate(toCreate)
   }

   public override fun toString(): String {
      return "PacketShipDataCreate(toCreate=${this.toCreate})"
   }

   public override fun hashCode(): Int {
      return this.toCreate.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label22@
      if (this === other) {
         return true
      } else {
         return other is PacketShipDataCreate && this.toCreate == (other as PacketShipDataCreate).toCreate
      }
   }
}
