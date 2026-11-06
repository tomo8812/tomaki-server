package maki.millenairelod;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public final class DeferredData extends SavedData {
    public final LongOpenHashSet pending = new LongOpenHashSet();
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
        return data;
    }

    public void remember(long key, boolean claimed) {
        if (pending.add(key)) setDirty();
        if (claimed) queued.add(key);
    }

    public void release(long key) { queued.remove(key); }

    public void consume(long key) {
        queued.remove(key);
        if (pending.remove(key)) setDirty();
    }

    @Override public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookup) {
        tag.putInt("version", 1);
        tag.putLongArray("pending", pending.toLongArray());
        return tag;
    }
}
