package org.valkyrienskies.core.impl.networking.impl

import org.valkyrienskies.core.impl.networking.simple.SimplePacket

public data class PacketPhysEntityRemove(toRemove: List<Long>) : SimplePacket {
   public final val toRemove: List<Long>

   init {
      this.toRemove = toRemove
   }

   public operator fun component1(): List<Long> {
      return this.toRemove
   }

   public fun copy(toRemove: List<Long> = this.toRemove): PacketPhysEntityRemove {
      return PacketPhysEntityRemove(toRemove)
   }

   public override fun toString(): String {
      return "PacketPhysEntityRemove(toRemove=${this.toRemove})"
   }

   public override fun hashCode(): Int {
      return this.toRemove.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label22@
      if (this === other) {
         return true
      } else {
         return other is PacketPhysEntityRemove && this.toRemove == (other as PacketPhysEntityRemove).toRemove
      }
   }
}
