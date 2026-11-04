package org.valkyrienskies.core.impl.networking.impl

import com.fasterxml.jackson.databind.JsonNode
import org.valkyrienskies.core.impl.networking.simple.SimplePacket

public data class PacketServerConfigUpdate(mainClass: Class<*>, newConfig: JsonNode) : SimplePacket {
   public final val mainClass: Class<*>
   public final val newConfig: JsonNode

   init {
      this.mainClass = mainClass
      this.newConfig = newConfig
   }

   public operator fun component1(): Class<*> {
      return this.mainClass
   }

   public operator fun component2(): JsonNode {
      return this.newConfig
   }

   public fun copy(mainClass: Class<*> = this.mainClass, newConfig: JsonNode = this.newConfig): PacketServerConfigUpdate {
      return PacketServerConfigUpdate(mainClass, newConfig)
   }

   public override fun toString(): String {
      return "PacketServerConfigUpdate(mainClass=${this.mainClass}, newConfig=${this.newConfig})"
   }

   public override fun hashCode(): Int {
      return this.mainClass.hashCode() * 31 + this.newConfig.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is PacketServerConfigUpdate
            && this.mainClass == (other as PacketServerConfigUpdate).mainClass
            && this.newConfig == (other as PacketServerConfigUpdate).newConfig
         }
   }
}
