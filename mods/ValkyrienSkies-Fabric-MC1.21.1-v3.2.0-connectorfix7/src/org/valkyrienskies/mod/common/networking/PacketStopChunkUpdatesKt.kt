package org.valkyrienskies.mod.common.networking

import kotlin.internal.ProgressionUtilKt
import org.joml.Vector2i
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking
import org.valkyrienskies.core.internal.world.VsiPlayer

private const val MAX_CHUNKS_PER_PACKET: Int = 10000

public fun SimplePacketNetworking.sendStopChunkUpdates(chunks: List<Vector2i>, player: VsiPlayer) {
   if (chunks.size() <= 10000) {
      `$this$sendStopChunkUpdates`.sendToClient1(PacketStopChunkUpdates(chunks), player)
   } else {
      val var3: Int = chunks.size() + -1
      var i: Int = 0
      val var5: Int = ProgressionUtilKt.getProgressionLastElement(0, var3, 10000)
      if (0 <= var5) {
         while (true) {
            `$this$sendStopChunkUpdates`.sendToClient1(PacketStopChunkUpdates(chunks.subList(i, Math.min(i + 10000, chunks.size()))), player)
            if (i == var5) {
               break
            }

            i += 10000
         }
      }
   }
}

public fun SimplePacketNetworking.sendRestartChunkUpdates(chunks: List<Vector2i>, player: VsiPlayer) {
   if (chunks.size() <= 10000) {
      `$this$sendRestartChunkUpdates`.sendToClient1(PacketRestartChunkUpdates(chunks), player)
   } else {
      val var3: Int = chunks.size() + -1
      var i: Int = 0
      val var5: Int = ProgressionUtilKt.getProgressionLastElement(0, var3, 10000)
      if (0 <= var5) {
         while (true) {
            `$this$sendRestartChunkUpdates`.sendToClient1(PacketRestartChunkUpdates(chunks.subList(i, Math.min(i + 10000, chunks.size()))), player)
            if (i == var5) {
               break
            }

            i += 10000
         }
      }
   }
}
