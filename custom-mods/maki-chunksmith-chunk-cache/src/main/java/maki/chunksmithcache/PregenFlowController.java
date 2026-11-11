package maki.chunksmithcache;

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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

/**
 * ACK-replenished admission state for unified pregen. Candidate reservations
 * predict all downstream work; asynchronous classification returns credits for
 * stages that are no longer required. Once a stage physically starts, only its
 * physical terminal callback can return that credit.
 */
final class PregenFlowController {
    enum Stage { LOD, VALIDATE, FULL }
    enum WorkClass { UNKNOWN, NEW, REPAIR }

    private record Key(ServerLevel level,long pos) {}
    private static final String PROCESS_GENERATION=UUID.randomUUID().toString();
    private static final AtomicLong SEQUENCE=new AtomicLong();
    private static final AtomicLong TASK_SEQUENCE=new AtomicLong();
    private static final IdentityHashMap<GenerationTask,Long> TASK_GENERATIONS=new IdentityHashMap<>();
    private static final Map<Key,Attempt> ACTIVE=new HashMap<>();
    private static final Map<String,Attempt> BY_TOKEN=new HashMap<>();
    private static final EnumMap<Stage,StageState> STAGES=new EnumMap<>(Stage.class);
    private static long pairConfirmedRuntime,pairNew,pairRepair,pairRevalidated,uncertain;
    private static boolean tuningFrozen;

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
        long terminals,successes;
        StageState(int initial,int safeMin,int hardMax,long targetWaitNanos) {
            this.window=initial;this.safeMin=safeMin;this.hardMax=hardMax;this.targetWaitNanos=targetWaitNanos;
        }
        void terminal(long serviceNanos,boolean success) {
            terminals++;
            if(success)successes++;
            if(serviceNanos>0) {
                serviceEwmaNanos=serviceEwmaNanos==0?serviceNanos:serviceEwmaNanos*.8+serviceNanos*.2;
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

    static synchronized boolean reserveCandidate(ServerLevel level,ChunkPos pos,GenerationTask owner) {
        prune();
        if(level==null||pos==null||owner==null||!FollowCaptureOwnership.active(level,owner)
            ||!FollowCaptureOwnership.admissionOpen(owner))return false;
        long epoch=FollowCaptureOwnership.admissionEpoch(owner);
        if(epoch<0)return false;
        Key key=new Key(level,pos.toLong());
        Attempt existing=ACTIVE.get(key);
        if(existing!=null)return existing.owner==owner&&existing.epoch==epoch;
        EnumSet<Stage> needed=EnumSet.allOf(Stage.class);
        if(!canReserve(needed))return false;
        String token=attemptToken(owner,epoch);
        Attempt attempt=new Attempt(key,owner,epoch,token);
        reserve(attempt,needed);
        ACTIVE.put(key,attempt);
        BY_TOKEN.put(token,attempt);
        PipelineMetrics.count("pregen-candidate-reserved");
        return true;
    }

    static synchronized void classify(ServerLevel level,ChunkPos pos,GenerationTask owner,boolean generated) {
        Attempt attempt=attempt(level,pos,owner);
        if(attempt==null)return;
        if(generated) {
            attempt.workClass=WorkClass.REPAIR;
            releaseIfUnstarted(attempt,Stage.VALIDATE);
            releaseIfUnstarted(attempt,Stage.FULL);
        } else attempt.workClass=WorkClass.NEW;
    }

    static synchronized boolean beginActual(ServerLevel level,ChunkPos pos,GenerationTask owner,boolean generatedNew) {
        prune();
        if(level==null||pos==null||owner==null||!FollowCaptureOwnership.active(level,owner)
            ||!FollowCaptureOwnership.admissionOpen(owner))return false;
        long epoch=FollowCaptureOwnership.admissionEpoch(owner);
        if(epoch<0)return false;
        Key key=new Key(level,pos.toLong());
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
        PipelineMetrics.count("pregen-actual-request-started");
        return true;
    }

    static synchronized String token(ServerLevel level,ChunkPos pos,GenerationTask owner) {
        Attempt attempt=attempt(level,pos,owner);
        return attempt==null||!attempt.actualStarted?null:attempt.token;
    }

    static synchronized void skipped(ServerLevel level,ChunkPos pos,GenerationTask owner) {
        Attempt attempt=attempt(level,pos,owner);
        if(attempt==null)return;
        releaseAllUnstarted(attempt);
        finishIfDone(attempt);
        PipelineMetrics.count("pregen-candidate-skipped");
    }

    static synchronized void candidateFailed(ServerLevel level,ChunkPos pos,GenerationTask owner) {
        Attempt attempt=attempt(level,pos,owner);
        if(attempt==null)return;
        releaseAllUnstarted(attempt);
        finishIfDone(attempt);
    }

    static synchronized void requestFailed(ServerLevel level,ChunkPos pos,GenerationTask owner) {
        Attempt attempt=attempt(level,pos,owner);
        if(attempt==null)return;
        releaseAllUnstarted(attempt);
        finishIfDone(attempt);
    }

    static synchronized void logicalCaptureFailed(String token) {
        Attempt attempt=BY_TOKEN.get(token);
        if(attempt==null)return;
        releaseAllUnstarted(attempt);
        finishIfDone(attempt);
    }

    static synchronized void physicalStart(String token,Stage stage) {
        Attempt attempt=BY_TOKEN.get(token);
        if(attempt!=null&&attempt.held.contains(stage))attempt.started.add(stage);
    }

    static synchronized void physicalTerminal(String token,Stage stage,long serviceNanos,boolean success) {
        Attempt attempt=BY_TOKEN.get(token);
        if(attempt==null)return;
        if(attempt.held.remove(stage)) {
            StageState state=STAGES.get(stage);
            state.outstanding=Math.max(0,state.outstanding-1);
            state.terminal(serviceNanos,success);
        }
        attempt.started.remove(stage);
        finishIfDone(attempt);
    }

    static synchronized void pairConfirmed(String token,boolean generatedNew) {
        Attempt attempt=BY_TOKEN.get(token);
        if(attempt==null||attempt.pairReported)return;
        attempt.pairReported=true;
        pairConfirmedRuntime++;
        if(generatedNew||attempt.workClass==WorkClass.NEW)pairNew++;else pairRepair++;
        PipelineMetrics.count(generatedNew||attempt.workClass==WorkClass.NEW?"pair-confirmed-new":"pair-confirmed-repair");
    }

    static synchronized void pairRevalidated() {
        pairRevalidated++;
        PipelineMetrics.count("pair-revalidated-after-restart");
    }

    static synchronized void uncertain(String token,String reason) {
        uncertain++;
        PipelineMetrics.count("pair-uncertain-"+sanitize(reason));
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
            state.window=Math.max(state.safeMin,Math.min(state.window,state.safeMin+1));
        }
        PipelineMetrics.gauge("pregen-generation-cap",Math.max(0,cap));
        PipelineMetrics.count("pregen-service-model-reset");
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

    static synchronized void clear(MinecraftServer server) {
        Iterator<Map.Entry<Key,Attempt>> iterator=ACTIVE.entrySet().iterator();
        while(iterator.hasNext()) {
            Attempt attempt=iterator.next().getValue();
            if(attempt.key.level().getServer()!=server)continue;
            for(Stage stage:attempt.held)STAGES.get(stage).outstanding=Math.max(0,STAGES.get(stage).outstanding-1);
            BY_TOKEN.remove(attempt.token);
            iterator.remove();
        }
    }

    private static String attemptToken(GenerationTask owner,long epoch) {
        long taskGeneration=TASK_GENERATIONS.computeIfAbsent(owner,ignored->TASK_SEQUENCE.incrementAndGet());
        return PROCESS_GENERATION+"-"+Long.toUnsignedString(taskGeneration,36)+"-"
            +Long.toUnsignedString(epoch,36)+"-"+Long.toUnsignedString(SEQUENCE.incrementAndGet(),36);
    }

    private static Attempt attempt(ServerLevel level,ChunkPos pos,GenerationTask owner) {
        if(level==null||pos==null||owner==null)return null;
        Attempt attempt=ACTIVE.get(new Key(level,pos.toLong()));
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
        if(!attempt.held.isEmpty())return;
        ACTIVE.remove(attempt.key,attempt);
        BY_TOKEN.remove(attempt.token,attempt);
    }

    private static void prune() {
        for(Attempt attempt:new ArrayList<>(ACTIVE.values())) {
            if(FollowCaptureOwnership.active(attempt.key.level(),attempt.owner))continue;
            releaseAllUnstarted(attempt);
            finishIfDone(attempt);
        }
    }

    private static String sanitize(String reason) {
        if(reason==null||reason.isBlank())return "unknown";
        return reason.replaceAll("[^A-Za-z0-9_-]","-");
    }
}
