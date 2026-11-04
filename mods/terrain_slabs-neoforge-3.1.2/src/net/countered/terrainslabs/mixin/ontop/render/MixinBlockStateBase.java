package net.countered.terrainslabs.mixin.ontop.render;

import net.countered.terrainslabs.util.MixinHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockStateBase.class)
public abstract class MixinBlockStateBase {
   @Inject(method = "getOffset", at = @At("RETURN"), cancellable = true)
   private void terrain_slabs$getOffset(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Vec3> cir) {
      if (!(level instanceof ServerLevel)) {
         BlockState state = (BlockState)this;
         if (MixinHelper.terrain_slabs$isStateValidOnTop(state)) {
            BlockPos belowPos = pos.below();
            if (state.getBlock() instanceof DoublePlantBlock && state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
               belowPos = pos.below(2);
            }

            BlockState belowState = level.getBlockState(belowPos);
            if (belowState.is(BlockTags.SLABS) && belowState.hasProperty(SlabBlock.TYPE) && belowState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM) {
               Vec3 currentOffset = (Vec3)cir.getReturnValue();
               cir.setReturnValue(new Vec3(currentOffset.x, -0.5, currentOffset.z));
            }
         }
      }
   }

   @Inject(
      method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
      at = @At("RETURN"),
      cancellable = true
   )
   private void terrain_slabs$smartShapeOffset(BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
      if (!(level instanceof ServerLevel)) {
         BlockState state = (BlockState)this;
         if (MixinHelper.terrain_slabs$isStateValidOnTop(state)) {
            BlockPos belowPos = pos.below();
            if (state.getBlock() instanceof DoublePlantBlock && state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
               belowPos = pos.below(2);
            }

            BlockState belowState = level.getBlockState(belowPos);
            if (belowState.is(BlockTags.SLABS) && belowState.hasProperty(SlabBlock.TYPE) && belowState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM) {
               Vec3 offset = state.getOffset(level, pos);
               if (offset.y < 0.0) {
                  VoxelShape currentShape = (VoxelShape)cir.getReturnValue();
                  if (currentShape.min(Axis.Y) >= 0.0) {
                     cir.setReturnValue(currentShape.move(offset.x, offset.y, offset.z));
                  }
               }
            }
         }
      }
   }

   @Inject(method = "getFaceOcclusionShape", at = @At("RETURN"), cancellable = true)
   private void terrain_slabs$reduceOcclusion(BlockGetter level, BlockPos pos, Direction direction, CallbackInfoReturnable<VoxelShape> cir) {
      BlockState state = (BlockState)this;
      if (state.getBlock() instanceof SnowLayerBlock && direction.getAxis().isHorizontal()) {
         cir.setReturnValue(Shapes.empty());
      }
   }
}
