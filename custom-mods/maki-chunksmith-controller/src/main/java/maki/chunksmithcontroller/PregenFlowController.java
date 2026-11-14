package maki.chunksmithcontroller;

import com.kishku7.chunksmith.GenerationTask;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/** Native FULL acquisition and LOD writer WIP; Minecraft owns normal persistence. */
final class PregenFlowController {
    enum Stage { FULL, LOD }
    enum WorkClass { UNKNOWN, NEW, REPAIR }
    private record Key(String dimension,long pos) {}
    private static final Map<Key,Attempt> ACTIVE=new HashMap<>();
    private static final Map<String,Attempt> BY_TOKEN=new HashMap<>();
    private static final long RECEIPT_GRACE_NANOS=60_000_000_000L;
    private static final EnumMap<Stage,StageState> STAGES=new EnumMap<>(Stage.class);
    private static final AtomicLong SEQUENCE=new AtomicLong();
    private static final String PROCESS=UUID.randomUUID().toString();
    private static final int MIN_SERVICE_SAMPLES=3;
    private static final long SERVICE_SAMPLE_STALE_NANOS=30_000_000_000L;
    private static long pairConfirmedRuntime,pairNew,pairRepair,uncertain;
    static {
        STAGES.put(Stage.FULL,new StageState(3,1,16,8_000_000_000L));
        STAGES.put(Stage.LOD,new StageState(4,2,32,6_000_000_000L));
    }
    private static final class Attempt {
        final Key key; final GenerationTask owner; final long epoch; final String token;
        final long reservedNanos=System.nanoTime();
        long progressNanos=reservedNanos,detachedNanos;
        final EnumSet<Stage> held=EnumSet.noneOf(Stage.class),started=EnumSet.noneOf(Stage.class);
        WorkClass workClass=WorkClass.UNKNOWN;
        boolean actualStarted,fullReady,lodReady,lodSettled,pairResolved;
        Attempt(Key key,GenerationTask owner,long epoch,String token){this.key=key;this.owner=owner;this.epoch=epoch;this.token=token;}
    }
    private static final class StageState {
        int outstanding;
        int window;
        final int safeMin,hardMax;
        final long targetWaitNanos;
        double serviceEwmaNanos;
        int serviceSamples;
        long lastServiceSampleNanos;
        long terminals,successes;
        StageState(int initial,int safeMin,int hardMax,long targetWaitNanos) {
            this.window=initial;this.safeMin=safeMin;this.hardMax=hardMax;this.targetWaitNanos=targetWaitNanos;
        }
        void terminal(long serviceNanos,boolean success) {
            terminals++;
            if(success)successes++;
            if(serviceNanos>0) {
                long now=System.nanoTime();
                if(lastServiceSampleNanos!=0L&&now>=lastServiceSampleNanos
                    &&now-lastServiceSampleNanos>SERVICE_SAMPLE_STALE_NANOS) {
                    serviceEwmaNanos=0;
                    serviceSamples=0;
                }
                lastServiceSampleNanos=now;
                serviceEwmaNanos=serviceEwmaNanos==0?serviceNanos:serviceEwmaNanos*.8+serviceNanos*.2;
                if(serviceSamples<Integer.MAX_VALUE)serviceSamples++;
                if(serviceSamples<MIN_SERVICE_SAMPLES)return;
                long proposed=Math.round(Math.ceil(targetWaitNanos/Math.max(1.0,serviceEwmaNanos)));
                // FAST remains adaptive during c-neighbourhood probes. The
                // SLOW controller compares (c, the same FAST policy) as a closed
                // loop rather than freezing one arm at a stale window.
                window=(int)Math.max(safeMin,Math.min(hardMax,proposed));
            }
        }
    }


