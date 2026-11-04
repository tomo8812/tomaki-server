package org.valkyrienskies.core.internal.hooks

import io.netty.buffer.ByteBuf
import org.valkyrienskies.core.internal.world.VsiPlayer

public interface VsiCoreHooksIn {
   public abstract fun onReceiveServer(buf: ByteBuf, sender: VsiPlayer) {
   }

   public abstract fun onReceiveClient(buf: ByteBuf) {
   }

   public abstract fun afterDisconnect() {
   }

   public abstract fun afterClientJoinServer(player: VsiPlayer) {
   }
}
