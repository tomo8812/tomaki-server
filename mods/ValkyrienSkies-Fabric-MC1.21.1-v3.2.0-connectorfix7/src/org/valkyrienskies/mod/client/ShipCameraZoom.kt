package org.valkyrienskies.mod.client

import org.valkyrienskies.mod.common.config.VSClientConfig

public object ShipCameraZoom {
   private const val STEPS: Double = 8.0
   private final var multiplier: Double = 1.0
   private final var shipCameraActive: Boolean

   @JvmStatic
   public fun setShipCameraActive(active: Boolean) {
      shipCameraActive = active
   }

   @JvmStatic
   public fun isShipCameraActive(): Boolean {
      return shipCameraActive
   }

   private final val minZoom: Double
      private final get() {
         return RangesKt.coerceAtLeast(VSClientConfig.CLIENT.shipCameraZoomMin, 0.1)
      }


   private final val maxZoom: Double
      private final get() {
         return RangesKt.coerceAtLeast(VSClientConfig.CLIENT.shipCameraZoomMax, this.minZoom)
      }


   @JvmStatic
   public fun getMultiplier(): Double {
      return RangesKt.coerceIn(multiplier, INSTANCE.minZoom, INSTANCE.maxZoom)
   }

   @JvmStatic
   public fun scroll(notches: Double) {
      multiplier = RangesKt.coerceIn(getMultiplier() - notches * ((INSTANCE.maxZoom - INSTANCE.minZoom) / 8.0), INSTANCE.minZoom, INSTANCE.maxZoom)
   }

   @JvmStatic
   public fun reset() {
      multiplier = INSTANCE.minZoom
      shipCameraActive = false
   }
}
