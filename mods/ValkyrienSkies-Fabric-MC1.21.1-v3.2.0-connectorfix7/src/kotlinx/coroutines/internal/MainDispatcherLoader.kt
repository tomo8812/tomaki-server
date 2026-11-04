package kotlinx.coroutines.internal

import java.util.ServiceLoader
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.MainCoroutineDispatcher
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nMainDispatchers.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MainDispatchers.kt\nkotlinx/coroutines/internal/MainDispatcherLoader\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,134:1\n1963#2,14:135\n*S KotlinDebug\n*F\n+ 1 MainDispatchers.kt\nkotlinx/coroutines/internal/MainDispatcherLoader\n*L\n38#1:135,14\n*E\n"])
internal object MainDispatcherLoader {
   private final val FAST_SERVICE_LOADER_ENABLED: Boolean = SystemPropsKt.systemProp("kotlinx.coroutines.fast.service.loader", true)

   @JvmField
   @NotNull
   public final val dispatcher: MainCoroutineDispatcher = INSTANCE.loadMainDispatcher()

   private fun loadMainDispatcher(): MainCoroutineDispatcher {
      var factories: MainCoroutineDispatcher
      try {
         val var12: java.util.List = if (FAST_SERVICE_LOADER_ENABLED)
            FastServiceLoader.INSTANCE.loadMainDispatcherFactory$kotlinx_coroutines_core()
            else
            SequencesKt.toList(SequencesKt.asSequence(ServiceLoader.load(MainDispatcherFactory.class, MainDispatcherFactory.class.getClassLoader()).iterator()))
            val `iterator$iv`: java.util.Iterator = var12.iterator()
         var var10000: Any
         if (!`iterator$iv`.hasNext()) {
            var10000 = null
         } else {
            var `maxElem$iv`: Any = `iterator$iv`.next()
            if (!`iterator$iv`.hasNext()) {
               var10000 = (MainDispatcherFactory)`maxElem$iv`
            } else {
               var var13: Int = (`maxElem$iv` as MainDispatcherFactory).loadPriority

               do {
                  val var14: Any = `iterator$iv`.next()
                  val var15: Int = (var14 as MainDispatcherFactory).loadPriority
                  if (var13 < var15) {
                     `maxElem$iv` = var14
                     var13 = var15
                  }
               } while (`iterator$iv`.hasNext())

               var10000 = (MainDispatcherFactory)`maxElem$iv`
            }
         }

         run label60@{
            var10000 = var10000
            if (var10000 != null) {
               var17 = MainDispatchersKt.tryCreateDispatcher(var10000, var12)
               if (var17 != null) {
                  return@label60
               }
            }

            var17 = MainDispatchersKt.createMissingDispatcher$default(null, null, 3, null)
         }

         factories = var17
      } catch (var11: java.lang.Throwable) {
         factories = MainDispatchersKt.createMissingDispatcher$default(var11, null, 2, null)
      }

      return factories
   }
}
