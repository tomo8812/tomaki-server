package maki.lodcpu;

import com.kishku7.chunksmith.GenerationTask;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * E-lite's bounded admission and per-stage WIP controller.
 * Chunksmith owns LOD validity; chunk-cache owns FULL validity and its receipt.
 * Completion events enter via ResourceBudgetApi. Never overrides cache reuse.
 */
final class PregenFlowController {
    enum Stage { LOD, VALIDATE, FULL }
    enum WorkClass { UNKNOWN, NEW, REPAIR }

    private record Key(String dimension,long pos) {}
    private static final String PROCESS_GENERATION=UUID.randomUUID().toString();
    private static final AtomicLong SEQUENCE=new AtomicLong();
    private static final AtomicLong TASK_SEQUENCE=new AtomicLong();
    private static final IdentityHashMap<GenerationTask,Long> TASK_GENERATIONS=new IdentityHashMap<>();
    private static final Map<Key,Attempt> ACTIVE=new HashMap<>();
    private static final Map<String,Attempt> BY_TOKEN=new HashMap<>();
    private static final EnumMap<Stage,StageState> STAGES=new EnumMap<>(Stage.class);
    private static final int MIN_SERVICE_SAMPLES=3;
    private static final long SERVICE_SAMPLE_STALE_NANOS=30_000_000_000L;
    private static long pairConfirmedRuntime,pairNew,pairRepair,pairRevalidated,uncertain;
    private static boolean tuningFrozen;
    private static boolean proofFeedConnected;
    private static final int MAX_DEFERRED=4096;
    private static final IdentityHashMap<GenerationTask,java.util.Set<Key>> REJECTED_UPDATES=new IdentityHashMap<>();
    private static final Map<Key,Deferred> DEFERRED=new HashMap<>();
    private static boolean deferredOverflow;
    private record Deferred(GenerationTask owner,int failures,long readyNanos){}

    static synchronized void defer(String dimension,int x,int z,GenerationTask owner){
        if(dimension==null||owner==null)return;
        Key key=new Key(dimension,packed(x,z));
        REJECTED_UPDATES.computeIfAbsent(owner,ignored->new java.util.HashSet<>()).add(key);
        Deferred old=DEFERRED.get(key);
        int failures=old==null?1:Math.min(16,old.failures()+1);
        if(old!=null||DEFERRED.size()<MAX_DEFERRED){
            long delay=java.util.concurrent.TimeUnit.SECONDS.toNanos(Math.min(300,5L<<(Math.min(6,failures-1))));
            DEFERRED.put(key,new Deferred(owner,failures,System.nanoTime()+delay));
        }else deferredOverflow=true;
        candidateFailed(dimension,x,z,owner);
        metric("pregen-admission-deferred");
    }
    static synchronized boolean consumeRejected(String dimension,int x,int z,GenerationTask owner){
        java.util.Set<Key> set=REJECTED_UPDATES.get(owner);
        if(set==null)return false;
        boolean rejected=set.remove(new Key(dimension,packed(x,z)));
        if(set.isEmpty())REJECTED_UPDATES.remove(owner);
        return rejected;
    }
    static synchronized int deferredFor(GenerationTask owner){
        int count=0;
        for(Deferred item:DEFERRED.values())if(item.owner()==owner)count++;
        return count+(deferredOverflow?1:0);
    }
    static synchronized boolean retryDue(){
        if(deferredOverflow)return true;
        long now=System.nanoTime();
        for(Deferred item:DEFERRED.values())if(now>=item.readyNanos())return true;
        return false;
    }
    private static void fulfilled(Attempt attempt){
        DEFERRED.remove(attempt.key);
    }
    static synchronized boolean beginClassified(String dimension,int x,int z,GenerationTask owner){
        Attempt existing=attempt(dimension,x,z,owner);
        if(existing==null)return false;
        return beginActual(dimension,x,z,owner,existing.workClass!=WorkClass.REPAIR);
    }
    static synchronized void nativeCoordinateFinished(String dimension,int x,int z,GenerationTask owner){
        Attempt attempt=attempt(dimension,x,z,owner);
        if(attempt!=null&&!attempt.actualStarted)skipped(dimension,x,z,owner);
    }

    private static final Map<String,java.util.concurrent.atomic.LongAdder> COUNTERS=new HashMap<>();

    static synchronized boolean connectProofFeed(String protocol) {
        proofFeedConnected="E_LITE_PAIR_V1".equals(protocol);
        return proofFeedConnected;
    }
    static synchronized boolean proofFeedConnected(){return proofFeedConnected;}
    static synchronized long counter(String name){
        var count=COUNTERS.get(name);return count==null?0L:count.sum();
    }
    private static void metric(String name){
        COUNTERS.computeIfAbsent(name,ignored->new java.util.concurrent.atomic.LongAdder()).increment();
    }
    private static boolean active(GenerationTask owner){
        return owner!=null&&TaskOwnership.isOwned(owner)&&!owner.isStopping()
            &&PlayerCenter.admissionEpoch(owner)>=0;
    }
    private static long packed(int x,int z){return ((long)x<<32)|(z&0xffffffffL);}


