package org.valkyrienskies.mod.common.config

import kotlin.enums.EnumEntries

public enum class ConfigType {
   CORE_SERVER,
   SERVER,
   COMMON,
   CLIENT;

   @JvmStatic
   fun getEntries(): EnumEntries<ConfigType> {
      $ENTRIES
   }

   @JvmStatic
   @JvmSynthetic
   fun `$values`(): Array<ConfigType> {
      arrayOf(CORE_SERVER, SERVER, COMMON, CLIENT)
   }
}
