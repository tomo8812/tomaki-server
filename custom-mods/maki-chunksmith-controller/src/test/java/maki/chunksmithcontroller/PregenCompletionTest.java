package maki.chunksmithcontroller;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Completion observations affect credits and measurements only. */
class PregenCompletionTest {
    private static final Class<?> FLOW=PregenFlowController.class;
    private static Object field(Object target,String name)throws Exception{
        Field f=(target instanceof Class<?> type?type:target.getClass()).getDeclaredField(name);f.setAccessible(true);
        return f.get(target instanceof Class<?>?null:target);
    }
    private static void set(Object target,String name,Object value)throws Exception{
        Field f=(target instanceof Class<?> type?type:target.getClass()).getDeclaredField(name);f.setAccessible(true);
        f.set(target instanceof Class<?>?null:target,value);
    }
    @BeforeEach void reset()throws Exception{
        PregenFlowController.clear();NativeLodTelemetry.clear();NativeLodTelemetry.observeWrites();
        set(FLOW,"pairConfirmedRuntime",0L);set(FLOW,"uncertain",0L);set(FLOW,"pairNew",0L);set(FLOW,"pairRepair",0L);
    }
    @SuppressWarnings({"unchecked","rawtypes"})
    private Object attempt(String token)throws Exception{
        Class<?> keyType=Class.forName(FLOW.getName()+"$Key");var keyConstructor=keyType.getDeclaredConstructors()[0];keyConstructor.setAccessible(true);
        Object key=keyConstructor.newInstance("minecraft:overworld",(long)token.hashCode());
        Class<?> type=Class.forName(FLOW.getName()+"$Attempt");var constructor=type.getDeclaredConstructors()[0];constructor.setAccessible(true);
        Object a=constructor.newInstance(key,null,0L,token);set(a,"actualStarted",true);set(a,"workClass",PregenFlowController.WorkClass.NEW);
        ((EnumSet)field(a,"held")).addAll(EnumSet.allOf(PregenFlowController.Stage.class));
        for(Object state:((Map<?,?>)field(FLOW,"STAGES")).values())set(state,"outstanding",(int)field(state,"outstanding")+1);
        ((Map)field(FLOW,"ACTIVE")).put(key,a);((Map)field(FLOW,"BY_TOKEN")).put(token,a);return a;
    }
    private com.kishku7.chunksmith.GenerationTask owner(Object a)throws Exception{
        var f=Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");f.setAccessible(true);Object unsafe=f.get(null);
        var owner=(com.kishku7.chunksmith.GenerationTask)unsafe.getClass().getMethod("allocateInstance",Class.class).invoke(unsafe,com.kishku7.chunksmith.GenerationTask.class);
        set(a,"owner",owner);return owner;
    }
    private int outstanding(PregenFlowController.Stage stage)throws Exception{
        return (int)field(((Map<?,?>)field(FLOW,"STAGES")).get(stage),"outstanding");
    }
    private long pairs()throws Exception{return (long)field(FLOW,"pairConfirmedRuntime");}
    private void finish(String token,PregenFlowController.Stage stage,boolean success){
        PregenFlowController.physicalStart(token,stage);PregenFlowController.physicalTerminal(token,stage,1000,success);
    }
    private void allReleased()throws Exception{
        for(var stage:PregenFlowController.Stage.values())assertEquals(0,outstanding(stage));
        assertTrue(((Map<?,?>)field(FLOW,"ACTIVE")).isEmpty());assertTrue(((Map<?,?>)field(FLOW,"BY_TOKEN")).isEmpty());
    }
    @Test void fullAndLodCompleteInEitherOrderAndCountOnce()throws Exception{
        attempt("one");PregenFlowController.physicalStart("one",PregenFlowController.Stage.LOD);
        finish("one",PregenFlowController.Stage.FULL,true);assertEquals(0,pairs());
        finish("one",PregenFlowController.Stage.LOD,true);finish("one",PregenFlowController.Stage.LOD,true);assertEquals(1,pairs());
        attempt("two");finish("two",PregenFlowController.Stage.LOD,true);finish("two",PregenFlowController.Stage.FULL,true);
        assertEquals(2,pairs());allReleased();
    }
    @Test void fullFailureReleasesUnstartedLodWithoutKeepingRecoveryState()throws Exception{
        attempt("failed");finish("failed",PregenFlowController.Stage.FULL,false);
        assertEquals(0,pairs());assertEquals(1L,field(FLOW,"uncertain"));allReleased();
    }
    @Test void lodFailureDoesNotCountPairAndReleasesObservation()throws Exception{
        attempt("failed-lod");PregenFlowController.physicalStart("failed-lod",PregenFlowController.Stage.LOD);
        finish("failed-lod",PregenFlowController.Stage.FULL,true);finish("failed-lod",PregenFlowController.Stage.LOD,false);
        assertEquals(0,pairs());allReleased();
    }
    @Test void nativeSkipOnlyReleasesPredictiveCredits()throws Exception{
        Object a=attempt("skip");set(a,"actualStarted",false);Object key=field(a,"key");long pos=(long)field(key,"pos");
        PregenFlowController.nativeCoordinateFinished("minecraft:overworld",(int)(pos>>32),(int)pos,null);
        assertEquals(0,pairs());allReleased();
    }
    @Test void newAndExistingWorkAreMeasuredSeparately()throws Exception{
        attempt("new");finish("new",PregenFlowController.Stage.LOD,true);finish("new",PregenFlowController.Stage.FULL,true);
        Object a=attempt("existing");set(a,"workClass",PregenFlowController.WorkClass.REPAIR);
        finish("existing",PregenFlowController.Stage.LOD,true);finish("existing",PregenFlowController.Stage.FULL,true);
        assertEquals(1L,field(FLOW,"pairNew"));assertEquals(1L,field(FLOW,"pairRepair"));allReleased();
    }
    @Test void noLodOfferOrUnavailableLodEndsObservation()throws Exception{
        attempt("missing");finish("missing",PregenFlowController.Stage.FULL,true);allReleased();
        attempt("disabled");PregenFlowController.physicalStart("disabled",PregenFlowController.Stage.FULL);
        PregenFlowController.stageUnavailable("disabled",PregenFlowController.Stage.LOD);finish("disabled",PregenFlowController.Stage.FULL,true);
        assertEquals(0,pairs());allReleased();
    }
    @Test void taskReturnReleasesCreditsButAcceptsLateLodAck()throws Exception{
        Object a=attempt("queued");var task=owner(a);PregenFlowController.physicalStart("queued",PregenFlowController.Stage.LOD);
        finish("queued",PregenFlowController.Stage.FULL,true);PregenFlowController.taskFinished(task);
        assertEquals(0,outstanding(PregenFlowController.Stage.LOD));assertTrue(PregenFlowController.tracked("queued"));
        finish("queued",PregenFlowController.Stage.LOD,true);assertEquals(1,pairs());allReleased();
    }
    @Test void taskReturnBeforeFullStillObservesLateLodSubmission()throws Exception{
        Object a=attempt("late");var task=owner(a);PregenFlowController.physicalStart("late",PregenFlowController.Stage.FULL);
        PregenFlowController.taskFinished(task);assertEquals(0L,field(FLOW,"uncertain"));
        PregenFlowController.physicalStart("late",PregenFlowController.Stage.LOD);
        finish("late",PregenFlowController.Stage.FULL,true);finish("late",PregenFlowController.Stage.LOD,true);
        assertEquals(1,pairs());allReleased();
    }
    @Test void staleObservationReturnsCreditsWithoutTouchingNativeFuture()throws Exception{
        Object a=attempt("slow");var nativeFuture=new java.util.concurrent.CompletableFuture<Void>();
        PregenFlowController.physicalStart("slow",PregenFlowController.Stage.FULL);
        set(a,"progressNanos",System.nanoTime()-61_000_000_000L);PregenFlowController.expireReceipts();
        assertFalse(nativeFuture.isDone());allReleased();
        attempt("fresh");finish("slow",PregenFlowController.Stage.FULL,true);
        assertEquals(1,outstanding(PregenFlowController.Stage.FULL));assertEquals(0,pairs());
    }
    @Test void supersededRecordOnlyReleasesCredit()throws Exception{
        attempt("superseded");PregenFlowController.physicalStart("superseded",PregenFlowController.Stage.LOD);
        finish("superseded",PregenFlowController.Stage.FULL,true);PregenFlowController.superseded("superseded",1000);
        assertEquals(0,pairs());allReleased();
    }
    @Test void acknowledgementBelongsToExactRecordIdentity()throws Exception{
        attempt("record");Class<?> type=Class.forName(NativeLodTelemetry.class.getName()+"$Offer");
        var ctor=type.getDeclaredConstructors()[0];ctor.setAccessible(true);
        @SuppressWarnings("unchecked") ThreadLocal<Object> current=(ThreadLocal<Object>)field(NativeLodTelemetry.class,"CURRENT");
        var exact=new com.kishku7.chunksmith.lod.CsLodChunk("minecraft:overworld",0,0,0,java.util.List.of(),java.util.List.of(),java.util.List.of());
        var other=new com.kishku7.chunksmith.lod.CsLodChunk("minecraft:overworld",0,0,0,java.util.List.of(),java.util.List.of(),java.util.List.of());
        current.set(ctor.newInstance("record",0,0));try{maki.chunksmithfixes.LodWriteEvents.submitted(exact);}finally{current.remove();}
        maki.chunksmithfixes.LodWriteEvents.completed(other,1000,maki.chunksmithfixes.LodWriteEvents.Outcome.WRITTEN);
        assertEquals(1,outstanding(PregenFlowController.Stage.LOD));
        maki.chunksmithfixes.LodWriteEvents.completed(exact,1000,maki.chunksmithfixes.LodWriteEvents.Outcome.WRITTEN);
        assertEquals(0,outstanding(PregenFlowController.Stage.LOD));
    }
}
