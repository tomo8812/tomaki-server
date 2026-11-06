package maki.lodcpu;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

final class AdaptivePolicyBackfillPauseTest {
    private static Settings settings() {
        Settings s=new Settings();
        s.min=1;
        s.max=16;
        s.highMspt=50;
        s.emergencyMspt=100;
        s.highHeap=75;
        s.memoryReserveMb=512;
        s.gcPressurePercent=20;
        return s;
    }

    private static AdaptiveSample safe(double mspt) {
        return new AdaptiveSample(
            5,55,mspt,40,2,4096,
            0,0,true,0,0,0,-1,0,false,0,0
        );
    }

    @Test
    void intentionalBackfillPausePreservesExperimentAndCap() {
        AdaptivePolicy policy=new AdaptivePolicy();
        Settings s=settings();
        policy.cap=6;
        policy.phase="probe";

        int cap=policy.updateSafetyOnly(safe(12),0,s);

        assertEquals(6,cap);
        assertEquals("probe",policy.phase);
        assertEquals("shared-backfill-pause",policy.reason);
    }

    @Test
    void emergencyTickPressureStillReducesCapDuringBackfillPause() {
        AdaptivePolicy policy=new AdaptivePolicy();
        Settings s=settings();
        policy.cap=8;

        int cap=policy.updateSafetyOnly(safe(120),0,s);

        assertTrue(cap<8);
        assertEquals("emergency-tick-pressure",policy.reason);
    }

    @Test
    void playerArrivalStillForcesOnlineSafetyPolicy() {
        AdaptivePolicy policy=new AdaptivePolicy();
        Settings s=settings();
        policy.cap=8;

        int cap=policy.updateSafetyOnly(safe(12),1,s);

        assertEquals(1,cap);
        assertEquals("players-online-paused",policy.reason);
    }
}
