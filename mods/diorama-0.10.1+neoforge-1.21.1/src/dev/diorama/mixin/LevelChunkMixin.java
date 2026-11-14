package dev.diorama.mixin;

import dev.diorama.MapSession;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {
   @Shadow
   @Final
   private Level level;

   @Inject(method = "setBlockState", at = @At("RETURN"))
   private void diorama$onSetBlockState(BlockPos pos, BlockState state, boolean isMoving, CallbackInfoReturnable<BlockState> cir) {
      BlockState old = (BlockState)cir.getReturnValue();
      if (old != null && this.level instanceof ClientLevel clientLevel) {
         MapSession.blockChanged(clientLevel, pos, old, state);
      }
   }
}
