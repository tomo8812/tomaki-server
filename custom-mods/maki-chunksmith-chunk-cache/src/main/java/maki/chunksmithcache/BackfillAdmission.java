package maki.chunksmithcache;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/** Admission ordering only. Call on the server thread; suppliers own all live state. */
final class BackfillAdmission {
    record Decision(boolean ready, boolean afterDrain, String rejection) {}
    static Decision evaluate(boolean held, Supplier<String> check, BooleanSupplier reserve) {
        if (!held) {
            String reason = check.get();
            if (reason != null) return new Decision(false, false, reason);
        }
        if (!reserve.getAsBoolean()) return new Decision(false, false, null);
        String reason = check.get(); // Mandatory even after a successful preflight.
        return new Decision(reason == null, true, reason);
    }
    private BackfillAdmission() {}
}
