package kotlinx.coroutines.internal

import java.util.WeakHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nExceptionsConstructor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ExceptionsConstructor.kt\nkotlinx/coroutines/internal/WeakMapCtorCache\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,116:1\n1#2:117\n*E\n"])
private object WeakMapCtorCache : CtorCache {
   private final val cacheLock: ReentrantReadWriteLock = ReentrantReadWriteLock()
   private final val exceptionCtors: WeakHashMap<Class<out Throwable>, (Throwable) -> Throwable?> = WeakHashMap()

   public override fun get(key: Class<out Throwable>): (Throwable) -> Throwable? {
      var var3: ReadLock = cacheLock.readLock()
      var3.lock()

      try {
         val var10000: Function1 = exceptionCtors.get(key)
         if (var10000 != null) {
            return var10000
         }
      } finally {
         var3.unlock()
      }

      val var2: ReentrantReadWriteLock = cacheLock
      var3 = cacheLock.readLock()
      val var21: Int = if (var2.getWriteHoldCount() == 0) var2.getReadHoldCount() else 0

      repeat(var21) {
         var3.unlock()
      }

      val var22: WriteLock = var2.writeLock()
      var22.lock()

      try {
         val var27: Function1 = exceptionCtors.get(key)
         if (var27 != null) {
            return var27
         } else {
            val var9: Function1 = ExceptionsConstructorKt.access$createConstructor(key)
            exceptionCtors.put(key, var9)
            return var9
         }
      } finally {
         repeat(var21) { var7 ->
            var3.lock()
         }

         var22.unlock()
      }
   }
}
