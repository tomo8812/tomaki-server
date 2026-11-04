package org.valkyrienskies.mod.client

import java.lang.reflect.Method
import java.util.NoSuchElementException
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nShipMountPerspective.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShipMountPerspective.kt\norg/valkyrienskies/mod/client/ShoulderSurfingCompat\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,187:1\n1137#2,2:188\n*S KotlinDebug\n*F\n+ 1 ShipMountPerspective.kt\norg/valkyrienskies/mod/client/ShoulderSurfingCompat\n*L\n145#1:188,2\n*E\n"])
private object ShoulderSurfingCompat {
   private final val api: org.valkyrienskies.mod.client.ShoulderSurfingCompat.Api?

   public fun isShoulderSurfing(): Boolean {
      if (api == null) {
         return false
      } else {
         val a: ShoulderSurfingCompat.Api = api

         var var2: Boolean
         try {
            val var10000: Any = a.isShoulderSurfing.invoke(a.getInstance.invoke(null))
            var2 = var10000 as java.lang.Boolean
         } catch (var4: java.lang.Throwable) {
            var2 = false
         }

         return var2
      }
   }

   public fun replacesDefaultPerspective(): Boolean {
      if (api == null) {
         return false
      } else {
         val a: ShoulderSurfingCompat.Api = api

         var var2: Boolean
         try {
            val var10000: Any = a.replaceDefaultPerspective.invoke(a.getClientConfig.invoke(a.getInstance.invoke(null)))
            var2 = var10000 as java.lang.Boolean
         } catch (var4: java.lang.Throwable) {
            var2 = false
         }

         return var2
      }
   }

   public fun enterShoulderSurfing(): Boolean {
      if (api == null) {
         return false
      } else {
         val a: ShoulderSurfingCompat.Api = api

         var var2: Boolean
         try {
            a.changePerspective.invoke(a.getInstance.invoke(null), a.shoulderSurfing)
            var2 = true
         } catch (var4: java.lang.Throwable) {
            var2 = false
         }

         return var2
      }
   }

   @JvmStatic
   fun {
      var entry: ShoulderSurfingCompat.Api
      try {
         val var13: Class = Class.forName("com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing")
         val t: Class = Class.forName("com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing")
         val config: Class = Class.forName("com.github.exopandora.shouldersurfing.api.client.IClientConfig")
         val perspective: Class = Class.forName("com.github.exopandora.shouldersurfing.api.model.Perspective")
         val var10000: Array<Any> = perspective.getEnumConstants()
         val `$this$first$iv`: Array<Any> = var10000
         var var7: Int = 0
         val var8: Int = var10000.length

         while (true) {
            if (var7 >= var8) {
               throw NoSuchElementException("Array contains no element matching the predicate.")
            }

            val `element$iv`: Any = `$this$first$iv`[var7]
            val it: Any = `$this$first$iv`[var7]
            if ((it as java.lang.Enum).name() == "SHOULDER_SURFING") {
               val var10002: Method = var13.getMethod("getInstance")
               val var10003: Method = t.getMethod("isShoulderSurfing")
               val var10004: Method = t.getMethod("changePerspective", perspective)
               val var10005: Method = t.getMethod("getClientConfig")
               val var10006: Method = config.getMethod("replaceDefaultPerspective")
               entry = ShoulderSurfingCompat.Api(var10002, var10003, var10004, var10005, var10006, `element$iv`)
               break
            }

            var7++
         }
      } catch (var12: java.lang.Throwable) {
         entry = null
      }

      api = entry
   }

   private class Api(getInstance: Method,
      isShoulderSurfing: Method,
      changePerspective: Method,
      getClientConfig: Method,
      replaceDefaultPerspective: Method,
      shoulderSurfing: Any
   ) {
      public final val getInstance: Method
      public final val isShoulderSurfing: Method
      public final val changePerspective: Method
      public final val getClientConfig: Method
      public final val replaceDefaultPerspective: Method
      public final val shoulderSurfing: Any

      init {
         this.getInstance = getInstance
         this.isShoulderSurfing = isShoulderSurfing
         this.changePerspective = changePerspective
         this.getClientConfig = getClientConfig
         this.replaceDefaultPerspective = replaceDefaultPerspective
         this.shoulderSurfing = shoulderSurfing
      }
   }
}
