package fuzs.puzzleslib.api.event.v1.core;

import java.util.NoSuchElementException;
import org.jetbrains.annotations.Nullable;

public enum EventResult {
   PASS(false, null),
   INTERRUPT(true, Boolean.FALSE),
   ALLOW(true, Boolean.TRUE),
   DENY(true, Boolean.FALSE);

   private final boolean interrupt;
   @Nullable
   private final Boolean value;

   EventResult(boolean interrupt, @Nullable Boolean value) {
      this.interrupt = interrupt;
      this.value = value;
   }

   public boolean getAsBoolean() {
      if (this.value == null) {
         throw new NoSuchElementException("No value present");
      } else {
         return this.value;
      }
   }

   public boolean isInterrupt() {
      return this.interrupt;
   }

   public boolean isPass() {
      return !this.interrupt;
   }

   // $VF: synthetic method
   private static EventResult[] $values() {
      return new EventResult[]{PASS, INTERRUPT, ALLOW, DENY};
   }
}
