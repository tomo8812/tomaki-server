package kotlinx.coroutines.flow

public fun interface SharingStarted {
   @JvmStatic
   SharingStarted.Companion Companion = SharingStarted.Companion.$$INSTANCE;

   public abstract fun command(subscriptionCount: StateFlow<Int>): Flow<SharingCommand> {
   }

   public companion object {
      @JvmStatic
      @JvmSynthetic
      SharingStarted.Companion $$INSTANCE = SharingStarted.Companion();
      public final val Eagerly: SharingStarted = StartedEagerly() as SharingStarted
      public final val Lazily: SharingStarted = StartedLazily() as SharingStarted

      public fun WhileSubscribed(stopTimeoutMillis: Long = 0L, replayExpirationMillis: Long = java.lang.Long.MAX_VALUE): SharingStarted {
         return StartedWhileSubscribed(stopTimeoutMillis, replayExpirationMillis)
      }
   }
}
