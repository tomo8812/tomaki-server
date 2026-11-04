package kotlinx.coroutines.channels

public enum class BufferOverflow {
   SUSPEND,
   DROP_OLDEST,
   DROP_LATEST;

   @JvmStatic
   @JvmSynthetic
   fun `$values`(): Array<BufferOverflow> {
      arrayOf(SUSPEND, DROP_OLDEST, DROP_LATEST)
   }
}
