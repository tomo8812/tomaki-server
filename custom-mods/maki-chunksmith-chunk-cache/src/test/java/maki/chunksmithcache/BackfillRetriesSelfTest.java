package maki.chunksmithcache;

import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import com.kishku7.chunksmith.lod.CsLodChunk;

public final class BackfillRetriesSelfTest {
    public static void main(String[] args) throws Exception {
        DependencyCursor walk=new DependencyCursor(-3,7,11);
        java.util.Set<String> positions=new java.util.HashSet<>();
        int slices=0;
        while (walk.hasNext()) {
            // The slowest permitted slice processes one item. Next slice must
            // continue rather than re-snapshot the already inspected prefix.
            require(positions.add(walk.x()+","+walk.z()),"yield must not repeat a dependency");
            walk.advance(); slices++;
            require(slices<=529,"all yields still reach the ready transition");
        }
        require(slices==529 && positions.contains("-14,-4") && positions.contains("8,18"),"complete exact dependency footprint");
        BackfillRetries ledger=new BackfillRetries();
        String dimension="minecraft_overworld@epoch";
        ledger.defer(dimension,1,1000,"bad-target");
        ledger.defer(dimension,2,1000,"busy-target");
        require(ledger.waiting(dimension,1,1001) && !ledger.waiting(dimension,3,1001),"bad target does not pin healthy discovery");
        require(ledger.due(dimension,6000).size()==2,"failed targets become ready again");
        ledger.defer(dimension,1,6000,"again");
        require(ledger.waiting(dimension,1,15000) && !ledger.waiting(dimension,2,15000),"per-coordinate backoff");
        var directory=Files.createTempDirectory("backfill-retry-test");
        var path=directory.resolve("retries.properties");
        ledger.save(path);
        BackfillRetries restored=new BackfillRetries(); restored.load(path);
        require(restored.waiting(dimension,1,15000),"restart retains debt");
        restored.complete(dimension,1);
        require(!restored.waiting(dimension,1,15000),"success retires only its target");
        var first=new CsLodChunk("minecraft:overworld",0,0,0,java.util.List.of(),java.util.List.of(),java.util.List.of());
        var other=new CsLodChunk("minecraft:overworld",0,0,0,java.util.List.of(),java.util.List.of(),java.util.List.of());
        CompletableFuture<Boolean> receipt=new CompletableFuture<>();
        LodCommitTracker.track(first,receipt);
        LodCommitTracker.written(other,null);
        require(!receipt.isDone(),"another record at the same coordinate is not the job ACK");
        LodCommitTracker.written(first,null);
        require(receipt.join(),"actual native write completes receipt");
        CompletableFuture<Boolean> failed=new CompletableFuture<>(); LodCommitTracker.track(first,failed);
        LodCommitTracker.written(first,new java.io.IOException("disk failure"));
        require(failed.isCompletedExceptionally(),"native writer errors terminate the receipt");
        Files.delete(path); Files.delete(directory);
        System.out.println("BackfillRetriesSelfTest PASS (retry fairness/restart/record ACK)");
    }
    private static void require(boolean ok,String reason) { if (!ok) throw new AssertionError(reason); }
}
