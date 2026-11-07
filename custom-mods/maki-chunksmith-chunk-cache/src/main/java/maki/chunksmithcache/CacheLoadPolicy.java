package maki.chunksmithcache;

import net.minecraft.nbt.CompoundTag;

final class CacheLoadPolicy {
    private CacheLoadPolicy() {}

    static boolean keepNormal(CompoundTag normal) {
        // Unknown normal data also wins: do not overwrite it with an optimistic cache fallback.
        return normal!=null && NormalChunkStorage.fromTag(normal)!=NormalChunkStorage.State.PROTO;
    }
}
