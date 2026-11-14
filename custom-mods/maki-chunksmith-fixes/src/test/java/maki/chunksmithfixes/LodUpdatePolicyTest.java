package maki.chunksmithfixes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LodUpdatePolicyTest {
    @Test void oneBlockIsNotEnoughAndThresholdIsCumulative(){
        var count=new LodChangeCounter();count.changed(0);assertFalse(count.due(64));
        for(int i=1;i<64;i++)count.changed(0);assertTrue(count.due(64));
    }
    @Test void onlySavedSnapshotChangesAreAcknowledged(){
        var count=new LodChangeCounter();for(int i=0;i<64;i++)count.changed(0);long snapshot=count.snapshot();
        for(int i=0;i<64;i++)count.changed(0);count.saved(snapshot);
        assertTrue(count.due(64));assertFalse(count.clean());
        count.saved(count.snapshot());assertTrue(count.clean());
    }
    @Test void smallerChangesDuringWriteCountTowardNextThreshold(){
        var count=new LodChangeCounter();for(int i=0;i<64;i++)count.changed(0);long snapshot=count.snapshot();
        count.changed(0);count.saved(snapshot);assertFalse(count.due(64));assertFalse(count.clean());
        for(int i=1;i<64;i++)count.changed(0);assertTrue(count.due(64));
    }
    @Test void failureDoesNotAdvanceSavedSnapshotAndOldAckCannotUndoNewerAck(){
        var count=new LodChangeCounter();for(int i=0;i<64;i++)count.changed(0);long old=count.snapshot();
        assertTrue(count.due(64));count.changed(0);count.saved(count.snapshot());count.saved(old);assertTrue(count.clean());
    }
    @Test void restoredUpdateIsEligibleWithoutNewChanges(){
        var count=new LodChangeCounter();count.restore(64,0);assertTrue(count.due(64));
    }
    @Test void eachChangeRestartsQuietPeriodEvenAfterThreshold(){
        var count=new LodChangeCounter();for(int i=0;i<32;i++)count.changed(0);
        assertFalse(count.ready(32,59,60));
        count.changed(59);assertFalse(count.ready(32,60,60));
        assertFalse(count.ready(32,118,60));assertTrue(count.ready(32,119,60));
    }
    @Test void quietTimeCannotBypassThresholdAndRestartedTargetsAlsoWait(){
        var count=new LodChangeCounter();count.changed(0);assertFalse(count.ready(32,120,60));
        count.restore(32,120);assertFalse(count.ready(32,179,60));assertTrue(count.ready(32,180,60));
    }
}
