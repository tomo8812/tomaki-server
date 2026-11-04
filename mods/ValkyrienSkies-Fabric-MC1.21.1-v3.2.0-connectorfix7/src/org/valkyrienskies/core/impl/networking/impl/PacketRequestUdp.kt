package org.valkyrienskies.core.impl.networking.impl

import java.util.Arrays
import org.valkyrienskies.core.impl.networking.simple.SimplePacket

public data class PacketRequestUdp(udpV: Int, secretKeyBytes: ByteArray) : SimplePacket {
   public final val udpV: Int
   public final val secretKeyBytes: ByteArray

   init {
      this.udpV = udpV
      this.secretKeyBytes = secretKeyBytes
   }

   public operator fun component1(): Int {
      return this.udpV
   }

   public operator fun component2(): ByteArray {
      return this.secretKeyBytes
   }

   public fun copy(udpV: Int = this.udpV, secretKeyBytes: ByteArray = this.secretKeyBytes): PacketRequestUdp {
      return PacketRequestUdp(udpV, secretKeyBytes)
   }

   public override fun toString(): String {
      return "PacketRequestUdp(udpV=${this.udpV}, secretKeyBytes=${Arrays.toString(this.secretKeyBytes)})"
   }

   public override fun hashCode(): Int {
      return Integer.hashCode(this.udpV) * 31 + Arrays.hashCode(this.secretKeyBytes)
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is PacketRequestUdp && this.udpV == (other as PacketRequestUdp).udpV && this.secretKeyBytes == (other as PacketRequestUdp).secretKeyBytes
      }
   }
}
