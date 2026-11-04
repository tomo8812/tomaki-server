package org.valkyrienskies.core.internal.hooks

import io.netty.buffer.ByteBuf
import java.nio.file.Path
import org.valkyrienskies.core.api.world.ShipWorld
import org.valkyrienskies.core.internal.world.VsiPlayer

public interface VsiCoreHooksOut {
   public val isPhysicalClient: Boolean

   public val configDir: Path

   public val playState: VsiPlayState

   public val currentShipServerWorld: ShipWorld?

   public val currentShipClientWorld: ShipWorld

   public var enableConnectivity: Boolean

   public var enableWorldConnectivity: Boolean

   public var enableBlockEdgeConnectivity: Boolean

   public var enableBlockCornerConnectivity: Boolean

   public var enableSplitting: Boolean

   public var enableDrag: Boolean

   public var enableLift: Boolean

   public var dragCoefficient: Double

   public var dragMultiplier: Double

   public var liftMultiplier: Double

   public abstract fun sendToServer(buf: ByteBuf) {
   }

   public abstract fun sendToClient(buf: ByteBuf, player: VsiPlayer) {
   }
}
