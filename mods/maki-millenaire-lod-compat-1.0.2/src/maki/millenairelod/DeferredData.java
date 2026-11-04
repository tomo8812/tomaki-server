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

   public static DeferredData get(ServerLevel var0) {
      return (DeferredData)var0.getDataStorage()
         .computeIfAbsent(new Factory(DeferredData::new, (var0x, var1) -> load(var0x), null), "maki_millenaire_lod_pending");
   }

   public static DeferredData load(CompoundTag var0) {
      DeferredData var1 = new DeferredData();
      var1.pending.addAll(LongOpenHashSet.of(var0.getLongArray("pending")));
      return var1;
   }

   public void remember(long var1, boolean var3) {
      if (this.pending.add(var1)) {
         this.setDirty();
      }

      if (var3) {
         this.queued.add(var1);
      }
   }

   public void release(long var1) {
      this.queued.remove(var1);
   }

   public void consume(long var1) {
      this.queued.remove(var1);
      if (this.pending.remove(var1)) {
         this.setDirty();
      }
   }

   public CompoundTag save(CompoundTag var1, Provider var2) {
      var1.putInt("version", 1);
      var1.putLongArray("pending", this.pending.toLongArray());
      return var1;
   }
}
