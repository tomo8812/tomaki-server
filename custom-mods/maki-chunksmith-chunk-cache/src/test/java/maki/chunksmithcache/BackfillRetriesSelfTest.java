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
        var directory=Files.createTempDirectory("backfill-retry-test");
        var path=directory.resolve("retries.properties");
        BackfillRetries ledger=new BackfillRetries();
        require(ledger.load(path),"missing initial retry ledger is a confirmed empty state");
        String dimension="minecraft_overworld@epoch";
        ledger.defer(dimension,1,1000,"bad-target");
        ledger.defer(dimension,2,1000,"busy-target");
        require(ledger.waiting(dimension,1,1001) && !ledger.waiting(dimension,3,1001),"bad target does not pin healthy discovery");
        require(ledger.due(dimension,6000).size()==2,"failed targets become ready again");
        ledger.defer(dimension,1,6000,"again");
        require(ledger.waiting(dimension,1,15000) && !ledger.waiting(dimension,2,15000),"per-coordinate backoff");
        ledger.save(path);
        BackfillRetries restored=new BackfillRetries(); restored.load(path);
        require(restored.waiting(dimension,1,15000),"restart retains debt");
        restored.complete(dimension,1);
        require(!restored.waiting(dimension,1,15000),"success retires only its target");

        var guardedPath=directory.resolve("guarded.properties");
        BackfillRetries persisted=new BackfillRetries();
        require(persisted.load(guardedPath),"missing retry ledger is a confirmed empty state");
        require(persisted.defer(dimension,10,1000,"persisted-before-outage"),"seed persisted retry debt");
        persisted.save(guardedPath);
        var backup=directory.resolve("guarded.backup");
        Files.move(guardedPath,backup);
        Files.createDirectory(guardedPath); // deterministic read failure, independent of OS permission semantics
        BackfillRetries guarded=new BackfillRetries();
        require(!guarded.load(guardedPath),"retry ledger read failure stays unresolved");
        require(guarded.defer(dimension,11,1000,"observed-during-outage"),"new debt is retained in memory while disk state is unresolved");
        boolean refused=false;
        try { guarded.save(guardedPath); } catch(java.io.IOException expected) { refused=true; }
        require(refused,"unresolved ledger cannot be overwritten by a partial in-memory map");
        Files.delete(guardedPath);
        Files.move(backup,guardedPath);
        require(guarded.load(guardedPath),"storage repair retries the persisted ledger read");
        guarded.save(guardedPath);
        BackfillRetries merged=new BackfillRetries();
        require(merged.load(guardedPath)
            && merged.waiting(dimension,10,1001)
            && merged.waiting(dimension,11,1001),
            "repair merges persisted debt with failures observed during the read outage");

        var malformedPath=directory.resolve("malformed.properties");
        String malformedSource=dimension+"/20=1,6000,persisted-direct\nmalformed-entry=not-a-retry\n";
        Files.writeString(malformedPath,malformedSource);
        BackfillRetries malformed=new BackfillRetries();
        require(!malformed.load(malformedPath),"malformed persisted debt keeps the entire ledger unresolved");
        require(malformed.defer(dimension,21,1000,"observed-while-malformed"),"new debt is retained while malformed persisted state is unresolved");
        boolean malformedRefused=false;
        try { malformed.save(malformedPath); } catch(java.io.IOException expected) { malformedRefused=true; }
        require(malformedRefused && Files.readString(malformedPath).equals(malformedSource),
            "partial parse must never overwrite malformed persisted debt");
        Files.writeString(malformedPath,dimension+"/20=1,6000,persisted-direct\n");
        require(malformed.load(malformedPath),"repairing malformed persisted debt allows a fresh authoritative load");
        malformed.save(malformedPath);
        BackfillRetries malformedMerged=new BackfillRetries();
        require(malformedMerged.load(malformedPath)
            && malformedMerged.waiting(dimension,20,1001)
            && malformedMerged.waiting(dimension,21,1001),
            "repair preserves valid persisted debt and merges failures observed while malformed");

        require(BackfillController.retrySatisfied(CacheManager.CacheState.COMPLETE,NormalChunkStorage.State.ABSENT),
            "validated schema3 cache can retire retry debt");
        require(BackfillController.retrySatisfied(CacheManager.CacheState.UNKNOWN,NormalChunkStorage.State.FULL),
            "authoritative normal FULL can retire debt even when cache ownership is unreadable");
        require(!BackfillController.retrySatisfied(CacheManager.CacheState.UNKNOWN,NormalChunkStorage.State.ABSENT)
            && BackfillController.retryBlockedByCache(CacheManager.CacheState.UNKNOWN),
            "inconclusive claim inspection blocks generation without retiring retry debt");
        require(!BackfillController.retrySatisfied(CacheManager.CacheState.CLAIMED,NormalChunkStorage.State.ABSENT)
            && BackfillController.retryBlockedByCache(CacheManager.CacheState.CLAIMED),
            "an active promotion claim is ownership, not proof that retry work completed");
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
        Files.delete(path); Files.delete(guardedPath); Files.delete(malformedPath); Files.delete(directory);
        System.out.println("BackfillRetriesSelfTest PASS (retry fairness/restart/record ACK)");
    }
    private static void require(boolean ok,String reason) { if (!ok) throw new AssertionError(reason); }
}
