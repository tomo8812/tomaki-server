package kotlinx.coroutines

import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.internal.Symbol
import kotlinx.coroutines.internal.ThreadLocalKt

@SourceDebugExtension(["SMAP\nEventLoop.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EventLoop.common.kt\nkotlinx/coroutines/ThreadLocalEventLoop\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,543:1\n1#2:544\n*E\n"])
internal object ThreadLocalEventLoop {
   private final val ref: ThreadLocal<EventLoop?> = ThreadLocalKt.commonThreadLocal(Symbol("ThreadLocalEventLoop"))

   internal final val eventLoop: EventLoop
      internal final get() {
         var var10000: EventLoop = ref.get()
         if (var10000 == null) {
            val var1: EventLoop = EventLoopKt.createEventLoop()
            ref.set(var1)
            var10000 = var1
         }

         return var10000
      }


   internal fun currentOrNull(): EventLoop? {
      return ref.get()
   }

   internal fun resetEventLoop() {
      ref.set(null)
   }

   internal fun setEventLoop(eventLoop: EventLoop) {
      ref.set(eventLoop)
   }
}
