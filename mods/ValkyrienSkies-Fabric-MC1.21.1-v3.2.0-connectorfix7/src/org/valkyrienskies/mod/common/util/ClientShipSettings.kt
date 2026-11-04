package org.valkyrienskies.mod.common.util

import org.valkyrienskies.mod.common.config.ShipRenderer

public data class ClientShipSettings(renderer: ShipRenderer? = null) {
   public final var renderer: ShipRenderer?

   init {
      this.renderer = renderer
   }

   public operator fun component1(): ShipRenderer? {
      return this.renderer
   }

   public fun copy(renderer: ShipRenderer? = this.renderer): ClientShipSettings {
      return ClientShipSettings(renderer)
   }

   public override fun toString(): String {
      return "ClientShipSettings(renderer=${this.renderer})"
   }

   public override fun hashCode(): Int {
      return if (this.renderer == null) 0 else this.renderer.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label22@
      if (this === other) {
         return true
      } else {
         return other is ClientShipSettings && this.renderer === (other as ClientShipSettings).renderer
      }
   }

   fun ClientShipSettings() {
      this(null, 1, null)
   }
}
