package kotlinx.coroutines.channels

import kotlinx.coroutines.InternalCoroutinesApi
import org.jetbrains.annotations.Nullable

@JvmInline
public value class ChannelResult<T> {
   @PublishedApi
   internal final val holder: Any?

   public final val isSuccess: Boolean
      public final get() {
         return arg0 !is ChannelResult.Failed
      }


   public final val isFailure: Boolean
      public final get() {
         return arg0 is ChannelResult.Failed
      }


   public final val isClosed: Boolean
      public final get() {
         return arg0 is ChannelResult.Closed
      }


   @JvmStatic
   public fun getOrNull(): Any? {
      return (T)(if (arg0 !is ChannelResult.Failed) arg0 else null)
   }

   @JvmStatic
   public fun getOrThrow(): Any {
      if (arg0 !is ChannelResult.Failed) {
         return (T)arg0
      } else if (arg0 is ChannelResult.Closed && (arg0 as ChannelResult.Closed).cause != null) {
         throw (arg0 as ChannelResult.Closed).cause
      } else {
         throw IllegalStateException(("Trying to call 'getOrThrow' on a failed channel result: $arg0").toString())
      }
   }

   @JvmStatic
   public fun exceptionOrNull(): Throwable? {
      return if ((arg0 as? ChannelResult.Closed) != null) (arg0 as? ChannelResult.Closed).cause else null
   }

   @JvmStatic
   public open fun toString(): String {
      return if (arg0 is ChannelResult.Closed) (arg0 as ChannelResult.Closed).toString() else "Value($arg0)"
   }

   override fun toString(): java.lang.String {
      toString_impl/* $VF was: toString-impl */(this.holder)
   }

   @JvmStatic
   public open fun hashCode(): Int {
      return if (arg0 == null) 0 else arg0.hashCode()
   }

   override fun hashCode(): Int {
      hashCode_impl/* $VF was: hashCode-impl */(this.holder)
   }

   @JvmStatic
   public open operator fun equals(other: Any?): Boolean {
      return other is ChannelResult && arg0 == (other as ChannelResult).unbox_impl/* $VF was: unbox-impl */()
   }

   override fun equals(other: Any): Boolean {
      equals_impl/* $VF was: equals-impl */(this.holder, other)
   }

   @PublishedApi
   @JvmSynthetic
   fun ChannelResult(holder: Any) {
      this.holder = holder
   }

   @PublishedApi
   @JvmStatic
   fun <T> `constructor-impl`(holder: Any?) {
      holder
   }

   @JvmStatic
   @JvmSynthetic
   fun `box-impl`(v: Any): ChannelResult {
      ChannelResult(v)
   }

   @JvmSynthetic
   fun `unbox-impl`() {
      this.holder
   }

   @JvmStatic
   fun `equals-impl0`(p1: Any, p2: Any): Boolean {
      p1 == p2
   }

   internal class Closed(cause: Throwable?) : ChannelResult.Failed {
      @JvmField
      @Nullable
      public final val cause: Throwable?

      init {
         this.cause = cause
      }

      public override operator fun equals(other: Any?): Boolean {
         return other is ChannelResult.Closed && this.cause == (other as ChannelResult.Closed).cause
      }

      public override fun hashCode(): Int {
         return if (this.cause != null) this.cause.hashCode() else 0
      }

      public override fun toString(): String {
         return "Closed(${this.cause})"
      }
   }

   @InternalCoroutinesApi
   public companion object {
      private final val failed: kotlinx.coroutines.channels.ChannelResult.Failed

      @InternalCoroutinesApi
      public fun <E> success(value: Any): ChannelResult<Any> {
         return ChannelResult.constructor_impl/* $VF was: constructor-impl */(value)
      }

      @InternalCoroutinesApi
      public fun <E> failure(): ChannelResult<Any> {
         return ChannelResult.constructor_impl/* $VF was: constructor-impl */(ChannelResult.failed)
      }

      @InternalCoroutinesApi
      public fun <E> closed(cause: Throwable?): ChannelResult<Any> {
         return ChannelResult.constructor_impl/* $VF was: constructor-impl */(ChannelResult.Closed(cause))
      }
   }

   internal open class Failed {
      public override fun toString(): String {
         return "Failed"
      }
   }
}
