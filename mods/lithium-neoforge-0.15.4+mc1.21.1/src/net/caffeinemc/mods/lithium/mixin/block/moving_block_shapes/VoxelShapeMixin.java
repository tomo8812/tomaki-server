package net.caffeinemc.mods.lithium.mixin.block.moving_block_shapes;

import net.caffeinemc.mods.lithium.common.shapes.OffsetVoxelShapeCache;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VoxelShape.class)
public class VoxelShapeMixin implements OffsetVoxelShapeCache {
   private volatile VoxelShape[] offsetAndSimplified;

   @Override
   public void lithium$setShape(float offset, Direction direction, VoxelShape offsetShape) {
      if (offsetShape == null) {
         throw new IllegalArgumentException("offsetShape must not be null!");
      }

      int index = getIndexForOffsetSimplifiedShapes(offset, direction);
      VoxelShape[] offsetAndSimplifiedShapes = this.offsetAndSimplified;
      if (offsetAndSimplifiedShapes == null) {
         offsetAndSimplifiedShapes = new VoxelShape[13];
      } else {
         offsetAndSimplifiedShapes = (VoxelShape[])offsetAndSimplifiedShapes.clone();
      }

      offsetAndSimplifiedShapes[index] = offsetShape;
      this.offsetAndSimplified = offsetAndSimplifiedShapes;
   }

   @Override
   public VoxelShape lithium$getOffsetSimplifiedShape(float offset, Direction direction) {
      VoxelShape[] offsetAndSimplified = this.offsetAndSimplified;
      if (offsetAndSimplified == null) {
         return null;
      }

      int index = getIndexForOffsetSimplifiedShapes(offset, direction);
      return offsetAndSimplified[index];
   }

   private static int getIndexForOffsetSimplifiedShapes(float offset, Direction direction) {
      if (offset != 0.0F && offset != 0.5F && offset != 1.0F) {
         throw new IllegalArgumentException("offset must be one of {0f, 0.5f, 1f}");
      } else {
         return offset == 0.0F ? 0 : (int)(2.0F * offset) + 2 * direction.get3DDataValue();
      }
   }
}
