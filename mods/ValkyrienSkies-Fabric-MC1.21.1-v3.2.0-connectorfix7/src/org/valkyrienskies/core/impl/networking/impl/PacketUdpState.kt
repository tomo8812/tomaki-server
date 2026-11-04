package org.valkyrienskies.core.impl.networking.impl

import org.valkyrienskies.core.impl.networking.simple.SimplePacket

public data class PacketUdpState(port: Int, state: Boolean, id: Long) : SimplePacket {
   public final val port: Int
   public final val state: Boolean
   public final val id: Long

   init {
      this.port = port
      this.state = state
      this.id = id
   }

   public operator fun component1(): Int {
      return this.port
   }

   public operator fun component2(): Boolean {
      return this.state
   }

   public operator fun component3(): Long {
      return this.id
   }

   public fun copy(port: Int = this.port, state: Boolean = this.state, id: Long = this.id): PacketUdpState {
      return PacketUdpState(port, state, id)
   }

   public override fun toString(): String {
      return "PacketUdpState(port=${this.port}, state=${this.state}, id=${this.id})"
   }

   public override fun hashCode(): Int {
      return (Integer.hashCode(this.port) * 31 + java.lang.Boolean.hashCode(this.state)) * 31 + java.lang.Long.hashCode(this.id)
   }

   public override operator fun equals(other: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is PacketUdpState
            && this.port == (other as PacketUdpState).port
            && this.state == (other as PacketUdpState).state
            && this.id == (other as PacketUdpState).id
         }
   }
}
