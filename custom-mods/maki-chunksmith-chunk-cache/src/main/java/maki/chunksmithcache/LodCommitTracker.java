package maki.chunksmithcache;

import com.kishku7.chunksmith.lod.CsLodChunk;
import com.kishku7.chunksmith.lod.LodSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

/** Correlates the exact native LOD record with its physical writer terminal. */
public final class LodCommitTracker {
    private static final int MAX_PHYSICAL=4096;
    private static final IdentityHashMap<CsLodChunk,Physical> PHYSICAL=new IdentityHashMap<>();
    private static final Map<Coordinate,Set<Physical>> BY_COORDINATE=new HashMap<>();
    private static final Map<CompletableFuture<Boolean>,LodReceipt> RECEIPTS=
        Collections.synchronizedMap(new WeakHashMap<>());
    private record Coordinate(String dimension,int x,int z){}
    private static final ThreadLocal<Request> CURRENT=new ThreadLocal<>();
    private static long unknownPhysical;

    private static final class Request {
        final int x,z;
        final String sourceToken;
        final CompletableFuture<Boolean> result;
        int registrations;
        Request(int x,int z,String sourceToken,CompletableFuture<Boolean> result){
            this.x=x;this.z=z;this.sourceToken=sourceToken;this.result=result;
        }
    }
    private static final class Physical {
        final CompletableFuture<Boolean> result;
        final Coordinate coordinate;
        final String sourceToken;
        final long startedNanos;
        long writeStartedNanos;
        boolean collided;
        Physical(CompletableFuture<Boolean> result,Coordinate coordinate,String sourceToken){
            this.result=result;this.coordinate=coordinate;this.sourceToken=sourceToken;this.startedNanos=System.nanoTime();
        }
    }
    public record LodReceipt(String sourceToken,String dimension,int x,int z,String contentSha256,long startedNanos,long completedNanos){}
    public record PhysicalStatus(int inFlight,long oldestMillis,long unknown){public boolean reliable(){return unknown==0;}}
    private LodCommitTracker(){}

    public static CompletableFuture<Boolean> offer(ServerLevel level,LevelChunk chunk){return offer(level,chunk,null);}
    public static CompletableFuture<Boolean> offer(ServerLevel level,LevelChunk chunk,String sourceToken){
        CompletableFuture<Boolean> result=new CompletableFuture<>();
        Request previous=CURRENT.get();
        Request request=new Request(chunk.getPos().x,chunk.getPos().z,sourceToken,result);
        CURRENT.set(request);
        try{LodSupport.offer(level,chunk);}
        catch(Throwable error){result.completeExceptionally(error);}
        finally{if(previous==null)CURRENT.remove();else CURRENT.set(previous);}
        if(request.registrations!=1&&!result.isDone()) {
            if(request.registrations==0&&sourceToken!=null)
                PregenFlowController.stageUnavailable(sourceToken,PregenFlowController.Stage.LOD);
            result.complete(false);
        }
        result.orTimeout(30,TimeUnit.SECONDS).whenComplete((ok,error)->{
            if(error!=null||!Boolean.TRUE.equals(ok)){
                try{
                    var index=LodSupport.presenceIndexFor(level.getServer(),level.dimension().location().toString());
                    if(index!=null)index.invalidate();
                }catch(Throwable ignored){}
            }
        });
        return result;
    }

    public static void offered(Object value){
        if(!(value instanceof CsLodChunk record))return;
        Request request=CURRENT.get();
        if(request!=null&&record.getChunkX()==request.x&&record.getChunkZ()==request.z){
            request.registrations++;
            track(record,request.result,request.sourceToken);
        }else if(request==null)track(record,null,null);
    }

