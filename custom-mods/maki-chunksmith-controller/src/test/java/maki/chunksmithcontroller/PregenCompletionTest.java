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
        ((Map<?,?>)field(FLOW,"ACTIVE")).clear();((Map<?,?>)field(FLOW,"BY_TOKEN")).clear();
        for(Object state:((Map<?,?>)field(FLOW,"STAGES")).values())set(state,"outstanding",0);
        set(FLOW,"pairConfirmedRuntime",0L);
        PregenFlowController.connectProofFeed("E_LITE_PAIR_V1");
    }
    @SuppressWarnings({"unchecked","rawtypes"})
    private void attempt(String token)throws Exception{
        Class<?> keyType=Class.forName(FLOW.getName()+"$Key");
        var keyConstructor=keyType.getDeclaredConstructors()[0];keyConstructor.setAccessible(true);
        Object key=keyConstructor.newInstance("minecraft:overworld",(long)token.hashCode());
        Class<?> attemptType=Class.forName(FLOW.getName()+"$Attempt");
        var constructor=attemptType.getDeclaredConstructors()[0];constructor.setAccessible(true);
        Object attempt=constructor.newInstance(key,null,0L,token);
        set(attempt,"actualStarted",true);
        ((EnumSet)field(attempt,"held")).addAll(EnumSet.allOf(PregenFlowController.Stage.class));
        for(Object state:((Map<?,?>)field(FLOW,"STAGES")).values())set(state,"outstanding",(int)field(state,"outstanding")+1);
        ((Map)field(FLOW,"ACTIVE")).put(key,attempt);((Map)field(FLOW,"BY_TOKEN")).put(token,attempt);
    }
    private long pairs()throws Exception{return (long)field(FLOW,"pairConfirmedRuntime");}
    private int outstanding(PregenFlowController.Stage stage)throws Exception{
        return (int)field(((Map<?,?>)field(FLOW,"STAGES")).get(stage),"outstanding");
    }
    private void finish(String token,PregenFlowController.Stage stage,boolean success){
        PregenFlowController.physicalStart(token,stage);
        PregenFlowController.physicalTerminal(token,stage,1000,success);
    }
    @Test void oppositeOrdersAndDuplicatesCountOnce()throws Exception{
        attempt("one");finish("one",PregenFlowController.Stage.VALIDATE,true);
        finish("one",PregenFlowController.Stage.FULL,true);
        assertEquals(0,pairs());finish("one",PregenFlowController.Stage.LOD,true);
        assertEquals(1,pairs());PregenFlowController.fullReady("one");PregenFlowController.lodCommitted("one");
        assertEquals(1,pairs());
        attempt("two");finish("two",PregenFlowController.Stage.LOD,true);
        finish("two",PregenFlowController.Stage.VALIDATE,true);
        finish("two",PregenFlowController.Stage.FULL,true);assertEquals(2,pairs());
    }
    @Test void differentTokensCannotFormPair()throws Exception{
        attempt("full");attempt("lod");
        PregenFlowController.fullReady("full");PregenFlowController.lodCommitted("lod");
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
        PregenFlowController.fullReady("failed");assertEquals(0,pairs());
        attempt("no-sink");PregenFlowController.stageUnavailable("no-sink",PregenFlowController.Stage.LOD);
        PregenFlowController.fullReady("no-sink");assertEquals(0,pairs());
    }
}
