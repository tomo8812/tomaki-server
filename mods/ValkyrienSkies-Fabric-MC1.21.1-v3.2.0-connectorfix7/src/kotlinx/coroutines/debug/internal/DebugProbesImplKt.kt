package kotlinx.coroutines.debug.internal

private fun String.repr(): String {
   val var1: StringBuilder = StringBuilder()
   val `$this$repr_u24lambda_u240`: StringBuilder = var1
   var1.append('"')
   var var4: Int = 0

   for (var5 in `$this$repr`.length()..var4) {
      val c: Char = `$this$repr`.charAt(var4)
      if (c == '"') {
         `$this$repr_u24lambda_u240`.append("\\\"")
      } else if (c == '\\') {
         `$this$repr_u24lambda_u240`.append("\\\\")
      } else if (c == '\b') {
         `$this$repr_u24lambda_u240`.append("\\b")
      } else if (c == '\n') {
         `$this$repr_u24lambda_u240`.append("\\n")
      } else if (c == '\r') {
         `$this$repr_u24lambda_u240`.append("\\r")
      } else if (c == '\t') {
         `$this$repr_u24lambda_u240`.append("\\t")
      } else {
         `$this$repr_u24lambda_u240`.append(c)
      }
   }

   `$this$repr_u24lambda_u240`.append('"')
   val var10000: java.lang.String = var1.toString()
   return var10000
}

@JvmSynthetic
fun `access$repr`(`$receiver`: java.lang.String): java.lang.String {
   repr(`$receiver`)
}
