package maki.chunksmithcache.mixin;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkMap.class)
public interface ChunkMapLifecycleAccessor {
   @Accessor("updatingChunkMap")
   Long2ObjectLinkedOpenHashMap<ChunkHolder> maki$getUpdatingChunkMap();

   @Accessor("visibleChunkMap")
   Long2ObjectLinkedOpenHashMap<ChunkHolder> maki$getVisibleChunkMap();

   @Accessor("pendingUnloads")
   Long2ObjectLinkedOpenHashMap<ChunkHolder> maki$getPendingUnloads();
}
