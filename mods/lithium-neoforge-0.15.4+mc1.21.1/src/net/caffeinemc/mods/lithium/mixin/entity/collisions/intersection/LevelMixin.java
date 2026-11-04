package net.caffeinemc.mods.lithium.mixin.entity.collisions.intersection;

import net.caffeinemc.mods.lithium.common.entity.LithiumEntityCollisions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelAccessor {
   public boolean noCollision(@Nullable Entity entity, AABB box) {
      boolean ret = !LithiumEntityCollisions.doesBoxCollideWithBlocks((Level)this, entity, box);
      if (ret) {
         ret = !LithiumEntityCollisions.doesBoxCollideWithHardEntities(this, entity, box);
      }

      if (ret && entity != null) {
         ret = !LithiumEntityCollisions.doesBoxCollideWithWorldBorder(this, entity, box);
      }

      return ret;
   }
}