    record Snapshot(int candidates,int actual,long pairConfirmedRuntime,long pairNew,long pairRepair,long uncertain,
        int lodOutstanding,int lodWindow,int fullOutstanding,int fullWindow,
        long oldestMillis,boolean blocked,boolean sensorUnknown,long fastSignature) {}
    private PregenFlowController(){}
    private static long packed(int x,int z){return ((long)x<<32)|(z&0xffffffffL);}
    private static boolean active(GenerationTask task){return task!=null&&TaskOwnership.isOwned(task)&&!task.isStopping()&&PlayerCenter.admissionEpoch(task)>=0;}
    private static Attempt attempt(String dim,int x,int z,GenerationTask owner){
        Attempt a=ACTIVE.get(new Key(dim,packed(x,z)));return a!=null&&a.owner==owner?a:null;
    }
    private static void progress(Attempt a){
        a.progressNanos=System.nanoTime();
    }
    static synchronized boolean reserveCandidate(String dim,int x,int z,GenerationTask owner){
        prune();
        if(!active(owner)||!PlayerCenter.admissionOpen(owner))return false;
        Key key=new Key(dim,packed(x,z));long epoch=PlayerCenter.admissionEpoch(owner);
        Attempt prior=ACTIVE.get(key);if(prior!=null)return prior.owner==owner&&prior.epoch==epoch;
        if(STAGES.get(Stage.FULL).outstanding+MakiChunksmithController.activeWork()>=CpuGovernor.cap())return false;
        var needed=EnumSet.allOf(Stage.class);if(!canReserve(needed))return false;
        Attempt a=new Attempt(key,owner,epoch,PROCESS+"-"+SEQUENCE.incrementAndGet());
        reserve(a,needed);ACTIVE.put(key,a);BY_TOKEN.put(a.token,a);return true;
    }
    static synchronized void classify(String dim,int x,int z,GenerationTask owner,boolean generated){
        Attempt a=attempt(dim,x,z,owner);if(a==null)return;
        progress(a);a.workClass=generated?WorkClass.REPAIR:WorkClass.NEW;
    }
    static synchronized boolean beginClassified(String dim,int x,int z,GenerationTask owner){
        Attempt a=attempt(dim,x,z,owner);
        if(a==null||!active(owner)||!PlayerCenter.admissionOpen(owner)||a.epoch!=PlayerCenter.admissionEpoch(owner))return false;
        if(a.workClass==WorkClass.UNKNOWN&&!owner.getChunky().getConfig().isForceLoadExistingChunks())a.workClass=WorkClass.REPAIR;
        a.actualStarted=true;progress(a);return true;
    }
    static synchronized String token(String dim,int x,int z,GenerationTask owner){
        Attempt a=attempt(dim,x,z,owner);return a!=null&&a.actualStarted?a.token:null;
    }
    static synchronized void nativeCoordinateFinished(String dim,int x,int z,GenerationTask owner){
        Attempt a=attempt(dim,x,z,owner);
        if(a!=null&&!a.actualStarted){progress(a);a.pairResolved=true;releaseAllUnstarted(a);finishIfDone(a);}
    }
    private static void fail(Attempt a){if(!a.pairResolved&&a.actualStarted)uncertain++;a.pairResolved=true;releaseAllUnstarted(a);finishIfDone(a);}
    static synchronized void candidateFailed(String dim,int x,int z,GenerationTask owner){
        Attempt a=attempt(dim,x,z,owner);if(a!=null)fail(a);
    }
    static synchronized void logicalCaptureFailed(String token){Attempt a=BY_TOKEN.get(token);if(a!=null)fail(a);}
    static synchronized void stageUnavailable(String token,Stage stage){
        Attempt a=BY_TOKEN.get(token);if(a!=null){
            if(stage==Stage.LOD)a.lodSettled=true;
            releaseIfUnstarted(a,stage);fail(a);
        }
    }
    static synchronized void physicalStart(String token,Stage stage){
        Attempt a=BY_TOKEN.get(token);
        if(a!=null&&(a.held.contains(stage)||stage==Stage.LOD&&!a.lodSettled&&a.started.contains(Stage.FULL))
            &&a.started.add(stage)){
            progress(a);
        }
    }
    static synchronized void physicalTerminal(String token,Stage stage,long nanos,boolean success){
        Attempt a=BY_TOKEN.get(token);if(a==null)return;
        boolean held=a.held.remove(stage),started=a.started.remove(stage);
        if(!held&&!started)return;
        if(held)STAGES.get(stage).outstanding--;
        progress(a);
        if(stage==Stage.LOD)a.lodSettled=true;
        STAGES.get(stage).terminal(nanos,success);
        if(!success)fail(a);
        else{
            if(stage==Stage.FULL){
                a.fullReady=true;
                // Native ChunkResult failure can complete Void normally without
                // offering LOD. offer() runs synchronously before this callback.
                if(!a.lodSettled&&!a.started.contains(Stage.LOD)){
                    fail(a);
                }
            }else a.lodReady=true;
            tryConfirmRuntimePair(a);finishIfDone(a);
        }
    }
    private static void tryConfirmRuntimePair(Attempt a){
        if(a.pairResolved||!a.actualStarted||!a.fullReady||!a.lodReady)return;
        a.pairResolved=true;pairConfirmedRuntime++;
        if(a.workClass==WorkClass.NEW)pairNew++;else if(a.workClass==WorkClass.REPAIR)pairRepair++;
    }
    // Return native admission credits, but retain receipts for writes/Futures
    // that the native task does not drain. Late ACKs never debit another attempt.
    static synchronized void taskFinished(GenerationTask owner){
        for(Attempt a:new ArrayList<>(ACTIVE.values()))if(a.owner==owner){
            ACTIVE.remove(a.key,a);a.detachedNanos=System.nanoTime();
            if(!a.actualStarted)fail(a);
            else {
                for(Stage stage:Stage.values()){
                    boolean ready=stage==Stage.FULL?a.fullReady:a.lodReady;
                    boolean expectedFromFull=stage==Stage.LOD&&!a.lodSettled&&a.started.contains(Stage.FULL);
                    if(!ready&&!a.started.contains(stage)&&!expectedFromFull){
                        fail(a);
                    }
                }
            }
            for(Stage stage:EnumSet.copyOf(a.held)){
                a.held.remove(stage);STAGES.get(stage).outstanding--;
            }
            finishIfDone(a);
        }
    }
    // Expire observation only. Native Futures, writes and coordinate decisions
    // continue under Chunksmith; the controller keeps no recovery backlog.
    static synchronized void expireReceipts(){
        long now=System.nanoTime();
        for(Attempt a:new ArrayList<>(BY_TOKEN.values())){
            long last=a.detachedNanos==0?a.progressNanos:Math.max(a.detachedNanos,a.progressNanos);
            if(now-last<RECEIPT_GRACE_NANOS)continue;
            a.started.clear();fail(a);finishIfDone(a);
        }
    }
    static synchronized boolean tracked(String token){return BY_TOKEN.containsKey(token);}
    static synchronized void superseded(String token,long nanos){
        Attempt a=BY_TOKEN.get(token);if(a==null)return;
        boolean held=a.held.remove(Stage.LOD),started=a.started.remove(Stage.LOD);
        if(!held&&!started)return;
        if(held)STAGES.get(Stage.LOD).outstanding--;
        a.lodSettled=true;progress(a);
        // A newer saved snapshot covers the coordinate, but this attempt did
        // not produce a stored LOD and contributes no PAIR or service sample.
        fail(a);finishIfDone(a);
    }
    static synchronized int fullOutstanding(){return STAGES.get(Stage.FULL).outstanding;}
    static synchronized boolean admissionBlocked(){prune();return admissionBlockedNoPrune();}
    static synchronized Snapshot snapshot(){
        prune();long oldest=0;int candidates=0,actual=0;boolean unknown=false;
        for(Attempt a:ACTIVE.values()){
            long age=Math.max(0,System.nanoTime()-a.reservedNanos);oldest=Math.max(oldest,age);
            if(a.actualStarted)actual++;else candidates++;
            if(System.nanoTime()-a.progressNanos>60_000_000_000L&&!a.held.isEmpty())unknown=true;
        }
        return new Snapshot(candidates,actual,pairConfirmedRuntime,pairNew,pairRepair,uncertain,
            STAGES.get(Stage.LOD).outstanding,effectiveWindow(Stage.LOD),STAGES.get(Stage.FULL).outstanding,effectiveWindow(Stage.FULL),
            TimeUnit.NANOSECONDS.toMillis(oldest),admissionBlockedNoPrune(),unknown,fastSignature());
    }
    static synchronized void generationCapChanged(int cap){
        for(StageState s:STAGES.values()){s.serviceEwmaNanos=0;s.serviceSamples=0;s.lastServiceSampleNanos=0;s.window=Math.max(s.safeMin,Math.min(s.window,s.safeMin+1));}
    }
    private static long fastSignature(){long result=0xcbf29ce484222325L;for(Stage s:Stage.values()){
        StageState state=STAGES.get(s);result=(result^s.ordinal())*0x100000001b3L;
        result=(result^state.safeMin^state.hardMax^state.targetWaitNanos)*0x100000001b3L;
    }return result;}
    static synchronized void clear(){ACTIVE.clear();BY_TOKEN.clear();for(StageState s:STAGES.values()){
        s.outstanding=0;s.serviceSamples=0;s.serviceEwmaNanos=0;s.lastServiceSampleNanos=0;
    }}
    private static boolean canReserve(EnumSet<Stage> needed) {
        for(Stage stage:needed) {
            StageState state=STAGES.get(stage);
            int limit=effectiveWindow(stage);
            if(limit<=0||state.outstanding+1>limit)return false;
        }
        return true;
    }

