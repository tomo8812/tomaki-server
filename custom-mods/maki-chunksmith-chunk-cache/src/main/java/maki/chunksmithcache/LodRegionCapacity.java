package maki.chunksmithcache;

import com.kishku7.chunksmith.lod.CsLodChunk;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

/**
 * Follow-specific headroom in front of CsLodRegionStore's signed-int offset.
 * The writer Mixin remains the absolute corruption barrier for every source;
 * Follow stops earlier so ordinary/player LOD retains emergency headroom.
 */
final class LodRegionCapacity {
    private static final long PREGEN_HEADROOM=64L<<20;
    private static final long PREGEN_LIMIT=Integer.MAX_VALUE-PREGEN_HEADROOM;
    private static final Map<Path,Long> SIZES=new ConcurrentHashMap<>();
    private LodRegionCapacity(){}

    static boolean allowPregen(ServerLevel level,ChunkPos pos) {
        Path region=region(level,pos.x,pos.z);
        long size=SIZES.computeIfAbsent(region,LodRegionCapacity::readSize);
        if(size==Long.MAX_VALUE) {
            PipelineMetrics.count("lod-region-size-unknown");
            return false;
        }
        if(size>PREGEN_LIMIT) {
            PipelineMetrics.count("lod-region-pregen-headroom");
            return false;
        }
        return true;
    }

    static void written(MinecraftServer server,CsLodChunk record,int bytes) {
        if(server==null||record==null||bytes<=0)return;
        ServerLevel level=null;
        for(ServerLevel candidate:server.getAllLevels()) {
            if(candidate.dimension().location().toString().equals(record.getDimension())) {
                level=candidate;
                break;
            }
        }
        if(level==null)return;
        Path region=region(level,record.getChunkX(),record.getChunkZ());
        SIZES.compute(region,(path,known)->{
            if(known==null||known==Long.MAX_VALUE)return readSize(path);
            try{return Math.addExact(known,(long)bytes);}
            catch(ArithmeticException overflow){return Long.MAX_VALUE;}
        });
    }

    static void clear(){SIZES.clear();}

    private static Path region(ServerLevel level,int x,int z) {
        return CacheManager.lodRoot(level)
            .resolve("r."+Math.floorDiv(x,32)+"."+Math.floorDiv(z,32)+".cslod")
            .toAbsolutePath().normalize();
    }

    private static long readSize(Path path) {
        try{return Files.isRegularFile(path)?Files.size(path):0L;}
        catch(Exception unavailable){return Long.MAX_VALUE;}
    }
}
