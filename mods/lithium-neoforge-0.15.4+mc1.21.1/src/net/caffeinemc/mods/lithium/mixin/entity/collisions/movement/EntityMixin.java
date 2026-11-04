package net.caffeinemc.mods.lithium.mixin.entity.collisions.movement;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import java.util.ArrayList;
import java.util.List;
import net.caffeinemc.mods.lithium.common.entity.LithiumEntityCollisions;
import net.caffeinemc.mods.lithium.common.entity.movement.ChunkAwareBlockCollisionSweeper;
import net.caffeinemc.mods.lithium.common.util.collections.LazyList;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;

@Mixin(Entity.class)
public abstract class EntityMixin {
   @Shadow
   public abstract Level level();

   @Shadow
   public abstract AABB getBoundingBox();

   @Redirect(
      method = "collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;getEntityCollisions(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
      )
   )
   private List<VoxelShape> postponeGetEntities(Level world, Entity entity, AABB box, @Share("requireAddEntities") LocalBooleanRef requireAddEntities) {
      requireAddEntities.set(true);
      return new ArrayList<>();
   }

   @Redirect(
      method = "collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;collideBoundingBox(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/level/Level;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;"
      )
   )
   private Vec3 collideMovementWithPostponedGetEntities(
      @Nullable Entity entity,
      Vec3 movement,
      AABB entityBoundingBox,
      Level world,
      List<VoxelShape> entityCollisions,
      @Share("requireAddEntities") LocalBooleanRef requireAddEntities
   ) {
      return lithium$CollideMovement(entity, movement, entityBoundingBox, world, entityCollisions, requireAddEntities);
   }

   @ModifyVariable(
      method = "collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
      at = @At(
         value = "INVOKE",
         shift = Shift.BEFORE,
         target = "Lnet/minecraft/world/entity/Entity;collectColliders(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
      )
   )
   private List<VoxelShape> collectEntities(List<VoxelShape> entityCollisions, @Share("requireAddEntities") LocalBooleanRef requireAddEntities) {
      if (requireAddEntities.get()) {
         requireAddEntities.set(false);
         ArrayList<VoxelShape> collisions = entityCollisions instanceof ArrayList ? (ArrayList)entityCollisions : new ArrayList<>(entityCollisions);
         LithiumEntityCollisions.appendEntityCollisions(collisions, this.level(), (Entity)this, this.getBoundingBox());
         return collisions;
      } else {
         return entityCollisions;
      }
   }

   @Overwrite
   public static Vec3 collideBoundingBox(@Nullable Entity entity, Vec3 movement, AABB entityBoundingBox, Level world, List<VoxelShape> entityCollisions) {
      return lithium$CollideMovement(entity, movement, entityBoundingBox, world, entityCollisions, null);
   }

   @Unique
   private static Vec3 lithium$CollideMovement(
      @Nullable Entity entity, Vec3 movement, AABB entityBoundingBox, Level world, List<VoxelShape> entityCollisions, LocalBooleanRef requireAddEntities
   ) {
      double movementX = movement.x;
      double movementY = movement.y;
      double movementZ = movement.z;
      boolean isSingleAxisMovement = (movementX == 0.0 ? 0 : 1) + (movementY == 0.0 ? 0 : 1) + (movementZ == 0.0 ? 0 : 1) == 1;
      if (movementY < 0.0) {
         VoxelShape voxelShape = LithiumEntityCollisions.getSupportingCollisionForEntity(world, entity, entityBoundingBox);
         if (voxelShape != null) {
            double v = voxelShape.collide(Axis.Y, entityBoundingBox, movementY);
            if (v == 0.0) {
               if (isSingleAxisMovement) {
                  return Vec3.ZERO;
               }

               movementY = 0.0;
               isSingleAxisMovement = (movementX == 0.0 ? 0 : 1) + (movementZ == 0.0 ? 0 : 1) == 1;
            }
         }
      }

      AABB movementSpace;
      if (isSingleAxisMovement) {
         movementSpace = LithiumEntityCollisions.getSmallerBoxForSingleAxisMovement(movement, entityBoundingBox, movementY, movementX, movementZ);
      } else {
         movementSpace = entityBoundingBox.expandTowards(movement);
      }

      boolean shouldAddEntities = requireAddEntities != null && requireAddEntities.get();
      boolean shouldAddWorldBorder = true;
      boolean shouldAddLastBlock = true;
      ChunkAwareBlockCollisionSweeper blockCollisionSweeper = new ChunkAwareBlockCollisionSweeper(world, entity, movementSpace, true);
      LazyList<VoxelShape> blockCollisions = new LazyList<>(new ArrayList<>(), blockCollisionSweeper);
      ArrayList<VoxelShape> worldBorderAndLastBlockCollision = new ArrayList<>(2);
      if (movementY != 0.0) {
         movementY = Shapes.collide(Axis.Y, entityBoundingBox, blockCollisions, movementY);
         if (movementY != 0.0) {
            shouldAddEntities = LithiumEntityCollisions.addEntityCollisionsIfRequired(shouldAddEntities, entity, world, entityCollisions, movementSpace);
            shouldAddWorldBorder = LithiumEntityCollisions.addWorldBorderCollisionIfRequired(
               shouldAddWorldBorder, entity, worldBorderAndLastBlockCollision, movementSpace
            );
            shouldAddLastBlock = LithiumEntityCollisions.addLastBlockCollisionIfRequired(
               shouldAddLastBlock, blockCollisionSweeper, worldBorderAndLastBlockCollision
            );
            if (!entityCollisions.isEmpty()) {
               movementY = Shapes.collide(Axis.Y, entityBoundingBox, entityCollisions, movementY);
            }

            if (!worldBorderAndLastBlockCollision.isEmpty()) {
               movementY = Shapes.collide(Axis.Y, entityBoundingBox, worldBorderAndLastBlockCollision, movementY);
            }

            if (movementY != 0.0) {
               entityBoundingBox = entityBoundingBox.move(0.0, movementY, 0.0);
            }
         }
      }

      boolean zMovementBiggerThanXMovement = Math.abs(movementX) < Math.abs(movementZ);
      if (zMovementBiggerThanXMovement) {
         movementZ = Shapes.collide(Axis.Z, entityBoundingBox, blockCollisions, movementZ);
         if (movementZ != 0.0) {
            shouldAddEntities = LithiumEntityCollisions.addEntityCollisionsIfRequired(shouldAddEntities, entity, world, entityCollisions, movementSpace);
            shouldAddWorldBorder = LithiumEntityCollisions.addWorldBorderCollisionIfRequired(
               shouldAddWorldBorder, entity, worldBorderAndLastBlockCollision, movementSpace
            );
            shouldAddLastBlock = LithiumEntityCollisions.addLastBlockCollisionIfRequired(
               shouldAddLastBlock, blockCollisionSweeper, worldBorderAndLastBlockCollision
            );
            if (!entityCollisions.isEmpty()) {
               movementZ = Shapes.collide(Axis.Z, entityBoundingBox, entityCollisions, movementZ);
            }

            if (!worldBorderAndLastBlockCollision.isEmpty()) {
               movementZ = Shapes.collide(Axis.Z, entityBoundingBox, worldBorderAndLastBlockCollision, movementZ);
            }

            if (movementZ != 0.0) {
               entityBoundingBox = entityBoundingBox.move(0.0, 0.0, movementZ);
            }
         }
      }

      if (movementX != 0.0) {
         movementX = Shapes.collide(Axis.X, entityBoundingBox, blockCollisions, movementX);
         if (movementX != 0.0) {
            shouldAddEntities = LithiumEntityCollisions.addEntityCollisionsIfRequired(shouldAddEntities, entity, world, entityCollisions, movementSpace);
            shouldAddWorldBorder = LithiumEntityCollisions.addWorldBorderCollisionIfRequired(
               shouldAddWorldBorder, entity, worldBorderAndLastBlockCollision, movementSpace
            );
            shouldAddLastBlock = LithiumEntityCollisions.addLastBlockCollisionIfRequired(
               shouldAddLastBlock, blockCollisionSweeper, worldBorderAndLastBlockCollision
            );
            if (!entityCollisions.isEmpty()) {
               movementX = Shapes.collide(Axis.X, entityBoundingBox, entityCollisions, movementX);
            }

            if (!worldBorderAndLastBlockCollision.isEmpty()) {
               movementX = Shapes.collide(Axis.X, entityBoundingBox, worldBorderAndLastBlockCollision, movementX);
            }

            if (movementX != 0.0) {
               entityBoundingBox = entityBoundingBox.move(movementX, 0.0, 0.0);
            }
         }
      }

      if (!zMovementBiggerThanXMovement && movementZ != 0.0) {
         movementZ = Shapes.collide(Axis.Z, entityBoundingBox, blockCollisions, movementZ);
         if (movementZ != 0.0) {
            shouldAddEntities = LithiumEntityCollisions.addEntityCollisionsIfRequired(shouldAddEntities, entity, world, entityCollisions, movementSpace);
            shouldAddWorldBorder = LithiumEntityCollisions.addWorldBorderCollisionIfRequired(
               shouldAddWorldBorder, entity, worldBorderAndLastBlockCollision, movementSpace
            );
            shouldAddLastBlock = LithiumEntityCollisions.addLastBlockCollisionIfRequired(
               shouldAddLastBlock, blockCollisionSweeper, worldBorderAndLastBlockCollision
            );
            if (!entityCollisions.isEmpty()) {
               movementZ = Shapes.collide(Axis.Z, entityBoundingBox, entityCollisions, movementZ);
            }

            if (!worldBorderAndLastBlockCollision.isEmpty()) {
               movementZ = Shapes.collide(Axis.Z, entityBoundingBox, worldBorderAndLastBlockCollision, movementZ);
            }
         }
      }

      if (requireAddEntities != null && !shouldAddEntities) {
         requireAddEntities.set(false);
      }

      return new Vec3(movementX, movementY, movementZ);
   }
}
