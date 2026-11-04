package kotlinx.coroutines

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import kotlin.jvm.functions.Function1

private class ThreadState(job: Job) : Function1<java.lang.Throwable, Unit> {
   private final val job: Job
   @Volatile
   @Volatile
   private int _state;
   private final val targetThread: Thread
   private final var cancelHandle: DisposableHandle?
   @JvmStatic
   private AtomicIntegerFieldUpdater _state$FU = AtomicIntegerFieldUpdater.newUpdater(ThreadState.class, "_state");

   init {
      this.job = job
      this.targetThread = Thread.currentThread()
   }

   public fun setup() {
      this.cancelHandle = this.job.invokeOnCompletion(true, true, this as (java.lang.Throwable?) -> Unit)
      val `atomicfu$handler$iv`: AtomicIntegerFieldUpdater = _state$FU

      label24@ while (true) {
         val state: Int = `atomicfu$handler$iv`.get(this)
         when (state) {
            0 -> {
               if (_state$FU.compareAndSet(this, state, 0)) {
                  break@label24
               }
               continue
            }
            1 -> {
               this.invalidState(state)
               throw KotlinNothingValueException()
            }
            2, 3 -> return
            else -> {
               this.invalidState(state)
               throw KotlinNothingValueException()
            }
         }
      }
   }

   public fun clearInterrupt() {
      val `atomicfu$handler$iv`: AtomicIntegerFieldUpdater = _state$FU

      while (true) {
         val state: Int = `atomicfu$handler$iv`.get(this)
         when (state) {
            0 -> {
               if (_state$FU.compareAndSet(this, state, 1)) {
                  if (this.cancelHandle != null) {
                     this.cancelHandle.dispose()
                  }

                  return
               }
            }
            2 -> continue
            1 -> {
               this.invalidState(state)
               throw KotlinNothingValueException()
            }
            3 -> {
               Thread.interrupted()
               return
            }
            else -> {
               this.invalidState(state)
               throw KotlinNothingValueException()
            }
         }
      }
   }

   public open operator fun invoke(cause: Throwable?) {
      val `atomicfu$handler$iv`: AtomicIntegerFieldUpdater = _state$FU

      label24@ while (true) {
         val state: Int = `atomicfu$handler$iv`.get(this)
         when (state) {
            0 -> {
               if (_state$FU.compareAndSet(this, state, 2)) {
                  break@label24
               }
               continue
            }
            1, 2, 3 -> return
            else -> {
               this.invalidState(state)
               throw KotlinNothingValueException()
            }
         }
      }

      this.targetThread.interrupt()
      _state$FU.set(this, 3)
   }

   private fun invalidState(state: Int): Nothing {
      throw IllegalStateException(("Illegal state $state").toString())
   }

   fun `loop$atomicfu`(`atomicfu$handler`: AtomicIntegerFieldUpdater, `atomicfu$action`: (Int?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
      while (true) {
         `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
      }
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun invoke(p1: Any) {
      this.invoke(p1 as java.lang.Throwable)
      Unit.INSTANCE
   }
}
