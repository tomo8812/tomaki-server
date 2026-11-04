package org.valkyrienskies.core.impl.networking.impl

import com.fasterxml.jackson.databind.JsonNode
import org.valkyrienskies.core.impl.networking.simple.SimplePacket

public data class PacketCommonConfigUpdate(mainClass: Class<*>, newConfig: JsonNode) : SimplePacket {
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

   public fun copy(mainClass: Class<*> = this.mainClass, newConfig: JsonNode = this.newConfig): PacketCommonConfigUpdate {
      return PacketCommonConfigUpdate(mainClass, newConfig)
   }

   public override fun toString(): String {
      return "PacketCommonConfigUpdate(mainClass=${this.mainClass}, newConfig=${this.newConfig})"
   }

   public override fun hashCode(): Int {
      return this.mainClass.hashCode() * 31 + this.newConfig.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is PacketCommonConfigUpdate
            && this.mainClass == (other as PacketCommonConfigUpdate).mainClass
            && this.newConfig == (other as PacketCommonConfigUpdate).newConfig
         }
   }
}
