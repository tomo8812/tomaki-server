package maki.chunksmithcache;

import net.minecraft.world.level.ChunkPos;

public interface PregenPoiDirtyBridge {
   long[] maki$clearPregenPoiDirty(ChunkPos var1);

   void maki$restorePregenPoiDirty(long[] var1);
}
