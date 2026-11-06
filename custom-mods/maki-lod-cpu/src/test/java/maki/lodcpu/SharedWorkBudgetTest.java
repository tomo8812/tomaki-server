package maki.lodcpu;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

final class SharedWorkBudgetTest {
    private static Settings settings() {
        Settings s=new Settings();
        s.highMspt=50;
        s.highHeap=75;
        s.memoryReserveMb=512;
        s.gcPressurePercent=20;
        s.backfillIoPressurePercent=25;
        s.backfillLodQueueHigh=64;
        s.backfillWriteQueueHigh=32;
        s.backfillMinSpacingSeconds=2;
        s.backfillMaxSafeWaitSeconds=30;
        s.backfillDrainWaitSeconds=10;
        s.backfillSampleMaxAgeSeconds=10;
        return s;
    }

    private static SharedWorkBudget.Sample safe(long now,int players,double cpu) {
        return new SharedWorkBudget.Sample(
            now,players,cpu,12,40,3,4096,0,2,
            5,5,8,2,0,0,0,false
        );
    }

    @Test
    void followDispatchDrainsWithoutStoppingTaskBeforeBackfill() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        long now=100_000_000_000L;
        budget.observe(safe(now,0,75));

        SharedWorkBudget.Decision draining=budget.request(now,"follow",s);
        assertTrue(draining.reserved());
        assertFalse(draining.ready());
        assertEquals("draining-follow-dispatch",draining.reason());
        assertTrue(budget.blockFollowDispatch());

        budget.noteFollowInFlight(1);
        assertFalse(budget.request(now+100_000_000L,"follow",s).ready());

        budget.noteFollowInFlight(0);
        SharedWorkBudget.Decision ready=budget.request(now+200_000_000L,"follow",s);
        assertTrue(ready.reserved());
        assertTrue(ready.ready());
        assertEquals("granted",ready.reason());
    }

    @Test
    void barrierRemainsUntilBackfillReleasesBudget() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        long now=100_000_000_000L;
        budget.observe(safe(now,0,75));
        budget.request(now,"follow",s);
        budget.noteFollowInFlight(0);
        assertTrue(budget.request(now+100_000_000L,"follow",s).ready());

        budget.backfillStarted(now+200_000_000L);
        assertTrue(budget.blockFollowDispatch());

        budget.release(now+1_200_000_000L,"completed",s);
        assertFalse(budget.blockFollowDispatch());
    }

    @Test
    void externalTaskIsNeverClaimedOrBlocked() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        long now=100_000_000_000L;
        budget.observe(safe(now,0,75));

        SharedWorkBudget.Decision decision=budget.request(now,"external",s);
        assertFalse(decision.reserved());
        assertFalse(decision.ready());
        assertEquals("external-chunksmith-task",decision.reason());
        assertFalse(budget.blockFollowDispatch());
    }

    @Test
    void pressurePreventsReservationBeforeFollowIsDisturbed() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        long now=100_000_000_000L;
        SharedWorkBudget.Sample base=safe(now,0,75);
        budget.observe(new SharedWorkBudget.Sample(
            now,0,base.cpu(),base.mspt(),base.heap(),base.gc(),base.memoryHeadroomMb(),
            base.memoryPsi(),40,base.lodWritesPerSecond(),base.followRequestsPerSecond(),
            base.nativeWidth(),base.inFlight(),base.lodQueue(),base.worldWriteQueue(),0,false
        ));

        SharedWorkBudget.Decision decision=budget.request(now,"follow",s);
        assertFalse(decision.reserved());
        assertEquals("io-pressure",decision.reason());
        assertFalse(budget.blockFollowDispatch());
    }

    @Test
    void cpuSaturationAloneDoesNotBlockSafeBackfill() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        long now=100_000_000_000L;
        budget.observe(safe(now,0,99));

        SharedWorkBudget.Decision decision=budget.request(now,"follow",s);
        assertTrue(decision.reserved());
        assertEquals("draining-follow-dispatch",decision.reason());
    }

    @Test
    void playerPresenceBlocksNewBackfill() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        long now=100_000_000_000L;
        budget.observe(safe(now,1,40));

        SharedWorkBudget.Decision decision=budget.request(now,"none",s);
        assertFalse(decision.reserved());
        assertEquals("players-online",decision.reason());
    }

    @Test
    void completedBackfillCreatesBoundedDynamicFollowWindow() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        long now=100_000_000_000L;
        budget.observe(safe(now,0,40));
        assertTrue(budget.request(now,"none",s).ready());
        budget.backfillStarted(now+100_000_000L);
        budget.release(now+1_100_000_000L,"completed",s);

        long immediate=now+1_200_000_000L;
        budget.observe(safe(immediate,0,40));
        SharedWorkBudget.Decision cooldown=budget.request(immediate,"none",s);
        assertFalse(cooldown.reserved());
        assertEquals("dynamic-cooldown",cooldown.reason());

        long afterMax=now+(s.backfillMaxSafeWaitSeconds+2L)*1_000_000_000L;
        budget.observe(safe(afterMax,0,40));
        assertTrue(budget.request(afterMax,"none",s).ready());
    }

    @Test
    void stalledDrainTimesOutAndRestoresFollowDispatch() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        long now=100_000_000_000L;
        budget.observe(safe(now,0,40));
        assertTrue(budget.request(now,"follow",s).reserved());

        long timeout=now+s.backfillDrainWaitSeconds*1_000_000_000L;
        budget.observe(safe(timeout,0,40));
        SharedWorkBudget.Decision decision=budget.request(timeout,"follow",s);
        assertFalse(decision.reserved());
        assertEquals("follow-drain-timeout",decision.reason());
        assertFalse(budget.blockFollowDispatch());
    }
}
