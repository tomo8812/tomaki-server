@file:JvmMultifileClass
@file:JvmName("SystemPropsKt")

package kotlinx.coroutines.internal

internal final val AVAILABLE_PROCESSORS: Int = Runtime.getRuntime().availableProcessors()

internal fun systemProp(propertyName: String): String? {
   var var1: java.lang.String
   try {
      var1 = System.getProperty(propertyName)
   } catch (var3: SecurityException) {
      var1 = null
   }

   return var1
}
