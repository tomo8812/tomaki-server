package maki.shiptracking;

import java.util.*;
import java.nio.file.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.*;
import maki.shiptracking.mixin.*;

/** A bounded re-pair of already-tracked players after assembly, with no teleport or respawn. */
@Mod("maki_assembly_tracking")
public final class AssemblyTracking {
    private static final Logger LOG=LoggerFactory.getLogger("MakiShipTrackingRepair");
    private static final List<Job> JOBS=new ArrayList<>();
    private static final Map<Pair,Long> LAST=new HashMap<>();
    private static MinecraftServer owner;
    private static long sequence,stamp=Long.MIN_VALUE,lastConfigRead;
    private static boolean enabled=true;
    private record Pair(UUID target,UUID viewer){}
    private record Job(ServerLevel level,Set<UUID> affected,long due,int delay){}
    public AssemblyTracking(){
        LOG.info("Maki Assembly Tracking initialized; bounded post-assembly player tracking repair is enabled by default and follows config/maki-assembly-tracking.properties.");
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post e)->tick(e.getServer()));
        NeoForge.EVENT_BUS.addListener((ServerStoppingEvent e)->{
            LOG.info("Maki Assembly Tracking stopping; clearing {} pending repair job(s).", JOBS.size());
            JOBS.clear();LAST.clear();owner=null;
        });
    }
    public static void assembled(ServerLevel level,Set<BlockPos> blocks){
        if(!enabled||blocks.isEmpty())return;
        int minX=Integer.MAX_VALUE,minY=minX,minZ=minX,maxX=Integer.MIN_VALUE,maxY=maxX,maxZ=maxX;
        for(BlockPos p:blocks){minX=Math.min(minX,p.getX());minY=Math.min(minY,p.getY());minZ=Math.min(minZ,p.getZ());maxX=Math.max(maxX,p.getX());maxY=Math.max(maxY,p.getY());maxZ=Math.max(maxZ,p.getZ());}
        AABB area=new AABB(minX,minY,minZ,maxX+1,maxY+1,maxZ+1).inflate(4);
        Set<UUID> affected=new HashSet<>();
        for(ServerPlayer p:level.players())if(area.intersects(p.getBoundingBox()))affected.add(p.getUUID());
        if(affected.isEmpty())return;
        while(JOBS.size()>60)JOBS.removeFirst();
        for(int delay:new int[]{20,100})JOBS.add(new Job(level,Set.copyOf(affected),sequence+delay,delay));
        LOG.info("Assembly near {} player(s); scheduling tracked-player synchronization at 20 and 100 ticks",affected.size());
    }
    public static boolean repairPair(boolean samePlayer,boolean sameLevel,boolean alreadyTracked,boolean alive){return !samePlayer&&sameLevel&&alreadyTracked&&alive;}
    private static void reload(){
        long now=System.currentTimeMillis();if(now-lastConfigRead<5000)return;lastConfigRead=now;
        Path path=Path.of("config/maki-assembly-tracking.properties");
        try{
            if(!Files.exists(path))return;
            long next=Files.getLastModifiedTime(path).toMillis();if(next==stamp)return;
            Properties p=new Properties();try(var r=Files.newBufferedReader(path)){p.load(r);}
            String value=p.getProperty("enabled","true");if(!value.equals("true")&&!value.equals("false"))throw new IllegalArgumentException("enabled");
            enabled=Boolean.parseBoolean(value);stamp=next;LOG.info("Assembly tracking resync enabled={}",enabled);
        }catch(Exception e){enabled=false;LOG.warn("Invalid assembly tracking settings; repair disabled",e);}
    }
    private static void tick(MinecraftServer server){
        if(owner!=server){owner=server;JOBS.clear();LAST.clear();sequence=0;}
        sequence++;reload();
        if(!enabled){JOBS.clear();return;}
        var iterator=JOBS.iterator();
        while(iterator.hasNext()){
            Job job=iterator.next();if(sequence<job.due())continue;iterator.remove();int refreshed=0;
            var map=((ChunkMapAccess)job.level().getChunkSource().chunkMap).maki$entities();
            for(ServerPlayer target:job.level().players()){
                Object entry=map.get(target.getId());if(!(entry instanceof TrackedEntityAccess tracked))continue;
                for(ServerPlayer viewer:job.level().players()){
                    if(!job.affected().contains(target.getUUID())&&!job.affected().contains(viewer.getUUID()))continue;
                    if(!repairPair(target==viewer,target.level()==viewer.level(),tracked.maki$seenBy().contains(viewer.connection),target.isAlive()&&viewer.isAlive()))continue;
                    Pair pair=new Pair(target.getUUID(),viewer.getUUID());
                    if(sequence-LAST.getOrDefault(pair,Long.MIN_VALUE/2)<20)continue;
                    // Keep the original seenBy membership; use vanilla's full remove/add pairing packets.
                    tracked.maki$serverEntity().removePairing(viewer);
                    tracked.maki$serverEntity().addPairing(viewer);
                    LAST.put(pair,sequence);refreshed++;
                }
            }
            LOG.info("Post-assembly synchronization: delay={} ticks, refreshed={} existing player tracking pair(s)",job.delay(),refreshed);
        }
        if(sequence%200==0)LAST.entrySet().removeIf(e->sequence-e.getValue()>200);
    }
}
