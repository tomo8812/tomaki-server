package kotlinx.coroutines.internal

import kotlinx.coroutines.internal.ClassValueCtorCache.cache.1
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

@IgnoreJRERequirement
private object ClassValueCtorCache : CtorCache {
   private final val cache: 1 = object : ClassValue<(Throwable) -> Throwable?> {
      protected open fun computeValue(type: Class<*>?): (Throwable) -> Throwable? {
         return ExceptionsConstructorKt.access$createConstructor(type)
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override fun computeValue(p0: Class) {
         this.computeValue(p0)
      }
   }

   public override fun get(key: Class<out Throwable>): (Throwable) -> Throwable? {
      return cache.get(key)
   }
}
