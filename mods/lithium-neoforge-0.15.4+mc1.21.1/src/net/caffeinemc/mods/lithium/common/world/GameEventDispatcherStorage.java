package net.caffeinemc.mods.lithium.common.world;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.world.level.gameevent.GameEventListenerRegistry;

public record GameEventDispatcherStorage(Long2ReferenceOpenHashMap<Int2ObjectMap<GameEventListenerRegistry>> storage, LongOpenHashSet loadedChunks) {
   public GameEventDispatcherStorage() {
      this(new Long2ReferenceOpenHashMap(), new LongOpenHashSet());
   }

   public void addChunk(long pos, Int2ObjectMap<GameEventListenerRegistry> dispatchers) {
      if (dispatchers != null) {
         this.storage.put(pos, dispatchers);
      }

      this.loadedChunks.add(pos);
   }

   public void removeChunk(long pos) {
      this.storage.remove(pos);
      this.loadedChunks.remove(pos);
   }

   public void replace(long pos, Int2ObjectMap<GameEventListenerRegistry> dispatchers) {
      if (this.loadedChunks.contains(pos)) {
         if (dispatchers == null) {
            this.storage.remove(pos);
         } else {
            this.storage.put(pos, dispatchers);
         }
      }
   }

   public Int2ObjectMap<GameEventListenerRegistry> get(long pos) {
      return (Int2ObjectMap<GameEventListenerRegistry>)this.storage.get(pos);
   }
}
