package kotlinx.coroutines

import java.util.concurrent.atomic.AtomicLong
import kotlin.internal.InlineOnly
import kotlinx.coroutines.internal.SystemPropsKt

public const val DEBUG_PROPERTY_NAME: String = "kotlinx.coroutines.debug"
internal const val STACKTRACE_RECOVERY_PROPERTY_NAME: String = "kotlinx.coroutines.stacktrace.recovery"
public const val DEBUG_PROPERTY_VALUE_AUTO: String = "auto"
public const val DEBUG_PROPERTY_VALUE_ON: String = "on"
public const val DEBUG_PROPERTY_VALUE_OFF: String = "off"
internal final val ASSERTIONS_ENABLED: Boolean
internal final val DEBUG: Boolean

@PublishedApi
internal final val RECOVER_STACK_TRACES: Boolean

internal final val COROUTINE_ID: AtomicLong

internal fun resetCoroutineId() {
   COROUTINE_ID.set(0L)
}

@InlineOnly
internal inline fun assert(value: () -> Boolean) {
   if (ASSERTIONS_ENABLED && !value() as java.lang.Boolean) {
      throw AssertionError()
   }
}

fun {
   var var10000: Boolean
   run label65@{
      ASSERTIONS_ENABLED = CoroutineId.class.desiredAssertionStatus()
      val value: java.lang.String = SystemPropsKt.systemProp("kotlinx.coroutines.debug")
      label64@
      if (value != null) {
         when (value.hashCode()) {
            0 -> {
               if (!value.equals("")) {
                  throw IllegalStateException(("System property 'kotlinx.coroutines.debug' has unrecognized value '$value'").toString())
               }
            }
            3551 -> {
               if (!value.equals("on")) {
                  throw IllegalStateException(("System property 'kotlinx.coroutines.debug' has unrecognized value '$value'").toString())
               }
            }
            109935 -> {
               if (!value.equals("off")) {
                  throw IllegalStateException(("System property 'kotlinx.coroutines.debug' has unrecognized value '$value'").toString())
               }

               var10000 = false
               return@label65
            }
            3005871 -> {
               if (!value.equals("auto")) {
                  throw IllegalStateException(("System property 'kotlinx.coroutines.debug' has unrecognized value '$value'").toString())
               }
               break@label64
            }
            else -> throw IllegalStateException(("System property 'kotlinx.coroutines.debug' has unrecognized value '$value'").toString())
         }

         var10000 = true
         return@label65
      }

      var10000 = ASSERTIONS_ENABLED
   }

   DEBUG = var10000
   RECOVER_STACK_TRACES = DEBUG && SystemPropsKt.systemProp("kotlinx.coroutines.stacktrace.recovery", true)
   COROUTINE_ID = AtomicLong(0L)
}
