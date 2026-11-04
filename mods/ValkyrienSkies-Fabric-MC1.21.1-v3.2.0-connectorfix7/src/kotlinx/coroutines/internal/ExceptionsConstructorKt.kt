@file:SourceDebugExtension(["SMAP\nExceptionsConstructor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ExceptionsConstructor.kt\nkotlinx/coroutines/internal/ExceptionsConstructorKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,116:1\n1#2:117\n11335#3:118\n11670#3,3:119\n12904#3,3:136\n1963#4,14:122\n*S KotlinDebug\n*F\n+ 1 ExceptionsConstructor.kt\nkotlinx/coroutines/internal/ExceptionsConstructorKt\n*L\n45#1:118\n45#1:119,3\n82#1:136,3\n63#1:122,14\n*E\n"])

package kotlinx.coroutines.internal

import java.lang.reflect.Modifier
import java.util.ArrayList
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KClass
import kotlinx.coroutines.CopyableThrowable

private final val throwableFields: Int = fieldsCountOrDefault(java.lang.Throwable.class, -1)
private final val ctorCache: CtorCache

internal fun <E : Throwable> tryCopyException(exception: Any): Any? {
   if (exception is CopyableThrowable) {
      var var1: Any
      try {
         var1 = Result.constructor_impl/* $VF was: constructor-impl */((exception as CopyableThrowable).createCopy())
      } catch (var3: java.lang.Throwable) {
         var1 = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var3))
      }

      return (E)((if (Result.isFailure_impl/* $VF was: isFailure-impl */(var1)) null else var1) as java.lang.Throwable)
   } else {
      return (E)(ctorCache.get((Class<? extends java.lang.Throwable>)exception.getClass())(exception) as java.lang.Throwable)
   }
}

private fun <E : Throwable> createConstructor(clz: Class<Any>): (Throwable) -> Throwable? {
   val nullResult: Function1 = { it ->
      null
   }
   if (throwableFields != fieldsCountOrDefault(clz, 0)) {
      return nullResult
   } else {
      val `$this$maxByOrNull$iv`: Array<Any> = clz.getConstructors()
      val `maxElem$iv`: java.util.Collection = ArrayList(`$this$maxByOrNull$iv`.length)

      for (var9 in `$this$maxByOrNull$iv`) {
         val p: Array<Class> = var9.getParameterTypes()
var var10000: Pair
         when (p.length) {
            0 -> {
               var10000 = TuplesKt.to(safeCtor({ e: Throwable ->
                  val var10000: Any = item$iv$iv.newInstance()
                  val var2: java.lang.Throwable = var10000 as java.lang.Throwable
                  (var10000 as java.lang.Throwable).initCause(e)
                  return var2
               } as (java.lang.Throwable?) -> java.lang.Throwable), 0)
            }
            1 -> {
               var10000 = if (p[0] == java.lang.String::class.java) TuplesKt.to(safeCtor({ e: Throwable ->
                  val var6: Any = item$iv$iv.newInstance(e.getMessage())
                  val var2: java.lang.Throwable = var6 as java.lang.Throwable
                  (var6 as java.lang.Throwable).initCause(e)
                  return var2
               } as (java.lang.Throwable?) -> java.lang.Throwable), 2) else (
                  if (p[0] == java.lang.Throwable::class.java) TuplesKt.to(safeCtor({ e: Throwable ->
                     val var3: Any = item$iv$iv.newInstance(e)
                     return var3 as java.lang.Throwable
                  } as (java.lang.Throwable?) -> java.lang.Throwable), 1) else TuplesKt.to(null, -1)
               )
            }
            2 -> {
               var10000 = if (p[0] == java.lang.String::class.java && p[1] == java.lang.Throwable::class.java) TuplesKt.to(safeCtor({ e: Throwable ->
                  val var3: Any = item$iv$iv.newInstance(e.getMessage(), e)
                  return var3 as java.lang.Throwable
               } as (java.lang.Throwable?) -> java.lang.Throwable), 3) else TuplesKt.to(null, -1)
            }
            else -> var10000 = TuplesKt.to(null, -1)
         }

         `maxElem$iv`.add(var10000)
      }

      val var17: java.util.Iterator = (`maxElem$iv` as java.util.List).iterator()
      var var26: Any
      if (!var17.hasNext()) {
         var26 = null
      } else {
         var var18: Any = var17.next()
         if (!var17.hasNext()) {
            var26 = (Pair)var18
         } else {
            var var20: Int = ((var18 as Pair).getSecond() as java.lang.Number).intValue()

            do {
               val var22: Any = var17.next()
               val var24: Int = ((var22 as Pair).getSecond() as java.lang.Number).intValue()
               if (var20 < var24) {
                  var18 = var22
                  var20 = var24
               }
            } while (var17.hasNext())

            var26 = (Pair)var18
         }
      }

      var26 = var26
      if (var26 != null) {
         val var28: Function1 = var26.getFirst() as Function1
         if (var28 != null) {
            return var28
         }
      }

      return nullResult
   }
}

