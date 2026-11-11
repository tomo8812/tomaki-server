package maki.chunksmithcache;

import com.kishku7.chunksmith.lod.CsLodChunk;
import com.kishku7.chunksmith.lod.CsLodRegionStore;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

/**
 * Durable PAIR receipt. A sidecar alone never proves completion: restart
 * revalidation also checks the current FULL file identity and decodes/hashes
 * the current LOD slot.
 */
final class PairReceiptStore {
    enum PublishResult { NEW, EXISTING, FAILED }
    private static final int VERSION=2;
    private static final int VALIDATION_CACHE_LIMIT=4096;
    private static final String PROCESS_REVALIDATION_ID=UUID.randomUUID().toString();
    private static final Map<Path,ValidationStamp> VALIDATED=Collections.synchronizedMap(
        new LinkedHashMap<Path,ValidationStamp>(256,0.75f,true) {
            @Override protected boolean removeEldestEntry(Map.Entry<Path,ValidationStamp> eldest) {
                return size()>VALIDATION_CACHE_LIMIT;
            }
        });
    private record FullMetadata(long size,long mtime,String fileKey) {}
    private record FullIdentity(long size,long mtime,String fileKey,String contentSha256) {}
    private record ValidationStamp(long fullSize,long fullMtime,String fullKey,String fullSha256,long lodSize,long lodMtime) {}
    private PairReceiptStore() {}

    static PublishResult publish(ServerLevel level,ChunkPos pos,String fingerprint,Path full,String sourceToken,LodCommitTracker.LodReceipt lod) {
        if(level==null||pos==null||fingerprint==null||full==null||sourceToken==null||lod==null
            ||!sourceToken.equals(lod.sourceToken())||lod.x()!=pos.x||lod.z()!=pos.z
            ||!level.dimension().location().toString().equals(lod.dimension()))return PublishResult.FAILED;
        try(var ignored=CachePathLocks.lock(PromotionClaim.claimedPath(full))) {
            FullIdentity fullIdentity=fullIdentity(full);
            long fullSize=fullIdentity.size(),fullMtime=fullIdentity.mtime();
            if(!currentLodMatches(level,pos,lod)) {
                PipelineMetrics.count("pair-receipt-lod-raced");
                return PublishResult.FAILED;
            }
            Path region=lodRegion(level,pos);
            long lodSize=Files.size(region),lodMtime=Files.getLastModifiedTime(region).toMillis();
            // completionIdentity is canonical content/state, not an attempt or
            // filesystem-instance identity. mtime/fileKey remain revalidation metadata.
            String completion=hex(sha256((level.dimension().location()+"\n"+pos.x+"\n"+pos.z+"\n"+fingerprint+"\n"
                +fullIdentity.contentSha256()+"\n"+lod.contentSha256())
                .getBytes(java.nio.charset.StandardCharsets.UTF_8)));
            Path receipt=receipt(full);
            Properties prior=load(receipt);
            boolean existing=prior!=null
                &&Integer.toString(VERSION).equals(prior.getProperty("version"))
                &&completion.equals(prior.getProperty("completion"));
            Properties properties=new Properties();
            properties.setProperty("version",Integer.toString(VERSION));
            properties.setProperty("dimension",level.dimension().location().toString());
            properties.setProperty("x",Integer.toString(pos.x));
            properties.setProperty("z",Integer.toString(pos.z));
            properties.setProperty("fingerprint",fingerprint);
            properties.setProperty("source",sourceToken);
            properties.setProperty("fullSize",Long.toString(fullSize));
            properties.setProperty("fullMtime",Long.toString(fullMtime));
            properties.setProperty("fullFileKey",fullIdentity.fileKey());
            properties.setProperty("fullSha256",fullIdentity.contentSha256());
            properties.setProperty("lodSha256",lod.contentSha256());
            properties.setProperty("completion",completion);
            properties.setProperty("confirmedAt",Long.toString(System.currentTimeMillis()));
            properties.setProperty("revalidatedProcess",PROCESS_REVALIDATION_ID);
            if(!existing || !receiptMetadataMatches(prior,properties))writeReceipt(receipt,properties);
            // The sidecar is the PAIR commit point. Re-check after its atomic
            // publication so a same-coordinate LOD offer/write racing the receipt
            // cannot make an older source look current.
            if(!currentLodMatches(level,pos,lod)) {
                invalidate(full);
                PipelineMetrics.count("pair-receipt-lod-raced");
                return PublishResult.FAILED;
            }
            long finalLodSize=Files.size(region),finalLodMtime=Files.getLastModifiedTime(region).toMillis();
            VALIDATED.put(receipt,new ValidationStamp(fullSize,fullMtime,fullIdentity.fileKey(),fullIdentity.contentSha256(),finalLodSize,finalLodMtime));
            return existing?PublishResult.EXISTING:PublishResult.NEW;
        } catch(Throwable error) {
            PipelineMetrics.count("pair-receipt-publish-failed");
            return PublishResult.FAILED;
        }
    }

