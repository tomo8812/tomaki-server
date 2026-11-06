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
        s.backfillExclusiveMaxSeconds=60;
        s.backfillExclusiveMaxChunks=3;
        s.backfillFollowMinSliceSeconds=30;
        s.backfillDrainWaitSeconds=10;
        s.backfillSampleMaxAgeSeconds=10;
        return s;
    }

    private static SharedWorkBudget.Sample safe(long now) {
        return new SharedWorkBudget.Sample(
            now,0,75,12,40,3,4096,0,2,
            5,5,8,2,0,0,0,false
        );
    }

    @Test
    void followMustDrainBeforeExclusiveBackfillStarts() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        long now=100_000_000_000L;
        budget.observe(safe(now));

        SharedWorkBudget.Decision draining=budget.request(now,"follow",s);
        assertTrue(draining.reserved());
        assertFalse(draining.ready());
        assertEquals("pausing-follow",draining.reason());

        long later=now+1_000_000_000L;
        budget.observe(safe(later));
        SharedWorkBudget.Decision ready=budget.request(later,"none",s);
        assertTrue(ready.reserved());
        assertTrue(ready.ready());
        assertEquals("granted",ready.reason());
    }

    @Test
    void externalTaskIsNeverClaimedOrPaused() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        long now=100_000_000_000L;
        budget.observe(safe(now));

        SharedWorkBudget.Decision decision=budget.request(now,"external",s);
        assertFalse(decision.reserved());
        assertFalse(decision.ready());
        assertEquals("external-chunksmith-task",decision.reason());
    }

    @Test
    void storagePressureWaitsAfterFollowDrainThenTimesOut() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        long now=100_000_000_000L;
        SharedWorkBudget.Sample base=safe(now);
        budget.observe(new SharedWorkBudget.Sample(
            now,0,base.cpu(),base.mspt(),base.heap(),base.gc(),base.memoryHeadroomMb(),
            base.memoryPsi(),40,base.lodWritesPerSecond(),base.followRequestsPerSecond(),
            base.nativeWidth(),0,base.lodQueue(),base.worldWriteQueue(),0,false
        ));

        SharedWorkBudget.Decision waiting=budget.request(now,"none",s);
        assertTrue(waiting.reserved());
        assertFalse(waiting.ready());
        assertEquals("waiting-io-pressure",waiting.reason());

        long timeout=now+s.backfillDrainWaitSeconds*1_000_000_000L;
        budget.observe(new SharedWorkBudget.Sample(
            timeout,0,base.cpu(),base.mspt(),base.heap(),base.gc(),base.memoryHeadroomMb(),
            base.memoryPsi(),40,base.lodWritesPerSecond(),base.followRequestsPerSecond(),
            base.nativeWidth(),0,base.lodQueue(),base.worldWriteQueue(),0,false
        ));
        SharedWorkBudget.Decision released=budget.request(timeout,"none",s);
        assertFalse(released.reserved());
        assertEquals("pressure-timeout-io-pressure",released.reason());
    }

    @Test
    void completedBackfillsStayExclusiveUntilQuotaThenYieldToFollow() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        long now=100_000_000_000L;
        budget.observe(safe(now));
        assertTrue(budget.request(now,"none",s).ready());

        assertTrue(budget.completed(now+1_000_000_000L,"completed",s));
        assertTrue(budget.completed(now+2_000_000_000L,"completed",s));
        assertFalse(budget.completed(now+3_000_000_000L,"completed",s));

        long immediate=now+4_000_000_000L;
        budget.observe(safe(immediate));
        SharedWorkBudget.Decision slice=budget.request(immediate,"none",s);
        assertFalse(slice.reserved());
        assertEquals("follow-min-slice",slice.reason());
    }

    @Test
    void failureEndsExclusiveEpochImmediately() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        long now=100_000_000_000L;
        budget.observe(safe(now));
        assertTrue(budget.request(now,"none",s).ready());

        assertFalse(budget.completed(now+1_000_000_000L,"failed",s));
        assertFalse(budget.reserved());
    }

    @Test
    void abundantResourcesExpandButDoNotRemoveFairnessBound() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        long now=100_000_000_000L;
        SharedWorkBudget.Sample base=safe(now);
        budget.observe(new SharedWorkBudget.Sample(
            now,0,40,10,35,2,4096,0,1,8,8,8,0,0,0,0,false
        ));
        assertTrue(budget.request(now,"none",s).ready());

        for(int i=1;i<s.backfillExclusiveMaxChunks*2;i++) {
            assertTrue(budget.completed(now+i*1_000_000_000L,"completed",s));
        }
        assertFalse(budget.completed(now+s.backfillExclusiveMaxChunks*2L*1_000_000_000L,"completed",s));
    }

    @Test
    void playerPresenceBlocksBackfillWhenFollowIsAlreadyDrained() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        long now=100_000_000_000L;
        SharedWorkBudget.Sample base=safe(now);
        budget.observe(new SharedWorkBudget.Sample(
            now,1,base.cpu(),base.mspt(),base.heap(),base.gc(),base.memoryHeadroomMb(),
            base.memoryPsi(),base.ioPsi(),base.lodWritesPerSecond(),base.followRequestsPerSecond(),
            base.nativeWidth(),0,base.lodQueue(),base.worldWriteQueue(),0,false
        ));

        SharedWorkBudget.Decision decision=budget.request(now,"none",s);
        assertTrue(decision.reserved());
        assertFalse(decision.ready());
        assertEquals("waiting-players-online",decision.reason());
    }
}
