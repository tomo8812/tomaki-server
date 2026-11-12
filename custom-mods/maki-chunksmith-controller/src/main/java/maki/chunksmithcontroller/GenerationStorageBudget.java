package maki.chunksmithcontroller;

import com.kishku7.chunksmith.lod.LodSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import maki.chunksmithcache.CacheManager;
import net.minecraft.server.MinecraftServer;

/** Read-only storage observations for generation admission. Chunksmith owns every LOD file. */
final class GenerationStorageBudget {
    private record Sample(Path root,long bytes,long writtenBefore,long at,boolean failed){}
    private static volatile Sample sample;
    private static CompletableFuture<Void> scan;
    private static Path currentRoot;
    private GenerationStorageBudget(){}
    static synchronized void observe(MinecraftServer server){
        Path root=LodSupport.storeRootBase(server);
        if(!root.equals(currentRoot)){currentRoot=root;sample=null;}
        if(scan!=null&&!scan.isDone())return;
        Sample current=sample;
        if(current!=null&&System.nanoTime()-current.at()<15_000_000_000L)return;
        long written=LodCounters.read().bytes();
        scan=CompletableFuture.runAsync(()->{
            Sample next;
            try{next=new Sample(root,inventory(root),written,System.nanoTime(),false);}
            catch(IOException|RuntimeException error){next=new Sample(root,0,written,System.nanoTime(),true);}
            synchronized(GenerationStorageBudget.class){if(root.equals(currentRoot))sample=next;}
        });
    }
    static String pressure(){
        Sample current=sample;
        if(current==null)return "generation-storage-warming-up";
        if(current.failed()||System.nanoTime()-current.at()>60_000_000_000L)return "generation-storage-unavailable";
        // Writes during/after the inventory may already be counted: overestimate
        // until the next inventory rather than admit based on a stale lower bound.
        long lod=current.bytes()+Math.max(0,LodCounters.read().bytes()-current.writtenBefore());
        var full=CacheManager.resourceStatus();
        return atLimit(full.activeCacheBytes(),full.pendingBytes(),lod,CpuGovernor.settings().maxStorageBytes)
            ?"generation-storage-capacity":null;
    }
    static boolean atLimit(long full,long pending,long lod,long limit){
        return full>=limit||lod>=limit-full||pending>=limit-full-lod;
    }
    static long inventory(Path root)throws IOException{
        if(!Files.exists(root))return 0;
        try(var walk=Files.walk(root)){
            long total=0;var files=walk.filter(Files::isRegularFile).iterator();
            while(files.hasNext())total=Math.addExact(total,Files.size(files.next()));
            return total;
        }
    }
    static synchronized void clear(){currentRoot=null;sample=null;}
}
