package org.millenaire.content.validate;

import java.util.List;

public final class LoadCyclePreconditions {
   private LoadCyclePreconditions() {
   }

   public static void check(String contentLoadFailure, boolean captureInstalled, boolean captureCompleted, List<Finding> out) {
      if (contentLoadFailure != null) {
         out.add(
            Finding.of(
               "VALIDATOR",
               Severity.ERROR,
               "load",
               "load-cycle",
               null,
               "the last content load did not complete: " + contentLoadFailure,
               "fix the cause in the log."
            )
         );
      }

      if (!captureInstalled) {
         out.add(
            Finding.of(
               "VALIDATOR",
               Severity.ERROR,
               "load",
               "load-cycle",
               null,
               "the loader-diagnostic capture was not installed for the last load cycle — an empty capture proves nothing.",
               "check the Log4j backend."
            )
         );
      } else if (!captureCompleted) {
         out.add(
            Finding.of(
               "VALIDATOR",
               Severity.ERROR,
               "load",
               "load-cycle",
               null,
               "the loader-diagnostic capture window was never closed — the cycle did not run to its end.",
               "see the server log."
            )
         );
      }
   }
}
