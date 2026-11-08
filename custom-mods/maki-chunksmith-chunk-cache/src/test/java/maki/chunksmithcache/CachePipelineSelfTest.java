package maki.chunksmithcache;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtAccounter;

public final class CachePipelineSelfTest {
    public static void main(String[] args) throws Exception {
        budget();
        promotions();
        recovery();
        publication();
        envelopes();
        generationMigration();
        disk();
        abi();
        System.out.println("CachePipelineSelfTest passed (bounded admission, promotion ACK/restart, entities envelope, disk reserve, native ABI)");
    }

    private static void budget() {
        CaptureBudget budget=new CaptureBudget(2,100);
        var first=budget.tryAcquire(40);
        require(first!=null && budget.bytes()==40,"admit estimate");
        require(first.resize(70),"account measured NBT");
        require(budget.tryAcquire(31)==null,"reject excess bytes without waiting");
        var second=budget.tryAcquire(30);
        require(second!=null && budget.tryAcquire(1)==null,"count and byte ceilings");
        require(!second.resize(31),"failed resize preserves accounting");
        first.close(); first.close();
        require(budget.count()==1 && budget.bytes()==30,"idempotent release");
        second.close();
        require(budget.count()==0 && budget.bytes()==0,"all terminal paths return permits");
    }

    private static void promotions() throws Exception {
        Path root=Files.createTempDirectory("cache-promotion-test");
        Path directory=Files.createDirectories(root.resolve("dimension/epoch/r.0.0"));
        try {
            Path zero=directory.resolve("zero.nbt.gz");
            Files.writeString(zero,"zero");
            PromotionClaim empty=PromotionClaim.take(zero,false);
            require(empty!=null && !Files.exists(zero) && Files.exists(empty.path()),"claim atomically consumes available snapshot");
            require(PromotionClaim.take(zero,false)==null,"restart cannot claim unavailable snapshot");
            require(empty.loaded() && !empty.loaded(),"one deserialization owner");
            require(!empty.canRetire(),"save enqueue is not an ACK");
            empty.chunkWrite(true);
            require(empty.canRetire(),"zero entities needs only chunk write ACK");
            require(empty.beginRetirement() && !empty.beginRetirement(),"only one retirement task may be queued for a claim");
            empty.retirementRejected();
            require(empty.beginRetirement(),"queue rejection reopens retirement for the next maintenance pass");
            empty.retirementRejected();

            Path populated=directory.resolve("entities.nbt.gz");
            Files.writeString(populated,"entities");
            PromotionClaim full=PromotionClaim.take(populated,true);
            full.loaded(); full.chunkWrite(true);
            require(!full.canRetire() && !full.acceptsEntityWrite(),"chunk ACK does not acknowledge entity storage");
            full.entitiesRestored();
            require(full.acceptsEntityWrite(),"only entity writes begun after restoration can acknowledge it");
            full.entityWrite(true);
            require(full.canRetire(),"both native stores acknowledged");
            full.entityWrite(false);
            require(!full.canRetire(),"write failure retains recovery payload");
            full.entityWrite(true);
            require(!full.canRetire(),"failure is sticky; no automatic rollback or revival");

            // Simulate restart: only files survive. An accidentally recreated available path
            // cannot override the in-use tombstone, nor can a late capture publish over it.
            Files.writeString(populated,"new available");
            require(PromotionClaim.take(populated,true)==null,"persistent tombstone wins across restart");
            Path temporary=directory.resolve("publication.tmp");
            Files.writeString(temporary,"late snapshot");
            require(!PromotionClaim.publish(temporary,populated),"late publication cannot reopen consumed data");

            Path live=directory.resolve("live.nbt.gz");
            Files.writeString(live,"handoff snapshot");
            PromotionClaim adopted=PromotionClaim.adoptLive(live,false);
            require(adopted!=null && !Files.exists(live),"live handoff claims available snapshot before normal write");
            adopted.chunkWrite(false);
            require(!adopted.canRetire() && PromotionClaim.take(live,true)==null,"failed handoff save cannot replay old snapshot");
            Path pending=directory.resolve("pending.nbt.gz");
            PromotionClaim marker=PromotionClaim.adoptLive(pending,true);
            marker.publicationPending();
            require(marker!=null && !PromotionClaim.publish(temporary,pending),"normal save closes pending-capture publication race");
            marker.chunkWrite(true);
            require(!marker.canRetire(),"native FULL ACK cannot retire the ownership barrier while an older capture can still publish");
            require(!PromotionClaim.publish(temporary,pending),"delayed LOD ACK still cannot reopen stale cache after normal FULL ACK");
            marker.publicationResolved();
            require(marker.canRetire(),"payload-free marker retires only after native FULL ACK and pending capture termination");

            Path emptyLive=directory.resolve("empty-live.nbt.gz");
            Files.writeString(emptyLive,"schema3 empty fixture");
            PromotionClaim emptyAdopted=PromotionClaim.adoptLive(emptyLive,false);
            emptyAdopted.chunkWrite(true);
            require(!emptyAdopted.canRetire() && emptyAdopted.beginClassification(),"unknown adopted payload retains entity obligation");
            emptyAdopted.classificationRejected();
            require(emptyAdopted.beginClassification(),"bounded reader rejection can be retried by maintenance");
            emptyAdopted.classified(true);
            require(emptyAdopted.canRetire() && !emptyAdopted.beginClassification(),"verified schema3 empty payload needs no fabricated native entity ACK");
        } finally { deleteTree(root); }
    }

