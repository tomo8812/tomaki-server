package org.valkyrienskies.mod.common.networking

import org.valkyrienskies.core.impl.networking.simple.SimplePacket

public data class PacketPlayerShipMotion(shipID: Long, x: Double, y: Double, z: Double, yRot: Double) : SimplePacket {
   public final val shipID: Long
   public final val x: Double
   public final val y: Double
   public final val z: Double
   public final val yRot: Double

   init {
      this.shipID = shipID
      this.x = x
      this.y = y
      this.z = z
      this.yRot = yRot
   }

   public operator fun component1(): Long {
      return this.shipID
   }

   public operator fun component2(): Double {
      return this.x
   }

   public operator fun component3(): Double {
      return this.y
   }

   public operator fun component4(): Double {
      return this.z
   }

   public operator fun component5(): Double {
      return this.yRot
   }

   public fun copy(shipID: Long = this.shipID, x: Double = this.x, y: Double = this.y, z: Double = this.z, yRot: Double = this.yRot): PacketPlayerShipMotion {
      return PacketPlayerShipMotion(shipID, x, y, z, yRot)
   }

   public override fun toString(): String {
      return "PacketPlayerShipMotion(shipID=${this.shipID}, x=${this.x}, y=${this.y}, z=${this.z}, yRot=${this.yRot})"
   }

   public override fun hashCode(): Int {
      return (
               ((java.lang.Long.hashCode(this.shipID) * 31 + java.lang.Double.hashCode(this.x)) * 31 + java.lang.Double.hashCode(this.y)) * 31
                  + java.lang.Double.hashCode(this.z)
            )
            * 31
         + java.lang.Double.hashCode(this.yRot)
      }

   public override operator fun equals(other: Any?): Boolean {
      label46@
      if (this === other) {
         return true
      } else {
         return other is PacketPlayerShipMotion
            && this.shipID == (other as PacketPlayerShipMotion).shipID
            && java.lang.Double.compare(this.x, (other as PacketPlayerShipMotion).x) == 0
            && java.lang.Double.compare(this.y, (other as PacketPlayerShipMotion).y) == 0
            && java.lang.Double.compare(this.z, (other as PacketPlayerShipMotion).z) == 0
            && java.lang.Double.compare(this.yRot, (other as PacketPlayerShipMotion).yRot) == 0
         }
   }
}