    static void track(CsLodChunk record,CompletableFuture<Boolean> future){track(record,future,null);}
    static void track(CsLodChunk record,CompletableFuture<Boolean> future,String sourceToken){
        Set<CompletableFuture<Boolean>> unsafe=new HashSet<>();
        boolean overflow=false,overlap=false,started=false;
        synchronized(PHYSICAL){
            Physical duplicate=PHYSICAL.get(record);
            if(duplicate!=null){
                duplicate.collided=true;
                if(future!=null)unsafe.add(future);
                if(duplicate.result!=null)unsafe.add(duplicate.result);
            }else if(PHYSICAL.size()>=MAX_PHYSICAL){
                unknownPhysical++;overflow=true;if(future!=null)unsafe.add(future);
            }else{
                Coordinate coordinate=new Coordinate(record.getDimension(),record.getChunkX(),record.getChunkZ());
                Physical current=new Physical(future,coordinate,sourceToken);
                Set<Physical> existing=BY_COORDINATE.computeIfAbsent(coordinate,ignored->new HashSet<>());
                if(!existing.isEmpty()){
                    current.collided=true;overlap=true;
                    for(Physical old:existing){old.collided=true;if(old.result!=null)unsafe.add(old.result);}
                    if(future!=null)unsafe.add(future);
                }
                existing.add(current);PHYSICAL.put(record,current);started=sourceToken!=null;
            }
        }
        if(started)PregenFlowController.physicalStart(sourceToken,PregenFlowController.Stage.LOD);
        for(CompletableFuture<Boolean> result:unsafe)result.complete(false);
        if(overlap)PipelineMetrics.count("lod-physical-coordinate-overlap");
        if(overflow)PipelineMetrics.count("lod-physical-handle-overflow");
    }

    /** Marks the start of the actual CsLodRegionStore.write call, not queue admission. */
    public static void writing(CsLodChunk record){
        if(record==null)return;
        long queuedAt=0L,started=0L;
        synchronized(PHYSICAL){
            Physical work=PHYSICAL.get(record);
            if(work!=null&&work.writeStartedNanos==0L){
                work.writeStartedNanos=System.nanoTime();
                queuedAt=work.startedNanos;
                started=work.writeStartedNanos;
            }
        }
        if(started!=0L)PipelineMetrics.time("lod-physical-queue-wait",Math.max(0L,started-queuedAt));
    }

    public static void written(CsLodChunk record,Throwable error){
        Physical work;
        synchronized(PHYSICAL){
            work=PHYSICAL.remove(record);
            if(work!=null){
                Set<Physical> active=BY_COORDINATE.get(work.coordinate);
                if(active!=null){active.remove(work);if(active.isEmpty())BY_COORDINATE.remove(work.coordinate);}
            }
        }
        if(work==null)return;
        long completed=System.nanoTime();
        boolean safe=error==null&&!work.collided;
        if(safe&&work.result!=null){
            try{
                RECEIPTS.put(work.result,new LodReceipt(work.sourceToken,record.getDimension(),record.getChunkX(),record.getChunkZ(),
                    PairReceiptStore.lodDigest(record),work.startedNanos,completed));
            }catch(Throwable digestFailure){safe=false;PipelineMetrics.count("lod-receipt-digest-failed");}
        }
        boolean late=work.result!=null&&work.result.isDone();
        if(work.result!=null){
            if(!safe){if(error!=null)work.result.completeExceptionally(error);else work.result.complete(false);}
            else work.result.complete(true);
        }
        if(work.sourceToken!=null) {
            long serviceStarted=work.writeStartedNanos!=0L?work.writeStartedNanos:work.startedNanos;
            if(work.writeStartedNanos==0L)PipelineMetrics.count("lod-physical-service-start-missing");
            PregenFlowController.physicalTerminal(work.sourceToken,PregenFlowController.Stage.LOD,
                Math.max(0L,completed-serviceStarted),safe);
        }
        PipelineMetrics.count(error==null?"lod-physical-write-ack":"lod-physical-write-failed");
        if(late)PipelineMetrics.count("lod-physical-late-terminal");
    }

    public static LodReceipt receiptOf(CompletableFuture<Boolean> future){return future==null?null:RECEIPTS.get(future);}

    /**
     * A PAIR receipt may only be published while no newer queued/running write
     * for the same dimension/coordinate exists. offer() tracks records before
     * they enter the native writer, so this also covers queued and synchronous
     * fallback work.
     */
    static boolean coordinateClear(LodReceipt receipt){
        if(receipt==null)return false;
        Coordinate coordinate=new Coordinate(receipt.dimension(),receipt.x(),receipt.z());
        synchronized(PHYSICAL){
            Set<Physical> active=BY_COORDINATE.get(coordinate);
            return active==null||active.isEmpty();
        }
    }
    public static PhysicalStatus physicalStatus(){
        synchronized(PHYSICAL){
            long now=System.nanoTime(),oldest=0;
            for(Physical work:PHYSICAL.values())oldest=Math.max(oldest,Math.max(0,now-work.startedNanos));
            return new PhysicalStatus(PHYSICAL.size(),TimeUnit.NANOSECONDS.toMillis(oldest),unknownPhysical);
        }
    }
}
