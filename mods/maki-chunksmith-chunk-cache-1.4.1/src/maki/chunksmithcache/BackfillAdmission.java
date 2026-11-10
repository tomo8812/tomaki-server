package maki.chunksmithcache;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

final class BackfillAdmission {
   static BackfillAdmission.Decision evaluate(boolean held, Supplier<String> check, BooleanSupplier reserve) {
      if (!held) {
         String reason = check.get();
         if (reason != null) {
            return new BackfillAdmission.Decision(false, false, reason);
         }
      }

      if (!reserve.getAsBoolean()) {
         return new BackfillAdmission.Decision(false, false, null);
      }

      String reason = check.get();
      return new BackfillAdmission.Decision(reason == null, true, reason);
   }

   private BackfillAdmission() {
   }

   record Decision(boolean ready, boolean afterDrain, String rejection) {
   }
}
