package maki.millenairelod;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public final class DeferredData extends SavedData {
    public final LongOpenHashSet pending = new LongOpenHashSet();
    public record Lone(BlockPos pos,double distance,long enqueuedTick,long retryAfterTick) {}
    public final Long2ObjectOpenHashMap<Lone> lone = new Long2ObjectOpenHashMap<>();
    // Claims refer to the live Millenaire queues and must not survive a restart.
    public final LongOpenHashSet queued = new LongOpenHashSet();

    public static DeferredData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(DeferredData::new, (tag, lookup) -> load(tag), null),
            "maki_millenaire_lod_pending");
    }

    public static DeferredData load(CompoundTag tag) {
        DeferredData data = new DeferredData();
        data.pending.addAll(LongOpenHashSet.of(tag.getLongArray("pending")));
        ListTag list=tag.getList("lone",10);
        for(int i=0;i<list.size();i++){
            CompoundTag entry=list.getCompound(i);
            BlockPos pos=BlockPos.of(entry.getLong("pos"));
            long key=new ChunkPos(pos).toLong();
            data.lone.put(key,new Lone(pos,entry.getDouble("distance"),entry.getLong("enqueued"),entry.getLong("retryAfter")));
            data.pending.add(key);
        }
        return data;
    }

    public void remember(long key, boolean claimed) {
        if (pending.add(key)) setDirty();
        if (claimed) queued.add(key);
    }

    public void rememberLone(BlockPos pos,double distance,long enqueuedTick,long retryAfterTick){
        long key=new ChunkPos(pos).toLong();
        Lone value=new Lone(pos.immutable(),distance,enqueuedTick,retryAfterTick);
        Lone previous=lone.put(key,value);
        if(pending.add(key)||!value.equals(previous))setDirty();
        queued.add(key);
    }

    public void release(long key) { queued.remove(key); }

    public void consume(long key) {
        queued.remove(key);
        boolean removed=pending.remove(key);
        if(lone.remove(key)!=null||removed)setDirty();
    }

    @Override public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookup) {
        tag.putInt("version", 2);
        tag.putLongArray("pending", pending.toLongArray());
        ListTag list=new ListTag();
        for(Lone value:lone.values()){
            CompoundTag entry=new CompoundTag();
            entry.putLong("pos",value.pos().asLong());entry.putDouble("distance",value.distance());
            entry.putLong("enqueued",value.enqueuedTick());entry.putLong("retryAfter",value.retryAfterTick());
            list.add(entry);
        }
        tag.put("lone",list);
        return tag;
    }
}
