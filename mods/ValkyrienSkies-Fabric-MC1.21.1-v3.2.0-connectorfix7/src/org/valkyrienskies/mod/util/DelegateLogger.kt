package org.valkyrienskies.mod.util

import kotlin.reflect.KProperty
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

public object DelegateLogger {
   public operator fun provideDelegate(thisRef: Any, property: KProperty<*>): ClassLogger {
      val var10000: Logger = LogManager.getLogger(if ((thisRef.getClass()::class).isCompanion()) thisRef.getClass().getDeclaringClass() else thisRef.getClass())
      return ClassLogger.constructor_impl/* $VF was: constructor-impl */(var10000)
   }
}
