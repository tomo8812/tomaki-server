package maki.chunksmithcache;

import java.util.stream.Stream;
import maki.chunksmithcache.mixin.EntitySectionManagerAccessor;
import maki.chunksmithcache.mixin.ServerLevelEntityAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;

final class EntitySnapshot {
   private EntitySnapshot() {
   }

   static ListTag capture(ServerLevel level, ChunkPos pos) {
      if (!level.getServer().isSameThread()) {
         throw new IllegalStateException("Entity snapshot requires server thread");
      }

      PersistentEntitySectionManager<Entity> manager = ((ServerLevelEntityAccessor)level).maki$getEntityManager();
      EntitySectionStorage<Entity> sections = ((EntitySectionManagerAccessor)manager).maki$getSectionStorage();
      ListTag result = new ListTag();

      try (Stream<Entity> stream = sections.getExistingSectionsInChunk(pos.toLong()).flatMap(EntitySection::getEntities)) {
         stream.filter(Entity::shouldBeSaved).forEach(entity -> {
            CompoundTag tag = new CompoundTag();
            if (entity.save(tag)) {
               result.add(tag);
            }
         });
      }

      return result;
   }
}
