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
        ownershipDeadline();
        publication();
        envelopes();
        disk();
        abi();
        System.out.println("CachePipelineSelfTest passed (bounded admission, localized promotion recovery, ACK/restart, entities envelope, disk reserve, native ABI)");
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

        Path localized=Files.createTempDirectory("cache-recovery-localized");
        try {
            Path promotions=Files.createDirectories(RecoveryClaims.root(localized));
            Path blockedRegion=promotions.resolve("r.9.9");
            Files.writeString(blockedRegion,"unrelated canonical region is inaccessible as a directory");
            Path unrelated=Files.createDirectories(localized.resolve("old-generation/r.9.9"))
                .resolve("c.300.300.nbt.gz.in-use");
            Files.writeString(unrelated,"unrelated interrupted state");
            require(RecoveryClaims.prepare(localized,Runnable::run).isCompletedExceptionally(),
                "an unrelated recovery target collision reproduces dimension-wide initialization failure");
            require(RecoveryClaims.claimForCoordinate(localized,1,2)==null,
                "unrelated recovery failure does not block a coordinate whose ownership absence is proven");

            Path direct=Files.createDirectories(localized.resolve("old-generation/r.0.0"))
                .resolve("c.1.2.nbt.gz.in-use");
            Files.writeString(direct,"target legacy claim");
            require(direct.equals(RecoveryClaims.claimForCoordinate(localized,1,2)),
                "targeted proof finds an active/old-generation legacy claim");

            Path quarantined=Files.createDirectories(localized.resolve(".quarantine/older/r.0.0"))
                .resolve("c.5.6.nbt.gz.in-use");
            Files.writeString(quarantined,"target quarantine claim");
            require(quarantined.equals(RecoveryClaims.claimForCoordinate(localized,5,6)),
                "targeted proof finds a quarantined legacy claim");

            Path conflict=Files.createDirectories(RecoveryClaims.root(localized).resolve(".conflicts/test"))
                .resolve("c.7.8.nbt.gz.in-use");
            Files.writeString(conflict,"conflicting interrupted state");
            require(conflict.equals(RecoveryClaims.claimForCoordinate(localized,7,8)),
                "targeted proof treats preserved conflict payloads as unresolved ownership");

            Path uncertainGeneration=Files.createDirectories(localized.resolve("uncertain-generation"));
            Files.writeString(uncertainGeneration.resolve("r.1.1"),"not-a-directory");
            boolean uncertain=false;
            try { RecoveryClaims.claimForCoordinate(localized,33,34); }
            catch(java.io.IOException expected) { uncertain=true; }
            require(uncertain,
                "target-specific I/O ambiguity fails closed instead of being localized away");

            Files.delete(blockedRegion);
            RecoveryClaims.prepare(localized,Runnable::run).join();
            Path canonical=RecoveryClaims.root(localized).resolve("r.0.0/c.1.2.nbt.gz.in-use");
            require(canonical.equals(RecoveryClaims.claimForCoordinate(localized,1,2))
                    && Files.isRegularFile(canonical),
                "repair retries global recovery and migrates the target claim");
            require(conflict.equals(RecoveryClaims.claimForCoordinate(localized,7,8)),
                "conflict ownership remains visible after global recovery reaches READY");
        } finally { RecoveryClaims.clear(localized); deleteTree(localized); }

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

    private static void ownershipDeadline() {
        CompletableFuture<Path> stalled=new CompletableFuture<>();
        boolean timedOut=false;
        try {
            CacheManager.boundedOwnershipProbe(stalled,25,java.util.concurrent.TimeUnit.MILLISECONDS).join();
        } catch(java.util.concurrent.CompletionException expected) {
            Throwable cause=expected.getCause();
            timedOut=cause instanceof TimeoutException;
        }
        require(timedOut && stalled.isCompletedExceptionally(),
            "coordinate-local ownership proof deadline fails closed instead of waiting forever");
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
        require(!CacheEnvelope.completeEntities(envelope),"schema1 cannot claim entity completeness");
        envelope.putInt("schema",2);
        envelope.putBoolean("lodCommitted",true);
        require(!CacheEnvelope.completeEntities(envelope),"draft schema2 cannot prove native LOD acknowledgement");
        envelope.putInt("schema",CacheEnvelope.SCHEMA);
        envelope.remove("lodCommitted");
        require(!CacheEnvelope.completeEntities(envelope),"missing LOD acknowledgement is not complete");
        envelope.putBoolean("lodCommitted",true);
        require(CacheEnvelope.completeEntities(envelope),"empty entity set is explicitly complete");
        chunk.remove("entities");
        require(!CacheEnvelope.completeEntities(envelope),"missing is not empty");
        ListTag invalid=new ListTag(); invalid.add(StringTag.valueOf("bad")); chunk.put("entities",invalid);
        require(!CacheEnvelope.completeEntities(envelope),"reject wrongly typed entities rather than silently dropping them");
        ListTag roots=new ListTag();
        CompoundTag root=new CompoundTag(); root.putString("id","minecraft:pig");
        ListTag passengers=new ListTag(); CompoundTag rider=new CompoundTag(); rider.putString("id","minecraft:chicken");
        passengers.add(rider); root.put("Passengers",passengers); roots.add(root); chunk.put("entities",roots);
        require(CacheEnvelope.completeEntities(envelope),"root/passenger tree supported");
        Path file=Files.createTempFile("cache-entities",".nbt.gz");
        try {
            NbtIo.writeCompressed(envelope,file);
            CompoundTag restored=NbtIo.readCompressed(file,NbtAccounter.create(1L<<20));
            require(envelope.equals(restored),"entity roots/passengers survive compressed cache roundtrip");
        } finally { Files.deleteIfExists(file); }
    }

    private static void disk() throws Exception {
        var oldSchema=new FingerprintManifest.Entry("cache:envelope-schema",1,CompatibilityFingerprint.sha256("2"),FingerprintManifest.Impact.RELEVANT,"protocol");
        var newSchema=new FingerprintManifest.Entry("cache:envelope-schema",1,CompatibilityFingerprint.sha256("3"),FingerprintManifest.Impact.RELEVANT,"protocol");
        require(!CompatibilityFingerprint.compute(List.of(oldSchema)).equals(CompatibilityFingerprint.compute(List.of(newSchema))),"protocol change cannot reuse old cache paths in generated hints");
        var oldManifest=new FingerprintManifest.Manifest(CompatibilityFingerprint.compute(List.of()),0,java.util.Set.of(),List.of());
        var newManifest=new FingerprintManifest.Manifest(CompatibilityFingerprint.compute(List.of(newSchema)),0,java.util.Set.of(),List.of(newSchema));
        require(!FingerprintManifest.sameCompatibilityInputsExcept(oldManifest,newManifest,java.util.Set.of("policy:compatibility"),null),"historical semantic adoption cannot bypass the missing protocol proof");
        Path root=Files.createTempDirectory("cache-disk-test");
        try {
            Path configuration=root.resolveSibling(root.getFileName()+".properties");
            try {
                Files.write(root.resolve("entry.nbt.gz"),new byte[80]);
                Files.createDirectories(root.resolve(".quarantine"));
                Files.write(root.resolve(".quarantine/old.nbt.gz"),new byte[1000]);
                Files.writeString(configuration,"cache.max-active-bytes=100\ncache.minimum-free-bytes=1\n");
                CacheDiskBudget disk=CacheDiskBudget.at(root);
                require(disk.tryAcquire(1)==null,"unmeasured disk fails closed");
                disk.refresh(configuration);
                var lease=disk.tryAcquire(20);
                require(lease!=null && disk.tryAcquire(1)==null,"active quota excludes quarantine and includes reserved bytes");
                lease.close();
                require(disk.tryAcquire(21)==null,"full active cache rejects promptly");
                Files.move(root.resolve("entry.nbt.gz"),root.resolve(".quarantine/moved.nbt.gz"));
                disk.invalidateInventory(); disk.refresh(configuration);
                var recovered=disk.tryAcquire(100);
                require(recovered!=null,"generation quarantine invalidates active quota promptly");
                recovered.close();
                Files.writeString(configuration,"cache.max-active-bytes=100\ncache.minimum-free-bytes="+Long.MAX_VALUE+"\n");
                disk.refresh(configuration);
                require("cache-disk-reserve".equals(disk.pressure(1)),"normal world save space is reserved");
            } finally { Files.deleteIfExists(configuration); }
        } finally { deleteTree(root); }
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
