package maki.chunksmithcache;

import java.util.concurrent.CompletableFuture;

/** Native dispatch may drain while the live FULL still owns a persistence obligation. */
final class FullSaveCompletion {
    private static final int MAX_FOREGROUND_FAILURES = 3;
    final CompletableFuture<Void> requestDone = new CompletableFuture<>();
    final CompletableFuture<Boolean> persisted = new CompletableFuture<>();
    private int failures;

    boolean failedAttempt() {
        if (failures < MAX_FOREGROUND_FAILURES) failures++;
        return failures == MAX_FOREGROUND_FAILURES && !requestDone.isDone();
    }

    long retryDelayTicks() { return failures < MAX_FOREGROUND_FAILURES ? 20L : 1200L; }

    void deferRequest() { requestDone.complete(null); }

    void finished(boolean cached) {
        persisted.complete(cached);
        requestDone.complete(null);
    }

    void aborted(Throwable error) {
        persisted.complete(false);
        requestDone.completeExceptionally(error);
    }
}