    private static void recovery() throws Exception {
        Path dimension=Files.createTempDirectory("cache-recovery-test");
        try {
            Path old=Files.createDirectories(dimension.resolve("old-generation/r.0.0")).resolve("c.1.2.nbt.gz.in-use");
            Files.writeString(old,"interrupted player state");
            Path quarantined=Files.createDirectories(dimension.resolve(".quarantine/older/r.0.0")).resolve("c.3.4.nbt.gz.in-use");
            Files.writeString(quarantined,"quarantined interrupted state");
            Path newEntry=dimension.resolve("new-generation/r.0.0/c.1.2.nbt.gz");
            Path claim=PromotionClaim.claimedPath(newEntry);
            AtomicInteger submissions=new AtomicInteger();
            java.util.ArrayList<Runnable> queued=new java.util.ArrayList<>();
            java.util.concurrent.Executor executor=task -> { submissions.incrementAndGet(); queued.add(task); };
            var first=RecoveryClaims.prepare(dimension,executor);
            var concurrent=RecoveryClaims.prepare(dimension,executor);
            require(first==concurrent && !first.isDone() && submissions.get()==1,"startup recovery is single flight without synchronous disk scan");
            queued.removeFirst().run(); first.join();
            require(!Files.exists(old) && Files.readString(claim).equals("interrupted player state"),"legacy payload is the generation-independent tombstone");
            require(Files.isRegularFile(RecoveryClaims.root(dimension).resolve("r.0.0/c.3.4.nbt.gz.in-use")),"recover claims already moved into historical quarantine");
            CacheDirectoryMerge.merge(dimension.resolve("old-generation"),dimension.resolve("merged-generation"));
            deleteTree(dimension.resolve(".quarantine"));
            require(Files.isRegularFile(claim),"merge/quarantine deletion cannot erase coordinate ownership");
            Files.createDirectories(newEntry.getParent()); Files.writeString(newEntry,"new snapshot");
            require(PromotionClaim.take(newEntry,true)==null,"new generation cannot replay an interrupted coordinate");
            Path temporary=dimension.resolve("late.tmp"); Files.writeString(temporary,"late");
            require(!PromotionClaim.publish(temporary,newEntry),"new generation cannot publish over old coordinate ownership");
            RecoveryClaims.clear(dimension);
            RecoveryClaims.prepare(dimension,Runnable::run).join();
            require(Files.isRegularFile(claim),"restart preserves canonical claims before fingerprint readiness");
        } finally { RecoveryClaims.clear(dimension); deleteTree(dimension); }
        Path broken=Files.createTempDirectory("cache-recovery-failure");
        try {
            Path legacy=Files.createDirectories(broken.resolve("old/r.0.0")).resolve("c.1.2.nbt.gz.in-use");
            Files.writeString(legacy,"must survive");
            Files.writeString(RecoveryClaims.root(broken),"blocked-directory");
            require(RecoveryClaims.prepare(broken,Runnable::run).isCompletedExceptionally(),"recovery I/O failure has an explicit terminal result");
            require(Files.isRegularFile(legacy),"failed migration preserves the original payload");
            Files.delete(RecoveryClaims.root(broken));
            RecoveryClaims.prepare(broken,Runnable::run).join();
            require(Files.isRegularFile(RecoveryClaims.root(broken).resolve("r.0.0/c.1.2.nbt.gz.in-use")),"repair retries migration rather than retaining a failed readiness latch");
        } finally { RecoveryClaims.clear(broken); deleteTree(broken); }

        Path ambiguous=Files.createTempDirectory("cache-claim-inspection");
        try {
            Path promotions=RecoveryClaims.root(ambiguous);
            Files.writeString(promotions,"not-a-directory");
            Path claim=promotions.resolve("r.0.0/c.1.2.nbt.gz.in-use");
            boolean failed=false;
            try { RecoveryClaims.claimExists(claim); }
            catch(java.io.IOException expected) { failed=true; }
            require(failed,"claim inspection I/O errors cannot be treated as confirmed absence");
            Path available=Files.createDirectories(ambiguous.resolve("epoch/r.0.0")).resolve("c.1.2.nbt.gz");
            Files.writeString(available,"available cache");
            failed=false;
            try { PromotionClaim.take(available,false); }
            catch(java.io.IOException expected) { failed=true; }
            require(failed && Files.isRegularFile(available),
                "final promotion acquisition propagates ownership I/O failure without consuming or quarantining the cache");
            Files.delete(promotions);
            require(!RecoveryClaims.claimExists(claim),"a genuinely missing claim is a confirmed absence");
            Files.createDirectories(claim);
            failed=false;
            try { RecoveryClaims.claimExists(claim); }
            catch(java.io.IOException expected) { failed=true; }
            require(failed,"non-file ownership state fails closed instead of looking absent");
        } finally { RecoveryClaims.clear(ambiguous); deleteTree(ambiguous); }
    }

