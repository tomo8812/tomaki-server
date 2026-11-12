package maki.chunksmithcontroller;

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
    void completedBackfillLeavesOneWholeGovernorMeasurementWindow() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        s.interval=5;
        s.backfillMinSpacingSeconds=2;
        s.backfillMaxSafeWaitSeconds=30;
        long now=100_000_000_000L;
        budget.observe(safe(now,0,40));
        assertTrue(budget.request(now,"follow",s).reserved());
        budget.noteFollowInFlight(0);
        assertTrue(budget.request(now+1_000_000L,"follow",s).ready());
        budget.backfillStarted(now+100_000_000L);
        budget.release(now+1_100_000_000L,"completed",s);
        budget.noteCleanFollowSample();

        long beforeCleanWindow=now+11_000_000_000L;
        budget.observe(safe(beforeCleanWindow,0,40));
        SharedWorkBudget.Decision blocked=budget.request(beforeCleanWindow,"follow",s);
        assertFalse(blocked.reserved());
        assertEquals("dynamic-cooldown",blocked.reason());

        long afterCleanWindow=now+11_200_000_000L;
        budget.observe(safe(afterCleanWindow,0,40));
        assertTrue(budget.request(afterCleanWindow,"follow",s).reserved());
        budget.noteFollowInFlight(0);
        assertTrue(budget.request(afterCleanWindow+1_000_000L,"follow",s).ready());
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

    @Test
    void transitionSerialMarksPauseThatFitsBetweenGovernorSamples() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        long now=100_000_000_000L;
        budget.observe(safe(now,0,40));
        long before=budget.transitionSerial();

        assertTrue(budget.request(now,"none",s).ready());
        long reserved=budget.transitionSerial();
        assertTrue(reserved>before);

        budget.backfillStarted(now+100_000_000L);
        budget.release(now+500_000_000L,"completed",s);
        assertTrue(budget.transitionSerial()>reserved);
        assertFalse(budget.reserved());
    }

    @Test
    void cleanFollowMeasurementWindowOverridesShortConfiguredSpacing() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        s.interval=2;
        s.backfillMinSpacingSeconds=2;
        s.backfillMaxSafeWaitSeconds=30;
        long now=100_000_000_000L;
        budget.observe(safe(now,0,40));
        assertTrue(budget.request(now,"follow",s).reserved());
        budget.noteFollowInFlight(0);
        assertTrue(budget.request(now+1_000_000L,"follow",s).ready());
        budget.backfillStarted(now+100_000_000L);
        long releasedAt=now+500_000_000L;
        budget.release(releasedAt,"completed",s);
        budget.noteCleanFollowSample();

        long beforeCleanWindow=releasedAt+3_900_000_000L;
        budget.observe(safe(beforeCleanWindow,0,40));
        SharedWorkBudget.Decision blocked=budget.request(beforeCleanWindow,"follow",s);
        assertFalse(blocked.reserved());
        assertEquals("dynamic-cooldown",blocked.reason());

        long afterCleanWindow=releasedAt+4_100_000_000L;
        budget.observe(safe(afterCleanWindow,0,40));
        assertTrue(budget.request(afterCleanWindow,"follow",s).reserved());
        budget.noteFollowInFlight(0);
        assertTrue(budget.request(afterCleanWindow+1_000_000L,"follow",s).ready());
    }

    @Test
    void settingsRejectMaxWaitShorterThanCleanMeasurementWindow() {
        java.util.Properties properties=new java.util.Properties();
        properties.setProperty("sample-seconds","10");
        properties.setProperty("backfill-max-safe-wait-seconds","10");

        assertThrows(IllegalArgumentException.class,()->Settings.parse(properties));
    }

    @Test
    void followBackfillRequiresAnExplicitCleanGovernorSampleBeforeAnotherSlice() {
        SharedWorkBudget budget=new SharedWorkBudget();
        Settings s=settings();
        s.interval=2;
        long now=100_000_000_000L;
        budget.observe(safe(now,0,40));

        SharedWorkBudget.Decision drain=budget.request(now,"follow",s);
        assertTrue(drain.reserved());
        budget.noteFollowInFlight(0);
        assertTrue(budget.request(now+100_000_000L,"follow",s).ready());
        budget.backfillStarted(now+200_000_000L);
        long releasedAt=now+500_000_000L;
        budget.release(releasedAt,"completed",s);

        long afterCooldown=releasedAt+5_000_000_000L;
        budget.observe(safe(afterCooldown,0,40));
        SharedWorkBudget.Decision waiting=budget.request(afterCooldown,"follow",s);
        assertFalse(waiting.reserved());
        assertEquals("waiting-clean-follow-sample",waiting.reason());

        budget.noteCleanFollowSample();
        SharedWorkBudget.Decision next=budget.request(afterCooldown+100_000_000L,"follow",s);
        assertTrue(next.reserved());
        assertEquals("draining-follow-dispatch",next.reason());
    }
    @Test
    void longExclusiveSliceRepaysActualServiceBeyondNominalCooldownCap() {
        SharedWorkBudget budget=new SharedWorkBudget(); Settings s=settings();
        long start=100_000_000_000L;
        budget.observe(safe(start,0,80));
        assertTrue(budget.request(start,"follow",s).reserved());
        budget.noteFollowInFlight(0);
        budget.backfillStarted(start+1_000_000_000L);
        long release=start+60_000_000_000L;
        budget.release(release,"snapshot-complete",s);
        budget.noteCleanFollowSample();
        long tooEarly=release+31_000_000_000L;
        budget.observe(safe(tooEarly,0,80));
        assertFalse(budget.request(tooEarly,"follow",s).reserved());
        long repaid=release+301_000_000_000L;
        budget.observe(safe(repaid,0,80));
        assertTrue(budget.request(repaid,"follow",s).reserved());
    }

    @Test
    void storagePressureBlocksOnlyAdmissionAndRecoversWithoutLifecycleReset() {
        SharedWorkBudget budget=new SharedWorkBudget(); Settings s=settings();
        long now=100_000_000_000L; budget.observe(safe(now,0,40));
        budget.pipelinePressure("cache-memory-pressure");
        assertFalse(budget.request(now,"none",s).reserved());
        assertFalse(budget.blockFollowDispatch());
        budget.pipelinePressure(null);
        assertTrue(budget.request(now+1_000_000L,"none",s).ready());
    }

}
