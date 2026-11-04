package net.caffeinemc.mods.lithium.mixin.experimental.entity.block_caching.suffocation;

import net.caffeinemc.mods.lithium.common.tracking.block.BlockCache;
import net.caffeinemc.mods.lithium.common.tracking.block.BlockCacheProvider;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Entity.class)
public abstract class EntityMixin implements BlockCacheProvider {
   @Shadow
   public Level level;
   @Shadow
   private EntityDimensions dimensions;

   protected EntityMixin(EntityDimensions dimensions) {
      this.dimensions = dimensions;
   }

   @Inject(
      method = "isInWall",
      cancellable = true,
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/core/BlockPos;betweenClosedStream(Lnet/minecraft/world/phys/AABB;)Ljava/util/stream/Stream;",
         shift = Shift.BEFORE
      ),
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   public void isInsideWall(CallbackInfoReturnable<Boolean> cir, float f, AABB box) {
      int minX = Mth.floor(box.minX);
      int minY = Mth.floor(box.minY);
      int minZ = Mth.floor(box.minZ);
      int maxX = Mth.floor(box.maxX);
      int maxY = Mth.floor(box.maxY);
      int maxZ = Mth.floor(box.maxZ);
      BlockCache bc = this.getUpdatedBlockCache((Entity)this);
      byte cachedSuffocation = bc.getIsSuffocating();
      if (cachedSuffocation == 0) {
         cir.setReturnValue(false);
      } else if (cachedSuffocation == 1) {
         cir.setReturnValue(true);
      } else {
         Level world = this.level;
         if (world.getMinBuildHeight() <= maxY && world.getMaxBuildHeight() >= minY) {
            MutableBlockPos blockPos = new MutableBlockPos();
            VoxelShape suffocationShape = null;
            boolean shouldCache = true;

            for (int y = minY; y <= maxY; y++) {
               for (int z = minZ; z <= maxZ; z++) {
                  for (int x = minX; x <= maxX; x++) {
                     blockPos.set(x, y, z);
                     BlockState blockState = world.getBlockState(blockPos);
                     if (!blockState.isAir() && blockState.isSuffocating(this.level, blockPos)) {
                        if (shouldCache && blockState.is(BlockTags.SHULKER_BOXES)) {
                           shouldCache = false;
                        }

                        if (suffocationShape == null) {
                           suffocationShape = Shapes.create(new AABB(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ));
                        }

                        if (Shapes.joinIsNotEmpty(
                           blockState.getCollisionShape(this.level, blockPos).move(blockPos.getX(), blockPos.getY(), blockPos.getZ()),
                           suffocationShape,
                           BooleanOp.AND
                        )) {
                           if (shouldCache) {
                              bc.setCachedIsSuffocating(true);
                           }

                           cir.setReturnValue(true);
                           return;
                        }
                     }
                  }
               }
            }

            if (shouldCache) {
               bc.setCachedIsSuffocating(false);
            }

            cir.setReturnValue(false);
         } else {
            bc.setCachedIsSuffocating(false);
            cir.setReturnValue(false);
         }
      }
   }
}
