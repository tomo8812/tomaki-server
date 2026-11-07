package maki.chunksmithcache;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

final class CacheEnvelope {
   static final int SCHEMA = 3;

   private CacheEnvelope() {
   }

   static boolean completeEntities(CompoundTag envelope) {
      return envelope.getInt("schema") == 3 && envelope.getBoolean("entitiesComplete") && envelope.getBoolean("lodCommitted")
         ? envelope.getCompound("chunk").get("entities") instanceof ListTag list && (list.isEmpty() || list.getElementType() == 10)
         : false;
   }
}
