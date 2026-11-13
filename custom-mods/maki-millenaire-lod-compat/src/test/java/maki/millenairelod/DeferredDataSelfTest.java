package maki.millenairelod;

import java.nio.file.Files;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtAccounter;

public final class DeferredDataSelfTest {
    public static void main(String[] args)throws Exception{
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
    private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
}
