package maki.lodcpu;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

final class SharedWorkBudgetTest {
    private static Settings settings() {
        Settings s = new Settings();
        s.highMspt = 50;
        s.highHeap = 75;
        s.memoryReserveMb = 512;
        s.gcPressurePercent = 20;
        s.backfillIoPressurePercent = 25;
        s.backfillLodQueueHigh = 64;
        s.backfillWriteQueueHigh = 32;
        s.backfillMinSpacingSeconds = 2;
        s.backfillMaxSafeWaitSeconds = 30;
        s.backfillSampleMaxAgeSeconds = 10;
        return s;
    }

    private static SharedWorkBudget.Sample safe(long now, int inFlight) {
        return new SharedWorkBudget.Sample(
            now, 0, 12, 40, 3, 4096, 0, 2,
            5, 5, 8, inFlight, 0, 0, 0, false
        );
    }

    @Test
    void followReservationYieldsOneSlotWithoutStoppingFollow() {
        SharedWorkBudget budget = new SharedWorkBudget();
        Settings s = settings();
        long now = 100_000_000_000L;
        budget.observe(safe(now, 7));

        SharedWorkBudget.Decision decision = budget.reserve(now, "follow", 8, s);

        assertTrue(decision.reserved());
        assertTrue(decision.ready());
        assertEquals("granted", decision.reason());
        assertEquals(7, budget.effectiveFollowCap(8));
    }

    @Test
    void externalChunksmithTaskRemainsProtected() {
        SharedWorkBudget budget = new SharedWorkBudget();
        Settings s = settings();
        long now = 100_000_000_000L;
        budget.observe(safe(now, 1));

        SharedWorkBudget.Decision decision = budget.reserve(now, "external", 8, s);

        assertFalse(decision.reserved());
        assertEquals("external-chunksmith-task", decision.reason());
        assertEquals(8, budget.effectiveFollowCap(8));
    }

    @Test
    void ioPressureBlocksNewBackfillEvenAfterDemand() {
        SharedWorkBudget budget = new SharedWorkBudget();
        Settings s = settings();
        long now = 100_000_000_000L;
        SharedWorkBudget.Sample sample = safe(now, 1);
        budget.observe(new SharedWorkBudget.Sample(
            now, sample.players(), sample.mspt(), sample.heap(), sample.gc(),
            sample.memoryHeadroomMb(), sample.memoryPsi(), 40,
            sample.lodWritesPerSecond(), sample.followRequestsPerSecond(),
            sample.nativeWidth(), sample.inFlight(), sample.lodQueue(),
            sample.worldWriteQueue(), sample.synchronousWrites(), sample.nativeHeld()
        ));

        SharedWorkBudget.Decision decision = budget.reserve(now, "follow", 8, s);

        assertFalse(decision.reserved());
        assertEquals("io-pressure", decision.reason());
    }

    @Test
    void widthOneFollowCannotStarveBackfillForeverWhenSafe() {
        SharedWorkBudget budget = new SharedWorkBudget();
        Settings s = settings();
        long start = 100_000_000_000L;
        budget.observe(safe(start, 1));

        SharedWorkBudget.Decision first = budget.reserve(start, "follow", 1, s);
        assertFalse(first.reserved());
        assertEquals("follow-width-one-fairness-wait", first.reason());

        long later = start + s.backfillMaxSafeWaitSeconds * 1_000_000_000L;
        budget.observe(safe(later, 1));
        SharedWorkBudget.Decision laterDecision = budget.reserve(later, "follow", 1, s);

        assertTrue(laterDecision.reserved());
        assertTrue(laterDecision.ready());
        assertEquals(1, budget.effectiveFollowCap(1));
    }

    @Test
    void playerArrivalSuppressesPendingReservation() {
        SharedWorkBudget budget = new SharedWorkBudget();
        Settings s = settings();
        long now = 100_000_000_000L;
        budget.observe(safe(now, 1));
        assertTrue(budget.reserve(now, "follow", 8, s).reserved());

        budget.observe(new SharedWorkBudget.Sample(
            now + 1_000_000_000L, 1, 12, 40, 3, 4096, 0, 2,
            5, 5, 8, 1, 0, 0, 0, false
        ));
        SharedWorkBudget.Decision decision = budget.reserve(now + 1_000_000_000L, "follow", 8, s);

        assertFalse(decision.reserved());
        assertEquals("players-online", decision.reason());
        assertEquals(8, budget.effectiveFollowCap(8));
    }

    @Test
    void inFlightFollowWorkDrainsToReservedCapBeforeBackfillStarts() {
        SharedWorkBudget budget = new SharedWorkBudget();
        Settings s = settings();
        long now = 100_000_000_000L;
        budget.observe(safe(now, 8));

        SharedWorkBudget.Decision draining = budget.reserve(now, "follow", 8, s);
        assertTrue(draining.reserved());
        assertFalse(draining.ready());
        assertEquals("follow-draining-to-reserved-cap", draining.reason());

        long later = now + 1_000_000_000L;
        budget.observe(safe(later, 7));
        SharedWorkBudget.Decision ready = budget.reserve(later, "follow", 8, s);
        assertTrue(ready.ready());
        assertEquals("granted", ready.reason());
    }
}
