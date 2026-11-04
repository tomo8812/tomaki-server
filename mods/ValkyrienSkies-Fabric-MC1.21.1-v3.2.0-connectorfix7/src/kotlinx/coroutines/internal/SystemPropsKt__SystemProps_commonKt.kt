@file:JvmMultifileClass
@file:JvmName("SystemPropsKt")

package kotlinx.coroutines.internal

internal fun systemProp(propertyName: String, defaultValue: Boolean): Boolean {
   val var10000: java.lang.String = SystemPropsKt.systemProp(propertyName)
   return if (var10000 != null) java.lang.Boolean.parseBoolean(var10000) else defaultValue
}

internal fun systemProp(propertyName: String, defaultValue: Int, minValue: Int = 1, maxValue: Int = Integer.MAX_VALUE): Int {
   return (int)SystemPropsKt.systemProp(propertyName, (long)defaultValue, (long)minValue, (long)maxValue)
}

internal fun systemProp(propertyName: String, defaultValue: Long, minValue: Long = 1L, maxValue: Long = java.lang.Long.MAX_VALUE): Long {
   val var10000: java.lang.String = SystemPropsKt.systemProp(propertyName)
   if (var10000 == null) {
      return defaultValue
   } else {
      val var10: java.lang.Long = StringsKt.toLongOrNull(var10000)
      if (var10 != null) {
         val parsed: Long = var10
         if (minValue > parsed || parsed > maxValue) {
            throw IllegalStateException(("System property '$propertyName' should be in range $minValue..$maxValue, but is '$parsed'").toString())
         } else {
            return parsed
         }
      } else {
         throw IllegalStateException(("System property '$propertyName' has unrecognized value '$var10000'").toString())
      }
   }
}

internal fun systemProp(propertyName: String, defaultValue: String): String {
   var var10000: java.lang.String = SystemPropsKt.systemProp(propertyName)
   if (var10000 == null) {
      var10000 = defaultValue
   }

   return var10000
}