    static {
        STAGES.put(Stage.LOD,new StageState(4,2,32,6_000_000_000L));
        STAGES.put(Stage.VALIDATE,new StageState(4,1,16,6_000_000_000L));
        STAGES.put(Stage.FULL,new StageState(2,1,8,8_000_000_000L));
    }

    private static final class Attempt {
        final Key key;
        final GenerationTask owner;
        final long epoch;
        final String token;
        final long reservedNanos=System.nanoTime();
        final EnumSet<Stage> held=EnumSet.noneOf(Stage.class);
        final EnumSet<Stage> started=EnumSet.noneOf(Stage.class);
        WorkClass workClass=WorkClass.UNKNOWN;
        boolean actualStarted;
        boolean pairReported;
        boolean pairResolved;
        Attempt(Key key,GenerationTask owner,long epoch,String token) {
            this.key=key;this.owner=owner;this.epoch=epoch;this.token=token;
        }
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

    record Snapshot(
        int candidates,int actual,long pairConfirmedRuntime,long pairNew,long pairRepair,long pairRevalidated,long uncertain,
        int lodOutstanding,int lodWindow,int validateOutstanding,int validateWindow,int fullOutstanding,int fullWindow,
        long oldestMillis,boolean blocked,boolean sensorUnknown,long fastSignature,boolean tuningFrozen
    ) {}

    private PregenFlowController() {}

    static synchronized boolean reserveCandidate(String dimension,int x,int z,GenerationTask owner) {
        prune();
        if(dimension==null||owner==null||!active(owner)
            ||!PlayerCenter.admissionOpen(owner))return false;
        long epoch=PlayerCenter.admissionEpoch(owner);
        if(epoch<0)return false;
        Key key=new Key(dimension,packed(x,z));
        Attempt existing=ACTIVE.get(key);
        if(existing!=null)return existing.owner==owner&&existing.epoch==epoch;
        EnumSet<Stage> needed=EnumSet.allOf(Stage.class);
        if(!canReserve(needed))return false;
        String token=attemptToken(owner,epoch);
        Attempt attempt=new Attempt(key,owner,epoch,token);
        reserve(attempt,needed);
        ACTIVE.put(key,attempt);
        BY_TOKEN.put(token,attempt);
        metric("pregen-candidate-reserved");
        return true;
    }

    static synchronized void classify(String dimension,int x,int z,GenerationTask owner,boolean generated) {
        Attempt attempt=attempt(dimension,x,z,owner);
        if(attempt==null)return;
        if(generated) {
            attempt.workClass=WorkClass.REPAIR;
            releaseIfUnstarted(attempt,Stage.VALIDATE);
            releaseIfUnstarted(attempt,Stage.FULL);
        } else attempt.workClass=WorkClass.NEW;
    }

    static synchronized boolean beginActual(String dimension,int x,int z,GenerationTask owner,boolean generatedNew) {
        prune();
        if(dimension==null||owner==null||!active(owner)
            ||!PlayerCenter.admissionOpen(owner))return false;
        long epoch=PlayerCenter.admissionEpoch(owner);
        if(epoch<0)return false;
        Key key=new Key(dimension,packed(x,z));
        Attempt attempt=ACTIVE.get(key);
        if(attempt==null) {
            EnumSet<Stage> needed=generatedNew?EnumSet.allOf(Stage.class):EnumSet.of(Stage.LOD);
            if(!canReserve(needed))return false;
            String token=attemptToken(owner,epoch);
            attempt=new Attempt(key,owner,epoch,token);
            attempt.workClass=generatedNew?WorkClass.NEW:WorkClass.REPAIR;
            reserve(attempt,needed);
            ACTIVE.put(key,attempt);
            BY_TOKEN.put(token,attempt);
        } else {
            if(attempt.owner!=owner||attempt.epoch!=epoch)return false;
            if(generatedNew)attempt.workClass=WorkClass.NEW;
            else if(attempt.workClass==WorkClass.UNKNOWN) {
                attempt.workClass=WorkClass.REPAIR;
                releaseIfUnstarted(attempt,Stage.VALIDATE);
                releaseIfUnstarted(attempt,Stage.FULL);
            }
        }
        attempt.actualStarted=true;
        metric("pregen-actual-request-started");
        return true;
    }

    static synchronized String token(String dimension,int x,int z,GenerationTask owner) {
        Attempt attempt=attempt(dimension,x,z,owner);
        return attempt==null||!attempt.actualStarted?null:attempt.token;
    }

