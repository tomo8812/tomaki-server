package net.caffeinemc.mods.lithium.common.entity;

import com.google.common.collect.AbstractIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.caffeinemc.mods.lithium.common.entity.movement.ChunkAwareBlockCollisionSweeper;
import net.caffeinemc.mods.lithium.common.util.Pos;
import net.caffeinemc.mods.lithium.common.world.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LithiumEntityCollisions {
   public static final double EPSILON = 1.0E-7;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !LithiumEntityCollisions.class.desiredAssertionStatus();

   public static List<VoxelShape> getBlockCollisions(Level world, Entity entity, AABB box) {
      return new ChunkAwareBlockCollisionSweeper(world, entity, box).collectAll();
   }

   public static boolean doesBoxCollideWithBlocks(Level world, @Nullable Entity entity, AABB box) {
      ChunkAwareBlockCollisionSweeper sweeper = new ChunkAwareBlockCollisionSweeper(world, entity, box);
      VoxelShape shape = sweeper.computeNext();
      return shape != null && !shape.isEmpty();
   }

   public static boolean doesBoxCollideWithHardEntities(EntityGetter view, @Nullable Entity entity, AABB box) {
      return isBoxEmpty(box) ? false : getEntityWorldBorderCollisionIterable(view, entity, box.inflate(1.0E-7), false).iterator().hasNext();
   }

   public static void appendEntityCollisions(List<VoxelShape> entityCollisions, Level world, Entity entity, AABB box) {
      if (!isBoxEmpty(box)) {
         AABB expandedBox = box.inflate(1.0E-7);

         for (Entity otherEntity : WorldHelper.getEntitiesForCollision(world, expandedBox, entity)) {
            if (entity == null ? otherEntity.canBeCollidedWith() : entity.canCollideWith(otherEntity)) {
               entityCollisions.add(Shapes.create(otherEntity.getBoundingBox()));
            }
         }
      }
   }

   public static void appendWorldBorderCollision(ArrayList<VoxelShape> worldBorderCollisions, Entity entity, AABB box) {
      WorldBorder worldBorder = entity.level().getWorldBorder();
      if (!isWithinWorldBorder(worldBorder, box) && isWithinWorldBorder(worldBorder, entity.getBoundingBox())) {
         worldBorderCollisions.add(worldBorder.getCollisionShape());
      }
   }

   public static Iterable<VoxelShape> getEntityWorldBorderCollisionIterable(
      final EntityGetter view, @Nullable final Entity entity, final AABB box, final boolean includeWorldBorder
   ) {
      assert !includeWorldBorder || entity != null;
      return new Iterable<VoxelShape>() {
         private List<Entity> entityList;
         private int nextFilterIndex;

         @NotNull
         @Override
         public Iterator<VoxelShape> iterator() {
            return new AbstractIterator<VoxelShape>() {
               int index = 0;
               boolean consumedWorldBorder = false;

               protected VoxelShape computeNext() {
                  if (entityList == null) {
                     entityList = WorldHelper.getEntitiesForCollision(view, box, entity);
                     nextFilterIndex = 0;
                  }

                  List<Entity> list = entityList;

                  while (this.index < list.size()) {
                     Entity otherEntity = list.get(this.index);
                     if (this.index >= nextFilterIndex) {
                        if (entity == null) {
                           if (!otherEntity.canBeCollidedWith()) {
                              otherEntity = null;
                           }
                        } else if (!entity.canCollideWith(otherEntity)) {
                           otherEntity = null;
                        }

                        nextFilterIndex++;
                     }

                     this.index++;
                     if (otherEntity != null) {
                        return Shapes.create(otherEntity.getBoundingBox());
                     }
                  }

                  if (includeWorldBorder && !this.consumedWorldBorder) {
                     this.consumedWorldBorder = true;
                     WorldBorder worldBorder = entity.level().getWorldBorder();
                     if (!LithiumEntityCollisions.isWithinWorldBorder(worldBorder, box)
                        && LithiumEntityCollisions.isWithinWorldBorder(worldBorder, entity.getBoundingBox())) {
                        return worldBorder.getCollisionShape();
                     }
                  }

                  return (VoxelShape)this.endOfData();
               }

               // $VF: synthetic method
               // $VF: bridge method
               protected Object computeNext() {
                  return this.computeNext();
               }
            };
         }
      };
   }

   public static boolean isWithinWorldBorder(WorldBorder border, AABB box) {
      double wboxMinX = Math.floor(border.getMinX());
      double wboxMinZ = Math.floor(border.getMinZ());
      double wboxMaxX = Math.ceil(border.getMaxX());
      double wboxMaxZ = Math.ceil(border.getMaxZ());
      return box.minX >= wboxMinX
         && box.minX <= wboxMaxX
         && box.minZ >= wboxMinZ
         && box.minZ <= wboxMaxZ
         && box.maxX >= wboxMinX
         && box.maxX <= wboxMaxX
         && box.maxZ >= wboxMinZ
         && box.maxZ <= wboxMaxZ;
   }

   private static boolean isBoxEmpty(AABB box) {
      return box.getSize() <= 1.0E-7;
   }

   public static boolean doesBoxCollideWithWorldBorder(CollisionGetter collisionView, Entity entity, AABB box) {
      if (isWithinWorldBorder(collisionView.getWorldBorder(), box)) {
         return false;
      }

      VoxelShape worldBorderShape = getWorldBorderCollision(collisionView, entity, box);
      return worldBorderShape != null && Shapes.joinIsNotEmpty(worldBorderShape, Shapes.create(box), BooleanOp.AND);
   }

   public static VoxelShape getWorldBorderCollision(CollisionGetter collisionView, @Nullable Entity entity, AABB box) {
      WorldBorder worldBorder = collisionView.getWorldBorder();
      return worldBorder.isInsideCloseToBorder(entity, box) ? worldBorder.getCollisionShape() : null;
   }

   @Nullable
   public static VoxelShape getSupportingCollisionForEntity(Level world, @Nullable Entity entity, AABB entityBoundingBox) {
      if (entity instanceof LithiumEntityCollisions.SupportingBlockCollisionShapeProvider supportingBlockCollisionShapeProvider) {
         VoxelShape voxelShape = supportingBlockCollisionShapeProvider.lithium$getCollisionShapeBelow();
         if (voxelShape != null) {
            return voxelShape;
         }
      }

      return getCollisionShapeBelowEntityFallback(world, entity, entityBoundingBox);
   }

   @Nullable
   private static VoxelShape getCollisionShapeBelowEntityFallback(Level world, Entity entity, AABB entityBoundingBox) {
      int x = Mth.floor(entityBoundingBox.minX + (entityBoundingBox.maxX - entityBoundingBox.minX) / 2.0);
      int y = Mth.floor(entityBoundingBox.minY);
      int z = Mth.floor(entityBoundingBox.minZ + (entityBoundingBox.maxZ - entityBoundingBox.minZ) / 2.0);
      if (world.isOutsideBuildHeight(y)) {
         return null;
      } else {
         ChunkAccess chunk = world.getChunk(Pos.ChunkCoord.fromBlockCoord(x), Pos.ChunkCoord.fromBlockCoord(z), ChunkStatus.FULL, false);
         if (chunk != null) {
            LevelChunkSection cachedChunkSection = chunk.getSections()[Pos.SectionYIndex.fromBlockCoord(world, y)];
            return cachedChunkSection.getBlockState(x & 15, y & 15, z & 15)
               .getCollisionShape(world, new BlockPos(x, y, z), entity == null ? CollisionContext.empty() : CollisionContext.of(entity))
               .move(x, y, z);
         } else {
            return null;
         }
      }
   }

   public static boolean addLastBlockCollisionIfRequired(
      boolean addLastBlockCollision, ChunkAwareBlockCollisionSweeper blockCollisionSweeper, List<VoxelShape> list
   ) {
      if (addLastBlockCollision) {
         VoxelShape lastCollision = blockCollisionSweeper.getLastCollision();
         if (lastCollision != null) {
            list.add(lastCollision);
         }
      }

      return false;
   }

   public static AABB getSmallerBoxForSingleAxisMovement(Vec3 movement, AABB entityBoundingBox, double velY, double velX, double velZ) {
      double minX = entityBoundingBox.minX;
      double minY = entityBoundingBox.minY;
      double minZ = entityBoundingBox.minZ;
      double maxX = entityBoundingBox.maxX;
      double maxY = entityBoundingBox.maxY;
      double maxZ = entityBoundingBox.maxZ;
      if (velY > 0.0) {
         minY = maxY;
         maxY += velY;
      } else if (velY < 0.0) {
         maxY = minY;
         minY += velY;
      } else if (velX > 0.0) {
         minX = maxX;
         maxX += velX;
      } else if (velX < 0.0) {
         maxX = minX;
         minX += velX;
      } else if (velZ > 0.0) {
         minZ = maxZ;
         maxZ += velZ;
      } else {
         if (!(velZ < 0.0)) {
            return entityBoundingBox.expandTowards(movement);
         }

         maxZ = minZ;
         minZ += velZ;
      }

      return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
   }

   public static boolean addEntityCollisionsIfRequired(
      boolean getEntityCollisions, @Nullable Entity entity, Level world, List<VoxelShape> entityCollisions, AABB movementSpace
   ) {
      if (getEntityCollisions) {
         appendEntityCollisions(entityCollisions, world, entity, movementSpace);
      }

      return false;
   }

   public static boolean addWorldBorderCollisionIfRequired(
      boolean getWorldBorderCollision, @Nullable Entity entity, ArrayList<VoxelShape> worldBorderCollisions, AABB movementSpace
   ) {
      if (getWorldBorderCollision && entity != null) {
         appendWorldBorderCollision(worldBorderCollisions, entity, movementSpace);
      }

      return false;
   }

   public interface SupportingBlockCollisionShapeProvider {
      @Nullable
      VoxelShape lithium$getCollisionShapeBelow();
   }
}
