package net.countered.terrainslabs.mixin.feature;

import com.llamalad7.mixinextras.sugar.Local;
import net.countered.terrainslabs.block.ModSlabsMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.feature.DiskFeature;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DiskFeature.class)
public class MixinDiskFeature {
   @Inject(
      method = "placeColumn",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
      )
   )
   private void countered$updateSlabsAroundDisk(
      DiskConfiguration config,
      WorldGenLevel level,
      RandomSource random,
      int maxY,
      int minY,
      MutableBlockPos pos,
      CallbackInfoReturnable<Boolean> cir,
      @Local(ordinal = 0) BlockState blockState
   ) {
      Block diskBlock = blockState.getBlock();
      Block newSlab = ModSlabsMap.getSlabForBlock(diskBlock);
      if (newSlab != null) {
         this.countered$checkAndReplaceDiskSlab(level, pos.above(), newSlab);
         this.countered$checkAndReplaceDiskSlab(level, pos.below(), newSlab);
      }
   }

   @Unique
   private void countered$checkAndReplaceDiskSlab(WorldGenLevel level, BlockPos targetPos, Block newSlabBlock) {
      if (!level.isOutsideBuildHeight(targetPos.getY())) {
         BlockState currentState = level.getBlockState(targetPos);
         if (currentState.getBlock() instanceof SlabBlock && !currentState.is(newSlabBlock)) {
            BlockState newState = (BlockState)((BlockState)newSlabBlock.defaultBlockState()
                  .setValue(SlabBlock.TYPE, (SlabType)currentState.getValue(SlabBlock.TYPE)))
               .setValue(SlabBlock.WATERLOGGED, (Boolean)currentState.getValue(SlabBlock.WATERLOGGED));
            level.setBlock(targetPos, newState, 2);
         }
      }
   }
}