    static synchronized void skipped(String dimension,int x,int z,GenerationTask owner) {
        Attempt attempt=attempt(dimension,x,z,owner);
        if(attempt==null)return;
        attempt.pairResolved=true;
        fulfilled(attempt);
        releaseAllUnstarted(attempt);
        finishIfDone(attempt);
        metric("pregen-candidate-skipped");
    }

    static synchronized void candidateFailed(String dimension,int x,int z,GenerationTask owner) {
        Attempt attempt=attempt(dimension,x,z,owner);
        if(attempt==null)return;
        attempt.pairResolved=true;
        releaseAllUnstarted(attempt);
        finishIfDone(attempt);
    }

    static synchronized void requestFailed(String dimension,int x,int z,GenerationTask owner) {
        Attempt attempt=attempt(dimension,x,z,owner);
        if(attempt==null)return;
        attempt.pairResolved=true;
        releaseAllUnstarted(attempt);
        finishIfDone(attempt);
    }

    static synchronized void logicalCaptureFailed(String token) {
        Attempt attempt=BY_TOKEN.get(token);
        if(attempt==null)return;
        attempt.pairResolved=true;
        releaseAllUnstarted(attempt);
        finishIfDone(attempt);
    }

    static synchronized void stageUnavailable(String token,Stage stage) {
        Attempt attempt=BY_TOKEN.get(token);
        if(attempt==null)return;
        attempt.pairResolved=true;
        releaseIfUnstarted(attempt,stage);
        finishIfDone(attempt);
    }

    static synchronized void physicalStart(String token,Stage stage) {
        Attempt attempt=BY_TOKEN.get(token);
        if(attempt!=null&&attempt.held.contains(stage)&&attempt.started.add(stage))
            metric(stageMetric(stage,"physical-start"));
    }

    static synchronized void physicalTerminal(String token,Stage stage,long serviceNanos,boolean success) {
        Attempt attempt=BY_TOKEN.get(token);
        if(attempt==null)return;
        if(attempt.held.remove(stage)) {
            StageState state=STAGES.get(stage);
            state.outstanding=Math.max(0,state.outstanding-1);
            state.terminal(serviceNanos,success);
            metric(stageMetric(stage,"terminal"));
            metric(stageMetric(stage,success?"success":"failure"));
        }
        attempt.started.remove(stage);
        if(!success)attempt.pairResolved=true;
        finishIfDone(attempt);
    }

    static synchronized void pairConfirmed(String token,boolean generatedNew) {
        Attempt attempt=BY_TOKEN.get(token);
        if(attempt==null||attempt.pairReported)return;
        attempt.pairReported=true;
        attempt.pairResolved=true;
        pairConfirmedRuntime++;
        fulfilled(attempt);
        if(generatedNew||attempt.workClass==WorkClass.NEW)pairNew++;else pairRepair++;
        metric(generatedNew||attempt.workClass==WorkClass.NEW?"pair-confirmed-new":"pair-confirmed-repair");
        finishIfDone(attempt);
    }

    static synchronized void pairExisting(String token) {
        Attempt attempt=BY_TOKEN.get(token);
        if(attempt==null||attempt.pairResolved)return;
        attempt.pairResolved=true;
        attempt.pairReported=true;
        fulfilled(attempt);
        metric("pair-confirmed-existing");
        finishIfDone(attempt);
    }

    static synchronized void pairRevalidated() {
        pairRevalidated++;
        metric("pair-revalidated-after-restart");
    }

    static synchronized void uncertain(String token,String reason) {
        uncertain++;
        Attempt attempt=token==null?null:BY_TOKEN.get(token);
        if(attempt!=null) {
            attempt.pairResolved=true;
            finishIfDone(attempt);
        }
        metric("pair-uncertain-"+sanitize(reason));
    }

    static synchronized boolean admissionBlocked() {
        prune();
        return admissionBlockedNoPrune();
    }

    static synchronized Snapshot snapshot() {
        prune();
        long now=System.nanoTime(),oldest=0L;
        int candidates=0,actual=0;
        for(Attempt attempt:ACTIVE.values()) {
            oldest=Math.max(oldest,Math.max(0L,now-attempt.reservedNanos));
            if(attempt.actualStarted)actual++;else candidates++;
        }
        StageState lod=STAGES.get(Stage.LOD),validate=STAGES.get(Stage.VALIDATE),full=STAGES.get(Stage.FULL);
        boolean unknown=false;
        for(Attempt attempt:ACTIVE.values()) {
            if(!attempt.actualStarted)continue;
            // Logical deadlines are 30s. A stage still physically outstanding
            // after 60s is no longer safe input for active learning, regardless
            // of whether its start callback was observed.
            if(now-attempt.reservedNanos>60_000_000_000L&&!attempt.held.isEmpty())unknown=true;
        }
        return new Snapshot(candidates,actual,pairConfirmedRuntime,pairNew,pairRepair,pairRevalidated,uncertain,
            lod.outstanding,effectiveWindow(Stage.LOD),validate.outstanding,effectiveWindow(Stage.VALIDATE),
            full.outstanding,effectiveWindow(Stage.FULL),TimeUnit.NANOSECONDS.toMillis(oldest),admissionBlockedNoPrune(),unknown,
            fastSignature(),tuningFrozen);
    }

