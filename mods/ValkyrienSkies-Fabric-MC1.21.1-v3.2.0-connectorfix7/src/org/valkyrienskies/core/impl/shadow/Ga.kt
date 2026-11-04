package org.valkyrienskies.core.impl.shadow

import kotlin.reflect.KProperty
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

public object Ga {
   @JvmStatic
   public operator fun a(p0: Any, p1: KProperty<*>): FY {
      val var10000: Logger = LogManager.getLogger(if ((var0.getClass()::class).isCompanion()) var0.getClass().getDeclaringClass() else var0.getClass())
      return var10000
   }
}
