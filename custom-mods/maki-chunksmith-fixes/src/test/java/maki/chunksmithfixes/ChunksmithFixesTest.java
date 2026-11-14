package maki.chunksmithfixes;

import com.kishku7.chunksmith.lod.CsLodChunk;
import com.kishku7.chunksmith.lod.CsLodPresenceIndex;
import com.kishku7.chunksmith.lod.CsLodRegionStore;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.server.level.ChunkResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class ChunksmithFixesTest {
    @TempDir Path temp;
    private final java.util.ArrayList<CsLodRegionStore> stores = new java.util.ArrayList<>();
    private CsLodRegionStore open(Path root) { var store = new CsLodRegionStore(root); stores.add(store); return store; }
    @AfterEach void reset() throws IOException {
        for (var store : stores) store.close();
        SavedLodPresence.clear(); LodWriteEvents.observe(null); ExploredLodRefresh.clear();
    }
    private static CsLodChunk record(int x, String value) {
        return new CsLodChunk("minecraft:overworld", x, -1, 0, List.of(value), List.of(), List.of());
    }
    private static int size(Object instance, String field) throws Exception {
        var member = instance.getClass().getDeclaredField(field); member.setAccessible(true);
        return ((Map<?,?>)member.get(instance)).size();
    }
    @Test void presenceBecomesTrueOnlyAfterRealStoreWrite() throws Exception {
        var index = new CsLodPresenceIndex(temp);
        SavedLodPresence.register(temp, index);
        var record = record(3, "minecraft:stone");
        assertFalse(index.hasLod(3, -1));
        var order = new LodWriteOrder(); order.submitted(record);
        assertFalse(index.hasLod(3, -1), "queued work is not saved data");
        { var store = open(temp);
            order.write(record, () -> {
                int bytes = store.write(record); SavedLodPresence.written(temp, record); return bytes;
            });
            assertTrue(index.hasLod(3, -1));
            assertEquals("minecraft:stone", store.read(3, -1).getBlockPalette().getFirst());
        }
    }
    @Test void failedWriteRemainsMissingAndCanBeRetried() throws Exception {
        Path root = temp.resolve("store"); Files.writeString(root, "not a directory");
        var index = new CsLodPresenceIndex(root); SavedLodPresence.register(root, index);
        var record = record(5, "minecraft:stone"); var order = new LodWriteOrder();
        order.submitted(record);
        { var store = open(root);
            assertThrows(IOException.class, () -> order.write(record, () -> store.write(record)));
        }
        assertFalse(index.hasLod(5, -1)); assertEquals(0, size(order, "pending"));
        Files.delete(root); order.submitted(record);
        { var store = open(root);
            order.write(record, () -> { int n = store.write(record); SavedLodPresence.written(root, record); return n; });
            assertTrue(index.hasLod(5, -1));
        }
    }
    @Test void synchronousNewerRecordCannotBeOverwrittenByOlderQueuedRecord() throws Exception {
        var old = record(8, "minecraft:stone"); var fresh = record(8, "minecraft:dirt");
        var order = new LodWriteOrder(); order.submitted(old); order.submitted(fresh);
        { var store = open(temp);
            order.write(fresh, () -> store.write(fresh));
            assertFalse(order.write(old, () -> { fail("stale snapshot must not be written"); return 1; }).written());
            assertEquals("minecraft:dirt", store.read(8, -1).getBlockPalette().getFirst());
        }
        assertEquals(0, size(order, "pending")); assertEquals(0, size(order, "slots"));
    }
    @Test void failedNewerWriteDoesNotSuppressOlderUsableRecord() throws Exception {
        var old = record(1, "minecraft:stone"); var fresh = record(1, "minecraft:dirt");
        var order = new LodWriteOrder(); order.submitted(old); order.submitted(fresh);
        assertThrows(IOException.class, () -> order.write(fresh, () -> { throw new IOException("disk failure"); }));
        assertEquals(17, order.write(old, () -> 17).bytes()); assertEquals(0, size(order, "slots"));
    }
    @Test void submissionsDoNotWaitForDiskIoButWritesSerialize() throws Exception {
        var order = new LodWriteOrder(); var first = record(1, "one"); var second = record(2, "two");
        order.submitted(first);
        var entered = new CountDownLatch(1); var release = new CountDownLatch(1);
        try (var executor = Executors.newSingleThreadExecutor()) {
            var writing = executor.submit(() -> order.write(first, () -> {
                entered.countDown();
                try { if (!release.await(5, TimeUnit.SECONDS)) throw new IOException("test timeout"); }
                catch (InterruptedException e) { throw new IOException(e); }
                return 1;
            }));
            assertTrue(entered.await(5, TimeUnit.SECONDS));
            try { order.submitted(second); } finally { release.countDown(); }
            assertEquals(1, writing.get(5, TimeUnit.SECONDS).bytes());
            assertEquals(2, order.write(second, () -> 2).bytes());
        }
    }
    @Test void savedWorldsWithSameCoordinatesHaveIndependentPresence() throws Exception {
        var first = new CsLodPresenceIndex(temp.resolve("first"));
        var second = new CsLodPresenceIndex(temp.resolve("second"));
        SavedLodPresence.register(temp.resolve("first"), first); SavedLodPresence.register(temp.resolve("second"), second);
        assertFalse(first.hasLod(0, -1)); assertFalse(second.hasLod(0, -1));
        SavedLodPresence.written(temp.resolve("first"), record(0, "one"));
        assertTrue(first.hasLod(0, -1)); assertFalse(second.hasLod(0, -1));
    }
    @Test void existingLodDoesNotRefreshOnOrdinarySaveOrUnload(){
        assertFalse(LodRefreshPolicy.shouldTake(true,true,true,true,true));
        assertFalse(LodRefreshPolicy.shouldTake(true,true,true,false,true));
        assertTrue(LodRefreshPolicy.shouldTake(true,true,true,false,false));
    }
    private void submit(Path root,CsLodChunk record){ExploredLodRefresh.submitted(root,record);}
    @Test void failedInitialLodNeverCreatesUpdateTargetOrReceipt()throws Exception{
        var record=record(1,"one");submit(temp,record);
        ExploredLodRefresh.completed(record,LodWriteEvents.Outcome.FAILED);
        ExploredLodRefresh.flush(temp).join();
        assertFalse(ExploredLodRefresh.needsRefresh(temp,1,-1));assertTrue(ExploredLodRefresh.targets(temp).isEmpty());
        assertFalse(Files.exists(temp.resolve("maki-lod-refresh.pending")));
    }
    @Test void overlappingInitialLodFailureAlsoLeavesRecoveryToNativeScan()throws Exception{
        var old=record(1,"one");var fresh=record(1,"two");submit(temp,old);submit(temp,fresh);
        ExploredLodRefresh.completed(fresh,LodWriteEvents.Outcome.FAILED);ExploredLodRefresh.completed(old,LodWriteEvents.Outcome.WRITTEN);
        ExploredLodRefresh.flush(temp).join();assertTrue(ExploredLodRefresh.targets(temp).isEmpty());
    }
    @Test void explicitUpdateCompletesOnlyOnExactWriteAck()throws Exception{
        var record=record(3,"one");var other=record(3,"other");
        var result=ExploredLodRefresh.capture(temp,3,-1,()->submit(temp,record));assertFalse(result.isDone());
        ExploredLodRefresh.completed(other,LodWriteEvents.Outcome.WRITTEN);assertFalse(result.isDone());
        ExploredLodRefresh.completed(record,LodWriteEvents.Outcome.WRITTEN);result.join();
        // The update owner acknowledges its captured version, then resolves the target.
        assertTrue(ExploredLodRefresh.needsRefresh(temp,3,-1));ExploredLodRefresh.resolved(temp,3,-1);
        ExploredLodRefresh.flush(temp).join();assertTrue(ExploredLodRefresh.targets(temp).isEmpty());
    }
    @Test void failedUpdateRemainsPendingAcrossRestart()throws Exception{
        var record=record(4,"one");var result=ExploredLodRefresh.capture(temp,4,-1,()->submit(temp,record));
        ExploredLodRefresh.completed(record,LodWriteEvents.Outcome.FAILED);assertThrows(CompletionException.class,result::join);
        ExploredLodRefresh.clear();ExploredLodRefresh.flush(temp).join();assertTrue(ExploredLodRefresh.needsRefresh(temp,4,-1));
    }
    @Test void noLodSubmissionFailsUpdateWithoutClearingTarget()throws Exception{
        var result=ExploredLodRefresh.capture(temp,5,-1,()->{});assertThrows(CompletionException.class,result::join);
        ExploredLodRefresh.flush(temp).join();assertTrue(ExploredLodRefresh.needsRefresh(temp,5,-1));
    }
    @Test void unsuccessfulChunkResultFailsAfterCleanupAndSuccessfulResultCompletes() {
        var cleaned = new AtomicBoolean();
        var failed = CompletableFuture.completedFuture(ChunkResult.error("unloaded"))
            .whenComplete((value, error) -> cleaned.set(true)).thenApply(NativeChunkResult::complete);
        assertThrows(CompletionException.class, failed::join); assertTrue(cleaned.get());
        assertNull(NativeChunkResult.complete(ChunkResult.of("FULL")));
        assertThrows(CompletionException.class, () -> NativeChunkResult.complete(null));
    }
}
