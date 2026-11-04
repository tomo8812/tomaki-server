package org.valkyrienskies.core.util

import java.time.Duration
import java.time.Instant

public class RateLimiter(minTimeSincePrev: Duration) {
   private final val minTimeSincePrev: Duration
   private final var lastCall: Instant

   init {
      this.minTimeSincePrev = minTimeSincePrev
      val var10001: Instant = Instant.MIN
      this.lastCall = var10001
   }

   public fun maybeRun(block: Runnable) {
      val now: Instant = Instant.now()
      if (Duration.between(this.lastCall, now).compareTo(this.minTimeSincePrev) > 0) {
         block.run()
         this.lastCall = now
      }
   }
}