    static synchronized void setTuningFrozen(boolean frozen){tuningFrozen=frozen;}

    static synchronized void generationCapChanged(int cap) {
        for(StageState state:STAGES.values()){
            state.serviceEwmaNanos=0;
            state.serviceSamples=0;
            state.lastServiceSampleNanos=0;
            state.window=Math.max(state.safeMin,Math.min(state.window,state.safeMin+1));
        }
        metric("pregen-generation-cap-updated");
        metric("pregen-service-model-reset");
    }

    private static long fastSignature(){
        // Signature the control law/configuration, not its live window state.
        // Live windows are expected to move as ACK service changes, including
        // while SLOW probes a neighbouring generation cap.
        long signature=0xcbf29ce484222325L;
        for(Stage stage:Stage.values()){
            StageState state=STAGES.get(stage);
            signature=(signature^stage.ordinal())*0x100000001b3L;
            signature=(signature^state.safeMin)*0x100000001b3L;
            signature=(signature^state.hardMax)*0x100000001b3L;
            signature=(signature^state.targetWaitNanos)*0x100000001b3L;
        }
        return signature;
    }

    static synchronized void clear() {
        ACTIVE.clear();BY_TOKEN.clear();TASK_GENERATIONS.clear();
        for(StageState state:STAGES.values()){
            state.outstanding=0;state.serviceSamples=0;state.serviceEwmaNanos=0;
            state.lastServiceSampleNanos=0;
        }
        proofFeedConnected=false;
        DEFERRED.clear();REJECTED_UPDATES.clear();deferredOverflow=false;
    }

    private static String attemptToken(GenerationTask owner,long epoch) {
        long taskGeneration=TASK_GENERATIONS.computeIfAbsent(owner,ignored->TASK_SEQUENCE.incrementAndGet());
        return PROCESS_GENERATION+"-"+Long.toUnsignedString(taskGeneration,36)+"-"
            +Long.toUnsignedString(epoch,36)+"-"+Long.toUnsignedString(SEQUENCE.incrementAndGet(),36);
    }

    private static Attempt attempt(String dimension,int x,int z,GenerationTask owner) {
        if(dimension==null||owner==null)return null;
        Attempt attempt=ACTIVE.get(new Key(dimension,packed(x,z)));
        return attempt!=null&&attempt.owner==owner?attempt:null;
    }

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
            metric(stageMetric(stage,"reserved"));
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
            if(attempt.held.contains(stage))oldest=Math.max(oldest,Math.max(0L,now-attempt.reservedNanos));
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

    private static void finishIfDone(Attempt attempt) {
        if(!attempt.held.isEmpty()||(attempt.actualStarted&&!attempt.pairResolved))return;
        ACTIVE.remove(attempt.key,attempt);
        BY_TOKEN.remove(attempt.token,attempt);
        releaseTaskGenerationIfUnused(attempt.owner);
    }

    private static void releaseTaskGenerationIfUnused(GenerationTask owner) {
        if(owner==null)return;
        for(Attempt active:ACTIVE.values())if(active.owner==owner)return;
        TASK_GENERATIONS.remove(owner);
    }

    private static void prune() {
        for(Attempt attempt:new ArrayList<>(ACTIVE.values())) {
            if(active(attempt.owner))continue;
            // Before actual request admission, a detached candidate cannot start
            // downstream work, so predictive credits are safe to return. After
            // beginActual(), however, validation/FULL/LOD work may already be
            // queued even when the worker-start callback has not fired yet.
            // Keep those credits until an explicit request/logical failure or
            // the exact physical terminal proves the work is gone.
            if(!attempt.actualStarted) {
                releaseAllUnstarted(attempt);
                finishIfDone(attempt);
            }
        }
    }

    private static String stageMetric(Stage stage,String suffix) {
        return switch(stage) {
            case LOD -> "stage-lod-"+suffix;
            case VALIDATE -> "stage-validate-"+suffix;
            case FULL -> "stage-full-"+suffix;
        };
    }

    private static String sanitize(String reason) {
        if(reason==null||reason.isBlank())return "unknown";
        return reason.replaceAll("[^A-Za-z0-9_-]","-");
    }
}
