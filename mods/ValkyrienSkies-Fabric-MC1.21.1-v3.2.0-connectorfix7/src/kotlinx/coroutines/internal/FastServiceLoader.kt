package kotlinx.coroutines.internal

import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStreamReader
import java.net.URL
import java.util.ArrayList
import java.util.Collections
import java.util.LinkedHashSet
import java.util.ServiceLoader
import java.util.jar.JarFile
import java.util.zip.ZipEntry
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nFastServiceLoader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FastServiceLoader.kt\nkotlinx/coroutines/internal/FastServiceLoader\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n1#1,161:1\n77#1,5:162\n77#1,5:168\n131#1,13:183\n1#2:167\n1360#3:173\n1446#3,5:174\n1549#3:179\n1620#3,3:180\n1064#4,2:196\n*S KotlinDebug\n*F\n+ 1 FastServiceLoader.kt\nkotlinx/coroutines/internal/FastServiceLoader\n*L\n60#1:162,5\n61#1:168,5\n117#1:183,13\n99#1:173\n99#1:174,5\n101#1:179\n101#1:180,3\n153#1:196,2\n*E\n"])
internal object FastServiceLoader {
   private const val PREFIX: String = "META-INF/services/"

   internal fun loadMainDispatcherFactory(): List<MainDispatcherFactory> {
      val clz: Class = MainDispatcherFactory::class.java
      if (!ANDROID_DETECTED) {
         return this.load(MainDispatcherFactory.class, MainDispatcherFactory.class.getClassLoader())
      } else {
         var result: ArrayList
         try {
            result = ArrayList(2)
            var `$this$loadMainDispatcherFactory_u24lambda_u241`: java.lang.String = "kotlinx.coroutines.android.AndroidDispatcherFactory"

            var `clz$iv`: MainDispatcherFactory
            try {
               `clz$iv` = clz.cast(
                  Class.forName(`$this$loadMainDispatcherFactory_u24lambda_u241`, true, clz.getClassLoader()).getDeclaredConstructor().newInstance()
               ) as MainDispatcherFactory
            } catch (var10: ClassNotFoundException) {
               `clz$iv` = null
            }

            if (`clz$iv` != null) {
               result.add(`clz$iv`)
            }

            `$this$loadMainDispatcherFactory_u24lambda_u241` = "kotlinx.coroutines.test.internal.TestMainDispatcherFactory"

            try {
               `clz$iv` = clz.cast(
                  Class.forName(`$this$loadMainDispatcherFactory_u24lambda_u241`, true, clz.getClassLoader()).getDeclaredConstructor().newInstance()
               ) as MainDispatcherFactory
            } catch (var9: ClassNotFoundException) {
               `clz$iv` = null
            }

            if (`clz$iv` != null) {
               result.add(`clz$iv`)
            }

            result = result
         } catch (var11: java.lang.Throwable) {
            result = this.<MainDispatcherFactory>load(MainDispatcherFactory.class, MainDispatcherFactory.class.getClassLoader())
         }

         return result
      }
   }

   private inline fun createInstanceOf(baseClass: Class<MainDispatcherFactory>, serviceClass: String): MainDispatcherFactory? {
      var clz: MainDispatcherFactory
      try {
         clz = baseClass.cast(Class.forName(serviceClass, true, baseClass.getClassLoader()).getDeclaredConstructor().newInstance()) as MainDispatcherFactory
      } catch (var6: ClassNotFoundException) {
         clz = null
      }

      return clz
   }

   private fun <S> load(service: Class<Any>, loader: ClassLoader): List<Any> {
      var var3: java.util.List
      try {
         var3 = this.loadProviders$kotlinx_coroutines_core(service, loader)
      } catch (var5: java.lang.Throwable) {
         var3 = CollectionsKt.toList(ServiceLoader.load(service, loader))
      }

      return var3
   }

   internal fun <S> loadProviders(service: Class<Any>, loader: ClassLoader): List<Any> {
      val var10000: ArrayList = Collections.list(loader.getResources("META-INF/services/${service.getName()}"))
      var `$this$mapTo$iv$iv`: java.lang.Iterable = var10000
      var `destination$iv$iv`: java.util.Collection = ArrayList()

      for (`item$iv$iv` in `$this$mapTo$iv$iv`) {
         CollectionsKt.addAll(`destination$iv$iv`, INSTANCE.parse(`item$iv$iv` as URL))
      }

      val providers: java.util.Set = CollectionsKt.toSet(`destination$iv$iv` as java.util.List)
      if (providers.isEmpty()) {
         throw IllegalArgumentException("No providers were loaded with FastServiceLoader".toString())
      } else {
         `$this$mapTo$iv$iv` = providers
         `destination$iv$iv` = ArrayList(CollectionsKt.collectionSizeOrDefault(providers, 10))

         for (var24 in `$this$mapTo$iv$iv`) {
            `destination$iv$iv`.add(INSTANCE.getProviderInstance(var24 as java.lang.String, loader, service))
         }

         return `destination$iv$iv` as MutableList<S>
      }
   }

