package maki.chunksmithcache;

import maki.chunksmithcache.mixin.EntitySectionManagerAccessor;
import maki.chunksmithcache.mixin.ServerLevelEntityAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;

/** The same section selection and root/passenger serialization as vanilla EntityStorage. */
final class EntitySnapshot {
    private EntitySnapshot() {}

    static ListTag capture(ServerLevel level, ChunkPos pos) {
        if (!level.getServer().isSameThread()) throw new IllegalStateException("Entity snapshot requires server thread");
        var manager = ((ServerLevelEntityAccessor) level).maki$getEntityManager();
        EntitySectionStorage<Entity> sections = ((EntitySectionManagerAccessor) manager).maki$getSectionStorage();
        ListTag result = new ListTag();
        // getAllEntities only includes visible entities; freshly generated hidden sections matter too.
        try (var stream = sections.getExistingSectionsInChunk(pos.toLong()).flatMap(EntitySection::getEntities)) {
            stream.filter(Entity::shouldBeSaved).forEach(entity -> {
                CompoundTag tag = new CompoundTag();
                if (entity.save(tag)) result.add(tag);
            });
        }
        return result;
    }
}
