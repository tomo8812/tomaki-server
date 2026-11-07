package maki.chunksmithcache;

import net.minecraft.nbt.CompoundTag;

final class CacheLoadPolicy {
   private CacheLoadPolicy() {
   }

   static boolean keepNormal(CompoundTag normal) {
      return normal != null && NormalChunkStorage.fromTag(normal) != NormalChunkStorage.State.PROTO;
   }
}
