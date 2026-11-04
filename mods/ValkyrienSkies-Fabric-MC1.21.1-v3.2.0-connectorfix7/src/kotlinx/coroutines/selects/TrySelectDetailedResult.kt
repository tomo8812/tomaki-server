package kotlinx.coroutines.selects

internal enum class TrySelectDetailedResult {
   SUCCESSFUL,
   REREGISTER,
   CANCELLED,
   ALREADY_SELECTED;

   @JvmStatic
   @JvmSynthetic
   fun `$values`(): Array<TrySelectDetailedResult> {
      arrayOf(SUCCESSFUL, REREGISTER, CANCELLED, ALREADY_SELECTED)
   }
}
