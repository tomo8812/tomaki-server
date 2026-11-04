package kotlinx.coroutines

import kotlin.coroutines.AbstractCoroutineContextElement

public data class CoroutineName(name: String) : AbstractCoroutineContextElement(Key) {
   public final val name: String

   init {
      this.name = name
   }

   public open fun toString(): String {
      return "CoroutineName(${this.name})"
   }

   public operator fun component1(): String {
      return this.name
   }

   public fun copy(name: String = this.name): CoroutineName {
      return CoroutineName(name)
   }

   public open fun hashCode(): Int {
      return this.name.hashCode()
   }

   public open operator fun equals(other: Any?): Boolean {
      label22@
      if (this === other) {
         return true
      } else {
         return other is CoroutineName && this.name == (other as CoroutineName).name
      }
   }

   public companion object Key : kotlin.coroutines.CoroutineContext.Key<CoroutineName>
}
