package org.valkyrienskies.mod.common.networking

import org.valkyrienskies.core.impl.networking.simple.SimplePacket

public data class PacketSyncVSEntityTypes(entity2Handler: Map<Int, String>) : SimplePacket {
   public final val entity2Handler: Map<Int, String>

   init {
      this.entity2Handler = entity2Handler
   }

   public operator fun component1(): Map<Int, String> {
      return this.entity2Handler
   }

   public fun copy(entity2Handler: Map<Int, String> = this.entity2Handler): PacketSyncVSEntityTypes {
      return PacketSyncVSEntityTypes(entity2Handler)
   }

   public override fun toString(): String {
      return "PacketSyncVSEntityTypes(entity2Handler=${this.entity2Handler})"
   }

   public override fun hashCode(): Int {
      return this.entity2Handler.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label22@
      if (this === other) {
         return true
      } else {
         return other is PacketSyncVSEntityTypes && this.entity2Handler == (other as PacketSyncVSEntityTypes).entity2Handler
      }
   }
}
