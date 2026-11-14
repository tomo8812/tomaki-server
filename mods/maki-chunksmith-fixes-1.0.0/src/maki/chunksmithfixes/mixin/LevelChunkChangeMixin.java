package maki.chunksmithfixes.mixin;

import maki.chunksmithfixes.LodUpdateQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public abstract class LevelChunkChangeMixin {
   @Inject(method = "setBlockState", at = @At("RETURN"), require = 1)
   private void maki$changed(BlockPos pos, BlockState state, boolean moving, CallbackInfoReturnable<BlockState> ci) {
      if (ci.getReturnValue() != null) {
         LodUpdateQueue.changed((LevelChunk)this);
      }
   }
}
