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
    static final int FORMAT_VERSION=3;
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
        int version=tag.getInt("version");
        if(version==FORMAT_VERSION)data.pending.addAll(LongOpenHashSet.of(tag.getLongArray("mainCandidates")));
        // Versions 1/2 used an untyped pending array. Version 2 may itself have
        // imported version-1 lone candidates as main candidates, so it cannot
        // prove that its untyped entries are villages. Discard those entries.
        int ambiguous=0;
        ListTag list=tag.getList(version==FORMAT_VERSION?"loneRetries":"lone",10);
        int invalidLone=0;
        for(int i=0;i<list.size();i++){
            CompoundTag entry=list.getCompound(i);
            if(!entry.contains("pos",4)||!entry.contains("distance",6)
                ||!entry.contains("enqueued",4)||!entry.contains("retryAfter",4)){
                invalidLone++;continue;
            }
            BlockPos pos=BlockPos.of(entry.getLong("pos"));
            long key=new ChunkPos(pos).toLong();
            data.lone.put(key,new Lone(pos,entry.getDouble("distance"),entry.getLong("enqueued"),entry.getLong("retryAfter")));
            data.pending.add(key);
        }
        if(version<FORMAT_VERSION)for(long key:tag.getLongArray("pending"))if(!data.lone.containsKey(key))ambiguous++;
        if(version!=FORMAT_VERSION||invalidLone>0){
            data.setDirty(); // Persist the cleanup even if no player revisits these coordinates.
            org.slf4j.LoggerFactory.getLogger("MakiMillenaireLOD").info(
                "Deferred candidates upgraded from format {} to {}: discarded untyped={} invalidLone={} retainedLone={}",
                version,FORMAT_VERSION,ambiguous,invalidLone,data.lone.size());
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
        tag.putInt("version", FORMAT_VERSION);
        tag.remove("pending");
        tag.remove("lone");
        long[] candidates=new long[pending.size()-lone.size()];
        int count=0;
        for(long key:pending)if(!lone.containsKey(key))candidates[count++]=key;
        tag.putLongArray("mainCandidates", candidates);
        ListTag list=new ListTag();
        for(Lone value:lone.values()){
            CompoundTag entry=new CompoundTag();
            entry.putLong("pos",value.pos().asLong());entry.putDouble("distance",value.distance());
            entry.putLong("enqueued",value.enqueuedTick());entry.putLong("retryAfter",value.retryAfterTick());
            list.add(entry);
        }
        tag.put("loneRetries",list);
        return tag;
    }
}
