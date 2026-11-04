package net.caffeinemc.mods.lithium.mixin.ai.pathing;

import net.caffeinemc.mods.lithium.common.ai.pathing.PathNodeCache;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = WalkNodeEvaluator.class, priority = 990)
public abstract class WalkNodeEvaluatorMixin {
   @Inject(
      method = "getPathTypeFromState(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/pathfinder/PathType;",
      at = @At(
         value = "INVOKE_ASSIGN",
         target = "Lnet/minecraft/world/level/BlockGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
         shift = Shift.AFTER
      ),
      cancellable = true,
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private static void getLithiumCachedCommonNodeType(BlockGetter world, BlockPos pos, CallbackInfoReturnable<PathType> cir, BlockState blockState) {
      PathType type = PathNodeCache.getPathNodeType(blockState);
      if (type != null) {
         cir.setReturnValue(type);
      }
   }

   @Inject(
      method = "checkNeighbourBlocks(Lnet/minecraft/world/level/pathfinder/PathfindingContext;IIILnet/minecraft/world/level/pathfinder/PathType;)Lnet/minecraft/world/level/pathfinder/PathType;",
      locals = LocalCapture.CAPTURE_FAILHARD,
      at = @At(
         value = "INVOKE",
         shift = Shift.BEFORE,
         target = "Lnet/minecraft/world/level/pathfinder/PathfindingContext;getPathTypeFromState(III)Lnet/minecraft/world/level/pathfinder/PathType;"
      ),
      cancellable = true
   )
   private static void doNotIteratePositionsIfLithiumSinglePosCall(
      PathfindingContext context, int x, int y, int z, PathType fallback, CallbackInfoReturnable<PathType> cir, int i, int j, int k
   ) {
      if (fallback == null && (i != -1 || j != -1 || k != -1)) {
         cir.setReturnValue(null);
      }
   }

   @Redirect(
      method = "getPathTypeStatic(Lnet/minecraft/world/level/pathfinder/PathfindingContext;Lnet/minecraft/core/BlockPos$MutableBlockPos;)Lnet/minecraft/world/level/pathfinder/PathType;",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/pathfinder/WalkNodeEvaluator;checkNeighbourBlocks(Lnet/minecraft/world/level/pathfinder/PathfindingContext;IIILnet/minecraft/world/level/pathfinder/PathType;)Lnet/minecraft/world/level/pathfinder/PathType;"
      )
   )
   private static PathType getNodeTypeFromNeighbors(PathfindingContext context, int x, int y, int z, PathType fallback) {
      return PathNodeCache.getNodeTypeFromNeighbors(context, x, y, z, fallback);
   }
}
