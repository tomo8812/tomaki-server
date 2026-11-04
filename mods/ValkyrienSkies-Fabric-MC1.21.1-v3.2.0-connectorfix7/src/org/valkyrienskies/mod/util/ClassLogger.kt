package org.valkyrienskies.mod.util

import kotlin.reflect.KProperty
import org.apache.logging.log4j.Logger

@JvmInline
public value class ClassLogger {
   public final val logger: Logger

   @JvmStatic
   public operator fun getValue(thisRef: Any?, property: KProperty<*>): Logger {
      return arg0
   }

   @JvmStatic
   public open fun toString(): String {
      return "ClassLogger(logger=$arg0)"
   }

   override fun toString(): java.lang.String {
      toString_impl/* $VF was: toString-impl */(this.logger)
   }

   @JvmStatic
   public open fun hashCode(): Int {
      return arg0.hashCode()
   }

   override fun hashCode(): Int {
      hashCode_impl/* $VF was: hashCode-impl */(this.logger)
   }

   @JvmStatic
   public open operator fun equals(other: Any?): Boolean {
      return other is ClassLogger && arg0 == (other as ClassLogger).unbox_impl/* $VF was: unbox-impl */()
   }

   override fun equals(other: Any): Boolean {
      equals_impl/* $VF was: equals-impl */(this.logger, other)
   }

   @JvmSynthetic
   fun ClassLogger(logger: Logger) {
      this.logger = logger
   }

   @JvmStatic
   fun `constructor-impl`(logger: Logger): Logger {
      logger
   }

   @JvmStatic
   @JvmSynthetic
   fun `box-impl`(v: Logger): ClassLogger {
      ClassLogger(v)
   }

   @JvmSynthetic
   fun `unbox-impl`(): Logger {
      this.logger
   }

   @JvmStatic
   fun `equals-impl0`(p1: Logger, p2: Logger): Boolean {
      p1 == p2
   }
}