    static boolean revalidate(ServerLevel level,ChunkPos pos,String fingerprint,Path full) {
        if(level==null||pos==null||fingerprint==null||full==null)return false;
        Path receipt=receipt(full);
        try {
            Properties properties=load(receipt);
            if(properties==null||Integer.parseInt(properties.getProperty("version","-1"))!=VERSION
                ||!level.dimension().location().toString().equals(properties.getProperty("dimension"))
                ||pos.x!=Integer.parseInt(properties.getProperty("x","2147483647"))
                ||pos.z!=Integer.parseInt(properties.getProperty("z","2147483647"))
                ||!fingerprint.equals(properties.getProperty("fingerprint")))return false;
            FullMetadata metadata=fullMetadata(full);
            long fullSize=metadata.size(),fullMtime=metadata.mtime();
            Path region=lodRegion(level,pos);
            long lodSize=Files.size(region),lodMtime=Files.getLastModifiedTime(region).toMillis();
            ValidationStamp cached=VALIDATED.get(receipt);
            if(cached!=null&&cached.fullSize()==fullSize&&cached.fullMtime()==fullMtime
                &&cached.fullKey().equals(metadata.fileKey())
                &&cached.lodSize()==lodSize&&cached.lodMtime()==lodMtime)return true;
            FullIdentity fullIdentity=new FullIdentity(fullSize,fullMtime,metadata.fileKey(),fileSha256(full));
            if(fullSize!=Long.parseLong(properties.getProperty("fullSize","-1"))
                ||fullMtime!=Long.parseLong(properties.getProperty("fullMtime","-1"))
                ||!fullIdentity.fileKey().equals(properties.getProperty("fullFileKey",""))
                ||!fullIdentity.contentSha256().equals(properties.getProperty("fullSha256","")))return false;
            CsLodRegionStore store=new CsLodRegionStore(CacheManager.lodRoot(level));
            CsLodChunk record;
            try{record=store.read(pos.x,pos.z);}
            finally{try{store.close();}catch(IOException ignored){}}
            if(record==null||!properties.getProperty("lodSha256","").equals(lodDigest(record)))return false;
            VALIDATED.put(receipt,new ValidationStamp(fullSize,fullMtime,fullIdentity.fileKey(),fullIdentity.contentSha256(),lodSize,lodMtime));
            if(!PROCESS_REVALIDATION_ID.equals(properties.getProperty("revalidatedProcess"))) {
                properties.setProperty("revalidatedProcess",PROCESS_REVALIDATION_ID);
                writeReceipt(receipt,properties);
                PregenFlowController.pairRevalidated();
            }
            return true;
        } catch(Throwable invalid) {
            VALIDATED.remove(receipt);
            return false;
        }
    }

