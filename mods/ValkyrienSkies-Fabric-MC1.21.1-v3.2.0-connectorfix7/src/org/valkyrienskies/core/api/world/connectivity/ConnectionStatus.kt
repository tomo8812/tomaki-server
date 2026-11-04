package org.valkyrienskies.core.api.world.connectivity

import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
public enum class ConnectionStatus {
   CONNECTED,
   DISCONNECTED,
   UNKNOWN;

   @JvmStatic
   @JvmSynthetic
   fun `$values`(): Array<ConnectionStatus> {
      arrayOf(CONNECTED, DISCONNECTED, UNKNOWN)
   }
}
