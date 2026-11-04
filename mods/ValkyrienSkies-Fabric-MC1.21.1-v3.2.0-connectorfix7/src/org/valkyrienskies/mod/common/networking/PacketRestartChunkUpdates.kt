package org.valkyrienskies.mod.common.networking

import org.joml.Vector2i
import org.valkyrienskies.core.impl.networking.simple.SimplePacket

public data class PacketRestartChunkUpdates(chunks: List<Vector2i>) : SimplePacket {
   public final val chunks: List<Vector2i>

   init {
      this.chunks = chunks
   }

   public operator fun component1(): List<Vector2i> {
      return this.chunks
   }

   public fun copy(chunks: List<Vector2i> = this.chunks): PacketRestartChunkUpdates {
      return PacketRestartChunkUpdates(chunks)
   }

   public override fun toString(): String {
      return "PacketRestartChunkUpdates(chunks=${this.chunks})"
   }

   public override fun hashCode(): Int {
      return this.chunks.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label22@
      if (this === other) {
         return true
      } else {
         return other is PacketRestartChunkUpdates && this.chunks == (other as PacketRestartChunkUpdates).chunks
      }
   }
}
