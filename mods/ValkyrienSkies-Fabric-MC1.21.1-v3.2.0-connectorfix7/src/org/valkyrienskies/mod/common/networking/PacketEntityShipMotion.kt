package org.valkyrienskies.mod.common.networking

import org.valkyrienskies.core.impl.networking.simple.SimplePacket

public data class PacketEntityShipMotion(entityID: Int,
      shipID: Long,
      x: Double,
      y: Double,
      z: Double,
      xVel: Double,
      yVel: Double,
      zVel: Double,
      yRot: Double,
      xRot: Double
   ) :
   SimplePacket {
   public final val entityID: Int
   public final val shipID: Long
   public final val x: Double
   public final val y: Double
   public final val z: Double
   public final val xVel: Double
   public final val yVel: Double
   public final val zVel: Double
   public final val yRot: Double
   public final val xRot: Double

   init {
      this.entityID = entityID
      this.shipID = shipID
      this.x = x
      this.y = y
      this.z = z
      this.xVel = xVel
      this.yVel = yVel
      this.zVel = zVel
      this.yRot = yRot
      this.xRot = xRot
   }

   public operator fun component1(): Int {
      return this.entityID
   }

   public operator fun component2(): Long {
      return this.shipID
   }

   public operator fun component3(): Double {
      return this.x
   }

   public operator fun component4(): Double {
      return this.y
   }

   public operator fun component5(): Double {
      return this.z
   }

   public operator fun component6(): Double {
      return this.xVel
   }

   public operator fun component7(): Double {
      return this.yVel
   }

   public operator fun component8(): Double {
      return this.zVel
   }

   public operator fun component9(): Double {
      return this.yRot
   }

   public operator fun component10(): Double {
      return this.xRot
   }

   public fun copy(
      entityID: Int = this.entityID,
      shipID: Long = this.shipID,
      x: Double = this.x,
      y: Double = this.y,
      z: Double = this.z,
      xVel: Double = this.xVel,
      yVel: Double = this.yVel,
      zVel: Double = this.zVel,
      yRot: Double = this.yRot,
      xRot: Double = this.xRot
   ): PacketEntityShipMotion {
      return PacketEntityShipMotion(entityID, shipID, x, y, z, xVel, yVel, zVel, yRot, xRot)
   }

   public override fun toString(): String {
      return "PacketEntityShipMotion(entityID=${this.entityID}, shipID=${this.shipID}, x=${this.x}, y=${this.y}, z=${this.z}, xVel=${this.xVel}, yVel=${this.yVel}, zVel=${this.zVel}, yRot=${this.yRot}, xRot=${this.xRot})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          (
                                                   (
                                                            (
                                                                     (Integer.hashCode(this.entityID) * 31 + java.lang.Long.hashCode(this.shipID)) * 31
                                                                        + java.lang.Double.hashCode(this.x)
                                                                  )
                                                                  * 31
                                                               + java.lang.Double.hashCode(this.y)
                                                         )
                                                         * 31
                                                      + java.lang.Double.hashCode(this.z)
                                                )
                                                * 31
                                             + java.lang.Double.hashCode(this.xVel)
                                       )
                                       * 31
                                    + java.lang.Double.hashCode(this.yVel)
                              )
                              * 31
                           + java.lang.Double.hashCode(this.zVel)
                     )
                     * 31
                  + java.lang.Double.hashCode(this.yRot)
            )
            * 31
         + java.lang.Double.hashCode(this.xRot)
      }

   public override operator fun equals(other: Any?): Boolean {
      label76@
      if (this === other) {
         return true
      } else {
         return other is PacketEntityShipMotion
            && this.entityID == (other as PacketEntityShipMotion).entityID
            && this.shipID == (other as PacketEntityShipMotion).shipID
            && java.lang.Double.compare(this.x, (other as PacketEntityShipMotion).x) == 0
            && java.lang.Double.compare(this.y, (other as PacketEntityShipMotion).y) == 0
            && java.lang.Double.compare(this.z, (other as PacketEntityShipMotion).z) == 0
            && java.lang.Double.compare(this.xVel, (other as PacketEntityShipMotion).xVel) == 0
            && java.lang.Double.compare(this.yVel, (other as PacketEntityShipMotion).yVel) == 0
            && java.lang.Double.compare(this.zVel, (other as PacketEntityShipMotion).zVel) == 0
            && java.lang.Double.compare(this.yRot, (other as PacketEntityShipMotion).yRot) == 0
            && java.lang.Double.compare(this.xRot, (other as PacketEntityShipMotion).xRot) == 0
         }
   }
}
