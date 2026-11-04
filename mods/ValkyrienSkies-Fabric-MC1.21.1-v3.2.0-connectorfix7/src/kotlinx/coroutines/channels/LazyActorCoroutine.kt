package kotlinx.coroutines.channels

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.TypeIntrinsics
import kotlinx.coroutines.intrinsics.CancellableKt
import kotlinx.coroutines.selects.SelectClause2
import kotlinx.coroutines.selects.SelectClause2Impl
import kotlinx.coroutines.selects.SelectInstance

private class LazyActorCoroutine<E>(parentContext: CoroutineContext, channel: Channel<Any>, block: (ActorScope<Any>, Continuation<Unit>) -> Any?) : ActorCoroutine(
      parentContext, channel, false
   ) {
   private final var continuation: Continuation<Unit>

   init {
      this.continuation = IntrinsicsKt.createCoroutineUnintercepted(block, this, this)
   }

   protected override fun onStart() {
      CancellableKt.startCoroutineCancellable(this.continuation, this)
   }

   public override suspend fun send(element: Any) {
      this.start()
      val var10000: Any = super.send((E)element, `$completion`)
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }

   @Deprecated(message = "Deprecated in the favour of 'trySend' method", replaceWith = @ReplaceWith(expression = "trySend(element).isSuccess", imports = []), level = DeprecationLevel.ERROR)
   public override fun offer(element: Any): Boolean {
      this.start()
      return super.offer((E)element)
   }

   public override fun trySend(element: Any): ChannelResult<Unit> {
      this.start()
      return super.trySend_JP2dKIU/* $VF was: trySend-JP2dKIU */((E)element)
   }

   public override fun close(cause: Throwable?): Boolean {
      val closed: Boolean = super.close(cause)
      this.start()
      return closed
   }

   public open val onSend: SelectClause2<Any, SendChannel<Any>>
      public open get() {
         val var10003: <unrepresentable> = <unrepresentable>.INSTANCE
         return SelectClause2Impl<>(
            this, TypeIntrinsics.beforeCheckcastToFunctionOfArity(var10003, 3) as Function3, super.getOnSend().getProcessResFunc(), null, 8, null
         )
      }


   private fun onSendRegFunction(select: SelectInstance<*>, element: Any?) {
      this.onStart()
      super.getOnSend().getRegFunc()(this, select, element)
   }
}
