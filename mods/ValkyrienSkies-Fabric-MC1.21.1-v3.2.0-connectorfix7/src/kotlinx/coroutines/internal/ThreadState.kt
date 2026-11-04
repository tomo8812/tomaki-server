package kotlinx.coroutines.internal

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.ThreadContextElement
import org.jetbrains.annotations.NotNull

private class ThreadState(context: CoroutineContext, n: Int) {
   @JvmField
   @NotNull
   public final val context: CoroutineContext

   private final val values: Array<Any?>
   private final val elements: Array<ThreadContextElement<Any?>?>
   private final var i: Int

   init {
      this.context = context
      this.values = arrayOfNulls(n)
      this.elements = arrayOfNulls(n)
   }

   public fun append(element: ThreadContextElement<*>, value: Any?) {
      this.values[this.i] = value
      val var10000: Array<ThreadContextElement> = this.elements
      val var3: Int = this.i++
      var10000[var3] = element
   }

   public fun restore(context: CoroutineContext) {
      var var2: Int = this.elements.length + -1
      if (0 <= this.elements.length + -1) {
         do {
            val i: Int = var2--
            val var10000: ThreadContextElement = this.elements[i]
            var10000.restoreThreadContext(context, this.values[i])
         } while (0 <= var2)
      }
   }
}
