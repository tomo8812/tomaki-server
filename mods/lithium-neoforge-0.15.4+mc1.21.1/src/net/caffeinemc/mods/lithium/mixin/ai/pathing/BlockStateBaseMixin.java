package net.caffeinemc.mods.lithium.mixin.ai.pathing;

import net.caffeinemc.mods.lithium.common.ai.pathing.BlockStatePathingCache;
import net.caffeinemc.mods.lithium.common.world.blockview.SingleBlockBlockView;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.apache.commons.lang3.Validate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockStateBase.class)
public abstract class BlockStateBaseMixin implements BlockStatePathingCache {
   private PathType pathNodeType = null;
   private PathType pathNodeTypeNeighbor = null;

   @Override
   public void lithium$initializePathNodeTypeCache() {
      this.pathNodeType = null;
      this.pathNodeTypeNeighbor = null;
      BlockState state = this.asState();
      SingleBlockBlockView singleBlockBlockView = SingleBlockBlockView.of(state, BlockPos.ZERO);

      try {
         this.pathNodeType = (PathType)Validate.notNull(
            WalkNodeEvaluator.getPathTypeFromState(singleBlockBlockView, BlockPos.ZERO), "Block has no common path node type!", new Object[0]
         );
      } catch (SingleBlockBlockView.SingleBlockViewException | ClassCastException e) {
         this.pathNodeType = null;
      }

      try {
         this.pathNodeTypeNeighbor = WalkNodeEvaluator.checkNeighbourBlocks(new PathfindingContext(singleBlockBlockView, null), 1, 1, 1, null);
         if (this.pathNodeTypeNeighbor == null) {
            this.pathNodeTypeNeighbor = PathType.OPEN;
         }
      } catch (SingleBlockBlockView.SingleBlockViewException | NullPointerException | ClassCastException e) {
         this.pathNodeTypeNeighbor = null;
      }
   }

   @Override
   public PathType lithium$getPathNodeType() {
      return this.pathNodeType;
   }

   @Override
   public PathType lithium$getNeighborPathNodeType() {
      return this.pathNodeTypeNeighbor;
   }

   @Shadow
   protected abstract BlockState asState();

   @Shadow
   public abstract Block getBlock();
}
