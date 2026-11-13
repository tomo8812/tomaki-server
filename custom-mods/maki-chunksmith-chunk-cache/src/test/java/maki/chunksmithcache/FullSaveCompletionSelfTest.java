package maki.chunksmithcache;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public final class FullSaveCompletionSelfTest {
    public static void main(String[] args) {
        var inFlight = new AtomicInteger(2);
        var releasedTickets = new AtomicInteger();
        var failed = new FullSaveCompletion[2];
        for (int i = 0; i < failed.length; i++) {
            var save = failed[i] = new FullSaveCompletion();
            save.requestDone.thenRun(inFlight::decrementAndGet);
            save.persisted.thenRun(releasedTickets::incrementAndGet);
            require(!save.failedAttempt() && !save.failedAttempt(), "short failures retain dispatch");
            require(!save.requestDone.isDone() && save.retryDelayTicks() == 20, "foreground retry");
            require(save.failedAttempt(), "third failure isolates this coordinate");
            save.deferRequest();
            require(!save.persisted.isDone(), "dispatch release is not a save ACK");
            require(!save.failedAttempt() && save.retryDelayTicks() == 1200, "background retry is rate limited");
        }
        require(inFlight.get() == 0 && releasedTickets.get() == 0, "failed slots drain while tickets remain");
        var healthy = new FullSaveCompletion();healthy.finished(true);
        require(healthy.requestDone.isDone() && healthy.persisted.join(), "another coordinate can finish");
        failed[0].finished(true);
        require(failed[0].persisted.join() && releasedTickets.get() == 1, "recovery releases only its ticket");
        failed[1].finished(false);
        require(!failed[1].persisted.join() && releasedTickets.get() == 2, "handoff does not claim cache publication");
        var stopped = new FullSaveCompletion();stopped.aborted(new IOException("stopped"));
        require(stopped.requestDone.isCompletedExceptionally() && !stopped.persisted.join(), "shutdown cannot claim saved FULL");
        System.out.println("FullSaveCompletionSelfTest PASS (dispatch isolation, retained save ACK, recovery, handoff, shutdown)");
    }
    private static void require(boolean ok, String reason) {if (!ok) throw new AssertionError(reason);}
}
