package net.caffeinemc.mods.lithium.mixin.alloc.chunk_random;

import net.caffeinemc.mods.lithium.common.world.ChunkRandomSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
   private final MutableBlockPos randomPosInChunkCachedPos = new MutableBlockPos();

   @Redirect(
      method = "tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getBlockRandomPos(IIII)Lnet/minecraft/core/BlockPos;")
   )
   private BlockPos redirectTickGetRandomPosInChunk(ServerLevel serverWorld, int x, int y, int z, int mask) {
      ((ChunkRandomSource)serverWorld).lithium$getRandomPosInChunk(x, y, z, mask, this.randomPosInChunkCachedPos);
      return this.randomPosInChunkCachedPos;
   }

   @Redirect(
      method = "tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/state/BlockState;randomTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V"
      )
   )
   private void redirectBlockStateTick(BlockState blockState, ServerLevel world, BlockPos pos, RandomSource rand) {
      blockState.randomTick(world, pos.immutable(), rand);
   }

   @Redirect(
      method = "tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/material/FluidState;randomTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V"
      )
   )
   private void redirectFluidStateTick(FluidState fluidState, Level world, BlockPos pos, RandomSource rand) {
      fluidState.randomTick(world, pos.immutable(), rand);
   }
}
