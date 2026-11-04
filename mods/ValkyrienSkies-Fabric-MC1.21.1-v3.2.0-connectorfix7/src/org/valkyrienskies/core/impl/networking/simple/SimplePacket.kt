package org.valkyrienskies.core.impl.networking.simple

import org.valkyrienskies.core.internal.world.VsiPlayer

public interface SimplePacket {
   public open fun receivedByClient() {
   }

   public open fun receivedByServer(player: VsiPlayer) {
   }
}
