package maki.millenairelod;

import java.util.Map;
import java.util.WeakHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import maki.millenairelod.mixin.SiteHunterWorkAccessor;
import maki.millenairelod.mixin.VillageQueueAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.millenaire.config.MillenaireServerConfig;
import org.millenaire.world.SiteHunter;
import org.millenaire.world.VillageSpawnQueue;

/** Reintroduces player-loaded village candidates into Millenaire's own queues. */
public final class ReplayController {
    private static final Map<ServerLevel,Long2LongOpenHashMap> RETRIES=new WeakHashMap<>();
    private ReplayController() {}

    public static void replay(ServerLevel level,VillageSpawnQueue queue){
        long tick=level.getGameTime();
        if(tick%20!=0)return;
        var config=MillenaireServerConfig.SERVER;
        if(!config.generateVillages.get()&&!config.generateLoneBuildings.get())return;
        VillageQueueAccessor access=(VillageQueueAccessor)queue;
        int budget=ReplayBudget.limit(access.maki$candidates().size(),access.maki$loneBuildings().size(),
            ((SiteHunterWorkAccessor)(Object)queue.hunter()).maki$work().size());
        if(budget==0)return;
        DeferredData data=DeferredData.get(level);
        int radius=level.getServer().getPlayerList().getViewDistance()+1;
        for(ServerPlayer player:level.players()){
            ChunkPos at=player.chunkPosition();
            for(int dx=-radius;dx<=radius;dx++)for(int dz=-radius;dz<=radius;dz++){
                ChunkPos pos=new ChunkPos(at.x+dx,at.z+dz);
                long key=pos.toLong();
                if(!data.pending.contains(key)||data.queued.contains(key)||!retryReady(level,key,tick))continue;
                if(!VillagePlacementReadiness.candidateReady(level,pos))continue;
                DeferredData.Lone lone=data.lone.get(key);
                if(lone!=null&&access.maki$loneBuildings().size()>=64)continue;
                data.remember(key,true);
                if(lone==null)queue.enqueue(pos.getMiddleBlockPosition(64));
                else access.maki$loneBuildings().addLast(NativeLoneCandidate.restore(
                    lone.pos(),lone.distance(),lone.enqueuedTick(),lone.retryAfterTick()));
                if(--budget==0)return;
            }
        }
    }

    public static boolean beginCandidate(ServerLevel level,BlockPos pos){
        long key=new ChunkPos(pos).toLong();
        DeferredData data=DeferredData.get(level);
        data.release(key);
        if(!retryReady(level,key,level.getGameTime())
            ||!VillagePlacementReadiness.candidateReady(level,new ChunkPos(pos))){
            data.remember(key,false);return false;
        }
        data.consume(key);
        if(SiteHunter.ENABLED)resolved(level,pos);
        return true;
    }

    public static void deferred(ServerLevel level,BlockPos pos,boolean mainCandidate,boolean nativeRequeued){
        long key=new ChunkPos(pos).toLong();
        DeferredData data=DeferredData.get(level);
        if(!nativeRequeued)data.release(key);
        data.remember(key,nativeRequeued);
        // Lone buildings retain Millenaire's native retry and 72,000-tick TTL.
        if(mainCandidate)RETRIES.computeIfAbsent(level,ignored->new Long2LongOpenHashMap())
            .put(key,level.getGameTime()+20L);
    }

    public static void resolved(ServerLevel level,BlockPos pos){
        Long2LongOpenHashMap retries=RETRIES.get(level);
        if(retries!=null)retries.remove(new ChunkPos(pos).toLong());
    }

    private static boolean retryReady(ServerLevel level,long key,long tick){
        Long2LongOpenHashMap retries=RETRIES.get(level);
        return retries==null||tick>=retries.get(key);
    }
}