    private static void publication() {
        AtomicInteger publications=new AtomicInteger();
        CompletableFuture<Boolean> lod=new CompletableFuture<>(), cache=new CompletableFuture<>();
        PublicationGate.afterLod(lod,cache,() -> { publications.incrementAndGet(); cache.complete(true); });
        require(publications.get()==0 && !cache.isDone(),"detached capture waits without publishing while LOD is pending");
        lod.complete(true);
        require(publications.get()==1 && cache.join(),"native success permits exactly one publication");
        lod.complete(true);
        require(publications.get()==1,"duplicate native completion cannot publish twice");
        for (int failure=0;failure<3;failure++) {
            CompletableFuture<Boolean> failed=new CompletableFuture<>(), result=new CompletableFuture<>();
            PublicationGate.afterLod(failed,result,publications::incrementAndGet);
            if (failure==0) failed.complete(false);
            else failed.completeExceptionally(failure==1 ? new java.io.IOException("native write failed") : new TimeoutException("native ACK expired"));
            require(!result.join() && publications.get()==1,"LOD rejection/error/timeout cannot leave a visible cache that erases retry debt");
        }
        CompletableFuture<Boolean> late=new CompletableFuture<>(), expired=new CompletableFuture<>();
        PublicationGate.afterLod(late,expired,publications::incrementAndGet);
        expired.complete(false); // capture deadline/stop returns detached memory immediately
        late.complete(true);
        require(publications.get()==1,"late LOD ACK cannot revive expired or stopped capture");
        CompletableFuture<Boolean> rejected=new CompletableFuture<>();
        PublicationGate.afterLod(CompletableFuture.completedFuture(true),rejected,() -> { throw new java.util.concurrent.RejectedExecutionException(); });
        require(!rejected.join(),"bounded writer rejection terminates without waiting forever");
    }

