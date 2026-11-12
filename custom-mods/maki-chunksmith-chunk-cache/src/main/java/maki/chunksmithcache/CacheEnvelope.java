package maki.chunksmithcache;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

final class CacheEnvelope {
    static final int SCHEMA = 3;
    private CacheEnvelope() {}

    static boolean completeEntities(CompoundTag envelope) {
        if (envelope.getInt("schema") != SCHEMA || !envelope.getBoolean("entitiesComplete")
            || !(envelope.getBoolean("fullCommitted") || envelope.getBoolean("lodCommitted"))) return false;
        Tag entities=envelope.getCompound("chunk").get("entities");
        return entities instanceof ListTag list && (list.isEmpty() || list.getElementType()==Tag.TAG_COMPOUND);
    }
}
