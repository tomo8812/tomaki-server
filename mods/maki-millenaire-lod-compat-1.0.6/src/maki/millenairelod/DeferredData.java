package maki.millenairelod;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;

public final class DeferredData extends SavedData {
   public final LongOpenHashSet pending = new LongOpenHashSet();
   public final LongOpenHashSet queued = new LongOpenHashSet();

   public static DeferredData get(ServerLevel level) {
      return (DeferredData)level.getDataStorage()
         .computeIfAbsent(new Factory(DeferredData::new, (tag, lookup) -> load(tag), null), "maki_millenaire_lod_pending");
   }

   public static DeferredData load(CompoundTag tag) {
      DeferredData data = new DeferredData();
      data.pending.addAll(LongOpenHashSet.of(tag.getLongArray("pending")));
      return data;
   }

   public void remember(long key, boolean claimed) {
      if (this.pending.add(key)) {
         this.setDirty();
      }

      if (claimed) {
         this.queued.add(key);
      }
   }

   public void release(long key) {
      this.queued.remove(key);
   }

   public void consume(long key) {
      this.queued.remove(key);
      if (this.pending.remove(key)) {
         this.setDirty();
      }
   }

   public CompoundTag save(CompoundTag tag, Provider lookup) {
      tag.putInt("version", 1);
      tag.putLongArray("pending", this.pending.toLongArray());
      return tag;
   }
}
