package maki.lodcpu;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

final class AdaptivePolicyNeighbourSearchTest {
    private static Settings settings() {
        Settings s=new Settings();
        s.min=1;
        s.max=8;
        s.windowSeconds=5;
        s.settleSeconds=0;
        s.retrySeconds=0;
        s.minimumEvents=2;
        s.minimumGainPercent=5;
        s.highMspt=50;
        s.emergencyMspt=100;
        s.highHeap=75;
        s.memoryReserveMb=512;
        s.gcPressurePercent=20;
        return s;
    }

    private static AdaptiveSample sample(int width) {
        return new AdaptiveSample(5,50,12,40,1,4096,0,0,true,width,width,0,-1,0,false,0,0);
    }

    @Test
    void upwardProbeIsExactlyOneAndUsesConfirmedPairGain() {
        AdaptivePolicy policy=new AdaptivePolicy();
        Settings s=settings();
        policy.cap=2;
        assertEquals(3,policy.update(sample(2),0,s,5,true,false,0x000400040002L));
        assertEquals("probe",policy.phase);
        assertEquals(3,policy.update(sample(3),0,s,6,true,false,0x000400040002L));
        assertEquals("baseline",policy.phase);
        assertEquals("pair-goodput-gain",policy.reason);
    }

    @Test
    void changedFastStateMakesProbeInconclusive() {
        AdaptivePolicy policy=new AdaptivePolicy();
        Settings s=settings();
        policy.cap=2;
        assertEquals(3,policy.update(sample(2),0,s,5,true,false,11L));
        assertEquals(2,policy.update(sample(3),0,s,6,true,false,12L));
        assertEquals("fast-state-changed",policy.reason);
    }

    @Test
    void downstreamConstraintAbortsUpwardProbe() {
        AdaptivePolicy policy=new AdaptivePolicy();
        Settings s=settings();
        policy.cap=2;
        assertEquals(3,policy.update(sample(2),0,s,5,true,false,7L));
        assertEquals(2,policy.update(sample(3),0,s,6,true,true,7L));
        assertEquals("trial-downstream-constrained",policy.reason);
    }

    @Test
    void unchangedActualDispatchMakesProbeInconclusive() {
        AdaptivePolicy policy=new AdaptivePolicy();
        Settings s=settings();
        policy.cap=2;
        assertEquals(3,policy.update(sample(2),0,s,5,true,false,13L));
        assertEquals(2,policy.update(sample(2),0,s,6,true,false,13L));
        assertEquals("native-dispatch-unchanged",policy.reason);
    }

    @Test
    void changedWorkClassMixMakesProbeInconclusive() {
        AdaptivePolicy policy=new AdaptivePolicy();
        Settings s=settings();
        policy.cap=2;
        assertEquals(3,policy.update(sample(2),0,s,5,true,false,17L,5,0,0,100));
        assertEquals(2,policy.update(sample(3),0,s,6,true,false,17L,0,6,0,100));
        assertEquals("work-class-mix-changed",policy.reason);
    }

    @Test
    void unavailablePairProofDoesNotLearn() {
        AdaptivePolicy policy=new AdaptivePolicy();
        Settings s=settings();
        policy.cap=4;
        assertEquals(4,policy.update(sample(4),0,s,100,false,false,9L));
        assertEquals("pair-proof-unavailable",policy.reason);
    }
}
