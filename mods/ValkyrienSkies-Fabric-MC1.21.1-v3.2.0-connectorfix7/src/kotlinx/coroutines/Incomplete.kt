package kotlinx.coroutines

internal interface Incomplete {
   public val isActive: Boolean

   public val list: NodeList?
}
