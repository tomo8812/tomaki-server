package maki.millenairelod;

import java.nio.file.Files;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtAccounter;

public final class DeferredDataSelfTest {
    public static void main(String[] args)throws Exception{
        upgradeAmbiguousQueues();
        millionDeferredVillages();
        DeferredData data=new DeferredData();
        long main=new ChunkPos(2,3).toLong();data.remember(main,true);
        BlockPos pos=new BlockPos(-123,72,48);long key=new ChunkPos(pos).toLong();
        data.rememberLone(pos,12345.5,100,300);
        var file=Files.createTempFile("millenaire-pending", ".dat");
        try{
            NbtIo.writeCompressed(data.save(new CompoundTag(),null),file);
            DeferredData loaded=DeferredData.load(NbtIo.readCompressed(file,NbtAccounter.unlimitedHeap()));
            require(loaded.pending.contains(main)&&loaded.pending.contains(key),"main and lone survive disk reload");
            require(loaded.queued.isEmpty(),"live queue claims reset so candidates may be replayed");
            DeferredData.Lone lone=loaded.lone.get(key);
            require(lone!=null&&lone.pos().equals(pos)&&lone.distance()==12345.5&&lone.enqueuedTick()==100&&lone.retryAfterTick()==300,
                "lone type, position, distance, TTL origin and backoff survive reload");
            Object nativeRecord=NativeLoneCandidate.restore(lone.pos(),lone.distance(),lone.enqueuedTick(),lone.retryAfterTick());
            require(nativeRecord.equals(NativeLoneCandidate.restore(pos,12345.5,100,300)),"restore uses the exact native lone record");
            loaded.consume(key);
            DeferredData savedAgain=DeferredData.load(loaded.save(new CompoundTag(),null));
            require(!savedAgain.pending.contains(key)&&!savedAgain.lone.containsKey(key)&&savedAgain.pending.contains(main),
                "resolved or expired lone is not resurrected after another restart");
            System.out.println("DeferredDataSelfTest PASS (disk restart, queue claims, native lone state, terminal cleanup)");
        }finally{Files.deleteIfExists(file);}
    }
    private static void upgradeAmbiguousQueues(){
        CompoundTag old=new CompoundTag();old.putInt("version",1);old.putLongArray("pending",new long[]{1L,2L});
        DeferredData migrated=DeferredData.load(old);
        require(migrated.pending.isEmpty()&&migrated.isDirty(),"untyped old village/lone candidates are removed and cleanup is scheduled for saving");
        CompoundTag rewritten=migrated.save(new CompoundTag(),null);
        require(rewritten.getInt("version")==3&&!rewritten.contains("pending"),"cleanup removes the old format permanently");
        require(DeferredData.load(rewritten).pending.isEmpty(),"old candidates do not reappear after restart");
        BlockPos pos=new BlockPos(80,70,96);long key=new ChunkPos(pos).toLong();
        DeferredData typed=new DeferredData();typed.rememberLone(pos,900,100,200);
        CompoundTag version2=typed.save(new CompoundTag(),null);
        version2.putInt("version",2);version2.remove("mainCandidates");
        version2.put("lone",version2.getList("loneRetries",10));version2.remove("loneRetries");version2.putLongArray("pending",new long[]{key,3L});
        DeferredData preserved=DeferredData.load(version2);
        require(preserved.pending.size()==1&&preserved.lone.containsKey(key),"version2 keeps only explicitly typed lone retries, dropping possibly imported untyped entries");
        CompoundTag invalid=version2.copy();invalid.getList("lone",10).getCompound(0).remove("retryAfter");
        require(DeferredData.load(invalid).pending.isEmpty(),"incomplete lone records are discarded during upgrade");
    }
    private static void millionDeferredVillages(){
        DeferredData data=new DeferredData();
        for(long key=0;key<1_000_000;key++)data.remember(key,false);
        CompoundTag saved=data.save(new CompoundTag(),null);
        require(saved.getLongArray("mainCandidates").length==1_000_000,"persistent world backlog is not limited by a native active queue");
        DeferredData restored=DeferredData.load(saved);
        require(restored.pending.size()==1_000_000&&restored.queued.isEmpty(),"a million deferred candidates survive reload without loss or live queue claims");
    }
    private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
}
