package maki.chunksmithcache;

import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtAccounter;

public final class CachePipelineSelfTest {
    public static void main(String[] args) throws Exception {
        budget();
        promotions();
        envelopes();
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
        Path directory=Files.createTempDirectory("cache-promotion-test");
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
            require(marker!=null && !PromotionClaim.publish(temporary,pending),"normal save closes pending-capture publication race");
            marker.chunkWrite(true);
            require(marker.canRetire(),"payload-free pending marker retires with native chunk ACK");

            Path emptyLive=directory.resolve("empty-live.nbt.gz");
            Files.writeString(emptyLive,"schema2 empty fixture");
            PromotionClaim emptyAdopted=PromotionClaim.adoptLive(emptyLive,false);
            emptyAdopted.chunkWrite(true);
            require(!emptyAdopted.canRetire() && emptyAdopted.beginClassification(),"unknown adopted payload retains entity obligation");
            emptyAdopted.classificationRejected();
            require(emptyAdopted.beginClassification(),"bounded reader rejection can be retried by maintenance");
            emptyAdopted.classified(true);
            require(emptyAdopted.canRetire() && !emptyAdopted.beginClassification(),"verified schema2 empty payload needs no fabricated native entity ACK");
        } finally { deleteTree(directory); }
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
