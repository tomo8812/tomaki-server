package org.valkyrienskies.mod.common.util

public data class ShipSettings(shouldGenerateChunks: Boolean = false, changeDimensionOnTouchPortals: Boolean = true, keepActive: Boolean = false) {
   public final var shouldGenerateChunks: Boolean
   public final var changeDimensionOnTouchPortals: Boolean
   public final var keepActive: Boolean

   init {
      this.shouldGenerateChunks = shouldGenerateChunks
      this.changeDimensionOnTouchPortals = changeDimensionOnTouchPortals
      this.keepActive = keepActive
   }

   public operator fun component1(): Boolean {
      return this.shouldGenerateChunks
   }

   public operator fun component2(): Boolean {
      return this.changeDimensionOnTouchPortals
   }

   public operator fun component3(): Boolean {
      return this.keepActive
   }

   public fun copy(
      shouldGenerateChunks: Boolean = this.shouldGenerateChunks,
      changeDimensionOnTouchPortals: Boolean = this.changeDimensionOnTouchPortals,
      keepActive: Boolean = this.keepActive
   ): ShipSettings {
      return ShipSettings(shouldGenerateChunks, changeDimensionOnTouchPortals, keepActive)
   }

   public override fun toString(): String {
      return "ShipSettings(shouldGenerateChunks=${this.shouldGenerateChunks}, changeDimensionOnTouchPortals=${this.changeDimensionOnTouchPortals}, keepActive=${this.keepActive})"
   }

   public override fun hashCode(): Int {
      return (java.lang.Boolean.hashCode(this.shouldGenerateChunks) * 31 + java.lang.Boolean.hashCode(this.changeDimensionOnTouchPortals)) * 31
         + java.lang.Boolean.hashCode(this.keepActive)
      }

   public override operator fun equals(other: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is ShipSettings
            && this.shouldGenerateChunks == (other as ShipSettings).shouldGenerateChunks
            && this.changeDimensionOnTouchPortals == (other as ShipSettings).changeDimensionOnTouchPortals
            && this.keepActive == (other as ShipSettings).keepActive
         }
   }

   fun ShipSettings() {
      this(false, false, false, 7, null)
   }
}
