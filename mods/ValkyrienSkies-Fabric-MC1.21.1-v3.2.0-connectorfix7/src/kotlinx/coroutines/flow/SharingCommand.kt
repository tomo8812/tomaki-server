package kotlinx.coroutines.flow

public enum class SharingCommand {
   START,
   STOP,
   STOP_AND_RESET_REPLAY_CACHE;

   @JvmStatic
   @JvmSynthetic
   fun `$values`(): Array<SharingCommand> {
      arrayOf(START, STOP, STOP_AND_RESET_REPLAY_CACHE)
   }
}
