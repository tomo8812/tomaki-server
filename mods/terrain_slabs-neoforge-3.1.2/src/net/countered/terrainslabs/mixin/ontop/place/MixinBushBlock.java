package net.countered.terrainslabs.mixin.ontop.place;

import net.countered.terrainslabs.block.ModBlockTags;
import net.countered.terrainslabs.platform.PlatformConfigHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BushBlock.class)
public class MixinBushBlock {
   @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
   private void terrain_slabs$mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
      if (state.is(ModBlockTags.DIRT_SLABS)
         && !(Boolean)state.getValue(BlockStateProperties.WATERLOGGED)
         && PlatformConfigHooks.isVegetationOnSlabsEnabled()
         && !(this instanceof SaplingBlock)) {
         cir.setReturnValue(true);
      }
   }
}
