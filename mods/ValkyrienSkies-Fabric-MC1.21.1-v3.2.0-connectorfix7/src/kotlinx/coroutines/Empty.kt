package kotlinx.coroutines

private class Empty(isActive: Boolean) : Incomplete {
   public open val isActive: Boolean

   init {
      this.isActive = isActive
   }

   public open val list: NodeList?
      public open get() {
         return null
      }


   public override fun toString(): String {
      return "Empty{${if (this.isActive) "Active" else "New"}}"
   }
}
