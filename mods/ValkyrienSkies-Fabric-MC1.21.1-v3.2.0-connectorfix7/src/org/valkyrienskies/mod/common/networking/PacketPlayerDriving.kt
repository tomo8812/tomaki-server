package org.valkyrienskies.mod.common.networking

import org.joml.Vector3f
import org.valkyrienskies.core.impl.networking.simple.SimplePacket

public data class PacketPlayerDriving(impulse: Vector3f, sprint: Boolean, cruise: Boolean) : SimplePacket {
   public final val impulse: Vector3f
   public final val sprint: Boolean
   public final val cruise: Boolean

   init {
      this.impulse = impulse
      this.sprint = sprint
      this.cruise = cruise
   }

   public operator fun component1(): Vector3f {
      return this.impulse
   }

   public operator fun component2(): Boolean {
      return this.sprint
   }

   public operator fun component3(): Boolean {
      return this.cruise
   }

   public fun copy(impulse: Vector3f = this.impulse, sprint: Boolean = this.sprint, cruise: Boolean = this.cruise): PacketPlayerDriving {
      return PacketPlayerDriving(impulse, sprint, cruise)
   }

   public override fun toString(): String {
      return "PacketPlayerDriving(impulse=${this.impulse}, sprint=${this.sprint}, cruise=${this.cruise})"
   }

   public override fun hashCode(): Int {
      return (this.impulse.hashCode() * 31 + java.lang.Boolean.hashCode(this.sprint)) * 31 + java.lang.Boolean.hashCode(this.cruise)
   }

   public override operator fun equals(other: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is PacketPlayerDriving
            && this.impulse == (other as PacketPlayerDriving).impulse
            && this.sprint == (other as PacketPlayerDriving).sprint
            && this.cruise == (other as PacketPlayerDriving).cruise
         }
   }
}
