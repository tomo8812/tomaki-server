package org.valkyrienskies.mod.common.networking

import org.valkyrienskies.core.impl.networking.simple.SimplePacket

public data class PacketRequestPassengerSeat(unused: Boolean = false) : SimplePacket {
   public final val unused: Boolean

   init {
      this.unused = unused
   }

   public operator fun component1(): Boolean {
      return this.unused
   }

   public fun copy(unused: Boolean = this.unused): PacketRequestPassengerSeat {
      return PacketRequestPassengerSeat(unused)
   }

   public override fun toString(): String {
      return "PacketRequestPassengerSeat(unused=${this.unused})"
   }

   public override fun hashCode(): Int {
      return java.lang.Boolean.hashCode(this.unused)
   }

   public override operator fun equals(other: Any?): Boolean {
      label22@
      if (this === other) {
         return true
      } else {
         return other is PacketRequestPassengerSeat && this.unused == (other as PacketRequestPassengerSeat).unused
      }
   }

   fun PacketRequestPassengerSeat() {
      this(false, 1, null)
   }
}
