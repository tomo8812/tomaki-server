package org.valkyrienskies.mod.common.config

import kotlin.enums.EnumEntries

public enum class ShipRenderer {
   VANILLA,
   FLYWHEEL;

   @JvmStatic
   fun getEntries(): EnumEntries<ShipRenderer> {
      $ENTRIES
   }

   @JvmStatic
   @JvmSynthetic
   fun `$values`(): Array<ShipRenderer> {
      arrayOf(VANILLA, FLYWHEEL)
   }
}
