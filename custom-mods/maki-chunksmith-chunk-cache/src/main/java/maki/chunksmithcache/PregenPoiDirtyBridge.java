package maki.chunksmithcache;

import net.minecraft.world.level.ChunkPos;

/** Mixin bridge used to clear and later restore only the POI sections vetoed by unified pregen. */
public interface PregenPoiDirtyBridge {
    long[] maki$clearPregenPoiDirty(ChunkPos pos);
    void maki$restorePregenPoiDirty(long[] sections);
}