private fun safeCtor(block: (Throwable) -> Throwable): (Throwable) -> Throwable? {
   return { e: Throwable ->
      val var2: Function1 = block

      var var3: Any
      try {
         val result: java.lang.Throwable = var2(e) as java.lang.Throwable
         var3 = Result.constructor_impl/* $VF was: constructor-impl */(
            if (!(e.getMessage() == result.getMessage()) && !(result.getMessage() == e.toString())) null else result
         )
      } catch (var5: java.lang.Throwable) {
         var3 = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var5))
      }

      return (if (Result.isFailure_impl/* $VF was: isFailure-impl */(var3)) null else var3) as java.lang.Throwable
   } as (java.lang.Throwable?) -> java.lang.Throwable
}

private fun Class<*>.fieldsCountOrDefault(defaultValue: Int): Int {
   val var2: KClass = JvmClassMappingKt.getKotlinClass(`$this$fieldsCountOrDefault`)

   var `$this$fieldsCountOrDefault_u24lambda_u242`: Any
   try {
      `$this$fieldsCountOrDefault_u24lambda_u242` = Result.constructor_impl/* $VF was: constructor-impl */(
         fieldsCount$default(`$this$fieldsCountOrDefault`, 0, 1, null)
      )
   } catch (var5: java.lang.Throwable) {
      `$this$fieldsCountOrDefault_u24lambda_u242` = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var5))
   }

   return ((
         if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$fieldsCountOrDefault_u24lambda_u242`))
            defaultValue
            else
            `$this$fieldsCountOrDefault_u24lambda_u242`
      ) as java.lang.Number)
      .intValue()
   }

private tailrec fun Class<*>.fieldsCount(accumulator: Int = 0): Int {
   while (true) {
      val totalFields: Array<Any> = `$this$fieldsCount`.getDeclaredFields()
      var `count$iv`: Int = 0

      for (`element$iv` in totalFields) {
         if (!Modifier.isStatic(`element$iv`.getModifiers())) {
            `count$iv`++
         }
      }

      val var11: Int = accumulator + `count$iv`
      val var10000: Class = `$this$fieldsCount`.getSuperclass()
      if (var10000 == null) {
         return var11
      }

      `$this$fieldsCount` = var10000
      accumulator = var11
   }
}

@JvmSynthetic
fun `access$createConstructor`(clz: Class): Function1 {
   createConstructor(clz)
}

fun {
   var var0: CtorCache
   try {
      var0 = if (ANDROID_DETECTED) WeakMapCtorCache.INSTANCE else ClassValueCtorCache.INSTANCE
   } catch (var2: java.lang.Throwable) {
      var0 = WeakMapCtorCache.INSTANCE
   }

   ctorCache = var0
}