    static boolean fullProven(ServerLevel level,ChunkPos pos,String fingerprint,Path full) {
        if(level==null||pos==null||fingerprint==null||full==null)return false;
        Path receipt=receipt(full);
        try {
            Properties properties=load(receipt);
            if(!receiptHeaderMatches(level,pos,fingerprint,properties))return false;
            FullMetadata metadata=fullMetadata(full);
            ValidationStamp cached=VALIDATED.get(receipt);
            String expectedHash=properties.getProperty("fullSha256","");
            if(cached!=null&&cached.fullSize()==metadata.size()&&cached.fullMtime()==metadata.mtime()
                &&cached.fullKey().equals(metadata.fileKey())&&cached.fullSha256().equals(expectedHash))return true;
            FullIdentity identity=new FullIdentity(metadata.size(),metadata.mtime(),metadata.fileKey(),fileSha256(full));
            boolean valid=identity.size()==Long.parseLong(properties.getProperty("fullSize","-1"))
                &&identity.mtime()==Long.parseLong(properties.getProperty("fullMtime","-1"))
                &&identity.fileKey().equals(properties.getProperty("fullFileKey",""))
                &&identity.contentSha256().equals(expectedHash);
            if(valid)VALIDATED.put(receipt,new ValidationStamp(identity.size(),identity.mtime(),identity.fileKey(),
                identity.contentSha256(),-1L,-1L));
            return valid;
        } catch(Throwable invalid) {
            VALIDATED.remove(receipt);
            return false;
        }
    }

    private static boolean receiptHeaderMatches(ServerLevel level,ChunkPos pos,String fingerprint,Properties properties) {
        return properties!=null&&Integer.toString(VERSION).equals(properties.getProperty("version"))
            &&level.dimension().location().toString().equals(properties.getProperty("dimension"))
            &&Integer.toString(pos.x).equals(properties.getProperty("x"))
            &&Integer.toString(pos.z).equals(properties.getProperty("z"))
            &&fingerprint.equals(properties.getProperty("fingerprint"));
    }

    static void invalidate(Path full){
        if(full==null)return;
        Path sidecar=receipt(full);
        VALIDATED.remove(sidecar);
        try{Files.deleteIfExists(sidecar);}catch(IOException ignored){}
    }
    static void clear(){VALIDATED.clear();}

    static String lodDigest(CsLodChunk record) {
        try {
            MessageDigest digest=MessageDigest.getInstance("SHA-256");
            try(DataOutputStream out=new DataOutputStream(new DigestOutputStream(OutputStream.nullOutputStream(),digest))) {
                writeString(out,record.getDimension());
                out.writeInt(record.getChunkX());out.writeInt(record.getChunkZ());out.writeInt(record.getMinSectionY());
                writeStrings(out,record.getBlockPalette());writeStrings(out,record.getBiomePalette());
                out.writeInt(record.getSections().size());
                for(CsLodChunk.Section section:record.getSections()) {
                    writeInts(out,section.getBlocks());out.writeInt(section.getUniformBlock());
                    writeInts(out,section.getBiomes());out.writeInt(section.getUniformBiome());
                    writeBytes(out,section.getSkyLight());out.writeInt(section.getUniformSky());
                    writeBytes(out,section.getBlockLight());out.writeInt(section.getUniformBlockLight());
                }
            }
            return hex(digest.digest());
        } catch(Exception error) {
            throw new IllegalStateException("Could not hash CsLodChunk",error);
        }
    }

    private static boolean currentLodMatches(ServerLevel level,ChunkPos pos,LodCommitTracker.LodReceipt receipt) {
        if(!LodCommitTracker.coordinateClear(receipt))return false;
        CsLodRegionStore store=new CsLodRegionStore(CacheManager.lodRoot(level));
        try {
            CsLodChunk record=store.read(pos.x,pos.z);
            return record!=null
                &&record.getChunkX()==pos.x&&record.getChunkZ()==pos.z
                &&level.dimension().location().toString().equals(record.getDimension())
                &&receipt.contentSha256().equals(lodDigest(record));
        } catch(Throwable invalid) {
            return false;
        } finally {
            try{store.close();}catch(IOException ignored){}
        }
    }

    private static boolean receiptMetadataMatches(Properties prior,Properties current) {
        if(prior==null||current==null)return false;
        for(String key:new String[]{"version","dimension","x","z","fingerprint","source","fullSize","fullMtime",
            "fullFileKey","fullSha256","lodSha256","completion","revalidatedProcess"}) {
            if(!java.util.Objects.equals(prior.getProperty(key),current.getProperty(key)))return false;
        }
        return true;
    }