    private static void envelopes() throws Exception {
        CompoundTag chunk=new CompoundTag();
        chunk.putString("Status","minecraft:full");
        require(CacheLoadPolicy.keepNormal(chunk),"normal FULL always wins, including when an old in-use artifact exists");
        CompoundTag proto=new CompoundTag(); proto.putString("Status","minecraft:features");
        require(!CacheLoadPolicy.keepNormal(proto) && !CacheLoadPolicy.keepNormal(null),"cache only replaces absent/proto normal data");
        require(CacheLoadPolicy.keepNormal(new CompoundTag()),"unknown normal data fails closed");
        CompoundTag envelope=new CompoundTag();
        envelope.putInt("schema",1);
        envelope.putBoolean("entitiesComplete",true);
        chunk.put("entities",new ListTag()); envelope.put("chunk",chunk);
        require(!CacheEnvelope.promotionSafe(envelope) && !CacheEnvelope.generatedComplete(envelope),
            "schema1 cannot claim entity completeness");
        envelope.putInt("schema",2);
        envelope.putBoolean("lodCommitted",true);
        require(CacheEnvelope.promotionSafe(envelope) && !CacheEnvelope.generatedComplete(envelope),
            "schema2 entity-complete data is promotion-safe but cannot prove native LOD acknowledgement");
        envelope.putInt("schema",CacheEnvelope.SCHEMA);
        envelope.remove("lodCommitted");
        require(CacheEnvelope.promotionSafe(envelope) && !CacheEnvelope.generatedComplete(envelope),
            "schema3 without LOD acknowledgement can promote but cannot suppress retry debt");
        envelope.putBoolean("lodCommitted",true);
        require(CacheEnvelope.promotionSafe(envelope) && CacheEnvelope.generatedComplete(envelope),
            "schema3 with entity and LOD receipts is fully reusable");
        chunk.remove("entities");
        require(!CacheEnvelope.promotionSafe(envelope),"missing entities are not complete");
        ListTag invalid=new ListTag(); invalid.add(StringTag.valueOf("bad")); chunk.put("entities",invalid);
        require(!CacheEnvelope.promotionSafe(envelope),"reject wrongly typed entities rather than silently dropping them");
        ListTag roots=new ListTag();
        CompoundTag root=new CompoundTag(); root.putString("id","minecraft:pig");
        ListTag passengers=new ListTag(); CompoundTag rider=new CompoundTag(); rider.putString("id","minecraft:chicken");
        passengers.add(rider); root.put("Passengers",passengers); roots.add(root); chunk.put("entities",roots);
        require(CacheEnvelope.promotionSafe(envelope),"root/passenger tree supported");
        Path file=Files.createTempFile("cache-entities",".nbt.gz");
        try {
            NbtIo.writeCompressed(envelope,file);
            CompoundTag restored=NbtIo.readCompressed(file,NbtAccounter.create(1L<<20));
            require(envelope.equals(restored),"entity roots/passengers survive compressed cache roundtrip");
        } finally { Files.deleteIfExists(file); }
    }

    private static void generationMigration() throws Exception {
        Path root=Files.createTempDirectory("cache-generation-migration");
        try {
            Path source=Files.createDirectories(root.resolve("source"));
            Path target=Files.createDirectories(root.resolve("target"));
            FingerprintManifest.writeAtomic(
                source.resolve(FingerprintManifest.FILE_NAME),
                new FingerprintManifest.Manifest(
                    "111111111111111111111111", 1L,
                    java.util.Set.of("111111111111111111111111"), List.of()
                )
            );
            writeEnvelope(source,1,1,false,false);
            writeEnvelope(source,2,2,true,false);
            writeEnvelope(source,3,3,true,true);
            Path invalid=source.resolve("r.0.0/c.4.0.nbt.gz");
            Files.createDirectories(invalid.getParent());
            Files.writeString(invalid,"not-nbt");

            CacheGenerationMigration.Stats stats=CacheGenerationMigration.copyReusableEntriesPreservingSource(source,target,"minecraft:overworld");
            require(stats.sourceEntries()==4 && stats.adoptedEntries()==2 && stats.duplicateEntries()==0,
                "migration accounts all source/adopted/duplicate entries");
            require(stats.promotionOnlyEntries()==1 && stats.retainedUnprovenEntries()==1 && stats.retainedInvalidEntries()==1,
                "schema2 is promotion-only while schema1/corrupt data remains retained");
            require(!Files.exists(target.resolve("r.0.0/c.1.0.nbt.gz")),
                "schema1 without entity proof is never copied into active cache");
            Path schema2=target.resolve("r.0.0/c.2.0.nbt.gz");
            Path schema3=target.resolve("r.0.0/c.3.0.nbt.gz");
            require(Files.isRegularFile(schema2) && Files.isRegularFile(schema3),
                "provably safe schema2/3 entries are re-adopted");
            require(!Files.exists(CacheManager.generatedCompleteMarker(schema2))
                    && Files.isRegularFile(CacheManager.generatedCompleteMarker(schema3)),
                "only record-correlated schema3 entries may suppress LOD retry debt");
            require(Files.isRegularFile(source.resolve("r.0.0/c.1.0.nbt.gz"))
                    && Files.isRegularFile(source.resolve("r.0.0/c.2.0.nbt.gz"))
                    && Files.isRegularFile(source.resolve("r.0.0/c.3.0.nbt.gz"))
                    && Files.isRegularFile(invalid),
                "automatic migration preserves every original source file");

            // Simulate a crash after payload publication but before the target manifest was
            // updated. The next run must recognize byte-identical migrated payloads, repair
            // their sidecar state, and still report source fingerprint evidence.
            CacheManager.markGeneratedComplete(schema2);
            Files.deleteIfExists(CacheManager.generatedCompleteMarker(schema3));
            CacheGenerationMigration.Stats replay =
                CacheGenerationMigration.copyReusableEntriesPreservingSource(source,target,"minecraft:overworld");
            require(replay.adoptedEntries()==0 && replay.recoveredExistingEntries()==2
                    && replay.acceptedSourceEntries()==2 && replay.duplicateEntries()==0,
                "interrupted migration recovers exact existing copies instead of treating them as unrelated collisions");
            require(!Files.exists(CacheManager.generatedCompleteMarker(schema2))
                    && Files.isRegularFile(CacheManager.generatedCompleteMarker(schema3)),
                "interrupted migration restores promotion-only/generated-complete sidecars from validated payloads");

            // Simulate a crash after atomic schema-3 publication but before the
            // completion sidecar was written. A validated generated-complete
            // payload must recreate the marker, while schema-2 must not.
            Files.deleteIfExists(CacheManager.generatedCompleteMarker(schema3));
            CompoundTag schema3Envelope=NbtIo.readCompressed(schema3,NbtAccounter.create(1L<<20));
            require(CacheManager.ensureGeneratedCompleteMarker(schema3,schema3Envelope,true)
                    && Files.isRegularFile(CacheManager.generatedCompleteMarker(schema3)),
                "validated schema3 payload repairs a missing completion marker");
            CompoundTag schema2Envelope=NbtIo.readCompressed(schema2,NbtAccounter.create(1L<<20));
            require(!CacheManager.ensureGeneratedCompleteMarker(schema2,schema2Envelope,true)
                    && !Files.exists(CacheManager.generatedCompleteMarker(schema2)),
                "promotion-only schema2 payload cannot manufacture a generated-complete marker");
        } finally { deleteTree(root); }
    }

