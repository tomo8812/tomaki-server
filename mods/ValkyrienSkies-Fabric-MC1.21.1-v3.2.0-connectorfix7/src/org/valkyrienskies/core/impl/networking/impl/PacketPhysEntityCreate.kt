package org.valkyrienskies.core.impl.networking.impl

import org.valkyrienskies.core.impl.networking.simple.SimplePacket

public data class PacketPhysEntityCreate(toCreate: List<PhysEntityCreateData>) : SimplePacket {
   public final val toCreate: List<PhysEntityCreateData>

   init {
      this.toCreate = toCreate
   }

   public operator fun component1(): List<PhysEntityCreateData> {
      return this.toCreate
   }

   public fun copy(toCreate: List<PhysEntityCreateData> = this.toCreate): PacketPhysEntityCreate {
      return PacketPhysEntityCreate(toCreate)
   }

   public override fun toString(): String {
      return "PacketPhysEntityCreate(toCreate=${this.toCreate})"
   }

   public override fun hashCode(): Int {
      return this.toCreate.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label22@
      if (this === other) {
         return true
      } else {
         return other is PacketPhysEntityCreate && this.toCreate == (other as PacketPhysEntityCreate).toCreate
      }
   }
}
