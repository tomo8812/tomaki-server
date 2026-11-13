package maki.chunksmithcontroller;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Drives the real completion controller with independently ordered storage events. */
class PregenCompletionTest {
    private static final Class<?> FLOW=PregenFlowController.class;
    private static Object field(Object target,String name)throws Exception{
        Field field=(target instanceof Class<?> type?type:target.getClass()).getDeclaredField(name);
        field.setAccessible(true);return field.get(target instanceof Class<?>?null:target);
    }
    private static void set(Object target,String name,Object value)throws Exception{
        Field field=(target instanceof Class<?> type?type:target.getClass()).getDeclaredField(name);
        field.setAccessible(true);field.set(target instanceof Class<?>?null:target,value);
    }
    @BeforeEach void reset()throws Exception{
        ((Map<?,?>)field(FLOW,"INCOMPLETE")).clear();
        ((Map<?,?>)field(FLOW,"ACTIVE")).clear();((Map<?,?>)field(FLOW,"BY_TOKEN")).clear();
        for(Object state:((Map<?,?>)field(FLOW,"STAGES")).values())set(state,"outstanding",0);
        set(FLOW,"pairConfirmedRuntime",0L);
        set(FLOW,"uncertain",0L);
        set(FLOW,"pairNew",0L);set(FLOW,"pairRepair",0L);
    }
    @SuppressWarnings({"unchecked","rawtypes"})
    private Object attempt(String token)throws Exception{
        Class<?> keyType=Class.forName(FLOW.getName()+"$Key");
        var keyConstructor=keyType.getDeclaredConstructors()[0];keyConstructor.setAccessible(true);
        Object key=keyConstructor.newInstance("minecraft:overworld",(long)token.hashCode());
        Class<?> attemptType=Class.forName(FLOW.getName()+"$Attempt");
        var constructor=attemptType.getDeclaredConstructors()[0];constructor.setAccessible(true);
        Object attempt=constructor.newInstance(key,null,0L,token);
        set(attempt,"actualStarted",true);
        set(attempt,"workClass",PregenFlowController.WorkClass.NEW);
        ((EnumSet)field(attempt,"held")).addAll(EnumSet.allOf(PregenFlowController.Stage.class));
        for(Object state:((Map<?,?>)field(FLOW,"STAGES")).values())set(state,"outstanding",(int)field(state,"outstanding")+1);
        ((Map)field(FLOW,"ACTIVE")).put(key,attempt);((Map)field(FLOW,"BY_TOKEN")).put(token,attempt);
        return attempt;
    }
    private long pairs()throws Exception{return (long)field(FLOW,"pairConfirmedRuntime");}
    private int outstanding(PregenFlowController.Stage stage)throws Exception{
        return (int)field(((Map<?,?>)field(FLOW,"STAGES")).get(stage),"outstanding");
    }
    private void finish(String token,PregenFlowController.Stage stage,boolean success){
        PregenFlowController.physicalStart(token,stage);
        PregenFlowController.physicalTerminal(token,stage,1000,success);
        if(stage==PregenFlowController.Stage.FULL&&success)PregenFlowController.fullPublished(token);
    }
    @Test void isolatedPreparationDebtDoesNotConsumeStageCredits()throws Exception{
        PregenFlowController.preparationFailed("minecraft:overworld",1,2);
        assertEquals(1,PregenFlowController.incompleteInRange("minecraft:overworld",0,0,4096));
        assertTrue(PregenFlowController.hasPreparationFailures("minecraft:overworld"));
        for(var stage:PregenFlowController.Stage.values())assertEquals(0,outstanding(stage));
        PregenFlowController.preparationSucceeded("minecraft:overworld",1,2);
        assertEquals(0,PregenFlowController.incompleteInRange("minecraft:overworld",0,0,4096));
        assertFalse(PregenFlowController.hasPreparationFailures("minecraft:overworld"));
    }
    @Test void oppositeOrdersAndDuplicatesCountOnce()throws Exception{
        attempt("one");finish("one",PregenFlowController.Stage.VALIDATE,true);
        finish("one",PregenFlowController.Stage.FULL,true);
        assertEquals(0,pairs());finish("one",PregenFlowController.Stage.LOD,true);
        assertEquals(1,pairs());PregenFlowController.fullPublished("one");PregenFlowController.lodCommitted("one");
        assertEquals(1,pairs());
        attempt("two");finish("two",PregenFlowController.Stage.LOD,true);
        finish("two",PregenFlowController.Stage.VALIDATE,true);
        finish("two",PregenFlowController.Stage.FULL,true);assertEquals(2,pairs());
    }
    @Test void differentTokensCannotFormPair()throws Exception{
        attempt("full");attempt("lod");
        PregenFlowController.fullPublished("full");PregenFlowController.lodCommitted("lod");
        assertEquals(0,pairs());
    }
    @Test void logicalFailureRetainsStartedPhysicalCreditUntilAck()throws Exception{
        attempt("slow");PregenFlowController.physicalStart("slow",PregenFlowController.Stage.FULL);
        PregenFlowController.logicalCaptureFailed("slow");
        assertEquals(1,outstanding(PregenFlowController.Stage.FULL));
        assertEquals(0,outstanding(PregenFlowController.Stage.LOD));
        PregenFlowController.physicalTerminal("slow",PregenFlowController.Stage.FULL,1000,true);
        assertEquals(0,outstanding(PregenFlowController.Stage.FULL));assertEquals(0,pairs());
        PregenFlowController.physicalTerminal("slow",PregenFlowController.Stage.FULL,1000,true);
        assertEquals(0,outstanding(PregenFlowController.Stage.FULL));
    }
    @Test void borrowedFullDoesNotCountAsNewFullEvenWhenNativeHintSaidNew()throws Exception{
        attempt("borrowed");
        PregenFlowController.fullReused("borrowed");
        finish("borrowed",PregenFlowController.Stage.LOD,true);
        assertEquals(1,pairs());assertEquals(0L,field(FLOW,"pairNew"));assertEquals(1L,field(FLOW,"pairRepair"));
        assertEquals(0,outstanding(PregenFlowController.Stage.FULL));
        assertEquals(0,outstanding(PregenFlowController.Stage.VALIDATE));
    }
    @Test void physicalWriteAloneDoesNotProvePublication()throws Exception{
        attempt("unpublished");finish("unpublished",PregenFlowController.Stage.VALIDATE,true);
        finish("unpublished",PregenFlowController.Stage.LOD,true);
        PregenFlowController.physicalStart("unpublished",PregenFlowController.Stage.FULL);
        PregenFlowController.physicalTerminal("unpublished",PregenFlowController.Stage.FULL,1000,true);
        assertEquals(0,pairs());
        PregenFlowController.fullPublished("unpublished");
        assertEquals(1,pairs());assertEquals(1L,field(FLOW,"pairNew"));
    }
    @Test void forcedUnclassifiedPublicationIsNotInventedNewGeneration()throws Exception{
        Object attempt=attempt("forced");set(attempt,"workClass",PregenFlowController.WorkClass.UNKNOWN);
        finish("forced",PregenFlowController.Stage.VALIDATE,true);
        finish("forced",PregenFlowController.Stage.LOD,true);
        finish("forced",PregenFlowController.Stage.FULL,true);
        assertEquals(1,pairs());assertEquals(0L,field(FLOW,"pairNew"));assertEquals(0L,field(FLOW,"pairRepair"));
    }
    @Test void failedFullSaveRemainsPendingUntilRetryPublishes()throws Exception{
        attempt("retry");PregenFlowController.fullPending("retry");
        finish("retry",PregenFlowController.Stage.LOD,true);
        PregenFlowController.physicalStart("retry",PregenFlowController.Stage.VALIDATE);
        PregenFlowController.logicalCaptureFailed("retry");
        assertFalse(PregenFlowController.reserveFullRetry("retry"),"real validation is still outstanding");
        PregenFlowController.physicalTerminal("retry",PregenFlowController.Stage.VALIDATE,1000,false);
        assertTrue(((Map<?,?>)field(FLOW,"BY_TOKEN")).containsKey("retry"));
        assertEquals(0,pairs());assertTrue(PregenFlowController.reserveFullRetry("retry"));
        finish("retry",PregenFlowController.Stage.VALIDATE,true);
        finish("retry",PregenFlowController.Stage.FULL,true);
        PregenFlowController.fullSettled("retry");
        assertEquals(1,pairs());assertFalse(((Map<?,?>)field(FLOW,"BY_TOKEN")).containsKey("retry"));
    }
    @Test void deferredFullSaveReleasesIdleCreditsButKeepsDebtAndRetryOwnership()throws Exception{
        attempt("deferred");PregenFlowController.fullPending("deferred");
        finish("deferred",PregenFlowController.Stage.LOD,true);
        PregenFlowController.fullSaveDeferred("deferred");
        for(var stage:PregenFlowController.Stage.values())assertEquals(0,outstanding(stage));
        assertEquals(1,((Map<?,?>)field(FLOW,"INCOMPLETE")).size());
        assertTrue(((Map<?,?>)field(FLOW,"BY_TOKEN")).containsKey("deferred"));
        assertTrue(PregenFlowController.reserveFullRetry("deferred"));
        finish("deferred",PregenFlowController.Stage.VALIDATE,true);
        finish("deferred",PregenFlowController.Stage.FULL,true);
        PregenFlowController.fullSaveResolved("deferred");
        PregenFlowController.fullSettled("deferred");
        assertEquals(0,((Map<?,?>)field(FLOW,"INCOMPLETE")).size());
        assertFalse(((Map<?,?>)field(FLOW,"BY_TOKEN")).containsKey("deferred"));
        assertEquals(0,pairs(),"isolated failed attempts must not become PAIR later");
    }
    @Test void deferredFullKeepsActualLodCreditUntilNativeAck()throws Exception{
        attempt("queued-lod");PregenFlowController.fullPending("queued-lod");
        PregenFlowController.physicalStart("queued-lod",PregenFlowController.Stage.LOD);
        PregenFlowController.fullSaveDeferred("queued-lod");
        assertEquals(1,outstanding(PregenFlowController.Stage.LOD));
        assertEquals(0,outstanding(PregenFlowController.Stage.FULL));
        assertEquals(0,outstanding(PregenFlowController.Stage.VALIDATE));
        finish("queued-lod",PregenFlowController.Stage.LOD,false);
        PregenFlowController.fullSaveResolved("queued-lod");
        PregenFlowController.fullSettled("queued-lod");
        assertEquals(1,((Map<?,?>)field(FLOW,"INCOMPLETE")).size(),"FULL recovery retains native LOD failure");
        assertEquals(0,outstanding(PregenFlowController.Stage.LOD));
    }
    @Test void playerHandoffSettlesPendingSaveWithoutClaimingNewFull()throws Exception{
        attempt("handoff");PregenFlowController.fullPending("handoff");
        finish("handoff",PregenFlowController.Stage.LOD,true);
        PregenFlowController.fullSettled("handoff");
        PregenFlowController.logicalCaptureFailed("handoff");
        assertEquals(0,pairs());assertEquals(1L,field(FLOW,"uncertain"));
        assertFalse(((Map<?,?>)field(FLOW,"BY_TOKEN")).containsKey("handoff"));
        assertEquals(0,outstanding(PregenFlowController.Stage.FULL));
    }
    @Test void nativeAckBelongsToExactRecord()throws Exception{
        attempt("record");
        Class<?> offerType=Class.forName(NativeLodTelemetry.class.getName()+"$Offer");
        var constructor=offerType.getDeclaredConstructors()[0];constructor.setAccessible(true);
        @SuppressWarnings("unchecked") ThreadLocal<Object> current=(ThreadLocal<Object>)field(NativeLodTelemetry.class,"CURRENT");
        var exact=new com.kishku7.chunksmith.lod.CsLodChunk("minecraft:overworld",0,0,0,java.util.List.of(),java.util.List.of(),java.util.List.of());
        var other=new com.kishku7.chunksmith.lod.CsLodChunk("minecraft:overworld",0,0,0,java.util.List.of(),java.util.List.of(),java.util.List.of());
        current.set(constructor.newInstance("record",0,0));
        try{NativeLodTelemetry.offered(exact);}finally{current.remove();}
        assertNull(NativeLodTelemetry.writing(other));
        assertEquals(1,outstanding(PregenFlowController.Stage.LOD));
        String token=NativeLodTelemetry.writing(exact);assertEquals("record",token);
        NativeLodTelemetry.written(token,1000,true);
        assertEquals(0,outstanding(PregenFlowController.Stage.LOD));
        assertNull(NativeLodTelemetry.writing(exact));
    }
    @Test void failureAndMissingSinkDoNotCountPair()throws Exception{
        attempt("failed");finish("failed",PregenFlowController.Stage.LOD,false);
        assertEquals(1,((Map<?,?>)field(FLOW,"INCOMPLETE")).size(),"failed native LOD ACK remains incomplete after WIP drains");
        PregenFlowController.fullPublished("failed");assertEquals(0,pairs());
        PregenFlowController.logicalCaptureFailed("failed");
        assertEquals(1L,field(FLOW,"uncertain"));
        attempt("no-sink");PregenFlowController.stageUnavailable("no-sink",PregenFlowController.Stage.LOD);
        PregenFlowController.fullPublished("no-sink");assertEquals(0,pairs());
    }
}
