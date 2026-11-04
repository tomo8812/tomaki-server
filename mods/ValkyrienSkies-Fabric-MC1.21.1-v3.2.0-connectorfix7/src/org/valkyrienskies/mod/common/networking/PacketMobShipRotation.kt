package org.valkyrienskies.mod.common.networking

import org.valkyrienskies.core.impl.networking.simple.SimplePacket

public data class PacketMobShipRotation(entityID: Int, shipID: Long, yaw: Double, pitch: Double) : SimplePacket {
   public final val entityID: Int
   public final val shipID: Long
   public final val yaw: Double
   public final val pitch: Double

   init {
      this.entityID = entityID
      this.shipID = shipID
      this.yaw = yaw
      this.pitch = pitch
   }

   public operator fun component1(): Int {
      return this.entityID
   }

   public operator fun component2(): Long {
      return this.shipID
   }

   public operator fun component3(): Double {
      return this.yaw
   }

   public operator fun component4(): Double {
      return this.pitch
   }

   public fun copy(entityID: Int = this.entityID, shipID: Long = this.shipID, yaw: Double = this.yaw, pitch: Double = this.pitch): PacketMobShipRotation {
      return PacketMobShipRotation(entityID, shipID, yaw, pitch)
   }

   public override fun toString(): String {
      return "PacketMobShipRotation(entityID=${this.entityID}, shipID=${this.shipID}, yaw=${this.yaw}, pitch=${this.pitch})"
   }

   public override fun hashCode(): Int {
      return ((Integer.hashCode(this.entityID) * 31 + java.lang.Long.hashCode(this.shipID)) * 31 + java.lang.Double.hashCode(this.yaw)) * 31
         + java.lang.Double.hashCode(this.pitch)
      }

   public override operator fun equals(other: Any?): Boolean {
      label40@
      if (this === other) {
         return true
      } else {
         return other is PacketMobShipRotation
            && this.entityID == (other as PacketMobShipRotation).entityID
            && this.shipID == (other as PacketMobShipRotation).shipID
            && java.lang.Double.compare(this.yaw, (other as PacketMobShipRotation).yaw) == 0
            && java.lang.Double.compare(this.pitch, (other as PacketMobShipRotation).pitch) == 0
         }
   }
}
