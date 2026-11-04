package org.valkyrienskies.mod.util

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

public fun logger(): DelegateLogger {
   return DelegateLogger.INSTANCE
}

public fun logger(name: String): ClassLogger {
   val var10000: Logger = LogManager.getLogger(name)
   return ClassLogger.constructor_impl/* $VF was: constructor-impl */(var10000)
}