    private static void writeEnvelope(Path generation,int schema,int x,boolean entitiesComplete,boolean lodCommitted) throws Exception {
        CompoundTag chunk=new CompoundTag();
        chunk.putString("Status","minecraft:full");
        if(schema>=2) chunk.put("entities",new ListTag());
        CompoundTag envelope=new CompoundTag();
        envelope.putInt("schema",schema);
        if(entitiesComplete) envelope.putBoolean("entitiesComplete",true);
        if(lodCommitted) envelope.putBoolean("lodCommitted",true);
        envelope.putString("fingerprint","111111111111111111111111");
        envelope.putString("dimension","minecraft:overworld");
        envelope.putInt("x",x); envelope.putInt("z",0);
        envelope.put("chunk",chunk);
        Path file=generation.resolve("r.0.0/c."+x+".0.nbt.gz");
        Files.createDirectories(file.getParent());
        NbtIo.writeCompressed(envelope,file);
    }

    private static void disk() throws Exception {
        var oldSchema=new FingerprintManifest.Entry("cache:envelope-schema",1,CompatibilityFingerprint.sha256("2"),FingerprintManifest.Impact.RELEVANT,"legacy protocol");
        var oldManifest=new FingerprintManifest.Manifest("111111111111111111111111",0,java.util.Set.of(),List.of(oldSchema));
        var newManifest=new FingerprintManifest.Manifest("222222222222222222222222",0,java.util.Set.of(),List.of());
        require(FingerprintManifest.sameCompatibilityInputsExcept(
                oldManifest,newManifest,java.util.Set.of("policy:compatibility","cache:envelope-schema"),null),
            "serialization schema changes are migration concerns, not generation incompatibility");
        Path root=Files.createTempDirectory("cache-disk-test");
        try {
            Path configuration=root.resolveSibling(root.getFileName()+".properties");
            try {
                String activeFingerprint="aaaaaaaaaaaaaaaaaaaaaaaa";
                FingerprintManifest.writeAtomic(
                    root.resolve(FingerprintManifest.CURRENT_FILE_NAME),
                    new FingerprintManifest.Manifest(
                        activeFingerprint,1L,java.util.Set.of(activeFingerprint),List.of()
                    )
                );
                Files.createDirectories(root.resolve(activeFingerprint));
                for(int i=1;i<=2;i++) {
                    String inactive=String.format("%024x",i);
                    Path retained=Files.createDirectories(root.resolve(inactive).resolve("r.0.0"))
                        .resolve("c."+i+".0.nbt.gz");
                    Files.write(retained,new byte[11]);
                }
                GenerationStore.RetainedDataStats inactiveOnly =
                    GenerationStore.inspectRetainedData(root,FingerprintPolicy.forTesting(List.of()));
                require(inactiveOnly.generations()==2 && inactiveOnly.bytes()>=22,
                    "inactive fingerprint generations are counted even when .quarantine does not exist");

                for(int i=0;i<9;i++) {
                    Path retained=Files.createDirectories(root.resolve(".quarantine/retained-"+i+"/r.0.0"))
                        .resolve("c."+i+".0.nbt.gz");
                    Files.write(retained,new byte[8]);
                }
                GenerationStore.RetainedDataStats retained =
                    GenerationStore.inspectRetainedData(root,FingerprintPolicy.forTesting(List.of()));
                require(retained.generations()==11,
                    "retained warning inventory includes quarantine plus inactive fingerprint generations but excludes active");
                for(int i=0;i<9;i++) require(Files.isRegularFile(root.resolve(".quarantine/retained-"+i+"/r.0.0/c."+i+".0.nbt.gz")),
                    "warning thresholds must never auto-delete retained cache data");

                Path activeEntry=Files.createDirectories(root.resolve(activeFingerprint).resolve("r.0.0"))
                    .resolve("c.100.0.nbt.gz");
                Files.write(activeEntry,new byte[1_000_000]);
                String largeInactiveFingerprint="bbbbbbbbbbbbbbbbbbbbbbbb";
                Path largeInactive=Files.createDirectories(root.resolve(largeInactiveFingerprint).resolve("r.0.0"))
                    .resolve("c.200.0.nbt.gz");
                Files.write(largeInactive,new byte[1_000_000]);

                Files.writeString(configuration,"cache.max-active-bytes=1500000\ncache.minimum-free-bytes=1\n");
                CacheDiskBudget disk=CacheDiskBudget.at(root);
                require(disk.tryAcquire(1)==null,"unmeasured disk fails closed");
                disk.refresh(configuration);
                var lease=disk.tryAcquire(400_000);
                require(lease!=null && disk.tryAcquire(100_001)==null,
                    "active quota counts current generation plus reservations but excludes retained inactive generations");
                lease.close();
                require(disk.tryAcquire(500_001)==null,
                    "current generation payload still enforces the active capacity limit");

                Path moved=root.resolve(".quarantine/moved-active.nbt.gz");
                Files.move(activeEntry,moved);
                disk.invalidateInventory(); disk.refresh(configuration);
                var recovered=disk.tryAcquire(1_400_000);
                require(recovered!=null,
                    "moving the active payload out of the current fingerprint frees payload quota while small current metadata remains budgeted");
                recovered.close();

                Files.writeString(configuration,"cache.max-active-bytes=1500000\ncache.minimum-free-bytes="+Long.MAX_VALUE+"\n");
                disk.refresh(configuration);
                require("cache-disk-reserve".equals(disk.pressure(1)),"normal world save space is reserved");
            } finally { Files.deleteIfExists(configuration); }
        } finally { deleteTree(root); }

        Path retryRoot=Files.createTempDirectory("cache-recovery-retry-test");
        try {
            String sourceFingerprint="111111111111111111111111";
            String targetFingerprint="222222222222222222222222";
            Path source=Files.createDirectories(retryRoot.resolve(sourceFingerprint));
            Path payload=Files.createDirectories(source.resolve("r.0.0")).resolve("c.1.0.nbt.gz");
            Files.write(payload,new byte[]{1,2,3});
            FingerprintManifest.Manifest activeManifest=new FingerprintManifest.Manifest(
                targetFingerprint,1L,java.util.Set.of(targetFingerprint,sourceFingerprint),List.of()
            );
            Files.writeString(
                source.resolve("recovery-info.txt"),
                "sourceFingerprint="+sourceFingerprint+"\n"
                    +"targetFingerprint="+targetFingerprint+"\n"
                    +"migrationContract="+CacheGenerationMigration.MIGRATION_CONTRACT+"\n"
                    +"sourcePreserved=true\n"
                    +"acceptedSourceEntries=0\n"
                    +"retainedInvalidEntries=1\n"
            );
            require(!GenerationStore.alreadyRecovered(source,targetFingerprint,sourceFingerprint,activeManifest),
                "retained invalid/unreadable entries keep a generation eligible for retry after temporary I/O failure");

            long newest=Files.getLastModifiedTime(payload).toMillis();
            Files.writeString(
                source.resolve("recovery-info.txt"),
                "sourceFingerprint="+sourceFingerprint+"\n"
                    +"targetFingerprint="+targetFingerprint+"\n"
                    +"migrationContract="+CacheGenerationMigration.MIGRATION_CONTRACT+"\n"
                    +"sourcePreserved=true\n"
                    +"acceptedSourceEntries=0\n"
                    +"retainedInvalidEntries=0\n"
                    +"sourceInventoryEntries=1\n"
                    +"sourceInventoryBytes=3\n"
                    +"sourceInventoryNewestMillis="+newest+"\n"
            );
            require(GenerationStore.alreadyRecovered(source,targetFingerprint,sourceFingerprint,activeManifest),
                "clean unchanged source may be skipped after a completed migration scan");
            Files.write(payload,new byte[]{1,2,3,4});
            require(!GenerationStore.alreadyRecovered(source,targetFingerprint,sourceFingerprint,activeManifest),
                "source inventory changes invalidate recovery-info and force a re-scan");
        } finally { deleteTree(retryRoot); }
    }