    private static void reserve(Attempt attempt,EnumSet<Stage> needed) {
        for(Stage stage:needed) {
            attempt.held.add(stage);
            STAGES.get(stage).outstanding++;
        }
    }

    private static void releaseIfUnstarted(Attempt attempt,Stage stage) {
        if(attempt.started.contains(stage)||!attempt.held.remove(stage))return;
        STAGES.get(stage).outstanding=Math.max(0,STAGES.get(stage).outstanding-1);
    }

    private static void releaseAllUnstarted(Attempt attempt) {
        for(Stage stage:Stage.values())releaseIfUnstarted(attempt,stage);
    }

    private static int effectiveWindow(Stage stage) {
        StageState state=STAGES.get(stage);
        long now=System.nanoTime(),oldest=0L;
        for(Attempt attempt:ACTIVE.values())
            if(attempt.held.contains(stage))oldest=Math.max(oldest,Math.max(0L,now-attempt.progressNanos));
        if(oldest>=25_000_000_000L)return 0;
        if(oldest>=15_000_000_000L)return Math.max(state.safeMin,state.window/2);
        return state.window;
    }

    private static boolean admissionBlockedNoPrune() {
        for(Stage stage:Stage.values()) {
            StageState state=STAGES.get(stage);
            int limit=effectiveWindow(stage);
            if(limit<=0||state.outstanding>=limit)return true;
        }
        return false;
    }


    private static void finishIfDone(Attempt a){
        if(!a.held.isEmpty()||!a.started.isEmpty()||(a.actualStarted&&!a.pairResolved))return;
        ACTIVE.remove(a.key,a);BY_TOKEN.remove(a.token,a);
    }
    private static void prune(){expireReceipts();for(Attempt a:new ArrayList<>(ACTIVE.values()))if(!active(a.owner)&&!a.actualStarted){
        a.pairResolved=true;releaseAllUnstarted(a);finishIfDone(a);
    }}
}