    private static void writeReceipt(Path receipt,Properties properties)throws IOException {
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        properties.store(bytes,"maki pregen PAIR receipt");
        Files.createDirectories(receipt.getParent());
        Path temporary=receipt.resolveSibling(receipt.getFileName()+".tmp-"+Long.toUnsignedString(System.nanoTime()));
        try {
            try(FileChannel channel=FileChannel.open(temporary,StandardOpenOption.CREATE_NEW,StandardOpenOption.WRITE)) {
                ByteBuffer buffer=ByteBuffer.wrap(bytes.toByteArray());
                while(buffer.hasRemaining())channel.write(buffer);
                channel.force(true);
            }
            Files.move(temporary,receipt,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);
            syncDirectory(receipt.getParent());
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static FullMetadata fullMetadata(Path full)throws IOException {
        BasicFileAttributes attrs=Files.readAttributes(full,BasicFileAttributes.class);
        return new FullMetadata(attrs.size(),attrs.lastModifiedTime().toMillis(),String.valueOf(attrs.fileKey()));
    }
    private static FullIdentity fullIdentity(Path full)throws IOException {
        FullMetadata metadata=fullMetadata(full);
        return new FullIdentity(metadata.size(),metadata.mtime(),metadata.fileKey(),fileSha256(full));
    }
    private static String fileSha256(Path file)throws IOException {
        try {
            MessageDigest digest=MessageDigest.getInstance("SHA-256");
            try(DigestInputStream in=new DigestInputStream(Files.newInputStream(file),digest)) {
                byte[] buffer=new byte[64*1024];
                while(in.read(buffer)>=0){}
            }
            return hex(digest.digest());
        } catch(java.security.NoSuchAlgorithmException impossible) {
            throw new IOException("SHA-256 unavailable",impossible);
        }
    }
    private static Path receipt(Path full){return full.resolveSibling(full.getFileName()+".pair");}
    private static Path lodRegion(ServerLevel level,ChunkPos pos){
        return CacheManager.lodRoot(level).resolve("r."+Math.floorDiv(pos.x,32)+"."+Math.floorDiv(pos.z,32)+".cslod");
    }
    private static Properties load(Path path){
        if(!Files.isRegularFile(path))return null;
        Properties properties=new Properties();
        try(var in=Files.newInputStream(path)){properties.load(in);return properties;}
        catch(IOException|RuntimeException error){return null;}
    }
    private static void syncDirectory(Path directory){
        try(FileChannel channel=FileChannel.open(directory,StandardOpenOption.READ)){channel.force(true);}
        catch(Throwable unsupported){PipelineMetrics.count("pair-receipt-directory-sync-unavailable");}
    }
    private static byte[] sha256(byte[] bytes)throws Exception{return MessageDigest.getInstance("SHA-256").digest(bytes);}
    private static String hex(byte[] bytes){
        StringBuilder out=new StringBuilder(bytes.length*2);
        for(byte value:bytes)out.append(Character.forDigit((value>>>4)&15,16)).append(Character.forDigit(value&15,16));
        return out.toString();
    }
    private static void writeString(DataOutputStream out,String value)throws IOException{
        byte[] bytes=value.getBytes(java.nio.charset.StandardCharsets.UTF_8);out.writeInt(bytes.length);out.write(bytes);
    }
    private static void writeStrings(DataOutputStream out,java.util.List<String> values)throws IOException{
        out.writeInt(values.size());for(String value:values)writeString(out,value);
    }
    private static void writeInts(DataOutputStream out,int[] values)throws IOException{
        if(values==null){out.writeInt(-1);return;}out.writeInt(values.length);for(int value:values)out.writeInt(value);
    }
    private static void writeBytes(DataOutputStream out,byte[] values)throws IOException{
        if(values==null){out.writeInt(-1);return;}out.writeInt(values.length);out.write(values);
    }
}