    private static void abi() throws Exception {
        String jar=System.getProperty("cachePipeline.minecraftJar");
        if(jar==null) throw new AssertionError("cachePipeline.minecraftJar must identify the actual NeoForge compiled artifact");
        String map=bytecode(jar,"net.minecraft.server.level.ChunkMap");
        String storage=bytecode(jar,"net.minecraft.world.level.chunk.storage.ChunkStorage");
        String worker=bytecode(jar,"net.minecraft.world.level.chunk.storage.IOWorker");
        String entity=bytecode(jar,"net.minecraft.world.level.chunk.storage.EntityStorage");
        String serializer=bytecode(jar,"net.minecraft.world.level.chunk.storage.ChunkSerializer");
        String statuses=bytecode(jar,"net.minecraft.world.level.chunk.status.ChunkStatusTasks");
        require(map.contains("Method write:(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/nbt/CompoundTag;)Ljava/util/concurrent/CompletableFuture;"),"ChunkMap save write invocation ABI");
        require(storage.contains("IOWorker.store:"),"native write returns IOWorker store Future");
        int scanner=worker.indexOf("lambda$scanChunk$12(");
        int nativeRead=worker.indexOf("RegionFileStorage.scanChunk:",scanner);
        require(scanner>=0 && nativeRead>scanner && worker.substring(scanner,nativeRead).contains("Field pendingWrites:"),"ownership scanner sees pending writes before disk");
        require(worker.contains("RegionFileStorage.write:") && worker.contains("CompletableFuture.complete:"),"store ACK follows region write");
        require(occurrences(entity,"SimpleRegionStorage.write:")==2,"entity empty and nonempty store write call sites");
        require(serializer.contains("ServerLevel.addLegacyChunkEntities:") && serializer.contains("PoiManager.checkConsistencyWithBlocks:"),"FULL entity restoration and POI reconstruction paths");
        require(statuses.contains("LevelChunk.runPostLoad:") && statuses.contains("ServerLevel.addWorldGenChunkEntities:"),"FULL conversion inserts generated entities before completion");
    }

    private static String bytecode(String jar,String name) throws Exception {
        Process process=new ProcessBuilder(Path.of(System.getProperty("java.home"),"bin/javap").toString(),"-c","-p","-classpath",jar,name).redirectErrorStream(true).start();
        String result=new String(process.getInputStream().readAllBytes(),java.nio.charset.StandardCharsets.UTF_8);
        require(process.waitFor()==0,"javap "+name); return result;
    }
    private static int occurrences(String source,String token) { return (source.length()-source.replace(token,"").length())/token.length(); }
    private static void deleteTree(Path root) throws Exception {
        try(var files=Files.walk(root)) { for(Path file:files.sorted(java.util.Comparator.reverseOrder()).toList()) Files.deleteIfExists(file); }
    }
    private static void require(boolean condition,String message) { if(!condition) throw new AssertionError(message); }
}
