package net.caffeinemc.mods.lithium.mixin.block.moving_block_shapes;

import net.caffeinemc.mods.lithium.common.shapes.OffsetVoxelShapeCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PistonMovingBlockEntity.class)
public abstract class PistonMovingBlockEntityMixin {
   private static final VoxelShape[] PISTON_BASE_WITH_MOVING_HEAD_SHAPES = precomputePistonBaseWithMovingHeadShapes();
   @Shadow
   private Direction direction;
   @Shadow
   private boolean extending;
   @Shadow
   private boolean isSourcePiston;
   @Shadow
   private BlockState movedState;

   @Inject(
      method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Direction;getStepX()I", shift = Shift.BEFORE),
      locals = LocalCapture.CAPTURE_FAILHARD,
      cancellable = true
   )
   private void skipVoxelShapeUnion(
      BlockGetter world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir, VoxelShape voxelShape, Direction direction, BlockState blockState, float offset
   ) {
      float absOffset = Math.abs(offset);
      if (absOffset == 0.0F || absOffset == 0.5F || absOffset == 1.0F) {
         if (!this.extending && this.isSourcePiston && this.movedState.getBlock() instanceof PistonBaseBlock) {
            int index = getIndexForMergedShape(offset, this.direction);
            cir.setReturnValue(PISTON_BASE_WITH_MOVING_HEAD_SHAPES[index]);
         } else {
            VoxelShape blockShape = blockState.getCollisionShape(world, pos);
            VoxelShape offsetAndSimplified = getOffsetAndSimplified(blockShape, absOffset, offset < 0.0F ? this.direction.getOpposite() : this.direction);
            cir.setReturnValue(offsetAndSimplified);
         }
      }
   }

   private static VoxelShape getOffsetAndSimplified(VoxelShape blockShape, float offset, Direction direction) {
      VoxelShape offsetSimplifiedShape = ((OffsetVoxelShapeCache)blockShape).lithium$getOffsetSimplifiedShape(offset, direction);
      if (offsetSimplifiedShape == null) {
         offsetSimplifiedShape = blockShape.move(direction.getStepX() * offset, direction.getStepY() * offset, direction.getStepZ() * offset).optimize();
         ((OffsetVoxelShapeCache)blockShape).lithium$setShape(offset, direction, offsetSimplifiedShape);
      }

      return offsetSimplifiedShape;
   }

   private static VoxelShape[] precomputePistonBaseWithMovingHeadShapes() {
      float[] offsets = new float[]{0.0F, 0.5F, 1.0F};
      Direction[] directions = Direction.values();
      VoxelShape[] mergedShapes = new VoxelShape[offsets.length * directions.length];

      for (Direction facing : directions) {
         VoxelShape baseShape = ((BlockState)((BlockState)Blocks.PISTON.defaultBlockState().setValue(PistonBaseBlock.EXTENDED, true))
               .setValue(PistonBaseBlock.FACING, facing))
            .getCollisionShape(null, null);

         for (float offset : offsets) {
            boolean isShort = offset < 0.25F;
            VoxelShape headShape = ((BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, facing))
                  .setValue(PistonHeadBlock.SHORT, isShort))
               .getCollisionShape(null, null);
            VoxelShape offsetHead = headShape.move(facing.getStepX() * offset, facing.getStepY() * offset, facing.getStepZ() * offset);
            mergedShapes[getIndexForMergedShape(offset, facing)] = Shapes.or(baseShape, offsetHead);
         }
      }

      return mergedShapes;
   }

   private static int getIndexForMergedShape(float offset, Direction direction) {
      return offset != 0.0F && offset != 0.5F && offset != 1.0F ? -1 : (int)(2.0F * offset) + 3 * direction.get3DDataValue();
   }
}
