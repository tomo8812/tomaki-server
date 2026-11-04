package net.caffeinemc.mods.lithium.mixin.minimal_nonvanilla.collisions.empty_space;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.caffeinemc.mods.lithium.common.entity.LithiumEntityCollisions;
import net.caffeinemc.mods.lithium.common.shapes.VoxelShapeHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelAccessor {
   public Optional<Vec3> findFreePosition(
      @Nullable Entity collidingEntity, VoxelShape collidingShape, Vec3 originalPosition, double maxXOffset, double maxYOffset, double maxZOffset
   ) {
      if (collidingShape.isEmpty()) {
         return Optional.empty();
      }

      AABB collidingBox = collidingShape.bounds();
      AABB searchBox = collidingBox.inflate(maxXOffset, maxYOffset, maxZOffset);
      List<VoxelShape> blockCollisions = LithiumEntityCollisions.getBlockCollisions((Level)this, collidingEntity, searchBox);
      if (blockCollisions.isEmpty()) {
         return collidingShape.closestPointTo(originalPosition);
      }

      WorldBorder worldBorder = this.getWorldBorder();
      if (worldBorder != null) {
         double sideLength = Math.max(searchBox.getXsize(), searchBox.getZsize());
         double centerX = Mth.lerp(0.5, searchBox.minX, searchBox.maxX);
         double centerZ = Mth.lerp(0.5, searchBox.minZ, searchBox.maxZ);
         boolean worldBorderIsNearby = 2.0 + 2.0 * sideLength >= worldBorder.getDistanceToBorder(centerX, centerZ);
         if (worldBorderIsNearby) {
            blockCollisions.removeIf(voxelShape -> !worldBorder.isWithinBounds(voxelShape.bounds()));
         }
      }

      List<AABB> allCollisionBoxes = new ArrayList<>();

      for (VoxelShape blockCollision : blockCollisions) {
         for (AABB box : blockCollision.toAabbs()) {
            AABB foldedBox = box.inflate(maxXOffset / 2.0, maxYOffset / 2.0, maxZOffset / 2.0);
            allCollisionBoxes.add(foldedBox);
         }
      }

      return VoxelShapeHelper.getClosestPointTo(originalPosition, collidingShape, allCollisionBoxes);
   }
}
