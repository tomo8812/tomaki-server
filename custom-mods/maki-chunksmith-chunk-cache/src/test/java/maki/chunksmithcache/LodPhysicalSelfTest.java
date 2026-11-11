package maki.chunksmithcache;

import com.kishku7.chunksmith.lod.CsLodChunk;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Native physical handles must outlive logical deadlines and never alias coordinates. */
public final class LodPhysicalSelfTest {
    private static CsLodChunk record(int x,int z) {
        return new CsLodChunk("minecraft:overworld",x,z,0,List.of(),List.of(),List.of());
    }
    private static void require(boolean condition,String message) {
        if(!condition)throw new AssertionError(message);
    }
    public static void main(String[] args) {
        CsLodChunk timedOut=record(71,13);
        CompletableFuture<Boolean> old=new CompletableFuture<>();
        LodCommitTracker.track(timedOut,old);
        old.complete(false);
        require(LodCommitTracker.physicalStatus().inFlight()==1,"logical timeout must retain native WIP");
        LodCommitTracker.written(timedOut,null);
        require(LodCommitTracker.physicalStatus().inFlight()==0,"late ACK releases exact handle");
        require(!old.join(),"late ACK cannot resurrect expired PAIR");

        CsLodChunk queued=record(72,13),syncFallback=record(72,13);
        CompletableFuture<Boolean> first=new CompletableFuture<>(),second=new CompletableFuture<>();
        LodCommitTracker.track(queued,first);
        LodCommitTracker.track(syncFallback,second);
        require(first.isDone()&&!first.join()&&second.isDone()&&!second.join(),
            "overlapping writes to one coordinate are unprovable");
        LodCommitTracker.written(syncFallback,null);
        require(LodCommitTracker.physicalStatus().inFlight()==1,"older writer still physically active");
        LodCommitTracker.written(queued,null);
        require(LodCommitTracker.physicalStatus().inFlight()==0,"both native handles ended");

        CsLodChunk fresh=record(73,13);
        CompletableFuture<Boolean> good=new CompletableFuture<>();
        LodCommitTracker.track(fresh,good);
        LodCommitTracker.written(fresh,null);
        LodCommitTracker.written(fresh,null);
        require(Boolean.TRUE.equals(good.join()),"uncontended ACK should succeed exactly once");
        require(LodCommitTracker.physicalStatus().reliable(),"no native record identities were lost");
        System.out.println("LodPhysicalSelfTest passed");
    }
}
