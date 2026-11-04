package kotlinx.coroutines

internal class InactiveNodeList(list: NodeList) : Incomplete {
   public open val list: NodeList

   init {
      this.list = list
   }

   public open val isActive: Boolean
      public open get() {
         return false
      }


   public override fun toString(): String {
      return if (DEBUG) this.list.getString("New") else super.toString()
   }
}
