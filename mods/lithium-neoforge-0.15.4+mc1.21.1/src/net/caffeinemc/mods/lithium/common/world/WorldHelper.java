package net.caffeinemc.mods.lithium.common.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import net.caffeinemc.mods.lithium.common.client.ClientWorldAccessor;
import net.caffeinemc.mods.lithium.common.entity.EntityClassGroup;
import net.caffeinemc.mods.lithium.common.entity.pushable.EntityPushablePredicate;
import net.caffeinemc.mods.lithium.common.world.chunk.ClassGroupFilterableList;
import net.caffeinemc.mods.lithium.mixin.util.accessors.EntitySectionAccessor;
import net.caffeinemc.mods.lithium.mixin.util.accessors.PersistentEntitySectionManagerAccessor;
import net.caffeinemc.mods.lithium.mixin.util.accessors.ServerLevelAccessor;
import net.caffeinemc.mods.lithium.mixin.util.accessors.TransientEntitySectionManagerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.util.AbortableIterationConsumer.Continuation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class WorldHelper {
   public static final boolean CUSTOM_TYPE_FILTERABLE_LIST_DISABLED = !ClassGroupFilterableList.class.isAssignableFrom(ClassInstanceMultiMap.class);

   public static List<Entity> getEntitiesForCollision(EntityGetter entityView, AABB box, Entity collidingEntity) {
      if (!CUSTOM_TYPE_FILTERABLE_LIST_DISABLED
         && entityView instanceof Level world
         && (collidingEntity == null || !EntityClassGroup.CUSTOM_COLLIDE_LIKE_MINECART_BOAT_WINDCHARGE.contains(collidingEntity))) {
         EntitySectionStorage<Entity> cache = getEntityCacheOrNull(world);
         if (cache != null) {
            world.getProfiler().incrementCounter("getEntities");
            return getEntitiesOfEntityGroup(cache, collidingEntity, EntityClassGroup.NoDragonClassGroup.BOAT_SHULKER_LIKE_COLLISION, box, null);
         }
      }

      return entityView.getEntities(collidingEntity, box);
   }

   public static List<Entity> getOtherEntitiesForCollision(
      EntityGetter entityView, AABB box, @Nullable Entity collidingEntity, Predicate<? super Entity> entityFilter
   ) {
      if (!CUSTOM_TYPE_FILTERABLE_LIST_DISABLED
         && entityView instanceof Level world
         && (collidingEntity == null || !EntityClassGroup.CUSTOM_COLLIDE_LIKE_MINECART_BOAT_WINDCHARGE.contains(collidingEntity))) {
         EntitySectionStorage<Entity> cache = getEntityCacheOrNull(world);
         if (cache != null) {
            world.getProfiler().incrementCounter("getEntities");
            return getEntitiesOfEntityGroup(cache, collidingEntity, EntityClassGroup.NoDragonClassGroup.BOAT_SHULKER_LIKE_COLLISION, box, entityFilter);
         }
      }

      return entityView.getEntities(collidingEntity, box, entityFilter);
   }

   public static EntitySectionStorage<Entity> getEntityCacheOrNull(Level world) {
      if (world instanceof ClientWorldAccessor) {
         return ((TransientEntitySectionManagerAccessor)((ClientWorldAccessor)world).lithium$getEntityManager()).getCache();
      } else {
         return world instanceof ServerLevelAccessor
            ? ((PersistentEntitySectionManagerAccessor)((ServerLevelAccessor)world).getEntityManager()).getCache()
            : null;
      }
   }

   public static List<Entity> getEntitiesOfEntityGroup(
      EntitySectionStorage<Entity> cache,
      Entity collidingEntity,
      EntityClassGroup.NoDragonClassGroup entityClassGroup,
      AABB box,
      Predicate<? super Entity> entityFilter
   ) {
      ArrayList<Entity> entities = new ArrayList<>();
      cache.forEachAccessibleNonEmptySection(
         box,
         section -> {
            ClassInstanceMultiMap<Entity> allEntities = ((EntitySectionAccessor)section).getCollection();
            Collection<Entity> entitiesOfType = ((ClassGroupFilterableList)allEntities).lithium$getAllOfGroupType(entityClassGroup);
            if (!entitiesOfType.isEmpty()) {
               for (Entity entity : entitiesOfType) {
                  if (entity.getBoundingBox().intersects(box)
                     && !entity.isSpectator()
                     && entity != collidingEntity
                     && (entityFilter == null || entityFilter.test(entity))) {
                     entities.add(entity);
                  }
               }
            }

            return Continuation.CONTINUE;
         }
      );
      return entities;
   }

   public static List<Entity> getPushableEntities(
      Level world, EntitySectionStorage<Entity> cache, Entity except, AABB box, EntityPushablePredicate<? super Entity> entityPushablePredicate
   ) {
      ArrayList<Entity> entities = new ArrayList<>();
      cache.forEachAccessibleNonEmptySection(
         box, section -> ((ClimbingMobCachingSection)section).lithium$collectPushableEntities(world, except, box, entityPushablePredicate, entities)
      );
      return entities;
   }

   public static boolean areNeighborsWithinSameChunk(BlockPos pos) {
      int localX = pos.getX() & 15;
      int localZ = pos.getZ() & 15;
      return localX > 0 && localZ > 0 && localX < 15 && localZ < 15;
   }

   public static boolean areNeighborsWithinSameChunkSection(int x, int y, int z) {
      int localX = x & 15;
      int localY = y & 15;
      int localZ = z & 15;
      return localX > 0 && localY > 0 && localZ > 0 && localX < 15 && localY < 15 && localZ < 15;
   }

   public static boolean arePosWithinSameChunk(BlockPos pos1, BlockPos pos2) {
      return pos1.getX() >> 4 == pos2.getX() >> 4 && pos1.getZ() >> 4 == pos2.getZ() >> 4;
   }
}