   private fun <S> getProviderInstance(name: String, loader: ClassLoader, service: Class<Any>): Any {
      val clazz: Class = Class.forName(name, false, loader)
      if (!service.isAssignableFrom(clazz)) {
         throw IllegalArgumentException(("Expected service of class $service, but found $clazz").toString())
      } else {
         return (S)service.cast(clazz.getDeclaredConstructor().newInstance())
      }
   }

   private fun parse(url: URL): List<String> {
      val path: java.lang.String = url.toString()
      if (StringsKt.startsWith$default(path, "jar", false, 2, null)) {
         val var50: java.lang.String = StringsKt.substringBefore$default(StringsKt.substringAfter$default(path, "jar:file:", null, 2, null), '!', null, 2, null)
         val var51: java.lang.String = StringsKt.substringAfter$default(path, "!/", null, 2, null)
         val var52: JarFile = JarFile(var50, false)
         var `cause$iv`: java.lang.Throwable = null

         var var16: java.util.List
         try {
            val var53: Boolean = true
            val var11: Closeable = BufferedReader(InputStreamReader(var52.getInputStream(ZipEntry(var51)), "UTF-8"))
            var var12: java.lang.Throwable = null

            var var15: java.util.List
            try {
               var15 = INSTANCE.parseFile(var11 as BufferedReader)
            } catch (var43: java.lang.Throwable) {
               var12 = var43
               throw var43
            } finally {
               CloseableKt.closeFinally(var11, var12)
            }

            var16 = var15
         } catch (var45: java.lang.Throwable) {
            `cause$iv` = var45
            throw var45
         } finally {
            if (var33) {
               try {
                  var52.close()
               } catch (var48: java.lang.Throwable) {
                  if (`cause$iv` == null) {
                     throw var48
                  }

                  ExceptionsKt.addSuppressed(`cause$iv`, var48)
                  throw `cause$iv`
               }
            }
         }

         val var33: Boolean
         try {
            var52.close()
            return var16
         } catch (var42: java.lang.Throwable) {
            throw var42
         }
      } else {
         val pathToJar: Closeable = BufferedReader(InputStreamReader(url.openStream()))
         var entry: java.lang.Throwable = null

         try {
            return INSTANCE.parseFile(pathToJar as BufferedReader)
         } catch (var46: java.lang.Throwable) {
            entry = var46
            throw var46
         } finally {
            CloseableKt.closeFinally(pathToJar, entry)
         }
      }
   }

   private inline fun <R> JarFile.use(block: (JarFile) -> Any): Any {
      var cause: java.lang.Throwable = null

      var e: Any
      try {
         val var16: Boolean = true
         e = block(`$this$use`)
      } catch (var13: java.lang.Throwable) {
         cause = var13
         throw var13
      } finally {
         if (var11) {
            InlineMarker.finallyStart(1)

            try {
               `$this$use`.close()
            } catch (var14: java.lang.Throwable) {
               if (cause == null) {
                  throw var14
               }

               ExceptionsKt.addSuppressed(cause, var14)
               throw cause
            }

            InlineMarker.finallyEnd(1)
         }
      }

      InlineMarker.finallyStart(1)

      try {
         `$this$use`.close()
      } catch (var12: java.lang.Throwable) {
         throw var12
      }

      InlineMarker.finallyEnd(1)
      return (R)e
      val var11: Boolean
   }

   private fun parseFile(r: BufferedReader): List<String> {
      val names: java.util.Set = LinkedHashSet()

      while (true) {
         val var10000: java.lang.String = r.readLine()
         if (var10000 == null) {
            return CollectionsKt.toList(names)
         }

         val serviceName: java.lang.String = StringsKt.trim(StringsKt.substringBefore$default(var10000, "#", null, 2, null)).toString()
         val `$this$all$iv`: java.lang.CharSequence = serviceName
         var var7: Int = 0

         while (true) {
            if (var7 >= `$this$all$iv`.length()) {
               var13 = true
               break
            }

            val it: Char = `$this$all$iv`.charAt(var7)
            if (it != '.' && !Character.isJavaIdentifierPart(it)) {
               var13 = false
               break
            }

            var7++
         }

         if (!var13) {
            throw IllegalArgumentException(("Illegal service provider class name: $serviceName").toString())
         }

         if (serviceName.length() > 0) {
            names.add(serviceName)
         }
      }
   }
}
