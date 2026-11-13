package maki.chunksmithcontroller;

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
    private enum Failure { FULL_PREPARATION, FULL_SAVE, LOD_WRITE }
    // Failed observations consume no physical WIP. Native generation owns repair.
    private static final Map<Key,EnumSet<Failure>> INCOMPLETE=new HashMap<>();
    static synchronized void preparationFailed(String dim,int x,int z){incomplete(new Key(dim,packed(x,z)),Failure.FULL_PREPARATION);}
    static synchronized void preparationSucceeded(String dim,int x,int z){
        Key key=new Key(dim,packed(x,z));var failures=INCOMPLETE.get(key);
        if(failures!=null){failures.remove(Failure.FULL_PREPARATION);if(failures.isEmpty())INCOMPLETE.remove(key);}
    }
    private static void incomplete(Key key,Failure failure){
        if(INCOMPLETE.computeIfAbsent(key,ignored->EnumSet.noneOf(Failure.class)).add(failure))
            org.slf4j.LoggerFactory.getLogger("MakiChunksmithController").warn(
                "Pregen coordinate incomplete: dimension={} x={} z={} stage={}",key.dimension(),(int)(key.pos()>>32),(int)key.pos(),failure);
    }
    static synchronized boolean hasPreparationFailures(String dim){
        return INCOMPLETE.entrySet().stream().anyMatch(entry->entry.getKey().dimension().equals(dim)
            &&entry.getValue().contains(Failure.FULL_PREPARATION));
    }
    static synchronized int incompleteInRange(String dim,int centerX,int centerZ,int radius){
        long distance=(long)radius+16L;int count=0;
        for(Key key:INCOMPLETE.keySet())if(key.dimension().equals(dim)){
            long dx=((long)(int)(key.pos()>>32)<<4)+8L-centerX,dz=((long)(int)key.pos()<<4)+8L-centerZ;
            if(dx*dx+dz*dz<=distance*distance)count++;
        }
        return count;
    }
    private static final EnumMap<Stage,StageState> STAGES=new EnumMap<>(Stage.class);
    private static final int MIN_SERVICE_SAMPLES=3;
    private static final long SERVICE_SAMPLE_STALE_NANOS=30_000_000_000L;
    private static long pairConfirmedRuntime,pairNew,pairRepair,uncertain;
    /**
     * Native LOD/force-load paths bypass isChunkGenerated. Reserve candidates
     * at ChunkIterator.next and wait instead of forging failed native Futures.
     */
    static synchronized boolean beginClassified(String dimension,int x,int z,GenerationTask owner){
        Attempt attempt=attempt(dimension,x,z,owner);
        if(attempt==null)return false;
        if(attempt.workClass==WorkClass.UNKNOWN) {
            // Without force-load, 4.8.1 bypasses isChunkGenerated ONLY for an
            // existing FULL chunk missing its LOD: it needs LOD credit, not
            // speculative FULL validation/write credit.
            boolean forced=owner.getChunky().getConfig().isForceLoadExistingChunks();
            if(!forced)return beginActual(dimension,x,z,owner,false);
            // Force-load bypasses the same query for arbitrary coordinates.
            // Retain all stages and avoid inventing NEW/REPAIR evidence.
            if(!active(owner)||!PlayerCenter.admissionOpen(owner)
                ||attempt.epoch!=PlayerCenter.admissionEpoch(owner))return false;
            attempt.actualStarted=true;
            metric("pregen-actual-request-unclassified");
            return true;
        }
        return beginActual(dimension,x,z,owner,attempt.workClass==WorkClass.NEW);
    }
    static synchronized void nativeCoordinateFinished(String dimension,int x,int z,GenerationTask owner){
        Attempt attempt=attempt(dimension,x,z,owner);
        if(attempt!=null&&!attempt.requestInvoked)skipped(dimension,x,z,owner);
    }

    private static final Map<String,java.util.concurrent.atomic.LongAdder> COUNTERS=new HashMap<>();

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
        boolean requestInvoked,persistencePending;
        boolean lodReady;
        boolean fullReady;
        boolean fullPublished;
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
        int candidates,int actual,long pairConfirmedRuntime,long pairNew,long pairRepair,long uncertain,
        int lodOutstanding,int lodWindow,int validateOutstanding,int validateWindow,int fullOutstanding,int fullWindow,
        long oldestMillis,boolean blocked,boolean sensorUnknown,long fastSignature
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

    static synchronized void requestInvoked(String dim,int x,int z,GenerationTask owner){
        Attempt attempt=attempt(dim,x,z,owner);if(attempt!=null)attempt.requestInvoked=true;
    }
    static synchronized void fullPending(String token){
        Attempt attempt=BY_TOKEN.get(token);if(attempt!=null)attempt.persistencePending=true;
    }
    static synchronized void fullSettled(String token){
        Attempt attempt=BY_TOKEN.get(token);if(attempt!=null){attempt.persistencePending=false;finishIfDone(attempt);}
    }
    static synchronized void fullSaveDeferred(String token){
        Attempt attempt=BY_TOKEN.get(token);if(attempt==null)return;
        incomplete(attempt.key,Failure.FULL_SAVE);
        failAttempt(attempt);
        releaseIfUnstarted(attempt,Stage.VALIDATE);
        releaseIfUnstarted(attempt,Stage.FULL);
        finishIfDone(attempt);
    }
    static synchronized void fullSaveResolved(String token){
        Attempt attempt=BY_TOKEN.get(token);if(attempt==null)return;
        var failures=INCOMPLETE.get(attempt.key);
        if(failures!=null){failures.remove(Failure.FULL_SAVE);if(failures.isEmpty())INCOMPLETE.remove(attempt.key);}
    }
    static synchronized boolean reserveFullRetry(String token){
        Attempt attempt=BY_TOKEN.get(token);if(attempt==null)return false;
        if(attempt.started.contains(Stage.VALIDATE)||attempt.started.contains(Stage.FULL))return false;
        EnumSet<Stage> needed=EnumSet.of(Stage.VALIDATE,Stage.FULL);
        needed.removeAll(attempt.held);
        if(!canReserve(needed))return false;
        reserve(attempt,needed);return true;
    }
    static synchronized String token(String dimension,int x,int z,GenerationTask owner) {
        Attempt attempt=attempt(dimension,x,z,owner);
        return attempt==null||!attempt.actualStarted?null:attempt.token;
    }

    static synchronized void skipped(String dimension,int x,int z,GenerationTask owner) {
        Attempt attempt=attempt(dimension,x,z,owner);
        if(attempt==null)return;
        attempt.pairResolved=true;
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

    private static void failAttempt(Attempt attempt){
        if(!attempt.pairResolved&&attempt.actualStarted)uncertain++;
        attempt.pairResolved=true;
    }

    static synchronized void requestFailed(String dimension,int x,int z,GenerationTask owner) {
        Attempt attempt=attempt(dimension,x,z,owner);
        if(attempt==null)return;
        failAttempt(attempt);
        releaseAllUnstarted(attempt);
        finishIfDone(attempt);
    }

    static synchronized void logicalCaptureFailed(String token) {
        Attempt attempt=BY_TOKEN.get(token);
        if(attempt==null)return;
        if(attempt.persistencePending){
            releaseIfUnstarted(attempt,Stage.VALIDATE);releaseIfUnstarted(attempt,Stage.FULL);
        }else{failAttempt(attempt);releaseAllUnstarted(attempt);}
        finishIfDone(attempt);
    }

    static synchronized void stageUnavailable(String token,Stage stage) {
        Attempt attempt=BY_TOKEN.get(token);
        if(attempt==null)return;
        failAttempt(attempt);
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
        if(!success&&stage==Stage.LOD)incomplete(attempt.key,Failure.LOD_WRITE);
        if(!success&&(!attempt.persistencePending||stage==Stage.LOD))failAttempt(attempt);
        else if(stage==Stage.LOD)attempt.lodReady=true;
        // FULL physical success and FULL publication are distinct notifications.
        tryConfirmRuntimePair(attempt);
        finishIfDone(attempt);
    }

    /** LOD proof comes from Chunksmith's physical writer; no reuse decision is made here. */
    static synchronized void lodCommitted(String token){
        Attempt attempt=BY_TOKEN.get(token);
        if(attempt==null||!attempt.actualStarted)return;
        attempt.lodReady=true;
        tryConfirmRuntimePair(attempt);
    }

    /** FULL validity and publication come from the FULL owner (cache or normal world). */
    static synchronized void fullPublished(String token){
        Attempt attempt=BY_TOKEN.get(token);
        if(attempt==null||!attempt.actualStarted)return;
        attempt.fullReady=true;
        attempt.fullPublished=true;
        tryConfirmRuntimePair(attempt);
    }

    static synchronized void fullReused(String token){
        Attempt attempt=BY_TOKEN.get(token);
        if(attempt==null||!attempt.actualStarted)return;
        attempt.fullReady=true;
        releaseIfUnstarted(attempt,Stage.VALIDATE);
        releaseIfUnstarted(attempt,Stage.FULL);
        tryConfirmRuntimePair(attempt);
    }

    static synchronized boolean fullCaptureExpected(String token){
        Attempt attempt=BY_TOKEN.get(token);
        return attempt!=null&&attempt.workClass!=WorkClass.REPAIR;
    }

    private static void tryConfirmRuntimePair(Attempt attempt){
        if(attempt.pairReported||attempt.pairResolved
            ||!attempt.actualStarted||!attempt.lodReady||!attempt.fullReady)return;
        INCOMPLETE.remove(attempt.key);
        attempt.pairReported=true;
        attempt.pairResolved=true;
        pairConfirmedRuntime++;
        if(attempt.fullPublished&&attempt.workClass==WorkClass.NEW) {
            pairNew++;
            metric("pair-confirmed-new");
        }else if(!attempt.fullPublished) {
            pairRepair++;
            metric("pair-confirmed-repair");
        }else {
            // Forced existing-chunk loads bypass the native generated check.
            // Completion is still real goodput, but its class is unproven.
            metric("pair-confirmed-unclassified");
        }
        finishIfDone(attempt);
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
        return new Snapshot(candidates,actual,pairConfirmedRuntime,pairNew,pairRepair,uncertain,
            lod.outstanding,effectiveWindow(Stage.LOD),validate.outstanding,effectiveWindow(Stage.VALIDATE),
            full.outstanding,effectiveWindow(Stage.FULL),TimeUnit.NANOSECONDS.toMillis(oldest),admissionBlockedNoPrune(),unknown,
            fastSignature());
    }

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
        ACTIVE.clear();BY_TOKEN.clear();TASK_GENERATIONS.clear();INCOMPLETE.clear();
        for(StageState state:STAGES.values()){
            state.outstanding=0;state.serviceSamples=0;state.serviceEwmaNanos=0;
            state.lastServiceSampleNanos=0;
        }
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
        if(attempt.persistencePending||!attempt.held.isEmpty()||(attempt.actualStarted&&!attempt.pairResolved))return;
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

}
